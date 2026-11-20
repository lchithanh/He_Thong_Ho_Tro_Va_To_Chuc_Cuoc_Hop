package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KhachMoiCuocHop;

public interface KhachMoiCuocHopRepository
        extends JpaRepository<KhachMoiCuocHop, Integer> {

    List<KhachMoiCuocHop> findByCuocHop_CuocHopIdOrderByNgayMoiDescKhachMoiCuocHopIdDesc(Integer cuocHopId);

    boolean existsByCuocHop_CuocHopIdAndKhachMoi_EmailIgnoreCase(Integer cuocHopId, String email);

    boolean existsByCuocHop_CuocHopIdAndTrangThaiMoiNot(
            Integer cuocHopId, String trangThaiMoi);
}
