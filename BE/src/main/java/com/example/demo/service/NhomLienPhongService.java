package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.TaoDeXuatCuocHopRequest;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.PhongBanRepository;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED)
public class NhomLienPhongService {
    private static final String CHO_CU = "CHO_CU_NHAN_SU";
    private static final String CHO_KICH_HOAT = "CHO_KICH_HOAT";
    private static final String HOAT_DONG = "HOAT_DONG";
    private static final String DA_DONG = "DA_DONG";

    private final JdbcClient jdbc;
    private final NguoiDungHienTaiService hienTai;
    private final PhanQuyenCuocHopService phanQuyen;
    private final PhongBanRepository phongBanRepository;
    private final CuocHopRepository cuocHopRepository;
    private final DeXuatCuocHopService deXuatService;
    private final PhanCongXuLyKeHoachService phanCong;
    private final ObjectMapper objectMapper;
    private final CapNhatHoSoRealtime realtime;
    private final ThongBaoService thongBao;
    private final NhomThamDuCuocHopService nhomThamDu;

    public record Nhom(Integer nhomId, String tenNhom, String mucDich, Integer phongBanChuTriId,
            Integer nguoiLapId, String capNhom, String trangThai, Integer truongNhomId, Integer thuKyNhomId) {}
    public record Phong(Integer phongBanId, String tenPhongBan, Integer nguoiCuId, LocalDateTime ngayCu) {}
    public record NhanSu(Integer nguoiDungId, String hoTen, Integer phongBanId) {}
    public record DoiNguoi(Integer doiNguoiId, Integer phongBanId, Integer nguoiCuId, Integer nguoiMoiId,
            Integer nguoiYeuCauId, String lyDo, String trangThai, Integer truongPhongXacNhanId, boolean duocXuLy,
            String tenNguoiCu, String tenNguoiMoi, String tenNguoiYeuCau) {}
    public record DeXuat(Integer cuocHopId, String tieuDe, String trangThai, String capDuyet, String tenNguoiDuyet,
            Integer nguoiGuiId, String buocXuLyHienTai) {}
    public record Quyen(boolean lanhDao, Integer phongTruongPhong) {
        public boolean duocTao() { return lanhDao || phongTruongPhong != null; }
    }
    public record TrangNhom(Quyen quyen, List<Nhom> danhSach, List<Phong> phongBanTao) {}
    public record ChiTiet(Nhom nhom, List<Phong> phongBan, List<NhanSu> thanhVien, List<NhanSu> nguonNhanSu,
            List<DoiNguoi> doiNguoi, List<DeXuat> deXuat, Set<Integer> hoSoCoQuyenXem,
            List<NhomThamDuCuocHopService.LanhDaoPhuTrach> lanhDaoDuyet,
            List<NhanSu> nguoiMoiThem,
            boolean duocKichHoat, boolean duocDong, boolean duocGui) {}

    public record TaoNhom(String tenNhom, String mucDich, String capNhom, Integer phongBanChuTriId, List<Integer> phongBanIds) {}
    public record CuNguoi(List<Integer> nguoiDungIds) {}
    public record GanVaiTro(Integer truongNhomId, Integer thuKyNhomId) {}
    public record ThayNguoi(Integer phongBanId, Integer nguoiCuId, Integer nguoiMoiId, String lyDo) {}
    public record XuLyThayNguoi(boolean dongY, String yKien) {}
    public record GuiDeXuat(String tieuDe, String mucTieu, String noiDung, String loaiHinh,
            boolean coKhachNgoai, String mucDichHop, List<Integer> nguoiMoiThemIds, List<String> emailKhachNgoai, Integer lanhDaoDuyetId) {}

    public record LichCongTac(Integer lichHopId, String tieuDe, String tenNhom, String tenPhong,
            LocalDateTime batDau, LocalDateTime ketThuc, String trangThai) {}

    @Transactional(readOnly = true)
    public List<LichCongTac> layLichCongTac(LocalDate ngay, Integer id) {
        timTaiKhoan(id);
        choPhep(id.equals(nhomThamDu.layTruongVanPhong()), "Chỉ Trưởng phòng Văn phòng được xem lịch công tác nhóm");
        LocalDate dauTuan = ngay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return jdbc.sql("""
                SELECT l.lich_hop_id, ch.tieu_de, n.ten_nhom, p.ten_phong_hop AS ten_phong,
                    l.thoi_gian_bat_dau AS bat_dau, l.thoi_gian_ket_thuc AS ket_thuc, l.trang_thai
                FROM lich_hop l
                JOIN cuoc_hop ch ON ch.cuoc_hop_id = l.cuoc_hop_id
                JOIN nhom_lien_phong_de_xuat dx ON dx.cuoc_hop_id = ch.cuoc_hop_id
                JOIN nhom_lien_phong n ON n.nhom_id = dx.nhom_id
                LEFT JOIN phong_hop p ON p.phong_hop_id = l.phong_hop_id
                WHERE l.thoi_gian_bat_dau < :ketThuc AND l.thoi_gian_ket_thuc > :batDau AND (
                    l.trang_thai = 'DA_XAC_NHAN' OR
                    (l.trang_thai = 'DE_XUAT' AND ch.buoc_xu_ly_hien_tai IN
                        ('THU_KY_LANH_DAO_XU_LY', 'CHO_TRUONG_NHOM', 'CHO_LANH_DAO')
                        AND EXISTS (SELECT 1 FROM yeu_cau_phe_duyet yc
                            WHERE yc.lich_hop_id = l.lich_hop_id AND yc.cuoc_hop_id = ch.cuoc_hop_id
                              AND yc.loai_yeu_cau = 'KE_HOACH_CUOC_HOP' AND yc.trang_thai = 'CHO_DUYET'
                              AND yc.thu_ky_tiep_nhan_id = :id)))
                ORDER BY l.thoi_gian_bat_dau, l.lich_hop_id
                """).param("batDau", dauTuan.atStartOfDay()).param("ketThuc", dauTuan.plusWeeks(1).atStartOfDay())
                .param("id", id).query(LichCongTac.class).list();
    }

