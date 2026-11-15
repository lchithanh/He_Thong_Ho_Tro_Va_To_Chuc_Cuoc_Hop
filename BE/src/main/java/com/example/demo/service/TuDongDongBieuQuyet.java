package com.example.demo.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.repository.BieuQuyetRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TuDongDongBieuQuyet {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(TuDongDongBieuQuyet.class);

    private final BieuQuyetRepository bieuQuyetRepository;
    private final BieuQuyetService bieuQuyetService;

    @Scheduled(fixedDelayString = "${app.bieu-quyet.tu-dong-dong-delay-ms:5000}")
    public void dongCacPhienHetHan() {
        for (var phien : bieuQuyetRepository.timPhienDangMoDaHetHan(LocalDateTime.now())) {
            try {
                bieuQuyetService.dongBieuQuyetHetHan(
                        phien.getCuocHopId(),
                        phien.getBieuQuyetId()
                );
            } catch (RuntimeException ex) {
                LOGGER.warn("Không thể tự đóng biểu quyết {}", phien.getBieuQuyetId(), ex);
            }
        }
    }
}