package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "yeu_cau_phe_duyet_tai_lieu")
@Getter
@NoArgsConstructor
public class YeuCauPheDuyetTaiLieu {

    @EmbeddedId
    @Setter
    private YeuCauPheDuyetTaiLieuId id;

    @MapsId("yeuCauPheDuyetId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "yeu_cau_phe_duyet_id", nullable = false)
    @Setter
    private YeuCauPheDuyet yeuCauPheDuyet;

    @MapsId("taiLieuId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tai_lieu_id", nullable = false)
    @Setter
    private TaiLieuCuocHop taiLieu;

    @Column(
            name = "ngay_dinh_kem",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime ngayDinhKem;
}