    @Transactional(readOnly = true)
    public TrangNhom danhSach(Integer id) {
        Quyen quyen = quyen(id);
        List<Nhom> ds = jdbc.sql("""
                SELECT n.* FROM nhom_lien_phong n
                WHERE :toanQuyen OR n.nguoi_lap_id = :id
                  OR EXISTS (SELECT 1 FROM nhom_lien_phong_phong_ban p WHERE p.nhom_id = n.nhom_id AND p.phong_ban_id = :phong)
                  OR EXISTS (SELECT 1 FROM nhom_lien_phong_thanh_vien t WHERE t.nhom_id = n.nhom_id AND t.nguoi_dung_id = :id)
                ORDER BY n.nhom_id DESC
                """).param("toanQuyen", quyen.lanhDao()).param("id", id)
                .param("phong", quyen.phongTruongPhong(), java.sql.Types.INTEGER).query(Nhom.class).list();
        List<Phong> phongBan = quyen.duocTao() ? jdbc.sql("""
                SELECT phong_ban_id, ten_phong_ban, NULL AS nguoi_cu_id, NULL AS ngay_cu
                FROM phong_ban WHERE trang_thai = 'HOAT_DONG' ORDER BY ten_phong_ban
                """).query(Phong.class).list() : List.of();
        return new TrangNhom(quyen, ds, phongBan);
    }

