package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record CuocHopDangDienRaResponse(
        Integer cuocHopId,
        String tieuDe,
        String loaiHinh,
        String tenChuTri,
        boolean laChuTri,
        LocalDateTime thoiGianBatDau,
        String mucTieu,
        String noiDungDuKien,
        String trangThaiThamDu,
        LocalDateTime thoiGianDiemDanh,
        LocalDateTime thoiGianRoiCuocHop
) {}
