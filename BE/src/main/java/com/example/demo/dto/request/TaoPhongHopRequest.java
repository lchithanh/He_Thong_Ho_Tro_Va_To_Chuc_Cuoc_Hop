package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaoPhongHopRequest(

        @NotBlank(message = "Tên phòng họp không được để trống")
        @Size(max = 120, message = "Tên phòng họp không được vượt quá 120 ký tự")
        String tenPhongHop,

        @Size(max = 255, message = "Vị trí không được vượt quá 255 ký tự")
        String viTri,

        @NotNull(message = "Sức chứa không được để trống")
        @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
        Integer sucChua,

        String moTa

) {}