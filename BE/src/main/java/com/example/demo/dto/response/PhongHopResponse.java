package com.example.demo.dto.response;

public record PhongHopResponse(
        Integer phongHopId,
        String tenPhongHop,
        String viTri,
        Integer sucChua,
        String moTa,
        String trangThai
) {}