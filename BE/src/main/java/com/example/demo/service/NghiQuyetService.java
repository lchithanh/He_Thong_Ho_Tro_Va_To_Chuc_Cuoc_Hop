package com.example.demo.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.TaiLieuCuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NghiQuyetService {
    private static final Path THU_MUC = Path.of("uploads", "nghi-quyet");
    private final JdbcClient jdbc;
    private final PhanCongXuLyKeHoachService phanCong;
    private final LuuTaiLieuCuocHopService luuTaiLieu;
    private final TaiLieuCuocHopRepository taiLieuRepository;
    private final CuocHopRepository cuocHopRepository;
    private final ThanhVienCuocHopRepository thanhVienRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final CapNhatHoSoRealtime realtime;

    public record NoiDung(Integer nghiQuyetChaId, String tieuDe, String noiDung) {}
    public record Muc(Integer nghiQuyetId, Integer nghiQuyetChaId, int cap,
            String tieuDe, String noiDung, String trangThai, String lyDo, Integer taiLieuId,
            LocalDateTime ngayCongBo) {}

    public record CuocHopCoNghiQuyet(Integer cuocHopId, String tieuDe) {}

    @Transactional(readOnly = true)
    public List<CuocHopCoNghiQuyet> cuaToi(Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        return jdbc.sql("""
                SELECT ch.cuoc_hop_id, ch.tieu_de FROM cuoc_hop ch
                JOIN thanh_vien_cuoc_hop tv ON tv.cuoc_hop_id = ch.cuoc_hop_id
                WHERE tv.nguoi_dung_id = ? AND ch.trang_thai <> 'DA_HUY'
                  AND (tv.trang_thai_tham_du = 'CO_MAT'
                       OR tv.trang_thai_moi IN ('CHO_PHAN_HOI', 'DA_XAC_NHAN'))
                  AND EXISTS (SELECT 1 FROM nghi_quyet nq
                    WHERE nq.cuoc_hop_id = ch.cuoc_hop_id AND nq.ngay_cong_bo IS NOT NULL)
                ORDER BY ch.cuoc_hop_id DESC
                """).param(nguoiDungId).query(CuocHopCoNghiQuyet.class).list();
    }

    @Transactional(readOnly = true)
    public List<Muc> xem(Integer cuocHopId, Integer nguoiDungId) {
        CuocHop hop = xemCuocHop(cuocHopId, nguoiDungId);
        return jdbc.sql("""
                SELECT * FROM nghi_quyet WHERE cuoc_hop_id = ?
                AND (? OR ngay_cong_bo IS NOT NULL)
                ORDER BY nghi_quyet_id
                """).params(cuocHopId, duocQuanLy(hop, nguoiDungId)).query(Muc.class).list();
    }

    private CuocHop xemCuocHop(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        var hop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!duocQuanLy(hop, nguoiDungId) && thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream().noneMatch(tv -> nguoiDungId.equals(tv.getId().getNguoiDungId())
                        && ("CO_MAT".equals(tv.getTrangThaiThamDu()) || "CHO_PHAN_HOI".equals(tv.getTrangThaiMoi())
                            || "DA_XAC_NHAN".equals(tv.getTrangThaiMoi())))) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn chưa có quyền xem nghị quyết của cuộc họp");
        }
        return hop;
    }

    private boolean duocQuanLy(CuocHop hop, Integer nguoiDungId) {
        return ThaoTacCuocHop.laNguoiChuTri(hop, nguoiDungId) || phanCong.laThuKyToChuc(hop, nguoiDungId);
    }

    @Transactional(readOnly = true)
    public LuuTaiLieuCuocHopService.TepTaiLieu taiTep(Integer cuocHopId, Integer id, Integer nguoiDungId) {
        var hop = xemCuocHop(cuocHopId, nguoiDungId);
        var muc = tim(cuocHopId, id);
        if (muc.ngayCongBo() == null && !duocQuanLy(hop, nguoiDungId)) {
            throw loi(HttpStatus.NOT_FOUND, "Nghị quyết chưa được công bố");
        }
        return docTep(cuocHopId, muc.taiLieuId());
    }

    LuuTaiLieuCuocHopService.TepTaiLieu docTep(Integer cuocHopId, Integer taiLieuId) {
        if (taiLieuId == null) throw loi(HttpStatus.NOT_FOUND, "Nghị quyết chưa có tệp");
        var taiLieu = taiLieuRepository.findById(taiLieuId)
                .filter(tl -> cuocHopId.equals(tl.getCuocHop().getCuocHopId()) && "NGHI_QUYET".equals(tl.getLoaiTaiLieu()))
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy tệp nghị quyết"));
        return luuTaiLieu.docTep(taiLieu, THU_MUC);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void congBo(Integer cuocHopId, Integer id, Integer nguoiDungId) {
        var hop = khoaHop(cuocHopId, nguoiDungId);
        var muc = tim(cuocHopId, id);
        if (!"NHAP".equals(muc.trangThai()) || muc.ngayCongBo() != null || muc.taiLieuId() == null) {
            throw loi(HttpStatus.CONFLICT, "Chỉ công bố nghị quyết nháp đã có tệp");
        }
        jdbc.sql("UPDATE nghi_quyet SET ngay_cong_bo = CURRENT_TIMESTAMP WHERE nghi_quyet_id = ?").param(id).update();
        realtime.baoCuocHop(hop, nguoiDungId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer tao(Integer cuocHopId, Integer nguoiDungId, NoiDung request, MultipartFile tep) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId);
        kiemTraNoiDung(request);
        Integer chaId = request.nghiQuyetChaId();
        int cap = chaId == null ? 1 : tim(cuocHopId, chaId).cap() + 1;
        if (cap > 4) throw loi(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ đến cấp nghị quyết chắt");
        Integer taiLieuId = luuTep(hop, nguoiDungId, tep);
        var khoa = new GeneratedKeyHolder();
        jdbc.sql("""
                INSERT INTO nghi_quyet (cuoc_hop_id, nghi_quyet_cha_id, cap, tieu_de, noi_dung, tai_lieu_id)
                VALUES (:hop, :cha, :cap, :tieuDe, '', :taiLieuId)
                """).param("hop", cuocHopId).param("cha", chaId, java.sql.Types.INTEGER)
                .param("cap", cap).param("tieuDe", request.tieuDe().strip()).param("taiLieuId", taiLieuId)
                .update(khoa, "nghi_quyet_id");
        realtime.baoCuocHop(hop, nguoiDungId);
        return Objects.requireNonNull(khoa.getKey()).intValue();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void sua(Integer cuocHopId, Integer id, Integer nguoiDungId, NoiDung request, MultipartFile tep) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId);
        kiemTraNoiDung(request);
        Muc muc = tim(cuocHopId, id);
        if (!List.of("NHAP", "YEU_CAU_CHINH_SUA").contains(muc.trangThai())
                || !Objects.equals(request.nghiQuyetChaId(), muc.nghiQuyetChaId())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ sửa nội dung nghị quyết nháp hoặc đã yêu cầu chỉnh sửa");
        }
        Integer taiLieuId = luuTep(hop, nguoiDungId, tep);
        jdbc.sql("""
                UPDATE nghi_quyet SET tieu_de = ?, noi_dung = '', tai_lieu_id = ?, trang_thai = 'NHAP',
                ly_do = NULL, ngay_cong_bo = NULL WHERE nghi_quyet_id = ?
                """).params(request.tieuDe().strip(), taiLieuId, id).update();
        realtime.baoCuocHop(hop, nguoiDungId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void yeuCauSua(Integer cuocHopId, Integer id, Integer nguoiDungId, String lyDo) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId);
        Muc muc = tim(cuocHopId, id);
        if (!List.of("KHONG_THONG_QUA", "CHUA_DAT_DA_SO").contains(muc.trangThai())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ yêu cầu sửa nghị quyết chưa được thông qua");
        }
        if (lyDo == null || lyDo.isBlank() || lyDo.strip().length() > 500) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập lý do chỉnh sửa tối đa 500 ký tự");
        }
        jdbc.sql("UPDATE nghi_quyet SET trang_thai = 'YEU_CAU_CHINH_SUA', ly_do = ? WHERE nghi_quyet_id = ?")
                .params(lyDo.strip(), id).update();
        realtime.baoCuocHop(hop, nguoiDungId);
    }

    // Bên gọi giữ khóa cuộc họp để nghị quyết và phiên biểu quyết được ghi cùng giao dịch.
    Muc ganVaoBieuQuyet(Integer cuocHopId, Integer id) {
        Muc muc = tim(cuocHopId, id);
        int daGan = jdbc.sql("""
                UPDATE nghi_quyet SET trang_thai = 'DANG_BIEU_QUYET'
                WHERE nghi_quyet_id = ? AND cuoc_hop_id = ? AND trang_thai = 'NHAP' AND ngay_cong_bo IS NOT NULL AND tai_lieu_id IS NOT NULL
                """).params(id, cuocHopId).update();
        if (daGan != 1) {
            throw loi(HttpStatus.CONFLICT, "Cần công bố tệp nghị quyết trước khi tạo phiên biểu quyết");
        }
        return muc;
    }

    String chotBieuQuyet(Integer id, long soDongY, long soKhongDongY, long coQuyenBoPhieu) {
        String ketQua = soDongY > coQuyenBoPhieu / 2 ? "THONG_QUA"
                : soKhongDongY > coQuyenBoPhieu / 2 ? "KHONG_THONG_QUA" : "CHUA_DAT_DA_SO";
        capNhatTrangThai(id, ketQua);
        return ketQua;
    }

    private Muc tim(Integer cuocHopId, Integer id) {
        return jdbc.sql("SELECT * FROM nghi_quyet WHERE cuoc_hop_id = ? AND nghi_quyet_id = ?")
                .params(cuocHopId, id).query(Muc.class).optional()
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy nghị quyết trong cuộc họp"));
    }

    private void capNhatTrangThai(Integer id, String trangThai) {
        jdbc.sql("UPDATE nghi_quyet SET trang_thai = ? WHERE nghi_quyet_id = ?").params(trangThai, id).update();
    }

    private CuocHop khoaHop(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        var hop = cuocHopRepository.findByIdForUpdate(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        if (!duocQuanLy(hop, nguoiDungId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ chủ trì hoặc thư ký tổ chức được quản lý nghị quyết");
        }
        if (!ThaoTacCuocHop.coTheLapLich(hop) && !ThaoTacCuocHop.dangDienRa(hop)) {
            throw loi(HttpStatus.CONFLICT, "Cần cuộc họp đã duyệt hoặc đang diễn ra");
        }
        return hop;
    }

    private Integer luuTep(CuocHop hop, Integer nguoiDungId, MultipartFile tep) {
        String ten = tep == null ? null : tep.getOriginalFilename();
        if (ten == null || !ten.toLowerCase(Locale.ROOT).endsWith(".pdf") || tep.isEmpty()) {
            throw loi(HttpStatus.BAD_REQUEST, "Chọn tệp nghị quyết PDF");
        }
        try (var input = tep.getInputStream()) {
            if (!"%PDF-".equals(new String(input.readNBytes(5), java.nio.charset.StandardCharsets.US_ASCII))) {
                throw loi(HttpStatus.BAD_REQUEST, "Tệp nghị quyết phải là PDF hợp lệ");
            }
        } catch (java.io.IOException ex) {
            throw loi(HttpStatus.BAD_REQUEST, "Không thể đọc tệp nghị quyết");
        }
        return luuTaiLieu.luuTaiLieu(hop, nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId),
                tep, "NGHI_QUYET", THU_MUC);
    }

    private void kiemTraNoiDung(NoiDung request) {
        if (request.tieuDe() == null || request.tieuDe().isBlank() || request.tieuDe().strip().length() > 255) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập tiêu đề tối đa 255 ký tự");
        }
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
