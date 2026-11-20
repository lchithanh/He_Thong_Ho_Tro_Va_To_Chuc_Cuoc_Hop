package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MoiThanhVienCuocHopRequest(
        @NotNull(message = "Vui lòng chọn người tham dự")
        @Positive(message = "ID người tham dự phải lớn hơn 0")
        Integer nguoiDuocMoiId,
        @NotNull @Valid NoiDungThuMoi thuMoi
) {
}