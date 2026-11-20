package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.demo.entity.LichHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.repository.ThanhVienCuocHopRepository;

@Service
class ThongBaoEmailCuocHop {
    private static final Set<String> LOI_MOI_CON_HIEU_LUC = Set.of("CHO_PHAN_HOI", "DA_XAC_NHAN");
    private static final DateTimeFormatter DINH_DANG_GIO = DateTimeFormatter.ofPattern("HH:mm dd/MM/uuuu");

    private final boolean batGuiEmail;
    private final ApplicationEventPublisher suKien;
    private final ThanhVienCuocHopRepository thanhVienRepository;

    ThongBaoEmailCuocHop(@Value("${app.email.enabled:false}") boolean batGuiEmail,
            ApplicationEventPublisher suKien, ThanhVienCuocHopRepository thanhVienRepository) {
        this.batGuiEmail = batGuiEmail;
        this.suKien = suKien;
        this.thanhVienRepository = thanhVienRepository;
    }

    void moiHop(LichHop lich, List<NguoiDung> nguoiDuocMoi) {
        bao(lich, nguoiDuocMoi, "Lời mời họp", "Mở mục Lời mời họp trong hệ thống để phản hồi.");
    }

    void xacNhanLich(LichHop lich, boolean coLichCu) {
        if (!batGuiEmail) return;
        bao(lich, nguoiDuocMoi(lich), coLichCu ? "Thay đổi lịch họp" : "Xác nhận lịch họp",
                "Mở mục Lịch họp trong hệ thống để xem lịch đã xác nhận.");
    }

    void huyLich(LichHop lich, String lyDo) {
        if (!batGuiEmail) return;
        bao(lich, nguoiDuocMoi(lich), "Hủy lịch họp",
                "Lịch ở trên đã bị hủy.\nLý do: " + lyDo
                        + "\nTheo dõi mục Lịch họp trong hệ thống để xem cập nhật tiếp theo.");
    }

    void huyCuocHop(LichHop lich, String lyDo) {
        if (!batGuiEmail) return;
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
        if (!batGuiEmail) return;
        List<String> diaChi = nguoiNhan.stream()
                .map(NguoiDung::getEmail)
                .filter(email -> email != null && !email.isBlank())
                .map(String::strip)
                .distinct()
                .toList();
        if (diaChi.isEmpty()) return;

        var cuocHop = lich.getCuocHop();
        String noiDung = """
                %s

                Cuộc họp: %s
                Bắt đầu: %s
                Kết thúc: %s
                Địa điểm: %s

                %s
                """.formatted(suKienLich, cuocHop.getTieuDe(), gio(lich.getThoiGianBatDau()),
                        gio(lich.getThoiGianKetThuc()), lich.getPhongHop() == null
                                ? "Trực tuyến" : lich.getPhongHop().getTenPhongHop(), huongDan);
        suKien.publishEvent(new EmailCanGui(lich.getLichHopId(), diaChi, "[Họp hành chính] " + suKienLich, noiDung));
    }

    private String gio(LocalDateTime moc) {
        return moc == null ? "Chưa xác định" : DINH_DANG_GIO.format(moc);
    }

    record EmailCanGui(Integer lichHopId, List<String> diaChi, String chuDe, String noiDung) {}
}
