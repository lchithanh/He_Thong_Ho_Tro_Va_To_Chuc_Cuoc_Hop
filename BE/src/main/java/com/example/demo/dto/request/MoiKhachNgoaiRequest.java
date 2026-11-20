package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MoiKhachNgoaiRequest(
        @NotBlank @Size(max = 150) String hoTen,
        @NotBlank @Email @Size(max = 190) String email,
        @Size(max = 200) String donViCongTac,
        @Size(max = 120) String chucVu,
        @Size(max = 500) String duongDanHop) {
}
