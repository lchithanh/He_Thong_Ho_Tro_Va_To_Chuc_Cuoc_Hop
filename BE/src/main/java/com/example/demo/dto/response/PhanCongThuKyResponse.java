package com.example.demo.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PhanCongThuKyResponse {

    private Integer nguoiDuocHoTroId;
    private String tenNguoiDuocHoTro;

    private Integer thuKyId;
    private String tenThuKy;

    private Integer nguoiPhanCongId;
    private String tenNguoiPhanCong;

    private LocalDateTime ngayPhanCong;
    private LocalDateTime ngayCapNhat;
}