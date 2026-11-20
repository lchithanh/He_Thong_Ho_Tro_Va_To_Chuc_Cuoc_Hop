package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.MoiKhachNgoaiRequest;
import com.example.demo.dto.response.KhachNgoaiCuocHopResponse;
import com.example.demo.service.KhachNgoaiCuocHopService;
import com.example.demo.service.NguoiDungHienTaiService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuoc-hop/{cuocHopId}/khach-ngoai")
@RequiredArgsConstructor
public class KhachNgoaiCuocHopController {

    private final KhachNgoaiCuocHopService khachNgoaiService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    @GetMapping
    public List<KhachNgoaiCuocHopResponse> layDanhSach(
            @PathVariable("cuocHopId") Integer cuocHopId, Authentication auth) {
        return khachNgoaiService.layDanhSach(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth));
    }

    @PostMapping
    public ResponseEntity<KhachNgoaiCuocHopResponse> moiQuaEmail(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @Valid @RequestBody MoiKhachNgoaiRequest request, Authentication auth) {
        var loiMoi = khachNgoaiService.moiQuaEmail(
                cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(loiMoi);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> traLoiLoi(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getBody());
    }
}
