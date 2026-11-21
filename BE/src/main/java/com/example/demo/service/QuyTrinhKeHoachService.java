package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import com.example.demo.dto.request.TrinhKeHoachRequest;
import com.example.demo.dto.response.TrinhKeHoachResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.entity.*;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KhachMoiCuocHopRepository;
import com.example.demo.repository.LichHopRepository;
import com.example.demo.repository.PhanCongThuKyRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;
import com.example.demo.service.QuyTacDuyetKeHoach.CapPheDuyet;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QuyTrinhKeHoachService {
    private final CuocHopRepository cuocHopRepository;
    private final KhachMoiCuocHopRepository khachMoiCuocHopRepository;
    private final LichHopRepository lichHopRepository;
    private final XacNhanLichHopService xacNhanLichHopService;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final PhanCongThuKyRepository phanCongThuKyRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final LichSuPheDuyetService lichSuPheDuyetService;
    private final TaiLieuKeHoachService taiLieuKeHoachService;
    private final tools.jackson.databind.ObjectMapper objectMapper;
    private final ThongBaoService thongBaoService;
    
    public TrinhKeHoachResponse trinhKeHoachVaLayKetQua(
            Integer cuocHopId, TrinhKeHoachRequest request, Integer nguoiTrinhId) {
        Integer yeuCauId = trinhKeHoachCuocHop(cuocHopId,
                request == null ? null : request.getLichHopId(), nguoiTrinhId,
                request == null ? null : request.getGhiChuTrinh(),
                request == null ? null : request.getLanhDaoDuyetId(),
                request == null ? null : request.getTaiLieuIds());
        CuocHop ch = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        return new TrinhKeHoachResponse(
                "Đã trình kế hoạch, lịch họp và tài liệu được chọn",
                yeuCauId, ch.getTrangThai(), ch.getBuocXuLyHienTai());
    }

    // Chữ ký cũ vẫn hoạt động, không có đính kèm.
    public Integer trinhKeHoachCuocHop(Integer cuocHopId, Integer lichHopId,
            Integer nguoiTrinhId, String ghiChuTrinh, Integer lanhDaoDuyetId) {
        return trinhKeHoachCuocHop(cuocHopId, lichHopId, nguoiTrinhId,
                ghiChuTrinh, lanhDaoDuyetId, List.of());
    }

    public Integer trinhKeHoachCuocHop(Integer cuocHopId, Integer lichHopId,
            Integer nguoiTrinhId, String ghiChuTrinh, Integer lanhDaoDuyetId,
            List<Integer> taiLieuIds) {
        NguoiDung nguoiTrinh = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiTrinhId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        if (!phanCongXuLyKeHoachService.laThuKyDangXuLy(cuocHopId, nguoiTrinhId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký đang phụ trách được trình kế hoạch");
        }
        if (!ThaoTacCuocHop.dangLapKeHoach(ch) || ThaoTacCuocHop.daBatDau(ch) || ThaoTacCuocHop.daKetThuc(ch)) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp không còn ở bước lập kế hoạch");
        }
        List<TaiLieuCuocHop> taiLieuDaChon = taiLieuKeHoachService
                .kiemTraTruocKhiTrinh(ch, nguoiTrinhId, taiLieuIds);
        LichHop lich = lichHopId == null ? null : lichHopRepository.findById(lichHopId).orElse(null);
        var lanDaDuyet = phanCongXuLyKeHoachService.layLanDuyetCoLichDaXacNhan(cuocHopId);
        xacNhanLichHopService.kiemTraLichCuaLanTrinh(ch, lich, lichDaDuyet(lanDaDuyet), HttpStatus.BAD_REQUEST);
        boolean giuLichDaDuyet = lanDaDuyet != null
                && Objects.equals(lanDaDuyet.getLichHop().getLichHopId(), lichHopId);
        if (giuLichDaDuyet && (ghiChuTrinh == null || ghiChuTrinh.isBlank())) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập lý do cập nhật kế hoạch trong ghi chú trình");
        }
        if (lanDaDuyet != null && !giuLichDaDuyet
                && (lich.getLyDoThayDoi() == null || lich.getLyDoThayDoi().isBlank())) {
            if (ghiChuTrinh == null || ghiChuTrinh.isBlank()) {
                throw loi(HttpStatus.BAD_REQUEST, "Nhập lý do đề xuất lịch thay thế trong ghi chú trình");
            }
            lich.setLyDoThayDoi(ghiChuTrinh.trim());
        }
        if (ch.getNguoiChuTri() == null || !NguoiDungHienTaiService.dangHoatDong(ch.getNguoiChuTri())) {
            throw loi(HttpStatus.BAD_REQUEST, "Cần chọn người chủ trì đang hoạt động");
        }
        if (yeuCauPheDuyetRepository
                .existsByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThai(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.CHO_DUYET)) {
            throw loi(HttpStatus.CONFLICT, "Đã có kế hoạch đang chờ xử lý");
        }
        NguoiDung nguoiDeXuat = ch.getNguoiDeXuat();
        if (nguoiDeXuat == null) throw loi(HttpStatus.CONFLICT, "Thiếu người đề xuất");
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDeXuat.getNguoiDungId());
        Integer nguoiDeXuatId = nguoiDeXuat.getNguoiDungId();
        boolean doLanhDao = phanQuyenCuocHopService.coVaiTro(nguoiDeXuatId, "LANH_DAO");
        if (!doLanhDao && !phanQuyenCuocHopService.laTruongPhong(nguoiDeXuatId)) {
            throw loi(HttpStatus.CONFLICT, "Người đề xuất không còn là trưởng phòng hoặc lãnh đạo");
        }
        CapPheDuyet cap = doLanhDao ? CapPheDuyet.LANH_DAO : CapPheDuyet.TRUONG_PHONG;
        boolean canTrinhCapTren;
        try {
            canTrinhCapTren = QuyTacDuyetKeHoach.canTrinhCapTren(
                    doLanhDao, ch.getPhamViCuocHop(), coKhachNgoai(ch));
        } catch (IllegalArgumentException ex) {
            throw loi(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        if (doLanhDao && lanhDaoDuyetId != null && !lanhDaoDuyetId.equals(nguoiDeXuatId)) {
            throw loi(HttpStatus.BAD_REQUEST, "Đề xuất của lãnh đạo chỉ do chính lãnh đạo đó duyệt");
        }
        if (!doLanhDao && !canTrinhCapTren) {
            kiemTraTruongPhongNoiBo(ch, nguoiDeXuatId);
        }
        if (Objects.equals(nguoiTrinhId, nguoiDeXuatId)) {
            throw loi(HttpStatus.CONFLICT, "Người trình và người phê duyệt phải khác nhau");
        }
        kiemTraDuLieuKeHoach(ch);
        String buoc = doLanhDao ? "CHO_LANH_DAO" : "CHO_TRUONG_PHONG";
        YeuCauPheDuyet lanTrinhTruoc = phanCongXuLyKeHoachService
                .layLanTrinhMoiNhat(cuocHopId);
        YeuCauPheDuyet yc = ThaoTacCuocHop.thucHien(() ->
                ThaoTacYeuCauPheDuyet.taoYeuCauKeHoach(ch, nguoiTrinh,
                        nguoiDeXuat, null, cap.name(), ghiChuTrinh));
        yc.setYeuCauTruoc(lanTrinhTruoc);
        NguoiDung thuKyCuocHop = phanCongXuLyKeHoachService.layThuKyCuocHop(ch);
        if (thuKyCuocHop == null) throw loi(HttpStatus.BAD_REQUEST, "Chưa chọn thư ký cuộc họp");
        yc.setThuKyBanGiao(phanCongXuLyKeHoachService.chonThuKyCuocHop(ch, thuKyCuocHop.getNguoiDungId()));
        yc.setLichHop(lich);
        yc.setKeHoachDaTrinh(objectMapper.writeValueAsString(PheDuyetKeHoachService.taoBanChup(yc)));
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.trinhKeHoach(ch, buoc));
        yc.setThoiGianTrinh(LocalDateTime.now());
        YeuCauPheDuyet daLuu = yeuCauPheDuyetRepository.save(yc);
        taiLieuKeHoachService.ganVaoLanTrinh(daLuu, taiLieuDaChon);
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiTrinhId,
                "KE_HOACH", "TRINH_DUYET", ghiChuTrinh, daLuu);
        thongBaoService.gui(ch, nguoiTrinhId, "KE_HOACH_CHO_DUYET",
                "Có kế hoạch cuộc họp cần bạn xem xét", ch.getTieuDe(), nguoiDeXuat);
        return daLuu.getYeuCauPheDuyetId();
    }

    public TrinhKeHoachResponse guiKeHoachLenCapTren(
            Integer cuocHopId,
            Integer yeuCauTruocId,
            Integer thuKyId,
            Integer lanhDaoId,
            String ghiChu) {
        if (yeuCauTruocId == null) {
            throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã lần duyệt của trưởng phòng");
        }
        NguoiDung thuKy = nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        if (!phanCongXuLyKeHoachService.laThuKyBanDau(cuocHopId, thuKyId)
                || !phanQuyenCuocHopService.coVaiTro(thuKyId, "THU_KY")) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký ban đầu được chuyển hồ sơ lên cấp trên");
        }
        if (!"DANG_CHUAN_BI".equals(ch.getTrangThai())
                || !"CHO_GUI_CAP_TREN".equals(ch.getBuocXuLyHienTai())) {
            throw loi(HttpStatus.CONFLICT, "Hồ sơ chưa ở bước chờ gửi cấp trên");
        }

        YeuCauPheDuyet yeuCauTruoc = yeuCauPheDuyetRepository.findByIdForUpdate(yeuCauTruocId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy lần duyệt của trưởng phòng"));
        var lanTrinhMoiNhat = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHopId);
        if (lanTrinhMoiNhat == null || !yeuCauTruocId.equals(lanTrinhMoiNhat.getYeuCauPheDuyetId())
                || yeuCauTruoc.getLoaiYeuCau() != LoaiYeuCau.KE_HOACH_CUOC_HOP
                || !cuocHopId.equals(yeuCauTruoc.getCuocHop().getCuocHopId())
                || yeuCauTruoc.getTrangThai() != TrangThai.DA_DUYET
                || !"TRUONG_PHONG".equals(yeuCauTruoc.getCapPheDuyet())) {
            throw loi(HttpStatus.CONFLICT, "Lần duyệt trước không thuộc hồ sơ đang chờ chuyển cấp");
        }
        if (!QuyTacDuyetKeHoach.canTrinhCapTren(
                phanQuyenCuocHopService.coVaiTro(ch.getNguoiDeXuat().getNguoiDungId(), "LANH_DAO"),
                ch.getPhamViCuocHop(), coKhachNgoai(ch))) {
            throw loi(HttpStatus.CONFLICT, "Cuộc họp này không thuộc quy trình duyệt hai cấp");
        }
        if (lanhDaoId == null) {
            throw loi(HttpStatus.BAD_REQUEST, "Vui lòng chọn lãnh đạo nhận hồ sơ");
        }
        if (yeuCauPheDuyetRepository.existsByYeuCauTruoc_YeuCauPheDuyetId(yeuCauTruocId)) {
            throw loi(HttpStatus.CONFLICT, "Lần duyệt này đã được chuyển lên cấp trên");
        }
        xacNhanLichHopService.kiemTraLichCuaLanTrinh(ch, yeuCauTruoc.getLichHop(),
                lichDaDuyet(phanCongXuLyKeHoachService.layLanDuyetCoLichDaXacNhan(cuocHopId)), HttpStatus.CONFLICT);

        NguoiDung lanhDao = nguoiDungHienTaiService.layTaiKhoanHoatDong(lanhDaoId);
        if (!phanQuyenCuocHopService.coVaiTro(lanhDaoId, "LANH_DAO")) {
            throw loi(HttpStatus.BAD_REQUEST, "Người được chọn phải có vai trò LANH_DAO");
        }
        var phanCong = phanCongThuKyRepository.findById(lanhDaoId)
                .orElseThrow(() -> loi(HttpStatus.CONFLICT, "Lãnh đạo được chọn chưa có thư ký mặc định"));
        NguoiDung thuKyLanhDao = nguoiDungHienTaiService.layTaiKhoanHoatDong(
                phanCong.getThuKy().getNguoiDungId());
        if (!phanQuyenCuocHopService.coVaiTro(thuKyLanhDao.getNguoiDungId(), "THU_KY")) {
            throw loi(HttpStatus.CONFLICT, "Thư ký mặc định của lãnh đạo không còn vai trò THU_KY");
        }
        ganThuKyLanhDao(ch, thuKyLanhDao);
        taiLieuKeHoachService.kiemTraTepCuaLanTrinh(yeuCauTruoc);

        YeuCauPheDuyet yeuCauMoi = ThaoTacYeuCauPheDuyet.taoYeuCauKeHoach(
                ch, thuKy, lanhDao, thuKyLanhDao, CapPheDuyet.LANH_DAO.name(), ghiChu);
        yeuCauMoi.setYeuCauTruoc(yeuCauTruoc);
        yeuCauMoi.setThuKyBanGiao(yeuCauTruoc.getThuKyBanGiao());
        yeuCauMoi.setLichHop(yeuCauTruoc.getLichHop());
        yeuCauMoi.setThoiGianTrinh(LocalDateTime.now());
        YeuCauPheDuyet daLuu = yeuCauPheDuyetRepository.saveAndFlush(yeuCauMoi);
        daLuu.setKeHoachDaTrinh(objectMapper.writeValueAsString(
                PheDuyetKeHoachService.taoBanChup(daLuu)));
        taiLieuKeHoachService.saoChepTepLanTrinh(yeuCauTruoc, daLuu);
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.guiKeHoachCapTren(ch));
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, thuKyId,
                "KE_HOACH", "TRINH_CAP_TREN", ghiChu, daLuu);
        thongBaoService.gui(ch, thuKyId, "KE_HOACH_CHUYEN_CAP_TREN",
                "Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận",
                ch.getTieuDe(), thuKyLanhDao, lanhDao, ch.getNguoiDeXuat());
        return new TrinhKeHoachResponse(
                "Đã chuyển hồ sơ lên thư ký lãnh đạo; kế hoạch chưa được duyệt cuối",
                daLuu.getYeuCauPheDuyetId(), ch.getTrangThai(), ch.getBuocXuLyHienTai());
    }

    public TrinhKeHoachResponse trinhLaiLichSauHuy(
            Integer cuocHopId, Integer lichHopMoiId, Integer nguoiDungId, String ghiChuTrinh) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = timVaKhoaCuocHop(cuocHopId);
        YeuCauPheDuyet lanDaDuyet = phanCongXuLyKeHoachService.layLanDuyetCoLichDaXacNhan(cuocHopId);
        if (lanDaDuyet == null || lanDaDuyet.getLichHop() == null
                || !ThaoTacLichHop.daHuy(lanDaDuyet.getLichHop())
                || (!ThaoTacCuocHop.coTheLapLich(cuocHop) && !ThaoTacCuocHop.dangLapKeHoach(cuocHop))) {
            throw loi(HttpStatus.CONFLICT, "Chỉ trình lại khi lịch đã xác nhận bị hủy và cuộc họp chưa bắt đầu");
        }
        boolean laThuKyToChuc = lanDaDuyet.getThuKyBanGiao() == null
                ? phanCongXuLyKeHoachService.laThuKyBanDau(cuocHopId, nguoiDungId)
                : phanCongXuLyKeHoachService.laThuKyDaNhanBanGiao(lanDaDuyet, nguoiDungId);
        if (!laThuKyToChuc) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký được bàn giao cuộc họp được trình lịch thay thế");
        }

        LichHop lichMoi = lichHopMoiId == null ? null : lichHopRepository.findById(lichHopMoiId).orElse(null);
        xacNhanLichHopService.kiemTraLichTruocKhiTrinh(cuocHop, lichMoi, HttpStatus.BAD_REQUEST);
        String lyDo = ghiChuTrinh == null || ghiChuTrinh.isBlank() ? lichMoi.getLyDoThayDoi() : ghiChuTrinh;
        if (lyDo == null || lyDo.isBlank()) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập lý do đề xuất lịch thay thế");
        }
        if (ThaoTacCuocHop.coTheLapLich(cuocHop)) {
            ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.moLaiDeCapNhatKeHoach(cuocHop));
        }
        List<Integer> taiLieuIds = taiLieuKeHoachService.layIdTepLanTrinh(lanDaDuyet);
        Integer yeuCauId = trinhKeHoachCuocHop(cuocHopId, lichHopMoiId, nguoiDungId, lyDo.trim(), null, taiLieuIds);
        return new TrinhKeHoachResponse("Đã trình lịch thay thế", yeuCauId,
                cuocHop.getTrangThai(), cuocHop.getBuocXuLyHienTai());
    }

    public TrinhKeHoachResponse deXuatLaiLichKhiChoChuyenCap(
            Integer cuocHopId, Integer yeuCauId, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        if (!phanCongXuLyKeHoachService.laThuKyBanDau(cuocHopId, thuKyId)
                || !phanQuyenCuocHopService.coVaiTro(thuKyId, "THU_KY")) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký ban đầu được đề xuất lại lịch ở bước chuyển cấp");
        }

        var yeuCau = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHopId);
        if (!"CHO_GUI_CAP_TREN".equals(ch.getBuocXuLyHienTai()) || yeuCau == null
                || !yeuCauId.equals(yeuCau.getYeuCauPheDuyetId())
                || yeuCau.getTrangThai() != TrangThai.DA_DUYET
                || !"TRUONG_PHONG".equals(yeuCau.getCapPheDuyet())) {
            throw loi(HttpStatus.CONFLICT, "Hồ sơ không còn ở lần trình chờ chuyển cấp");
        }

        // Bảo toàn quyết định đã có. Lần trình mới sẽ quay lại trưởng phòng.
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.moLaiKeHoachChoChuyenCap(ch));
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, thuKyId, "KE_HOACH",
                "YEU_CAU_CHINH_SUA", "Thư ký mở lại hồ sơ để đề xuất lịch mới", yeuCau);
        thongBaoService.gui(ch, thuKyId, "KE_HOACH_MO_LAI",
                "Kế hoạch đã được mở lại để đề xuất lịch mới", ch.getTieuDe(),
                ch.getNguoiDeXuat(), yeuCau.getNguoiDuyet());
        return new TrinhKeHoachResponse("Đã mở lại kế hoạch. Hãy chọn lịch mới và trình lại trưởng phòng.",
                yeuCauId, ch.getTrangThai(), ch.getBuocXuLyHienTai());
    }

    public void tiepNhanHoSoLanhDao(Integer cuocHopId, Integer yeuCauId, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        YeuCauPheDuyet yc = timVaKhoaYeuCau(ch, yeuCauId);
        kiemTraThuKyNhan(yc, thuKyId);
        if (!"CHO_THU_KY_LANH_DAO".equals(ch.getBuocXuLyHienTai())) {
            throw loi(HttpStatus.CONFLICT, "Hồ sơ không còn chờ thư ký lãnh đạo tiếp nhận");
        }
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.tiepNhanHoSoLanhDao(ch));
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, thuKyId,
                "KE_HOACH", "TIEP_NHAN", "Thư ký lãnh đạo tiếp nhận hồ sơ", yc);
        thongBaoService.gui(ch, thuKyId, "KE_HOACH_DA_TIEP_NHAN",
                "Thư ký lãnh đạo đã tiếp nhận hồ sơ", ch.getTieuDe(),
                ch.getNguoiDeXuat(), yc.getNguoiTrinh(), yc.getNguoiDuyet());
    }

    public void trinhLanhDao(Integer cuocHopId, Integer yeuCauId, Integer thuKyId, String ghiChu) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        YeuCauPheDuyet yc = timVaKhoaYeuCau(ch, yeuCauId);
        kiemTraThuKyNhan(yc, thuKyId);
        if (!"THU_KY_LANH_DAO_XU_LY".equals(ch.getBuocXuLyHienTai())) {
            throw loi(HttpStatus.CONFLICT, "Phải tiếp nhận hồ sơ trước khi trình lãnh đạo");
        }
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.trinhLanhDao(ch));
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, thuKyId,
                "KE_HOACH", "TRINH_DUYET", ghiChu, yc);
        thongBaoService.gui(ch, thuKyId, "KE_HOACH_CHO_LANH_DAO",
                "Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt",
                ch.getTieuDe(), yc.getNguoiDuyet(), yc.getNguoiTrinh(), ch.getNguoiDeXuat());
    }

    public boolean pheDuyetKeHoach(Integer cuocHopId, Integer yeuCauId, Integer nguoiDuyetId, String yKien) {
        NguoiDung nguoiDuyet = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDuyetId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        YeuCauPheDuyet yc = timVaKhoaYeuCau(ch, yeuCauId);
        kiemTraNguoiDuyet(yc, nguoiDuyetId);
        taiLieuKeHoachService.kiemTraTepCuaLanTrinh(yc);
        if (yc.getThuKyBanGiao() != null) {
            phanCongXuLyKeHoachService.chonThuKyCuocHop(ch, yc.getThuKyBanGiao().getNguoiDungId());
        }
        boolean dongYChuyenCap = "TRUONG_PHONG".equals(yc.getCapPheDuyet())
                && QuyTacDuyetKeHoach.canTrinhCapTren(
                        phanQuyenCuocHopService.coVaiTro(ch.getNguoiDeXuat().getNguoiDungId(), "LANH_DAO"),
                        ch.getPhamViCuocHop(), coKhachNgoai(ch));
        LichHop lich = yc.getLichHop();
        LichHop lichDaDuyet = lichDaDuyet(phanCongXuLyKeHoachService.layLanDuyetCoLichDaXacNhan(cuocHopId));
        if (dongYChuyenCap || (lich != null && ThaoTacLichHop.daXacNhan(lich))) {
            xacNhanLichHopService.kiemTraLichCuaLanTrinh(ch, lich, lichDaDuyet, HttpStatus.CONFLICT);
        } else {
            xacNhanLichHopService.xacNhan(ch, lich, nguoiDuyet, yc);
        }
        ThaoTacCuocHop.thucHien(() -> {
            ThaoTacYeuCauPheDuyet.pheDuyet(yc);
            if (dongYChuyenCap) {
                ThaoTacCuocHop.dongYTrinhCapTren(ch);
            } else {
                ThaoTacCuocHop.pheDuyetKeHoach(ch);
            }
        });
        if (dongYChuyenCap) {
            lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiDuyetId,
                    "KE_HOACH", "DONG_Y_TRINH_CAP_TREN", yKien, yc);
            thongBaoService.gui(ch, nguoiDuyetId, "KE_HOACH_DONG_Y_CAP_TREN",
                    "Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên",
                    ch.getTieuDe(), phanCongXuLyKeHoachService.layThuKyBanDau(cuocHopId),
                    yc.getNguoiTrinh());
            return false;
        }
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiDuyetId,
                "KE_HOACH", "PHE_DUYET", yKien, yc);
        thongBaoService.gui(ch, nguoiDuyetId, "KE_HOACH_DA_DUYET",
                "Kế hoạch và lịch họp đã được duyệt", ch.getTieuDe(),
                ch.getNguoiDeXuat(), yc.getNguoiTrinh(), yc.getThuKyBanGiao(), yc.getThuKyTiepNhan());
        return true;
    }

    public void tuChoiTiepNhan(Integer cuocHopId, Integer yeuCauId, Integer thuKyId, String lyDo) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        YeuCauPheDuyet yc = timVaKhoaYeuCau(ch, yeuCauId);
        kiemTraThuKyNhan(yc, thuKyId);
        if (!"CHO_THU_KY_LANH_DAO".equals(ch.getBuocXuLyHienTai())) {
            throw loi(HttpStatus.CONFLICT, "Hồ sơ không còn chờ tiếp nhận");
        }
        if (lyDo == null || lyDo.isBlank()) throw loi(HttpStatus.BAD_REQUEST, "Vui lòng nhập lý do");
        ThaoTacYeuCauPheDuyet.tuChoi(yc);
        ch.setTrangThai("TU_CHOI");
        ch.setBuocXuLyHienTai("KET_THUC");
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, thuKyId, "KE_HOACH", "TU_CHOI", lyDo.trim(), yc);
        thongBaoService.gui(ch, thuKyId, "KE_HOACH_TU_CHOI_TIEP_NHAN",
                "Thư ký lãnh đạo đã từ chối tiếp nhận hồ sơ",
                lyDo.trim(), yc.getNguoiTrinh(), ch.getNguoiDeXuat());
    }

    public void tuChoiKeHoach(Integer cuocHopId, Integer yeuCauId, Integer nguoiDuyetId, String lyDo) {
        xuLyKhongDuyet(cuocHopId, yeuCauId, nguoiDuyetId, lyDo, false);
    }

    public void traLaiKeHoach(Integer cuocHopId, Integer yeuCauId, Integer nguoiThucHienId, String lyDo) {
        xuLyKhongDuyet(cuocHopId, yeuCauId, nguoiThucHienId, lyDo, true);
    }

    private void xuLyKhongDuyet(Integer cuocHopId, Integer yeuCauId, Integer nguoiId, String lyDo, boolean traLai) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        if (lyDo == null || lyDo.isBlank()) throw loi(HttpStatus.BAD_REQUEST, "Vui lòng nhập lý do");
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        YeuCauPheDuyet yc = timVaKhoaYeuCau(ch, yeuCauId);
        boolean thuKyDuocTraLai = traLai && yc.getThuKyTiepNhan() != null
                && nguoiId.equals(yc.getThuKyTiepNhan().getNguoiDungId())
                && ("CHO_THU_KY_LANH_DAO".equals(ch.getBuocXuLyHienTai())
                || "THU_KY_LANH_DAO_XU_LY".equals(ch.getBuocXuLyHienTai()));
        if (thuKyDuocTraLai) kiemTraThuKyNhan(yc, nguoiId);
        else kiemTraNguoiDuyet(yc, nguoiId);
        ThaoTacCuocHop.thucHien(() -> {
            if (traLai) {
                ThaoTacCuocHop.traLaiKeHoach(ch);
                ThaoTacYeuCauPheDuyet.traLai(yc);
            } else {
                ThaoTacCuocHop.tuChoiKeHoach(ch);
                ThaoTacYeuCauPheDuyet.tuChoi(yc);
            }
        });
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiId, "KE_HOACH",
                traLai ? "YEU_CAU_CHINH_SUA" : "TU_CHOI", lyDo.trim(), yc);
        thongBaoService.gui(ch, nguoiId,
                traLai ? "KE_HOACH_YEU_CAU_CHINH_SUA" : "KE_HOACH_TU_CHOI",
                traLai ? "Kế hoạch cần được chỉnh sửa" : "Kế hoạch đã bị từ chối",
                lyDo.trim(), yc.getNguoiTrinh(), ch.getNguoiDeXuat());
    }

    @Transactional(readOnly = true)
    public void kiemTraQuyenXacNhanLich(CuocHop ch, Integer nguoiId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        // Chỉ kiểm tra thẩm quyền ở đây: hàm này cũng được dùng khi hủy lịch.
        // Điều kiện giai đoạn để XÁC NHẬN nằm ở LichHopService.xacNhanLichHop.
        YeuCauPheDuyet yc = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(ch.getCuocHopId());
        if (yc == null || yc.getTrangThai() != TrangThai.DA_DUYET) {
            throw loi(HttpStatus.CONFLICT, "Không tìm thấy kế hoạch đã duyệt theo luồng mới");
        }
        kiemTraDanhTinhVaCapDuyet(yc, nguoiId);
    }

    @Transactional(readOnly = true)
    public boolean coQuyenXacNhanLich(CuocHop cuocHop, Integer nguoiDungId) {
        try {
            kiemTraQuyenXacNhanLich(cuocHop, nguoiDungId);
            return true;
        } catch (ResponseStatusException ex) {
            int status = ex.getStatusCode().value();
            if (status == 403 || status == 409) return false;
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public void kiemTraDuocMoiKhachNgoai(CuocHop ch, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        if (!phanCongXuLyKeHoachService.laThuKyDangXuLy(ch.getCuocHopId(), thuKyId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký đang phụ trách được mời khách");
        }
        YeuCauPheDuyet yc = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(ch.getCuocHopId());
        if (!ThaoTacCuocHop.coTheLapLich(ch) || !ThaoTacCuocHop.coLichDaXacNhan(ch) || !coKhachNgoai(ch)
                || yc == null || yc.getTrangThai() != TrangThai.DA_DUYET
                || !"LANH_DAO".equals(yc.getCapPheDuyet())) {
            throw loi(HttpStatus.CONFLICT, "Khách ngoài phải được khai báo và lãnh đạo duyệt kế hoạch trước khi gửi lời mời");
        }
    }

    /** Chỉ dùng trước khi có lịch còn hiệu lực; không sửa lịch sử quyết định cũ. */
    public void moLaiDeBoSungKhachNgoai(Integer cuocHopId, Integer nguoiId, String lyDo) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiId);
        CuocHop ch = timVaKhoaCuocHop(cuocHopId);
        if (!ThaoTacCuocHop.laNguoiDeXuat(ch, nguoiId)
                && !phanCongXuLyKeHoachService.laThuKyDangXuLy(cuocHopId, nguoiId)) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn không phụ trách hồ sơ này");
        }
        if (!ThaoTacCuocHop.coTheLapLich(ch) || lyDo == null || lyDo.isBlank()) {
            throw loi(HttpStatus.CONFLICT, "Chỉ mở lại kế hoạch đã duyệt, chưa bắt đầu và phải có lý do");
        }
        boolean coLichConHieuLuc = lichHopRepository.existsByCuocHop_CuocHopIdAndTrangThaiIn(
                cuocHopId, List.of("DE_XUAT", "DA_XAC_NHAN"));
        if (coLichConHieuLuc) throw loi(HttpStatus.CONFLICT,
                "Phải xử lý hủy lịch và thông báo người đã được mời trước khi mở lại kế hoạch");
        ThaoTacCuocHop.thucHien(() -> ThaoTacCuocHop.moLaiDeBoSungKhachNgoai(ch));
        lichSuPheDuyetService.ghiNhan(cuocHopId, null, null, nguoiId, "KE_HOACH",
                "YEU_CAU_CHINH_SUA", "Mở lại để bổ sung khách ngoài: " + lyDo.trim(),
                phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHopId));
    }

    @Transactional(readOnly = true)
    public boolean coQuyenXuLyQuyetDinh(YeuCauPheDuyet yeuCau, Integer nguoiDungId) {
        if (yeuCau == null || nguoiDungId == null) return false;
        try {
            kiemTraDanhTinhVaCapDuyet(yeuCau, nguoiDungId);
            return true;
        } catch (ResponseStatusException ex) {
            int trangThai = ex.getStatusCode().value();
            if (trangThai == 403 || trangThai == 409) return false;
            throw ex;
        }
    }

    private void kiemTraNguoiDuyet(YeuCauPheDuyet yc, Integer nguoiId) {
        kiemTraDanhTinhVaCapDuyet(yc, nguoiId);
        String buoc = "TRUONG_PHONG".equals(yc.getCapPheDuyet())
                ? "CHO_TRUONG_PHONG" : "CHO_LANH_DAO";
        if (!buoc.equals(yc.getCuocHop().getBuocXuLyHienTai())) {
            throw loi(HttpStatus.CONFLICT, "Hồ sơ chưa được trình đến đúng bước phê duyệt");
        }
    }

    private void kiemTraDanhTinhVaCapDuyet(YeuCauPheDuyet yc, Integer nguoiId) {
        if (yc.getNguoiDuyet() == null
                || !Objects.equals(nguoiId, yc.getNguoiDuyet().getNguoiDungId())) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn không phải người được chỉ định duyệt yêu cầu này");
        }
        CuocHop ch = yc.getCuocHop();
        if ("TRUONG_PHONG".equals(yc.getCapPheDuyet())) {
            boolean canTrinhCapTren = QuyTacDuyetKeHoach.canTrinhCapTren(
                    phanQuyenCuocHopService.coVaiTro(ch.getNguoiDeXuat().getNguoiDungId(), "LANH_DAO"),
                    ch.getPhamViCuocHop(), coKhachNgoai(ch));
            if (canTrinhCapTren) {
                if (!ThaoTacCuocHop.laNguoiDeXuat(ch, nguoiId)
                        || !phanQuyenCuocHopService.laTruongPhong(nguoiId)) {
                    throw loi(HttpStatus.FORBIDDEN, "Chỉ trưởng phòng đề xuất được đồng ý chuyển hồ sơ lên cấp trên");
                }
            } else {
                kiemTraTruongPhongNoiBo(ch, nguoiId);
            }
        } else if ("LANH_DAO".equals(yc.getCapPheDuyet())) {
            if (!phanQuyenCuocHopService.coVaiTro(nguoiId, "LANH_DAO")) {
                throw loi(HttpStatus.FORBIDDEN, "Người duyệt không còn vai trò lãnh đạo");
            }
        } else throw loi(HttpStatus.CONFLICT, "Yêu cầu cũ chưa có cấp duyệt, cần rà soát chuyển đổi");
    }

    private void kiemTraTruongPhongNoiBo(CuocHop ch, Integer nguoiId) {
        if (!ThaoTacCuocHop.laCuocHopNoiBo(ch) || coKhachNgoai(ch) || ch.getPhongBanChuTri() == null
                || !ThaoTacCuocHop.laNguoiDeXuat(ch, nguoiId)
                || phanQuyenCuocHopService.coVaiTro(nguoiId, "LANH_DAO")
                || !phanQuyenCuocHopService.laTruongPhongCuaPhongBan(
                        nguoiId, ch.getPhongBanChuTri().getPhongBanId())) {
            throw loi(HttpStatus.FORBIDDEN, "Trưởng phòng chỉ duyệt nội bộ phòng mình, không có khách ngoài");
        }
    }

    private void kiemTraDuLieuKeHoach(CuocHop ch) {
        if (ch.getTieuDe() == null || ch.getTieuDe().isBlank()
                || ch.getTieuDe().trim().length() > 255) {
            throw loi(HttpStatus.BAD_REQUEST, "Tiêu đề phải có từ 1 đến 255 ký tự");
        }
        if (ch.getLoaiHinh() == null
                || !java.util.Set.of("TRUC_TIEP", "TRUC_TUYEN").contains(ch.getLoaiHinh())) {
            throw loi(HttpStatus.BAD_REQUEST, "Loại hình cuộc họp không hợp lệ");
        }
        if (ThaoTacCuocHop.laCuocHopNoiBo(ch) && ch.getPhongBanChuTri() == null) {
            throw loi(HttpStatus.BAD_REQUEST, "Họp nội bộ phải có phòng ban chủ trì");
        }
    }

    private LichHop lichDaDuyet(YeuCauPheDuyet yeuCau) {
        return yeuCau == null ? null : yeuCau.getLichHop();
    }

    private boolean coKhachNgoai(CuocHop ch) {
        return Boolean.TRUE.equals(ch.getCoKhachMoiBenNgoai())
                || khachMoiCuocHopRepository.existsByCuocHop_CuocHopIdAndTrangThaiMoiNot(
                        ch.getCuocHopId(), "DA_HUY");
    }

    private void kiemTraThuKyNhan(YeuCauPheDuyet yc, Integer nguoiId) {
        if (yc.getThuKyTiepNhan() == null
                || !Objects.equals(nguoiId, yc.getThuKyTiepNhan().getNguoiDungId())
                || !phanQuyenCuocHopService.coVaiTro(nguoiId, "THU_KY")) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ thư ký lãnh đạo được chỉ định mới được xử lý hồ sơ");
        }
    }

    private void ganThuKyLanhDao(CuocHop ch, NguoiDung thuKy) {
        ThanhVienCuocHopId id = new ThanhVienCuocHopId();
        id.setCuocHopId(ch.getCuocHopId());
        id.setNguoiDungId(thuKy.getNguoiDungId());
        ThanhVienCuocHop tv = thanhVienCuocHopRepository.findById(id).orElse(null);
        if (tv != null) {
            if (!List.of("THU_KY", "THU_KY_LANH_DAO").contains(tv.getVaiTroTaiCuocHop())) {
                throw loi(HttpStatus.CONFLICT, "Thư ký lãnh đạo đang có vai trò khác trong cuộc họp");
            }
            return;
        }
        tv = new ThanhVienCuocHop();
        tv.setId(id); tv.setCuocHop(ch); tv.setNguoiDung(thuKy);
        tv.setVaiTroTaiCuocHop("THU_KY_LANH_DAO");
        tv.setTrangThaiMoi("CHO_PHAN_HOI");
        tv.setTrangThaiThamDu("CHUA_DIEM_DANH");
        thanhVienCuocHopRepository.save(tv);
    }

    private CuocHop timVaKhoaCuocHop(Integer id) {
        if (id == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
        return cuocHopRepository.findByIdForUpdate(id)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
    }

    private YeuCauPheDuyet timVaKhoaYeuCau(CuocHop ch, Integer id) {
        if (id == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã yêu cầu phê duyệt");
        YeuCauPheDuyet yc = yeuCauPheDuyetRepository.findByIdForUpdate(id).orElse(null);
        if (yc == null || yc.getLoaiYeuCau() != LoaiYeuCau.KE_HOACH_CUOC_HOP
                || !ch.getCuocHopId().equals(yc.getCuocHop().getCuocHopId())) {
            throw loi(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu của cuộc họp");
        }
        if (yc.getTrangThai() != TrangThai.CHO_DUYET || ThaoTacCuocHop.daBatDau(ch) || ThaoTacCuocHop.daKetThuc(ch)
                || !"DANG_CHUAN_BI".equals(ch.getTrangThai())) {
            throw loi(HttpStatus.CONFLICT, "Yêu cầu không còn chờ xử lý");
        }
        YeuCauPheDuyet moiNhat = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(ch.getCuocHopId());
        if (moiNhat == null || !id.equals(moiNhat.getYeuCauPheDuyetId())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ xử lý lần trình mới nhất");
        }
        return yc;
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}



