package com.example.demo.controller;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = {
        NguoiDungController.class, PhongBanController.class, PhongHopController.class,
        ThietBiController.class, PhanCongThuKyController.class
})
public class LoiQuanTriHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> phanHoiLoiNghiepVu(ResponseStatusException loi) {
        return ResponseEntity.status(loi.getStatusCode()).body(loi.getBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> phanHoiLoiDuLieu(MethodArgumentNotValidException loi) {
        ProblemDetail chiTiet = loi.getBody();
        var truongLoi = loi.getBindingResult().getFieldError();
        if (truongLoi != null) {
            chiTiet.setDetail(truongLoi.getDefaultMessage());
        }
        return ResponseEntity.status(loi.getStatusCode()).body(chiTiet);
    }
}
