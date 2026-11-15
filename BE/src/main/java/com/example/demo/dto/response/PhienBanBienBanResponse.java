package com.example.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhienBanBienBanResponse {

    private Integer phienBanId;

    private Integer bienBanId;

    private Integer soPhienBan;

    private Integer nguoiCapNhatId;

    private String noiDung;

    private String ghiChuThayDoi;

    private LocalDateTime ngayTao;
}