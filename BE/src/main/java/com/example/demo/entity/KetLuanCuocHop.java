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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ket_luan_cuoc_hop")
@Getter
@Setter
@NoArgsConstructor
public class KetLuanCuocHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ket_luan_id")
    private Integer ketLuanId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuoc_hop_id", nullable = false, unique = true)
    private CuocHop cuocHop;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguoi_soan_id", nullable = false)
    private NguoiDung nguoiSoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_phat_hanh_id")
    private NguoiDung nguoiPhatHanh;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "LONGTEXT")
    private String noiDung;

    @Column(name = "trang_thai", nullable = false, length = 25)
    private String trangThai = "NHAP";

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat", insertable = false, updatable = false)
    private LocalDateTime ngayCapNhat;

    @Column(name = "ngay_phat_hanh")
    private LocalDateTime ngayPhatHanh;
    
    
}