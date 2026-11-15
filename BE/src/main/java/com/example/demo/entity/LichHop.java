package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Ánh xạ lịch họp và lịch được thay thế. */
@Entity
@Table(name = "lich_hop")
@Getter
@Setter
@NoArgsConstructor
public class LichHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lich_hop_id")
    private Integer lichHopId;

    // Cuộc họp sở hữu lịch này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    // Phòng họp được đề xuất
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_hop_id")
    private PhongHop phongHop;

    // Người đề xuất lịch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_de_xuat_id", nullable = false)
    private NguoiDung nguoiDeXuat;

    // Người xác nhận lịch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_xac_nhan_id")
    private NguoiDung nguoiXacNhan;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "diem_phu_hop", precision = 5, scale = 2)
    private BigDecimal diemPhuHop;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "ly_do_thay_doi", columnDefinition = "TEXT")
    private String lyDoThayDoi;

    @Column(name = "thoi_gian_xac_nhan")
    private LocalDateTime thoiGianXacNhan;

    // Lịch cũ được lịch này thay thế
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_thay_the_id")
    private LichHop lichThayThe;

    // Các lịch mới thay thế lịch hiện tại
    @OneToMany(mappedBy = "lichThayThe", fetch = FetchType.LAZY)
    private List<LichHop> danhSachLichThayThe = new ArrayList<>();

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;
}
