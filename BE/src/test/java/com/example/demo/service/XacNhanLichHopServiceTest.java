package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongHop;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.LichHopRepository;

class XacNhanLichHopServiceTest {
    private final LichHopRepository lichHopRepository = mock(LichHopRepository.class);
    private final LichSuPheDuyetService lichSuPheDuyetService = mock(LichSuPheDuyetService.class);
    private final ThongBaoEmailCuocHop thongBaoEmail =
            mock(ThongBaoEmailCuocHop.class);

    private final XacNhanLichHopService service = new XacNhanLichHopService(
            lichHopRepository, lichSuPheDuyetService, thongBaoEmail);
    @Test
    void finalApprovalConfirmsTheSubmittedScheduleAndReplacesThePreviousOne() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setCuocHopId(1);
        cuocHop.setLoaiHinh("TRUC_TUYEN");
        LichHop lichMoi = taoLichDeXuat(cuocHop, LocalDateTime.now().plusDays(1));
        LichHop lichCu = new LichHop();
        lichCu.setLichHopId(9);
        lichCu.setTrangThai("DA_XAC_NHAN");
        when(lichHopRepository.findFirstByCuocHop_CuocHopIdAndTrangThaiOrderByLichHopIdDesc(
                1, "DA_XAC_NHAN")).thenReturn(Optional.of(lichCu));

        NguoiDung nguoiDuyet = new NguoiDung();
        nguoiDuyet.setNguoiDungId(7);
        YeuCauPheDuyet yeuCau = new YeuCauPheDuyet();

        service.xacNhan(cuocHop, lichMoi, nguoiDuyet, yeuCau);

        assertEquals("DA_XAC_NHAN", lichMoi.getTrangThai());
        assertSame(nguoiDuyet, lichMoi.getNguoiXacNhan());
        assertEquals("THAY_THE", lichCu.getTrangThai());
        assertSame(lichCu, lichMoi.getLichThayThe());
        verify(lichSuPheDuyetService).ghiNhan(1, 11, null, 7, "LICH_HOP", "PHE_DUYET",
                "Lịch họp được xác nhận cùng kế hoạch", yeuCau);
    }

    @Test
    void expiredScheduleStaysUnconfirmed() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setCuocHopId(1);
        cuocHop.setLoaiHinh("TRUC_TUYEN");
        LichHop lich = taoLichDeXuat(cuocHop, LocalDateTime.now().minusMinutes(5));

        ResponseStatusException loi = assertThrows(ResponseStatusException.class,
                () -> service.xacNhan(cuocHop, lich, new NguoiDung(), new YeuCauPheDuyet()));

        assertEquals(HttpStatus.CONFLICT, loi.getStatusCode());
        assertEquals("DE_XUAT", lich.getTrangThai());
        verifyNoInteractions(lichSuPheDuyetService);
    }

    @Test
    void newlyOccupiedRoomBlocksConfirmation() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setCuocHopId(1);
        cuocHop.setLoaiHinh("TRUC_TIEP");
        LichHop lich = taoLichDeXuat(cuocHop, LocalDateTime.now().plusDays(1));
        PhongHop phong = new PhongHop();
        phong.setPhongHopId(4);
        phong.setTrangThai("SAN_SANG");
        lich.setPhongHop(phong);
        when(lichHopRepository.khoaLichTrungPhong(4, 11, 11,
                lich.getThoiGianBatDau(), lich.getThoiGianKetThuc())).thenReturn(List.of(12));

        ResponseStatusException loi = assertThrows(ResponseStatusException.class,
                () -> service.xacNhan(cuocHop, lich, new NguoiDung(), new YeuCauPheDuyet()));

        assertEquals(HttpStatus.CONFLICT, loi.getStatusCode());
        assertEquals("DE_XUAT", lich.getTrangThai());
        verify(lichHopRepository).khoaPhongHop(phong);
        verifyNoInteractions(lichSuPheDuyetService);
    }

    private LichHop taoLichDeXuat(CuocHop cuocHop, LocalDateTime batDau) {
        LichHop lich = new LichHop();
        lich.setCuocHop(cuocHop);
        lich.setLichHopId(11);
        lich.setTrangThai("DE_XUAT");
        lich.setThoiGianBatDau(batDau);
        lich.setThoiGianKetThuc(batDau.plusHours(1));
        return lich;
    }
}
