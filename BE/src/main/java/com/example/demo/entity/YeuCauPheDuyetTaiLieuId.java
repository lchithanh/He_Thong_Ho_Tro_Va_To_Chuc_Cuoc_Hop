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
public class YeuCauPheDuyetTaiLieuId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "yeu_cau_phe_duyet_id", nullable = false)
    private Integer yeuCauPheDuyetId;

    @Column(name = "tai_lieu_id", nullable = false)
    private Integer taiLieuId;
}