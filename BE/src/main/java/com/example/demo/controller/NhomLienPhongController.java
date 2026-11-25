package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.NguoiDungHienTaiService;
import com.example.demo.service.NhomLienPhongService;
import com.example.demo.service.NhomLienPhongService.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/nhom-lien-phong")
@RequiredArgsConstructor
public class NhomLienPhongController {
    private final NhomLienPhongService service;
    private final NguoiDungHienTaiService hienTai;

    public record LyDo(String lyDo) {}
    public record NhomMoi(Integer nhomId) {}
    public record DeXuatMoi(Integer cuocHopId) {}

    @GetMapping
    public TrangNhom danhSach(Authentication auth) {
        return service.danhSach(hienTai.layNguoiDungId(auth));
    }

    @PostMapping
    public NhomMoi tao(@RequestBody TaoNhom request, Authentication auth) {
        return new NhomMoi(service.tao(request, hienTai.layNguoiDungId(auth)));
    }

    @GetMapping("/{nhomId}")
    public ChiTiet chiTiet(@PathVariable Integer nhomId, Authentication auth) {
        return service.chiTiet(nhomId, hienTai.layNguoiDungId(auth));
    }

    @PutMapping("/{nhomId}/phong-ban/{phongId}/cu-nguoi")
    public void cuNguoi(@PathVariable Integer nhomId, @PathVariable Integer phongId,
            @RequestBody CuNguoi request, Authentication auth) {
        service.cuNguoi(nhomId, phongId, request, hienTai.layNguoiDungId(auth));
    }

    @PostMapping("/{nhomId}/kich-hoat")
    public void kichHoat(@PathVariable Integer nhomId, @RequestBody GanVaiTro request, Authentication auth) {
        service.kichHoat(nhomId, request, hienTai.layNguoiDungId(auth));
    }

    @PostMapping("/{nhomId}/doi-nguoi")
    public void doiNguoi(@PathVariable Integer nhomId, @RequestBody ThayNguoi request, Authentication auth) {
        service.doiNguoi(nhomId, request, hienTai.layNguoiDungId(auth));
    }

    @PutMapping("/{nhomId}/doi-nguoi/{doiId}")
    public void xuLyDoiNguoi(@PathVariable Integer nhomId, @PathVariable Integer doiId,
            @RequestBody XuLyThayNguoi request, Authentication auth) {
        service.xuLyDoiNguoi(nhomId, doiId, request, hienTai.layNguoiDungId(auth));
    }

    @PostMapping("/{nhomId}/dong")
    public void dong(@PathVariable Integer nhomId, @RequestBody LyDo request, Authentication auth) {
        service.dong(nhomId, request.lyDo(), hienTai.layNguoiDungId(auth));
    }

    @PostMapping("/{nhomId}/de-xuat")
    public DeXuatMoi gui(@PathVariable Integer nhomId, @RequestBody GuiDeXuat request, Authentication auth) {
        return new DeXuatMoi(service.guiDeXuat(nhomId, request, hienTai.layNguoiDungId(auth)));
    }
}
