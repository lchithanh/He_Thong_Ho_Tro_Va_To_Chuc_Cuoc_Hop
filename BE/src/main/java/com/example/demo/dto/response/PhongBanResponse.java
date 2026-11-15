
package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record PhongBanResponse(
        Integer phongBanId,
        String tenPhongBan,
        Integer truongPhongId,
        String tenTruongPhong,
        String moTa,
        String trangThai,
        LocalDateTime ngayTao
) {}
