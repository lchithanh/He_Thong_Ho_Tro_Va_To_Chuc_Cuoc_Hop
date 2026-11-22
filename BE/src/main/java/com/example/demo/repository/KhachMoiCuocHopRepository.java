package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KhachMoiCuocHop;

public interface KhachMoiCuocHopRepository
        extends JpaRepository<KhachMoiCuocHop, Integer> {

    Optional<KhachMoiCuocHop> findByMaTruyCapSha256(String maTruyCapSha256);

    List<KhachMoiCuocHop> findByCuocHop_CuocHopIdOrderByNgayMoiDescKhachMoiCuocHopIdDesc(Integer cuocHopId);

    Optional<KhachMoiCuocHop> findFirstByCuocHop_CuocHopIdAndKhachMoi_EmailIgnoreCaseOrderByKhachMoiCuocHopIdAsc(
            Integer cuocHopId, String email);

    boolean existsByCuocHop_CuocHopIdAndTrangThaiMoiNot(
            Integer cuocHopId, String trangThaiMoi);
}
