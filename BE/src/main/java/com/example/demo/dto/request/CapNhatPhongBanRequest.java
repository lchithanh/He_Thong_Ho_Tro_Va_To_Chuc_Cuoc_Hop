package com.example.demo.dto.request;

public record CapNhatPhongBanRequest(
    String tenPhongBan,
    Integer truongPhongId,
    String moTa
) {}