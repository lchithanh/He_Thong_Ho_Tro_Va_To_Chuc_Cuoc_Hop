CREATE TABLE IF NOT EXISTS cuoc_hop_nhom_tham_du (
    cuoc_hop_id INT NOT NULL,
    nhom_id INT NOT NULL,
    thanh_vien_khi_chon JSON NOT NULL,
    PRIMARY KEY (cuoc_hop_id),
    KEY idx_chnt_nhom (nhom_id, cuoc_hop_id),
    CONSTRAINT fk_chnt_hop FOREIGN KEY (cuoc_hop_id) REFERENCES cuoc_hop (cuoc_hop_id) ON DELETE CASCADE,
    CONSTRAINT fk_chnt_nhom FOREIGN KEY (nhom_id) REFERENCES nhom_lien_phong (nhom_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
