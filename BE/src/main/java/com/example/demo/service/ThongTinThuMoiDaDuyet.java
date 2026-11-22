package com.example.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.example.demo.dto.request.NoiDungThuMoi;
import com.example.demo.dto.response.ChiTietKeHoachPheDuyetResponse;
import com.example.demo.entity.YeuCauPheDuyet;

import tools.jackson.databind.ObjectMapper;

@Component
public class ThongTinThuMoiDaDuyet {
    private static final DateTimeFormatter GIO = DateTimeFormatter.ofPattern("HH:mm dd/MM/uuuu");
    private final ObjectMapper objectMapper;

    public ThongTinThuMoiDaDuyet(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    DuLieu doc(YeuCauPheDuyet yeuCau) {
        String daTrinh = yeuCau.getKeHoachDaTrinh();
        var banChup = daTrinh == null || daTrinh.isBlank()
                ? PheDuyetKeHoachService.taoBanChup(yeuCau)
                : objectMapper.readValue(daTrinh, ChiTietKeHoachPheDuyetResponse.class);
        var lich = ThaoTacCuocHop.lichHienHanh(yeuCau.getCuocHop());
        var phong = lich.getPhongHop();
        String diaDiem = phong == null ? "Họp trực tuyến" : phong.getTenPhongHop()
                + (phong.getViTri() == null || phong.getViTri().isBlank() ? "" : " – " + phong.getViTri());
        return new DuLieu(lich.getLichHopId(), banChup.tieuDe(), banChup.noiDungDuKien(), banChup.tenChuTri(),
                lich.getThoiGianBatDau(), lich.getThoiGianKetThuc(), diaDiem);
    }

    public record DuLieu(Integer lichHopId, String tieuDe, String noiDung, String chuTri,
            LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc, String diaDiem) {
        String chuDe(NoiDungThuMoi thuMoi) {
            if (thuMoi.tieuDe() != null && !thuMoi.tieuDe().isBlank()) return thuMoi.tieuDe().strip();
            return "Thư mời họp – " + tieuDe.replaceAll("[\\p{Cntrl}]+", " ").strip();
        }

        String noiDungEmail(NoiDungThuMoi thuMoi, String nguoiNhan, String diaDiemGui) {
            String moDau = thuMoi.noiDung() == null || thuMoi.noiDung().isBlank()
                    ? "Trân trọng kính mời " + nguoiNhan + " tham dự cuộc họp." : thuMoi.noiDung().strip();
            return """
                    %s

                    Cuộc họp: %s
                    Nội dung: %s
                    Bắt đầu: %s (giờ Việt Nam)
                    Kết thúc: %s (giờ Việt Nam)
                    Địa điểm/Hình thức: %s
                    """.formatted(moDau, tieuDe, noiDung == null || noiDung.isBlank() ? "Chưa ghi nội dung" : noiDung,
                    GIO.format(thoiGianBatDau), GIO.format(thoiGianKetThuc), diaDiemGui);
        }
    }
}
