-- MySQL 8. Áp dụng một lần sau các migration ngày 20261125,
-- hoặc trực tiếp trên bản sao lưu quan_ly_cuoc_hop (20).sql.
-- Chạy 20261126_cap_nhat_rang_buoc_duyet_hop_nhom.sql trước file này.
ALTER TABLE nhom_lien_phong_de_xuat
    ADD COLUMN muc_dich_hop VARCHAR(25) NOT NULL DEFAULT 'HOP_THUONG',
    ADD COLUMN thanh_vien_moi_them JSON NULL,
    ADD COLUMN email_khach_ngoai JSON NULL;

ALTER TABLE nhom_lien_phong_doi_nguoi
    ADD COLUMN truong_phong_xac_nhan_id INT NULL,
    ADD CONSTRAINT fk_nlp_dn_truong_phong_xac_nhan
        FOREIGN KEY (truong_phong_xac_nhan_id) REFERENCES nguoi_dung (nguoi_dung_id) ON DELETE SET NULL;

START TRANSACTION;

-- Hồ sơ lãnh đạo theo quy trình cũ vẫn cần Chủ tịch xét nếu trình lại.
UPDATE nhom_lien_phong_de_xuat
SET muc_dich_hop = 'LANH_DAO_CU'
WHERE cap_duyet = 'LANH_DAO';

-- Người đứng đầu phòng đã trực tiếp lập đề xuất được tính là đã xác nhận.
UPDATE nhom_lien_phong_doi_nguoi d
JOIN phong_ban p ON p.phong_ban_id = d.phong_ban_id AND p.truong_phong_id = d.nguoi_yeu_cau_id
SET d.truong_phong_xac_nhan_id = d.nguoi_yeu_cau_id
WHERE d.trang_thai = 'CHO_XU_LY';

-- Giữ lịch sử và tập tin của lần trình trước. Thư ký nhóm trình lại
-- hồ sơ đang đi giữa các bước cũ theo tuyến duyệt mới.
UPDATE yeu_cau_phe_duyet yc
JOIN nhom_lien_phong_de_xuat dx ON dx.cuoc_hop_id = yc.cuoc_hop_id
JOIN cuoc_hop ch ON ch.cuoc_hop_id = dx.cuoc_hop_id
SET yc.trang_thai = 'DA_HUY'
WHERE yc.loai_yeu_cau = 'KE_HOACH_CUOC_HOP'
  AND yc.trang_thai = 'CHO_DUYET'
  AND ch.trang_thai = 'DANG_CHUAN_BI'
  AND ch.buoc_xu_ly_hien_tai IN
      ('CHO_TRUONG_NHOM', 'CHO_GUI_CAP_TREN', 'CHO_VAN_PHONG_DUYET',
       'CHO_LANH_DAO', 'CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY');

UPDATE de_xuat_cuoc_hop dt
JOIN cuoc_hop ch ON ch.cuoc_hop_id = dt.cuoc_hop_nguon_id
JOIN nhom_lien_phong_de_xuat dx ON dx.cuoc_hop_id = ch.cuoc_hop_id
SET dt.buoc_xu_ly_hien_tai = 'LAP_KE_HOACH'
WHERE ch.trang_thai = 'DANG_CHUAN_BI'
  AND ch.buoc_xu_ly_hien_tai IN
      ('CHO_TRUONG_NHOM', 'CHO_GUI_CAP_TREN', 'CHO_VAN_PHONG_DUYET',
       'CHO_LANH_DAO', 'CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY');

UPDATE cuoc_hop ch
JOIN nhom_lien_phong_de_xuat dx ON dx.cuoc_hop_id = ch.cuoc_hop_id
SET ch.buoc_xu_ly_hien_tai = 'LAP_KE_HOACH'
WHERE ch.trang_thai = 'DANG_CHUAN_BI'
  AND ch.buoc_xu_ly_hien_tai IN
      ('CHO_TRUONG_NHOM', 'CHO_GUI_CAP_TREN', 'CHO_VAN_PHONG_DUYET',
       'CHO_LANH_DAO', 'CHO_THU_KY_LANH_DAO', 'THU_KY_LANH_DAO_XU_LY');

COMMIT;

DROP TABLE IF EXISTS thu_ky_van_phong;
