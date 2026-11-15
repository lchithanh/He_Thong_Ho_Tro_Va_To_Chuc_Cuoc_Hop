package com.example.demo.dto.response;

public record BienBanChoDuyetResponse(
        Integer yeuCauPheDuyetId,
        Integer bienBanId,
        Integer cuocHopId,
        String tieuDeCuocHop,
        String tieuDe,
        Integer soPhienBan,
        String noiDung,
        String ghiChuTrinh,
        String capPheDuyet
) {}



