package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record ChiTietKeHoachChoDuyetResponse(
        Integer yeuCauPheDuyetId,
        Integer cuocHopId,
        String tieuDe,
        String mucTieu,
        String noiDungDuKien,
        String loaiHinh,
        String phamViCuocHop,
        Integer lichHopId,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String ghiChuTrinh,
        String trangThai
) {
}