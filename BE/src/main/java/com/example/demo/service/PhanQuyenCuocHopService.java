package com.example.demo.service;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;

import com.example.demo.entity.NguoiDung;

import com.example.demo.repository.NguoiDungRepository;

import com.example.demo.repository.NguoiDungVaiTroRepository;

import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service

@RequiredArgsConstructor

@Transactional(readOnly = true)

public class PhanQuyenCuocHopService {

    private final NguoiDungRepository nguoiDungRepository;

    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;

    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;

    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;

    // =====================================================

    // LẤY NGƯỜI DÙNG

    // =====================================================

    private NguoiDung timNguoiDung(Integer nguoiDungId) {

        if (nguoiDungId == null) {

            throw new RuntimeException(

                    "Mã người dùng không được để trống"

            );

        }

        return nguoiDungRepository.findById(nguoiDungId)

                .orElseThrow(() -> new RuntimeException(

                        "Không tìm thấy người dùng"

                ));

    }

    // =====================================================

    // KIỂM TRA VAI TRÒ HỆ THỐNG

    // =====================================================

    public boolean coVaiTro(

            Integer nguoiDungId,

            String maVaiTro) {

        if (nguoiDungId == null

                || maVaiTro == null

                || maVaiTro.isBlank()) {

            return false;

        }

        return nguoiDungVaiTroRepository

                .findByNguoiDungIdAndVaiTro_MaVaiTro(

                        nguoiDungId,

                        maVaiTro.trim().toUpperCase()

                )

                .isPresent();

    }

    public void kiemTraVaiTro(

            Integer nguoiDungId,

            String maVaiTro) {

        if (!coVaiTro(nguoiDungId, maVaiTro)) {

            throw new RuntimeException(

                    "Bạn không có vai trò phù hợp để thực hiện thao tác này"

            );

        }

    }

    // =====================================================

    // KIỂM TRA TRƯỞNG PHÒNG ĐƯỢC PHÂN CÔNG

    // =====================================================

    public boolean laTruongPhong(Integer nguoiDungId) {

        return nguoiDungId != null

                && NguoiDungHienTaiService.laTruongPhongCuaPhongBanMinh(timNguoiDung(nguoiDungId));

    }

    public void kiemTraLaTruongPhong(Integer nguoiDungId) {

        if (!laTruongPhong(nguoiDungId)) {

            throw new ResponseStatusException(

                    HttpStatus.FORBIDDEN,

                    "Chỉ trưởng phòng được phân công mới có quyền thực hiện thao tác này"

            );

        }

    }

    // =====================================================

    // KIỂM TRA PHÒNG BAN

    // =====================================================

    public Integer layPhongBanId(Integer nguoiDungId) {

        NguoiDung nguoiDung = timNguoiDung(nguoiDungId);

        if (nguoiDung.getPhongBan() == null) {

            throw new RuntimeException(

                    "Người dùng chưa được phân công vào phòng ban"

            );

        }

        return nguoiDung.getPhongBan().getPhongBanId();

    }

    public boolean laTruongPhongCuaPhongBan(

	        Integer nguoiDungId,

	        Integer phongBanId) {

	    if (nguoiDungId == null || phongBanId == null) {

	        return false;

	    }

	    NguoiDung nguoiDung = timNguoiDung(nguoiDungId);

	    return NguoiDungHienTaiService.laTruongPhongCuaPhongBanMinh(nguoiDung)

	            && NguoiDungHienTaiService.thuocPhongBan(nguoiDung, phongBanId);

	}

	public boolean cungPhongBan(Integer nguoiDungId, Integer phongBanId) {

        if (nguoiDungId == null || phongBanId == null) {

            return false;

        }

        NguoiDung nguoiDung = timNguoiDung(nguoiDungId);

        if (nguoiDung.getPhongBan() == null) {

            throw new RuntimeException(

                    "Người dùng chưa được phân công vào phòng ban");

        }

        return NguoiDungHienTaiService.thuocPhongBan(nguoiDung, phongBanId);

    }

    public void kiemTraCungPhongBan(

            Integer nguoiDungId,

            Integer phongBanId) {

        if (!cungPhongBan(nguoiDungId, phongBanId)) {

            throw new ResponseStatusException(

                    HttpStatus.FORBIDDEN,

                    "Bạn chỉ được tổ chức cuộc họp trong phòng ban của mình"

            );

        }

    }

    // =====================================================

    // KIỂM TRA VAI TRÒ THEO TỪNG CUỘC HỌP

    // =====================================================

private boolean coVaiTroTaiCuocHop(

        Integer cuocHopId,

        Integer nguoiDungId,

        String maVaiTro) {

    if (cuocHopId == null || nguoiDungId == null) {

        return false;

    }

    return thanhVienCuocHopRepository

            .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungIdAndVaiTroTaiCuocHop(

                    cuocHopId, nguoiDungId, maVaiTro);

}

    public boolean laThuKy(Integer cuocHopId, Integer nguoiDungId) {
        return phanCongXuLyKeHoachService.laThuKyDangXuLy(cuocHopId, nguoiDungId);
    }

