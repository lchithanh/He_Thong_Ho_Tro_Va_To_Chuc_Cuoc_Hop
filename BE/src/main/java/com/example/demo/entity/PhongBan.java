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
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phong_ban")

@Getter
@Setter
@NoArgsConstructor
public class PhongBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phong_ban_id")
    private Integer phongBanId;

    @Column(name = "ten_phong_ban", nullable = false, length = 150)
    private String tenPhongBan;

    @Column(name = "truong_phong_id")
    private Integer truongPhongId;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai = "HOAT_DONG";

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;
    
    
 // Thông tin người được phân công làm trưởng phòng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "truong_phong_id",
        insertable = false,
        updatable = false
    )
    private NguoiDung truongPhong;

    // Danh sách người dùng thuộc phòng ban
    @OneToMany(mappedBy = "phongBan", fetch = FetchType.LAZY)
    private List<NguoiDung> danhSachNguoiDung = new ArrayList<>();
}