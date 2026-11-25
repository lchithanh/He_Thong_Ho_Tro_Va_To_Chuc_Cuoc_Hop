package com.example.demo.dto.request;

import com.example.demo.dto.ThongTinBoSungDeXuatDto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaoDeXuatCuocHopRequest {

    private String tieuDe;
    private String mucTieu;
    private String noiDungDuKien;
    private Integer thoiLuongDuKienPhut;

    @Valid
    private ThongTinBoSungDeXuatDto thongTinDeXuat;

    // TRUC_TIEP hoặc TRUC_TUYEN
    private String loaiHinh;

    // NOI_BO, LIEN_PHONG_BAN, TOAN_CO_QUAN, BEN_NGOAI
    private String phamViCuocHop;

    private Integer phongBanChuTriId;
    private Integer nguoiChuTriId;
    private Integer thuKyId;
    private Integer thuKyCuocHopId;

    private boolean guiNgay = false;
    private boolean coKhachMoiBenNgoai;

    public boolean isCoKhachMoiBenNgoai() {
        return coKhachMoiBenNgoai;
    }

    public void setCoKhachMoiBenNgoai(boolean coKhachMoiBenNgoai) {
        this.coKhachMoiBenNgoai = coKhachMoiBenNgoai;
    }
}