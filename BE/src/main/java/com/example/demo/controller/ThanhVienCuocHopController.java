package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.request.DiemDanhThanhVienRequest;
import com.example.demo.dto.request.MoiThanhVienCuocHopRequest;
import com.example.demo.dto.request.PhanHoiLoiMoiRequest;
import com.example.demo.dto.response.ThanhVienCuocHopResponse;
import com.example.demo.service.ThanhVienCuocHopService;
import com.example.demo.service.NguoiDungHienTaiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.demo.dto.response.LoiMoiChoPhanHoiResponse;
import com.example.demo.dto.response.NguoiCoTheMoiResponse;
@RestController
@RequestMapping("/api/cuoc-hop")
@RequiredArgsConstructor
public class ThanhVienCuocHopController {
	
	private final ThanhVienCuocHopService thanhVienCuocHopService;
	private final NguoiDungHienTaiService nguoiDungHienTaiService;
	@PostMapping("/{cuocHopId}/thanh-vien")
	public ResponseEntity<Void> moiThanhVien(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @Valid @RequestBody MoiThanhVienCuocHopRequest request,
            Authentication authentication) {

    	Integer thuKyId = nguoiDungHienTaiService.layNguoiDungId(authentication);    	
    	
        thanhVienCuocHopService.moiThanhVien(
                cuocHopId,
                request.nguoiDuocMoiId(),
                thuKyId
        );

        return ResponseEntity.noContent().build();
    }
    
    public record MoiDanhSachRequest(List<Integer> nguoiDuocMoiIds) {}

    @PostMapping("/{cuocHopId}/thanh-vien/moi-nhieu")
    public ResponseEntity<Void> moiDanhSachThanhVien(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody MoiDanhSachRequest request, Authentication auth) {
        thanhVienCuocHopService.moiDanhSachThanhVien(
                cuocHopId, request.nguoiDuocMoiIds(), nguoiDungHienTaiService.layNguoiDungId(auth));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cuocHopId}/thanh-vien")
    public List<ThanhVienCuocHopResponse> layDanhSachThanhVien(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer thuKyId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return thanhVienCuocHopService.layDanhSachThanhVien(cuocHopId, thuKyId);
    }
    
    @GetMapping("/{cuocHopId}/thanh-vien/nguoi-co-the-moi")
    public List<NguoiCoTheMoiResponse> layNguoiCoTheMoi(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer thuKyId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return thanhVienCuocHopService.layNguoiCoTheMoi(cuocHopId, thuKyId);
    }
    
    @PutMapping("/{cuocHopId}/thanh-vien/phan-hoi")
    public ResponseEntity<Void> phanHoiLoiMoi(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @Valid @RequestBody PhanHoiLoiMoiRequest request,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);

        thanhVienCuocHopService.phanHoiLoiMoi(
                cuocHopId, nguoiDungId, request.thamDu());

        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/thanh-vien/loi-moi-cua-toi")
    public List<LoiMoiChoPhanHoiResponse> layLoiMoiCuaToi(
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return thanhVienCuocHopService.layLoiMoiChoPhanHoi(nguoiDungId);
    }

    @PutMapping("/{cuocHopId}/thanh-vien/{nguoiDungId}/diem-danh")
    public ResponseEntity<Void> diemDanhThanhVien(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("nguoiDungId") Integer nguoiDungId,
            @Valid @RequestBody DiemDanhThanhVienRequest request,
            Authentication authentication) {

        Integer thuKyId = nguoiDungHienTaiService.layNguoiDungId(authentication);

        thanhVienCuocHopService.diemDanhThanhVien(
                cuocHopId, nguoiDungId, thuKyId, request.coMat());

        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{cuocHopId}/thanh-vien/{nguoiDungId}/roi-som")
    public ResponseEntity<Void> ghiNhanRoiSom(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("nguoiDungId") Integer nguoiDungId,
            Authentication authentication) {

        Integer thuKyId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        thanhVienCuocHopService.ghiNhanRoiSom(cuocHopId, nguoiDungId, thuKyId);

        return ResponseEntity.noContent().build();
    }
    // Dùng lại hàm layNguoiDungId(Authentication) từ LichHopController.
}