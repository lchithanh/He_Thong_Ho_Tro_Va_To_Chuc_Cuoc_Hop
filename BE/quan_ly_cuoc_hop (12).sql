-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Nov 10, 2026 at 10:13 AM
-- Server version: 8.4.7
-- PHP Version: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `quan_ly_cuoc_hop`
--

-- --------------------------------------------------------

--
-- Table structure for table `bien_ban_cuoc_hop`
--

DROP TABLE IF EXISTS `bien_ban_cuoc_hop`;
CREATE TABLE IF NOT EXISTS `bien_ban_cuoc_hop` (
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
  PRIMARY KEY (`bien_ban_id`),
  UNIQUE KEY `uk_bien_ban_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bien_ban_nguoi_soan` (`nguoi_soan_id`),
  KEY `idx_bien_ban_nguoi_duyet` (`nguoi_duyet_id`),
  KEY `idx_bien_ban_trang_thai` (`trang_thai`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bien_ban_cuoc_hop`
--

INSERT INTO `bien_ban_cuoc_hop` (`bien_ban_id`, `cuoc_hop_id`, `nguoi_soan_id`, `nguoi_duyet_id`, `tieu_de`, `noi_dung_goc`, `noi_dung_chinh_thuc`, `duong_dan_file_word`, `duong_dan_file_pdf`, `trang_thai`, `y_kien_phan_hoi`, `ngay_soan`, `ngay_trinh_duyet`, `ngay_duyet`, `ngay_tao`, `ngay_cap_nhat`) VALUES
(1, 11, 6, 8, 'Biên bản cuộc họp số 11', 'Ghi chú của thư ký trong cuộc họp.', '<h2>I. Thành phần tham dự</h2><p>Danh sách người tham dự thực tế.</p><h2>II. Nội dung cuộc họp</h2><p>Đã rà soát tiến độ và thảo luận những khó khăn.</p><h2>III. Biểu quyết</h2><p>Không tổ chức biểu quyết.</p><h2>IV. Kết luận</h2><p>Các thành viên hoàn thiện báo cáo trước cuộc họp tiếp theo.</p>', NULL, NULL, 'CHO_DUYET', NULL, '2026-11-10 15:38:43', '2026-11-10 15:43:54', NULL, '2026-11-10 15:38:42', '2026-11-10 15:43:54');

-- --------------------------------------------------------

--
-- Table structure for table `bieu_quyet`
--

DROP TABLE IF EXISTS `bieu_quyet`;
CREATE TABLE IF NOT EXISTS `bieu_quyet` (
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
  PRIMARY KEY (`bieu_quyet_id`),
  KEY `idx_bieu_quyet_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bieu_quyet_trang_thai` (`trang_thai`),
  KEY `fk_bieu_quyet_nguoi_tao` (`nguoi_tao_id`)
) ;

-- --------------------------------------------------------

--
-- Table structure for table `cuoc_hop`
--

DROP TABLE IF EXISTS `cuoc_hop`;
CREATE TABLE IF NOT EXISTS `cuoc_hop` (
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
  PRIMARY KEY (`cuoc_hop_id`),
  UNIQUE KEY `uk_cuoc_hop_ma` (`ma_cuoc_hop`),
  KEY `idx_cuoc_hop_trang_thai` (`trang_thai`),
  KEY `idx_cuoc_hop_nguoi_tao` (`nguoi_tao_id`),
  KEY `idx_cuoc_hop_chu_tri` (`nguoi_chu_tri_id`),
  KEY `idx_cuoc_hop_nguoi_de_xuat` (`nguoi_de_xuat_id`),
  KEY `idx_cuoc_hop_phong_ban_chu_tri` (`phong_ban_chu_tri_id`),
  KEY `idx_cuoc_hop_pham_vi_buoc_xu_ly` (`pham_vi_cuoc_hop`,`buoc_xu_ly_hien_tai`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cuoc_hop`
--

INSERT INTO `cuoc_hop` (`cuoc_hop_id`, `ma_cuoc_hop`, `tieu_de`, `muc_tieu`, `noi_dung_du_kien`, `loai_hinh`, `pham_vi_cuoc_hop`, `phong_ban_chu_tri_id`, `nguoi_tao_id`, `nguoi_de_xuat_id`, `nguoi_chu_tri_id`, `buoc_xu_ly_hien_tai`, `trang_thai`, `thoi_gian_bat_dau_thuc_te`, `thoi_gian_ket_thuc_thuc_te`, `ngay_tao`, `ngay_cap_nhat`, `co_khach_moi_ben_ngoai`) VALUES
(1, 'CH-TEST-001', 'Cuộc họp kiểm thử chức năng đặt lịch', 'Kiểm tra chức năng tạo và xác nhận lịch họp', 'Thảo luận và kiểm tra lịch họp thử nghiệm', 'TRUC_TIEP', 'NOI_BO', 2, 1, 1, 8, 'NHAP', 'NHAP', NULL, NULL, '2026-11-02 00:36:18', '2026-11-04 00:43:41', 0),
(2, 'CH-26cb0c8d-2048-4dea-90d7-0bd6aebaf43f', 'Đề xuất họp nội bộ phòng', 'Thống nhất kế hoạch công việc', 'Đánh giá tiến độ và phân công nhiệm vụ', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'NHAP', 'NHAP', NULL, NULL, '2026-11-04 20:26:38', NULL, 0),
(3, 'CH-6c4cbd2a-595d-4339-bc53-5cd6f78e0c36', 'Kiểm tra đề xuất họp nội bộ', 'Thống nhất công việc', 'Đánh giá tiến độ', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'NHAP', 'NHAP', NULL, NULL, '2026-11-04 20:29:06', NULL, 0),
(4, 'CH-8b3a70f0-919b-4fe5-8155-7a53c439890f', 'Họp nội bộ tự động xác định phòng ban', 'Thống nhất công việc', 'Đánh giá tiến độ', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'NHAP', 'NHAP', NULL, NULL, '2026-11-04 20:40:15', NULL, 0),
(5, 'CH-94a7ccc3-4431-4037-a326-88c55343598e', 'Họp triển khai kế hoạch toàn cơ quan', 'Thống nhất kế hoạch công tác', 'Triển khai nhiệm vụ cho các phòng ban', 'TRUC_TIEP', 'TOAN_CO_QUAN', NULL, 8, 8, NULL, 'NHAP', 'NHAP', NULL, NULL, '2026-11-04 20:41:00', NULL, 0),
(6, 'CH-406b4987-315c-494a-a0ee-c60620e3c008', 'Họp nội bộ — nội dung đã cập nhật', 'Thống nhất kế hoạch công việc mới', 'Bổ sung nội dung đánh giá tiến độ và phân công', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'NHAP', 'NHAP', NULL, NULL, '2026-11-04 20:48:35', '2026-11-04 23:33:55', 0),
(7, 'CH-5f5697a8-9d87-49fa-9609-41d16ad45e51', 'Họp triển khai kế hoạch', 'Thống nhất công việc', 'Đánh giá tiến độ', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'LAP_KE_HOACH', 'DANG_CHUAN_BI', NULL, NULL, '2026-11-04 20:48:42', '2026-11-04 21:47:02', 0),
(8, 'CH-9b05d4cd-52f0-4757-9b5e-2d95bd96ffcf', 'Họp triển khai kế hoạch', 'Thống nhất công việc', 'Đánh giá tiến độ', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'LAP_KE_HOACH', 'DANG_CHUAN_BI', NULL, NULL, '2026-11-04 20:58:19', '2026-11-04 21:04:46', 0),
(9, 'CH-334221a2-2fdc-4cea-9609-4be180d3a1c6', 'Kiểm tra lưu nháp không chọn thư ký', 'Chuẩn bị nội dung', 'Nội dung chưa hoàn thiện', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, NULL, 'NHAP', 'NHAP', NULL, NULL, '2026-11-04 21:01:56', NULL, 0),
(10, 'CH-c675dc30-2fb4-4d8a-8457-707a8b279158', 'Đề xuất họp kèm văn bản', 'Theo nội dung văn bản đính kèm', NULL, 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'LAP_KE_HOACH', 'DANG_CHUAN_BI', NULL, NULL, '2026-11-04 21:52:26', '2026-11-04 22:01:48', 0),
(11, 'CH-8c7bd90a-555c-4f5b-93f7-b6fdf8c1b1be', 'Họp triển khai kế hoạch công tác', 'Thống nhất nhiệm vụ và tiến độ thực hiện', '1. Báo cáo tiến độ\n2. Thảo luận khó khăn\n3. Phân công nhiệm vụ', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-10 00:20:49', '2026-11-10 00:21:40', '2026-11-04 22:19:55', '2026-11-10 00:21:40', 0),
(12, 'CH-81b3a995-1c4a-4816-b688-cf08e00e041c', 'Họp liên phòng triển khai kế hoạch công tác', 'Thống nhất nhiệm vụ phối hợp giữa các phòng ban', 'Rà soát tiến độ và phân công đầu mối thực hiện', 'TRUC_TIEP', 'LIEN_PHONG_BAN', NULL, 4, 4, NULL, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-05 18:50:26', '2026-11-05 22:03:27', 0),
(13, 'CH-78ea4e51-9250-4ce4-af1f-ab78487bfea9', 'Kiểm thử tự động phân công lãnh đạo duyệt', 'Thống nhất kế hoạch phối hợp liên phòng', 'Rà soát tiến độ và phân công đầu mối', 'TRUC_TIEP', 'LIEN_PHONG_BAN', NULL, 4, 4, NULL, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-05 22:15:30', '2026-11-05 22:18:22', 0),
(14, 'CH-cf9bd401-fb64-4b69-9ddb-7deb871e3d49', 'Kiểm thử trả lại kế hoạch họp liên phòng', 'Thống nhất công việc phối hợp giữa các phòng ban', 'Phòng Hành chính tổng hợp tiến độ. Mỗi phòng chuyên môn cử một đầu mối và gửi báo cáo hằng tuần.', 'TRUC_TIEP', 'LIEN_PHONG_BAN', NULL, 4, 4, NULL, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-05 22:40:13', '2026-11-05 22:48:24', 0),
(15, 'CH-07f9eba3-b852-4b69-84ed-970ca920570f', 'Kiểm thử duyệt kế hoạch họp liên phòng ban', 'Thống nhất tiến độ và phân công giữa các phòng ban', '1. Báo cáo tiến độ\n2. Thảo luận các vướng mắc\n3. Phân công nhiệm vụ', 'TRUC_TIEP', 'LIEN_PHONG_BAN', 2, 4, 4, 8, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-05 23:58:37', '2026-11-06 00:01:53', 0),
(16, 'CH-42316c63-edd9-4fa7-9285-d5bc3d4d86d2', 'Kiểm thử luồng lãnh đạo đề xuất và duyệt kế hoạch', 'Kiểm tra duyệt kế hoạch trước khi tạo lịch', 'Đánh giá tiến độ công việc và phân công nhiệm vụ', 'TRUC_TIEP', 'TOAN_CO_QUAN', NULL, 4, 4, 8, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-09 22:33:52', '2026-11-09 22:41:32', 0),
(17, 'CH-86d78915-be34-47e6-8be2-af007c81b9c6', 'Kiểm thử họp nội bộ phòng Văn phòng', 'Kiểm tra thư ký mặc định và trưởng phòng duyệt', 'Đánh giá công việc nội bộ', 'TRUC_TIEP', 'NOI_BO', 1, 5, 5, 5, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-09 22:49:39', '2026-11-09 22:51:31', 0);

-- --------------------------------------------------------

--
-- Table structure for table `ket_qua_xu_ly_ai`
--

DROP TABLE IF EXISTS `ket_qua_xu_ly_ai`;
CREATE TABLE IF NOT EXISTS `ket_qua_xu_ly_ai` (
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

-- --------------------------------------------------------

--
-- Table structure for table `khach_moi`
--

DROP TABLE IF EXISTS `khach_moi`;
CREATE TABLE IF NOT EXISTS `khach_moi` (
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

-- --------------------------------------------------------

--
-- Table structure for table `khach_moi_cuoc_hop`
--

DROP TABLE IF EXISTS `khach_moi_cuoc_hop`;
CREATE TABLE IF NOT EXISTS `khach_moi_cuoc_hop` (
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

-- --------------------------------------------------------

--
-- Table structure for table `lich_hop`
--

DROP TABLE IF EXISTS `lich_hop`;
CREATE TABLE IF NOT EXISTS `lich_hop` (
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
) ;

--
-- Dumping data for table `lich_hop`
--

INSERT INTO `lich_hop` (`lich_hop_id`, `cuoc_hop_id`, `phong_hop_id`, `nguoi_de_xuat_id`, `nguoi_xac_nhan_id`, `thoi_gian_bat_dau`, `thoi_gian_ket_thuc`, `diem_phu_hop`, `trang_thai`, `ly_do_thay_doi`, `thoi_gian_xac_nhan`, `lich_thay_the_id`, `ngay_tao`) VALUES
(1, 1, 1, 1, 1, '2026-11-05 08:00:00', '2026-11-05 10:00:00', NULL, 'THAY_THE', NULL, '2026-11-02 00:39:58', NULL, '2026-11-02 00:38:11'),
(2, 1, 2, 1, 1, '2026-11-06 08:00:00', '2026-11-06 10:00:00', NULL, 'TU_CHOI', 'Thoi gian khong phu hop', '2026-11-02 00:41:01', NULL, '2026-11-02 00:40:39'),
(3, 1, 2, 1, 1, '2026-11-06 08:00:00', '2026-11-06 10:00:00', NULL, 'DA_XAC_NHAN', 'Đề xuất lịch thay thế do cần điều chỉnh thời gian họp', '2026-11-02 00:55:34', 1, '2026-11-02 00:45:37'),
(4, 1, 1, 8, NULL, '2026-11-10 08:00:00', '2026-11-10 10:00:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-04 00:48:01'),
(5, 8, 1, 6, 8, '2026-11-10 09:00:00', '2026-11-10 10:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-04 21:13:40', NULL, '2026-11-04 21:12:06'),
(6, 11, 1, 8, 8, '2026-11-10 08:00:00', '2026-11-10 09:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-05 18:42:25', NULL, '2026-11-05 18:40:56'),
(7, 12, 1, 6, NULL, '2026-11-10 10:30:00', '2026-11-10 11:30:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-05 18:53:06'),
(8, 12, 1, 6, 4, '2026-11-10 10:30:00', '2026-11-10 11:30:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-05 22:03:28', NULL, '2026-11-05 18:53:15'),
(9, 13, 1, 6, 4, '2026-11-11 10:30:00', '2026-11-11 11:30:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-05 22:18:22', NULL, '2026-11-05 22:16:34'),
(10, 14, 2, 6, 4, '2026-11-12 14:00:00', '2026-11-12 15:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-05 22:48:24', NULL, '2026-11-05 22:41:03'),
(11, 15, 1, 6, 4, '2026-11-13 09:00:00', '2026-11-13 10:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-06 00:01:53', NULL, '2026-11-05 23:59:13'),
(12, 16, 1, 6, 4, '2026-11-16 09:00:00', '2026-11-16 10:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-09 22:43:39', NULL, '2026-11-09 22:42:48'),
(13, 11, 1, 6, 8, '2026-11-17 09:00:00', '2026-11-17 10:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-09 23:43:42', NULL, '2026-11-09 23:43:09');

-- --------------------------------------------------------

--
-- Table structure for table `lich_su_phe_duyet`
--

DROP TABLE IF EXISTS `lich_su_phe_duyet`;
CREATE TABLE IF NOT EXISTS `lich_su_phe_duyet` (
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
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `lich_su_phe_duyet`
--

INSERT INTO `lich_su_phe_duyet` (`lich_su_phe_duyet_id`, `cuoc_hop_id`, `lich_hop_id`, `bien_ban_id`, `nguoi_phe_duyet_id`, `loai_phe_duyet`, `hanh_dong`, `y_kien`, `thoi_gian_phe_duyet`, `yeu_cau_phe_duyet_id`) VALUES
(1, 1, 1, NULL, 1, 'LICH_HOP', 'XAC_NHAN', 'Lịch họp đã được xác nhận', '2026-11-02 00:39:57', NULL),
(2, 1, 2, NULL, 1, 'LICH_HOP', 'TU_CHOI', 'Thoi gian khong phu hop', '2026-11-02 00:41:01', NULL),
(3, 1, 3, NULL, 1, 'LICH_HOP', 'XAC_NHAN', 'Lịch họp đã được xác nhận', '2026-11-02 00:55:33', NULL),
(4, 8, 5, NULL, 8, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-04 21:13:40', NULL),
(5, 11, 6, NULL, 8, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-05 18:42:25', NULL),
(6, 12, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình lãnh đạo xem xét kế hoạch và lịch họp liên phòng ban', '2026-11-05 18:54:14', 1),
(7, 12, 8, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-05 22:03:27', NULL),
(8, 12, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý kế hoạch và lịch dự kiến', '2026-11-05 22:03:27', 1),
(9, 13, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Kiểm thử tự động phân công lãnh đạo', '2026-11-05 22:16:57', 2),
(10, 13, 9, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-05 22:18:22', NULL),
(11, 13, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý kế hoạch và lịch dự kiến', '2026-11-05 22:18:22', 2),
(12, 14, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình kế hoạch họp liên phòng để lãnh đạo xem xét', '2026-11-05 22:41:30', 3),
(13, 14, NULL, NULL, 4, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'Bổ sung nội dung phân công cụ thể cho từng phòng ban', '2026-11-05 22:41:47', 3),
(14, 14, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Đã bổ sung nội dung phân công theo ý kiến lãnh đạo', '2026-11-05 22:43:11', 4),
(15, 14, 10, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-05 22:48:24', NULL),
(16, 14, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý kế hoạch sau khi chỉnh sửa', '2026-11-05 22:48:24', 4),
(17, 15, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình duyệt kế hoạch và lịch họp liên phòng ban', '2026-11-06 00:00:41', 5),
(18, 15, 11, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-06 00:01:53', NULL),
(19, 15, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý kế hoạch', '2026-11-06 00:01:53', 5),
(20, 16, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Kính trình lãnh đạo xem xét kế hoạch tổ chức cuộc họp', '2026-11-09 22:40:57', 6),
(21, 16, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-09 22:41:32', 6),
(22, 16, 12, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-09 22:43:39', NULL),
(23, 17, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Kính trình trưởng phòng duyệt kế hoạch họp nội bộ', '2026-11-09 22:50:56', 7),
(24, 17, NULL, NULL, 5, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-09 22:51:31', 7),
(25, 11, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Kính trình xem xét kế hoạch và tài liệu đính kèm', '2026-11-09 23:39:02', 8),
(26, 11, NULL, NULL, 8, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý kế hoạch và tài liệu đính kèm', '2026-11-09 23:42:34', 8),
(27, 11, 13, NULL, 8, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-09 23:43:41', NULL),
(28, 11, NULL, 1, 6, 'BIEN_BAN', 'TRINH_DUYET', 'Kính trình chủ trì xem xét biên bản đã hoàn thiện.', '2026-11-10 15:43:54', 9);

-- --------------------------------------------------------

--
-- Table structure for table `lua_chon_bieu_quyet`
--

DROP TABLE IF EXISTS `lua_chon_bieu_quyet`;
CREATE TABLE IF NOT EXISTS `lua_chon_bieu_quyet` (
  `lua_chon_id` int NOT NULL AUTO_INCREMENT,
  `bieu_quyet_id` int NOT NULL,
  `noi_dung_lua_chon` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `la_lua_chon_trang_thai` tinyint(1) NOT NULL DEFAULT '0',
  `thu_tu_hien_thi` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`lua_chon_id`),
  UNIQUE KEY `uk_lua_chon_bieu_quyet_pair` (`lua_chon_id`,`bieu_quyet_id`),
  KEY `idx_lua_chon_bieu_quyet` (`bieu_quyet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `nguoi_dung`
--

DROP TABLE IF EXISTS `nguoi_dung`;
CREATE TABLE IF NOT EXISTS `nguoi_dung` (
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `nguoi_dung`
--

INSERT INTO `nguoi_dung` (`nguoi_dung_id`, `phong_ban_id`, `ma_nhan_vien`, `ho_ten`, `email`, `so_dien_thoai`, `ten_dang_nhap`, `mat_khau_ma_hoa`, `chuc_vu`, `trang_thai`, `ngay_tao`, `ngay_cap_nhat`) VALUES
(1, 2, 'NV0001', 'Quản trị viên', 'admin@example.com', '0900000000', 'admin', '$2a$10$iSMXKeJ9E5cNI8TOGqLSiuGZfePMCIbIALdlWx0ylGB4UdwVuOSy.', 'Quản trị hệ thống', 'HOAT_DONG', '2026-10-31 02:30:49', NULL),
(4, 2, 'NV_TEST_01', 'Nguyen Van Test Updated', 'test01@example.com', '0901234567', 'test01', '$2a$10$vlZmUm5tBlqzCOCYNmKE0.uV/0rrdiesP.RvmRLDRYzACMDCv780G', 'Chuyên viên', 'HOAT_DONG', '2026-10-31 22:32:23', '2026-10-31 22:56:50'),
(5, 1, 'NV005', 'Nguyen Van Can Bo', 'canbo@example.com', '0900000005', 'canbo', '$2a$10$DxJJd7aBWgqH8.UBAe/yYeVSlB27q6vmpXg5a8aYyL26C7A4pa54W', 'Trưởng phòng', 'HOAT_DONG', '2026-10-31 23:32:25', '2026-11-09 22:53:42'),
(6, 2, 'NV_TK_001', 'Nguyễn Thị Lan', 'thuky01@example.com', NULL, 'thuky01', '$2a$10$SkGdWC8C7JHZNJXPsVgsQOL4J6DSjpeF67.BeKNBKWiXnLq/LXTw6', 'Chuyên viên', 'HOAT_DONG', '2026-11-04 00:31:24', '2026-11-04 00:37:04'),
(8, 2, 'NV_TP_002', 'Trần Văn Minh', 'truongphong02@example.com', NULL, 'truongphong02', '$2a$10$QDQniFvghqVRKdmJAHFmreny9tjpMwdCl7OIajms4gJsun5aMSmQa', 'Trưởng phòng', 'HOAT_DONG', '2026-11-04 00:43:27', '2026-11-04 00:44:39');

-- --------------------------------------------------------

--
-- Table structure for table `nguoi_dung_vai_tro`
--

DROP TABLE IF EXISTS `nguoi_dung_vai_tro`;
CREATE TABLE IF NOT EXISTS `nguoi_dung_vai_tro` (
  `nguoi_dung_id` int NOT NULL,
  `vai_tro_id` int NOT NULL,
  PRIMARY KEY (`nguoi_dung_id`),
  KEY `idx_ndvt_vai_tro` (`vai_tro_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `nguoi_dung_vai_tro`
--

INSERT INTO `nguoi_dung_vai_tro` (`nguoi_dung_id`, `vai_tro_id`) VALUES
(1, 1),
(6, 2),
(4, 3),
(5, 4),
(8, 4);

-- --------------------------------------------------------

--
-- Table structure for table `nhat_ky_he_thong`
--

DROP TABLE IF EXISTS `nhat_ky_he_thong`;
CREATE TABLE IF NOT EXISTS `nhat_ky_he_thong` (
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

-- --------------------------------------------------------

--
-- Table structure for table `nhiem_vu`
--

DROP TABLE IF EXISTS `nhiem_vu`;
CREATE TABLE IF NOT EXISTS `nhiem_vu` (
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
) ;

-- --------------------------------------------------------

--
-- Table structure for table `phan_cong_thu_ky`
--

DROP TABLE IF EXISTS `phan_cong_thu_ky`;
CREATE TABLE IF NOT EXISTS `phan_cong_thu_ky` (
  `nguoi_duoc_ho_tro_id` int NOT NULL COMMENT 'Lanh dao hoac truong phong duoc ho tro',
  `thu_ky_id` int NOT NULL COMMENT 'Nguoi dung co vai tro he thong THU_KY',
  `nguoi_phan_cong_id` int NOT NULL COMMENT 'Nguoi thuc hien phan cong',
  `ngay_phan_cong` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ngay_cap_nhat` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`nguoi_duoc_ho_tro_id`),
  KEY `idx_pctk_thu_ky` (`thu_ky_id`),
  KEY `idx_pctk_nguoi_phan_cong` (`nguoi_phan_cong_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `phan_cong_thu_ky`
--

INSERT INTO `phan_cong_thu_ky` (`nguoi_duoc_ho_tro_id`, `thu_ky_id`, `nguoi_phan_cong_id`, `ngay_phan_cong`, `ngay_cap_nhat`) VALUES
(5, 6, 1, '2026-11-09 22:46:38', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `phien_ban_bien_ban`
--

DROP TABLE IF EXISTS `phien_ban_bien_ban`;
CREATE TABLE IF NOT EXISTS `phien_ban_bien_ban` (
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
) ;

--
-- Dumping data for table `phien_ban_bien_ban`
--

INSERT INTO `phien_ban_bien_ban` (`phien_ban_id`, `bien_ban_id`, `nguoi_cap_nhat_id`, `so_phien_ban`, `noi_dung`, `ghi_chu_thay_doi`, `ngay_tao`) VALUES
(1, 1, 6, 1, '<h2>I. Thành phần tham dự</h2><p>Danh sách người tham dự thực tế.</p><h2>II. Nội dung cuộc họp</h2><p>Các nội dung đã thảo luận.</p><h2>III. Biểu quyết</h2><p>Không tổ chức biểu quyết.</p><h2>IV. Kết luận</h2><p>Các nội dung được thống nhất.</p>', 'Tạo bản nháp lần đầu', '2026-11-10 15:38:43'),
(2, 1, 6, 2, '<h2>I. Thành phần tham dự</h2><p>Danh sách người tham dự thực tế.</p><h2>II. Nội dung cuộc họp</h2><p>Đã rà soát tiến độ và thảo luận những khó khăn.</p><h2>III. Biểu quyết</h2><p>Không tổ chức biểu quyết.</p><h2>IV. Kết luận</h2><p>Các thành viên hoàn thiện báo cáo trước cuộc họp tiếp theo.</p>', 'Bổ sung nội dung thảo luận và kết luận', '2026-11-10 15:39:46');

-- --------------------------------------------------------

--
-- Table structure for table `phieu_bieu_quyet`
--

DROP TABLE IF EXISTS `phieu_bieu_quyet`;
CREATE TABLE IF NOT EXISTS `phieu_bieu_quyet` (
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

-- --------------------------------------------------------

--
-- Table structure for table `phong_ban`
--

DROP TABLE IF EXISTS `phong_ban`;
CREATE TABLE IF NOT EXISTS `phong_ban` (
  `phong_ban_id` int NOT NULL AUTO_INCREMENT,
  `ten_phong_ban` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `truong_phong_id` int DEFAULT NULL COMMENT 'Nguoi dung duoc phan cong lam truong phong',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HOAT_DONG',
  `ngay_tao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phong_ban_id`),
  UNIQUE KEY `uk_phong_ban_ten` (`ten_phong_ban`),
  UNIQUE KEY `uk_phong_ban_truong_phong` (`truong_phong_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `phong_ban`
--

INSERT INTO `phong_ban` (`phong_ban_id`, `ten_phong_ban`, `truong_phong_id`, `mo_ta`, `trang_thai`, `ngay_tao`) VALUES
(1, 'Văn phòng', 5, 'Phòng ban quản lý và điều phối công việc', 'HOAT_DONG', '2026-10-31 02:21:02'),
(2, 'Phòng Hành chính - Tổng hợp', 8, 'Phòng ban mẫu phục vụ kiểm thử hệ thống', 'HOAT_DONG', '2026-10-31 02:22:04'),
(3, 'Phòng Nội vụ', NULL, 'Tham mưu và thực hiện công tác nội vụ', 'HOAT_DONG', '2026-10-31 23:17:01');

-- --------------------------------------------------------

--
-- Table structure for table `phong_hop`
--

DROP TABLE IF EXISTS `phong_hop`;
CREATE TABLE IF NOT EXISTS `phong_hop` (
  `phong_hop_id` int NOT NULL AUTO_INCREMENT,
  `ten_phong_hop` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vi_tri` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `suc_chua` int NOT NULL DEFAULT '1',
  `mo_ta` text COLLATE utf8mb4_unicode_ci,
  `trang_thai` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'SAN_SANG',
  PRIMARY KEY (`phong_hop_id`),
  UNIQUE KEY `uk_phong_hop_ten` (`ten_phong_hop`)
) ;

--
-- Dumping data for table `phong_hop`
--

INSERT INTO `phong_hop` (`phong_hop_id`, `ten_phong_hop`, `vi_tri`, `suc_chua`, `mo_ta`, `trang_thai`) VALUES
(1, 'Phòng họp A2', 'Tầng 3 - Tòa nhà chính', 2222222, '- Mô tả: Phòng họp đã cập nhật thông tin', 'SAN_SANG'),
(2, 'phòng hội trường', 'tầng 3 ubnd bến thành', 100, 'Phòng họp đã được nâng cấp\n\n', 'SAN_SANG'),
(3, 'Phòng họp A1', 'Sảnh D', 50, 'Phòng họp thử nghiệm', 'NGUNG_SU_DUNG');

-- --------------------------------------------------------

--
-- Table structure for table `tai_lieu_cuoc_hop`
--

DROP TABLE IF EXISTS `tai_lieu_cuoc_hop`;
CREATE TABLE IF NOT EXISTS `tai_lieu_cuoc_hop` (
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tai_lieu_cuoc_hop`
--

INSERT INTO `tai_lieu_cuoc_hop` (`tai_lieu_id`, `cuoc_hop_id`, `nguoi_tai_len_id`, `ten_tai_lieu`, `loai_tai_lieu`, `ten_file_goc`, `duong_dan_file`, `dinh_dang_file`, `dung_luong_byte`, `noi_dung_van_ban`, `trang_thai_xu_ly`, `mo_ta`, `ngay_tai_len`) VALUES
(1, 10, 8, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'DE_XUAT_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\de-xuat-cuoc-hop\\ea9a2559-9317-46a3-8471-abd596d965bf.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-04 21:59:04'),
(2, 11, 8, 'Diem.xlsx', 'DE_XUAT_CUOC_HOP', 'Diem.xlsx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\de-xuat-cuoc-hop\\06e03fce-a289-4800-9480-803bb03c463a.xlsx', 'xlsx', 44502, NULL, 'CHUA_XU_LY', NULL, '2026-11-04 22:20:51'),
(3, 11, 8, 'TLANDROID (1).docx', 'DE_XUAT_CUOC_HOP', 'TLANDROID (1).docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\de-xuat-cuoc-hop\\8f9b4fbe-acf6-406c-832a-9514e525f687.docx', 'docx', 1390386, NULL, 'CHUA_XU_LY', NULL, '2026-11-04 22:21:37'),
(4, 11, 8, 'TLANDROID (1).docx', 'DE_XUAT_CUOC_HOP', 'TLANDROID (1).docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\de-xuat-cuoc-hop\\1b5848ac-a124-4cdb-bcd0-3200110ff493.docx', 'docx', 1390386, NULL, 'CHUA_XU_LY', NULL, '2026-11-04 22:21:48'),
(6, 11, 6, 'CV_Lu_Chi_Thanh_dieu_chinh.pdf', 'KE_HOACH_CUOC_HOP', 'CV_Lu_Chi_Thanh_dieu_chinh.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\65017202-4e79-4e62-a984-f621caf8d47110107502200517849166.pdf', 'pdf', 75757, NULL, 'CHUA_XU_LY', NULL, '2026-11-09 23:37:27');

-- --------------------------------------------------------

--
-- Table structure for table `thanh_vien_cuoc_hop`
--

DROP TABLE IF EXISTS `thanh_vien_cuoc_hop`;
CREATE TABLE IF NOT EXISTS `thanh_vien_cuoc_hop` (
  `cuoc_hop_id` int NOT NULL,
  `nguoi_dung_id` int NOT NULL,
  `vai_tro_tai_cuoc_hop` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'THAM_DU',
  `trang_thai_moi` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHO_PHAN_HOI',
  `trang_thai_tham_du` varchar(25) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CHUA_DIEM_DANH',
  `thoi_gian_diem_danh` datetime DEFAULT NULL,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`cuoc_hop_id`,`nguoi_dung_id`),
  KEY `idx_tvc_hop_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `thanh_vien_cuoc_hop`
--

INSERT INTO `thanh_vien_cuoc_hop` (`cuoc_hop_id`, `nguoi_dung_id`, `vai_tro_tai_cuoc_hop`, `trang_thai_moi`, `trang_thai_tham_du`, `thoi_gian_diem_danh`, `ghi_chu`) VALUES
(7, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(8, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(10, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(11, 5, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-10 00:21:04', NULL),
(11, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(12, 4, 'LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(12, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(13, 4, 'LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(13, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(14, 4, 'LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(14, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(15, 4, 'LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(15, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(16, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL),
(17, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `thiet_bi`
--

DROP TABLE IF EXISTS `thiet_bi`;
CREATE TABLE IF NOT EXISTS `thiet_bi` (
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
) ;

--
-- Dumping data for table `thiet_bi`
--

INSERT INTO `thiet_bi` (`thiet_bi_id`, `phong_hop_id`, `ten_thiet_bi`, `ma_thiet_bi`, `so_luong`, `tinh_trang`, `ghi_chu`) VALUES
(1, 1, 'Máy chiếu Epson EB-X06', 'TB001', 2, 'TOT', 'Đã cập nhật thông tin thiết bị'),
(2, 1, 'máy tính 01', 'tb002', 1, 'TOT', NULL),
(3, 1, 'loa', 'tb03', 4, 'NGUNG_SU_DUNG', NULL),
(4, 3, 'Máy chiếu Epson EB-X06', 'tb0022', 1, 'BAO_TRI', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `thong_bao`
--

DROP TABLE IF EXISTS `thong_bao`;
CREATE TABLE IF NOT EXISTS `thong_bao` (
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

-- --------------------------------------------------------

--
-- Table structure for table `tien_do_nhiem_vu`
--

DROP TABLE IF EXISTS `tien_do_nhiem_vu`;
CREATE TABLE IF NOT EXISTS `tien_do_nhiem_vu` (
  `tien_do_id` int NOT NULL AUTO_INCREMENT,
  `nhiem_vu_id` int NOT NULL,
  `nguoi_cap_nhat_id` int NOT NULL,
  `phan_tram_hoan_thanh` tinyint UNSIGNED NOT NULL DEFAULT '0',
  `noi_dung_cap_nhat` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `ngay_cap_nhat` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`tien_do_id`),
  KEY `idx_tien_do_nhiem_vu` (`nhiem_vu_id`),
  KEY `idx_tien_do_nguoi_cap_nhat` (`nguoi_cap_nhat_id`)
) ;

-- --------------------------------------------------------

--
-- Table structure for table `vai_tro`
--

DROP TABLE IF EXISTS `vai_tro`;
CREATE TABLE IF NOT EXISTS `vai_tro` (
  `vai_tro_id` int NOT NULL AUTO_INCREMENT,
  `ma_vai_tro` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ten_vai_tro` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `mo_ta` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trang_thai` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HOAT_DONG',
  PRIMARY KEY (`vai_tro_id`),
  UNIQUE KEY `uk_vai_tro_ma` (`ma_vai_tro`),
  UNIQUE KEY `uk_vai_tro_ten` (`ten_vai_tro`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `vai_tro`
--

INSERT INTO `vai_tro` (`vai_tro_id`, `ma_vai_tro`, `ten_vai_tro`, `mo_ta`, `trang_thai`) VALUES
(1, 'ADMIN', 'Quản trị hệ thống', 'Quản lý tài khoản và cấu hình hệ thống', 'HOAT_DONG'),
(2, 'THU_KY', 'Thư ký', 'Tổ chức cuộc họp, quản lý tài liệu và biên bản', 'HOAT_DONG'),
(3, 'LANH_DAO', 'Lãnh đạo', 'Phê duyệt cuộc họp, lịch họp và biên bản', 'HOAT_DONG'),
(4, 'CAN_BO', 'Cán bộ', 'Tham dự cuộc họp và cập nhật nhiệm vụ', 'HOAT_DONG');

-- --------------------------------------------------------

--
-- Table structure for table `yeu_cau_phe_duyet`
--

DROP TABLE IF EXISTS `yeu_cau_phe_duyet`;
CREATE TABLE IF NOT EXISTS `yeu_cau_phe_duyet` (
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
  `yeu_cau_truoc_id` int DEFAULT NULL,
  `ke_hoach_da_trinh` longtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`yeu_cau_phe_duyet_id`),
  KEY `idx_ycpd_trang_thai_thoi_gian` (`trang_thai`,`thoi_gian_trinh`),
  KEY `idx_ycpd_cuoc_hop` (`cuoc_hop_id`),
  KEY `fk_ycpd_lich_hop` (`lich_hop_id`),
  KEY `fk_ycpd_phien_ban` (`phien_ban_id`),
  KEY `fk_ycpd_nguoi_trinh` (`nguoi_trinh_id`),
  KEY `idx_ycpd_nguoi_duyet_tt` (`nguoi_duyet_id`,`trang_thai`),
  KEY `idx_ycpd_thu_ky_tt` (`thu_ky_tiep_nhan_id`,`trang_thai`),
  KEY `idx_ycpd_yeu_cau_truoc` (`yeu_cau_truoc_id`)
) ;

--
-- Dumping data for table `yeu_cau_phe_duyet`
--

INSERT INTO `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`, `loai_yeu_cau`, `cuoc_hop_id`, `lich_hop_id`, `phien_ban_id`, `nguoi_trinh_id`, `thoi_gian_trinh`, `trang_thai`, `ghi_chu_trinh`, `nguoi_duyet_id`, `thu_ky_tiep_nhan_id`, `cap_phe_duyet`) VALUES
(1, 'KE_HOACH_CUOC_HOP', 12, 8, NULL, 6, '2026-11-05 18:54:14', 'DA_DUYET', 'Trình lãnh đạo xem xét kế hoạch và lịch họp liên phòng ban', NULL, NULL, NULL),
(2, 'KE_HOACH_CUOC_HOP', 13, 9, NULL, 6, '2026-11-05 22:16:57', 'DA_DUYET', 'Kiểm thử tự động phân công lãnh đạo', NULL, NULL, NULL),
(3, 'KE_HOACH_CUOC_HOP', 14, 10, NULL, 6, '2026-11-05 22:41:30', 'TRA_LAI', 'Trình kế hoạch họp liên phòng để lãnh đạo xem xét', NULL, NULL, NULL),
(4, 'KE_HOACH_CUOC_HOP', 14, 10, NULL, 6, '2026-11-05 22:43:12', 'DA_DUYET', 'Đã bổ sung nội dung phân công theo ý kiến lãnh đạo', NULL, NULL, NULL),
(5, 'KE_HOACH_CUOC_HOP', 15, 11, NULL, 6, '2026-11-06 00:00:42', 'DA_DUYET', 'Trình duyệt kế hoạch và lịch họp liên phòng ban', NULL, NULL, NULL),
(6, 'KE_HOACH_CUOC_HOP', 16, NULL, NULL, 6, '2026-11-09 22:40:57', 'DA_DUYET', 'Kính trình lãnh đạo xem xét kế hoạch tổ chức cuộc họp', 4, NULL, 'LANH_DAO'),
(7, 'KE_HOACH_CUOC_HOP', 17, NULL, NULL, 6, '2026-11-09 22:50:56', 'DA_DUYET', 'Kính trình trưởng phòng duyệt kế hoạch họp nội bộ', 5, NULL, 'TRUONG_PHONG'),
(8, 'KE_HOACH_CUOC_HOP', 11, NULL, NULL, 6, '2026-11-09 23:39:02', 'DA_DUYET', 'Kính trình xem xét kế hoạch và tài liệu đính kèm', 8, NULL, 'TRUONG_PHONG'),
(9, 'BIEN_BAN', 11, NULL, 2, 6, '2026-11-10 15:43:54', 'CHO_DUYET', 'Kính trình chủ trì xem xét biên bản đã hoàn thiện.', 8, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `yeu_cau_phe_duyet_tai_lieu`
--

DROP TABLE IF EXISTS `yeu_cau_phe_duyet_tai_lieu`;
CREATE TABLE IF NOT EXISTS `yeu_cau_phe_duyet_tai_lieu` (
  `yeu_cau_phe_duyet_id` int NOT NULL,
  `tai_lieu_id` int NOT NULL,
  `ngay_dinh_kem` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`yeu_cau_phe_duyet_id`,`tai_lieu_id`),
  KEY `idx_ycpdtl_tai_lieu` (`tai_lieu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `yeu_cau_phe_duyet_tai_lieu`
--

INSERT INTO `yeu_cau_phe_duyet_tai_lieu` (`yeu_cau_phe_duyet_id`, `tai_lieu_id`, `ngay_dinh_kem`) VALUES
(8, 6, '2026-11-09 23:39:02');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `bien_ban_cuoc_hop`
--
ALTER TABLE `bien_ban_cuoc_hop`
  ADD CONSTRAINT `fk_bien_ban_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bien_ban_nguoi_duyet` FOREIGN KEY (`nguoi_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bien_ban_nguoi_soan` FOREIGN KEY (`nguoi_soan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `bieu_quyet`
--
ALTER TABLE `bieu_quyet`
  ADD CONSTRAINT `fk_bieu_quyet_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bieu_quyet_nguoi_tao` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `cuoc_hop`
--
ALTER TABLE `cuoc_hop`
  ADD CONSTRAINT `fk_cuoc_hop_chu_tri` FOREIGN KEY (`nguoi_chu_tri_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_nguoi_de_xuat` FOREIGN KEY (`nguoi_de_xuat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_nguoi_tao` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_phong_ban_chu_tri` FOREIGN KEY (`phong_ban_chu_tri_id`) REFERENCES `phong_ban` (`phong_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `ket_qua_xu_ly_ai`
--
ALTER TABLE `ket_qua_xu_ly_ai`
  ADD CONSTRAINT `fk_ai_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ai_nguoi_yeu_cau` FOREIGN KEY (`nguoi_yeu_cau_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ai_tai_lieu` FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `khach_moi_cuoc_hop`
--
ALTER TABLE `khach_moi_cuoc_hop`
  ADD CONSTRAINT `fk_kmch_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_kmch_khach_moi` FOREIGN KEY (`khach_moi_id`) REFERENCES `khach_moi` (`khach_moi_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `lich_hop`
--
ALTER TABLE `lich_hop`
  ADD CONSTRAINT `fk_lich_hop_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_nguoi_de_xuat` FOREIGN KEY (`nguoi_de_xuat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_nguoi_xac_nhan` FOREIGN KEY (`nguoi_xac_nhan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_phong_hop` FOREIGN KEY (`phong_hop_id`) REFERENCES `phong_hop` (`phong_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lich_hop_thay_the` FOREIGN KEY (`lich_thay_the_id`) REFERENCES `lich_hop` (`lich_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `lich_su_phe_duyet`
--
ALTER TABLE `lich_su_phe_duyet`
  ADD CONSTRAINT `fk_lspd_bien_ban` FOREIGN KEY (`bien_ban_id`) REFERENCES `bien_ban_cuoc_hop` (`bien_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_lich_hop` FOREIGN KEY (`lich_hop_id`) REFERENCES `lich_hop` (`lich_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_nguoi_phe_duyet` FOREIGN KEY (`nguoi_phe_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_lspd_yeu_cau_phe_duyet` FOREIGN KEY (`yeu_cau_phe_duyet_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`);

--
-- Constraints for table `lua_chon_bieu_quyet`
--
ALTER TABLE `lua_chon_bieu_quyet`
  ADD CONSTRAINT `fk_lua_chon_bieu_quyet` FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `nguoi_dung`
--
ALTER TABLE `nguoi_dung`
  ADD CONSTRAINT `fk_nguoi_dung_phong_ban` FOREIGN KEY (`phong_ban_id`) REFERENCES `phong_ban` (`phong_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `nguoi_dung_vai_tro`
--
ALTER TABLE `nguoi_dung_vai_tro`
  ADD CONSTRAINT `fk_ndvt_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ndvt_vai_tro` FOREIGN KEY (`vai_tro_id`) REFERENCES `vai_tro` (`vai_tro_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `nhat_ky_he_thong`
--
ALTER TABLE `nhat_ky_he_thong`
  ADD CONSTRAINT `fk_nhat_ky_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `nhiem_vu`
--
ALTER TABLE `nhiem_vu`
  ADD CONSTRAINT `fk_nhiem_vu_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nhiem_vu_nguoi_giao` FOREIGN KEY (`nguoi_giao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nhiem_vu_nguoi_thuc_hien` FOREIGN KEY (`nguoi_thuc_hien_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `phan_cong_thu_ky`
--
ALTER TABLE `phan_cong_thu_ky`
  ADD CONSTRAINT `fk_pctk_nguoi_duoc_ho_tro` FOREIGN KEY (`nguoi_duoc_ho_tro_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pctk_nguoi_phan_cong` FOREIGN KEY (`nguoi_phan_cong_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pctk_thu_ky` FOREIGN KEY (`thu_ky_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `phien_ban_bien_ban`
--
ALTER TABLE `phien_ban_bien_ban`
  ADD CONSTRAINT `fk_pbbb_bien_ban` FOREIGN KEY (`bien_ban_id`) REFERENCES `bien_ban_cuoc_hop` (`bien_ban_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pbbb_nguoi_cap_nhat` FOREIGN KEY (`nguoi_cap_nhat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `phieu_bieu_quyet`
--
ALTER TABLE `phieu_bieu_quyet`
  ADD CONSTRAINT `fk_phieu_bieu_quyet_bieu_quyet` FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_phieu_bieu_quyet_lua_chon` FOREIGN KEY (`lua_chon_id`,`bieu_quyet_id`) REFERENCES `lua_chon_bieu_quyet` (`lua_chon_id`, `bieu_quyet_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_phieu_bieu_quyet_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `phong_ban`
--
ALTER TABLE `phong_ban`
  ADD CONSTRAINT `fk_phong_ban_truong_phong` FOREIGN KEY (`truong_phong_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `tai_lieu_cuoc_hop`
--
ALTER TABLE `tai_lieu_cuoc_hop`
  ADD CONSTRAINT `fk_tai_lieu_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tai_lieu_nguoi_tai` FOREIGN KEY (`nguoi_tai_len_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `thanh_vien_cuoc_hop`
--
ALTER TABLE `thanh_vien_cuoc_hop`
  ADD CONSTRAINT `fk_tvc_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tvc_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `thiet_bi`
--
ALTER TABLE `thiet_bi`
  ADD CONSTRAINT `fk_thiet_bi_phong_hop` FOREIGN KEY (`phong_hop_id`) REFERENCES `phong_hop` (`phong_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `thong_bao`
--
ALTER TABLE `thong_bao`
  ADD CONSTRAINT `fk_thong_bao_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_thong_bao_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tien_do_nhiem_vu`
--
ALTER TABLE `tien_do_nhiem_vu`
  ADD CONSTRAINT `fk_tdnv_nguoi_cap_nhat` FOREIGN KEY (`nguoi_cap_nhat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tdnv_nhiem_vu` FOREIGN KEY (`nhiem_vu_id`) REFERENCES `nhiem_vu` (`nhiem_vu_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `yeu_cau_phe_duyet`
--
ALTER TABLE `yeu_cau_phe_duyet`
  ADD CONSTRAINT `fk_ycpd_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`),
  ADD CONSTRAINT `fk_ycpd_lich_hop` FOREIGN KEY (`lich_hop_id`) REFERENCES `lich_hop` (`lich_hop_id`),
  ADD CONSTRAINT `fk_ycpd_nguoi_duyet` FOREIGN KEY (`nguoi_duyet_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ycpd_nguoi_trinh` FOREIGN KEY (`nguoi_trinh_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`),
  ADD CONSTRAINT `fk_ycpd_phien_ban` FOREIGN KEY (`phien_ban_id`) REFERENCES `phien_ban_bien_ban` (`phien_ban_id`),
  ADD CONSTRAINT `fk_ycpd_thu_ky_tiep_nhan` FOREIGN KEY (`thu_ky_tiep_nhan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ycpd_yeu_cau_truoc` FOREIGN KEY (`yeu_cau_truoc_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `yeu_cau_phe_duyet_tai_lieu`
--
ALTER TABLE `yeu_cau_phe_duyet_tai_lieu`
  ADD CONSTRAINT `fk_ycpdtl_tai_lieu` FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_ycpdtl_yeu_cau` FOREIGN KEY (`yeu_cau_phe_duyet_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`) ON DELETE RESTRICT ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
