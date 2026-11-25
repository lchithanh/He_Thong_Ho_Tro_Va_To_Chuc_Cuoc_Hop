package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record TaiLieuDeXuatResponse(
        Integer taiLieuId,
        String tenTaiLieu,
        String tenFileGoc,
        String dinhDangFile,
        Long dungLuongByte,
        LocalDateTime ngayTaiLen) {
}
