package com.example.demo.service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.response.LichHopResponse;
import com.example.demo.dto.request.CapNhatLichHopRequest;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.LichHopRepository;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.PhongHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
@Transactional
public class LichHopService {
    private final JdbcClient jdbc;
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final ThongBaoService thongBao;
    private final PhanCongXuLyKeHoachService phanCong;
    private final ThongBaoEmailCuocHop thongBaoEmail;
    private final HuyCuocHopService huyCuocHopService;
    private final LichHopRepository lichHopRepository;
    private final QuyTrinhKeHoachService quyTrinhKeHoachService;
    private final XacNhanLichHopService xacNhanLichHopService;
    private final CuocHopRepository cuocHopRepository;
    private final PhongHopRepository phongHopRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LichSuPheDuyetService lichSuPheDuyetService;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;

    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    public record LuaChonPhongHop(Integer phongHopId, String tenPhongHop, String viTri,
            Integer sucChua, String moTa, String trangThai, Boolean trungLich) {}

    @Transactional(readOnly = true)
    public List<LuaChonPhongHop> layPhongHopDeXuat(Integer cuocHopId, Integer nguoiDungId,
            LocalDateTime batDau, LocalDateTime ketThuc) {
        phanQuyenCuocHopService.kiemTraLaThuKy(cuocHopId, nguoiDungId);
        if (batDau != null || ketThuc != null) {
            if (batDau == null || ketThuc == null || !ketThuc.isAfter(batDau)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoảng giờ kiểm tra không hợp lệ");
            }
        }
        LichHop lichDangDung = batDau == null ? null : xacNhanLichHopService.layLichDaXacNhan(cuocHopId);
        Integer lichDangDungId = lichDangDung == null ? -1 : lichDangDung.getLichHopId();
        return phongHopRepository.findAll().stream()
                .map(phong -> new LuaChonPhongHop(phong.getPhongHopId(), phong.getTenPhongHop(),
                        phong.getViTri(), phong.getSucChua(), phong.getMoTa(), phong.getTrangThai(),
                        batDau == null ? null : lichHopRepository.existsLichHopTrung(
                                phong.getPhongHopId(), lichDangDungId, batDau, ketThuc)))
                .toList();
    }

    // =====================================================
    // LẤY DANH SÁCH LỊCH HỌP
    // =====================================================
    @Transactional(readOnly = true)
    public List<LichHopResponse> layDanhSachLichHop(Integer nguoiDungId) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        if (phanQuyenCuocHopService.coVaiTro(nguoiDungId, "ADMIN")) {
            return lichHopRepository.findAll().stream().map(this::chuyenDoiResponse).toList();
        }
        return lichHopRepository
                .findLichHopCuaNguoiDung(nguoiDungId, LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .stream()
                .map(this::chuyenDoiResponse)
                .toList();
    }

