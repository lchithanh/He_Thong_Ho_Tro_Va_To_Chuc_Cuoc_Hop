package com.example.demo.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.BieuQuyet;

@Repository
public interface BieuQuyetRepository extends JpaRepository<BieuQuyet, Integer> {

    // Lấy danh sách biểu quyết thuộc một cuộc họp
    List<BieuQuyet> findByCuocHop_CuocHopId(Integer cuocHopId);

    // Lấy biểu quyết theo trạng thái
    List<BieuQuyet> findByTrangThai(String trangThai);

    // Lấy biểu quyết do một người tạo
    List<BieuQuyet> findByNguoiTao_NguoiDungId(Integer nguoiDungId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT bq
            FROM BieuQuyet bq
            WHERE bq.bieuQuyetId = :bieuQuyetId
              AND bq.cuocHop.cuocHopId = :cuocHopId
            """)
    Optional<BieuQuyet> findByIdAndCuocHopIdForUpdate(
            @Param("bieuQuyetId") Integer bieuQuyetId,
            @Param("cuocHopId") Integer cuocHopId
    );
    @Query("""
            SELECT bq
            FROM BieuQuyet bq
            JOIN FETCH bq.cuocHop ch
            WHERE bq.trangThai = 'DANG_MO'
              AND (:cuocHopId IS NULL OR ch.cuocHopId = :cuocHopId)
              AND bq.thoiGianBatDau <= :hienTai
              AND bq.thoiGianKetThuc > :hienTai
              AND EXISTS (
                  SELECT tv.id.cuocHopId
                  FROM ThanhVienCuocHop tv
                  WHERE tv.id.cuocHopId = ch.cuocHopId
                    AND tv.id.nguoiDungId = :nguoiDungId
                    AND tv.trangThaiThamDu = 'CO_MAT'
                    AND tv.thoiGianRoiCuocHop IS NULL
              )
            ORDER BY bq.thoiGianKetThuc ASC, bq.bieuQuyetId ASC
            """)
    List<BieuQuyet> timBieuQuyetDangMoCuaThanhVien(
            @Param("cuocHopId") Integer cuocHopId,
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("hienTai") LocalDateTime hienTai
    );
    
    @Query("""
            SELECT bq
            FROM BieuQuyet bq
            JOIN FETCH bq.cuocHop
            WHERE bq.trangThai = 'DA_DONG'
              AND (:cuocHopId IS NULL OR bq.cuocHop.cuocHopId = :cuocHopId)
              AND EXISTS (
                  SELECT tv.id.nguoiDungId
                  FROM ThanhVienCuocHop tv
                  WHERE tv.id.cuocHopId = bq.cuocHop.cuocHopId
                    AND tv.id.nguoiDungId = :nguoiDungId
              )
            ORDER BY bq.thoiGianKetThuc DESC, bq.bieuQuyetId DESC
            """)
    List<BieuQuyet> timBieuQuyetDaDongCuaThanhVien(
            @Param("cuocHopId") Integer cuocHopId,
            @Param("nguoiDungId") Integer nguoiDungId
    );
    

    List<BieuQuyet> findByCuocHop_CuocHopIdOrderByBieuQuyetIdDesc(Integer cuocHopId);
    
    interface PhienHetHan {
        Integer getCuocHopId();
        Integer getBieuQuyetId();
    }

    @Query("""
            select b.cuocHop.cuocHopId as cuocHopId,
                   b.bieuQuyetId as bieuQuyetId
            from BieuQuyet b
            where b.trangThai = 'DANG_MO'
              and b.thoiGianKetThuc <= :hienTai
            order by b.thoiGianKetThuc, b.bieuQuyetId
            """)
    List<PhienHetHan> timPhienDangMoDaHetHan(
            @Param("hienTai") LocalDateTime hienTai
    );
}