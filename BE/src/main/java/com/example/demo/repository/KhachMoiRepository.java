package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KhachMoi;

public interface KhachMoiRepository extends JpaRepository<KhachMoi, Integer> {


	// Bên trong interface KhachMoiRepository
	Optional<KhachMoi> findFirstByEmailIgnoreCaseOrderByKhachMoiIdAsc(String email);}
