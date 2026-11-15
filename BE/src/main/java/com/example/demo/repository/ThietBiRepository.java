
package com.example.demo.repository;

import com.example.demo.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Integer> {

    boolean existsByMaThietBi(String maThietBi);

    boolean existsByMaThietBiAndThietBiIdNot(
            String maThietBi,
            Integer thietBiId
    );

    Optional<ThietBi> findByMaThietBi(String maThietBi);
}
//Repository hỗ trợ:
//- Kiểm tra mã thiết bị đã tồn tại.
//- Kiểm tra mã thiết bị trùng khi cập nhật.
//- Tìm thiết bị theo mã.