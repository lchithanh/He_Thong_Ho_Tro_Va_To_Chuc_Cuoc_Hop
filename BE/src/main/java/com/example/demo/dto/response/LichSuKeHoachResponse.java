package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record LichSuKeHoachResponse(
        Integer yeuCauPheDuyetId,
        LocalDateTime thoiGianTrinh,
        String ghiChuTrinh,
        String trangThai,
        String capPheDuyet,
        Integer yeuCauTruocId,
        List<HanhDong> hanhDongs
) {
    public record HanhDong(
            String hanhDong,
            Integer nguoiThucHienId,
            String hoTenNguoiThucHien,
            LocalDateTime thoiGian,
            String yKien
    ) {
    }
}