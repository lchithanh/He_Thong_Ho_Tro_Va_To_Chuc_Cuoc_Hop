package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "de_xuat_cuoc_hop")
@Getter
@NoArgsConstructor
public class DeXuatCuocHop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "de_xuat_id")
    private Integer deXuatId;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "muc_tieu", columnDefinition = "TEXT")
    private String mucTieu;

    @Column(name = "noi_dung_du_kien", columnDefinition = "LONGTEXT")
    private String noiDungDuKien;

    @Setter
    @Column(name = "thoi_luong_du_kien_phut")
    private Integer thoiLuongDuKienPhut;

    @Setter
    @Embedded
    private ThongTinBoSungDeXuat thongTinBoSung;

    @Column(name = "loai_hinh", nullable = false, length = 20)
    private String loaiHinh;

    @Column(name = "pham_vi_cuoc_hop", nullable = false, length = 30)
    private String phamViCuocHop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_ban_chu_tri_id")
    private PhongBan phongBanChuTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_tao_id", nullable = false)
    private NguoiDung nguoiTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_de_xuat_id", nullable = false)
    private NguoiDung nguoiDeXuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_chu_tri_de_nghi_id")
    private NguoiDung nguoiChuTriDeNghi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thu_ky_de_nghi_id")
    private NguoiDung thuKyDeNghi;

    @Column(name = "co_khach_moi_ben_ngoai", nullable = false)
    private boolean coKhachMoiBenNgoai;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "buoc_xu_ly_hien_tai", nullable = false, length = 35)
    private String buocXuLyHienTai;

    public void capNhatTuHoSo(CuocHop cuocHop) {
        tieuDe = cuocHop.getTieuDe();
        mucTieu = cuocHop.getMucTieu();
        noiDungDuKien = cuocHop.getNoiDungDuKien();
        loaiHinh = cuocHop.getLoaiHinh();
        phamViCuocHop = cuocHop.getPhamViCuocHop();
        phongBanChuTri = cuocHop.getPhongBanChuTri();
        nguoiTao = cuocHop.getNguoiTao();
        nguoiDeXuat = cuocHop.getNguoiDeXuat();
        nguoiChuTriDeNghi = cuocHop.getNguoiChuTri();
        thuKyDeNghi = cuocHop.getThuKyCuocHop();
        coKhachMoiBenNgoai = Boolean.TRUE.equals(cuocHop.getCoKhachMoiBenNgoai());
        ghiNhanBuocXuLy(cuocHop);
    }

    public void ghiNhanBuocXuLy(CuocHop cuocHop) {
        trangThai = cuocHop.getTrangThai();
        buocXuLyHienTai = cuocHop.getBuocXuLyHienTai();
    }
}
