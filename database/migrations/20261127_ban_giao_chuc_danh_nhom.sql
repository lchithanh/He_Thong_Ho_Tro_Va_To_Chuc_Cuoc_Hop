-- Áp dụng trên bản SQL (22) đã có hồ sơ bầu lại.
CREATE TABLE IF NOT EXISTS nhom_ban_giao_chuc_danh (
    vong_bau_id INT NOT NULL PRIMARY KEY,
    yeu_cau_id INT NOT NULL,
    nguoi_ban_giao_id INT NOT NULL,
    nguoi_nhan_id INT NOT NULL,
    noi_dung VARCHAR(2000) NULL,
    ngay_gui DATETIME NULL,
    ngay_nhan DATETIME NULL,
    FOREIGN KEY (vong_bau_id) REFERENCES vong_bau(vong_bau_id),
    FOREIGN KEY (yeu_cau_id) REFERENCES nhom_bau_lai(yeu_cau_id),
    FOREIGN KEY (nguoi_ban_giao_id) REFERENCES nguoi_dung(nguoi_dung_id),
    FOREIGN KEY (nguoi_nhan_id) REFERENCES nguoi_dung(nguoi_dung_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
