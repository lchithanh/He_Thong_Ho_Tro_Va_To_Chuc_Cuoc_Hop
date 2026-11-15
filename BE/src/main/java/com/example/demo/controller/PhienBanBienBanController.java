package com.example.demo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.request.TaoPhienBanRequest;
import com.example.demo.dto.response.PhienBanBienBanResponse;
import com.example.demo.service.PhienBanBienBanService;
import com.example.demo.service.NguoiDungHienTaiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/phien-ban-bien-ban")
@RequiredArgsConstructor
public class PhienBanBienBanController {
    private final PhienBanBienBanService phienBanBienBanService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    @PostMapping
    public ResponseEntity<PhienBanBienBanResponse> taoPhienBan(
            @RequestBody TaoPhienBanRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                phienBanBienBanService.taoPhienBan(request.getBienBanId(),
                        nguoiDungHienTaiService.layNguoiDungId(authentication),
                        request.getNoiDung(), request.getGhiChuThayDoi()));
    }

    @GetMapping("/bien-ban/{bienBanId}")
    public ResponseEntity<List<PhienBanBienBanResponse>> layDanhSachPhienBan(
            @PathVariable Integer bienBanId, Authentication authentication) {
        return ResponseEntity.ok(phienBanBienBanService.layDanhSachPhienBan(
                bienBanId, nguoiDungHienTaiService.layNguoiDungId(authentication)));
    }

    @GetMapping("/bien-ban/{bienBanId}/moi-nhat")
    public ResponseEntity<PhienBanBienBanResponse> layPhienBanMoiNhat(
            @PathVariable Integer bienBanId, Authentication authentication) {
        return ResponseEntity.ok(phienBanBienBanService.layPhienBanMoiNhat(
                bienBanId, nguoiDungHienTaiService.layNguoiDungId(authentication)));
    }
}
