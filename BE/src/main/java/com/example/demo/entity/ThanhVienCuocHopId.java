
package com.example.demo.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class ThanhVienCuocHopId implements Serializable {

    @Column(name = "cuoc_hop_id")
    private Integer cuocHopId;

    @Column(name = "nguoi_dung_id")
    private Integer nguoiDungId;
}
