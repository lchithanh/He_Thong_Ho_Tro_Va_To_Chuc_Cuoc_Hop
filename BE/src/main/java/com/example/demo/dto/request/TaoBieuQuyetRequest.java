package com.example.demo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TaoBieuQuyetRequest(
        String tieuDe,
        String noiDung,
        String cheDoBieuQuyet,
        BigDecimal tyLeThamGiaToiThieu,
        LocalDateTime thoiGianBatDau,
        LocalDateTime thoiGianKetThuc,
        List<Integer> nguoiDuocBieuQuyetIds,
        Integer nghiQuyetId,
        String phuongThuc,
        List<String> phuongAn,
        Integer thoiLuongPhut,
        Boolean moNgay
) {
    public TaoBieuQuyetRequest(String tieuDe, String noiDung, String cheDoBieuQuyet,
            BigDecimal tyLeThamGiaToiThieu, LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc,
            List<Integer> nguoiDuocBieuQuyetIds, Integer nghiQuyetId, String phuongThuc, List<String> phuongAn) {
        this(tieuDe, noiDung, cheDoBieuQuyet, tyLeThamGiaToiThieu, thoiGianBatDau, thoiGianKetThuc,
                nguoiDuocBieuQuyetIds, nghiQuyetId, phuongThuc, phuongAn, null, false);
    }
}