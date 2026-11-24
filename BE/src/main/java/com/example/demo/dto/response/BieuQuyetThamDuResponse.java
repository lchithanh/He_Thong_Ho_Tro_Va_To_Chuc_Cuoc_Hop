package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record BieuQuyetThamDuResponse(
        Integer cuocHopId,
        String tieuDeCuocHop,
        Integer bieuQuyetId,
        String tieuDeBieuQuyet,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        Integer nghiQuyetId
) {
}