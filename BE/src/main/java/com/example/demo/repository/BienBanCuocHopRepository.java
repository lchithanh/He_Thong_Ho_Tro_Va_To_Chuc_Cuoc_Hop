package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.BienBanCuocHop;

import jakarta.persistence.LockModeType;

@Repository
public interface BienBanCuocHopRepository
        extends JpaRepository<BienBanCuocHop, Integer> {

    List<BienBanCuocHop> findByTrangThaiOrderByBienBanIdDesc(String trangThai);

    boolean existsByCuocHop_CuocHopId(Integer cuocHopId);

    Optional<BienBanCuocHop> findByCuocHop_CuocHopId(
            Integer cuocHopId);

    // Khóa biên bản trong lúc cập nhật để tránh trùng số phiên bản.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT bb
            FROM BienBanCuocHop bb
            WHERE bb.bienBanId = :bienBanId
            """)
    Optional<BienBanCuocHop> findByIdForUpdate(
            @Param("bienBanId") Integer bienBanId);
}