
package com.example.demo.security;

import java.io.IOException;
import java.util.List;

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

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        System.out.println("========== JWT FILTER ==========");
        System.out.println("REQUEST: " + request.getRequestURI());
        System.out.println("AUTH HEADER EXISTS: " + (authHeader != null));
        System.out.println("AUTH HEADER BEARER: "
                + (authHeader != null && authHeader.startsWith("Bearer ")));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Lấy username từ JWT
            String username = jwtService.extractUsername(token);

            // Kiểm tra chưa có Authentication
            if (username != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null
                    && jwtService.isTokenValid(token, username)) {

                // Lấy vai trò từ JWT
                String role = jwtService.extractRole(token);
                System.out.println("========== JWT DEBUG ==========");
                System.out.println("USERNAME: " + username);
                System.out.println("ROLE: " + role);
                System.out.println("AUTHORITY: ROLE_" + role);
                System.out.println("================================");

                if (role == null || role.isBlank()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                User userDetails = new User(
                        username,
                        "",
                        List.of(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        )
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                System.out.println(
                	    "CURRENT AUTHORITIES: " +
                	    SecurityContextHolder.getContext()
                	        .getAuthentication()
                	        .getAuthorities()
                	);
            }

        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
