package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.LuuBienBanRequest;
import com.example.demo.dto.response.BienBanDaPhatHanhResponse;
import com.example.demo.dto.response.BienBanDaPhatHanhResponse.ThongTinCuocHop;
import com.example.demo.dto.response.TaiLieuBienBanResponse;
import com.example.demo.dto.response.BienBanResponse;
import com.example.demo.dto.response.PhienBanBienBanResponse;
import com.example.demo.entity.BienBanCuocHop;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhienBanBienBan;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.PhienBanBienBanRepository;
import com.example.demo.repository.PhienBanBienBanRepository.SoPhienBanMoiNhat;
import com.example.demo.repository.TaiLieuCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BienBanService {

    static final String LOAI_TAI_LIEU_BIEN_BAN = "BIEN_BAN_CUOC_HOP";

    private final CapNhatHoSoRealtime capNhatRealtime;
    private final BienBanCuocHopRepository bienBanRepository;
    private final TaiLieuCuocHopRepository taiLieuCuocHopRepository;
    private final PhienBanBienBanRepository phienBanRepository;

    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final QuyenBienBanService quyenBienBanService;

    private final CuocHopRepository cuocHopRepository;
    private final ThongBaoService thongBaoService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;

    @Transactional
    public BienBanResponse taoBienBan(
            Integer cuocHopId,
            LuuBienBanRequest request,
            Integer nguoiDungId) {

        NguoiDung nguoiSoan =
                nguoiDungHienTaiService.layTaiKhoanHoatDong(
                        nguoiDungId);

        kiemTraDuLieu(request);

        kiemTraMaHopLe(cuocHopId, "cuộc họp");

        // Khóa cuộc họp để hai yêu cầu không tạo hai biên bản.
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"));

        quyenBienBanService.kiemTraQuyenSoan(cuocHop, nguoiDungId);

        if (!"DA_KET_THUC".equals(cuocHop.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ tạo biên bản sau khi cuộc họp đã kết thúc");
        }

        if (bienBanRepository.existsByCuocHop_CuocHopId(
                cuocHopId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cuộc họp đã có biên bản");
        }

        BienBanCuocHop bienBan = ThaoTacBienBan.thucHien(() ->
                ThaoTacBienBan.taoNhap(cuocHop, nguoiSoan,
                        request.getTieuDe().trim(), request.getNoiDungGoc(),
                        request.getNoiDungChinhThuc(), LocalDateTime.now()));

        bienBanRepository.saveAndFlush(bienBan);

        luuPhienBan(
                bienBan,
                nguoiSoan,
                request.getGhiChuThayDoi(),
                "Tạo bản nháp biên bản");

        capNhatRealtime.baoCho(nguoiDungId);
        return chuyenResponse(bienBan);
    }

    @Transactional
    public BienBanResponse capNhatBienBan(
            Integer bienBanId,
            LuuBienBanRequest request,
            Integer nguoiDungId) {

        NguoiDung nguoiCapNhat =
                nguoiDungHienTaiService.layTaiKhoanHoatDong(
                        nguoiDungId);

        kiemTraDuLieu(request);

        kiemTraMaHopLe(bienBanId, "biên bản");
        BienBanCuocHop bienBan = bienBanRepository
                .findByIdForUpdate(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biên bản"));

        quyenBienBanService.kiemTraQuyenSoan(bienBan.getCuocHop(), nguoiDungId);
        ThaoTacBienBan.thucHien(() -> ThaoTacBienBan.capNhatNoiDung(bienBan,
                request.getTieuDe().trim(), request.getNoiDungGoc(),
                request.getNoiDungChinhThuc()));

        // Giữ nguyên trạng thái và ý kiến phản hồi khi chỉnh sửa.
        // Chuyển trạng thái sẽ được xử lý ở chức năng trình duyệt.
        luuPhienBan(
                bienBan,
                nguoiCapNhat,
                request.getGhiChuThayDoi(),
                "Cập nhật nội dung biên bản");

        capNhatRealtime.baoCho(nguoiDungId);
        return chuyenResponse(bienBan);
    }

    @Transactional(readOnly = true)
    public BienBanResponse layBienBanTheoCuocHop(
            Integer cuocHopId,
            Integer nguoiDungId) {

        return chuyenResponse(layBienBanDuocXem(cuocHopId, nguoiDungId));
    }

    @Transactional(readOnly = true)
    public List<BienBanResponse> layBienBanChoPhatHanh(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        return bienBanRepository.findByTrangThaiOrderByBienBanIdDesc("DA_PHE_DUYET").stream()
                .filter(bienBan -> ThaoTacCuocHop.laNguoiChuTri(bienBan.getCuocHop(), nguoiDungId))
                .map(this::chuyenResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BienBanDaPhatHanhResponse> layBienBanDaPhatHanhCuaToi(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        var bienBanDaPhatHanh = bienBanRepository.timBienBanDaPhatHanhDuocXem(
                nguoiDungId, LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.DA_DUYET);
        if (bienBanDaPhatHanh.isEmpty()) {
            return List.of();
        }

        var cuocHopIds = bienBanDaPhatHanh.stream()
                .map(bienBan -> bienBan.getCuocHop().getCuocHopId()).toList();
        var taiLieuTheoCuocHop = taiLieuCuocHopRepository
                .findByCuocHop_CuocHopIdInAndLoaiTaiLieuOrderByTaiLieuIdAsc(cuocHopIds, LOAI_TAI_LIEU_BIEN_BAN)
                .stream()
                .collect(Collectors.groupingBy(tep -> tep.getCuocHop().getCuocHopId(),
                        Collectors.mapping(TaiLieuBienBanResponse::tuTaiLieu, Collectors.toList())));

        var bienBanIds = bienBanDaPhatHanh.stream().map(BienBanCuocHop::getBienBanId).toList();
        var phienBanTheoBienBan = phienBanRepository.timSoPhienBanMoiNhat(bienBanIds).stream()
                .collect(Collectors.toMap(SoPhienBanMoiNhat::getBienBanId, SoPhienBanMoiNhat::getSoPhienBan));

        return bienBanDaPhatHanh.stream().map(bienBan -> {
            var cuocHop = bienBan.getCuocHop();
            var chuTri = cuocHop.getNguoiChuTri();
            var thongTin = new ThongTinCuocHop(
                    cuocHop.getMaCuocHop(), chuTri == null ? null : chuTri.getHoTen(),
                    cuocHop.getLoaiHinh(), cuocHop.getPhamViCuocHop(),
                    cuocHop.getThoiGianBatDauThucTe(), cuocHop.getThoiGianKetThucThucTe());
            return new BienBanDaPhatHanhResponse(
                    bienBan.getBienBanId(), cuocHop.getCuocHopId(), cuocHop.getTieuDe(),
                    bienBan.getTieuDe(), bienBan.getNoiDungChinhThuc(), bienBan.getTrangThai(),
                    bienBan.getNgayDuyet(), phienBanTheoBienBan.get(bienBan.getBienBanId()), thongTin,
                    taiLieuTheoCuocHop.getOrDefault(cuocHop.getCuocHopId(), List.of()));
        }).toList();
    }

    BienBanCuocHop layBienBanDuocXem(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        kiemTraMaHopLe(cuocHopId, "cuộc họp");
        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        BienBanCuocHop bienBan = bienBanRepository.findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuộc họp chưa có biên bản"));
        boolean duocDocBanPhatHanh = "DA_PHAT_HANH".equals(bienBan.getTrangThai())
                && cuocHopRepository.coQuyenDocVanBanDaPhatHanh(
                        cuocHopId, nguoiDungId, LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.DA_DUYET);
        if (!duocDocBanPhatHanh
                && !phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, nguoiDungId)) {
            quyenBienBanService.kiemTraQuyenXem(cuocHop, nguoiDungId);
        }
        return bienBan;
    }

    @Transactional
    public BienBanResponse phatHanhBienBan(
            Integer cuocHopId,
            Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ chủ trì cuộc họp được phát hành biên bản");
        }
        BienBanCuocHop bienBan = bienBanRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có biên bản"));
        if (!"DA_PHE_DUYET".equals(bienBan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Biên bản chỉ được phát hành sau khi đã duyệt cuối");
        }
        bienBan.setTrangThai("DA_PHAT_HANH");
        thongBaoService.gui(cuocHop, nguoiDungId, "BIEN_BAN_DA_PHAT_HANH",
                "Biên bản cuộc họp đã được phát hành",
                bienBan.getTieuDe(), capNhatRealtime.nguoiDocVanBan(cuocHop));
        return chuyenResponse(bienBan);
    }

    @Transactional(readOnly = true)
    public List<PhienBanBienBanResponse> layLichSuPhienBan(
            Integer bienBanId,
            Integer nguoiDungId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(
                nguoiDungId);

        kiemTraMaHopLe(bienBanId, "biên bản");
        BienBanCuocHop bienBan = bienBanRepository
                .findById(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biên bản"));

        quyenBienBanService.kiemTraQuyenXem(
                bienBan.getCuocHop(),
                nguoiDungId);

        return phienBanRepository
                .findByBienBan_BienBanIdOrderBySoPhienBanDesc(
                        bienBanId)
                .stream()
                .map(pb -> new PhienBanBienBanResponse(
                        pb.getPhienBanId(),
                        pb.getBienBan().getBienBanId(),
                        pb.getSoPhienBan(),
                        pb.getNguoiCapNhat().getNguoiDungId(),
                        pb.getNoiDung(),
                        pb.getGhiChuThayDoi(),
                        pb.getNgayTao()))
                .toList();
    }

    private void kiemTraDuLieu(LuuBienBanRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Dữ liệu biên bản không được để trống");
        }
        kiemTraNoiDung(request.getTieuDe(), request.getNoiDungChinhThuc());
    }

    // Dùng chung cho tạo, cập nhật, API phiên bản và bước trình duyệt.
    void kiemTraNoiDung(String tieuDe, String noiDung) {
        if (tieuDe == null || tieuDe.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tiêu đề biên bản không được để trống");
        }
        if (tieuDe.trim().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tiêu đề biên bản không được quá 255 ký tự");
        }
        if (noiDung == null || noiDung.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nội dung biên bản không được để trống");
        }
    }

    private void kiemTraMaHopLe(Integer id, String doiTuong) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mã " + doiTuong + " không hợp lệ");
        }
    }

    /** API phiên bản cũ cũng đi qua quyền, khóa và cập nhật nội dung thống nhất. */
    @Transactional
    public PhienBanBienBan taoPhienBan(
            Integer bienBanId, Integer nguoiDungId,
            String noiDung, String ghiChu) {
        NguoiDung nguoiCapNhat = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        kiemTraMaHopLe(bienBanId, "biên bản");
        BienBanCuocHop bienBan = bienBanRepository.findByIdForUpdate(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy biên bản"));
        quyenBienBanService.kiemTraQuyenSoan(bienBan.getCuocHop(), nguoiDungId);
        kiemTraNoiDung(bienBan.getTieuDe(), noiDung);
        ThaoTacBienBan.thucHien(() -> ThaoTacBienBan.capNhatNoiDung(bienBan,
                bienBan.getTieuDe(), bienBan.getNoiDungGoc(), noiDung));
        var phienBan = luuPhienBan(bienBan, nguoiCapNhat, ghiChu, "Cập nhật nội dung biên bản");
        capNhatRealtime.baoCho(nguoiDungId);
        return phienBan;
    }

    private PhienBanBienBan luuPhienBan(
            BienBanCuocHop bienBan,
            NguoiDung nguoiCapNhat,
            String ghiChu,
            String ghiChuMacDinh) {

        if (!ThaoTacBienBan.coTheChinhSua(bienBan)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ tạo phiên bản khi biên bản được phép chỉnh sửa");
        }
        kiemTraNoiDung(bienBan.getTieuDe(), bienBan.getNoiDungChinhThuc());
        Integer soPhienBanMoi = phienBanRepository
                .timSoPhienBanLonNhat(bienBan.getBienBanId()) + 1;

        if (soPhienBanMoi < 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Số phiên bản không hợp lệ");
        }
        String ghiChuDaXuLy = ghiChu == null || ghiChu.isBlank()
                ? ghiChuMacDinh : ghiChu.trim();
        PhienBanBienBan phienBan = ThaoTacBienBan.thucHien(() ->
                taoBanChupPhienBan(bienBan, nguoiCapNhat, soPhienBanMoi,
                        ghiChuDaXuLy));
        return phienBanRepository.save(phienBan);
    }

    private BienBanResponse chuyenResponse(
            BienBanCuocHop bienBan) {

        NguoiDung nguoiDuyet = "CHO_DUYET".equals(bienBan.getTrangThai())
                ? bienBan.getCuocHop().getNguoiChuTri() : bienBan.getNguoiDuyet();
        return new BienBanResponse(
                bienBan.getBienBanId(),
                bienBan.getCuocHop().getCuocHopId(),
                bienBan.getNguoiSoan().getNguoiDungId(),
                nguoiDuyet != null
                        ? nguoiDuyet.getNguoiDungId()
                        : null,
                nguoiDuyet != null
                        ? nguoiDuyet.getHoTen()
                        : null,
                bienBan.getTieuDe(),
                bienBan.getNoiDungGoc(),
                bienBan.getNoiDungChinhThuc(),
                bienBan.getTrangThai(),
                bienBan.getYKienPhanHoi(),
                bienBan.getNgaySoan(),
                bienBan.getNgayTrinhDuyet(),
                bienBan.getNgayDuyet(),
                bienBan.getCuocHop().getTieuDe(),
                bienBan.getNguoiSoan().getHoTen());
    }

    private static PhienBanBienBan taoBanChupPhienBan(
            BienBanCuocHop bienBan, NguoiDung nguoiCapNhat,
            Integer soPhienBan, String ghiChuThayDoi) {
        PhienBanBienBan phienBan = new PhienBanBienBan();
        phienBan.setBienBan(bienBan);
        phienBan.setNguoiCapNhat(nguoiCapNhat);
        phienBan.setSoPhienBan(soPhienBan);
        phienBan.setNoiDung(bienBan.getNoiDungChinhThuc());
        phienBan.setGhiChuThayDoi(ghiChuThayDoi);
        return phienBan;
    }
}
