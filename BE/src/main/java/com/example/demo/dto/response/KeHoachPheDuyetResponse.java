package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record KeHoachPheDuyetResponse(
        Integer yeuCauPheDuyetId,
        Integer cuocHopId,
        String tieuDe,
        Integer lichHopId,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        String trangThai,
        Integer yeuCauTruocId,
        String capPheDuyet,
        boolean choPhepTrinhCapTren,
        LocalDateTime thoiGianTrinh
) {
}