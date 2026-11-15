package com.example.demo.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.response.PhienBanBienBanResponse;
import com.example.demo.entity.BienBanCuocHop;
import com.example.demo.entity.PhienBanBienBan;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.PhienBanBienBanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PhienBanBienBanService {
    private final PhienBanBienBanRepository phienBanBienBanRepository;
    private final BienBanCuocHopRepository bienBanCuocHopRepository;
    private final BienBanService bienBanService;
    private final QuyenBienBanService quyenBienBanService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    private BienBanCuocHop layBienBan(Integer bienBanId) {
        kiemTraMaBienBan(bienBanId);
        return bienBanCuocHopRepository.findById(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy biên bản"));
    }

    private void kiemTraMaBienBan(Integer bienBanId) {
        if (bienBanId == null || bienBanId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã biên bản không hợp lệ");
        }
    }

    public PhienBanBienBanResponse taoPhienBan(Integer bienBanId, Integer nguoiDungId,
            String noiDung, String ghiChuThayDoi) {
        kiemTraMaBienBan(bienBanId);
        return chuyenDoiDTO(bienBanService.taoPhienBan(
                bienBanId, nguoiDungId, noiDung, ghiChuThayDoi));
    }

    @Transactional(readOnly = true)
    public List<PhienBanBienBanResponse> layDanhSachPhienBan(
            Integer bienBanId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        BienBanCuocHop bienBan = layBienBan(bienBanId);
        quyenBienBanService.kiemTraQuyenXem(bienBan.getCuocHop(), nguoiDungId);
        return phienBanBienBanRepository
                .findByBienBan_BienBanIdOrderBySoPhienBanDesc(bienBanId)
                .stream().map(this::chuyenDoiDTO).toList();
    }

    @Transactional(readOnly = true)
    public PhienBanBienBanResponse layPhienBanMoiNhat(Integer bienBanId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        BienBanCuocHop bienBan = layBienBan(bienBanId);
        quyenBienBanService.kiemTraQuyenXem(bienBan.getCuocHop(), nguoiDungId);
        return phienBanBienBanRepository
                .findFirstByBienBan_BienBanIdOrderBySoPhienBanDesc(bienBanId)
                .map(this::chuyenDoiDTO)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Biên bản chưa có phiên bản"));
    }

    private PhienBanBienBanResponse chuyenDoiDTO(PhienBanBienBan phienBan) {
        return new PhienBanBienBanResponse(
                phienBan.getPhienBanId(), phienBan.getBienBan().getBienBanId(),
                phienBan.getSoPhienBan(), phienBan.getNguoiCapNhat().getNguoiDungId(),
                phienBan.getNoiDung(), phienBan.getGhiChuThayDoi(), phienBan.getNgayTao());
    }
}
