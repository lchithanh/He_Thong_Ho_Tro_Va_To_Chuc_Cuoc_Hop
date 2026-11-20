
package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.entity.PhongHop;
import com.example.demo.entity.ThietBi;
import com.example.demo.repository.PhongHopRepository;
import com.example.demo.repository.ThietBiRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThietBiService {

    private final ThietBiRepository thietBiRepository;
    private final PhongHopRepository phongHopRepository;
    private final CapNhatHoSoRealtime capNhatRealtime;

    private static final List<String> TINH_TRANG_HOP_LE = List.of(
            "TOT",
            "HU_HONG",
            "BAO_TRI",
            "NGUNG_SU_DUNG"
    );

    // Lấy danh sách thiết bị
    @Transactional(readOnly = true)
    public List<ThietBiResponse> layDanhSachThietBi() {

        return thietBiRepository.findAll()
                .stream()
                .map(this::chuyenDoiResponse)
                .toList();
    }

    // Lấy chi tiết thiết bị
    @Transactional(readOnly = true)
    public ThietBiResponse layChiTietThietBi(Integer id) {

        ThietBi thietBi = timThietBi(id);

        return chuyenDoiResponse(thietBi);
    }

    // Thêm thiết bị
    @Transactional
    public ThietBiResponse themThietBi(TaoThietBiRequest request) {

        // Kiểm tra mã thiết bị trùng
        kiemTraMaThietBi(request.maThietBi(), null);

        // Kiểm tra tình trạng
        String tinhTrang = request.tinhTrang() == null
                || request.tinhTrang().isBlank()
                ? "TOT"
                : request.tinhTrang();

        kiemTraTinhTrang(tinhTrang);

        ThietBi thietBi = new ThietBi();

        thietBi.setPhongHop(
                timPhongHop(request.phongHopId())
        );

        thietBi.setTenThietBi(request.tenThietBi());
        thietBi.setMaThietBi(chuanHoaMa(request.maThietBi()));
        thietBi.setSoLuong(request.soLuong());
        thietBi.setTinhTrang(tinhTrang);
        thietBi.setGhiChu(request.ghiChu());

        ThietBi daLuu = thietBiRepository.save(thietBi);
        
        capNhatRealtime.baoDanhMucPhongVaThietBiThayDoi();

        return chuyenDoiResponse(daLuu);
    }

    // Cập nhật thiết bị
    @Transactional
    public ThietBiResponse capNhatThietBi(
            Integer id,
            CapNhatThietBiRequest request) {

        ThietBi thietBi = timThietBi(id);

        kiemTraMaThietBi(request.maThietBi(), id);

        String tinhTrang = request.tinhTrang() == null
                || request.tinhTrang().isBlank()
                ? thietBi.getTinhTrang()
                : request.tinhTrang();

        kiemTraTinhTrang(tinhTrang);

        thietBi.setPhongHop(
                timPhongHop(request.phongHopId())
        );

        thietBi.setTenThietBi(request.tenThietBi());
        thietBi.setMaThietBi(chuanHoaMa(request.maThietBi()));
        thietBi.setSoLuong(request.soLuong());
        thietBi.setTinhTrang(tinhTrang);
        thietBi.setGhiChu(request.ghiChu());

        ThietBi daCapNhat = thietBiRepository.save(thietBi);
        
        capNhatRealtime.baoDanhMucPhongVaThietBiThayDoi();

        return chuyenDoiResponse(daCapNhat);
    }

    // Cập nhật riêng tình trạng thiết bị
    @Transactional
    public ThietBiResponse capNhatTinhTrang(
            Integer id,
            String tinhTrangMoi) {

        ThietBi thietBi = timThietBi(id);

        kiemTraTinhTrang(tinhTrangMoi);

        thietBi.setTinhTrang(tinhTrangMoi);

        ThietBi daCapNhat = thietBiRepository.save(thietBi);
        
        capNhatRealtime.baoDanhMucPhongVaThietBiThayDoi();

        return chuyenDoiResponse(daCapNhat);
    }

    // Tìm thiết bị theo ID
    private ThietBi timThietBi(Integer id) {

        return thietBiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy thiết bị có ID: " + id
                ));
    }

    // Tìm phòng họp theo ID
    private PhongHop timPhongHop(Integer phongHopId) {

        if (phongHopId == null) {
            return null;
        }

        return phongHopRepository.findById(phongHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy phòng họp có ID: " + phongHopId
                ));
    }

    // Kiểm tra mã thiết bị trùng
    private void kiemTraMaThietBi(
            String maThietBi,
            Integer idHienTai) {

        String ma = chuanHoaMa(maThietBi);

        if (ma == null) {
            return;
        }

        boolean biTrung = idHienTai == null
                ? thietBiRepository.existsByMaThietBi(ma)
                : thietBiRepository.existsByMaThietBiAndThietBiIdNot(
                        ma, idHienTai
                );

        if (biTrung) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Mã thiết bị đã tồn tại"
            );
        }
    }

    // Kiểm tra tình trạng hợp lệ
    private void kiemTraTinhTrang(String tinhTrang) {

        if (tinhTrang == null
                || !TINH_TRANG_HOP_LE.contains(tinhTrang)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tình trạng thiết bị không hợp lệ. "
                    + "Các giá trị: TOT, HU_HONG, BAO_TRI, NGUNG_SU_DUNG"
            );
        }
    }

    // Chuẩn hóa mã thiết bị
    private String chuanHoaMa(String ma) {

        if (ma == null || ma.isBlank()) {
            return null;
        }

        return ma.trim();
    }

    // Chuyển Entity sang Response DTO
    private ThietBiResponse chuyenDoiResponse(ThietBi thietBi) {

        PhongHop phongHop = thietBi.getPhongHop();

        return new ThietBiResponse(
                thietBi.getThietBiId(),
                phongHop != null ? phongHop.getPhongHopId() : null,
                phongHop != null ? phongHop.getTenPhongHop() : null,
                thietBi.getTenThietBi(),
                thietBi.getMaThietBi(),
                thietBi.getSoLuong(),
                thietBi.getTinhTrang(),
                thietBi.getGhiChu()
        );
    }
}
