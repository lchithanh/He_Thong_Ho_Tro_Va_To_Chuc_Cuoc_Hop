package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> dangNhap(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.dangNhap(request);
        return ResponseEntity.ok(response);
    }

    // Lấy thông tin người dùng đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> layThongTinNguoiDung(
            Authentication authentication) {
    	if (authentication == null || !authentication.isAuthenticated()) {
    	    throw new ResponseStatusException(
    	            HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
    	}
        LoginResponse response = authService.layThongTinNguoiDung(
                authentication.getName());

        return ResponseEntity.ok(response);
    }
}