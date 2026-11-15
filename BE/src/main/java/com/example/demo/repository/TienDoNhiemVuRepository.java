package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TienDoNhiemVu;

@Repository
public interface TienDoNhiemVuRepository
        extends JpaRepository<TienDoNhiemVu, Integer> {

    // Lấy lịch sử tiến độ của một nhiệm vụ
    List<TienDoNhiemVu> findByNhiemVu_NhiemVuIdOrderByNgayCapNhatDesc(
            Integer nhiemVuId
    );

    // Lấy lịch sử cập nhật do một người thực hiện
    List<TienDoNhiemVu> findByNguoiCapNhat_NguoiDungId(
            Integer nguoiDungId
    );
}