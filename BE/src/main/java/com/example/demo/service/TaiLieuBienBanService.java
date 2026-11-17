package com.example.demo.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;
import com.example.demo.service.LuuTaiLieuCuocHopService.TepTaiLieu;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaiLieuBienBanService {
    private static final String LOAI_BIEN_BAN = "BIEN_BAN_CUOC_HOP";

    private final CuocHopRepository cuocHopRepository;
    private final BienBanCuocHopRepository bienBanRepository;
    private final TaiLieuCuocHopRepository taiLieuRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final QuyenBienBanService quyenBienBanService;
    private final BienBanService bienBanService;
    private final LuuTaiLieuCuocHopService luuTaiLieuService;

    @Value("${app.upload.bien-ban-dir:uploads/bien-ban-cuoc-hop}")
    private String thuMucTaiLieu;

    public record TaiLieuBienBan(Integer taiLieuId, String tenFileGoc, String dinhDangFile, Long dungLuongByte) {}

    @Transactional
    public Integer taiLen(Integer cuocHopId, Integer nguoiDungId, MultipartFile file) {
        var nguoiTaiLen = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        var cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        quyenBienBanService.kiemTraQuyenSoan(cuocHop, nguoiDungId);
        var bienBan = bienBanRepository.findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hãy lưu nháp biên bản trước"));
        if (!"NHAP".equals(bienBan.getTrangThai()) && !"BI_TRA_LAI".equals(bienBan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ thêm tệp khi biên bản đang nháp hoặc bị trả lại");
        }
        return luuTaiLieuService.luuTaiLieu(cuocHop, nguoiTaiLen, file, LOAI_BIEN_BAN,
                Path.of(thuMucTaiLieu).toAbsolutePath().normalize());
    }

    @Transactional(readOnly = true)
    public List<TaiLieuBienBan> layDanhSach(Integer cuocHopId, Integer nguoiDungId) {
        bienBanService.layBienBanDuocXem(cuocHopId, nguoiDungId);
        return taiLieuRepository.findByCuocHop_CuocHopIdAndLoaiTaiLieuOrderByTaiLieuIdAsc(cuocHopId, LOAI_BIEN_BAN)
                .stream().map(tep -> new TaiLieuBienBan(tep.getTaiLieuId(), tep.getTenFileGoc(),
                        tep.getDinhDangFile(), tep.getDungLuongByte()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TepTaiLieu taiXuong(Integer cuocHopId, Integer taiLieuId, Integer nguoiDungId) {
        bienBanService.layBienBanDuocXem(cuocHopId, nguoiDungId);
        var taiLieu = taiLieuRepository.findById(taiLieuId)
                .filter(tep -> cuocHopId.equals(tep.getCuocHop().getCuocHopId()))
                .filter(tep -> LOAI_BIEN_BAN.equals(tep.getLoaiTaiLieu()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tệp biên bản"));
        return luuTaiLieuService.docTep(taiLieu, Path.of(thuMucTaiLieu));
    }
}
