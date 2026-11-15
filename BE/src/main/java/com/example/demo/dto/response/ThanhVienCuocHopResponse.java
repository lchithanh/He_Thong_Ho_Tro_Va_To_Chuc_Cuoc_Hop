package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record ThanhVienCuocHopResponse(
        Integer nguoiDungId,
        String hoTen,
        String chucVu,
        String tenPhongBan,
        String vaiTroTaiCuocHop,
        String trangThaiMoi,
        String trangThaiThamDu,
        LocalDateTime thoiGianDiemDanh,
        LocalDateTime thoiGianRoiCuocHop
) {
}