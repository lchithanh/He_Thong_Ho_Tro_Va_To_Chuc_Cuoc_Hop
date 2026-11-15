package com.example.demo.repository;

import com.example.demo.entity.PhongHop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhongHopRepository extends JpaRepository<PhongHop, Integer> {

    boolean existsByTenPhongHopIgnoreCase(String tenPhongHop);

    boolean existsByTenPhongHopIgnoreCaseAndPhongHopIdNot(
            String tenPhongHop,
            Integer phongHopId
    );
}
//Kiểm tra phòng họp đã tồn tại khi thêm mới.
//Kiểm tra trùng tên khi cập nhật, ngoại trừ chính phòng đang sửa.
//Các thao tác cơ bản: lấy danh sách, lấy chi tiết, thêm, sửa