package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ChiTietLoiMoiCuocHopService;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cuoc-hop")
public class ChiTietLoiMoiCuocHopController {
    private final ChiTietLoiMoiCuocHopService loiMoi;
    private final NguoiDungHienTaiService nguoiDungHienTai;

    @GetMapping("/thanh-vien/loi-moi")
    public List<ChiTietLoiMoiCuocHopService.TomTat> danhSach(Authentication authentication) {
        return loiMoi.danhSach(nguoiDungHienTai.layNguoiDungId(authentication));
    }

    @GetMapping("/{cuocHopId}/thanh-vien/loi-moi")
    public ChiTietLoiMoiCuocHopService.ChiTiet xem(@PathVariable Integer cuocHopId, Authentication authentication) {
        return loiMoi.xem(cuocHopId, nguoiDungHienTai.layNguoiDungId(authentication));
    }

    @GetMapping("/{cuocHopId}/thanh-vien/loi-moi/{nhom}/tep/{taiLieuId}")
    public ResponseEntity<byte[]> docTep(@PathVariable Integer cuocHopId, @PathVariable String nhom,
            @PathVariable Integer taiLieuId, Authentication authentication) {
        var tep = loiMoi.docTep(cuocHopId, nguoiDungHienTai.layNguoiDungId(authentication), nhom, taiLieuId);
        boolean pdf = tep.tenFile().toLowerCase(Locale.ROOT).endsWith(".pdf");
        var hienThi = pdf ? ContentDisposition.inline() : ContentDisposition.attachment();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(pdf ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, hienThi.filename(tep.tenFile(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(tep.noiDung());
    }
}
