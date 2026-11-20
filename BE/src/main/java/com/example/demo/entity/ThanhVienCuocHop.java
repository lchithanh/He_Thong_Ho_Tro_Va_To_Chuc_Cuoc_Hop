
package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "thanh_vien_cuoc_hop")
@Getter
@Setter
@NoArgsConstructor
public class ThanhVienCuocHop {

    @EmbeddedId
    private ThanhVienCuocHopId id;

    // Cuộc họp mà người dùng tham gia
    @MapsId("cuocHopId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    // Người dùng tham gia cuộc họp
    @MapsId("nguoiDungId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Column(
        name = "vai_tro_tai_cuoc_hop",
        nullable = false,
        length = 30
    )
    private String vaiTroTaiCuocHop = "THAM_DU";

    @Column(
        name = "trang_thai_moi",
        nullable = false,
        length = 25
    )
    private String trangThaiMoi = "CHO_PHAN_HOI";

    @Column(
        name = "trang_thai_tham_du",
        nullable = false,
        length = 25
    )
    private String trangThaiThamDu = "CHUA_DIEM_DANH";

    @Column(name = "ngay_moi")
    private LocalDateTime ngayMoi;

    @Column(name = "thoi_gian_diem_danh")
    private LocalDateTime thoiGianDiemDanh;

    @Column(name = "thoi_gian_tu_diem_danh")
    private LocalDateTime thoiGianTuDiemDanh;

    @Column(name = "thoi_gian_doi_chieu_diem_danh")
    private LocalDateTime thoiGianDoiChieuDiemDanh;

    @Column(name = "thoi_gian_roi_cuoc_hop")
    private LocalDateTime thoiGianRoiCuocHop;
    
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;
}
