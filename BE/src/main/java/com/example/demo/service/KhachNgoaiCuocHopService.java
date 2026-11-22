package com.example.demo.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Set;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
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
    private static final SecureRandom NGUON_MA = new SecureRandom();

    public record LoiMoiKhach(String hoTen, String tieuDe, String noiDung, String chuTri, String loaiHinh,
            String diaDiem, String duongDanHop, LocalDateTime batDau, LocalDateTime ketThuc,
            String trangThaiLich, String trangThaiMoi, boolean duocPhanHoi) {}


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
        if (loiMoi.getHanTruyCap() != null || "DA_HUY".equals(loiMoi.getTrangThaiMoi())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã có lời mời cho cuộc họp");
        }

        LichHop lich = ThaoTacCuocHop.lichHienHanh(cuocHop);
        String diaDiemHop = layDiaDiemHop(cuocHop, lich, request.duongDanHop());
        byte[] duLieuMa = new byte[32];
        NGUON_MA.nextBytes(duLieuMa);
        String ma = Base64.getUrlEncoder().withoutPadding().encodeToString(duLieuMa);
        String lienKetLoiMoi = layUrlTrangLoiMoi(ma);
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
        loiMoi.capMaTruyCap(bamMa(ma), ThaoTacCuocHop.laCuocHopTrucTuyen(cuocHop)
                ? request.duongDanHop().strip() : null, lich.getThoiGianKetThuc().plusDays(7));
        // Ghi và kiểm tra ràng buộc trước khi gửi. Lỗi SMTP làm giao dịch quay lui.
        khachMoiCuocHopRepository.saveAndFlush(loiMoi);
        guiThuMoi(khachMoi, thongTinThuMoi.doc(keHoach), diaDiemHop, request, lienKetLoiMoi);
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

    private KhachMoiCuocHop timLoiMoi(String ma) {
        if (ma == null || !ma.matches("[A-Za-z0-9_-]{43}")) throw khongTimThayLoiMoi();
        var loiMoi = khachMoiCuocHopRepository.findByMaTruyCapSha256(bamMa(ma))
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
        boolean lichHieuLuc = lich != null && !choDoi && !"DA_HUY".equals(hop.getTrangThai());
        var hoSo = yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(hop.getCuocHopId(),
                        LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .filter(yc -> yc.getTrangThai() == com.example.demo.entity.YeuCauPheDuyet.TrangThai.DA_DUYET)
                .orElse(null);
        var duLieu = lich != null && hoSo != null ? thongTinThuMoi.doc(hoSo) : null;
        boolean duocPhanHoi = lichHieuLuc && duLieu != null && duLieu.thoiGianBatDau().isAfter(LocalDateTime.now())
                && "CHO_PHAN_HOI".equals(loiMoi.getTrangThaiMoi());
        return new LoiMoiKhach(loiMoi.getKhachMoi().getHoTen(),
                duLieu == null ? hop.getTieuDe() : duLieu.tieuDe(),
                !lichHieuLuc || duLieu == null ? null : duLieu.noiDung(),
                duLieu == null ? null : duLieu.chuTri(), hop.getLoaiHinh(),
                duLieu == null ? null : duLieu.diaDiem(),
                lichHieuLuc && duLieu != null && "DONG_Y".equals(loiMoi.getTrangThaiMoi()) ? loiMoi.getDuongDanHop() : null,
                duLieu == null ? null : duLieu.thoiGianBatDau(), duLieu == null ? null : duLieu.thoiGianKetThuc(),
                choDoi ? "CHO_DOI_LICH" : duLieu == null ? "DA_HUY" : "DA_XAC_NHAN",
                loiMoi.getTrangThaiMoi(), duocPhanHoi);
    }

    private static ResponseStatusException khongTimThayLoiMoi() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Lời mời không còn hiệu lực hoặc không hợp lệ");
    }

    private static String bamMa(String ma) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(ma.getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String layUrlTrangLoiMoi(String ma) {
        String cauHinh = environment.getProperty("app.url-cong-khai", "http://localhost:5173");
        try {
            URI uri = URI.create(cauHinh.strip());
            boolean trangLocal = Set.of("localhost", "127.0.0.1").contains(
                    uri.getHost() == null ? "" : uri.getHost());
            if (uri.getHost() != null && uri.getUserInfo() == null && uri.getQuery() == null
                    && uri.getFragment() == null && ("https".equalsIgnoreCase(uri.getScheme())
                            || trangLocal && "http".equalsIgnoreCase(uri.getScheme()))) {
                return uri.toASCIIString().replaceFirst("/+$", "") + "/loi-moi-khach#" + ma;
            }
        } catch (IllegalArgumentException ignored) {
            // Dùng cùng thông báo cho các cấu hình URL không hợp lệ.
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chưa cấu hình URL trang khách hợp lệ");
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
            String diaDiemHop, MoiKhachNgoaiRequest request, String lienKetLoiMoi) {
        NoiDungThuMoi thuMoi = request.thuMoi();
        TepThuMoi tep = TepThuMoi.doc(thuMoi.tepDinhKem(), thongTin.lichHopId());
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
