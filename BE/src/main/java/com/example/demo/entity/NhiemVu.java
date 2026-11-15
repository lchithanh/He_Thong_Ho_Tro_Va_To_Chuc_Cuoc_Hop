
package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nhiem_vu")
@Getter
@Setter
@NoArgsConstructor
public class NhiemVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nhiem_vu_id")
    private Integer nhiemVuId;

    // Cuộc họp phát sinh nhiệm vụ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    // Người giao nhiệm vụ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_giao_id", nullable = false)
    private NguoiDung nguoiGiao;

    // Người thực hiện nhiệm vụ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_thuc_hien_id", nullable = false)
    private NguoiDung nguoiThucHien;

    @Column(name = "han_hoan_thanh")
    private LocalDateTime hanHoanThanh;

    @Column(name = "muc_do_uu_tien", nullable = false, length = 20)
    private String mucDoUuTien = "TRUNG_BINH";

    @Column(name = "trang_thai", nullable = false, length = 25)
    private String trangThai = "CHUA_BAT_DAU";

    @Column(name = "phan_tram_hoan_thanh", nullable = false)
    private Integer phanTramHoanThanh = 0;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    // Lịch sử cập nhật tiến độ
    @OneToMany(mappedBy = "nhiemVu", fetch = FetchType.LAZY)
    private List<TienDoNhiemVu> danhSachTienDo = new ArrayList<>();
}
