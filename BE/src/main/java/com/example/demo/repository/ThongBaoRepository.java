package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ThongBao;

@Repository
public interface ThongBaoRepository
        extends JpaRepository<ThongBao, Integer> {

    // Lấy thông báo của một người dùng
    List<ThongBao> findByNguoiDung_NguoiDungId(Integer nguoiDungId);

    // Lấy thông báo chưa đọc của một người dùng
    List<ThongBao> findByNguoiDung_NguoiDungIdAndDaDocFalse(
            Integer nguoiDungId
    );

    // Lấy thông báo liên quan đến một cuộc họp
    List<ThongBao> findByCuocHop_CuocHopId(Integer cuocHopId);
    Slice<ThongBao> findByNguoiDung_NguoiDungIdOrderByThongBaoIdDesc(Integer nguoiDungId, Pageable pageable);

    long countByNguoiDung_NguoiDungIdAndDaDocFalse(Integer nguoiDungId);

    Optional<ThongBao> findByThongBaoIdAndNguoiDung_NguoiDungId(Integer thongBaoId, Integer nguoiDungId);

    @Modifying
    @Query("""
            update ThongBao tb set tb.daDoc = true, tb.thoiGianDoc = :luc
            where tb.nguoiDung.nguoiDungId = :id and tb.daDoc = false
            """)
    int danhDauTatCaDaDoc(@Param("id") Integer nguoiDungId, @Param("luc") LocalDateTime luc);
}
