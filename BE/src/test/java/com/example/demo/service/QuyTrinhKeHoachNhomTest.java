package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.PhongHop;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KhachMoiCuocHopRepository;
import com.example.demo.repository.LichHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;
import com.example.demo.service.NhomThamDuCuocHopService.LanhDaoPhuTrach;
import com.example.demo.service.NhomThamDuCuocHopService.TuyenDuyet;

import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class QuyTrinhKeHoachNhomTest {
    private static final int CUOC_HOP = 1;
    private static final int LICH_HOP = 2;
    private static final int NHOM = 3;
    private static final int THU_KY = 4;
    private static final int TO_TRUONG = 5;
    private static final int CHU_TICH = 6;

    @Mock private CuocHopRepository cuocHopRepository;
    @Mock private KhachMoiCuocHopRepository khachMoiCuocHopRepository;
    @Mock private LichHopRepository lichHopRepository;
    @Mock private XacNhanLichHopService xacNhanLichHopService;
    @Mock private PhanCongXuLyKeHoachService phanCongXuLyKeHoachService;
    @Mock private YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    @Mock private NguoiDungHienTaiService nguoiDungHienTaiService;
    @Mock private LichSuPheDuyetService lichSuPheDuyetService;
    @Mock private TaiLieuKeHoachService taiLieuKeHoachService;
    @Mock private ThongBaoService thongBaoService;
    @Mock private NhomThamDuCuocHopService nhomThamDu;
    @Spy private JsonMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    @InjectMocks private QuyTrinhKeHoachService service;

    private final NguoiDung thuKy = taiKhoan(THU_KY);
    private final NguoiDung toTruong = taiKhoan(TO_TRUONG);
    private final CuocHop cuocHop = new CuocHop();
    private final LichHop lichHop = new LichHop();

    @BeforeEach
    void taoHoSoDangLapKeHoach() {
        cuocHop.setCuocHopId(CUOC_HOP);
        cuocHop.setTieuDe("Họp nhóm công tác");
        cuocHop.setLoaiHinh("TRUC_TIEP");
        cuocHop.setPhamViCuocHop("LIEN_PHONG_BAN");
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("LAP_KE_HOACH");
        cuocHop.setNguoiDeXuat(toTruong);
        cuocHop.setNguoiChuTri(toTruong);
        lichHop.setLichHopId(LICH_HOP);
        lichHop.setPhongHop(new PhongHop());

        when(nguoiDungHienTaiService.layTaiKhoanHoatDong(THU_KY)).thenReturn(thuKy);
        when(nguoiDungHienTaiService.layTaiKhoanHoatDong(TO_TRUONG)).thenReturn(toTruong);
        when(cuocHopRepository.findByIdForUpdate(CUOC_HOP)).thenReturn(Optional.of(cuocHop));
        when(phanCongXuLyKeHoachService.laThuKyDangXuLy(CUOC_HOP, THU_KY)).thenReturn(true);
        when(lichHopRepository.findById(LICH_HOP)).thenReturn(Optional.of(lichHop));
        when(phanCongXuLyKeHoachService.layThuKyCuocHop(cuocHop)).thenReturn(thuKy);
        when(phanCongXuLyKeHoachService.chonThuKyCuocHop(cuocHop, THU_KY)).thenReturn(thuKy);
        when(yeuCauPheDuyetRepository.save(any(YeuCauPheDuyet.class))).thenAnswer(call -> {
            YeuCauPheDuyet yeuCau = call.getArgument(0);
            yeuCau.setYeuCauPheDuyetId(7);
            return yeuCau;
        });
    }

    @Test
    void hopThuongDungPhongHopTrinhThangToTruong() {
        when(nhomThamDu.layTuyenDuyet(CUOC_HOP))
                .thenReturn(new TuyenDuyet(NHOM, "HOP_THUONG", TO_TRUONG, null, null));

        service.trinhKeHoachCuocHop(CUOC_HOP, LICH_HOP, THU_KY, null, null);

        kiemTraTrinhThang("CHO_TRUONG_NHOM", "TRUONG_NHOM", toTruong);
    }

    @Test
    void hopXinKetLuanTrinhThangChuTich() {
        NguoiDung chuTich = taiKhoan(CHU_TICH);
        TuyenDuyet tuyen = new TuyenDuyet(NHOM, "XIN_CHU_TICH_KET_LUAN", CHU_TICH, null, null);
        when(nhomThamDu.layTuyenDuyet(CUOC_HOP)).thenReturn(tuyen);
        when(nhomThamDu.canLanhDao(tuyen, false)).thenReturn(true);
        when(nhomThamDu.layLanhDaoCoThamQuyen(NHOM, true))
                .thenReturn(List.of(new LanhDaoPhuTrach(CHU_TICH, "Người duyệt", "Chủ tịch", true)));
        when(nguoiDungHienTaiService.layTaiKhoanHoatDong(CHU_TICH)).thenReturn(chuTich);

        service.trinhKeHoachCuocHop(CUOC_HOP, LICH_HOP, THU_KY, null, CHU_TICH);

        kiemTraTrinhThang("CHO_LANH_DAO", "LANH_DAO", chuTich);
    }

    private void kiemTraTrinhThang(String buoc, String cap, NguoiDung nguoiDuyet) {
        ArgumentCaptor<YeuCauPheDuyet> captor = ArgumentCaptor.forClass(YeuCauPheDuyet.class);
        verify(yeuCauPheDuyetRepository).save(captor.capture());
        assertEquals(buoc, cuocHop.getBuocXuLyHienTai());
        assertEquals(cap, captor.getValue().getCapPheDuyet());
        assertSame(nguoiDuyet, captor.getValue().getNguoiDuyet());
        assertNull(captor.getValue().getThuKyTiepNhan());
        verify(nhomThamDu, never()).layTruongVanPhong();
        verify(xacNhanLichHopService).kiemTraLichCuaLanTrinh(
                cuocHop, lichHop, null, HttpStatus.BAD_REQUEST);
        verify(thongBaoService).gui(cuocHop, THU_KY, "KE_HOACH_CHO_DUYET",
                "Có kế hoạch cuộc họp cần bạn xem xét", cuocHop.getTieuDe(), nguoiDuyet);
    }

    private static NguoiDung taiKhoan(int id) {
        NguoiDung nd = new NguoiDung();
        nd.setNguoiDungId(id);
        nd.setTrangThai("HOAT_DONG");
        return nd;
    }
}
