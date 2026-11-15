package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LuuBienBanRequest {

    private String tieuDe;

    private String noiDungGoc;

    private String noiDungChinhThuc;

    private String ghiChuThayDoi;
}