    // =====================================================
    // LẤY LỊCH THEO CUỘC HỌP
    // =====================================================
    @Transactional(readOnly = true)
    public List<LichHopResponse> layLichTheoCuocHop(
            Integer cuocHopId,
            Integer nguoiDungId) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        kiemTraMaHopLe(cuocHopId, "cuộc họp");
        if (!cuocHopRepository.existsById(cuocHopId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp");
        }
        kiemTraQuyenXemCuocHop(cuocHopId, nguoiDungId);
        return lichHopRepository
                .findByCuocHop_CuocHopIdOrderByThoiGianBatDauAsc(cuocHopId)
                .stream()
                .map(this::chuyenDoiResponse)
                .toList();
    }
    // =====================================================
    // XEM CHI TIẾT LỊCH HỌP
    // =====================================================
    @Transactional(readOnly = true)
    public LichHopResponse layChiTiet(
            Integer lichHopId,
            Integer nguoiDungId) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        LichHop lichHop = timLichHop(lichHopId);
        kiemTraQuyenXemCuocHop(
                lichHop.getCuocHop().getCuocHopId(),
                nguoiDungId
        );
        return chuyenDoiResponse(lichHop);
    }
    public record ChiTietLichCalendar(LichHopResponse lich, LuaChonPhongHop phong,
            String mucTieu, String noiDungDuKien, Integer yeuCauPheDuyetId,
            String ghiChuTrinh, boolean duocXacNhan, boolean duocXemThanhVien,
            boolean duocHuyLich, boolean duocHuyCuocHop, boolean duocGiuLichCu) {}

    @Transactional(readOnly = true)
    public ChiTietLichCalendar layChiTietCalendar(Integer lichHopId, Integer nguoiDungId) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        LichHop lich = timLichHop(lichHopId);
        CuocHop ch = lich.getCuocHop();
        Integer cuocHopId = ch.getCuocHopId();
        kiemTraQuyenXemCuocHop(cuocHopId, nguoiDungId);
        var yc = yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP).orElse(null);
        boolean duocGiuLichCu = lich.isLaLichDoi() && ThaoTacLichHop.dangDeXuat(lich)
                && lich.getLichThayThe() != null && ThaoTacLichHop.daXacNhan(lich.getLichThayThe())
                && lich.getLichThayThe().getThoiGianBatDau().isAfter(LocalDateTime.now())
                && quyTrinhKeHoachService.coQuyenXacNhanLich(ch, nguoiDungId);
        boolean duocXacNhan = ThaoTacCuocHop.coTheLapLich(ch) && ThaoTacLichHop.coTheXacNhan(lich)
                && lich.getThoiGianBatDau() != null && lich.getThoiGianBatDau().isAfter(LocalDateTime.now())
                && (lich.isLaLichDoi()
                        || (yc != null && yc.getLichHop() != null && lichHopId.equals(yc.getLichHop().getLichHopId())))
                && quyTrinhKeHoachService.coQuyenXacNhanLich(ch, nguoiDungId);
        boolean nguoiDeXuat = ThaoTacCuocHop.laNguoiDeXuat(ch, nguoiDungId);
        boolean xemTep = yc != null && (nguoiDeXuat || phanQuyenCuocHopService.coVaiTro(nguoiDungId, "ADMIN")
                || (yc.getNguoiTrinh() != null && nguoiDungId.equals(yc.getNguoiTrinh().getNguoiDungId()))
                || (yc.getNguoiDuyet() != null && nguoiDungId.equals(yc.getNguoiDuyet().getNguoiDungId()))
                || (yc.getThuKyTiepNhan() != null && nguoiDungId.equals(yc.getThuKyTiepNhan().getNguoiDungId())));
        boolean xemThanhVien = nguoiDeXuat || ThaoTacCuocHop.laNguoiChuTri(ch, nguoiDungId)
                || phanQuyenCuocHopService.laThuKy(cuocHopId, nguoiDungId);
        boolean xemKeHoach = xemTep || ThaoTacCuocHop.laNguoiChuTri(ch, nguoiDungId)
                || phanQuyenCuocHopService.coQuyenXemChiTietDeXuat(ch, nguoiDungId);
        PhongHop phong = lich.getPhongHop();
        return new ChiTietLichCalendar(chuyenDoiResponse(lich), phong == null ? null : new LuaChonPhongHop(
                phong.getPhongHopId(), phong.getTenPhongHop(), phong.getViTri(), phong.getSucChua(),
                phong.getMoTa(), phong.getTrangThai(), null),
                xemKeHoach ? ch.getMucTieu() : null, xemKeHoach ? ch.getNoiDungDuKien() : null,
                xemTep ? yc.getYeuCauPheDuyetId() : null,
                xemTep ? yc.getGhiChuTrinh() : null, duocXacNhan, xemThanhVien,
                duocHuyLich(lich, nguoiDungId), huyCuocHopService.duocHuy(ch, nguoiDungId), duocGiuLichCu);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LichHopResponse doiLich(Integer lichCuId, Integer nguoiDungId, CapNhatLichHopRequest request) {
        LichHop lichCu = timLichHopVaKhoa(lichCuId);
        CuocHop hop = lichCu.getCuocHop();
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        if (!phanCong.laThuKyToChuc(hop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ thư ký tổ chức được dời lịch");
        }
        if (!ThaoTacCuocHop.coTheLapLich(hop) || !ThaoTacLichHop.daXacNhan(lichCu)
                || ThaoTacCuocHop.choXacNhanDoiLich(hop)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cần lịch đã xác nhận và chưa có yêu cầu dời lịch đang chờ xử lý");
        }
        if (request == null || request.lyDoThayDoi() == null || request.lyDoThayDoi().isBlank()
                || request.lyDoThayDoi().strip().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nhập lý do dời lịch tối đa 500 ký tự");
        }
        kiemTraThoiGian(request.thoiGianBatDau(), request.thoiGianKetThuc());
        if (request.thoiGianBatDau().equals(lichCu.getThoiGianBatDau())
                && request.thoiGianKetThuc().equals(lichCu.getThoiGianKetThuc())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khung giờ mới phải khác lịch hiện tại");
        }
        var keHoach = yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        hop.getCuocHopId(), LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .filter(yc -> yc.getTrangThai() == TrangThai.DA_DUYET)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Cần kế hoạch đã duyệt"));
        var lichMoi = ThaoTacLichHop.taoDeXuat(hop, lichCu.getPhongHop(), timNguoiDung(nguoiDungId),
                request.thoiGianBatDau(), request.thoiGianKetThuc(), request.lyDoThayDoi().strip());
        lichMoi.setLaLichDoi(true);
        ThaoTacLichHop.ganVoiLichBiThayThe(lichMoi, lichCu);
        lichHopRepository.saveAndFlush(lichMoi);
        xacNhanLichHopService.kiemTraLichTruocKhiTrinh(hop, lichMoi, HttpStatus.CONFLICT);
        lichSuPheDuyetService.ghiNhan(hop.getCuocHopId(), lichMoi.getLichHopId(), null,
                nguoiDungId, "LICH_HOP", "DOI_LICH", lichMoi.getLyDoThayDoi());
        var nguoiNhan = new java.util.ArrayList<>(nguoiDaDuocMoi(hop));
        jdbc.sql("""
                INSERT INTO phan_hoi_doi_lich (lich_hop_id, nguoi_dung_id)
                SELECT :lich, tv.nguoi_dung_id
                FROM thanh_vien_cuoc_hop tv JOIN nguoi_dung nd ON nd.nguoi_dung_id = tv.nguoi_dung_id
                WHERE tv.cuoc_hop_id = :hop AND tv.vai_tro_tai_cuoc_hop = 'THAM_DU'
                  AND tv.trang_thai_moi IN ('CHO_PHAN_HOI', 'DA_XAC_NHAN') AND nd.trang_thai = 'HOAT_DONG'
                """).param("lich", lichMoi.getLichHopId()).param("hop", hop.getCuocHopId()).update();
        jdbc.sql("""
                INSERT INTO phan_hoi_doi_lich_khach (lich_hop_id, khach_moi_cuoc_hop_id)
                SELECT :lich, tv.khach_moi_cuoc_hop_id
                FROM khach_moi_cuoc_hop tv JOIN khach_moi km ON km.khach_moi_id = tv.khach_moi_id
                WHERE tv.cuoc_hop_id = :hop AND tv.trang_thai_moi IN ('CHO_PHAN_HOI', 'DONG_Y')
                  AND km.trang_thai = 'HOAT_DONG'
                """).param("lich", lichMoi.getLichHopId()).param("hop", hop.getCuocHopId()).update();
        nguoiNhan.add(keHoach.getNguoiDuyet());
        thongBao.gui(hop, nguoiDungId, "DOI_LICH", "Chờ xác nhận dời lịch",
                "Giờ đề xuất: " + lichMoi.getThoiGianBatDau() + ". " + lichMoi.getLyDoThayDoi(),
                nguoiNhan.toArray(NguoiDung[]::new));
        thongBaoEmail.deXuatDoiLich(lichMoi);
        capNhatRealtime.baoCuocHop(hop, nguoiDungId);
        return chuyenDoiResponse(lichMoi);
    }

    public record LoiMoiDoiLich(Integer lichHopId, String tieuDe, LocalDateTime batDauCu, LocalDateTime ketThucCu,
            LocalDateTime batDauMoi, LocalDateTime ketThucMoi, String lyDo, Boolean dongY, boolean conHan) {}

    public record TienDoDoiLich(int tong, int dongY, int tuChoi) {}

    @Transactional(readOnly = true)
    public List<LoiMoiDoiLich> loiMoiDoiLich(Integer nguoiDungId) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        return jdbc.sql("""
                SELECT l.lich_hop_id, ch.tieu_de, c.thoi_gian_bat_dau AS bat_dau_cu,
                       c.thoi_gian_ket_thuc AS ket_thuc_cu, l.thoi_gian_bat_dau AS bat_dau_moi,
                       l.thoi_gian_ket_thuc AS ket_thuc_moi, l.ly_do_thay_doi AS ly_do, p.dong_y,
                       (l.thoi_gian_bat_dau > CURRENT_TIMESTAMP) AS con_han
                FROM phan_hoi_doi_lich p JOIN lich_hop l ON l.lich_hop_id = p.lich_hop_id
                JOIN lich_hop c ON c.lich_hop_id = l.lich_thay_the_id
                JOIN cuoc_hop ch ON ch.cuoc_hop_id = l.cuoc_hop_id
                WHERE p.nguoi_dung_id = :nguoi AND l.trang_thai = 'DE_XUAT' AND l.la_lich_doi = 1
                ORDER BY l.thoi_gian_bat_dau
                """).param("nguoi", nguoiDungId).query(LoiMoiDoiLich.class).list();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void phanHoiDoiLich(Integer lichHopId, Integer nguoiDungId, boolean dongY) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        var lich = timLichHopVaKhoa(lichHopId);
        if (!lich.isLaLichDoi() || !ThaoTacLichHop.dangDeXuat(lich)
                || !lich.getThoiGianBatDau().isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lịch mới đã ngừng nhận phản hồi");
        }
        int soDong = jdbc.sql("""
                UPDATE phan_hoi_doi_lich SET dong_y = :dongY, thoi_gian_phan_hoi = CURRENT_TIMESTAMP
                WHERE lich_hop_id = :lich AND nguoi_dung_id = :nguoi
                """).param("dongY", dongY).param("lich", lichHopId).param("nguoi", nguoiDungId).update();
        if (soDong == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bạn không có lời mời xác nhận lịch này");
        capNhatRealtime.baoCuocHop(lich.getCuocHop(), nguoiDungId);
    }

    @Transactional(readOnly = true)
    public TienDoDoiLich xemTienDoDoiLich(Integer lichHopId, Integer nguoiDungId) {
        var lich = timLichHop(lichHopId);
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        if (!phanCong.laThuKyToChuc(lich.getCuocHop(), nguoiDungId)
                && !quyTrinhKeHoachService.coQuyenXacNhanLich(lich.getCuocHop(), nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem phản hồi dời lịch");
        }
        return demPhanHoiDoiLich(lichHopId);
    }

    private TienDoDoiLich demPhanHoiDoiLich(Integer lichHopId) {
        return jdbc.sql("""
                SELECT COUNT(*) AS tong, COUNT(CASE WHEN dong_y = 1 THEN 1 END) AS dong_y,
                       COUNT(CASE WHEN dong_y = 0 THEN 1 END) AS tu_choi
                FROM (
                    SELECT dong_y FROM phan_hoi_doi_lich WHERE lich_hop_id = :lich
                    UNION ALL
                    SELECT dong_y FROM phan_hoi_doi_lich_khach WHERE lich_hop_id = :lich
                ) p
                """).param("lich", lichHopId).query(TienDoDoiLich.class).single();
    }

    /** Xác nhận riêng cho hồ sơ đã được duyệt trước khi áp dụng duyệt kèm lịch. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LichHopResponse xacNhanLichHop(Integer lichHopId, Integer nguoiXacNhanId) {
        kiemTraTaiKhoanHoatDong(nguoiXacNhanId);
        LichHop lich = timLichHopVaKhoa(lichHopId);
        CuocHop cuocHop = lich.getCuocHop();

        kiemTraQuyenXacNhanLich(cuocHop, nguoiXacNhanId);
        kiemTraLichKhongChoDuyetKeHoach(lichHopId);
        if (!ThaoTacCuocHop.coTheLapLich(cuocHop)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cuộc họp không ở bước tổ chức");
        }
        var yeuCau = yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHop.getCuocHopId(), LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElseThrow();
        if (!lich.isLaLichDoi()
                && (yeuCau.getLichHop() == null || !lichHopId.equals(yeuCau.getLichHop().getLichHopId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hãy trình lịch đề xuất cùng kế hoạch để duyệt.");
        }
        if (lich.isLaLichDoi()) {
            var phanHoi = demPhanHoiDoiLich(lichHopId);
            if (phanHoi.dongY() != phanHoi.tong()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Chỉ chốt lịch mới khi tất cả người được hỏi đã đồng ý");
            }
        }
        xacNhanLichHopService.xacNhan(cuocHop, lich, timNguoiDung(nguoiXacNhanId), null);
        if (lich.isLaLichDoi()) {
            var nguoiNhan = new java.util.ArrayList<>(nguoiDaDuocMoi(cuocHop));
            nguoiNhan.add(lich.getNguoiDeXuat());
            thongBao.gui(cuocHop, nguoiXacNhanId, "DOI_LICH", "Đã xác nhận giờ họp mới",
                    "Giờ họp mới: " + lich.getThoiGianBatDau(), nguoiNhan.toArray(NguoiDung[]::new));
        }
        capNhatRealtime.baoCuocHop(cuocHop, nguoiXacNhanId);
        return chuyenDoiResponse(lich);
    }

    // =====================================================
    // TỪ CHỐI LỊCH HỌP
    // Người duyệt kế hoạch mới nhất được xác nhận/từ chối lịch.
    // =====================================================

    /** Giữ tương thích với controller cũ; hàm này thực hiện từ chối lịch. */
    @Deprecated
    public LichHopResponse timLichHopVaKhoa(
            Integer lichHopId, Integer nguoiXacNhanId, String lyDoTuChoi) {
        return tuChoiLichHop(lichHopId, nguoiXacNhanId, lyDoTuChoi);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LichHopResponse tuChoiLichHop(
            Integer lichHopId, Integer nguoiXacNhanId, String lyDoTuChoi) {
        return xuLyLichKhongDuyet(lichHopId, nguoiXacNhanId, lyDoTuChoi, "TU_CHOI");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LichHopResponse traLaiLichHop(Integer lichHopId, Integer nguoiXacNhanId, String lyDo) {
        return xuLyLichKhongDuyet(lichHopId, nguoiXacNhanId, lyDo, "YEU_CAU_CHINH_SUA");
    }

    private LichHopResponse xuLyLichKhongDuyet(
            Integer lichHopId, Integer nguoiXacNhanId, String lyDoTuChoi, String hanhDong) {
        kiemTraTaiKhoanHoatDong(nguoiXacNhanId);
        LichHop lich = timLichHopVaKhoa(lichHopId);
        if (!lich.isLaLichDoi() && !ThaoTacCuocHop.coTheLapLich(lich.getCuocHop())) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Cuộc họp không ở bước tổ chức lịch");
        kiemTraQuyenXacNhanLich(lich.getCuocHop(), nguoiXacNhanId);
        kiemTraLichKhongChoDuyetKeHoach(lichHopId);
        if (!ThaoTacLichHop.coTheTuChoi(lich)) throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Chỉ được xử lý lịch đang đề xuất");
        if (lyDoTuChoi == null || lyDoTuChoi.isBlank()) throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Vui lòng nhập lý do xử lý lịch");
        if (lich.isLaLichDoi()) {
            if (!"TU_CHOI".equals(hanhDong)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Chọn giữ lịch cũ hoặc hủy cuộc họp");
            }
            LichHop lichCu = lich.getLichThayThe();
            if (lichCu == null || !ThaoTacLichHop.daXacNhan(lichCu)
                    || !lichCu.getThoiGianBatDau().isAfter(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Lịch cũ đã qua hoặc hết hiệu lực. Chọn hủy cuộc họp.");
            }
            thongBaoEmail.giuLichCu(lichCu, lyDoTuChoi.strip());
            var nguoiNhan = new java.util.ArrayList<>(nguoiDaDuocMoi(lich.getCuocHop()));
            nguoiNhan.add(lich.getNguoiDeXuat());
            thongBao.gui(lich.getCuocHop(), nguoiXacNhanId, "DOI_LICH", "Giữ lịch họp cũ",
                    lyDoTuChoi.strip(), nguoiNhan.toArray(NguoiDung[]::new));
        }
        NguoiDung nguoiTuChoi = timNguoiDung(nguoiXacNhanId);
        ThaoTacCuocHop.thucHien(() -> ThaoTacLichHop.tuChoi(lich, nguoiTuChoi, lyDoTuChoi.trim()));
        LichHop daLuu = lichHopRepository.save(lich);
        lichSuPheDuyetService.ghiNhan(lich.getCuocHop().getCuocHopId(), lichHopId,
                null, nguoiXacNhanId, "LICH_HOP", hanhDong, lyDoTuChoi.trim());
        if (lich.isLaLichDoi()) capNhatRealtime.baoCuocHop(lich.getCuocHop(), nguoiXacNhanId);
        else capNhatRealtime.baoNguoiPhuTrach(lich.getCuocHop(), nguoiXacNhanId);
        return chuyenDoiResponse(daLuu);
    }

    // =====================================================
    // ĐỀ XUẤT THAY THẾ LỊCH HỌP
    // Người có quyền đề xuất lịch được đề xuất thay thế.
    // =====================================================
    // =====================================================
    // HỦY LỊCH HỌP
    //
    // Lịch DE_XUAT:
    // - Người đề xuất được hủy nếu còn thẩm quyền.
    //
    // Lịch DA_XAC_NHAN:
    // - Trưởng phòng hủy lịch NOI_BO.
    // - Lãnh đạo hủy lịch thuộc phạm vi còn lại.
    // =====================================================
//=====================================================
//HỦY LỊCH HỌP
//
//Lịch DE_XUAT:
//- Người đề xuất được hủy nếu còn thẩm quyền.
//
//Lịch DA_XAC_NHAN:
//- Trưởng phòng hủy lịch NOI_BO.
//- Lãnh đạo hủy lịch thuộc phạm vi còn lại.
//
//Ghi nhận lịch sử hủy lịch.
//=====================================================
public LichHopResponse huyLichHop(
     Integer lichHopId,
     String lyDoHuy,
     Integer nguoiDungId) {
	
 kiemTraTaiKhoanHoatDong(nguoiDungId);
 // 1. Tìm và khóa cuộc họp, lịch họp.
 LichHop lichHop = timLichHopVaKhoa(lichHopId);
 CuocHop cuocHop = lichHop.getCuocHop();
 if (ThaoTacCuocHop.daBatDau(cuocHop) || ThaoTacCuocHop.daKetThuc(cuocHop)
         || !ThaoTacCuocHop.coTheTiepTucXuLy(cuocHop)) {
     throw new ResponseStatusException(HttpStatus.CONFLICT,
             "Chỉ hủy lịch trước khi cuộc họp bắt đầu và còn được phép xử lý");
 } 
	
	// 2. Kiểm tra lý do hủy

 if (lyDoHuy == null || lyDoHuy.isBlank()) {
     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập lý do hủy lịch");
 }
 // 3. Kiểm tra trạng thái lịch
 if (ThaoTacCuocHop.choXacNhanDoiLich(cuocHop)) {
     throw new ResponseStatusException(HttpStatus.CONFLICT, "Xử lý yêu cầu dời lịch đang chờ xác nhận");
 }
 String trangThai = lichHop.getTrangThai();
 if ("DE_XUAT".equals(trangThai)) {
     // Người đề xuất được hủy lịch chưa xác nhận
     kiemTraQuyenDeXuatLich(
             lichHop.getCuocHop(),
             nguoiDungId
     );
     // Chỉ người tạo lịch mới được hủy
     kiemTraNguoiTaoLich(
             lichHop,
             nguoiDungId
     );
 } else if ("DA_XAC_NHAN".equals(trangThai)) {
     // Người duyệt kế hoạch mới nhất, đúng cấp duyệt, được hủy lịch xác nhận.
     kiemTraQuyenXacNhanLich(
             lichHop.getCuocHop(),
             nguoiDungId
     );
 } else {
     throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ có thể hủy lịch đang đề xuất hoặc đã xác nhận");
 }
//4. Kiểm tra lịch có đang được trình duyệt rồi mới hủy
kiemTraLichKhongChoDuyetKeHoach(lichHopId);
ThaoTacCuocHop.thucHien(() -> ThaoTacLichHop.huyLich(lichHop, lyDoHuy.trim()));
//5. Lưu lịch họp
LichHop saved = lichHopRepository.save(lichHop);
 // 6. Ghi nhận lịch sử hủy lịch
 lichSuPheDuyetService.ghiNhan(
         saved.getCuocHop().getCuocHopId(),
         saved.getLichHopId(),
         null,
         nguoiDungId,
         "LICH_HOP",
         "HUY",
         lyDoHuy.trim()
 );
 if ("DA_XAC_NHAN".equals(trangThai)) {
     capNhatRealtime.baoCuocHop(cuocHop, nguoiDungId);
     thongBaoEmail.huyLich(saved, lyDoHuy.trim());
 } else {
     capNhatRealtime.baoNguoiPhuTrach(cuocHop, nguoiDungId);
 }
 return chuyenDoiResponse(saved);
}
    // =====================================================
    // KIỂM TRA QUYỀN ĐỀ XEM CUỘC HỌP
    // =====================================================
    private void kiemTraQuyenXemCuocHop(
        Integer cuocHopId,
        Integer nguoiDungId) {
    if (nguoiDungId == null) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Người dùng chưa đăng nhập"
        );
    }
    CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy cuộc họp"
            ));
    if (phanQuyenCuocHopService.coVaiTro(nguoiDungId, "ADMIN")) {
        return;
    }
    boolean laNguoiDeXuat =
            cuocHop.getNguoiDeXuat() != null
            && nguoiDungId.equals(
                    cuocHop.getNguoiDeXuat().getNguoiDungId()
            );
    boolean laNguoiChuTri =
            cuocHop.getNguoiChuTri() != null
            && nguoiDungId.equals(
                    cuocHop.getNguoiChuTri().getNguoiDungId()
            );
    boolean laThanhVien = thanhVienCuocHopRepository
            .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungId(
                    cuocHopId,
                    nguoiDungId
            );
    if (!laNguoiDeXuat
            && !laNguoiChuTri
            && !laThanhVien
            && !phanQuyenCuocHopService.coQuyenXemChiTietDeXuat(cuocHop, nguoiDungId)
            && !phanQuyenCuocHopService.laLanhDao(cuocHopId, nguoiDungId)) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Bạn không có quyền xem lịch của cuộc họp này"
        );
    }
}
    // =====================================================
    // KIỂM TRA QUYỀN ĐỀ XUẤT LỊCH
    // =====================================================

    void kiemTraQuyenDeXuatLich(CuocHop cuocHop, Integer nguoiDungId) {
        kiemTraTaiKhoanHoatDong(nguoiDungId);
        if (!phanQuyenCuocHopService.laThuKyDuocPhanCong(cuocHop.getCuocHopId(), nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ thư ký đang phụ trách được lập lịch");
        }
    }

    // =====================================================
    // KIỂM TRA QUYỀN XÁC NHẬN LỊCH
    // =====================================================

    private void kiemTraQuyenXacNhanLich(CuocHop cuocHop, Integer nguoiDungId) {
        quyTrinhKeHoachService.kiemTraQuyenXacNhanLich(cuocHop, nguoiDungId);
    }

    // =====================================================
    // KIỂM TRA TRƯỞNG PHÒNG CỦA PHÒNG BAN CHỦ TRÌ
    // =====================================================
    private void kiemTraTruongPhongChuTri(
            CuocHop cuocHop,
            Integer nguoiDungId) {
        if (cuocHop.getPhongBanChuTri() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cuộc họp nội bộ chưa xác định phòng ban chủ trì");
        }
        Integer phongBanChuTriId = cuocHop
                .getPhongBanChuTri()
                .getPhongBanId();
        phanQuyenCuocHopService.kiemTraLaTruongPhong(nguoiDungId);
        phanQuyenCuocHopService.kiemTraCungPhongBan(
                nguoiDungId,
                phongBanChuTriId
        );
    }
    // =====================================================
    // LẤY PHẠM VI CUỘC HỌP
    // =====================================================
    private String layPhamViCuocHop(CuocHop cuocHop) {
        if (cuocHop == null
                || cuocHop.getPhamViCuocHop() == null
                || cuocHop.getPhamViCuocHop().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cuộc họp chưa xác định phạm vi");
        }
        return cuocHop.getPhamViCuocHop()
                .trim()
                .toUpperCase();
    }
    // =====================================================
    // KIỂM TRA NGƯỜI TẠO LỊCH
    // =====================================================
    void kiemTraNguoiTaoLich(
            LichHop lichHop,
            Integer nguoiDungId) {
        if (lichHop.getNguoiDeXuat() == null
                || !lichHop.getNguoiDeXuat()
                        .getNguoiDungId()
                        .equals(nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn chỉ được thao tác với lịch do chính mình đề xuất");
        }
    }
    // =====================================================
    // TÌM NGƯỜI DÙNG
    // =====================================================
    NguoiDung timNguoiDung(Integer nguoiDungId) {
        kiemTraMaHopLe(nguoiDungId, "người dùng");
        return nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }
    // =====================================================
    // KIỂM TRA THỜI GIAN
    // =====================================================
    void kiemTraThoiGian(LocalDateTime batDau, LocalDateTime ketThuc) {
        if (batDau == null || ketThuc == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thời gian bắt đầu và kết thúc không được để trống");
        }
        if (!ketThuc.isAfter(batDau)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Thời gian kết thúc phải sau thời gian bắt đầu");
        }
    }
    // =====================================================
    // KIỂM TRA TRÙNG LỊCH PHÒNG
    // =====================================================
    void kiemTraTrungLichDeXuat(
            Integer cuocHopId, Integer phongHopId, Integer lichHopId,
            LocalDateTime batDau, LocalDateTime ketThuc) {
        LichHop lichDangDung = xacNhanLichHopService.layLichDaXacNhan(cuocHopId);
        Integer lichDangDungId = lichDangDung == null ? -1 : lichDangDung.getLichHopId();
        kiemTraTrungLichKhiThayThe(phongHopId, lichDangDungId, lichHopId, batDau, ketThuc);
    }
    // =====================================================
    // KIỂM TRA TRÙNG LỊCH KHI THAY THẾ
    // =====================================================
    void kiemTraTrungLichKhiThayThe(
            Integer phongHopId,
            Integer lichHopCuId,
            Integer lichHopMoiId,
            LocalDateTime batDau,
            LocalDateTime ketThuc) {
        boolean biTrung = lichHopRepository.existsLichHopTrungKhiThayThe(
                phongHopId,
                lichHopCuId,
                lichHopMoiId,
                batDau,
                ketThuc
        );
        if (biTrung) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phòng họp đã có lịch khác trong khoảng thời gian này");
        }
    }
    // =====================================================
    // TÌM LỊCH HỌP
    // =====================================================
    LichHop timLichHop(Integer lichHopId) {
        kiemTraMaHopLe(lichHopId, "lịch họp");
        return lichHopRepository.findById(lichHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch họp"));
    }
    // =====================================================
    // TÌM PHÒNG HỌP
    // =====================================================
    PhongHop timPhongHop(Integer phongHopId) {
        kiemTraMaHopLe(phongHopId, "phòng họp");
        PhongHop phongHop = phongHopRepository.findById(phongHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng họp"));
        if (!"SAN_SANG".equals(phongHop.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phòng họp hiện không ở trạng thái sẵn sàng");
        }
        return phongHop;
    }
    // =====================================================
    // CHUYỂN ENTITY SANG RESPONSE
    // =====================================================
    LichHopResponse chuyenDoiResponse(LichHop lichHop) {
        CuocHop cuocHop = lichHop.getCuocHop();
        PhongHop phongHop = lichHop.getPhongHop();
        NguoiDung nguoiDeXuat = lichHop.getNguoiDeXuat();
        NguoiDung nguoiXacNhan = lichHop.getNguoiXacNhan();
        LichHop lichThayThe = lichHop.getLichThayThe();
        return new LichHopResponse(
                lichHop.getLichHopId(),
                cuocHop != null
                        ? cuocHop.getCuocHopId() : null,
                cuocHop != null
                        ? cuocHop.getTieuDe() : null,
                phongHop != null
                        ? phongHop.getPhongHopId() : null,
                phongHop != null
                        ? phongHop.getTenPhongHop() : null,
                nguoiDeXuat != null
                        ? nguoiDeXuat.getNguoiDungId() : null,
                nguoiDeXuat != null
                        ? nguoiDeXuat.getHoTen() : null,
                nguoiXacNhan != null
                        ? nguoiXacNhan.getNguoiDungId() : null,
                nguoiXacNhan != null
                        ? nguoiXacNhan.getHoTen() : null,
                lichHop.getThoiGianBatDau(),
                lichHop.getThoiGianKetThuc(),
                lichHop.getDiemPhuHop(),
                lichHop.getTrangThai(),
                lichHop.getLyDoThayDoi(),
                lichHop.getThoiGianXacNhan(),
                lichThayThe != null
                        ? lichThayThe.getLichHopId() : null,
                lichHop.getNgayTao(),
                lichHop.isLaLichDoi()
        );
    }
    private boolean duocHuyLich(LichHop lich, Integer nguoiDungId) {
        CuocHop cuocHop = lich.getCuocHop();
        if (ThaoTacCuocHop.choXacNhanDoiLich(cuocHop) || ThaoTacCuocHop.daBatDau(cuocHop)
                || ThaoTacCuocHop.daKetThuc(cuocHop) || !ThaoTacCuocHop.coTheTiepTucXuLy(cuocHop) || !ThaoTacLichHop.coTheHuy(lich)
                || yeuCauPheDuyetRepository.existsByLichHop_LichHopIdAndLoaiYeuCauAndTrangThai(
                        lich.getLichHopId(), LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.CHO_DUYET)) {
            return false;
        }
        if (ThaoTacLichHop.daXacNhan(lich)) {
            return quyTrinhKeHoachService.coQuyenXacNhanLich(cuocHop, nguoiDungId);
        }
        return lich.getNguoiDeXuat() != null && nguoiDungId.equals(lich.getNguoiDeXuat().getNguoiDungId())
                && phanQuyenCuocHopService.laThuKyDuocPhanCong(cuocHop.getCuocHopId(), nguoiDungId);
    }

    void kiemTraLichKhongChoDuyetKeHoach(Integer lichHopId) {
        boolean dangChoDuyet = yeuCauPheDuyetRepository
                .existsByLichHop_LichHopIdAndLoaiYeuCauAndTrangThai(
                        lichHopId,
                        LoaiYeuCau.KE_HOACH_CUOC_HOP,
                        TrangThai.CHO_DUYET);

        if (dangChoDuyet) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Lịch đang được trình cùng kế hoạch. "
                            + "Hãy xử lý yêu cầu phê duyệt kế hoạch trước.");
        }
    }
    LichHop timLichHopVaKhoa(Integer lichHopId) {
        kiemTraMaHopLe(lichHopId, "lịch họp");
        LichHop lich = timLichHop(lichHopId);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(lich.getCuocHop().getCuocHopId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        // Đọc lại dữ liệu sau khi lấy khóa cuộc họp.
        lichHopRepository.lamMoiCuocHopVaLich(cuocHop, lich);
        return lich;
    }

    private void kiemTraMaHopLe(Integer id, String doiTuong) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mã " + doiTuong + " không hợp lệ");
        }
    }

    private List<NguoiDung> nguoiDaDuocMoi(CuocHop hop) {
        return thanhVienCuocHopRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(hop.getCuocHopId())
                .stream().filter(tv -> "CHO_PHAN_HOI".equals(tv.getTrangThaiMoi())
                        || "DA_XAC_NHAN".equals(tv.getTrangThaiMoi()))
                .map(tv -> tv.getNguoiDung()).toList();
    }

    private void kiemTraTaiKhoanHoatDong(Integer nguoiDungId) {
        if (nguoiDungId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }
        NguoiDung nguoiDung = timNguoiDung(nguoiDungId);
        if (!NguoiDungHienTaiService.dangHoatDong(nguoiDung)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tài khoản không hoạt động");
        }
    }
}
