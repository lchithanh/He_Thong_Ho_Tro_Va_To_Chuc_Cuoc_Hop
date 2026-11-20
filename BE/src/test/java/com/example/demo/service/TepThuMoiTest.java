package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.request.NoiDungThuMoi.TepDinhKem;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

class TepThuMoiTest {
    private static final byte[] PDF = "%PDF-1.4\n%%EOF\n".getBytes(StandardCharsets.US_ASCII);

    @Test
    void ganTepPdfVaoEmailGiuNguyenNoiDungVaDinhDang() throws Exception {
        var tep = TepThuMoi.doc(tepPdf(17), 17);
        var message = new MimeMessage(Session.getInstance(new Properties()));
        var helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setText("Mời anh/chị tham dự họp.");
        tep.dinhKem(helper);
        message.saveChanges();

        var phanTep = ((MimeMultipart) message.getContent()).getBodyPart(1);
        assertEquals("Giay-moi.pdf", phanTep.getFileName());
        assertEquals("application/pdf", tep.kieuTep());
        assertArrayEquals(PDF, phanTep.getInputStream().readAllBytes());
    }

    @Test
    void giayMoiTheoLichCuCanTaoLai() {
        var loi = assertThrows(ResponseStatusException.class, () -> TepThuMoi.doc(tepPdf(17), 19));
        assertEquals(HttpStatus.CONFLICT, loi.getStatusCode());
    }

    private TepDinhKem tepPdf(int lichHopId) {
        return new TepDinhKem("Giay-moi.pdf", Base64.getEncoder().encodeToString(PDF), lichHopId);
    }
}
