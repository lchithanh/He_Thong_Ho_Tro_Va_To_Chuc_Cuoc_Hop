package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.request.NoiDungThuMoi;
import com.example.demo.dto.response.ThanhVienCuocHopResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.LichHop;
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
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final ThongBaoService thongBaoService;
    private final ThongBaoEmailCuocHop thongBaoEmail;
    private final NhomThamDuCuocHopService nhomThamDu;

    @Transactional
    public void moiThanhVien(Integer cuocHopId, Integer nguoiDuocMoiId, Integer thuKyId, NoiDungThuMoi thuMoi) {
        moiDanhSachThanhVien(cuocHopId, java.util.Collections.singletonList(nguoiDuocMoiId), thuKyId, thuMoi);
    }

    @Transactional
    public void moiDanhSachThanhVien(Integer cuocHopId, List<Integer> nguoiDuocMoiIds, Integer thuKyId, NoiDungThuMoi thuMoi) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        if (nguoiDuocMoiIds == null || nguoiDuocMoiIds.isEmpty()
                || nguoiDuocMoiIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw loi(HttpStatus.BAD_REQUEST, "Vui lòng chọn người tham dự hợp lệ");
        }
        CuocHop ch = khoaCuocHop(cuocHopId);
        kiemTraThuKy(cuocHopId, thuKyId);
        YeuCauPheDuyet keHoachMoi = layKeHoachCoTheMoi(ch);
        List<NguoiDung> nguoiNhan = new ArrayList<>();
        for (Integer nguoiDuocMoiId : new java.util.LinkedHashSet<>(nguoiDuocMoiIds)) {
            nguoiNhan.add(themThanhVienDuocMoi(ch, keHoachMoi, nguoiDuocMoiId));
        }
        thongBaoEmail.moiHop(keHoachMoi, nguoiNhan, thuMoi);
        capNhatRealtime.baoCuocHop(ch, thuKyId);
    }

    @Transactional
    public void guiLaiEmailLoiMoi(Integer cuocHopId, Integer nguoiDuocMoiId, Integer thuKyId, NoiDungThuMoi thuMoi) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop cuocHop = khoaCuocHop(cuocHopId);
        kiemTraThuKy(cuocHopId, thuKyId);
        YeuCauPheDuyet keHoachMoi = layKeHoachCoTheMoi(cuocHop);
        ThanhVienCuocHop thanhVien = thanhVienCuocHopRepository.findByIdForUpdate(taoId(cuocHopId, nguoiDuocMoiId))
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy lời mời"));
        if (!"THAM_DU".equals(thanhVien.getVaiTroTaiCuocHop())
                || !"CHO_PHAN_HOI".equals(thanhVien.getTrangThaiMoi())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ gửi lại email cho lời mời đang chờ phản hồi");
        }
        NguoiDung nguoiNhan = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDuocMoiId);
        if (!thongBaoEmail.moiHop(keHoachMoi, List.of(nguoiNhan), thuMoi)) {
            throw loi(HttpStatus.CONFLICT, "Kiểm tra email người nhận và cấu hình gửi mail của hệ thống");
        }
    }

    private YeuCauPheDuyet layKeHoachCoTheMoi(CuocHop cuocHop) {
        YeuCauPheDuyet keHoach = layKeHoachMoiNhat(cuocHop.getCuocHopId());
        if (!ThaoTacCuocHop.coTheLapLich(cuocHop) || !ThaoTacCuocHop.coLichMoiThamDu(cuocHop, keHoach)) {
            throw loi(HttpStatus.CONFLICT,
                    "Cần kế hoạch đã duyệt và lịch sắp tới đã xác nhận trước khi gửi lời mời");
        }
        return keHoach;
    }

    private NguoiDung themThanhVienDuocMoi(CuocHop ch, YeuCauPheDuyet keHoach, Integer nguoiDuocMoiId) {
        NguoiDung nd = nguoiDungRepository.findById(nguoiDuocMoiId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy người được mời"));
        if (!NguoiDungHienTaiService.dangHoatDong(nd)) throw loi(HttpStatus.CONFLICT, "Tài khoản được mời không hoạt động");
        if ("VAN_PHONG".equals(keHoach.getCapPheDuyet())
                && (ch.getPhongBanChuTri() == null
                    || !NguoiDungHienTaiService.thuocPhongBan(nd, ch.getPhongBanChuTri().getPhongBanId())
                    || phanQuyenCuocHopService.coVaiTro(nguoiDuocMoiId, "LANH_DAO"))) {
            throw loi(HttpStatus.CONFLICT, "Mời lãnh đạo hoặc người ngoài phòng chủ trì cần kế hoạch do lãnh đạo duyệt");
        }
        ThanhVienCuocHopId id = taoId(ch.getCuocHopId(), nguoiDuocMoiId);
        if (thanhVienCuocHopRepository.existsById(id)) throw loi(HttpStatus.CONFLICT, "Người dùng đã có trong cuộc họp");
        ThanhVienCuocHop tv = new ThanhVienCuocHop();
        tv.setId(id); tv.setCuocHop(ch); tv.setNguoiDung(nd);
        tv.setVaiTroTaiCuocHop("THAM_DU");
        tv.setNgayMoi(LocalDateTime.now());
        tv.setTrangThaiMoi("CHO_PHAN_HOI");
        tv.setTrangThaiThamDu("CHUA_DIEM_DANH");
        thanhVienCuocHopRepository.themMoi(tv);
        return nd;
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
                        tv.getThoiGianDiemDanh(), tv.getThoiGianRoiCuocHop(),
                        tv.getThoiGianTuDiemDanh(), tv.getThoiGianDoiChieuDiemDanh(), tv.getNgayMoi())).toList();
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

        Set<Integer> idsNhom = nhomThamDu.layThanhVienDaChon(cuocHopId);
        var keHoach = layKeHoachMoiNhat(cuocHopId);
        boolean vanPhongDuyet = keHoach != null && "VAN_PHONG".equals(keHoach.getCapPheDuyet());
        return nguoiDungRepository.findAll()
                .stream()
                .filter(NguoiDungHienTaiService::dangHoatDong)
                .filter(nguoiDung -> !idsDaThamGia.contains(nguoiDung.getNguoiDungId()))
                .filter(nguoiDung -> !vanPhongDuyet || (cuocHop.getPhongBanChuTri() != null
                        && NguoiDungHienTaiService.thuocPhongBan(nguoiDung, cuocHop.getPhongBanChuTri().getPhongBanId())
                        && !phanQuyenCuocHopService.coVaiTro(nguoiDung.getNguoiDungId(), "LANH_DAO")))
                .map(nguoiDung -> new NguoiCoTheMoiResponse(
                        nguoiDung.getNguoiDungId(),
                        nguoiDung.getHoTen(),
                        nguoiDung.getPhongBan() == null
                                ? null
                                : nguoiDung.getPhongBan().getTenPhongBan(),
                        nguoiDung.getChucVu(),
                        idsNhom.contains(nguoiDung.getNguoiDungId())))
                .sorted(Comparator.comparing(NguoiCoTheMoiResponse::thuocNhomThamDu).reversed()
                        .thenComparing(NguoiCoTheMoiResponse::hoTen))
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
        capNhatRealtime.baoCuocHop(ch, nguoiDungId);
    }

	@Transactional(readOnly = true)
	public List<LoiMoiChoPhanHoiResponse> layLoiMoiChoPhanHoi(Integer nguoiDungId) {
	    nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);

	    return thanhVienCuocHopRepository
	            .findById_NguoiDungIdAndVaiTroTaiCuocHopAndTrangThaiMoi(
	                    nguoiDungId, "THAM_DU", "CHO_PHAN_HOI")
	            .stream()
	            .filter(tv -> ThaoTacCuocHop.coTheLapLich(tv.getCuocHop()))
	            .filter(tv -> coLichKeHoachDaDuyet(tv.getCuocHop()))
	            .map(this::chuyenLoiMoi)
	            .toList();
	}
	
    private LoiMoiChoPhanHoiResponse chuyenLoiMoi(ThanhVienCuocHop thanhVien) {
        CuocHop cuocHop = thanhVien.getCuocHop();
        var lich = ThaoTacCuocHop.lichHienHanh(cuocHop);
        if (lich == null || !ThaoTacLichHop.daXacNhan(lich)) {
            throw loi(HttpStatus.CONFLICT,
                    "Lịch gắn với kế hoạch mới nhất chưa được xác nhận");
        }
        return new LoiMoiChoPhanHoiResponse(cuocHop.getCuocHopId(), cuocHop.getMaCuocHop(),
                cuocHop.getTieuDe(), lich.getThoiGianBatDau(), lich.getThoiGianKetThuc(),
                lich.getPhongHop() == null ? null : lich.getPhongHop().getTenPhongHop(),
                cuocHop.getLoaiHinh(),
                cuocHop.getNguoiChuTri() == null ? null : cuocHop.getNguoiChuTri().getHoTen(),
                cuocHop.getMucTieu(), cuocHop.getNoiDungDuKien(), thanhVien.getNgayMoi());
    }

    boolean coLichKeHoachDaDuyet(CuocHop cuocHop) {
        return ThaoTacCuocHop.coTheLapLich(cuocHop)
                && ThaoTacCuocHop.coLichMoiThamDu(
                        cuocHop, layKeHoachMoiNhat(cuocHop.getCuocHopId()));
    }

    private YeuCauPheDuyet layKeHoachMoiNhat(Integer cuocHopId) {
        return yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElse(null);
    }
    
    void baoCuocHopBatDau(CuocHop cuocHop, Integer nguoiBatDauId) {
        YeuCauPheDuyet lanTrinh = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHop.getCuocHopId());
        NguoiDung thuKy = lanTrinh != null && lanTrinh.getThuKyBanGiao() != null
                ? lanTrinh.getThuKyBanGiao()
                : phanCongXuLyKeHoachService.layThuKyBanDau(cuocHop.getCuocHopId());
        themNguoiDieuHanhNeuThieu(cuocHop, cuocHop.getNguoiChuTri(), "CHU_TRI");
        themNguoiDieuHanhNeuThieu(cuocHop, thuKy, "THU_KY_CUOC_HOP");

        List<NguoiDung> nguoiNhan = new ArrayList<>();
        nguoiNhan.add(cuocHop.getNguoiChuTri());
        nguoiNhan.add(thuKy);
        thanhVienCuocHopRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHop.getCuocHopId())
                .stream().filter(ThanhVienCuocHopService::coLoiMoiHopLe)
                .map(ThanhVienCuocHop::getNguoiDung).forEach(nguoiNhan::add);
        thongBaoService.gui(cuocHop, nguoiBatDauId, "CUOC_HOP_BAT_DAU", "Cuộc họp đã bắt đầu",
                cuocHop.getTieuDe(), nguoiNhan.stream().filter(Objects::nonNull)
                        .filter(NguoiDungHienTaiService::dangHoatDong).toArray(NguoiDung[]::new));
    }

    private void themNguoiDieuHanhNeuThieu(CuocHop cuocHop, NguoiDung nguoiDung, String vaiTro) {
        if (nguoiDung == null || !NguoiDungHienTaiService.dangHoatDong(nguoiDung)) return;
        ThanhVienCuocHopId id = taoId(cuocHop.getCuocHopId(), nguoiDung.getNguoiDungId());
        if (!thanhVienCuocHopRepository.existsById(id)) {
            thanhVienCuocHopRepository.themMoi(taoThanhVienDieuHanh(cuocHop, nguoiDung, id, vaiTro));
        }
    }

    private ThanhVienCuocHop taoThanhVienDieuHanh(
            CuocHop cuocHop, NguoiDung nguoiDung, ThanhVienCuocHopId id, String vaiTro) {
        ThanhVienCuocHop thanhVien = new ThanhVienCuocHop();
        thanhVien.setId(id);
        thanhVien.setCuocHop(cuocHop);
        thanhVien.setNguoiDung(nguoiDung);
        thanhVien.setVaiTroTaiCuocHop(vaiTro);
        thanhVien.setTrangThaiMoi("DA_XAC_NHAN");
        return thanhVien;
    }

    @Transactional(readOnly = true)
    public List<CuocHopDangDienRaResponse> layCuocHopDangDienRaCuaToi(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        return cuocHopRepository.findCuocHopDangDienRaGanVoiNguoiDung(nguoiDungId).stream()
                .filter(ThaoTacCuocHop::dangDienRa)
                .map(cuocHop -> taoPhongHop(cuocHop, nguoiDungId))
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional(readOnly = true)
    public CuocHopDangDienRaResponse layPhongHop(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        CuocHopDangDienRaResponse phongHop = taoPhongHop(cuocHop, nguoiDungId);
        if (phongHop == null) throw loi(HttpStatus.FORBIDDEN, "Bạn không thuộc cuộc họp này");
        if (!ThaoTacCuocHop.daBatDau(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp chưa bắt đầu");
        }
        return phongHop;
    }

    private CuocHopDangDienRaResponse taoPhongHop(CuocHop cuocHop, Integer nguoiDungId) {
        ThanhVienCuocHop tv = thanhVienCuocHopRepository
                .findById(taoId(cuocHop.getCuocHopId(), nguoiDungId)).orElse(null);
        boolean laChuTri = ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId);
        boolean laThuKy = phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, nguoiDungId);
        if (!laChuTri && !laThuKy && !coLoiMoiHopLe(tv)) return null;

        NguoiDung chuTri = cuocHop.getNguoiChuTri();
        return new CuocHopDangDienRaResponse(cuocHop.getCuocHopId(), cuocHop.getTieuDe(),
                cuocHop.getLoaiHinh(), chuTri == null ? null : chuTri.getHoTen(), laChuTri,
                cuocHop.getThoiGianBatDauThucTe(), cuocHop.getMucTieu(), cuocHop.getNoiDungDuKien(),
                tv == null ? "CHUA_DIEM_DANH" : tv.getTrangThaiThamDu(),
                tv == null ? null : tv.getThoiGianDiemDanh(), tv == null ? null : tv.getThoiGianRoiCuocHop(),
                tv == null ? null : tv.getThoiGianTuDiemDanh(),
                tv == null ? null : tv.getThoiGianDoiChieuDiemDanh(),
                cuocHop.getTrangThai(), tv == null ? null : tv.getTrangThaiMoi(), laThuKy,
                cuocHop.getDanhSachLichHop().stream().filter(ThaoTacLichHop::daXacNhan)
                        .map(LichHop::getPhongHop).filter(Objects::nonNull)
                        .map(phong -> phong.getTenPhongHop()).findFirst().orElse(null),
                cuocHop.getThoiGianKetThucThucTe());
    }

    private static boolean coLoiMoiHopLe(ThanhVienCuocHop thanhVien) {
        return thanhVien != null && ("DA_XAC_NHAN".equals(thanhVien.getTrangThaiMoi())
                || "CHO_PHAN_HOI".equals(thanhVien.getTrangThaiMoi()));
    }

    @Transactional
    public LocalDateTime thamGiaCuocHop(Integer cuocHopId, Integer nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = khoaCuocHop(cuocHopId);
        if (!ThaoTacCuocHop.dangDienRa(cuocHop)) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp không đang diễn ra");
        }

        ThanhVienCuocHopId id = taoId(cuocHopId, nguoiDungId);
        ThanhVienCuocHop thanhVien = thanhVienCuocHopRepository.findByIdForUpdate(id).orElse(null);
        boolean laChuTri = ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId);
        boolean laThuKy = phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, nguoiDungId);
        if (!laChuTri && !laThuKy && !coLoiMoiHopLe(thanhVien)) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn không thuộc cuộc họp này");
        }

        boolean themMoi = thanhVien == null;
        if (themMoi) {
            thanhVien = taoThanhVienDieuHanh(cuocHop, nguoiDung, id,
                    laChuTri ? "CHU_TRI" : "THU_KY_CUOC_HOP");
        }
        if (thanhVien.getThoiGianRoiCuocHop() != null) {
            throw loi(HttpStatus.CONFLICT, "Bạn đã được ghi nhận rời cuộc họp");
        }
        if (thanhVien.getThoiGianTuDiemDanh() != null) {
            return thanhVien.getThoiGianTuDiemDanh();
        }

        LocalDateTime bayGio = LocalDateTime.now();
        thanhVien.setTrangThaiMoi("DA_XAC_NHAN");
        thanhVien.setThoiGianTuDiemDanh(bayGio);
        if (thanhVien.getThoiGianDoiChieuDiemDanh() == null
                && !"VANG_MAT".equals(thanhVien.getTrangThaiThamDu())) {
            thanhVien.setTrangThaiThamDu("CO_MAT");
            if (thanhVien.getThoiGianDiemDanh() == null) thanhVien.setThoiGianDiemDanh(bayGio);
        }
        if (themMoi) thanhVienCuocHopRepository.themMoi(thanhVien);
        capNhatRealtime.baoNguoiPhuTrach(cuocHop, nguoiDungId);
        return bayGio;
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
        LocalDateTime bayGio = LocalDateTime.now();
        if (!trangThai.equals(tv.getTrangThaiThamDu()) || tv.getThoiGianDiemDanh() == null) {
            tv.setTrangThaiThamDu(trangThai);
            tv.setThoiGianDiemDanh(bayGio);
        }
        tv.setThoiGianDoiChieuDiemDanh(bayGio);
        capNhatRealtime.baoNguoiPhuTrach(ch, nguoiDuocDiemDanhId);
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
            capNhatRealtime.baoNguoiPhuTrach(cuocHop, nguoiDungId);
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