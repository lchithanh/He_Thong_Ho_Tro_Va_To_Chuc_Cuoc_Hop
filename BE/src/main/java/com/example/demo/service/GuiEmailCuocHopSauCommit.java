package com.example.demo.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.service.ThongBaoEmailCuocHop.EmailCanGui;

import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;

@Component
@Profile("gmail")
class GuiEmailCuocHopSauCommit {
    private static final Logger log = LoggerFactory.getLogger(GuiEmailCuocHopSauCommit.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final ExecutorService hangDoi = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(200), Thread.ofPlatform().name("email-cuoc-hop-", 0).factory());

    GuiEmailCuocHopSauCommit(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @TransactionalEventListener
    public void nhanSauKhiLuu(EmailCanGui email) {
        try {
            hangDoi.execute(() -> email.diaChi().forEach(diaChi -> gui(email, diaChi)));
        } catch (RejectedExecutionException ex) {
            log.warn("Chưa thể xếp email vào hàng đợi cho lịch họp {}", email.lichHopId());
        }
    }

    private void gui(EmailCanGui email, String diaChi) {
        try {
            var message = new MimeMessageHelper(mailSender.createMimeMessage(), "UTF-8");
            message.setValidateAddresses(true);
            message.setFrom(from);
            message.setTo(diaChi);
            message.setSubject(email.chuDe());
            message.setText(email.noiDung());
            mailSender.send(message.getMimeMessage());
        } catch (MailException | MessagingException ex) {
            log.warn("Không gửi được email cho lịch họp {} ({})", email.lichHopId(), ex.getClass().getSimpleName());
        }
    }

    @PreDestroy
    void dongHangDoi() {
        hangDoi.shutdownNow();
    }
}
