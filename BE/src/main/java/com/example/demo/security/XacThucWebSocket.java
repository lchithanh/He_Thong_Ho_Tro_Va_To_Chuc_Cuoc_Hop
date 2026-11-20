package com.example.demo.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.entity.NguoiDung;
import com.example.demo.repository.NguoiDungRepository;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class XacThucWebSocket implements ChannelInterceptor {

    public static final String KENH_RIENG = "/user/queue/cap-nhat";

    private final JwtService jwtService;
    private final NguoiDungRepository nguoiDungRepository;
    private final Map<String, Instant> hanTheoPhien = new ConcurrentHashMap<>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor header = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (header == null) {
            throw new AccessDeniedException("Khung STOMP không hợp lệ");
        }

        StompCommand lenh = header.getCommand();
        if (lenh == StompCommand.CONNECT || lenh == StompCommand.STOMP) {
            xacThucKetNoi(header);
        } else if (lenh == StompCommand.SUBSCRIBE) {
            if (header.getUser() == null || !phienConHan(header.getSessionId())
                    || !KENH_RIENG.equals(header.getDestination())) {
                throw new AccessDeniedException("Bạn không có quyền đăng ký kênh này");
            }
        } else if (lenh != StompCommand.UNSUBSCRIBE && lenh != StompCommand.DISCONNECT
                && header.getMessageType() != SimpMessageType.HEARTBEAT) {
            throw new AccessDeniedException("Lệnh STOMP không được phép");
        }
        return message;
    }

    private void xacThucKetNoi(StompHeaderAccessor header) {
        String authorization = header.getFirstNativeHeader("Authorization");
        header.removeNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ") || header.getSessionId() == null
                || hanTheoPhien.containsKey(header.getSessionId())) {
            throw new AccessDeniedException("Yêu cầu JWT hợp lệ để kết nối");
        }

        JwtService.PhienJwt phien;
        try {
            phien = jwtService.docPhien(authorization.substring(7));
        } catch (JwtException | IllegalArgumentException ex) {
            throw new AccessDeniedException("Phiên đăng nhập không hợp lệ", ex);
        }

        String nguoiDungId = nguoiDungRepository.findByTenDangNhap(phien.tenDangNhap())
                .filter(nguoi -> "HOAT_DONG".equals(nguoi.getTrangThai()))
                .map(NguoiDung::getNguoiDungId)
                .map(String::valueOf)
                .orElseThrow(() -> new AccessDeniedException("Tài khoản không hoạt động"));

        header.setUser(() -> nguoiDungId);
        hanTheoPhien.put(header.getSessionId(), phien.hetHanLuc());
    }

    private boolean phienConHan(String phienId) {
        Instant hetHan = phienId == null ? null : hanTheoPhien.get(phienId);
        return hetHan != null && hetHan.isAfter(Instant.now());
    }

    public ChannelInterceptor kiemTraHanKhiGui() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                if (SimpMessageHeaderAccessor.getMessageType(message.getHeaders()) == SimpMessageType.MESSAGE
                        && !phienConHan(SimpMessageHeaderAccessor.getSessionId(message.getHeaders()))) {
                    return null;
                }
                return message;
            }
        };
    }

    @EventListener
    public void huyPhien(SessionDisconnectEvent suKien) {
        hanTheoPhien.remove(suKien.getSessionId());
    }
}
