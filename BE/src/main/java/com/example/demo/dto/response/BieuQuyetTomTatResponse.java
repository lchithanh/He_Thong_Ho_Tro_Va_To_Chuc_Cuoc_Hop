package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record BieuQuyetTomTatResponse(
        Integer bieuQuyetId,
        String tieuDe,
        String trangThai,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc
) {}