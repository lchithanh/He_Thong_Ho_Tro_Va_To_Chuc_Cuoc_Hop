
package com.example.demo.dto.response;

public record ThietBiResponse(
    Integer thietBiId,
    Integer phongHopId,
    String tenPhongHop,
    String tenThietBi,
    String maThietBi,
    Integer soLuong,
    String tinhTrang,
    String ghiChu
) {}
