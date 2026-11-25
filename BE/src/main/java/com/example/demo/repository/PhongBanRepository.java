
package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PhongBan;

@Repository
public interface PhongBanRepository
        extends JpaRepository<PhongBan, Integer> {

    boolean existsByTenPhongBanIgnoreCase(String tenPhongBan);

    boolean existsByTenPhongBanIgnoreCaseAndPhongBanIdNot(
            String tenPhongBan,
            Integer phongBanId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select pb from PhongBan pb where pb.phongBanId = :id")
    Optional<PhongBan> khoaTheoId(@Param("id") Integer id);

    List<PhongBan> findByTrangThaiOrderByTenPhongBanAsc(String trangThai);

    List<PhongBan> findByTruongPhongId(Integer truongPhongId);
}
