package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.LichSuPheDuyet;

@Repository
public interface LichSuPheDuyetRepository
        extends JpaRepository<LichSuPheDuyet, Integer> {

    // Lấy lịch sử phê duyệt của một cuộc họp
    List<LichSuPheDuyet> findByCuocHop_CuocHopId(Integer cuocHopId);

    // Lấy lịch sử phê duyệt của một người
    List<LichSuPheDuyet> findByNguoiPheDuyet_NguoiDungId(
            Integer nguoiDungId
    );

    // Lọc theo loại phê duyệt
    List<LichSuPheDuyet> findByLoaiPheDuyet(String loaiPheDuyet);
    
    Optional<LichSuPheDuyet>
    findFirstByYeuCauPheDuyet_YeuCauPheDuyetIdAndLoaiPheDuyetAndHanhDongOrderByThoiGianPheDuyetDescLichSuPheDuyetIdDesc(
            Integer yeuCauPheDuyetId,
            String loaiPheDuyet,
            String hanhDong);
    @Query("""
            select ls
            from LichSuPheDuyet ls
            join fetch ls.nguoiPheDuyet
            where ls.yeuCauPheDuyet.yeuCauPheDuyetId in :yeuCauIds
              and ls.loaiPheDuyet = 'KE_HOACH'
            order by ls.thoiGianPheDuyet asc, ls.lichSuPheDuyetId asc
            """)
    List<LichSuPheDuyet> findLichSuKeHoachTheoYeuCauIds(
            @Param("yeuCauIds") List<Integer> yeuCauIds);
}