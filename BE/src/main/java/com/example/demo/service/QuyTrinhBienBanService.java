package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.response.BienBanChoDuyetResponse;
import com.example.demo.entity.BienBanCuocHop;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhienBanBienBan;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.PhienBanBienBanRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuyTrinhBienBanService {

    private final BienBanCuocHopRepository bienBanRepository;

    private final PhienBanBienBanRepository phienBanRepository;

    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;

    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    private final QuyenBienBanService quyenBienBanService;
    private final BienBanService bienBanService;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final ThongBaoService thongBaoService;

    private final LichSuPheDuyetService lichSuPheDuyetService;

    @Transactional
    public Integer trinhBienBan(
            Integer bienBanId,
            Integer nguoiTrinhId,
            String ghiChuTrinh) {

        NguoiDung nguoiTrinh =
                nguoiDungHienTaiService.layTaiKhoanHoatDong(
                        nguoiTrinhId);

        if (bienBanId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thiếu mã biên bản");
        }

        // Dùng cùng khóa với chức năng cập nhật biên bản.
        BienBanCuocHop bienBan = bienBanRepository
                .findByIdForUpdate(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biên bản"));

        CuocHop cuocHop = bienBan.getCuocHop();
        Integer cuocHopId = cuocHop.getCuocHopId();

        quyenBienBanService.kiemTraQuyenSoan(cuocHop, nguoiTrinhId);

        if (!"DA_KET_THUC".equals(cuocHop.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ trình biên bản sau khi cuộc họp đã kết thúc");
        }

        if (cuocHop.getNguoiChuTri() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cuộc họp chưa có người chủ trì để duyệt biên bản");
        }

        Integer chuTriId = cuocHop.getNguoiChuTri()
                .getNguoiDungId();

        NguoiDung nguoiDuyet =
                nguoiDungHienTaiService.layTaiKhoanHoatDong(
                        chuTriId);

        if (Objects.equals(nguoiTrinhId, chuTriId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Người trình và người duyệt biên bản phải khác nhau");
        }
        boolean daCoYeuCauChoDuyet = yeuCauPheDuyetRepository
                .existsByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThai(
                        cuocHopId,
                        LoaiYeuCau.BIEN_BAN,
                        TrangThai.CHO_DUYET);

        if (daCoYeuCauChoDuyet) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Đã có yêu cầu duyệt biên bản đang chờ xử lý");
        }

        PhienBanBienBan phienBan = phienBanRepository
                .findFirstByBienBan_BienBanIdOrderBySoPhienBanDesc(
                        bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Biên bản chưa có phiên bản để trình duyệt"));

        // Bảo đảm phiên bản được trình đúng với nội dung hiện tại.
        if (!Objects.equals(
                phienBan.getNoiDung(),
                bienBan.getNoiDungChinhThuc())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Nội dung hiện tại chưa khớp phiên bản mới nhất, "
                    + "vui lòng lưu lại biên bản trước khi trình");
        }

        bienBanService.kiemTraNoiDung(bienBan.getTieuDe(), bienBan.getNoiDungChinhThuc());

        LocalDateTime thoiGianTrinh = LocalDateTime.now();

        YeuCauPheDuyet yeuCau = new YeuCauPheDuyet();
        yeuCau.setTrangThai(TrangThai.CHO_DUYET);

        yeuCau.setLoaiYeuCau(LoaiYeuCau.BIEN_BAN);
        yeuCau.setCuocHop(cuocHop);
        yeuCau.setPhienBanBienBan(phienBan);
        yeuCau.setNguoiTrinh(nguoiTrinh);
        yeuCau.setNguoiDuyet(nguoiDuyet);
        yeuCau.setThoiGianTrinh(thoiGianTrinh);
        yeuCau.setGhiChuTrinh(
                ghiChuTrinh == null ? null : ghiChuTrinh.trim());
        yeuCau.setCapPheDuyet("CHU_TRI");
        yeuCau.setYeuCauTruoc(yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.BIEN_BAN)
                .orElse(null));

        yeuCau.setLichHop(null);
        yeuCau.setThuKyTiepNhan(null);

        yeuCauPheDuyetRepository.save(yeuCau);

        ThaoTacBienBan.thucHien(() ->
                ThaoTacBienBan.trinhDuyet(bienBan, nguoiDuyet, thoiGianTrinh));

        // Giữ ý kiến phản hồi lần trước để tham khảo khi trình lại.
        lichSuPheDuyetService.ghiNhan(
                cuocHopId,
                null,
                bienBanId,
                nguoiTrinhId,
                "BIEN_BAN",
                "TRINH_DUYET",
                yeuCau.getGhiChuTrinh(),
                yeuCau);
        thongBaoService.gui(cuocHop, nguoiTrinhId, "BIEN_BAN_CHO_DUYET",
                "Biên bản cuộc họp cần được duyệt", bienBan.getTieuDe(), nguoiDuyet);

        return yeuCau.getYeuCauPheDuyetId();
    }
    private YeuCauPheDuyet kiemTraYeuCauDuyet(
            BienBanCuocHop bienBan,
            Integer yeuCauId,
            Integer nguoiDuyetId) {

        if (yeuCauId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thiếu mã yêu cầu phê duyệt");
        }

        // Biên bản đã được khóa trước khi gọi hàm này.
        YeuCauPheDuyet yeuCau = yeuCauPheDuyetRepository
                .findById(yeuCauId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy yêu cầu phê duyệt"));

        if (yeuCau.getLoaiYeuCau() != LoaiYeuCau.BIEN_BAN
                || !Objects.equals(
                        yeuCau.getCuocHop().getCuocHopId(),
                        bienBan.getCuocHop().getCuocHopId())
                || yeuCau.getPhienBanBienBan() == null
                || !Objects.equals(
                        yeuCau.getPhienBanBienBan()
                                .getBienBan().getBienBanId(),
                        bienBan.getBienBanId())) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Yêu cầu không thuộc biên bản này");
        }

        if (!ThaoTacCuocHop.laNguoiChuTri(bienBan.getCuocHop(), nguoiDuyetId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ chủ trì cuộc họp được duyệt hoặc trả lại biên bản");
        }

        if (yeuCau.getTrangThai() != TrangThai.CHO_DUYET
                || !"CHO_DUYET".equals(bienBan.getTrangThai())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Biên bản hoặc yêu cầu không còn chờ duyệt");
        }

        PhienBanBienBan phienBanMoiNhat = phienBanRepository
                .findFirstByBienBan_BienBanIdOrderBySoPhienBanDesc(
                        bienBan.getBienBanId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Không tìm thấy phiên bản biên bản"));

        if (!Objects.equals(
                    phienBanMoiNhat.getPhienBanId(),
                    yeuCau.getPhienBanBienBan().getPhienBanId())
                || !Objects.equals(
                    bienBan.getNoiDungChinhThuc(),
                    phienBanMoiNhat.getNoiDung())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Phiên bản đã trình không khớp nội dung hiện tại");
        }

        yeuCau.setNguoiDuyet(bienBan.getCuocHop().getNguoiChuTri());
        yeuCau.setCapPheDuyet("CHU_TRI");
        return yeuCau;
    }
    @Transactional
    public void pheDuyetBienBan(
            Integer bienBanId,
            Integer yeuCauId,
            Integer nguoiDuyetId,
            String yKien) {

        NguoiDung nguoiDuyet =
                nguoiDungHienTaiService.layTaiKhoanHoatDong(
                        nguoiDuyetId);

        BienBanCuocHop bienBan = bienBanRepository
                .findByIdForUpdate(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biên bản"));

        YeuCauPheDuyet yeuCau = kiemTraYeuCauDuyet(
                bienBan,
                yeuCauId,
                nguoiDuyetId);

        String yKienDaXuLy = yKien == null ? null : yKien.trim();

        CuocHop cuocHop = bienBan.getCuocHop();
        ThaoTacYeuCauPheDuyet.pheDuyet(yeuCau);
        bienBan.setNguoiDuyet(nguoiDuyet);
        ThaoTacBienBan.thucHien(() ->
                ThaoTacBienBan.pheDuyet(bienBan, yKienDaXuLy, LocalDateTime.now()));

        lichSuPheDuyetService.ghiNhan(
                bienBan.getCuocHop().getCuocHopId(),
                null,
                bienBanId,
                nguoiDuyetId,
                "BIEN_BAN",
                "PHE_DUYET",
                yKienDaXuLy,
                yeuCau);
        thongBaoService.gui(cuocHop, nguoiDuyetId,
                "BIEN_BAN_DA_DUYET",
                "Biên bản đã được duyệt và chờ chủ trì phát hành",
                bienBan.getTieuDe(), bienBan.getNguoiSoan());
    }
    @Transactional
    public void traLaiBienBan(
            Integer bienBanId,
            Integer yeuCauId,
            Integer nguoiDuyetId,
            String lyDo) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(
                nguoiDuyetId);

        if (lyDo == null || lyDo.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng nhập lý do trả lại");
        }

        BienBanCuocHop bienBan = bienBanRepository
                .findByIdForUpdate(bienBanId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biên bản"));

        YeuCauPheDuyet yeuCau = kiemTraYeuCauDuyet(
                bienBan,
                yeuCauId,
                nguoiDuyetId);

        bienBan.setNguoiDuyet(bienBan.getCuocHop().getNguoiChuTri());
        ThaoTacBienBan.thucHien(() -> {
            ThaoTacYeuCauPheDuyet.traLai(yeuCau);
            ThaoTacBienBan.traLai(bienBan, lyDo.trim());
        });

        lichSuPheDuyetService.ghiNhan(
                bienBan.getCuocHop().getCuocHopId(),
                null,
                bienBanId,
                nguoiDuyetId,
                "BIEN_BAN",
                "YEU_CAU_CHINH_SUA",
                lyDo.trim(),
                yeuCau);
        thongBaoService.gui(bienBan.getCuocHop(), nguoiDuyetId,
                "BIEN_BAN_TRA_LAI",
                "Biên bản cần được chỉnh sửa theo ý kiến duyệt",
                lyDo.trim(), bienBan.getNguoiSoan());
    }
    
    @Transactional(readOnly = true)
    public List<BienBanChoDuyetResponse> layBienBanChoToiDuyet(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        return yeuCauPheDuyetRepository
                .findByCuocHop_NguoiChuTri_NguoiDungIdAndLoaiYeuCauAndTrangThaiOrderByThoiGianTrinhDesc(
                        nguoiDungId, LoaiYeuCau.BIEN_BAN, TrangThai.CHO_DUYET)
                .stream()
                .filter(yeuCau -> yeuCau.getPhienBanBienBan() != null)
                .map(yeuCau -> {
                    PhienBanBienBan phienBan = yeuCau.getPhienBanBienBan();
                    BienBanCuocHop bienBan = phienBan.getBienBan();

                    return new BienBanChoDuyetResponse(
                            yeuCau.getYeuCauPheDuyetId(),
                            bienBan.getBienBanId(),
                            bienBan.getCuocHop().getCuocHopId(),
                            bienBan.getCuocHop().getTieuDe(),
                            bienBan.getTieuDe(),
                            phienBan.getSoPhienBan(),
                            phienBan.getNoiDung(),
                            yeuCau.getGhiChuTrinh(),
                            "CHU_TRI");
                })
                .toList();
    }
}
