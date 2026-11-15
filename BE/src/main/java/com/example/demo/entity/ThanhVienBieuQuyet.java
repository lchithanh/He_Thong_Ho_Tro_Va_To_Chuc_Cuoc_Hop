package com.example.demo.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "thanh_vien_bieu_quyet")
@Getter
@NoArgsConstructor
public class ThanhVienBieuQuyet {

    @EmbeddedId
    private ThanhVienBieuQuyetId id;

    public ThanhVienBieuQuyet(ThanhVienBieuQuyetId id) {
        this.id = id;
    }
}