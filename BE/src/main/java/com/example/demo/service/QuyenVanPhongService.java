package com.example.demo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuyenVanPhongService {
    private final JdbcClient jdbc;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final PhanQuyenCuocHopService phanQuyenCuocHopService;
    private final CapNhatHoSoRealtime realtime;

    public record TaiKhoan(Integer nguoiDungId, String hoTen, String email, String tenPhongBan, boolean vanPhong) {}

    public boolean duocDieuPhoi(Integer id) {
        return jdbc.sql("""
                SELECT EXISTS (
                    SELECT 1 FROM thu_ky_van_phong vp JOIN nguoi_dung nd ON nd.nguoi_dung_id = vp.nguoi_dung_id
                    JOIN nguoi_dung_vai_tro nvt ON nvt.nguoi_dung_id = nd.nguoi_dung_id
                    JOIN vai_tro vt ON vt.vai_tro_id = nvt.vai_tro_id
                    WHERE vp.nguoi_dung_id = :id AND nd.trang_thai = 'HOAT_DONG' AND vt.ma_vai_tro = 'THU_KY'
                )
                """).param("id", id).query(Boolean.class).single();
    }

    public List<TaiKhoan> danhSach() {
        return jdbc.sql("""
                SELECT nd.nguoi_dung_id, nd.ho_ten, nd.email, pb.ten_phong_ban,
                    (vp.nguoi_dung_id IS NOT NULL) AS van_phong
                FROM nguoi_dung nd
                JOIN nguoi_dung_vai_tro ndvt ON ndvt.nguoi_dung_id = nd.nguoi_dung_id
                JOIN vai_tro vt ON vt.vai_tro_id = ndvt.vai_tro_id
                LEFT JOIN thu_ky_van_phong vp ON vp.nguoi_dung_id = nd.nguoi_dung_id
                LEFT JOIN phong_ban pb ON pb.phong_ban_id = nd.phong_ban_id
                WHERE nd.trang_thai = 'HOAT_DONG' AND vt.ma_vai_tro = 'THU_KY'
                ORDER BY nd.ho_ten, nd.nguoi_dung_id
                """).query(TaiKhoan.class).list();
    }

    @Transactional
    public void capNhat(Integer adminId, Integer nguoiDungId, boolean capQuyen) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(adminId);
        if (!phanQuyenCuocHopService.coVaiTro(adminId, "ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ quản trị viên được cấp quyền Văn phòng");
        }
        if (!capQuyen) {
            jdbc.sql("DELETE FROM thu_ky_van_phong WHERE nguoi_dung_id = :id").param("id", nguoiDungId).update();
            realtime.baoDanhMucNhomThayDoi();
            return;
        }
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!phanQuyenCuocHopService.coVaiTro(nguoiDungId, "THU_KY")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ cấp quyền Văn phòng cho tài khoản thư ký");
        }
        jdbc.sql("""
                INSERT INTO thu_ky_van_phong (nguoi_dung_id, nguoi_cap_id) VALUES (:id, :admin)
                ON DUPLICATE KEY UPDATE nguoi_cap_id = :admin
                """).param("id", nguoiDungId).param("admin", adminId).update();
        realtime.baoDanhMucNhomThayDoi();
    }
}
