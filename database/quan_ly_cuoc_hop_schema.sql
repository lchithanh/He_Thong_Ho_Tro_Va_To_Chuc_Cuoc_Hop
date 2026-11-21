-- MySQL 8.4. Khởi tạo trên cơ sở dữ liệu trống.
-- Cấu trúc theo bản xuất ngày 20/11/2026 và hai bảng nhiệm vụ của backend.
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `quan_ly_cuoc_hop`
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `quan_ly_cuoc_hop`;

CREATE TABLE `bien_ban_cuoc_hop` (
  `bien_ban_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `nguoi_soan_id` int NOT NULL,
  `nguoi_duyet_id` int DEFAULT NULL,
  `tieu_de` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `noi_dung_goc` longtext COLLATE utf8mb4_unicode_ci,
  `noi_dung_chinh_thuc` longtext COLLATE utf8mb4_unicode_ci,
  `duong_dan_file_word` varchar(700) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duong_dan_file_pdf` varchar(700) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NHAP',
  `y_kien_phan_hoi` text COLLATE utf8mb4_unicode_ci,
  `ngay_soan` datetime DEFAULT NULL,
  `ngay_trinh_duyet` datetime DEFAULT NULL,
  `ngay_duyet` datetime DEFAULT NULL,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `ngay_phat_hanh` datetime DEFAULT NULL,
  PRIMARY KEY (`bien_ban_id`),
  UNIQUE KEY `uk_bien_ban_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bien_ban_nguoi_soan` (`nguoi_soan_id`),
  KEY `idx_bien_ban_nguoi_duyet` (`nguoi_duyet_id`),
  KEY `idx_bien_ban_trang_thai` (`trang_thai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `bieu_quyet` (
  `bieu_quyet_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `nguoi_tao_id` int NOT NULL,
  `tieu_de` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `noi_dung` text COLLATE utf8mb4_unicode_ci,
  `che_do_bieu_quyet` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CONG_KHAI',
  `cho_phep_trang_thai` tinyint(1) NOT NULL DEFAULT '1',
  `ty_le_tham_gia_toi_thieu` decimal(5,2) DEFAULT NULL,
  `thoi_gian_bat_dau` datetime NOT NULL,
  `thoi_gian_ket_thuc` datetime NOT NULL,
  `trang_thai` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NHAP',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `so_thanh_vien_co_mat_khi_dong` bigint DEFAULT NULL,
  PRIMARY KEY (`bieu_quyet_id`),
  KEY `idx_bieu_quyet_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bieu_quyet_trang_thai` (`trang_thai`),
  KEY `fk_bieu_quyet_nguoi_tao` (`nguoi_tao_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `cuoc_hop` (
  `cuoc_hop_id` int NOT NULL AUTO_INCREMENT,
  `ma_cuoc_hop` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tieu_de` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `muc_tieu` text COLLATE utf8mb4_unicode_ci,
  `noi_dung_du_kien` longtext COLLATE utf8mb4_unicode_ci,
  `loai_hinh` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TRUC_TIEP',
  `pham_vi_cuoc_hop` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NOI_BO' COMMENT 'NOI_BO, LIEN_PHONG, BEN_NGOAI',
  `phong_ban_chu_tri_id` int DEFAULT NULL COMMENT 'Phong ban chiu trach nhiem chinh cua cuoc hop',
  `nguoi_tao_id` int NOT NULL,
  `nguoi_de_xuat_id` int NOT NULL COMMENT 'Nguoi chu dong de xuat/mo cuoc hop',
  `nguoi_chu_tri_id` int DEFAULT NULL,
  `buoc_xu_ly_hien_tai` varchar(35) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NHAP' COMMENT 'NHAP, CHO_TRUONG_PHONG, CHO_THU_KY_TIEP_NHAN, CHO_LANH_DAO, THU_KY_TO_CHUC, DA_HOAN_TAT, TU_CHOI',
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NHAP',
  `thoi_gian_bat_dau_thuc_te` datetime DEFAULT NULL,
  `thoi_gian_ket_thuc_thuc_te` datetime DEFAULT NULL,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `co_khach_moi_ben_ngoai` tinyint(1) NOT NULL DEFAULT '0',
  `thu_ky_cuoc_hop_id` int DEFAULT NULL,
  PRIMARY KEY (`cuoc_hop_id`),
  UNIQUE KEY `uk_cuoc_hop_ma` (`ma_cuoc_hop`),
  KEY `idx_cuoc_hop_trang_thai` (`trang_thai`),
  KEY `idx_cuoc_hop_nguoi_tao` (`nguoi_tao_id`),
  KEY `idx_cuoc_hop_chu_tri` (`nguoi_chu_tri_id`),
  KEY `idx_cuoc_hop_nguoi_de_xuat` (`nguoi_de_xuat_id`),
  KEY `idx_cuoc_hop_phong_ban_chu_tri` (`phong_ban_chu_tri_id`),
  KEY `idx_cuoc_hop_pham_vi_buoc_xu_ly` (`pham_vi_cuoc_hop`,`buoc_xu_ly_hien_tai`),
  KEY `fk_cuoc_hop_thu_ky` (`thu_ky_cuoc_hop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ket_luan_cuoc_hop` (
  `ket_luan_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `nguoi_soan_id` int NOT NULL,
  `nguoi_phat_hanh_id` int DEFAULT NULL,
  `tieu_de` varchar(255) NOT NULL,
  `noi_dung` longtext,
  `trang_thai` varchar(25) NOT NULL DEFAULT 'NHAP',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ngay_phat_hanh` datetime DEFAULT NULL,
  PRIMARY KEY (`ket_luan_id`),
  UNIQUE KEY `uk_ket_luan_cuoc_hop` (`cuoc_hop_id`),
  KEY `fk_ket_luan_nguoi_soan` (`nguoi_soan_id`),
  KEY `fk_ket_luan_nguoi_phat_hanh` (`nguoi_phat_hanh_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ket_qua_xu_ly_ai` (
  `ket_qua_ai_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `tai_lieu_id` int DEFAULT NULL,
  `nguoi_yeu_cau_id` int NOT NULL,
  `loai_xu_ly` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `noi_dung_dau_vao` longtext COLLATE utf8mb4_unicode_ci,
  `ket_qua_dau_ra` longtext COLLATE utf8mb4_unicode_ci,
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHO_XU_LY',
  `thong_bao_loi` text COLLATE utf8mb4_unicode_ci,
  `thoi_gian_bat_dau` datetime DEFAULT NULL,
  `thoi_gian_ket_thuc` datetime DEFAULT NULL,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ket_qua_ai_id`),
  KEY `idx_ai_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_ai_tai_lieu` (`tai_lieu_id`),
  KEY `idx_ai_nguoi_yeu_cau` (`nguoi_yeu_cau_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `khach_moi` (
  `khach_moi_id` int NOT NULL AUTO_INCREMENT,
  `ho_ten` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `don_vi_cong_tac` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chuc_vu` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(190) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `so_dien_thoai` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HOAT_DONG' COMMENT 'HOAT_DONG, NGUNG_HOAT_DONG',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`khach_moi_id`),
  KEY `idx_khach_moi_ho_ten` (`ho_ten`),
  KEY `idx_khach_moi_email` (`email`),
  KEY `idx_khach_moi_don_vi` (`don_vi_cong_tac`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `khach_moi_cuoc_hop` (
  `khach_moi_cuoc_hop_id` int NOT NULL AUTO_INCREMENT,
  `khach_moi_id` int NOT NULL,
  `cuoc_hop_id` int NOT NULL,
  `trang_thai_moi` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHO_PHAN_HOI' COMMENT 'CHO_PHAN_HOI, DONG_Y, TU_CHOI, DA_HUY',
  `trang_thai_tham_du` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHUA_DIEM_DANH' COMMENT 'CHUA_DIEM_DANH, CO_MAT, VANG_MAT',
  `thoi_gian_diem_danh` datetime DEFAULT NULL,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ngay_moi` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`khach_moi_cuoc_hop_id`),
  UNIQUE KEY `uk_khach_moi_cuoc_hop` (`khach_moi_id`,`cuoc_hop_id`),
  KEY `idx_kmch_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_kmch_trang_thai_moi` (`trang_thai_moi`),
  KEY `idx_kmch_trang_thai_tham_du` (`trang_thai_tham_du`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `lich_hop` (
  `lich_hop_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `phong_hop_id` int DEFAULT NULL,
  `nguoi_de_xuat_id` int NOT NULL,
  `nguoi_xac_nhan_id` int DEFAULT NULL,
  `thoi_gian_bat_dau` datetime NOT NULL,
  `thoi_gian_ket_thuc` datetime NOT NULL,
  `diem_phu_hop` decimal(5,2) DEFAULT NULL,
  `trang_thai` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DE_XUAT',
  `ly_do_thay_doi` text COLLATE utf8mb4_unicode_ci,
  `thoi_gian_xac_nhan` datetime DEFAULT NULL,
  `lich_thay_the_id` int DEFAULT NULL,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lich_hop_id`),
  KEY `idx_lich_hop_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_lich_hop_phong_thoi_gian` (`phong_hop_id`,`thoi_gian_bat_dau`,`thoi_gian_ket_thuc`),
  KEY `idx_lich_hop_trang_thai` (`trang_thai`),
  KEY `idx_lich_hop_nguoi_de_xuat` (`nguoi_de_xuat_id`),
  KEY `fk_lich_hop_nguoi_xac_nhan` (`nguoi_xac_nhan_id`),
  KEY `fk_lich_hop_thay_the` (`lich_thay_the_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `lich_su_phe_duyet` (
  `lich_su_phe_duyet_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `lich_hop_id` int DEFAULT NULL,
  `bien_ban_id` int DEFAULT NULL,
  `nguoi_phe_duyet_id` int NOT NULL,
  `loai_phe_duyet` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hanh_dong` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `y_kien` text COLLATE utf8mb4_unicode_ci,
  `thoi_gian_phe_duyet` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `yeu_cau_phe_duyet_id` int DEFAULT NULL,
  PRIMARY KEY (`lich_su_phe_duyet_id`),
  KEY `idx_lspd_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_lspd_lich_hop` (`lich_hop_id`),
  KEY `idx_lspd_bien_ban` (`bien_ban_id`),
  KEY `idx_lspd_nguoi_phe_duyet` (`nguoi_phe_duyet_id`),
  KEY `fk_lspd_yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `lua_chon_bieu_quyet` (
  `lua_chon_id` int NOT NULL AUTO_INCREMENT,
  `bieu_quyet_id` int NOT NULL,
  `noi_dung_lua_chon` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `la_lua_chon_trang_thai` tinyint(1) NOT NULL DEFAULT '0',
  `thu_tu_hien_thi` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`lua_chon_id`),
  UNIQUE KEY `uk_lua_chon_bieu_quyet_pair` (`lua_chon_id`,`bieu_quyet_id`),
  KEY `idx_lua_chon_bieu_quyet` (`bieu_quyet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `nguoi_dung` (
  `nguoi_dung_id` int NOT NULL AUTO_INCREMENT,
  `phong_ban_id` int DEFAULT NULL,
  `ma_nhan_vien` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ho_ten` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(190) COLLATE utf8mb4_unicode_ci NOT NULL,
  `so_dien_thoai` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ten_dang_nhap` varchar(80) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mat_khau_ma_hoa` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `chuc_vu` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HOAT_DONG',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`nguoi_dung_id`),
  UNIQUE KEY `uk_nguoi_dung_email` (`email`),
  UNIQUE KEY `uk_nguoi_dung_ten_dn` (`ten_dang_nhap`),
  UNIQUE KEY `uk_nguoi_dung_ma_nv` (`ma_nhan_vien`),
  KEY `idx_nguoi_dung_phong_ban` (`phong_ban_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `nguoi_dung_vai_tro` (
  `nguoi_dung_id` int NOT NULL,
  `vai_tro_id` int NOT NULL,
  PRIMARY KEY (`nguoi_dung_id`),
  KEY `idx_ndvt_vai_tro` (`vai_tro_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `nhat_ky_he_thong` (
  `nhat_ky_id` int NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` int DEFAULT NULL,
  `hanh_dong` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `doi_tuong` varchar(60) COLLATE utf8mb4_unicode_ci NOT NULL,
  `doi_tuong_id` int DEFAULT NULL,
  `dia_chi_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `chi_tiet` json DEFAULT NULL,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`nhat_ky_id`),
  KEY `idx_nhat_ky_nguoi_dung` (`nguoi_dung_id`),
  KEY `idx_nhat_ky_doi_tuong` (`doi_tuong`,`doi_tuong_id`),
  KEY `idx_nhat_ky_ngay_tao` (`ngay_tao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `phan_cong_thu_ky` (
  `nguoi_duoc_ho_tro_id` int NOT NULL COMMENT 'Lanh dao hoac truong phong duoc ho tro',
  `thu_ky_id` int NOT NULL COMMENT 'Nguoi dung co vai tro he thong THU_KY',
  `nguoi_phan_cong_id` int NOT NULL COMMENT 'Nguoi thuc hien phan cong',
  `ngay_phan_cong` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`nguoi_duoc_ho_tro_id`),
  KEY `idx_pctk_thu_ky` (`thu_ky_id`),
  KEY `idx_pctk_nguoi_phan_cong` (`nguoi_phan_cong_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `phien_ban_bien_ban` (
  `phien_ban_id` int NOT NULL AUTO_INCREMENT,
  `bien_ban_id` int NOT NULL,
  `nguoi_cap_nhat_id` int NOT NULL,
  `so_phien_ban` int NOT NULL,
  `noi_dung` longtext COLLATE utf8mb4_unicode_ci NOT NULL,
  `ghi_chu_thay_doi` text COLLATE utf8mb4_unicode_ci,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phien_ban_id`),
  UNIQUE KEY `uk_phien_ban_bien_ban` (`bien_ban_id`,`so_phien_ban`),
  KEY `idx_phien_ban_nguoi_cap_nhat` (`nguoi_cap_nhat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `phieu_bieu_quyet` (
  `phieu_bieu_quyet_id` int NOT NULL AUTO_INCREMENT,
  `bieu_quyet_id` int NOT NULL,
  `lua_chon_id` int NOT NULL,
  `nguoi_dung_id` int DEFAULT NULL,
  `ma_phieu` char(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thoi_gian_bieu_quyet` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phieu_bieu_quyet_id`),
  UNIQUE KEY `uk_phieu_bieu_quyet_ma_phieu` (`bieu_quyet_id`,`ma_phieu`),
  KEY `idx_phieu_bieu_quyet_lua_chon` (`lua_chon_id`),
  KEY `idx_phieu_bieu_quyet_nguoi_dung` (`bieu_quyet_id`,`nguoi_dung_id`),
  KEY `fk_phieu_bieu_quyet_lua_chon` (`lua_chon_id`,`bieu_quyet_id`),
  KEY `fk_phieu_bieu_quyet_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `phong_ban` (
  `phong_ban_id` int NOT NULL AUTO_INCREMENT,
  `ten_phong_ban` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `truong_phong_id` int DEFAULT NULL COMMENT 'Nguoi dung duoc phan cong lam truong phong',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HOAT_DONG',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phong_ban_id`),
  UNIQUE KEY `uk_phong_ban_ten` (`ten_phong_ban`),
  UNIQUE KEY `uk_phong_ban_truong_phong` (`truong_phong_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `phong_hop` (
  `phong_hop_id` int NOT NULL AUTO_INCREMENT,
  `ten_phong_hop` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vi_tri` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `suc_chua` int NOT NULL DEFAULT '1',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `trang_thai` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SAN_SANG',
  PRIMARY KEY (`phong_hop_id`),
  UNIQUE KEY `uk_phong_hop_ten` (`ten_phong_hop`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tai_lieu_cuoc_hop` (
  `tai_lieu_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `nguoi_tai_len_id` int NOT NULL,
  `ten_tai_lieu` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `loai_tai_lieu` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TAI_LIEU_DAU_VAO',
  `ten_file_goc` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duong_dan_file` varchar(700) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dinh_dang_file` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dung_luong_byte` bigint DEFAULT NULL,
  `noi_dung_van_ban` longtext COLLATE utf8mb4_unicode_ci,
  `trang_thai_xu_ly` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHUA_XU_LY',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `ngay_tai_len` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tai_lieu_id`),
  KEY `idx_tai_lieu_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_tai_lieu_loai` (`loai_tai_lieu`),
  KEY `idx_tai_lieu_nguoi_tai` (`nguoi_tai_len_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `thanh_vien_bieu_quyet` (
  `bieu_quyet_id` int NOT NULL,
  `nguoi_dung_id` int NOT NULL,
  PRIMARY KEY (`bieu_quyet_id`,`nguoi_dung_id`),
  KEY `fk_tvbq_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `thanh_vien_cuoc_hop` (
  `cuoc_hop_id` int NOT NULL,
  `nguoi_dung_id` int NOT NULL,
  `vai_tro_tai_cuoc_hop` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'THAM_DU',
  `trang_thai_moi` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHO_PHAN_HOI',
  `trang_thai_tham_du` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHUA_DIEM_DANH',
  `thoi_gian_diem_danh` datetime DEFAULT NULL,
  `thoi_gian_roi_cuoc_hop` datetime DEFAULT NULL,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thoi_gian_tu_diem_danh` datetime(6) DEFAULT NULL,
  `thoi_gian_doi_chieu_diem_danh` datetime(6) DEFAULT NULL,
  `ngay_moi` datetime DEFAULT NULL,
  PRIMARY KEY (`cuoc_hop_id`,`nguoi_dung_id`),
  KEY `idx_tvc_hop_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `thiet_bi` (
  `thiet_bi_id` int NOT NULL AUTO_INCREMENT,
  `phong_hop_id` int DEFAULT NULL,
  `ten_thiet_bi` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ma_thiet_bi` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `so_luong` int NOT NULL DEFAULT '1',
  `tinh_trang` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TOT',
  `ghi_chu` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`thiet_bi_id`),
  UNIQUE KEY `uk_thiet_bi_ma` (`ma_thiet_bi`),
  KEY `idx_thiet_bi_phong` (`phong_hop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `thong_bao` (
  `thong_bao_id` int NOT NULL AUTO_INCREMENT,
  `nguoi_dung_id` int NOT NULL,
  `cuoc_hop_id` int DEFAULT NULL,
  `tieu_de` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `noi_dung` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `loai_thong_bao` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HE_THONG',
  `da_doc` tinyint(1) NOT NULL DEFAULT '0',
  `thoi_gian_doc` datetime DEFAULT NULL,
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`thong_bao_id`),
  KEY `idx_thong_bao_nguoi_doc` (`nguoi_dung_id`,`da_doc`),
  KEY `idx_thong_bao_cuoc_hop` (`cuoc_hop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `vai_tro` (
  `vai_tro_id` int NOT NULL AUTO_INCREMENT,
  `ma_vai_tro` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ten_vai_tro` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HOAT_DONG',
  PRIMARY KEY (`vai_tro_id`),
  UNIQUE KEY `uk_vai_tro_ma` (`ma_vai_tro`),
  UNIQUE KEY `uk_vai_tro_ten` (`ten_vai_tro`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `yeu_cau_phe_duyet` (
  `yeu_cau_phe_duyet_id` int NOT NULL AUTO_INCREMENT,
  `loai_yeu_cau` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cuoc_hop_id` int NOT NULL,
  `lich_hop_id` int DEFAULT NULL,
  `phien_ban_id` int DEFAULT NULL,
  `nguoi_trinh_id` int NOT NULL,
  `thoi_gian_trinh` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHO_DUYET',
  `ghi_chu_trinh` text COLLATE utf8mb4_unicode_ci,
  `nguoi_duyet_id` int DEFAULT NULL,
  `thu_ky_tiep_nhan_id` int DEFAULT NULL,
  `cap_phe_duyet` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thu_ky_ban_giao_id` int DEFAULT NULL,
  `ke_hoach_da_trinh` longtext COLLATE utf8mb4_unicode_ci,
  `yeu_cau_truoc_id` int DEFAULT NULL,
  PRIMARY KEY (`yeu_cau_phe_duyet_id`),
  KEY `idx_ycpd_trang_thai_thoi_gian` (`trang_thai`,`thoi_gian_trinh`),
  KEY `idx_ycpd_cuoc_hop` (`cuoc_hop_id`),
  KEY `fk_ycpd_lich_hop` (`lich_hop_id`),
  KEY `fk_ycpd_phien_ban` (`phien_ban_id`),
  KEY `fk_ycpd_nguoi_trinh` (`nguoi_trinh_id`),
  KEY `idx_ycpd_nguoi_duyet_tt` (`nguoi_duyet_id`,`trang_thai`),
  KEY `idx_ycpd_thu_ky_tt` (`thu_ky_tiep_nhan_id`,`trang_thai`),
  KEY `fk_yeu_cau_thu_ky_ban_giao` (`thu_ky_ban_giao_id`),
  KEY `idx_ycpd_yeu_cau_truoc` (`yeu_cau_truoc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `yeu_cau_phe_duyet_tai_lieu` (
  `yeu_cau_phe_duyet_id` int NOT NULL,
  `tai_lieu_id` int NOT NULL,
  `ngay_dinh_kem` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`yeu_cau_phe_duyet_id`,`tai_lieu_id`),
  KEY `idx_ycpdtl_tai_lieu` (`tai_lieu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `nhiem_vu` (
  `nhiem_vu_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `tieu_de` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `nguoi_giao_id` int NOT NULL,
  `nguoi_thuc_hien_id` int NOT NULL,
  `han_hoan_thanh` datetime DEFAULT NULL,
  `muc_do_uu_tien` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TRUNG_BINH',
  `trang_thai` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHUA_BAT_DAU',
  `phan_tram_hoan_thanh` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_hoan_thanh` datetime DEFAULT NULL,
  PRIMARY KEY (`nhiem_vu_id`),
  KEY `idx_nhiem_vu_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_nhiem_vu_nguoi_thuc_hien` (`nguoi_thuc_hien_id`),
  KEY `idx_nhiem_vu_nguoi_giao` (`nguoi_giao_id`),
  KEY `idx_nhiem_vu_trang_thai_han` (`trang_thai`,`han_hoan_thanh`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `tien_do_nhiem_vu` (
  `tien_do_id` int NOT NULL AUTO_INCREMENT,
  `nhiem_vu_id` int NOT NULL,
  `nguoi_cap_nhat_id` int NOT NULL,
  `phan_tram_hoan_thanh` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `noi_dung_cap_nhat` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_cap_nhat` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tien_do_id`),
  KEY `idx_tien_do_nhiem_vu` (`nhiem_vu_id`),
  KEY `idx_tien_do_nguoi_cap_nhat` (`nguoi_cap_nhat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `bien_ban_cuoc_hop`
  ADD CONSTRAINT `fk_bien_ban_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bien_ban_nguoi_duyet` FOREIGN KEY (`nguoi_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bien_ban_nguoi_soan` FOREIGN KEY (`nguoi_soan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `bieu_quyet`
  ADD CONSTRAINT `fk_bieu_quyet_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bieu_quyet_nguoi_tao` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `cuoc_hop`
  ADD CONSTRAINT `fk_cuoc_hop_chu_tri` FOREIGN KEY (`nguoi_chu_tri_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_nguoi_de_xuat` FOREIGN KEY (`nguoi_de_xuat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_nguoi_tao` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_phong_ban_chu_tri` FOREIGN KEY (`phong_ban_chu_tri_id`) REFERENCES `phong_ban` (`phong_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_thu_ky` FOREIGN KEY (`thu_ky_cuoc_hop_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

ALTER TABLE `ket_luan_cuoc_hop`
  ADD CONSTRAINT `fk_ket_luan_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`),
  ADD CONSTRAINT `fk_ket_luan_nguoi_phat_hanh` FOREIGN KEY (`nguoi_phat_hanh_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`),
  ADD CONSTRAINT `fk_ket_luan_nguoi_soan` FOREIGN KEY (`nguoi_soan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

ALTER TABLE `ket_qua_xu_ly_ai`
  ADD CONSTRAINT `fk_ai_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ai_nguoi_yeu_cau` FOREIGN KEY (`nguoi_yeu_cau_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ai_tai_lieu` FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `khach_moi_cuoc_hop`
  ADD CONSTRAINT `fk_kmch_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_kmch_khach_moi` FOREIGN KEY (`khach_moi_id`) REFERENCES `khach_moi` (`khach_moi_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `lich_hop`
  ADD CONSTRAINT `fk_lich_hop_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_nguoi_de_xuat` FOREIGN KEY (`nguoi_de_xuat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_nguoi_xac_nhan` FOREIGN KEY (`nguoi_xac_nhan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_phong_hop` FOREIGN KEY (`phong_hop_id`) REFERENCES `phong_hop` (`phong_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_thay_the` FOREIGN KEY (`lich_thay_the_id`) REFERENCES `lich_hop` (`lich_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `lich_su_phe_duyet`
  ADD CONSTRAINT `fk_lspd_bien_ban` FOREIGN KEY (`bien_ban_id`) REFERENCES `bien_ban_cuoc_hop` (`bien_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_lich_hop` FOREIGN KEY (`lich_hop_id`) REFERENCES `lich_hop` (`lich_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_nguoi_phe_duyet` FOREIGN KEY (`nguoi_phe_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_yeu_cau_phe_duyet` FOREIGN KEY (`yeu_cau_phe_duyet_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`);

ALTER TABLE `lua_chon_bieu_quyet`
  ADD CONSTRAINT `fk_lua_chon_bieu_quyet` FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `nguoi_dung`
  ADD CONSTRAINT `fk_nguoi_dung_phong_ban` FOREIGN KEY (`phong_ban_id`) REFERENCES `phong_ban` (`phong_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `nguoi_dung_vai_tro`
  ADD CONSTRAINT `fk_ndvt_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ndvt_vai_tro` FOREIGN KEY (`vai_tro_id`) REFERENCES `vai_tro` (`vai_tro_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `nhat_ky_he_thong`
  ADD CONSTRAINT `fk_nhat_ky_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `phan_cong_thu_ky`
  ADD CONSTRAINT `fk_pctk_nguoi_duoc_ho_tro` FOREIGN KEY (`nguoi_duoc_ho_tro_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pctk_nguoi_phan_cong` FOREIGN KEY (`nguoi_phan_cong_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pctk_thu_ky` FOREIGN KEY (`thu_ky_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `phien_ban_bien_ban`
  ADD CONSTRAINT `fk_pbbb_bien_ban` FOREIGN KEY (`bien_ban_id`) REFERENCES `bien_ban_cuoc_hop` (`bien_ban_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pbbb_nguoi_cap_nhat` FOREIGN KEY (`nguoi_cap_nhat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `phieu_bieu_quyet`
  ADD CONSTRAINT `fk_phieu_bieu_quyet_bieu_quyet` FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_phieu_bieu_quyet_lua_chon` FOREIGN KEY (`lua_chon_id`,`bieu_quyet_id`) REFERENCES `lua_chon_bieu_quyet` (`lua_chon_id`, `bieu_quyet_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_phieu_bieu_quyet_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `phong_ban`
  ADD CONSTRAINT `fk_phong_ban_truong_phong` FOREIGN KEY (`truong_phong_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `tai_lieu_cuoc_hop`
  ADD CONSTRAINT `fk_tai_lieu_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tai_lieu_nguoi_tai` FOREIGN KEY (`nguoi_tai_len_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `thanh_vien_bieu_quyet`
  ADD CONSTRAINT `fk_tvbq_bieu_quyet` FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`),
  ADD CONSTRAINT `fk_tvbq_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

ALTER TABLE `thanh_vien_cuoc_hop`
  ADD CONSTRAINT `fk_tvc_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tvc_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `thiet_bi`
  ADD CONSTRAINT `fk_thiet_bi_phong_hop` FOREIGN KEY (`phong_hop_id`) REFERENCES `phong_hop` (`phong_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `thong_bao`
  ADD CONSTRAINT `fk_thong_bao_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_thong_bao_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `yeu_cau_phe_duyet`
  ADD CONSTRAINT `fk_ycpd_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`),
  ADD CONSTRAINT `fk_ycpd_lich_hop` FOREIGN KEY (`lich_hop_id`) REFERENCES `lich_hop` (`lich_hop_id`),
  ADD CONSTRAINT `fk_ycpd_nguoi_duyet` FOREIGN KEY (`nguoi_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ycpd_nguoi_trinh` FOREIGN KEY (`nguoi_trinh_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`),
  ADD CONSTRAINT `fk_ycpd_phien_ban` FOREIGN KEY (`phien_ban_id`) REFERENCES `phien_ban_bien_ban` (`phien_ban_id`),
  ADD CONSTRAINT `fk_ycpd_thu_ky_tiep_nhan` FOREIGN KEY (`thu_ky_tiep_nhan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ycpd_yeu_cau_truoc` FOREIGN KEY (`yeu_cau_truoc_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_yeu_cau_thu_ky_ban_giao` FOREIGN KEY (`thu_ky_ban_giao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

ALTER TABLE `yeu_cau_phe_duyet_tai_lieu`
  ADD CONSTRAINT `fk_ycpdtl_tai_lieu` FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ycpdtl_yeu_cau` FOREIGN KEY (`yeu_cau_phe_duyet_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `nhiem_vu`
  ADD CONSTRAINT `fk_nhiem_vu_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nhiem_vu_nguoi_giao` FOREIGN KEY (`nguoi_giao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nhiem_vu_nguoi_thuc_hien` FOREIGN KEY (`nguoi_thuc_hien_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE `tien_do_nhiem_vu`
  ADD CONSTRAINT `fk_tdnv_nguoi_cap_nhat` FOREIGN KEY (`nguoi_cap_nhat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tdnv_nhiem_vu` FOREIGN KEY (`nhiem_vu_id`) REFERENCES `nhiem_vu` (`nhiem_vu_id`) ON DELETE CASCADE ON UPDATE CASCADE;

INSERT INTO `vai_tro` (`ma_vai_tro`, `ten_vai_tro`, `trang_thai`) VALUES
  ('ADMIN', 'Quản trị viên', 'HOAT_DONG'),
  ('THU_KY', 'Thư ký', 'HOAT_DONG'),
  ('LANH_DAO', 'Lãnh đạo', 'HOAT_DONG'),
  ('CAN_BO', 'Cán bộ', 'HOAT_DONG');
