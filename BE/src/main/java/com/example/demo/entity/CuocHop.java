
package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ cuộc họp. */
@Entity
@Table(name = "cuoc_hop")
@Getter
@Setter
@NoArgsConstructor
public class CuocHop {

    @Column(name = "co_khach_moi_ben_ngoai", nullable = false)
    private Boolean coKhachMoiBenNgoai;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cuoc_hop_id")
    private Integer cuocHopId;

    @Column(name = "ma_cuoc_hop", length = 40, unique = true)
    private String maCuocHop;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "muc_tieu", columnDefinition = "TEXT")
    private String mucTieu;

    @Column(name = "noi_dung_du_kien", columnDefinition = "LONGTEXT")
    private String noiDungDuKien;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "de_xuat_id", unique = true)
    private DeXuatCuocHop deXuat;

    public Integer getThoiLuongDuKienPhut() {
        return deXuat == null ? null : deXuat.getThoiLuongDuKienPhut();
    }

    public ThongTinBoSungDeXuat getThongTinDeXuat() {
        return deXuat == null ? null : deXuat.getThongTinBoSung();
    }

    @Column(name = "loai_hinh", nullable = false, length = 20)
    private String loaiHinh;

    @Column(name = "pham_vi_cuoc_hop", nullable = false, length = 30)
    private String phamViCuocHop;

    // Phòng ban chủ trì cuộc họp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_ban_chu_tri_id")
    private PhongBan phongBanChuTri;

    // Người tạo cuộc họp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_tao_id", nullable = false)
    private NguoiDung nguoiTao;

    // Người đề xuất cuộc họp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_de_xuat_id", nullable = false)
    private NguoiDung nguoiDeXuat;

    // Người chủ trì cuộc họp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_chu_tri_id")
    private NguoiDung nguoiChuTri;

    @Column(name = "buoc_xu_ly_hien_tai", nullable = false, length = 35)
    private String buocXuLyHienTai;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "thoi_gian_bat_dau_thuc_te")
    private LocalDateTime thoiGianBatDauThucTe;

    @Column(name = "thoi_gian_ket_thuc_thuc_te")
    private LocalDateTime thoiGianKetThucThucTe;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat", insertable = false, updatable = false)
    private LocalDateTime ngayCapNhat;

    // Danh sách lịch họp
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<LichHop> danhSachLichHop = new ArrayList<>();

    // Danh sách thành viên
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<ThanhVienCuocHop> danhSachThanhVien = new ArrayList<>();

    // Danh sách tài liệu
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<TaiLieuCuocHop> danhSachTaiLieu = new ArrayList<>();

    // Danh sách biểu quyết
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<BieuQuyet> danhSachBieuQuyet = new ArrayList<>();

    // Danh sách biên bản
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<BienBanCuocHop> danhSachBienBan = new ArrayList<>();

    // Danh sách nhiệm vụ
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<NhiemVu> danhSachNhiemVu = new ArrayList<>();

    // Kết quả xử lý AI
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<KetQuaXuLyAI> danhSachKetQuaAI = new ArrayList<>();

    // Lịch sử phê duyệt
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<LichSuPheDuyet> danhSachLichSuPheDuyet = new ArrayList<>();

    // Thông báo liên quan đến cuộc họp
    @OneToMany(mappedBy = "cuocHop", fetch = FetchType.LAZY)
    private List<ThongBao> danhSachThongBao = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "thu_ky_cuoc_hop_id")
private NguoiDung thuKyCuocHop;
}
