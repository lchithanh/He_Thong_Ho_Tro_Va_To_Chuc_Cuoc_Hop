
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
@Table(name = "thong_bao")
@Getter
@Setter
@NoArgsConstructor
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thong_bao_id")
    private Integer thongBaoId;

    // Người nhận thông báo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    // Cuộc họp liên quan, có thể NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuoc_hop_id")
    private CuocHop cuocHop;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "loai_thong_bao", nullable = false, length = 40)
    private String loaiThongBao = "HE_THONG";

    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc = false;

    @Column(name = "thoi_gian_doc")
    private LocalDateTime thoiGianDoc;

    @Column(
        name = "ngay_tao",
        insertable = false,
        updatable = false
    )
    private LocalDateTime ngayTao;
}
