
package com.example.demo.service;

import java.util.List;

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

@Service
public class PhongBanService {

    private final PhongBanRepository phongBanRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;

    public PhongBanService(
            PhongBanRepository phongBanRepository,
            NguoiDungRepository nguoiDungRepository,
            NguoiDungVaiTroRepository nguoiDungVaiTroRepository) {

        this.phongBanRepository = phongBanRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.nguoiDungVaiTroRepository = nguoiDungVaiTroRepository;
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
                || request.tenPhongBan().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ten phong ban khong duoc de trong"
            );
        }

        String tenPhongBan = request.tenPhongBan().trim();

        if (phongBanRepository.existsByTenPhongBanIgnoreCase(tenPhongBan)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ten phong ban da ton tai"
            );
        }

        PhongBan phongBan = new PhongBan();
        phongBan.setTenPhongBan(tenPhongBan);
        phongBan.setMoTa(request.moTa());
        phongBan.setTrangThai("HOAT_DONG");

        // Nếu có chọn trưởng phòng thì kiểm tra và phân công
        if (request.truongPhongId() != null) {
            NguoiDung truongPhong = kiemTraTruongPhong(
                    request.truongPhongId()
            );

            phongBan.setTruongPhongId(truongPhong.getNguoiDungId());
        }

        phongBan = phongBanRepository.save(phongBan);

        return chuyenDoiResponse(phongBan);
    }

    // Cập nhật phòng ban
    @Transactional
    public PhongBanResponse capNhatPhongBan(
            Integer id,
            CapNhatPhongBanRequest request) {
    	System.out.println("===== UPDATE PHONG BAN =====");
    	System.out.println("Request: " + request);
        PhongBan phongBan = timPhongBan(id);

        if (request.tenPhongBan() == null
                || request.tenPhongBan().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ten phong ban khong duoc de trong"
            );
        }

        String tenPhongBan = request.tenPhongBan().trim();

        if (phongBanRepository.existsByTenPhongBanIgnoreCaseAndPhongBanIdNot(
                tenPhongBan, id)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ten phong ban da ton tai"
            );
        }

        phongBan.setTenPhongBan(tenPhongBan);
        phongBan.setMoTa(request.moTa());

        // Chỉ cập nhật trưởng phòng nếu ID thay đổi
        if (request.truongPhongId() != null
                && !request.truongPhongId().equals(
                        phongBan.getTruongPhongId())) {

            NguoiDung truongPhong = kiemTraTruongPhong(
                    request.truongPhongId()
            );

            boPhanCongTruongPhongCu(truongPhong.getNguoiDungId());

            phongBan.setTruongPhongId(truongPhong.getNguoiDungId());
        }

        return chuyenDoiResponse(phongBanRepository.save(phongBan));
    }

    // Phân công trưởng phòng
    @Transactional
    public PhongBanResponse phanCongTruongPhong(
            Integer phongBanId,
            Integer nguoiDungId) {

        PhongBan phongBan = timPhongBan(phongBanId);

        NguoiDung truongPhong = kiemTraTruongPhong(nguoiDungId);

        // Bỏ phân công tại phòng ban cũ
        boPhanCongTruongPhongCu(nguoiDungId);

        // Gán trưởng phòng mới
        phongBan.setTruongPhongId(truongPhong.getNguoiDungId());

        phongBanRepository.save(phongBan);

        return chuyenDoiResponse(phongBan);
    }

    // Kiểm tra người dùng đủ điều kiện làm trưởng phòng
    private NguoiDung kiemTraTruongPhong(Integer nguoiDungId) {

        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Khong tim thay nguoi dung"
                ));

        if (!"HOAT_DONG".equals(nguoiDung.getTrangThai())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tai khoan nguoi dung khong hoat dong"
            );
        }

        boolean laCanBo = nguoiDungVaiTroRepository
                .findByNguoiDungIdAndVaiTro_MaVaiTro(
                        nguoiDungId,
                        "CAN_BO"
                )
                .isPresent();

        if (!laCanBo) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Truong phong phai co vai tro CAN_BO"
            );
        }

        return nguoiDung;
    }

    // Bỏ phân công trưởng phòng tại phòng ban cũ
    private void boPhanCongTruongPhongCu(Integer nguoiDungId) {

        List<PhongBan> phongBanCu =
                phongBanRepository.findByTruongPhongId(nguoiDungId);

        for (PhongBan phongBan : phongBanCu) {
            phongBan.setTruongPhongId(null);
        }

        phongBanRepository.saveAll(phongBanCu);
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
