package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import com.example.demo.dto.request.XuLyBienBanRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.request.LuuBienBanRequest;
import com.example.demo.dto.request.TrinhBienBanRequest;
import com.example.demo.dto.response.BienBanChoDuyetResponse;
import com.example.demo.dto.response.BienBanDaPhatHanhResponse;
import com.example.demo.dto.response.BienBanResponse;
import com.example.demo.dto.response.PhienBanBienBanResponse;
import com.example.demo.service.BienBanService;
import com.example.demo.service.TaiLieuBienBanService;
import com.example.demo.dto.response.TaiLieuBienBanResponse;
import com.example.demo.service.NguoiDungHienTaiService;
import com.example.demo.service.QuyTrinhBienBanService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bien-ban")
@RequiredArgsConstructor
public class BienBanController {

    private final BienBanService bienBanService;
    private final TaiLieuBienBanService taiLieuBienBanService;

    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final QuyTrinhBienBanService quyTrinhBienBanService;

    @PostMapping("/cuoc-hop/{cuocHopId}")
    public ResponseEntity<BienBanResponse> taoBienBan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody LuuBienBanRequest request,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        BienBanResponse response = bienBanService.taoBienBan(
                cuocHopId,
                request,
                nguoiDungId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{bienBanId}")
    public ResponseEntity<BienBanResponse> capNhatBienBan(
            @PathVariable("bienBanId") Integer bienBanId,
            @RequestBody LuuBienBanRequest request,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        BienBanResponse response = bienBanService.capNhatBienBan(
                bienBanId,
                request,
                nguoiDungId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cuoc-hop/{cuocHopId}")
    public ResponseEntity<BienBanResponse> layBienBanTheoCuocHop(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        BienBanResponse response =
                bienBanService.layBienBanTheoCuocHop(
                        cuocHopId,
                        nguoiDungId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cho-toi-phat-hanh")
    public List<BienBanResponse> layBienBanChoPhatHanh(Authentication auth) {
        return bienBanService.layBienBanChoPhatHanh(nguoiDungHienTaiService.layNguoiDungId(auth));
    }

    @GetMapping("/da-phat-hanh-cua-toi")
    public List<BienBanDaPhatHanhResponse> layBienBanDaPhatHanhCuaToi(Authentication authentication) {
        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return bienBanService.layBienBanDaPhatHanhCuaToi(nguoiDungId);
    }

    @PostMapping(value = "/cuoc-hop/{cuocHopId}/tai-lieu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Integer>> taiLenTaiLieu(
            @PathVariable Integer cuocHopId, @RequestParam("file") MultipartFile file, Authentication auth) {
        Integer taiLieuId = taiLieuBienBanService.taiLen(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth), file);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("taiLieuId", taiLieuId));
    }

    @GetMapping("/cuoc-hop/{cuocHopId}/tai-lieu")
    public List<TaiLieuBienBanResponse> layTaiLieu(@PathVariable Integer cuocHopId, Authentication auth) {
        return taiLieuBienBanService.layDanhSach(cuocHopId, nguoiDungHienTaiService.layNguoiDungId(auth));
    }

    @GetMapping("/cuoc-hop/{cuocHopId}/tai-lieu/{taiLieuId}/tai-xuong")
    public ResponseEntity<byte[]> taiXuongTaiLieu(
            @PathVariable Integer cuocHopId, @PathVariable Integer taiLieuId, Authentication auth) {
        var tep = taiLieuBienBanService.taiXuong(cuocHopId, taiLieuId, nguoiDungHienTaiService.layNguoiDungId(auth));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(tep.tenFile(), StandardCharsets.UTF_8).build().toString())
                .body(tep.noiDung());
    }

    @GetMapping("/{bienBanId}/phien-ban")
    public ResponseEntity<List<PhienBanBienBanResponse>>
            layLichSuPhienBan(
                    @PathVariable("bienBanId") Integer bienBanId,
                    Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        List<PhienBanBienBanResponse> response =
                bienBanService.layLichSuPhienBan(
                        bienBanId,
                        nguoiDungId);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/{bienBanId}/trinh-duyet")
    public ResponseEntity<Map<String, Object>> trinhBienBan(
            @PathVariable("bienBanId") Integer bienBanId,
            @RequestBody TrinhBienBanRequest request,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        Integer yeuCauId = quyTrinhBienBanService.trinhBienBan(
                bienBanId,
                nguoiDungId,
                request.getGhiChuTrinh());

        return ResponseEntity.ok(Map.of(
                "message", "Trình duyệt biên bản thành công",
                "bienBanId", bienBanId,
                "yeuCauPheDuyetId", yeuCauId,
                "trangThai", "CHO_DUYET"));
    }
    @PostMapping("/{bienBanId}/phe-duyet")
    public ResponseEntity<Map<String, Object>> pheDuyetBienBan(
            @PathVariable("bienBanId") Integer bienBanId,
            @RequestBody XuLyBienBanRequest request,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        quyTrinhBienBanService.pheDuyetBienBan(
                bienBanId,
                request.getYeuCauPheDuyetId(),
                nguoiDungId,
                request.getYKien());

        return ResponseEntity.ok(Map.of(
                "message", "Biên bản đã được chủ trì duyệt và chờ chủ trì phát hành",
                "bienBanId", bienBanId,
                "trangThai", "DA_PHE_DUYET"));
    }

    @PostMapping("/{bienBanId}/tra-lai")
    public ResponseEntity<Map<String, Object>> traLaiBienBan(
            @PathVariable("bienBanId") Integer bienBanId,
            @RequestBody XuLyBienBanRequest request,
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);

        quyTrinhBienBanService.traLaiBienBan(
                bienBanId,
                request.getYeuCauPheDuyetId(),
                nguoiDungId,
                request.getYKien());

        return ResponseEntity.ok(Map.of(
                "message", "Trả lại biên bản để chỉnh sửa thành công",
                "bienBanId", bienBanId,
                "trangThai", "BI_TRA_LAI"));
    }
    @GetMapping("/cho-toi-duyet")
    public List<BienBanChoDuyetResponse> layBienBanChoToiDuyet(
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return quyTrinhBienBanService.layBienBanChoToiDuyet(nguoiDungId);
    }

    @PostMapping("/cuoc-hop/{cuocHopId}/phat-hanh")
    public ResponseEntity<BienBanResponse> phatHanhBienBan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {
        Integer nguoiDungId = nguoiDungHienTaiService
                .layNguoiDungId(authentication);
        return ResponseEntity.ok(
                bienBanService.phatHanhBienBan(cuocHopId, nguoiDungId));
    }
}