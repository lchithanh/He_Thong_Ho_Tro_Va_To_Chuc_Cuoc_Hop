package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NghiQuyetService {
    private final JdbcClient jdbc;
    private final CuocHopRepository cuocHopRepository;
    private final ThanhVienCuocHopRepository thanhVienRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final CapNhatHoSoRealtime realtime;

    public record NoiDung(Integer nghiQuyetChaId, String tieuDe, String noiDung) {}
    public record Muc(Integer nghiQuyetId, Integer nghiQuyetChaId, int cap,
            String tieuDe, String noiDung, String trangThai, String lyDo) {}

    @Transactional(readOnly = true)
    public List<Muc> xem(Integer cuocHopId, Integer nguoiDungId) {
        nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        var hop = cuocHopRepository.findById(cuocHopId)
                .orElseThrow(() -> loi(HttpStatus.NOT_FOUND, "Không tìm thấy cuộc họp"));
        boolean chuTri = ThaoTacCuocHop.laNguoiChuTri(hop, nguoiDungId);
        if (!chuTri && thanhVienRepository.findByCuocHop_CuocHopIdOrderById_NguoiDungIdAsc(cuocHopId)
                .stream().noneMatch(tv -> nguoiDungId.equals(tv.getId().getNguoiDungId())
                        && "CO_MAT".equals(tv.getTrangThaiThamDu()))) {
            throw loi(HttpStatus.FORBIDDEN, "Bạn chưa có quyền xem nghị quyết của cuộc họp");
        }
        return jdbc.sql("""
                SELECT * FROM nghi_quyet WHERE cuoc_hop_id = ?
                AND (? OR trang_thai NOT IN ('NHAP', 'DANG_BIEU_QUYET'))
                ORDER BY nghi_quyet_id
                """).params(cuocHopId, chuTri).query(Muc.class).list();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer tao(Integer cuocHopId, Integer nguoiDungId, NoiDung request) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId);
        kiemTraNoiDung(request);
        Integer chaId = request.nghiQuyetChaId();
        int cap = chaId == null ? 1 : tim(cuocHopId, chaId).cap() + 1;
        if (cap > 4) throw loi(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ đến cấp nghị quyết chắt");
        var khoa = new GeneratedKeyHolder();
        jdbc.sql("""
                INSERT INTO nghi_quyet (cuoc_hop_id, nghi_quyet_cha_id, cap, tieu_de, noi_dung)
                VALUES (:hop, :cha, :cap, :tieuDe, :noiDung)
                """).param("hop", cuocHopId).param("cha", chaId, java.sql.Types.INTEGER)
                .param("cap", cap).param("tieuDe", request.tieuDe().strip()).param("noiDung", request.noiDung().strip())
                .update(khoa, "nghi_quyet_id");
        realtime.baoCuocHop(hop, nguoiDungId);
        return Objects.requireNonNull(khoa.getKey()).intValue();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void sua(Integer cuocHopId, Integer id, Integer nguoiDungId, NoiDung request) {
        CuocHop hop = khoaHop(cuocHopId, nguoiDungId);
        kiemTraNoiDung(request);
        Muc muc = tim(cuocHopId, id);
        if (!List.of("NHAP", "YEU_CAU_CHINH_SUA").contains(muc.trangThai())
                || !Objects.equals(request.nghiQuyetChaId(), muc.nghiQuyetChaId())) {
            throw loi(HttpStatus.CONFLICT, "Chỉ sửa nội dung nghị quyết nháp hoặc đã yêu cầu chỉnh sửa");
        }
        jdbc.sql("UPDATE nghi_quyet SET tieu_de = ?, noi_dung = ?, trang_thai = 'NHAP', ly_do = NULL WHERE nghi_quyet_id = ?")
                .params(request.tieuDe().strip(), request.noiDung().strip(), id).update();
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
                WHERE nghi_quyet_id = ? AND cuoc_hop_id = ? AND trang_thai = 'NHAP'
                """).params(id, cuocHopId).update();
        if (daGan != 1) {
            throw loi(HttpStatus.CONFLICT, "Chỉ nghị quyết nháp được đưa ra biểu quyết");
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
        if (!ThaoTacCuocHop.laNguoiChuTri(hop, nguoiDungId)) {
            throw loi(HttpStatus.FORBIDDEN, "Chỉ chủ trì được quản lý nghị quyết");
        }
        if (!ThaoTacCuocHop.coTheLapLich(hop) && !ThaoTacCuocHop.dangDienRa(hop)) {
            throw loi(HttpStatus.CONFLICT, "Cần cuộc họp đã duyệt hoặc đang diễn ra");
        }
        return hop;
    }

    private void kiemTraNoiDung(NoiDung request) {
        if (request.tieuDe() == null || request.tieuDe().isBlank() || request.tieuDe().strip().length() > 255
                || request.noiDung() == null || request.noiDung().isBlank() || request.noiDung().length() > 10000) {
            throw loi(HttpStatus.BAD_REQUEST, "Nhập tiêu đề tối đa 255 ký tự và nội dung tối đa 10.000 ký tự");
        }
    }

    private ResponseStatusException loi(HttpStatus status, String message) {
        return new ResponseStatusException(status, message);
    }
}
