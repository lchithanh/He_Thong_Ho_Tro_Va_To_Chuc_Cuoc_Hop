package com.example.demo.repository;

import java.util.List;

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
}