package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.BienBanCuocHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;

import jakarta.persistence.LockModeType;

@Repository
public interface BienBanCuocHopRepository
        extends JpaRepository<BienBanCuocHop, Integer> {

    List<BienBanCuocHop> findByTrangThaiOrderByBienBanIdDesc(String trangThai);

    @Query("""
            select bb
            from BienBanCuocHop bb
            join fetch bb.cuocHop hop
            """ + CuocHopRepository.NOI_CUOC_HOP_VOI_NGUOI_PHU_TRACH + """
            where bb.trangThai = 'DA_PHAT_HANH'
            """ + CuocHopRepository.PHAM_VI_DOC_VAN_BAN_DA_PHAT_HANH + """
            order by bb.bienBanId desc
            """)
    List<BienBanCuocHop> timBienBanDaPhatHanhDuocXem(
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("loaiKeHoach") LoaiYeuCau loaiKeHoach,
            @Param("daDuyet") TrangThai daDuyet);

    boolean existsByCuocHop_CuocHopId(Integer cuocHopId);

    Optional<BienBanCuocHop> findByCuocHop_CuocHopId(
            Integer cuocHopId);

    // Khóa biên bản trong lúc cập nhật để tránh trùng số phiên bản.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT bb
            FROM BienBanCuocHop bb
            WHERE bb.bienBanId = :bienBanId
            """)
    Optional<BienBanCuocHop> findByIdForUpdate(
            @Param("bienBanId") Integer bienBanId);
}
