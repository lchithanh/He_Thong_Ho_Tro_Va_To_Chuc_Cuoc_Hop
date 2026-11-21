
package com.example.demo.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.NguoiDungVaiTro;

public interface NguoiDungVaiTroRepository
        extends JpaRepository<NguoiDungVaiTro, Integer> {

    Optional<NguoiDungVaiTro> findByNguoiDungId(Integer nguoiDungId);

    @Query("""
            select ndvt.vaiTro.maVaiTro from NguoiDungVaiTro ndvt
            where ndvt.nguoiDung.tenDangNhap = :tenDangNhap
              and ndvt.nguoiDung.trangThai = 'HOAT_DONG'
              and ndvt.vaiTro.trangThai = 'HOAT_DONG'
            """)
    Optional<String> layMaVaiTroDangHoatDong(@Param("tenDangNhap") String tenDangNhap);

    @Query("""
            select ndvt.nguoiDungId from NguoiDungVaiTro ndvt
            where ndvt.nguoiDungId in :ids
              and ndvt.nguoiDung.trangThai = 'HOAT_DONG'
              and ndvt.vaiTro.trangThai = 'HOAT_DONG'
              and ndvt.vaiTro.maVaiTro = 'ADMIN'
            """)
    List<Integer> locQuanTriVienHoatDong(@Param("ids") Collection<Integer> ids);

    Optional<NguoiDungVaiTro> findByNguoiDungIdAndVaiTro_MaVaiTro(
            Integer nguoiDungId,
            String maVaiTro
    );
}
