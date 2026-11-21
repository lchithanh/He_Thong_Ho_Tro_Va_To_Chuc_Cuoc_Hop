package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.PhanCongThuKyRequest;
import com.example.demo.dto.response.PhanCongThuKyResponse;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhanCongThuKy;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.PhanCongThuKyRepository;
import com.example.demo.repository.PhongBanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhanCongThuKyService {

    private final PhanCongThuKyRepository phanCongThuKyRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PhongBanRepository phongBanRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;

    // Tạo phân công mới hoặc đổi thư ký của phân công hiện tại.
    @Transactional
    public PhanCongThuKyResponse phanCongThuKy(
            PhanCongThuKyRequest request,
            Integer nguoiDangNhapId) {

        kiemTraQuyenAdmin(nguoiDangNhapId);

        if (request == null
                || request.getNguoiDuocHoTroId() == null
                || request.getThuKyId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng chọn người được hỗ trợ và thư ký");
        }

        Integer nguoiDuocHoTroId = request.getNguoiDuocHoTroId();
        Integer thuKyId = request.getThuKyId();

        if (nguoiDuocHoTroId.equals(thuKyId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Không thể phân công một người làm thư ký cho chính mình");
        }

        NguoiDung taiKhoanNho = khoaTaiKhoanHoatDong(Math.min(nguoiDuocHoTroId, thuKyId));
        NguoiDung taiKhoanLon = khoaTaiKhoanHoatDong(Math.max(nguoiDuocHoTroId, thuKyId));
        boolean nguoiDuocHoTroKhoaTruoc = nguoiDuocHoTroId.equals(taiKhoanNho.getNguoiDungId());
        NguoiDung nguoiDuocHoTro = nguoiDuocHoTroKhoaTruoc ? taiKhoanNho : taiKhoanLon;
        NguoiDung thuKy = nguoiDuocHoTroKhoaTruoc ? taiKhoanLon : taiKhoanNho;

        boolean laLanhDao = phanQuyenCuocHopService.coVaiTro(nguoiDuocHoTroId, "LANH_DAO");
        boolean laTruongPhong = !laLanhDao
                && phanQuyenCuocHopService.coVaiTro(nguoiDuocHoTroId, "CAN_BO")
                && nguoiDuocHoTro.getPhongBan() != null
                && phongBanRepository.khoaTheoId(nguoiDuocHoTro.getPhongBan().getPhongBanId())
                        .filter(phong -> nguoiDuocHoTroId.equals(phong.getTruongPhongId()))
                        .isPresent();

        if (!laLanhDao && !laTruongPhong) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Người được hỗ trợ phải là lãnh đạo hoặc trưởng phòng");
        }

        if (!phanQuyenCuocHopService.coVaiTro(thuKyId, "THU_KY")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thư ký mặc định phải có vai trò hệ thống THU_KY");
        }

        if (phanCongThuKyRepository
                .findFirstByThuKy_NguoiDungIdAndNguoiDuocHoTroIdNot(thuKyId, nguoiDuocHoTroId)
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Thư ký này đã được phân công cho người khác");
        }

        NguoiDung nguoiPhanCong = nguoiDungRepository
                .findById(nguoiDangNhapId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy người thực hiện phân công"));

        PhanCongThuKy phanCong = phanCongThuKyRepository
                .findById(nguoiDuocHoTroId)
                .orElse(null);

        if (phanCong == null) {
            phanCong = new PhanCongThuKy();
            phanCong.setNguoiDuocHoTro(nguoiDuocHoTro);
        } else if (phanCong.getThuKy()
                .getNguoiDungId()
                .equals(thuKyId)) {

            // Phân công đã đúng: không thay đổi thông tin người phân công.
            return chuyenSangResponse(phanCong);
        }

        phanCong.setThuKy(thuKy);
        phanCong.setNguoiPhanCong(nguoiPhanCong);

        // Flush để callback cập nhật thời gian trước khi tạo response.
        PhanCongThuKy daLuu =
                phanCongThuKyRepository.saveAndFlush(phanCong);

        return chuyenSangResponse(daLuu);
    }

    // Danh sách toàn bộ phân công dành cho ADMIN.
    public List<PhanCongThuKyResponse> layDanhSach(
            Integer nguoiDangNhapId) {

        kiemTraQuyenAdmin(nguoiDangNhapId);

        return phanCongThuKyRepository
                .findAllByOrderByNgayPhanCongDesc()
                .stream()
                .map(this::chuyenSangResponse)
                .toList();
    }

    // ADMIN xem phân công của một lãnh đạo/trưởng phòng.
    public PhanCongThuKyResponse layChiTiet(
            Integer nguoiDuocHoTroId,
            Integer nguoiDangNhapId) {

        kiemTraQuyenAdmin(nguoiDangNhapId);

        PhanCongThuKy phanCong =
                layPhanCongBatBuoc(nguoiDuocHoTroId);

        return chuyenSangResponse(phanCong);
    }

    // ADMIN xem những người mà một thư ký đang hỗ trợ.
    public List<PhanCongThuKyResponse> layDanhSachTheoThuKy(
            Integer thuKyId,
            Integer nguoiDangNhapId) {

        kiemTraQuyenAdmin(nguoiDangNhapId);

        if (thuKyId == null || thuKyId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID thư ký không hợp lệ");
        }

        if (!nguoiDungRepository.existsById(thuKyId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy người dùng");
        }

        return phanCongThuKyRepository
                .findByThuKy_NguoiDungIdOrderByNgayPhanCongDesc(thuKyId)
                .stream()
                .map(this::chuyenSangResponse)
                .toList();
    }

    // Chỉ hủy quan hệ mặc định, không xóa tài khoản hay thành viên cuộc họp.
    @Transactional
    public void huyPhanCong(
            Integer nguoiDuocHoTroId,
            Integer nguoiDangNhapId) {

        kiemTraQuyenAdmin(nguoiDangNhapId);

        PhanCongThuKy phanCong =
                layPhanCongBatBuoc(nguoiDuocHoTroId);

        phanCongThuKyRepository.delete(phanCong);
    }

    private void kiemTraQuyenAdmin(Integer nguoiDangNhapId) {

        if (nguoiDangNhapId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bạn chưa đăng nhập");
        }

        nguoiDungHienTaiService.layTaiKhoanHoatDong(
                nguoiDangNhapId);

        if (!phanQuyenCuocHopService.coVaiTro(
                nguoiDangNhapId,
                "ADMIN")) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Chỉ ADMIN được quản lý phân công thư ký");
        }
    }

    private NguoiDung khoaTaiKhoanHoatDong(Integer nguoiDungId) {

        if (nguoiDungId == null || nguoiDungId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID người dùng không hợp lệ");
        }

        NguoiDung nguoiDung = nguoiDungRepository.khoaTheoId(nguoiDungId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng có ID: " + nguoiDungId));
        if (!NguoiDungHienTaiService.dangHoatDong(nguoiDung)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tài khoản được chọn không hoạt động.");
        }
        return nguoiDung;
    }

    private PhanCongThuKy layPhanCongBatBuoc(
            Integer nguoiDuocHoTroId) {

        if (nguoiDuocHoTroId == null || nguoiDuocHoTroId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID người được hỗ trợ không hợp lệ");
        }

        return phanCongThuKyRepository
                .findById(nguoiDuocHoTroId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Người này chưa được phân công thư ký mặc định"));
    }

    private PhanCongThuKyResponse chuyenSangResponse(
            PhanCongThuKy phanCong) {

        return new PhanCongThuKyResponse(
                phanCong.getNguoiDuocHoTroId(),
                phanCong.getNguoiDuocHoTro().getHoTen(),

                phanCong.getThuKy().getNguoiDungId(),
                phanCong.getThuKy().getHoTen(),

                phanCong.getNguoiPhanCong().getNguoiDungId(),
                phanCong.getNguoiPhanCong().getHoTen(),

                phanCong.getNgayPhanCong(),
                phanCong.getNgayCapNhat()
        );
    }
}