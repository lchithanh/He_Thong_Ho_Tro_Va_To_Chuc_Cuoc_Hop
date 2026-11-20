package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.repository.ThanhVienBieuQuyetRepository;
import com.example.demo.dto.request.TaoBieuQuyetRequest;
import com.example.demo.dto.response.BieuQuyetThamDuResponse;
import com.example.demo.dto.response.BieuQuyetTomTatResponse;
import com.example.demo.dto.response.ChiTietBieuQuyetResponse;
import com.example.demo.dto.response.ChiTietBieuQuyetResponse.LuaChonResponse;
import com.example.demo.dto.response.KetQuaBieuQuyetResponse;
import com.example.demo.dto.response.KetQuaBieuQuyetResponse.KetQuaLuaChon;
import com.example.demo.entity.BieuQuyet;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LuaChonBieuQuyet;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhieuBieuQuyet;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;
import com.example.demo.entity.ThanhVienBieuQuyetId;
import com.example.demo.repository.BieuQuyetRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.LuaChonBieuQuyetRepository;
import com.example.demo.repository.PhieuBieuQuyetRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BieuQuyetService {

    private final CapNhatHoSoRealtime capNhatRealtime;
    private final CuocHopRepository cuocHopRepository;
    private final BieuQuyetRepository bieuQuyetRepository;
    private final LuaChonBieuQuyetRepository luaChonBieuQuyetRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhieuBieuQuyetRepository phieuBieuQuyetRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final ThanhVienBieuQuyetRepository thanhVienBieuQuyetRepository;
    
    @Value("${app.bieu-quyet.khoa-phieu:}")
    private String khoaPhieuBase64;

    @Transactional
    public Integer taoBieuQuyet(
            Integer cuocHopId,
            TaoBieuQuyetRequest request,
            Integer nguoiDangNhapId
    ) {
        NguoiDung nguoiTao = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiDangNhapId);

        if (cuocHopId == null || cuocHopId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }
        kiemTraDuLieu(request);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"
                ));

        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDangNhapId)) {
            throw loi(
                    HttpStatus.FORBIDDEN,
                    "Chỉ người chủ trì được tạo biểu quyết"
            );
        }

        if (!ThaoTacCuocHop.coTheLapLich(cuocHop)
                && !ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(
                    HttpStatus.CONFLICT,
                    "Chỉ được tạo biểu quyết sau khi kế hoạch đã duyệt "
                            + "và cuộc họp chưa kết thúc"
            );
        }
        List<Integer> nguoiDuocBieuQuyetIds = request.nguoiDuocBieuQuyetIds();
        if (nguoiDuocBieuQuyetIds == null || nguoiDuocBieuQuyetIds.isEmpty()
                || nguoiDuocBieuQuyetIds.stream().anyMatch(id -> id == null || id <= 0)
                || new HashSet<>(nguoiDuocBieuQuyetIds).size() != nguoiDuocBieuQuyetIds.size()) {
            throw loi(HttpStatus.BAD_REQUEST,
                    "Chọn ít nhất một thành viên, không chọn trùng người bỏ phiếu");
        }
        Set<Integer> thanhVienHopLe = thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream()
                .map(thanhVien -> thanhVien.getId().getNguoiDungId())
                .collect(java.util.stream.Collectors.toSet());
        if (!thanhVienHopLe.containsAll(nguoiDuocBieuQuyetIds)) {
            throw loi(HttpStatus.BAD_REQUEST,
                    "Danh sách người bỏ phiếu có tài khoản không thuộc cuộc họp");
        }
        BieuQuyet bieuQuyet = new BieuQuyet();
        bieuQuyet.setCuocHop(cuocHop);
        bieuQuyet.setNguoiTao(nguoiTao);
        bieuQuyet.setTieuDe(request.tieuDe().strip());
        bieuQuyet.setNoiDung(
                request.noiDung() == null ? null : request.noiDung().strip()
        );
        bieuQuyet.setCheDoBieuQuyet(request.cheDoBieuQuyet());
        bieuQuyet.setChoPhepTrangThai(true);
        bieuQuyet.setTyLeThamGiaToiThieu(null);
        bieuQuyet.setThoiGianBatDau(request.thoiGianBatDau());
        bieuQuyet.setThoiGianKetThuc(request.thoiGianKetThuc());
        bieuQuyet.setTrangThai("NHAP");
        BieuQuyet daLuu = bieuQuyetRepository.save(bieuQuyet);
        thanhVienBieuQuyetRepository.saveAll(nguoiDuocBieuQuyetIds.stream()
                .map(nguoiDungId -> new com.example.demo.entity.ThanhVienBieuQuyet(
                        new ThanhVienBieuQuyetId(daLuu.getBieuQuyetId(), nguoiDungId)))
                .toList());
        luaChonBieuQuyetRepository.saveAll(List.of(
                taoLuaChon(daLuu, "Đồng ý", 1, false),
                taoLuaChon(daLuu, "Không đồng ý", 2, false),
                taoLuaChon(daLuu, "Không ý kiến", 3, true)
        ));

        capNhatRealtime.baoCho(nguoiDangNhapId);
        return daLuu.getBieuQuyetId();
    }

    @Transactional
    public void moBieuQuyet(
            Integer cuocHopId,
            Integer bieuQuyetId,
            Integer nguoiDangNhapId) {

        if (nguoiDangNhapId == null) {
            throw loi(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }
        if (cuocHopId == null || cuocHopId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }
        if (bieuQuyetId == null || bieuQuyetId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST, "Mã biểu quyết không hợp lệ");
        }

        var cuocHopDaKhoa = cuocHopRepository.findByIdForUpdate(cuocHopId);
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopDaKhoa.orElseThrow(() ->
                loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDangNhapId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ người chủ trì được mở biểu quyết");
        }
        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT,
                    "Chỉ được mở biểu quyết khi cuộc họp đang diễn ra");
        }

        BieuQuyet bieuQuyet = timVaKhoaBieuQuyet(cuocHopId, bieuQuyetId);
        if (!"NHAP".equals(bieuQuyet.getTrangThai())) {
            throw loi(HttpStatus.CONFLICT,
                    "Chỉ được mở biểu quyết đang ở trạng thái nháp");
        }

        LocalDateTime hienTai = LocalDateTime.now();
        if (hienTai.isBefore(bieuQuyet.getThoiGianBatDau())
                || !hienTai.isBefore(bieuQuyet.getThoiGianKetThuc())) {
            throw loi(HttpStatus.CONFLICT,
                    "Chỉ được mở biểu quyết trong khung thời gian đã thiết lập");
        }

        long soThanhVienDaChon = thanhVienBieuQuyetRepository
                .demDanhSachBieuQuyet(bieuQuyetId);
        if (soThanhVienDaChon == 0) {
            soThanhVienDaChon = thanhVienBieuQuyetRepository
                    .chotDanhSachBieuQuyet(bieuQuyetId);
        } else {
            thanhVienBieuQuyetRepository.loaiNguoiKhongCoMatKhoiDanhSach(
                    bieuQuyetId, cuocHopId);
            soThanhVienDaChon = thanhVienBieuQuyetRepository
                    .demDanhSachBieuQuyet(bieuQuyetId);
        }
        if (soThanhVienDaChon == 0) {
            throw loi(HttpStatus.CONFLICT,
                    "Chưa có thành viên được điểm danh có mặt và còn ở cuộc họp");
        }

        bieuQuyet.setTrangThai("DANG_MO");
        var nguoiNhan = new HashSet<>(thanhVienBieuQuyetRepository.timNguoiDuocBieuQuyet(bieuQuyetId));
        nguoiNhan.add(nguoiDangNhapId);
        capNhatRealtime.baoCho(nguoiNhan, false);
    }
    
    
    @Transactional
    public void guiPhieuBieuQuyet(
            Integer cuocHopId,
            Integer bieuQuyetId,
            Integer luaChonId,
            Integer nguoiDangNhapId) {

        if (nguoiDangNhapId == null) {
            throw loi(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }
        if (cuocHopId == null || cuocHopId <= 0
                || bieuQuyetId == null || bieuQuyetId <= 0
                || luaChonId == null || luaChonId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST,
                    "Mã cuộc họp, biểu quyết hoặc lựa chọn không hợp lệ");
        }

        var cuocHopDaKhoa = cuocHopRepository.findByIdForUpdate(cuocHopId);
        NguoiDung nguoiBoPhieu = nguoiDungHienTaiService
                .layTaiKhoanHoatDong(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopDaKhoa.orElseThrow(() ->
                loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp không còn diễn ra");
        }

        BieuQuyet bieuQuyet = timVaKhoaBieuQuyet(cuocHopId, bieuQuyetId);

        ThanhVienCuocHopId thanhVienId = new ThanhVienCuocHopId();
        thanhVienId.setCuocHopId(cuocHopId);
        thanhVienId.setNguoiDungId(nguoiDangNhapId);

        ThanhVienCuocHop thanhVien = thanhVienCuocHopRepository
                .findByIdForUpdate(thanhVienId)
                .orElseThrow(() -> loi(HttpStatus.FORBIDDEN,
                        "Bạn không phải thành viên của cuộc họp"));

        if (!"CO_MAT".equals(thanhVien.getTrangThaiThamDu())) {
            throw loi(HttpStatus.FORBIDDEN,
                    "Chỉ thành viên đã được điểm danh có mặt mới được bỏ phiếu");
        }
        if (thanhVien.getThoiGianRoiCuocHop() != null) {
            throw loi(HttpStatus.CONFLICT,
                    "Thành viên đã rời cuộc họp và không thể gửi phiếu");
        }

        LocalDateTime hienTai = LocalDateTime.now();
        if (!"DANG_MO".equals(bieuQuyet.getTrangThai())
                || hienTai.isBefore(bieuQuyet.getThoiGianBatDau())
                || !hienTai.isBefore(bieuQuyet.getThoiGianKetThuc())) {
            throw loi(HttpStatus.CONFLICT, "Biểu quyết hiện không nhận phiếu");
        }

        // Lượt mở trước khi có bảng danh sách vẫn áp dụng điều kiện điểm danh.
        if (!duocThamGiaLuotBieuQuyet(bieuQuyetId, nguoiDangNhapId)) {
            throw loi(HttpStatus.FORBIDDEN,
                    "Bạn không có trong danh sách biểu quyết của lượt này");
        }

        LuaChonBieuQuyet luaChon = luaChonBieuQuyetRepository
                .findByLuaChonIdAndBieuQuyet_BieuQuyetId(luaChonId, bieuQuyetId)
                .orElseThrow(() -> loi(HttpStatus.BAD_REQUEST,
                        "Lựa chọn không thuộc biểu quyết này"));

        PhieuBieuQuyet phieu = new PhieuBieuQuyet();
        phieu.setBieuQuyet(bieuQuyet);
        phieu.setLuaChonId(luaChon.getLuaChonId());

        switch (bieuQuyet.getCheDoBieuQuyet()) {
            case "CONG_KHAI" -> {
                if (phieuBieuQuyetRepository
                        .findFirstByBieuQuyet_BieuQuyetIdAndNguoiDung_NguoiDungId(
                                bieuQuyetId, nguoiDangNhapId)
                        .isPresent()) {
                    throw loi(HttpStatus.CONFLICT, "Bạn đã bỏ phiếu");
                }
                phieu.setNguoiDung(nguoiBoPhieu);
            }
            case "AN_DANH" -> {
                String maPhieu = taoMaPhieuAnDanh(bieuQuyetId, nguoiDangNhapId);
                if (phieuBieuQuyetRepository
                        .findFirstByBieuQuyet_BieuQuyetIdAndMaPhieu(bieuQuyetId, maPhieu)
                        .isPresent()) {
                    throw loi(HttpStatus.CONFLICT, "Bạn đã bỏ phiếu");
                }
                phieu.setMaPhieu(maPhieu);
            }
            default -> throw loi(HttpStatus.CONFLICT, "Chế độ biểu quyết không hợp lệ");
        }

        phieuBieuQuyetRepository.save(phieu);
        capNhatRealtime.baoCho(nguoiDangNhapId);
    }
    
    
    
    @Transactional
    public KetQuaBieuQuyetResponse dongBieuQuyet(
            Integer cuocHopId,
            Integer bieuQuyetId,
            Integer nguoiDangNhapId) {

        if (nguoiDangNhapId == null) {
            throw loi(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }
        if (cuocHopId == null || cuocHopId <= 0
                || bieuQuyetId == null || bieuQuyetId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST, "Mã cuộc họp hoặc mã biểu quyết không hợp lệ");
        }

        var cuocHopDaKhoa = cuocHopRepository.findByIdForUpdate(cuocHopId);
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopDaKhoa.orElseThrow(() ->
                loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDangNhapId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ người chủ trì được đóng biểu quyết");
        }

        BieuQuyet bieuQuyet = timVaKhoaBieuQuyet(cuocHopId, bieuQuyetId);
        if (!"DANG_MO".equals(bieuQuyet.getTrangThai())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ được đóng biểu quyết đang mở");
        }

        chotBieuQuyet(cuocHopId, bieuQuyet);
        return taoKetQuaBieuQuyet(bieuQuyet);
    }
    
    @Transactional
    public void dongBieuQuyetHetHan(Integer cuocHopId, Integer bieuQuyetId) {
        cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"
                ));

        BieuQuyet bieuQuyet = timVaKhoaBieuQuyet(cuocHopId, bieuQuyetId);

        if ("DANG_MO".equals(bieuQuyet.getTrangThai())
                && !LocalDateTime.now().isBefore(bieuQuyet.getThoiGianKetThuc())) {
            chotBieuQuyet(cuocHopId, bieuQuyet);
        }
    }
    
    private void chotBieuQuyet(Integer cuocHopId, BieuQuyet bieuQuyet) {
        long soThanhVienCoMat = thanhVienBieuQuyetRepository
                .demDanhSachBieuQuyet(bieuQuyet.getBieuQuyetId());

        if (soThanhVienCoMat == 0) {
            // Hỗ trợ phiên cũ chưa có danh sách thành viên biểu quyết.
            soThanhVienCoMat = thanhVienCuocHopRepository
                    .countByCuocHop_CuocHopIdAndTrangThaiThamDu(cuocHopId, "CO_MAT");
        }

        bieuQuyet.setSoThanhVienCoMatKhiDong(soThanhVienCoMat);
        bieuQuyet.setTrangThai("DA_DONG");
        Set<Integer> nguoiNhan = new HashSet<>();
        for (var thanhVien : thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)) {
            nguoiNhan.add(thanhVien.getId().getNguoiDungId());
        }
        NguoiDung chuTri = bieuQuyet.getCuocHop().getNguoiChuTri();
        if (chuTri != null) {
            nguoiNhan.add(chuTri.getNguoiDungId());
        }
        capNhatRealtime.baoCho(nguoiNhan, false);
    }
    
    @Transactional(readOnly = true)
    public List<BieuQuyetThamDuResponse> layDanhSachBieuQuyetDaDongCuaToi(
            Integer nguoiDangNhapId, Integer cuocHopId
    ) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);

        return bieuQuyetRepository
                .timBieuQuyetDaDongCuaThanhVien(cuocHopId, nguoiDangNhapId)
                .stream()
                .map(bq -> new BieuQuyetThamDuResponse(
                        bq.getCuocHop().getCuocHopId(),
                        bq.getCuocHop().getTieuDe(),
                        bq.getBieuQuyetId(),
                        bq.getTieuDe(), bq.getThoiGianBatDau(), bq.getThoiGianKetThuc()
                ))
                .toList();
    }
    
    @Transactional(readOnly = true)
    public List<BieuQuyetTomTatResponse> layDanhSachBieuQuyetCuaChuTri(
            Integer cuocHopId, Integer nguoiDangNhapId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);

        if (cuocHopId == null || cuocHopId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }

        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDangNhapId)) {
            throw loi(HttpStatus.FORBIDDEN,
                    "Chỉ chủ trì được xem danh sách quản lý biểu quyết");
        }

        return bieuQuyetRepository
                .findByCuocHop_CuocHopIdOrderByBieuQuyetIdDesc(cuocHopId)
                .stream()
                .map(muc -> new BieuQuyetTomTatResponse(
                        muc.getBieuQuyetId(),
                        muc.getTieuDe(),
                        muc.getTrangThai(),
                        muc.getThoiGianBatDau(),
                        muc.getThoiGianKetThuc()))
                .toList();
    }
    
    @Transactional(readOnly = true)
    public ChiTietBieuQuyetResponse layChiTietBieuQuyet(
            Integer cuocHopId,
            Integer bieuQuyetId,
            Integer nguoiDangNhapId
    ) {
        BieuQuyet bieuQuyet = timBieuQuyetDuocPhepXem(
                cuocHopId, bieuQuyetId, nguoiDangNhapId
        );

        List<LuaChonResponse> luaChon = luaChonBieuQuyetRepository
                .findByBieuQuyet_BieuQuyetIdOrderByThuTuHienThiAsc(bieuQuyetId)
                .stream()
                .map(lc -> new LuaChonResponse(
                        lc.getLuaChonId(),
                        lc.getNoiDungLuaChon()
                ))
                .toList();

        return new ChiTietBieuQuyetResponse(
                bieuQuyet.getBieuQuyetId(),
                bieuQuyet.getTieuDe(),
                bieuQuyet.getNoiDung(),
                bieuQuyet.getCheDoBieuQuyet(),
                bieuQuyet.getTrangThai(),
                bieuQuyet.getThoiGianBatDau(),
                bieuQuyet.getThoiGianKetThuc(),
                luaChon
        );
    }

    @Transactional(readOnly = true)
    public KetQuaBieuQuyetResponse layKetQuaBieuQuyet(
            Integer cuocHopId,
            Integer bieuQuyetId,
            Integer nguoiDangNhapId
    ) {
        BieuQuyet bieuQuyet = timBieuQuyetDuocPhepXem(
                cuocHopId, bieuQuyetId, nguoiDangNhapId
        );

        if (!"DA_DONG".equals(bieuQuyet.getTrangThai())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ được xem kết quả sau khi đóng biểu quyết");
        }

        return taoKetQuaBieuQuyet(bieuQuyet);
    }
    
    @Transactional(readOnly = true)
    public List<BieuQuyetThamDuResponse> layDanhSachBieuQuyetThamDu(
            Integer nguoiDangNhapId, Integer cuocHopId
    ) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);

        return bieuQuyetRepository
                .timBieuQuyetDangMoCuaThanhVien(cuocHopId, nguoiDangNhapId, LocalDateTime.now())
                .stream()
                .filter(bq -> ThaoTacCuocHop.dangDienRa(bq.getCuocHop()))
                .filter(bq -> duocThamGiaLuotBieuQuyet(
                        bq.getBieuQuyetId(), nguoiDangNhapId
                ))
                .map(bq -> new BieuQuyetThamDuResponse(
                        bq.getCuocHop().getCuocHopId(),
                        bq.getCuocHop().getTieuDe(),
                        bq.getBieuQuyetId(),
                        bq.getTieuDe(), bq.getThoiGianBatDau(), bq.getThoiGianKetThuc()
                ))
                .toList();
    }

    private boolean duocThamGiaLuotBieuQuyet(
            Integer bieuQuyetId, Integer nguoiDungId
    ) {
        return thanhVienBieuQuyetRepository.demThanhVienTrongLuot(
                bieuQuyetId, nguoiDungId
        ) > 0 || thanhVienBieuQuyetRepository.demDanhSachBieuQuyet(bieuQuyetId) == 0;
    }
    

    private void kiemTraDuLieu(TaoBieuQuyetRequest request) {
        if (request == null
                || request.tieuDe() == null
                || request.tieuDe().isBlank()
                || request.tieuDe().strip().length() > 255) {
            throw loi(
                    HttpStatus.BAD_REQUEST,
                    "Tiêu đề biểu quyết phải dài từ 1 đến 255 ký tự"
            );
        }

        if (!"CONG_KHAI".equals(request.cheDoBieuQuyet())
                && !"AN_DANH".equals(request.cheDoBieuQuyet())) {
            throw loi(
                    HttpStatus.BAD_REQUEST,
                    "Chế độ biểu quyết phải là CONG_KHAI hoặc AN_DANH"
            );
        }

        if (request.thoiGianBatDau() == null
                || request.thoiGianKetThuc() == null
                || !request.thoiGianKetThuc().isAfter(request.thoiGianBatDau())) {
            throw loi(
                    HttpStatus.BAD_REQUEST,
                    "Thời gian kết thúc phải sau thời gian bắt đầu"
            );
        }
    }

    private LuaChonBieuQuyet taoLuaChon(
            BieuQuyet bieuQuyet,
            String noiDung,
            int thuTu,
            boolean laLuaChonTrangThai
    ) {
        LuaChonBieuQuyet luaChon = new LuaChonBieuQuyet();
        luaChon.setBieuQuyet(bieuQuyet);
        luaChon.setNoiDungLuaChon(noiDung);
        luaChon.setThuTuHienThi(thuTu);
        luaChon.setLaLuaChonTrangThai(laLuaChonTrangThai);
        return luaChon;
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }

    private String taoMaPhieuAnDanh(Integer bieuQuyetId, Integer nguoiDungId) {
        byte[] khoa;

        try {
            khoa = Base64.getDecoder().decode(khoaPhieuBase64.strip());
        } catch (IllegalArgumentException ex) {
            throw loi(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Khóa mã phiếu ẩn danh phải có định dạng Base64"
            );
        }

        if (khoa.length < 32) {
            throw loi(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Chưa cấu hình khóa hợp lệ cho phiếu ẩn danh"
            );
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(khoa, "HmacSHA256"));
            String duLieu = "bieu-quyet:" + bieuQuyetId + ":" + nguoiDungId;
            byte[] ma = mac.doFinal(duLieu.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(ma);
        } catch (GeneralSecurityException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể tạo mã phiếu ẩn danh",
                    ex
            );
        }
    }

    private BieuQuyet timVaKhoaBieuQuyet(Integer cuocHopId, Integer bieuQuyetId) {
        return bieuQuyetRepository.findByIdAndCuocHopIdForUpdate(bieuQuyetId, cuocHopId)
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biểu quyết thuộc cuộc họp này"
                ));
    }

    private BieuQuyet timBieuQuyetDuocPhepXem(
            Integer cuocHopId,
            Integer bieuQuyetId,
            Integer nguoiDangNhapId
    ) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);

        if (cuocHopId == null || cuocHopId <= 0
                || bieuQuyetId == null || bieuQuyetId <= 0) {
            throw loi(
                    HttpStatus.BAD_REQUEST,
                    "Mã cuộc họp hoặc mã biểu quyết không hợp lệ"
            );
        }
        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"
                ));
        boolean laChuTri = ThaoTacCuocHop.laNguoiChuTri(
                cuocHop, nguoiDangNhapId
        );

        if (!laChuTri && !thanhVienCuocHopRepository
                .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungId(
                        cuocHopId, nguoiDangNhapId
                )) {
            throw loi(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem biểu quyết của cuộc họp này"
            );
        }
        BieuQuyet bieuQuyet = bieuQuyetRepository.findById(bieuQuyetId)
                .filter(bq -> cuocHopId.equals(bq.getCuocHop().getCuocHopId()))
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy biểu quyết thuộc cuộc họp này"
                ));

        if ("NHAP".equals(bieuQuyet.getTrangThai()) && !laChuTri) {
            throw loi(
                    HttpStatus.FORBIDDEN,
                    "Chỉ người chủ trì được xem biểu quyết nháp"
            );
        }

        return bieuQuyet;
    }

    private KetQuaBieuQuyetResponse taoKetQuaBieuQuyet(BieuQuyet bieuQuyet) {
        Integer bieuQuyetId = bieuQuyet.getBieuQuyetId();
        Map<Integer, Long> soPhieuTheoLuaChon = new HashMap<>();
        for (var dong : phieuBieuQuyetRepository.demPhieuTheoLuaChon(bieuQuyetId)) {
            soPhieuTheoLuaChon.put(dong.getLuaChonId(), dong.getSoPhieu());
        }
        List<KetQuaLuaChon> ketQuaLuaChon = luaChonBieuQuyetRepository
                .findByBieuQuyet_BieuQuyetIdOrderByThuTuHienThiAsc(bieuQuyetId)
                .stream()
                .map(luaChon -> new KetQuaLuaChon(
                        luaChon.getLuaChonId(),
                        luaChon.getNoiDungLuaChon(),
                        soPhieuTheoLuaChon.getOrDefault(luaChon.getLuaChonId(), 0L)
                ))
                .toList();
        long tongSoPhieu = ketQuaLuaChon.stream()
                .mapToLong(KetQuaLuaChon::soPhieu)
                .sum();
        long soNguoiDuocBieuQuyet = thanhVienBieuQuyetRepository
                .demDanhSachBieuQuyet(bieuQuyetId);
        long soPhieuCaoNhat = ketQuaLuaChon.stream()
                .mapToLong(KetQuaLuaChon::soPhieu)
                .max()
                .orElse(0);
        List<KetQuaLuaChon> luaChonDatCaoNhat = ketQuaLuaChon.stream()
                .filter(luaChon -> luaChon.soPhieu() == soPhieuCaoNhat)
                .toList();
        Integer luaChonThangId = tongSoPhieu == 0 || luaChonDatCaoNhat.size() != 1
                ? null : luaChonDatCaoNhat.get(0).luaChonId();
        return new KetQuaBieuQuyetResponse(
                bieuQuyetId,
                bieuQuyet.getTrangThai(),
                soNguoiDuocBieuQuyet,
                tongSoPhieu,
                luaChonThangId,
                tongSoPhieu > 0 && luaChonDatCaoNhat.size() > 1,
                ketQuaLuaChon
        );
    }
}
