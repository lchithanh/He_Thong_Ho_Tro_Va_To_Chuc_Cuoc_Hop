package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.response.TaiLieuKetLuanResponse;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.KetLuanCuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KetLuanCuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaiLieuKetLuanService {

    private static final String LOAI_KET_LUAN = "KET_LUAN_CUOC_HOP";

    private final CuocHopRepository cuocHopRepository;
    private final KetLuanCuocHopRepository ketLuanCuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final LuuTaiLieuCuocHopService luuTaiLieuCuocHopService;
    
    private static final int DUNG_LUONG_TOI_DA = 10 * 1024 * 1024;

    private final KetLuanCuocHopService ketLuanCuocHopService;
    private final TaiLieuCuocHopRepository taiLieuCuocHopRepository;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;

    @Value("${app.upload.ket-luan-dir:uploads/ket-luan-cuoc-hop}")
    private String thuMucTaiLieuKetLuan;
    
    public record TepKetLuanTaiXuong(String tenFile, byte[] noiDung) {
    }

    @Transactional
    public Integer taiLenTaiLieuKetLuan(
            Integer cuocHopId,
            MultipartFile file,
            Integer nguoiDangNhapId) {

        if (nguoiDangNhapId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }
        if (cuocHopId == null || cuocHopId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }

        var cuocHopDaKhoa = cuocHopRepository.findByIdForUpdate(cuocHopId);
        NguoiDung nguoiTaiLen = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopDaKhoa.orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        if (!phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, nguoiDangNhapId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ thư ký cuộc họp được bàn giao sau khi duyệt cuối mới được tải tài liệu kết luận");
        }

        KetLuanCuocHop ketLuan = ketLuanCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có kết luận"));

        if (!"NHAP".equals(ketLuan.getTrangThai())
                && !"BI_TRA_LAI".equals(ketLuan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ được thêm tài liệu khi kết luận còn nháp hoặc bị trả lại");
        }

        Path thuMuc = Path.of(thuMucTaiLieuKetLuan).toAbsolutePath().normalize();

        return luuTaiLieuCuocHopService.luuTaiLieu(
                cuocHop, nguoiTaiLen, file, LOAI_KET_LUAN, thuMuc);
    }
    

    @Transactional(readOnly = true)
    public List<TaiLieuKetLuanResponse> layDanhSachTaiLieuKetLuan(
            Integer cuocHopId, Integer nguoiDangNhapId) {

        ketLuanCuocHopService.timKetLuanDuocXem(cuocHopId, nguoiDangNhapId);

        return taiLieuCuocHopRepository
                .findByCuocHop_CuocHopIdAndLoaiTaiLieuOrderByTaiLieuIdAsc(
                        cuocHopId, LOAI_KET_LUAN)
                .stream()
                .map(taiLieu -> new TaiLieuKetLuanResponse(
                        taiLieu.getTaiLieuId(),
                        taiLieu.getTenFileGoc(),
                        taiLieu.getDinhDangFile(),
                        taiLieu.getDungLuongByte()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TepKetLuanTaiXuong taiXuongTaiLieuKetLuan(
            Integer cuocHopId, Integer taiLieuId, Integer nguoiDangNhapId) {

        ketLuanCuocHopService.timKetLuanDuocXem(cuocHopId, nguoiDangNhapId);

        if (taiLieuId == null || taiLieuId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã tài liệu không hợp lệ");
        }

        TaiLieuCuocHop taiLieu = taiLieuCuocHopRepository.findById(taiLieuId)
                .filter(tl -> cuocHopId.equals(tl.getCuocHop().getCuocHopId()))
                .filter(tl -> LOAI_KET_LUAN.equals(tl.getLoaiTaiLieu()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy tài liệu kết luận"));

        if (taiLieu.getDuongDanFile() == null || taiLieu.getDuongDanFile().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Tài liệu kết luận chưa có file");
        }

        try {
            Path thuMuc = Path.of(thuMucTaiLieuKetLuan).toRealPath();
            Path file = Path.of(taiLieu.getDuongDanFile()).toRealPath();

            if (!file.startsWith(thuMuc) || !Files.isRegularFile(file)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy file kết luận");
            }

            byte[] noiDung;
            try (InputStream input = Files.newInputStream(file)) {
                noiDung = input.readNBytes(DUNG_LUONG_TOI_DA + 1);
            }
            if (noiDung.length > DUNG_LUONG_TOI_DA) {
                throw new ResponseStatusException(
                        HttpStatus.PAYLOAD_TOO_LARGE, "File vượt quá 10 MB");
            }

            String tenFile = taiLieu.getTenFileGoc();
            tenFile = tenFile == null ? "" : tenFile.replaceAll("[\\p{Cntrl}]", "");
            
            return new TepKetLuanTaiXuong(
                    tenFile.isBlank() ? "tai-lieu-ket-luan" : tenFile,
                    noiDung);

        } catch (NoSuchFileException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "File kết luận không còn trên máy chủ");
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Không thể đọc file kết luận", ex);
        }
    }
}