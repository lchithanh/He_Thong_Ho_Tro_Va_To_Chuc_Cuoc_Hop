package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CapNhatLichHopRequest;
import com.example.demo.dto.request.TaoLichHopRequest;
import com.example.demo.dto.request.TuChoiLichHopRequest;
import com.example.demo.dto.response.LichHopResponse;
import com.example.demo.service.DeXuatLichHopService;
import com.example.demo.service.LichHopService;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lich-hop")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class LichHopController {

    private final LichHopService lichHopService;
    private final DeXuatLichHopService deXuatLichHopService;
    
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    private Integer layNguoiDungId(Authentication authentication) {
        return nguoiDungHienTaiService.layNguoiDungId(authentication);
    }
    @GetMapping
    public ResponseEntity<List<LichHopResponse>> layDanhSachLichHop(
            Authentication authentication) {
        return ResponseEntity.ok(lichHopService.layDanhSachLichHop(layNguoiDungId(authentication)));
    }

    @GetMapping("/cuoc-hop/{cuocHopId}")
    public ResponseEntity<List<LichHopResponse>> layLichTheoCuocHop(
            @PathVariable("cuocHopId") Integer cuocHopId, Authentication authentication) {
        return ResponseEntity.ok(lichHopService.layLichTheoCuocHop(
                cuocHopId, layNguoiDungId(authentication)));
    }

    @GetMapping("/cuoc-hop/{cuocHopId}/phong-hop")
    public List<LichHopService.LuaChonPhongHop> layPhongHopDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestParam(value = "batDau", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime batDau,
            @RequestParam(value = "ketThuc", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ketThuc,
            Authentication authentication) {
        return lichHopService.layPhongHopDeXuat(cuocHopId, layNguoiDungId(authentication), batDau, ketThuc);
    }

    @GetMapping("/{lichHopId}")
    public ResponseEntity<LichHopResponse> layChiTiet(
            @PathVariable("lichHopId") Integer lichHopId, Authentication authentication) {
        return ResponseEntity.ok(lichHopService.layChiTiet(lichHopId, layNguoiDungId(authentication)));
    }

    @GetMapping("/{lichHopId}/chi-tiet-calendar")
    public LichHopService.ChiTietLichCalendar layChiTietCalendar(
            @PathVariable("lichHopId") Integer lichHopId, Authentication auth) {
        return lichHopService.layChiTietCalendar(lichHopId, layNguoiDungId(auth));
    }

    @PutMapping("/{lichHopId}/tu-choi")
    public LichHopResponse tuChoiLich(@PathVariable("lichHopId") Integer lichHopId,
            @RequestBody TuChoiLichHopRequest request, Authentication auth) {
        return lichHopService.tuChoiLichHop(lichHopId, layNguoiDungId(auth), request.lyDoTuChoi());
    }

    @PutMapping("/{lichHopId}/tra-lai")
    public LichHopResponse traLaiLich(@PathVariable("lichHopId") Integer lichHopId,
            @RequestBody TuChoiLichHopRequest request, Authentication auth) {
        return lichHopService.traLaiLichHop(lichHopId, layNguoiDungId(auth), request.lyDoTuChoi());
    }

    @PostMapping
    public ResponseEntity<LichHopResponse> taoLichHop(
            @RequestBody TaoLichHopRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                deXuatLichHopService.taoLichHop(request, layNguoiDungId(authentication)));
    }

    @PutMapping("/{lichHopId}")
    public ResponseEntity<LichHopResponse> capNhatLichHop(
            @PathVariable("lichHopId") Integer lichHopId,
            @RequestBody CapNhatLichHopRequest request, Authentication authentication) {
        return ResponseEntity.ok(deXuatLichHopService.capNhatLichHop(
                lichHopId, request, layNguoiDungId(authentication)));
    }

    @PostMapping("/{lichHopId}/doi-lich")
    public LichHopResponse doiLich(@PathVariable("lichHopId") Integer lichHopId,
            @RequestBody CapNhatLichHopRequest request, Authentication auth) {
        return lichHopService.doiLich(lichHopId, nguoiDungHienTaiService.layNguoiDungId(auth), request);
    }

    public record PhanHoiDoiLichRequest(Boolean dongY) {}

    @GetMapping("/doi-lich/cua-toi")
    public java.util.List<LichHopService.LoiMoiDoiLich> loiMoiDoiLich(Authentication auth) {
        return lichHopService.loiMoiDoiLich(layNguoiDungId(auth));
    }

    @GetMapping("/{lichHopId}/doi-lich/phan-hoi")
    public LichHopService.TienDoDoiLich tienDoDoiLich(@PathVariable("lichHopId") Integer lichHopId, Authentication auth) {
        return lichHopService.xemTienDoDoiLich(lichHopId, layNguoiDungId(auth));
    }

    @PutMapping("/{lichHopId}/doi-lich/phan-hoi")
    public ResponseEntity<Void> phanHoiDoiLich(@PathVariable("lichHopId") Integer lichHopId,
            @RequestBody PhanHoiDoiLichRequest request, Authentication auth) {
        if (request.dongY() == null) return ResponseEntity.badRequest().build();
        lichHopService.phanHoiDoiLich(lichHopId, layNguoiDungId(auth), request.dongY());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{lichHopId}/xac-nhan")
    public ResponseEntity<LichHopResponse> xacNhanLichHop(
            @PathVariable("lichHopId") Integer lichHopId, Authentication authentication) {
        return ResponseEntity.ok(lichHopService.xacNhanLichHop(
                lichHopId, layNguoiDungId(authentication)));
    }

    @PutMapping("/{lichHopCuId}/thay-the/{lichHopMoiId}")
    public ResponseEntity<LichHopResponse> thayTheLichHop(
            @PathVariable("lichHopCuId") Integer lichHopCuId,
            @PathVariable("lichHopMoiId") Integer lichHopMoiId, Authentication authentication) {
        return ResponseEntity.ok(deXuatLichHopService.thayTheLichHop(
                lichHopCuId, lichHopMoiId, layNguoiDungId(authentication)));
    }

    @PutMapping("/{lichHopId}/huy")
    public ResponseEntity<LichHopResponse> huyLichHop(
            @PathVariable("lichHopId") Integer lichHopId,
            @RequestParam("lyDoHuy") String lyDoHuy, Authentication authentication) {
        return ResponseEntity.ok(lichHopService.huyLichHop(
                lichHopId, lyDoHuy, layNguoiDungId(authentication)));
    }
}
