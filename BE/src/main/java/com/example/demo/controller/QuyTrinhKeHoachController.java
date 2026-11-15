package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.response.KeHoachPheDuyetResponse;
import com.example.demo.dto.response.TrinhKeHoachResponse;
import com.example.demo.dto.request.TrinhKeHoachRequest;
import com.example.demo.dto.request.GuiKeHoachCapTrenRequest;
import com.example.demo.service.PheDuyetKeHoachService;
import com.example.demo.service.QuyTrinhKeHoachService;
import com.example.demo.service.NguoiDungHienTaiService;
import lombok.RequiredArgsConstructor;

/** API bổ sung; ID người thực hiện lấy từ tài khoản JWT, không lấy từ body. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quy-trinh-ke-hoach")
public class QuyTrinhKeHoachController {
    private final QuyTrinhKeHoachService quyTrinhKeHoachService;
    private final PheDuyetKeHoachService pheDuyetKeHoachService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;

    public record YKienRequest(String yKien) { }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/gui-cap-tren")
    public TrinhKeHoachResponse guiCapTren(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId,
            @RequestBody(required = false) GuiKeHoachCapTrenRequest request,
            Authentication auth) {
        return quyTrinhKeHoachService.guiKeHoachLenCapTren(
                cuocHopId, yeuCauId, layNguoiDungId(auth),
                request == null ? null : request.lanhDaoId(),
                request == null ? null : request.ghiChu());
    }

    @PostMapping("/{cuocHopId}/trinh")

    public TrinhKeHoachResponse trinh(@PathVariable Integer cuocHopId,
            @RequestBody(required = false) TrinhKeHoachRequest request, Authentication auth) {
        return quyTrinhKeHoachService.trinhKeHoachVaLayKetQua(
                cuocHopId, request, layNguoiDungId(auth));
    }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/tiep-nhan")
    public Map<String, String> tiepNhan(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId, Authentication auth) {
        quyTrinhKeHoachService.tiepNhanHoSoLanhDao(cuocHopId, yeuCauId, layNguoiDungId(auth));
        return Map.of("message", "Thư ký lãnh đạo đã tiếp nhận hồ sơ");
    }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/tu-choi-tiep-nhan")
    public Map<String, String> tuChoiTiepNhan(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId, @RequestBody YKienRequest request, Authentication auth) {
        quyTrinhKeHoachService.tuChoiTiepNhan(cuocHopId, yeuCauId, layNguoiDungId(auth), yKien(request));
        return Map.of("message", "Đã từ chối tiếp nhận hồ sơ");
    }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/trinh-lanh-dao")
    public Map<String, String> trinhLanhDao(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId, @RequestBody(required = false) YKienRequest request,
            Authentication auth) {
        quyTrinhKeHoachService.trinhLanhDao(cuocHopId, yeuCauId,
                layNguoiDungId(auth), yKien(request));
        return Map.of("message", "Đã trình lãnh đạo phê duyệt");
    }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/phe-duyet")
    public Map<String, String> pheDuyet(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId, @RequestBody(required = false) YKienRequest request,
            Authentication auth) {
        boolean daDuyetCuoi = quyTrinhKeHoachService.pheDuyetKeHoach(
                cuocHopId, yeuCauId, layNguoiDungId(auth), yKien(request));
        return Map.of("message", daDuyetCuoi
                ? "Phê duyệt kế hoạch thành công"
                : "Đã đồng ý trình cấp trên; hồ sơ vẫn chờ thư ký chuyển tiếp");
    }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/tra-lai")
    public Map<String, String> traLai(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId, @RequestBody YKienRequest request, Authentication auth) {
        quyTrinhKeHoachService.traLaiKeHoach(cuocHopId, yeuCauId,
                layNguoiDungId(auth), yKien(request));
        return Map.of("message", "Đã trả lại kế hoạch để chỉnh sửa");
    }

    @PostMapping("/{cuocHopId}/yeu-cau/{yeuCauId}/tu-choi")
    public Map<String, String> tuChoi(@PathVariable Integer cuocHopId,
            @PathVariable Integer yeuCauId, @RequestBody YKienRequest request, Authentication auth) {
        quyTrinhKeHoachService.tuChoiKeHoach(cuocHopId, yeuCauId,
                layNguoiDungId(auth), yKien(request));
        return Map.of("message", "Đã từ chối kế hoạch");
    }

    @PostMapping("/{cuocHopId}/mo-lai-bo-sung-khach-ngoai")
    public Map<String, String> moLai(@PathVariable Integer cuocHopId,
            @RequestBody YKienRequest request, Authentication auth) {
        quyTrinhKeHoachService.moLaiDeBoSungKhachNgoai(cuocHopId,
                layNguoiDungId(auth), yKien(request));
        return Map.of("message", "Đã mở lại kế hoạch; cần trình duyệt lại");
    }

    @GetMapping("/cua-toi/cho-duyet")
    public List<KeHoachPheDuyetResponse> choDuyet(Authentication auth) {
        return pheDuyetKeHoachService.layDanhSachKeHoachChoDuyet(layNguoiDungId(auth));
    }
    @GetMapping("/cua-toi/da-duyet")
    public List<KeHoachPheDuyetResponse> daDuyet(Authentication auth) {
        return pheDuyetKeHoachService.layDanhSachKeHoachDaDuyet(layNguoiDungId(auth));
    }
    @GetMapping("/thu-ky-lanh-dao/ho-so")
    public List<KeHoachPheDuyetResponse> hoSoThuKy(Authentication auth) {
        return pheDuyetKeHoachService.layHoSoCuaThuKyLanhDao(layNguoiDungId(auth));
    }

    private String yKien(YKienRequest request) {
        return request == null ? null : request.yKien();
    }

    private Integer layNguoiDungId(Authentication auth) {
        return nguoiDungHienTaiService.layNguoiDungId(auth);
    }
}



