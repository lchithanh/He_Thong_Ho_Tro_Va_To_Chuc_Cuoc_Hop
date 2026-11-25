package com.example.demo.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.GhiChuCuocHopService;
import com.example.demo.service.GhiChuCuocHopService.DuLieuBanNhap;
import com.example.demo.service.GhiChuCuocHopService.GhiChu;
import com.example.demo.service.GhiChuCuocHopService.LuuGhiChu;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuoc-hop/{cuocHopId}/ghi-chu")
@RequiredArgsConstructor
public class GhiChuCuocHopController {
    private final GhiChuCuocHopService ghiChuService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    @GetMapping
    public List<GhiChu> xem(@PathVariable Integer cuocHopId, Authentication auth) {
        return ghiChuService.xem(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth));
    }

    @PostMapping
    public List<GhiChu> tao(@PathVariable Integer cuocHopId, @RequestBody LuuGhiChu request, Authentication auth) {
        return ghiChuService.luu(cuocHopId, null, nguoiDungHienTaiService.layNguoiDungId(auth), request);
    }

    @PutMapping("/{ghiChuId}")
    public List<GhiChu> sua(@PathVariable Integer cuocHopId, @PathVariable Integer ghiChuId,
            @RequestBody LuuGhiChu request, Authentication auth) {
        return ghiChuService.luu(cuocHopId, ghiChuId, nguoiDungHienTaiService.layNguoiDungId(auth), request);
    }

    @DeleteMapping("/{ghiChuId}")
    public List<GhiChu> xoa(@PathVariable Integer cuocHopId, @PathVariable Integer ghiChuId,
            @RequestParam int phienBan, Authentication auth) {
        return ghiChuService.xoa(cuocHopId, ghiChuId, phienBan, nguoiDungHienTaiService.layNguoiDungId(auth));
    }

    @GetMapping("/ban-nhap-bien-ban")
    public DuLieuBanNhap layDuLieuBanNhap(@PathVariable Integer cuocHopId, Authentication auth) {
        return ghiChuService.taoDuLieuBanNhap(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth));
    }
}
