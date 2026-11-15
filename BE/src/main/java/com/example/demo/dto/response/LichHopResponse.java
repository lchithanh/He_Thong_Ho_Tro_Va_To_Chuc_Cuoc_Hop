package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LichHopResponse(
        Integer lichHopId,
        Integer cuocHopId,
        String tieuDeCuocHop,
        Integer phongHopId,
        String tenPhongHop,
        Integer nguoiDeXuatId,
        String tenNguoiDeXuat,
        Integer nguoiXacNhanId,
        String tenNguoiXacNhan,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        BigDecimal diemPhuHop,
        String trangThai,
        String lyDoThayDoi,
        LocalDateTime thoiGianXacNhan,
        Integer lichThayTheId,
        LocalDateTime ngayTao
) {
}