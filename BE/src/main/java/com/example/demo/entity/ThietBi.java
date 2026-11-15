
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "thiet_bi")
@Getter
@Setter
@NoArgsConstructor
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thiet_bi_id")
    private Integer thietBiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_hop_id")
    private PhongHop phongHop;

    @Column(name = "ten_thiet_bi", nullable = false, length = 150)
    private String tenThietBi;

    @Column(name = "ma_thiet_bi", length = 50, unique = true)
    private String maThietBi;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "tinh_trang", nullable = false, length = 30)
    private String tinhTrang = "TOT";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;
}
