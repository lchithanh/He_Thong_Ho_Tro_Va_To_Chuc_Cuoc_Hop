package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.TaoNguoiDungRequest;
import com.example.demo.dto.response.NguoiDungResponse;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.NguoiDungVaiTro;
import com.example.demo.entity.PhongBan;
import com.example.demo.entity.VaiTro;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.NguoiDungVaiTroRepository;
import com.example.demo.repository.PhongBanRepository;
import com.example.demo.repository.VaiTroRepository;
import com.example.demo.dto.request.CapNhatNguoiDungRequest;
import com.example.demo.dto.request.CapNhatVaiTroRequest;
@Service
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;
    private final PhongBanRepository phongBanRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    public NguoiDungService(
            NguoiDungRepository nguoiDungRepository,
            NguoiDungVaiTroRepository nguoiDungVaiTroRepository,
            PhongBanRepository phongBanRepository,
            VaiTroRepository vaiTroRepository,
            PasswordEncoder passwordEncoder) {

        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungVaiTroRepository = nguoiDungVaiTroRepository;
        this.phongBanRepository = phongBanRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lấy danh sách người dùng
    @Transactional(readOnly = true)
    public List<NguoiDungResponse> layDanhSachNguoiDung() {
        return nguoiDungRepository.findAll()
                .stream()
                .map(this::chuyenDoiResponse)
                .toList();
    }

    // Thêm người dùng
    @Transactional
    public NguoiDungResponse themNguoiDung(TaoNguoiDungRequest request) {

        // Kiểm tra dữ liệu bắt buộc
        if (request.hoTen() == null || request.hoTen().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.tenDangNhap() == null || request.tenDangNhap().isBlank()
                || request.matKhau() == null || request.matKhau().isBlank()
                || request.vaiTroId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui long nhap day du thong tin bat buoc");
        }

        String email = request.email().trim();
        String tenDangNhap = request.tenDangNhap().trim();

        // Kiểm tra email trùng
        if (nguoiDungRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email da ton tai");
        }

        // Kiểm tra tên đăng nhập trùng
        if (nguoiDungRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ten dang nhap da ton tai");
        }

        // Kiểm tra mã nhân viên trùng nếu có nhập
        if (request.maNhanVien() != null
                && !request.maNhanVien().isBlank()
                && nguoiDungRepository.existsByMaNhanVien(request.maNhanVien().trim())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ma nhan vien da ton tai");
        }

        // Tìm vai trò
        VaiTro vaiTro = vaiTroRepository.findById(request.vaiTroId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Khong tim thay vai tro"));

        if (!"HOAT_DONG".equals(vaiTro.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vai tro dang khong hoat dong");
        }

        // Tìm phòng ban nếu người dùng có chọn
        PhongBan phongBan = null;

        if (request.phongBanId() != null) {
            phongBan = phongBanRepository.findById(request.phongBanId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Khong tim thay phong ban"));

            if (!"HOAT_DONG".equals(phongBan.getTrangThai())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Phong ban dang khong hoat dong");
            }
        }

        // Tạo người dùng
        NguoiDung nguoiDung = new NguoiDung();

        nguoiDung.setPhongBan(phongBan);
        nguoiDung.setMaNhanVien(request.maNhanVien());
        nguoiDung.setHoTen(request.hoTen().trim());
        nguoiDung.setEmail(email);
        nguoiDung.setSoDienThoai(request.soDienThoai());
        nguoiDung.setTenDangNhap(tenDangNhap);

        // Mã hóa mật khẩu bằng BCrypt
        nguoiDung.setMatKhauMaHoa(
                passwordEncoder.encode(request.matKhau()));

        nguoiDung.setChucVu(request.chucVu());
        nguoiDung.setTrangThai("HOAT_DONG");

        nguoiDung = nguoiDungRepository.save(nguoiDung);

     // Gán vai trò cho người dùng
        NguoiDungVaiTro nguoiDungVaiTro = new NguoiDungVaiTro();

        nguoiDungVaiTro.setNguoiDung(nguoiDung);
        nguoiDungVaiTro.setVaiTro(vaiTro);

        // @MapsId tự lấy nguoiDungId từ đối tượng NguoiDung
        nguoiDungVaiTroRepository.save(nguoiDungVaiTro);

        return chuyenDoiResponse(nguoiDung);
    }

 // Lấy chi tiết người dùng
    @Transactional(readOnly = true)
    public NguoiDungResponse layChiTietNguoiDung(Integer id) {

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
        		.orElseThrow(() -> new ResponseStatusException(
        		        HttpStatus.NOT_FOUND,
        		        "Khong tim thay nguoi dung co ID: " + id
        		));

        return chuyenDoiResponse(nguoiDung);
    }

    
//cập nhật người dùng
@Transactional
public NguoiDungResponse capNhatNguoiDung(
        Integer id,
        CapNhatNguoiDungRequest request) {

    NguoiDung nguoiDung = nguoiDungRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Khong tim thay nguoi dung"
            ));

    // Kiểm tra thông tin bắt buộc
    if (request.hoTen() == null || request.hoTen().isBlank()
            || request.email() == null || request.email().isBlank()) {

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Ho ten va email khong duoc de trong"
        );
    }

    String email = request.email().trim();

    // Kiểm tra email đã được tài khoản khác sử dụng chưa
    nguoiDungRepository.findByEmail(email)
            .filter(user -> !user.getNguoiDungId().equals(id))
            .ifPresent(user -> {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email da ton tai"
                );
            });

    // Kiểm tra mã nhân viên trùng
    if (request.maNhanVien() != null
            && !request.maNhanVien().isBlank()) {

        nguoiDungRepository.findByMaNhanVien(
                request.maNhanVien().trim()
        ).filter(user -> !user.getNguoiDungId().equals(id))
         .ifPresent(user -> {
             throw new ResponseStatusException(
                     HttpStatus.CONFLICT,
                     "Ma nhan vien da ton tai"
             );
         });
    }

    // Cập nhật phòng ban
    PhongBan phongBan = null;

    if (request.phongBanId() != null) {
        phongBan = phongBanRepository.findById(request.phongBanId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Khong tim thay phong ban"
                ));

        if (!"HOAT_DONG".equals(phongBan.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Phong ban dang khong hoat dong"
            );
        }
    }

    // Cập nhật thông tin
    nguoiDung.setMaNhanVien(request.maNhanVien());
    nguoiDung.setHoTen(request.hoTen().trim());
    nguoiDung.setEmail(email);
    nguoiDung.setSoDienThoai(request.soDienThoai());
    nguoiDung.setChucVu(request.chucVu());
    nguoiDung.setPhongBan(phongBan);

    nguoiDungRepository.save(nguoiDung);

    return chuyenDoiResponse(nguoiDung);
}
//Thay đổi vai trò người dùng
@Transactional
public NguoiDungResponse capNhatVaiTro(
     Integer id,
     CapNhatVaiTroRequest request) {

 NguoiDung nguoiDung = nguoiDungRepository.findById(id)
         .orElseThrow(() -> new ResponseStatusException(
                 HttpStatus.NOT_FOUND,
                 "Khong tim thay nguoi dung"
         ));

 if (request.vaiTroId() == null) {
     throw new ResponseStatusException(
             HttpStatus.BAD_REQUEST,
             "Vui long chon vai tro"
     );
 }

 VaiTro vaiTro = vaiTroRepository.findById(request.vaiTroId())
         .orElseThrow(() -> new ResponseStatusException(
                 HttpStatus.NOT_FOUND,
                 "Khong tim thay vai tro"
         ));

 if (!"HOAT_DONG".equals(vaiTro.getTrangThai())) {
     throw new ResponseStatusException(
             HttpStatus.BAD_REQUEST,
             "Vai tro dang khong hoat dong"
     );
 }

 NguoiDungVaiTro nguoiDungVaiTro =
         nguoiDungVaiTroRepository
                 .findByNguoiDungId(id)
                 .orElseGet(() -> {
                     NguoiDungVaiTro moi = new NguoiDungVaiTro();
                     moi.setNguoiDung(nguoiDung);
                     return moi;
                 });

 nguoiDungVaiTro.setVaiTro(vaiTro);
 nguoiDungVaiTroRepository.save(nguoiDungVaiTro);

 return chuyenDoiResponse(nguoiDung);
}

    
    // Chuyển Entity thành Response DTO
    private NguoiDungResponse chuyenDoiResponse(NguoiDung nguoiDung) {

        Integer phongBanId = null;
        String tenPhongBan = null;

        if (nguoiDung.getPhongBan() != null) {
            phongBanId = nguoiDung.getPhongBan().getPhongBanId();
            tenPhongBan = nguoiDung.getPhongBan().getTenPhongBan();
        }

        Integer vaiTroId = null;
        String maVaiTro = null;
        String tenVaiTro = null;

        NguoiDungVaiTro nguoiDungVaiTro = nguoiDungVaiTroRepository
                .findByNguoiDungId(nguoiDung.getNguoiDungId())
                .orElse(null);

        if (nguoiDungVaiTro != null && nguoiDungVaiTro.getVaiTro() != null) {
            vaiTroId = nguoiDungVaiTro.getVaiTro().getVaiTroId();
            maVaiTro = nguoiDungVaiTro.getVaiTro().getMaVaiTro();
            tenVaiTro = nguoiDungVaiTro.getVaiTro().getTenVaiTro();
        }

        return new NguoiDungResponse(
                nguoiDung.getNguoiDungId(),
                nguoiDung.getMaNhanVien(),
                nguoiDung.getHoTen(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getChucVu(),
                nguoiDung.getTrangThai(),
                nguoiDung.getNgayTao(),
                phongBanId,
                tenPhongBan,
                vaiTroId,
                maVaiTro,
                tenVaiTro
        );
    }
    
    }