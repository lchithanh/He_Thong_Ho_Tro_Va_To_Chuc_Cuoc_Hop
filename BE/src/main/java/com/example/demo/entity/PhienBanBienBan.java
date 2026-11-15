
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
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/** Bản chụp nội dung; Service kiểm tra điều kiện tạo và cấp số phiên bản. */
@Entity
@Table(name = "phien_ban_bien_ban")
@Getter
@NoArgsConstructor
public class PhienBanBienBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phien_ban_id")
    private Integer phienBanId;

    // Biên bản được tạo phiên bản
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_ban_id", nullable = false)
    @Setter
    private BienBanCuocHop bienBan;

    // Người cập nhật phiên bản
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_cap_nhat_id", nullable = false)
    @Setter
    private NguoiDung nguoiCapNhat;

    @Column(name = "so_phien_ban", nullable = false)
    @Setter
    private Integer soPhienBan;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "LONGTEXT")
    @Setter
    private String noiDung;

    @Column(name = "ghi_chu_thay_doi", columnDefinition = "TEXT")
    @Setter
    private String ghiChuThayDoi;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;
}
