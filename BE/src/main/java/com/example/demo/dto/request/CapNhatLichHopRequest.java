package com.example.demo.dto.request;

import java.time.LocalDateTime;

public record CapNhatLichHopRequest(
        Integer phongHopId,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String lyDoThayDoi
) {
}