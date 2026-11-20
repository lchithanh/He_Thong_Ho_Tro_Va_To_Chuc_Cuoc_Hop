package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.NoiDungThuMoi;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;
import com.example.demo.service.ThongBaoEmailCuocHop.EmailCanGui;
import org.mockito.ArgumentCaptor;

class ThanhVienCuocHopServiceTest {
    private static final int HOP_ID = 10;
    private static final int THU_KY_ID = 2;
    private static final int THANH_VIEN_ID = 3;
    private static final NoiDungThuMoi THU_MOI = new NoiDungThuMoi(
            "Mời dự họp giao ban", "Kính mời anh/chị tham dự.\nVui lòng đọc trước báo cáo.");

    private final CuocHopRepository cuocHopRepository = mock(CuocHopRepository.class);
    private final ThanhVienCuocHopRepository thanhVienRepository = mock(ThanhVienCuocHopRepository.class);
    private final NguoiDungHienTaiService nguoiDungHienTai = mock(NguoiDungHienTaiService.class);
    private final PhanQuyenCuocHopService phanQuyen = mock(PhanQuyenCuocHopService.class);
    private final PhanCongXuLyKeHoachService phanCong = mock(PhanCongXuLyKeHoachService.class);
    private final ThongBaoService thongBao = mock(ThongBaoService.class);
    private final NguoiDungRepository nguoiDungRepository = mock(NguoiDungRepository.class);
    private final YeuCauPheDuyetRepository yeuCauRepository = mock(YeuCauPheDuyetRepository.class);
    private final ApplicationEventPublisher suKien = mock(ApplicationEventPublisher.class);
    private final ThanhVienCuocHopService service = new ThanhVienCuocHopService(
            cuocHopRepository, nguoiDungRepository, thanhVienRepository,
            nguoiDungHienTai, phanQuyen, phanCong, yeuCauRepository,
            mock(CapNhatHoSoRealtime.class), thongBao, new ThongBaoEmailCuocHop(true, suKien, thanhVienRepository,
                    new ThongTinThuMoiDaDuyet(tools.jackson.databind.json.JsonMapper.builder().findAndAddModules().build())));
    private final CuocHop cuocHop = new CuocHop();
    private final ThanhVienCuocHop thanhVien = new ThanhVienCuocHop();

    @BeforeEach
    void chuanBiCuocHopDangDienRa() {
        cuocHop.setCuocHopId(HOP_ID);
        cuocHop.setTrangThai("DANG_DIEN_RA");
        cuocHop.setTieuDe("Họp giao ban");
        cuocHop.setThoiGianBatDauThucTe(LocalDateTime.now().minusMinutes(2));
        when(cuocHopRepository.findByIdForUpdate(HOP_ID)).thenReturn(Optional.of(cuocHop));
        when(cuocHopRepository.findById(HOP_ID)).thenReturn(Optional.of(cuocHop));
        when(thanhVienRepository.findByIdForUpdate(idThanhVien(THANH_VIEN_ID))).thenReturn(Optional.of(thanhVien));
        when(thanhVienRepository.findById(idThanhVien(THANH_VIEN_ID))).thenReturn(Optional.of(thanhVien));
        when(phanQuyen.laThuKyDuocPhanCong(HOP_ID, THU_KY_ID)).thenReturn(true);
    }

    @Test
    void bamThamGiaXacNhanLoiMoiVaChiGhiGioMotLan() {
        LocalDateTime lanDau = service.thamGiaCuocHop(HOP_ID, THANH_VIEN_ID);

        assertNotNull(lanDau);
        assertEquals("DA_XAC_NHAN", thanhVien.getTrangThaiMoi());
        assertEquals("CO_MAT", thanhVien.getTrangThaiThamDu());
        assertEquals(lanDau, thanhVien.getThoiGianDiemDanh());
        assertEquals(lanDau, service.thamGiaCuocHop(HOP_ID, THANH_VIEN_ID));
        assertNull(thanhVien.getThoiGianDoiChieuDiemDanh());
    }

    @Test
    void thuKyGhiVangVanGiuLaiLanTuDiemDanh() {
        LocalDateTime tuDiemDanh = service.thamGiaCuocHop(HOP_ID, THANH_VIEN_ID);

        service.diemDanhThanhVien(HOP_ID, THANH_VIEN_ID, THU_KY_ID, false);
        service.thamGiaCuocHop(HOP_ID, THANH_VIEN_ID);

        assertEquals("VANG_MAT", thanhVien.getTrangThaiThamDu());
        assertEquals(tuDiemDanh, thanhVien.getThoiGianTuDiemDanh());
        assertNotNull(thanhVien.getThoiGianDoiChieuDiemDanh());
    }

