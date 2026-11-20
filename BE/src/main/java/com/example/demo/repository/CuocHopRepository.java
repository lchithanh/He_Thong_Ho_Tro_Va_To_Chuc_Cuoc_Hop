package com.example.demo.repository;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;

@Repository
public interface CuocHopRepository extends JpaRepository<CuocHop, Integer> {

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
	                WHERE yc.cuocHop = ch
	                  AND yc.thuKyBanGiao.nguoiDungId = :nguoiDungId
	                  AND yc.loaiYeuCau = :loai
	                  AND yc.trangThai = :daDuyet
	                  AND yc.yeuCauPheDuyetId = (
	                      SELECT MAX(moi.yeuCauPheDuyetId)
	                      FROM YeuCauPheDuyet moi
	                      WHERE moi.cuocHop = ch AND moi.loaiYeuCau = :loai
	                        AND (
	                            ch.trangThai IN ('DA_DUYET', 'DANG_DIEN_RA', 'DA_KET_THUC')
	                            OR (moi.trangThai = :daDuyet AND EXISTS (
	                                SELECT lich.lichHopId FROM LichHop lich
	                                WHERE lich = moi.lichHop AND lich.thoiGianXacNhan IS NOT NULL
	                            ) AND NOT EXISTS (
	                                SELECT ls FROM LichSuPheDuyet ls
	                                WHERE ls.yeuCauPheDuyet = moi AND ls.hanhDong = 'DONG_Y_TRINH_CAP_TREN'
	                            ))
	                        )
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



