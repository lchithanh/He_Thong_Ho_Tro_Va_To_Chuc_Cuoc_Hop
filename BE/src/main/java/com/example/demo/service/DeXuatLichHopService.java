package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.request.CapNhatLichHopRequest;
import com.example.demo.dto.request.TaoLichHopRequest;
import com.example.demo.dto.response.LichHopResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongHop;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.LichHopRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeXuatLichHopService {
    private final LichHopRepository lichHopRepository;
    private final LichHopService lichHopService;
    private final CuocHopRepository cuocHopRepository;

    public LichHopResponse taoLichHop(TaoLichHopRequest request, Integer nguoiDeXuatId) {
        if (request == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu thông tin lịch");
        CuocHop ch = khoaCuocHop(request.cuocHopId());
        lichHopService.kiemTraQuyenDeXuatLich(ch, nguoiDeXuatId);
        kiemTraDuocLapLich(ch);
        lichHopService.kiemTraThoiGian(request.thoiGianBatDau(), request.thoiGianKetThuc());
        NguoiDung nguoiDeXuat = lichHopService.timNguoiDung(nguoiDeXuatId);
        PhongHop phong = null;
        if (request.phongHopId() != null) {
            phong = lichHopService.timPhongHop(request.phongHopId());
            lichHopService.kiemTraTrungLich(request.phongHopId(), -1,
                    request.thoiGianBatDau(), request.thoiGianKetThuc());
        }
        PhongHop phongDaChon = phong;
        LichHop lich = ThaoTacCuocHop.thucHien(() -> ThaoTacLichHop.taoDeXuat(
                ch, phongDaChon, nguoiDeXuat, request.thoiGianBatDau(),
                request.thoiGianKetThuc(), request.lyDoThayDoi()));
        return lichHopService.chuyenDoiResponse(lichHopRepository.save(lich));
    }

    public LichHopResponse capNhatLichHop(Integer lichHopId,
            CapNhatLichHopRequest request, Integer nguoiDungId) {
        if (request == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu thông tin lịch");
        LichHop lich = lichHopService.timLichHop(lichHopId);
        CuocHop ch = khoaCuocHop(lich.getCuocHop().getCuocHopId());
        lich = lichHopService.timLichHopVaKhoa(lichHopId);
        lichHopService.kiemTraQuyenDeXuatLich(ch, nguoiDungId);
        kiemTraDuocLapLich(ch);
        if (!ThaoTacLichHop.coTheCapNhat(lich)) throw loi(HttpStatus.CONFLICT, "Chỉ sửa lịch đang đề xuất");
        lichHopService.kiemTraNguoiTaoLich(lich, nguoiDungId);
        lichHopService.kiemTraLichKhongChoDuyetKeHoach(lichHopId);
        lichHopService.kiemTraThoiGian(request.thoiGianBatDau(), request.thoiGianKetThuc());
        PhongHop phong = null;
        if (request.phongHopId() != null) {
            phong = lichHopService.timPhongHop(request.phongHopId());
            lichHopService.kiemTraTrungLich(request.phongHopId(), lichHopId,
                    request.thoiGianBatDau(), request.thoiGianKetThuc());
        }
        LichHop lichDaKhoa = lich;
        PhongHop phongDaChon = phong;
        ThaoTacCuocHop.thucHien(() -> ThaoTacLichHop.capNhatLich(lichDaKhoa, phongDaChon,
                request.thoiGianBatDau(), request.thoiGianKetThuc(), request.lyDoThayDoi()));
        return lichHopService.chuyenDoiResponse(lichHopRepository.save(lich));
    }

    public LichHopResponse thayTheLichHop(Integer lichHopCuId, Integer lichHopMoiId, Integer nguoiDungId) {
        if (lichHopCuId == null || lichHopMoiId == null || lichHopCuId.equals(lichHopMoiId)) {
            throw loi(HttpStatus.BAD_REQUEST, "Cần hai mã lịch khác nhau");
        }
        LichHop cu = lichHopService.timLichHop(lichHopCuId);
        CuocHop ch = khoaCuocHop(cu.getCuocHop().getCuocHopId());
        LichHop lichMoiBanDau = lichHopService.timLichHop(lichHopMoiId);
        if (!ch.getCuocHopId().equals(lichMoiBanDau.getCuocHop().getCuocHopId())) {
            throw loi(HttpStatus.BAD_REQUEST, "Hai lịch phải thuộc cùng cuộc họp");
        }
        // Khóa lịch theo thứ tự ID cố định để hạn chế deadlock.
        LichHop first = lichHopService.timLichHopVaKhoa(Math.min(lichHopCuId, lichHopMoiId));
        LichHop second = lichHopService.timLichHopVaKhoa(Math.max(lichHopCuId, lichHopMoiId));
        cu = first.getLichHopId().equals(lichHopCuId) ? first : second;
        LichHop moi = first.getLichHopId().equals(lichHopMoiId) ? first : second;
        if (!ch.getCuocHopId().equals(moi.getCuocHop().getCuocHopId())) {
            throw loi(HttpStatus.BAD_REQUEST, "Hai lịch phải thuộc cùng cuộc họp");
        }
        lichHopService.kiemTraQuyenDeXuatLich(ch, nguoiDungId);
        kiemTraDuocLapLich(ch);
        if (!ThaoTacLichHop.daXacNhan(cu) || !ThaoTacLichHop.coTheXacNhan(moi)) {
            throw loi(HttpStatus.CONFLICT, "Lịch cũ phải đã xác nhận, lịch mới phải đang đề xuất");
        }
        if (moi.getLichThayThe() != null
                && !lichHopCuId.equals(moi.getLichThayThe().getLichHopId())) {
            throw loi(HttpStatus.CONFLICT, "Lịch mới đã gắn với một lịch khác");
        }
        lichHopService.kiemTraNguoiTaoLich(moi, nguoiDungId);
        lichHopService.kiemTraLichKhongChoDuyetKeHoach(lichHopMoiId);
        lichHopService.kiemTraThoiGian(moi.getThoiGianBatDau(), moi.getThoiGianKetThuc());
        if (moi.getPhongHop() != null) {
            lichHopService.kiemTraTrungLichKhiThayThe(moi.getPhongHop().getPhongHopId(),
                    lichHopCuId, lichHopMoiId, moi.getThoiGianBatDau(), moi.getThoiGianKetThuc());
        }
        LichHop lichCuDaKhoa = cu;
        ThaoTacCuocHop.thucHien(() -> ThaoTacLichHop.ganVoiLichBiThayThe(moi, lichCuDaKhoa));
        return lichHopService.chuyenDoiResponse(lichHopRepository.save(moi));
    }

    private CuocHop khoaCuocHop(Integer id) {
        if (id == null) throw loi(HttpStatus.BAD_REQUEST, "Thiếu mã cuộc họp");
        return cuocHopRepository.findByIdForUpdate(id)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
    }
    private void kiemTraDuocLapLich(CuocHop ch) {
        if (!ThaoTacCuocHop.coTheLapLich(ch) && !ThaoTacCuocHop.dangLapKeHoach(ch)) throw loi(HttpStatus.CONFLICT,
                "Chỉ lập lịch khi đang chuẩn bị hoặc tổ chức cuộc họp");
    }
    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}



