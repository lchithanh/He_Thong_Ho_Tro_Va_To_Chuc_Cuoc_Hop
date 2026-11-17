package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.LuuKetLuanCuocHopRequest;
import com.example.demo.dto.response.KetLuanCuocHopResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.KetLuanCuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KetLuanCuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KetLuanCuocHopService {

    private final CuocHopRepository cuocHopRepository;
    private final KetLuanCuocHopRepository ketLuanCuocHopRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final TaiLieuCuocHopRepository taiLieuCuocHopRepository;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    private final ThongBaoService thongBaoService;
    private final LichSuPheDuyetService lichSuPheDuyetService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;


    @Transactional
    public KetLuanCuocHopResponse taoKetLuanNhap(
            Integer cuocHopId,
            LuuKetLuanCuocHopRequest request,
            Integer nguoiDangNhapId) {

        if (nguoiDangNhapId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }
        if (cuocHopId == null || cuocHopId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }
        if (request == null || request.tieuDe() == null
                || request.tieuDe().isBlank()
                || request.tieuDe().strip().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tiêu đề phải dài từ 1 đến 255 ký tự");
        }
        

        var cuocHopDaKhoa = cuocHopRepository.findByIdForUpdate(cuocHopId);
        NguoiDung nguoiSoan = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopDaKhoa.orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        kiemTraThuKy(cuocHop, nguoiDangNhapId);
        if (!"DA_KET_THUC".equals(cuocHop.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ được tạo kết luận sau khi cuộc họp đã kết thúc");
        }
        if (ketLuanCuocHopRepository.existsByCuocHop_CuocHopId(cuocHopId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cuộc họp đã có kết luận");
        }

        KetLuanCuocHop ketLuan = new KetLuanCuocHop();
        ketLuan.setCuocHop(cuocHop);
        ketLuan.setNguoiSoan(nguoiSoan);
        ketLuan.setTieuDe(request.tieuDe().strip());

        String noiDung = request.noiDung();
        ketLuan.setNoiDung(
                noiDung == null || noiDung.isBlank() ? null : noiDung.strip());
        ketLuan.setTrangThai("NHAP");

        KetLuanCuocHop daLuu = ketLuanCuocHopRepository.save(ketLuan);

        return toResponse(daLuu);
    }

    @Transactional
    public KetLuanCuocHopResponse capNhatKetLuanNhap(
            Integer cuocHopId,
            LuuKetLuanCuocHopRequest request,
            Integer nguoiDangNhapId) {
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        kiemTraThuKy(cuocHop, nguoiDangNhapId);
        KetLuanCuocHop ketLuan = ketLuanCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có kết luận"));
        if (!"NHAP".equals(ketLuan.getTrangThai())
                && !"BI_TRA_LAI".equals(ketLuan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ được sửa kết luận nháp hoặc bị trả lại");
        }
        kiemTraNoiDungKetLuan(request);
        ketLuan.setTieuDe(request.tieuDe().strip());
        ketLuan.setNoiDung(request.noiDung() == null || request.noiDung().isBlank()
                ? null : request.noiDung().strip());
        return toResponse(ketLuan);
    }

    @Transactional
    public Integer trinhDuyetKetLuan(
            Integer cuocHopId,
            String ghiChuTrinh,
            Integer nguoiTrinhId) {
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        NguoiDung nguoiTrinh = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiTrinhId);
        kiemTraThuKy(cuocHop, nguoiTrinhId);
        if (!"DA_KET_THUC".equals(cuocHop.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ trình kết luận sau khi cuộc họp đã kết thúc");
        }
        KetLuanCuocHop ketLuan = ketLuanCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có kết luận"));
        if (!"NHAP".equals(ketLuan.getTrangThai())
                && !"BI_TRA_LAI".equals(ketLuan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ trình kết luận nháp hoặc bị trả lại");
        }
        if (cuocHop.getNguoiChuTri() == null
                || Objects.equals(cuocHop.getNguoiChuTri().getNguoiDungId(), nguoiTrinhId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cuộc họp cần có chủ trì khác thư ký để duyệt kết luận");
        }
        boolean coNoiDung = ketLuan.getNoiDung() != null
                && !ketLuan.getNoiDung().isBlank();
        if (!coNoiDung && !taiLieuCuocHopRepository
                .existsByCuocHop_CuocHopIdAndLoaiTaiLieu(
                        cuocHopId, "KET_LUAN_CUOC_HOP")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Kết luận cần có nội dung hoặc ít nhất một tệp đính kèm");
        }
        if (yeuCauPheDuyetRepository.existsByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThai(
                cuocHopId, LoaiYeuCau.KET_LUAN, TrangThai.CHO_DUYET)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Đã có yêu cầu duyệt kết luận đang chờ xử lý");
        }
        YeuCauPheDuyet yeuCau = new YeuCauPheDuyet();
        yeuCau.setLoaiYeuCau(LoaiYeuCau.KET_LUAN);
        yeuCau.setCuocHop(cuocHop);
        yeuCau.setNguoiTrinh(nguoiTrinh);
        yeuCau.setNguoiDuyet(cuocHop.getNguoiChuTri());
        yeuCau.setCapPheDuyet("CHU_TRI");
        yeuCau.setYeuCauTruoc(yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KET_LUAN)
                .orElse(null));
        yeuCau.setTrangThai(TrangThai.CHO_DUYET);
        yeuCau.setThoiGianTrinh(LocalDateTime.now());
        yeuCau.setGhiChuTrinh(ghiChuTrinh == null ? null : ghiChuTrinh.strip());
        yeuCauPheDuyetRepository.save(yeuCau);
        ketLuan.setTrangThai("CHO_DUYET");
        thongBaoService.gui(cuocHop, nguoiTrinhId, "KET_LUAN_CHO_DUYET",
                "Kết luận cuộc họp cần được duyệt", ketLuan.getTieuDe(),
                cuocHop.getNguoiChuTri());
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiTrinhId,
                "KET_LUAN", "TRINH_DUYET", ghiChuTrinh, yeuCau);
        return yeuCau.getYeuCauPheDuyetId();
    }

    @Transactional
    public void xuLyDuyetKetLuan(
            Integer cuocHopId,
            Integer yeuCauId,
            Integer nguoiDuyetId,
            String thaoTac,
            String yKien) {
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDuyetId);
        YeuCauPheDuyet yeuCau = yeuCauPheDuyetRepository.findByIdForUpdate(yeuCauId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu duyệt kết luận"));
        if (yeuCau.getLoaiYeuCau() != LoaiYeuCau.KET_LUAN
                || !cuocHopId.equals(yeuCau.getCuocHop().getCuocHopId())
                || yeuCau.getTrangThai() != TrangThai.CHO_DUYET
                || !ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDuyetId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Yêu cầu không chờ bạn xử lý");
        }
        KetLuanCuocHop ketLuan = ketLuanCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có kết luận"));
        if (!"CHO_DUYET".equals(ketLuan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Kết luận không còn chờ duyệt");
        }
        yeuCau.setNguoiDuyet(cuocHop.getNguoiChuTri());
        yeuCau.setCapPheDuyet("CHU_TRI");
        if ("tra-lai".equals(thaoTac)) {
            if (yKien == null || yKien.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Vui lòng nhập lý do trả lại kết luận");
            }
            yeuCau.setTrangThai(TrangThai.TRA_LAI);
            yeuCau.setGhiChuTrinh(yKien.strip());
            ketLuan.setTrangThai("BI_TRA_LAI");
            thongBaoService.gui(cuocHop, nguoiDuyetId, "KET_LUAN_TRA_LAI",
                    "Kết luận cần được chỉnh sửa", yKien.strip(),
                    ketLuan.getNguoiSoan());
            lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiDuyetId,
                    "KET_LUAN", "YEU_CAU_CHINH_SUA", yKien, yeuCau);
            return;
        }
        if (!"phe-duyet".equals(thaoTac)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thao tác duyệt kết luận không hợp lệ");
        }
        yeuCau.setTrangThai(TrangThai.DA_DUYET);
        ketLuan.setTrangThai("DA_DUYET");
        thongBaoChoThuKy(cuocHop, nguoiDuyetId, ketLuan);
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiDuyetId,
                "KET_LUAN", "PHE_DUYET", yKien, yeuCau);
    }
    
    @Transactional
    public KetLuanCuocHopResponse phatHanhKetLuan(
            Integer cuocHopId,
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
        NguoiDung nguoiPhatHanh = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopDaKhoa.orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDangNhapId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ chủ trì cuộc họp được phát hành kết luận");
        }
        if (!"DA_KET_THUC".equals(cuocHop.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chỉ được phát hành kết luận sau khi cuộc họp đã kết thúc");
        }

        KetLuanCuocHop ketLuan = ketLuanCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có kết luận"));

        if (!"DA_DUYET".equals(ketLuan.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Kết luận chỉ được phát hành sau khi đã duyệt cuối");
        }

        String noiDung = ketLuan.getNoiDung();
        boolean coNoiDung = noiDung != null && !noiDung.isBlank();

        if (!coNoiDung && !taiLieuCuocHopRepository
                .existsByCuocHop_CuocHopIdAndLoaiTaiLieu(
                        cuocHopId, "KET_LUAN_CUOC_HOP")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Kết luận cần có nội dung hoặc ít nhất một file đính kèm");
        }

        ketLuan.setNguoiPhatHanh(nguoiPhatHanh);
        ketLuan.setNgayPhatHanh(LocalDateTime.now());
        ketLuan.setTrangThai("DA_PHAT_HANH");
        for (var thanhVien : thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)) {
            thongBaoService.gui(cuocHop, nguoiDangNhapId,
                    "KET_LUAN_DA_PHAT_HANH",
                    "Kết luận cuộc họp đã được phát hành",
                    ketLuan.getTieuDe(), thanhVien.getNguoiDung());
        }

        return toResponse(ketLuan);
    }
    
    KetLuanCuocHop timKetLuanDuocXem(
            Integer cuocHopId,
            Integer nguoiDangNhapId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);

        if (cuocHopId == null || cuocHopId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }

        KetLuanCuocHop ketLuan = ketLuanCuocHopRepository
                .findByCuocHop_CuocHopId(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cuộc họp chưa có kết luận"));

        boolean duocXem = ThaoTacCuocHop.laNguoiChuTri(
                ketLuan.getCuocHop(), nguoiDangNhapId)
                || phanCongXuLyKeHoachService.laThuKyToChuc(ketLuan.getCuocHop(), nguoiDangNhapId)
                || thanhVienCuocHopRepository
                        .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungIdAndVaiTroTaiCuocHop(
                                cuocHopId, nguoiDangNhapId, "THU_KY")
                || ("DA_PHAT_HANH".equals(ketLuan.getTrangThai())
                        && thanhVienCuocHopRepository
                                .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungId(
                                        cuocHopId, nguoiDangNhapId));

        if (!duocXem) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Bạn không có quyền xem kết luận cuộc họp");
        }

        return ketLuan;
    }
    
    @Transactional(readOnly = true)
    public List<KetLuanCuocHopResponse> layKetLuanChoToiPhatHanh(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        Map<Integer, KetLuanCuocHopResponse> ketLuanTheoId = new LinkedHashMap<>();
        yeuCauPheDuyetRepository
                .findByCuocHop_NguoiChuTri_NguoiDungIdAndLoaiYeuCauAndTrangThaiOrderByThoiGianTrinhDesc(
                        nguoiDungId, LoaiYeuCau.KET_LUAN, TrangThai.CHO_DUYET)
                .forEach(yeuCau -> ketLuanCuocHopRepository
                        .findByCuocHop_CuocHopId(yeuCau.getCuocHop().getCuocHopId())
                        .filter(ketLuan -> "CHO_DUYET".equals(ketLuan.getTrangThai()))
                        .ifPresent(ketLuan -> ketLuanTheoId.put(
                                ketLuan.getKetLuanId(), toResponse(ketLuan))));

        ketLuanCuocHopRepository.findAll().stream()
                .filter(ketLuan -> "DA_DUYET".equals(ketLuan.getTrangThai()))
                .filter(ketLuan -> ThaoTacCuocHop.laNguoiChuTri(ketLuan.getCuocHop(), nguoiDungId))
                .forEach(ketLuan -> ketLuanTheoId.put(
                        ketLuan.getKetLuanId(), toResponse(ketLuan)));
        return new ArrayList<>(ketLuanTheoId.values());
    }
    
    @Transactional(readOnly = true)
    public List<KetLuanCuocHopResponse> layKetLuanDaPhatHanhCuaToi(
            Integer nguoiDungId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        return ketLuanCuocHopRepository
                .timKetLuanDaPhatHanhDuocXem(nguoiDungId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public KetLuanCuocHopResponse layKetLuanTheoCuocHop(
            Integer cuocHopId, Integer nguoiDangNhapId) {

        return toResponse(timKetLuanDuocXem(cuocHopId, nguoiDangNhapId));
    }
    
    public record TepKetLuanTaiXuong(String tenFile, byte[] noiDung) {
    }

    
    private KetLuanCuocHopResponse toResponse(KetLuanCuocHop ketLuan) {
        CuocHop cuocHop = ketLuan.getCuocHop();

        LocalDateTime ketThuc = cuocHop.getThoiGianKetThucThucTe();
        LocalDate hanPhatHanh = ketThuc == null
                ? null
                : ketThuc.toLocalDate().plusDays(3);

        LocalDateTime ngayPhatHanh = ketLuan.getNgayPhatHanh();
        LocalDate ngayKiemTra = ngayPhatHanh == null
                ? LocalDate.now()
                : ngayPhatHanh.toLocalDate();

        Boolean quaHanPhatHanh = hanPhatHanh == null
                ? null
                : ngayKiemTra.isAfter(hanPhatHanh);
        YeuCauPheDuyet yeuCau = yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHop.getCuocHopId(), LoaiYeuCau.KET_LUAN)
                .orElse(null);

        boolean choChuTriDuyet = "CHO_DUYET".equals(ketLuan.getTrangThai()) && yeuCau != null;
        NguoiDung nguoiDuyet = choChuTriDuyet ? cuocHop.getNguoiChuTri()
                : yeuCau == null ? null : yeuCau.getNguoiDuyet();

        return new KetLuanCuocHopResponse(
                ketLuan.getKetLuanId(),
                cuocHop.getCuocHopId(),
                cuocHop.getTieuDe(),
                ketLuan.getTieuDe(),
                ketLuan.getNoiDung(),
                ketLuan.getTrangThai(),
                hanPhatHanh,
                quaHanPhatHanh,
                yeuCau == null ? null : yeuCau.getYeuCauPheDuyetId(),
                choChuTriDuyet ? "CHU_TRI" : yeuCau == null ? null : yeuCau.getCapPheDuyet(),
                nguoiDuyet == null ? null : nguoiDuyet.getNguoiDungId(),
                nguoiDuyet == null ? null : nguoiDuyet.getHoTen(),
                yeuCau == null || yeuCau.getTrangThai() != TrangThai.TRA_LAI
                        ? null : yeuCau.getGhiChuTrinh(),
                ketLuan.getNguoiSoan().getHoTen(),
                yeuCau == null ? null : yeuCau.getThoiGianTrinh(),
                ketLuan.getNguoiPhatHanh() == null ? null : ketLuan.getNguoiPhatHanh().getHoTen(),
                ngayPhatHanh
        );
    }

    private void kiemTraNoiDungKetLuan(LuuKetLuanCuocHopRequest request) {
        if (request == null || request.tieuDe() == null
                || request.tieuDe().isBlank()
                || request.tieuDe().strip().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tiêu đề phải dài từ 1 đến 255 ký tự");
        }
    }

    private void kiemTraThuKy(CuocHop cuocHop, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Chỉ thư ký cuộc họp được bàn giao sau khi duyệt cuối mới được xử lý kết luận");
        }
    }

    private void thongBaoChoThuKy(
            CuocHop cuocHop, Integer nguoiThaoTacId, KetLuanCuocHop ketLuan) {
        thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdAndVaiTroTaiCuocHop(
                        cuocHop.getCuocHopId(), "THU_KY")
                .forEach(thanhVien -> thongBaoService.gui(
                        cuocHop, nguoiThaoTacId, "KET_LUAN_DA_DUYET",
                        "Kết luận đã được duyệt và chờ chủ trì phát hành",
                        ketLuan.getTieuDe(), thanhVien.getNguoiDung()));
    }
}



