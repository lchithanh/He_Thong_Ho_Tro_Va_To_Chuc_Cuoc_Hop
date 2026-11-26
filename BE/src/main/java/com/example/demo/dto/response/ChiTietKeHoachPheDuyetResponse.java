package com.example.demo.dto.response;

import java.time.LocalDateTime;

import java.util.List;

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
	    LocalDateTime thoiGianTrinh,
        ThanhPhanHopNhom thanhPhanHopNhom
	)
{
    public record ThanhPhanHopNhom(String mucDichHop, List<String> thanhVienNhom,
            List<String> nguoiMoiThem, List<String> emailKhachNgoai) {}

    public record QuyetDinh(
            Integer nguoiDuyetId,
            String hoTenNguoiDuyet,
            LocalDateTime thoiGianDuyet,
            String yKien
    ) {}
}