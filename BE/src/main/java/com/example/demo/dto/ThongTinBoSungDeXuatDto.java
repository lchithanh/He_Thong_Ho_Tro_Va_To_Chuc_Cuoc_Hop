package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.example.demo.entity.ThongTinBoSungDeXuat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ThongTinBoSungDeXuatDto(
        @Size(max = 4000) String ketQuaKyVong,
        @Size(max = 3) List<@NotNull LocalDateTime> khungGioDeXuat,
        @Size(max = 255) String diaDiemDeXuat,
        @Size(max = 100) String nenTangTrucTuyen,
        @Size(max = 2048) String lienKetTrucTuyen,
        @Size(max = 500) String huongDanTrucTuyen,
        @Size(max = 4000) String thanhPhanBatBuoc,
        @Size(max = 4000) String thanhPhanTuyChon,
        @Size(max = 4000) String yeuCauChuanBi,
        @Size(max = 4000) String lienKetTaiLieu
) {
    public static ThongTinBoSungDeXuatDto tu(ThongTinBoSungDeXuat muc) {
        if (muc == null) return null;
        var gio = Stream.of(muc.getBatDauDeXuat1(), muc.getBatDauDeXuat2(), muc.getBatDauDeXuat3())
                .filter(Objects::nonNull).toList();
        return new ThongTinBoSungDeXuatDto(muc.getKetQuaKyVong(), gio, muc.getDiaDiemDeXuat(),
                muc.getNenTangTrucTuyen(), muc.getLienKetTrucTuyen(), muc.getHuongDanTrucTuyen(),
                muc.getThanhPhanBatBuoc(), muc.getThanhPhanTuyChon(), muc.getYeuCauChuanBi(), muc.getLienKetTaiLieu());
    }

    public ThongTinBoSungDeXuat denHoSo(String loaiHinh) {
        List<LocalDateTime> gio = khungGioDeXuat == null ? List.of() : khungGioDeXuat;
        boolean trucTuyen = "TRUC_TUYEN".equals(loaiHinh);
        return new ThongTinBoSungDeXuat(chuanHoa(ketQuaKyVong), gio.isEmpty() ? null : gio.get(0),
                gio.size() > 1 ? gio.get(1) : null, gio.size() > 2 ? gio.get(2) : null,
                trucTuyen ? null : chuanHoa(diaDiemDeXuat),
                trucTuyen ? chuanHoa(nenTangTrucTuyen) : null,
                trucTuyen ? chuanHoa(lienKetTrucTuyen) : null,
                trucTuyen ? chuanHoa(huongDanTrucTuyen) : null,
                chuanHoa(thanhPhanBatBuoc), chuanHoa(thanhPhanTuyChon),
                chuanHoa(yeuCauChuanBi), chuanHoa(lienKetTaiLieu));
    }

    private static String chuanHoa(String noiDung) {
        return noiDung == null || noiDung.isBlank() ? null : noiDung.trim();
    }
}
