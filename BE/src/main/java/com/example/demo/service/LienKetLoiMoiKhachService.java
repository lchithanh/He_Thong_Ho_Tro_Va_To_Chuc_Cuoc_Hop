package com.example.demo.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.KhachMoiCuocHop;
import com.example.demo.repository.KhachMoiCuocHopRepository;

@Service
public class LienKetLoiMoiKhachService {
    private static final SecureRandom NGUON_MA = new SecureRandom();
    private static final Set<String> TRANG_THAI_NHAN_CAP_NHAT = Set.of("CHO_PHAN_HOI", "DONG_Y");

    private final JdbcClient jdbc;
    private final KhachMoiCuocHopRepository loiMoiRepository;
    private final ApplicationEventPublisher suKien;
    private final String urlCongKhai;
    private final boolean batGuiEmail;

    public LienKetLoiMoiKhachService(JdbcClient jdbc, KhachMoiCuocHopRepository loiMoiRepository,
            ApplicationEventPublisher suKien, @Value("${app.url-cong-khai:http://localhost:5173}") String urlCongKhai,
            @Value("${app.email.enabled:false}") boolean batGuiEmail) {
        this.jdbc = jdbc;
        this.loiMoiRepository = loiMoiRepository;
        this.suKien = suKien;
        this.urlCongKhai = urlCongKhai;
        this.batGuiEmail = batGuiEmail;
    }

    public record CapNhat(long capNhatId, String noiDung, LocalDateTime ngayCapNhat) {}
    record MaMoi(String duongDan, String bam) {}

    MaMoi taoMa() {
        byte[] duLieu = new byte[32];
        NGUON_MA.nextBytes(duLieu);
        String ma = Base64.getUrlEncoder().withoutPadding().encodeToString(duLieu);
        return new MaMoi(layUrl(ma), bamMa(ma));
    }

    void ghiNhan(KhachMoiCuocHop loiMoi, MaMoi ma, String noiDung) {
        jdbc.sql("""
                INSERT INTO cap_nhat_loi_moi_khach (khach_moi_cuoc_hop_id, ma_truy_cap_sha256, noi_dung)
                VALUES (?, ?, ?)
                """).params(loiMoi.getKhachMoiCuocHopId(), ma.bam(), noiDung).update();
    }

    Optional<KhachMoiCuocHop> timTheoMa(String ma) {
        if (ma == null || !ma.matches("[A-Za-z0-9_-]{43}")) return Optional.empty();
        String bam = bamMa(ma);
        return jdbc.sql("SELECT khach_moi_cuoc_hop_id FROM cap_nhat_loi_moi_khach WHERE ma_truy_cap_sha256 = ?")
                .param(bam).query(Integer.class).optional().flatMap(loiMoiRepository::findById)
                .or(() -> loiMoiRepository.findByMaTruyCapSha256(bam));
    }

    List<CapNhat> lichSu(KhachMoiCuocHop loiMoi) {
        return jdbc.sql("""
                SELECT cap_nhat_id, noi_dung, ngay_cap_nhat FROM cap_nhat_loi_moi_khach
                WHERE khach_moi_cuoc_hop_id = ? ORDER BY cap_nhat_id DESC LIMIT 50
                """).param(loiMoi.getKhachMoiCuocHopId()).query(CapNhat.class).list();
    }

    void baoCapNhat(CuocHop hop, Integer lichHopId, String tieuDe, String noiDung) {
        for (KhachMoiCuocHop loiMoi : loiMoiRepository
                .findByCuocHop_CuocHopIdOrderByNgayMoiDescKhachMoiCuocHopIdDesc(hop.getCuocHopId())) {
            if (loiMoi.getHanTruyCap() == null || "DA_HUY".equals(loiMoi.getTrangThaiMoi())
                    || !"HOAT_DONG".equals(loiMoi.getKhachMoi().getTrangThai())) continue;
            var ma = taoMa();
            ghiNhan(loiMoi, ma, tieuDe);
            String email = loiMoi.getKhachMoi().getEmail();
            if (batGuiEmail && TRANG_THAI_NHAN_CAP_NHAT.contains(loiMoi.getTrangThaiMoi()) && email != null
                    && !email.toLowerCase(Locale.ROOT).endsWith(".invalid")) {
                suKien.publishEvent(new ThongBaoEmailCuocHop.EmailCanGui(lichHopId, List.of(email),
                        "[Họp hành chính] " + tieuDe, noiDung + "\n\nXem hồ sơ cập nhật:\n" + ma.duongDan(), null));
            }
        }
    }

    private static String bamMa(String ma) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(ma.getBytes(StandardCharsets.US_ASCII)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String layUrl(String ma) {
        try {
            URI uri = URI.create(urlCongKhai.strip());
            String host = uri.getHost();
            boolean local = "localhost".equals(host) || "127.0.0.1".equals(host);
            if (host != null && uri.getUserInfo() == null && uri.getQuery() == null && uri.getFragment() == null
                    && ("https".equalsIgnoreCase(uri.getScheme()) || local && "http".equalsIgnoreCase(uri.getScheme()))) {
                return uri.toASCIIString().replaceFirst("/+$", "") + "/loi-moi-khach#" + ma;
            }
        } catch (IllegalArgumentException ignored) {
            // Báo cùng một lỗi cấu hình cho mọi URL không hợp lệ.
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Chưa cấu hình URL trang khách hợp lệ");
    }
}
