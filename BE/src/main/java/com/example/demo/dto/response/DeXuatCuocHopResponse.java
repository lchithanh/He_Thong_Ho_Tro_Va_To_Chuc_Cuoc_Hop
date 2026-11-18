package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record DeXuatCuocHopResponse(
        Integer cuocHopId,
        String maCuocHop,
        String tieuDe,
        String mucTieu,
        String noiDungDuKien,
        String loaiHinh,
        String phamViCuocHop,
        Integer phongBanChuTriId,
        Integer nguoiDeXuatId,
        String tenNguoiDeXuat,
        Integer nguoiChuTriId,
        String trangThai,
        String buocXuLyHienTai,
        LocalDateTime ngayTao,
        Boolean coKhachMoiBenNgoai,
        PhanHoiKeHoach phanHoiKeHoach,
        Integer thuKyCuocHopId,
        Integer yeuCauBanGiaoId,
        boolean duocLapKeHoach,
        boolean duocToChuc,
        boolean daHoanTatHoSo,
        String vaiTroNguoiDeXuat,
        Integer yeuCauChoGuiCapTrenId,
        Integer nguoiDangXuLyId,
        String tenNguoiDangXuLy,
        Integer lichHopIdKeHoachDaDuyet,
        String tenNguoiChuTri,
        String tenThuKyCuocHop,
        LocalDateTime thoiGianBatDauThucTe,
        LocalDateTime thoiGianKetThucThucTe,
        boolean coLichDaXacNhan
) {
    public record PhanHoiKeHoach(
            Integer yeuCauPheDuyetId,
            String trangThai,
            String lyDo,
            String nguoiXuLy,
            LocalDateTime thoiGian
    ) {}
}