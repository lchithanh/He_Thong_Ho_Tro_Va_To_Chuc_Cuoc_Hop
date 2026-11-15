package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record ChiTietKeHoachDaDuyetResponse(
        Integer yeuCauPheDuyetId,
        Integer cuocHopId,
        String tieuDe,
        Integer nguoiDuyetId,
        String hoTenNguoiDuyet,
        LocalDateTime thoiGianDuyet,
        String yKien,
        String trangThai
) {
}