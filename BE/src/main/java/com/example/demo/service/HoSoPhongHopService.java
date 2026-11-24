package com.example.demo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.response.CuocHopDangDienRaResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KetLuanCuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;
import com.example.demo.service.LuuTaiLieuCuocHopService.TepTaiLieu;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HoSoPhongHopService {
    private static final String LOAI_TAI_LIEU_CHIA_SE = "TAI_LIEU_TRONG_CUOC_HOP";
    private static final Path THU_MUC_CHIA_SE = Path.of("uploads", "tai-lieu-trong-cuoc-hop");

    private final ThanhVienCuocHopService thanhVienCuocHopService;
    private final CuocHopRepository cuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final TaiLieuCuocHopRepository taiLieuRepository;
    private final TaiLieuKeHoachService taiLieuKeHoachService;
    private final NghiQuyetService nghiQuyetService;
    private final BienBanCuocHopRepository bienBanRepository;
    private final KetLuanCuocHopRepository ketLuanRepository;
    private final TaiLieuBienBanService taiLieuBienBanService;
    private final TaiLieuKetLuanService taiLieuKetLuanService;
    private final LuuTaiLieuCuocHopService luuTaiLieuService;
    private final CapNhatHoSoRealtime realtime;

    public record TaiLieu(Integer taiLieuId, String ten, String dinhDang, Long dungLuongByte,
            String nguoiTaiLen, LocalDateTime ngayTaiLen, String duongDan, String ghiChu) {}

    public record VanBan(String tieuDe, String noiDung, String trangThai, boolean coTheXem, List<TaiLieu> taiLieu) {}

    public record HoSo(String noiDungDuKien, List<TaiLieu> keHoach, List<NghiQuyetService.Muc> nghiQuyet,
            List<TaiLieu> chiaSe, VanBan bienBan, VanBan ketLuan, boolean coTheTaiLen) {}

    @Transactional(readOnly = true)
    public HoSo xem(Integer cuocHopId, Integer nguoiDungId) {
        var phongHop = thanhVienCuocHopService.layPhongHop(cuocHopId, nguoiDungId);
        boolean dieuHanh = phongHop.laChuTri() || phongHop.laThuKyToChuc();
        List<TaiLieu> keHoach = taiLieuKeHoachService.taiLieuDaDuyetCuaPhongHop(cuocHopId).stream()
                .map(taiLieu -> taiLieu(cuocHopId, "ke-hoach", taiLieu)).toList();
        List<TaiLieu> chiaSe = taiLieuRepository
                .findByCuocHop_CuocHopIdAndLoaiTaiLieuOrderByTaiLieuIdAsc(cuocHopId, LOAI_TAI_LIEU_CHIA_SE)
                .stream().sorted(Comparator.comparing(TaiLieuCuocHop::getTaiLieuId).reversed())
                .map(taiLieu -> taiLieu(cuocHopId, "chia-se", taiLieu)).toList();
        return new HoSo(phongHop.noiDungDuKien(), keHoach, nghiQuyetService.xem(cuocHopId, nguoiDungId),
                chiaSe, xemBienBan(cuocHopId, nguoiDungId, dieuHanh), xemKetLuan(cuocHopId, nguoiDungId, dieuHanh),
                coTheTaiLen(phongHop));
    }

    @Transactional
    public Integer taiLen(Integer cuocHopId, Integer nguoiDungId, MultipartFile file, String ghiChu) {
        String moTa = ghiChu == null ? null : ghiChu.strip();
        if (moTa != null && moTa.length() > 500) {
            throw loi(HttpStatus.BAD_REQUEST, "Ghi chú tài liệu tối đa 500 ký tự");
        }
        var nguoiDung = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        var phongHop = thanhVienCuocHopService.layPhongHop(cuocHopId, nguoiDungId);
        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Chỉ tải tài liệu khi cuộc họp đang diễn ra");
        }
        if (!coTheTaiLen(phongHop)) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn cần có mặt và chưa rời cuộc họp để tải tài liệu");
        }
        kiemTraPdf(file);
        Integer taiLieuId = luuTaiLieuService.luuTaiLieu(cuocHop, nguoiDung, file, LOAI_TAI_LIEU_CHIA_SE, THU_MUC_CHIA_SE);
        if (moTa != null && !moTa.isBlank()) {
            taiLieuRepository.findById(taiLieuId)
                    .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy tài liệu vừa tải lên"))
                    .setMoTa(moTa);
        }
        realtime.baoCuocHop(cuocHop, nguoiDungId);
        return taiLieuId;
    }

    @Transactional(readOnly = true)
    public TepTaiLieu docTep(Integer cuocHopId, Integer nguoiDungId, String nhom, Integer taiLieuId) {
        var phongHop = thanhVienCuocHopService.layPhongHop(cuocHopId, nguoiDungId);
        boolean dieuHanh = phongHop.laChuTri() || phongHop.laThuKyToChuc();
        return switch (nhom) {
            case "ke-hoach" -> {
                var tep = taiLieuKeHoachService.docTepDaDuyetCuaPhongHop(cuocHopId, taiLieuId);
                yield new TepTaiLieu(tep.tenFile(), tep.noiDung());
            }
            case "chia-se" -> luuTaiLieuService.docTep(taiLieuRepository.findById(taiLieuId)
                    .filter(tep -> LOAI_TAI_LIEU_CHIA_SE.equals(tep.getLoaiTaiLieu())
                            && cuocHopId.equals(tep.getCuocHop().getCuocHopId()))
                    .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy tệp chia sẻ của cuộc họp")),
                    THU_MUC_CHIA_SE);
            case "bien-ban" -> {
                kiemTraDuocDoc(xemBienBan(cuocHopId, nguoiDungId, dieuHanh));
                yield taiLieuBienBanService.taiXuong(cuocHopId, taiLieuId, nguoiDungId);
            }
            case "ket-luan" -> {
                kiemTraDuocDoc(xemKetLuan(cuocHopId, nguoiDungId, dieuHanh));
                var tep = taiLieuKetLuanService.taiXuongTaiLieuKetLuan(cuocHopId, taiLieuId, nguoiDungId);
                yield new TepTaiLieu(tep.tenFile(), tep.noiDung());
            }
            default -> throw loi(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm tài liệu");
        };
    }

    private VanBan xemBienBan(Integer cuocHopId, Integer nguoiDungId, boolean dieuHanh) {
        return bienBanRepository.findByCuocHop_CuocHopId(cuocHopId).map(bienBan -> {
            if (!dieuHanh && !"DA_PHAT_HANH".equals(bienBan.getTrangThai())) {
                return new VanBan(null, null, bienBan.getTrangThai(), false, List.of());
            }
            var tep = taiLieuBienBanService.layDanhSach(cuocHopId, nguoiDungId).stream()
                    .map(muc -> taiLieuVanBan(cuocHopId, "bien-ban", muc.taiLieuId(),
                            muc.tenFileGoc(), muc.dinhDangFile(), muc.dungLuongByte()))
                    .toList();
            return new VanBan(bienBan.getTieuDe(), bienBan.getNoiDungChinhThuc(), bienBan.getTrangThai(), true, tep);
        }).orElse(null);
    }

    private VanBan xemKetLuan(Integer cuocHopId, Integer nguoiDungId, boolean dieuHanh) {
        return ketLuanRepository.findByCuocHop_CuocHopId(cuocHopId).map(ketLuan -> {
            if (!dieuHanh && !"DA_PHAT_HANH".equals(ketLuan.getTrangThai())) {
                return new VanBan(null, null, ketLuan.getTrangThai(), false, List.of());
            }
            var tep = taiLieuKetLuanService.layDanhSachTaiLieuKetLuan(cuocHopId, nguoiDungId).stream()
                    .map(muc -> taiLieuVanBan(cuocHopId, "ket-luan", muc.taiLieuId(),
                            muc.tenFileGoc(), muc.dinhDangFile(), muc.dungLuongByte()))
                    .toList();
            return new VanBan(ketLuan.getTieuDe(), ketLuan.getNoiDung(), ketLuan.getTrangThai(), true, tep);
        }).orElse(null);
    }

    private void kiemTraDuocDoc(VanBan vanBan) {
        if (vanBan == null || !vanBan.coTheXem()) {
            throw loi(HttpStatus.NOT_FOUND, "Chưa có văn bản được chia sẻ trong phòng họp");
        }
    }

    private boolean coTheTaiLen(CuocHopDangDienRaResponse phongHop) {
        return "DANG_DIEN_RA".equals(phongHop.trangThai())
                && (phongHop.laChuTri() || phongHop.laThuKyToChuc()
                || ("CO_MAT".equals(phongHop.trangThaiThamDu()) && phongHop.thoiGianRoiCuocHop() == null));
    }

    private TaiLieu taiLieu(Integer cuocHopId, String nhom, TaiLieuCuocHop muc) {
        return new TaiLieu(muc.getTaiLieuId(), muc.getTenTaiLieu(), muc.getDinhDangFile(), muc.getDungLuongByte(),
                muc.getNguoiTaiLen().getHoTen(), muc.getNgayTaiLen(), duongDan(cuocHopId, nhom, muc.getTaiLieuId()),
                LOAI_TAI_LIEU_CHIA_SE.equals(muc.getLoaiTaiLieu()) ? muc.getMoTa() : null);
    }

    private TaiLieu taiLieuVanBan(Integer cuocHopId, String nhom, Integer id, String ten, String dinhDang, Long byteTep) {
        return new TaiLieu(id, ten, dinhDang, byteTep, null, null, duongDan(cuocHopId, nhom, id), null);
    }

    private String duongDan(Integer cuocHopId, String nhom, Integer id) {
        return "/api/cuoc-hop/" + cuocHopId + "/ho-so-phong-hop/" + nhom + "/tep/" + id;
    }

    private void kiemTraPdf(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase(Locale.ROOT).endsWith(".pdf")) return;
        try (var stream = file.getInputStream()) {
            if (!"%PDF-".equals(new String(stream.readNBytes(5), StandardCharsets.US_ASCII))) {
                throw loi(HttpStatus.BAD_REQUEST, "Tệp xem trước phải là PDF hợp lệ");
            }
        } catch (IOException ex) {
            throw loi(HttpStatus.BAD_REQUEST, "Không thể đọc tệp PDF");
        }
    }

    private ResponseStatusException loi(HttpStatus status, String thongBao) {
        return new ResponseStatusException(status, thongBao);
    }
}
