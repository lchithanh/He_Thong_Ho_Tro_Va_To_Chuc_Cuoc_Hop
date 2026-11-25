CREATE TABLE IF NOT EXISTS ghi_chu_cuoc_hop (
    ghi_chu_id INT NOT NULL AUTO_INCREMENT,
    cuoc_hop_id INT NOT NULL,
    chu_de VARCHAR(255) NOT NULL,
    nguoi_phat_bieu VARCHAR(190),
    y_kien_chinh TEXT NOT NULL,
    noi_dung_chot TEXT NOT NULL,
    phien_ban INT NOT NULL DEFAULT 1,
    PRIMARY KEY (ghi_chu_id),
    KEY idx_ghi_chu_cuoc_hop (cuoc_hop_id, ghi_chu_id),
    CONSTRAINT fk_ghi_chu_cuoc_hop FOREIGN KEY (cuoc_hop_id)
        REFERENCES cuoc_hop (cuoc_hop_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
