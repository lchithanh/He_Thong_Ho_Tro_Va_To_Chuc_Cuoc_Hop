package com.example.demo.dto.request;

import java.time.LocalDateTime;

public record TaoLichHopRequest(
        Integer cuocHopId,
        Integer phongHopId,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String lyDoThayDoi
) {
}