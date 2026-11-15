package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phan_cong_thu_ky")
@Getter
@Setter
@NoArgsConstructor
public class PhanCongThuKy {

    @Id
    @Column(name = "nguoi_duoc_ho_tro_id")
    private Integer nguoiDuocHoTroId;

    // Người được hỗ trợ đồng thời quyết định khóa chính.
    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguoi_duoc_ho_tro_id", nullable = false)
    private NguoiDung nguoiDuocHoTro;

    // Một thư ký có thể hỗ trợ nhiều người.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "thu_ky_id", nullable = false)
    private NguoiDung thuKy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguoi_phan_cong_id", nullable = false)
    private NguoiDung nguoiPhanCong;

    @Column(name = "ngay_phan_cong", nullable = false, updatable = false)
    private LocalDateTime ngayPhanCong;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @PrePersist
    protected void truocKhiTao() {
        ngayPhanCong = LocalDateTime.now();
    }

    @PreUpdate
    protected void truocKhiCapNhat() {
        ngayCapNhat = LocalDateTime.now();
    }
}	