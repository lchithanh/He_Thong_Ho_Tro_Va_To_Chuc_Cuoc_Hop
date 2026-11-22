package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
@RequestMapping("/api/cuoc-hop")
public class NghiQuyetController {
    private static final String NGHI_QUYET = "/{cuocHopId}/nghi-quyet";
    private final NghiQuyetService nghiQuyet;
    private final NguoiDungHienTaiService nguoiDung;

    public record YeuCauSua(String lyDo) {}

    @GetMapping("/nghi-quyet/cua-toi")
    public List<NghiQuyetService.CuocHopCoNghiQuyet> cuaToi(Authentication auth) {
        return nghiQuyet.cuaToi(nguoiDung.layNguoiDungId(auth));
    }

    @GetMapping(NGHI_QUYET)
    public List<NghiQuyetService.Muc> xem(@PathVariable Integer cuocHopId, Authentication auth) {
        return nghiQuyet.xem(cuocHopId, nguoiDung.layNguoiDungId(auth));
    }

    @PostMapping(value = NGHI_QUYET, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> tao(@PathVariable Integer cuocHopId,
            @RequestParam(required = false) Integer nghiQuyetChaId, @RequestParam String tieuDe,
            @RequestParam MultipartFile tep, Authentication auth) {
        return Map.of("nghiQuyetId", nghiQuyet.tao(cuocHopId, nguoiDung.layNguoiDungId(auth),
                new NghiQuyetService.NoiDung(nghiQuyetChaId, tieuDe, null), tep));
    }

    @PutMapping(value = NGHI_QUYET + "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sua(@PathVariable Integer cuocHopId, @PathVariable Integer id,
            @RequestParam(required = false) Integer nghiQuyetChaId, @RequestParam String tieuDe,
            @RequestParam MultipartFile tep, Authentication auth) {
        nghiQuyet.sua(cuocHopId, id, nguoiDung.layNguoiDungId(auth),
                new NghiQuyetService.NoiDung(nghiQuyetChaId, tieuDe, null), tep);
    }

    @PostMapping(NGHI_QUYET + "/{id}/cong-bo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void congBo(@PathVariable Integer cuocHopId, @PathVariable Integer id, Authentication auth) {
        nghiQuyet.congBo(cuocHopId, id, nguoiDung.layNguoiDungId(auth));
    }

    @GetMapping(NGHI_QUYET + "/{id}/tep")
    public ResponseEntity<byte[]> tep(@PathVariable Integer cuocHopId, @PathVariable Integer id, Authentication auth) {
        var tep = nghiQuyet.taiTep(cuocHopId, id, nguoiDung.layNguoiDungId(auth));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.APPLICATION_PDF)
                .body(tep.noiDung());
    }

    @PostMapping(NGHI_QUYET + "/{id}/yeu-cau-sua")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void yeuCauSua(@PathVariable Integer cuocHopId, @PathVariable Integer id,
            @RequestBody YeuCauSua request, Authentication auth) {
        nghiQuyet.yeuCauSua(cuocHopId, id, nguoiDung.layNguoiDungId(auth), request.lyDo());
    }
}
