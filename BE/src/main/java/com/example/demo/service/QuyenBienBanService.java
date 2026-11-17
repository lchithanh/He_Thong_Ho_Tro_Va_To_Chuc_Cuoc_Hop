package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import lombok.RequiredArgsConstructor;

/** Quyền biên bản dựa trên phân công tại cuộc họp, độc lập hồ sơ kế hoạch. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuyenBienBanService {
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;

    public void kiemTraQuyenSoan(CuocHop cuocHop, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ thư ký cuộc họp được bàn giao sau khi duyệt cuối mới được xử lý biên bản");
        }
    }

    public void kiemTraQuyenXem(CuocHop cuocHop, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!laThuKyCuocHop(cuocHop, nguoiDungId)
                && !ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem biên bản và lịch sử phiên bản");
        }
    }

    private boolean laThuKyCuocHop(CuocHop cuocHop, Integer nguoiDungId) {
        var banGiao = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHop.getCuocHopId());
        return phanCongXuLyKeHoachService.laThuKyDaNhanBanGiao(banGiao, nguoiDungId)
                || thanhVienCuocHopRepository
                .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungIdAndVaiTroTaiCuocHop(
                        cuocHop.getCuocHopId(), nguoiDungId, "THU_KY");
    }
}
