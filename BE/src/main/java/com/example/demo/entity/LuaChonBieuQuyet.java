
package com.example.demo.entity;

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
@Table(name = "lua_chon_bieu_quyet")
@Getter
@Setter
@NoArgsConstructor
public class LuaChonBieuQuyet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lua_chon_id")
    private Integer luaChonId;

    // Biểu quyết chứa lựa chọn này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bieu_quyet_id", nullable = false)
    private BieuQuyet bieuQuyet;

    @Column(name = "noi_dung_lua_chon", nullable = false, length = 255)
    private String noiDungLuaChon;

    @Column(name = "la_lua_chon_trang_thai", nullable = false)
    private Boolean laLuaChonTrangThai = false;

    @Column(name = "thu_tu_hien_thi", nullable = false)
    private Integer thuTuHienThi = 1;

    // Các phiếu đã chọn lựa chọn này
    @OneToMany(mappedBy = "luaChon", fetch = FetchType.LAZY)
    private List<PhieuBieuQuyet> danhSachPhieu = new ArrayList<>();
}
