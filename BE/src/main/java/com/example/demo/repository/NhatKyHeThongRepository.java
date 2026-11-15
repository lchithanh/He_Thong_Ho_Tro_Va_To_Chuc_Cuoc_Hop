package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.NhatKyHeThong;

@Repository
public interface NhatKyHeThongRepository
        extends JpaRepository<NhatKyHeThong, Integer> {

    // Lấy nhật ký do một người dùng thực hiện
    List<NhatKyHeThong> findByNguoiDung_NguoiDungId(
            Integer nguoiDungId
    );

    // Lấy nhật ký theo hành động
    List<NhatKyHeThong> findByHanhDong(String hanhDong);

    // Lấy nhật ký theo loại đối tượng
    List<NhatKyHeThong> findByDoiTuong(String doiTuong);

    // Lấy nhật ký của một bản ghi cụ thể
    List<NhatKyHeThong> findByDoiTuongAndDoiTuongId(
            String doiTuong,
            Integer doiTuongId
    );
}