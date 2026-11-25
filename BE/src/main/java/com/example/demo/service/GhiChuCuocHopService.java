package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GhiChuCuocHopService {
    private final JdbcClient jdbc;
    private final CuocHopRepository cuocHopRepository;
    private final QuyenBienBanService quyenBienBanService;
    private final BienBanCuocHopRepository bienBanRepository;
    private final ThanhVienCuocHopRepository thanhVienRepository;
    private final BieuQuyetService bieuQuyetService;

    public record GhiChu(Integer ghiChuId, String chuDe, String nguoiPhatBieu,
            String yKienChinh, String noiDungChot, int phienBan) {}

    public record LuuGhiChu(String chuDe, String nguoiPhatBieu, String yKienChinh,
            String noiDungChot, Integer phienBan) {}

    public record DuLieuBanNhap(String noiDungGoc, String noiDungChinhThuc) {}

    @Transactional(readOnly = true)
    public List<GhiChu> xem(Integer cuocHopId, Integer nguoiDungId) {
        CuocHop hop = cuocHopRepository.findById(cuocHopId).orElseThrow(this::khongTimThayCuocHop);
        kiemTraQuyenGhiChu(hop, nguoiDungId);
        return danhSach(cuocHopId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<GhiChu> luu(Integer cuocHopId, Integer ghiChuId, Integer nguoiDungId, LuuGhiChu request) {
        CuocHop hop = cuocHopRepository.findByIdForUpdate(cuocHopId).orElseThrow(this::khongTimThayCuocHop);
        kiemTraQuyenGhiChu(hop, nguoiDungId);
        if (ghiChuId != null && (request.phienBan() == null || request.phienBan() < 1)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu phiên bản ghi chú cần sửa");
        }
        String chuDe = chuanHoa(request.chuDe(), 255, "Chủ đề");
        String nguoiPhatBieu = chuanHoa(request.nguoiPhatBieu(), 190, "Người phát biểu");
        String yKienChinh = chuanHoa(request.yKienChinh(), 4000, "Ý kiến chính");
        String noiDungChot = chuanHoa(request.noiDungChot(), 4000, "Nội dung chủ trì chốt");
        if (chuDe.isEmpty() || (yKienChinh.isEmpty() && noiDungChot.isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nhập chủ đề và ít nhất một nội dung ghi chú");
        }

        String sql = ghiChuId == null ? """
                INSERT INTO ghi_chu_cuoc_hop (cuoc_hop_id, chu_de, nguoi_phat_bieu, y_kien_chinh, noi_dung_chot)
                VALUES (:hop, :chuDe, NULLIF(:nguoiPhatBieu, ''), :yKien, :noiDungChot)
                """ : """
                UPDATE ghi_chu_cuoc_hop
                SET chu_de = :chuDe, nguoi_phat_bieu = NULLIF(:nguoiPhatBieu, ''),
                    y_kien_chinh = :yKien, noi_dung_chot = :noiDungChot, phien_ban = phien_ban + 1
                WHERE cuoc_hop_id = :hop AND ghi_chu_id = :id AND phien_ban = :phienBan
                """;
        var lenh = jdbc.sql(sql).param("hop", cuocHopId).param("chuDe", chuDe)
                .param("nguoiPhatBieu", nguoiPhatBieu).param("yKien", yKienChinh).param("noiDungChot", noiDungChot);
        if (ghiChuId != null) {
            lenh.param("id", ghiChuId).param("phienBan", request.phienBan());
        }
        kiemTraDaCapNhat(lenh.update());
        return danhSach(cuocHopId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<GhiChu> xoa(Integer cuocHopId, Integer ghiChuId, int phienBan, Integer nguoiDungId) {
        CuocHop hop = cuocHopRepository.findByIdForUpdate(cuocHopId).orElseThrow(this::khongTimThayCuocHop);
        kiemTraQuyenGhiChu(hop, nguoiDungId);
        int soDong = jdbc.sql("""
                DELETE FROM ghi_chu_cuoc_hop
                WHERE cuoc_hop_id = :hop AND ghi_chu_id = :id AND phien_ban = :phienBan
                """).param("hop", cuocHopId).param("id", ghiChuId).param("phienBan", phienBan).update();
        kiemTraDaCapNhat(soDong);
        return danhSach(cuocHopId);
    }

    @Transactional(readOnly = true)
    public DuLieuBanNhap taoDuLieuBanNhap(Integer cuocHopId, Integer nguoiDungId) {
        CuocHop hop = cuocHopRepository.findById(cuocHopId).orElseThrow(this::khongTimThayCuocHop);
        quyenBienBanService.kiemTraQuyenSoan(hop, nguoiDungId);
        if (!"DA_KET_THUC".equals(hop.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ được lấy ghi chú cho biên bản sau khi cuộc họp kết thúc");
        }
        bienBanRepository.findByCuocHop_CuocHopId(cuocHopId).ifPresent(bienBan -> {
            if (!List.of("NHAP", "BI_TRA_LAI").contains(bienBan.getTrangThai())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Biên bản hiện không thể chỉnh sửa");
            }
        });

        List<GhiChu> cacGhiChu = danhSach(cuocHopId);
        if (cacGhiChu.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cuộc họp chưa có ghi chú đã lưu");
        }
        StringBuilder ghiChu = new StringBuilder("GHI CHÚ CUỘC HỌP");
        for (GhiChu muc : cacGhiChu) {
            ghiChu.append("\n\n").append(muc.chuDe());
            themDong(ghiChu, "Người phát biểu", muc.nguoiPhatBieu());
            themDong(ghiChu, "Ý kiến chính", muc.yKienChinh());
            themDong(ghiChu, "Nội dung chủ trì chốt", muc.noiDungChot());
        }
        StringBuilder noiDung = new StringBuilder(ghiChu).append("\n\nĐIỂM DANH THÀNH VIÊN NỘI BỘ");
        List<ThanhVienCuocHop> thanhVien = thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream().filter(tv -> !List.of("TU_CHOI", "DA_HUY").contains(tv.getTrangThaiMoi())).toList();
        if (thanhVien.isEmpty()) {
            noiDung.append("\nChưa có thành viên nội bộ để tổng hợp.");
        }
        for (ThanhVienCuocHop tv : thanhVien) {
            String trangThai = switch (Objects.toString(tv.getTrangThaiThamDu(), "")) {
                case "CO_MAT" -> tv.getThoiGianRoiCuocHop() == null ? "Có mặt" : "Có mặt, đã rời cuộc họp";
                case "VANG_MAT" -> "Vắng mặt";
                default -> "Chưa điểm danh";
            };
            noiDung.append("\n- ").append(tv.getNguoiDung().getHoTen()).append(" — ").append(trangThai);
        }

        noiDung.append("\n\nKẾT QUẢ BIỂU QUYẾT");
        var cacKetQua = bieuQuyetService.layTongHopDaDong(cuocHopId);
        if (cacKetQua.isEmpty()) {
            noiDung.append("\nChưa có biểu quyết đã đóng.");
        }
        for (var muc : cacKetQua) {
            var ketQua = muc.ketQua();
            noiDung.append("\n- ").append(muc.tieuDe()).append(" — ").append(ketQua.tongSoPhieu())
                    .append('/').append(ketQua.soNguoiDuocBieuQuyet()).append(" người đã bỏ phiếu");
            for (var luaChon : ketQua.luaChon()) {
                noiDung.append("\n  • ").append(luaChon.noiDung()).append(" — ").append(luaChon.soPhieu()).append(" phiếu");
            }
        }
        return new DuLieuBanNhap(ghiChu.toString(), noiDung.toString());
    }

    private List<GhiChu> danhSach(Integer cuocHopId) {
        return jdbc.sql("""
                SELECT ghi_chu_id, chu_de, nguoi_phat_bieu, y_kien_chinh, noi_dung_chot, phien_ban
                FROM ghi_chu_cuoc_hop WHERE cuoc_hop_id = :hop ORDER BY ghi_chu_id
                """).param("hop", cuocHopId).query(GhiChu.class).list();
    }

    private void kiemTraQuyenGhiChu(CuocHop hop, Integer nguoiDungId) {
        quyenBienBanService.kiemTraQuyenSoan(hop, nguoiDungId);
        if (!"DANG_DIEN_RA".equals(hop.getTrangThai()) || !ThaoTacCuocHop.dangDienRa(hop)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ ghi chú khi cuộc họp đang diễn ra");
        }
    }

    private void kiemTraDaCapNhat(int soDong) {
        if (soDong == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ghi chú đã thay đổi hoặc không còn tồn tại. Hãy tải lại danh sách");
        }
    }

    private String chuanHoa(String giaTri, int gioiHan, String tenTruong) {
        String noiDung = Objects.toString(giaTri, "").strip();
        if (noiDung.length() > gioiHan) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    tenTruong + " tối đa " + gioiHan + " ký tự");
        }
        return noiDung;
    }

    private void themDong(StringBuilder noiDung, String nhan, String giaTri) {
        if (giaTri != null && !giaTri.isBlank()) {
            noiDung.append('\n').append(nhan).append(" — ").append(giaTri);
        }
    }

    private ResponseStatusException khongTimThayCuocHop() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp");
    }
}
