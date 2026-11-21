
package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.TaoPhongBanRequest;
import com.example.demo.dto.request.CapNhatPhongBanRequest;
import com.example.demo.dto.response.PhongBanResponse;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongBan;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.NguoiDungVaiTroRepository;
import com.example.demo.repository.PhongBanRepository;
import com.example.demo.repository.PhanCongThuKyRepository;

@Service
public class PhongBanService {

    private final PhongBanRepository phongBanRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;
    private final PhanCongThuKyRepository phanCongThuKyRepository;

    public PhongBanService(
            PhongBanRepository phongBanRepository,
            NguoiDungRepository nguoiDungRepository,
            NguoiDungVaiTroRepository nguoiDungVaiTroRepository,
            PhanCongThuKyRepository phanCongThuKyRepository) {

        this.phongBanRepository = phongBanRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungVaiTroRepository = nguoiDungVaiTroRepository;
        this.phanCongThuKyRepository = phanCongThuKyRepository;
    }

    // Lấy danh sách phòng ban
    @Transactional(readOnly = true)
    public List<PhongBanResponse> layDanhSachPhongBan() {
        return phongBanRepository.findAll()
                .stream()
                .map(this::chuyenDoiResponse)
                .toList();
    }

    // Lấy chi tiết phòng ban
    @Transactional(readOnly = true)
    public PhongBanResponse layChiTietPhongBan(Integer id) {

        PhongBan phongBan = timPhongBan(id);

        return chuyenDoiResponse(phongBan);
    }

    // Thêm phòng ban
    @Transactional
    public PhongBanResponse themPhongBan(TaoPhongBanRequest request) {

        if (request.tenPhongBan() == null
                || request.tenPhongBan().isBlank() || request.tenPhongBan().trim().length() > 150) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên phòng ban phải có từ 1 đến 150 ký tự."
            );
        }

        String tenPhongBan = request.tenPhongBan().trim();

        if (phongBanRepository.existsByTenPhongBanIgnoreCase(tenPhongBan)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tên phòng ban đã tồn tại."
            );
        }

        PhongBan phongBan = new PhongBan();
        phongBan.setTenPhongBan(tenPhongBan);
        phongBan.setMoTa(request.moTa());
        phongBan.setTrangThai("HOAT_DONG");

        if (request.truongPhongId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Hãy tạo phòng ban và thêm cán bộ vào phòng trước khi phân công trưởng phòng.");
        }

        phongBan = phongBanRepository.save(phongBan);

        return chuyenDoiResponse(phongBan);
    }

    // Cập nhật phòng ban
    @Transactional
    public PhongBanResponse capNhatPhongBan(
            Integer id,
            CapNhatPhongBanRequest request) {
        if (request.truongPhongId() != null) {
            kiemTraTruongPhong(id, request.truongPhongId());
        }
        PhongBan phongBan = khoaPhongBan(id);

        if (request.tenPhongBan() == null
                || request.tenPhongBan().isBlank() || request.tenPhongBan().trim().length() > 150) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên phòng ban phải có từ 1 đến 150 ký tự."
            );
        }

        String tenPhongBan = request.tenPhongBan().trim();

        if (phongBanRepository.existsByTenPhongBanIgnoreCaseAndPhongBanIdNot(
                tenPhongBan, id)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Tên phòng ban đã tồn tại."
            );
        }

        phongBan.setTenPhongBan(tenPhongBan);
        phongBan.setMoTa(request.moTa());

        if (request.truongPhongId() != null) {
            ganTruongPhong(phongBan, request.truongPhongId());
        }

        return chuyenDoiResponse(phongBanRepository.save(phongBan));
    }

    // Phân công trưởng phòng
    @Transactional
    public PhongBanResponse phanCongTruongPhong(
            Integer phongBanId,
            Integer nguoiDungId) {

        if (nguoiDungId != null) {
            kiemTraTruongPhong(phongBanId, nguoiDungId);
        }
        PhongBan phongBan = khoaPhongBan(phongBanId);
        ganTruongPhong(phongBan, nguoiDungId);
        return chuyenDoiResponse(phongBan);
    }

    private void kiemTraTruongPhong(Integer phongBanId, Integer nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.khoaTheoId(nguoiDungId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng."));
        boolean laCanBo = nguoiDungVaiTroRepository
                .findByNguoiDungIdAndVaiTro_MaVaiTro(nguoiDungId, "CAN_BO").isPresent();
        if (!"HOAT_DONG".equals(nguoiDung.getTrangThai()) || !laCanBo || nguoiDung.getPhongBan() == null
                || !phongBanId.equals(nguoiDung.getPhongBan().getPhongBanId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Trưởng phòng phải là cán bộ đang hoạt động trong phòng ban này.");
        }
        if (phongBanRepository.findByTruongPhongId(nguoiDungId).stream()
                .anyMatch(phong -> !phongBanId.equals(phong.getPhongBanId()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cán bộ vẫn đang được phân công làm trưởng phòng ở phòng ban khác.");
        }
    }

    private void ganTruongPhong(PhongBan phongBan, Integer nguoiDungId) {
        Integer nguoiCu = phongBan.getTruongPhongId();
        if (Objects.equals(nguoiCu, nguoiDungId)) return;
        if (nguoiCu != null && phanCongThuKyRepository.existsById(nguoiCu)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Hãy bỏ phân công thư ký mặc định của trưởng phòng hiện tại trước.");
        }
        phongBan.setTruongPhongId(nguoiDungId);
    }

    private PhongBan khoaPhongBan(Integer id) {
        return phongBanRepository.khoaTheoId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phòng ban."));
    }

    // Tìm phòng ban
    private PhongBan timPhongBan(Integer id) {
        return phongBanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Khong tim thay phong ban"
                ));
    }

    // Chuyển Entity thành Response DTO
    private PhongBanResponse chuyenDoiResponse(PhongBan phongBan) {

        String tenTruongPhong = null;

        if (phongBan.getTruongPhongId() != null) {
            tenTruongPhong = nguoiDungRepository
                    .findById(phongBan.getTruongPhongId())
                    .map(NguoiDung::getHoTen)
                    .orElse(null);
        }

        return new PhongBanResponse(
                phongBan.getPhongBanId(),
                phongBan.getTenPhongBan(),
                phongBan.getTruongPhongId(),
                tenTruongPhong,
                phongBan.getMoTa(),
                phongBan.getTrangThai(),
                phongBan.getNgayTao()
        );
    }
}
