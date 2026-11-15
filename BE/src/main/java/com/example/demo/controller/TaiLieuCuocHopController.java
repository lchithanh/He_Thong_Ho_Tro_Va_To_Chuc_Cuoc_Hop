package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.example.demo.service.TaiLieuCuocHopService;

import com.example.demo.service.NguoiDungHienTaiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuoc-hop")
@RequiredArgsConstructor
public class TaiLieuCuocHopController {

    private final TaiLieuCuocHopService taiLieuCuocHopService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    @PostMapping(
            value = "/{cuocHopId}/tai-lieu-de-xuat",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> taiLenDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        Integer taiLieuId = taiLieuCuocHopService
                .taiLenTaiLieuDeXuat(
                        cuocHopId,
                        layNguoiDungId(authentication),
                        file
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Tải tài liệu đề xuất thành công",
                        "cuocHopId", cuocHopId,
                        "taiLieuId", taiLieuId,
                        "loaiTaiLieu", "DE_XUAT_CUOC_HOP"
                ));
    }
    private Integer layNguoiDungId(Authentication authentication) {
        return nguoiDungHienTaiService.layNguoiDungId(authentication);
    }

    @GetMapping("/{cuocHopId}/tai-lieu-de-xuat")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        return ResponseEntity.ok(
                taiLieuCuocHopService.layDanhSachDeXuat(
                        cuocHopId,
                        layNguoiDungId(authentication)
                )
        );
    }

    @GetMapping("/{cuocHopId}/tai-lieu-de-xuat/{taiLieuId}/tai-xuong")
    public ResponseEntity<byte[]> taiXuongDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("taiLieuId") Integer taiLieuId,
            Authentication authentication) {

        TaiLieuCuocHopService.FileDeXuat file =
                taiLieuCuocHopService.taiXuongDeXuat(
                        cuocHopId,
                        taiLieuId,
                        layNguoiDungId(authentication)
                );

        String disposition = ContentDisposition.attachment()
                .filename(file.tenFile(), StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentLength(file.noiDung().length)
                .body(file.noiDung());
    }
    
    @DeleteMapping("/{cuocHopId}/tai-lieu-de-xuat/{taiLieuId}")
    public ResponseEntity<Map<String, Object>> xoaTaiLieuDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("taiLieuId") Integer taiLieuId,
            Authentication authentication) {

        taiLieuCuocHopService.xoaTaiLieuDeXuat(
                cuocHopId,
                taiLieuId,
                layNguoiDungId(authentication)
        );

        return ResponseEntity.ok(Map.of(
                "message", "Xóa tài liệu đề xuất thành công",
                "cuocHopId", cuocHopId,
                "taiLieuId", taiLieuId
        ));
    }
}