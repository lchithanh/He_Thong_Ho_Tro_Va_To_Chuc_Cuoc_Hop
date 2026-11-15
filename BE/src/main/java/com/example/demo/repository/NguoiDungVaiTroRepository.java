
package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.NguoiDungVaiTro;

public interface NguoiDungVaiTroRepository
        extends JpaRepository<NguoiDungVaiTro, Integer> {

    Optional<NguoiDungVaiTro> findByNguoiDungId(Integer nguoiDungId);

    Optional<NguoiDungVaiTro> findByNguoiDungIdAndVaiTro_MaVaiTro(
            Integer nguoiDungId,
            String maVaiTro
    );
}
