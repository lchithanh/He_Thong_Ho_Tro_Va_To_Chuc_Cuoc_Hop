package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.response.ChiTietKeHoachPheDuyetResponse;
import com.example.demo.dto.response.ChiTietKeHoachPheDuyetResponse.QuyetDinh;
import com.example.demo.dto.response.KeHoachPheDuyetResponse;
import com.example.demo.dto.response.LichSuKeHoachResponse;
import com.example.demo.dto.response.LichSuKeHoachResponse.HanhDong;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.LichSuPheDuyet;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KhachMoiCuocHopRepository;
import com.example.demo.repository.LichSuPheDuyetRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class PheDuyetKeHoachService {

    private final CuocHopRepository cuocHopRepository;
    private final KhachMoiCuocHopRepository khachMoiCuocHopRepository;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    private final LichSuPheDuyetService lichSuPheDuyetService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final QuyTrinhKeHoachService quyTrinhKeHoachService;
    private final LichSuPheDuyetRepository lichSuPheDuyetRepository;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final tools.jackson.databind.ObjectMapper objectMapper;
    
    @Transactional

    public Integer trinhKeHoachCuocHop(Integer cuocHopId, Integer lichHopId,
            Integer nguoiTrinhId, String ghiChuTrinh, Integer lanhDaoDuyetId) {
        return quyTrinhKeHoachService.trinhKeHoachCuocHop(
                cuocHopId, lichHopId, nguoiTrinhId, ghiChuTrinh, lanhDaoDuyetId);
    }

	@Transactional

    public boolean pheDuyetKeHoach(Integer cuocHopId, Integer yeuCauPheDuyetId,
            Integer nguoiDuyetId, String yKien) {
        return quyTrinhKeHoachService.pheDuyetKeHoach(
                cuocHopId, yeuCauPheDuyetId, nguoiDuyetId, yKien);
    }

	@Transactional

    public void tuChoiKeHoach(Integer cuocHopId, Integer yeuCauPheDuyetId,
            Integer nguoiDuyetId, String lyDo) {
        quyTrinhKeHoachService.tuChoiKeHoach(cuocHopId, yeuCauPheDuyetId, nguoiDuyetId, lyDo);
    }

    @Transactional

    public void traLaiKeHoach(Integer cuocHopId, Integer yeuCauPheDuyetId,
            Integer nguoiDuyetId, String lyDo) {
        quyTrinhKeHoachService.traLaiKeHoach(cuocHopId, yeuCauPheDuyetId, nguoiDuyetId, lyDo);
    }

    @Transactional(readOnly = true)
    public List<KeHoachPheDuyetResponse> layDanhSachKeHoachChoDuyet(
            Integer nguoiDungId) {
        return layDanhSachKeHoachTheoTrangThai(nguoiDungId, TrangThai.CHO_DUYET);
    }

    @Transactional(readOnly = true)
    public List<KeHoachPheDuyetResponse> layDanhSachKeHoachDaDuyet(
            Integer nguoiDungId) {
        return layDanhSachKeHoachTheoTrangThai(nguoiDungId, TrangThai.DA_DUYET);
    }

    private List<KeHoachPheDuyetResponse> layDanhSachKeHoachTheoTrangThai(
            Integer nguoiDungId, TrangThai trangThai) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        return yeuCauPheDuyetRepository
                .timTheoNguoiDuyet(LoaiYeuCau.KE_HOACH_CUOC_HOP, trangThai, nguoiDungId)
                .stream()
                .filter(yc -> trangThai != TrangThai.CHO_DUYET
                        || quyTrinhKeHoachService.dangChoDungCapDuyet(yc))
                .filter(yc -> trangThai != TrangThai.DA_DUYET || !laDuyetCapTrungGian(yc))
                .map(this::chuyenDanhSachResponse).toList();
    }

    @Transactional(readOnly = true)
    public ChiTietKeHoachPheDuyetResponse layChiTietKeHoachHienTai(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        YeuCauPheDuyet yeuCau = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHopId);
        if (yeuCau == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hồ sơ chưa có lần trình kế hoạch");
        }
        kiemTraQuyenXemYeuCau(yeuCau, nguoiDungId);
        QuyetDinh quyetDinh = yeuCau.getTrangThai() == TrangThai.DA_DUYET
                ? lichSuPheDuyetRepository
                        .findFirstByYeuCauPheDuyet_YeuCauPheDuyetIdAndLoaiPheDuyetAndHanhDongOrderByThoiGianPheDuyetDescLichSuPheDuyetIdDesc(
                                yeuCau.getYeuCauPheDuyetId(), "KE_HOACH",
                                laDuyetCapTrungGian(yeuCau) ? "DONG_Y_TRINH_CAP_TREN" : "PHE_DUYET")
                        .map(muc -> new QuyetDinh(muc.getNguoiPheDuyet().getNguoiDungId(),
                                muc.getNguoiPheDuyet().getHoTen(), muc.getThoiGianPheDuyet(), muc.getYKien()))
                        .orElse(null)
                : null;
        return taoChiTietKeHoachResponse(yeuCau, quyetDinh);
    }

    @Transactional(readOnly = true)

    public ChiTietKeHoachPheDuyetResponse layChiTietKeHoachChoDuyet(
            Integer yeuCauPheDuyetId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        YeuCauPheDuyet yc = layYeuCauKeHoach(yeuCauPheDuyetId);
        kiemTraQuyenXemYeuCau(yc, nguoiDungId);
        if (yc.getTrangThai() != TrangThai.CHO_DUYET) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Yêu cầu không còn chờ xử lý");
        }
        return taoChiTietKeHoachResponse(yc, null);
    }

