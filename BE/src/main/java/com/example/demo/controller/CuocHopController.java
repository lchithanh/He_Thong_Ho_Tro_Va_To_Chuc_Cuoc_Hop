package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.CapNhatKeHoachCuocHopRequest;
import com.example.demo.dto.request.PheDuyetKeHoachRequest;
import com.example.demo.dto.request.TaoDeXuatCuocHopRequest;
import com.example.demo.dto.request.TraLaiKeHoachRequest;
import com.example.demo.dto.request.TrinhKeHoachRequest;
import com.example.demo.dto.response.BuocXuLyCuocHopResponse;
import com.example.demo.dto.response.DeXuatCuocHopResponse;
import com.example.demo.dto.response.DeXuatThaoTacResponse;
import com.example.demo.dto.response.KeHoachPheDuyetResponse;
import com.example.demo.dto.response.LichSuKeHoachResponse;
import com.example.demo.dto.response.NguoiCoTheMoiResponse;
import com.example.demo.dto.response.TrinhKeHoachResponse;
import com.example.demo.service.CuocHopService;
import com.example.demo.service.HuyCuocHopService;
import com.example.demo.service.DeXuatCuocHopService;
import com.example.demo.service.PheDuyetKeHoachService;
import com.example.demo.dto.response.ChiTietKeHoachPheDuyetResponse;
import com.example.demo.service.NguoiDungHienTaiService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cuoc-hop")
@RequiredArgsConstructor
public class CuocHopController {

    private final CuocHopService cuocHopService;
    private final HuyCuocHopService huyCuocHopService;
    private final DeXuatCuocHopService deXuatCuocHopService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PheDuyetKeHoachService pheDuyetKeHoachService;
    private final com.example.demo.service.QuyTrinhKeHoachService quyTrinhKeHoachService;

    @GetMapping("/de-xuat/thu-ky-co-the-chon")
    public List<NguoiCoTheMoiResponse> layThuKyCoTheDeXuat(Authentication authentication) {
        return cuocHopService.layThuKyCoTheDeXuat(layNguoiDungId(authentication));
    }

