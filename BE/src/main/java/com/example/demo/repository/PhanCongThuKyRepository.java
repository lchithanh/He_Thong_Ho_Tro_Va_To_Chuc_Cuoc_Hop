package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.PhanCongThuKy;

import jakarta.persistence.LockModeType;

@Repository
public interface PhanCongThuKyRepository
        extends JpaRepository<PhanCongThuKy, Integer> {

    // Lấy phân công của một lãnh đạo hoặc trưởng phòng.
    @Override
    @EntityGraph(attributePaths = {
        "nguoiDuocHoTro",
        "thuKy",
        "nguoiPhanCong"
    })
    Optional<PhanCongThuKy> findById(Integer nguoiDuocHoTroId);

    // Danh sách người mà một thư ký đang hỗ trợ.
    @EntityGraph(attributePaths = {
        "nguoiDuocHoTro",
        "thuKy",
        "nguoiPhanCong"
    })
    List<PhanCongThuKy>
        findByThuKy_NguoiDungIdOrderByNgayPhanCongDesc(Integer thuKyId);

    // Danh sách toàn bộ phân công, mới nhất trước.
    @EntityGraph(attributePaths = {
        "nguoiDuocHoTro",
        "thuKy",
        "nguoiPhanCong"
    })
    List<PhanCongThuKy> findAllByOrderByNgayPhanCongDesc();

    // Kiểm tra người dùng có đang được phân công làm thư ký mặc định.
    boolean existsByThuKy_NguoiDungId(Integer thuKyId);
    
 // Người dùng có đang là trưởng phòng của phòng ban nào không?
    @Query(value = """
        SELECT COUNT(*)
        FROM phong_ban
        WHERE truong_phong_id = :nguoiDungId
        """, nativeQuery = true)
    long demPhongBanDangLamTruongPhong(
            @Param("nguoiDungId") Integer nguoiDungId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PhanCongThuKy> findFirstByThuKy_NguoiDungIdAndNguoiDuocHoTroIdNot(
            Integer thuKyId,
            Integer nguoiDuocHoTroId);
}