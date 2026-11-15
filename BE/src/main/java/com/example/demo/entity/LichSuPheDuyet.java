
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
@Table(name = "lich_su_phe_duyet")
@Getter
@Setter
@NoArgsConstructor
public class LichSuPheDuyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lich_su_phe_duyet_id")
    private Integer lichSuPheDuyetId;

    // Cuộc họp liên quan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id", nullable = false)
    private CuocHop cuocHop;

    // Lịch họp được phê duyệt, có thể NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lich_hop_id")
    private LichHop lichHop;

    // Biên bản được phê duyệt, có thể NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_ban_id")
    private BienBanCuocHop bienBan;

    // Người thực hiện phê duyệt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_phe_duyet_id", nullable = false)
    private NguoiDung nguoiPheDuyet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yeu_cau_phe_duyet_id")
    private YeuCauPheDuyet yeuCauPheDuyet;
    
    @Column(name = "loai_phe_duyet", nullable = false, length = 30)
    private String loaiPheDuyet;

    @Column(name = "hanh_dong", nullable = false, length = 30)
    private String hanhDong;

    @Column(name = "y_kien", columnDefinition = "TEXT")
    private String yKien;

    @Column(
        name = "thoi_gian_phe_duyet",
        insertable = false,
        updatable = false
    )
    private LocalDateTime thoiGianPheDuyet;
    
}
