package com.example.demo.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.service.KhachNgoaiCuocHopService;
import com.example.demo.service.KhachNgoaiCuocHopService.LoiMoiKhach;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loi-moi-khach")
@RequiredArgsConstructor
public class LoiMoiKhachController {
    private final KhachNgoaiCuocHopService khachNgoai;

    public record PhanHoi(Boolean thamDu) {}

    @GetMapping
    public ResponseEntity<LoiMoiKhach> xem(@RequestHeader(value = "X-Ma-Loi-Moi", required = false) String ma) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(khachNgoai.xemLoiMoi(ma));
    }

    @PostMapping("/phan-hoi")
    public ResponseEntity<LoiMoiKhach> phanHoi(
            @RequestHeader(value = "X-Ma-Loi-Moi", required = false) String ma, @RequestBody PhanHoi phanHoi) {
        if (phanHoi.thamDu() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chọn tham dự hoặc từ chối");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(khachNgoai.phanHoi(ma, phanHoi.thamDu()));
    }
}
