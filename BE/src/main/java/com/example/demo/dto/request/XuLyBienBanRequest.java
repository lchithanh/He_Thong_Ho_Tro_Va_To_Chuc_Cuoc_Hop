package com.example.demo.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class XuLyBienBanRequest {

    private Integer yeuCauPheDuyetId;

    // Khi trả lại, trường này chứa lý do bắt buộc.
    private String yKien;
}