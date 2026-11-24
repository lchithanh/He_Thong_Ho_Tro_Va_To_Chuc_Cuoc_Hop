package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.LuaChonBieuQuyet;

@Repository
public interface LuaChonBieuQuyetRepository
        extends JpaRepository<LuaChonBieuQuyet, Integer> {

    @Modifying
    @Query("DELETE FROM LuaChonBieuQuyet lc WHERE lc.bieuQuyet.bieuQuyetId = :bieuQuyetId")
    void xoaTheoBieuQuyet(@Param("bieuQuyetId") Integer bieuQuyetId);

    // Lấy các lựa chọn của một biểu quyết
    List<LuaChonBieuQuyet> findByBieuQuyet_BieuQuyetIdOrderByThuTuHienThiAsc(
            Integer bieuQuyetId
    );
    Optional<LuaChonBieuQuyet> findByLuaChonIdAndBieuQuyet_BieuQuyetId(
            Integer luaChonId,
            Integer bieuQuyetId
    );
}