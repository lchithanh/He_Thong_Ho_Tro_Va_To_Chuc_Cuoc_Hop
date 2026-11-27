package com.example.demo.service;

import java.time.LocalDateTime;
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
import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.NguoiDungRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class BauLaiNhomService {
    private static final String TO_TRUONG = "TO_TRUONG";
    private static final String THU_KY = "THU_KY";

    private final JdbcClient jdbc;
    private final BauChonService bauChon;
    private final CuocHopRepository cuocHopRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final ObjectMapper json;
    private final NguoiDungHienTaiService hienTai;
    private final PhanQuyenCuocHopService phanQuyen;
    private final PhanCongXuLyKeHoachService phanCong;
    private final NhomLienPhongService nhomService;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThongBaoService thongBao;
    private final CapNhatHoSoRealtime realtime;

    private record Nhom(int nhomId, String trangThai, Integer truongNhomId, Integer thuKyNhomId,
            Integer nguoiLapId, Integer nguoiKichHoatId) {}
    private record HoSo(int yeuCauId, int nguoiDeXuatId, String lyDo, boolean bauToTruong, boolean bauThuKy,
            String trangThai, LocalDateTime gioDuKien, String ungVienToTruong, String ungVienThuKy,
            Integer thuKyTiepNhanId, LocalDateTime ngayTiepNhan, Integer nguoiDuyetId, LocalDateTime ngayDuyet,
            String phanHoi, LocalDateTime ngayTao, Integer cuocHopId) {}
    private record Vong(int vongId, int cuocBauId, int soVong, String chucDanh, String trangThai, Integer nguoiTrungCuId,
            Integer nguoiXacNhanId, LocalDateTime ngayDong,
            LocalDateTime ngayXacNhan) {}
    private record SoPhieu(Integer ungVienId, int soPhieu) {}

    public record ThanhVien(Integer nguoiDungId, String hoTen, boolean duocLamToTruong, boolean duocLamThuKy) {}
    public record UngVien(Integer nguoiDungId, String hoTen, Integer soPhieu) {}
    public record ChiTietVong(int vongId, String chucDanh, String trangThai, List<UngVien> ungVien,
            int soCuTri, int soDaBoPhieu, boolean duocBoPhieu, boolean daBoPhieu, Integer nguoiTrungCuId,
            String tenNguoiXacNhan, LocalDateTime ngayXacNhan) {}
    public record BanGiao(int vongId, String chucDanh, String nguoiBanGiao, String nguoiNhan,
            String noiDung, LocalDateTime ngayGui, LocalDateTime ngayNhan, boolean duocGui, boolean duocNhan) {}
    public record ChiTiet(int yeuCauId, int nguoiDeXuatId, String tenNguoiDeXuat, String lyDo, boolean bauToTruong,
            boolean bauThuKy, String trangThai, LocalDateTime gioDuKien, List<Integer> ungVienToTruong,
            List<Integer> ungVienThuKy, String phanHoi, LocalDateTime ngayTao, String tenNguoiDuyet,
            LocalDateTime ngayDuyet, Integer cuocHopId, String trangThaiCuocHop, List<ChiTietVong> cacVong, List<BanGiao> banGiao) {}
    public record TrangBauLai(boolean thanhVien, boolean thuKy, boolean toTruong, boolean lanhDaoPhuTrach,
            boolean dangHoatDong, List<ThanhVien> thanhVienHoatDong, List<ChiTiet> hoSo) {}
    public record TrangTrongHop(Integer nhomId, TrangBauLai trang, List<ThanhVien> cuTriCoMat) {}
    public record YeuCauMoi(boolean bauToTruong, boolean bauThuKy, String lyDo) {}
    public record XuLy(Integer vongId, String chucDanh, LocalDateTime gioDuKien,
            List<Integer> ungVienToTruong, List<Integer> ungVienThuKy, List<Integer> cuTriIds,
            Integer ungVienId, String lyDo, Integer cuocHopId) {}

    @Transactional(readOnly = true)
    public TrangBauLai xem(int nhomId, int id) {
        hienTai.layTaiKhoanHoatDong(id);
        Nhom nhom = timNhom(nhomId, false);
        return trangBauLai(nhom, thanhVien(nhom), id, null);
    }

    @Transactional(readOnly = true)
    public TrangTrongHop xemTrongHop(int cuocHopId, int id) {
        hienTai.layTaiKhoanHoatDong(id);
        Integer nhomId = jdbc.sql("""
                SELECT nhom_id FROM nhom_lien_phong_de_xuat WHERE cuoc_hop_id=?
                """).param(cuocHopId).query(Integer.class).optional().orElse(null);
        if (nhomId == null) return null;
        Nhom nhom = timNhom(nhomId, false);
        var thanhVien = thanhVien(nhom);
        if (!coThanhVien(thanhVien, id)) return null;
        var trang = trangBauLai(nhom, thanhVien, id, cuocHopId);
        if (trang.hoSo().isEmpty()) return null;
        return new TrangTrongHop(nhomId, trang, cuTriCoMat(cuocHopId, thanhVien));
    }

    private TrangBauLai trangBauLai(Nhom nhom, List<ThanhVien> thanhVien, int id, Integer cuocHopId) {
        boolean laThanhVien = coThanhVien(thanhVien, id);
        boolean lanhDao = laLanhDaoPhuTrach(nhom, id);
        boolean duocXem = laThanhVien || lanhDao;
        return new TrangBauLai(laThanhVien, laThanhVien && Objects.equals(nhom.thuKyNhomId(), id),
                laThanhVien && Objects.equals(nhom.truongNhomId(), id), lanhDao, dangHoatDong(nhom),
                duocXem ? thanhVien : List.of(),
                duocXem ? layHoSo(nhom.nhomId()).stream()
                        .filter(hs -> cuocHopId == null || cuocHopId.equals(hs.cuocHopId()))
                        .map(hs -> chiTiet(hs, id)).toList() : List.of());
    }

    public void guiYeuCau(int nhomId, int id, YeuCauMoi request) {
        Nhom nhom = chuanBiThaoTac(nhomId, id);
        choPhep(coThanhVien(thanhVien(nhom), id), "Chỉ thành viên chính thức được đề xuất bầu lại");
        hopLe(request.bauToTruong() || request.bauThuKy(), "Chọn chức danh cần bầu lại");
        xungDot(layHoSo(nhomId).stream().noneMatch(hs -> !List.of("HOAN_TAT", "DA_HUY").contains(hs.trangThai())),
                "Nhóm đang có hồ sơ bầu lại chưa xử lý xong");
        jdbc.sql("""
                INSERT INTO nhom_bau_lai (nhom_id, nguoi_de_xuat_id, ly_do, bau_to_truong, bau_thu_ky)
                VALUES (?, ?, ?, ?, ?)
                """).params(nhomId, id, lyDo(request.lyDo()), request.bauToTruong(), request.bauThuKy()).update();
        thongBao.guiTheoId(null, id, "NHOM_BAU_LAI", "Có yêu cầu bầu lại cần tiếp nhận",
                lyDo(request.lyDo()), java.util.Collections.singletonList(nhom.thuKyNhomId()));
        realtime.baoDanhMucNhomThayDoi();
    }

    public void xuLy(int nhomId, int yeuCauId, String thaoTac, int id, XuLy request) {
        boolean banGiao = List.of("gui-ban-giao", "nhan-ban-giao").contains(thaoTac);
        hienTai.layTaiKhoanHoatDong(id);
        Nhom nhom = timNhom(nhomId, true);
        if (!banGiao) xungDot(dangHoatDong(nhom), "Chỉ xử lý bầu lại trong nhóm đang hoạt động");
        HoSo hs = layHoSo(nhomId).stream().filter(h -> h.yeuCauId() == yeuCauId).findFirst()
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ bầu lại của nhóm"));
        switch (thaoTac) {
            case "gui-ban-giao", "nhan-ban-giao" -> xuLyBanGiao(hs, id, request, "gui-ban-giao".equals(thaoTac));
            case "tiep-nhan" -> {
                laThuKy(nhom, id);
                oTrangThai(hs.trangThai(), "CHO_TIEP_NHAN");
                jdbc.sql("""
                        UPDATE nhom_bau_lai SET trang_thai='LAP_KE_HOACH', thu_ky_tiep_nhan_id=?,
                            ngay_tiep_nhan=CURRENT_TIMESTAMP WHERE yeu_cau_id=?
                        """).params(id, yeuCauId).update();
            }
            case "trinh" -> trinh(nhom, hs, id, request);
            case "duyet", "tra-lai" -> {
                laToTruong(nhom, id);
                oTrangThai(hs.trangThai(), "CHO_DUYET");
                if ("duyet".equals(thaoTac)) {
                    jdbc.sql("""
                            UPDATE nhom_bau_lai SET trang_thai='DA_DUYET', nguoi_duyet_id=?,
                                ngay_duyet=CURRENT_TIMESTAMP WHERE yeu_cau_id=?
                            """).params(id, yeuCauId).update();
                } else {
                    jdbc.sql("UPDATE nhom_bau_lai SET trang_thai='LAP_KE_HOACH', phan_hoi=? WHERE yeu_cau_id=?")
                            .params(lyDo(request.lyDo()), yeuCauId).update();
                }
            }
            case "huy" -> {
                boolean nguoiGuiRut = "CHO_TIEP_NHAN".equals(hs.trangThai()) && hs.nguoiDeXuatId() == id;
                choPhep(nguoiGuiRut || Objects.equals(nhom.truongNhomId(), id), "Chỉ Tổ trưởng được hủy hồ sơ đã tiếp nhận");
                xungDot(!List.of("HOAN_TAT", "DA_HUY").contains(hs.trangThai())
                        && cacVong(yeuCauId).stream().noneMatch(v -> "DANG_MO".equals(v.trangThai())),
                        "Cần đóng lượt đang mở trước khi hủy hồ sơ");
                jdbc.sql("UPDATE nhom_bau_lai SET trang_thai='DA_HUY', phan_hoi=? WHERE yeu_cau_id=?")
                        .params(lyDo(request.lyDo()), yeuCauId).update();
            }
            case "gan-cuoc-hop" -> ganCuocHop(nhom, hs, id, request.cuocHopId());
            case "tao-vong" -> {
                laThuKy(nhom, id);
                kiemTraTrongHop(hs, request.cuocHopId(), false);
                taoVong(nhom, hs, id, request.chucDanh());
            }
            case "mo-vong", "bo-phieu", "dong-vong", "xac-nhan", "bau-lai", "bo-vong-nhap" -> {
                oTrangThai(hs.trangThai(), "DA_DUYET");
                Vong vong = cacVong(yeuCauId).stream().filter(v -> Objects.equals(v.vongId(), request.vongId())).findFirst()
                        .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy lượt bầu của hồ sơ"));
                switch (thaoTac) {
                    case "mo-vong" -> {
                        laThuKy(nhom, id);
                        kiemTraTrongHop(hs, request.cuocHopId(), false);
                        moVong(nhom, hs, vong);
                    }
                    case "bo-phieu" -> {
                        if (hs.cuocHopId() != null) kiemTraTrongHop(hs, request.cuocHopId(), false);
                        boPhieu(nhom, hs, vong, id, request.ungVienId());
                    }
                    case "dong-vong" -> {
                        laThuKy(nhom, id);
                        if (hs.cuocHopId() != null) kiemTraTrongHop(hs, request.cuocHopId(), true);
                        dongVong(nhom, vong, id);
                    }
                    case "bo-vong-nhap" -> {
                        laThuKy(nhom, id);
                        oTrangThai(vong.trangThai(), "NHAP");
                        jdbc.sql("UPDATE vong_bau SET trang_thai='BAU_LAI' WHERE vong_bau_id=?")
                                .param(vong.vongId()).update();
                    }
                    case "bau-lai" -> {
                        kiemTraNguoiXacNhan(nhom, hs, vong, id);
                        oTrangThai(vong.trangThai(), "CHO_XAC_NHAN");
                        jdbc.sql("UPDATE vong_bau SET trang_thai='BAU_LAI' WHERE vong_bau_id=?")
                                .param(vong.vongId()).update();
                    }
                    default -> xacNhan(nhom, hs, vong, id);
                }
            }
            default -> throw loi(HttpStatus.BAD_REQUEST, "Thao tác bầu lại không hợp lệ");
        }
        switch (thaoTac) {
            case "tiep-nhan" -> thongBao.guiTheoId(null, id, "NHOM_BAU_LAI", "Yêu cầu bầu lại đã được tiếp nhận",
                    hs.lyDo(), List.of(hs.nguoiDeXuatId()));
            case "trinh" -> thongBao.guiTheoId(null, id, "NHOM_BAU_LAI", "Kế hoạch bầu lại chờ duyệt",
                    hs.lyDo(), java.util.Collections.singletonList(nhom.truongNhomId()));
            case "duyet", "tra-lai" -> thongBao.guiTheoId(null, id, "NHOM_BAU_LAI",
                    "duyet".equals(thaoTac) ? "Đã duyệt kế hoạch bầu lại" : "Kế hoạch bầu lại cần chỉnh sửa",
                    "duyet".equals(thaoTac) ? hs.lyDo() : request.lyDo(),
                    java.util.Arrays.asList(hs.nguoiDeXuatId(), nhom.thuKyNhomId()));
            default -> { }
        }
        realtime.baoDanhMucNhomThayDoi();
    }

    private void ganCuocHop(Nhom nhom, HoSo hs, int id, Integer cuocHopId) {
        laThuKy(nhom, id);
        oTrangThai(hs.trangThai(), "DA_DUYET");
        hopLe(cuocHopId != null, "Chọn cuộc họp để tổ chức bầu");
        xungDot(cacVong(hs.yeuCauId()).stream().noneMatch(v -> "DANG_MO".equals(v.trangThai())),
                "Đóng lượt đang nhận phiếu trước khi đổi cuộc họp");
        Integer nhomCuaHop = jdbc.sql("SELECT nhom_id FROM nhom_lien_phong_de_xuat WHERE cuoc_hop_id=?")
                .param(cuocHopId).query(Integer.class).optional().orElse(null);
        hopLe(Objects.equals(nhomCuaHop, nhom.nhomId()), "Chọn cuộc họp được đề xuất từ chính nhóm này");
        CuocHop hop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        xungDot(List.of("DA_DUYET", "DANG_DIEN_RA").contains(hop.getTrangThai()),
                "Chỉ gắn cuộc họp đã duyệt hoặc đang diễn ra");
        jdbc.sql("UPDATE nhom_bau_lai SET cuoc_hop_id=? WHERE yeu_cau_id=?")
                .params(cuocHopId, hs.yeuCauId()).update();
    }

    private void kiemTraTrongHop(HoSo hs, Integer cuocHopIdTuYeuCau, boolean coTheDongSauHop) {
        Integer cuocHopId = hs.cuocHopId();
        xungDot(cuocHopId != null, "Gắn hồ sơ bầu lại với một cuộc họp của nhóm trước");
        CuocHop hop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp đã gắn"));
        if (coTheDongSauHop && ThaoTacCuocHop.daKetThuc(hop)) return;
        xungDot(Objects.equals(cuocHopId, cuocHopIdTuYeuCau) && ThaoTacCuocHop.dangDienRa(hop),
                "Thực hiện thao tác trong cuộc họp đã gắn và đang diễn ra");
    }

    private List<ThanhVien> cuTriCoMat(int cuocHopId, List<ThanhVien> thanhVien) {
        var coMat = thanhVienCuocHopRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream().filter(tv -> "CO_MAT".equals(tv.getTrangThaiThamDu()) && tv.getThoiGianRoiCuocHop() == null)
                .map(tv -> tv.getId().getNguoiDungId()).collect(java.util.stream.Collectors.toSet());
        return thanhVien.stream().filter(tv -> coMat.contains(tv.nguoiDungId())).toList();
    }

    private void trinh(Nhom nhom, HoSo hs, int id, XuLy request) {
        laThuKy(nhom, id);
        oTrangThai(hs.trangThai(), "LAP_KE_HOACH");
        hopLe(request.gioDuKien() != null, "Nhập thời gian dự kiến bầu lại");
        var thanhVien = thanhVien(nhom);
        List<Integer> truong = hs.bauToTruong()
                ? kiemTraDanhSach(request.ungVienToTruong(), coTheUngCu(thanhVien, TO_TRUONG), "ứng viên Tổ trưởng") : List.of();
        List<Integer> thuKy = hs.bauThuKy()
                ? kiemTraDanhSach(request.ungVienThuKy(), coTheUngCu(thanhVien, THU_KY), "ứng viên Thư ký") : List.of();
        jdbc.sql("""
                UPDATE nhom_bau_lai SET trang_thai='CHO_DUYET', gio_du_kien=?, ung_vien_to_truong=?,
                    ung_vien_thu_ky=?, phan_hoi=NULL WHERE yeu_cau_id=?
                """).params(request.gioDuKien(), json.writeValueAsString(truong),
                        json.writeValueAsString(thuKy), hs.yeuCauId()).update();
    }

    private void taoVong(Nhom nhom, HoSo hs, int id, String chucDanh) {
        laThuKy(nhom, id);
        oTrangThai(hs.trangThai(), "DA_DUYET");
        boolean bauTruong = TO_TRUONG.equals(chucDanh);
        hopLe(bauTruong ? hs.bauToTruong() : THU_KY.equals(chucDanh) && hs.bauThuKy(),
                "Chức danh không nằm trong kế hoạch đã duyệt");
        var cacVong = cacVong(hs.yeuCauId());
        xungDot(cacVong.stream().noneMatch(v -> chucDanh.equals(v.chucDanh()) && !"BAU_LAI".equals(v.trangThai())),
                "Chức danh này đã có lượt bầu đang xử lý hoặc đã xác nhận");
        var hopLe = coTheUngCu(thanhVien(nhom), chucDanh);
        var ungVien = ids(bauTruong ? hs.ungVienToTruong() : hs.ungVienThuKy()).stream().filter(hopLe::contains).toList();
        xungDot(!ungVien.isEmpty(), "Không còn ứng viên đủ điều kiện trong kế hoạch đã duyệt");
        Vong truoc = cacVong.stream().filter(v -> chucDanh.equals(v.chucDanh())).findFirst().orElse(null);
        int cuocBau;
        if (truoc == null) {
            var khoa = new GeneratedKeyHolder();
            jdbc.sql("""
                    INSERT INTO cuoc_bau (bau_lai_yeu_cau_id, chuc_danh_nhom, tieu_de, nguoi_tao_id)
                    VALUES (?, ?, ?, ?)
                    """).params(hs.yeuCauId(), chucDanh, bauTruong ? "Bầu lại Tổ trưởng" : "Bầu lại Thư ký nhóm", id)
                    .update(khoa, "cuoc_bau_id");
            cuocBau = Objects.requireNonNull(khoa.getKey()).intValue();
            jdbc.sql("""
                    INSERT INTO cau_hinh_vong_bau (cuoc_bau_id, so_vong, so_nguoi_duoc_chon, so_lua_chon_moi_phieu)
                    VALUES (?, 1, 1, 1)
                    """).param(cuocBau).update();
        } else {
            cuocBau = truoc.cuocBauId();
        }
        var dsUngVien = ungVien.stream().map(nguoi -> new BauChonService.Nguoi(nguoi, hoTen(nguoi))).toList();
        bauChon.taoVong(cuocBau, truoc == null ? null : truoc.vongId(), truoc == null ? 1 : truoc.soVong() + 1,
                new BauChonService.CauHinhVong(1, 1), dsUngVien);
    }

    private void moVong(Nhom nhom, HoSo hs, Vong vong) {
        oTrangThai(vong.trangThai(), "NHAP");
        var thanhVien = thanhVien(nhom);
        kiemTraDanhSach(ungVienCuaVong(vong.vongId()).stream().map(UngVien::nguoiDungId).toList(), coTheUngCu(thanhVien, vong.chucDanh()), "ứng viên còn đủ điều kiện");
        var cuTri = cuTriCoMat(hs.cuocHopId(), thanhVien).stream().map(ThanhVien::nguoiDungId).toList();
        xungDot(!cuTri.isEmpty(), "Cần ít nhất một thành viên nhóm được điểm danh có mặt");
        bauChon.ganCuTri(vong.vongId(), cuTri);
        jdbc.sql("UPDATE vong_bau SET trang_thai='DANG_MO', ngay_mo_nhom=CURRENT_TIMESTAMP WHERE vong_bau_id=?")
                .param(vong.vongId()).update();
    }

    private void boPhieu(Nhom nhom, HoSo hs, Vong vong, int id, Integer ungVienId) {
        oTrangThai(vong.trangThai(), "DANG_MO");
        var thanhVien = thanhVien(nhom);
        boolean duocBoPhieu = coThanhVien(thanhVien, id) && laCuTri(vong.vongId(), id)
                && (hs.cuocHopId() == null || coThanhVien(cuTriCoMat(hs.cuocHopId(), thanhVien), id));
        choPhep(duocBoPhieu, "Bạn cần thuộc danh sách cử tri và đang có mặt trong cuộc họp");
        hopLe(ungVienId != null && ungVienCuaVong(vong.vongId()).stream().anyMatch(u -> ungVienId.equals(u.nguoiDungId())),
                "Chọn một ứng viên hợp lệ");
        xungDot(!daBoPhieu(vong.vongId(), id), "Bạn đã bỏ phiếu");
        bauChon.luuPhieu(vong.vongId(), id, List.of(ungVienId));
    }

    private void dongVong(Nhom nhom, Vong vong, int id) {
        laThuKy(nhom, id);
        oTrangThai(vong.trangThai(), "DANG_MO");
        var dem = kiemPhieu(vong.vongId());
        int nuaSoCuTri = demCuTri(vong.vongId()) / 2;
        Integer trungCu = dem.stream().filter(m -> m.soPhieu() > nuaSoCuTri)
                .map(SoPhieu::ungVienId).findFirst().orElse(null);
        jdbc.sql("""
                UPDATE vong_bau SET trang_thai=:trangThai, nguoi_trung_cu_nhom_id=:nguoi, ngay_dong_nhom=CURRENT_TIMESTAMP
                WHERE vong_bau_id=:vong
                """)
                .param("trangThai", trungCu == null ? "BAU_LAI" : "CHO_XAC_NHAN")
                .param("nguoi", trungCu, java.sql.Types.INTEGER).param("vong", vong.vongId()).update();
    }

    private void xacNhan(Nhom nhom, HoSo hs, Vong vong, int id) {
        kiemTraNguoiXacNhan(nhom, hs, vong, id);
        oTrangThai(vong.trangThai(), "CHO_XAC_NHAN");
        Integer ungVien = vong.nguoiTrungCuId();
        xungDot(ungVien != null && coTheUngCu(thanhVien(nhom), vong.chucDanh()).contains(ungVien),
                "Người trúng cử không còn đủ điều kiện. Hãy yêu cầu bầu lại.");
        boolean bauTruong = TO_TRUONG.equals(vong.chucDanh());
        nhomService.ganVaiTro(nhom.nhomId(), bauTruong ? ungVien : nhom.truongNhomId(),
                bauTruong ? nhom.thuKyNhomId() : ungVien);
        jdbc.sql("""
                UPDATE vong_bau SET trang_thai='DA_XAC_NHAN', nguoi_xac_nhan_nhom_id=?,
                    ngay_xac_nhan_nhom=CURRENT_TIMESTAMP WHERE vong_bau_id=?
                """).params(id, vong.vongId()).update();
        var daXacNhan = cacVong(hs.yeuCauId()).stream().filter(v -> "DA_XAC_NHAN".equals(v.trangThai())).toList();
        if ((!hs.bauToTruong() || daXacNhan.stream().anyMatch(v -> TO_TRUONG.equals(v.chucDanh())))
                && (!hs.bauThuKy() || daXacNhan.stream().anyMatch(v -> THU_KY.equals(v.chucDanh())))) {
            jdbc.sql("UPDATE nhom_bau_lai SET trang_thai='HOAN_TAT' WHERE yeu_cau_id=?").param(hs.yeuCauId()).update();
        }
        Integer nguoiCu = bauTruong ? nhom.truongNhomId() : nhom.thuKyNhomId();
        if (nguoiCu != null && !nguoiCu.equals(ungVien)) {
            jdbc.sql("""
                    INSERT INTO nhom_ban_giao_chuc_danh
                        (vong_bau_id, yeu_cau_id, nguoi_ban_giao_id, nguoi_nhan_id)
                    VALUES (?, ?, ?, ?)
                    """).params(vong.vongId(), hs.yeuCauId(), nguoiCu, ungVien).update();
            thongBao.gui(null, id, "NHOM_BAN_GIAO", "Cần bàn giao chức danh",
                    "Mở hồ sơ bầu lại của nhóm để ghi nội dung bàn giao cho " + hoTen(ungVien) + ".",
                    nguoiDungRepository.findById(nguoiCu).orElse(null));
        }
        thongBaoKetQuaChoNhom(nhom.nhomId(), id, ungVien, bauTruong);
    }

    private List<BanGiao> layBanGiao(int yeuCauId, int id) {
        return jdbc.sql("""
                SELECT bg.vong_bau_id AS vong_id, cb.chuc_danh_nhom AS chuc_danh,
                    cu.ho_ten AS nguoi_ban_giao, moi.ho_ten AS nguoi_nhan,
                    CASE WHEN :id IN (bg.nguoi_ban_giao_id, bg.nguoi_nhan_id) THEN bg.noi_dung END AS noi_dung,
                    bg.ngay_gui, bg.ngay_nhan,
                    (bg.nguoi_ban_giao_id = :id AND bg.ngay_gui IS NULL) AS duoc_gui,
                    (bg.nguoi_nhan_id = :id AND bg.ngay_gui IS NOT NULL AND bg.ngay_nhan IS NULL) AS duoc_nhan
                FROM nhom_ban_giao_chuc_danh bg
                JOIN vong_bau v ON v.vong_bau_id = bg.vong_bau_id
                JOIN cuoc_bau cb ON cb.cuoc_bau_id = v.cuoc_bau_id
                JOIN nguoi_dung cu ON cu.nguoi_dung_id = bg.nguoi_ban_giao_id
                JOIN nguoi_dung moi ON moi.nguoi_dung_id = bg.nguoi_nhan_id
                WHERE bg.yeu_cau_id = :yeuCau
                ORDER BY bg.vong_bau_id
                """).param("id", id).param("yeuCau", yeuCauId).query(BanGiao.class).list();
    }

    private void xuLyBanGiao(HoSo hs, int id, XuLy request, boolean gui) {
        hopLe(request.vongId() != null, "Thiếu lượt bầu cần bàn giao");
        record HaiBen(int nguoiBanGiaoId, int nguoiNhanId) {}
        var ben = jdbc.sql("""
                SELECT nguoi_ban_giao_id, nguoi_nhan_id FROM nhom_ban_giao_chuc_danh
                WHERE yeu_cau_id = ? AND vong_bau_id = ? FOR UPDATE
                """).params(hs.yeuCauId(), request.vongId()).query(HaiBen.class).optional()
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu bàn giao"));
        choPhep(id == (gui ? ben.nguoiBanGiaoId() : ben.nguoiNhanId()),
                "Bạn không được giao thực hiện bước bàn giao này");
        int soDong;
        if (gui) {
            String noiDung = request.lyDo() == null ? "" : request.lyDo().strip();
            hopLe(!noiDung.isEmpty() && noiDung.length() <= 2000, "Nhập nội dung bàn giao tối đa 2.000 ký tự");
            soDong = jdbc.sql("""
                    UPDATE nhom_ban_giao_chuc_danh SET noi_dung = ?, ngay_gui = CURRENT_TIMESTAMP
                    WHERE vong_bau_id = ? AND ngay_gui IS NULL
                    """).params(noiDung, request.vongId()).update();
        } else {
            soDong = jdbc.sql("""
                    UPDATE nhom_ban_giao_chuc_danh SET ngay_nhan = CURRENT_TIMESTAMP
                    WHERE vong_bau_id = ? AND ngay_gui IS NOT NULL AND ngay_nhan IS NULL
                    """).param(request.vongId()).update();
        }
        xungDot(soDong == 1, "Phiếu bàn giao không còn ở bước này");
        thongBao.gui(null, id, "NHOM_BAN_GIAO", gui ? "Có hồ sơ bàn giao cần tiếp nhận" : "Đã xác nhận tiếp nhận bàn giao",
                "Xem nội dung và thời gian xác nhận trong hồ sơ bầu lại của nhóm.",
                nguoiDungRepository.findById(gui ? ben.nguoiNhanId() : ben.nguoiBanGiaoId()).orElse(null));
    }

    private void thongBaoKetQuaChoNhom(int nhomId, int nguoiXacNhan, int nguoiTrungCu, boolean bauTruong) {
        var ids = jdbc.sql("SELECT nguoi_dung_id FROM nhom_lien_phong_thanh_vien WHERE nhom_id=?")
                .param(nhomId).query(Integer.class).list();
        NguoiDung[] nguoiNhan = nguoiDungRepository.findAllById(ids).stream()
                .filter(NguoiDungHienTaiService::dangHoatDong).toArray(NguoiDung[]::new);
        String tenNhom = jdbc.sql("SELECT ten_nhom FROM nhom_lien_phong WHERE nhom_id=?")
                .param(nhomId).query(String.class).single();
        String nguoiDuyet = bauTruong ? "Lãnh đạo" : "Tổ trưởng";
        String chucDanh = bauTruong ? "Tổ trưởng" : "Thư ký nhóm";
        thongBao.gui(null, nguoiXacNhan, "NHOM_XAC_NHAN_KET_QUA_BAU",
                "Đã xác nhận kết quả bầu " + chucDanh,
                nguoiDuyet + " " + hoTen(nguoiXacNhan) + " đã xác nhận kết quả bầu cử. "
                        + hoTen(nguoiTrungCu) + " chính thức giữ chức danh " + chucDanh + " tại nhóm " + tenNhom + ".",
                nguoiNhan);
    }

    private void kiemTraNguoiXacNhan(Nhom nhom, HoSo hs, Vong vong, int id) {
        if (TO_TRUONG.equals(vong.chucDanh())) {
            choPhep(laLanhDaoPhuTrach(nhom, id), "Chỉ lãnh đạo phụ trách xác nhận kết quả bầu Tổ trưởng");
        } else {
            laToTruong(nhom, id);
            xungDot(!hs.bauToTruong() || cacVong(hs.yeuCauId()).stream()
                    .anyMatch(v -> TO_TRUONG.equals(v.chucDanh()) && "DA_XAC_NHAN".equals(v.trangThai())),
                    "Cần xác nhận Tổ trưởng mới trước");
        }
    }

    private List<HoSo> layHoSo(int nhomId) {
        return jdbc.sql("SELECT * FROM nhom_bau_lai WHERE nhom_id=? ORDER BY yeu_cau_id DESC")
                .param(nhomId).query(HoSo.class).list();
    }

    private List<Vong> cacVong(int yeuCauId) {
        return jdbc.sql("""
                SELECT v.vong_bau_id AS vong_id, v.cuoc_bau_id, v.so_vong, cb.chuc_danh_nhom AS chuc_danh,
                    v.trang_thai, v.nguoi_trung_cu_nhom_id AS nguoi_trung_cu_id,
                    v.nguoi_xac_nhan_nhom_id AS nguoi_xac_nhan_id, v.ngay_dong_nhom AS ngay_dong,
                    v.ngay_xac_nhan_nhom AS ngay_xac_nhan
                FROM vong_bau v JOIN cuoc_bau cb ON cb.cuoc_bau_id=v.cuoc_bau_id
                WHERE cb.bau_lai_yeu_cau_id=? AND cb.cuoc_hop_id IS NULL ORDER BY v.vong_bau_id DESC
                """).param(yeuCauId).query(Vong.class).list();
    }

    private List<UngVien> ungVienCuaVong(int vongId) {
        return jdbc.sql("""
                SELECT nguoi_dung_id, ho_ten, NULL AS so_phieu
                FROM ung_vien_vong_bau WHERE vong_bau_id=? ORDER BY ho_ten, nguoi_dung_id
                """).param(vongId).query(UngVien.class).list();
    }

    private List<SoPhieu> kiemPhieu(int vongId) {
        return jdbc.sql("""
                SELECT ung_vien_id, COUNT(*) AS so_phieu FROM lua_chon_phieu_bau
                WHERE vong_bau_id=? GROUP BY ung_vien_id
                """).param(vongId).query(SoPhieu.class).list();
    }

    private int demCuTri(int vongId) {
        return jdbc.sql("SELECT COUNT(*) FROM cu_tri_vong_bau WHERE vong_bau_id=?")
                .param(vongId).query(Integer.class).single();
    }

    private boolean laCuTri(int vongId, int id) {
        return jdbc.sql("SELECT EXISTS (SELECT 1 FROM cu_tri_vong_bau WHERE vong_bau_id=? AND nguoi_dung_id=?)")
                .params(vongId, id).query(Boolean.class).single();
    }

    private boolean daBoPhieu(int vongId, int id) {
        return jdbc.sql("SELECT EXISTS (SELECT 1 FROM phieu_bau WHERE vong_bau_id=? AND nguoi_bo_phieu_id=?)")
                .params(vongId, id).query(Boolean.class).single();
    }

    private ChiTiet chiTiet(HoSo hs, int id) {
        return new ChiTiet(hs.yeuCauId(), hs.nguoiDeXuatId(), hoTen(hs.nguoiDeXuatId()), hs.lyDo(), hs.bauToTruong(),
                hs.bauThuKy(), hs.trangThai(), hs.gioDuKien(), ids(hs.ungVienToTruong()), ids(hs.ungVienThuKy()),
                hs.phanHoi(), hs.ngayTao(), hoTen(hs.nguoiDuyetId()), hs.ngayDuyet(), hs.cuocHopId(),
                hs.cuocHopId() == null ? null : jdbc.sql("SELECT trang_thai FROM cuoc_hop WHERE cuoc_hop_id=?")
                        .param(hs.cuocHopId()).query(String.class).optional().orElse(null),
                cacVong(hs.yeuCauId()).stream().map(v -> chiTietVong(hs, v, id)).toList(),
                layBanGiao(hs.yeuCauId(), id));
    }

    private ChiTietVong chiTietVong(HoSo hs, Vong v, int id) {
        boolean dangMo = "DANG_MO".equals(v.trangThai());
        var dem = kiemPhieu(v.vongId());
        List<UngVien> ungVien = ungVienCuaVong(v.vongId()).stream().map(nguoi -> new UngVien(
                nguoi.nguoiDungId(), nguoi.hoTen(), v.ngayDong() == null ? null
                        : dem.stream().filter(p -> nguoi.nguoiDungId().equals(p.ungVienId()))
                            .map(SoPhieu::soPhieu).findFirst().orElse(0))).toList();
        boolean daBoPhieu = dangMo && daBoPhieu(v.vongId(), id);
        boolean dangCoMat = hs.cuocHopId() == null || thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(hs.cuocHopId()).stream()
                .anyMatch(tv -> Objects.equals(tv.getId().getNguoiDungId(), id)
                        && "CO_MAT".equals(tv.getTrangThaiThamDu()) && tv.getThoiGianRoiCuocHop() == null);
        return new ChiTietVong(v.vongId(), v.chucDanh(), v.trangThai(), ungVien, demCuTri(v.vongId()),
                dem.stream().mapToInt(SoPhieu::soPhieu).sum(), dangMo && dangCoMat && laCuTri(v.vongId(), id), daBoPhieu,
                v.nguoiTrungCuId(), hoTen(v.nguoiXacNhanId()), v.ngayXacNhan());
    }

    private String hoTen(Integer id) {
        return id == null ? null : jdbc.sql("SELECT ho_ten FROM nguoi_dung WHERE nguoi_dung_id=?")
                .param(id).query(String.class).optional().orElse(null);
    }

    private Nhom chuanBiThaoTac(int nhomId, int id) {
        hienTai.layTaiKhoanHoatDong(id);
        Nhom nhom = timNhom(nhomId, true);
        xungDot(dangHoatDong(nhom), "Chỉ xử lý bầu lại trong nhóm đang hoạt động");
        return nhom;
    }

    private Nhom timNhom(int nhomId, boolean khoa) {
        return jdbc.sql("""
                SELECT nhom_id, trang_thai, truong_nhom_id, thu_ky_nhom_id, nguoi_lap_id, nguoi_kich_hoat_id
                FROM nhom_lien_phong WHERE nhom_id=?
                """ + (khoa ? " FOR UPDATE" : "")).param(nhomId).query(Nhom.class).optional()
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm"));
    }

    private List<ThanhVien> thanhVien(Nhom nhom) {
        record Nguoi(Integer nguoiDungId, String hoTen) {}
        return jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten
                FROM nhom_lien_phong_thanh_vien tv JOIN nguoi_dung nd ON nd.nguoi_dung_id=tv.nguoi_dung_id
                WHERE tv.nhom_id=? AND nd.trang_thai='HOAT_DONG' AND nd.phong_ban_id=tv.phong_ban_id
                ORDER BY nd.ho_ten, nd.nguoi_dung_id
                """).param(nhom.nhomId()).query(Nguoi.class).list().stream().map(nguoi -> {
            Integer id = nguoi.nguoiDungId();
            boolean dangGiuChucDanh = Objects.equals(id, nhom.truongNhomId()) || Objects.equals(id, nhom.thuKyNhomId());
            return new ThanhVien(id, nguoi.hoTen(), !dangGiuChucDanh && phanQuyen.laTruongPhong(id),
                    !dangGiuChucDanh && phanCong.laNguoiCoTheLamThuKy(hienTai.layTaiKhoanHoatDong(id)));
        }).toList();
    }

    private List<Integer> coTheUngCu(List<ThanhVien> ds, String chucDanh) {
        return ds.stream().filter(v -> TO_TRUONG.equals(chucDanh) ? v.duocLamToTruong() : v.duocLamThuKy())
                .map(ThanhVien::nguoiDungId).toList();
    }

    private List<Integer> ids(String duLieu) {
        return duLieu == null ? List.of() : json.readerForListOf(Integer.class).readValue(duLieu);
    }

    private boolean coThanhVien(List<ThanhVien> ds, int id) {
        return ds.stream().anyMatch(v -> v.nguoiDungId() == id);
    }

    private boolean laLanhDaoPhuTrach(Nhom nhom, int id) {
        return (Objects.equals(nhom.nguoiKichHoatId(), id) || Objects.equals(nhom.nguoiLapId(), id))
                && phanQuyen.coVaiTro(id, "LANH_DAO");
    }

    private boolean dangHoatDong(Nhom nhom) {
        return "HOAT_DONG".equals(nhom.trangThai());
    }

    private void laThuKy(Nhom nhom, int id) {
        choPhep(Objects.equals(nhom.thuKyNhomId(), id), "Chỉ thư ký nhóm được thao tác");
    }

    private void laToTruong(Nhom nhom, int id) {
        choPhep(Objects.equals(nhom.truongNhomId(), id), "Chỉ Tổ trưởng được thao tác");
    }

    private List<Integer> kiemTraDanhSach(List<Integer> ds, List<Integer> hopLe, String nhan) {
        hopLe(ds != null && !ds.isEmpty() && new HashSet<>(ds).size() == ds.size() && hopLe.containsAll(ds),
                "Chọn danh sách " + nhan + " hợp lệ");
        return ds;
    }

    private String lyDo(String noiDung) {
        hopLe(noiDung != null && !noiDung.isBlank() && noiDung.strip().length() <= 500, "Nhập lý do từ 1 đến 500 ký tự");
        return noiDung.strip();
    }

    private void oTrangThai(String trangThai, String yeuCau) {
        xungDot(yeuCau.equals(trangThai), "Hồ sơ hoặc lượt bầu đã chuyển bước. Hãy tải lại.");
    }

    private void choPhep(boolean hopLe, String loi) {
        if (!hopLe) throw loi(HttpStatus.FORBIDDEN, loi);
    }

    private void hopLe(boolean hopLe, String loi) {
        if (!hopLe) throw loi(HttpStatus.BAD_REQUEST, loi);
    }

    private void xungDot(boolean hopLe, String loi) {
        if (!hopLe) throw loi(HttpStatus.CONFLICT, loi);
    }

    private ResponseStatusException loi(HttpStatus trangThai, String thongBao) {
        return new ResponseStatusException(trangThai, thongBao);
    }
}
