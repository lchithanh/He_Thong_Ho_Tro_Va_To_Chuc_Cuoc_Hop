package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;

import jakarta.persistence.LockModeType;

public interface YeuCauPheDuyetRepository
        extends JpaRepository<YeuCauPheDuyet, Integer> {

    Page<YeuCauPheDuyet> findByTrangThaiOrderByThoiGianTrinhDescYeuCauPheDuyetIdDesc(
            TrangThai trangThai,
            Pageable pageable);

    boolean existsByCuocHop_CuocHopIdAndLoaiYeuCauAndTrangThai(
            Integer cuocHopId,
            LoaiYeuCau loaiYeuCau,
            TrangThai trangThai);

    boolean existsByYeuCauTruoc_YeuCauPheDuyetId(Integer yeuCauTruocId);

    @Query("""
            select yc
            from YeuCauPheDuyet yc
            join fetch yc.cuocHop ch
            join fetch yc.lichHop lh
            where yc.loaiYeuCau = :loaiYeuCau
              and yc.trangThai = :trangThai
              and (:kiemTraBuocChoDuyet = false
                   or ch.buocXuLyHienTai = 'CHO_LANH_DAO')
              and exists (
                  select tv
                  from ThanhVienCuocHop tv
                  where tv.cuocHop = ch
                    and tv.nguoiDung.nguoiDungId = :nguoiDungId
                    and tv.vaiTroTaiCuocHop = 'LANH_DAO'
              )
            order by yc.thoiGianTrinh desc, yc.yeuCauPheDuyetId desc
            """)
    List<YeuCauPheDuyet> findKeHoachCuaLanhDao(
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("loaiYeuCau") LoaiYeuCau loaiYeuCau,
            @Param("trangThai") TrangThai trangThai,
            @Param("kiemTraBuocChoDuyet") boolean kiemTraBuocChoDuyet);
    
    List<YeuCauPheDuyet>
    findByCuocHop_CuocHopIdAndLoaiYeuCauOrderByThoiGianTrinhDescYeuCauPheDuyetIdDesc(
            Integer cuocHopId,
            LoaiYeuCau loaiYeuCau);
    
    boolean existsByLichHop_LichHopIdAndLoaiYeuCauAndTrangThai(
            Integer lichHopId,
            LoaiYeuCau loaiYeuCau,
            TrangThai trangThai);
    
    
    Optional<YeuCauPheDuyet>
    findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
            Integer cuocHopId,
            LoaiYeuCau loaiYeuCau
    );

    @Query("""
            SELECT yc FROM YeuCauPheDuyet yc
            JOIN FETCH yc.lichHop lh
            WHERE yc.cuocHop.cuocHopId = :cuocHopId
              AND yc.loaiYeuCau = :loai AND yc.trangThai = :trangThai
              AND lh.thoiGianXacNhan IS NOT NULL
              AND NOT EXISTS (
                  SELECT ls FROM LichSuPheDuyet ls
                  WHERE ls.yeuCauPheDuyet = yc AND ls.hanhDong = 'DONG_Y_TRINH_CAP_TREN'
              )
            ORDER BY yc.yeuCauPheDuyetId DESC
            """)
    List<YeuCauPheDuyet> timLanDuyetCoLichDaXacNhan(
            @Param("cuocHopId") Integer cuocHopId, @Param("loai") LoaiYeuCau loai,
            @Param("trangThai") TrangThai trangThai, Pageable gioiHan);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT yc FROM YeuCauPheDuyet yc WHERE yc.yeuCauPheDuyetId = :id")
    Optional<YeuCauPheDuyet> findByIdForUpdate(@Param("id") Integer id);

    @Query("""
            SELECT yc FROM YeuCauPheDuyet yc
            WHERE yc.loaiYeuCau = :loai
              AND yc.trangThai = :trangThai
              AND yc.nguoiDuyet.nguoiDungId = :nguoiDungId
            ORDER BY yc.yeuCauPheDuyetId DESC
            """)
    List<YeuCauPheDuyet> timTheoNguoiDuyet(
            @Param("loai") LoaiYeuCau loai,
            @Param("trangThai") TrangThai trangThai,
            @Param("nguoiDungId") Integer nguoiDungId
    );

    @Query("""
            SELECT yc FROM YeuCauPheDuyet yc
            WHERE yc.loaiYeuCau = :loai
              AND yc.trangThai = :trangThai
              AND yc.thuKyTiepNhan.nguoiDungId = :nguoiDungId
              AND yc.cuocHop.buocXuLyHienTai IN :cacBuoc
            ORDER BY yc.yeuCauPheDuyetId DESC
            """)
    List<YeuCauPheDuyet> timTheoThuKyTiepNhan(
            @Param("loai") LoaiYeuCau loai,
            @Param("trangThai") TrangThai trangThai,
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("cacBuoc") List<String> cacBuoc
    );
    
    
    List<YeuCauPheDuyet> findByCuocHop_NguoiChuTri_NguoiDungIdAndLoaiYeuCauAndTrangThaiOrderByThoiGianTrinhDesc(
            Integer nguoiDungId, LoaiYeuCau loaiYeuCau, TrangThai trangThai);
}
