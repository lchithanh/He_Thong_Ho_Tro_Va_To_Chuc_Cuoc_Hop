package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "phong_hop")
@Getter
@Setter
@NoArgsConstructor
public class PhongHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phong_hop_id")
    private Integer phongHopId;

    @Column(name = "ten_phong_hop", nullable = false, length = 120)
    private String tenPhongHop;

    @Column(name = "vi_tri", length = 255)
    private String viTri;

    @Column(name = "suc_chua", nullable = false)
    private Integer sucChua = 1;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "trang_thai", nullable = false, length = 25)
    private String trangThai = "SAN_SANG";

    // Danh sách lịch họp sử dụng phòng này
    @OneToMany(mappedBy = "phongHop", fetch = FetchType.LAZY)
    private List<LichHop> danhSachLichHop = new ArrayList<>();

    // Danh sách thiết bị thuộc phòng này
    @OneToMany(mappedBy = "phongHop", fetch = FetchType.LAZY)
    private List<ThietBi> danhSachThietBi = new ArrayList<>();
}
