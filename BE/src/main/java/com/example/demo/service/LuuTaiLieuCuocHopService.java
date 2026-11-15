package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.repository.TaiLieuCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LuuTaiLieuCuocHopService {

    private static final long DUNG_LUONG_TOI_DA = 10L * 1024 * 1024;
    private static final Set<String> DINH_DANG_HO_TRO =
            Set.of("pdf", "doc", "docx", "xls", "xlsx");

    private final TaiLieuCuocHopRepository taiLieuCuocHopRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Integer luuTaiLieu(
            CuocHop cuocHop,
            NguoiDung nguoiTaiLen,
            MultipartFile file,
            String loaiTaiLieu,
            Path thuMuc) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Vui lòng chọn file");
        }
        if (file.getSize() > DUNG_LUONG_TOI_DA) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE, "File không được vượt quá 10 MB");
        }

        String tenGoc = layTenFileGoc(file);
        int viTriCham = tenGoc.lastIndexOf('.');
        String dinhDang = viTriCham >= 0
                ? tenGoc.substring(viTriCham + 1).toLowerCase(Locale.ROOT)
                : "";

        if (!DINH_DANG_HO_TRO.contains(dinhDang)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ PDF, Word và Excel");
        }

        Path duongDan = null;

        try {
            Files.createDirectories(thuMuc);
            Path fileMoi = Files.createTempFile(thuMuc, "tai-lieu-", "." + dinhDang);
            duongDan = fileMoi;

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCompletion(int status) {
                            if (status == STATUS_ROLLED_BACK) {
                                xoaFileVaGhiLog(fileMoi);
                            }
                        }
                    });

            try (InputStream input = file.getInputStream()) {
                Files.copy(input, fileMoi, StandardCopyOption.REPLACE_EXISTING);
            }

            TaiLieuCuocHop taiLieu = new TaiLieuCuocHop();
            taiLieu.setCuocHop(cuocHop);
            taiLieu.setNguoiTaiLen(nguoiTaiLen);
            taiLieu.setTenTaiLieu(tenGoc);
            taiLieu.setTenFileGoc(tenGoc);
            taiLieu.setLoaiTaiLieu(loaiTaiLieu);
            taiLieu.setDuongDanFile(fileMoi.toString());
            taiLieu.setDinhDangFile(dinhDang);
            taiLieu.setDungLuongByte(file.getSize());
            taiLieu.setTrangThaiXuLy("CHUA_XU_LY");

            return taiLieuCuocHopRepository.saveAndFlush(taiLieu).getTaiLieuId();
        } catch (IOException | RuntimeException ex) {
            if (duongDan != null) {
                xoaFileVaGhiLog(duongDan);
            }
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể lưu tài liệu cuộc họp",
                    ex);
        }
    }

    private String layTenFileGoc(MultipartFile file) {
        String tenGoc = file.getOriginalFilename();

        if (tenGoc == null || tenGoc.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tên file không hợp lệ");
        }

        tenGoc = tenGoc.replace('\\', '/');
        tenGoc = tenGoc.substring(tenGoc.lastIndexOf('/') + 1);

        if (tenGoc.isBlank() || tenGoc.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tên file phải có từ 1 đến 255 ký tự");
        }

        return tenGoc;
    }

    void xoaFileVaGhiLog(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            System.getLogger(LuuTaiLieuCuocHopService.class.getName())
                    .log(System.Logger.Level.ERROR,
                            "Không thể dọn file: " + file, ex);
        }
    }
}