package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.NhiemVu;

@Repository
public interface NhiemVuRepository extends JpaRepository<NhiemVu, Integer> {

    // Lấy nhiệm vụ thuộc một cuộc họp
    List<NhiemVu> findByCuocHop_CuocHopId(Integer cuocHopId);

    // Lấy nhiệm vụ được giao cho một người
    List<NhiemVu> findByNguoiThucHien_NguoiDungId(Integer nguoiDungId);

    // Lấy nhiệm vụ theo trạng thái
    List<NhiemVu> findByTrangThai(String trangThai);

    // Lấy nhiệm vụ theo người thực hiện và trạng thái
    List<NhiemVu> findByNguoiThucHien_NguoiDungIdAndTrangThai(
            Integer nguoiDungId,
            String trangThai
    );
}