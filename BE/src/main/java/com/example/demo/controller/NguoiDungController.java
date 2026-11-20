
package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.request.TaoNguoiDungRequest;
import com.example.demo.dto.request.CapNhatNguoiDungRequest;
import com.example.demo.dto.request.CapNhatVaiTroRequest;
import com.example.demo.dto.response.NguoiDungResponse;
import com.example.demo.dto.response.VaiTroLuaChonResponse;
import com.example.demo.service.NguoiDungService;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    public NguoiDungController(NguoiDungService nguoiDungService) {
        this.nguoiDungService = nguoiDungService;
    }

    // Lấy danh sách người dùng
    @GetMapping
    public ResponseEntity<List<NguoiDungResponse>> layDanhSachNguoiDung() {
        return ResponseEntity.ok(
                nguoiDungService.layDanhSachNguoiDung()
        );
    }

   

    // Thêm người dùng
    @PostMapping
    public ResponseEntity<NguoiDungResponse> themNguoiDung(
            @RequestBody TaoNguoiDungRequest request) {

        NguoiDungResponse response =
                nguoiDungService.themNguoiDung(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Lấy chi tiết người dùng
    @GetMapping("/{id}")
    public ResponseEntity<NguoiDungResponse> layChiTietNguoiDung(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                nguoiDungService.layChiTietNguoiDung(id)
        );
    }

    // Cập nhật thông tin người dùng
    @PutMapping("/{id}")
    public ResponseEntity<NguoiDungResponse> capNhatNguoiDung(
            @PathVariable Integer id,
            @RequestBody CapNhatNguoiDungRequest request) {

        NguoiDungResponse response =
                nguoiDungService.capNhatNguoiDung(id, request);

        return ResponseEntity.ok(response);
    }
 // Thay đổi vai trò người dùng
    @PatchMapping("/{id}/vai-tro")
    public ResponseEntity<NguoiDungResponse> capNhatVaiTro(
            @PathVariable Integer id,
            @RequestBody CapNhatVaiTroRequest request) {

        return ResponseEntity.ok(
                nguoiDungService.capNhatVaiTro(id, request)
        );
    }
    @GetMapping("/roles")
    public List<VaiTroLuaChonResponse> layVaiTroHoatDong() {
        return nguoiDungService.layVaiTroHoatDong();
    }
}
