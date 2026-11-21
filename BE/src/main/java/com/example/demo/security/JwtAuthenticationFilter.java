
package com.example.demo.security;

import java.io.IOException;
import java.util.List;

import com.example.demo.repository.NguoiDungVaiTroRepository;

import io.jsonwebtoken.JwtException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final NguoiDungVaiTroRepository vaiTroRepository;

    public JwtAuthenticationFilter(JwtService jwtService, NguoiDungVaiTroRepository vaiTroRepository) {
        this.jwtService = jwtService;
        this.vaiTroRepository = vaiTroRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            xacThuc(request, authorization.substring(7));
        }
        filterChain.doFilter(request, response);
    }

    private void xacThuc(HttpServletRequest request, String token) {
        JwtService.PhienJwt phien;
        try {
            phien = jwtService.docPhien(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return;
        }

        vaiTroRepository.layMaVaiTroDangHoatDong(phien.tenDangNhap())
                .filter(vaiTro -> vaiTro.equals(phien.maVaiTro()))
                .ifPresent(vaiTro -> {
                    User nguoiDung = new User(phien.tenDangNhap(), "",
                            List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro)));
                    var xacThuc = UsernamePasswordAuthenticationToken.authenticated(
                            nguoiDung, null, nguoiDung.getAuthorities());
                    xacThuc.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(xacThuc);
                });
    }
}
