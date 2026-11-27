package com.example.demo.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.NguoiDungRepository;
import com.example.demo.repository.ThongBaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final CapNhatHoSoRealtime capNhatRealtime;
    private final NguoiDungRepository nguoiDungRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void guiTheoId(CuocHop cuocHop, Integer nguoiThaoTacId, String loai, String tieuDe,
            String noiDung, Collection<Integer> ids) {
        NguoiDung[] nguoiNhan = nguoiDungRepository.findAllById(ids.stream().filter(Objects::nonNull).distinct().toList())
                .stream().filter(NguoiDungHienTaiService::dangHoatDong).toArray(NguoiDung[]::new);
        gui(cuocHop, nguoiThaoTacId, loai, tieuDe, noiDung, nguoiNhan);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void gui(
            CuocHop cuocHop,
            Integer nguoiThaoTacId,
            String loai,
            String tieuDe,
            String noiDung,
            NguoiDung... nguoiNhan) {

        Set<Integer> daThem = new HashSet<>();
        daThem.add(nguoiThaoTacId);

        for (NguoiDung nguoi : nguoiNhan) {
            if (nguoi == null || !daThem.add(nguoi.getNguoiDungId())) {
                continue;
            }

            ThongBao thongBao = new ThongBao();
            thongBao.setCuocHop(cuocHop);
            thongBao.setNguoiDung(nguoi);
            thongBao.setLoaiThongBao(loai);
            thongBao.setTieuDe(tieuDe);
            thongBao.setNoiDung(noiDung);
            thongBaoRepository.save(thongBao);
        }

        if (loai != null && loai.startsWith("KE_HOACH_")) {
            capNhatRealtime.baoBuocDeXuat(daThem);
        } else {
            capNhatRealtime.baoCho(daThem, false);
        }
    }
}
