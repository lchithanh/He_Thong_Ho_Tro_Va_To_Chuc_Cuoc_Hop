
package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "khach_moi_cuoc_hop",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_khach_moi_cuoc_hop",
            columnNames = {"khach_moi_id", "cuoc_hop_id"}
        )
    }
)
public class KhachMoiCuocHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "khach_moi_cuoc_hop_id")
    private Integer khachMoiCuocHopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_moi_id", nullable = false)
    private KhachMoi khachMoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    @Column(name = "trang_thai_moi", nullable = false, length = 25)
    private String trangThaiMoi = "CHO_PHAN_HOI";

    @Column(name = "trang_thai_tham_du", nullable = false, length = 25)
    private String trangThaiThamDu = "CHUA_DIEM_DANH";

    @Column(name = "thoi_gian_diem_danh")
    private LocalDateTime thoiGianDiemDanh;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_moi", nullable = false, updatable = false)
    private LocalDateTime ngayMoi;

    @Column(name = "ma_truy_cap_sha256", length = 64)
    private String maTruyCapSha256;

    @Column(name = "duong_dan_hop", length = 2048)
    private String duongDanHop;

    @Column(name = "han_truy_cap")
    private LocalDateTime hanTruyCap;

    public void capMaTruyCap(String maTruyCapSha256, String duongDanHop, LocalDateTime hanTruyCap) {
        this.maTruyCapSha256 = maTruyCapSha256;
        this.duongDanHop = duongDanHop;
        this.hanTruyCap = hanTruyCap;
    }

    public String getDuongDanHop() {
        return duongDanHop;
    }

    public LocalDateTime getHanTruyCap() {
        return hanTruyCap;
    }

    public KhachMoiCuocHop() {
    }

    @PrePersist
    protected void onCreate() {
        if (ngayMoi == null) {
            ngayMoi = LocalDateTime.now();
        }
    }

    public Integer getKhachMoiCuocHopId() {
        return khachMoiCuocHopId;
    }

    public void setKhachMoiCuocHopId(Integer khachMoiCuocHopId) {
        this.khachMoiCuocHopId = khachMoiCuocHopId;
    }

    public KhachMoi getKhachMoi() {
        return khachMoi;
    }

    public void setKhachMoi(KhachMoi khachMoi) {
        this.khachMoi = khachMoi;
    }

    public CuocHop getCuocHop() {
        return cuocHop;
    }

    public void setCuocHop(CuocHop cuocHop) {
        this.cuocHop = cuocHop;
    }

    public String getTrangThaiMoi() {
        return trangThaiMoi;
    }

    public void setTrangThaiMoi(String trangThaiMoi) {
        this.trangThaiMoi = trangThaiMoi;
    }

    public String getTrangThaiThamDu() {
        return trangThaiThamDu;
    }

    public void setTrangThaiThamDu(String trangThaiThamDu) {
        this.trangThaiThamDu = trangThaiThamDu;
    }

    public LocalDateTime getThoiGianDiemDanh() {
        return thoiGianDiemDanh;
    }

    public void setThoiGianDiemDanh(LocalDateTime thoiGianDiemDanh) {
        this.thoiGianDiemDanh = thoiGianDiemDanh;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public LocalDateTime getNgayMoi() {
        return ngayMoi;
    }

    public void setNgayMoi(LocalDateTime ngayMoi) {
        this.ngayMoi = ngayMoi;
    }
}
