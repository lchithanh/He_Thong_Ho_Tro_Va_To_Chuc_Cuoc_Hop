package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.response.TaiLieuKetLuanResponse;
import com.example.demo.dto.request.LuuKetLuanCuocHopRequest;
import com.example.demo.dto.request.TrinhBienBanRequest;
import com.example.demo.dto.request.XuLyBienBanRequest;
import com.example.demo.dto.response.KetLuanCuocHopResponse;
import com.example.demo.dto.response.TaiLenTaiLieuKetLuanResponse;
import com.example.demo.service.KetLuanCuocHopService;
import com.example.demo.service.NguoiDungHienTaiService;
import com.example.demo.service.TaiLieuKetLuanService;
import com.example.demo.service.TaiLieuKetLuanService.TepKetLuanTaiXuong;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuoc-hop")
@RequiredArgsConstructor
public class KetLuanCuocHopController {

    private final KetLuanCuocHopService ketLuanCuocHopService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final TaiLieuKetLuanService taiLieuKetLuanService;

    @PostMapping("/{cuocHopId}/ket-luan")
    public ResponseEntity<KetLuanCuocHopResponse> taoKetLuanNhap(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody LuuKetLuanCuocHopRequest request,
            Authentication authentication) {

        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        KetLuanCuocHopResponse response = ketLuanCuocHopService
                .taoKetLuanNhap(cuocHopId, request, nguoiDangNhapId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{cuocHopId}/ket-luan")
    public ResponseEntity<KetLuanCuocHopResponse> capNhatKetLuanNhap(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody LuuKetLuanCuocHopRequest request,
            Authentication authentication) {
        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);
        return ResponseEntity.ok(ketLuanCuocHopService.capNhatKetLuanNhap(
                cuocHopId, request, nguoiDangNhapId));
    }

    @PostMapping("/{cuocHopId}/ket-luan/trinh-duyet")
    public ResponseEntity<Map<String, Object>> trinhDuyetKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody TrinhBienBanRequest request,
            Authentication authentication) {
        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);
        Integer yeuCauId = ketLuanCuocHopService.trinhDuyetKetLuan(
                cuocHopId, request.getGhiChuTrinh(), nguoiDangNhapId);
        return ResponseEntity.ok(Map.of(
                "message", "Đã trình kết luận cho chủ trì duyệt",
                "yeuCauPheDuyetId", yeuCauId,
                "trangThai", "CHO_DUYET"));
    }
    @PostMapping("/{cuocHopId}/ket-luan/phat-hanh")
    public ResponseEntity<KetLuanCuocHopResponse> phatHanhKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        return ResponseEntity.ok(
                ketLuanCuocHopService.phatHanhKetLuan(cuocHopId, nguoiDangNhapId));
    }

    @PostMapping("/{cuocHopId}/ket-luan/yeu-cau/{yeuCauId}/phe-duyet")
    public ResponseEntity<Map<String, Object>> pheDuyetKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("yeuCauId") Integer yeuCauId,
            @RequestBody XuLyBienBanRequest request,
            Authentication authentication) {
        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);
        boolean daDuyetCuoi = ketLuanCuocHopService.xuLyDuyetKetLuan(
                cuocHopId, yeuCauId, nguoiDangNhapId,
                "phe-duyet", request.getYKien());
        return ResponseEntity.ok(Map.of(
                "message", daDuyetCuoi
                        ? "Kết luận đã được duyệt cuối; thư ký cuộc họp có thể phát hành"
                        : "Chủ trì đã duyệt; kết luận đã chuyển lãnh đạo duyệt",
                "trangThai", daDuyetCuoi ? "DA_DUYET" : "CHO_DUYET"));
    }

    @PostMapping("/{cuocHopId}/ket-luan/yeu-cau/{yeuCauId}/tra-lai")
    public ResponseEntity<Map<String, Object>> traLaiKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("yeuCauId") Integer yeuCauId,
            @RequestBody XuLyBienBanRequest request,
            Authentication authentication) {
        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);
        ketLuanCuocHopService.xuLyDuyetKetLuan(
                cuocHopId, yeuCauId, nguoiDangNhapId,
                "tra-lai", request.getYKien());
        return ResponseEntity.ok(Map.of(
                "message", "Đã trả kết luận để thư ký chỉnh sửa",
                "trangThai", "BI_TRA_LAI"));
    }
    
    @PostMapping(path = "/{cuocHopId}/ket-luan/tai-lieu",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    
    public ResponseEntity<TaiLenTaiLieuKetLuanResponse> taiLenTaiLieuKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        Integer taiLieuId = taiLieuKetLuanService.taiLenTaiLieuKetLuan(
                cuocHopId, file, nguoiDangNhapId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TaiLenTaiLieuKetLuanResponse(taiLieuId));
    }
    
    @GetMapping("/{cuocHopId}/ket-luan")
    public ResponseEntity<KetLuanCuocHopResponse> layKetLuanTheoCuocHop(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        return ResponseEntity.ok(
                ketLuanCuocHopService.layKetLuanTheoCuocHop(
                        cuocHopId, nguoiDangNhapId));
    }
    
    @GetMapping("/{cuocHopId}/ket-luan/tai-lieu")
    public ResponseEntity<List<TaiLieuKetLuanResponse>> layDanhSachTaiLieuKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        return ResponseEntity.ok(taiLieuKetLuanService
                .layDanhSachTaiLieuKetLuan(cuocHopId, nguoiDangNhapId));
    }

	@GetMapping("/{cuocHopId}/ket-luan/tai-lieu/{taiLieuId}/tai-xuong")
    public ResponseEntity<byte[]> taiXuongTaiLieuKetLuan(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("taiLieuId") Integer taiLieuId,
            Authentication authentication) {

        Integer nguoiDangNhapId =
                nguoiDungHienTaiService.layNguoiDungId(authentication);

        TepKetLuanTaiXuong tep = taiLieuKetLuanService
                .taiXuongTaiLieuKetLuan(cuocHopId, taiLieuId, nguoiDangNhapId);

        String contentDisposition = ContentDisposition.attachment()
                .filename(tep.tenFile(), StandardCharsets.UTF_8)
                .build()
                .toString();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(tep.noiDung());
    }
    
    @GetMapping("/ket-luan/cho-toi-phat-hanh")
    public List<KetLuanCuocHopResponse> layKetLuanChoToiPhatHanh(
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return ketLuanCuocHopService.layKetLuanChoToiPhatHanh(nguoiDungId);
    }
    
    @GetMapping("/ket-luan/da-phat-hanh-cua-toi")
    public List<KetLuanCuocHopResponse> layKetLuanDaPhatHanhCuaToi(
            Authentication authentication) {

        Integer nguoiDungId = nguoiDungHienTaiService.layNguoiDungId(authentication);
        return ketLuanCuocHopService.layKetLuanDaPhatHanhCuaToi(nguoiDungId);
    }
}