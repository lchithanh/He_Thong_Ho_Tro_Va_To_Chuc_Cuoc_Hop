package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PhienBanBienBan;

@Repository
public interface PhienBanBienBanRepository
        extends JpaRepository<PhienBanBienBan, Integer> {

    // Danh sách phiên bản, mới nhất trước.
    List<PhienBanBienBan>
            findByBienBan_BienBanIdOrderBySoPhienBanDesc(
                    Integer bienBanId);

    // Phiên bản có số phiên bản lớn nhất.
    Optional<PhienBanBienBan>
            findFirstByBienBan_BienBanIdOrderBySoPhienBanDesc(
                    Integer bienBanId);

    @Query("""
            SELECT COALESCE(MAX(pb.soPhienBan), 0)
            FROM PhienBanBienBan pb
            WHERE pb.bienBan.bienBanId = :bienBanId
            """)
    Integer timSoPhienBanLonNhat(
            @Param("bienBanId") Integer bienBanId);
}