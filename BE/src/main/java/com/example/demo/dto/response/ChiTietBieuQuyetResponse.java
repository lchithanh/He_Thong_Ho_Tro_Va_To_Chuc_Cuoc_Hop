package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChiTietBieuQuyetResponse(
        Integer bieuQuyetId,
        String tieuDe,
        String noiDung,
        String cheDoBieuQuyet,
        String trangThai,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        List<LuaChonResponse> luaChon
) {
    public record LuaChonResponse(
            Integer luaChonId,
            String noiDung
    ) {
    }
}