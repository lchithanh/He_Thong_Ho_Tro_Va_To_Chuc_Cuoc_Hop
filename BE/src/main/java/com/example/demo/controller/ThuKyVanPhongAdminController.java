package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.NguoiDungHienTaiService;
import com.example.demo.service.QuyenVanPhongService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/thu-ky-van-phong")
@RequiredArgsConstructor
public class ThuKyVanPhongAdminController {
    private final QuyenVanPhongService quyen;
    private final NguoiDungHienTaiService hienTai;

    public record CapQuyen(boolean vanPhong) {}

    @GetMapping
    public List<QuyenVanPhongService.TaiKhoan> danhSach(Authentication auth) {
        hienTai.layTaiKhoanHoatDong(hienTai.layNguoiDungId(auth));
        return quyen.danhSach();
    }

    @PutMapping("/{id}")
    public void capNhat(@PathVariable Integer id, @RequestBody CapQuyen request, Authentication auth) {
        quyen.capNhat(hienTai.layNguoiDungId(auth), id, request.vanPhong());
    }
}
