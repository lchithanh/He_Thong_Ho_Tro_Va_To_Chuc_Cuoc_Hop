package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.LuaChonBieuQuyet;

@Repository
public interface LuaChonBieuQuyetRepository
        extends JpaRepository<LuaChonBieuQuyet, Integer> {

    // Lấy các lựa chọn của một biểu quyết
    List<LuaChonBieuQuyet> findByBieuQuyet_BieuQuyetIdOrderByThuTuHienThiAsc(
            Integer bieuQuyetId
    );
    Optional<LuaChonBieuQuyet> findByLuaChonIdAndBieuQuyet_BieuQuyetId(
            Integer luaChonId,
            Integer bieuQuyetId
    );
}