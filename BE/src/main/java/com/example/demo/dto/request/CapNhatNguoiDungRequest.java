
package com.example.demo.dto.request;

public record CapNhatNguoiDungRequest(
        String maNhanVien,
        String hoTen,
        String email,
        String soDienThoai,
        String chucVu,
        Integer phongBanId
) {}
