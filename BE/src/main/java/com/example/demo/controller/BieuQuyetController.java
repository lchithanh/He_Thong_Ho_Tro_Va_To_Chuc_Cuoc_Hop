package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.GuiPhieuBieuQuyetRequest;
import com.example.demo.dto.request.TaoBieuQuyetRequest;
import com.example.demo.dto.response.BieuQuyetThamDuResponse;
import com.example.demo.dto.response.BieuQuyetTomTatResponse;
import com.example.demo.dto.response.ChiTietBieuQuyetResponse;
import com.example.demo.dto.response.KetQuaBieuQuyetResponse;
import com.example.demo.service.BieuQuyetService;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuoc-hop")
@RequiredArgsConstructor
public class BieuQuyetController {

    private final BieuQuyetService bieuQuyetService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    @PostMapping("/{cuocHopId}/bieu-quyet")
    public ResponseEntity<Map<String, Integer>> taoBieuQuyet(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody TaoBieuQuyetRequest request,
            Authentication authentication
    ) {
        Integer nguoiDungId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        Integer bieuQuyetId = bieuQuyetService.taoBieuQuyet(
                cuocHopId, request, nguoiDungId
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("bieuQuyetId", bieuQuyetId));
    }

    @PostMapping("/{cuocHopId}/bieu-quyet/{bieuQuyetId}/mo")
    public ResponseEntity<Void> moBieuQuyet(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("bieuQuyetId") Integer bieuQuyetId,
            Authentication authentication
    ) {
        Integer nguoiDungId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        bieuQuyetService.moBieuQuyet(cuocHopId, bieuQuyetId, nguoiDungId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cuocHopId}/bieu-quyet/{bieuQuyetId}/phieu")
    public ResponseEntity<Void> guiPhieuBieuQuyet(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("bieuQuyetId") Integer bieuQuyetId,
            @RequestBody GuiPhieuBieuQuyetRequest request,
            Authentication authentication
    ) {
        Integer nguoiDungId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        bieuQuyetService.guiPhieuBieuQuyet(
                cuocHopId, bieuQuyetId, request.luaChonId(), nguoiDungId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cuocHopId}/bieu-quyet/{bieuQuyetId}/dong")
    public ResponseEntity<KetQuaBieuQuyetResponse> dongBieuQuyet(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("bieuQuyetId") Integer bieuQuyetId,
            Authentication authentication
    ) {
        Integer nguoiDungId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        KetQuaBieuQuyetResponse ketQua = bieuQuyetService.dongBieuQuyet(
                cuocHopId, bieuQuyetId, nguoiDungId
        );

        return ResponseEntity.ok(ketQua);
    }
    
    @GetMapping("/{cuocHopId}/bieu-quyet/{bieuQuyetId}")
    public ResponseEntity<ChiTietBieuQuyetResponse> layChiTietBieuQuyet(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("bieuQuyetId") Integer bieuQuyetId,
            Authentication authentication
    ) {
        Integer nguoiDungId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        ChiTietBieuQuyetResponse chiTiet = bieuQuyetService.layChiTietBieuQuyet(
                cuocHopId, bieuQuyetId, nguoiDungId
        );

        return ResponseEntity.ok(chiTiet);
    }
    
    @GetMapping("/{cuocHopId}/bieu-quyet/{bieuQuyetId}/ket-qua")
    public ResponseEntity<KetQuaBieuQuyetResponse> layKetQuaBieuQuyet(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("bieuQuyetId") Integer bieuQuyetId,
            Authentication authentication
    ) {
        Integer nguoiDungId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        return ResponseEntity.ok(
                bieuQuyetService.layKetQuaBieuQuyet(
                        cuocHopId, bieuQuyetId, nguoiDungId
                )
        );
    }
    
    @GetMapping("/{cuocHopId}/bieu-quyet")
    public List<BieuQuyetTomTatResponse> layDanhSachBieuQuyetCuaChuTri(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return bieuQuyetService.layDanhSachBieuQuyetCuaChuTri(cuocHopId, nguoiDungId);
    }
    
    @GetMapping("/bieu-quyet/cua-toi")
    public List<BieuQuyetThamDuResponse> layDanhSachBieuQuyetThamDu(
            @RequestParam(name = "cuocHopId", required = false) Integer cuocHopId,
            Authentication authentication
    ) {
        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return bieuQuyetService.layDanhSachBieuQuyetThamDu(nguoiDungId, cuocHopId);
    }
    
    @GetMapping("/bieu-quyet/ket-qua-cua-toi")
    public List<BieuQuyetThamDuResponse> layDanhSachBieuQuyetDaDongCuaToi(
            @RequestParam(name = "cuocHopId", required = false) Integer cuocHopId,
            Authentication authentication
    ) {
        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return bieuQuyetService.layDanhSachBieuQuyetDaDongCuaToi(nguoiDungId, cuocHopId);
    }
}