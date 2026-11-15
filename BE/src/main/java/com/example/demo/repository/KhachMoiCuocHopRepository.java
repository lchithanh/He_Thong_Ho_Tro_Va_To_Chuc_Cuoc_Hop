package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KhachMoiCuocHop;

public interface KhachMoiCuocHopRepository
        extends JpaRepository<KhachMoiCuocHop, Integer> {

    boolean existsByCuocHop_CuocHopIdAndTrangThaiMoiNot(
            Integer cuocHopId, String trangThaiMoi);
}
