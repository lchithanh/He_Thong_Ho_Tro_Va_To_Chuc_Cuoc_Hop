package com.example.demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.demo.security.XacThucWebSocket;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final XacThucWebSocket xacThuc;
    private final String[] nguonChoPhep;
    private final TaskScheduler lichNhipKetNoi;

    public WebSocketConfig(XacThucWebSocket xacThuc,
            @Lazy @Qualifier("messageBrokerTaskScheduler") TaskScheduler lichNhipKetNoi,
            @Value("${app.websocket.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
            String[] nguonChoPhep) {
        this.xacThuc = xacThuc;
        this.lichNhipKetNoi = lichNhipKetNoi;
        this.nguonChoPhep = nguonChoPhep;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(nguonChoPhep);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue").setTaskScheduler(lichNhipKetNoi);
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(xacThuc);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(xacThuc.kiemTraHanKhiGui());
    }
}