@Transactional(readOnly = true)
public ChiTietKeHoachPheDuyetResponse layChiTietKeHoachDaDuyet(
        Integer yeuCauPheDuyetId,
        Integer nguoiDungId) {

    nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

    YeuCauPheDuyet yeuCau = layYeuCauKeHoach(yeuCauPheDuyetId);
    kiemTraQuyenXemYeuCau(yeuCau, nguoiDungId);

    if (yeuCau.getTrangThai() != TrangThai.DA_DUYET) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Yêu cầu chưa ở trạng thái đã duyệt");
    }
    if (laDuyetCapTrungGian(yeuCau)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Đây là quyết định cấp 1; kế hoạch chưa được duyệt cuối");
    }

    LichSuPheDuyet quyetDinh = lichSuPheDuyetRepository
            .findFirstByYeuCauPheDuyet_YeuCauPheDuyetIdAndLoaiPheDuyetAndHanhDongOrderByThoiGianPheDuyetDescLichSuPheDuyetIdDesc(
                    yeuCauPheDuyetId, "KE_HOACH", "PHE_DUYET")
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy bản ghi quyết định phê duyệt"));

    NguoiDung nguoiDuyet = quyetDinh.getNguoiPheDuyet();

    QuyetDinh thongTinQuyetDinh = new QuyetDinh(
            nguoiDuyet.getNguoiDungId(),
            nguoiDuyet.getHoTen(),
            quyetDinh.getThoiGianPheDuyet(),
            quyetDinh.getYKien());

    return taoChiTietKeHoachResponse(yeuCau, thongTinQuyetDinh);
}

private ChiTietKeHoachPheDuyetResponse taoChiTietKeHoachResponse(
        YeuCauPheDuyet yeuCau,
        QuyetDinh quyetDinh) {

    var banChup = yeuCau.getKeHoachDaTrinh() == null ? taoBanChup(yeuCau)
            : objectMapper.readValue(yeuCau.getKeHoachDaTrinh(), ChiTietKeHoachPheDuyetResponse.class);
    return new ChiTietKeHoachPheDuyetResponse(banChup.yeuCauPheDuyetId() == null
            ? yeuCau.getYeuCauPheDuyetId() : banChup.yeuCauPheDuyetId(), banChup.cuocHopId(),
            banChup.tieuDe(), banChup.mucTieu(), banChup.noiDungDuKien(), banChup.loaiHinh(),
            banChup.phamViCuocHop(), banChup.lichHopId(), banChup.thoiGianBatDau(), banChup.thoiGianKetThuc(),
            banChup.ghiChuTrinh(), yeuCau.getTrangThai().name(), banChup.tenNguoiTrinh(),
            banChup.tenThuKyCuocHop(), banChup.tenChuTri(), banChup.tenNguoiDeXuat(),
            banChup.tenPhongHop(), banChup.viTriPhongHop(), quyetDinh,
            yeuCau.getCapPheDuyet(),
            yeuCau.getYeuCauTruoc() == null ? null
                    : yeuCau.getYeuCauTruoc().getYeuCauPheDuyetId(),
            ten(yeuCau.getNguoiDuyet()), yeuCau.getThoiGianTrinh());
}

