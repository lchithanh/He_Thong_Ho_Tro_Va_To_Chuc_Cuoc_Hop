package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record LoiMoiChoPhanHoiResponse(
        Integer cuocHopId,
        String maCuocHop,
        String tieuDe,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String tenPhongHop,
        String loaiHinh,
        String tenNguoiChuTri,
        String mucTieu,
        String noiDungDuKien,
        LocalDateTime ngayMoi
) {}



