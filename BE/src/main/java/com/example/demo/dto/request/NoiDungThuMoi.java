package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NoiDungThuMoi(
        @Size(max = 150)
        @Pattern(regexp = "[^\\p{Cntrl}]+", message = "Tiêu đề email phải nằm trên một dòng")
        String tieuDe,
        @Size(max = 4000)
        String noiDung,
        @Valid TepDinhKem tepDinhKem) {
    public NoiDungThuMoi(String tieuDe, String noiDung) {
        this(tieuDe, noiDung, null);
    }

    public record TepDinhKem(
            @NotBlank @Size(max = 150) String tenTep,
            @NotBlank @Size(max = 2796204) String duLieuBase64,
            @Positive Integer lichHopId) {}
}
