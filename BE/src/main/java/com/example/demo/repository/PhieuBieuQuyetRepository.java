package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.PhieuBieuQuyet;

import jakarta.persistence.LockModeType;

public interface PhieuBieuQuyetRepository
        extends JpaRepository<PhieuBieuQuyet, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PhieuBieuQuyet>
    findFirstByBieuQuyet_BieuQuyetIdAndNguoiDung_NguoiDungId(
            Integer bieuQuyetId,
            Integer nguoiDungId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PhieuBieuQuyet> findFirstByBieuQuyet_BieuQuyetIdAndMaPhieu(
            Integer bieuQuyetId,
            String maPhieu
    );
    long countByBieuQuyet_BieuQuyetId(Integer bieuQuyetId);

    @Query(value = """
            SELECT pa.lua_chon_id AS luaChonId, COUNT(*) AS soPhieu
            FROM phuong_an_bo_sung_cua_phieu pa
            JOIN phieu_bieu_quyet p ON p.phieu_bieu_quyet_id = pa.phieu_bieu_quyet_id
            WHERE p.bieu_quyet_id = :bieuQuyetId
            GROUP BY pa.lua_chon_id
            """, nativeQuery = true)
    List<SoPhieuTheoLuaChon> demPhuongAnBoSung(@Param("bieuQuyetId") Integer bieuQuyetId);

    interface SoPhieuTheoLuaChon {
        Integer getLuaChonId();
        Long getSoPhieu();
    }

    @Query("""
            SELECT p.luaChonId AS luaChonId, COUNT(p) AS soPhieu
            FROM PhieuBieuQuyet p
            WHERE p.bieuQuyet.bieuQuyetId = :bieuQuyetId
            GROUP BY p.luaChonId
            """)
    List<SoPhieuTheoLuaChon> demPhieuTheoLuaChon(
            @Param("bieuQuyetId") Integer bieuQuyetId
    );
    @Query("""
            SELECT p.lyDoKhongDongY FROM PhieuBieuQuyet p
            WHERE p.bieuQuyet.bieuQuyetId = :bieuQuyetId AND p.lyDoKhongDongY IS NOT NULL
            ORDER BY p.lyDoKhongDongY
            """)
    List<String> layLyDoKhongDongY(@Param("bieuQuyetId") Integer bieuQuyetId);
}