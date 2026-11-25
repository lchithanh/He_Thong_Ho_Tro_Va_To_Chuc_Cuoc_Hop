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

    record TuyenDuyet(String capDuyet, Integer nhomId) {}

    public record LanhDaoPhuTrach(Integer nguoiDungId, String hoTen) {}

    LanhDaoPhuTrach layLanhDaoPhuTrach(Integer nhomId) {
        return jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten
                FROM nhom_lien_phong n
                JOIN nguoi_dung nd ON nd.nguoi_dung_id IN (n.nguoi_lap_id, n.nguoi_kich_hoat_id)
                JOIN nguoi_dung_vai_tro nvt ON nvt.nguoi_dung_id = nd.nguoi_dung_id
                JOIN vai_tro vt ON vt.vai_tro_id = nvt.vai_tro_id
                WHERE n.nhom_id = :nhom AND nd.trang_thai = 'HOAT_DONG' AND vt.ma_vai_tro = 'LANH_DAO'
                ORDER BY CASE WHEN nd.nguoi_dung_id = n.nguoi_lap_id THEN 0 ELSE 1 END
                LIMIT 1
                """).param("nhom", nhomId).query(LanhDaoPhuTrach.class).optional().orElse(null);
    }

    LanhDaoPhuTrach yeuCauLanhDaoPhuTrach(Integer nhomId) {
        var lanhDao = layLanhDaoPhuTrach(nhomId);
        if (lanhDao == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Chưa có lãnh đạo đang hoạt động trong hồ sơ lập hoặc kích hoạt nhóm");
        }
        return lanhDao;
    }

    TuyenDuyet layTuyenDuyet(Integer cuocHopId) {
        return jdbc.sql("""
                SELECT cap_duyet, nhom_id FROM nhom_lien_phong_de_xuat WHERE cuoc_hop_id = :id
                """).param("id", cuocHopId).query(TuyenDuyet.class).optional().orElse(null);
    }

    void ganNguoiDuyetCuoi(Integer cuocHopId, Integer nguoiDuyetId, String cap) {
        jdbc.sql("""
                UPDATE nhom_lien_phong_de_xuat SET nguoi_duyet_id = :nguoi, cap_duyet = :cap WHERE cuoc_hop_id = :id
                """).param("nguoi", nguoiDuyetId).param("cap", cap).param("id", cuocHopId).update();
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
