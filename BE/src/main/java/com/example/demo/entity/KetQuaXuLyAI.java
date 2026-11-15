
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
@Table(name = "ket_qua_xu_ly_ai")
@Getter
@Setter
@NoArgsConstructor
public class KetQuaXuLyAI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ket_qua_ai_id")
    private Integer ketQuaAiId;

    // Cuộc họp cần xử lý
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    // Tài liệu đầu vào, có thể không có
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_lieu_id")
    private TaiLieuCuocHop taiLieu;

    // Người yêu cầu AI xử lý
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_yeu_cau_id", nullable = false)
    private NguoiDung nguoiYeuCau;

    @Column(name = "loai_xu_ly", nullable = false, length = 40)
    private String loaiXuLy;

    @Column(name = "noi_dung_dau_vao", columnDefinition = "LONGTEXT")
    private String noiDungDauVao;

    @Column(name = "ket_qua_dau_ra", columnDefinition = "LONGTEXT")
    private String ketQuaDauRa;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = "CHO_XU_LY";

    @Column(name = "thong_bao_loi", columnDefinition = "TEXT")
    private String thongBaoLoi;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;
}
