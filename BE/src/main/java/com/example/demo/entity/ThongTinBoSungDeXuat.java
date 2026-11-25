package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ThongTinBoSungDeXuat {
    @Column(name = "ket_qua_ky_vong", columnDefinition = "TEXT")
    private String ketQuaKyVong;

    @Column(name = "bat_dau_de_xuat_1")
    private LocalDateTime batDauDeXuat1;

    @Column(name = "bat_dau_de_xuat_2")
    private LocalDateTime batDauDeXuat2;

    @Column(name = "bat_dau_de_xuat_3")
    private LocalDateTime batDauDeXuat3;

    @Column(name = "dia_diem_de_xuat", length = 255)
    private String diaDiemDeXuat;

    @Column(name = "nen_tang_truc_tuyen_de_xuat", length = 100)
    private String nenTangTrucTuyen;

    @Column(name = "lien_ket_truc_tuyen_de_xuat", length = 2048)
    private String lienKetTrucTuyen;

    @Column(name = "huong_dan_truc_tuyen_de_xuat", length = 500)
    private String huongDanTrucTuyen;

    @Column(name = "thanh_phan_bat_buoc_de_xuat", columnDefinition = "TEXT")
    private String thanhPhanBatBuoc;

    @Column(name = "thanh_phan_tuy_chon_de_xuat", columnDefinition = "TEXT")
    private String thanhPhanTuyChon;

    @Column(name = "yeu_cau_chuan_bi_de_xuat", columnDefinition = "TEXT")
    private String yeuCauChuanBi;

    @Column(name = "lien_ket_tai_lieu_de_xuat", columnDefinition = "TEXT")
    private String lienKetTaiLieu;
}
