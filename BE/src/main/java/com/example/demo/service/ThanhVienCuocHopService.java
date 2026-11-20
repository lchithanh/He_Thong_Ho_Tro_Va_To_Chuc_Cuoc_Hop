package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.response.ThanhVienCuocHopResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;
import lombok.RequiredArgsConstructor;
import com.example.demo.dto.response.CuocHopDangDienRaResponse;

import com.example.demo.dto.response.LoiMoiChoPhanHoiResponse;
import com.example.demo.dto.response.NguoiCoTheMoiResponse;
@Service
@RequiredArgsConstructor
public class ThanhVienCuocHopService {
    private final CuocHopRepository cuocHopRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;

    @Transactional
    public void moiThanhVien(Integer cuocHopId, Integer nguoiDuocMoiId, Integer thuKyId) {
        moiDanhSachThanhVien(cuocHopId, java.util.Collections.singletonList(nguoiDuocMoiId), thuKyId);
    }

    @Transactional
    public void moiDanhSachThanhVien(Integer cuocHopId, List<Integer> nguoiDuocMoiIds, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        if (nguoiDuocMoiIds == null || nguoiDuocMoiIds.isEmpty()
                || nguoiDuocMoiIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw loi(HttpStatus.BAD_REQUEST, "Vui lòng chọn người tham dự hợp lệ");
        }
        CuocHop ch = khoaCuocHop(cuocHopId);
        kiemTraThuKy(cuocHopId, thuKyId);
        if (!coLichKeHoachDaDuyet(ch)) {
            throw loi(HttpStatus.CONFLICT,
                    "Cần kế hoạch đã duyệt và lịch gắn với bản kế hoạch đó đã xác nhận trước khi mời");
        }
        for (Integer nguoiDuocMoiId : new java.util.LinkedHashSet<>(nguoiDuocMoiIds)) {
            themThanhVienDuocMoi(ch, nguoiDuocMoiId);
        }
    }

    private void themThanhVienDuocMoi(CuocHop ch, Integer nguoiDuocMoiId) {
        NguoiDung nd = nguoiDungRepository.findById(nguoiDuocMoiId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy người được mời"));
        if (!NguoiDungHienTaiService.dangHoatDong(nd)) throw loi(HttpStatus.CONFLICT, "Tài khoản được mời không hoạt động");
        ThanhVienCuocHopId id = taoId(ch.getCuocHopId(), nguoiDuocMoiId);
        if (thanhVienCuocHopRepository.existsById(id)) throw loi(HttpStatus.CONFLICT, "Người dùng đã có trong cuộc họp");
        ThanhVienCuocHop tv = new ThanhVienCuocHop();
        tv.setId(id); tv.setCuocHop(ch); tv.setNguoiDung(nd);
        tv.setVaiTroTaiCuocHop("THAM_DU");
        tv.setTrangThaiMoi("CHO_PHAN_HOI");
        tv.setTrangThaiThamDu("CHUA_DIEM_DANH");
        thanhVienCuocHopRepository.themMoi(tv);
    }

    @Transactional(readOnly = true)
    public List<ThanhVienCuocHopResponse> layDanhSachThanhVien(Integer cuocHopId, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        if (cuocHopId == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
        CuocHop ch = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!phanCongXuLyKeHoachService.laThuKyThamGia(cuocHopId, thuKyId)
                && !ThaoTacCuocHop.laNguoiChuTri(ch, thuKyId)
                && !ThaoTacCuocHop.laNguoiDeXuat(ch, thuKyId)) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn không có quyền xem danh sách thành viên cuộc họp này");
        }
        return thanhVienCuocHopRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream().map(tv -> new ThanhVienCuocHopResponse(tv.getId().getNguoiDungId(),
                        tv.getNguoiDung().getHoTen(), tv.getNguoiDung().getChucVu(),
                        tv.getNguoiDung().getPhongBan() == null
                                ? null : tv.getNguoiDung().getPhongBan().getTenPhongBan(),
                        tv.getVaiTroTaiCuocHop(),
                        tv.getTrangThaiMoi(), tv.getTrangThaiThamDu(),
                        tv.getThoiGianDiemDanh(), tv.getThoiGianRoiCuocHop())).toList();
    }

    @Transactional(readOnly = true)
    public List<NguoiCoTheMoiResponse> layNguoiCoTheMoi(
            Integer cuocHopId, Integer thuKyId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);

