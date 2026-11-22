
package com.example.demo.entity;

import java.math.BigDecimal;
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
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bieu_quyet")
@Getter
@Setter
@NoArgsConstructor
public class BieuQuyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bieu_quyet_id")
    private Integer bieuQuyetId;

    // Cuộc họp có biểu quyết này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    // Người tạo biểu quyết
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_tao_id", nullable = false)
    private NguoiDung nguoiTao;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "che_do_bieu_quyet", nullable = false, length = 20)
    private String cheDoBieuQuyet = "CONG_KHAI";

    @Column(name = "cho_phep_trang_thai", nullable = false)
    private Boolean choPhepTrangThai = true;

    @Column(name = "ty_le_tham_gia_toi_thieu", precision = 5, scale = 2)
    private BigDecimal tyLeThamGiaToiThieu;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "trang_thai", nullable = false, length = 25)
    private String trangThai = "NHAP";

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "nghi_quyet_id")
    private Integer nghiQuyetId;

    @Column(name = "tai_lieu_nghi_quyet_id")
    private Integer taiLieuNghiQuyetId;

    @Column(name = "ket_qua_nghi_quyet")
    private String ketQuaNghiQuyet;

    // Các lựa chọn của biểu quyết
    @OneToMany(mappedBy = "bieuQuyet", fetch = FetchType.LAZY)
    private List<LuaChonBieuQuyet> danhSachLuaChon = new ArrayList<>();

    // Các phiếu biểu quyết
    @OneToMany(mappedBy = "bieuQuyet", fetch = FetchType.LAZY)
    private List<PhieuBieuQuyet> danhSachPhieu = new ArrayList<>();

    @Column(name = "so_thanh_vien_co_mat_khi_dong")
    private Long soThanhVienCoMatKhiDong;
}
