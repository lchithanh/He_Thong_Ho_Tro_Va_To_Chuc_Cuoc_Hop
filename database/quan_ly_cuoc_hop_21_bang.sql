-- ============================================================
-- CSDL: QUAN_LY_CUOC_HOP
-- 21 bang - phuc vu he thong quan ly va ho tro to chuc cuoc hop
-- Tuong thich MySQL 8.x / phpMyAdmin 5.3.x
--
-- LUU Y:
-- 1. File nay tao CSDL moi va cac bang; khong them du lieu nguoi dung mau.
-- 2. Phan quyen chi tiet duoc xu ly trong Backend Spring Boot.
--    Bang vai_tro va nguoi_dung_vai_tro van duoc giu de gan vai tro.
-- 3. lich_hop luu ca phuong an lich va lich chinh thuc theo trang_thai.
-- 4. Cac FK duoc khai bao de phpMyAdmin Designer co the hien thi quan he.
-- ============================================================

CREATE DATABASE IF NOT EXISTS `quan_ly_cuoc_hop`
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quan_ly_cuoc_hop`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Xoa bang theo thu tu phu thuoc de co the import lai file.
DROP TABLE IF EXISTS `phieu_bieu_quyet`;
DROP TABLE IF EXISTS `lua_chon_bieu_quyet`;
DROP TABLE IF EXISTS `bieu_quyet`;
DROP TABLE IF EXISTS `tien_do_nhiem_vu`;
DROP TABLE IF EXISTS `nhiem_vu`;
DROP TABLE IF EXISTS `phien_ban_bien_ban`;
DROP TABLE IF EXISTS `ket_qua_xu_ly_ai`;
DROP TABLE IF EXISTS `bien_ban_cuoc_hop`;
DROP TABLE IF EXISTS `tai_lieu_cuoc_hop`;
DROP TABLE IF EXISTS `thong_bao`;
DROP TABLE IF EXISTS `lich_su_phe_duyet`;
DROP TABLE IF EXISTS `lich_hop`;
DROP TABLE IF EXISTS `thiet_bi`;
DROP TABLE IF EXISTS `thanh_vien_cuoc_hop`;
DROP TABLE IF EXISTS `nhat_ky_he_thong`;
DROP TABLE IF EXISTS `nguoi_dung_vai_tro`;
DROP TABLE IF EXISTS `cuoc_hop`;
DROP TABLE IF EXISTS `phong_hop`;
DROP TABLE IF EXISTS `vai_tro`;
DROP TABLE IF EXISTS `nguoi_dung`;
DROP TABLE IF EXISTS `phong_ban`;

-- 1. PHONG_BAN
CREATE TABLE `phong_ban` (
  `phong_ban_id` INT NOT NULL AUTO_INCREMENT,
  `ten_phong_ban` VARCHAR(150) NOT NULL,
  `mo_ta` TEXT NULL,
  `trang_thai` VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG',
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phong_ban_id`),
  UNIQUE KEY `uk_phong_ban_ten` (`ten_phong_ban`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. VAI_TRO
CREATE TABLE `vai_tro` (
  `vai_tro_id` INT NOT NULL AUTO_INCREMENT,
  `ma_vai_tro` VARCHAR(30) NOT NULL,
  `ten_vai_tro` VARCHAR(100) NOT NULL,
  `mo_ta` VARCHAR(500) NULL,
  `trang_thai` VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG',
  PRIMARY KEY (`vai_tro_id`),
  UNIQUE KEY `uk_vai_tro_ma` (`ma_vai_tro`),
  UNIQUE KEY `uk_vai_tro_ten` (`ten_vai_tro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. NGUOI_DUNG
CREATE TABLE `nguoi_dung` (
  `nguoi_dung_id` INT NOT NULL AUTO_INCREMENT,
  `phong_ban_id` INT NULL,
  `ma_nhan_vien` VARCHAR(30) NULL,
  `ho_ten` VARCHAR(150) NOT NULL,
  `email` VARCHAR(190) NOT NULL,
  `so_dien_thoai` VARCHAR(25) NULL,
  `ten_dang_nhap` VARCHAR(80) NOT NULL,
  `mat_khau_ma_hoa` VARCHAR(255) NOT NULL,
  `chuc_vu` VARCHAR(120) NULL,
  `trang_thai` VARCHAR(20) NOT NULL DEFAULT 'HOAT_DONG',
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`nguoi_dung_id`),
  UNIQUE KEY `uk_nguoi_dung_ma_nv` (`ma_nhan_vien`),
  UNIQUE KEY `uk_nguoi_dung_email` (`email`),
  UNIQUE KEY `uk_nguoi_dung_ten_dn` (`ten_dang_nhap`),
  KEY `idx_nguoi_dung_phong_ban` (`phong_ban_id`),
  CONSTRAINT `fk_nguoi_dung_phong_ban`
    FOREIGN KEY (`phong_ban_id`) REFERENCES `phong_ban` (`phong_ban_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. NGUOI_DUNG_VAI_TRO
CREATE TABLE `nguoi_dung_vai_tro` (
  `nguoi_dung_id` INT NOT NULL,
  `vai_tro_id` INT NOT NULL,
  `nguoi_gan_id` INT NULL,
  `ngay_gan` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`nguoi_dung_id`, `vai_tro_id`),
  KEY `idx_ndvt_vai_tro` (`vai_tro_id`),
  KEY `idx_ndvt_nguoi_gan` (`nguoi_gan_id`),
  CONSTRAINT `fk_ndvt_nguoi_dung`
    FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_ndvt_vai_tro`
    FOREIGN KEY (`vai_tro_id`) REFERENCES `vai_tro` (`vai_tro_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_ndvt_nguoi_gan`
    FOREIGN KEY (`nguoi_gan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. PHONG_HOP
CREATE TABLE `phong_hop` (
  `phong_hop_id` INT NOT NULL AUTO_INCREMENT,
  `ten_phong_hop` VARCHAR(120) NOT NULL,
  `vi_tri` VARCHAR(255) NULL,
  `suc_chua` INT NOT NULL DEFAULT 1,
  `mo_ta` TEXT NULL,
  `trang_thai` VARCHAR(25) NOT NULL DEFAULT 'SAN_SANG',
  PRIMARY KEY (`phong_hop_id`),
  UNIQUE KEY `uk_phong_hop_ten` (`ten_phong_hop`),
  CONSTRAINT `chk_phong_hop_suc_chua` CHECK (`suc_chua` >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. THIET_BI
CREATE TABLE `thiet_bi` (
  `thiet_bi_id` INT NOT NULL AUTO_INCREMENT,
  `phong_hop_id` INT NULL,
  `ten_thiet_bi` VARCHAR(150) NOT NULL,
  `ma_thiet_bi` VARCHAR(50) NULL,
  `so_luong` INT NOT NULL DEFAULT 1,
  `tinh_trang` VARCHAR(30) NOT NULL DEFAULT 'TOT',
  `ghi_chu` TEXT NULL,
  PRIMARY KEY (`thiet_bi_id`),
  UNIQUE KEY `uk_thiet_bi_ma` (`ma_thiet_bi`),
  KEY `idx_thiet_bi_phong` (`phong_hop_id`),
  CONSTRAINT `fk_thiet_bi_phong_hop`
    FOREIGN KEY (`phong_hop_id`) REFERENCES `phong_hop` (`phong_hop_id`)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `chk_thiet_bi_so_luong` CHECK (`so_luong` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. CUOC_HOP
CREATE TABLE `cuoc_hop` (
  `cuoc_hop_id` INT NOT NULL AUTO_INCREMENT,
  `ma_cuoc_hop` VARCHAR(40) NULL,
  `tieu_de` VARCHAR(255) NOT NULL,
  `muc_tieu` TEXT NULL,
  `noi_dung_du_kien` LONGTEXT NULL,
  `loai_hinh` VARCHAR(20) NOT NULL DEFAULT 'TRUC_TIEP',
  `nguoi_tao_id` INT NOT NULL,
  `nguoi_chu_tri_id` INT NULL,
  `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'NHAP',
  `thoi_gian_bat_dau_thuc_te` DATETIME NULL,
  `thoi_gian_ket_thuc_thuc_te` DATETIME NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cuoc_hop_id`),
  UNIQUE KEY `uk_cuoc_hop_ma` (`ma_cuoc_hop`),
  KEY `idx_cuoc_hop_trang_thai` (`trang_thai`),
  KEY `idx_cuoc_hop_nguoi_tao` (`nguoi_tao_id`),
  KEY `idx_cuoc_hop_chu_tri` (`nguoi_chu_tri_id`),
  CONSTRAINT `fk_cuoc_hop_nguoi_tao`
    FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_cuoc_hop_chu_tri`
    FOREIGN KEY (`nguoi_chu_tri_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. THANH_VIEN_CUOC_HOP
CREATE TABLE `thanh_vien_cuoc_hop` (
  `cuoc_hop_id` INT NOT NULL,
  `nguoi_dung_id` INT NOT NULL,
  `vai_tro_tai_cuoc_hop` VARCHAR(30) NOT NULL DEFAULT 'THAM_DU',
  `trang_thai_moi` VARCHAR(25) NOT NULL DEFAULT 'CHO_PHAN_HOI',
  `trang_thai_tham_du` VARCHAR(25) NOT NULL DEFAULT 'CHUA_DIEM_DANH',
  `thoi_gian_diem_danh` DATETIME NULL,
  `ghi_chu` VARCHAR(500) NULL,
  PRIMARY KEY (`cuoc_hop_id`, `nguoi_dung_id`),
  KEY `idx_tvc_hop_nguoi_dung` (`nguoi_dung_id`),
  CONSTRAINT `fk_tvc_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_tvc_nguoi_dung`
    FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. LICH_HOP
-- Luu tung phuong an lich va lich da chot. Lich chinh thuc co trang_thai='DA_XAC_NHAN'.
-- Khi doi lich, co the danh dau lich cu='DA_THAY_THE' va tao ban ghi moi.
CREATE TABLE `lich_hop` (
  `lich_hop_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `phong_hop_id` INT NULL,
  `nguoi_de_xuat_id` INT NOT NULL,
  `nguoi_xac_nhan_id` INT NULL,
  `thoi_gian_bat_dau` DATETIME NOT NULL,
  `thoi_gian_ket_thuc` DATETIME NOT NULL,
  `diem_phu_hop` DECIMAL(5,2) NULL,
  `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'DE_XUAT',
  `ly_do_thay_doi` TEXT NULL,
  `thoi_gian_xac_nhan` DATETIME NULL,
  `lich_thay_the_id` INT NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lich_hop_id`),
  KEY `idx_lich_hop_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_lich_hop_phong_thoi_gian` (`phong_hop_id`, `thoi_gian_bat_dau`, `thoi_gian_ket_thuc`),
  KEY `idx_lich_hop_trang_thai` (`trang_thai`),
  KEY `idx_lich_hop_nguoi_de_xuat` (`nguoi_de_xuat_id`),
  CONSTRAINT `fk_lich_hop_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_lich_hop_phong_hop`
    FOREIGN KEY (`phong_hop_id`) REFERENCES `phong_hop` (`phong_hop_id`)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `fk_lich_hop_nguoi_de_xuat`
    FOREIGN KEY (`nguoi_de_xuat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_lich_hop_nguoi_xac_nhan`
    FOREIGN KEY (`nguoi_xac_nhan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `fk_lich_hop_thay_the`
    FOREIGN KEY (`lich_thay_the_id`) REFERENCES `lich_hop` (`lich_hop_id`)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `chk_lich_hop_thoi_gian` CHECK (`thoi_gian_ket_thuc` > `thoi_gian_bat_dau`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. LICH_SU_PHE_DUYET
CREATE TABLE `lich_su_phe_duyet` (
  `lich_su_phe_duyet_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `lich_hop_id` INT NULL,
  `bien_ban_id` INT NULL,
  `nguoi_phe_duyet_id` INT NOT NULL,
  `loai_phe_duyet` VARCHAR(30) NOT NULL,
  `hanh_dong` VARCHAR(30) NOT NULL,
  `y_kien` TEXT NULL,
  `thoi_gian_phe_duyet` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lich_su_phe_duyet_id`),
  KEY `idx_lspd_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_lspd_lich_hop` (`lich_hop_id`),
  KEY `idx_lspd_bien_ban` (`bien_ban_id`),
  KEY `idx_lspd_nguoi_phe_duyet` (`nguoi_phe_duyet_id`),
  CONSTRAINT `fk_lspd_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_lspd_lich_hop`
    FOREIGN KEY (`lich_hop_id`) REFERENCES `lich_hop` (`lich_hop_id`)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `fk_lspd_nguoi_phe_duyet`
    FOREIGN KEY (`nguoi_phe_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. THONG_BAO
CREATE TABLE `thong_bao` (
  `thong_bao_id` INT NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` INT NOT NULL,
  `cuoc_hop_id` INT NULL,
  `tieu_de` VARCHAR(255) NOT NULL,
  `noi_dung` TEXT NOT NULL,
  `loai_thong_bao` VARCHAR(40) NOT NULL DEFAULT 'HE_THONG',
  `da_doc` TINYINT(1) NOT NULL DEFAULT 0,
  `thoi_gian_doc` DATETIME NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`thong_bao_id`),
  KEY `idx_thong_bao_nguoi_doc` (`nguoi_dung_id`, `da_doc`),
  KEY `idx_thong_bao_cuoc_hop` (`cuoc_hop_id`),
  CONSTRAINT `fk_thong_bao_nguoi_dung`
    FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_thong_bao_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. TAI_LIEU_CUOC_HOP
-- Ho tro tai lieu dau vao, audio, transcript speech-to-text va anh ghi chep tay.
CREATE TABLE `tai_lieu_cuoc_hop` (
  `tai_lieu_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `nguoi_tai_len_id` INT NOT NULL,
  `ten_tai_lieu` VARCHAR(255) NOT NULL,
  `loai_tai_lieu` VARCHAR(40) NOT NULL DEFAULT 'TAI_LIEU_DAU_VAO',
  `ten_file_goc` VARCHAR(255) NULL,
  `duong_dan_file` VARCHAR(700) NULL,
  `dinh_dang_file` VARCHAR(20) NULL,
  `dung_luong_byte` BIGINT NULL,
  `noi_dung_van_ban` LONGTEXT NULL,
  `trang_thai_xu_ly` VARCHAR(30) NOT NULL DEFAULT 'CHUA_XU_LY',
  `mo_ta` TEXT NULL,
  `ngay_tai_len` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tai_lieu_id`),
  KEY `idx_tai_lieu_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_tai_lieu_loai` (`loai_tai_lieu`),
  KEY `idx_tai_lieu_nguoi_tai` (`nguoi_tai_len_id`),
  CONSTRAINT `fk_tai_lieu_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_tai_lieu_nguoi_tai`
    FOREIGN KEY (`nguoi_tai_len_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. BIEN_BAN_CUOC_HOP
CREATE TABLE `bien_ban_cuoc_hop` (
  `bien_ban_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `nguoi_soan_id` INT NOT NULL,
  `nguoi_duyet_id` INT NULL,
  `tieu_de` VARCHAR(255) NOT NULL,
  `noi_dung_goc` LONGTEXT NULL,
  `noi_dung_chinh_thuc` LONGTEXT NULL,
  `duong_dan_file_word` VARCHAR(700) NULL,
  `duong_dan_file_pdf` VARCHAR(700) NULL,
  `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'NHAP',
  `y_kien_phan_hoi` TEXT NULL,
  `ngay_soan` DATETIME NULL,
  `ngay_trinh_duyet` DATETIME NULL,
  `ngay_duyet` DATETIME NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`bien_ban_id`),
  UNIQUE KEY `uk_bien_ban_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bien_ban_nguoi_soan` (`nguoi_soan_id`),
  KEY `idx_bien_ban_nguoi_duyet` (`nguoi_duyet_id`),
  KEY `idx_bien_ban_trang_thai` (`trang_thai`),
  CONSTRAINT `fk_bien_ban_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_bien_ban_nguoi_soan`
    FOREIGN KEY (`nguoi_soan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_bien_ban_nguoi_duyet`
    FOREIGN KEY (`nguoi_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. PHIEN_BAN_BIEN_BAN
CREATE TABLE `phien_ban_bien_ban` (
  `phien_ban_id` INT NOT NULL AUTO_INCREMENT,
  `bien_ban_id` INT NOT NULL,
  `nguoi_cap_nhat_id` INT NOT NULL,
  `so_phien_ban` INT NOT NULL,
  `noi_dung` LONGTEXT NOT NULL,
  `ghi_chu_thay_doi` TEXT NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phien_ban_id`),
  UNIQUE KEY `uk_phien_ban_bien_ban` (`bien_ban_id`, `so_phien_ban`),
  KEY `idx_phien_ban_nguoi_cap_nhat` (`nguoi_cap_nhat_id`),
  CONSTRAINT `fk_pbbb_bien_ban`
    FOREIGN KEY (`bien_ban_id`) REFERENCES `bien_ban_cuoc_hop` (`bien_ban_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_pbbb_nguoi_cap_nhat`
    FOREIGN KEY (`nguoi_cap_nhat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `chk_phien_ban_so` CHECK (`so_phien_ban` >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 15. KET_QUA_XU_LY_AI
CREATE TABLE `ket_qua_xu_ly_ai` (
  `ket_qua_ai_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `tai_lieu_id` INT NULL,
  `nguoi_yeu_cau_id` INT NOT NULL,
  `loai_xu_ly` VARCHAR(40) NOT NULL,
  `noi_dung_dau_vao` LONGTEXT NULL,
  `ket_qua_dau_ra` LONGTEXT NULL,
  `trang_thai` VARCHAR(30) NOT NULL DEFAULT 'CHO_XU_LY',
  `thong_bao_loi` TEXT NULL,
  `thoi_gian_bat_dau` DATETIME NULL,
  `thoi_gian_ket_thuc` DATETIME NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ket_qua_ai_id`),
  KEY `idx_ai_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_ai_tai_lieu` (`tai_lieu_id`),
  KEY `idx_ai_nguoi_yeu_cau` (`nguoi_yeu_cau_id`),
  CONSTRAINT `fk_ai_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_ai_tai_lieu`
    FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`)
    ON UPDATE CASCADE ON DELETE SET NULL,
  CONSTRAINT `fk_ai_nguoi_yeu_cau`
    FOREIGN KEY (`nguoi_yeu_cau_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 16. NHIEM_VU
CREATE TABLE `nhiem_vu` (
  `nhiem_vu_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `tieu_de` VARCHAR(255) NOT NULL,
  `mo_ta` TEXT NULL,
  `nguoi_giao_id` INT NOT NULL,
  `nguoi_thuc_hien_id` INT NOT NULL,
  `han_hoan_thanh` DATETIME NULL,
  `muc_do_uu_tien` VARCHAR(20) NOT NULL DEFAULT 'TRUNG_BINH',
  `trang_thai` VARCHAR(25) NOT NULL DEFAULT 'CHUA_BAT_DAU',
  `phan_tram_hoan_thanh` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_hoan_thanh` DATETIME NULL,
  PRIMARY KEY (`nhiem_vu_id`),
  KEY `idx_nhiem_vu_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_nhiem_vu_nguoi_thuc_hien` (`nguoi_thuc_hien_id`),
  KEY `idx_nhiem_vu_nguoi_giao` (`nguoi_giao_id`),
  KEY `idx_nhiem_vu_trang_thai_han` (`trang_thai`, `han_hoan_thanh`),
  CONSTRAINT `fk_nhiem_vu_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_nhiem_vu_nguoi_giao`
    FOREIGN KEY (`nguoi_giao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_nhiem_vu_nguoi_thuc_hien`
    FOREIGN KEY (`nguoi_thuc_hien_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `chk_nhiem_vu_phan_tram` CHECK (`phan_tram_hoan_thanh` <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 17. TIEN_DO_NHIEM_VU
CREATE TABLE `tien_do_nhiem_vu` (
  `tien_do_id` INT NOT NULL AUTO_INCREMENT,
  `nhiem_vu_id` INT NOT NULL,
  `nguoi_cap_nhat_id` INT NOT NULL,
  `phan_tram_hoan_thanh` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `noi_dung_cap_nhat` TEXT NOT NULL,
  `ngay_cap_nhat` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tien_do_id`),
  KEY `idx_tien_do_nhiem_vu` (`nhiem_vu_id`),
  KEY `idx_tien_do_nguoi_cap_nhat` (`nguoi_cap_nhat_id`),
  CONSTRAINT `fk_tdnv_nhiem_vu`
    FOREIGN KEY (`nhiem_vu_id`) REFERENCES `nhiem_vu` (`nhiem_vu_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_tdnv_nguoi_cap_nhat`
    FOREIGN KEY (`nguoi_cap_nhat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `chk_tien_do_phan_tram` CHECK (`phan_tram_hoan_thanh` <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 18. NHAT_KY_HE_THONG
CREATE TABLE `nhat_ky_he_thong` (
  `nhat_ky_id` INT NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` INT NULL,
  `hanh_dong` VARCHAR(100) NOT NULL,
  `doi_tuong` VARCHAR(60) NOT NULL,
  `doi_tuong_id` INT NULL,
  `dia_chi_ip` VARCHAR(45) NULL,
  `chi_tiet` JSON NULL,
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`nhat_ky_id`),
  KEY `idx_nhat_ky_nguoi_dung` (`nguoi_dung_id`),
  KEY `idx_nhat_ky_doi_tuong` (`doi_tuong`, `doi_tuong_id`),
  KEY `idx_nhat_ky_ngay_tao` (`ngay_tao`),
  CONSTRAINT `fk_nhat_ky_nguoi_dung`
    FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 19. BIEU_QUYET
CREATE TABLE `bieu_quyet` (
  `bieu_quyet_id` INT NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` INT NOT NULL,
  `nguoi_tao_id` INT NOT NULL,
  `tieu_de` VARCHAR(255) NOT NULL,
  `noi_dung` TEXT NULL,
  `che_do_bieu_quyet` VARCHAR(20) NOT NULL DEFAULT 'CONG_KHAI',
  `cho_phep_trang_thai` TINYINT(1) NOT NULL DEFAULT 1,
  `ty_le_tham_gia_toi_thieu` DECIMAL(5,2) NULL,
  `thoi_gian_bat_dau` DATETIME NOT NULL,
  `thoi_gian_ket_thuc` DATETIME NOT NULL,
  `trang_thai` VARCHAR(25) NOT NULL DEFAULT 'NHAP',
  `ngay_tao` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`bieu_quyet_id`),
  KEY `idx_bieu_quyet_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bieu_quyet_trang_thai` (`trang_thai`),
  CONSTRAINT `fk_bieu_quyet_cuoc_hop`
    FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_bieu_quyet_nguoi_tao`
    FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `chk_bieu_quyet_thoi_gian` CHECK (`thoi_gian_ket_thuc` > `thoi_gian_bat_dau`),
  CONSTRAINT `chk_bieu_quyet_ty_le` CHECK (`ty_le_tham_gia_toi_thieu` IS NULL OR (`ty_le_tham_gia_toi_thieu` >= 0 AND `ty_le_tham_gia_toi_thieu` <= 100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 20. LUA_CHON_BIEU_QUYET
CREATE TABLE `lua_chon_bieu_quyet` (
  `lua_chon_id` INT NOT NULL AUTO_INCREMENT,
  `bieu_quyet_id` INT NOT NULL,
  `noi_dung_lua_chon` VARCHAR(255) NOT NULL,
  `la_lua_chon_trang_thai` TINYINT(1) NOT NULL DEFAULT 0,
  `thu_tu_hien_thi` INT NOT NULL DEFAULT 1,
  PRIMARY KEY (`lua_chon_id`),
  UNIQUE KEY `uk_lua_chon_bieu_quyet_pair` (`lua_chon_id`, `bieu_quyet_id`),
  KEY `idx_lua_chon_bieu_quyet` (`bieu_quyet_id`),
  CONSTRAINT `fk_lua_chon_bieu_quyet`
    FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`)
    ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 21. PHIEU_BIEU_QUYET
CREATE TABLE `phieu_bieu_quyet` (
  `phieu_bieu_quyet_id` INT NOT NULL AUTO_INCREMENT,
  `bieu_quyet_id` INT NOT NULL,
  `lua_chon_id` INT NOT NULL,
  `nguoi_dung_id` INT NULL,
  `ma_phieu` CHAR(64) NULL,
  `thoi_gian_bieu_quyet` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phieu_bieu_quyet_id`),
  UNIQUE KEY `uk_phieu_bieu_quyet_ma_phieu` (`bieu_quyet_id`, `ma_phieu`),
  KEY `idx_phieu_bieu_quyet_lua_chon` (`lua_chon_id`),
  KEY `idx_phieu_bieu_quyet_nguoi_dung` (`bieu_quyet_id`, `nguoi_dung_id`),
  CONSTRAINT `fk_phieu_bieu_quyet_bieu_quyet`
    FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT `fk_phieu_bieu_quyet_lua_chon`
    FOREIGN KEY (`lua_chon_id`, `bieu_quyet_id`)
    REFERENCES `lua_chon_bieu_quyet` (`lua_chon_id`, `bieu_quyet_id`)
    ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_phieu_bieu_quyet_nguoi_dung`
    FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bo sung FK bien ban cho lich su phe duyet (bang bien ban duoc tao sau bang lich su).
ALTER TABLE `lich_su_phe_duyet`
  ADD CONSTRAINT `fk_lspd_bien_ban`
  FOREIGN KEY (`bien_ban_id`) REFERENCES `bien_ban_cuoc_hop` (`bien_ban_id`)
  ON UPDATE CASCADE ON DELETE SET NULL;

-- Du lieu vai tro co ban; phan quyen chi tiet se duoc kiem tra trong Backend.
INSERT INTO `vai_tro` (`ma_vai_tro`, `ten_vai_tro`, `mo_ta`) VALUES
('ADMIN', 'Quản trị hệ thống', 'Quản lý tài khoản và cấu hình hệ thống'),
('THU_KY', 'Thư ký', 'Tổ chức cuộc họp, quản lý tài liệu và biên bản'),
('LANH_DAO', 'Lãnh đạo', 'Phê duyệt cuộc họp, lịch họp và biên bản'),
('CAN_BO', 'Cán bộ', 'Tham dự cuộc họp và cập nhật nhiệm vụ');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- KET THUC: 21 BANG
-- Mo phpMyAdmin > chon database quan_ly_cuoc_hop > Designer de xem quan he.
-- ============================================================
