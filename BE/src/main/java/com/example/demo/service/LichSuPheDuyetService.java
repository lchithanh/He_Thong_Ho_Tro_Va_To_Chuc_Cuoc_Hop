package com.example.demo.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.BienBanCuocHop;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.LichSuPheDuyet;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.LichHopRepository;
import com.example.demo.repository.LichSuPheDuyetRepository;
import com.example.demo.repository.NguoiDungRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LichSuPheDuyetService {

    private final LichSuPheDuyetRepository lichSuPheDuyetRepository;
    private final CuocHopRepository cuocHopRepository;
    private final LichHopRepository lichHopRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BienBanCuocHopRepository bienBanCuocHopRepository;

    public LichSuPheDuyet ghiNhan(
            Integer cuocHopId,
            Integer lichHopId,
            Integer bienBanId,
            Integer nguoiPheDuyetId,
            String loaiPheDuyet,
            String hanhDong,
            String yKien) {

        return ghiNhan(
                cuocHopId,
                lichHopId,
                bienBanId,
                nguoiPheDuyetId,
                loaiPheDuyet,
                hanhDong,
                yKien,
                null);
    }

    public LichSuPheDuyet ghiNhan(
            Integer cuocHopId,
            Integer lichHopId,
            Integer bienBanId,
            Integer nguoiPheDuyetId,
            String loaiPheDuyet,
            String hanhDong,
            String yKien,
            YeuCauPheDuyet yeuCauPheDuyet) {

        if (cuocHopId == null) {
            throw new RuntimeException("Mã cuộc họp không được để trống");
        }

        if (nguoiPheDuyetId == null) {
            throw new RuntimeException("Mã người phê duyệt không được để trống");
        }

        if (loaiPheDuyet == null || loaiPheDuyet.isBlank()) {
            throw new RuntimeException("Loại phê duyệt không được để trống");
        }

        if (hanhDong == null || hanhDong.isBlank()) {
            throw new RuntimeException("Hành động phê duyệt không được để trống");
        }

        loaiPheDuyet = loaiPheDuyet.trim().toUpperCase(Locale.ROOT);
        hanhDong = hanhDong.trim().toUpperCase(Locale.ROOT);

        switch (loaiPheDuyet) {
            case "KE_HOACH", "LICH_HOP", "BIEN_BAN", "KET_LUAN" -> {
                // Loại phê duyệt hợp lệ.
            }
            default -> throw new RuntimeException("Loại phê duyệt không hợp lệ");
        }

        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc họp"));

        NguoiDung nguoiPheDuyet = nguoiDungRepository.findById(nguoiPheDuyetId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy người phê duyệt"));

        LichHop lichHop = null;

        if (lichHopId != null) {
            lichHop = lichHopRepository.findById(lichHopId)
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy lịch họp"));

            if (!cuocHopId.equals(lichHop.getCuocHop().getCuocHopId())) {
                throw new RuntimeException(
                        "Lịch họp không thuộc cuộc họp được ghi nhận");
            }
        }

        BienBanCuocHop bienBan = null;

        if (bienBanId != null) {
            bienBan = bienBanCuocHopRepository.findById(bienBanId)
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy biên bản"));

            if (!cuocHopId.equals(bienBan.getCuocHop().getCuocHopId())) {
                throw new RuntimeException(
                        "Biên bản không thuộc cuộc họp được ghi nhận");
            }
        }

        switch (loaiPheDuyet) {
            case "KE_HOACH" -> {
                if (lichHop != null || bienBan != null) {
                    throw new RuntimeException(
                            "Lịch sử kế hoạch chỉ gắn trực tiếp với cuộc họp");
                }
            }
            case "LICH_HOP" -> {
                if (lichHop == null) {
                    throw new RuntimeException(
                            "Phê duyệt lịch họp phải có mã lịch họp");
                }
            }
            case "BIEN_BAN" -> {
                if (bienBan == null) {
                    throw new RuntimeException(
                            "Phê duyệt biên bản phải có mã biên bản");
                }
            }
        }

        switch (hanhDong) {
        case "TRINH_DUYET", "TRINH_CAP_TREN", "DONG_Y_TRINH_CAP_TREN",
                "TIEP_NHAN", "PHE_DUYET", "TU_CHOI",
                "YEU_CAU_CHINH_SUA", "HUY" -> {
            // Hành động phê duyệt hợp lệ
        }
        default -> throw new RuntimeException(
                "Hành động phê duyệt không hợp lệ");
    }

        if (yeuCauPheDuyet != null
                && !cuocHopId.equals(
                        yeuCauPheDuyet.getCuocHop().getCuocHopId())) {
            throw new IllegalArgumentException(
                    "Yêu cầu phê duyệt không thuộc cuộc họp được ghi nhận");
        }

        LichSuPheDuyet lichSu = new LichSuPheDuyet();
        lichSu.setCuocHop(cuocHop);
        lichSu.setLichHop(lichHop);
        lichSu.setBienBan(bienBan);
        lichSu.setNguoiPheDuyet(nguoiPheDuyet);
        lichSu.setLoaiPheDuyet(loaiPheDuyet);
        lichSu.setHanhDong(hanhDong);
        lichSu.setYeuCauPheDuyet(yeuCauPheDuyet);
        lichSu.setYKien(yKien == null || yKien.isBlank() ? null : yKien.trim());

        return lichSuPheDuyetRepository.save(lichSu);
    }

    @Transactional(readOnly = true)
    public List<LichSuPheDuyet> layLichSuTheoCuocHop(Integer cuocHopId) {
        if (cuocHopId == null) {
            throw new RuntimeException("Mã cuộc họp không được để trống");
        }

        if (!cuocHopRepository.existsById(cuocHopId)) {
            throw new RuntimeException("Không tìm thấy cuộc họp");
        }

        return lichSuPheDuyetRepository.findByCuocHop_CuocHopId(cuocHopId);
    }

    @Transactional(readOnly = true)
    public List<LichSuPheDuyet> layLichSuTheoNguoiPheDuyet(Integer nguoiDungId) {
        if (nguoiDungId == null) {
            throw new RuntimeException("Mã người dùng không được để trống");
        }

        if (!nguoiDungRepository.existsById(nguoiDungId)) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return lichSuPheDuyetRepository
                .findByNguoiPheDuyet_NguoiDungId(nguoiDungId);
    }

    @Transactional(readOnly = true)
    public List<LichSuPheDuyet> layLichSuTheoLoaiPheDuyet(String loaiPheDuyet) {
        if (loaiPheDuyet == null || loaiPheDuyet.isBlank()) {
            throw new RuntimeException("Vui lòng cung cấp loại phê duyệt");
        }

        return lichSuPheDuyetRepository
                .findByLoaiPheDuyet(loaiPheDuyet.trim().toUpperCase(Locale.ROOT));
    }
}