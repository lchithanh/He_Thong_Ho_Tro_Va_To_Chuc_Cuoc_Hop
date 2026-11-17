package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.service.TaiLieuKeHoachService.TepTaiXuong;

@Service
public class XemTruocTaiLieuKeHoachService {
    private static final Logger LOG = LoggerFactory.getLogger(XemTruocTaiLieuKeHoachService.class);
    private static final long GIOI_HAN_PDF = 25L * 1024 * 1024;
    private final Semaphore luotChuyenDoi = new Semaphore(2);
    private final String lenhLibreOffice;

    public XemTruocTaiLieuKeHoachService(
            @Value("${app.preview.libreoffice:soffice}") String lenhLibreOffice) {
        this.lenhLibreOffice = lenhLibreOffice;
    }

    public byte[] taoPdf(TepTaiXuong tep) {
        String dinhDang = tep.dinhDangFile();
        if ("pdf".equals(dinhDang)) return tep.noiDung();
        if (!"doc".equals(dinhDang) && !"docx".equals(dinhDang)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Xem trước hỗ trợ tệp PDF và Word. Hãy tải tệp gốc để mở định dạng này.");
        }
        if (!luotChuyenDoi.tryAcquire()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Máy chủ đang chuyển đổi tài liệu khác. Vui lòng thử lại.");
        }

        Path thuMuc = null;
        Process tienTrinh = null;
        try {
            thuMuc = Files.createTempDirectory("xem-truoc-ke-hoach-");
            Path tepWord = Files.write(thuMuc.resolve("tai-lieu." + dinhDang), tep.noiDung());
            Path tepPdf = thuMuc.resolve("tai-lieu.pdf");
            // Dùng hồ sơ LibreOffice độc lập để mỗi lần chuyển đổi có kết quả riêng.
            tienTrinh = new ProcessBuilder(lenhLibreOffice,
                    "-env:UserInstallation=" + thuMuc.resolve("profile").toUri(),
                    "--headless", "--convert-to", "pdf:writer_pdf_Export",
                    "--outdir", thuMuc.toString(), tepWord.toString())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();
            if (!tienTrinh.waitFor(30, TimeUnit.SECONDS)) {
                throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT,
                        "Chuyển đổi tệp Word quá thời gian. Bạn có thể tải tệp gốc.");
            }
            if (tienTrinh.exitValue() != 0 || !Files.isRegularFile(tepPdf) || Files.size(tepPdf) == 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT,
                        "Không thể xem trước tệp Word này. Bạn có thể tải tệp gốc.");
            }
            if (Files.size(tepPdf) > GIOI_HAN_PDF) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                        "Bản xem trước quá lớn. Bạn có thể tải tệp gốc.");
            }
            return Files.readAllBytes(tepPdf);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Đã gián đoạn chuyển đổi tài liệu", ex);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Máy chủ chưa thể chuyển tệp Word sang PDF. Hãy kiểm tra cấu hình LibreOffice.", ex);
        } finally {
            if (tienTrinh != null && tienTrinh.isAlive()) {
                tienTrinh.descendants().forEach(ProcessHandle::destroyForcibly);
                tienTrinh.destroyForcibly();
            }
            try {
                if (thuMuc != null) FileSystemUtils.deleteRecursively(thuMuc);
            } catch (IOException ex) {
                LOG.warn("Không thể xóa thư mục chuyển đổi tài liệu", ex);
            } finally {
                luotChuyenDoi.release();
            }
        }
    }
}
