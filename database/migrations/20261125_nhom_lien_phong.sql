CREATE TABLE IF NOT EXISTS nhom_lien_phong (
    nhom_id INT NOT NULL AUTO_INCREMENT,
    ten_nhom VARCHAR(150) NOT NULL,
    muc_dich VARCHAR(1000) NOT NULL,
    phong_ban_chu_tri_id INT NOT NULL,
    nguoi_lap_id INT NOT NULL,
    cap_nhom VARCHAR(20) NOT NULL DEFAULT 'THUONG',
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'CHO_CU_NHAN_SU',
    truong_nhom_id INT,
    thu_ky_nhom_id INT,
    nguoi_kich_hoat_id INT,
    ngay_kich_hoat DATETIME,
    nguoi_dong_id INT,
    ngay_dong DATETIME,
    ly_do_dong VARCHAR(500),
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (nhom_id),
    KEY idx_nlp_trang_thai (trang_thai, nhom_id),
    CONSTRAINT fk_nlp_phong_chu_tri FOREIGN KEY (phong_ban_chu_tri_id) REFERENCES phong_ban (phong_ban_id),
    CONSTRAINT fk_nlp_nguoi_lap FOREIGN KEY (nguoi_lap_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_truong_nhom FOREIGN KEY (truong_nhom_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_thu_ky FOREIGN KEY (thu_ky_nhom_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_nguoi_kich_hoat FOREIGN KEY (nguoi_kich_hoat_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_nguoi_dong FOREIGN KEY (nguoi_dong_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT ck_nlp_cap CHECK (cap_nhom IN ('THUONG', 'CAP_CAO')),
    CONSTRAINT ck_nlp_trang_thai CHECK (trang_thai IN (
        'CHO_CU_NHAN_SU', 'CHO_KICH_HOAT', 'HOAT_DONG', 'DA_DONG'
    )),
    CONSTRAINT ck_nlp_vai_tro CHECK (truong_nhom_id IS NULL OR thu_ky_nhom_id IS NULL OR truong_nhom_id <> thu_ky_nhom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS nhom_lien_phong_phong_ban (
    nhom_id INT NOT NULL,
    phong_ban_id INT NOT NULL,
    nguoi_cu_id INT,
    ngay_cu DATETIME,
    PRIMARY KEY (nhom_id, phong_ban_id),
    KEY idx_nlp_pb_phong (phong_ban_id, nhom_id),
    CONSTRAINT fk_nlp_pb_nhom FOREIGN KEY (nhom_id) REFERENCES nhom_lien_phong (nhom_id),
    CONSTRAINT fk_nlp_pb_phong FOREIGN KEY (phong_ban_id) REFERENCES phong_ban (phong_ban_id),
    CONSTRAINT fk_nlp_pb_nguoi_cu FOREIGN KEY (nguoi_cu_id) REFERENCES nguoi_dung (nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS nhom_lien_phong_thanh_vien (
    nhom_id INT NOT NULL,
    nguoi_dung_id INT NOT NULL,
    phong_ban_id INT NOT NULL,
    ngay_tham_gia DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (nhom_id, nguoi_dung_id),
    KEY idx_nlp_tv_phong (nhom_id, phong_ban_id),
    KEY idx_nlp_tv_nguoi (nguoi_dung_id, nhom_id),
    CONSTRAINT fk_nlp_tv_phong FOREIGN KEY (nhom_id, phong_ban_id)
        REFERENCES nhom_lien_phong_phong_ban (nhom_id, phong_ban_id),
    CONSTRAINT fk_nlp_tv_nguoi FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS nhom_lien_phong_doi_nguoi (
    doi_nguoi_id INT NOT NULL AUTO_INCREMENT,
    nhom_id INT NOT NULL,
    phong_ban_id INT NOT NULL,
    nguoi_cu_id INT,
    nguoi_moi_id INT,
    nguoi_yeu_cau_id INT NOT NULL,
    ly_do VARCHAR(500) NOT NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'CHO_XU_LY',
    nguoi_xu_ly_id INT,
    y_kien_xu_ly VARCHAR(500),
    ngay_tao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_xu_ly DATETIME,
    PRIMARY KEY (doi_nguoi_id),
    KEY idx_nlp_dn_trang_thai (nhom_id, trang_thai, doi_nguoi_id),
    CONSTRAINT fk_nlp_dn_phong FOREIGN KEY (nhom_id, phong_ban_id)
        REFERENCES nhom_lien_phong_phong_ban (nhom_id, phong_ban_id),
    CONSTRAINT fk_nlp_dn_nguoi_cu FOREIGN KEY (nguoi_cu_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_dn_nguoi_moi FOREIGN KEY (nguoi_moi_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_dn_nguoi_yeu_cau FOREIGN KEY (nguoi_yeu_cau_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_dn_nguoi_xu_ly FOREIGN KEY (nguoi_xu_ly_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT ck_nlp_dn_trang_thai CHECK (trang_thai IN ('CHO_XU_LY', 'DA_CAP_NHAT', 'TU_CHOI', 'DA_HUY')),
    CONSTRAINT ck_nlp_dn_thanh_vien CHECK (
        (nguoi_cu_id IS NOT NULL OR nguoi_moi_id IS NOT NULL)
        AND (nguoi_cu_id IS NULL OR nguoi_moi_id IS NULL OR nguoi_cu_id <> nguoi_moi_id)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS nhom_lien_phong_de_xuat (
    cuoc_hop_id INT NOT NULL,
    nhom_id INT NOT NULL,
    nguoi_gui_id INT NOT NULL,
    thanh_vien_khi_gui JSON NOT NULL,
    nguoi_duyet_id INT NOT NULL,
    cap_duyet VARCHAR(20) NOT NULL,
    ngay_gui DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cuoc_hop_id),
    KEY idx_nlp_dx_nhom (nhom_id, cuoc_hop_id),
    CONSTRAINT fk_nlp_dx_cuoc_hop FOREIGN KEY (cuoc_hop_id)
        REFERENCES cuoc_hop (cuoc_hop_id) ON DELETE CASCADE,
    CONSTRAINT fk_nlp_dx_nhom FOREIGN KEY (nhom_id) REFERENCES nhom_lien_phong (nhom_id),
    CONSTRAINT fk_nlp_dx_nguoi_gui FOREIGN KEY (nguoi_gui_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_nlp_dx_nguoi_duyet FOREIGN KEY (nguoi_duyet_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT ck_nlp_dx_cap CHECK (cap_duyet IN ('VAN_PHONG', 'LANH_DAO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS thu_ky_van_phong (
    nguoi_dung_id INT NOT NULL,
    nguoi_cap_id INT NOT NULL,
    ngay_cap DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (nguoi_dung_id),
    CONSTRAINT fk_tkvp_nguoi FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (nguoi_dung_id),
    CONSTRAINT fk_tkvp_nguoi_cap FOREIGN KEY (nguoi_cap_id) REFERENCES nguoi_dung (nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
