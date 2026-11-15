package com.example.demo.service;

import java.time.LocalDateTime;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongHop;

final class ThaoTacLichHop {

    private ThaoTacLichHop() {
    }

    static boolean dangDeXuat(LichHop lichHop) {
        return "DE_XUAT".equals(lichHop.getTrangThai());
    }

    static boolean daXacNhan(LichHop lichHop) {
        return "DA_XAC_NHAN".equals(lichHop.getTrangThai());
    }

    static boolean coTheXacNhan(LichHop lichHop) {
        return dangDeXuat(lichHop);
    }

    static void xacNhan(LichHop lichHop, NguoiDung nguoiXacNhan) {
        if (!coTheXacNhan(lichHop)) {
            throw new IllegalStateException("Chỉ được xác nhận lịch đang ở trạng thái đề xuất");
        }
        lichHop.setNguoiXacNhan(nguoiXacNhan);
        lichHop.setThoiGianXacNhan(LocalDateTime.now());
        lichHop.setTrangThai("DA_XAC_NHAN");
    }

    static void danhDauDaBiThayThe(LichHop lichHop) {

        if (!daXacNhan(lichHop)) {
            throw new IllegalStateException(
                    "Chỉ được thay thế lịch đã xác nhận"
            );
        }

        lichHop.setTrangThai("THAY_THE");
    }

    static void capNhatLich(
            LichHop lichHop,
            PhongHop phongHopMoi,
            LocalDateTime thoiGianBatDauMoi,
            LocalDateTime thoiGianKetThucMoi,
            String lyDoThayDoiMoi) {

        if (!coTheCapNhat(lichHop)) {
            throw new IllegalStateException(
                    "Chỉ được cập nhật lịch đang ở trạng thái đề xuất"
            );
        }

        lichHop.setPhongHop(phongHopMoi);
        lichHop.setThoiGianBatDau(thoiGianBatDauMoi);
        lichHop.setThoiGianKetThuc(thoiGianKetThucMoi);
        lichHop.setLyDoThayDoi(lyDoThayDoiMoi);
    }

    static boolean daTuChoi(LichHop lichHop) {
        return "TU_CHOI".equals(lichHop.getTrangThai());
    }

    static boolean daHuy(LichHop lichHop) {
        return "DA_HUY".equals(lichHop.getTrangThai());
    }

    static boolean daBiThayThe(LichHop lichHop) {
        return "THAY_THE".equals(lichHop.getTrangThai());
    }

    static boolean coTheCapNhat(LichHop lichHop) {
        return dangDeXuat(lichHop);
    }

    static boolean coTheTuChoi(LichHop lichHop) {
        return dangDeXuat(lichHop);
    }

    static boolean coTheHuy(LichHop lichHop) {
        return dangDeXuat(lichHop) || daXacNhan(lichHop);
    }

    static void tuChoi(LichHop lichHop, NguoiDung nguoiXacNhan, String lyDoTuChoi) {
        if (!coTheTuChoi(lichHop)) {
            throw new IllegalStateException("Chỉ được từ chối lịch đang ở trạng thái đề xuất");
        }
        lichHop.setNguoiXacNhan(nguoiXacNhan);
        lichHop.setThoiGianXacNhan(LocalDateTime.now());
        lichHop.setTrangThai("TU_CHOI");
        lichHop.setLyDoThayDoi(lyDoTuChoi);
    }

    static void ganVoiLichBiThayThe(LichHop lichHop, LichHop lichCu) {
        if (!dangDeXuat(lichHop)) {
            throw new IllegalStateException("Lịch mới phải đang ở trạng thái đề xuất");
        }
        lichHop.setLichThayThe(lichCu);
    }

    static void huyLich(LichHop lichHop, String lyDoHuy) {
        if (!coTheHuy(lichHop)) {
            throw new IllegalStateException("Chỉ được hủy lịch đang đề xuất hoặc đã xác nhận");
        }
        lichHop.setTrangThai("DA_HUY");
        lichHop.setLyDoThayDoi(lyDoHuy);
    }

    static LichHop taoDeXuat(
            CuocHop cuocHop, PhongHop phongHop, NguoiDung nguoiDeXuat,
            LocalDateTime batDau, LocalDateTime ketThuc, String lyDo) {
        LichHop lich = new LichHop();
        lich.setTrangThai("DE_XUAT");
        lich.setCuocHop(cuocHop);
        lich.setPhongHop(phongHop);
        lich.setNguoiDeXuat(nguoiDeXuat);
        lich.setThoiGianBatDau(batDau);
        lich.setThoiGianKetThuc(ketThuc);
        lich.setLyDoThayDoi(lyDo);
        return lich;
    }
}
