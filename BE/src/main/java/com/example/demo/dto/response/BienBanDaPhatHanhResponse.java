package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BienBanDaPhatHanhResponse(
        Integer bienBanId,
        Integer cuocHopId,
        String tieuDeCuocHop,
        String tieuDe,
        String noiDungChinhThuc,
        String trangThai,
        LocalDateTime ngayDuyet,
        Integer soPhienBan,
        ThongTinCuocHop cuocHop,
        List<TaiLieuBienBanResponse> taiLieu) {

    public record ThongTinCuocHop(
            String maCuocHop,
            String tenNguoiChuTri,
            String loaiHinh,
            String phamViCuocHop,
            LocalDateTime thoiGianBatDauThucTe,
            LocalDateTime thoiGianKetThucThucTe) {
    }
}
