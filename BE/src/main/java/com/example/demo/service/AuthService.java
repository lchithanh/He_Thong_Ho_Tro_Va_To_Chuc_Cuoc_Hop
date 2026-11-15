package com.example.demo.service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.NguoiDungVaiTro;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.NguoiDungVaiTroRepository;
import com.example.demo.security.JwtService;
import com.example.demo.repository.PhanCongThuKyRepository;

@Service
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PhanCongThuKyRepository phanCongThuKyRepository;


    public AuthService(
            NguoiDungRepository nguoiDungRepository,
            NguoiDungVaiTroRepository nguoiDungVaiTroRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            PhanCongThuKyRepository phanCongThuKyRepository) {

        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungVaiTroRepository = nguoiDungVaiTroRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.phanCongThuKyRepository = phanCongThuKyRepository;
    }

    @Transactional(readOnly = true)
    public LoginResponse dangNhap(LoginRequest request) {

        if (request.getTaiKhoan() == null
                || request.getTaiKhoan().isBlank()
                || request.getMatKhau() == null
                || request.getMatKhau().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui long nhap tai khoan va mat khau");
        }

        String taiKhoan = request.getTaiKhoan().trim();

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(taiKhoan)
                .or(() -> nguoiDungRepository.findByEmail(taiKhoan))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Tai khoan hoac mat khau khong chinh xac"));

        if (!"HOAT_DONG".equals(nguoiDung.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tai khoan da bi khoa hoac ngung hoat dong");
        }

        if (!passwordEncoder.matches(
                request.getMatKhau(),
                nguoiDung.getMatKhauMaHoa())) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Tai khoan hoac mat khau khong chinh xac");
        }

        NguoiDungVaiTro nguoiDungVaiTro = nguoiDungVaiTroRepository
                .findByNguoiDungId(nguoiDung.getNguoiDungId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Tai khoan chua duoc gan vai tro"));

        LoginResponse response = new LoginResponse();

        response.setNguoiDungId(nguoiDung.getNguoiDungId());
        response.setHoTen(nguoiDung.getHoTen());
        response.setEmail(nguoiDung.getEmail());
        response.setTenDangNhap(nguoiDung.getTenDangNhap());

        if (nguoiDung.getPhongBan() != null) {
            response.setPhongBanId(nguoiDung.getPhongBan().getPhongBanId());
            response.setTenPhongBan(
                    nguoiDung.getPhongBan().getTenPhongBan());
        }

        response.setMaVaiTro(
                nguoiDungVaiTro.getVaiTro().getMaVaiTro());

        response.setTenVaiTro(
                nguoiDungVaiTro.getVaiTro().getTenVaiTro());

        // Tạo JWT Token
        response.setToken(
        	    jwtService.generateToken(
        	        nguoiDung.getTenDangNhap(),
        	        nguoiDungVaiTro.getVaiTro().getMaVaiTro()
        	    )
        	);

        ganThongTinPhanCong(response, nguoiDung);
        return response;
    }
    @Transactional(readOnly = true)
    public LoginResponse layThongTinNguoiDung(String tenDangNhap) {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Khong tim thay nguoi dung"));

        if (!"HOAT_DONG".equals(nguoiDung.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tai khoan da bi khoa hoac ngung hoat dong");
        }

        NguoiDungVaiTro nguoiDungVaiTro = nguoiDungVaiTroRepository
                .findByNguoiDungId(nguoiDung.getNguoiDungId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Tai khoan chua duoc gan vai tro"));

        LoginResponse response = new LoginResponse();

        response.setNguoiDungId(nguoiDung.getNguoiDungId());
        response.setHoTen(nguoiDung.getHoTen());
        response.setEmail(nguoiDung.getEmail());
        response.setTenDangNhap(nguoiDung.getTenDangNhap());

        if (nguoiDung.getPhongBan() != null) {
            response.setPhongBanId(nguoiDung.getPhongBan().getPhongBanId());
            response.setTenPhongBan(
                    nguoiDung.getPhongBan().getTenPhongBan());
        }

        response.setMaVaiTro(
                nguoiDungVaiTro.getVaiTro().getMaVaiTro());

        response.setTenVaiTro(
                nguoiDungVaiTro.getVaiTro().getTenVaiTro());

        // Không tạo JWT mới khi lấy thông tin tài khoản
        ganThongTinPhanCong(response, nguoiDung);
        return response;
    }
    private void ganThongTinPhanCong(LoginResponse response, NguoiDung nguoiDung) {
        boolean laTruongPhong = "CAN_BO".equals(response.getMaVaiTro())
                && NguoiDungHienTaiService.laTruongPhongCuaPhongBanMinh(nguoiDung);
        response.setTruongPhong(laTruongPhong);
        if (laTruongPhong || "LANH_DAO".equals(response.getMaVaiTro())) {
            phanCongThuKyRepository.findById(nguoiDung.getNguoiDungId())
                    .map(phanCong -> phanCong.getThuKy().getHoTen())
                    .ifPresent(response::setTenThuKyMacDinh);
        }
    }
}