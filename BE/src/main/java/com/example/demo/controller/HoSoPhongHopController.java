package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.HoSoPhongHopService;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cuoc-hop/{cuocHopId}/ho-so-phong-hop")
public class HoSoPhongHopController {
    private final HoSoPhongHopService hoSoPhongHop;
    private final NguoiDungHienTaiService nguoiDungHienTai;

    @GetMapping
    public HoSoPhongHopService.HoSo xem(@PathVariable Integer cuocHopId, Authentication auth) {
        return hoSoPhongHop.xem(cuocHopId, nguoiDungHienTai.layNguoiDungId(auth));
    }

    @PostMapping(path = "/chia-se", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> chiaSe(@PathVariable Integer cuocHopId, @RequestParam MultipartFile file,
            @RequestParam(required = false) String ghiChu, Authentication auth) {
        return Map.of("taiLieuId", hoSoPhongHop.taiLen(cuocHopId, nguoiDungHienTai.layNguoiDungId(auth), file, ghiChu));
    }

    @GetMapping("/{nhom}/tep/{taiLieuId}")
    public ResponseEntity<byte[]> docTep(@PathVariable Integer cuocHopId, @PathVariable String nhom,
            @PathVariable Integer taiLieuId, Authentication auth) {
        var tep = hoSoPhongHop.docTep(cuocHopId, nguoiDungHienTai.layNguoiDungId(auth), nhom, taiLieuId);
        boolean pdf = tep.tenFile().toLowerCase(Locale.ROOT).endsWith(".pdf");
        var cachHienThi = pdf ? ContentDisposition.inline() : ContentDisposition.attachment();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(pdf ? MediaType.APPLICATION_PDF : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, cachHienThi.filename(tep.tenFile(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(tep.noiDung());
    }
}