static ChiTietKeHoachPheDuyetResponse taoBanChup(YeuCauPheDuyet yeuCau) {
    CuocHop cuocHop = yeuCau.getCuocHop();
    LichHop lichHop = yeuCau.getLichHop();
    var phong = lichHop == null ? null : lichHop.getPhongHop();

    return new ChiTietKeHoachPheDuyetResponse(
            yeuCau.getYeuCauPheDuyetId(),
            cuocHop.getCuocHopId(),
            cuocHop.getTieuDe(),
            cuocHop.getMucTieu(),
            cuocHop.getNoiDungDuKien(),
            cuocHop.getLoaiHinh(),
            cuocHop.getPhamViCuocHop(),
            lichHop == null ? null : lichHop.getLichHopId(),
            lichHop == null ? null : lichHop.getThoiGianBatDau(),
            lichHop == null ? null : lichHop.getThoiGianKetThuc(),
            yeuCau.getGhiChuTrinh(),
            yeuCau.getTrangThai().name(),
            ten(yeuCau.getNguoiTrinh()),
            ten(yeuCau.getThuKyBanGiao()),
            ten(cuocHop.getNguoiChuTri()),
            ten(cuocHop.getNguoiDeXuat()),
            phong == null ? null : phong.getTenPhongHop(),
            phong == null ? null : phong.getViTri(),
            null,
            yeuCau.getCapPheDuyet(),
            yeuCau.getYeuCauTruoc() == null ? null
                    : yeuCau.getYeuCauTruoc().getYeuCauPheDuyetId(),
            ten(yeuCau.getNguoiDuyet()), yeuCau.getThoiGianTrinh());
}

