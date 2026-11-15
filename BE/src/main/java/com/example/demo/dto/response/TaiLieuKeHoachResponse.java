package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record TaiLieuKeHoachResponse(
        Integer taiLieuId,
        String tenTaiLieu,
        String tenFileGoc,
        String dinhDangFile,
        Long dungLuongByte,
        LocalDateTime ngayTaiLen,
        boolean daDuocTrinh) { }
