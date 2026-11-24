package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
    private final NghiQuyetService nghiQuyetService;
    private final CuocHopRepository cuocHopRepository;
    private final BieuQuyetRepository bieuQuyetRepository;
    private final LuaChonBieuQuyetRepository luaChonBieuQuyetRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhieuBieuQuyetRepository phieuBieuQuyetRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final ThanhVienBieuQuyetRepository thanhVienBieuQuyetRepository;
    
    @Value("${app.bieu-quyet.khoa-phieu:}")
    private String khoaPhieuBase64;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer taoBieuQuyet(
            Integer cuocHopId,
            TaoBieuQuyetRequest request,
            Integer nguoiDangNhapId
    ) {
        return luuBieuQuyet(cuocHopId, null, request, nguoiDangNhapId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void capNhatBieuQuyet(Integer cuocHopId, Integer id, TaoBieuQuyetRequest request, Integer nguoiDangNhapId) {
        luuBieuQuyet(cuocHopId, id, request, nguoiDangNhapId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void xoaBieuQuyet(Integer cuocHopId, Integer id, Integer nguoiDangNhapId) {
        CuocHop cuocHop = khoaCuocHopDeLuu(cuocHopId, nguoiDangNhapId);
        kiemTraCuocHopChoPhepLuu(cuocHop);
        BieuQuyet bieuQuyet = khoaBanNhap(cuocHopId, id);
        xoaCauHinh(id);
        bieuQuyetRepository.delete(bieuQuyet);
        if (bieuQuyet.getNghiQuyetId() != null) {
            nghiQuyetService.goGanBieuQuyetNhap(cuocHopId, bieuQuyet.getNghiQuyetId());
            capNhatRealtime.baoCuocHop(cuocHop, nguoiDangNhapId);
        } else {
            capNhatRealtime.baoCho(nguoiDangNhapId);
        }
    }

    private CuocHop khoaCuocHopDeLuu(Integer cuocHopId, Integer nguoiDangNhapId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDangNhapId);
        if (cuocHopId == null || cuocHopId <= 0) {
            throw loi(HttpStatus.BAD_REQUEST, "Mã cuộc họp không hợp lệ");
        }
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDangNhapId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ chủ trì được quản lý biểu quyết");
        }
        return cuocHop;
    }

    private void kiemTraCuocHopChoPhepLuu(CuocHop cuocHop) {
        if (!ThaoTacCuocHop.coTheLapLich(cuocHop) && !ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Chỉ được quản lý bản nháp sau khi kế hoạch đã duyệt và trước khi họp kết thúc");
        }
    }

    private BieuQuyet khoaBanNhap(Integer cuocHopId, Integer id) {
        BieuQuyet bieuQuyet = timVaKhoaBieuQuyet(cuocHopId, id);
        if (!"NHAP".equals(bieuQuyet.getTrangThai())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ sửa hoặc xóa biểu quyết còn ở bản nháp");
        }
        return bieuQuyet;
    }

    private void xoaCauHinh(Integer id) {
        thanhVienBieuQuyetRepository.xoaTheoBieuQuyet(id);
        luaChonBieuQuyetRepository.xoaTheoBieuQuyet(id);
    }

    private Integer luuBieuQuyet(Integer cuocHopId, Integer id, TaoBieuQuyetRequest request, Integer nguoiDangNhapId) {
        kiemTraDuLieu(request);
        boolean moNgay = Boolean.TRUE.equals(request.moNgay());
        if (moNgay && (id != null || request.thoiLuongPhut() == null)) {
            throw loi(HttpStatus.BAD_REQUEST, "Tạo và mở cần một phiên mới có thời lượng");
        }
        CuocHop cuocHop = khoaCuocHopDeLuu(cuocHopId, nguoiDangNhapId);
        kiemTraCuocHopChoPhepLuu(cuocHop);
        if (moNgay && !ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Chỉ mở biểu quyết khi cuộc họp đang diễn ra");
        }
        String phuongThuc = request.phuongThuc() == null ? "TAN_THANH" : request.phuongThuc();
        List<String> phuongAn = chuanHoaPhuongAn(request, phuongThuc);
        List<Integer> nguoiDuocBieuQuyetIds = request.nguoiDuocBieuQuyetIds();
        if (nguoiDuocBieuQuyetIds == null || nguoiDuocBieuQuyetIds.isEmpty()
                || nguoiDuocBieuQuyetIds.stream().anyMatch(nguoiId -> nguoiId == null || nguoiId <= 0)
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
        BieuQuyet bieuQuyet = id == null ? new BieuQuyet() : khoaBanNhap(cuocHopId, id);
        bieuQuyet.setCuocHop(cuocHop);
        if (id == null) {
            bieuQuyet.setNguoiTao(cuocHop.getNguoiChuTri());
        } else {
            if (!Objects.equals(bieuQuyet.getNghiQuyetId(), request.nghiQuyetId())) {
                throw loi(HttpStatus.CONFLICT, "Không đổi liên kết nghị quyết khi sửa bản nháp");
            }
            xoaCauHinh(id);
        }
        if (request.nghiQuyetId() == null) {
            bieuQuyet.setTieuDe(request.tieuDe().strip());
            bieuQuyet.setNoiDung(request.noiDung() == null ? null : request.noiDung().strip());
        } else if (id == null) {
            var nghiQuyet = nghiQuyetService.ganVaoBieuQuyet(cuocHopId, request.nghiQuyetId());
            bieuQuyet.setNghiQuyetId(nghiQuyet.nghiQuyetId());
            bieuQuyet.setTaiLieuNghiQuyetId(nghiQuyet.taiLieuId());
            bieuQuyet.setTieuDe(nghiQuyet.tieuDe());
            bieuQuyet.setNoiDung(nghiQuyet.noiDung());
        }
        bieuQuyet.setPhuongThuc(phuongThuc);
        bieuQuyet.setCheDoBieuQuyet(request.cheDoBieuQuyet());
        bieuQuyet.setChoPhepTrangThai("TAN_THANH".equals(phuongThuc));
        bieuQuyet.setTyLeThamGiaToiThieu(null);
        bieuQuyet.setThoiLuongPhut(request.thoiLuongPhut());
        if (request.thoiLuongPhut() == null) {
            bieuQuyet.setThoiGianBatDau(request.thoiGianBatDau());
            bieuQuyet.setThoiGianKetThuc(request.thoiGianKetThuc());
        } else {
            // Hai cột thời gian bắt buộc trong schema. Bản nháp được tính lại khi mở.
            capNhatKhoangGio(bieuQuyet, LocalDateTime.now());
        }
        bieuQuyet.setTrangThai("NHAP");
        BieuQuyet daLuu = bieuQuyetRepository.save(bieuQuyet);
        thanhVienBieuQuyetRepository.saveAll(nguoiDuocBieuQuyetIds.stream()
                .map(nguoiDungId -> new com.example.demo.entity.ThanhVienBieuQuyet(
                        new ThanhVienBieuQuyetId(daLuu.getBieuQuyetId(), nguoiDungId)))
                .toList());
        luaChonBieuQuyetRepository.saveAll(java.util.stream.IntStream.range(0, phuongAn.size())
                .mapToObj(index -> taoLuaChon(daLuu, phuongAn.get(index), index + 1,
                        "TAN_THANH".equals(phuongThuc) && index == 2))
                .toList());

        if (moNgay) {
            moBanNhap(cuocHopId, daLuu, nguoiDangNhapId);
        } else if (bieuQuyet.getNghiQuyetId() != null) {
            capNhatRealtime.baoCuocHop(cuocHop, nguoiDangNhapId);
        } else {
            capNhatRealtime.baoCho(nguoiDangNhapId);
        }
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

        moBanNhap(cuocHopId, khoaBanNhap(cuocHopId, bieuQuyetId), nguoiDangNhapId);
    }

    private void moBanNhap(Integer cuocHopId, BieuQuyet bieuQuyet, Integer nguoiDangNhapId) {
        Integer bieuQuyetId = bieuQuyet.getBieuQuyetId();
        LocalDateTime hienTai = LocalDateTime.now();
        if (bieuQuyet.getThoiLuongPhut() == null && (hienTai.isBefore(bieuQuyet.getThoiGianBatDau())
                || !hienTai.isBefore(bieuQuyet.getThoiGianKetThuc()))) {
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

        if (bieuQuyet.getThoiLuongPhut() != null) {
            capNhatKhoangGio(bieuQuyet, LocalDateTime.now());
        }
        bieuQuyet.setTrangThai("DANG_MO");
        var nguoiNhan = new HashSet<>(thanhVienBieuQuyetRepository.timNguoiDuocBieuQuyet(bieuQuyetId));
        nguoiNhan.add(nguoiDangNhapId);
        capNhatRealtime.baoCho(nguoiNhan, false);
    }
    
    
    @Transactional
    public void guiPhieuBieuQuyet(Integer cuocHopId, Integer bieuQuyetId, Integer luaChonId,
            String lyDoKhongDongY, Integer nguoiDangNhapId) {
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
                .orElseThrow(() -> loi(HttpStatus.BAD_REQUEST, "Lựa chọn không thuộc biểu quyết này"));
        boolean laPhieuCoKhong = "TAN_THANH".equals(bieuQuyet.getPhuongThuc());
        if (laPhieuCoKhong && !List.of("Đồng ý", "Không đồng ý", "Phiếu trắng", "Không ý kiến").contains(luaChon.getNoiDungLuaChon())) {
            throw loi(HttpStatus.BAD_REQUEST, "Phiếu này chỉ nhận Đồng ý, Không đồng ý hoặc Phiếu trắng");
        }
        PhieuBieuQuyet phieu = new PhieuBieuQuyet();
        phieu.setBieuQuyet(bieuQuyet);
        phieu.setLuaChonId(luaChon.getLuaChonId());
        if (bieuQuyet.getNghiQuyetId() != null && laPhieuCoKhong
                && "Không đồng ý".equals(luaChon.getNoiDungLuaChon())) {
            if (lyDoKhongDongY == null || lyDoKhongDongY.isBlank() || lyDoKhongDongY.strip().length() > 500) {
                throw loi(HttpStatus.BAD_REQUEST, "Nhập lý do không đồng ý tối đa 500 ký tự");
            }
            phieu.setLyDoKhongDongY(lyDoKhongDongY.strip());
        }

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
        return taoKetQuaBieuQuyet(bieuQuyet, true);
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
        if (bieuQuyet.getNghiQuyetId() != null) {
            var ketQua = taoKetQuaBieuQuyet(bieuQuyet, false);
            if ("TAN_THANH".equals(bieuQuyet.getPhuongThuc())) {
                bieuQuyet.setKetQuaNghiQuyet(nghiQuyetService.chotBieuQuyet(bieuQuyet.getNghiQuyetId(),
                        ketQua.luaChon().get(0).soPhieu(), ketQua.luaChon().get(1).soPhieu(), soThanhVienCoMat));
            } else {
                long soPhieuDanDau = ketQua.luaChon().stream()
                        .filter(pa -> pa.luaChonId().equals(ketQua.luaChonThangId()))
                        .mapToLong(KetQuaLuaChon::soPhieu)
                        .findFirst().orElse(0);
                bieuQuyet.setKetQuaNghiQuyet(nghiQuyetService.chotBieuQuyetPhuongAn(
                        bieuQuyet.getNghiQuyetId(), soPhieuDanDau > soThanhVienCoMat / 2));
            }
        }
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
                        bq.getTieuDe(), bq.getThoiGianBatDau(), bq.getThoiGianKetThuc(), bq.getNghiQuyetId()
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
                        muc.getThoiGianKetThuc(),
                        muc.getNghiQuyetId(),
                        muc.getPhuongThuc(),
                        muc.getThoiLuongPhut()))
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
                bieuQuyet.getNghiQuyetId(),
                bieuQuyet.getTaiLieuNghiQuyetId(),
                luaChon,
                bieuQuyet.getPhuongThuc(),
                "NHAP".equals(bieuQuyet.getTrangThai())
                        ? thanhVienBieuQuyetRepository.timNguoiDuocBieuQuyet(bieuQuyetId) : List.of(),
                bieuQuyet.getThoiLuongPhut()
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

        return taoKetQuaBieuQuyet(bieuQuyet, ThaoTacCuocHop.laNguoiChuTri(bieuQuyet.getCuocHop(), nguoiDangNhapId));
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
                        bq.getTieuDe(), bq.getThoiGianBatDau(), bq.getThoiGianKetThuc(), bq.getNghiQuyetId()
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
    

    private void capNhatKhoangGio(BieuQuyet bieuQuyet, LocalDateTime batDau) {
        bieuQuyet.setThoiGianBatDau(batDau);
        bieuQuyet.setThoiGianKetThuc(batDau.plusMinutes(bieuQuyet.getThoiLuongPhut()));
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

        if (request.thoiLuongPhut() != null) {
            if (request.thoiLuongPhut() <= 0) {
                throw loi(HttpStatus.BAD_REQUEST, "Thời lượng phải là số phút nguyên lớn hơn 0");
            }
            return;
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

    private List<String> chuanHoaPhuongAn(TaoBieuQuyetRequest request, String phuongThuc) {
        if ("TAN_THANH".equals(phuongThuc)) {
            return List.of("Đồng ý", "Không đồng ý", "Phiếu trắng");
        }
        if (!"CHON_MOT".equals(phuongThuc)) {
            throw loi(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ Có / Không hoặc chọn một phương án");
        }
        if (!"CONG_KHAI".equals(request.cheDoBieuQuyet())) {
            throw loi(HttpStatus.BAD_REQUEST, "Chọn phương án chỉ hỗ trợ chế độ công khai");
        }
        List<String> danhSach = request.phuongAn();
        if (danhSach == null || danhSach.size() < 2 || danhSach.size() > 20
                || danhSach.stream().anyMatch(Objects::isNull)) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập từ 2 đến 20 phương án");
        }
        List<String> phuongAn = danhSach.stream()
                .map(pa -> Normalizer.normalize(pa.strip(), Normalizer.Form.NFC)).toList();
        if (phuongAn.stream().anyMatch(pa -> pa.isBlank() || pa.length() > 255
                || pa.indexOf('\n') >= 0 || pa.indexOf('\r') >= 0)) {
            throw loi(HttpStatus.BAD_REQUEST, "Mỗi phương án phải có nội dung trên một dòng, tối đa 255 ký tự");
        }
        if (phuongAn.stream().map(pa -> pa.toLowerCase(Locale.ROOT)).distinct().count() != phuongAn.size()) {
            throw loi(HttpStatus.BAD_REQUEST, "Các phương án phải khác nhau");
        }
        return phuongAn;
    }

    private LuaChonBieuQuyet taoLuaChon(
            BieuQuyet bieuQuyet,
            String noiDung,
            int thuTu,
            boolean laPhieuTrang
    ) {
        LuaChonBieuQuyet luaChon = new LuaChonBieuQuyet();
        luaChon.setBieuQuyet(bieuQuyet);
        luaChon.setNoiDungLuaChon(noiDung);
        luaChon.setThuTuHienThi(thuTu);
        luaChon.setLaLuaChonTrangThai(laPhieuTrang);
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

    @Transactional(readOnly = true)
    public LuuTaiLieuCuocHopService.TepTaiLieu taiLieuNghiQuyet(Integer cuocHopId, Integer bieuQuyetId, Integer nguoiDungId) {
        var bieuQuyet = timBieuQuyetDuocPhepXem(cuocHopId, bieuQuyetId, nguoiDungId);
        return nghiQuyetService.docTep(cuocHopId, bieuQuyet.getTaiLieuNghiQuyetId());
    }

    private KetQuaBieuQuyetResponse taoKetQuaBieuQuyet(BieuQuyet bieuQuyet, boolean xemLyDo) {
        Integer bieuQuyetId = bieuQuyet.getBieuQuyetId();
        Map<Integer, Long> soPhieuTheoLuaChon = new HashMap<>();
        for (var dong : phieuBieuQuyetRepository.demPhieuTheoLuaChon(bieuQuyetId)) {
            soPhieuTheoLuaChon.put(dong.getLuaChonId(), dong.getSoPhieu());
        }
        // Chỉ dùng để đọc dữ liệu nếu bản thử nghiệm chọn nhiều đã được cài trước đó.
        if ("CHON_NHIEU".equals(bieuQuyet.getPhuongThuc())) {
            for (var dong : phieuBieuQuyetRepository.demPhuongAnBoSung(bieuQuyetId)) {
                soPhieuTheoLuaChon.merge(dong.getLuaChonId(), dong.getSoPhieu(), Long::sum);
            }
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
        long tongSoPhieu = phieuBieuQuyetRepository.countByBieuQuyet_BieuQuyetId(bieuQuyetId);
        long soNguoiDuocBieuQuyet = thanhVienBieuQuyetRepository
                .demDanhSachBieuQuyet(bieuQuyetId);
        List<KetQuaLuaChon> luaChonCoTheDanDau = "TAN_THANH".equals(bieuQuyet.getPhuongThuc())
                ? ketQuaLuaChon.stream().filter(muc -> !"Phiếu trắng".equals(muc.noiDung())
                        && !"Không ý kiến".equals(muc.noiDung())).toList()
                : ketQuaLuaChon;
        long soPhieuCaoNhat = luaChonCoTheDanDau.stream()
                .mapToLong(KetQuaLuaChon::soPhieu)
                .max()
                .orElse(0);
        List<KetQuaLuaChon> luaChonDatCaoNhat = luaChonCoTheDanDau.stream()
                .filter(luaChon -> luaChon.soPhieu() == soPhieuCaoNhat)
                .toList();
        Integer luaChonThangId = soPhieuCaoNhat == 0 || luaChonDatCaoNhat.size() != 1
                ? null : luaChonDatCaoNhat.get(0).luaChonId();
        return new KetQuaBieuQuyetResponse(
                bieuQuyetId,
                bieuQuyet.getTrangThai(),
                soNguoiDuocBieuQuyet,
                tongSoPhieu,
                luaChonThangId,
                soPhieuCaoNhat > 0 && luaChonDatCaoNhat.size() > 1,
                ketQuaLuaChon,
                bieuQuyet.getKetQuaNghiQuyet(),
                xemLyDo && bieuQuyet.getNghiQuyetId() != null
                        ? phieuBieuQuyetRepository.layLyDoKhongDongY(bieuQuyetId) : List.of()
        );
    }
}
