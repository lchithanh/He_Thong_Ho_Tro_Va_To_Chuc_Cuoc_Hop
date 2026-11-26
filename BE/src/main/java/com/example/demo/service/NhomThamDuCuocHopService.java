package com.example.demo.service;

import java.util.HashSet;
import java.util.List;

import com.example.demo.dto.response.ChiTietKeHoachPheDuyetResponse.ThanhPhanHopNhom;
import com.example.demo.entity.CuocHop;
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

    static final String HOP_THUONG = "HOP_THUONG";
    static final String CHU_TICH_CHU_TRI = "CHU_TICH_CHU_TRI";
    static final String XIN_CHU_TICH_KET_LUAN = "XIN_CHU_TICH_KET_LUAN";

    record TuyenDuyet(Integer nhomId, String mucDichHop, Integer nguoiDuyetId, String thanhVienMoiThem, String emailKhachNgoai) {}

    public record LanhDaoPhuTrach(Integer nguoiDungId, String hoTen, String chucVu, boolean chuTich) {}

    List<LanhDaoPhuTrach> layLanhDaoCoThamQuyen(Integer nhomId, boolean chiChuTich) {
        return jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten, nd.chuc_vu,
                    (TRIM(nd.chuc_vu) COLLATE utf8mb4_unicode_ci = 'Chủ tịch') AS chu_tich
                FROM nhom_lien_phong n
                JOIN nguoi_dung nd ON nd.trang_thai = 'HOAT_DONG' AND nd.chuc_vu IS NOT NULL
                JOIN nguoi_dung_vai_tro nvt ON nvt.nguoi_dung_id = nd.nguoi_dung_id
                JOIN vai_tro vt ON vt.vai_tro_id = nvt.vai_tro_id AND vt.ma_vai_tro = 'LANH_DAO'
                WHERE n.nhom_id = :nhom AND (
                    TRIM(nd.chuc_vu) COLLATE utf8mb4_unicode_ci = 'Chủ tịch'
                    OR (NOT :chiChuTich AND TRIM(nd.chuc_vu) COLLATE utf8mb4_unicode_ci = 'Phó Chủ tịch'
                        AND nd.nguoi_dung_id IN (n.nguoi_lap_id, n.nguoi_kich_hoat_id)))
                ORDER BY nd.ho_ten, nd.nguoi_dung_id
                """).param("nhom", nhomId).param("chiChuTich", chiChuTich).query(LanhDaoPhuTrach.class).list();
    }

    TuyenDuyet layTuyenDuyet(Integer cuocHopId) {
        return jdbc.sql("""
                SELECT nhom_id, muc_dich_hop, nguoi_duyet_id, thanh_vien_moi_them, email_khach_ngoai
                FROM nhom_lien_phong_de_xuat WHERE cuoc_hop_id = :id
                """).param("id", cuocHopId).query(TuyenDuyet.class).optional().orElse(null);
    }

    boolean canLanhDao(TuyenDuyet tuyen, boolean coKhachNgoai) {
        return !HOP_THUONG.equals(tuyen.mucDichHop()) || coKhachNgoai || coNguoiMoiThem(tuyen);
    }

    boolean coNguoiMoiThem(TuyenDuyet tuyen) {
        return tuyen.thanhVienMoiThem() != null && !objectMapper.readTree(tuyen.thanhVienMoiThem()).isEmpty();
    }

    boolean laEmailNoiBo(String email) {
        return jdbc.sql("SELECT EXISTS (SELECT 1 FROM nguoi_dung WHERE email = :email)")
                .param("email", email).query(Boolean.class).single();
    }

    boolean duocMoiKhachNgoai(TuyenDuyet tuyen, String email) {
        if (tuyen == null || tuyen.emailKhachNgoai() == null) return true;
        for (var muc : objectMapper.readTree(tuyen.emailKhachNgoai())) {
            if (email.equals(muc.asText())) return true;
        }
        return false;
    }

    ThanhPhanHopNhom layThanhPhanTrinh(Integer cuocHopId) {
        var tuyen = layTuyenDuyet(cuocHopId);
        if (tuyen == null) return null;
        var idsNhom = layThanhVienDaChon(cuocHopId);
        List<Integer> idsMoi = tuyen.thanhVienMoiThem() == null ? List.of()
                : objectMapper.readerForListOf(Integer.class).readValue(tuyen.thanhVienMoiThem());
        List<String> email = tuyen.emailKhachNgoai() == null ? List.of()
                : objectMapper.readerForListOf(String.class).readValue(tuyen.emailKhachNgoai());
        return new ThanhPhanHopNhom(tuyen.mucDichHop(), layHoTen(idsNhom), layHoTen(idsMoi), email);
    }

    private List<String> layHoTen(java.util.Collection<Integer> ids) {
        if (ids.isEmpty()) return List.of();
        return jdbc.sql("SELECT ho_ten FROM nguoi_dung WHERE nguoi_dung_id IN (:ids) ORDER BY ho_ten, nguoi_dung_id")
                .param("ids", ids).query(String.class).list();
    }

    boolean duocMoi(CuocHop hop, TuyenDuyet tuyen, Integer nguoiId) {
        if (tuyen == null) return true;
        if (layThanhVienDaChon(hop.getCuocHopId()).contains(nguoiId)) return true;
        return duocMoiThem(tuyen, nguoiId);
    }

    boolean duocMoiThem(TuyenDuyet tuyen, Integer nguoiId) {
        if (tuyen.thanhVienMoiThem() != null) {
            for (var muc : objectMapper.readTree(tuyen.thanhVienMoiThem())) {
                if (muc.asInt() == nguoiId) return true;
            }
        }
        return false;
    }

    Integer layTruongVanPhong() {
        var danhSach = jdbc.sql("""
                SELECT pb.truong_phong_id
                FROM phong_ban pb JOIN nguoi_dung nd ON nd.nguoi_dung_id = pb.truong_phong_id
                WHERE TRIM(pb.ten_phong_ban) COLLATE utf8mb4_unicode_ci = 'Văn phòng'
                  AND pb.trang_thai = 'HOAT_DONG' AND nd.trang_thai = 'HOAT_DONG'
                  AND nd.phong_ban_id = pb.phong_ban_id
                """).query(Integer.class).list();
        return danhSach.size() == 1 ? danhSach.getFirst() : null;
    }

    List<Integer> layTruongPhongPhoiHop(Integer nhomId, Integer phongChuTriId) {
        return jdbc.sql("""
                SELECT pb.truong_phong_id FROM nhom_lien_phong_phong_ban np
                JOIN phong_ban pb ON pb.phong_ban_id = np.phong_ban_id
                JOIN nguoi_dung nd ON nd.nguoi_dung_id = pb.truong_phong_id
                WHERE np.nhom_id = :nhom AND pb.phong_ban_id <> :chuTri
                  AND pb.trang_thai = 'HOAT_DONG' AND nd.trang_thai = 'HOAT_DONG'
                """).param("nhom", nhomId).param("chuTri", phongChuTriId).query(Integer.class).list();
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
