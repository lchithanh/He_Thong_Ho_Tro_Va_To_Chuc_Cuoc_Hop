package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record BienBanResponse(
        Integer bienBanId,
        Integer cuocHopId,
        Integer nguoiSoanId,
        Integer nguoiDuyetId,
        String tenNguoiDuyet,
        String tieuDe,
        String noiDungGoc,
        String noiDungChinhThuc,
        String trangThai,
        String yKienPhanHoi,
        LocalDateTime ngaySoan,
        LocalDateTime ngayTrinhDuyet,
        LocalDateTime ngayDuyet) {
}