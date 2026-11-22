package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongBan;
import com.example.demo.entity.YeuCauPheDuyet;
import java.time.LocalDateTime;
import java.util.Objects;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.YeuCauPheDuyet;

final class ThaoTacCuocHop {
    private ThaoTacCuocHop() { }

    static <T> T thucHien(Supplier<T> thaoTac) {
        try {
            return thaoTac.get();
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    static void thucHien(Runnable thaoTac) {
        thucHien(() -> { thaoTac.run(); return null; });
    }

    static boolean laCuocHopNoiBo(CuocHop cuocHop) {
        return "NOI_BO".equals(cuocHop.getPhamViCuocHop());
    }

    static boolean laCuocHopLienPhong(CuocHop cuocHop) {
        return "LIEN_PHONG_BAN".equals(cuocHop.getPhamViCuocHop());
    }

    static boolean laCuocHopTrucTuyen(CuocHop cuocHop) {
        return "TRUC_TUYEN".equals(cuocHop.getLoaiHinh());
    }

    static boolean laCuocHopTrucTiep(CuocHop cuocHop) {
        return "TRUC_TIEP".equals(cuocHop.getLoaiHinh());
    }

    static boolean coThongTinCoBanHopLe(CuocHop cuocHop) {
        return cuocHop.getTieuDe() != null
                && !cuocHop.getTieuDe().isBlank()
                && cuocHop.getNguoiTao() != null
                && cuocHop.getNguoiDeXuat() != null;
    }

    static boolean daDuocPheDuyet(CuocHop cuocHop) {
        return "DA_DUYET".equals(cuocHop.getTrangThai());
    }



    static boolean biTuChoi(CuocHop cuocHop) {
        return "TU_CHOI".equals(cuocHop.getTrangThai());
    }

    static boolean coTheTrinhPheDuyet(CuocHop cuocHop) {
        return dangLapKeHoach(cuocHop) && !daBatDau(cuocHop) && !daKetThuc(cuocHop);
    }

    static boolean coTheTiepTucXuLy(CuocHop cuocHop) {
        return !"TU_CHOI".equals(cuocHop.getTrangThai())
                && !"DA_HUY".equals(cuocHop.getTrangThai())
                && !"DA_KET_THUC".equals(cuocHop.getTrangThai());
    }

    static boolean daBatDau(CuocHop cuocHop) {
        return cuocHop.getThoiGianBatDauThucTe() != null;
    }

    static boolean daKetThuc(CuocHop cuocHop) {
        return cuocHop.getThoiGianKetThucThucTe() != null;
    }

    static boolean dangDienRa(CuocHop cuocHop) {
        return daBatDau(cuocHop) && !daKetThuc(cuocHop);
    }

    static boolean coTheCapNhat(CuocHop cuocHop) {
        return (laDeXuatNhap(cuocHop) || dangLapKeHoach(cuocHop))
                && !daBatDau(cuocHop)
                && !daKetThuc(cuocHop);
    }

    static boolean coTheHuy(CuocHop cuocHop) {
        return !daBatDau(cuocHop)
                && !"DA_KET_THUC".equals(cuocHop.getTrangThai())
                && !"DA_HUY".equals(cuocHop.getTrangThai());
    }

    static void capNhatThongTin(
            CuocHop cuocHop,
            String tieuDe,
            String mucTieu,
            String noiDungDuKien,
            String loaiHinh,
            String phamViCuocHop) {

        if (!coTheCapNhat(cuocHop)) {
            throw new IllegalStateException(
                    "Cuộc họp không được phép chỉnh sửa"
            );
        }

        cuocHop.setTieuDe(tieuDe);
        cuocHop.setMucTieu(mucTieu);
        cuocHop.setNoiDungDuKien(noiDungDuKien);
        cuocHop.setLoaiHinh(loaiHinh);
        cuocHop.setPhamViCuocHop(phamViCuocHop);
    }

    static void batDauCuocHop(CuocHop cuocHop) {
        if (!coTheLapLich(cuocHop)) {
            throw new IllegalStateException("Cuộc họp chưa ở bước được phép bắt đầu");
        }
        cuocHop.setThoiGianBatDauThucTe(LocalDateTime.now());
        cuocHop.setTrangThai("DANG_DIEN_RA");
        cuocHop.setBuocXuLyHienTai("DANG_HOP");
    }

    static void ketThucCuocHop(CuocHop cuocHop) {
        if (!dangDienRa(cuocHop)) {
            throw new IllegalStateException(
                    "Cuộc họp chưa bắt đầu hoặc đã kết thúc"
            );
        }

        cuocHop.setThoiGianKetThucThucTe(LocalDateTime.now());
        cuocHop.setTrangThai("DA_KET_THUC");
        cuocHop.setBuocXuLyHienTai("SAU_CUOC_HOP");
    }

    static void huyCuocHop(CuocHop cuocHop) {
        if (!coTheHuy(cuocHop)) {
            throw new IllegalStateException(
                    "Cuộc họp không thể hủy"
            );
        }

        cuocHop.setTrangThai("DA_HUY");
        cuocHop.setBuocXuLyHienTai("DA_HUY");
    }

    static boolean laDeXuatNhap(CuocHop cuocHop) {
        return "NHAP".equals(cuocHop.getTrangThai())
                && "NHAP".equals(cuocHop.getBuocXuLyHienTai());
    }

    static boolean dangLapKeHoach(CuocHop cuocHop) {
        return "DANG_CHUAN_BI".equals(cuocHop.getTrangThai())
                && "LAP_KE_HOACH".equals(cuocHop.getBuocXuLyHienTai());
    }

    static boolean dangChoTiepNhan(CuocHop cuocHop) {
        return "CHO_TIEP_NHAN".equals(cuocHop.getTrangThai())
                && "CHO_TIEP_NHAN".equals(cuocHop.getBuocXuLyHienTai());
    }

    static boolean laNguoiDeXuat(CuocHop cuocHop, Integer nguoiDungId) {
        return nguoiDungId != null
                && cuocHop.getNguoiDeXuat() != null
                && nguoiDungId.equals(cuocHop.getNguoiDeXuat().getNguoiDungId());
    }

    static boolean laNguoiChuTri(CuocHop cuocHop, Integer nguoiDungId) {
        return nguoiDungId != null
                && cuocHop.getNguoiChuTri() != null
                && nguoiDungId.equals(cuocHop.getNguoiChuTri().getNguoiDungId());
    }

    static void tiepNhanDeXuat(CuocHop cuocHop) {
        if (!dangChoTiepNhan(cuocHop) || daBatDau(cuocHop) || daKetThuc(cuocHop)) {
            throw new IllegalStateException(
                    "Đề xuất không còn ở bước chờ tiếp nhận");
        }

        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("LAP_KE_HOACH");
    }

    static boolean coTheLapLich(CuocHop cuocHop) {
        return daDuocPheDuyet(cuocHop) && "THU_KY_TO_CHUC".equals(cuocHop.getBuocXuLyHienTai())
                && !daBatDau(cuocHop) && !daKetThuc(cuocHop);
    }

    static LichHop lichHienHanh(CuocHop cuocHop) {
        return cuocHop.getDanhSachLichHop().stream().filter(ThaoTacLichHop::daXacNhan)
                .max(java.util.Comparator.comparing(LichHop::getLichHopId)).orElse(null);
    }

    static boolean choXacNhanDoiLich(CuocHop cuocHop) {
        return cuocHop.getDanhSachLichHop().stream()
                .anyMatch(lich -> lich.isLaLichDoi() && ThaoTacLichHop.dangDeXuat(lich));
    }

    static boolean coLichDaXacNhan(CuocHop cuocHop) {
        return !choXacNhanDoiLich(cuocHop) && lichHienHanh(cuocHop) != null;
    }
    static boolean coLichMoiThamDu(CuocHop cuocHop, YeuCauPheDuyet yeuCau) {
        if (!coTheLapLich(cuocHop) || yeuCau == null
                || yeuCau.getTrangThai() != YeuCauPheDuyet.TrangThai.DA_DUYET) {
            return false;
        }

        LichHop lichHop = choXacNhanDoiLich(cuocHop) ? null : lichHienHanh(cuocHop);
        return lichHop != null
                && lichHop.getCuocHop() != null
                && Objects.equals(cuocHop.getCuocHopId(), lichHop.getCuocHop().getCuocHopId())
                && ThaoTacLichHop.daXacNhan(lichHop)
                && lichHop.getThoiGianBatDau() != null
                && lichHop.getThoiGianBatDau().isAfter(LocalDateTime.now());
    }

    static CuocHop taoDeXuatNhap(
            String maCuocHop, NguoiDung nguoiDeXuat,
            String tieuDe, String mucTieu, String noiDungDuKien,
            String loaiHinh, String phamVi, PhongBan phongBan,
            NguoiDung nguoiChuTri, boolean coKhachNgoai) {
        CuocHop ch = new CuocHop();
        ch.setTrangThai("NHAP");
        ch.setBuocXuLyHienTai("NHAP");
        capNhatThongTin(ch, tieuDe, mucTieu, noiDungDuKien, loaiHinh, phamVi);
        ch.setMaCuocHop(maCuocHop);
        ch.setNguoiTao(nguoiDeXuat);
        ch.setNguoiDeXuat(nguoiDeXuat);
        ch.setPhongBanChuTri(phongBan);
        ch.setNguoiChuTri(nguoiChuTri);
        ch.setCoKhachMoiBenNgoai(coKhachNgoai);
        return ch;
    }

    static void capNhatDeXuat(
            CuocHop cuocHop,
            String tieuDe, String mucTieu, String noiDungDuKien,
            String loaiHinh, String phamVi, PhongBan phongBan,
            NguoiDung chuTri, boolean coKhachNgoai) {
        if (!laDeXuatNhap(cuocHop)) {
            throw new IllegalStateException("Chỉ được sửa đề xuất khi còn nháp");
        }
        capNhatThongTin(cuocHop, tieuDe, mucTieu, noiDungDuKien, loaiHinh, phamVi);
        cuocHop.setPhongBanChuTri(phongBan);
        cuocHop.setNguoiChuTri(chuTri);
        cuocHop.setCoKhachMoiBenNgoai(coKhachNgoai);
    }

    static void capNhatKeHoach(
            CuocHop cuocHop,
            String tieuDe, String mucTieu, String noiDungDuKien,
            String loaiHinh, NguoiDung chuTri, boolean coKhachNgoai) {
        kiemTraDangLapKeHoach(cuocHop);
        capNhatThongTin(cuocHop, tieuDe, mucTieu, noiDungDuKien, loaiHinh, cuocHop.getPhamViCuocHop());
        cuocHop.setNguoiChuTri(chuTri);
        cuocHop.setCoKhachMoiBenNgoai(coKhachNgoai);
    }

    static void moLaiDeCapNhatKeHoach(CuocHop cuocHop) {
        if (!coTheLapLich(cuocHop)) {
            throw new IllegalStateException(
                    "Chỉ cập nhật lại kế hoạch đã duyệt khi cuộc họp chưa bắt đầu");
        }
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("LAP_KE_HOACH");
    }

    static void guiDeXuat(CuocHop cuocHop) {
        if (!laDeXuatNhap(cuocHop) || daBatDau(cuocHop) || daKetThuc(cuocHop)) {
            throw new IllegalStateException("Chỉ được gửi đề xuất đang ở trạng thái nháp");
        }
        cuocHop.setTrangThai("CHO_TIEP_NHAN");
        cuocHop.setBuocXuLyHienTai("CHO_TIEP_NHAN");
    }

    static void trinhKeHoach(CuocHop cuocHop, String buocChoDuyet) {
        kiemTraDangLapKeHoach(cuocHop);
        if (buocChoDuyet == null || !java.util.Set.of(
                "CHO_TRUONG_PHONG", "CHO_LANH_DAO", "CHO_THU_KY_LANH_DAO")
                .contains(buocChoDuyet)) {
            throw new IllegalArgumentException("Bước trình kế hoạch không hợp lệ");
        }
        cuocHop.setBuocXuLyHienTai(buocChoDuyet);
    }

    static void dongYTrinhCapTren(CuocHop cuocHop) {
        kiemTraBuocKeHoach(cuocHop, "CHO_TRUONG_PHONG");
        cuocHop.setBuocXuLyHienTai("CHO_GUI_CAP_TREN");
    }

    static void moLaiKeHoachChoChuyenCap(CuocHop cuocHop) {
        kiemTraBuocKeHoach(cuocHop, "CHO_GUI_CAP_TREN");
        cuocHop.setBuocXuLyHienTai("LAP_KE_HOACH");
    }

    static void guiKeHoachCapTren(CuocHop cuocHop) {
        kiemTraBuocKeHoach(cuocHop, "CHO_GUI_CAP_TREN");
        cuocHop.setBuocXuLyHienTai("CHO_THU_KY_LANH_DAO");
    }

    static void tiepNhanHoSoLanhDao(CuocHop cuocHop) {
        kiemTraBuocKeHoach(cuocHop, "CHO_THU_KY_LANH_DAO");
        cuocHop.setBuocXuLyHienTai("THU_KY_LANH_DAO_XU_LY");
    }

    static void trinhLanhDao(CuocHop cuocHop) {
        kiemTraBuocKeHoach(cuocHop, "THU_KY_LANH_DAO_XU_LY");
        cuocHop.setBuocXuLyHienTai("CHO_LANH_DAO");
    }

    static void pheDuyetKeHoach(CuocHop cuocHop) {
        kiemTraDangChoNguoiDuyet(cuocHop);
        cuocHop.setTrangThai("DA_DUYET");
        cuocHop.setBuocXuLyHienTai("THU_KY_TO_CHUC");
    }

    static void traLaiKeHoach(CuocHop cuocHop) {
        kiemTraKeHoachChuaDienRa(cuocHop);
        if (!java.util.Set.of("CHO_TRUONG_PHONG", "CHO_LANH_DAO",
                "CHO_THU_KY_LANH_DAO", "THU_KY_LANH_DAO_XU_LY")
                .contains(cuocHop.getBuocXuLyHienTai())) {
            throw new IllegalStateException("Kế hoạch không ở bước được phép trả lại");
        }
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("LAP_KE_HOACH");
    }

    static void tuChoiKeHoach(CuocHop cuocHop) {
        kiemTraDangChoNguoiDuyet(cuocHop);
        cuocHop.setTrangThai("TU_CHOI");
        cuocHop.setBuocXuLyHienTai("KET_THUC");
    }

    static void moLaiDeBoSungKhachNgoai(CuocHop cuocHop) {
        if (!coTheLapLich(cuocHop)) {
            throw new IllegalStateException("Chỉ mở lại kế hoạch đã duyệt và chưa bắt đầu");
        }
        cuocHop.setCoKhachMoiBenNgoai(true);
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("LAP_KE_HOACH");
    }

    private static void kiemTraDangLapKeHoach(CuocHop cuocHop) {
        kiemTraKeHoachChuaDienRa(cuocHop);
        if (!dangLapKeHoach(cuocHop)) {
            throw new IllegalStateException("Cuộc họp không còn ở bước lập kế hoạch");
        }
    }

    private static void kiemTraKeHoachChuaDienRa(CuocHop cuocHop) {
        if (!"DANG_CHUAN_BI".equals(cuocHop.getTrangThai()) || daBatDau(cuocHop) || daKetThuc(cuocHop)) {
            throw new IllegalStateException("Kế hoạch không còn được phép xử lý");
        }
    }

    private static void kiemTraBuocKeHoach(CuocHop cuocHop, String buoc) {
        kiemTraKeHoachChuaDienRa(cuocHop);
        if (!buoc.equals(cuocHop.getBuocXuLyHienTai())) {
            throw new IllegalStateException("Hồ sơ chưa ở đúng bước xử lý kế hoạch");
        }
    }

    private static void kiemTraDangChoNguoiDuyet(CuocHop cuocHop) {
        kiemTraKeHoachChuaDienRa(cuocHop);
        if (!"CHO_TRUONG_PHONG".equals(cuocHop.getBuocXuLyHienTai())
                && !"CHO_LANH_DAO".equals(cuocHop.getBuocXuLyHienTai())) {
            throw new IllegalStateException("Kế hoạch chưa được trình đến người duyệt");
        }
    }
    
    
}
