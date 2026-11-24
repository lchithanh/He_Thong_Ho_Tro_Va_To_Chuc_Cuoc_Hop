package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.response.TaiLieuKeHoachResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyetTaiLieu;
import com.example.demo.entity.YeuCauPheDuyetTaiLieuId;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;
import com.example.demo.repository.YeuCauPheDuyetTaiLieuRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaiLieuKeHoachService {
    private static final String LOAI_KE_HOACH = "KE_HOACH_CUOC_HOP";
    private static final long TOI_DA = 10L * 1024 * 1024;
    private static final int SO_TEP_TOI_DA_MOI_LAN_TRINH = 20;
    private static final int SO_BYTE_KIEM_TRA_PDF = 1024;
    private static final Pattern DAU_TEP_PDF = Pattern.compile(
            "(?:\\xEF\\xBB\\xBF)?[ \\t\\r\\n\\f]*%PDF-[0-9]\\.[0-9](?=[\\r\\n])");
    private static final Set<String> DINH_DANG = Set.of("pdf", "doc", "docx", "xls", "xlsx");
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final CuocHopRepository cuocHopRepository;
    private final TaiLieuCuocHopRepository taiLieuCuocHopRepository;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    private final YeuCauPheDuyetTaiLieuRepository yeuCauPheDuyetTaiLieuRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;

    @Value("${app.upload.ke-hoach-dir:uploads/ke-hoach-cuoc-hop}")
    private String thuMucUpload;

    public record TepTaiXuong(byte[] noiDung, String tenFile, String dinhDangFile) { }

    @Transactional
    public Integer taiLen(Integer cuocHopId, Integer nguoiId, MultipartFile file) {
        NguoiDung nguoi = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        CuocHop ch = timCuocHop(cuocHopId, true);
        kiemTraQuyenSua(ch, nguoiId);
        if (file == null || file.isEmpty()) throw loi(HttpStatus.BAD_REQUEST, "Vui lòng chọn tệp");
        if (file.getSize() > TOI_DA) throw loi(HttpStatus.PAYLOAD_TOO_LARGE, "Tệp không được vượt quá 10 MB");
        String ten = tenAnToan(file.getOriginalFilename());
        String duoi = ten.contains(".") ? ten.substring(ten.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
        if (!DINH_DANG.contains(duoi)) throw loi(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ PDF, Word và Excel");
        Path daTao = null;
        try {
            Path root = thuMuc();
            Files.createDirectories(root);
            // Tạo tên mới, không ghi đè tệp đã có.
            Path fileMoi = Files.createTempFile(root, UUID.randomUUID().toString(), "." + duoi);
            daTao = fileMoi;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) donTep(fileMoi);
                }
            });
            try (InputStream in = file.getInputStream()) {
                // Chỉ ghi đè tệp rỗng vừa được tạo riêng bởi thao tác này.
                Files.copy(in, fileMoi, StandardCopyOption.REPLACE_EXISTING);
            }
            if (Files.size(fileMoi) > TOI_DA) throw loi(HttpStatus.PAYLOAD_TOO_LARGE, "Tệp vượt quá 10 MB");
            kiemTraDinhDangThucTe(fileMoi, duoi);
            TaiLieuCuocHop tl = new TaiLieuCuocHop();
            tl.setCuocHop(ch); tl.setNguoiTaiLen(nguoi);
            tl.setTenTaiLieu(ten); tl.setTenFileGoc(ten); tl.setLoaiTaiLieu(LOAI_KE_HOACH);
            tl.setDuongDanFile(fileMoi.toString()); tl.setDinhDangFile(duoi);
            tl.setDungLuongByte(Files.size(fileMoi)); tl.setTrangThaiXuLy("CHUA_XU_LY");
            Integer taiLieuId = taiLieuCuocHopRepository.saveAndFlush(tl).getTaiLieuId();
            capNhatRealtime.baoCho(nguoiId);
            return taiLieuId;
        } catch (ResponseStatusException ex) {
            donTep(daTao); throw ex;
        } catch (IOException | RuntimeException ex) {
            donTep(daTao);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể lưu tài liệu kế hoạch", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<TaiLieuKeHoachResponse> layDanhSachSoanThao(Integer cuocHopId, Integer nguoiId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        CuocHop ch = timCuocHop(cuocHopId, false);
        kiemTraQuyenXemSoanThao(ch, nguoiId);
        Set<Integer> daTrinh = Set.copyOf(
                yeuCauPheDuyetTaiLieuRepository.timIdTaiLieuDaTrinh(cuocHopId)
        );
        return taiLieuCuocHopRepository.findByCuocHop_CuocHopId(cuocHopId).stream()
                .filter(tl -> LOAI_KE_HOACH.equals(tl.getLoaiTaiLieu()))
                .map(tl -> response(tl, daTrinh.contains(tl.getTaiLieuId()))).toList();
    }

    @Transactional(readOnly = true)
    public List<TaiLieuKeHoachResponse> layTheoLanTrinh(Integer yeuCauId, Integer nguoiId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        YeuCauPheDuyet yc = timYeuCau(yeuCauId);
        kiemTraQuyenXemLanTrinh(yc, nguoiId);
        return yeuCauPheDuyetTaiLieuRepository
                .timIdTaiLieuTheoLanTrinh(yeuCauId)
                .stream()
                .map(id -> timTaiLieu(yc.getCuocHop().getCuocHopId(), id))
                .map(tl -> response(tl, true))
                .toList();
    }

    // Hồ sơ phòng họp đã kiểm tra quyền tham dự trước khi đọc bản được duyệt.
    List<TaiLieuCuocHop> taiLieuDaDuyetCuaPhongHop(Integer cuocHopId) {
        return yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThaiOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP, YeuCauPheDuyet.TrangThai.DA_DUYET)
                .map(yeuCau -> layIdTepLanTrinh(yeuCau).stream()
                        .map(id -> timTaiLieu(cuocHopId, id))
                        .toList())
                .orElseGet(List::of);
    }

    TepTaiXuong docTepDaDuyetCuaPhongHop(Integer cuocHopId, Integer taiLieuId) {
        return taiLieuDaDuyetCuaPhongHop(cuocHopId).stream()
                .filter(tep -> taiLieuId.equals(tep.getTaiLieuId()))
                .findFirst()
                .map(this::docTep)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Tệp không thuộc kế hoạch đã duyệt của cuộc họp"));
    }

    @Transactional(readOnly = true)
    public TepTaiXuong taiXuongSoanThao(Integer cuocHopId, Integer taiLieuId, Integer nguoiId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        kiemTraQuyenXemSoanThao(timCuocHop(cuocHopId, false), nguoiId);
        return docTep(timTaiLieu(cuocHopId, taiLieuId));
    }

    @Transactional(readOnly = true)
    public TepTaiXuong taiXuongTheoLanTrinh(Integer yeuCauId, Integer taiLieuId, Integer nguoiId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        YeuCauPheDuyet yc = timYeuCau(yeuCauId);
        kiemTraQuyenXemLanTrinh(yc, nguoiId);
        if (taiLieuId == null || !yeuCauPheDuyetTaiLieuRepository.existsById(
                new YeuCauPheDuyetTaiLieuId(yeuCauId, taiLieuId))) {
            throw loi(HttpStatus.NOT_FOUND, "Tài liệu không thuộc lần trình này");
        }
        return docTep(timTaiLieu(yc.getCuocHop().getCuocHopId(), taiLieuId));
    }

    @Transactional
    public void xoa(Integer cuocHopId, Integer taiLieuId, Integer nguoiId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        CuocHop ch = timCuocHop(cuocHopId, true);
        kiemTraQuyenSua(ch, nguoiId);
        TaiLieuCuocHop tl = timTaiLieu(cuocHopId, taiLieuId);
        if (yeuCauPheDuyetTaiLieuRepository.existsById_TaiLieuId(taiLieuId)) {
            throw loi(
                    HttpStatus.CONFLICT,
                    "Tài liệu đã trình phải được giữ nguyên; hãy tải bản mới để chỉnh sửa"
            );
        }
        Path file = duongDanTrongThuMuc(tl, false);
        taiLieuCuocHopRepository.delete(tl);
        taiLieuCuocHopRepository.flush();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { donTep(file); }
        });
        capNhatRealtime.baoCho(nguoiId);
    }

    /** Caller QuyTrinhKeHoachService đã khóa cuộc họp; không mở transaction độc lập. */
    @Transactional(propagation = Propagation.MANDATORY)
    public List<TaiLieuCuocHop> kiemTraTruocKhiTrinh(CuocHop ch, Integer nguoiId, List<Integer> ids) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        kiemTraQuyenSua(ch, nguoiId);
        if (ids == null || ids.isEmpty()) return List.of();
        if (ids.size() > SO_TEP_TOI_DA_MOI_LAN_TRINH) {
            throw loi(HttpStatus.BAD_REQUEST, "Mỗi lần trình tối đa 20 tài liệu");
        }
        LinkedHashSet<Integer> duyNhat = new LinkedHashSet<>(ids);
        if (duyNhat.size() != ids.size() || duyNhat.contains(null)) {
            throw loi(HttpStatus.BAD_REQUEST, "Danh sách tài liệu không được trùng ID hoặc chứa null");
        }
        List<TaiLieuCuocHop> ketQua = new ArrayList<>();
        for (Integer id : duyNhat) {
            TaiLieuCuocHop tl = timTaiLieu(ch.getCuocHopId(), id);
            Path path = duongDanTrongThuMuc(tl, true);
            try {
                if (Files.size(path) > TOI_DA) throw loi(HttpStatus.PAYLOAD_TOO_LARGE, "Tài liệu đính kèm vượt quá 10 MB");
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể kiểm tra tài liệu", ex);
            }
            ketQua.add(tl);
        }
        return ketQua;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ganVaoLanTrinh(
            YeuCauPheDuyet yc,
            List<TaiLieuCuocHop> taiLieu) {

        Integer cuocHopId = yc.getCuocHop().getCuocHopId();
        List<YeuCauPheDuyetTaiLieu> danhSachLienKet = new ArrayList<>();

        for (TaiLieuCuocHop tl : taiLieu) {
            if (!cuocHopId.equals(tl.getCuocHop().getCuocHopId())) {
                throw loi(
                        HttpStatus.BAD_REQUEST,
                        "Tài liệu không thuộc cuộc họp được trình"
                );
            }

            YeuCauPheDuyetTaiLieu lienKet = new YeuCauPheDuyetTaiLieu();
            lienKet.setId(new YeuCauPheDuyetTaiLieuId(
                    yc.getYeuCauPheDuyetId(),
                    tl.getTaiLieuId()
            ));
            lienKet.setYeuCauPheDuyet(yc);
            lienKet.setTaiLieu(tl);
            danhSachLienKet.add(lienKet);
        }

        if (!danhSachLienKet.isEmpty()) {
            yeuCauPheDuyetTaiLieuRepository.saveAllAndFlush(danhSachLienKet);
        }
    }

    public List<Integer> layIdTepLanTrinh(YeuCauPheDuyet lanTrinh) {
        return yeuCauPheDuyetTaiLieuRepository.timIdTaiLieuTheoLanTrinh(lanTrinh.getYeuCauPheDuyetId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void saoChepTepLanTrinh(YeuCauPheDuyet lanTrinhCu, YeuCauPheDuyet lanTrinhMoi) {
        List<TaiLieuCuocHop> taiLieu = yeuCauPheDuyetTaiLieuRepository
                .timIdTaiLieuTheoLanTrinh(lanTrinhCu.getYeuCauPheDuyetId())
                .stream()
                .map(id -> timTaiLieu(lanTrinhCu.getCuocHop().getCuocHopId(), id))
                .toList();
        ganVaoLanTrinh(lanTrinhMoi, taiLieu);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void kiemTraTepCuaLanTrinh(YeuCauPheDuyet yc) {
        Integer cuocHopId = yc.getCuocHop().getCuocHopId();
        List<Integer> ids = yeuCauPheDuyetTaiLieuRepository
                .timIdTaiLieuTheoLanTrinh(yc.getYeuCauPheDuyetId());

        for (Integer id : ids) {
            duongDanTrongThuMuc(timTaiLieu(cuocHopId, id), true);
        }
    }

    private void kiemTraQuyenSua(CuocHop ch, Integer nguoiId) {
        if (!phanCongXuLyKeHoachService.laThuKyDangXuLy(ch.getCuocHopId(), nguoiId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký đang phụ trách được thay đổi tài liệu kế hoạch");
        }
        if (!ThaoTacCuocHop.dangLapKeHoach(ch) || ThaoTacCuocHop.daBatDau(ch) || ThaoTacCuocHop.daKetThuc(ch)) {
            throw loi(HttpStatus.CONFLICT, "Chỉ được thay đổi tài liệu khi đang lập kế hoạch");
        }
    }

    private void kiemTraQuyenXemSoanThao(CuocHop ch, Integer nguoiId) {
        if (!ThaoTacCuocHop.laNguoiDeXuat(ch, nguoiId) && !phanQuyenCuocHopService.coVaiTro(nguoiId, "ADMIN")
                && !phanCongXuLyKeHoachService.laThuKyThamGia(ch.getCuocHopId(), nguoiId)) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn không được xem kho tài liệu soạn thảo của cuộc họp này");
        }
    }

    private void kiemTraQuyenXemLanTrinh(YeuCauPheDuyet yc, Integer nguoiId) {
        boolean duocXem = phanCongXuLyKeHoachService.laThuKyDaNhanBanGiao(yc, nguoiId)
                || phanQuyenCuocHopService.coVaiTro(nguoiId, "ADMIN")
                || ThaoTacCuocHop.laNguoiDeXuat(yc.getCuocHop(), nguoiId)
                || (yc.getNguoiTrinh() != null && nguoiId.equals(yc.getNguoiTrinh().getNguoiDungId()))
                || (yc.getNguoiDuyet() != null && nguoiId.equals(yc.getNguoiDuyet().getNguoiDungId()))
                || (yc.getThuKyTiepNhan() != null && nguoiId.equals(yc.getThuKyTiepNhan().getNguoiDungId()));
        if (!duocXem) throw loi(HttpStatus.FORBIDDEN, "Bạn không có quyền xem tài liệu của lần trình này");
    }

    private CuocHop timCuocHop(Integer id, boolean khoa) {
        if (id == null) {
            throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
        }

        return (khoa ? cuocHopRepository.findByIdForUpdate(id) : cuocHopRepository.findById(id))
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
    }

    private YeuCauPheDuyet timYeuCau(Integer id) {
        if (id == null) {
            throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã yêu cầu");
        }

        return yeuCauPheDuyetRepository.findById(id)
                .filter(yc -> yc.getLoaiYeuCau() == LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu duyệt kế hoạch"));
    }

    private TaiLieuCuocHop timTaiLieu(Integer cuocHopId, Integer id) {
        if (id == null || id <= 0) throw loi(HttpStatus.BAD_REQUEST, "ID tài liệu không hợp lệ");
        TaiLieuCuocHop tl = taiLieuCuocHopRepository.findById(id)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy tài liệu"));
        if (!cuocHopId.equals(tl.getCuocHop().getCuocHopId()) || !LOAI_KE_HOACH.equals(tl.getLoaiTaiLieu())) {
            throw loi(HttpStatus.NOT_FOUND, "Không tìm thấy tài liệu kế hoạch thuộc cuộc họp này");
        }
        return tl;
    }
    private TaiLieuKeHoachResponse response(TaiLieuCuocHop tl, boolean daTrinh) {
        return new TaiLieuKeHoachResponse(tl.getTaiLieuId(), tl.getTenTaiLieu(), tl.getTenFileGoc(),
                tl.getDinhDangFile(), tl.getDungLuongByte(), tl.getNgayTaiLen(), daTrinh);
    }
    private Path thuMuc() { return Path.of(thuMucUpload).toAbsolutePath().normalize(); }
    private Path duongDanTrongThuMuc(TaiLieuCuocHop tl, boolean batBuocTonTai) {
        if (tl.getDuongDanFile() == null || tl.getDuongDanFile().isBlank()) {
            throw loi(HttpStatus.NOT_FOUND, "Tài liệu chưa có tệp");
        }
        Path path = Path.of(tl.getDuongDanFile()).toAbsolutePath().normalize();
        if (!path.startsWith(thuMuc())) throw loi(HttpStatus.CONFLICT, "Đường dẫn tài liệu không hợp lệ");
        try {
            if (!Files.exists(path)) {
                if (batBuocTonTai) throw loi(HttpStatus.NOT_FOUND, "Tệp tài liệu không còn trên máy chủ");
                return path;
            }
            if (!Files.isRegularFile(path) || !path.toRealPath().startsWith(thuMuc().toRealPath())) {
                throw loi(HttpStatus.CONFLICT, "Đường dẫn tài liệu không hợp lệ");
            }
            return path;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể kiểm tra đường dẫn tệp", ex);
        }
    }
    private TepTaiXuong docTep(TaiLieuCuocHop tl) {
        Path path = duongDanTrongThuMuc(tl, true);
        try {
            if (Files.size(path) > TOI_DA) throw loi(HttpStatus.PAYLOAD_TOO_LARGE, "Tệp vượt quá 10 MB");
            return new TepTaiXuong(Files.readAllBytes(path), tenAnToan(tl.getTenFileGoc()), tl.getDinhDangFile());
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể đọc tài liệu", ex);
        }
    }
    private String tenAnToan(String ten) {
        if (ten == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu tên tệp");
        ten = ten.replace('\\', '/');
        ten = ten.substring(ten.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "").trim();
        if (ten.isBlank() || ten.length() > 255) throw loi(HttpStatus.BAD_REQUEST, "Tên tệp phải dài từ 1 đến 255 ký tự");
        return ten;
    }
    private void kiemTraDinhDangThucTe(Path path, String duoi) throws IOException {
        if ("pdf".equals(duoi)) {
            if (!coDauTepPdf(path)) {
                throw loi(HttpStatus.BAD_REQUEST,
                        "Không nhận diện được đầu tệp PDF. Hãy mở tài liệu gốc và xuất lại dưới dạng PDF.");
            }
        } else if ("doc".equals(duoi) || "xls".equals(duoi)) {
            byte[] dau;
            try (InputStream in = Files.newInputStream(path)) {
                dau = in.readNBytes(8);
            }
            boolean ole = java.util.Arrays.equals(dau, new byte[]{
                    (byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1});
            if (!ole) throw loi(HttpStatus.BAD_REQUEST, "Tệp DOC/XLS phải dùng định dạng Office nhị phân");
        } else {
            try (ZipFile zip = new ZipFile(path.toFile())) {
                String entry = "docx".equals(duoi) ? "word/document.xml" : "xl/workbook.xml";
                if (zip.getEntry("[Content_Types].xml") == null || zip.getEntry(entry) == null) {
                    throw loi(HttpStatus.BAD_REQUEST, "Nội dung không khớp định dạng Word/Excel được chọn");
                }
            } catch (java.util.zip.ZipException ex) {
                throw loi(HttpStatus.BAD_REQUEST, "Tệp Word/Excel không hợp lệ");
            }
        }
    }
    private static boolean coDauTepPdf(Path path) throws IOException {
        // Một số tệp có xuống dòng hoặc BOM trước dòng khai báo phiên bản PDF.
        try (InputStream in = Files.newInputStream(path)) {
            String dauTep = new String(in.readNBytes(SO_BYTE_KIEM_TRA_PDF), StandardCharsets.ISO_8859_1);
            return DAU_TEP_PDF.matcher(dauTep).lookingAt();
        }
    }
    private void donTep(Path path) {
        if (path == null) return;
        try { Files.deleteIfExists(path); }
        catch (IOException ex) { System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Không thể dọn tệp " + path, ex); }
    }
    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}



