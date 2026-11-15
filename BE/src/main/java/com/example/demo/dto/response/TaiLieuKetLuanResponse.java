package com.example.demo.dto.response;

public record TaiLieuKetLuanResponse(
        Integer taiLieuId,
        String tenFileGoc,
        String dinhDangFile,
        Long dungLuongByte
) {
}