package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.BienBanCuocHop;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;

final class ThaoTacBienBan {
    private ThaoTacBienBan() { }

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

    static BienBanCuocHop taoNhap(
            CuocHop cuocHop, NguoiDung nguoiSoan, String tieuDe,
            String noiDungGoc, String noiDungChinhThuc, LocalDateTime thoiGian) {
        BienBanCuocHop bienBan = new BienBanCuocHop();
        bienBan.setTrangThai("NHAP");
        bienBan.setCuocHop(cuocHop);
        bienBan.setNguoiSoan(nguoiSoan);
        capNhatNoiDung(bienBan, tieuDe, noiDungGoc, noiDungChinhThuc);
        bienBan.setNgaySoan(thoiGian);
        return bienBan;
    }

    static void capNhatNoiDung(
            BienBanCuocHop bienBan,
            String tieuDe, String noiDungGoc, String noiDungChinhThuc) {
        if (!coTheChinhSua(bienBan)) {
            throw new IllegalStateException("Chỉ sửa biên bản nháp hoặc bị trả lại");
        }
        bienBan.setTieuDe(tieuDe);
        bienBan.setNoiDungGoc(noiDungGoc);
        bienBan.setNoiDungChinhThuc(noiDungChinhThuc);
    }

    static boolean coTheChinhSua(BienBanCuocHop bienBan) {
        return "NHAP".equals(bienBan.getTrangThai())
                || "BI_TRA_LAI".equals(bienBan.getTrangThai());
    }

    static void trinhDuyet(BienBanCuocHop bienBan, NguoiDung nguoiDuocGiaoDuyet, LocalDateTime thoiGian) {
        if (!coTheChinhSua(bienBan)) {
            throw new IllegalStateException("Chỉ trình biên bản nháp hoặc bị trả lại");
        }
        bienBan.setTrangThai("CHO_DUYET");
        bienBan.setNguoiDuyet(nguoiDuocGiaoDuyet);
        bienBan.setNgayTrinhDuyet(thoiGian);
        bienBan.setNgayDuyet(null);
    }

    static void pheDuyet(BienBanCuocHop bienBan, String yKien, LocalDateTime thoiGian) {
        kiemTraDangChoDuyet(bienBan);
        bienBan.setTrangThai("DA_PHE_DUYET");
        bienBan.setNgayDuyet(thoiGian);
        bienBan.setYKienPhanHoi(yKien);
    }

    static void traLai(BienBanCuocHop bienBan, String lyDo) {
        kiemTraDangChoDuyet(bienBan);
        bienBan.setTrangThai("BI_TRA_LAI");
        bienBan.setNgayDuyet(null);
        bienBan.setYKienPhanHoi(lyDo);
    }

    private static void kiemTraDangChoDuyet(BienBanCuocHop bienBan) {
        if (!"CHO_DUYET".equals(bienBan.getTrangThai())) {
            throw new IllegalStateException(
                    "Biên bản không còn chờ duyệt");
        }
    }
}
