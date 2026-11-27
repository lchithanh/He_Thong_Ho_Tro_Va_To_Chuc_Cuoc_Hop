package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.BauLaiNhomService;
import com.example.demo.service.BauLaiNhomService.TrangBauLai;
import com.example.demo.service.BauLaiNhomService.TrangTrongHop;
import com.example.demo.service.BauLaiNhomService.XuLy;
import com.example.demo.service.BauLaiNhomService.YeuCauMoi;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BauLaiNhomController {
    private final BauLaiNhomService bauLai;
    private final NguoiDungHienTaiService hienTai;

    @GetMapping("/nhom-lien-phong/{nhomId}/bau-lai")
    public TrangBauLai xem(@PathVariable int nhomId, Authentication auth) {
        return bauLai.xem(nhomId, hienTai.layNguoiDungId(auth));
    }

    @GetMapping("/cuoc-hop/{cuocHopId}/bau-lai-nhom")
    public TrangTrongHop xemTrongHop(@PathVariable int cuocHopId, Authentication auth) {
        return bauLai.xemTrongHop(cuocHopId, hienTai.layNguoiDungId(auth));
    }

    @PostMapping("/nhom-lien-phong/{nhomId}/bau-lai")
    public void gui(@PathVariable int nhomId, @RequestBody YeuCauMoi request, Authentication auth) {
        bauLai.guiYeuCau(nhomId, hienTai.layNguoiDungId(auth), request);
    }

    @PostMapping("/nhom-lien-phong/{nhomId}/bau-lai/{yeuCauId}/{thaoTac}")
    public void xuLy(@PathVariable int nhomId, @PathVariable int yeuCauId, @PathVariable String thaoTac,
            @RequestBody XuLy request, Authentication auth) {
        bauLai.xuLy(nhomId, yeuCauId, thaoTac, hienTai.layNguoiDungId(auth), request);
    }
}
