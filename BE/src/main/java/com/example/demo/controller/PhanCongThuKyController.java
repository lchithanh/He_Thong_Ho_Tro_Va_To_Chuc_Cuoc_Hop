package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.PhanCongThuKyRequest;
import com.example.demo.dto.response.PhanCongThuKyResponse;
import com.example.demo.service.PhanCongThuKyService;

import com.example.demo.service.NguoiDungHienTaiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/phan-cong-thu-ky")
public class PhanCongThuKyController {

    private final PhanCongThuKyService phanCongThuKyService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    // Xem toàn bộ phân công.
    @GetMapping
    public List<PhanCongThuKyResponse> layDanhSach(
            Authentication authentication) {

        return phanCongThuKyService.layDanhSach(
                layNguoiDungId(authentication));
    }

    // Xem thư ký mặc định của một lãnh đạo/trưởng phòng.
    @GetMapping("/{nguoiDuocHoTroId}")
    public PhanCongThuKyResponse layChiTiet(
            @PathVariable("nguoiDuocHoTroId") Integer nguoiDuocHoTroId,
            Authentication authentication) {

        return phanCongThuKyService.layChiTiet(
                nguoiDuocHoTroId,
                layNguoiDungId(authentication));
    }

    // Xem một thư ký đang hỗ trợ những ai.
    @GetMapping("/thu-ky/{thuKyId}")
    public List<PhanCongThuKyResponse> layDanhSachTheoThuKy(
            @PathVariable("thuKyId") Integer thuKyId,
            Authentication authentication) {

        return phanCongThuKyService.layDanhSachTheoThuKy(
                thuKyId,
                layNguoiDungId(authentication));
    }

    // Tạo mới hoặc thay đổi thư ký mặc định.
    @PutMapping
    public PhanCongThuKyResponse phanCongThuKy(
            @RequestBody PhanCongThuKyRequest request,
            Authentication authentication) {

        return phanCongThuKyService.phanCongThuKy(
                request,
                layNguoiDungId(authentication));
    }

    // Hủy quan hệ thư ký mặc định.
    @DeleteMapping("/{nguoiDuocHoTroId}")
    public Map<String, String> huyPhanCong(
            @PathVariable("nguoiDuocHoTroId") Integer nguoiDuocHoTroId,
            Authentication authentication) {

        phanCongThuKyService.huyPhanCong(
                nguoiDuocHoTroId,
                layNguoiDungId(authentication));

        return Map.of(
                "message",
                "Hủy phân công thư ký mặc định thành công");
    }

    private Integer layNguoiDungId(Authentication authentication) {
        return nguoiDungHienTaiService.layNguoiDungId(authentication);
    }
}