private static String ten(NguoiDung nd) {
    return nd == null ? null : nd.getHoTen();
}
@Transactional(readOnly = true)
public List<LichSuKeHoachResponse> layLichSuPheDuyetKeHoach(
        Integer cuocHopId,
        Integer nguoiDungId) {

    nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

    if (cuocHopId == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
    }
    CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

    List<YeuCauPheDuyet> cacLanTrinh = yeuCauPheDuyetRepository
            .findByCuocHop_CuocHopIdAndLoaiYeuCauOrderByThoiGianTrinhDescYeuCauPheDuyetIdDesc(
                    cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP);
    boolean duocXem = phanQuyenCuocHopService.coVaiTro(nguoiDungId, "ADMIN")
            || ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDungId)
            || cacLanTrinh.stream().anyMatch(yc -> coQuyenXemYeuCau(yc, nguoiDungId));
    if (!duocXem) throw new ResponseStatusException(
            HttpStatus.FORBIDDEN, "Bạn không có quyền xem lịch sử kế hoạch này");

    if (cacLanTrinh.isEmpty()) {
        return List.of();
    }

    List<Integer> yeuCauIds = cacLanTrinh.stream()
            .map(YeuCauPheDuyet::getYeuCauPheDuyetId)
            .toList();

    Map<Integer, List<HanhDong>> hanhDongTheoYeuCau = new HashMap<>();

    for (LichSuPheDuyet lichSu :
            lichSuPheDuyetRepository.findLichSuKeHoachTheoYeuCauIds(yeuCauIds)) {

        Integer yeuCauId = lichSu.getYeuCauPheDuyet()
                .getYeuCauPheDuyetId();
        NguoiDung nguoiThucHien = lichSu.getNguoiPheDuyet();

        hanhDongTheoYeuCau
                .computeIfAbsent(yeuCauId, id -> new ArrayList<>())
                .add(new HanhDong(
                        lichSu.getHanhDong(),
                        nguoiThucHien.getNguoiDungId(),
                        nguoiThucHien.getHoTen(),
                        lichSu.getThoiGianPheDuyet(),
                        lichSu.getYKien()));
    }

    return cacLanTrinh.stream()
            .map(yeuCau -> new LichSuKeHoachResponse(
                    yeuCau.getYeuCauPheDuyetId(),
                    yeuCau.getThoiGianTrinh(),
                    yeuCau.getGhiChuTrinh(),
                    yeuCau.getTrangThai().name(),
                    yeuCau.getCapPheDuyet(),
                    yeuCau.getYeuCauTruoc() == null ? null
                            : yeuCau.getYeuCauTruoc().getYeuCauPheDuyetId(),
                    hanhDongTheoYeuCau.getOrDefault(
                            yeuCau.getYeuCauPheDuyetId(), List.of())))
            .toList();
}

    @Transactional(readOnly = true)
    public List<KeHoachPheDuyetResponse> layHoSoCuaThuKyLanhDao(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!phanQuyenCuocHopService.coVaiTro(nguoiDungId, "THU_KY")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có vai trò THU_KY");
        }
        return yeuCauPheDuyetRepository
                .timTheoThuKyTiepNhan(LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.CHO_DUYET,
                        nguoiDungId, List.of("CHO_THU_KY_LANH_DAO", "THU_KY_LANH_DAO_XU_LY"))
                .stream()
                .map(this::chuyenDanhSachResponse)
                .toList();
    }

    private KeHoachPheDuyetResponse chuyenDanhSachResponse(YeuCauPheDuyet yc) {
        LichHop lich = yc.getLichHop();
        return new KeHoachPheDuyetResponse(yc.getYeuCauPheDuyetId(),
                yc.getCuocHop().getCuocHopId(), yc.getCuocHop().getTieuDe(),
                lich == null ? null : lich.getLichHopId(),
                lich == null ? null : lich.getThoiGianBatDau(),
                lich == null ? null : lich.getThoiGianKetThuc(), yc.getTrangThai().name(),
                yc.getYeuCauTruoc() == null ? null
                        : yc.getYeuCauTruoc().getYeuCauPheDuyetId(),
                yc.getCapPheDuyet(),
                yc.getTrangThai() == TrangThai.CHO_DUYET && laDuyetCapTrungGian(yc),
                yc.getThoiGianTrinh());
    }

    private boolean laDuyetCapTrungGian(YeuCauPheDuyet yc) {
        return "TRUONG_NHOM".equals(yc.getCapPheDuyet()) || ("TRUONG_PHONG".equals(yc.getCapPheDuyet())
                && QuyTacDuyetKeHoach.canTrinhCapTren(
                        phanQuyenCuocHopService.coVaiTro(
                                yc.getCuocHop().getNguoiDeXuat().getNguoiDungId(), "LANH_DAO"),
                        yc.getCuocHop().getPhamViCuocHop(),
                        Boolean.TRUE.equals(yc.getCuocHop().getCoKhachMoiBenNgoai())
                                || khachMoiCuocHopRepository
                                        .existsByCuocHop_CuocHopIdAndTrangThaiMoiNot(
                                                yc.getCuocHop().getCuocHopId(), "DA_HUY")));
    }

    private YeuCauPheDuyet layYeuCauKeHoach(Integer id) {
        if (id == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu mã yêu cầu");
        return yeuCauPheDuyetRepository.findById(id)
                .filter(yc -> yc.getLoaiYeuCau() == LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy kế hoạch"));
    }

    private boolean coQuyenXemYeuCau(YeuCauPheDuyet yc, Integer id) {
        return phanCongXuLyKeHoachService.laThuKyDaNhanBanGiao(yc, id)
                || phanQuyenCuocHopService.coVaiTro(id, "ADMIN")
                || ThaoTacCuocHop.laNguoiDeXuat(yc.getCuocHop(), id)
                || (yc.getNguoiTrinh() != null && id.equals(yc.getNguoiTrinh().getNguoiDungId()))
                || (yc.getNguoiDuyet() != null && id.equals(yc.getNguoiDuyet().getNguoiDungId()))
                || (yc.getThuKyTiepNhan() != null && id.equals(yc.getThuKyTiepNhan().getNguoiDungId()));
    }

    private void kiemTraQuyenXemYeuCau(YeuCauPheDuyet yc, Integer id) {
        if (!coQuyenXemYeuCau(yc, id)) throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Bạn không có quyền xem hồ sơ này");
    }

    @Transactional
    public Integer trinhKeHoachCuocHop(Integer cuocHopId, Integer lichHopId,
            Integer nguoiTrinhId, String ghiChuTrinh, Integer lanhDaoDuyetId,
            List<Integer> taiLieuIds) {
        return quyTrinhKeHoachService.trinhKeHoachCuocHop(cuocHopId, lichHopId,
                nguoiTrinhId, ghiChuTrinh, lanhDaoDuyetId, taiLieuIds);
    }
}



