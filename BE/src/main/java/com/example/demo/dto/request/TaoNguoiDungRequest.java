package com.example.demo.dto.request;

public record TaoNguoiDungRequest(
    Integer phongBanId,
    String maNhanVien,
    String hoTen,
    String email,
    String soDienThoai,
    String tenDangNhap,
    String matKhau,
    String chucVu,
    Integer vaiTroId
) {
}