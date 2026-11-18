package com.example.demo.repository;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Set;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.response.CuocHopDangDienRaResponse;
import com.example.demo.entity.CuocHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;

@Repository
public interface CuocHopRepository extends JpaRepository<CuocHop, Integer> {

    String NOI_CUOC_HOP_VOI_NGUOI_PHU_TRACH = """
            left join hop.nguoiChuTri chuTri
            left join hop.thuKyCuocHop thuKy
            """;

    String PHAM_VI_DOC_VAN_BAN_DA_PHAT_HANH = """
            and (
                chuTri.nguoiDungId = :nguoiDungId
                or thuKy.nguoiDungId = :nguoiDungId
                or exists (
                    select 1 from ThanhVienCuocHop tv
                    where tv.cuocHop = hop and tv.id.nguoiDungId = :nguoiDungId
                )
                or exists (
                    select 1 from YeuCauPheDuyet yc
                    join yc.thuKyBanGiao nguoiNhan
                    where yc.cuocHop = hop
                      and yc.loaiYeuCau = :loaiKeHoach and yc.trangThai = :daDuyet
                      and nguoiNhan.nguoiDungId = :nguoiDungId
                      and yc.yeuCauPheDuyetId = (
                          select max(lanTrinh.yeuCauPheDuyetId) from YeuCauPheDuyet lanTrinh
                          where lanTrinh.cuocHop = hop
                            and lanTrinh.loaiYeuCau = :loaiKeHoach and lanTrinh.trangThai = :daDuyet
                      )
                )
            )
            """;

    @Query("""
            select case when count(hop) > 0 then true else false end
            from CuocHop hop
            """ + NOI_CUOC_HOP_VOI_NGUOI_PHU_TRACH + """
            where hop.cuocHopId = :cuocHopId
            """ + PHAM_VI_DOC_VAN_BAN_DA_PHAT_HANH)
    boolean coQuyenDocVanBanDaPhatHanh(
            @Param("cuocHopId") Integer cuocHopId,
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("loaiKeHoach") LoaiYeuCau loaiKeHoach,
            @Param("daDuyet") TrangThai daDuyet);

    @Query("""
            select new com.example.demo.dto.response.CuocHopDangDienRaResponse(
                ch.cuocHopId, ch.tieuDe, ch.loaiHinh, chuTri.hoTen,
                case when chuTri.nguoiDungId = :nguoiDungId then true else false end,
                ch.thoiGianBatDauThucTe, ch.mucTieu, ch.noiDungDuKien,
                tv.trangThaiThamDu, tv.thoiGianDiemDanh, tv.thoiGianRoiCuocHop)
            from CuocHop ch
            left join ch.nguoiChuTri chuTri
            left join ch.danhSachThanhVien tv on tv.id.nguoiDungId = :nguoiDungId
            where ch.trangThai = 'DANG_DIEN_RA'
              and ch.thoiGianBatDauThucTe is not null and ch.thoiGianKetThucThucTe is null
              and (chuTri.nguoiDungId = :nguoiDungId or tv.trangThaiMoi in :trangThaiMoiHopLe)
            order by ch.thoiGianBatDauThucTe desc, ch.cuocHopId desc
            """)
    List<CuocHopDangDienRaResponse> timCuocHopDangDienRaCuaToi(
            @Param("nguoiDungId") Integer nguoiDungId,
            @Param("trangThaiMoiHopLe") Set<String> trangThaiMoiHopLe);

