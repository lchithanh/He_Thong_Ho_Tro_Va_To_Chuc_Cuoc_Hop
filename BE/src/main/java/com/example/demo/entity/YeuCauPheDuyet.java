package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ánh xạ yêu cầu phê duyệt. */
@Entity
@Table(name = "yeu_cau_phe_duyet")
@Getter
@Setter
@NoArgsConstructor
public class YeuCauPheDuyet {

    public enum LoaiYeuCau {
        KE_HOACH_CUOC_HOP,
        LICH_HOP,
        BIEN_BAN,
        KET_LUAN
    }

    public enum TrangThai {
        CHO_DUYET,
        DA_DUYET,
        TRA_LAI,
        DA_HUY,
        TU_CHOI
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "yeu_cau_phe_duyet_id")
    private Integer yeuCauPheDuyetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_yeu_cau", nullable = false, length = 30)
    private LoaiYeuCau loaiYeuCau;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_hop_id")
    private LichHop lichHop;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "thu_ky_ban_giao_id")
private NguoiDung thuKyBanGiao;

@Column(name = "ke_hoach_da_trinh", columnDefinition = "LONGTEXT")
private String keHoachDaTrinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phien_ban_id")
    private PhienBanBienBan phienBanBienBan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "nguoi_trinh_id", nullable = false)
    private NguoiDung nguoiTrinh;

    // Người được chỉ định duyệt lần trình này.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet_id")
    private NguoiDung nguoiDuyet;

    // Thư ký lãnh đạo nhận hồ sơ do thư ký phòng chuyển lên.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thu_ky_tiep_nhan_id")
    private NguoiDung thuKyTiepNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yeu_cau_truoc_id")
    private YeuCauPheDuyet yeuCauTruoc;

    // TRUONG_PHONG hoặc LANH_DAO.
    @Column(name = "cap_phe_duyet", length = 20)
    private String capPheDuyet;

    @Column(name = "thoi_gian_trinh", nullable = false)
    private LocalDateTime thoiGianTrinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThai trangThai;

    @Column(name = "ghi_chu_trinh", columnDefinition = "TEXT")
    private String ghiChuTrinh;

    public NguoiDung getNguoiDuyet() {
        return nguoiDuyet;
    }

    public void setNguoiDuyet(NguoiDung nguoiDuyet) {
        this.nguoiDuyet = nguoiDuyet;
    }

    public NguoiDung getThuKyTiepNhan() {
        return thuKyTiepNhan;
    }

    public void setThuKyTiepNhan(NguoiDung thuKyTiepNhan) {
        this.thuKyTiepNhan = thuKyTiepNhan;
    }

    public String getCapPheDuyet() {
        return capPheDuyet;
    }

    public void setCapPheDuyet(String capPheDuyet) {
        this.capPheDuyet = capPheDuyet;
    }
}
