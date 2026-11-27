package com.example.demo.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.LichSuPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.LichHopRepository;
import com.example.demo.repository.LichSuPheDuyetRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HuyCuocHopService {
    private final CuocHopRepository cuocHopRepository;
    private final LichHopRepository lichHopRepository;
    private final YeuCauPheDuyetRepository yeuCauRepository;
    private final LichSuPheDuyetRepository lichSuRepository;
    private final LichSuPheDuyetService lichSuService;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final QuyTrinhKeHoachService quyTrinhKeHoachService;
    private final PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final ThongBaoEmailCuocHop thongBaoEmail;
    private final ThongBaoService thongBao;

    boolean duocHuy(CuocHop cuocHop, Integer nguoiDungId) {
        return coTheHuy(cuocHop) && coQuyenHuy(cuocHop, nguoiDungId);
    }

    String lyDoDaHuy(CuocHop cuocHop) {
        if (!"DA_HUY".equals(cuocHop.getTrangThai())) return null;
        return lichSuRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiPheDuyetAndHanhDongOrderByLichSuPheDuyetIdDesc(
                        cuocHop.getCuocHopId(), "CUOC_HOP", "HUY")
                .map(LichSuPheDuyet::getYKien).orElse(null);
    }

    @Transactional
    public void huy(Integer cuocHopId, Integer nguoiDungId, String lyDo) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (lyDo == null || lyDo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng nhập lý do hủy cuộc họp.");
        }
        CuocHop cuocHop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp."));
        if (!coTheHuy(cuocHop)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cuộc họp không còn ở trạng thái có thể hủy.");
        }
        if (!coQuyenHuy(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền hủy cuộc họp này.");
        }

        String lyDoHuy = lyDo.strip();
        List<LichHop> cacLich = lichHopRepository.findByCuocHop_CuocHopIdOrderByThoiGianBatDauAsc(cuocHopId);
        LichHop lichDaCongBo = cacLich.stream()
                .filter(ThaoTacLichHop::daXacNhan)
                .findFirst()
                .orElseGet(() -> cacLich.stream()
                        .filter(lich -> lich.getThoiGianXacNhan() != null
                                && (ThaoTacLichHop.daHuy(lich) || ThaoTacLichHop.daBiThayThe(lich)))
                        .max(Comparator.comparing(LichHop::getThoiGianXacNhan))
                        .orElse(null));
        for (LichHop lich : cacLich) {
            if (ThaoTacLichHop.coTheHuy(lich)) {
                ThaoTacLichHop.huyLich(lich, lyDoHuy);
                lichSuService.ghiNhan(cuocHopId, lich.getLichHopId(), null, nguoiDungId,
                        "LICH_HOP", "HUY", lyDoHuy);
            }
        }
        for (YeuCauPheDuyet yc : layLanTrinh(cuocHopId)) {
            if (yc.getTrangThai() == TrangThai.CHO_DUYET) {
                ThaoTacYeuCauPheDuyet.huy(yc);
                lichSuService.ghiNhan(cuocHopId, null, null, nguoiDungId,
                        "KE_HOACH", "HUY", lyDoHuy, yc);
                capNhatRealtime.baoCho(
                        yc.getNguoiTrinh() == null ? null : yc.getNguoiTrinh().getNguoiDungId(),
                        yc.getNguoiDuyet() == null ? null : yc.getNguoiDuyet().getNguoiDungId(),
                        yc.getThuKyTiepNhan() == null ? null : yc.getThuKyTiepNhan().getNguoiDungId());
            }
        }
        ThaoTacCuocHop.huyCuocHop(cuocHop);
        lichSuService.ghiNhan(cuocHopId, null, null, nguoiDungId, "CUOC_HOP", "HUY", lyDoHuy);
        thongBao.gui(cuocHop, nguoiDungId, "CUOC_HOP_HUY", "Cuộc họp đã bị hủy",
                cuocHop.getTieuDe() + " · " + lyDoHuy, capNhatRealtime.nguoiDocVanBan(cuocHop));
        capNhatRealtime.baoCuocHop(cuocHop, nguoiDungId);
        if (lichDaCongBo != null) {
            thongBaoEmail.huyCuocHop(lichDaCongBo, lyDoHuy);
        }
    }

    private boolean coTheHuy(CuocHop cuocHop) {
        return ThaoTacCuocHop.coTheHuy(cuocHop)
                && ThaoTacCuocHop.coTheTiepTucXuLy(cuocHop)
                && !ThaoTacCuocHop.daKetThuc(cuocHop)
                && !"DANG_DIEN_RA".equals(cuocHop.getTrangThai());
    }

    private boolean coQuyenHuy(CuocHop cuocHop, Integer nguoiDungId) {
        Integer cuocHopId = cuocHop.getCuocHopId();
        YeuCauPheDuyet lanDuyet = phanCongXuLyKeHoachService.layLanDuyetCoLichDaXacNhan(cuocHopId);
        if (lanDuyet == null && ThaoTacCuocHop.coTheLapLich(cuocHop)) {
            lanDuyet = phanCongXuLyKeHoachService.layLanTrinhMoiNhat(cuocHopId);
        }
        if (lanDuyet != null && lanDuyet.getTrangThai() == TrangThai.DA_DUYET) {
            return quyTrinhKeHoachService.coQuyenXuLyQuyetDinh(lanDuyet, nguoiDungId);
        }
        return ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDungId);
    }

    private List<YeuCauPheDuyet> layLanTrinh(Integer cuocHopId) {
        return yeuCauRepository.findByCuocHop_CuocHopIdAndLoaiYeuCauOrderByThoiGianTrinhDescYeuCauPheDuyetIdDesc(
                cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP);
    }
}