    @PostMapping("/de-xuat")
    public ResponseEntity<DeXuatThaoTacResponse> taoDeXuat(
            @RequestBody TaoDeXuatCuocHopRequest request,
            Authentication authentication) {

        Integer cuocHopId = deXuatCuocHopService.taoDeXuatCuocHop(
                request, layNguoiDungId(authentication));

        String trangThai = deXuatCuocHopService.layTrangThai(cuocHopId);
        String message = "CHO_TIEP_NHAN".equals(trangThai)
                ? "Gửi đề xuất cho thư ký thành công"
                : "Tạo bản nháp đề xuất thành công";

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DeXuatThaoTacResponse(message, cuocHopId, trangThai));
    }

    @PostMapping("/{cuocHopId}/tiep-nhan")
    public ResponseEntity<BuocXuLyCuocHopResponse> tiepNhanDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        deXuatCuocHopService.tiepNhanDeXuat(
                cuocHopId, layNguoiDungId(authentication));

        return ResponseEntity.ok(new BuocXuLyCuocHopResponse(
                "Tiếp nhận đề xuất thành công",
                cuocHopId,
                "DANG_CHUAN_BI",
                "LAP_KE_HOACH"));
    }

    @PostMapping("/{cuocHopId}/gui-de-xuat")
    public ResponseEntity<DeXuatThaoTacResponse> guiDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody TaoDeXuatCuocHopRequest request,
            Authentication authentication) {

        Integer nguoiDungId = layNguoiDungId(authentication);
        deXuatCuocHopService.guiDeXuat(
                cuocHopId, request.getThuKyId(), nguoiDungId);

        return ResponseEntity.ok(new DeXuatThaoTacResponse(
                "Gửi đề xuất cho thư ký thành công",
                cuocHopId,
                "CHO_TIEP_NHAN"));
    }

    @PutMapping("/{cuocHopId}/de-xuat")
    public ResponseEntity<DeXuatThaoTacResponse> capNhatDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody TaoDeXuatCuocHopRequest request,
            Authentication authentication) {

        deXuatCuocHopService.capNhatDeXuat(
                cuocHopId, request, layNguoiDungId(authentication));

        return ResponseEntity.ok(new DeXuatThaoTacResponse(
                "Cập nhật bản nháp đề xuất thành công",
                cuocHopId,
                "NHAP"));
    }

    @GetMapping("/de-xuat")
    public ResponseEntity<List<DeXuatCuocHopResponse>> layDanhSachDeXuat(
            Authentication authentication) {

        return ResponseEntity.ok(deXuatCuocHopService.layDanhSachDeXuat(
                layNguoiDungId(authentication)));
    }

    @GetMapping("/{cuocHopId}/de-xuat")
    public ResponseEntity<DeXuatCuocHopResponse> layChiTietDeXuat(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        return ResponseEntity.ok(deXuatCuocHopService.layChiTietDeXuat(
                cuocHopId, layNguoiDungId(authentication)));
    }

    @GetMapping("/{cuocHopId}/ke-hoach/lua-chon")
    public CuocHopService.LuaChonKeHoach layLuaChonKeHoach(
            @PathVariable("cuocHopId") Integer cuocHopId, Authentication authentication) {
        return cuocHopService.layLuaChonKeHoach(cuocHopId, layNguoiDungId(authentication));
    }

    @PutMapping("/{cuocHopId}/ke-hoach")
    public ResponseEntity<BuocXuLyCuocHopResponse> capNhatKeHoach(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody CapNhatKeHoachCuocHopRequest request,
            Authentication authentication) {

        cuocHopService.capNhatKeHoach(
                cuocHopId, request, layNguoiDungId(authentication));

        return ResponseEntity.ok(new BuocXuLyCuocHopResponse(
                "Cập nhật kế hoạch cuộc họp thành công",
                cuocHopId,
                "DANG_CHUAN_BI",
                "LAP_KE_HOACH"));
    }

    @PostMapping("/{cuocHopId}/ke-hoach/trinh-duyet")
    public ResponseEntity<TrinhKeHoachResponse> trinhKeHoach(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody TrinhKeHoachRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED).body(quyTrinhKeHoachService.trinhKeHoachVaLayKetQua(
                cuocHopId, request, layNguoiDungId(authentication)));
    }

    @PutMapping("/{cuocHopId}/ke-hoach/yeu-cau/{yeuCauPheDuyetId}/phe-duyet")
    public ResponseEntity<BuocXuLyCuocHopResponse> pheDuyetKeHoach(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("yeuCauPheDuyetId") Integer yeuCauPheDuyetId,
            @RequestBody PheDuyetKeHoachRequest request,
            Authentication authentication) {

        boolean daDuyetCuoi = pheDuyetKeHoachService.pheDuyetKeHoach(
                cuocHopId,
                yeuCauPheDuyetId,
                layNguoiDungId(authentication),
                request.getYKien());

        return ResponseEntity.ok(daDuyetCuoi
                ? new BuocXuLyCuocHopResponse(
                "Đã duyệt kế hoạch và xác nhận lịch họp",
                cuocHopId,
                "DA_DUYET",
                "THU_KY_TO_CHUC")
                : new BuocXuLyCuocHopResponse(
                        "Đã đồng ý trình cấp trên; hồ sơ đang chờ thư ký chuyển tiếp",
                        cuocHopId,
                        "DANG_CHUAN_BI",
                        "CHO_GUI_CAP_TREN"));
    }
    
    @PutMapping("/{cuocHopId}/ke-hoach/yeu-cau/{yeuCauPheDuyetId}/tra-lai")
    public ResponseEntity<BuocXuLyCuocHopResponse> traLaiKeHoach(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @PathVariable("yeuCauPheDuyetId") Integer yeuCauPheDuyetId,
            @RequestBody TraLaiKeHoachRequest request,
            Authentication authentication) {

        pheDuyetKeHoachService.traLaiKeHoach(
                cuocHopId,
                yeuCauPheDuyetId,
                layNguoiDungId(authentication),
                request.getLyDo());

        return ResponseEntity.ok(new BuocXuLyCuocHopResponse(
                "Đã trả lại kế hoạch để thư ký chỉnh sửa",
                cuocHopId,
                "DANG_CHUAN_BI",
                "LAP_KE_HOACH"));
    }
    
    @GetMapping("/ke-hoach/cho-duyet")
    public ResponseEntity<List<KeHoachPheDuyetResponse>> layKeHoachChoDuyet(
            Authentication authentication) {

        return ResponseEntity.ok(
                pheDuyetKeHoachService.layDanhSachKeHoachChoDuyet(
                        layNguoiDungId(authentication)));
    }
    
    @GetMapping("/{cuocHopId}/ke-hoach/lan-trinh-hien-tai")
    public ChiTietKeHoachPheDuyetResponse layChiTietKeHoachHienTai(
            @PathVariable("cuocHopId") Integer cuocHopId, Authentication auth) {
        return pheDuyetKeHoachService.layChiTietKeHoachHienTai(cuocHopId, layNguoiDungId(auth));
    }

    @GetMapping("/ke-hoach/cho-duyet/{yeuCauPheDuyetId}")
    public ResponseEntity<ChiTietKeHoachPheDuyetResponse> layChiTietKeHoachChoDuyet(
            @PathVariable("yeuCauPheDuyetId") Integer yeuCauPheDuyetId,
            Authentication authentication) {

        return ResponseEntity.ok(
                pheDuyetKeHoachService.layChiTietKeHoachChoDuyet(
                        yeuCauPheDuyetId,
                        layNguoiDungId(authentication)));
    }
    
    @GetMapping("/ke-hoach/da-duyet")
    public ResponseEntity<List<KeHoachPheDuyetResponse>> layKeHoachDaDuyet(
            Authentication authentication) {

        return ResponseEntity.ok(
                pheDuyetKeHoachService.layDanhSachKeHoachDaDuyet(
                        layNguoiDungId(authentication)));
    }
    
    @GetMapping("/ke-hoach/da-duyet/{yeuCauPheDuyetId}")
    public ResponseEntity<ChiTietKeHoachPheDuyetResponse> layChiTietKeHoachDaDuyet(
    		@PathVariable("yeuCauPheDuyetId") Integer yeuCauPheDuyetId,
            Authentication authentication) {

        return ResponseEntity.ok(
                pheDuyetKeHoachService.layChiTietKeHoachDaDuyet(
                        yeuCauPheDuyetId,
                        layNguoiDungId(authentication)));
    }
    
    @GetMapping("/{cuocHopId}/ke-hoach/lich-su")
    public ResponseEntity<List<LichSuKeHoachResponse>> layLichSuPheDuyetKeHoach(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        return ResponseEntity.ok(
                pheDuyetKeHoachService.layLichSuPheDuyetKeHoach(
                        cuocHopId, layNguoiDungId(authentication)));
    }

    private Integer layNguoiDungId(Authentication authentication) {
        return nguoiDungHienTaiService.layNguoiDungId(authentication);
    }

    @PostMapping("/{cuocHopId}/huy")
    public ResponseEntity<BuocXuLyCuocHopResponse> huyCuocHop(
            @PathVariable("cuocHopId") Integer cuocHopId,
            @RequestBody TraLaiKeHoachRequest request, Authentication authentication) {
        huyCuocHopService.huy(cuocHopId, layNguoiDungId(authentication), request.getLyDo());
        return ResponseEntity.ok(new BuocXuLyCuocHopResponse(
                "Đã hủy cuộc họp", cuocHopId, "DA_HUY", "DA_HUY"));
    }

    @PostMapping("/{cuocHopId}/bat-dau")
    public ResponseEntity<BuocXuLyCuocHopResponse> batDauCuocHop(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        cuocHopService.batDauCuocHop(
                cuocHopId,
                layNguoiDungId(authentication));

        return ResponseEntity.ok(
                new BuocXuLyCuocHopResponse(
                        "Bắt đầu cuộc họp thành công",
                        cuocHopId,
                        "DANG_DIEN_RA",
                        "DANG_HOP"));
    }

    @PostMapping("/{cuocHopId}/ket-thuc")
    public ResponseEntity<BuocXuLyCuocHopResponse> ketThucCuocHop(
            @PathVariable("cuocHopId") Integer cuocHopId,
            Authentication authentication) {

        cuocHopService.ketThucCuocHop(
                cuocHopId,
                layNguoiDungId(authentication));

        return ResponseEntity.ok(
                new BuocXuLyCuocHopResponse(
                        "Kết thúc cuộc họp thành công",
                        cuocHopId,
                        "DA_KET_THUC",
                        "SAU_CUOC_HOP"));
    }
}



