package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.response.TaiLieuKeHoachResponse;
import com.example.demo.service.TaiLieuKeHoachService;
import com.example.demo.service.NguoiDungHienTaiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tai-lieu-ke-hoach")
public class TaiLieuKeHoachController {
    private final TaiLieuKeHoachService taiLieuKeHoachService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    @PostMapping(value = "/cuoc-hop/{cuocHopId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> taiLen(@PathVariable Integer cuocHopId,
            @RequestParam("file") MultipartFile file, Authentication auth) {
        Integer id = taiLieuKeHoachService.taiLen(cuocHopId, layNguoiDungId(auth), file);
        return Map.of("message", "Tải tài liệu kế hoạch thành công", "taiLieuId", id);
    }

    @GetMapping("/cuoc-hop/{cuocHopId}")
    public List<TaiLieuKeHoachResponse> danhSachSoanThao(
            @PathVariable Integer cuocHopId, Authentication auth) {
        return taiLieuKeHoachService.layDanhSachSoanThao(cuocHopId, layNguoiDungId(auth));
    }

    @GetMapping("/cuoc-hop/{cuocHopId}/tai-lieu/{taiLieuId}/tai-xuong")
    public ResponseEntity<byte[]> taiXuongSoanThao(@PathVariable Integer cuocHopId,
            @PathVariable Integer taiLieuId, Authentication auth) {
        return traTep(taiLieuKeHoachService.taiXuongSoanThao(
                cuocHopId, taiLieuId, layNguoiDungId(auth)));
    }

    @DeleteMapping("/cuoc-hop/{cuocHopId}/tai-lieu/{taiLieuId}")
    public Map<String, String> xoa(@PathVariable Integer cuocHopId,
            @PathVariable Integer taiLieuId, Authentication auth) {
        taiLieuKeHoachService.xoa(cuocHopId, taiLieuId, layNguoiDungId(auth));
        return Map.of("message", "Đã xóa tài liệu chưa được trình");
    }

    @GetMapping("/yeu-cau/{yeuCauId}")
    public List<TaiLieuKeHoachResponse> danhSachLanTrinh(
            @PathVariable Integer yeuCauId, Authentication auth) {
        return taiLieuKeHoachService.layTheoLanTrinh(yeuCauId, layNguoiDungId(auth));
    }

    @GetMapping("/yeu-cau/{yeuCauId}/tai-lieu/{taiLieuId}/tai-xuong")
    public ResponseEntity<byte[]> taiXuongLanTrinh(@PathVariable Integer yeuCauId,
            @PathVariable Integer taiLieuId, Authentication auth) {
        return traTep(taiLieuKeHoachService.taiXuongTheoLanTrinh(
                yeuCauId, taiLieuId, layNguoiDungId(auth)));
    }

    private ResponseEntity<byte[]> traTep(TaiLieuKeHoachService.TepTaiXuong tep) {
        String contentType = switch (tep.dinhDangFile()) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(tep.tenFile(), StandardCharsets.UTF_8).build().toString())
                .cacheControl(CacheControl.noStore()).contentLength(tep.noiDung().length).body(tep.noiDung());
    }

    private Integer layNguoiDungId(Authentication auth) {
        return nguoiDungHienTaiService.layNguoiDungId(auth);
    }
}
