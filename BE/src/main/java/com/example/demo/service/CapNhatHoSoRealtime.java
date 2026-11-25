package com.example.demo.service;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.NguoiDungVaiTroRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CapNhatHoSoRealtime {

    private final SimpMessagingTemplate kenhGui;
    private final ApplicationEventPublisher suKien;
    private final ThanhVienCuocHopRepository thanhVienRepository;
    private final YeuCauPheDuyetRepository yeuCauRepository;
    private final SimpUserRegistry nguoiDungDangKetNoi;
    private final NguoiDungVaiTroRepository vaiTroRepository;
    
    public void baoCho(Integer... nguoiNhanIds) {
        baoCho(Arrays.asList(nguoiNhanIds), false);
    }

    void baoCho(Collection<Integer> nguoiNhanIds, boolean laKeHoach) {
        Set<Integer> ids = new HashSet<>(nguoiNhanIds);
        ids.remove(null);
        if (!ids.isEmpty()) {
            suKien.publishEvent(new DuLieuThayDoi(Set.copyOf(ids), laKeHoach));
        }
    }

    public void baoNguoiPhuTrach(CuocHop cuocHop, Integer... nguoiNhanThem) {
        Set<Integer> ids = nguoiPhuTrach(cuocHop);
        ids.addAll(Arrays.asList(nguoiNhanThem));
        baoCho(ids, false);
    }

    public void baoBuocDeXuat(CuocHop cuocHop, Integer nguoiThaoTacId) {
        Set<Integer> ids = nguoiPhuTrach(cuocHop);
        ids.add(nguoiThaoTacId);
        baoBuocDeXuat(ids);
    }

    void baoBuocDeXuat(Collection<Integer> nguoiNhanIds) {
        var ketNoi = layIdDangKetNoi();
        Set<Integer> ids = new HashSet<>(nguoiNhanIds);
        if (!ketNoi.isEmpty()) {
            ids.addAll(vaiTroRepository.locQuanTriVienHoatDong(ketNoi));
        }
        baoCho(ids, true);
    }

    public void baoCuocHop(CuocHop cuocHop, Integer nguoiThaoTacId) {
        Set<Integer> ids = nguoiPhuTrach(cuocHop);
        ids.add(nguoiThaoTacId);
        for (var thanhVien : thanhVienRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHop.getCuocHopId())) {
            if ("CHO_PHAN_HOI".equals(thanhVien.getTrangThaiMoi())
                    || "DA_XAC_NHAN".equals(thanhVien.getTrangThaiMoi())) {
                ids.add(thanhVien.getId().getNguoiDungId());
            }
        }
        baoCho(ids, false);
    }

    NguoiDung[] nguoiDocVanBan(CuocHop cuocHop) {
        var nguoiNhan = new ArrayList<>(thanhVienRepository
                .findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHop.getCuocHopId())
                .stream().map(thanhVien -> thanhVien.getNguoiDung()).toList());
        nguoiNhan.add(cuocHop.getThuKyCuocHop());
        yeuCauRepository.findFirstByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThaiOrderByYeuCauPheDuyetIdDesc(
                cuocHop.getCuocHopId(), LoaiYeuCau.KE_HOACH_CUOC_HOP, TrangThai.DA_DUYET)
                .ifPresent(yc -> nguoiNhan.add(yc.getThuKyBanGiao()));
        return nguoiNhan.toArray(NguoiDung[]::new);
    }

    private Set<Integer> nguoiPhuTrach(CuocHop cuocHop) {
        Set<Integer> ids = new HashSet<>();
        themId(ids, cuocHop.getNguoiDeXuat());
        themId(ids, cuocHop.getNguoiChuTri());
        themId(ids, cuocHop.getThuKyCuocHop());
        for (var thuKy : thanhVienRepository.findByCuocHop_CuocHopIdAndVaiTroTaiCuocHop(
                cuocHop.getCuocHopId(), "THU_KY")) {
            ids.add(thuKy.getId().getNguoiDungId());
        }
        return ids;
    }

    private void themId(Set<Integer> ids, NguoiDung nguoiDung) {
        if (nguoiDung != null) {
            ids.add(nguoiDung.getNguoiDungId());
        }
    }

    public record DuLieuThayDoi(Set<Integer> nguoiNhanIds, boolean laKeHoach) {}

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void guiTinHieu(DuLieuThayDoi thayDoi) {
        String tinHieu = thayDoi.laKeHoach() ? "KE_HOACH_CAP_NHAT" : "DU_LIEU_CAP_NHAT";
        for (Integer nguoiNhanId : thayDoi.nguoiNhanIds()) {
            try {
                kenhGui.convertAndSendToUser(nguoiNhanId.toString(), "/queue/cap-nhat", tinHieu);
            } catch (MessagingException ex) {
                log.warn("Không gửi được tín hiệu cập nhật cho tài khoản {}", nguoiNhanId, ex);
            }
        }
    }
    
    public void baoDanhMucPhongVaThietBiThayDoi() {
        baoCho(layIdDangKetNoi(), false);
    }

    public void baoDanhMucNhomThayDoi() {
        baoCho(layIdDangKetNoi(), false);
    }

    private List<Integer> layIdDangKetNoi() {
        return nguoiDungDangKetNoi.getUsers().stream()
                .map(nguoiDung -> Integer.valueOf(nguoiDung.getName()))
                .toList();
    }
}
