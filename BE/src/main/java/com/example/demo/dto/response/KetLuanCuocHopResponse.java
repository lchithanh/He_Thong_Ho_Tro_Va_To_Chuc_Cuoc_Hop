package com.example.demo.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record KetLuanCuocHopResponse(
        Integer ketLuanId,
        Integer cuocHopId,
        String tieuDeCuocHop,
        String tieuDe,
        String noiDung,
        String trangThai,
        LocalDate hanPhatHanh,
        Boolean quaHanPhatHanh,
        Integer yeuCauPheDuyetId,
        String capPheDuyet,
        Integer nguoiDuyetId,
        String tenNguoiDuyet,
        String ghiChuPhanHoi,
        String tenNguoiSoan,
        LocalDateTime ngayTrinhDuyet,
        String tenNguoiPhatHanh,
        LocalDateTime ngayPhatHanh
) {
}



