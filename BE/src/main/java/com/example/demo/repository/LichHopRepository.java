package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.LichHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Repository
public interface LichHopRepository
        extends JpaRepository<LichHop, Integer>, LichHopLamMoiRepository {

    List<LichHop> findByCuocHop_CuocHopIdOrderByThoiGianBatDauAsc(
            Integer cuocHopId
    );

    // Kiểm tra trùng với lịch đã xác nhận, bỏ qua lịch đang xử lý.
    @Query("""
            SELECT COUNT(l) > 0
            FROM LichHop l
            WHERE l.phongHop.phongHopId = :phongHopId
              AND l.lichHopId <> :lichHopId
              AND l.trangThai = 'DA_XAC_NHAN'
              AND l.thoiGianBatDau < :thoiGianKetThuc
              AND l.thoiGianKetThuc > :thoiGianBatDau
            """)
    boolean existsLichHopTrung(
            @Param("phongHopId") Integer phongHopId,
            @Param("lichHopId") Integer lichHopId,
            @Param("thoiGianBatDau") LocalDateTime thoiGianBatDau,
            @Param("thoiGianKetThuc") LocalDateTime thoiGianKetThuc
    );

    // Khi thay thế, bỏ qua cả lịch cũ và lịch mới đang xử lý.
    @Query("""
            SELECT COUNT(l) > 0
            FROM LichHop l
            WHERE l.phongHop.phongHopId = :phongHopId
              AND l.lichHopId NOT IN (:lichHopCuId, :lichHopMoiId)
              AND l.trangThai = 'DA_XAC_NHAN'
              AND l.thoiGianBatDau < :thoiGianKetThuc
              AND l.thoiGianKetThuc > :thoiGianBatDau
            """)
    boolean existsLichHopTrungKhiThayThe(
            @Param("phongHopId") Integer phongHopId,
            @Param("lichHopCuId") Integer lichHopCuId,
            @Param("lichHopMoiId") Integer lichHopMoiId,
            @Param("thoiGianBatDau") LocalDateTime thoiGianBatDau,
            @Param("thoiGianKetThuc") LocalDateTime thoiGianKetThuc
    );

    // Danh sách lịch cho người đề xuất, chủ trì hoặc thành viên.
    @Query("""
            SELECT l
            FROM LichHop l
            JOIN l.cuocHop ch
            LEFT JOIN ch.nguoiDeXuat ndx
            LEFT JOIN ch.nguoiChuTri nct
            WHERE ndx.nguoiDungId = :nguoiDungId
               OR nct.nguoiDungId = :nguoiDungId
               OR EXISTS (
                   SELECT tv.id
                   FROM ThanhVienCuocHop tv
                   WHERE tv.cuocHop = ch
                     AND tv.nguoiDung.nguoiDungId = :nguoiDungId
               )
            ORDER BY l.thoiGianBatDau ASC
            """)
    List<LichHop> findLichHopCuaNguoiDung(
            @Param("nguoiDungId") Integer nguoiDungId
    );

    // Danh sách của LichHopService, có xét người duyệt ở lần trình mới nhất.
    @Query("""
            SELECT l
            FROM LichHop l
            JOIN l.cuocHop ch
            LEFT JOIN ch.nguoiDeXuat ndx
            LEFT JOIN ch.nguoiChuTri nct
            WHERE ndx.nguoiDungId = :nguoiDungId
               OR nct.nguoiDungId = :nguoiDungId
               OR EXISTS (
                   SELECT tv.id
                   FROM ThanhVienCuocHop tv
                   WHERE tv.cuocHop = ch
                     AND tv.nguoiDung.nguoiDungId = :nguoiDungId
               )
               OR EXISTS (
                   SELECT yc.yeuCauPheDuyetId
                   FROM YeuCauPheDuyet yc
                   WHERE yc.cuocHop = ch
                     AND yc.nguoiDuyet.nguoiDungId = :nguoiDungId
                     AND yc.loaiYeuCau = :loai
                     AND yc.yeuCauPheDuyetId = (
                         SELECT MAX(ycMoi.yeuCauPheDuyetId)
                         FROM YeuCauPheDuyet ycMoi
                         WHERE ycMoi.cuocHop = ch
                           AND ycMoi.loaiYeuCau = :loai
                     )
               )
            ORDER BY l.thoiGianBatDau DESC
            """)
    List<LichHop> findLichHopCuaNguoiDung(
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("loai") LoaiYeuCau loaiYeuCau
    );

    boolean existsByCuocHop_CuocHopIdAndTrangThaiIn(
            Integer cuocHopId,
            List<String> trangThai
    );
}

interface LichHopLamMoiRepository {

    void lamMoiCuocHopVaLich(CuocHop cuocHop, LichHop lichHop);
}

class LichHopLamMoiRepositoryImpl implements LichHopLamMoiRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void lamMoiCuocHopVaLich(CuocHop cuocHop, LichHop lichHop) {
        entityManager.refresh(cuocHop, LockModeType.PESSIMISTIC_WRITE);
        entityManager.refresh(lichHop, LockModeType.PESSIMISTIC_WRITE);
    }
}