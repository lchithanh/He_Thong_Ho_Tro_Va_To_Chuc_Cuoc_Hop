package com.example.demo.controller;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = {
        BauChonController.class, NghiQuyetController.class, LoiMoiKhachController.class,
        LichHopController.class, BieuQuyetController.class
})
public class LoiNghiepVuCuocHopHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> phanHoi(ResponseStatusException loi) {
        return ResponseEntity.status(loi.getStatusCode()).headers(loi.getHeaders()).body(loi.getBody());
    }
}
