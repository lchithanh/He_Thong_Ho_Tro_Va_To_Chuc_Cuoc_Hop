package com.example.demo.dto.response;

import com.example.demo.entity.TaiLieuCuocHop;

public record TaiLieuBienBanResponse(
        Integer taiLieuId,
        String tenFileGoc,
        String dinhDangFile,
        Long dungLuongByte) {

    public static TaiLieuBienBanResponse tuTaiLieu(TaiLieuCuocHop taiLieu) {
        return new TaiLieuBienBanResponse(taiLieu.getTaiLieuId(), taiLieu.getTenFileGoc(),
                taiLieu.getDinhDangFile(), taiLieu.getDungLuongByte());
    }
}
