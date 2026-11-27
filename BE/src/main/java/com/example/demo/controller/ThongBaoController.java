package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.repository.ThongBaoRepository;
import com.example.demo.service.NguoiDungHienTaiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {
    private final ThongBaoRepository repository;
    private final NguoiDungHienTaiService hienTai;

    public record Muc(Integer id, Integer cuocHopId, String tieuDe, String noiDung,
            String loai, boolean daDoc, LocalDateTime ngayTao) {}
    public record HopThu(long chuaDoc, boolean conTrang, List<Muc> danhSach) {}

    @GetMapping
    @Transactional(readOnly = true)
    public HopThu danhSach(Authentication auth, @RequestParam(defaultValue = "0") int trang) {
        Integer id = hienTai.layNguoiDungId(auth);
        if (trang < 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trang không hợp lệ");
        var slice = repository.findByNguoiDung_NguoiDungIdOrderByThongBaoIdDesc(id, PageRequest.of(trang, 20));
        return new HopThu(repository.countByNguoiDung_NguoiDungIdAndDaDocFalse(id), slice.hasNext(),
                slice.stream().map(tb -> new Muc(tb.getThongBaoId(),
                        tb.getCuocHop() == null ? null : tb.getCuocHop().getCuocHopId(),
                        tb.getTieuDe(), tb.getNoiDung(), tb.getLoaiThongBao(),
                        Boolean.TRUE.equals(tb.getDaDoc()), tb.getNgayTao())).toList());
    }

    @PutMapping("/{thongBaoId}/da-doc")
    @Transactional
    public void daDoc(Authentication auth, @PathVariable Integer thongBaoId) {
        Integer id = hienTai.layNguoiDungId(auth);
        var tb = repository.findByThongBaoIdAndNguoiDung_NguoiDungId(thongBaoId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông báo"));
        if (!Boolean.TRUE.equals(tb.getDaDoc())) {
            tb.setDaDoc(true);
            tb.setThoiGianDoc(LocalDateTime.now());
        }
    }

    @PutMapping("/da-doc")
    @Transactional
    public void docTatCa(Authentication auth) {
        repository.danhDauTatCaDaDoc(hienTai.layNguoiDungId(auth), LocalDateTime.now());
    }
}
