package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record KhachNgoaiCuocHopResponse(
        Integer khachMoiCuocHopId,
        String hoTen,
        String email,
        String donViCongTac,
        String chucVu,
        String trangThaiMoi,
        LocalDateTime ngayMoi) {
}
