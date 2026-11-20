package com.example.demo.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.ZipInputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.NoiDungThuMoi.TepDinhKem;

import jakarta.mail.MessagingException;

record TepThuMoi(String tenTep, byte[] noiDung, String kieuTep) {
    private static final int TOI_DA = 2 * 1024 * 1024;

    static TepThuMoi doc(TepDinhKem tep, Integer lichHopId) {
        if (tep == null) return null;
        if (tep.lichHopId() != null && !Objects.equals(tep.lichHopId(), lichHopId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Lịch họp đã đổi. Tải lại lịch và tạo lại giấy mời.");
        }
        String ten = tep.tenTep().strip();
        if (ten.contains("/") || ten.contains("\\") || ten.codePoints().anyMatch(Character::isISOControl)) {
            throw loi("Tên tệp đính kèm không hợp lệ");
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(tep.duLieuBase64());
        } catch (IllegalArgumentException ex) {
            throw loi("Không đọc được nội dung tệp đính kèm");
        }
        if (bytes.length == 0 || bytes.length > TOI_DA) {
            throw loi("Tệp đính kèm phải có dung lượng tối đa 2 MB");
        }
        String tenThuong = ten.toLowerCase(Locale.ROOT);
        if (tenThuong.endsWith(".pdf") && bytes.length >= 5
                && "%PDF-".equals(new String(bytes, 0, 5, StandardCharsets.US_ASCII))) {
            return new TepThuMoi(ten, bytes, "application/pdf");
        }
        if (tenThuong.endsWith(".docx") && laWord(bytes)) {
            return new TepThuMoi(ten, bytes,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }
        throw loi("Tệp đính kèm phải là PDF hoặc Word .docx hợp lệ");
    }

    private static boolean laWord(byte[] bytes) {
        boolean coLoaiNoiDung = false;
        boolean coVanBan = false;
        int tongKichThuoc = 0;
        byte[] boDem = new byte[4096];
        try (var zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            for (var muc = zip.getNextEntry(); muc != null; muc = zip.getNextEntry()) {
                String tenMuc = muc.getName();
                coLoaiNoiDung |= "[Content_Types].xml".equals(tenMuc);
                coVanBan |= "word/document.xml".equals(tenMuc);
                for (int soByte = zip.read(boDem); soByte != -1; soByte = zip.read(boDem)) {
                    tongKichThuoc += soByte;
                    if (tongKichThuoc > 16 * TOI_DA) return false;
                }
            }
        } catch (IOException | IllegalArgumentException ex) {
            return false;
        }
        return coLoaiNoiDung && coVanBan;
    }

    void dinhKem(MimeMessageHelper helper) throws MessagingException {
        helper.addAttachment(tenTep, new ByteArrayResource(noiDung), kieuTep);
    }

    private static ResponseStatusException loi(String thongBao) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, thongBao);
    }
}
