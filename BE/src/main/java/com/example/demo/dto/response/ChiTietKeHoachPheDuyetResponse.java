package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record ChiTietKeHoachPheDuyetResponse(
	    Integer yeuCauPheDuyetId,
	    Integer cuocHopId,
	    String tieuDe,
	    String mucTieu,
	    String noiDungDuKien,
	    String loaiHinh,
	    String phamViCuocHop,
	    Integer lichHopId,
	    LocalDateTime thoiGianBatDau,
	    LocalDateTime thoiGianKetThuc,
	    String ghiChuTrinh,
	    String trangThai,
	    String tenNguoiTrinh,
	    String tenThuKyCuocHop,
	    String tenChuTri,
	    String tenNguoiDeXuat,
	    String tenPhongHop,
	    String viTriPhongHop,
	    QuyetDinh quyetDinh,
	    String capPheDuyet,
	    Integer yeuCauTruocId,
	    String tenNguoiDuyet,
	    LocalDateTime thoiGianTrinh
	)
{
    public record QuyetDinh(
            Integer nguoiDuyetId,
            String hoTenNguoiDuyet,
            LocalDateTime thoiGianDuyet,
            String yKien
    ) {}
}