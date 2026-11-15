package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaiLieuCuocHopService {

    private static final long DUNG_LUONG_TOI_DA = 10L * 1024 * 1024;

    private static final String LOAI_DE_XUAT = "DE_XUAT_CUOC_HOP";

    private static final Set<String> DINH_DANG_HO_TRO =
            Set.of("pdf", "doc", "docx", "xls", "xlsx");

    private final CuocHopRepository cuocHopRepository;
    private final TaiLieuCuocHopRepository taiLieuCuocHopRepository;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final LuuTaiLieuCuocHopService luuTaiLieuCuocHopService;
    
    @Value("${app.upload.de-xuat-dir:uploads/de-xuat-cuoc-hop}")
    private String thuMucUpload;

    // =====================================================
    // UPLOAD TÀI LIỆU ĐỀ XUẤT
    // =====================================================

    @Transactional
    public Integer taiLenTaiLieuDeXuat(
            Integer cuocHopId,
            Integer nguoiDangNhapId,
            MultipartFile file) {

        NguoiDung nguoiTaiLen = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiDangNhapId);

        phanQuyenCuocHopService.kiemTraQuyenTaoDeXuat(nguoiDangNhapId);

        CuocHop cuocHop = timCuocHopVaKhoa(cuocHopId);
        kiemTraNguoiDeXuat(cuocHop, nguoiDangNhapId);
        kiemTraConNhap(cuocHop);

        return luuTaiLieuCuocHopService.luuTaiLieu(
                cuocHop, nguoiTaiLen, file, LOAI_DE_XUAT, layThuMucUpload());
    }
    // =====================================================
    // DANH SÁCH TÀI LIỆU ĐỀ XUẤT
    // =====================================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layDanhSachDeXuat(
            Integer cuocHopId,
            Integer nguoiDungId) {

        kiemTraQuyenXemDeXuat(cuocHopId, nguoiDungId);

        return taiLieuCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .stream()
                .filter(tl -> LOAI_DE_XUAT.equals(
                        tl.getLoaiTaiLieu()))
                .map(tl -> {
                    Map<String, Object> ketQua = new LinkedHashMap<>();
                    ketQua.put("taiLieuId", tl.getTaiLieuId());
                    ketQua.put("tenTaiLieu", tl.getTenTaiLieu());
                    ketQua.put("tenFileGoc", tl.getTenFileGoc());
                    ketQua.put("dinhDangFile", tl.getDinhDangFile());
                    ketQua.put("dungLuongByte", tl.getDungLuongByte());
                    ketQua.put("ngayTaiLen", tl.getNgayTaiLen());
                    return ketQua;
                })
                .toList();
    }

    // =====================================================
    // TẢI XUỐNG
    // =====================================================

    public record FileDeXuat(
            byte[] noiDung,
            String tenFile) {
    }

    @Transactional(readOnly = true)
    public FileDeXuat taiXuongDeXuat(
            Integer cuocHopId,
            Integer taiLieuId,
            Integer nguoiDungId) {

        kiemTraQuyenXemDeXuat(cuocHopId, nguoiDungId);

        TaiLieuCuocHop taiLieu = timTaiLieuDeXuat(
                cuocHopId,
                taiLieuId
        );

        Path file = layDuongDanFile(taiLieu);

        try {
            if (!Files.isRegularFile(file)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy file"
                );
            }

            kiemTraFileTrongThuMuc(file);

            String tenFile = taiLieu.getTenFileGoc();

            if (tenFile == null || tenFile.isBlank()) {
                tenFile = file.getFileName().toString();
            }

            tenFile = tenFile.replace("\r", "").replace("\n", "");

            return new FileDeXuat(
                    Files.readAllBytes(file),
                    tenFile
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể đọc file tài liệu",
                    e
            );
        }
    }

    // =====================================================
    // XÓA TÀI LIỆU KHI CÒN NHÁP
    // =====================================================

    @Transactional
    public void xoaTaiLieuDeXuat(
            Integer cuocHopId,
            Integer taiLieuId,
            Integer nguoiDungId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        phanQuyenCuocHopService
                .kiemTraQuyenTaoDeXuat(nguoiDungId);

        CuocHop cuocHop = timCuocHopVaKhoa(cuocHopId);

        kiemTraNguoiDeXuat(cuocHop, nguoiDungId);
        kiemTraConNhap(cuocHop);

        TaiLieuCuocHop taiLieu = timTaiLieuDeXuat(
                cuocHopId,
                taiLieuId
        );

        Path fileCanXoa = null;

        if (taiLieu.getDuongDanFile() != null
                && !taiLieu.getDuongDanFile().isBlank()) {

            Path file = layDuongDanFile(taiLieu);

            try {
                if (Files.exists(file)) {
                    if (!Files.isRegularFile(file)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Đường dẫn tài liệu không hợp lệ"
                        );
                    }

                    kiemTraFileTrongThuMuc(file);
                    fileCanXoa = file;
                }
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Không thể kiểm tra file tài liệu",
                        e
                );
            }
        }

        taiLieuCuocHopRepository.delete(taiLieu);
        taiLieuCuocHopRepository.flush();

        if (fileCanXoa != null) {
            final Path fileDaChon = fileCanXoa;

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                        	luuTaiLieuCuocHopService.xoaFileVaGhiLog(fileDaChon);                        }
                    }
            );
        }
    }

    // =====================================================
    // KIỂM TRA QUYỀN XEM
    // =====================================================

    private void kiemTraQuyenXemDeXuat(
            Integer cuocHopId,
            Integer nguoiDungId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"
                ));

        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId)
                && !phanQuyenCuocHopService.coQuyenXemChiTietDeXuat(
                        cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem tài liệu đề xuất");
        }
    }

    // =====================================================
    // CÁC HÀM DÙNG CHUNG NỘI BỘ
    // =====================================================

    private CuocHop timCuocHopVaKhoa(Integer cuocHopId) {
        return cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"));
    }

    private void kiemTraNguoiDeXuat(
            CuocHop cuocHop,
            Integer nguoiDungId) {

    	if (!ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ người đề xuất được thay đổi tài liệu đề xuất"
            );
        }
    }

    private void kiemTraConNhap(CuocHop cuocHop) {
        if (!ThaoTacCuocHop.laDeXuatNhap(cuocHop)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ được thay đổi tài liệu đề xuất khi còn nháp"
            );
        }
    }

    private TaiLieuCuocHop timTaiLieuDeXuat(
            Integer cuocHopId,
            Integer taiLieuId) {

        TaiLieuCuocHop taiLieu = taiLieuCuocHopRepository
                .findById(taiLieuId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài liệu"
                ));

        if (!cuocHopId.equals(taiLieu.getCuocHop().getCuocHopId())
                || !LOAI_DE_XUAT.equals(taiLieu.getLoaiTaiLieu())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy tài liệu đề xuất trong cuộc họp này"
            );
        }

        return taiLieu;
    }

    private String layTenFileGoc(MultipartFile file) {
        String tenGoc = file.getOriginalFilename();

        if (tenGoc == null || tenGoc.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên file không hợp lệ"
            );
        }

        tenGoc = tenGoc.replace('\\', '/');
        tenGoc = tenGoc.substring(tenGoc.lastIndexOf('/') + 1);

        if (tenGoc.isBlank() || tenGoc.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên file phải có từ 1 đến 255 ký tự"
            );
        }

        return tenGoc;
    }

    private String layDinhDang(String tenFile) {
        int viTriCham = tenFile.lastIndexOf('.');

        return viTriCham >= 0
                ? tenFile.substring(viTriCham + 1)
                        .toLowerCase(Locale.ROOT)
                : "";
    }

    private Path layThuMucUpload() {
        return Path.of(thuMucUpload)
                .toAbsolutePath()
                .normalize();
    }

    private Path layDuongDanFile(TaiLieuCuocHop taiLieu) {
        if (taiLieu.getDuongDanFile() == null
                || taiLieu.getDuongDanFile().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tài liệu chưa có file đính kèm"
            );
        }

        Path file = Path.of(taiLieu.getDuongDanFile())
                .toAbsolutePath()
                .normalize();

        if (!file.startsWith(layThuMucUpload())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Đường dẫn tài liệu không hợp lệ"
            );
        }

        return file;
    }

    private void kiemTraFileTrongThuMuc(Path file)
            throws IOException {

        if (!file.toRealPath().startsWith(
                layThuMucUpload().toRealPath())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Đường dẫn tài liệu không hợp lệ"
            );
        }
    }

    private void xoaFileVaGhiLog(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            System.getLogger(TaiLieuCuocHopService.class.getName())
                    .log(
                            System.Logger.Level.ERROR,
                            "Không thể dọn file: " + file,
                            e
                    );
        }
    }
}