
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Ánh xạ biên bản cuộc họp. */
@Entity
@Table(name = "bien_ban_cuoc_hop")
@Getter
@Setter
@NoArgsConstructor
public class BienBanCuocHop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bien_ban_id")
    @Setter(AccessLevel.NONE)
    private Integer bienBanId;

    // Mỗi cuộc họp có tối đa một biên bản
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false, unique = true)
    private CuocHop cuocHop;

    // Người soạn biên bản
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_soan_id", nullable = false)
    private NguoiDung nguoiSoan;

    // Người duyệt biên bản
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet_id")
    private NguoiDung nguoiDuyet;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung_goc", columnDefinition = "LONGTEXT")
    private String noiDungGoc;

    @Column(name = "noi_dung_chinh_thuc", columnDefinition = "LONGTEXT")
    private String noiDungChinhThuc;

    @Column(name = "duong_dan_file_word", length = 700)
    private String duongDanFileWord;

    @Column(name = "duong_dan_file_pdf", length = 700)
    private String duongDanFilePdf;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "y_kien_phan_hoi", columnDefinition = "TEXT")
    private String yKienPhanHoi;

    @Column(name = "ngay_soan")
    private LocalDateTime ngaySoan;

    @Column(name = "ngay_trinh_duyet")
    private LocalDateTime ngayTrinhDuyet;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat", insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime ngayCapNhat;

    // Lịch sử các phiên bản biên bản
    @OneToMany(mappedBy = "bienBan", fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE)
    private List<PhienBanBienBan> danhSachPhienBan = new ArrayList<>();
}
