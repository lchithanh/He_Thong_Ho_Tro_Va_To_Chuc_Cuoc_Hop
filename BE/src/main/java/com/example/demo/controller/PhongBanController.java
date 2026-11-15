
package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.TaoPhongBanRequest;
import com.example.demo.dto.request.CapNhatPhongBanRequest;
import com.example.demo.dto.request.PhanCongTruongPhongRequest;
import com.example.demo.dto.response.PhongBanResponse;
import com.example.demo.service.PhongBanService;

@RestController
@RequestMapping("/api/admin/departments")
@CrossOrigin(origins = "http://localhost:5173")
public class PhongBanController {

    private final PhongBanService phongBanService;

    public PhongBanController(PhongBanService phongBanService) {
        this.phongBanService = phongBanService;
    }

    // 1. Lấy danh sách phòng ban
    @GetMapping
    public ResponseEntity<List<PhongBanResponse>> layDanhSachPhongBan() {

        return ResponseEntity.ok(
                phongBanService.layDanhSachPhongBan()
        );
    }

    // 2. Lấy chi tiết phòng ban
    @GetMapping("/{id}")
    public ResponseEntity<PhongBanResponse> layChiTietPhongBan(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                phongBanService.layChiTietPhongBan(id)
        );
    }

    // 3. Thêm phòng ban
    @PostMapping
    public ResponseEntity<PhongBanResponse> themPhongBan(
            @RequestBody TaoPhongBanRequest request) {

        PhongBanResponse response =
                phongBanService.themPhongBan(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 4. Cập nhật phòng ban
    @PutMapping("/{id}")
    public ResponseEntity<PhongBanResponse> capNhatPhongBan(
            @PathVariable Integer id,
            @RequestBody CapNhatPhongBanRequest request) {

        return ResponseEntity.ok(
                phongBanService.capNhatPhongBan(id, request)
        );
    }

    // 5. Phân công trưởng phòng
    @PatchMapping("/{id}/leader")
    public ResponseEntity<PhongBanResponse> phanCongTruongPhong(
            @PathVariable Integer id,
            @RequestBody PhanCongTruongPhongRequest request) {

        return ResponseEntity.ok(
                phongBanService.phanCongTruongPhong(
                        id,
                        request.truongPhongId()
                )
        );
    }
}