	@Query("""
		    SELECT ch
		    FROM CuocHop ch
		    LEFT JOIN ch.nguoiDeXuat ndx
		    WHERE ndx.nguoiDungId = :nguoiDungId
		       OR (
		           ch.trangThai <> 'NHAP'
		           AND EXISTS (
		               SELECT tv.id
		               FROM ThanhVienCuocHop tv
		               WHERE tv.cuocHop = ch
		                 AND tv.nguoiDung.nguoiDungId = :nguoiDungId
		                 AND tv.vaiTroTaiCuocHop = 'THU_KY'
		           )
		       )
		    ORDER BY ch.cuocHopId DESC
		    """)
		List<CuocHop> findDeXuatLienQuan(
		        @Param("nguoiDungId") Integer nguoiDungId
		);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from CuocHop c where c.cuocHopId = :cuocHopId")
	Optional<CuocHop> timTheoIdDeCapNhat(@Param("cuocHopId") Integer cuocHopId);
	
	
	@Query("""
		    SELECT ch
		    FROM CuocHop ch
		    LEFT JOIN ch.nguoiChuTri nct
		    WHERE ch.nguoiDeXuat.nguoiDungId = :nguoiDungId
		       OR (ch.trangThai <> 'NHAP' AND nct.nguoiDungId = :nguoiDungId)
		       OR (
		           ch.trangThai <> 'NHAP'
		           AND EXISTS (
		               SELECT tv
		               FROM ThanhVienCuocHop tv
		               WHERE tv.cuocHop = ch
		                 AND tv.nguoiDung.nguoiDungId = :nguoiDungId
		                 AND tv.vaiTroTaiCuocHop IN ('THU_KY', 'THU_KY_LANH_DAO')
		           )
		       )
		    ORDER BY ch.cuocHopId DESC
		    """)
		List<CuocHop> findDeXuatCuaNguoiDeXuatHoacThuKy(
		        @Param("nguoiDungId") Integer nguoiDungId);
	
	
	@Query("""
	        SELECT ch
	        FROM CuocHop ch
	        LEFT JOIN ch.nguoiChuTri nct
	        WHERE ch.nguoiDeXuat.nguoiDungId = :nguoiDungId
	           OR (
	                ch.trangThai <> 'NHAP'
	                AND (
	                    nct.nguoiDungId = :nguoiDungId
	                    OR EXISTS (
	                        SELECT tv.id
	                        FROM ThanhVienCuocHop tv
	                        WHERE tv.cuocHop = ch
	                          AND tv.nguoiDung.nguoiDungId = :nguoiDungId
	                          AND tv.vaiTroTaiCuocHop IN ('THU_KY', 'THU_KY_LANH_DAO')
	                    )
	                )
	           )
	           OR EXISTS (
	                SELECT yc.yeuCauPheDuyetId
	                FROM YeuCauPheDuyet yc
	                LEFT JOIN yc.thuKyBanGiao thuKyNhan
	                LEFT JOIN yc.nguoiTrinh nguoiTrinh
	                WHERE yc.cuocHop = ch
	                  AND (thuKyNhan.nguoiDungId = :nguoiDungId
	                      OR (ch.trangThai = 'DANG_CHUAN_BI' AND ch.buocXuLyHienTai = 'CHO_GUI_CAP_TREN'
	                          AND yc.capPheDuyet = 'TRUONG_PHONG' AND nguoiTrinh.nguoiDungId = :nguoiDungId))
	                  AND yc.loaiYeuCau = :loai
	                  AND yc.trangThai = :daDuyet
	                  AND yc.yeuCauPheDuyetId = (
	                      SELECT MAX(moi.yeuCauPheDuyetId)
	                      FROM YeuCauPheDuyet moi
	                      WHERE moi.cuocHop = ch AND moi.loaiYeuCau = :loai
	                  )
	           )
	        ORDER BY ch.cuocHopId DESC
	        """)
	List<CuocHop> findDeXuatCuaNguoiDeXuatHoacThuKy(
	        @Param("nguoiDungId") Integer nguoiDungId,
	        @Param("loai") LoaiYeuCau loai,
	        @Param("daDuyet") TrangThai daDuyet);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT ch FROM CuocHop ch WHERE ch.cuocHopId = :id")
	Optional<CuocHop> findByIdForUpdate(@Param("id") Integer id);
}
//Repository này cung cấp sẵn các thao tác:
//- Lấy danh sách cuộc họp.
//- Lấy chi tiết cuộc họp theo ID.
//- Thêm cuộc họp.
//- Cập nhật cuộc họp.
//- Xóa cuộc họp.



