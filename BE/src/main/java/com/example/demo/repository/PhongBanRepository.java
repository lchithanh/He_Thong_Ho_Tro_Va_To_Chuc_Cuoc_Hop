
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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

    List<PhongBan> findByTruongPhongId(Integer truongPhongId);
}
