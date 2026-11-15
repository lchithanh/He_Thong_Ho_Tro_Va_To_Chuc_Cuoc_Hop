package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record NguoiDungResponse(
    Integer nguoiDungId,
    String maNhanVien,
    String hoTen,
    String email,
    String soDienThoai,
    String tenDangNhap,
    String chucVu,
    String trangThai,
    LocalDateTime ngayTao,
    Integer phongBanId,
    String tenPhongBan,
    Integer vaiTroId,
    String maVaiTro,
    String tenVaiTro
) {
}