-- MySQL 8. Chạy sau migration duyệt họp nhóm theo mục đích.
-- Chuyển lần trình đang ở Văn phòng đến người duyệt đã được chỉ định.
START TRANSACTION;

UPDATE cuoc_hop ch
JOIN nhom_lien_phong_de_xuat nhom ON nhom.cuoc_hop_id = ch.cuoc_hop_id
JOIN yeu_cau_phe_duyet yc ON yc.cuoc_hop_id = ch.cuoc_hop_id
    AND yc.loai_yeu_cau = 'KE_HOACH_CUOC_HOP'
LEFT JOIN yeu_cau_phe_duyet lan_moi ON lan_moi.cuoc_hop_id = yc.cuoc_hop_id
    AND lan_moi.loai_yeu_cau = yc.loai_yeu_cau
    AND lan_moi.yeu_cau_phe_duyet_id > yc.yeu_cau_phe_duyet_id
LEFT JOIN de_xuat_cuoc_hop de_xuat ON de_xuat.cuoc_hop_nguon_id = ch.cuoc_hop_id
SET ch.buoc_xu_ly_hien_tai = CASE yc.cap_phe_duyet
        WHEN 'TRUONG_NHOM' THEN 'CHO_TRUONG_NHOM'
        WHEN 'LANH_DAO' THEN 'CHO_LANH_DAO'
    END,
    de_xuat.buoc_xu_ly_hien_tai = CASE yc.cap_phe_duyet
        WHEN 'TRUONG_NHOM' THEN 'CHO_TRUONG_NHOM'
        WHEN 'LANH_DAO' THEN 'CHO_LANH_DAO'
    END,
    yc.thu_ky_tiep_nhan_id = NULL
WHERE ch.trang_thai = 'DANG_CHUAN_BI'
    AND ch.buoc_xu_ly_hien_tai IN ('CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY')
    AND yc.trang_thai = 'CHO_DUYET'
    AND yc.cap_phe_duyet IN ('TRUONG_NHOM', 'LANH_DAO')
    AND yc.nguoi_duyet_id IS NOT NULL
    AND lan_moi.yeu_cau_phe_duyet_id IS NULL;

COMMIT;
