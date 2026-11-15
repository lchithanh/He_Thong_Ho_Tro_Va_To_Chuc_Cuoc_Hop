
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
@Table(name = "tai_lieu_cuoc_hop")
@Getter
@Setter
@NoArgsConstructor
public class TaiLieuCuocHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tai_lieu_id")
    private Integer taiLieuId;

    // Cuộc họp chứa tài liệu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    // Người tải tài liệu lên
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_tai_len_id", nullable = false)
    private NguoiDung nguoiTaiLen;

    @Column(name = "ten_tai_lieu", nullable = false, length = 255)
    private String tenTaiLieu;

    @Column(name = "loai_tai_lieu", nullable = false, length = 40)
    private String loaiTaiLieu = "TAI_LIEU_DAU_VAO";

    @Column(name = "ten_file_goc", length = 255)
    private String tenFileGoc;

    @Column(name = "duong_dan_file", length = 700)
    private String duongDanFile;

    @Column(name = "dinh_dang_file", length = 20)
    private String dinhDangFile;

    @Column(name = "dung_luong_byte")
    private Long dungLuongByte;

    @Column(name = "noi_dung_van_ban", columnDefinition = "LONGTEXT")
    private String noiDungVanBan;

    @Column(name = "trang_thai_xu_ly", nullable = false, length = 30)
    private String trangThaiXuLy = "CHUA_XU_LY";

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @Column(name = "ngay_tai_len", insertable = false, updatable = false)
    private LocalDateTime ngayTaiLen;
}
