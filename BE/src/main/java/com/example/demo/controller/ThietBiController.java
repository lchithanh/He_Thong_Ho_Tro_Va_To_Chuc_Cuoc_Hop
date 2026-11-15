package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.service.ThietBiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/equipment")
@RequiredArgsConstructor
public class ThietBiController {

    private final ThietBiService thietBiService;

    // Lấy danh sách thiết bị
    @GetMapping
    public ResponseEntity<List<ThietBiResponse>> layDanhSachThietBi() {
        return ResponseEntity.ok(thietBiService.layDanhSachThietBi());
    }

    // Lấy chi tiết thiết bị
    @GetMapping("/{id}")
    public ResponseEntity<ThietBiResponse> layChiTietThietBi(
            @PathVariable Integer id) {
        return ResponseEntity.ok(thietBiService.layChiTietThietBi(id));
    }

    // Thêm thiết bị
    @PostMapping
    public ResponseEntity<ThietBiResponse> themThietBi(
            @Valid @RequestBody TaoThietBiRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(thietBiService.themThietBi(request));
    }

    // Cập nhật thiết bị
    @PutMapping("/{id}")
    public ResponseEntity<ThietBiResponse> capNhatThietBi(
            @PathVariable Integer id,
            @Valid @RequestBody CapNhatThietBiRequest request) {
        return ResponseEntity.ok(
                thietBiService.capNhatThietBi(id, request));
    }

    // Cập nhật tình trạng thiết bị
    @PatchMapping("/{id}/status")
    public ResponseEntity<ThietBiResponse> capNhatTinhTrang(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        String tinhTrang = request.get("tinhTrang");

        return ResponseEntity.ok(
                thietBiService.capNhatTinhTrang(id, tinhTrang));
    }
}