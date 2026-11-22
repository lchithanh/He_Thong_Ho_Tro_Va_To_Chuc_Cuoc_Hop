package com.example.demo.service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BauChonService {
    private final JdbcClient jdbc;
    private final CuocHopRepository cuocHopRepository;
    private final ThanhVienCuocHopRepository thanhVienRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final CapNhatHoSoRealtime realtime;

    public record Nguoi(Integer nguoiDungId, String hoTen) {}
    public record UngVien(Integer nguoiDungId, String hoTen, Integer soLuotChon, String ketQua) {}
    public record CauHinhVong(int soNguoiDuocChon, int soLuaChonMoiPhieu) {}
    public record CuocBau(Integer cuocBauId, String tieuDe) {}
    public record Vong(Integer vongBauId, Integer cuocBauId, Integer vongTruocId,
            int soVong, int soLuaChonMoiPhieu, int soNguoiDuocChon, String trangThai) {}
    public record ChiTietVong(Vong vong, List<UngVien> ungVien, boolean duocBoPhieu, boolean daBoPhieu) {}
    public record ChiTietCuocBau(CuocBau cuocBau, List<ChiTietVong> cacVong) {}
    public record DuLieu(boolean laChuTri, List<Nguoi> thanhVien, List<ChiTietCuocBau> cuocBau) {}

    @Transactional(readOnly = true)
    public DuLieu xem(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop hop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        var thanhVien = thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId);
        boolean laChuTri = ThaoTacCuocHop.laNguoiChuTri(hop, nguoiDungId);
        if (!laChuTri && thanhVien.stream().noneMatch(tv -> "CO_MAT".equals(tv.getTrangThaiThamDu())
                && nguoiDungId.equals(tv.getId().getNguoiDungId()))) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn cần có mặt tại cuộc họp để xem bầu chọn");
        }
        var cuocBau = jdbc.sql("""
                SELECT cuoc_bau_id, tieu_de FROM cuoc_bau WHERE cuoc_hop_id = ? ORDER BY cuoc_bau_id DESC
                """).param(cuocHopId).query(CuocBau.class).list();
        return new DuLieu(laChuTri, laChuTri ? thanhVienCoMat(thanhVien) : List.of(),
                cuocBau.stream().map(cb -> new ChiTietCuocBau(cb, cacVong(cb.cuocBauId()).stream()
                        .map(vong -> new ChiTietVong(vong, ungVien(vong.vongBauId()),
                                coQuyenBoPhieu(vong.vongBauId(), nguoiDungId),
                                daBoPhieu(vong.vongBauId(), nguoiDungId)))
                        .toList())).toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer tao(Integer cuocHopId, Integer nguoiDungId, String tieuDe, List<Integer> ungVienIds,
            List<CauHinhVong> cacVong) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId, true);
        if (tieuDe == null || tieuDe.isBlank() || tieuDe.strip().length() > 255) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập tiêu đề cuộc bầu chọn");
        }
        var ungVien = chonNguoiCoMat(cuocHopId, ungVienIds, "ứng viên");
        kiemTraCauHinhVong(cacVong, ungVien.size());
        var khoa = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO cuoc_bau (cuoc_hop_id, tieu_de, nguoi_tao_id) VALUES (?, ?, ?)")
                .params(cuocHopId, tieuDe.strip(), nguoiDungId).update(khoa, "cuoc_bau_id");
        int cuocBauId = Objects.requireNonNull(khoa.getKey()).intValue();
        for (int i = 0; i < cacVong.size(); i++) {
            var cauHinh = cacVong.get(i);
            jdbc.sql("""
                    INSERT INTO cau_hinh_vong_bau (cuoc_bau_id, so_vong, so_nguoi_duoc_chon, so_lua_chon_moi_phieu)
                    VALUES (?, ?, ?, ?)
                    """).params(cuocBauId, i + 1, cauHinh.soNguoiDuocChon(), cauHinh.soLuaChonMoiPhieu()).update();
        }
        taoVong(cuocBauId, null, 1, cacVong.getFirst(), ungVien);
        realtime.baoCuocHop(hop, nguoiDungId);
        return cuocBauId;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void mo(Integer cuocHopId, Integer vongId, Integer nguoiDungId, List<Integer> cuTriIds) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId, true);
        Vong vong = timVong(cuocHopId, vongId);
        if (!"NHAP".equals(vong.trangThai())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ mở vòng ở trạng thái nháp");
        }
        var cuTri = chonNguoiCoMat(cuocHopId, cuTriIds, "người bỏ phiếu");
        for (Nguoi nguoi : cuTri) {
            jdbc.sql("INSERT INTO cu_tri_vong_bau VALUES (?, ?)")
                    .params(vongId, nguoi.nguoiDungId()).update();
        }
        capNhatTrangThai(vongId, "DANG_MO");
        realtime.baoCuocHop(hop, nguoiDungId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void boPhieu(Integer cuocHopId, Integer vongId, Integer nguoiDungId, List<Integer> luaChon) {
        khoaHop(cuocHopId, nguoiDungId, false);
        Vong vong = timVong(cuocHopId, vongId);
        if (!"DANG_MO".equals(vong.trangThai())) {
            throw loi(HttpStatus.CONFLICT, "Vòng bầu hiện không nhận phiếu");
        }
        if (!coQuyenBoPhieu(vongId, nguoiDungId) || danhSachCoMat(cuocHopId).stream()
                .noneMatch(nguoi -> nguoiDungId.equals(nguoi.nguoiDungId()))) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn không có quyền bỏ phiếu ở vòng này");
        }
        if (daBoPhieu(vongId, nguoiDungId)) throw loi(HttpStatus.CONFLICT, "Bạn đã gửi phiếu");
        var hopLe = ungVien(vongId).stream().map(UngVien::nguoiDungId).toList();
        int canChon = vong.soLuaChonMoiPhieu();
        if (luaChon == null || luaChon.size() != canChon || new HashSet<>(luaChon).size() != canChon
                || luaChon.stream().anyMatch(Objects::isNull) || !hopLe.containsAll(luaChon)) {
            throw loi(HttpStatus.BAD_REQUEST, "Chọn đúng " + canChon + " ứng viên khác nhau trong vòng này");
        }
        jdbc.sql("INSERT INTO phieu_bau (vong_bau_id, nguoi_bo_phieu_id) VALUES (?, ?)")
                .params(vongId, nguoiDungId).update();
        for (Integer id : luaChon) {
            jdbc.sql("INSERT INTO lua_chon_phieu_bau VALUES (?, ?, ?)").params(vongId, nguoiDungId, id).update();
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void dong(Integer cuocHopId, Integer vongId, Integer nguoiDungId) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId, true);
        Vong vong = timVong(cuocHopId, vongId);
        if (!"DANG_MO".equals(vong.trangThai())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ đóng vòng đang mở");
        }
        if (!jdbc.sql("SELECT EXISTS(SELECT 1 FROM phieu_bau WHERE vong_bau_id = ?)")
                .param(vongId).query(Boolean.class).single()) {
            capNhatTrangThai(vongId, "DA_HUY");
            realtime.baoCuocHop(hop, nguoiDungId);
            return;
        }
        var xepHang = jdbc.sql("""
                SELECT u.nguoi_dung_id, u.ho_ten, COUNT(l.ung_vien_id) AS so_luot_chon, u.ket_qua
                FROM ung_vien_vong_bau u LEFT JOIN lua_chon_phieu_bau l
                  ON l.vong_bau_id = u.vong_bau_id AND l.ung_vien_id = u.nguoi_dung_id
                WHERE u.vong_bau_id = ?
                GROUP BY u.nguoi_dung_id, u.ho_ten, u.ket_qua
                ORDER BY so_luot_chon DESC, u.nguoi_dung_id
                """).param(vongId).query(UngVien.class).list();
        int soSuat = vong.soNguoiDuocChon();
        int diemChot = xepHang.get(soSuat - 1).soLuotChon();
        long soHon = xepHang.stream().filter(u -> u.soLuotChon() > diemChot).count();
        var bangDiem = xepHang.stream().filter(u -> u.soLuotChon() == diemChot).toList();
        boolean canBauPhu = soHon + bangDiem.size() > soSuat;
        for (UngVien ungVien : xepHang) {
            String ketQua = ungVien.soLuotChon() > diemChot ? "DUOC_CHON"
                    : ungVien.soLuotChon() < diemChot ? "LOAI"
                    : canBauPhu ? "DONG_PHIEU" : "DUOC_CHON";
            jdbc.sql("""
                    UPDATE ung_vien_vong_bau SET so_luot_chon = ?, ket_qua = ?
                    WHERE vong_bau_id = ? AND nguoi_dung_id = ?
                    """).params(ungVien.soLuotChon(), ketQua, vongId, ungVien.nguoiDungId()).update();
        }
        if (canBauPhu) {
            capNhatTrangThai(vongId, "CHO_PHAN_DINH");
            int soSuatConLai = soSuat - (int) soHon;
            taoVong(vong.cuocBauId(), vongId, vong.soVong(), new CauHinhVong(soSuatConLai, soSuatConLai),
                    bangDiem.stream().map(u -> new Nguoi(u.nguoiDungId(), u.hoTen())).toList());
        } else {
            capNhatTrangThai(vongId, "DA_CHOT");
            chotVongGoc(cuocHopId, vong);
        }
        realtime.baoCuocHop(hop, nguoiDungId);
    }

    boolean coVongDangMo(Integer cuocHopId) {
        return jdbc.sql("""
                SELECT EXISTS(SELECT 1 FROM vong_bau v JOIN cuoc_bau b ON b.cuoc_bau_id = v.cuoc_bau_id
                    WHERE b.cuoc_hop_id = ? AND v.trang_thai = 'DANG_MO')
                """).param(cuocHopId).query(Boolean.class).single();
    }

    private void chotVongGoc(Integer cuocHopId, Vong vongDaChot) {
        Vong vong = vongDaChot;
        while (vong.vongTruocId() != null) {
            Vong truoc = timVong(cuocHopId, vong.vongTruocId());
            if (truoc.soVong() != vong.soVong()) break;

            jdbc.sql("""
                    UPDATE ung_vien_vong_bau goc JOIN ung_vien_vong_bau phu
                      ON phu.vong_bau_id = ? AND phu.nguoi_dung_id = goc.nguoi_dung_id
                    SET goc.ket_qua = phu.ket_qua WHERE goc.vong_bau_id = ?
                    """).params(vong.vongBauId(), truoc.vongBauId()).update();
            capNhatTrangThai(truoc.vongBauId(), "DA_CHOT");
            vong = truoc;
        }
        int soVongKe = vong.soVong() + 1;
        var cauHinh = jdbc.sql("""
                SELECT so_nguoi_duoc_chon, so_lua_chon_moi_phieu FROM cau_hinh_vong_bau
                WHERE cuoc_bau_id = ? AND so_vong = ?
                """).params(vong.cuocBauId(), soVongKe).query(CauHinhVong.class).optional();
        if (cauHinh.isPresent()) {
            var duocChon = ungVien(vong.vongBauId()).stream()
                    .filter(u -> "DUOC_CHON".equals(u.ketQua()))
                    .map(u -> new Nguoi(u.nguoiDungId(), u.hoTen())).toList();
            taoVong(vong.cuocBauId(), vong.vongBauId(), soVongKe, cauHinh.get(), duocChon);
        }
    }

    private void kiemTraCauHinhVong(List<CauHinhVong> cacVong, int soUngVien) {
        if (cacVong == null || cacVong.isEmpty()) {
            throw loi(HttpStatus.BAD_REQUEST, "Cấu hình ít nhất một vòng bầu");
        }
        int soCoTheChon = soUngVien;
        for (int i = 0; i < cacVong.size(); i++) {
            var vong = cacVong.get(i);
            if (vong == null || vong.soNguoiDuocChon() < 1 || vong.soNguoiDuocChon() > soCoTheChon
                    || (i > 0 && vong.soNguoiDuocChon() == soCoTheChon)
                    || vong.soLuaChonMoiPhieu() < 1 || vong.soLuaChonMoiPhieu() > soCoTheChon) {
                throw loi(HttpStatus.BAD_REQUEST, "Kiểm tra số suất và số lựa chọn ở vòng " + (i + 1));
            }
            soCoTheChon = vong.soNguoiDuocChon();
        }
    }

    private void taoVong(Integer cuocBauId, Integer vongTruocId, int soVong, CauHinhVong cauHinh, List<Nguoi> ungVien) {
        var khoa = new GeneratedKeyHolder();
        jdbc.sql("""
                INSERT INTO vong_bau (cuoc_bau_id, vong_truoc_id, so_vong, so_lua_chon_moi_phieu, so_nguoi_duoc_chon)
                VALUES (:cuocBauId, :vongTruocId, :soVong, :soLuaChon, :soSuat)
                """).param("cuocBauId", cuocBauId).param("vongTruocId", vongTruocId, java.sql.Types.INTEGER)
                .param("soVong", soVong).param("soSuat", cauHinh.soNguoiDuocChon())
                .param("soLuaChon", cauHinh.soLuaChonMoiPhieu()).update(khoa, "vong_bau_id");
        int vongId = Objects.requireNonNull(khoa.getKey()).intValue();
        for (Nguoi nguoi : ungVien) {
            jdbc.sql("INSERT INTO ung_vien_vong_bau (vong_bau_id, nguoi_dung_id, ho_ten) VALUES (?, ?, ?)")
                    .params(vongId, nguoi.nguoiDungId(), nguoi.hoTen()).update();
        }
    }

    private List<Vong> cacVong(Integer cuocBauId) {
        return jdbc.sql("SELECT * FROM vong_bau WHERE cuoc_bau_id = ? ORDER BY vong_bau_id")
                .param(cuocBauId).query(Vong.class).list();
    }

    private Vong timVong(Integer cuocHopId, Integer vongId) {
        return jdbc.sql("""
                SELECT v.* FROM vong_bau v JOIN cuoc_bau b ON b.cuoc_bau_id = v.cuoc_bau_id
                WHERE v.vong_bau_id = ? AND b.cuoc_hop_id = ?
                """).params(vongId, cuocHopId).query(Vong.class).optional()
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy vòng bầu"));
    }

    private List<UngVien> ungVien(Integer vongId) {
        return jdbc.sql("""
                SELECT nguoi_dung_id, ho_ten, so_luot_chon, ket_qua FROM ung_vien_vong_bau
                WHERE vong_bau_id = ? ORDER BY so_luot_chon DESC, ho_ten, nguoi_dung_id
                """).param(vongId).query(UngVien.class).list();
    }

    private boolean coQuyenBoPhieu(Integer vongId, Integer nguoiDungId) {
        return jdbc.sql("SELECT EXISTS(SELECT 1 FROM cu_tri_vong_bau WHERE vong_bau_id = ? AND nguoi_dung_id = ?)")
                .params(vongId, nguoiDungId).query(Boolean.class).single();
    }

    private boolean daBoPhieu(Integer vongId, Integer nguoiDungId) {
        return jdbc.sql("SELECT EXISTS(SELECT 1 FROM phieu_bau WHERE vong_bau_id = ? AND nguoi_bo_phieu_id = ?)")
                .params(vongId, nguoiDungId).query(Boolean.class).single();
    }

    private void capNhatTrangThai(Integer vongId, String trangThai) {
        jdbc.sql("UPDATE vong_bau SET trang_thai = ? WHERE vong_bau_id = ?").params(trangThai, vongId).update();
    }

    private CuocHop khoaHop(Integer cuocHopId, Integer nguoiDungId, boolean canChuTri) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop hop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (canChuTri && !ThaoTacCuocHop.laNguoiChuTri(hop, nguoiDungId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ chủ trì được điều hành bầu chọn");
        }
        if (!ThaoTacCuocHop.dangDienRa(hop)) {
            throw loi(HttpStatus.CONFLICT, "Cần cuộc họp đang diễn ra");
        }
        return hop;
    }

    private List<Nguoi> chonNguoiCoMat(Integer cuocHopId, List<Integer> ids, String vaiTro) {
        if (ids == null || ids.isEmpty() || ids.stream().anyMatch(Objects::isNull)) {
            throw loi(HttpStatus.BAD_REQUEST, "Chọn ít nhất một " + vaiTro);
        }
        var idsDaChon = new HashSet<>(ids);
        if (idsDaChon.size() != ids.size()) {
            throw loi(HttpStatus.BAD_REQUEST, "Danh sách " + vaiTro + " có người bị chọn trùng");
        }
        var daChon = danhSachCoMat(cuocHopId).stream()
                .filter(nguoi -> idsDaChon.contains(nguoi.nguoiDungId())).toList();
        if (daChon.size() != idsDaChon.size()) {
            throw loi(HttpStatus.BAD_REQUEST, "Chỉ chọn " + vaiTro + " trong số thành viên đang có mặt");
        }
        return daChon;
    }

    private List<Nguoi> danhSachCoMat(Integer cuocHopId) {
        return thanhVienCoMat(thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId));
    }

    private List<Nguoi> thanhVienCoMat(List<ThanhVienCuocHop> thanhVien) {
        return thanhVien.stream().filter(this::coMat)
                .map(tv -> new Nguoi(tv.getId().getNguoiDungId(), tv.getNguoiDung().getHoTen())).toList();
    }

    private boolean coMat(ThanhVienCuocHop tv) {
        return "CO_MAT".equals(tv.getTrangThaiThamDu()) && tv.getThoiGianRoiCuocHop() == null
                && NguoiDungHienTaiService.dangHoatDong(tv.getNguoiDung());
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
