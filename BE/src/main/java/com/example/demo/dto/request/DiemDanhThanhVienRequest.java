package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;

public record DiemDanhThanhVienRequest(
        @NotNull(message = "Vui lòng chọn có mặt hoặc vắng mặt")
        Boolean coMat
) {
}