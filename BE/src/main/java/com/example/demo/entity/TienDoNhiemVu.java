
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

@Entity
@Table(name = "tien_do_nhiem_vu")
@Getter
@Setter
@NoArgsConstructor
public class TienDoNhiemVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tien_do_id")
    private Integer tienDoId;

    // Nhiệm vụ được cập nhật tiến độ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhiem_vu_id", nullable = false)
    private NhiemVu nhiemVu;

    // Người cập nhật
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_cap_nhat_id", nullable = false)
    private NguoiDung nguoiCapNhat;

    @Column(name = "phan_tram_hoan_thanh", nullable = false)
    private Integer phanTramHoanThanh = 0;

    @Column(name = "noi_dung_cap_nhat", nullable = false, columnDefinition = "TEXT")
    private String noiDungCapNhat;

    @Column(name = "ngay_cap_nhat", insertable = false, updatable = false)
    private LocalDateTime ngayCapNhat;
}