    public boolean laLanhDao(Integer cuocHopId, Integer nguoiDungId) {
        return coVaiTroTaiCuocHop(cuocHopId, nguoiDungId, "LANH_DAO")
                || (coVaiTro(nguoiDungId, "LANH_DAO")
                && phanCongXuLyKeHoachService.laNguoiDuyetMoiNhat(cuocHopId, nguoiDungId));
    }

    public boolean laThanhVien(Integer cuocHopId, Integer nguoiDungId) {
        return coVaiTroTaiCuocHop(cuocHopId, nguoiDungId, "THAM_DU")
                || phanCongXuLyKeHoachService.laThuKyThamGia(cuocHopId, nguoiDungId)
                || laLanhDao(cuocHopId, nguoiDungId);
    }

    // =====================================================

    // KIỂM TRA QUYỀN THƯ KÝ

    // =====================================================

    public void kiemTraLaThuKy(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!laThuKyDuocPhanCong(cuocHopId, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không phải thư ký đang xử lý cuộc họp này");
        }
    }

    // =====================================================

    // CẦN TRÌNH LÃNH ĐẠO

    // =====================================================

    private boolean canTrinhLanhDao(CuocHop cuocHop) {
        return QuyTacDuyetKeHoach.xacDinhCapDuyet(
                coVaiTro(cuocHop.getNguoiDeXuat().getNguoiDungId(), "LANH_DAO"),
                cuocHop.getPhamViCuocHop(), Boolean.TRUE.equals(cuocHop.getCoKhachMoiBenNgoai()))
                == QuyTacDuyetKeHoach.CapPheDuyet.LANH_DAO;
    }

    // =====================================================

    // LA NỘI BÔ TRƯỞNG PHÒNG TỰ CHỦ TRÌ

    // =====================================================

    // =====================================================

    // KIỂM TRA QUYỀN LÃNH ĐẠO

    // =====================================================

    public void kiemTraLaLanhDao(

            Integer cuocHopId,

            Integer nguoiDungId) {

        if (!laLanhDao(cuocHopId, nguoiDungId)) {

            throw new RuntimeException(

                    "Bạn không có vai trò lãnh đạo trong cuộc họp này"

            );

        }

    }

    // =====================================================

    // KIỂM TRA THÀNH VIÊN

    // =====================================================

    public void kiemTraLaThanhVien(

            Integer cuocHopId,

            Integer nguoiDungId) {

        if (!laThanhVien(cuocHopId, nguoiDungId)) {

            throw new RuntimeException(

                    "Bạn không thuộc thành phần tham gia cuộc họp này"

            );

        }

    }

    //    Kiểm tra Thư ký được phân công

    public boolean laThuKyDuocPhanCong(Integer cuocHopId, Integer nguoiDungId) {
        return phanCongXuLyKeHoachService.laThuKyDangXuLy(cuocHopId, nguoiDungId);
    }

    //quyền đc tạo đề xuất

    public void kiemTraQuyenTaoDeXuat(Integer nguoiDungId) {

        NguoiDung nguoiDung = timNguoiDung(nguoiDungId);

        if (!"HOAT_DONG".equals(nguoiDung.getTrangThai())) {

        	throw new ResponseStatusException(

        	        HttpStatus.FORBIDDEN,

        	        "Tài khoản không hoạt động"

        	);

        }

        if (!coVaiTro(nguoiDungId, "LANH_DAO")

                && !laTruongPhong(nguoiDungId)) {

        	throw new ResponseStatusException(

        	        HttpStatus.FORBIDDEN,

        	        "Chỉ lãnh đạo hoặc trưởng phòng được tạo đề xuất cuộc họp"

        	);

        }

    }

    public void kiemTraQuyenTrinhKeHoach(

            Integer cuocHopId,

            Integer nguoiDungId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        if (!laThuKyDuocPhanCong(cuocHopId, nguoiDungId)) {

            throw new ResponseStatusException(

                    HttpStatus.FORBIDDEN,

                    "Chỉ thư ký được phân công mới có quyền trình kế hoạch cuộc họp này");

        }

    }

    public boolean coQuyenXemChiTietDeXuat(CuocHop cuocHop, Integer nguoiDungId) {
        if (nguoiDungId == null) return false;
        return ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDungId) || coVaiTro(nguoiDungId, "ADMIN")
                || (!"NHAP".equals(cuocHop.getTrangThai())
                && (ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId)
                || phanCongXuLyKeHoachService.laThuKyThamGia(cuocHop.getCuocHopId(), nguoiDungId)
                || ("CHO_GUI_CAP_TREN".equals(cuocHop.getBuocXuLyHienTai())
                        && phanCongXuLyKeHoachService.laThuKyDangXuLy(cuocHop.getCuocHopId(), nguoiDungId))
                || phanCongXuLyKeHoachService.laNguoiDuyetMoiNhat(cuocHop.getCuocHopId(), nguoiDungId)));
    }

}



