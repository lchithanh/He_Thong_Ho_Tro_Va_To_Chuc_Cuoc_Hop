package com.example.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ThanhVienBieuQuyetId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "bieu_quyet_id", nullable = false)
    private Integer bieuQuyetId;

    @Column(name = "nguoi_dung_id", nullable = false)
    private Integer nguoiDungId;
}