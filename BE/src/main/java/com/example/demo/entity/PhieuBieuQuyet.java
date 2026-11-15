
package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "phieu_bieu_quyet")
@Getter
@Setter
@NoArgsConstructor
public class PhieuBieuQuyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phieu_bieu_quyet_id")
    private Integer phieuBieuQuyetId;

    // Biểu quyết mà phiếu thuộc về
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bieu_quyet_id", nullable = false)
    private BieuQuyet bieuQuyet;

    // ID lựa chọn được ghi vào database
    @Column(name = "lua_chon_id", nullable = false)
    private Integer luaChonId;

    // Người tham gia biểu quyết
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    // Quan hệ lựa chọn, chỉ dùng để đọc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(
            name = "lua_chon_id",
            referencedColumnName = "lua_chon_id",
            insertable = false,
            updatable = false
        ),
        @JoinColumn(
            name = "bieu_quyet_id",
            referencedColumnName = "bieu_quyet_id",
            insertable = false,
            updatable = false
        )
    })
    private LuaChonBieuQuyet luaChon;
    @Column(name = "ma_phieu", length = 64)
    private String maPhieu;

    @Column(
        name = "thoi_gian_bieu_quyet",
        insertable = false,
        updatable = false
    )
    private LocalDateTime thoiGianBieuQuyet;
}
