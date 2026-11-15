package com.example.demo.repository;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.NguoiDung;

import jakarta.persistence.LockModeType;

@Repository
public interface NguoiDungRepository
        extends JpaRepository<NguoiDung, Integer> {

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    Optional<NguoiDung> findByEmail(String email);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    boolean existsByMaNhanVien(String maNhanVien);


    Optional<NguoiDung> findByMaNhanVien(String maNhanVien);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select nd from NguoiDung nd where nd.nguoiDungId = :id")
    Optional<NguoiDung> khoaTheoId(@Param("id") Integer id);}