        if (cuocHopId == null) {
            throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
        }

        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));

        kiemTraThuKy(cuocHopId, thuKyId);
        kiemTraDieuKienMoi(cuocHop);

        Set<Integer> idsDaThamGia = thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream()
                .map(thanhVien -> thanhVien.getId().getNguoiDungId())
                .collect(Collectors.toSet());

        return nguoiDungRepository.findAll()
                .stream()
                .filter(NguoiDungHienTaiService::dangHoatDong)
                .filter(nguoiDung -> !idsDaThamGia.contains(nguoiDung.getNguoiDungId()))
                .map(nguoiDung -> new NguoiCoTheMoiResponse(
                        nguoiDung.getNguoiDungId(),
                        nguoiDung.getHoTen(),
                        nguoiDung.getPhongBan() == null
                                ? null
                                : nguoiDung.getPhongBan().getTenPhongBan(),
                        nguoiDung.getChucVu()))
                .sorted(Comparator.comparing(NguoiCoTheMoiResponse::hoTen))
                .toList();
    }
    
    private void kiemTraDieuKienMoi(CuocHop cuocHop) {
        if (!coLichKeHoachDaDuyet(cuocHop)) {
            throw loi(HttpStatus.CONFLICT,
                    "Cần kế hoạch đã duyệt và lịch gắn với bản kế hoạch đó đã xác nhận trước khi mời");
        }
    }

	@Transactional
    public void phanHoiLoiMoi(Integer cuocHopId, Integer nguoiDungId, boolean thamDu) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop ch = khoaCuocHop(cuocHopId);
        if (!coLichKeHoachDaDuyet(ch)) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp không còn nhận phản hồi lời mời");
        }
        ThanhVienCuocHop tv = thanhVienCuocHopRepository
                .findByIdForUpdate(taoId(cuocHopId, nguoiDungId))
                .orElse(null);
        if (tv == null || !"THAM_DU".equals(tv.getVaiTroTaiCuocHop())) {
            throw loi(HttpStatus.NOT_FOUND, "Không tìm thấy lời mời của bạn");
        }
        if (!"CHO_PHAN_HOI".equals(tv.getTrangThaiMoi())) throw loi(HttpStatus.CONFLICT, "Lời mời đã được phản hồi");
        tv.setTrangThaiMoi(thamDu ? "DA_XAC_NHAN" : "TU_CHOI");
    }

	@Transactional(readOnly = true)
	public List<LoiMoiChoPhanHoiResponse> layLoiMoiChoPhanHoi(Integer nguoiDungId) {
	    nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

	    return thanhVienCuocHopRepository
	            .findById_NguoiDungIdAndVaiTroTaiCuocHopAndTrangThaiMoi(
	                    nguoiDungId, "THAM_DU", "CHO_PHAN_HOI")
	            .stream()
	            .map(ThanhVienCuocHop::getCuocHop)
	            .filter(ThaoTacCuocHop::coTheLapLich)
	            .filter(this::coLichKeHoachDaDuyet)
	            .map(this::chuyenLoiMoi)
	            .toList();
	}
	
    private LoiMoiChoPhanHoiResponse chuyenLoiMoi(CuocHop cuocHop) {
        YeuCauPheDuyet yeuCau = layKeHoachMoiNhat(cuocHop.getCuocHopId());
        var lich = yeuCau == null ? null : yeuCau.getLichHop();
        if (lich == null || !ThaoTacLichHop.daXacNhan(lich)) {
            throw loi(HttpStatus.CONFLICT,
                    "Lịch gắn với kế hoạch mới nhất chưa được xác nhận");
        }
        return new LoiMoiChoPhanHoiResponse(cuocHop.getCuocHopId(), cuocHop.getMaCuocHop(),
                cuocHop.getTieuDe(), lich.getThoiGianBatDau(), lich.getThoiGianKetThuc(),
                lich.getPhongHop() == null ? null : lich.getPhongHop().getTenPhongHop(),
                cuocHop.getLoaiHinh(),
                cuocHop.getNguoiChuTri() == null ? null : cuocHop.getNguoiChuTri().getHoTen(),
                cuocHop.getMucTieu(), cuocHop.getNoiDungDuKien());
    }

    private boolean coLichKeHoachDaDuyet(CuocHop cuocHop) {
        return ThaoTacCuocHop.coTheLapLich(cuocHop)
                && ThaoTacCuocHop.coLichMoiThamDu(cuocHop, layKeHoachMoiNhat(cuocHop.getCuocHopId()));
    }

    private YeuCauPheDuyet layKeHoachMoiNhat(Integer cuocHopId) {
        return yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<CuocHopDangDienRaResponse> layCuocHopDangDienRaCuaToi(
            Integer nguoiDungId) {

        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

        return thanhVienCuocHopRepository
                .findCuocHopDangDienRaDaXacNhan(nguoiDungId)
                .stream()
                .filter(thanhVien -> ThaoTacCuocHop.dangDienRa(thanhVien.getCuocHop()))
                .map(thanhVien -> {
                    CuocHop cuocHop = thanhVien.getCuocHop();
                    NguoiDung chuTri = cuocHop.getNguoiChuTri();

                    return new CuocHopDangDienRaResponse(
                            cuocHop.getCuocHopId(),
                            cuocHop.getTieuDe(),
                            cuocHop.getLoaiHinh(),
                            chuTri == null ? null : chuTri.getHoTen(),
                            ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId),
                            cuocHop.getThoiGianBatDauThucTe(),
                            cuocHop.getMucTieu(),
                            cuocHop.getNoiDungDuKien(),
                            thanhVien.getTrangThaiThamDu(),
                            thanhVien.getThoiGianDiemDanh(),
                            thanhVien.getThoiGianRoiCuocHop());
                })
                .toList();
    }
    
    @Transactional
    public LocalDateTime thamGiaCuocHop(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = khoaCuocHop(cuocHopId);

        ThanhVienCuocHop thanhVien = thanhVienCuocHopRepository
                .findByIdForUpdate(taoId(cuocHopId, nguoiDungId))
                .orElseThrow(() -> loi(HttpStatus.FORBIDDEN,
                        "Bạn không thuộc cuộc họp này"));

        if (!"DA_XAC_NHAN".equals(thanhVien.getTrangThaiMoi())) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn cần xác nhận lời mời trước khi tham gia");
        }
        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp không đang diễn ra");
        }
        if (thanhVien.getThoiGianRoiCuocHop() != null) {
            throw loi(HttpStatus.CONFLICT, "Bạn đã được ghi nhận rời cuộc họp");
        }

        if (!"CO_MAT".equals(thanhVien.getTrangThaiThamDu())
                || thanhVien.getThoiGianDiemDanh() == null) {
            thanhVien.setTrangThaiThamDu("CO_MAT");
            thanhVien.setThoiGianDiemDanh(LocalDateTime.now());
        }

        return thanhVien.getThoiGianDiemDanh();
    }

    @Transactional
    public void diemDanhThanhVien(Integer cuocHopId, Integer nguoiDuocDiemDanhId,
            Integer thuKyId, boolean coMat) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop ch = khoaCuocHop(cuocHopId);
        kiemTraThuKy(cuocHopId, thuKyId);
        if (!ThaoTacCuocHop.dangDienRa(ch)) throw loi(HttpStatus.CONFLICT, "Chỉ điểm danh khi cuộc họp đang diễn ra");
        ThanhVienCuocHop tv = thanhVienCuocHopRepository
                .findByIdForUpdate(taoId(cuocHopId, nguoiDuocDiemDanhId))
                .orElse(null);
        if (tv == null)
        	throw loi(HttpStatus.NOT_FOUND, "Người dùng không thuộc cuộc họp");
        
        String trangThai = coMat ? "CO_MAT" : "VANG_MAT";
        if (!coMat && tv.getThoiGianRoiCuocHop() != null) {
            throw loi(HttpStatus.CONFLICT,
                    "Thành viên đã được ghi nhận rời sớm và vẫn được tính là đã tham dự");
        }
        if (!trangThai.equals(tv.getTrangThaiThamDu())) {
            tv.setTrangThaiThamDu(trangThai);
            tv.setThoiGianDiemDanh(LocalDateTime.now());
        }
    }
    
    @Transactional
    public void ghiNhanRoiSom(Integer cuocHopId, Integer nguoiDungId, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);

        CuocHop cuocHop = khoaCuocHop(cuocHopId);
        kiemTraThuKy(cuocHopId, thuKyId);

        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT,
                    "Chỉ ghi nhận rời sớm khi cuộc họp đang diễn ra");
        }

        ThanhVienCuocHop thanhVien = thanhVienCuocHopRepository
                .findByIdForUpdate(taoId(cuocHopId, nguoiDungId))
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND,
                        "Người dùng không thuộc cuộc họp"));

        if (!"CO_MAT".equals(thanhVien.getTrangThaiThamDu())) {
            throw loi(HttpStatus.CONFLICT,
                    "Chỉ ghi nhận rời sớm cho thành viên đã điểm danh có mặt");
        }

        if (thanhVien.getThoiGianRoiCuocHop() == null) {
            thanhVien.setThoiGianRoiCuocHop(LocalDateTime.now());
        }
    }

    private void kiemTraThuKy(Integer cuocHopId, Integer thuKyId) {
        if (!phanQuyenCuocHopService.laThuKyDuocPhanCong(cuocHopId, thuKyId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký đang phụ trách được thực hiện thao tác này");
        }
    }
    private CuocHop khoaCuocHop(Integer id) {
        if (id == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
        return cuocHopRepository.findByIdForUpdate(id)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
    }
    private ThanhVienCuocHopId taoId(Integer cuocHopId, Integer nguoiDungId) {
        if (cuocHopId == null || nguoiDungId == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp hoặc người dùng");
        ThanhVienCuocHopId id = new ThanhVienCuocHopId();
        id.setCuocHopId(cuocHopId); id.setNguoiDungId(nguoiDungId);
        return id;
    }
    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}