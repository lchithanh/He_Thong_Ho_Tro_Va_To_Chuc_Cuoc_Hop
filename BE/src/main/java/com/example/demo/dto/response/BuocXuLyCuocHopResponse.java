package com.example.demo.dto.response;

public record BuocXuLyCuocHopResponse(
        String message,
        Integer cuocHopId,
        String trangThai,
        String buocXuLyHienTai
) {}