package com.example.demo.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class NhomThamDuCuocHopService {
    private final JdbcClient jdbc;
    private final ObjectMapper objectMapper;

    boolean laDeXuatTuNhom(Integer cuocHopId) {
        return cuocHopId != null && jdbc.sql("""
                SELECT EXISTS (SELECT 1 FROM nhom_lien_phong_de_xuat WHERE cuoc_hop_id = :id)
                """).param("id", cuocHopId).query(Boolean.class).single();
    }

    void chonNhom(Integer cuocHopId, Integer nhomId) {
        boolean dangHoatDong = jdbc.sql("""
                SELECT trang_thai FROM nhom_lien_phong WHERE nhom_id = :id FOR UPDATE
                """).param("id", nhomId).query(String.class).optional().filter("HOAT_DONG"::equals).isPresent();
        if (!dangHoatDong) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ chọn nhóm đang hoạt động");
        }

        var nguoiDungIds = jdbc.sql("""
                SELECT tv.nguoi_dung_id FROM nhom_lien_phong_thanh_vien tv
                JOIN nguoi_dung nd ON nd.nguoi_dung_id = tv.nguoi_dung_id
                WHERE tv.nhom_id = :id AND nd.trang_thai = 'HOAT_DONG'
                ORDER BY tv.nguoi_dung_id
                """).param("id", nhomId).query(Integer.class).list();
        if (nguoiDungIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nhóm không còn thành viên hoạt động");
        }

        jdbc.sql("""
                INSERT INTO cuoc_hop_nhom_tham_du (cuoc_hop_id, nhom_id, thanh_vien_khi_chon)
                VALUES (:hop, :nhom, :tv)
                ON DUPLICATE KEY UPDATE nhom_id = :nhom, thanh_vien_khi_chon = :tv
                """).param("hop", cuocHopId).param("nhom", nhomId)
                .param("tv", objectMapper.writeValueAsString(nguoiDungIds)).update();
    }

    Set<Integer> layThanhVienDaChon(Integer cuocHopId) {
        var ids = new HashSet<Integer>();
        var nhomDaChon = jdbc.sql("""
                SELECT thanh_vien_khi_chon FROM cuoc_hop_nhom_tham_du WHERE cuoc_hop_id = :id
                """).param("id", cuocHopId).query(String.class).optional();
        if (nhomDaChon.isPresent()) {
            for (var muc : objectMapper.readTree(nhomDaChon.get())) {
                ids.add(muc.intValue());
            }
            return ids;
        }
        jdbc.sql("""
                SELECT thanh_vien_khi_gui FROM nhom_lien_phong_de_xuat WHERE cuoc_hop_id = :id
                """).param("id", cuocHopId).query(String.class).optional().ifPresent(json -> {
            for (var muc : objectMapper.readTree(json)) {
                var nguoiDungId = muc.get("nguoiDungId");
                if (nguoiDungId != null && nguoiDungId.isIntegralNumber()) {
                    ids.add(nguoiDungId.intValue());
                }
            }
        });
        return ids;
    }
}
