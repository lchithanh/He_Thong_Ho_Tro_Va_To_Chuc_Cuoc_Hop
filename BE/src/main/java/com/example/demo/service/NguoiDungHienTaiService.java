package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.NguoiDungRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NguoiDungHienTaiService {

    private final NguoiDungRepository nguoiDungRepository;

    public NguoiDung layTaiKhoanHoatDong(Integer nguoiDungId) {
        if (nguoiDungId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bạn cần đăng nhập"
            );
        }

        NguoiDung nguoiDung = nguoiDungRepository
                .findById(nguoiDungId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Không tìm thấy tài khoản"
                ));

        if (!dangHoatDong(nguoiDung)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Tài khoản không hoạt động"
            );
        }

        return nguoiDung;
    }
    
    public Integer layNguoiDungId(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Bạn cần đăng nhập");
        }

        return nguoiDungRepository.findByTenDangNhap(authentication.getName())
                .map(NguoiDung::getNguoiDungId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Không tìm thấy tài khoản"));
    }

    static boolean dangHoatDong(NguoiDung nguoiDung) {
        return "HOAT_DONG".equals(nguoiDung.getTrangThai());
    }

    static boolean thuocPhongBan(NguoiDung nguoiDung, Integer phongBanId) {
        return phongBanId != null
                && nguoiDung.getPhongBan() != null
                && phongBanId.equals(nguoiDung.getPhongBan().getPhongBanId());
    }

    static boolean laTruongPhongCuaPhongBanMinh(NguoiDung nguoiDung) {
        return nguoiDung.getNguoiDungId() != null
                && nguoiDung.getPhongBan() != null
                && nguoiDung.getNguoiDungId().equals(nguoiDung.getPhongBan().getTruongPhongId());
    }
}
