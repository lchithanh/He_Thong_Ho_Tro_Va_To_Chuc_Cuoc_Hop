package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongHop;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.LichHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class XacNhanLichHopService {
    private final LichHopRepository lichHopRepository;
    private final LichSuPheDuyetService lichSuPheDuyetService;
    private final ThongBaoEmailCuocHop thongBaoEmail;

    // Bên gọi giữ khóa cuộc họp và thực hiện thao tác trong cùng giao dịch phê duyệt.
    void xacNhan(CuocHop cuocHop, LichHop lich, NguoiDung nguoiDuyet, YeuCauPheDuyet yeuCau) {
        kiemTraLichDeXuat(cuocHop, lich, HttpStatus.CONFLICT);
        LichHop lichCu = layLichBiThayThe(lich);
        PhongHop phong = lich.getPhongHop();
        if (phong != null) {
            lichHopRepository.khoaPhongHop(phong);
            kiemTraLichDeXuat(cuocHop, lich, HttpStatus.CONFLICT);
        }

        Integer lichCuId = lichCu == null ? lich.getLichHopId() : lichCu.getLichHopId();
        if (phong != null && (!"SAN_SANG".equals(phong.getTrangThai())
                || !lichHopRepository.khoaLichTrungPhong(phong.getPhongHopId(), lich.getLichHopId(),
                        lichCuId, lich.getThoiGianBatDau(), lich.getThoiGianKetThuc()).isEmpty())) {
            throw loi(HttpStatus.CONFLICT, "Phòng họp không còn sẵn sàng hoặc đã có lịch trùng. Hãy đề xuất lại lịch.");
        }

        ThaoTacCuocHop.thucHien(() -> {
            if (lichCu != null) {
                ThaoTacLichHop.ganVoiLichBiThayThe(lich, lichCu);
                ThaoTacLichHop.danhDauDaBiThayThe(lichCu);
            }
            ThaoTacLichHop.xacNhan(lich, nguoiDuyet);
        });
        lichSuPheDuyetService.ghiNhan(cuocHop.getCuocHopId(), lich.getLichHopId(), null,
                nguoiDuyet.getNguoiDungId(), "LICH_HOP", "PHE_DUYET",
                yeuCau == null ? "Lịch họp đã được xác nhận" : "Lịch họp được xác nhận cùng kế hoạch",
                yeuCau);
        thongBaoEmail.xacNhanLich(lich, lichCu != null);
    }

    void kiemTraLichCuaLanTrinh(CuocHop cuocHop, LichHop lich, LichHop lichDaDuyet, HttpStatus status) {
        if (lich == null || !ThaoTacLichHop.daXacNhan(lich)) {
            kiemTraLichTruocKhiTrinh(cuocHop, lich, status);
            return;
        }
        if (lichDaDuyet == null || !lichDaDuyet.getLichHopId().equals(lich.getLichHopId())
                || !lichPhuHop(cuocHop, lich)) {
            throw loi(status, "Lịch đã xác nhận không còn phù hợp. Chọn lịch đề xuất còn hiệu lực.");
        }
    }

    void kiemTraLichTruocKhiTrinh(CuocHop cuocHop, LichHop lich, HttpStatus status) {
        kiemTraLichDeXuat(cuocHop, lich, status);
        LichHop lichCu = layLichBiThayThe(lich);
        PhongHop phong = lich.getPhongHop();
        if (phong != null) {
            Integer lichCuId = lichCu == null ? lich.getLichHopId() : lichCu.getLichHopId();
            if (!"SAN_SANG".equals(phong.getTrangThai())
                    || lichHopRepository.existsLichHopTrungKhiThayThe(phong.getPhongHopId(),
                            lichCuId, lich.getLichHopId(), lich.getThoiGianBatDau(), lich.getThoiGianKetThuc())) {
                throw loi(HttpStatus.CONFLICT, "Phòng họp không còn sẵn sàng hoặc đã có lịch trùng. Hãy đề xuất lại lịch.");
            }
        }
    }

    private void kiemTraLichDeXuat(CuocHop cuocHop, LichHop lich, HttpStatus status) {
        if (lich == null || !ThaoTacLichHop.dangDeXuat(lich) || !lichPhuHop(cuocHop, lich)) {
            throw loi(status, "Chọn lịch đề xuất còn hiệu lực và phù hợp với hình thức họp.");
        }
    }

    private boolean lichPhuHop(CuocHop cuocHop, LichHop lich) {
        return lich.getCuocHop() != null
                && cuocHop.getCuocHopId().equals(lich.getCuocHop().getCuocHopId())
                && lich.getThoiGianBatDau() != null && lich.getThoiGianBatDau().isAfter(LocalDateTime.now())
                && lich.getThoiGianKetThuc() != null && lich.getThoiGianKetThuc().isAfter(lich.getThoiGianBatDau())
                && (!"TRUC_TIEP".equals(cuocHop.getLoaiHinh()) || lich.getPhongHop() != null)
                && (!"TRUC_TUYEN".equals(cuocHop.getLoaiHinh()) || lich.getPhongHop() == null);
    }

    private LichHop layLichBiThayThe(LichHop lich) {
        LichHop lichCu = lich.getLichThayThe();
        Integer cuocHopId = lich.getCuocHop().getCuocHopId();
        if (lichCu == null) {
            return layLichDaXacNhan(cuocHopId);
        }
        if (!cuocHopId.equals(lichCu.getCuocHop().getCuocHopId()) || !ThaoTacLichHop.daXacNhan(lichCu)) {
            throw loi(HttpStatus.CONFLICT, "Lịch cũ được thay thế không còn hợp lệ.");
        }
        return lichCu;
    }

    LichHop layLichDaXacNhan(Integer cuocHopId) {
        return lichHopRepository
                .findFirstByCuocHop_CuocHopIdAndTrangThaiOrderByLichHopIdDesc(cuocHopId, "DA_XAC_NHAN")
                .orElse(null);
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
