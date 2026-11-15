package com.example.demo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nhat_ky_he_thong")
@Getter
@Setter
@NoArgsConstructor
public class NhatKyHeThong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nhat_ky_id")
    private Integer nhatKyId;

    // Người thực hiện hành động, có thể NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "hanh_dong", nullable = false, length = 100)
    private String hanhDong;

    @Column(name = "doi_tuong", nullable = false, length = 60)
    private String doiTuong;

    @Column(name = "doi_tuong_id")
    private Integer doiTuongId;

    @Column(name = "dia_chi_ip", length = 45)
    private String diaChiIp;

    // Dữ liệu JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chi_tiet", columnDefinition = "JSON")
    private String chiTiet;

    @Column(
        name = "ngay_tao",
        insertable = false,
        updatable = false
    )
    private LocalDateTime ngayTao;
}