    @Test
    void bamThamGiaSauKhiBiGhiVangKhongDoiKetQuaDoiChieu() {
        service.diemDanhThanhVien(HOP_ID, THANH_VIEN_ID, THU_KY_ID, false);
        LocalDateTime doiChieu = thanhVien.getThoiGianDoiChieuDiemDanh();

        service.thamGiaCuocHop(HOP_ID, THANH_VIEN_ID);

        assertEquals("VANG_MAT", thanhVien.getTrangThaiThamDu());
        assertNotNull(thanhVien.getThoiGianTuDiemDanh());
        assertEquals(doiChieu, thanhVien.getThoiGianDoiChieuDiemDanh());
    }

    @Test
    void nguoiDaTuChoiLoiMoiKhongTheMoPhongHayTuDiemDanh() {
        thanhVien.setTrangThaiMoi("TU_CHOI");

        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> service.layPhongHop(HOP_ID, THANH_VIEN_ID)).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                () -> service.thamGiaCuocHop(HOP_ID, THANH_VIEN_ID)).getStatusCode());
    }

    @Test
    void chuTriThayPhongHopDangDienRaDuChuaCoDongThanhVien() {
        int chuTriId = 1;
        cuocHop.setNguoiChuTri(nguoiDung(chuTriId));
        when(cuocHopRepository.findCuocHopDangDienRaGanVoiNguoiDung(chuTriId))
                .thenReturn(List.of(cuocHop));

        var phongHop = service.layCuocHopDangDienRaCuaToi(chuTriId).getFirst();

        assertEquals(HOP_ID, phongHop.cuocHopId());
        assertTrue(phongHop.laChuTri());
        assertTrue(service.layPhongHop(HOP_ID, chuTriId).laChuTri());
    }

    @Test
    void thongBaoBatDauGuiChuTriThuKyVaThanhVienCoLoiMoiHopLe() {
        NguoiDung chuTri = nguoiDung(1);
        NguoiDung thuKy = nguoiDung(THU_KY_ID);
        NguoiDung thamDu = nguoiDung(THANH_VIEN_ID);
        cuocHop.setNguoiChuTri(chuTri);
        when(phanCong.layThuKyBanDau(HOP_ID)).thenReturn(thuKy);
        thanhVien.setNguoiDung(thamDu);
        ThanhVienCuocHop daTuChoi = new ThanhVienCuocHop();
        daTuChoi.setTrangThaiMoi("TU_CHOI");
        daTuChoi.setNguoiDung(nguoiDung(4));
        when(thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(HOP_ID))
                .thenReturn(List.of(thanhVien, daTuChoi));

        service.baoCuocHopBatDau(cuocHop, THU_KY_ID);

        verify(thongBao).gui(cuocHop, THU_KY_ID, "CUOC_HOP_BAT_DAU", "Cuộc họp đã bắt đầu",
                "Họp giao ban", chuTri, thuKy, thamDu);
    }

    @Test
    void moiNhieuNguoiChiChuyenDiaChiThatSangLuongGuiEmail() {
        chuanBiLichSapToi();
        NguoiDung nguoiNhan = nguoiDung(THANH_VIEN_ID);
        nguoiNhan.setEmail("canbo@example.org");
        NguoiDung taiKhoanMau = nguoiDung(4);
        taiKhoanMau.setEmail("canbo-mau@example.invalid");
        when(nguoiDungRepository.findById(THANH_VIEN_ID)).thenReturn(Optional.of(nguoiNhan));
        when(nguoiDungRepository.findById(4)).thenReturn(Optional.of(taiKhoanMau));

        service.moiDanhSachThanhVien(HOP_ID, List.of(THANH_VIEN_ID, 4, THANH_VIEN_ID), THU_KY_ID, THU_MOI);

        var email = ArgumentCaptor.forClass(EmailCanGui.class);
        verify(suKien).publishEvent(email.capture());
        assertEquals(THU_MOI.tieuDe(), email.getValue().chuDe());
        assertTrue(email.getValue().noiDung().startsWith(THU_MOI.noiDung()));
        assertTrue(email.getValue().noiDung().contains("Cuộc họp: Họp giao ban"));
        assertEquals(List.of("canbo@example.org"), email.getValue().diaChi());
    }

    @Test
    void guiLaiEmailKhongThayDoiLoiMoiDangChoPhanHoi() {
        chuanBiLichSapToi();
        NguoiDung nguoiNhan = nguoiDung(THANH_VIEN_ID);
        nguoiNhan.setEmail("canbo@example.org");
        thanhVien.setVaiTroTaiCuocHop("THAM_DU");
        thanhVien.setTrangThaiMoi("CHO_PHAN_HOI");
        var ngayMoi = LocalDateTime.now().minusDays(1);
        thanhVien.setNgayMoi(ngayMoi);
        when(nguoiDungHienTai.layTaiKhoanHoatDong(THANH_VIEN_ID)).thenReturn(nguoiNhan);

        service.guiLaiEmailLoiMoi(HOP_ID, THANH_VIEN_ID, THU_KY_ID, THU_MOI);

        var email = ArgumentCaptor.forClass(EmailCanGui.class);
        verify(suKien).publishEvent(email.capture());
        assertEquals(List.of("canbo@example.org"), email.getValue().diaChi());
        assertEquals(ngayMoi, thanhVien.getNgayMoi());
        assertEquals("CHO_PHAN_HOI", thanhVien.getTrangThaiMoi());
    }

    @Test
    void khongGuiLaiEmailSauKhiDaPhanHoi() {
        chuanBiLichSapToi();
        thanhVien.setVaiTroTaiCuocHop("THAM_DU");
        thanhVien.setTrangThaiMoi("DA_XAC_NHAN");

        assertEquals(HttpStatus.CONFLICT, assertThrows(ResponseStatusException.class,
                () -> service.guiLaiEmailLoiMoi(HOP_ID, THANH_VIEN_ID, THU_KY_ID, THU_MOI)).getStatusCode());

        verifyNoInteractions(suKien);
    }

    @Test
    void thuMoiTuDongDungNoiDungBanChupVaLichDaXacNhan() {
        chuanBiLichSapToi();
        var keHoach = yeuCauRepository.findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                HOP_ID, YeuCauPheDuyet.LoaiYeuCau.KE_HOACH_CUOC_HOP).orElseThrow();
        keHoach.setKeHoachDaTrinh("""
                {"tieuDe":"Họp đã duyệt","noiDungDuKien":"Chốt kế hoạch quý","thoiGianBatDau":"2020-01-01T07:00:00"}
                """);
        NguoiDung nguoiNhan = nguoiDung(THANH_VIEN_ID);
        nguoiNhan.setEmail("canbo@example.org");
        when(nguoiDungRepository.findById(THANH_VIEN_ID)).thenReturn(Optional.of(nguoiNhan));

        service.moiDanhSachThanhVien(HOP_ID, List.of(THANH_VIEN_ID), THU_KY_ID, new NoiDungThuMoi(null, null));

        var email = ArgumentCaptor.forClass(EmailCanGui.class);
        verify(suKien).publishEvent(email.capture());
        assertEquals("Thư mời họp – Họp đã duyệt", email.getValue().chuDe());
        assertTrue(email.getValue().noiDung().contains("Chốt kế hoạch quý"));
        assertTrue(!email.getValue().noiDung().contains("01/01/2020"));
    }

    private void chuanBiLichSapToi() {
        cuocHop.setTrangThai("DA_DUYET");
        cuocHop.setBuocXuLyHienTai("THU_KY_TO_CHUC");
        cuocHop.setThoiGianBatDauThucTe(null);
        LichHop lich = new LichHop();
        lich.setCuocHop(cuocHop);
        lich.setTrangThai("DA_XAC_NHAN");
        lich.setThoiGianBatDau(LocalDateTime.now().plusDays(1));
        lich.setThoiGianKetThuc(lich.getThoiGianBatDau().plusHours(1));
        YeuCauPheDuyet keHoach = new YeuCauPheDuyet();
        keHoach.setCuocHop(cuocHop);
        keHoach.setTrangThai(YeuCauPheDuyet.TrangThai.DA_DUYET);
        keHoach.setLichHop(lich);
        when(yeuCauRepository.findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                HOP_ID, YeuCauPheDuyet.LoaiYeuCau.KE_HOACH_CUOC_HOP)).thenReturn(Optional.of(keHoach));
    }

    private ThanhVienCuocHopId idThanhVien(int nguoiDungId) {
        ThanhVienCuocHopId id = new ThanhVienCuocHopId();
        id.setCuocHopId(HOP_ID);
        id.setNguoiDungId(nguoiDungId);
        return id;
    }

    private NguoiDung nguoiDung(int id) {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setNguoiDungId(id);
        return nguoiDung;
    }
}
