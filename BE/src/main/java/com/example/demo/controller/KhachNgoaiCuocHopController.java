package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping("/tai-lieu")
    public List<KhachNgoaiCuocHopService.TepCoTheChiaSe> taiLieu(@PathVariable Integer cuocHopId, Authentication auth) {
        return khachNgoaiService.taiLieuCoTheChiaSe(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth));
    }

    @PostMapping("/tai-lieu/{taiLieuId}/chia-se")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void chiaSeTepCoSan(@PathVariable Integer cuocHopId, @PathVariable Integer taiLieuId, Authentication auth) {
        khachNgoaiService.chiaSeTepCoSan(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth), taiLieuId);
    }

    @PostMapping(value = "/tai-lieu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void chiaSe(@PathVariable Integer cuocHopId, @RequestParam MultipartFile tep, Authentication auth) {
        khachNgoaiService.chiaSeTaiLieu(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth), tep);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> traLoiLoi(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getBody());
    }
}
