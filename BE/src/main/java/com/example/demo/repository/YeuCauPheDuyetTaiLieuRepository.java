package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.YeuCauPheDuyetTaiLieu;
import com.example.demo.entity.YeuCauPheDuyetTaiLieuId;

public interface YeuCauPheDuyetTaiLieuRepository
        extends JpaRepository<YeuCauPheDuyetTaiLieu, YeuCauPheDuyetTaiLieuId> {

    @Query("""
            SELECT DISTINCT lk.id.taiLieuId
            FROM YeuCauPheDuyetTaiLieu lk
            WHERE lk.yeuCauPheDuyet.cuocHop.cuocHopId = :cuocHopId
            """)
    List<Integer> timIdTaiLieuDaTrinh(@Param("cuocHopId") Integer cuocHopId);

    @Query("""
            SELECT lk.id.taiLieuId
            FROM YeuCauPheDuyetTaiLieu lk
            WHERE lk.id.yeuCauPheDuyetId = :yeuCauId
            ORDER BY lk.id.taiLieuId
            """)
    List<Integer> timIdTaiLieuTheoLanTrinh(@Param("yeuCauId") Integer yeuCauId);

    boolean existsById_TaiLieuId(Integer taiLieuId);
}
