package com.example.demo.dto.response;

public record NguoiCoTheMoiResponse(
        Integer nguoiDungId,
        String hoTen,
        String tenPhongBan,
        String chucVu
) {}