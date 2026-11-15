package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.KetQuaXuLyAI;

@Repository
public interface KetQuaXuLyAIRepository
        extends JpaRepository<KetQuaXuLyAI, Integer> {

    // Lấy kết quả AI theo cuộc họp
    List<KetQuaXuLyAI> findByCuocHop_CuocHopId(Integer cuocHopId);

    // Lấy kết quả AI theo trạng thái
    List<KetQuaXuLyAI> findByTrangThai(String trangThai);

    // Lấy kết quả AI theo người yêu cầu
    List<KetQuaXuLyAI> findByNguoiYeuCau_NguoiDungId(Integer nguoiDungId);

    // Lấy kết quả AI theo cuộc họp và loại xử lý
    List<KetQuaXuLyAI> findByCuocHop_CuocHopIdAndLoaiXuLy(
            Integer cuocHopId,
            String loaiXuLy
    );
}
/*
 * Repository này hỗ trợ theo dõi các yêu cầu AI, chẳng hạn: - Tóm tắt nội dung
 * cuộc họp. - Phân tích ghi chú hoặc nội dung Speech-to-Text. - Gợi ý nhiệm vụ
 * sau cuộc họp. - Theo dõi trạng thái xử lý và kết quả AI.
 */