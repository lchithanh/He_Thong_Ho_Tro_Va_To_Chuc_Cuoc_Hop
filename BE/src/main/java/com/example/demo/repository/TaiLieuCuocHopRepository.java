package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TaiLieuCuocHop;

@Repository
public interface TaiLieuCuocHopRepository
        extends JpaRepository<TaiLieuCuocHop, Integer> {

    // Lấy danh sách tài liệu thuộc một cuộc họp
    List<TaiLieuCuocHop> findByCuocHop_CuocHopId(Integer cuocHopId);

    // Lấy tài liệu theo trạng thái xử lý
    List<TaiLieuCuocHop> findByTrangThaiXuLy(String trangThaiXuLy);

    // Lấy tài liệu thuộc một cuộc họp và trạng thái cụ thể
    List<TaiLieuCuocHop> findByCuocHop_CuocHopIdAndTrangThaiXuLy(
            Integer cuocHopId,
            String trangThaiXuLy
    );
    
    boolean existsByCuocHop_CuocHopIdAndLoaiTaiLieu(
            Integer cuocHopId,
            String loaiTaiLieu);
    
    List<TaiLieuCuocHop> findByCuocHop_CuocHopIdAndLoaiTaiLieuOrderByTaiLieuIdAsc(
            Integer cuocHopId, String loaiTaiLieu);
    
    
}
//Các chức năng Repository hỗ trợ:
//- Lấy toàn bộ tài liệu.
//- Lấy tài liệu theo cuộc họp.
//- Lọc tài liệu theo trạng thái xử lý.
//- Lọc tài liệu theo cuộc họp và trạng thái.