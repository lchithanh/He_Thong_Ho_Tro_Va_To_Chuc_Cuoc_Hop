package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.NghiQuyetService;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cuoc-hop/{cuocHopId}/nghi-quyet")
public class NghiQuyetController {
    private final NghiQuyetService nghiQuyet;
    private final NguoiDungHienTaiService nguoiDung;

    public record YeuCauSua(String lyDo) {}

    @GetMapping
    public List<NghiQuyetService.Muc> xem(@PathVariable Integer cuocHopId, Authentication auth) {
        return nghiQuyet.xem(cuocHopId, nguoiDung.layNguoiDungId(auth));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> tao(@PathVariable Integer cuocHopId, @RequestBody NghiQuyetService.NoiDung request,
            Authentication auth) {
        return Map.of("nghiQuyetId", nghiQuyet.tao(cuocHopId, nguoiDung.layNguoiDungId(auth), request));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sua(@PathVariable Integer cuocHopId, @PathVariable Integer id,
            @RequestBody NghiQuyetService.NoiDung request, Authentication auth) {
        nghiQuyet.sua(cuocHopId, id, nguoiDung.layNguoiDungId(auth), request);
    }

    @PostMapping("/{id}/yeu-cau-sua")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void yeuCauSua(@PathVariable Integer cuocHopId, @PathVariable Integer id,
            @RequestBody YeuCauSua request, Authentication auth) {
        nghiQuyet.yeuCauSua(cuocHopId, id, nguoiDung.layNguoiDungId(auth), request.lyDo());
    }
}
