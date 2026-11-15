package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhanCongThuKyRequest {

    private Integer nguoiDuocHoTroId;

    private Integer thuKyId;
}