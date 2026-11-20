package com.example.demo.service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KhachNgoaiCuocHopService {

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
        if (khachMoiCuocHopRepository.existsByCuocHop_CuocHopIdAndKhachMoi_EmailIgnoreCase(cuocHopId, email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã có lời mời cho cuộc họp");
        }

        String diaDiemHop = layDiaDiemHop(cuocHop, keHoach.getLichHop(), request.duongDanHop());
        KhachMoi khachMoi = khachMoiRepository.findFirstByEmailIgnoreCaseOrderByKhachMoiIdAsc(email)
                .orElseGet(KhachMoi::new);
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

        KhachMoiCuocHop loiMoi = new KhachMoiCuocHop();
        loiMoi.setKhachMoi(khachMoi);
        loiMoi.setCuocHop(cuocHop);
        // Ghi và kiểm tra ràng buộc trước khi gửi. Lỗi SMTP làm giao dịch quay lui.
        khachMoiCuocHopRepository.saveAndFlush(loiMoi);
        guiThuMoi(khachMoi, thongTinThuMoi.doc(keHoach), diaDiemHop, request);
        return chuyenResponse(loiMoi);
    }

    private String layDiaDiemHop(CuocHop cuocHop, LichHop lichHop, String duongDan) {
        if (ThaoTacCuocHop.laCuocHopTrucTuyen(cuocHop)) {
            try {
                URI uri = URI.create(duongDan == null ? "" : duongDan.strip());
                if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null && uri.getUserInfo() == null) {
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
            String diaDiemHop, MoiKhachNgoaiRequest request) {
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
            helper.setText(thongTin.noiDungEmail(thuMoi, nguoiNhan, diaDiemHop));
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
