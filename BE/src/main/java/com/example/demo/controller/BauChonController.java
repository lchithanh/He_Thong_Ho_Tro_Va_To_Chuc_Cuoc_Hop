package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.BauChonService;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cuoc-hop/{cuocHopId}/bau-chon")
public class BauChonController {
    private final BauChonService bauChon;
    private final NguoiDungHienTaiService nguoiDung;

    public record Tao(String tieuDe, List<Integer> ungVienIds) {}
    public record Gui(List<Integer> ungVienIds) {}

    @GetMapping
    public BauChonService.DuLieu xem(@PathVariable Integer cuocHopId, Authentication auth) {
        return bauChon.xem(cuocHopId, nguoiDung.layNguoiDungId(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> tao(@PathVariable Integer cuocHopId, @RequestBody Tao request, Authentication auth) {
        return Map.of("cuocBauId", bauChon.tao(cuocHopId, nguoiDung.layNguoiDungId(auth),
                request.tieuDe(), request.ungVienIds()));
    }

    @PostMapping("/vong/{vongId}/mo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void mo(@PathVariable Integer cuocHopId, @PathVariable Integer vongId, Authentication auth) {
        bauChon.mo(cuocHopId, vongId, nguoiDung.layNguoiDungId(auth));
    }

    @PostMapping("/vong/{vongId}/phieu")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void gui(@PathVariable Integer cuocHopId, @PathVariable Integer vongId,
            @RequestBody Gui request, Authentication auth) {
        bauChon.boPhieu(cuocHopId, vongId, nguoiDung.layNguoiDungId(auth), request.ungVienIds());
    }

    @PostMapping("/vong/{vongId}/dong")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dong(@PathVariable Integer cuocHopId, @PathVariable Integer vongId, Authentication auth) {
        bauChon.dong(cuocHopId, vongId, nguoiDung.layNguoiDungId(auth));
    }
}
