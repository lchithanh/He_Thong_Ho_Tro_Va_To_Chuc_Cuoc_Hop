package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.service.PhongHopService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/meeting-rooms")
public class PhongHopController {

    private final PhongHopService phongHopService;

    public PhongHopController(PhongHopService phongHopService) {
        this.phongHopService = phongHopService;
    }

    // 1. Lấy danh sách phòng họp
    @GetMapping
    public List<PhongHopResponse> layDanhSachPhongHop() {
        return phongHopService.layDanhSachPhongHop();
    }

    // 2. Lấy chi tiết phòng họp
    @GetMapping("/{id}")
    public PhongHopResponse layChiTietPhongHop(
            @PathVariable Integer id) {
        return phongHopService.layChiTietPhongHop(id);
    }

    // 3. Thêm phòng họp
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PhongHopResponse themPhongHop(
            @Valid @RequestBody TaoPhongHopRequest request) {
        return phongHopService.themPhongHop(request);
    }

    // 4. Cập nhật phòng họp
    @PutMapping("/{id}")
    public PhongHopResponse capNhatPhongHop(
            @PathVariable Integer id,
            @Valid @RequestBody CapNhatPhongHopRequest request) {
        return phongHopService.capNhatPhongHop(id, request);
    }

    // 5. Cập nhật trạng thái phòng họp
    @PatchMapping("/{id}/status")
    public PhongHopResponse capNhatTrangThai(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        return phongHopService.capNhatTrangThai(
                id,
                request.get("trangThai")
        );
    }
}