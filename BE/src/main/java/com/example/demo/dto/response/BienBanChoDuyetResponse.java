package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record BienBanChoDuyetResponse(
        Integer yeuCauPheDuyetId,
        Integer bienBanId,
        Integer cuocHopId,
        String tieuDeCuocHop,
        String tieuDe,
        Integer soPhienBan,
        String noiDung,
        String ghiChuTrinh,
        String capPheDuyet,
        LocalDateTime thoiGianTrinh
) {}