    public Integer tao(TaoNhom request, Integer id) {
        Quyen quyen = quyen(id);
        choPhep(quyen.duocTao(), "Bạn không có quyền lập nhóm");
        var phongIds = request.phongBanIds();
        Integer chuTri = request.phongBanChuTriId();
        hopLe(phongIds != null && phongIds.size() >= 2 && phongIds.size() <= 50
                && phongIds.stream().allMatch(Objects::nonNull) && new HashSet<>(phongIds).size() == phongIds.size()
                && chuTri != null && phongIds.contains(chuTri), "Chọn phòng chủ trì và ít nhất một phòng khác");
        choPhep(quyen.lanhDao() || Objects.equals(chuTri, quyen.phongTruongPhong()),
                "Trưởng phòng chỉ được lập nhóm do phòng mình chủ trì");
        hopLe("THUONG".equals(request.capNhom()) || "CAP_CAO".equals(request.capNhom()), "Cấp nhóm không hợp lệ");
        var truongPhong = phongBanRepository.findAllById(phongIds).stream()
                .filter(p -> HOAT_DONG.equals(p.getTrangThai()) && p.getTruongPhongId() != null)
                .map(phong -> phong.getTruongPhongId()).toList();
        hopLe(truongPhong.size() == phongIds.size(), "Mỗi phòng tham gia cần đang hoạt động và có trưởng phòng");
        var key = new GeneratedKeyHolder();
        jdbc.sql("""
                INSERT INTO nhom_lien_phong (ten_nhom, muc_dich, phong_ban_chu_tri_id, nguoi_lap_id, cap_nhom)
                VALUES (:ten, :mucDich, :chuTri, :id, :cap)
                """).param("ten", vanBan(request.tenNhom(), 150)).param("mucDich", vanBan(request.mucDich(), 1000))
                .param("chuTri", chuTri).param("id", id).param("cap", request.capNhom()).update(key, "nhom_id");
        int nhomId = Objects.requireNonNull(key.getKey()).intValue();
        for (Integer phong : phongIds) {
            jdbc.sql("INSERT INTO nhom_lien_phong_phong_ban (nhom_id, phong_ban_id) VALUES (:nhom, :phong)")
                    .param("nhom", nhomId).param("phong", phong).update();
        }
        thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN", "Có nhóm liên phòng cần cử nhân sự",
                request.tenNhom().strip(), truongPhong);
        realtime.baoDanhMucNhomThayDoi();
        return nhomId;
    }

    @Transactional(readOnly = true)
    public ChiTiet chiTiet(Integer nhomId, Integer id) {
        var n = tim(nhomId, false);
        var q = quyen(id);
        var tv = thanhVien(nhomId);
        var pb = phongBan(nhomId);
        choPhep(q.lanhDao() || n.nguoiLapId().equals(id)
                || tv.stream().anyMatch(v -> v.nguoiDungId().equals(id))
                || pb.stream().anyMatch(p -> p.phongBanId().equals(q.phongTruongPhong())),
                "Bạn không có quyền xem nhóm này");
        boolean gui = duocGui(n, q, id);
        List<NhanSu> nguon = gui || q.phongTruongPhong() != null ? jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten, nd.phong_ban_id
                FROM nguoi_dung nd JOIN nhom_lien_phong_phong_ban pb ON pb.phong_ban_id = nd.phong_ban_id
                WHERE pb.nhom_id = :id AND nd.trang_thai = 'HOAT_DONG'
                  AND (:tatCaPhong OR pb.phong_ban_id = :phong)
                ORDER BY nd.ho_ten
                """).param("id", nhomId).param("tatCaPhong", gui)
                .param("phong", q.phongTruongPhong(), java.sql.Types.INTEGER).query(NhanSu.class).list() : List.of();
        List<DoiNguoi> doiNguoi = jdbc.sql("""
                SELECT d.doi_nguoi_id, d.phong_ban_id, d.nguoi_cu_id, d.nguoi_moi_id, d.nguoi_yeu_cau_id,
                    d.ly_do, d.trang_thai, IF(d.truong_phong_xac_nhan_id = pb.truong_phong_id,
                        d.truong_phong_xac_nhan_id, NULL) AS truong_phong_xac_nhan_id,
                    (d.trang_thai = 'CHO_XU_LY' AND CASE
                        WHEN d.nguoi_moi_id IS NULL THEN :nguoi = :toTruong
                        WHEN d.truong_phong_xac_nhan_id = pb.truong_phong_id THEN :nguoi = :toTruong
                        ELSE COALESCE(d.phong_ban_id = :phong, FALSE)
                    END) AS duoc_xu_ly, cu.ho_ten AS ten_nguoi_cu,
                    moi.ho_ten AS ten_nguoi_moi, yc.ho_ten AS ten_nguoi_yeu_cau
                FROM nhom_lien_phong_doi_nguoi d JOIN phong_ban pb ON pb.phong_ban_id = d.phong_ban_id
                LEFT JOIN nguoi_dung cu ON cu.nguoi_dung_id = d.nguoi_cu_id
                LEFT JOIN nguoi_dung moi ON moi.nguoi_dung_id = d.nguoi_moi_id
                LEFT JOIN nguoi_dung yc ON yc.nguoi_dung_id = d.nguoi_yeu_cau_id
                WHERE d.nhom_id = :id ORDER BY d.doi_nguoi_id DESC
                """).param("id", nhomId).param("nguoi", id).param("toTruong", n.truongNhomId(), java.sql.Types.INTEGER)
                .param("phong", q.phongTruongPhong(), java.sql.Types.INTEGER).query(DoiNguoi.class).list();
        List<DeXuat> deXuat = jdbc.sql("""
                SELECT ch.cuoc_hop_id, ch.tieu_de, ch.trang_thai, dx.cap_duyet,
                    nd.ho_ten AS ten_nguoi_duyet, dx.nguoi_gui_id, ch.buoc_xu_ly_hien_tai
                FROM nhom_lien_phong_de_xuat dx JOIN cuoc_hop ch ON ch.cuoc_hop_id = dx.cuoc_hop_id
                LEFT JOIN nguoi_dung nd ON nd.nguoi_dung_id = dx.nguoi_duyet_id
                WHERE dx.nhom_id = :id ORDER BY ch.cuoc_hop_id DESC
                """).param("id", nhomId).query(DeXuat.class).list();
        Set<Integer> hoSoCoQuyenXem = cuocHopRepository.findAllById(deXuat.stream().map(DeXuat::cuocHopId).toList())
                .stream()
                .filter(cuocHop -> phanQuyen.coQuyenXemChiTietDeXuat(cuocHop, id))
                .map(CuocHop::getCuocHopId)
                .collect(Collectors.toSet());
        return new ChiTiet(n, pb, tv, nguon, doiNguoi, deXuat, hoSoCoQuyenXem,
                nhomThamDu.layLanhDaoCoThamQuyen(nhomId, false),
                gui ? layNguoiNgoaiNhom(nhomId) : List.of(),
                CHO_KICH_HOAT.equals(n.trangThai()) && q.lanhDao(),
                !DA_DONG.equals(n.trangThai()) && duocDong(n, q, id), gui);
    }

    public void cuNguoi(Integer nhomId, Integer phongId, CuNguoi request, Integer id) {
        var n = tim(nhomId, true);
        var q = quyen(id);
        choPhep(phongId.equals(q.phongTruongPhong()), "Chỉ trưởng phòng được cử người của phòng mình");
        phaiOTrangThai(n, CHO_CU, CHO_KICH_HOAT);
        kiemTraPhong(nhomId, phongId);
        var ids = request.nguoiDungIds();
        hopLe(ids != null && !ids.isEmpty() && ids.size() <= 100 && ids.stream().allMatch(Objects::nonNull) && new HashSet<>(ids).size() == ids.size(),
                "Chọn ít nhất một và tối đa 100 người, mỗi người một lần");
        for (Integer nguoiId : ids) {
            kiemTraNguoiCuaPhong(nguoiId, phongId);
        }
        jdbc.sql("DELETE FROM nhom_lien_phong_thanh_vien WHERE nhom_id = :nhom AND phong_ban_id = :phong")
                .param("nhom", nhomId).param("phong", phongId).update();
        for (Integer nguoiId : ids) {
            themThanhVien(nhomId, phongId, nguoiId);
        }
        jdbc.sql("""
                UPDATE nhom_lien_phong_phong_ban SET nguoi_cu_id = :id, ngay_cu = CURRENT_TIMESTAMP
                WHERE nhom_id = :nhom AND phong_ban_id = :phong
                """).param("id", id).param("nhom", nhomId).param("phong", phongId).update();
        if (phongBan(nhomId).stream().allMatch(p -> p.ngayCu() != null)) {
            jdbc.sql("UPDATE nhom_lien_phong SET trang_thai = :tt WHERE nhom_id = :id")
                    .param("tt", CHO_KICH_HOAT).param("id", nhomId).update();
        }
        realtime.baoDanhMucNhomThayDoi();
    }

    public void kichHoat(Integer nhomId, GanVaiTro request, Integer id) {
        var n = tim(nhomId, true);
        choPhep(quyen(id).lanhDao(), "Bạn không có quyền kích hoạt cấp nhóm này");
        phaiOTrangThai(n, CHO_KICH_HOAT);
        doiChieuNhanSu(nhomId);
        ganVaiTro(nhomId, request.truongNhomId(), request.thuKyNhomId());
        jdbc.sql("""
                UPDATE nhom_lien_phong SET trang_thai = 'HOAT_DONG',
                    nguoi_kich_hoat_id = :id, ngay_kich_hoat = CURRENT_TIMESTAMP WHERE nhom_id = :nhom
                """).param("id", id).param("nhom", nhomId).update();
        thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN", "Nhóm liên phòng đã được kích hoạt",
                n.tenNhom(), thanhVien(nhomId).stream().map(NhanSu::nguoiDungId).toList());
        realtime.baoDanhMucNhomThayDoi();
    }

    public void doiNguoi(Integer nhomId, ThayNguoi request, Integer id) {
        var n = tim(nhomId, true);
        phaiOTrangThai(n, HOAT_DONG);
        var q = quyen(id);
        hopLe(request.phongBanId() != null, "Chọn phòng ban cần đổi người");
        boolean rutThanhVien = request.nguoiCuId() != null && request.nguoiMoiId() == null;
        boolean laTruongPhongLienQuan = request.phongBanId().equals(q.phongTruongPhong());
        if (rutThanhVien) {
            kiemTraQuyenRutThanhVien(n, id);
        } else {
            choPhep(laTruongPhongLienQuan || duocGui(n, q, id),
                    "Chỉ tổ trưởng, thư ký nhóm hoặc trưởng phòng liên quan được đề xuất đổi người");
        }
        String lyDo = vanBan(request.lyDo(), 500);
        kiemTraThayNguoi(n, request.phongBanId(), request.nguoiCuId(), request.nguoiMoiId());
        jdbc.sql("""
                INSERT INTO nhom_lien_phong_doi_nguoi
                    (nhom_id, phong_ban_id, nguoi_cu_id, nguoi_moi_id, nguoi_yeu_cau_id, ly_do,
                     truong_phong_xac_nhan_id, trang_thai, nguoi_xu_ly_id, ngay_xu_ly)
                VALUES (:nhom, :phong, :cu, :moi, :id, :lyDo, :xacNhan, :tt, :xuLy,
                        CASE WHEN :rut THEN CURRENT_TIMESTAMP END)
                """).param("nhom", nhomId).param("phong", request.phongBanId())
                .param("cu", request.nguoiCuId(), java.sql.Types.INTEGER)
                .param("moi", request.nguoiMoiId(), java.sql.Types.INTEGER)
                .param("id", id).param("lyDo", lyDo)
                .param("xacNhan", !rutThanhVien && laTruongPhongLienQuan ? id : null, java.sql.Types.INTEGER)
                .param("tt", rutThanhVien ? "DA_CAP_NHAT" : "CHO_XU_LY")
                .param("xuLy", rutThanhVien ? id : null, java.sql.Types.INTEGER).param("rut", rutThanhVien).update();
        if (rutThanhVien) {
            capNhatThanhVien(n, request.phongBanId(), request.nguoiCuId(), null);
            huyYeuCauCuaThanhVienDaRut(nhomId, request.nguoiCuId(), id);
            thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN", "Bạn đã được rút khỏi nhóm",
                    n.tenNhom() + " · " + lyDo, List.of(request.nguoiCuId()));
        } else {
            Integer nguoiXuLy = laTruongPhongLienQuan ? n.truongNhomId()
                    : phongBanRepository.findById(request.phongBanId()).orElseThrow().getTruongPhongId();
            thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN", "Có yêu cầu thay đổi nhân sự nhóm",
                    n.tenNhom() + " · " + lyDo, java.util.Collections.singletonList(nguoiXuLy));
        }
        realtime.baoDanhMucNhomThayDoi();
    }

    public void xuLyDoiNguoi(Integer nhomId, Integer doiId, XuLyThayNguoi request, Integer id) {
        var n = tim(nhomId, true);
        var q = quyen(id);
        phaiOTrangThai(n, HOAT_DONG);
        var doi = jdbc.sql("""
                SELECT doi_nguoi_id, phong_ban_id, nguoi_cu_id, nguoi_moi_id, nguoi_yeu_cau_id, ly_do, trang_thai,
                    truong_phong_xac_nhan_id, FALSE AS duoc_xu_ly,
                    NULL AS ten_nguoi_cu, NULL AS ten_nguoi_moi, NULL AS ten_nguoi_yeu_cau
                FROM nhom_lien_phong_doi_nguoi WHERE nhom_id = :nhom AND doi_nguoi_id = :doi
                """).param("nhom", nhomId).param("doi", doiId).query(DoiNguoi.class).optional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi người"));
        xungDot("CHO_XU_LY".equals(doi.trangThai()), "Yêu cầu đã được xử lý");
        Integer truongPhongId = phongBanRepository.findById(doi.phongBanId()).orElseThrow().getTruongPhongId();
        boolean daXacNhan = truongPhongId != null && truongPhongId.equals(doi.truongPhongXacNhanId());
        boolean laTruongPhong = doi.phongBanId().equals(q.phongTruongPhong());
        boolean laToTruong = id.equals(n.truongNhomId());
        boolean rutThanhVien = doi.nguoiMoiId() == null;
        if (rutThanhVien) {
            kiemTraQuyenRutThanhVien(n, id);
        } else {
            choPhep(daXacNhan ? laToTruong : laTruongPhong,
                    daXacNhan ? "Chỉ tổ trưởng được xử lý yêu cầu đã xác nhận"
                            : "Trưởng phòng có nhân sự thay đổi cần xác nhận trước");
        }
        if (!rutThanhVien && !daXacNhan && request.dongY()) {
            kiemTraThayNguoi(n, doi.phongBanId(), doi.nguoiCuId(), doi.nguoiMoiId());
            jdbc.sql("""
                    UPDATE nhom_lien_phong_doi_nguoi SET truong_phong_xac_nhan_id = :id WHERE doi_nguoi_id = :doi
                    """).param("id", id).param("doi", doiId).update();
            if (!laToTruong) {
                thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN", "Trưởng phòng đã xác nhận thay đổi nhân sự",
                        n.tenNhom(), List.of(n.truongNhomId()));
                realtime.baoDanhMucNhomThayDoi();
                return;
            }
        }
        String yKien = request.dongY() && (request.yKien() == null || request.yKien().isBlank())
                ? null : vanBan(request.yKien(), 500);
        if (request.dongY()) {
            kiemTraThayNguoi(n, doi.phongBanId(), doi.nguoiCuId(), doi.nguoiMoiId());
            capNhatThanhVien(n, doi.phongBanId(), doi.nguoiCuId(), doi.nguoiMoiId());
        }
        jdbc.sql("""
                UPDATE nhom_lien_phong_doi_nguoi SET trang_thai = :tt, nguoi_xu_ly_id = :id,
                    ngay_xu_ly = CURRENT_TIMESTAMP, y_kien_xu_ly = :yKien WHERE doi_nguoi_id = :doi
                """).param("tt", request.dongY() ? "DA_CAP_NHAT" : "TU_CHOI")
                .param("id", id).param("yKien", yKien, java.sql.Types.VARCHAR).param("doi", doiId).update();
        if (rutThanhVien && request.dongY()) huyYeuCauCuaThanhVienDaRut(nhomId, doi.nguoiCuId(), id);
        thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN",
                request.dongY() ? "Đã cập nhật nhân sự nhóm" : "Yêu cầu đổi nhân sự đã bị từ chối",
                n.tenNhom(), request.dongY() ? java.util.Arrays.asList(doi.nguoiYeuCauId(), doi.nguoiCuId(), doi.nguoiMoiId())
                        : List.of(doi.nguoiYeuCauId()));
        realtime.baoDanhMucNhomThayDoi();
    }

    public void dong(Integer nhomId, String lyDo, Integer id) {
        var n = tim(nhomId, true);
        choPhep(duocDong(n, quyen(id), id), "Bạn không có quyền đóng nhóm này");
        xungDot(!DA_DONG.equals(n.trangThai()), "Nhóm đã đóng");
        boolean conLuotBauDangMo = jdbc.sql("""
                SELECT EXISTS (
                    SELECT 1 FROM nhom_bau_lai hs
                    JOIN cuoc_bau b ON b.bau_lai_yeu_cau_id = hs.yeu_cau_id
                    JOIN vong_bau v ON v.cuoc_bau_id = b.cuoc_bau_id
                    WHERE hs.nhom_id = :nhom AND v.trang_thai = 'DANG_MO'
                )
                """).param("nhom", nhomId).query(Boolean.class).single();
        xungDot(!conLuotBauDangMo, "Thư ký nhóm cần đóng lượt bầu đang nhận phiếu trước khi đóng nhóm");
        jdbc.sql("""
                UPDATE nhom_lien_phong SET trang_thai = 'DA_DONG', ly_do_dong = :lyDo,
                    nguoi_dong_id = :id, ngay_dong = CURRENT_TIMESTAMP WHERE nhom_id = :nhom
                """).param("id", id).param("lyDo", vanBan(lyDo, 500)).param("nhom", nhomId).update();
        jdbc.sql("""
                UPDATE nhom_lien_phong_doi_nguoi SET trang_thai = 'DA_HUY', nguoi_xu_ly_id = :id,
                    ngay_xu_ly = CURRENT_TIMESTAMP, y_kien_xu_ly = 'Nhóm đã đóng'
                WHERE nhom_id = :nhom AND trang_thai = 'CHO_XU_LY'
                """).param("id", id).param("nhom", nhomId).update();
        thongBao.guiTheoId(null, id, "NHOM_THANH_VIEN", "Nhóm liên phòng đã đóng",
                n.tenNhom(), thanhVien(nhomId).stream().map(NhanSu::nguoiDungId).toList());
        realtime.baoDanhMucNhomThayDoi();
    }

    public Integer guiDeXuat(Integer nhomId, GuiDeXuat request, Integer id) {
        var n = tim(nhomId, true);
        var q = quyen(id);
        phaiOTrangThai(n, HOAT_DONG);
        choPhep(!q.lanhDao(), "Lãnh đạo gửi họp từ Đề xuất của tôi và chọn nhóm tham gia tại đó");
        choPhep(duocGui(n, q, id), "Bạn không có quyền gửi đề xuất họp của nhóm này");
        var thanhVien = doiChieuNhanSu(nhomId);
        String mucDich = request.mucDichHop();
        hopLe(mucDich != null && List.of(NhomThamDuCuocHopService.HOP_THUONG,
                NhomThamDuCuocHopService.CHU_TICH_CHU_TRI, NhomThamDuCuocHopService.XIN_CHU_TICH_KET_LUAN)
                .contains(mucDich), "Chọn mục đích cuộc họp");
        List<Integer> moiThem = request.nguoiMoiThemIds() == null ? List.of() : request.nguoiMoiThemIds();
        var hopLeIds = layNguoiNgoaiNhom(nhomId).stream().map(NhanSu::nguoiDungId).collect(java.util.stream.Collectors.toSet());
        hopLe(moiThem.size() <= 100 && hopLeIds.containsAll(moiThem) && new HashSet<>(moiThem).size() == moiThem.size(),
                "Danh sách mời thêm phải gồm các tài khoản đang hoạt động ngoài nhóm");
        var phong = phongBanRepository.findById(n.phongBanChuTriId()).orElseThrow();
        xungDot(HOAT_DONG.equals(phong.getTrangThai()), "Phòng chủ trì không còn hoạt động");
        Integer chuTri = n.truongNhomId();
        var taiKhoanChuTri = timTaiKhoan(chuTri);
        var thuKy = timTaiKhoan(n.thuKyNhomId());
        hopLe(phanCong.laNguoiCoTheLamThuKy(thuKy) && !thuKy.getNguoiDungId().equals(chuTri),
                "Thư ký nhóm phải đang hoạt động và khác trưởng nhóm");
        List<String> emailKhach = request.emailKhachNgoai() == null ? List.of() : request.emailKhachNgoai().stream()
                .map(email -> email == null ? "" : email.strip().toLowerCase(Locale.ROOT)).toList();
        boolean coKhachNgoai = !emailKhach.isEmpty();
        hopLe(request.coKhachNgoai() == coKhachNgoai && emailKhach.size() <= 100
                && new HashSet<>(emailKhach).size() == emailKhach.size()
                && emailKhach.stream().allMatch(email -> email.length() <= 255 && email.matches("[^\\s@]+@[^\\s@]+[.][^\\s@]+")),
                "Nhập email hợp lệ, mỗi khách ngoài một email");
        hopLe(emailKhach.stream().noneMatch(nhomThamDu::laEmailNoiBo),
                "Email của cán bộ trong cơ quan phải được chọn ở danh sách mời thêm");
        boolean canChuTich = !NhomThamDuCuocHopService.HOP_THUONG.equals(mucDich);
        boolean canLanhDao = canChuTich || coKhachNgoai || !moiThem.isEmpty();
        Integer duyetId = canLanhDao ? request.lanhDaoDuyetId() : chuTri;
        if (canLanhDao) {
            hopLe(nhomThamDu.layLanhDaoCoThamQuyen(nhomId, canChuTich).stream()
                    .anyMatch(nd -> nd.nguoiDungId().equals(duyetId)), "Chọn Chủ tịch hoặc Phó Chủ tịch có thẩm quyền");
        }
        if (NhomThamDuCuocHopService.CHU_TICH_CHU_TRI.equals(mucDich)) {
            taiKhoanChuTri = timTaiKhoan(duyetId);
        }

        var duLieu = new TaoDeXuatCuocHopRequest();
        duLieu.setTieuDe(vanBan(request.tieuDe(), 255));
        duLieu.setMucTieu(vanBan(request.mucTieu(), 3000));
        duLieu.setNoiDungDuKien(vanBan(request.noiDung(), 10000));
        duLieu.setLoaiHinh(request.loaiHinh());
        duLieu.setPhamViCuocHop(coKhachNgoai ? "BEN_NGOAI" : "LIEN_PHONG_BAN");
        duLieu.setCoKhachMoiBenNgoai(coKhachNgoai);
        Integer hopId = deXuatService.guiDeXuatTuNhom(duLieu, timTaiKhoan(id), phong, taiKhoanChuTri, thuKy);
        jdbc.sql("""
                INSERT INTO nhom_lien_phong_de_xuat (cuoc_hop_id, nhom_id, nguoi_gui_id,
                    thanh_vien_khi_gui, nguoi_duyet_id, cap_duyet, muc_dich_hop, thanh_vien_moi_them, email_khach_ngoai, to_truong_khi_gui_id)
                VALUES (:hop, :nhom, :id, :tv, :duyet, :cap, :mucDich, :moi, :emailKhach, :toTruong)
                """).param("hop", hopId).param("nhom", nhomId).param("id", id)
                .param("tv", objectMapper.writeValueAsString(thanhVien)).param("duyet", duyetId, java.sql.Types.INTEGER)
                .param("cap", canLanhDao ? "LANH_DAO" : "TRUONG_NHOM").param("mucDich", mucDich)
                .param("moi", objectMapper.writeValueAsString(moiThem))
                .param("emailKhach", objectMapper.writeValueAsString(emailKhach)).param("toTruong", chuTri).update();
        realtime.baoDanhMucNhomThayDoi();
        return hopId;
    }

    private Nhom tim(Integer nhomId, boolean khoa) {
        return jdbc.sql("SELECT * FROM nhom_lien_phong WHERE nhom_id = :id" + (khoa ? " FOR UPDATE" : ""))
                .param("id", nhomId).query(Nhom.class).optional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhóm"));
    }

    private List<Phong> phongBan(Integer nhomId) {
        return jdbc.sql("""
                SELECT p.phong_ban_id, p.ten_phong_ban, pb.nguoi_cu_id, pb.ngay_cu
                FROM nhom_lien_phong_phong_ban pb JOIN phong_ban p ON p.phong_ban_id = pb.phong_ban_id
                WHERE pb.nhom_id = :id ORDER BY p.ten_phong_ban
                """).param("id", nhomId).query(Phong.class).list();
    }

    private List<NhanSu> thanhVien(Integer nhomId) {
        return jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten, tv.phong_ban_id
                FROM nhom_lien_phong_thanh_vien tv JOIN nguoi_dung nd ON nd.nguoi_dung_id = tv.nguoi_dung_id
                WHERE tv.nhom_id = :id ORDER BY tv.phong_ban_id, nd.ho_ten
                """).param("id", nhomId).query(NhanSu.class).list();
    }

    private List<NhanSu> layNguoiNgoaiNhom(Integer nhomId) {
        return jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten, nd.phong_ban_id
                FROM nguoi_dung nd
                WHERE nd.trang_thai = 'HOAT_DONG' AND NOT EXISTS (
                    SELECT 1 FROM nhom_lien_phong_thanh_vien tv
                    WHERE tv.nhom_id = :nhom AND tv.nguoi_dung_id = nd.nguoi_dung_id)
                ORDER BY nd.ho_ten, nd.nguoi_dung_id
                """).param("nhom", nhomId).query(NhanSu.class).list();
    }

    private Quyen quyen(Integer id) {
        var nguoiDung = timTaiKhoan(id);
        return new Quyen(phanQuyen.coVaiTro(id, "LANH_DAO"),
                NguoiDungHienTaiService.laTruongPhongCuaPhongBanMinh(nguoiDung)
                        ? nguoiDung.getPhongBan().getPhongBanId() : null);
    }

    private boolean duocDong(Nhom n, Quyen q, Integer id) {
        return q.lanhDao() || (q.phongTruongPhong() != null && n.nguoiLapId().equals(id));
    }

    private boolean duocGui(Nhom n, Quyen q, Integer id) {
        return HOAT_DONG.equals(n.trangThai()) && !q.lanhDao()
                && (id.equals(n.thuKyNhomId()) || id.equals(n.truongNhomId()));
    }

    private NguoiDung timTaiKhoan(Integer id) {
        hopLe(id != null, "Chưa chọn tài khoản");
        return hienTai.layTaiKhoanHoatDong(id);
    }

    private void kiemTraPhong(Integer nhom, Integer phong) {
        hopLe(phongBan(nhom).stream().anyMatch(p -> p.phongBanId().equals(phong)), "Phòng ban không thuộc nhóm");
    }

    private void kiemTraNguoiCuaPhong(Integer nguoi, Integer phong) {
        var nd = timTaiKhoan(nguoi);
        hopLe(nd.getPhongBan() != null && phong.equals(nd.getPhongBan().getPhongBanId()),
                "Thành viên phải đang công tác tại phòng được chọn");
    }

    private List<NhanSu> doiChieuNhanSu(Integer nhomId) {
        var ds = thanhVien(nhomId);
        for (var phong : phongBan(nhomId)) {
            var thanhVienPhong = ds.stream().filter(tv -> tv.phongBanId().equals(phong.phongBanId())).toList();
            xungDot(!thanhVienPhong.isEmpty(), "Phòng " + phong.tenPhongBan() + " chưa có người được cử");
            for (var tv : thanhVienPhong) {
                kiemTraNguoiCuaPhong(tv.nguoiDungId(), phong.phongBanId());
            }
        }
        return ds;
    }

    private void themThanhVien(Integer nhom, Integer phong, Integer nguoi) {
        jdbc.sql("""
                INSERT INTO nhom_lien_phong_thanh_vien (nhom_id, phong_ban_id, nguoi_dung_id) VALUES (:nhom, :phong, :id)
                """).param("nhom", nhom).param("phong", phong).param("id", nguoi).update();
    }

    void ganVaiTro(Integer nhomId, Integer truong, Integer thuKy) {
        var tv = thanhVien(nhomId);
        hopLe(truong != null && thuKy != null && !truong.equals(thuKy)
                && tv.stream().anyMatch(v -> v.nguoiDungId().equals(truong))
                && tv.stream().anyMatch(v -> v.nguoiDungId().equals(thuKy)),
                "Chọn trưởng nhóm và thư ký là hai thành viên khác nhau");
        hopLe(phanQuyen.laTruongPhong(truong), "Trưởng nhóm phải là trưởng phòng đang được phân công");
        hopLe(phanCong.laNguoiCoTheLamThuKy(timTaiKhoan(thuKy)), "Thư ký nhóm phải là cán bộ hoặc thư ký");
        jdbc.sql("""
                UPDATE nhom_lien_phong SET truong_nhom_id = :truong, thu_ky_nhom_id = :thuKy WHERE nhom_id = :nhom
                """).param("truong", truong).param("thuKy", thuKy).param("nhom", nhomId).update();
    }

    private void kiemTraQuyenRutThanhVien(Nhom nhom, Integer id) {
        choPhep(id.equals(nhom.truongNhomId()), "Chỉ tổ trưởng được rút thành viên khỏi nhóm");
    }

    private void huyYeuCauCuaThanhVienDaRut(Integer nhomId, Integer nguoiCuId, Integer id) {
        jdbc.sql("""
                UPDATE nhom_lien_phong_doi_nguoi SET trang_thai = 'DA_HUY', nguoi_xu_ly_id = :id,
                    ngay_xu_ly = CURRENT_TIMESTAMP, y_kien_xu_ly = 'Thành viên đã rời nhóm'
                WHERE nhom_id = :nhom AND nguoi_cu_id = :cu AND trang_thai = 'CHO_XU_LY'
                """).param("nhom", nhomId).param("cu", nguoiCuId).param("id", id).update();
    }

    private void kiemTraThayNguoi(Nhom n, Integer phong, Integer cu, Integer moi) {
        kiemTraPhong(n.nhomId(), phong);
        hopLe(!Objects.equals(cu, moi), "Chọn người cần thêm, bớt hoặc thay thế");
        hopLe(cu == null || (!Objects.equals(cu, n.truongNhomId()) && !Objects.equals(cu, n.thuKyNhomId())),
                "Cần xác nhận kết quả bầu lại chức danh trước khi thay hoặc rút người đang giữ chức danh");
        if (cu != null) {
            boolean choBanGiao = jdbc.sql("""
                    SELECT EXISTS (
                        SELECT 1 FROM nhom_ban_giao_chuc_danh bg
                        JOIN nhom_bau_lai hs ON hs.yeu_cau_id = bg.yeu_cau_id
                        WHERE hs.nhom_id = ? AND bg.ngay_nhan IS NULL
                            AND ? IN (bg.nguoi_ban_giao_id, bg.nguoi_nhan_id)
                    )
                    """).params(n.nhomId(), cu).query(Boolean.class).single();
            xungDot(!choBanGiao, "Hoàn tất bàn giao chức danh trước khi thay hoặc rút người này");
        }
        var tv = thanhVien(n.nhomId());
        hopLe(cu == null || tv.stream().anyMatch(v -> v.nguoiDungId().equals(cu) && v.phongBanId().equals(phong)),
                "Người cần thay không còn thuộc phòng này trong nhóm");
        if (moi != null) {
            kiemTraNguoiCuaPhong(moi, phong);
            hopLe(tv.stream().noneMatch(v -> v.nguoiDungId().equals(moi)), "Người mới đã có trong nhóm");
        } else {
            hopLe(tv.stream().filter(v -> v.phongBanId().equals(phong)).count() > 1, "Phòng phải còn ít nhất một thành viên");
        }
    }

    private void capNhatThanhVien(Nhom n, Integer phong, Integer cu, Integer moi) {
        if (moi != null) themThanhVien(n.nhomId(), phong, moi);
        if (cu == null) return;
        jdbc.sql("DELETE FROM nhom_lien_phong_thanh_vien WHERE nhom_id = :nhom AND nguoi_dung_id = :cu")
                .param("nhom", n.nhomId()).param("cu", cu).update();
    }

    private void phaiOTrangThai(Nhom nhom, String... trangThai) {
        xungDot(List.of(trangThai).contains(nhom.trangThai()), "Nhóm không ở trạng thái cho phép thao tác");
    }

    private String vanBan(String value, int max) {
        String daChuanHoa = Objects.toString(value, "").strip();
        hopLe(!daChuanHoa.isEmpty() && daChuanHoa.length() <= max, "Nội dung bắt buộc, tối đa " + max + " ký tự");
        return daChuanHoa;
    }

    private void choPhep(boolean ok, String loi) {
        if (!ok) throw new ResponseStatusException(HttpStatus.FORBIDDEN, loi);
    }

    private void hopLe(boolean ok, String loi) {
        if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, loi);
    }

    private void xungDot(boolean ok, String loi) {
        if (!ok) throw new ResponseStatusException(HttpStatus.CONFLICT, loi);
    }
}
