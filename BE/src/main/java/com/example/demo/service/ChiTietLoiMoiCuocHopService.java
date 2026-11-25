package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;
import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChiTietLoiMoiCuocHopService {
    private final ThanhVienCuocHopRepository thanhVienRepository;
    private final NguoiDungHienTaiService nguoiDungHienTai;
    private final TaiLieuCuocHopService taiLieuDeXuat;
    private final TaiLieuKeHoachService taiLieuKeHoach;
    private final ThanhVienCuocHopService thanhVienService;

    public record TomTat(Integer cuocHopId, String maCuocHop, String tieuDe, String trangThaiMoi,
            LocalDateTime ngayMoi, LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc,
            String tenPhongHop, String loaiHinh, String tenNguoiChuTri, boolean coTheXemChiTiet) {}

    public record Tep(Integer taiLieuId, String ten, String dinhDang, String duongDan) {}

    public record ChiTiet(TomTat loiMoi, String phamVi, String tenNguoiDeXuat, String phongBanChuTri,
            String tenThuKyCuocHop, String mucTieu, String noiDungDuKien, boolean dangChoDoiLich, List<Tep> deXuat, List<Tep> keHoach) {}

    @Transactional(readOnly = true)
    public List<TomTat> danhSach(Integer nguoiDungId) {
        nguoiDungHienTai.layTaiKhoanHoatDong(nguoiDungId);
        return thanhVienRepository.findById_NguoiDungIdAndVaiTroTaiCuocHopAndTrangThaiMoiIn(
                nguoiDungId, "THAM_DU", List.of("CHO_PHAN_HOI", "DA_XAC_NHAN", "TU_CHOI")).stream()
                .filter(tv -> !"CHO_PHAN_HOI".equals(tv.getTrangThaiMoi()) || thanhVienService.coLichKeHoachDaDuyet(tv.getCuocHop()))
                .sorted(Comparator.comparing(ThanhVienCuocHop::getNgayMoi, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(tv -> tomTat(tv, !"TU_CHOI".equals(tv.getTrangThaiMoi())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ChiTiet xem(Integer cuocHopId, Integer nguoiDungId) {
        var thanhVien = kiemTraQuyenXem(cuocHopId, nguoiDungId);
        var cuocHop = thanhVien.getCuocHop();
        return new ChiTiet(tomTat(thanhVien, true), cuocHop.getPhamViCuocHop(), hoTen(cuocHop.getNguoiDeXuat()),
                cuocHop.getPhongBanChuTri() == null ? null : cuocHop.getPhongBanChuTri().getTenPhongBan(),
                hoTen(cuocHop.getThuKyCuocHop()), cuocHop.getMucTieu(), cuocHop.getNoiDungDuKien(),
                ThaoTacCuocHop.choXacNhanDoiLich(cuocHop),
                danhSachTep(cuocHopId, "de-xuat", taiLieuDeXuat.taiLieuDeXuat(cuocHopId)),
                danhSachTep(cuocHopId, "ke-hoach", taiLieuKeHoach.taiLieuDaDuyetCuaPhongHop(cuocHopId)));
    }

    @Transactional(readOnly = true)
    public TaiLieuCuocHopService.FileDeXuat docTep(Integer cuocHopId, Integer nguoiDungId, String nhom, Integer taiLieuId) {
        kiemTraQuyenXem(cuocHopId, nguoiDungId);
        return switch (nhom) {
            case "de-xuat" -> taiLieuDeXuat.docTepDeXuat(cuocHopId, taiLieuId);
            case "ke-hoach" -> {
                var tep = taiLieuKeHoach.docTepDaDuyetCuaPhongHop(cuocHopId, taiLieuId);
                yield new TaiLieuCuocHopService.FileDeXuat(tep.noiDung(), tep.tenFile());
            }
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm tài liệu");
        };
    }

    private ThanhVienCuocHop kiemTraQuyenXem(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTai.layTaiKhoanHoatDong(nguoiDungId);
        var id = new ThanhVienCuocHopId();
        id.setCuocHopId(cuocHopId);
        id.setNguoiDungId(nguoiDungId);
        return thanhVienRepository.findById(id)
                .filter(tv -> "THAM_DU".equals(tv.getVaiTroTaiCuocHop()))
                .filter(tv -> "DA_XAC_NHAN".equals(tv.getTrangThaiMoi())
                        || ("CHO_PHAN_HOI".equals(tv.getTrangThaiMoi()) && thanhVienService.coLichKeHoachDaDuyet(tv.getCuocHop())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lời mời đang có hiệu lực"));
    }

    private TomTat tomTat(ThanhVienCuocHop thanhVien, boolean coTheXem) {
        var cuocHop = thanhVien.getCuocHop();
        LichHop lich = ThaoTacCuocHop.lichHienHanh(cuocHop);
        return new TomTat(cuocHop.getCuocHopId(), cuocHop.getMaCuocHop(), cuocHop.getTieuDe(),
                thanhVien.getTrangThaiMoi(), thanhVien.getNgayMoi(), lich == null ? null : lich.getThoiGianBatDau(),
                lich == null ? null : lich.getThoiGianKetThuc(),
                lich == null || lich.getPhongHop() == null ? null : lich.getPhongHop().getTenPhongHop(),
                cuocHop.getLoaiHinh(), hoTen(cuocHop.getNguoiChuTri()), coTheXem);
    }

    private List<Tep> danhSachTep(Integer cuocHopId, String nhom, List<TaiLieuCuocHop> taiLieu) {
        return taiLieu.stream().map(tep -> new Tep(tep.getTaiLieuId(),
                Objects.requireNonNullElse(tep.getTenFileGoc(), Objects.toString(tep.getTenTaiLieu(), "Tài liệu")), tep.getDinhDangFile(),
                "/api/cuoc-hop/" + cuocHopId + "/thanh-vien/loi-moi/" + nhom + "/tep/" + tep.getTaiLieuId())).toList();
    }

    private String hoTen(NguoiDung nguoiDung) {
        return nguoiDung == null ? null : nguoiDung.getHoTen();
    }
}
