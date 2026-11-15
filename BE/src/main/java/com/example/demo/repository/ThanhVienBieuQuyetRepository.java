package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.ThanhVienBieuQuyet;
import com.example.demo.entity.ThanhVienBieuQuyetId;

public interface ThanhVienBieuQuyetRepository
        extends JpaRepository<ThanhVienBieuQuyet, ThanhVienBieuQuyetId> {

    @Modifying
    @Query(value = """
            INSERT INTO thanh_vien_bieu_quyet (bieu_quyet_id, nguoi_dung_id)
            SELECT bq.bieu_quyet_id, tv.nguoi_dung_id
            FROM bieu_quyet bq
            JOIN thanh_vien_cuoc_hop tv ON tv.cuoc_hop_id = bq.cuoc_hop_id
            WHERE bq.bieu_quyet_id = :bieuQuyetId
              AND tv.trang_thai_tham_du = 'CO_MAT'
              AND tv.thoi_gian_roi_cuoc_hop IS NULL
            """, nativeQuery = true)
    int chotDanhSachBieuQuyet(@Param("bieuQuyetId") Integer bieuQuyetId);

    @Modifying
    @Query("""
            DELETE FROM ThanhVienBieuQuyet tvb
            WHERE tvb.id.bieuQuyetId = :bieuQuyetId
              AND NOT EXISTS (
                  SELECT tv
                  FROM ThanhVienCuocHop tv
                  WHERE tv.id.cuocHopId = :cuocHopId
                    AND tv.id.nguoiDungId = tvb.id.nguoiDungId
                    AND tv.trangThaiThamDu = 'CO_MAT'
                    AND tv.thoiGianRoiCuocHop IS NULL
              )
            """)
    int loaiNguoiKhongCoMatKhoiDanhSach(
            @Param("bieuQuyetId") Integer bieuQuyetId,
            @Param("cuocHopId") Integer cuocHopId);

    @Query("""
            SELECT COUNT(tv)
            FROM ThanhVienBieuQuyet tv
            WHERE tv.id.bieuQuyetId = :bieuQuyetId
              AND tv.id.nguoiDungId = :nguoiDungId
            """)
    long demThanhVienTrongLuot(
            @Param("bieuQuyetId") Integer bieuQuyetId,
            @Param("nguoiDungId") Integer nguoiDungId);

    @Query("""
            SELECT COUNT(tv)
            FROM ThanhVienBieuQuyet tv
            WHERE tv.id.bieuQuyetId = :bieuQuyetId
            """)
    long demDanhSachBieuQuyet(@Param("bieuQuyetId") Integer bieuQuyetId);
}