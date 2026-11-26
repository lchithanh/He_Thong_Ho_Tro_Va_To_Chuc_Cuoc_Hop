-- Chạy trên CSDL quan_ly_cuoc_hop trước migration duyệt theo mục đích.
-- Có thể chạy riêng nếu migration duyệt theo mục đích đã áp dụng xong.
-- Mở ràng buộc tạm thời để chuyển dữ liệu trên CSDL còn dùng cấp duyệt cũ.
SET @sql_cap_duyet_nhom = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'nhom_lien_phong_de_xuat'
          AND CONSTRAINT_NAME = 'ck_nlp_dx_cap'
          AND CONSTRAINT_TYPE = 'CHECK'
    ),
    'ALTER TABLE nhom_lien_phong_de_xuat
       DROP CHECK ck_nlp_dx_cap,
       ADD CONSTRAINT ck_nlp_dx_cap CHECK (cap_duyet IN (''VAN_PHONG'', ''LANH_DAO'', ''TRUONG_NHOM''))',
    'ALTER TABLE nhom_lien_phong_de_xuat
       ADD CONSTRAINT ck_nlp_dx_cap CHECK (cap_duyet IN (''VAN_PHONG'', ''LANH_DAO'', ''TRUONG_NHOM''))'
);

PREPARE cap_duyet_nhom_stmt FROM @sql_cap_duyet_nhom;
EXECUTE cap_duyet_nhom_stmt;
DEALLOCATE PREPARE cap_duyet_nhom_stmt;
SET @sql_cap_duyet_nhom = NULL;

-- Chuyển cấp duyệt của đề xuất. Quyết định thực tế và người đã duyệt vẫn
-- được lưu theo từng lần trình trong yeu_cau_phe_duyet và lich_su_phe_duyet.
UPDATE nhom_lien_phong_de_xuat dx
JOIN nhom_lien_phong n ON n.nhom_id = dx.nhom_id
SET dx.cap_duyet = 'TRUONG_NHOM', dx.nguoi_duyet_id = n.truong_nhom_id
WHERE dx.cap_duyet = 'VAN_PHONG';

ALTER TABLE nhom_lien_phong_de_xuat
    DROP CHECK ck_nlp_dx_cap,
    ADD CONSTRAINT ck_nlp_dx_cap CHECK (cap_duyet IN ('TRUONG_NHOM', 'LANH_DAO'));
