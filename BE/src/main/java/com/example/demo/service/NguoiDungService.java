package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.TaoNguoiDungRequest;
import com.example.demo.dto.response.NguoiDungResponse;
import com.example.demo.dto.response.VaiTroLuaChonResponse;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.NguoiDungVaiTro;
import com.example.demo.entity.PhongBan;
import com.example.demo.entity.VaiTro;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.NguoiDungVaiTroRepository;
import com.example.demo.repository.PhongBanRepository;
import com.example.demo.repository.PhanCongThuKyRepository;
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
    private final PhanCongThuKyRepository phanCongThuKyRepository;

    public NguoiDungService(
            NguoiDungRepository nguoiDungRepository,
            NguoiDungVaiTroRepository nguoiDungVaiTroRepository,
            PhongBanRepository phongBanRepository,
            VaiTroRepository vaiTroRepository,
            PasswordEncoder passwordEncoder,
            PhanCongThuKyRepository phanCongThuKyRepository) {

        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungVaiTroRepository = nguoiDungVaiTroRepository;
        this.phongBanRepository = phongBanRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
        this.phanCongThuKyRepository = phanCongThuKyRepository;
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
        nguoiDung.setMaNhanVien(request.maNhanVien() == null || request.maNhanVien().isBlank()
            ? null : request.maNhanVien().trim());
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

    NguoiDung nguoiDung = khoaNguoiDung(id);
    Integer phongBanCuId = nguoiDung.getPhongBan() == null ? null : nguoiDung.getPhongBan().getPhongBanId();
    if (!Objects.equals(phongBanCuId, request.phongBanId()) && laTruongPhong(id)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Hãy bỏ phân công trưởng phòng trước khi chuyển phòng ban.");
    }

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
    nguoiDung.setMaNhanVien(request.maNhanVien() == null || request.maNhanVien().isBlank()
            ? null : request.maNhanVien().trim());
    nguoiDung.setHoTen(request.hoTen().trim());
    nguoiDung.setEmail(email);
    nguoiDung.setSoDienThoai(request.soDienThoai());
    nguoiDung.setChucVu(request.chucVu());
    nguoiDung.setPhongBan(phongBan);

    nguoiDungRepository.save(nguoiDung);

    return chuyenDoiResponse(nguoiDung);
}
    @Transactional
    public NguoiDungResponse capNhatVaiTro(Integer id, CapNhatVaiTroRequest request, String tenDangNhapAdmin) {
        NguoiDung nguoiDung = khoaNguoiDung(id);
        if (request.vaiTroId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng chọn vai trò.");
        }
        VaiTro vaiTro = vaiTroRepository.findById(request.vaiTroId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy vai trò."));
        if (!"HOAT_DONG".equals(vaiTro.getTrangThai())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vai trò đang ngừng hoạt động.");
        }

        NguoiDungVaiTro phanQuyen = nguoiDungVaiTroRepository.findByNguoiDungId(id).orElse(null);
        if (phanQuyen != null && vaiTro.getVaiTroId().equals(phanQuyen.getVaiTro().getVaiTroId())) {
            return chuyenDoiResponse(nguoiDung);
        }
        if (nguoiDung.getTenDangNhap().equals(tenDangNhapAdmin)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn không thể đổi vai trò của tài khoản đang đăng nhập.");
        }
        kiemTraPhanCong(id);
        if (phanQuyen == null) {
            phanQuyen = new NguoiDungVaiTro();
            phanQuyen.setNguoiDung(nguoiDung);
        }
        phanQuyen.setVaiTro(vaiTro);
        nguoiDungVaiTroRepository.save(phanQuyen);
        return chuyenDoiResponse(nguoiDung);
    }

    @Transactional
    public NguoiDungResponse capNhatTrangThai(Integer id, String trangThai, String tenDangNhapAdmin) {
        if (!"HOAT_DONG".equals(trangThai) && !"BI_KHOA".equals(trangThai)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái tài khoản không hợp lệ.");
        }
        NguoiDung nguoiDung = khoaNguoiDung(id);
        if ("BI_KHOA".equals(trangThai)) {
            if (nguoiDung.getTenDangNhap().equals(tenDangNhapAdmin)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bạn không thể khóa tài khoản đang đăng nhập.");
            }
            kiemTraPhanCong(id);
        }
        nguoiDung.setTrangThai(trangThai);
        return chuyenDoiResponse(nguoiDung);
    }

    private NguoiDung khoaNguoiDung(Integer id) {
        return nguoiDungRepository.khoaTheoId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng."));
    }

    private boolean laTruongPhong(Integer id) {
        return !phongBanRepository.findByTruongPhongId(id).isEmpty();
    }

    private void kiemTraPhanCong(Integer id) {
        if (laTruongPhong(id) || phanCongThuKyRepository.existsById(id)
                || phanCongThuKyRepository.existsByThuKy_NguoiDungId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Hãy bỏ phân công trưởng phòng hoặc thư ký mặc định liên quan trước khi thay đổi tài khoản.");
        }
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
    
   

    @Transactional(readOnly = true)
    public List<VaiTroLuaChonResponse> layVaiTroHoatDong() {
        return vaiTroRepository.findAll().stream()
                .filter(vaiTro -> "HOAT_DONG".equals(vaiTro.getTrangThai()))
                .map(vaiTro -> new VaiTroLuaChonResponse(
                        vaiTro.getVaiTroId(),
                        vaiTro.getMaVaiTro(),
                        vaiTro.getTenVaiTro()))
                .toList();
    }
    }