package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.TaoBieuQuyetRequest;
import com.example.demo.entity.BieuQuyet;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LuaChonBieuQuyet;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhieuBieuQuyet;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;
import com.example.demo.repository.BieuQuyetRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.LuaChonBieuQuyetRepository;
import com.example.demo.repository.PhieuBieuQuyetRepository;
import com.example.demo.repository.ThanhVienBieuQuyetRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;

class BieuQuyetPhuongAnServiceTest {
    private final CuocHopRepository cuocHopRepository = mock(CuocHopRepository.class);
    private final BieuQuyetRepository bieuQuyetRepository = mock(BieuQuyetRepository.class);
    private final LuaChonBieuQuyetRepository luaChonRepository = mock(LuaChonBieuQuyetRepository.class);
    private final NguoiDungHienTaiService nguoiDungService = mock(NguoiDungHienTaiService.class);
    private final PhieuBieuQuyetRepository phieuRepository = mock(PhieuBieuQuyetRepository.class);
    private final ThanhVienCuocHopRepository thanhVienRepository = mock(ThanhVienCuocHopRepository.class);
    private final ThanhVienBieuQuyetRepository cuTriRepository = mock(ThanhVienBieuQuyetRepository.class);
    private final BieuQuyetService service = new BieuQuyetService(mock(CapNhatHoSoRealtime.class),
            mock(NghiQuyetService.class), cuocHopRepository, bieuQuyetRepository, luaChonRepository,
            nguoiDungService, phieuRepository, thanhVienRepository, cuTriRepository);

    private final NguoiDung chuTri = new NguoiDung();
    private final CuocHop cuocHop = new CuocHop();
    private final ThanhVienCuocHop thanhVien = new ThanhVienCuocHop();
    private final LocalDateTime batDau = LocalDateTime.now().minusHours(1);
    private final LocalDateTime ketThuc = batDau.plusHours(3);

    @BeforeEach
    void moCuocHop() {
        chuTri.setNguoiDungId(2);
        cuocHop.setCuocHopId(1);
        cuocHop.setNguoiChuTri(chuTri);
        cuocHop.setThoiGianBatDauThucTe(batDau);
        when(cuocHopRepository.findByIdForUpdate(1)).thenReturn(Optional.of(cuocHop));
        when(nguoiDungService.layTaiKhoanHoatDong(2)).thenReturn(chuTri);

        var id = new ThanhVienCuocHopId();
        id.setCuocHopId(1);
        id.setNguoiDungId(2);
        thanhVien.setId(id);
        thanhVien.setTrangThaiThamDu("CO_MAT");
        when(thanhVienRepository.findByIdForUpdate(id)).thenReturn(Optional.of(thanhVien));
        when(thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(1))
                .thenReturn(List.of(thanhVien));
    }

    @Test
    void luuCacPhuongAnTheoThuTuDaNhap() {
        when(bieuQuyetRepository.save(any(BieuQuyet.class))).thenAnswer(yeuCau -> {
            BieuQuyet banNhap = yeuCau.getArgument(0);
            assertEquals("CHON_MOT", banNhap.getPhuongThuc());
            banNhap.setBieuQuyetId(5);
            return banNhap;
        });

        assertEquals(5, service.taoBieuQuyet(1, yeuCau(List.of(" Cách A ", "Cách B", "Cách C")), 2));

        ArgumentCaptor<List<LuaChonBieuQuyet>> cacPhuongAn = ArgumentCaptor.captor();
        verify(luaChonRepository).saveAll(cacPhuongAn.capture());
        assertEquals(List.of("Cách A", "Cách B", "Cách C"),
                cacPhuongAn.getValue().stream().map(LuaChonBieuQuyet::getNoiDungLuaChon).toList());
        assertEquals(List.of(1, 2, 3),
                cacPhuongAn.getValue().stream().map(LuaChonBieuQuyet::getThuTuHienThi).toList());
    }

    @Test
    void tuChoiPhuongAnTrungSauKhiChuanHoaUnicodeVaKhoangTrang() {
        var loi = assertThrows(ResponseStatusException.class,
                () -> service.taoBieuQuyet(1, yeuCau(List.of("Cách A", "  CA\u0301CH A ")), 2));
        assertEquals(HttpStatus.BAD_REQUEST, loi.getStatusCode());
        verify(bieuQuyetRepository, never()).save(any());
    }

    @Test
    void ghiMotPhuongAnVaTuChoiGuiPhieuLanHai() {
        moPhienChonMot();
        LuaChonBieuQuyet luaChon = new LuaChonBieuQuyet();
        luaChon.setLuaChonId(9);
        when(luaChonRepository.findByLuaChonIdAndBieuQuyet_BieuQuyetId(9, 5))
                .thenReturn(Optional.of(luaChon));

        service.guiPhieuBieuQuyet(1, 5, 9, null, 2);

        ArgumentCaptor<PhieuBieuQuyet> phieu = ArgumentCaptor.captor();
        verify(phieuRepository).save(phieu.capture());
        assertEquals(9, phieu.getValue().getLuaChonId());
        when(phieuRepository.findFirstByBieuQuyet_BieuQuyetIdAndNguoiDung_NguoiDungId(5, 2))
                .thenReturn(Optional.of(phieu.getValue()));

        var loi = assertThrows(ResponseStatusException.class,
                () -> service.guiPhieuBieuQuyet(1, 5, 9, null, 2));
        assertEquals(HttpStatus.CONFLICT, loi.getStatusCode());
    }

    @Test
    void tuChoiPhuongAnKhongThuocPhien() {
        moPhienChonMot();
        var loi = assertThrows(ResponseStatusException.class,
                () -> service.guiPhieuBieuQuyet(1, 5, 10, null, 2));
        assertEquals(HttpStatus.BAD_REQUEST, loi.getStatusCode());
        verify(phieuRepository, never()).save(any());
    }

    private TaoBieuQuyetRequest yeuCau(List<String> phuongAn) {
        return new TaoBieuQuyetRequest("Chọn cách triển khai", null, "CONG_KHAI",
                null, batDau, ketThuc, List.of(2), null, "CHON_MOT", phuongAn);
    }

    private void moPhienChonMot() {
        var phien = new BieuQuyet();
        phien.setBieuQuyetId(5);
        phien.setCuocHop(cuocHop);
        phien.setPhuongThuc("CHON_MOT");
        phien.setTrangThai("DANG_MO");
        phien.setThoiGianBatDau(batDau);
        phien.setThoiGianKetThuc(ketThuc);
        when(bieuQuyetRepository.findByIdAndCuocHopIdForUpdate(5, 1)).thenReturn(Optional.of(phien));
        when(cuTriRepository.demThanhVienTrongLuot(5, 2)).thenReturn(1L);
    }
}
