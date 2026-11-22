package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.request.TaoDeXuatCuocHopRequest;
import com.example.demo.dto.response.DeXuatCuocHopResponse;
import com.example.demo.dto.response.DeXuatCuocHopResponse.PhanHoiKeHoach;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.LichSuPheDuyetRepository;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongBan;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.PhanCongThuKyRepository;
import com.example.demo.repository.PhongBanRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import java.util.Set;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional
public class DeXuatCuocHopService {
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final CuocHopRepository cuocHopRepository;
    private final HuyCuocHopService huyCuocHopService;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhongBanRepository phongBanRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private  final PhanCongThuKyRepository phanCongThuKyRepository;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final LichSuPheDuyetRepository lichSuPheDuyetRepository;
    private final ThongTinThuMoiDaDuyet thongTinThuMoiDaDuyet;

    public Integer taoDeXuatCuocHop(
            TaoDeXuatCuocHopRequest request,
            Integer nguoiDangNhapId) {
        phanQuyenCuocHopService
                .kiemTraQuyenTaoDeXuat(nguoiDangNhapId);
        kiemTraDuLieuDeXuat(request);
        
        NguoiDung nguoiDeXuat = nguoiDungRepository
                .findById(nguoiDangNhapId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không tìm thấy người đề xuất"
                ));
        boolean laLanhDao = phanQuyenCuocHopService
                .coVaiTro(nguoiDangNhapId, "LANH_DAO");
        boolean laHopNoiBo =
                "NOI_BO".equals(request.getPhamViCuocHop());
        PhongBan phongBanChuTri = null;
        if (laHopNoiBo && !laLanhDao) {
            // Trưởng phòng tạo họp nội bộ:
            // tự lấy phòng ban từ tài khoản đăng nhập.
            phongBanChuTri = nguoiDeXuat.getPhongBan();
            if (phongBanChuTri == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Người đề xuất chưa được phân công vào phòng ban"
                );
            }
            // Nếu request có gửi phòng ban thì phải đúng phòng mình.
            if (request.getPhongBanChuTriId() != null
                    && !request.getPhongBanChuTriId().equals(
                            phongBanChuTri.getPhongBanId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Bạn chỉ được tổ chức cuộc họp trong phòng ban của mình"
                );
            }
        } else {
            // Lãnh đạo hoặc đề xuất ngoài phạm vi nội bộ:
            // lấy phòng ban được chọn trong request.
            if (request.getPhongBanChuTriId() != null) {
                phongBanChuTri = phongBanRepository.findById(request.getPhongBanChuTriId()).orElse(null);
                if (phongBanChuTri == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Không tìm thấy phòng ban chủ trì"
                    );
                }
            }
            if (laHopNoiBo && phongBanChuTri == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Vui lòng chọn phòng ban tổ chức cuộc họp nội bộ"
                );
            }
        }
        Integer nguoiChuTriId = request.getNguoiChuTriId();
        NguoiDung nguoiChuTri = nguoiChuTriId == null
                ? null
                : timNguoiChuTriHoatDong(nguoiChuTriId);
        CuocHop cuocHop = taoDeXuatNhap(request, nguoiDeXuat,
                phongBanChuTri, nguoiChuTri);
        Integer thuKyCuocHopId = request.getThuKyCuocHopId() != null
                ? request.getThuKyCuocHopId() : request.getThuKyId();
        cuocHop.setThuKyCuocHop(phanCongXuLyKeHoachService.chonThuKyCuocHop(cuocHop, thuKyCuocHopId));
        NguoiDung thuKy = chonThuKy(
                null,
                nguoiDangNhapId,
                null,
                request.isGuiNgay()
        );
        if (request.isGuiNgay()) {
            ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.guiDeXuat(cuocHop));
        }
        CuocHop daLuu = cuocHopRepository.save(cuocHop);
        if (thuKy != null) {
            ganThuKy(daLuu, thuKy);
        }
        if (request.isGuiNgay()) {
            capNhatRealtime.baoBuocDeXuat(daLuu, nguoiDangNhapId);
        } else {
            capNhatRealtime.baoCho(nguoiDangNhapId);
        }
        return daLuu.getCuocHopId();
    }

    public void capNhatDeXuat(
            Integer cuocHopId,
            TaoDeXuatCuocHopRequest request,
            Integer nguoiDangNhapId) {
        phanQuyenCuocHopService
                .kiemTraQuyenTaoDeXuat(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"));
        if (!ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDangNhapId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ người đề xuất được sửa bản nháp này"
            );
        }
        if (!ThaoTacCuocHop.laDeXuatNhap(cuocHop)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ được sửa đề xuất khi còn nháp"
            );
        }
        kiemTraDuLieuDeXuat(request);
        if (request.isGuiNgay() || request.getThuKyId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng dùng chức năng gửi đề xuất để chọn thư ký và gửi"
            );
        }
        NguoiDung nguoiDeXuat = cuocHop.getNguoiDeXuat();
        boolean laLanhDao = phanQuyenCuocHopService
                .coVaiTro(nguoiDangNhapId, "LANH_DAO");
        boolean laHopNoiBo =
                "NOI_BO".equals(request.getPhamViCuocHop());
        PhongBan phongBanChuTri = null;
        if (laHopNoiBo && !laLanhDao) {
            phongBanChuTri = nguoiDeXuat.getPhongBan();
            if (phongBanChuTri == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Người đề xuất chưa được phân công vào phòng ban"
                );
            }
            if (request.getPhongBanChuTriId() != null
                    && !request.getPhongBanChuTriId().equals(
                            phongBanChuTri.getPhongBanId())) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Bạn chỉ được tổ chức cuộc họp trong phòng ban của mình"
                );
            }
        } else {
            if (request.getPhongBanChuTriId() != null) {
                phongBanChuTri = phongBanRepository.findById(request.getPhongBanChuTriId()).orElse(null);
                if (phongBanChuTri == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Không tìm thấy phòng ban chủ trì"
                    );
                }
            }
            if (laHopNoiBo && phongBanChuTri == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Vui lòng chọn phòng ban tổ chức cuộc họp nội bộ"
                );
            }
        }
        Integer nguoiChuTriId = request.getNguoiChuTriId();
        NguoiDung nguoiChuTri = nguoiChuTriId == null
                ? null
                : timNguoiChuTriHoatDong(nguoiChuTriId);
        capNhatDeXuat(cuocHop, request, phongBanChuTri, nguoiChuTri);
        if (request.getThuKyCuocHopId() != null) {
            cuocHop.setThuKyCuocHop(phanCongXuLyKeHoachService.chonThuKyCuocHop(cuocHop, request.getThuKyCuocHopId()));
        }
        cuocHopRepository.save(cuocHop);
        capNhatRealtime.baoCho(nguoiDangNhapId);
    }

    public void guiDeXuat(
            Integer cuocHopId,
            Integer thuKyId,
            Integer nguoiDangNhapId) {
        phanQuyenCuocHopService
                .kiemTraQuyenTaoDeXuat(nguoiDangNhapId);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"));
        if (!ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDangNhapId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ người đề xuất mới được gửi bản nháp này"
            );
        }
        if (!ThaoTacCuocHop.laDeXuatNhap(cuocHop)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chỉ được gửi đề xuất đang ở trạng thái nháp"
            );
        }
        // Kiểm tra lại dữ liệu đã lưu.
        TaoDeXuatCuocHopRequest duLieu = new TaoDeXuatCuocHopRequest();
        duLieu.setTieuDe(cuocHop.getTieuDe());
        duLieu.setLoaiHinh(cuocHop.getLoaiHinh());
        duLieu.setPhamViCuocHop(cuocHop.getPhamViCuocHop());
        kiemTraDuLieuDeXuat(duLieu);
        if (ThaoTacCuocHop.laCuocHopNoiBo(cuocHop)) {
            if (cuocHop.getPhongBanChuTri() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Họp nội bộ phải có phòng ban chủ trì"
                );
            }
            if (!phanQuyenCuocHopService.coVaiTro(
                    nguoiDangNhapId, "LANH_DAO")) {
                phanQuyenCuocHopService.kiemTraCungPhongBan(
                        nguoiDangNhapId,
                        cuocHop.getPhongBanChuTri().getPhongBanId()
                );
            }
        }
        if (cuocHop.getNguoiDeXuat() == null || cuocHop.getNguoiTao() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Thiếu người tạo hoặc người đề xuất");
        }
        if (cuocHop.getNguoiChuTri() != null
                && !NguoiDungHienTaiService.dangHoatDong(cuocHop.getNguoiChuTri())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Người chủ trì không còn hoạt động"
            );
        }
        if (thuKyId != null) {
            cuocHop.setThuKyCuocHop(phanCongXuLyKeHoachService.chonThuKyCuocHop(cuocHop, thuKyId));
        }
        NguoiDung thuKy = chonThuKy(
                null,
                cuocHop.getNguoiDeXuat().getNguoiDungId(),
                cuocHop,
                true
        );

        ganThuKy(cuocHop, thuKy);
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.guiDeXuat(cuocHop));
        cuocHopRepository.save(cuocHop);
        capNhatRealtime.baoBuocDeXuat(cuocHop, nguoiDangNhapId);
    }
    // TIẾP NHẬN ĐỀ XUẤT
    public void tiepNhanDeXuat(
            Integer cuocHopId,
            Integer nguoiDangNhapId) {
        NguoiDung nguoiDung = nguoiDungRepository
                .findById(nguoiDangNhapId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Không tìm thấy tài khoản đăng nhập"
                ));
        if (!NguoiDungHienTaiService.dangHoatDong(nguoiDung)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản không hoạt động"
            );
        }
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!phanQuyenCuocHopService.laThuKyDuocPhanCong(
                cuocHopId, nguoiDangNhapId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ thư ký được chỉ định mới được tiếp nhận đề xuất"
            );
        }
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.tiepNhanDeXuat(cuocHop));
        cuocHopRepository.save(cuocHop);
        capNhatRealtime.baoBuocDeXuat(cuocHop, nguoiDangNhapId);
    }

    @Transactional(readOnly = true)
    public List<DeXuatCuocHopResponse> layDanhSachDeXuat(
            Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (phanQuyenCuocHopService.coVaiTro(nguoiDungId, "ADMIN")) {
            return cuocHopRepository.findAll()
                    .stream()
                    .sorted(Comparator.comparing(
                            CuocHop::getCuocHopId
                    ).reversed())
                    .map(ch -> chuyenDeXuatResponse(ch, nguoiDungId))
                    .toList();
        }
        return cuocHopRepository
                .findDeXuatCuaNguoiDeXuatHoacThuKy(nguoiDungId, LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.DA_DUYET)
                .stream()
                .map(ch -> chuyenDeXuatResponse(ch, nguoiDungId))
                .toList();
    }

    @Transactional(readOnly = true)
    public DeXuatCuocHopResponse layChiTietDeXuat(
            Integer cuocHopId,
            Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"));
        if (!phanQuyenCuocHopService.coQuyenXemChiTietDeXuat(
                cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền xem đề xuất này");
        }
        return chuyenDeXuatResponse(cuocHop, nguoiDungId, true);
    }

    @Transactional(readOnly = true)
    public String layTrangThai(Integer cuocHopId) {
        return cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cuộc họp"
                ))
                .getTrangThai();
    }

    private void kiemTraDuLieuDeXuat(
            TaoDeXuatCuocHopRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thông tin đề xuất không được để trống"
            );
        }
        kiemTraTieuDeVaLoaiHinh(request.getTieuDe(), request.getLoaiHinh());
        if (request.getPhamViCuocHop() == null
                || !PHAM_VI_HOP_LE.contains(
                        request.getPhamViCuocHop())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Phạm vi cuộc họp không hợp lệ"
            );
        }
    }

    private NguoiDung kiemTraThuKy(Integer thuKyId) {

        if (thuKyId == null || thuKyId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng chọn người làm thư ký cuộc họp");
        }

        NguoiDung thuKy = nguoiDungRepository.findById(thuKyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy người được chọn làm thư ký"));

        if (!NguoiDungHienTaiService.dangHoatDong(thuKy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tài khoản được chọn làm thư ký không hoạt động");
        }

        // Thư ký riêng của cuộc họp không bắt buộc
        // có vai trò hệ thống THU_KY.
        return thuKy;
    }

    private void ganThuKy(
            CuocHop cuocHop,
            NguoiDung thuKy) {

        Integer cuocHopId = cuocHop.getCuocHopId();
        Integer thuKyId = thuKy.getNguoiDungId();

        ThanhVienCuocHopId id = new ThanhVienCuocHopId();
        id.setCuocHopId(cuocHopId);
        id.setNguoiDungId(thuKyId);

        ThanhVienCuocHop thanhVien = thanhVienCuocHopRepository
                .findById(id)
                .orElse(null);

        if (thanhVien != null
                && !"THU_KY".equals(thanhVien.getVaiTroTaiCuocHop())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Người được chọn đã có vai trò khác trong cuộc họp");
        }

        var thuKyCu = thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdAndVaiTroTaiCuocHop(
                        cuocHopId,
                        "THU_KY")
                .stream()
                .filter(tv -> !thuKyId.equals(
                        tv.getId().getNguoiDungId()))
                .toList();

        thanhVienCuocHopRepository.deleteAll(thuKyCu);

        if (thanhVien != null) {
            return;
        }

        ThanhVienCuocHop thuKyMoi = new ThanhVienCuocHop();
        thuKyMoi.setId(id);
        thuKyMoi.setCuocHop(cuocHop);
        thuKyMoi.setNguoiDung(thuKy);
        thuKyMoi.setVaiTroTaiCuocHop("THU_KY");
        thuKyMoi.setTrangThaiMoi("CHO_PHAN_HOI");
        thuKyMoi.setTrangThaiThamDu("CHUA_DIEM_DANH");

        thanhVienCuocHopRepository.save(thuKyMoi);
    }

    private DeXuatCuocHopResponse chuyenDeXuatResponse(CuocHop ch, Integer nguoiDungId) {
        return chuyenDeXuatResponse(ch, nguoiDungId, false);
    }

    private DeXuatCuocHopResponse chuyenDeXuatResponse(CuocHop ch, Integer nguoiDungId, boolean laChiTiet) {
        var lanMoiNhat = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(ch.getCuocHopId());
        var banGiao = phanCongXuLyKeHoachService.layBanGiaoHoSo(ch, lanMoiNhat);
        NguoiDung nguoiDangXuLy = layNguoiDangXuLy(ch, lanMoiNhat);
        boolean nguoiNhan = phanCongXuLyKeHoachService.laThuKyDaNhanBanGiao(banGiao, nguoiDungId);
        boolean duocLap = phanQuyenCuocHopService.laThuKyDuocPhanCong(ch.getCuocHopId(), nguoiDungId)
                && (ThaoTacCuocHop.dangChoTiepNhan(ch) || ThaoTacCuocHop.dangLapKeHoach(ch));
        boolean hoanTat = phanCongXuLyKeHoachService.daHoanTatHoSo(ch);
        Integer choChuyenCap = phanCongXuLyKeHoachService.layYeuCauChoChuyenCap(ch, lanMoiNhat, nguoiDungId);
        return new DeXuatCuocHopResponse(
                ch.getCuocHopId(),
                ch.getMaCuocHop(),
                ch.getTieuDe(),
                ch.getMucTieu(),
                ch.getNoiDungDuKien(),
                ch.getLoaiHinh(),
                ch.getPhamViCuocHop(),
                ch.getPhongBanChuTri() != null
                        ? ch.getPhongBanChuTri().getPhongBanId() : null,
                ch.getNguoiDeXuat() != null
                        ? ch.getNguoiDeXuat().getNguoiDungId() : null,
                ch.getNguoiDeXuat() != null
                        ? ch.getNguoiDeXuat().getHoTen() : null,
                ch.getNguoiChuTri() != null
                        ? ch.getNguoiChuTri().getNguoiDungId() : null,
                ch.getTrangThai(),
                ch.getBuocXuLyHienTai(),
                ch.getNgayTao(),
                ch.getCoKhachMoiBenNgoai(),
                layPhanHoiKeHoach(ch),
                ch.getThuKyCuocHop() == null
                        ? null : ch.getThuKyCuocHop().getNguoiDungId(),
                nguoiNhan ? banGiao.getYeuCauPheDuyetId() : null,
                duocLap,
                !hoanTat && phanCongXuLyKeHoachService.laThuKyToChuc(ch, nguoiDungId),
                hoanTat,
                xacDinhVaiTroNguoiDeXuat(ch),
                choChuyenCap,
                nguoiDangXuLy == null ? null : nguoiDangXuLy.getNguoiDungId(),
                nguoiDangXuLy == null ? null : nguoiDangXuLy.getHoTen(),
                layLichHopIdKeHoachDaDuyet(banGiao),
                ch.getNguoiChuTri() == null ? null : ch.getNguoiChuTri().getHoTen(),
                ch.getThuKyCuocHop() == null ? null : ch.getThuKyCuocHop().getHoTen(),
                ch.getThoiGianBatDauThucTe(),
                ch.getThoiGianKetThucThucTe(),
                ThaoTacCuocHop.coTheLapLich(ch) && ThaoTacCuocHop.coLichDaXacNhan(ch),
                laChiTiet && huyCuocHopService.duocHuy(ch, nguoiDungId),
                laChiTiet ? huyCuocHopService.lyDoDaHuy(ch) : null,
                choChuyenCap == null ? null : mocKeHoach(ch.getCuocHopId(), "DONG_Y_TRINH_CAP_TREN"),
                lanMoiNhat == null || lanMoiNhat.getYeuCauTruoc() == null
                        ? null : mocKeHoach(ch.getCuocHopId(), "TRINH_CAP_TREN"),
                laChiTiet && ThaoTacCuocHop.coLichMoiThamDu(ch, lanMoiNhat)
                        ? thongTinThuMoiDaDuyet.doc(lanMoiNhat) : null
        );
    }

    private LocalDateTime mocKeHoach(Integer cuocHopId, String hanhDong) {
        return lichSuPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiPheDuyetAndHanhDongOrderByLichSuPheDuyetIdDesc(
                        cuocHopId, "KE_HOACH", hanhDong)
                .map(lichSu -> lichSu.getThoiGianPheDuyet())
                .orElse(null);
    }

    private NguoiDung layNguoiDangXuLy(CuocHop cuocHop, YeuCauPheDuyet yeuCau) {
        return switch (cuocHop.getBuocXuLyHienTai() == null ? "" : cuocHop.getBuocXuLyHienTai()) {
            case "CHO_TIEP_NHAN", "CHO_XAC_NHAN_LICH_DU_KIEN", "LAP_KE_HOACH" ->
                    phanCongXuLyKeHoachService.layThuKyCuocHop(cuocHop);
            case "CHO_TRUONG_PHONG", "CHO_LANH_DAO" ->
                    yeuCau == null ? null : yeuCau.getNguoiDuyet();
            case "CHO_GUI_CAP_TREN" ->
                    phanCongXuLyKeHoachService.layThuKyBanDau(cuocHop.getCuocHopId());
            case "CHO_THU_KY_LANH_DAO", "THU_KY_LANH_DAO_XU_LY" ->
                    yeuCau == null ? null : yeuCau.getThuKyTiepNhan();
            case "THU_KY_TO_CHUC", "SAU_CUOC_HOP" ->
                    yeuCau != null && yeuCau.getThuKyBanGiao() != null
                            ? yeuCau.getThuKyBanGiao()
                            : phanCongXuLyKeHoachService.layThuKyCuocHop(cuocHop);
            default -> null;
        };
    }

    private Integer layLichHopIdKeHoachDaDuyet(YeuCauPheDuyet yeuCau) {
        if (yeuCau == null
                || yeuCau.getTrangThai() != TrangThai.DA_DUYET
                || yeuCau.getLichHop() == null) {
            return null;
        }
        var lichHienHanh = ThaoTacCuocHop.lichHienHanh(yeuCau.getCuocHop());
        return lichHienHanh == null ? yeuCau.getLichHop().getLichHopId() : lichHienHanh.getLichHopId();
    }

    private PhanHoiKeHoach layPhanHoiKeHoach(CuocHop ch) {
        if (!ThaoTacCuocHop.dangLapKeHoach(ch) && !"TU_CHOI".equals(ch.getTrangThai())) {
            return null;
        }
        var yeuCau = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(ch.getCuocHopId());
        if (yeuCau == null || (yeuCau.getTrangThai() != TrangThai.TRA_LAI
                && yeuCau.getTrangThai() != TrangThai.TU_CHOI)) {
            return null;
        }
        String hanhDong = yeuCau.getTrangThai() == TrangThai.TRA_LAI ? "YEU_CAU_CHINH_SUA" : "TU_CHOI";
        var lichSu = lichSuPheDuyetRepository
                .findFirstByYeuCauPheDuyet_YeuCauPheDuyetIdAndLoaiPheDuyetAndHanhDongOrderByThoiGianPheDuyetDescLichSuPheDuyetIdDesc(
                        yeuCau.getYeuCauPheDuyetId(), "KE_HOACH", hanhDong)
                .orElse(null);
        return new PhanHoiKeHoach(yeuCau.getYeuCauPheDuyetId(), yeuCau.getTrangThai().name(),
                lichSu == null ? null : lichSu.getYKien(),
                lichSu == null ? null : lichSu.getNguoiPheDuyet().getHoTen(),
                lichSu == null ? null : lichSu.getThoiGianPheDuyet());
    }

    private NguoiDung chonThuKy(
            Integer thuKyDuocChonId,
            Integer nguoiDeXuatId,
            CuocHop cuocHop,
            boolean batBuoc) {

        // Ưu tiên 1: người đề xuất chủ động chọn.
        if (thuKyDuocChonId != null) {
            return kiemTraThuKy(thuKyDuocChonId);
        }

        // Ưu tiên 2: giữ người đã được chọn trong bản nháp.
        if (cuocHop != null && cuocHop.getCuocHopId() != null) {

            var danhSachThuKy = thanhVienCuocHopRepository
                    .findByCuocHop_CuocHopIdAndVaiTroTaiCuocHop(
                            cuocHop.getCuocHopId(),
                            "THU_KY");

            if (danhSachThuKy.size() > 1) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cuộc họp đang có nhiều thư ký, vui lòng chọn lại người phụ trách");
            }

            if (!danhSachThuKy.isEmpty()) {
                Integer thuKyDaChonId = danhSachThuKy
                        .get(0)
                        .getId()
                        .getNguoiDungId();

                return kiemTraThuKy(thuKyDaChonId);
            }
        }

        // Ưu tiên 3: thư ký mặc định của người đề xuất.
        var phanCong = phanCongThuKyRepository
                .findById(nguoiDeXuatId)
                .orElse(null);

        if (phanCong == null) {
            if (batBuoc) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Chưa có thư ký mặc định, vui lòng chọn người làm thư ký cuộc họp");
            }

            // Vẫn cho phép lưu nháp khi chưa có thư ký.
            return null;
        }

        Integer thuKyMacDinhId = phanCong.getThuKy().getNguoiDungId();

        NguoiDung thuKy = kiemTraThuKy(thuKyMacDinhId);

        // Chỉ bắt buộc vai trò hệ thống khi lấy từ phân công mặc định.
        if (!phanQuyenCuocHopService.coVaiTro(
                thuKyMacDinhId,
                "THU_KY")) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Thư ký mặc định không còn vai trò THU_KY, vui lòng chọn người khác hoặc cập nhật phân công");
        }

        return thuKy;
    }
    private static final Set<String> LOAI_HINH_HOP_LE =
            Set.of("TRUC_TIEP", "TRUC_TUYEN");
    private static final Set<String> PHAM_VI_HOP_LE =
            Set.of("NOI_BO", "LIEN_PHONG_BAN", "TOAN_CO_QUAN", "BEN_NGOAI");

    private NguoiDung timNguoiChuTriHoatDong(Integer nguoiChuTriId) {
        NguoiDung nguoiChuTri = nguoiDungRepository.findById(nguoiChuTriId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Không tìm thấy người chủ trì"));

        if (!NguoiDungHienTaiService.dangHoatDong(nguoiChuTri)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Người chủ trì không còn hoạt động");
        }
        return nguoiChuTri;
    }

    private void kiemTraTieuDeVaLoaiHinh(String tieuDe, String loaiHinh) {
        if (tieuDe == null || tieuDe.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tiêu đề cuộc họp không được để trống");
        }
        if (tieuDe.trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tiêu đề không được vượt quá 255 ký tự");
        }
        if (loaiHinh == null || !LOAI_HINH_HOP_LE.contains(loaiHinh)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Loại hình phải là TRUC_TIEP hoặc TRUC_TUYEN");
        }
    }

    private CuocHop taoDeXuatNhap(TaoDeXuatCuocHopRequest request,
            NguoiDung nguoiDeXuat, PhongBan phongBan, NguoiDung chuTri) {
        String maCuocHop = "CH-" + UUID.randomUUID();
        if (maCuocHop.length() > 40) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã cuộc họp không được quá 40 ký tự");
        }
        return ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.taoDeXuatNhap(
                maCuocHop, nguoiDeXuat,
                request.getTieuDe().trim(), request.getMucTieu(), request.getNoiDungDuKien(),
                request.getLoaiHinh(), request.getPhamViCuocHop(),
                phongBan, chuTri, request.isCoKhachMoiBenNgoai()));
    }

    private void capNhatDeXuat(CuocHop cuocHop, TaoDeXuatCuocHopRequest request,
            PhongBan phongBan, NguoiDung chuTri) {
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.capNhatDeXuat(cuocHop,
                request.getTieuDe().trim(), request.getMucTieu(), request.getNoiDungDuKien(),
                request.getLoaiHinh(), request.getPhamViCuocHop(),
                phongBan, chuTri, request.isCoKhachMoiBenNgoai()));
    }
    
    private String xacDinhVaiTroNguoiDeXuat(CuocHop cuocHop) {
        NguoiDung nguoiDeXuat = cuocHop.getNguoiDeXuat();
        if (nguoiDeXuat == null) {
            return null;
        }

        Integer nguoiDungId = nguoiDeXuat.getNguoiDungId();

        if (phanQuyenCuocHopService.coVaiTro(nguoiDungId, "LANH_DAO")) {
            return "LANH_DAO";
        }
        if (phanQuyenCuocHopService.laTruongPhong(nguoiDungId)) {
            return "TRUONG_PHONG";
        }
        if (phanQuyenCuocHopService.coVaiTro(nguoiDungId, "THU_KY")) {
            return "THU_KY";
        }

        return null;
    }
}



