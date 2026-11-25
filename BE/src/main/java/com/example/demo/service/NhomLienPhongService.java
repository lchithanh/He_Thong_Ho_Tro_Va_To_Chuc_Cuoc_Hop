package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.TaoDeXuatCuocHopRequest;
import com.example.demo.entity.NguoiDung;
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
    private final QuyenVanPhongService quyenVanPhong;
    private final PhongBanRepository phongBanRepository;
    private final DeXuatCuocHopService deXuatService;
    private final PhanCongXuLyKeHoachService phanCong;
    private final ObjectMapper objectMapper;
    private final CapNhatHoSoRealtime realtime;

    public record Nhom(Integer nhomId, String tenNhom, String mucDich, Integer phongBanChuTriId,
            Integer nguoiLapId, String capNhom, String trangThai, Integer truongNhomId, Integer thuKyNhomId) {}
    public record Phong(Integer phongBanId, String tenPhongBan, Integer nguoiCuId, LocalDateTime ngayCu) {}
    public record NhanSu(Integer nguoiDungId, String hoTen, Integer phongBanId) {}
    public record NguoiDuyet(Integer nguoiDungId, String hoTen, String capDuyet) {}
    public record DoiNguoi(Integer doiNguoiId, Integer phongBanId, Integer nguoiCuId, Integer nguoiMoiId,
            Integer nguoiYeuCauId, String lyDo, String trangThai) {}
    public record DeXuat(Integer cuocHopId, String tieuDe, String trangThai, String capDuyet, String tenNguoiDuyet,
            Integer nguoiGuiId) {}
    public record Quyen(boolean vanPhong, boolean lanhDao, Integer phongTruongPhong) {
        public boolean duocTao() { return vanPhong || lanhDao || phongTruongPhong != null; }
    }
    public record TrangNhom(Quyen quyen, List<Nhom> danhSach, List<Phong> phongBanTao) {}
    public record ChiTiet(Nhom nhom, List<Phong> phongBan, List<NhanSu> thanhVien, List<NhanSu> nguonNhanSu,
            List<DoiNguoi> doiNguoi, List<DeXuat> deXuat, List<NguoiDuyet> nguoiDuyet,
            boolean duocKichHoat, boolean duocDong, boolean duocGui) {}

    public record TaoNhom(String tenNhom, String mucDich, String capNhom, Integer phongBanChuTriId, List<Integer> phongBanIds) {}
    public record CuNguoi(List<Integer> nguoiDungIds) {}
    public record GanVaiTro(Integer truongNhomId, Integer thuKyNhomId) {}
    public record ThayNguoi(Integer phongBanId, Integer nguoiCuId, Integer nguoiMoiId, String lyDo) {}
    public record XuLyThayNguoi(boolean dongY, String yKien) {}
    public record GuiDeXuat(String tieuDe, String mucTieu, String noiDung, String loaiHinh, String phamVi,
            boolean coKhachNgoai, boolean coLanhDaoCapCao, Integer nguoiChuTriId,
            Integer nguoiDuyetId) {}

    @Transactional(readOnly = true)
    public TrangNhom danhSach(Integer id) {
        Quyen quyen = quyen(id);
        List<Nhom> ds = jdbc.sql("""
                SELECT n.* FROM nhom_lien_phong n
                WHERE :toanQuyen OR n.nguoi_lap_id = :id
                  OR EXISTS (SELECT 1 FROM nhom_lien_phong_phong_ban p WHERE p.nhom_id = n.nhom_id AND p.phong_ban_id = :phong)
                  OR EXISTS (SELECT 1 FROM nhom_lien_phong_thanh_vien t WHERE t.nhom_id = n.nhom_id AND t.nguoi_dung_id = :id)
                ORDER BY n.nhom_id DESC
                """).param("toanQuyen", quyen.vanPhong() || quyen.lanhDao()).param("id", id)
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
        choPhep(quyen.vanPhong() || quyen.lanhDao() || Objects.equals(chuTri, quyen.phongTruongPhong()),
                "Trưởng phòng chỉ được lập nhóm do phòng mình chủ trì");
        hopLe("THUONG".equals(request.capNhom()) || "CAP_CAO".equals(request.capNhom()), "Cấp nhóm không hợp lệ");
        long soPhong = phongBanRepository.findAllById(phongIds).stream()
                .filter(p -> HOAT_DONG.equals(p.getTrangThai()) && p.getTruongPhongId() != null).count();
        hopLe(soPhong == phongIds.size(), "Mỗi phòng tham gia cần đang hoạt động và có trưởng phòng");
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
        realtime.baoDanhMucNhomThayDoi();
        return nhomId;
    }

    @Transactional(readOnly = true)
    public ChiTiet chiTiet(Integer nhomId, Integer id) {
        var n = tim(nhomId, false);
        var q = quyen(id);
        var tv = thanhVien(nhomId);
        var pb = phongBan(nhomId);
        choPhep(q.lanhDao() || q.vanPhong() || n.nguoiLapId().equals(id)
                || tv.stream().anyMatch(v -> v.nguoiDungId().equals(id))
                || pb.stream().anyMatch(p -> p.phongBanId().equals(q.phongTruongPhong())),
                "Bạn không có quyền xem nhóm này");
        List<NhanSu> nguon = q.vanPhong() || q.phongTruongPhong() != null ? jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten, nd.phong_ban_id
                FROM nguoi_dung nd JOIN nhom_lien_phong_phong_ban pb ON pb.phong_ban_id = nd.phong_ban_id
                WHERE pb.nhom_id = :id AND nd.trang_thai = 'HOAT_DONG'
                  AND (:vanPhong OR pb.phong_ban_id = :phong)
                ORDER BY nd.ho_ten
                """).param("id", nhomId).param("vanPhong", q.vanPhong())
                .param("phong", q.phongTruongPhong(), java.sql.Types.INTEGER).query(NhanSu.class).list() : List.of();
        boolean gui = duocGui(n, q, id);
        List<NguoiDuyet> nguoiDuyet = gui ? jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten,
                    CASE WHEN vt.ma_vai_tro = 'LANH_DAO' THEN 'LANH_DAO' ELSE 'VAN_PHONG' END AS cap_duyet
                FROM nguoi_dung nd
                JOIN nguoi_dung_vai_tro ndvt ON ndvt.nguoi_dung_id = nd.nguoi_dung_id
                JOIN vai_tro vt ON vt.vai_tro_id = ndvt.vai_tro_id
                LEFT JOIN thu_ky_van_phong vp ON vp.nguoi_dung_id = nd.nguoi_dung_id
                WHERE nd.trang_thai = 'HOAT_DONG' AND
                    (vt.ma_vai_tro = 'LANH_DAO' OR (vt.ma_vai_tro = 'THU_KY' AND vp.nguoi_dung_id IS NOT NULL))
                ORDER BY nd.ho_ten
                """).query(NguoiDuyet.class).list() : List.of();
        List<DoiNguoi> doiNguoi = jdbc.sql("""
                SELECT doi_nguoi_id, phong_ban_id, nguoi_cu_id, nguoi_moi_id, nguoi_yeu_cau_id, ly_do, trang_thai
                FROM nhom_lien_phong_doi_nguoi WHERE nhom_id = :id ORDER BY doi_nguoi_id DESC
                """).param("id", nhomId).query(DoiNguoi.class).list();
        List<DeXuat> deXuat = jdbc.sql("""
                SELECT ch.cuoc_hop_id, ch.tieu_de, ch.trang_thai, dx.cap_duyet,
                    nd.ho_ten AS ten_nguoi_duyet, dx.nguoi_gui_id
                FROM nhom_lien_phong_de_xuat dx JOIN cuoc_hop ch ON ch.cuoc_hop_id = dx.cuoc_hop_id
                JOIN nguoi_dung nd ON nd.nguoi_dung_id = dx.nguoi_duyet_id
                WHERE dx.nhom_id = :id ORDER BY ch.cuoc_hop_id DESC
                """).param("id", nhomId).query(DeXuat.class).list();
        return new ChiTiet(n, pb, tv, nguon, doiNguoi, deXuat, nguoiDuyet,
                CHO_KICH_HOAT.equals(n.trangThai()) && duocKichHoat(n, q),
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
        choPhep(duocKichHoat(n, quyen(id)), "Bạn không có quyền kích hoạt cấp nhóm này");
        phaiOTrangThai(n, CHO_KICH_HOAT);
        doiChieuNhanSu(nhomId);
        ganVaiTro(nhomId, request.truongNhomId(), request.thuKyNhomId());
        jdbc.sql("""
                UPDATE nhom_lien_phong SET trang_thai = 'HOAT_DONG',
                    nguoi_kich_hoat_id = :id, ngay_kich_hoat = CURRENT_TIMESTAMP WHERE nhom_id = :nhom
                """).param("id", id).param("nhom", nhomId).update();
        realtime.baoDanhMucNhomThayDoi();
    }

    public void doiNguoi(Integer nhomId, ThayNguoi request, Integer id) {
        var n = tim(nhomId, true);
        phaiOTrangThai(n, HOAT_DONG);
        var q = quyen(id);
        hopLe(request.phongBanId() != null, "Chọn phòng ban cần đổi người");
        choPhep(q.vanPhong() || request.phongBanId().equals(q.phongTruongPhong()),
                "Chỉ Văn phòng hoặc trưởng phòng liên quan được đổi người");
        String lyDo = vanBan(request.lyDo(), 500);
        kiemTraThayNguoi(n, request.phongBanId(), request.nguoiCuId(), request.nguoiMoiId());
        if (q.vanPhong()) {
            capNhatThanhVien(n, request.phongBanId(), request.nguoiCuId(), request.nguoiMoiId());
        }
        jdbc.sql("""
                INSERT INTO nhom_lien_phong_doi_nguoi
                    (nhom_id, phong_ban_id, nguoi_cu_id, nguoi_moi_id, nguoi_yeu_cau_id, ly_do, trang_thai, nguoi_xu_ly_id, ngay_xu_ly)
                VALUES (:nhom, :phong, :cu, :moi, :id, :lyDo, :tt, :xuLy,
                    CASE WHEN :tt = 'DA_CAP_NHAT' THEN CURRENT_TIMESTAMP ELSE NULL END)
                """).param("nhom", nhomId).param("phong", request.phongBanId())
                .param("cu", request.nguoiCuId(), java.sql.Types.INTEGER).param("moi", request.nguoiMoiId(), java.sql.Types.INTEGER)
                .param("id", id).param("lyDo", lyDo).param("tt", q.vanPhong() ? "DA_CAP_NHAT" : "CHO_XU_LY")
                .param("xuLy", q.vanPhong() ? id : null, java.sql.Types.INTEGER).update();
        realtime.baoDanhMucNhomThayDoi();
    }

    public void xuLyDoiNguoi(Integer nhomId, Integer doiId, XuLyThayNguoi request, Integer id) {
        var n = tim(nhomId, true);
        choPhep(quyen(id).vanPhong(), "Chỉ thư ký Văn phòng được xử lý yêu cầu đổi người");
        phaiOTrangThai(n, HOAT_DONG);
        var doi = jdbc.sql("""
                SELECT doi_nguoi_id, phong_ban_id, nguoi_cu_id, nguoi_moi_id, nguoi_yeu_cau_id, ly_do, trang_thai
                FROM nhom_lien_phong_doi_nguoi WHERE nhom_id = :nhom AND doi_nguoi_id = :doi
                """).param("nhom", nhomId).param("doi", doiId).query(DoiNguoi.class).optional()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu đổi người"));
        xungDot("CHO_XU_LY".equals(doi.trangThai()), "Yêu cầu đã được xử lý");
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
        realtime.baoDanhMucNhomThayDoi();
    }

    public void dong(Integer nhomId, String lyDo, Integer id) {
        var n = tim(nhomId, true);
        choPhep(duocDong(n, quyen(id), id), "Bạn không có quyền đóng nhóm này");
        xungDot(!DA_DONG.equals(n.trangThai()), "Nhóm đã đóng");
        jdbc.sql("""
                UPDATE nhom_lien_phong SET trang_thai = 'DA_DONG', ly_do_dong = :lyDo,
                    nguoi_dong_id = :id, ngay_dong = CURRENT_TIMESTAMP WHERE nhom_id = :nhom
                """).param("id", id).param("lyDo", vanBan(lyDo, 500)).param("nhom", nhomId).update();
        jdbc.sql("""
                UPDATE nhom_lien_phong_doi_nguoi SET trang_thai = 'DA_HUY', nguoi_xu_ly_id = :id,
                    ngay_xu_ly = CURRENT_TIMESTAMP, y_kien_xu_ly = 'Nhóm đã đóng'
                WHERE nhom_id = :nhom AND trang_thai = 'CHO_XU_LY'
                """).param("id", id).param("nhom", nhomId).update();
        realtime.baoDanhMucNhomThayDoi();
    }

    public Integer guiDeXuat(Integer nhomId, GuiDeXuat request, Integer id) {
        var n = tim(nhomId, true);
        var q = quyen(id);
        phaiOTrangThai(n, HOAT_DONG);
        choPhep(duocGui(n, q, id), "Bạn không có quyền gửi đề xuất họp của nhóm này");
        var thanhVien = doiChieuNhanSu(nhomId);
        hopLe(List.of("NOI_BO", "LIEN_PHONG_BAN", "TOAN_CO_QUAN", "BEN_NGOAI")
                .stream().anyMatch(p -> p.equals(request.phamVi())), "Chọn phạm vi cuộc họp");
        var phong = phongBanRepository.findById(n.phongBanChuTriId()).orElseThrow();
        xungDot(HOAT_DONG.equals(phong.getTrangThai()), "Phòng chủ trì không còn hoạt động");
        Integer chuTriMacDinh = "NOI_BO".equals(request.phamVi()) ? phong.getTruongPhongId() : n.truongNhomId();
        Integer chuTri = request.nguoiChuTriId() == null ? chuTriMacDinh : request.nguoiChuTriId();
        var taiKhoanChuTri = timTaiKhoan(chuTri);
        boolean chuTriThuocNhom = thanhVien.stream().anyMatch(tv -> tv.nguoiDungId().equals(chuTri));
        boolean chuTriLanhDao = phanQuyen.coVaiTro(chuTri, "LANH_DAO");
        hopLe("NOI_BO".equals(request.phamVi())
                ? phanQuyen.laTruongPhongCuaPhongBan(chuTri, phong.getPhongBanId())
                : chuTriThuocNhom || chuTriLanhDao, "Chủ trì không phù hợp với phạm vi cuộc họp");
        boolean coKhachNgoai = request.coKhachNgoai() || "BEN_NGOAI".equals(request.phamVi());
        boolean canLanhDao = !"NOI_BO".equals(request.phamVi()) || coKhachNgoai
                || request.coLanhDaoCapCao() || chuTriLanhDao;
        Integer nguoiDuyetId = request.nguoiDuyetId();
        if (nguoiDuyetId == null && canLanhDao && phanQuyen.coVaiTro(n.nguoiLapId(), "LANH_DAO")) {
            nguoiDuyetId = n.nguoiLapId();
        }
        var nguoiDuyet = timTaiKhoan(nguoiDuyetId);
        var thuKy = timTaiKhoan(n.thuKyNhomId());
        Integer duyetId = nguoiDuyet.getNguoiDungId();
        hopLe(phanCong.laNguoiCoTheLamThuKy(thuKy) && !thuKy.getNguoiDungId().equals(chuTri),
                "Thư ký nhóm phải đang hoạt động và khác chủ trì cuộc họp");
        hopLe(!duyetId.equals(id) && !duyetId.equals(thuKy.getNguoiDungId()),
                "Người duyệt phải khác người gửi và thư ký lập kế hoạch");
        hopLe(canLanhDao ? phanQuyen.coVaiTro(duyetId, "LANH_DAO") : quyenVanPhong.duocDieuPhoi(duyetId),
                canLanhDao ? "Đề xuất này cần lãnh đạo duyệt" : "Đề xuất này cần thư ký Văn phòng duyệt");

        var duLieu = new TaoDeXuatCuocHopRequest();
        duLieu.setTieuDe(vanBan(request.tieuDe(), 255));
        duLieu.setMucTieu(vanBan(request.mucTieu(), 3000));
        duLieu.setNoiDungDuKien(vanBan(request.noiDung(), 10000));
        duLieu.setLoaiHinh(request.loaiHinh());
        duLieu.setPhamViCuocHop(request.phamVi());
        duLieu.setCoKhachMoiBenNgoai(coKhachNgoai);
        Integer hopId = deXuatService.guiDeXuatTuNhom(duLieu, timTaiKhoan(id), phong, taiKhoanChuTri, thuKy);
        jdbc.sql("""
                INSERT INTO nhom_lien_phong_de_xuat (cuoc_hop_id, nhom_id, nguoi_gui_id,
                    thanh_vien_khi_gui, nguoi_duyet_id, cap_duyet)
                VALUES (:hop, :nhom, :id, :tv, :duyet, :cap)
                """).param("hop", hopId).param("nhom", nhomId).param("id", id)
                .param("tv", objectMapper.writeValueAsString(thanhVien)).param("duyet", duyetId)
                .param("cap", canLanhDao ? "LANH_DAO" : "VAN_PHONG").update();
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

    private Quyen quyen(Integer id) {
        var nguoiDung = timTaiKhoan(id);
        return new Quyen(quyenVanPhong.duocDieuPhoi(id), phanQuyen.coVaiTro(id, "LANH_DAO"),
                NguoiDungHienTaiService.laTruongPhongCuaPhongBanMinh(nguoiDung)
                        ? nguoiDung.getPhongBan().getPhongBanId() : null);
    }

    private boolean duocKichHoat(Nhom n, Quyen q) {
        return "CAP_CAO".equals(n.capNhom()) ? q.lanhDao() : q.vanPhong();
    }

    private boolean duocDong(Nhom n, Quyen q, Integer id) {
        return q.vanPhong() || q.lanhDao() || (q.phongTruongPhong() != null && n.nguoiLapId().equals(id));
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

    private void ganVaiTro(Integer nhomId, Integer truong, Integer thuKy) {
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

    private void kiemTraThayNguoi(Nhom n, Integer phong, Integer cu, Integer moi) {
        kiemTraPhong(n.nhomId(), phong);
        hopLe(!Objects.equals(cu, moi), "Chọn người cần thêm, bớt hoặc thay thế");
        var tv = thanhVien(n.nhomId());
        hopLe(cu == null || tv.stream().anyMatch(v -> v.nguoiDungId().equals(cu) && v.phongBanId().equals(phong)),
                "Người cần thay không còn thuộc phòng này trong nhóm");
        if (moi != null) {
            kiemTraNguoiCuaPhong(moi, phong);
            hopLe(tv.stream().noneMatch(v -> v.nguoiDungId().equals(moi)), "Người mới đã có trong nhóm");
        } else {
            hopLe(!Objects.equals(cu, n.truongNhomId()) && !Objects.equals(cu, n.thuKyNhomId()),
                    "Cần chọn người thay thế trưởng nhóm hoặc thư ký nhóm");
            hopLe(tv.stream().filter(v -> v.phongBanId().equals(phong)).count() > 1, "Phòng phải còn ít nhất một thành viên");
        }
    }

    private void capNhatThanhVien(Nhom n, Integer phong, Integer cu, Integer moi) {
        if (moi != null) themThanhVien(n.nhomId(), phong, moi);
        if (cu == null) return;
        if (Objects.equals(cu, n.truongNhomId())) ganVaiTro(n.nhomId(), moi, n.thuKyNhomId());
        if (Objects.equals(cu, n.thuKyNhomId())) ganVaiTro(n.nhomId(), n.truongNhomId(), moi);
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
