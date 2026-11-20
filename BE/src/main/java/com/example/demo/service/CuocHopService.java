package com.example.demo.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import com.example.demo.dto.response.NguoiCoTheMoiResponse;
import com.example.demo.repository.KhachMoiCuocHopRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.request.CapNhatKeHoachCuocHopRequest;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CuocHopService {
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final CuocHopRepository cuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final KhachMoiCuocHopRepository khachMoiCuocHopRepository;

    public record LuaChonKeHoach(
            List<NguoiCoTheMoiResponse> nguoiChuTri,
            List<NguoiCoTheMoiResponse> lanhDaoDuyet,
            boolean canChonLanhDao,
            List<NguoiCoTheMoiResponse> thuKyCuocHop) {}

    @Transactional(readOnly = true)
    public LuaChonKeHoach layLuaChonKeHoach(Integer cuocHopId, Integer nguoiDungId) {
        phanQuyenCuocHopService.kiemTraLaThuKy(cuocHopId, nguoiDungId);
        CuocHop ch = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        boolean doLanhDao = phanQuyenCuocHopService.coVaiTro(ch.getNguoiDeXuat().getNguoiDungId(), "LANH_DAO");
        boolean noiBo = ThaoTacCuocHop.laCuocHopNoiBo(ch);
        boolean canChonLanhDao = !doLanhDao && (!noiBo || Boolean.TRUE.equals(ch.getCoKhachMoiBenNgoai())
                || khachMoiCuocHopRepository.existsByCuocHop_CuocHopIdAndTrangThaiMoiNot(cuocHopId, "DA_HUY"));
        List<NguoiDung> hoatDong = nguoiDungRepository.findAll().stream()
                .filter(NguoiDungHienTaiService::dangHoatDong)
                .sorted(Comparator.comparing(NguoiDung::getHoTen))
                .toList();
        List<NguoiCoTheMoiResponse> chuTri = hoatDong.stream()
                .filter(nd -> !noiBo || doLanhDao || (ch.getPhongBanChuTri() != null
                        && nd.getNguoiDungId().equals(ch.getPhongBanChuTri().getTruongPhongId())))
                .map(this::chuyenLuaChonNguoiDung)
                .toList();
        List<NguoiCoTheMoiResponse> lanhDao = hoatDong.stream()
                .filter(nd -> phanQuyenCuocHopService.coVaiTro(nd.getNguoiDungId(), "LANH_DAO"))
                .map(this::chuyenLuaChonNguoiDung)
                .toList();
        return new LuaChonKeHoach(chuTri, lanhDao, canChonLanhDao, hoatDong.stream()
                .filter(phanCongXuLyKeHoachService::laNguoiCoTheLamThuKy)
                .filter(nd -> !ThaoTacCuocHop.laNguoiDeXuat(ch, nd.getNguoiDungId()))
                .map(this::chuyenLuaChonNguoiDung).toList());
    }

    private NguoiCoTheMoiResponse chuyenLuaChonNguoiDung(NguoiDung nguoiDung) {
        return new NguoiCoTheMoiResponse(nguoiDung.getNguoiDungId(), nguoiDung.getHoTen(),
                nguoiDung.getPhongBan() == null ? null : nguoiDung.getPhongBan().getTenPhongBan(), nguoiDung.getChucVu());
    }

    public void capNhatKeHoach(Integer cuocHopId, CapNhatKeHoachCuocHopRequest request, Integer nguoiDangNhapId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);
        if (cuocHopId == null || request == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu thông tin kế hoạch");
        CuocHop ch = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!phanQuyenCuocHopService.laThuKyDuocPhanCong(cuocHopId, nguoiDangNhapId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký đang phụ trách được cập nhật kế hoạch");
        }
        boolean moLaiKeHoach = ThaoTacCuocHop.coTheLapLich(ch);
        if (!ThaoTacCuocHop.dangLapKeHoach(ch) && !moLaiKeHoach) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp không còn ở bước lập kế hoạch");
        }
        if (moLaiKeHoach) {
            ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.moLaiDeCapNhatKeHoach(ch));
        }
        if (request.getTieuDe() == null || request.getTieuDe().isBlank()
                || request.getTieuDe().trim().length() > 255) {
            throw loi(HttpStatus.BAD_REQUEST, "Tiêu đề phải có từ 1 đến 255 ký tự");
        }
        if (request.getLoaiHinh() == null || !Set.of("TRUC_TIEP", "TRUC_TUYEN").contains(request.getLoaiHinh())) {
            throw loi(HttpStatus.BAD_REQUEST, "Loại hình cuộc họp không hợp lệ");
        }
        if (ch.getPhamViCuocHop() == null || !Set.of(
                "NOI_BO", "LIEN_PHONG_BAN", "TOAN_CO_QUAN", "BEN_NGOAI")
                .contains(ch.getPhamViCuocHop())) {
            throw loi(HttpStatus.BAD_REQUEST, "Phạm vi cuộc họp không hợp lệ");
        }
        NguoiDung nguoiChuTri = null;
        if (request.getNguoiChuTriId() != null) {
            nguoiChuTri = nguoiDungRepository.findById(request.getNguoiChuTriId())
                    .orElseThrow(() -> loi(HttpStatus.BAD_REQUEST, "Không tìm thấy người chủ trì"));
            if (!NguoiDungHienTaiService.dangHoatDong(nguoiChuTri)) throw loi(HttpStatus.BAD_REQUEST, "Người chủ trì không hoạt động");
            boolean doLanhDao = phanQuyenCuocHopService.coVaiTro(
                    ch.getNguoiDeXuat().getNguoiDungId(), "LANH_DAO");
            if (ThaoTacCuocHop.laCuocHopNoiBo(ch) && !doLanhDao
                    && (ch.getPhongBanChuTri() == null
                    || !phanQuyenCuocHopService.laTruongPhongCuaPhongBan(
                            request.getNguoiChuTriId(), ch.getPhongBanChuTri().getPhongBanId()))) {
                throw loi(HttpStatus.BAD_REQUEST, "Người chủ trì phải là trưởng phòng của phòng tổ chức");
            }
        }
        NguoiDung chuTriDaChon = nguoiChuTri;
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.capNhatKeHoach(ch,
                request.getTieuDe().trim(), request.getMucTieu(), request.getNoiDungDuKien(),
                request.getLoaiHinh(), chuTriDaChon, request.isCoKhachMoiBenNgoai()));
        ch.setThuKyCuocHop(phanCongXuLyKeHoachService.chonThuKyCuocHop(ch, request.getThuKyCuocHopId()));
        capNhatRealtime.baoNguoiPhuTrach(ch, nguoiDangNhapId);
    }
    
    public void batDauCuocHop(
            Integer cuocHopId,
            Integer nguoiDangNhapId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(
                nguoiDangNhapId);

        CuocHop cuocHop = timCuocHopVaKhoa(cuocHopId);

        kiemTraQuyenDieuHanhCuocHop(
                cuocHop,
                nguoiDangNhapId);

        if (!ThaoTacCuocHop.coTheLapLich(cuocHop)) {
            throw loi(
                    HttpStatus.CONFLICT,
                    "Cuộc họp phải được duyệt, đang ở bước tổ chức và chưa bắt đầu");
        }

        if (!ThaoTacCuocHop.coLichDaXacNhan(cuocHop)) {
            throw loi(
                    HttpStatus.CONFLICT,
                    "Phải có lịch đã xác nhận trước khi bắt đầu cuộc họp");
        }

        try {
            ThaoTacCuocHop.batDauCuocHop(cuocHop);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage(),
                    ex);
        }
        capNhatRealtime.baoCuocHop(cuocHop, nguoiDangNhapId);
    }

    public void ketThucCuocHop(
            Integer cuocHopId,
            Integer nguoiDangNhapId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(
                nguoiDangNhapId);

        CuocHop cuocHop = timCuocHopVaKhoa(cuocHopId);

        kiemTraQuyenDieuHanhCuocHop(
                cuocHop,
                nguoiDangNhapId);

        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(
                    HttpStatus.CONFLICT,
                    "Chỉ được kết thúc cuộc họp đang diễn ra");
        }

        try {
            ThaoTacCuocHop.ketThucCuocHop(cuocHop);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ex.getMessage(),
                    ex);
        }
        capNhatRealtime.baoCuocHop(cuocHop, nguoiDangNhapId);
    }

    private CuocHop timCuocHopVaKhoa(Integer cuocHopId) {

        if (cuocHopId == null || cuocHopId <= 0) {
            throw loi(
                    HttpStatus.BAD_REQUEST,
                    "Mã cuộc họp không hợp lệ");
        }

        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"));

        return cuocHop;
    }

    private void kiemTraQuyenDieuHanhCuocHop(
            CuocHop cuocHop,
            Integer nguoiDungId) {

        boolean laChuTri =
                ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId);

        boolean laThuKyPhuTrach =
                phanQuyenCuocHopService.laThuKyDuocPhanCong(
                        cuocHop.getCuocHopId(),
                        nguoiDungId);

        if (!laChuTri && !laThuKyPhuTrach) {
            throw loi(
                    HttpStatus.FORBIDDEN,
                    "Chỉ chủ trì hoặc thư ký đang phụ trách được bắt đầu/kết thúc cuộc họp");
        }
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }

}



