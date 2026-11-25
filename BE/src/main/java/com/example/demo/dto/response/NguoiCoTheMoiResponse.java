package com.example.demo.dto.response;

public record NguoiCoTheMoiResponse(
        Integer nguoiDungId,
        String hoTen,
        String tenPhongBan,
        String chucVu,
        boolean thuocNhomThamDu
) {
    public NguoiCoTheMoiResponse(Integer nguoiDungId, String hoTen, String tenPhongBan, String chucVu) {
        this(nguoiDungId, hoTen, tenPhongBan, chucVu, false);
    }
}