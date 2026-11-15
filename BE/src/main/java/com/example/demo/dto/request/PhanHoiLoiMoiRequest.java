package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;

public record PhanHoiLoiMoiRequest(
        @NotNull(message = "Vui lòng chọn xác nhận hoặc từ chối")
        Boolean thamDu
) {
}