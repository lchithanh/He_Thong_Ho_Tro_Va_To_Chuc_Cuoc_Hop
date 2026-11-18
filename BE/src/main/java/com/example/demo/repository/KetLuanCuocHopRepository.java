package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.KetLuanCuocHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;

public interface KetLuanCuocHopRepository
        extends JpaRepository<KetLuanCuocHop, Integer> {

    boolean existsByCuocHop_CuocHopId(Integer cuocHopId);

    Optional<KetLuanCuocHop> findByCuocHop_CuocHopId(Integer cuocHopId);
    List<KetLuanCuocHop> findByCuocHop_NguoiChuTri_NguoiDungIdAndCuocHop_TrangThaiAndTrangThai(
            Integer nguoiChuTriId,
            String trangThaiCuocHop,
            String trangThaiKetLuan);
    
    
    @Query("""
            select kl
            from KetLuanCuocHop kl
            join kl.cuocHop hop
            """ + CuocHopRepository.NOI_CUOC_HOP_VOI_NGUOI_PHU_TRACH + """
            where kl.trangThai = 'DA_PHAT_HANH'
            """ + CuocHopRepository.PHAM_VI_DOC_VAN_BAN_DA_PHAT_HANH + """
            order by kl.ngayPhatHanh desc
            """)
    List<KetLuanCuocHop> timKetLuanDaPhatHanhDuocXem(
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("loaiKeHoach") LoaiYeuCau loaiKeHoach,
            @Param("daDuyet") TrangThai daDuyet);
}
