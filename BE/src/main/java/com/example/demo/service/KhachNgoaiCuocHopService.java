package com.example.demo.service;

import java.net.URI;
import java.nio.file.Path;
import java.sql.Types;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.entity.TaiLieuCuocHop;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.TaiLieuCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetTaiLieuRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.MoiKhachNgoaiRequest;
import com.example.demo.dto.request.NoiDungThuMoi;
import com.example.demo.dto.response.KhachNgoaiCuocHopResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.KhachMoi;
import com.example.demo.entity.KhachMoiCuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KhachMoiCuocHopRepository;
import com.example.demo.repository.KhachMoiRepository;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KhachNgoaiCuocHopService {
    private static final String TAI_LIEU_BO_SUNG = "CHIA_SE_KHACH_NGOAI";
    private static final Path THU_MUC_KHACH = Path.of("uploads", "chia-se-khach-ngoai");
    private final LienKetLoiMoiKhachService lienKet;
    private final JdbcClient jdbc;
    private final LuuTaiLieuCuocHopService luuTaiLieu;
    private final TaiLieuCuocHopRepository taiLieuRepository;
    private final YeuCauPheDuyetTaiLieuRepository taiLieuDaDuyetRepository;
    @Value("${app.upload.ke-hoach-dir:uploads/ke-hoach-cuoc-hop}")
    private String thuMucKeHoach;
    @Value("${app.upload.de-xuat-dir:uploads/de-xuat-cuoc-hop}")
    private String thuMucDeXuat;

    public record LoiMoiKhach(String hoTen, String tieuDe, String noiDung, String chuTri, String loaiHinh,
            String diaDiem, String duongDanHop, LocalDateTime batDau, LocalDateTime ketThuc,
            String trangThaiLich, String trangThaiMoi, boolean duocPhanHoi, String maCuocHop, String mucTieu,
            LocalDateTime batDauDeXuat, LocalDateTime ketThucDeXuat, Integer lichDoiId,
            String lyDoDoiLich, Boolean dongYLichMoi, boolean duocPhanHoiLichMoi, ThuMoiCongBo thuMoi, List<TaiLieuCongBo> taiLieu, List<LienKetLoiMoiKhachService.CapNhat> lichSu) {}
    public record ThuMoiCongBo(String tieuDe, String noiDung, String tenTep, String kieuTep) {}
    public record TaiLieuCongBo(Integer taiLieuId, String tenTaiLieu, String dinhDang, String nhom) {}
    public record TepCongBo(String kieuTep, byte[] noiDung) {}
    public record TepCoTheChiaSe(Integer taiLieuId, String tenTaiLieu, boolean daChiaSe) {}


    private final CuocHopRepository cuocHopRepository;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    private final KhachMoiRepository khachMoiRepository;
    private final KhachMoiCuocHopRepository khachMoiCuocHopRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final Environment environment;
    private final ThongTinThuMoiDaDuyet thongTinThuMoi;
    private final CapNhatHoSoRealtime capNhatRealtime;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<KhachNgoaiCuocHopResponse> layDanhSach(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!phanCongXuLyKeHoachService.laThuKyThamGia(cuocHopId, nguoiDungId)
                && !ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem danh sách khách ngoài");
        }
        return khachMoiCuocHopRepository.findByCuocHop_CuocHopIdOrderByNgayMoiDescKhachMoiCuocHopIdDesc(cuocHopId)
                .stream().map(KhachNgoaiCuocHopService::chuyenResponse).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public KhachNgoaiCuocHopResponse moiQuaEmail(Integer cuocHopId, Integer thuKyId, MoiKhachNgoaiRequest request) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!phanCongXuLyKeHoachService.laThuKyToChuc(cuocHop, thuKyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ thư ký tổ chức được mời khách ngoài");
        }

        var keHoach = yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElse(null);
        if (!ThaoTacCuocHop.coLichMoiThamDu(cuocHop, keHoach)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cần kế hoạch đã duyệt và lịch đã xác nhận còn hiệu lực để mời khách ngoài");
        }
        if (!(Boolean.TRUE.equals(cuocHop.getCoKhachMoiBenNgoai()) || "BEN_NGOAI".equals(cuocHop.getPhamViCuocHop()))
                || !"LANH_DAO".equals(keHoach.getCapPheDuyet())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cần bổ sung khách ngoài vào kế hoạch và trình lãnh đạo duyệt trước khi gửi thư");
        }

        String email = request.email().strip().toLowerCase(Locale.ROOT);
        if (nguoiDungRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này thuộc tài khoản nội bộ. Hãy mời qua mục Thành viên");
        }
        KhachMoiCuocHop loiMoi = khachMoiCuocHopRepository
                .findFirstByCuocHop_CuocHopIdAndKhachMoi_EmailIgnoreCaseOrderByKhachMoiCuocHopIdAsc(cuocHopId, email)
                .orElseGet(KhachMoiCuocHop::new);
        if ("DA_HUY".equals(loiMoi.getTrangThaiMoi())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lời mời cho khách này đã bị thu hồi");
        }

        LichHop lich = ThaoTacCuocHop.lichHienHanh(cuocHop);
        String diaDiemHop = layDiaDiemHop(cuocHop, lich, request.duongDanHop());
        var maMoi = lienKet.taoMa();
        boolean capNhat = loiMoi.getKhachMoiCuocHopId() != null;
        KhachMoi khachMoi = loiMoi.getKhachMoi() != null ? loiMoi.getKhachMoi()
                : khachMoiRepository.findFirstByEmailIgnoreCaseOrderByKhachMoiIdAsc(email).orElseGet(KhachMoi::new);
        if (!"HOAT_DONG".equals(khachMoi.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hồ sơ khách mời này đã ngừng hoạt động");
        }
        if (khachMoi.getKhachMoiId() == null) {
            khachMoi.setHoTen(request.hoTen().strip());
            khachMoi.setEmail(email);
            khachMoi.setDonViCongTac(chuanHoa(request.donViCongTac()));
            khachMoi.setChucVu(chuanHoa(request.chucVu()));
            khachMoiRepository.save(khachMoi);
        }

        loiMoi.setKhachMoi(khachMoi);
        loiMoi.setCuocHop(cuocHop);
        loiMoi.capMaTruyCap(maMoi.bam(), ThaoTacCuocHop.laCuocHopTrucTuyen(cuocHop)
                ? request.duongDanHop().strip() : null, lich.getThoiGianKetThuc().plusDays(7));
        // Ghi và kiểm tra ràng buộc trước khi gửi. Lỗi SMTP làm giao dịch quay lui.
        khachMoiCuocHopRepository.saveAndFlush(loiMoi);
        var thongTin = thongTinThuMoi.doc(keHoach);
        var tep = TepThuMoi.doc(request.thuMoi().tepDinhKem(), thongTin.lichHopId());
        lienKet.ghiNhan(loiMoi, maMoi, capNhat ? "Cập nhật thư mời" : "Phát hành thư mời");
        luuNoiDungThuMoi(loiMoi, thongTin.chuDe(request.thuMoi()), request.thuMoi(), tep);
        guiThuMoi(khachMoi, thongTin, diaDiemHop, request, maMoi.duongDan(), tep);
        return chuyenResponse(loiMoi);
    }

    @Transactional(readOnly = true)
    public LoiMoiKhach xemLoiMoi(String ma) {
        return taoLoiMoiKhach(timLoiMoi(ma));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LoiMoiKhach phanHoi(String ma, boolean thamDu) {
        KhachMoiCuocHop loiMoi = timLoiMoi(ma);
        cuocHopRepository.findByIdForUpdate(loiMoi.getCuocHop().getCuocHopId())
                .orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        // Đọc lại trạng thái sau khi khóa để hai phản hồi đồng thời không ghi đè nhau.
        entityManager.refresh(loiMoi);
        if (!taoLoiMoiKhach(loiMoi).duocPhanHoi()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lời mời hiện không nhận phản hồi");
        }
        loiMoi.setTrangThaiMoi(thamDu ? "DONG_Y" : "TU_CHOI");
        capNhatRealtime.baoCuocHop(loiMoi.getCuocHop(), null);
        return taoLoiMoiKhach(loiMoi);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LoiMoiKhach phanHoiLichMoi(String ma, Integer lichHopId, boolean dongY) {
        var loiMoi = timLoiMoi(ma);
        cuocHopRepository.findByIdForUpdate(loiMoi.getCuocHop().getCuocHopId())
                .orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        entityManager.refresh(loiMoi);
        var hienTai = taoLoiMoiKhach(loiMoi);
        if (!hienTai.duocPhanHoiLichMoi() || !java.util.Objects.equals(lichHopId, hienTai.lichDoiId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lịch này đã ngừng nhận phản hồi");
        }
        jdbc.sql("""
                UPDATE phan_hoi_doi_lich_khach SET dong_y = :dongY, thoi_gian_phan_hoi = CURRENT_TIMESTAMP
                WHERE lich_hop_id = :lich AND khach_moi_cuoc_hop_id = :khach
                """).param("dongY", dongY).param("lich", lichHopId)
                .param("khach", loiMoi.getKhachMoiCuocHopId()).update();
        capNhatRealtime.baoCuocHop(loiMoi.getCuocHop(), null);
        return taoLoiMoiKhach(loiMoi);
    }

    private record PhanHoiLichMoi(Boolean dongY) {}

    private KhachMoiCuocHop timLoiMoi(String ma) {
        var loiMoi = lienKet.timTheoMa(ma)
                .filter(muc -> !"DA_HUY".equals(muc.getTrangThaiMoi())
                        && "HOAT_DONG".equals(muc.getKhachMoi().getTrangThai()))
                .orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        var lich = ThaoTacCuocHop.lichHienHanh(loiMoi.getCuocHop());
        LocalDateTime han = loiMoi.getHanTruyCap();
        if (han == null) throw khongTimThayLoiMoi();
        if (lich != null && lich.getThoiGianKetThuc().plusDays(7).isAfter(han)) {
            han = lich.getThoiGianKetThuc().plusDays(7);
        }
        if (!han.isAfter(LocalDateTime.now())) throw khongTimThayLoiMoi();
        return loiMoi;
    }

    private LoiMoiKhach taoLoiMoiKhach(KhachMoiCuocHop loiMoi) {
        CuocHop hop = loiMoi.getCuocHop();
        LichHop lich = ThaoTacCuocHop.lichHienHanh(hop);
        boolean choDoi = ThaoTacCuocHop.choXacNhanDoiLich(hop);
        var lichDeXuat = choDoi ? hop.getDanhSachLichHop().stream()
                .filter(muc -> muc.isLaLichDoi() && ThaoTacLichHop.dangDeXuat(muc))
                .max(Comparator.comparing(LichHop::getLichHopId)).orElse(null) : null;
        var phanHoiLich = lichDeXuat == null ? null : jdbc.sql("""
                SELECT dong_y FROM phan_hoi_doi_lich_khach
                WHERE lich_hop_id = :lich AND khach_moi_cuoc_hop_id = :khach
                """).param("lich", lichDeXuat.getLichHopId()).param("khach", loiMoi.getKhachMoiCuocHopId())
                .query(PhanHoiLichMoi.class).optional().orElse(null);
        boolean lichHieuLuc = lich != null && !choDoi && !"DA_HUY".equals(hop.getTrangThai());
        var hoSo = keHoachDaDuyet(hop.getCuocHopId());
        var banChup = hoSo == null ? null : thongTinThuMoi.docBanChup(hoSo);
        var duLieu = lich != null && hoSo != null ? thongTinThuMoi.doc(hoSo) : null;
        boolean duocPhanHoi = lichHieuLuc && duLieu != null && duLieu.thoiGianBatDau().isAfter(LocalDateTime.now())
                && "CHO_PHAN_HOI".equals(loiMoi.getTrangThaiMoi());
        return new LoiMoiKhach(loiMoi.getKhachMoi().getHoTen(),
                banChup == null ? hop.getTieuDe() : banChup.tieuDe(),
                banChup == null ? null : banChup.noiDungDuKien(),
                banChup == null ? null : banChup.tenChuTri(), hop.getLoaiHinh(),
                duLieu == null ? null : duLieu.diaDiem(),
                lichHieuLuc && duLieu != null && "DONG_Y".equals(loiMoi.getTrangThaiMoi()) ? loiMoi.getDuongDanHop() : null,
                duLieu == null ? null : duLieu.thoiGianBatDau(), duLieu == null ? null : duLieu.thoiGianKetThuc(),
                choDoi ? "CHO_DOI_LICH" : duLieu == null ? "DA_HUY" : "DA_XAC_NHAN",
                loiMoi.getTrangThaiMoi(), duocPhanHoi, hop.getMaCuocHop(), banChup == null ? null : banChup.mucTieu(),
                lichDeXuat == null ? null : lichDeXuat.getThoiGianBatDau(),
                lichDeXuat == null ? null : lichDeXuat.getThoiGianKetThuc(),
                lichDeXuat == null ? null : lichDeXuat.getLichHopId(),
                lichDeXuat == null ? null : lichDeXuat.getLyDoThayDoi(),
                phanHoiLich == null ? null : phanHoiLich.dongY(),
                phanHoiLich != null && lichDeXuat.getThoiGianBatDau().isAfter(LocalDateTime.now())
                        && !"TU_CHOI".equals(loiMoi.getTrangThaiMoi()) && !"DA_HUY".equals(loiMoi.getTrangThaiMoi())
                        && !"DA_HUY".equals(hop.getTrangThai()),
                thuMoi(loiMoi), taiLieuCongBo(hop.getCuocHopId(), hoSo).stream().map(tl -> new TaiLieuCongBo(
                        tl.getTaiLieuId(), tl.getTenTaiLieu(), tl.getDinhDangFile(),
                        TAI_LIEU_BO_SUNG.equals(tl.getLoaiTaiLieu()) ? "Tài liệu bổ sung" : "Tệp hồ sơ cuộc họp")).toList(),
                lienKet.lichSu(loiMoi));
    }

    @Transactional(readOnly = true)
    public TepCongBo taiTaiLieu(String ma, Integer taiLieuId) {
        var loiMoi = timLoiMoi(ma);
        Integer cuocHopId = loiMoi.getCuocHop().getCuocHopId();
        var tl = taiLieuCongBo(cuocHopId, keHoachDaDuyet(cuocHopId)).stream()
                .filter(muc -> taiLieuId.equals(muc.getTaiLieuId()))
                .findFirst().orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        Path thuMuc = switch (tl.getLoaiTaiLieu()) {
            case TAI_LIEU_BO_SUNG -> THU_MUC_KHACH;
            case "KE_HOACH_CUOC_HOP" -> Path.of(thuMucKeHoach);
            default -> Path.of(thuMucDeXuat);
        };
        var tep = luuTaiLieu.docTep(tl, thuMuc);
        return new TepCongBo("pdf".equals(tl.getDinhDangFile()) ? "application/pdf" : "application/octet-stream",
                tep.noiDung());
    }

    @Transactional(readOnly = true)
    public TepCongBo taiGiayMoi(String ma) {
        var loiMoi = timLoiMoi(ma);
        return jdbc.sql("""
                SELECT kieu_tep, noi_dung_tep AS noi_dung FROM noi_dung_loi_moi_khach
                WHERE khach_moi_cuoc_hop_id = ? AND noi_dung_tep IS NOT NULL
                """).param(loiMoi.getKhachMoiCuocHopId()).query(TepCongBo.class).optional()
                .orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
    }

    @Transactional(readOnly = true)
    public List<TepCoTheChiaSe> taiLieuCoTheChiaSe(Integer cuocHopId, Integer thuKyId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        var hop = cuocHopRepository.findById(cuocHopId).orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        kiemTraChiaSe(hop, thuKyId);
        var congBo = taiLieuCongBo(cuocHopId, keHoachDaDuyet(cuocHopId)).stream()
                .map(TaiLieuCuocHop::getTaiLieuId).collect(java.util.stream.Collectors.toSet());
        return taiLieuRepository.findByCuocHop_CuocHopId(cuocHopId).stream().filter(this::laTepHoSo)
                .sorted(Comparator.comparing(TaiLieuCuocHop::getTaiLieuId))
                .map(tl -> new TepCoTheChiaSe(tl.getTaiLieuId(), tl.getTenTaiLieu(), congBo.contains(tl.getTaiLieuId())))
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void chiaSeTepCoSan(Integer cuocHopId, Integer thuKyId, Integer taiLieuId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        var hop = cuocHopRepository.findByIdForUpdate(cuocHopId).orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        kiemTraChiaSe(hop, thuKyId);
        var tep = taiLieuRepository.findById(taiLieuId)
                .filter(tl -> cuocHopId.equals(tl.getCuocHop().getCuocHopId()) && laTepHoSo(tl))
                .orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        if (taiLieuCongBo(cuocHopId, keHoachDaDuyet(cuocHopId)).stream()
                .anyMatch(tl -> tep.getTaiLieuId().equals(tl.getTaiLieuId()))) return;
        jdbc.sql("INSERT INTO tai_lieu_chia_se_khach (tai_lieu_id) VALUES (?)").param(taiLieuId).update();
        baoTaiLieuMoi(hop);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void chiaSeTaiLieu(Integer cuocHopId, Integer thuKyId, MultipartFile tep) {
        var nguoiTaiLen = nguoiDungHienTaiService.layTaiKhoanHoatDong(thuKyId);
        var hop = cuocHopRepository.findByIdForUpdate(cuocHopId).orElseThrow(KhachNgoaiCuocHopService::khongTimThayLoiMoi);
        kiemTraChiaSe(hop, thuKyId);
        luuTaiLieu.luuTaiLieu(hop, nguoiTaiLen, tep, TAI_LIEU_BO_SUNG, THU_MUC_KHACH);
        baoTaiLieuMoi(hop);
    }

    private void kiemTraChiaSe(CuocHop hop, Integer thuKyId) {
        if (!phanCongXuLyKeHoachService.laThuKyToChuc(hop, thuKyId) || keHoachDaDuyet(hop.getCuocHopId()) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ thư ký tổ chức của cuộc họp đã duyệt được chia sẻ");
        }
    }

    private void baoTaiLieuMoi(CuocHop hop) {
        var lich = ThaoTacCuocHop.lichHienHanh(hop);
        lienKet.baoCapNhat(hop, lich == null ? null : lich.getLichHopId(), "Bổ sung tài liệu cuộc họp",
                "Hồ sơ cuộc họp đã có tài liệu bổ sung dành cho khách mời.");
    }

    private boolean laTepHoSo(TaiLieuCuocHop tl) {
        return "KE_HOACH_CUOC_HOP".equals(tl.getLoaiTaiLieu()) || "DE_XUAT_CUOC_HOP".equals(tl.getLoaiTaiLieu());
    }

    private List<TaiLieuCuocHop> taiLieuCongBo(Integer cuocHopId, YeuCauPheDuyet keHoach) {
        var daDuyet = keHoach == null ? List.<Integer>of()
                : taiLieuDaDuyetRepository.timIdTaiLieuTheoLanTrinh(keHoach.getYeuCauPheDuyetId());
        var daChiaSe = jdbc.sql("""
                SELECT cs.tai_lieu_id FROM tai_lieu_chia_se_khach cs JOIN tai_lieu_cuoc_hop tl
                  ON tl.tai_lieu_id = cs.tai_lieu_id WHERE tl.cuoc_hop_id = ?
                """).param(cuocHopId).query(Integer.class).set();
        return taiLieuRepository.findByCuocHop_CuocHopId(cuocHopId).stream()
                .filter(tl -> TAI_LIEU_BO_SUNG.equals(tl.getLoaiTaiLieu()) || (laTepHoSo(tl)
                        && (daDuyet.contains(tl.getTaiLieuId()) || daChiaSe.contains(tl.getTaiLieuId()))))
                .sorted(Comparator.comparing(TaiLieuCuocHop::getTaiLieuId)).toList();
    }

    private YeuCauPheDuyet keHoachDaDuyet(Integer cuocHopId) {
        return yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThaiOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP, YeuCauPheDuyet.TrangThai.DA_DUYET)
                .orElse(null);
    }

    private ThuMoiCongBo thuMoi(KhachMoiCuocHop loiMoi) {
        return jdbc.sql("""
                SELECT tieu_de, noi_dung, ten_tep, kieu_tep FROM noi_dung_loi_moi_khach WHERE khach_moi_cuoc_hop_id = ?
                """).param(loiMoi.getKhachMoiCuocHopId()).query(ThuMoiCongBo.class).optional().orElse(null);
    }

    private void luuNoiDungThuMoi(KhachMoiCuocHop loiMoi, String tieuDe, NoiDungThuMoi thuMoi, TepThuMoi tep) {
        jdbc.sql("""
                INSERT INTO noi_dung_loi_moi_khach (khach_moi_cuoc_hop_id, tieu_de, noi_dung, ten_tep, kieu_tep, noi_dung_tep)
                VALUES (:id, :tieuDe, :noiDung, :ten, :kieu, :tep) AS moi
                ON DUPLICATE KEY UPDATE tieu_de = moi.tieu_de, noi_dung = moi.noi_dung,
                    ten_tep = moi.ten_tep, kieu_tep = moi.kieu_tep, noi_dung_tep = moi.noi_dung_tep
                """).param("id", loiMoi.getKhachMoiCuocHopId()).param("tieuDe", tieuDe)
                .param("noiDung", thuMoi.noiDung() == null || thuMoi.noiDung().isBlank()
                        ? "Trân trọng kính mời bạn tham dự cuộc họp." : thuMoi.noiDung().strip())
                .param("ten", tep == null ? null : tep.tenTep(), Types.VARCHAR)
                .param("kieu", tep == null ? null : tep.kieuTep(), Types.VARCHAR)
                .param("tep", tep == null ? null : tep.noiDung(), Types.BLOB).update();
    }

    private static ResponseStatusException khongTimThayLoiMoi() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Lời mời hoặc tài liệu không còn hiệu lực");
    }

    private String layDiaDiemHop(CuocHop cuocHop, LichHop lichHop, String duongDan) {
        if (ThaoTacCuocHop.laCuocHopTrucTuyen(cuocHop)) {
            try {
                URI uri = URI.create(duongDan == null ? "" : duongDan.strip());
                if (uri.toASCIIString().length() <= 2048 && "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null && uri.getUserInfo() == null) {
                    return "Link họp trực tuyến: " + uri;
                }
            } catch (IllegalArgumentException ignored) {
                // Thông báo giống nhau cho đường dẫn sai định dạng hoặc sai giao thức.
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập link họp trực tuyến HTTPS hợp lệ");
        }

        var phongHop = lichHop.getPhongHop();
        if (phongHop == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Lịch họp trực tiếp cần có phòng họp");
        }
        return phongHop.getTenPhongHop()
                + (chuanHoa(phongHop.getViTri()) == null ? "" : " – " + phongHop.getViTri().strip());
    }

    private void guiThuMoi(KhachMoi khachMoi, ThongTinThuMoiDaDuyet.DuLieu thongTin,
            String diaDiemHop, MoiKhachNgoaiRequest request, String lienKetLoiMoi, TepThuMoi tep) {
        NoiDungThuMoi thuMoi = request.thuMoi();
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        String diaChiGui = chuanHoa(environment.getProperty("app.mail.from", environment.getProperty("spring.mail.username", "")));
        if (mailSender == null || diaChiGui == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Backend chưa được cấu hình gửi email");
        }
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, tep != null, StandardCharsets.UTF_8.name());
            helper.setValidateAddresses(true);
            helper.setFrom(diaChiGui);
            helper.setTo(khachMoi.getEmail());
            helper.setSubject(thongTin.chuDe(thuMoi));
            String nguoiNhan = java.util.stream.Stream.of(request.hoTen(), request.chucVu(),
                    request.donViCongTac(), request.truSo()).map(KhachNgoaiCuocHopService::chuanHoa)
                    .filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.joining(", "));
            helper.setText(thongTin.noiDungEmail(thuMoi, nguoiNhan, diaDiemHop)
                    + "\nXem cuộc họp và phản hồi lời mời tại:\n" + lienKetLoiMoi);
            if (tep != null) tep.dinhKem(helper);
            mailSender.send(message);
        } catch (MailException | MessagingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Không gửi được thư mời. Vui lòng kiểm tra cấu hình SMTP và thử lại", ex);
        }
    }

    private static String chuanHoa(String giaTri) {
        return giaTri == null || giaTri.isBlank() ? null : giaTri.strip();
    }

    private static KhachNgoaiCuocHopResponse chuyenResponse(KhachMoiCuocHop loiMoi) {
        KhachMoi khachMoi = loiMoi.getKhachMoi();
        return new KhachNgoaiCuocHopResponse(loiMoi.getKhachMoiCuocHopId(), khachMoi.getHoTen(),
                khachMoi.getEmail(), khachMoi.getDonViCongTac(), khachMoi.getChucVu(),
                loiMoi.getTrangThaiMoi(), loiMoi.getNgayMoi());
    }
}
