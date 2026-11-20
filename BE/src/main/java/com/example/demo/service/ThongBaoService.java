package com.example.demo.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.ThongBao;
import com.example.demo.repository.ThongBaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final CapNhatHoSoRealtime capNhatRealtime;

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

        capNhatRealtime.baoCho(daThem, loai != null && loai.startsWith("KE_HOACH_"));
    }
}
