package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CapNhatKeHoachCuocHopRequest {

    private String tieuDe;
    private String mucTieu;
    private String noiDungDuKien;
    private String loaiHinh;

    private Integer nguoiChuTriId;
    private Integer thuKyCuocHopId;
    private boolean coKhachMoiBenNgoai;

    public boolean isCoKhachMoiBenNgoai() {
        return coKhachMoiBenNgoai;
    }

    public void setCoKhachMoiBenNgoai(boolean coKhachMoiBenNgoai) {
        this.coKhachMoiBenNgoai = coKhachMoiBenNgoai;
    }
}