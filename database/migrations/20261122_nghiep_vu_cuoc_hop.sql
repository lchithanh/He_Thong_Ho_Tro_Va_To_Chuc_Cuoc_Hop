CREATE TABLE cuoc_bau (
    cuoc_bau_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cuoc_hop_id INT NOT NULL,
    tieu_de VARCHAR(255) NOT NULL,
    nguoi_tao_id INT NOT NULL,
    FOREIGN KEY (cuoc_hop_id) REFERENCES cuoc_hop(cuoc_hop_id),
    FOREIGN KEY (nguoi_tao_id) REFERENCES nguoi_dung(nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE vong_bau (
    vong_bau_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cuoc_bau_id INT NOT NULL,
    vong_truoc_id INT,
    so_vong INT NOT NULL,
    so_lua_chon_moi_phieu INT NOT NULL,
    so_nguoi_duoc_chon INT NOT NULL,
    trang_thai VARCHAR(25) NOT NULL DEFAULT 'NHAP',
    UNIQUE (vong_truoc_id, so_vong),
    FOREIGN KEY (vong_truoc_id) REFERENCES vong_bau(vong_bau_id),
    FOREIGN KEY (cuoc_bau_id) REFERENCES cuoc_bau(cuoc_bau_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ung_vien_vong_bau (
    vong_bau_id INT NOT NULL,
    nguoi_dung_id INT NOT NULL,
    ho_ten VARCHAR(255) NOT NULL,
    so_luot_chon INT,
    ket_qua VARCHAR(20),
    PRIMARY KEY (vong_bau_id, nguoi_dung_id),
    FOREIGN KEY (vong_bau_id) REFERENCES vong_bau(vong_bau_id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cu_tri_vong_bau (
    vong_bau_id INT NOT NULL,
    nguoi_dung_id INT NOT NULL,
    PRIMARY KEY (vong_bau_id, nguoi_dung_id),
    FOREIGN KEY (vong_bau_id) REFERENCES vong_bau(vong_bau_id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE phieu_bau (
    vong_bau_id INT NOT NULL,
    nguoi_bo_phieu_id INT NOT NULL,
    thoi_gian_gui DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (vong_bau_id, nguoi_bo_phieu_id),
    FOREIGN KEY (vong_bau_id, nguoi_bo_phieu_id)
        REFERENCES cu_tri_vong_bau(vong_bau_id, nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE lua_chon_phieu_bau (
    vong_bau_id INT NOT NULL,
    nguoi_bo_phieu_id INT NOT NULL,
    ung_vien_id INT NOT NULL,
    PRIMARY KEY (vong_bau_id, nguoi_bo_phieu_id, ung_vien_id),
    FOREIGN KEY (vong_bau_id, nguoi_bo_phieu_id)
        REFERENCES phieu_bau(vong_bau_id, nguoi_bo_phieu_id),
    FOREIGN KEY (vong_bau_id, ung_vien_id)
        REFERENCES ung_vien_vong_bau(vong_bau_id, nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE nghi_quyet (
    nghi_quyet_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cuoc_hop_id INT NOT NULL,
    nghi_quyet_cha_id INT,
    cap INT NOT NULL,
    tieu_de VARCHAR(255) NOT NULL,
    noi_dung TEXT NOT NULL,
    trang_thai VARCHAR(25) NOT NULL DEFAULT 'NHAP',
    ly_do VARCHAR(500),
    UNIQUE (cuoc_hop_id, nghi_quyet_id),
    FOREIGN KEY (cuoc_hop_id) REFERENCES cuoc_hop(cuoc_hop_id),
    FOREIGN KEY (cuoc_hop_id, nghi_quyet_cha_id) REFERENCES nghi_quyet(cuoc_hop_id, nghi_quyet_id),
    CHECK (cap BETWEEN 1 AND 4)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE bieu_quyet
    ADD COLUMN nghi_quyet_id INT NULL,
    ADD CONSTRAINT fk_bieu_quyet_nghi_quyet FOREIGN KEY (cuoc_hop_id, nghi_quyet_id)
        REFERENCES nghi_quyet(cuoc_hop_id, nghi_quyet_id),
    ADD COLUMN ket_qua_nghi_quyet VARCHAR(25) NULL;

ALTER TABLE lich_hop ADD COLUMN la_lich_doi BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE khach_moi_cuoc_hop
    ADD COLUMN ma_truy_cap_sha256 CHAR(64),
    ADD COLUMN duong_dan_hop VARCHAR(2048),
    ADD COLUMN han_truy_cap DATETIME NULL,
    ADD CONSTRAINT uk_khach_ngoai_ma_truy_cap UNIQUE (ma_truy_cap_sha256);
