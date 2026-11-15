package com.example.demo.dto.response;

import java.util.List;

public record KetQuaBieuQuyetResponse(
        Integer bieuQuyetId,
        String trangThai,
        long soNguoiDuocBieuQuyet,
        long tongSoPhieu,
        Integer luaChonThangId,
        boolean hoaPhieu,
        List<KetQuaLuaChon> luaChon
) {
    public record KetQuaLuaChon(
            Integer luaChonId,
            String noiDung,
            long soPhieu
    ) {
    }
}