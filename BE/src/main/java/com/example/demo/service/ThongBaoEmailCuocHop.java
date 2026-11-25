package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.demo.dto.request.NoiDungThuMoi;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.repository.ThanhVienCuocHopRepository;


@Service
class ThongBaoEmailCuocHop {
    private static final Set<String> LOI_MOI_CON_HIEU_LUC = Set.of("CHO_PHAN_HOI", "DA_XAC_NHAN");
    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HH:mm dd/MM/uuuu");

    private final boolean batGuiEmail;
    private final ApplicationEventPublisher suKien;
    private final ThanhVienCuocHopRepository thanhVienRepository;

    private final ThongTinThuMoiDaDuyet thongTinThuMoi;
    private final LienKetLoiMoiKhachService lienKet;

    ThongBaoEmailCuocHop(@Value("${app.email.enabled:false}") boolean batGuiEmail,
            ApplicationEventPublisher suKien, ThanhVienCuocHopRepository thanhVienRepository,
            ThongTinThuMoiDaDuyet thongTinThuMoi, LienKetLoiMoiKhachService lienKet) {
        this.batGuiEmail = batGuiEmail;
        this.suKien = suKien;
        this.thanhVienRepository = thanhVienRepository;
        this.thongTinThuMoi = thongTinThuMoi;
        this.lienKet = lienKet;
    }

    boolean moiHop(YeuCauPheDuyet keHoach, List<NguoiDung> nguoiDuocMoi, NoiDungThuMoi thuMoi) {
        var thongTin = thongTinThuMoi.doc(keHoach);
        var tep = TepThuMoi.doc(thuMoi.tepDinhKem(), thongTin.lichHopId());
        return gui(thongTin.lichHopId(), nguoiDuocMoi, thongTin.chuDe(thuMoi),
                thongTin.noiDungEmail(thuMoi, "anh/chị", thongTin.diaDiem())
                        + "\nMở mục Lời mời họp trong hệ thống để phản hồi.", tep);
    }

    void deXuatDoiLich(LichHop lich) {
        bao(lich, nguoiDuocMoi(lich), "Cuộc họp đang chờ dời lịch",
                "Mở mục Lời mời họp để phản hồi khung giờ mới. Lý do: " + lich.getLyDoThayDoi());
    }

    void giuLichCu(LichHop lich, String lyDo) {
        bao(lich, nguoiDuocMoi(lich), "Giữ lịch họp cũ", "Tiếp tục dùng lịch đã xác nhận. Lý do: " + lyDo);
    }

    void xacNhanLich(LichHop lich, boolean coLichCu) {
        bao(lich, nguoiDuocMoi(lich), coLichCu ? "Thay đổi lịch họp" : "Xác nhận lịch họp",
                "Mở mục Lịch họp trong hệ thống để xem lịch đã xác nhận.");
    }

    void huyLich(LichHop lich, String lyDo) {
        bao(lich, nguoiDuocMoi(lich), "Hủy lịch họp",
                "Lịch ở trên đã bị hủy.\nLý do: " + lyDo
                        + "\nTheo dõi mục Lịch họp trong hệ thống để xem cập nhật tiếp theo.");
    }

    void huyCuocHop(LichHop lich, String lyDo) {
        bao(lich, nguoiDuocMoi(lich), "Hủy cuộc họp",
                "Cuộc họp đã bị hủy.\nLý do: " + lyDo);
    }

    private List<NguoiDung> nguoiDuocMoi(LichHop lich) {
        return thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(lich.getCuocHop().getCuocHopId())
                .stream()
                .filter(tv -> "THAM_DU".equals(tv.getVaiTroTaiCuocHop())
                        && tv.getTrangThaiMoi() != null && LOI_MOI_CON_HIEU_LUC.contains(tv.getTrangThaiMoi()))
                .map(ThanhVienCuocHop::getNguoiDung)
                .filter(NguoiDungHienTaiService::dangHoatDong)
                .toList();
    }

    private void bao(LichHop lich, List<NguoiDung> nguoiNhan, String suKienLich, String huongDan) {
        bao(lich, nguoiNhan, "[Họp hành chính] " + suKienLich, suKienLich, huongDan, null);
    }

    private boolean bao(LichHop lich, List<NguoiDung> nguoiNhan, String chuDe, String moDau, String huongDan, TepThuMoi tep) {
        var cuocHop = lich.getCuocHop();
        String noiDung = """
                %s

                Cuộc họp: %s
                Bắt đầu: %s
                Kết thúc: %s
                Địa điểm: %s

                %s
                """.formatted(moDau, cuocHop.getTieuDe(), gio(lich.getThoiGianBatDau()),
                        gio(lich.getThoiGianKetThuc()), lich.getPhongHop() == null
                                ? "Trực tuyến" : lich.getPhongHop().getTenPhongHop(), huongDan);
        lienKet.baoCapNhat(cuocHop, lich.getLichHopId(), moDau, noiDung);
        return gui(lich.getLichHopId(), nguoiNhan, chuDe, noiDung, tep);
    }

    private boolean gui(Integer lichHopId, List<NguoiDung> nguoiNhan, String chuDe, String noiDung, TepThuMoi tep) {
        if (!batGuiEmail) return false;
        List<String> diaChi = nguoiNhan.stream()
                .map(NguoiDung::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .map(String::strip)
                .filter(email -> !email.toLowerCase(Locale.ROOT).endsWith(".invalid"))
                .distinct()
                .toList();
        if (diaChi.isEmpty()) return false;

        suKien.publishEvent(new EmailCanGui(lichHopId, diaChi, chuDe, noiDung, tep));
        return true;
    }

    private String gio(LocalDateTime moc) {
        return moc == null ? "Chưa xác định" : DINH_DANG_GIO.format(moc);
    }

    record EmailCanGui(Integer lichHopId, List<String> diaChi, String chuDe, String noiDung, TepThuMoi tep) {}
}
