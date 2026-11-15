
package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CapNhatThietBiRequest(

    Integer phongHopId,

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(max = 150, message = "Tên thiết bị không được vượt quá 150 ký tự")
    String tenThietBi,

    @Size(max = 50, message = "Mã thiết bị không được vượt quá 50 ký tự")
    String maThietBi,

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    Integer soLuong,

    String tinhTrang,

    String ghiChu
) {}
