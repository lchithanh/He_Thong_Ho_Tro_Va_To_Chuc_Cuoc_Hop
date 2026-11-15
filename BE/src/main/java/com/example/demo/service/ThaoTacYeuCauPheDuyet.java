package com.example.demo.service;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;

final class ThaoTacYeuCauPheDuyet {

    private ThaoTacYeuCauPheDuyet() {
    }

    static void pheDuyet(YeuCauPheDuyet yeuCau) {
        kiemTraDangChoXuLy(yeuCau);
        yeuCau.setTrangThai(TrangThai.DA_DUYET);
    }

    static void traLai(YeuCauPheDuyet yeuCau) {
        kiemTraDangChoXuLy(yeuCau);
        yeuCau.setTrangThai(TrangThai.TRA_LAI);
    }

    private static void kiemTraDangChoXuLy(YeuCauPheDuyet yeuCau) {
        if (yeuCau.getTrangThai() != TrangThai.CHO_DUYET) {
            throw new IllegalStateException(
                    "Yêu cầu không còn chờ xử lý");
        }
    }

    static void tuChoi(YeuCauPheDuyet yeuCau) {
        kiemTraDangChoXuLy(yeuCau);
        yeuCau.setTrangThai(TrangThai.TU_CHOI);
    }

    static void huy(YeuCauPheDuyet yeuCau) {
        kiemTraDangChoXuLy(yeuCau);
        yeuCau.setTrangThai(TrangThai.DA_HUY);
    }

    static YeuCauPheDuyet taoYeuCauKeHoach(
            CuocHop cuocHop, NguoiDung nguoiTrinh, NguoiDung nguoiDuyet,
            NguoiDung thuKyTiepNhan, String capPheDuyet, String ghiChu) {
        YeuCauPheDuyet yc = new YeuCauPheDuyet();
        yc.setTrangThai(TrangThai.CHO_DUYET);
        yc.setCuocHop(cuocHop);
        yc.setNguoiTrinh(nguoiTrinh);
        yc.setNguoiDuyet(nguoiDuyet);
        yc.setThuKyTiepNhan(thuKyTiepNhan);
        yc.setCapPheDuyet(capPheDuyet);
        yc.setLoaiYeuCau(LoaiYeuCau.KE_HOACH_CUOC_HOP);
        yc.setGhiChuTrinh(ghiChu);
        return yc;
    }
}
