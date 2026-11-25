package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ThanhVienCuocHop;
import com.example.demo.entity.ThanhVienCuocHopId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Repository
public interface ThanhVienCuocHopRepository
        extends JpaRepository<ThanhVienCuocHop, ThanhVienCuocHopId>,
                ThanhVienCuocHopThemMoiRepository {

    boolean existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungIdAndVaiTroTaiCuocHop(
            Integer cuocHopId,
            Integer nguoiDungId,
            String vaiTroTaiCuocHop
    );

    boolean existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungId(
            Integer cuocHopId,
            Integer nguoiDungId
    );

    List<ThanhVienCuocHop> findByCuocHop_CuocHopIdAndVaiTroTaiCuocHop(
            Integer cuocHopId,
            String vaiTroTaiCuocHop
    );

    List<ThanhVienCuocHop> findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(
            Integer cuocHopId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT tv
            FROM ThanhVienCuocHop tv
            WHERE tv.id = :id
            """)
    Optional<ThanhVienCuocHop> findByIdForUpdate(
            @Param("id") ThanhVienCuocHopId id
    );
    long countByCuocHop_CuocHopIdAndTrangThaiThamDu(
            Integer cuocHopId,
            String trangThaiThamDu
    );
    
    List<ThanhVienCuocHop> findById_NguoiDungIdAndVaiTroTaiCuocHopAndTrangThaiMoi(
            Integer nguoiDungId, String vaiTroTaiCuocHop, String trangThaiMoi);
    
    List<ThanhVienCuocHop> findById_NguoiDungIdAndVaiTroTaiCuocHopAndTrangThaiMoiIn(
            Integer nguoiDungId, String vaiTroTaiCuocHop, List<String> trangThaiMoi);

    @Query("""
            SELECT tv
            FROM ThanhVienCuocHop tv
            JOIN FETCH tv.cuocHop ch
            LEFT JOIN FETCH ch.nguoiChuTri
            WHERE tv.id.nguoiDungId = :nguoiDungId
              AND tv.trangThaiMoi = 'DA_XAC_NHAN'
              AND ch.trangThai = 'DANG_DIEN_RA'
            ORDER BY ch.thoiGianBatDauThucTe DESC, ch.cuocHopId DESC
            """)
    List<ThanhVienCuocHop> findCuocHopDangDienRaDaXacNhan(
            @Param("nguoiDungId") Integer nguoiDungId);
}

interface ThanhVienCuocHopThemMoiRepository {

    void themMoi(ThanhVienCuocHop thanhVien);
}

class ThanhVienCuocHopThemMoiRepositoryImpl
        implements ThanhVienCuocHopThemMoiRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void themMoi(ThanhVienCuocHop thanhVien) {
        entityManager.persist(thanhVien);
    }
}