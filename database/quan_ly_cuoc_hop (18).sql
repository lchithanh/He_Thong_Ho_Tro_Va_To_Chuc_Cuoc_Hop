-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Nov 22, 2026 at 06:09 PM
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
  `ngay_phat_hanh` datetime DEFAULT NULL,
  PRIMARY KEY (`bien_ban_id`),
  UNIQUE KEY `uk_bien_ban_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bien_ban_nguoi_soan` (`nguoi_soan_id`),
  KEY `idx_bien_ban_nguoi_duyet` (`nguoi_duyet_id`),
  KEY `idx_bien_ban_trang_thai` (`trang_thai`)
) ENGINE=InnoDB AUTO_INCREMENT=416 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `bien_ban_cuoc_hop`
--

INSERT INTO `bien_ban_cuoc_hop` (`bien_ban_id`, `cuoc_hop_id`, `nguoi_soan_id`, `nguoi_duyet_id`, `tieu_de`, `noi_dung_goc`, `noi_dung_chinh_thuc`, `duong_dan_file_word`, `duong_dan_file_pdf`, `trang_thai`, `y_kien_phan_hoi`, `ngay_soan`, `ngay_trinh_duyet`, `ngay_duyet`, `ngay_tao`, `ngay_cap_nhat`, `ngay_phat_hanh`) VALUES
(401, 107, 6, 5, 'Biên bản tổng kết Văn phòng', NULL, 'Thống nhất lập bảng theo dõi hồ sơ.', NULL, NULL, 'DA_PHE_DUYET', NULL, '2026-11-11 17:45:26', '2026-11-11 18:45:26', '2026-11-11 19:45:26', '2026-11-12 15:45:26', NULL, NULL),
(402, 103, 6, 5, 'Biên bản cuộc họp: Họp nội bộ chờ trưởng phòng duyệt', 'aaaaaa', 'câcssaaaa', NULL, NULL, 'DA_PHE_DUYET', 'oke', '2026-11-12 17:07:14', '2026-11-12 17:10:16', '2026-11-12 17:14:42', '2026-11-12 17:07:14', '2026-11-12 17:14:42', NULL),
(403, 110, 10, 11, 'Biên bản cuộc họp: Họp triển khai kế hoạch laafn 2', 'a', 'a', NULL, NULL, 'DA_PHAT_HANH', NULL, '2026-11-14 00:06:05', '2026-11-14 00:08:56', '2026-11-14 00:09:02', '2026-11-14 00:06:04', '2026-11-18 15:35:51', NULL),
(404, 111, 10, 11, 'Biên bản cuộc họp: Họp nội bộ chuẩn bị kế hoạch tuần lần 3', 'đc này đc kia', 'này này này này kia', NULL, NULL, 'DA_PHE_DUYET', 'oke', '2026-11-14 00:40:10', '2026-11-14 00:41:15', '2026-11-14 00:41:22', '2026-11-14 00:40:10', '2026-11-14 00:41:21', NULL),
(405, 105, 6, 4, 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'hhhhhhh', 'hhhh', NULL, NULL, 'DA_PHAT_HANH', 'oke', '2026-11-14 15:27:02', '2026-11-14 15:27:18', '2026-11-18 00:00:04', '2026-11-14 15:27:01', '2026-11-18 15:30:45', NULL),
(406, 115, 10, 11, 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'hih hahaaa', 'biểu quyết chưa load đc nên ko có kết quả biểu quyết  , cần tạo thêm lối về từ calendar sang cuộc họp', NULL, NULL, 'DA_PHAT_HANH', 'ok tiếp theo cần bổ sung file  và ai phân tích cuộc họp', '2026-11-17 23:17:50', '2026-11-17 23:18:06', '2026-11-17 23:19:29', '2026-11-17 23:17:49', '2026-11-17 23:20:19', NULL),
(407, 116, 13, 4, 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'này nay này', 'aaaaaaaaa', NULL, NULL, 'DA_PHAT_HANH', 'aaaaaaa', '2026-11-18 00:05:57', '2026-11-18 00:55:59', '2026-11-18 00:56:09', '2026-11-18 00:05:56', '2026-11-18 00:56:10', NULL),
(408, 117, 6, 4, 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'aaaaaaaa', 'tạm thời chưa có ai nên chưa dùng để hỗ trợ tạo biên bản đc\n\nnếu bổ sung thêm file thì sao', NULL, NULL, 'DA_PHAT_HANH', 'oke có file đc đó', '2026-11-18 02:21:47', '2026-11-18 02:25:00', '2026-11-18 02:25:24', '2026-11-18 02:21:46', '2026-11-18 02:25:39', NULL),
(409, 114, 10, 11, 'Biên bản cuộc họp: aaa', 'aaaaaa', 'áaaaaa', NULL, NULL, 'DA_PHAT_HANH', 'aaaaaaaaaaaa', '2026-11-18 15:50:30', '2026-11-18 15:50:40', '2026-11-18 15:50:55', '2026-11-18 15:50:29', '2026-11-18 15:50:55', NULL),
(410, 113, 12, 11, 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'aaaaaaaaaaa', 'aaaaaaaaaaaaaaaaaaa', NULL, NULL, 'DA_PHAT_HANH', 'aaaaaaaaaaaaa', '2026-11-18 16:39:21', '2026-11-18 16:39:30', '2026-11-18 16:39:53', '2026-11-18 16:39:21', '2026-11-18 16:39:54', NULL),
(411, 118, 5, 4, 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'aaaaaaaaa', 'aaaaaaaaaaa', NULL, NULL, 'DA_PHAT_HANH', NULL, '2026-11-18 17:14:30', '2026-11-18 17:19:36', '2026-11-18 17:20:20', '2026-11-18 17:14:29', '2026-11-18 17:21:10', NULL),
(412, 119, 10, 11, 'Biên bản cuộc họp: test họp nội bộ', 'aaaaaa', 'aaaaaa', NULL, NULL, 'DA_PHE_DUYET', NULL, '2026-11-18 20:23:41', '2026-11-18 20:24:03', '2026-11-18 20:24:15', '2026-11-18 20:23:41', '2026-11-18 20:24:14', NULL),
(413, 120, 10, 11, 'Biên bản cuộc họp: test websocket', 'ha', 'haaihhi tnashdaaaaaaaaaaaaa', NULL, NULL, 'DA_PHAT_HANH', 'oke đã xử lý', '2026-11-18 21:10:17', '2026-11-18 21:11:00', '2026-11-18 21:11:17', '2026-11-18 21:10:16', '2026-11-18 21:13:25', NULL),
(414, 127, 15, 4, 'Biên bản cuộc họp: test khách mời ngoài', 'aaaaaaaa', 'aaaaaaaaaaaa', NULL, NULL, 'DA_PHAT_HANH', NULL, '2026-11-20 22:06:34', '2026-11-20 22:06:44', '2026-11-20 22:06:53', '2026-11-20 22:06:33', '2026-11-20 22:06:54', NULL),
(415, 130, 13, NULL, 'Biên bản cuộc họp: ggg', 'aaaaaaaaa', 'aaaaaaaa', NULL, NULL, 'NHAP', NULL, '2026-11-21 14:15:26', NULL, NULL, '2026-11-21 14:15:26', NULL, NULL);

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
  `so_thanh_vien_co_mat_khi_dong` bigint DEFAULT NULL,
  `nghi_quyet_id` int DEFAULT NULL,
  `ket_qua_nghi_quyet` varchar(25) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tai_lieu_nghi_quyet_id` int DEFAULT NULL,
  PRIMARY KEY (`bieu_quyet_id`),
  KEY `idx_bieu_quyet_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_bieu_quyet_trang_thai` (`trang_thai`),
  KEY `fk_bieu_quyet_nguoi_tao` (`nguoi_tao_id`),
  KEY `fk_bieu_quyet_nghi_quyet` (`cuoc_hop_id`,`nghi_quyet_id`),
  KEY `fk_bieu_quyet_tai_lieu_nghi_quyet` (`tai_lieu_nghi_quyet_id`)
) ;

--
-- Dumping data for table `bieu_quyet`
--

INSERT INTO `bieu_quyet` (`bieu_quyet_id`, `cuoc_hop_id`, `nguoi_tao_id`, `tieu_de`, `noi_dung`, `che_do_bieu_quyet`, `cho_phep_trang_thai`, `ty_le_tham_gia_toi_thieu`, `thoi_gian_bat_dau`, `thoi_gian_ket_thuc`, `trang_thai`, `ngay_tao`, `so_thanh_vien_co_mat_khi_dong`, `nghi_quyet_id`, `ket_qua_nghi_quyet`, `tai_lieu_nghi_quyet_id`) VALUES
(701, 107, 5, 'Thông qua bảng theo dõi hồ sơ', NULL, 'CONG_KHAI', 1, 50.00, '2026-11-11 16:05:26', '2026-11-11 16:15:26', 'DA_DONG', '2026-11-12 15:45:26', 1, NULL, NULL, NULL),
(702, 105, 4, 'Họp triển khai kế hoạch', '1z2aaaaaaaaaa', 'AN_DANH', 1, 70.00, '2026-11-12 17:40:00', '2026-11-12 17:46:00', 'NHAP', '2026-11-12 17:40:27', NULL, NULL, NULL, NULL),
(703, 105, 4, 'aaaa', 'sssss', 'CONG_KHAI', 1, 100.00, '2026-11-12 17:49:00', '2026-11-12 17:55:00', 'NHAP', '2026-11-12 17:49:37', NULL, NULL, NULL, NULL),
(704, 105, 4, 'Biên bản cuộc họp: Họp nội bộ chờ trưởng phòng duyệt', 'aaaaaaaaaaaa', 'AN_DANH', 1, 100.00, '2026-11-12 18:00:00', '2026-11-12 18:04:00', 'DA_DONG', '2026-11-12 18:00:11', 2, NULL, NULL, NULL),
(705, 105, 4, 'Họp triển khai kế hoạch', 'hihaha', 'CONG_KHAI', 1, 100.00, '2026-11-13 14:38:00', '2026-11-13 14:42:00', 'DA_DONG', '2026-11-13 14:38:17', 2, NULL, NULL, NULL),
(706, 105, 4, 'Họp triển khai kế hoạch', 'hihiaa', 'CONG_KHAI', 1, 100.00, '2026-11-13 14:46:00', '2026-11-13 14:48:00', 'DA_DONG', '2026-11-13 14:46:13', 2, NULL, NULL, NULL),
(707, 105, 4, 'aaaa', 'aaa', 'AN_DANH', 1, 100.00, '2026-11-13 15:13:00', '2026-11-13 15:14:00', 'DA_DONG', '2026-11-13 15:13:29', 2, NULL, NULL, NULL),
(708, 105, 4, 'aaaaaaaaaaaa', 'aaaaaaaaaaaaaaaaaaaa', 'AN_DANH', 1, 100.00, '2026-11-13 15:15:00', '2026-11-13 15:20:00', 'DA_DONG', '2026-11-13 15:15:55', 2, NULL, NULL, NULL),
(709, 105, 4, 'aaaaaaaaaaa', 'aaaaaaaaaaaaa', 'CONG_KHAI', 1, NULL, '2026-11-13 15:19:00', '2026-11-13 15:23:00', 'DA_DONG', '2026-11-13 15:19:41', 2, NULL, NULL, NULL),
(710, 105, 4, 'aaaaaaaabbbbbbbbb', 'accccccccc', 'CONG_KHAI', 1, 100.00, '2026-11-13 15:22:00', '2026-11-13 15:27:00', 'DA_DONG', '2026-11-13 15:23:06', 2, NULL, NULL, NULL),
(711, 105, 4, 'zzzzzzzzz', NULL, 'CONG_KHAI', 1, NULL, '2026-11-13 15:25:00', '2026-11-13 15:27:00', 'DA_DONG', '2026-11-13 15:25:50', 2, NULL, NULL, NULL),
(712, 105, 4, 'qqqqqqqqqqqq', 'aaaaaaaa', 'CONG_KHAI', 1, 100.00, '2026-11-13 15:43:00', '2026-11-13 15:45:00', 'DA_DONG', '2026-11-13 15:43:39', 2, NULL, NULL, NULL),
(713, 105, 4, 'zxzxxxzxzxz', 'ádsaasdsa', 'CONG_KHAI', 1, 100.00, '2026-11-13 15:52:00', '2026-11-13 15:54:00', 'DA_DONG', '2026-11-13 15:52:14', 2, NULL, NULL, NULL),
(714, 105, 4, 'Kết luận cuộc họp: Họp nội bộ chờ trưởng phòng duyệt', 'zxc', 'CONG_KHAI', 1, NULL, '2026-11-13 15:55:00', '2026-11-13 15:57:00', 'DA_DONG', '2026-11-13 15:55:41', 2, NULL, NULL, NULL),
(715, 105, 4, '741852', 'aaaaaaaaaaaaaaaaaaaaaaaaaaa', 'CONG_KHAI', 1, NULL, '2026-11-13 15:58:00', '2026-11-13 16:04:00', 'DA_DONG', '2026-11-13 15:58:48', 2, NULL, NULL, NULL),
(716, 105, 4, 'abc', 'aa', 'CONG_KHAI', 1, NULL, '2026-11-13 16:01:00', '2026-11-13 16:03:00', 'DA_DONG', '2026-11-13 16:01:33', 3, NULL, NULL, NULL),
(717, 105, 4, 'Họp triển khai kế hoạch', NULL, 'CONG_KHAI', 1, NULL, '2026-11-13 16:03:00', '2026-11-13 16:06:00', 'DA_DONG', '2026-11-13 16:03:49', 3, NULL, NULL, NULL),
(718, 105, 4, 'Họp triển khai kế hoạch', 'aaaa', 'CONG_KHAI', 1, NULL, '2026-11-13 16:06:00', '2026-11-13 16:08:00', 'DA_DONG', '2026-11-13 16:06:58', 3, NULL, NULL, NULL),
(719, 111, 11, 'vote đá banh', '3/4 gooo', 'CONG_KHAI', 1, 100.00, '2026-11-15 00:30:00', '2026-11-15 00:35:00', 'NHAP', '2026-11-14 00:30:55', NULL, NULL, NULL, NULL),
(720, 111, 11, 'hi', NULL, 'CONG_KHAI', 1, NULL, '2026-11-14 00:31:00', '2026-11-14 00:35:00', 'DA_DONG', '2026-11-14 00:31:28', 4, NULL, NULL, NULL),
(721, 111, 11, 'vote đi tiệc', '3/4', 'CONG_KHAI', 1, 50.00, '2026-11-14 00:36:00', '2026-11-14 00:39:00', 'DA_DONG', '2026-11-14 00:36:47', 4, NULL, NULL, NULL),
(722, 115, 11, 'biểu quyết t7 đá banh', 'aaaaaaaaaa', 'CONG_KHAI', 1, NULL, '2026-11-18 12:12:00', '2026-11-18 12:30:00', 'NHAP', '2026-11-17 23:13:01', NULL, NULL, NULL, NULL),
(723, 115, 11, 'aaa hihi', 'aaaaa', 'CONG_KHAI', 1, NULL, '2026-11-17 23:13:00', '2026-11-18 12:13:00', 'DA_DONG', '2026-11-17 23:13:39', 3, NULL, NULL, NULL),
(724, 116, 4, 'hihi hahaa', 'chọn1 trong 2', 'CONG_KHAI', 1, NULL, '2026-11-18 00:01:00', '2026-11-18 01:01:00', 'DA_DONG', '2026-11-18 00:01:54', 4, NULL, NULL, NULL),
(725, 117, 4, 'aaaaaaaa', 'aaaaaaa', 'CONG_KHAI', 1, NULL, '2026-11-18 02:17:00', '2026-11-18 03:17:00', 'DA_DONG', '2026-11-18 02:17:53', 5, NULL, NULL, NULL),
(726, 114, 11, 'tesst 12313', '123231', 'CONG_KHAI', 1, NULL, '2026-11-18 15:47:00', '2026-11-18 16:47:00', 'DA_DONG', '2026-11-18 15:47:41', 5, NULL, NULL, NULL),
(727, 118, 4, 'triẻn khai 2 /3', 'thoát và biểu quyết', 'CONG_KHAI', 1, NULL, '2026-11-18 17:03:00', '2026-11-18 17:06:00', 'DA_DONG', '2026-11-18 17:04:00', 4, NULL, NULL, NULL),
(728, 119, 11, 'test biểu quyết', NULL, 'CONG_KHAI', 1, NULL, '2026-11-18 20:12:00', '2026-11-18 21:12:00', 'DA_DONG', '2026-11-18 20:12:37', 1, NULL, NULL, NULL),
(729, 120, 11, 'socket oke', '4', 'CONG_KHAI', 1, NULL, '2026-11-18 21:06:00', '2026-11-18 21:12:00', 'DA_DONG', '2026-11-18 21:06:46', 4, NULL, NULL, NULL),
(730, 120, 11, 'hahi', 'aaaaaa', 'CONG_KHAI', 1, NULL, '2026-11-18 21:08:00', '2026-11-18 21:12:00', 'DA_DONG', '2026-11-18 21:08:35', 3, NULL, NULL, NULL),
(731, 127, 4, 'chọn thanh làm trưởng phòng', '123', 'CONG_KHAI', 1, NULL, '2026-11-20 22:04:00', '2026-11-20 22:10:00', 'DA_DONG', '2026-11-20 22:05:12', 5, NULL, NULL, NULL),
(732, 130, 11, 'aaa', 'aa', 'CONG_KHAI', 1, NULL, '2026-11-21 14:08:00', '2026-11-21 14:12:00', 'DA_DONG', '2026-11-21 14:08:32', 3, NULL, NULL, NULL),
(733, 133, 4, 'biểu quyết yes no trước', '5 yes 5 no', 'CONG_KHAI', 1, NULL, '2026-11-22 22:53:00', '2026-11-22 22:58:00', 'DA_DONG', '2026-11-22 22:53:26', 10, NULL, NULL, NULL),
(734, 133, 4, 'Triển khai thí điểm hệ thống quản lý cuộc họp', 'Thống nhất triển khai thí điểm hệ thống trong 30 ngày tại Phòng Hành chính – Tổng hợp. Phạm vi thí điểm gồm lập lịch, gửi lời mời, điểm danh, biểu quyết và lưu trữ biên bản. Cuối đợt thí điểm, chủ trì xem xét báo cáo để quyết định phạm vi triển khai tiếp theo.', 'CONG_KHAI', 1, NULL, '2026-11-22 22:58:00', '2026-11-23 23:12:00', 'DA_DONG', '2026-11-22 22:58:32', 10, 1, 'KHONG_THONG_QUA', NULL),
(735, 135, 4, 'Họp nội bộ chuẩn bị kế hoạch tuần', '', 'CONG_KHAI', 1, NULL, '2026-11-23 00:33:00', '2026-11-24 00:33:00', 'DA_DONG', '2026-11-23 00:33:16', 10, 2, 'CHUA_DAT_DA_SO', 1050),
(736, 135, 4, 'Họp nội bộ chuẩn bị kế hoạch tuần', '', 'AN_DANH', 1, NULL, '2026-11-23 00:36:00', '2026-11-24 00:36:00', 'DA_DONG', '2026-11-23 00:37:00', 10, 2, 'CHUA_DAT_DA_SO', 1052),
(737, 135, 4, 'Họp nội bộ chuẩn bị kế hoạch tuần', '', 'CONG_KHAI', 1, NULL, '2026-11-23 00:37:00', '2026-11-24 00:38:00', 'DA_DONG', '2026-11-23 00:38:06', 10, 2, 'CHUA_DAT_DA_SO', 1053),
(738, 135, 4, 'Tiếp tục test luồng 2 — Nghị quyết PDF 1.', '', 'CONG_KHAI', 1, NULL, '2026-11-23 00:41:00', '2026-11-24 00:41:00', 'DA_DONG', '2026-11-23 00:41:47', 3, 3, 'THONG_QUA', 1051),
(739, 135, 4, 'Họp nội bộ chuẩn bị kế hoạch tuần', '', 'CONG_KHAI', 1, NULL, '2026-11-23 00:43:00', '2026-11-24 00:43:00', 'DA_DONG', '2026-11-23 00:43:19', 3, 2, 'KHONG_THONG_QUA', 1054);

-- --------------------------------------------------------

--
-- Table structure for table `cap_nhat_loi_moi_khach`
--

DROP TABLE IF EXISTS `cap_nhat_loi_moi_khach`;
CREATE TABLE IF NOT EXISTS `cap_nhat_loi_moi_khach` (
  `cap_nhat_id` bigint NOT NULL AUTO_INCREMENT,
  `khach_moi_cuoc_hop_id` int NOT NULL,
  `ma_truy_cap_sha256` char(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `noi_dung` varchar(500) NOT NULL,
  `ngay_cap_nhat` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cap_nhat_id`),
  UNIQUE KEY `ma_truy_cap_sha256` (`ma_truy_cap_sha256`),
  KEY `khach_moi_cuoc_hop_id` (`khach_moi_cuoc_hop_id`,`cap_nhat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cap_nhat_loi_moi_khach`
--

INSERT INTO `cap_nhat_loi_moi_khach` (`cap_nhat_id`, `khach_moi_cuoc_hop_id`, `ma_truy_cap_sha256`, `noi_dung`, `ngay_cap_nhat`) VALUES
(1, 10, 'bc07686aca42ce59883c3be64024679a188e43fa100af85bea64a331d324d600', 'Phát hành thư mời', '2026-11-22 23:13:26'),
(2, 11, 'dda20f562afd887e42d0b69182da34ab48bc1e8bf35e238a5fcd044b66dadf6f', 'Phát hành thư mời', '2026-11-23 00:12:47'),
(3, 12, '0b2a694c79a79bb0aa2fcb5708bf1543e657a39547d21b068e6a384c3971a322', 'Phát hành thư mời', '2026-11-23 00:47:57'),
(4, 12, '87fd3b9598b471ae93d7368a11628b09f61bbd53668a7140ad66e6c06d7ce90e', 'Thay đổi lịch họp', '2026-11-23 00:49:37'),
(5, 12, '43d94724a397df4f46dbbdd643d83c7afc5f6faa5d9f9e21aafe9e3637e16d61', 'Bổ sung tài liệu cuộc họp', '2026-11-23 00:51:07'),
(6, 12, '4041ebfb8d91941f4bcd1d98ab026a64a4483e44d0d6e799ba76bf3195e1080a', 'Cập nhật thư mời', '2026-11-23 00:51:09');

-- --------------------------------------------------------

--
-- Table structure for table `cau_hinh_vong_bau`
--

DROP TABLE IF EXISTS `cau_hinh_vong_bau`;
CREATE TABLE IF NOT EXISTS `cau_hinh_vong_bau` (
  `cuoc_bau_id` int NOT NULL,
  `so_vong` int NOT NULL,
  `so_nguoi_duoc_chon` int NOT NULL,
  `so_lua_chon_moi_phieu` int NOT NULL,
  PRIMARY KEY (`cuoc_bau_id`,`so_vong`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cau_hinh_vong_bau`
--

INSERT INTO `cau_hinh_vong_bau` (`cuoc_bau_id`, `so_vong`, `so_nguoi_duoc_chon`, `so_lua_chon_moi_phieu`) VALUES
(1, 2, 3, 3),
(2, 2, 3, 3),
(3, 2, 3, 3),
(4, 1, 1, 3),
(5, 1, 1, 2),
(6, 1, 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `cuoc_bau`
--

DROP TABLE IF EXISTS `cuoc_bau`;
CREATE TABLE IF NOT EXISTS `cuoc_bau` (
  `cuoc_bau_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `tieu_de` varchar(255) NOT NULL,
  `nguoi_tao_id` int NOT NULL,
  PRIMARY KEY (`cuoc_bau_id`),
  KEY `cuoc_hop_id` (`cuoc_hop_id`),
  KEY `nguoi_tao_id` (`nguoi_tao_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cuoc_bau`
--

INSERT INTO `cuoc_bau` (`cuoc_bau_id`, `cuoc_hop_id`, `tieu_de`, `nguoi_tao_id`) VALUES
(1, 133, 'bâu chọn người làm trưởng phòng', 4),
(2, 133, 'chọn 3 trên 10 ng để có thể bầu làm trưởng phòng', 4),
(3, 133, 'tiếp tục chọn top 3 ng trên 6 ng', 4),
(4, 135, 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 4),
(5, 135, 'bầu trưởng phòng VHXH', 4),
(6, 135, 'chọn 3 ng xuất săc', 4);

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
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `cuoc_hop`
--

INSERT INTO `cuoc_hop` (`cuoc_hop_id`, `ma_cuoc_hop`, `tieu_de`, `muc_tieu`, `noi_dung_du_kien`, `loai_hinh`, `pham_vi_cuoc_hop`, `phong_ban_chu_tri_id`, `nguoi_tao_id`, `nguoi_de_xuat_id`, `nguoi_chu_tri_id`, `buoc_xu_ly_hien_tai`, `trang_thai`, `thoi_gian_bat_dau_thuc_te`, `thoi_gian_ket_thuc_thuc_te`, `ngay_tao`, `ngay_cap_nhat`, `co_khach_moi_ben_ngoai`, `thu_ky_cuoc_hop_id`) VALUES
(101, 'CH-MAU-101', 'Họp nội bộ rà soát hồ sơ', 'Xác định hồ sơ tồn đọng', 'Phân công chuẩn bị báo cáo', 'TRUC_TIEP', 'NOI_BO', 1, 5, 5, 5, 'LAP_KE_HOACH', 'DANG_CHUAN_BI', NULL, NULL, '2026-11-05 15:45:26', '2026-11-14 11:34:10', 0, NULL),
(102, 'CH-MAU-102', 'Họp nội bộ chuẩn bị kế hoạch tuần', 'Thống nhất kế hoạch tuần', 'Rà soát nhiệm vụ và chuẩn bị tài liệu', 'TRUC_TIEP', 'NOI_BO', 1, 5, 5, 5, 'LAP_KE_HOACH', 'DANG_CHUAN_BI', NULL, NULL, '2026-11-05 15:45:26', NULL, 0, NULL),
(103, 'CH-MAU-103', 'Họp nội bộ chờ trưởng phòng duyệt', 'Chốt kế hoạch của Văn phòng', 'Xem xét phân công và hạn xử lý', 'TRUC_TIEP', 'NOI_BO', 1, 5, 5, 5, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-12 16:48:50', '2026-11-12 16:55:53', '2026-11-05 15:45:26', '2026-11-12 16:55:52', 0, NULL),
(104, 'CH-MAU-104', 'Họp liên phòng ban chờ lãnh đạo duyệt', 'Thống nhất phương án phối hợp', 'Chọn đầu mối từng phòng', 'TRUC_TUYEN', 'LIEN_PHONG_BAN', 2, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-05 15:45:26', '2026-11-13 22:23:13', 0, NULL),
(105, 'CH-MAU-105', 'Họp toàn cơ quan chuẩn bị lập lịch', 'Triển khai cải tiến quy trình', 'Chọn thời gian và phòng họp', 'TRUC_TIEP', 'TOAN_CO_QUAN', 2, 4, 4, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-12 17:59:38', '2026-11-14 12:52:06', '2026-11-05 15:45:26', '2026-11-14 12:52:06', 0, NULL),
(106, 'CH-MAU-106', 'Họp với đơn vị phối hợp bên ngoài', 'Thống nhất kế hoạch phối hợp', 'Trao đổi phương án thực hiện', 'TRUC_TIEP', 'BEN_NGOAI', 2, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-05 15:45:26', NULL, 1, NULL),
(107, 'CH-MAU-107', 'Họp tổng kết xử lý hồ sơ Văn phòng', 'Đánh giá tiến độ xử lý hồ sơ', 'Biểu quyết phương án theo dõi và giao nhiệm vụ', 'TRUC_TIEP', 'NOI_BO', 1, 5, 5, 5, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-11 15:45:26', '2026-11-11 16:45:26', '2026-11-05 15:45:26', NULL, 0, NULL),
(108, 'CH-MAU-108', 'Họp rà soát quy trình tiếp nhận văn bản', 'Thống nhất các bước cải tiến', 'Tổng hợp ý kiến để soạn biên bản', 'TRUC_TIEP', 'NOI_BO', 2, 8, 8, 8, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-11 17:45:26', '2026-11-11 18:45:26', '2026-11-05 15:45:26', NULL, 0, NULL),
(109, 'CH-c64648c5-7be6-409a-b86c-47fe3603fb68', 'Họp triển khai kế hoạch laafn 1', 'hihaha', 'hihihahah', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-13 23:36:48', '2026-11-13 23:38:00', '2026-11-13 23:20:55', '2026-11-13 23:38:00', 0, NULL),
(110, 'CH-e6fd8333-c6cb-4cfb-8650-3c19b95a193d', 'Họp triển khai kế hoạch laafn 2', 'test', 'test 1', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-14 00:04:55', '2026-11-14 00:05:16', '2026-11-13 23:52:04', '2026-11-14 00:05:16', 0, NULL),
(111, 'CH-57357eef-1b80-48de-be0b-635fb2f7c69a', 'Họp nội bộ chuẩn bị kế hoạch tuần lần 3', 'aaaaaaaaa', 'aaaaaaaa', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-14 00:28:52', '2026-11-14 00:39:51', '2026-11-14 00:19:56', '2026-11-14 00:39:51', 0, NULL),
(112, 'CH-0ed374a8-01d1-4435-a68a-825f6aa9e6e6', 'Họp triển khai kế hoạch lần 4', 'aaaaa', 'aaaaaaaaaa', 'TRUC_TIEP', 'LIEN_PHONG_BAN', NULL, 11, 11, 11, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-14 11:31:00', '2026-11-14 11:35:44', 0, NULL),
(113, 'CH-fe563631-1058-4619-815c-3a20642908a0', 'Họp triển khai kế hoạch lần 5', 'aaaaaaa', 'ccccccccc', 'TRUC_TUYEN', 'LIEN_PHONG_BAN', NULL, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 16:35:11', '2026-11-18 16:39:07', '2026-11-14 12:55:17', '2026-11-18 16:39:07', 0, 12),
(114, 'CH-75f4e95a-ca2d-4ffb-9447-1c2f7294fad7', 'aaa', 'aaa', 'aaaa', 'TRUC_TIEP', 'TOAN_CO_QUAN', NULL, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 15:46:23', '2026-11-18 15:50:20', '2026-11-14 15:12:13', '2026-11-18 15:50:20', 1, NULL),
(115, 'CH-0ea13331-46c4-4ab8-95cc-846362021093', 'họp bàn giao cuốii tuần', 'đạt kpi', 'aaaaaaaaaaaaaaaaaaaaa', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-17 23:11:13', '2026-11-17 23:17:05', '2026-11-17 23:00:40', '2026-11-17 23:17:04', 0, NULL),
(116, 'CH-303bcf5f-2957-4094-8e7a-eec8de00ed96', 'họp bàn giao cuốii tuần lần 2', 'test liên phòng ban', 'kiểm tra biểu quyết kết luận', 'TRUC_TIEP', 'LIEN_PHONG_BAN', NULL, 11, 11, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 00:00:53', '2026-11-18 00:05:41', '2026-11-17 23:46:17', '2026-11-18 00:05:41', 0, 13),
(117, 'CH-f80a5241-c932-4e2b-9589-e40afa1d2c54', 'Họp triển khai  dự án lần 1', 'aaaaaaaaaaa', 'bbbbbbbbbb', 'TRUC_TIEP', 'BEN_NGOAI', NULL, 4, 4, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 02:16:51', '2026-11-18 02:21:13', '2026-11-18 01:24:53', '2026-11-18 02:21:13', 1, NULL),
(118, 'CH-97dcf51b-24ff-4ef1-8129-103d5ada08b2', 'test thử cấp 2 và tham gia cuộc họp', 'tự tham gia cuộc họp', 'aaaaaaaaaaaaaaaaaaaaaaaa', 'TRUC_TUYEN', 'TOAN_CO_QUAN', NULL, 11, 11, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 17:01:42', '2026-11-18 17:09:01', '2026-11-18 16:55:11', '2026-11-18 17:09:01', 0, 5),
(119, 'CH-0039efd4-09a2-4829-b0be-96e92adcc452', 'test họp nội bộ', 'test luồng', 'luồng oke là đc', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 20:11:23', '2026-11-18 20:13:50', '2026-11-18 20:09:51', '2026-11-18 20:13:50', 0, NULL),
(120, 'CH-33862eba-1d55-4550-b790-770daf25e4c5', 'test websocket', 'test websocket', 'test websocket', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-18 20:52:08', '2026-11-18 21:09:44', '2026-11-18 20:48:01', '2026-11-18 21:09:44', 0, NULL),
(121, 'CH-09cedc42-40d5-49c3-bf41-0c1fb11272f8', 'test phòng phòng', 'aaaaaaaaaaa', 'aaaaaaaaaaaaa', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'DA_HUY', 'DA_HUY', NULL, NULL, '2026-11-18 21:26:21', '2026-11-18 22:54:57', 0, NULL),
(122, 'CH-4e456c15-ac9f-42c9-b9f6-f59e0830f395', 'kiểm tra gmail', 'kiểm tra mail', 'abcdefghijk', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-18 23:00:25', '2026-11-18 23:02:58', 0, NULL),
(123, 'CH-beb71df8-aebb-44f9-bc40-da4aa0508f2e', 'aaaaaaaaaaaaaa test mail', '', '', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, NULL, 'NHAP', 'NHAP', NULL, NULL, '2026-11-18 23:36:30', NULL, 0, NULL),
(124, 'CH-12ebbdf9-4842-48bc-84ac-db3ba4ffd9f4', 'aaaaaaaaaaaaaa test mail', 'aaaaa', 'aaaaaaa', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'DA_HUY', 'DA_HUY', NULL, NULL, '2026-11-18 23:36:47', '2026-11-18 23:47:53', 0, NULL),
(125, 'CH-bf54aa66-7bc9-4dc9-a371-b0bb3b025398', 'aaaaaa tetss', 'a', 'a', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-18 23:48:11', '2026-11-19 00:07:57', 0, NULL),
(126, 'CH-1cc42fc1-33fa-4100-a2e5-6ac2e80909da', 'test hủy cuộc họp', 'a', 'a', 'TRUC_TIEP', 'NOI_BO', 3, 11, 11, 11, 'DA_HUY', 'DA_HUY', NULL, NULL, '2026-11-18 23:52:56', '2026-11-18 23:54:49', 0, NULL),
(127, 'CH-cc4f12a7-aba7-4df4-b2aa-45f4f5afaab8', 'test khách mời ngoài', 'test khách mời ngoài', 'aaaaaaaaaaaaaaaaaa', 'TRUC_TIEP', 'BEN_NGOAI', NULL, 11, 11, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-20 21:27:49', '2026-11-20 22:06:11', '2026-11-20 15:22:07', '2026-11-20 22:06:10', 1, 15),
(128, 'CH-944cdda3-6f29-4201-a83d-e027b2bc147e', 'test khách mail', '123456', '123456', 'TRUC_TIEP', 'BEN_NGOAI', NULL, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-20 22:53:09', '2026-11-20 23:46:47', 1, NULL),
(129, 'CH-bddf9135-3a8e-4067-870b-14a5de7ef9c7', 'ấccs', 'aaa', 'aaa', 'TRUC_TIEP', 'NOI_BO', 2, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-21 00:07:57', '2026-11-21 00:09:23', 1, NULL),
(130, 'CH-d9a74409-3500-4101-8b0a-87c1747303ba', 'ggg', 'aa', 'aaaa', 'TRUC_TIEP', 'TOAN_CO_QUAN', NULL, 11, 11, 11, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-21 14:06:22', '2026-11-21 14:14:52', '2026-11-21 13:47:49', '2026-11-21 14:14:52', 0, 13),
(131, 'CH-c74e8749-6d89-4d7d-866e-ee5195ccd8c8', 'aa', '', 'aa', 'TRUC_TIEP', 'NOI_BO', 2, 4, 4, NULL, 'NHAP', 'NHAP', NULL, NULL, '2026-11-21 13:59:42', NULL, 0, NULL),
(132, 'CH-45892160-d40c-434d-be74-acf73b08ac70', 'aaaa', 'aa', 'aaa', 'TRUC_TIEP', 'NOI_BO', 2, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-21 13:59:52', '2026-11-21 14:00:48', 1, NULL),
(133, 'CH-1cdc1ab4-43b7-423a-822d-fd462fd5f67f', 'Họp triển khai kế hoạch test bầu chọn', 'test bầu chọn', 'aaaa', 'TRUC_TIEP', 'NOI_BO', 2, 4, 4, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-22 22:51:22', '2026-11-22 23:11:34', '2026-11-22 22:44:10', '2026-11-22 23:11:33', 0, NULL),
(134, 'CH-32ad29cd-b6d5-490d-8f2d-17d5c20102bf', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'TRUC_TIEP', 'BEN_NGOAI', NULL, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-22 23:12:08', '2026-11-22 23:16:07', 1, NULL),
(135, 'CH-92ff45b8-9a0d-47b3-893a-f63d0aa8f539', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên\n1.', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên\n1.', 'TRUC_TIEP', 'TOAN_CO_QUAN', NULL, 4, 4, 4, 'SAU_CUOC_HOP', 'DA_KET_THUC', '2026-11-23 00:16:12', '2026-11-23 00:46:14', '2026-11-23 00:11:36', '2026-11-23 00:46:13', 1, NULL),
(136, 'CH-61f31983-73dd-4730-a1cb-8a55b91d9ba3', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'TRUC_TIEP', 'BEN_NGOAI', NULL, 4, 4, 4, 'THU_KY_TO_CHUC', 'DA_DUYET', NULL, NULL, '2026-11-23 00:46:35', '2026-11-23 00:49:37', 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `cu_tri_vong_bau`
--

DROP TABLE IF EXISTS `cu_tri_vong_bau`;
CREATE TABLE IF NOT EXISTS `cu_tri_vong_bau` (
  `vong_bau_id` int NOT NULL,
  `nguoi_dung_id` int NOT NULL,
  PRIMARY KEY (`vong_bau_id`,`nguoi_dung_id`),
  KEY `nguoi_dung_id` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `cu_tri_vong_bau`
--

INSERT INTO `cu_tri_vong_bau` (`vong_bau_id`, `nguoi_dung_id`) VALUES
(1, 17),
(2, 17),
(4, 17),
(6, 17),
(7, 17),
(8, 17),
(9, 17),
(10, 17),
(11, 17),
(1, 18),
(2, 18),
(6, 18),
(7, 18),
(8, 18),
(9, 18),
(10, 18),
(11, 18),
(1, 19),
(2, 19),
(4, 19),
(6, 19),
(7, 19),
(8, 19),
(9, 19),
(10, 19),
(11, 19),
(1, 20),
(2, 20),
(4, 20),
(6, 20),
(7, 20),
(8, 20),
(9, 20),
(10, 20),
(11, 20),
(1, 21),
(2, 21),
(4, 21),
(6, 21),
(7, 21),
(8, 21),
(9, 21),
(10, 21),
(11, 21),
(1, 22),
(2, 22),
(6, 22),
(7, 22),
(8, 22),
(9, 22),
(10, 22),
(11, 22),
(1, 23),
(2, 23),
(4, 23),
(6, 23),
(7, 23),
(8, 23),
(9, 23),
(10, 23),
(11, 23),
(1, 24),
(2, 24),
(6, 24),
(7, 24),
(8, 24),
(9, 24),
(10, 24),
(11, 24),
(1, 25),
(2, 25),
(6, 25),
(7, 25),
(8, 25),
(9, 25),
(10, 25),
(11, 25),
(1, 26),
(2, 26),
(4, 26),
(6, 26),
(7, 26),
(8, 26),
(9, 26),
(10, 26),
(11, 26);

-- --------------------------------------------------------

--
-- Table structure for table `ket_luan_cuoc_hop`
--

DROP TABLE IF EXISTS `ket_luan_cuoc_hop`;
CREATE TABLE IF NOT EXISTS `ket_luan_cuoc_hop` (
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
) ;

--
-- Dumping data for table `ket_luan_cuoc_hop`
--

INSERT INTO `ket_luan_cuoc_hop` (`ket_luan_id`, `cuoc_hop_id`, `nguoi_soan_id`, `nguoi_phat_hanh_id`, `tieu_de`, `noi_dung`, `trang_thai`, `ngay_tao`, `ngay_cap_nhat`, `ngay_phat_hanh`) VALUES
(2, 107, 6, 5, 'Kết luận họp tổng kết Văn phòng', 'Giao cán bộ lập bảng và cập nhật tiến độ hằng tuần.', 'DA_PHAT_HANH', '2026-11-12 15:45:26', '2026-11-12 15:45:26', '2026-11-11 20:45:26'),
(3, 103, 6, 5, 'Kết luận cuộc họp: Họp nội bộ chờ trưởng phòng duyệt', 'đá passs', 'DA_PHAT_HANH', '2026-11-12 17:18:15', '2026-11-12 17:29:50', '2026-11-12 17:29:50'),
(4, 110, 10, 11, 'Kết luận cuộc họp: Họp triển khai kế hoạch laafn 2', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaa', 'DA_PHAT_HANH', '2026-11-14 00:10:27', '2026-11-14 00:19:36', '2026-11-14 00:19:37'),
(5, 111, 10, 11, 'Kết luận cuộc họp: Họp nội bộ chuẩn bị kế hoạch tuần lần 3', 'abcdsdh', 'DA_PHAT_HANH', '2026-11-14 00:40:23', '2026-11-14 00:41:39', '2026-11-14 00:41:40'),
(6, 105, 6, NULL, 'Kết luận cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'hhhhhhhh', 'NHAP', '2026-11-14 15:27:28', '2026-11-14 15:27:28', NULL),
(7, 115, 10, 11, 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'hihih hahaaa', 'DA_PHAT_HANH', '2026-11-17 23:18:12', '2026-11-18 15:36:35', '2026-11-18 15:36:36'),
(8, 116, 13, 4, 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'ha', 'DA_PHAT_HANH', '2026-11-18 00:06:23', '2026-11-18 00:53:02', '2026-11-18 00:53:02'),
(9, 117, 6, 4, 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'aaaaaaaa', 'DA_PHAT_HANH', '2026-11-18 02:26:17', '2026-11-18 02:26:27', '2026-11-18 02:26:27'),
(10, 114, 10, 11, 'Kết luận cuộc họp: aaa', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaa cccccccccccccccccccccccccc', 'DA_PHAT_HANH', '2026-11-18 15:51:11', '2026-11-18 15:51:30', '2026-11-18 15:51:30'),
(11, 113, 12, 11, 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'aaaaaaaaaaaaaaaaaaaaaaaaaaa', 'DA_PHAT_HANH', '2026-11-18 16:40:06', '2026-11-18 16:40:42', '2026-11-18 16:40:42'),
(12, 118, 5, 4, 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'aaaaaaaaaaaaaaaaaaaaaa', 'DA_PHAT_HANH', '2026-11-18 17:20:02', '2026-11-18 17:21:12', '2026-11-18 17:21:13'),
(13, 119, 10, NULL, 'Kết luận cuộc họp: test họp nội bộ', 'aaaaaaaaaaaaa', 'DA_DUYET', '2026-11-18 20:24:23', '2026-11-18 20:51:50', NULL),
(14, 120, 10, 11, 'Kết luận cuộc họp: test websocket', 'websocket oke đã tự cập nhật sau 10s', 'DA_PHAT_HANH', '2026-11-18 21:11:41', '2026-11-18 21:12:57', '2026-11-18 21:12:57'),
(15, 127, 15, 4, 'Kết luận cuộc họp: test khách mời ngoài', 'a', 'DA_PHAT_HANH', '2026-11-20 22:07:07', '2026-11-20 22:07:14', '2026-11-20 22:07:14');

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `ket_qua_xu_ly_ai`
--

INSERT INTO `ket_qua_xu_ly_ai` (`ket_qua_ai_id`, `cuoc_hop_id`, `tai_lieu_id`, `nguoi_yeu_cau_id`, `loai_xu_ly`, `noi_dung_dau_vao`, `ket_qua_dau_ra`, `trang_thai`, `thong_bao_loi`, `thoi_gian_bat_dau`, `thoi_gian_ket_thuc`, `ngay_tao`) VALUES
(1, 107, 1001, 6, 'TOM_TAT', 'Tóm tắt nội dung tổng kết xử lý hồ sơ.', NULL, 'CHO_XU_LY', NULL, NULL, NULL, '2026-11-12 15:45:26');

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
) ENGINE=InnoDB AUTO_INCREMENT=906 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `khach_moi`
--

INSERT INTO `khach_moi` (`khach_moi_id`, `ho_ten`, `don_vi_cong_tac`, `chuc_vu`, `email`, `so_dien_thoai`, `ghi_chu`, `trang_thai`, `ngay_tao`, `ngay_cap_nhat`) VALUES
(901, 'Khách mời mẫu', 'Đơn vị phối hợp mẫu', NULL, NULL, NULL, NULL, 'HOAT_DONG', '2026-11-12 15:45:26', NULL),
(902, 'trần văn thanh', 'ủy ban nhân dân kế bên', 'chuyên viên đảm  nhận họp ngoại bộ', 'thanhlu912@gmail.com', NULL, NULL, 'HOAT_DONG', '2026-11-20 15:43:51', NULL),
(903, 'Lư Chí Thanh', 'STU', 'Sinh viên đc  phép tham gia', 'dh52201447@student.stu.edu.vn', NULL, NULL, 'HOAT_DONG', '2026-11-20 15:47:59', NULL),
(904, 'NGuyễn Hoàng Pha', 'STU', 'sinh viên', 'dh52200912@student.stu.edu.vn', NULL, NULL, 'HOAT_DONG', '2026-11-21 00:14:27', NULL),
(905, 'nguyễn hoàng pha', 'stu', 'stui', 'hoangkhoalx2004@gmail.com', NULL, NULL, 'HOAT_DONG', '2026-11-21 00:16:26', NULL);

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
  `ma_truy_cap_sha256` char(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duong_dan_hop` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `han_truy_cap` datetime DEFAULT NULL,
  PRIMARY KEY (`khach_moi_cuoc_hop_id`),
  UNIQUE KEY `uk_khach_moi_cuoc_hop` (`khach_moi_id`,`cuoc_hop_id`),
  UNIQUE KEY `uk_khach_ngoai_ma_truy_cap` (`ma_truy_cap_sha256`),
  KEY `idx_kmch_cuoc_hop` (`cuoc_hop_id`),
  KEY `idx_kmch_trang_thai_moi` (`trang_thai_moi`),
  KEY `idx_kmch_trang_thai_tham_du` (`trang_thai_tham_du`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `khach_moi_cuoc_hop`
--

INSERT INTO `khach_moi_cuoc_hop` (`khach_moi_cuoc_hop_id`, `khach_moi_id`, `cuoc_hop_id`, `trang_thai_moi`, `trang_thai_tham_du`, `thoi_gian_diem_danh`, `ghi_chu`, `ngay_moi`, `ma_truy_cap_sha256`, `duong_dan_hop`, `han_truy_cap`) VALUES
(1, 901, 106, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-12 15:45:26', NULL, NULL, NULL),
(2, 902, 127, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-20 15:43:51', NULL, NULL, NULL),
(3, 903, 127, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-20 15:47:59', NULL, NULL, NULL),
(4, 902, 128, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-20 22:57:36', NULL, NULL, NULL),
(5, 903, 128, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-21 00:05:48', NULL, NULL, NULL),
(6, 902, 129, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-21 00:10:13', NULL, NULL, NULL),
(7, 903, 129, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-21 00:12:33', NULL, NULL, NULL),
(8, 904, 129, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-21 00:14:27', NULL, NULL, NULL),
(9, 905, 129, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-21 00:16:26', NULL, NULL, NULL),
(10, 902, 134, 'DONG_Y', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-22 23:13:26', 'bc07686aca42ce59883c3be64024679a188e43fa100af85bea64a331d324d600', NULL, '2026-12-02 02:00:00'),
(11, 902, 135, 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-23 00:12:47', 'dda20f562afd887e42d0b69182da34ab48bc1e8bf35e238a5fcd044b66dadf6f', NULL, '2026-12-02 04:00:00'),
(12, 902, 136, 'DONG_Y', 'CHUA_DIEM_DANH', NULL, NULL, '2026-11-23 00:47:58', '4041ebfb8d91941f4bcd1d98ab026a64a4483e44d0d6e799ba76bf3195e1080a', NULL, '2026-12-03 05:00:00');

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
  `la_lich_doi` tinyint(1) NOT NULL DEFAULT '0',
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

INSERT INTO `lich_hop` (`lich_hop_id`, `cuoc_hop_id`, `phong_hop_id`, `nguoi_de_xuat_id`, `nguoi_xac_nhan_id`, `thoi_gian_bat_dau`, `thoi_gian_ket_thuc`, `diem_phu_hop`, `trang_thai`, `ly_do_thay_doi`, `thoi_gian_xac_nhan`, `lich_thay_the_id`, `ngay_tao`, `la_lich_doi`) VALUES
(301, 106, 2, 6, NULL, '2026-11-14 09:00:00', '2026-11-14 10:30:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-12 15:45:26', 0),
(302, 107, 1, 6, 5, '2026-11-11 15:45:26', '2026-11-11 16:45:26', NULL, 'DA_XAC_NHAN', NULL, '2026-11-07 15:45:26', NULL, '2026-11-12 15:45:26', 0),
(303, 108, 1, 6, 8, '2026-11-11 17:45:26', '2026-11-11 18:45:26', NULL, 'DA_XAC_NHAN', NULL, '2026-11-07 15:45:26', NULL, '2026-11-12 15:45:26', 0),
(304, 103, 1, 6, 5, '2026-11-13 10:00:00', '2026-11-13 11:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-12 16:08:22', NULL, '2026-11-12 16:03:04', 0),
(305, 105, 2, 6, 4, '2026-11-12 17:52:00', '2026-11-12 18:52:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-12 17:52:33', NULL, '2026-11-12 17:52:09', 0),
(306, 104, NULL, 6, NULL, '2026-11-13 22:23:00', '2026-11-14 22:23:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-13 22:23:58', 0),
(307, 109, 2, 10, 11, '2026-11-14 08:28:00', '2026-11-14 10:28:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-13 23:33:19', NULL, '2026-11-13 23:29:57', 0),
(308, 110, 1, 10, 11, '2026-11-14 10:00:00', '2026-11-14 11:00:00', NULL, 'TU_CHOI', 'ngày này anh bận ròi hẹn tuần sau đi em', '2026-11-13 23:56:16', NULL, '2026-11-13 23:54:48', 0),
(309, 110, 2, 10, 11, '2026-11-15 08:00:00', '2026-11-15 21:00:00', NULL, 'TU_CHOI', 'thời gian quá lâu', '2026-11-14 00:00:20', NULL, '2026-11-13 23:59:04', 0),
(310, 110, 1, 10, 11, '2026-11-15 08:01:00', '2026-11-15 09:01:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-14 00:02:00', NULL, '2026-11-14 00:01:25', 0),
(311, 111, 2, 10, 11, '2026-11-15 10:22:00', '2026-11-15 11:22:00', NULL, 'TU_CHOI', 'tới 12h đi', '2026-11-14 00:23:41', NULL, '2026-11-14 00:22:54', 0),
(312, 111, 2, 10, 11, '2026-11-15 10:24:00', '2026-11-15 11:24:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-14 00:24:40', NULL, '2026-11-14 00:24:23', 0),
(313, 113, NULL, 10, NULL, '2026-11-14 13:14:00', '2026-11-14 14:15:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-14 13:15:03', 0),
(314, 113, NULL, 10, NULL, '2026-11-14 13:17:00', '2026-11-14 14:16:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-14 13:16:06', 0),
(315, 113, NULL, 10, NULL, '2026-11-14 13:20:00', '2026-11-14 15:17:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-14 13:17:50', 0),
(316, 114, 1, 10, NULL, '2026-11-14 16:13:00', '2026-11-14 17:13:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-14 15:13:56', 0),
(317, 114, 2, 10, NULL, '2026-11-16 08:00:00', '2026-11-16 09:00:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-15 23:16:01', 0),
(318, 114, 1, 10, 4, '2026-11-17 09:00:00', '2026-11-17 10:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-16 00:54:08', NULL, '2026-11-16 00:52:43', 0),
(319, 114, 1, 10, 4, '2026-11-17 08:00:00', '2026-11-17 09:00:00', NULL, 'THAY_THE', NULL, '2026-11-16 01:18:01', NULL, '2026-11-16 01:17:39', 0),
(320, 115, 1, 10, 11, '2026-11-18 00:00:00', '2026-11-18 01:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-17 23:06:15', NULL, '2026-11-17 23:02:03', 0),
(321, 114, 2, 10, NULL, '2026-11-18 01:00:00', '2026-11-18 02:00:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-17 23:44:17', 0),
(322, 116, 1, 10, 4, '2026-11-18 03:00:00', '2026-11-18 04:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-17 23:54:51', NULL, '2026-11-17 23:48:13', 0),
(323, 117, 1, 6, 4, '2026-11-18 05:00:00', '2026-11-18 06:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 02:02:09', NULL, '2026-11-18 01:25:59', 0),
(324, 114, 2, 10, 4, '2026-11-18 16:00:00', '2026-11-18 17:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 15:43:21', 319, '2026-11-18 15:37:44', 0),
(325, 113, NULL, 12, 4, '2026-11-18 17:00:00', '2026-11-18 18:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 16:34:24', NULL, '2026-11-18 16:09:45', 0),
(326, 118, NULL, 10, 4, '2026-11-18 18:00:00', '2026-11-18 19:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 16:59:24', NULL, '2026-11-18 16:56:34', 0),
(327, 119, 1, 10, 11, '2026-11-18 21:00:00', '2026-11-18 22:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 20:11:02', NULL, '2026-11-18 20:10:21', 0),
(328, 120, 2, 10, 11, '2026-11-18 22:00:00', '2026-11-18 23:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 20:50:18', NULL, '2026-11-18 20:49:01', 0),
(329, 121, 2, 10, NULL, '2026-11-18 23:00:00', '2026-11-19 00:00:00', NULL, 'DA_HUY', 'h quá trễ', NULL, NULL, '2026-11-18 21:42:42', 0),
(330, 121, 1, 10, NULL, '2026-11-19 01:00:00', '2026-11-19 02:00:00', NULL, 'DA_HUY', 'oke hủy dc', NULL, NULL, '2026-11-18 22:54:27', 0),
(331, 122, 1, 10, 11, '2026-11-19 17:00:00', '2026-11-19 18:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-18 23:02:58', NULL, '2026-11-18 23:00:49', 0),
(332, 124, 1, 10, 11, '2026-11-19 02:00:00', '2026-11-19 03:00:00', NULL, 'DA_HUY', 'hủy đi làm lại', '2026-11-18 23:38:13', NULL, '2026-11-18 23:37:24', 0),
(333, 125, 1, 10, 11, '2026-11-19 10:00:00', '2026-11-19 11:00:00', NULL, 'DA_HUY', 'hủy', '2026-11-18 23:49:03', NULL, '2026-11-18 23:48:37', 0),
(334, 126, 1, 10, 11, '2026-11-19 08:00:00', '2026-11-19 09:00:00', NULL, 'DA_HUY', 'hủy cuộc họp', '2026-11-18 23:54:00', NULL, '2026-11-18 23:53:31', 0),
(335, 125, 1, 10, NULL, '2026-11-19 08:00:00', '2026-11-19 09:00:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-18 23:56:49', 0),
(336, 125, 2, 10, 11, '2026-11-19 16:00:00', '2026-11-19 17:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-19 00:07:57', NULL, '2026-11-18 23:58:17', 0),
(337, 127, 2, 10, 4, '2026-11-20 16:00:00', '2026-11-20 17:00:00', NULL, 'THAY_THE', NULL, '2026-11-20 15:42:47', NULL, '2026-11-20 15:24:01', 0),
(338, 127, 2, 15, NULL, '2026-11-20 18:00:00', '2026-11-20 19:00:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-20 15:49:30', 0),
(339, 127, 2, 15, NULL, '2026-11-20 21:00:00', '2026-11-20 22:00:00', NULL, 'DE_XUAT', NULL, NULL, NULL, '2026-11-20 20:33:59', 0),
(340, 127, 2, 10, 4, '2026-11-20 22:00:00', '2026-11-20 23:00:00', NULL, 'THAY_THE', 'do lịch đã trễ nên gấp rút trình lại lịch mới', '2026-11-20 21:24:57', 337, '2026-11-20 21:22:15', 0),
(341, 127, 2, 15, 4, '2026-11-20 23:00:00', '2026-11-21 00:00:00', NULL, 'DA_XAC_NHAN', 'lấy lịch mới thư thả thời gian', '2026-11-20 21:27:23', 340, '2026-11-20 21:25:18', 0),
(342, 128, 2, 6, 4, '2026-11-23 00:00:00', '2026-11-23 01:00:00', NULL, 'THAY_THE', NULL, '2026-11-20 22:55:22', NULL, '2026-11-20 22:55:00', 0),
(343, 128, 1, 6, 4, '2026-11-23 23:46:00', '2026-11-24 23:46:00', NULL, 'DA_XAC_NHAN', 'aaaaaac s', '2026-11-20 23:46:47', 342, '2026-11-20 23:46:15', 0),
(344, 129, 2, 6, 4, '2026-11-23 01:00:00', '2026-11-23 02:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-21 00:09:23', NULL, '2026-11-21 00:08:29', 0),
(345, 130, 2, 10, 4, '2026-11-23 02:00:00', '2026-11-23 03:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-21 13:54:28', NULL, '2026-11-21 13:52:18', 0),
(346, 132, 2, 6, 4, '2026-11-23 12:00:00', '2026-11-23 13:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-21 14:00:48', NULL, '2026-11-21 14:00:26', 0),
(347, 133, 1, 6, 4, '2026-11-25 00:00:00', '2026-11-25 01:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-22 22:45:49', NULL, '2026-11-22 22:45:24', 0),
(348, 134, 2, 6, 4, '2026-11-25 01:00:00', '2026-11-25 02:00:00', NULL, 'THAY_THE', NULL, '2026-11-22 23:12:57', NULL, '2026-11-22 23:12:47', 0),
(349, 134, 2, 6, 4, '2026-11-25 02:00:00', '2026-11-25 03:00:00', NULL, 'DA_XAC_NHAN', 'thay đổi lịch họp do ng chủ trì yêu câu', '2026-11-22 23:16:07', 348, '2026-11-22 23:15:22', 0),
(350, 135, 1, 6, 4, '2026-11-25 03:00:00', '2026-11-25 04:00:00', NULL, 'DA_XAC_NHAN', NULL, '2026-11-23 00:12:25', NULL, '2026-11-23 00:12:03', 0),
(351, 136, 1, 6, 4, '2026-11-25 04:00:00', '2026-11-25 05:00:00', NULL, 'THAY_THE', NULL, '2026-11-23 00:47:35', NULL, '2026-11-23 00:47:17', 0),
(352, 136, 2, 6, 4, '2026-11-26 04:00:00', '2026-11-26 05:00:00', NULL, 'DA_XAC_NHAN', 'thay thế lịch', '2026-11-23 00:49:38', 351, '2026-11-23 00:49:00', 0);

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
) ENGINE=InnoDB AUTO_INCREMENT=285 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `lich_su_phe_duyet`
--

INSERT INTO `lich_su_phe_duyet` (`lich_su_phe_duyet_id`, `cuoc_hop_id`, `lich_hop_id`, `bien_ban_id`, `nguoi_phe_duyet_id`, `loai_phe_duyet`, `hanh_dong`, `y_kien`, `thoi_gian_phe_duyet`, `yeu_cau_phe_duyet_id`) VALUES
(29, 103, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình xem xét kế hoạch.', '2026-11-06 15:45:26', 201),
(30, 104, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình xem xét kế hoạch.', '2026-11-06 15:45:26', 202),
(31, 105, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình xem xét kế hoạch.', '2026-11-06 15:45:26', 203),
(32, 106, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình xem xét kế hoạch.', '2026-11-06 15:45:26', 204),
(33, 107, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình xem xét kế hoạch.', '2026-11-06 15:45:26', 205),
(34, 108, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'Trình xem xét kế hoạch.', '2026-11-06 15:45:26', 206),
(35, 105, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý triển khai kế hoạch.', '2026-11-07 15:45:26', 203),
(36, 106, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý triển khai kế hoạch.', '2026-11-07 15:45:26', 204),
(37, 107, NULL, NULL, 5, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý triển khai kế hoạch.', '2026-11-07 15:45:26', 205),
(38, 108, NULL, NULL, 8, 'KE_HOACH', 'PHE_DUYET', 'Đồng ý triển khai kế hoạch.', '2026-11-07 15:45:26', 206),
(44, 103, NULL, NULL, 5, 'KE_HOACH', 'PHE_DUYET', 'duyệt', '2026-11-12 15:57:49', 201),
(45, 103, 304, NULL, 5, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-12 16:08:21', NULL),
(46, 103, NULL, 402, 6, 'BIEN_BAN', 'TRINH_DUYET', 'hihi', '2026-11-12 17:10:16', 207),
(47, 103, NULL, 402, 5, 'BIEN_BAN', 'PHE_DUYET', 'oke', '2026-11-12 17:14:42', 207),
(48, 105, 305, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-12 17:52:33', NULL),
(49, 104, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'lên cuọc họp', '2026-11-13 22:23:12', 202),
(50, 109, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', '11', '2026-11-13 23:26:20', 208),
(51, 109, NULL, NULL, 11, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'ko có kế hoạch file', '2026-11-13 23:27:23', 208),
(52, 109, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'cố gắng thêm', '2026-11-13 23:27:45', 209),
(53, 109, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-13 23:28:17', 209),
(54, 109, 307, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-13 23:33:19', NULL),
(55, 110, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'hahaat test', '2026-11-13 23:52:48', 210),
(56, 110, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', 'chưa thấy tài liệu kế hoạch', '2026-11-13 23:53:37', 210),
(57, 110, 308, NULL, 11, 'LICH_HOP', 'YEU_CAU_CHINH_SUA', 'ngày này anh bận ròi hẹn tuần sau đi em', '2026-11-13 23:56:15', NULL),
(58, 110, 309, NULL, 11, 'LICH_HOP', 'YEU_CAU_CHINH_SUA', 'thời gian quá lâu', '2026-11-14 00:00:20', NULL),
(59, 110, 310, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-14 00:02:00', NULL),
(60, 110, NULL, 403, 10, 'BIEN_BAN', 'TRINH_DUYET', 'test01', '2026-11-14 00:06:26', 211),
(61, 110, NULL, 403, 11, 'BIEN_BAN', 'YEU_CAU_CHINH_SUA', 'hong đc trả lại', '2026-11-14 00:06:43', 211),
(62, 110, NULL, 403, 10, 'BIEN_BAN', 'TRINH_DUYET', 'hihi', '2026-11-14 00:07:40', 212),
(63, 110, NULL, 403, 11, 'BIEN_BAN', 'YEU_CAU_CHINH_SUA', 'cacccc', '2026-11-14 00:07:55', 212),
(64, 110, NULL, 403, 10, 'BIEN_BAN', 'TRINH_DUYET', NULL, '2026-11-14 00:08:56', 213),
(65, 110, NULL, 403, 11, 'BIEN_BAN', 'PHE_DUYET', NULL, '2026-11-14 00:09:01', 213),
(66, 111, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'đã tải lên kế hoạch', '2026-11-14 00:20:34', 214),
(67, 111, NULL, NULL, 11, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'oke chưa đc', '2026-11-14 00:21:04', 214),
(68, 111, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'thêm 2 file lý do quá nhỏ chưa nổi bật', '2026-11-14 00:21:51', 215),
(69, 111, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-14 00:22:01', 215),
(70, 111, 311, NULL, 11, 'LICH_HOP', 'YEU_CAU_CHINH_SUA', 'tới 12h đi', '2026-11-14 00:23:40', NULL),
(71, 111, 312, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-14 00:24:39', NULL),
(72, 111, NULL, 404, 10, 'BIEN_BAN', 'TRINH_DUYET', 'abcdefg', '2026-11-14 00:40:16', 216),
(73, 111, NULL, 404, 11, 'BIEN_BAN', 'YEU_CAU_CHINH_SUA', 'ko có file', '2026-11-14 00:40:57', 216),
(74, 111, NULL, 404, 10, 'BIEN_BAN', 'TRINH_DUYET', 'ko có file', '2026-11-14 00:41:14', 217),
(75, 111, NULL, 404, 11, 'BIEN_BAN', 'PHE_DUYET', 'oke', '2026-11-14 00:41:21', 217),
(76, 112, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'hiha', '2026-11-14 11:32:04', 218),
(77, 112, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-14 11:34:42', 218),
(78, 112, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-14 11:34:46', 218),
(79, 112, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-14 11:35:44', 218),
(80, 113, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'aaaaaaaaaaaaa', '2026-11-14 13:16:20', 219),
(81, 113, NULL, NULL, 6, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'aaaaaaaaaaaaaaaaaaaaaaaaa', '2026-11-14 13:16:53', 219),
(82, 113, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'aaaaaaaaaaaaaaaaaa', '2026-11-14 13:18:06', 220),
(83, 113, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-14 13:18:16', 220),
(84, 113, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-14 13:18:18', 220),
(85, 113, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-14 13:18:32', 220),
(86, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'bbb', '2026-11-14 15:16:16', 221),
(87, 114, NULL, NULL, 6, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'ko hop ly', '2026-11-14 15:17:38', 221),
(88, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'tinhf lan 1', '2026-11-14 15:19:04', 222),
(89, 114, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-14 15:19:23', 222),
(90, 114, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-14 15:19:29', 222),
(91, 114, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-14 15:20:21', 222),
(92, 105, NULL, 405, 6, 'BIEN_BAN', 'TRINH_DUYET', NULL, '2026-11-14 15:27:18', 223),
(93, 114, 318, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-16 00:54:07', NULL),
(94, 114, 319, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp đã được xác nhận', '2026-11-16 01:18:01', NULL),
(95, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'do lịch cũ đã qua , hnay xin phép bổ sung cạap nhật ngày họp mới', '2026-11-16 01:26:48', 224),
(96, 114, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'ok chấp nhận lịch lần 3', '2026-11-16 01:28:23', 224),
(97, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', 'xin phép đc tổ chức cuộc họp toàn đon vị do bảo chủ trì', '2026-11-16 01:29:10', 225),
(98, 114, NULL, NULL, 6, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'ko đc toàn đơn vị phải do lãnh đạo chủ trì và nguyễn văn bảo làm thư kí', '2026-11-16 01:30:36', 225),
(99, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'xin phép cập nhật lại thư kí', '2026-11-16 01:31:57', 226),
(100, 114, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'okee', '2026-11-16 01:32:20', 226),
(101, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', NULL, '2026-11-16 02:02:52', 227),
(102, 114, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-16 02:03:26', 227),
(103, 114, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-16 02:03:28', 227),
(104, 114, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-16 02:03:36', 227),
(105, 114, NULL, NULL, 13, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-16 02:04:48', 228),
(106, 115, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'quá rườm rà', '2026-11-17 23:03:15', 229),
(107, 115, NULL, NULL, 11, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'file kế hoạch đâuu', '2026-11-17 23:05:00', 229),
(108, 115, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'đã gửi file kế hoach', '2026-11-17 23:05:54', 230),
(109, 115, 320, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-17 23:06:14', 230),
(110, 115, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', 'okee', '2026-11-17 23:06:14', 230),
(112, 115, NULL, 406, 10, 'BIEN_BAN', 'TRINH_DUYET', 'trình chủ trì duyệt', '2026-11-17 23:18:05', 231),
(113, 115, NULL, 406, 11, 'BIEN_BAN', 'PHE_DUYET', 'ok tiếp theo cần bổ sung file  và ai phân tích cuộc họp', '2026-11-17 23:19:28', 231),
(114, 114, NULL, NULL, 11, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'Chọn lịch đề xuất còn hiệu lực và phù hợp với hình thức họp', '2026-11-17 23:43:57', 228),
(115, 116, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'đã chọn lịch 3h- 4h', '2026-11-17 23:48:41', 232),
(116, 116, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'oke chốt', '2026-11-17 23:52:27', 232),
(117, 116, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', 'nhờ lãnh đạo phê duyệt', '2026-11-17 23:53:10', 233),
(118, 116, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-17 23:54:04', 233),
(119, 116, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'đã tiếp nhận cho mỏw cuộc họp mong lãnh đạo xem xét', '2026-11-17 23:54:28', 233),
(120, 116, 322, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-17 23:54:50', 233),
(121, 116, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'oke chấp nhận', '2026-11-17 23:54:50', 233),
(122, 105, NULL, 405, 4, 'BIEN_BAN', 'PHE_DUYET', 'oke', '2026-11-18 00:00:04', 223),
(125, 116, NULL, NULL, 13, 'KET_LUAN', 'TRINH_DUYET', 'a', '2026-11-18 00:48:24', 236),
(126, 116, NULL, NULL, 4, 'KET_LUAN', 'PHE_DUYET', NULL, '2026-11-18 00:53:00', 236),
(127, 116, NULL, 407, 13, 'BIEN_BAN', 'TRINH_DUYET', 'aaaaaaaaa', '2026-11-18 00:55:07', 237),
(128, 116, NULL, 407, 4, 'BIEN_BAN', 'YEU_CAU_CHINH_SUA', 'aaa', '2026-11-18 00:55:46', 237),
(129, 116, NULL, 407, 13, 'BIEN_BAN', 'TRINH_DUYET', 'aaaaaaa', '2026-11-18 00:55:59', 238),
(130, 116, NULL, 407, 4, 'BIEN_BAN', 'PHE_DUYET', 'aaaaaaa', '2026-11-18 00:56:08', 238),
(131, 117, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'AAAAAAAAAAAAA', '2026-11-18 02:00:53', 239),
(132, 117, 323, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 02:02:09', 239),
(133, 117, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'OKE CHẤO NHẬN', '2026-11-18 02:02:09', 239),
(134, 117, NULL, 408, 6, 'BIEN_BAN', 'TRINH_DUYET', 'y như nội dung', '2026-11-18 02:21:58', 240),
(135, 117, NULL, 408, 4, 'BIEN_BAN', 'YEU_CAU_CHINH_SUA', 'chưa đc làm lại đi  bucoocj phải có ai', '2026-11-18 02:22:42', 240),
(136, 117, NULL, 408, 6, 'BIEN_BAN', 'TRINH_DUYET', 'hih hahaa', '2026-11-18 02:25:00', 241),
(137, 117, NULL, 408, 4, 'BIEN_BAN', 'PHE_DUYET', 'oke có file đc đó', '2026-11-18 02:25:24', 241),
(138, 117, NULL, NULL, 6, 'KET_LUAN', 'TRINH_DUYET', NULL, '2026-11-18 02:26:19', 242),
(139, 117, NULL, NULL, 4, 'KET_LUAN', 'PHE_DUYET', NULL, '2026-11-18 02:26:26', 242),
(140, 115, NULL, NULL, 10, 'KET_LUAN', 'TRINH_DUYET', NULL, '2026-11-18 15:36:26', 243),
(141, 115, NULL, NULL, 11, 'KET_LUAN', 'PHE_DUYET', 'aaaaaaaaaaaaaaaaaaaa', '2026-11-18 15:36:34', 243),
(142, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-18 15:38:28', 244),
(143, 114, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'aaaaaaaaaaaaaaaaaaaaaaaaaccce', '2026-11-18 15:42:06', 244),
(144, 114, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaa áccccccccccccccccc', '2026-11-18 15:42:35', 245),
(145, 114, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-18 15:43:01', 245),
(146, 114, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-18 15:43:03', 245),
(147, 114, 324, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 15:43:21', 245),
(148, 114, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'âoce', '2026-11-18 15:43:21', 245),
(149, 114, NULL, 409, 10, 'BIEN_BAN', 'TRINH_DUYET', 'aaaaaaaaa', '2026-11-18 15:50:39', 246),
(150, 114, NULL, 409, 11, 'BIEN_BAN', 'PHE_DUYET', 'aaaaaaaaaaaa', '2026-11-18 15:50:54', 246),
(151, 114, NULL, NULL, 10, 'KET_LUAN', 'TRINH_DUYET', 'aaaaaaa', '2026-11-18 15:51:19', 247),
(152, 114, NULL, NULL, 11, 'KET_LUAN', 'PHE_DUYET', 'a acs', '2026-11-18 15:51:28', 247),
(153, 113, NULL, NULL, 12, 'KE_HOACH', 'TRINH_DUYET', 'aaaaaaaaa', '2026-11-18 16:12:23', 248),
(154, 113, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', NULL, '2026-11-18 16:15:09', 248),
(155, 113, NULL, NULL, 12, 'KE_HOACH', 'TRINH_CAP_TREN', '123465', '2026-11-18 16:33:08', 249),
(156, 113, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-18 16:33:59', 249),
(157, 113, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'aaaaaaaa', '2026-11-18 16:34:05', 249),
(158, 113, 325, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 16:34:24', 249),
(159, 113, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'aaaaaaaaaaaaaaaaaaa vvvvvvvvvvv', '2026-11-18 16:34:24', 249),
(160, 113, NULL, 410, 12, 'BIEN_BAN', 'TRINH_DUYET', 'aaaaaaaa', '2026-11-18 16:39:30', 250),
(161, 113, NULL, 410, 11, 'BIEN_BAN', 'PHE_DUYET', 'aaaaaaaaaaaaa', '2026-11-18 16:39:52', 250),
(162, 113, NULL, NULL, 12, 'KET_LUAN', 'TRINH_DUYET', 'aaaaaaaaaa', '2026-11-18 16:40:19', 251),
(163, 113, NULL, NULL, 11, 'KET_LUAN', 'YEU_CAU_CHINH_SUA', 'sssss', '2026-11-18 16:40:28', 251),
(164, 113, NULL, NULL, 12, 'KET_LUAN', 'TRINH_DUYET', 'aaaaaaaaa', '2026-11-18 16:40:35', 252),
(165, 113, NULL, NULL, 11, 'KET_LUAN', 'PHE_DUYET', 'aaaaaaaaaaaaac', '2026-11-18 16:40:41', 252),
(166, 118, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'chưa có phòng theo lịch', '2026-11-18 16:57:10', 253),
(167, 118, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'oce chưa có phòng nhe', '2026-11-18 16:57:42', 253),
(168, 118, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', 'cho ông làm chủ trì', '2026-11-18 16:58:11', 254),
(169, 118, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-18 16:58:41', 254),
(170, 118, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'cho phép mở', '2026-11-18 16:58:53', 254),
(171, 118, 326, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 16:59:23', 254),
(172, 118, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'oke kế hoạch ngày h tốt', '2026-11-18 16:59:23', 254),
(173, 118, NULL, 411, 5, 'BIEN_BAN', 'TRINH_DUYET', 'aaaaaaaaaaa', '2026-11-18 17:19:35', 255),
(174, 118, NULL, NULL, 5, 'KET_LUAN', 'TRINH_DUYET', NULL, '2026-11-18 17:20:13', 256),
(175, 118, NULL, 411, 4, 'BIEN_BAN', 'PHE_DUYET', NULL, '2026-11-18 17:20:20', 255),
(176, 118, NULL, NULL, 4, 'KET_LUAN', 'PHE_DUYET', NULL, '2026-11-18 17:20:23', 256),
(177, 119, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'hi haha', '2026-11-18 20:10:47', 257),
(178, 119, 327, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 20:11:02', 257),
(179, 119, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', 'oke file ổn xem đc', '2026-11-18 20:11:02', 257),
(180, 119, NULL, 412, 10, 'BIEN_BAN', 'TRINH_DUYET', 'aaaaaa', '2026-11-18 20:24:03', 258),
(181, 119, NULL, 412, 11, 'BIEN_BAN', 'PHE_DUYET', NULL, '2026-11-18 20:24:14', 258),
(182, 119, NULL, NULL, 10, 'KET_LUAN', 'TRINH_DUYET', 'aaaaaaaaaa', '2026-11-18 20:24:29', 259),
(183, 119, NULL, NULL, 11, 'KET_LUAN', 'YEU_CAU_CHINH_SUA', 'aaaaaaa', '2026-11-18 20:24:44', 259),
(184, 119, NULL, NULL, 10, 'KET_LUAN', 'TRINH_DUYET', 'aaaaaaaaaaaaaa', '2026-11-18 20:25:03', 260),
(185, 120, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'aaaaaaaaaaaa', '2026-11-18 20:49:59', 261),
(186, 120, 328, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 20:50:17', 261),
(187, 120, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', 'aaaaaaaaa oke', '2026-11-18 20:50:17', 261),
(188, 119, NULL, NULL, 11, 'KET_LUAN', 'PHE_DUYET', NULL, '2026-11-18 20:51:50', 260),
(189, 120, NULL, 413, 10, 'BIEN_BAN', 'TRINH_DUYET', 'oke ròi', '2026-11-18 21:11:00', 262),
(190, 120, NULL, 413, 11, 'BIEN_BAN', 'PHE_DUYET', 'oke đã xử lý', '2026-11-18 21:11:17', 262),
(191, 120, NULL, NULL, 10, 'KET_LUAN', 'TRINH_DUYET', NULL, '2026-11-18 21:12:07', 263),
(192, 120, NULL, NULL, 11, 'KET_LUAN', 'YEU_CAU_CHINH_SUA', 'oke chỉnh lại thử xem', '2026-11-18 21:12:23', 263),
(193, 120, NULL, NULL, 10, 'KET_LUAN', 'TRINH_DUYET', NULL, '2026-11-18 21:12:42', 264),
(194, 120, NULL, NULL, 11, 'KET_LUAN', 'PHE_DUYET', 'ok xử lý ổn thỏa', '2026-11-18 21:12:55', 264),
(195, 121, 329, NULL, 10, 'LICH_HOP', 'HUY', 'h quá trễ', '2026-11-18 22:54:09', NULL),
(196, 121, 330, NULL, 10, 'LICH_HOP', 'HUY', 'oke hủy dc', '2026-11-18 22:54:44', NULL),
(197, 121, NULL, NULL, 11, 'CUOC_HOP', 'HUY', 'cuộc họp test', '2026-11-18 22:54:57', NULL),
(198, 122, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'test gmail', '2026-11-18 23:01:53', 265),
(199, 122, NULL, NULL, 11, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'đã duyệt cho kiểm tra mail', '2026-11-18 23:02:13', 265),
(200, 122, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'oke đã test chỉnh sửa kế hoạch', '2026-11-18 23:02:39', 266),
(201, 122, 331, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 23:02:58', 266),
(202, 122, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', 'oke duyệt', '2026-11-18 23:02:58', 266),
(203, 124, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'test mail mời', '2026-11-18 23:37:55', 267),
(204, 124, 332, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 23:38:12', 267),
(205, 124, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-18 23:38:12', 267),
(206, 124, 332, NULL, 11, 'LICH_HOP', 'HUY', 'hủy đi làm lại', '2026-11-18 23:47:53', NULL),
(207, 124, NULL, NULL, 11, 'CUOC_HOP', 'HUY', 'hủy đi làm lại', '2026-11-18 23:47:53', NULL),
(208, 125, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'a', '2026-11-18 23:48:52', 268),
(209, 125, 333, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 23:49:03', 268),
(210, 125, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-18 23:49:03', 268),
(211, 125, 333, NULL, 11, 'LICH_HOP', 'HUY', 'hủy', '2026-11-18 23:50:19', NULL),
(212, 126, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'test lịch', '2026-11-18 23:53:51', 269),
(213, 126, 334, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-18 23:54:00', 269),
(214, 126, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-18 23:54:00', 269),
(215, 126, 334, NULL, 11, 'LICH_HOP', 'HUY', 'hủy cuộc họp', '2026-11-18 23:54:49', NULL),
(216, 126, NULL, NULL, 11, 'CUOC_HOP', 'HUY', 'hủy cuộc họp', '2026-11-18 23:54:49', NULL),
(217, 125, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-19 00:07:30', 270),
(218, 125, 336, NULL, 11, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-19 00:07:57', 270),
(219, 125, NULL, NULL, 11, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-19 00:07:57', 270),
(220, 127, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'đã lên kế hoạch , kềm kế hoạch', '2026-11-20 15:24:33', 271),
(221, 127, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'aa', '2026-11-20 15:41:39', 271),
(222, 127, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', NULL, '2026-11-20 15:41:58', 272),
(223, 127, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-20 15:42:18', 272),
(224, 127, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-20 15:42:21', 272),
(225, 127, 337, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-20 15:42:47', 272),
(226, 127, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-20 15:42:47', 272),
(227, 127, NULL, NULL, 15, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-20 15:50:13', 273),
(228, 127, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'oke nghĩa là cập nhật sẽ trình lại từ cấp 1 là trưởng phòng', '2026-11-20 15:51:34', 273),
(229, 127, NULL, NULL, 15, 'KE_HOACH', 'YEU_CAU_CHINH_SUA', 'Thư ký mở lại hồ sơ để đề xuất lịch mới', '2026-11-20 20:33:51', 273),
(230, 127, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', 'do lịch đã trễ nên gấp rút trình lại lịch mới', '2026-11-20 21:22:39', 274),
(231, 127, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'oke chấp nhận lịch', '2026-11-20 21:23:12', 274),
(232, 127, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', 'gửi cấp trên', '2026-11-20 21:23:59', 275),
(233, 127, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-20 21:24:17', 275),
(234, 127, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'oke gửi lãnh đạo', '2026-11-20 21:24:39', 275),
(235, 127, 340, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-20 21:24:56', 275),
(236, 127, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'oke chấp nhận', '2026-11-20 21:24:56', 275),
(237, 127, NULL, NULL, 15, 'KE_HOACH', 'TRINH_DUYET', 'lấy lịch mới thư thả thời gian', '2026-11-20 21:26:11', 276),
(238, 127, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', 'oke đồng ý cho lịch mới', '2026-11-20 21:26:25', 276),
(239, 127, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', 'oke đã tiếp nhânj', '2026-11-20 21:27:03', 277),
(240, 127, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-20 21:27:11', 277),
(241, 127, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-20 21:27:17', 277),
(242, 127, 341, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-20 21:27:22', 277),
(243, 127, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-20 21:27:22', 277),
(244, 127, NULL, 414, 15, 'BIEN_BAN', 'TRINH_DUYET', NULL, '2026-11-20 22:06:43', 278),
(245, 127, NULL, 414, 4, 'BIEN_BAN', 'PHE_DUYET', NULL, '2026-11-20 22:06:53', 278),
(246, 127, NULL, NULL, 15, 'KET_LUAN', 'TRINH_DUYET', NULL, '2026-11-20 22:07:09', 279),
(247, 127, NULL, NULL, 4, 'KET_LUAN', 'PHE_DUYET', NULL, '2026-11-20 22:07:13', 279),
(248, 128, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', '456', '2026-11-20 22:55:09', 280),
(249, 128, 342, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-20 22:55:22', 280),
(250, 128, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-20 22:55:22', 280),
(251, 128, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'aaaaaac s', '2026-11-20 23:46:39', 281),
(252, 128, 343, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-20 23:46:47', 281),
(253, 128, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-20 23:46:47', 281),
(254, 129, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-21 00:08:49', 282),
(255, 129, 344, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-21 00:09:23', 282),
(256, 129, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', 'dã duyệt', '2026-11-21 00:09:23', 282),
(257, 130, NULL, NULL, 10, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-21 13:52:53', 283),
(258, 130, NULL, NULL, 11, 'KE_HOACH', 'DONG_Y_TRINH_CAP_TREN', NULL, '2026-11-21 13:53:13', 283),
(259, 130, NULL, NULL, 10, 'KE_HOACH', 'TRINH_CAP_TREN', NULL, '2026-11-21 13:53:42', 284),
(260, 130, NULL, NULL, 6, 'KE_HOACH', 'TIEP_NHAN', 'Thư ký lãnh đạo tiếp nhận hồ sơ', '2026-11-21 13:54:09', 284),
(261, 130, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-21 13:54:12', 284),
(262, 130, 345, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-21 13:54:28', 284),
(263, 130, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-21 13:54:28', 284),
(264, 132, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'aaaaa', '2026-11-21 14:00:43', 285),
(265, 132, 346, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-21 14:00:48', 285),
(266, 132, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-21 14:00:48', 285),
(267, 133, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'abc', '2026-11-22 22:45:36', 286),
(268, 133, 347, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-22 22:45:49', 286),
(269, 133, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-22 22:45:49', 286),
(270, 134, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-22 23:12:52', 287),
(271, 134, 348, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-22 23:12:56', 287),
(272, 134, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-22 23:12:56', 287),
(273, 134, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'thay đổi lịch họp do ng chủ trì yêu câu', '2026-11-22 23:15:53', 288),
(274, 134, 349, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-22 23:16:07', 288),
(275, 134, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-22 23:16:07', 288),
(276, 135, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'a', '2026-11-23 00:12:15', 289),
(277, 135, 350, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-23 00:12:25', 289),
(278, 135, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-23 00:12:25', 289),
(279, 136, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', NULL, '2026-11-23 00:47:29', 290),
(280, 136, 351, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-23 00:47:35', 290),
(281, 136, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-23 00:47:35', 290),
(282, 136, NULL, NULL, 6, 'KE_HOACH', 'TRINH_DUYET', 'thay thế lịch', '2026-11-23 00:49:32', 291),
(283, 136, 352, NULL, 4, 'LICH_HOP', 'PHE_DUYET', 'Lịch họp được xác nhận cùng kế hoạch', '2026-11-23 00:49:37', 291),
(284, 136, NULL, NULL, 4, 'KE_HOACH', 'PHE_DUYET', NULL, '2026-11-23 00:49:37', 291);

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
) ENGINE=InnoDB AUTO_INCREMENT=918 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `lua_chon_bieu_quyet`
--

INSERT INTO `lua_chon_bieu_quyet` (`lua_chon_id`, `bieu_quyet_id`, `noi_dung_lua_chon`, `la_lua_chon_trang_thai`, `thu_tu_hien_thi`) VALUES
(801, 701, 'Đồng ý', 1, 1),
(802, 701, 'Không đồng ý', 1, 2),
(803, 701, 'Không ý kiến', 1, 3),
(804, 702, 'Đồng ý', 0, 1),
(805, 702, 'Không đồng ý', 0, 2),
(806, 702, 'Không ý kiến', 1, 3),
(807, 703, 'Đồng ý', 0, 1),
(808, 703, 'Không đồng ý', 0, 2),
(809, 703, 'Không ý kiến', 1, 3),
(810, 704, 'Đồng ý', 0, 1),
(811, 704, 'Không đồng ý', 0, 2),
(812, 704, 'Không ý kiến', 1, 3),
(813, 705, 'Đồng ý', 0, 1),
(814, 705, 'Không đồng ý', 0, 2),
(815, 705, 'Không ý kiến', 1, 3),
(816, 706, 'Đồng ý', 0, 1),
(817, 706, 'Không đồng ý', 0, 2),
(818, 706, 'Không ý kiến', 1, 3),
(819, 707, 'Đồng ý', 0, 1),
(820, 707, 'Không đồng ý', 0, 2),
(821, 707, 'Không ý kiến', 1, 3),
(822, 708, 'Đồng ý', 0, 1),
(823, 708, 'Không đồng ý', 0, 2),
(824, 708, 'Không ý kiến', 1, 3),
(825, 709, 'Đồng ý', 0, 1),
(826, 709, 'Không đồng ý', 0, 2),
(827, 709, 'Không ý kiến', 1, 3),
(828, 710, 'Đồng ý', 0, 1),
(829, 710, 'Không đồng ý', 0, 2),
(830, 710, 'Không ý kiến', 1, 3),
(831, 711, 'Đồng ý', 0, 1),
(832, 711, 'Không đồng ý', 0, 2),
(833, 711, 'Không ý kiến', 1, 3),
(834, 712, 'Đồng ý', 0, 1),
(835, 712, 'Không đồng ý', 0, 2),
(836, 712, 'Không ý kiến', 1, 3),
(837, 713, 'Đồng ý', 0, 1),
(838, 713, 'Không đồng ý', 0, 2),
(839, 713, 'Không ý kiến', 1, 3),
(840, 714, 'Đồng ý', 0, 1),
(841, 714, 'Không đồng ý', 0, 2),
(842, 714, 'Không ý kiến', 1, 3),
(843, 715, 'Đồng ý', 0, 1),
(844, 715, 'Không đồng ý', 0, 2),
(845, 715, 'Không ý kiến', 1, 3),
(846, 716, 'Đồng ý', 0, 1),
(847, 716, 'Không đồng ý', 0, 2),
(848, 716, 'Không ý kiến', 1, 3),
(849, 717, 'Đồng ý', 0, 1),
(850, 717, 'Không đồng ý', 0, 2),
(851, 717, 'Không ý kiến', 1, 3),
(852, 718, 'Đồng ý', 0, 1),
(853, 718, 'Không đồng ý', 0, 2),
(854, 718, 'Không ý kiến', 1, 3),
(855, 719, 'Đồng ý', 0, 1),
(856, 719, 'Không đồng ý', 0, 2),
(857, 719, 'Không ý kiến', 1, 3),
(858, 720, 'Đồng ý', 0, 1),
(859, 720, 'Không đồng ý', 0, 2),
(860, 720, 'Không ý kiến', 1, 3),
(861, 721, 'Đồng ý', 0, 1),
(862, 721, 'Không đồng ý', 0, 2),
(863, 721, 'Không ý kiến', 1, 3),
(864, 722, 'Đồng ý', 0, 1),
(865, 722, 'Không đồng ý', 0, 2),
(866, 722, 'Không ý kiến', 1, 3),
(867, 723, 'Đồng ý', 0, 1),
(868, 723, 'Không đồng ý', 0, 2),
(869, 723, 'Không ý kiến', 1, 3),
(870, 724, 'Đồng ý', 0, 1),
(871, 724, 'Không đồng ý', 0, 2),
(872, 724, 'Không ý kiến', 1, 3),
(873, 725, 'Đồng ý', 0, 1),
(874, 725, 'Không đồng ý', 0, 2),
(875, 725, 'Không ý kiến', 1, 3),
(876, 726, 'Đồng ý', 0, 1),
(877, 726, 'Không đồng ý', 0, 2),
(878, 726, 'Không ý kiến', 1, 3),
(879, 727, 'Đồng ý', 0, 1),
(880, 727, 'Không đồng ý', 0, 2),
(881, 727, 'Không ý kiến', 1, 3),
(882, 728, 'Đồng ý', 0, 1),
(883, 728, 'Không đồng ý', 0, 2),
(884, 728, 'Không ý kiến', 1, 3),
(885, 729, 'Đồng ý', 0, 1),
(886, 729, 'Không đồng ý', 0, 2),
(887, 729, 'Không ý kiến', 1, 3),
(888, 730, 'Đồng ý', 0, 1),
(889, 730, 'Không đồng ý', 0, 2),
(890, 730, 'Không ý kiến', 1, 3),
(891, 731, 'Đồng ý', 0, 1),
(892, 731, 'Không đồng ý', 0, 2),
(893, 731, 'Không ý kiến', 1, 3),
(894, 732, 'Đồng ý', 0, 1),
(895, 732, 'Không đồng ý', 0, 2),
(896, 732, 'Không ý kiến', 1, 3),
(897, 733, 'Đồng ý', 0, 1),
(898, 733, 'Không đồng ý', 0, 2),
(899, 733, 'Không ý kiến', 1, 3),
(900, 734, 'Đồng ý', 0, 1),
(901, 734, 'Không đồng ý', 0, 2),
(902, 734, 'Không ý kiến', 1, 3),
(903, 735, 'Đồng ý', 0, 1),
(904, 735, 'Không đồng ý', 0, 2),
(905, 735, 'Không ý kiến', 1, 3),
(906, 736, 'Đồng ý', 0, 1),
(907, 736, 'Không đồng ý', 0, 2),
(908, 736, 'Không ý kiến', 1, 3),
(909, 737, 'Đồng ý', 0, 1),
(910, 737, 'Không đồng ý', 0, 2),
(911, 737, 'Không ý kiến', 1, 3),
(912, 738, 'Đồng ý', 0, 1),
(913, 738, 'Không đồng ý', 0, 2),
(914, 738, 'Không ý kiến', 1, 3),
(915, 739, 'Đồng ý', 0, 1),
(916, 739, 'Không đồng ý', 0, 2),
(917, 739, 'Không ý kiến', 1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `lua_chon_phieu_bau`
--

DROP TABLE IF EXISTS `lua_chon_phieu_bau`;
CREATE TABLE IF NOT EXISTS `lua_chon_phieu_bau` (
  `vong_bau_id` int NOT NULL,
  `nguoi_bo_phieu_id` int NOT NULL,
  `ung_vien_id` int NOT NULL,
  PRIMARY KEY (`vong_bau_id`,`nguoi_bo_phieu_id`,`ung_vien_id`),
  KEY `vong_bau_id` (`vong_bau_id`,`ung_vien_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `lua_chon_phieu_bau`
--

INSERT INTO `lua_chon_phieu_bau` (`vong_bau_id`, `nguoi_bo_phieu_id`, `ung_vien_id`) VALUES
(2, 18, 17),
(2, 19, 17),
(2, 21, 17),
(2, 22, 17),
(2, 26, 17),
(2, 20, 18),
(2, 24, 18),
(2, 26, 18),
(2, 18, 19),
(2, 19, 19),
(2, 22, 19),
(2, 25, 19),
(2, 26, 19),
(2, 17, 20),
(2, 18, 20),
(2, 20, 20),
(2, 21, 20),
(2, 24, 20),
(2, 25, 20),
(2, 17, 21),
(2, 18, 21),
(2, 19, 21),
(2, 21, 21),
(2, 22, 21),
(2, 24, 21),
(2, 25, 21),
(2, 26, 21),
(2, 17, 22),
(2, 20, 22),
(2, 23, 22),
(2, 17, 23),
(2, 18, 23),
(2, 19, 23),
(2, 21, 23),
(2, 22, 23),
(2, 23, 23),
(2, 24, 23),
(2, 25, 23),
(2, 20, 24),
(2, 23, 24),
(2, 25, 24),
(2, 19, 25),
(2, 22, 25),
(2, 23, 25),
(2, 26, 25),
(2, 17, 26),
(2, 20, 26),
(2, 21, 26),
(2, 23, 26),
(2, 24, 26),
(4, 17, 17),
(4, 19, 17),
(4, 20, 17),
(4, 21, 17),
(4, 26, 17),
(4, 17, 19),
(4, 19, 19),
(4, 21, 19),
(4, 23, 19),
(4, 17, 20),
(4, 19, 20),
(4, 20, 20),
(4, 23, 20),
(4, 26, 20),
(4, 19, 21),
(4, 20, 21),
(4, 21, 21),
(4, 23, 21),
(4, 26, 21),
(4, 17, 23),
(4, 19, 23),
(4, 20, 23),
(4, 21, 23),
(4, 23, 23),
(4, 26, 23),
(4, 17, 26),
(4, 20, 26),
(4, 21, 26),
(4, 23, 26),
(4, 26, 26),
(6, 19, 17),
(6, 22, 17),
(6, 26, 17),
(6, 17, 18),
(6, 21, 18),
(6, 24, 18),
(6, 19, 19),
(6, 23, 19),
(6, 26, 19),
(6, 18, 20),
(6, 21, 20),
(6, 24, 20),
(6, 20, 21),
(6, 23, 21),
(6, 26, 21),
(6, 18, 22),
(6, 21, 22),
(6, 25, 22),
(6, 17, 23),
(6, 20, 23),
(6, 23, 23),
(6, 18, 24),
(6, 22, 24),
(6, 25, 24),
(6, 17, 25),
(6, 20, 25),
(6, 24, 25),
(6, 19, 26),
(6, 22, 26),
(6, 25, 26),
(7, 17, 17),
(7, 25, 19),
(7, 21, 21),
(7, 20, 22),
(7, 18, 23),
(7, 19, 23),
(7, 23, 23),
(7, 24, 24),
(7, 26, 24),
(7, 22, 25),
(8, 22, 17),
(8, 19, 18),
(8, 24, 18),
(8, 21, 19),
(8, 26, 19),
(8, 18, 20),
(8, 20, 20),
(8, 23, 20),
(8, 17, 21),
(8, 24, 21),
(8, 26, 21),
(8, 20, 22),
(8, 25, 22),
(8, 17, 23),
(8, 19, 23),
(8, 22, 23),
(8, 25, 23),
(8, 21, 25),
(8, 18, 26),
(8, 23, 26),
(9, 21, 4),
(9, 24, 4),
(9, 25, 6),
(9, 22, 18),
(9, 23, 18),
(9, 17, 19),
(9, 20, 19),
(9, 22, 19),
(9, 26, 19),
(9, 20, 20),
(9, 18, 21),
(9, 19, 21),
(9, 25, 21),
(9, 17, 22),
(9, 18, 22),
(9, 21, 22),
(9, 24, 22),
(9, 23, 23),
(9, 25, 23),
(9, 26, 23),
(9, 18, 24),
(9, 23, 24),
(9, 17, 25),
(9, 19, 25),
(9, 22, 25),
(9, 26, 25),
(9, 19, 26),
(9, 20, 26),
(9, 21, 26),
(9, 24, 26),
(10, 17, 19),
(10, 19, 19),
(10, 20, 19),
(10, 21, 19),
(10, 23, 19),
(10, 25, 19),
(10, 26, 19),
(10, 18, 22),
(10, 20, 22),
(10, 22, 22),
(10, 23, 22),
(10, 24, 22),
(10, 25, 22),
(10, 26, 22),
(10, 17, 25),
(10, 18, 25),
(10, 19, 25),
(10, 20, 25),
(10, 21, 25),
(10, 22, 25),
(10, 24, 25),
(10, 25, 25),
(10, 26, 25),
(10, 17, 26),
(10, 18, 26),
(10, 19, 26),
(10, 21, 26),
(10, 22, 26),
(10, 23, 26),
(10, 24, 26),
(11, 17, 19),
(11, 18, 19),
(11, 19, 19),
(11, 20, 19),
(11, 21, 19),
(11, 22, 19),
(11, 24, 19),
(11, 25, 19),
(11, 26, 19),
(11, 18, 22),
(11, 21, 22),
(11, 23, 22),
(11, 17, 26),
(11, 19, 26),
(11, 20, 26),
(11, 22, 26),
(11, 23, 26),
(11, 24, 26),
(11, 25, 26),
(11, 26, 26);

-- --------------------------------------------------------

--
-- Table structure for table `nghi_quyet`
--

DROP TABLE IF EXISTS `nghi_quyet`;
CREATE TABLE IF NOT EXISTS `nghi_quyet` (
  `nghi_quyet_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_hop_id` int NOT NULL,
  `nghi_quyet_cha_id` int DEFAULT NULL,
  `cap` int NOT NULL,
  `tieu_de` varchar(255) NOT NULL,
  `noi_dung` text NOT NULL,
  `trang_thai` varchar(25) NOT NULL DEFAULT 'NHAP',
  `ly_do` varchar(500) DEFAULT NULL,
  `tai_lieu_id` int DEFAULT NULL,
  `ngay_cong_bo` datetime DEFAULT NULL,
  PRIMARY KEY (`nghi_quyet_id`),
  UNIQUE KEY `cuoc_hop_id` (`cuoc_hop_id`,`nghi_quyet_id`),
  KEY `cuoc_hop_id_2` (`cuoc_hop_id`,`nghi_quyet_cha_id`),
  KEY `fk_nghi_quyet_tai_lieu` (`tai_lieu_id`)
) ;

--
-- Dumping data for table `nghi_quyet`
--

INSERT INTO `nghi_quyet` (`nghi_quyet_id`, `cuoc_hop_id`, `nghi_quyet_cha_id`, `cap`, `tieu_de`, `noi_dung`, `trang_thai`, `ly_do`, `tai_lieu_id`, `ngay_cong_bo`) VALUES
(1, 133, NULL, 1, 'Triển khai thí điểm hệ thống quản lý cuộc họp', 'Thống nhất triển khai thí điểm hệ thống trong 30 ngày tại Phòng Hành chính – Tổng hợp. Phạm vi thí điểm gồm lập lịch, gửi lời mời, điểm danh, biểu quyết và lưu trữ biên bản. Cuối đợt thí điểm, chủ trì xem xét báo cáo để quyết định phạm vi triển khai tiếp theo.', 'KHONG_THONG_QUA', NULL, NULL, '2026-11-22 23:43:22'),
(2, 135, NULL, 1, 'Họp nội bộ chuẩn bị kế hoạch tuần', '', 'KHONG_THONG_QUA', NULL, 1054, '2026-11-23 00:42:53'),
(3, 135, NULL, 1, 'Tiếp tục test luồng 2 — Nghị quyết PDF 1.', '', 'THONG_QUA', NULL, 1051, '2026-11-23 00:41:29');

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
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `nguoi_dung`
--

INSERT INTO `nguoi_dung` (`nguoi_dung_id`, `phong_ban_id`, `ma_nhan_vien`, `ho_ten`, `email`, `so_dien_thoai`, `ten_dang_nhap`, `mat_khau_ma_hoa`, `chuc_vu`, `trang_thai`, `ngay_tao`, `ngay_cap_nhat`) VALUES
(1, 2, 'NV0001', 'Quản trị viên', 'admin@example.com', '0900000000', 'admin', '$2a$10$iSMXKeJ9E5cNI8TOGqLSiuGZfePMCIbIALdlWx0ylGB4UdwVuOSy.', 'Quản trị hệ thống', 'HOAT_DONG', '2026-10-31 02:30:49', NULL),
(4, 2, 'NV_TEST_01', 'Nguyen Van Test Updated', 'test01@example.com', '0901234567', 'test01', '$2a$10$vlZmUm5tBlqzCOCYNmKE0.uV/0rrdiesP.RvmRLDRYzACMDCv780G', 'Chuyên viên', 'HOAT_DONG', '2026-10-31 22:32:23', '2026-10-31 22:56:50'),
(5, 1, 'NV005', 'Nguyen Van Can Bo', 'canbo@example.com', '0900000005', 'canbo', '$2a$10$DxJJd7aBWgqH8.UBAe/yYeVSlB27q6vmpXg5a8aYyL26C7A4pa54W', 'Trưởng phòng', 'HOAT_DONG', '2026-10-31 23:32:25', '2026-11-09 22:53:42'),
(6, 2, 'NV_TK_001', 'Nguyễn Thị Lan', 'thuky01@example.com', NULL, 'thuky01', '$2a$10$SkGdWC8C7JHZNJXPsVgsQOL4J6DSjpeF67.BeKNBKWiXnLq/LXTw6', 'Chuyên viên', 'HOAT_DONG', '2026-11-04 00:31:24', '2026-11-04 00:37:04'),
(8, 2, 'NV_TP_002', 'Trần Văn Minh', 'truongphong02@example.com', NULL, 'truongphong02', '$2a$10$QDQniFvghqVRKdmJAHFmreny9tjpMwdCl7OIajms4gJsun5aMSmQa', 'Trưởng phòng', 'HOAT_DONG', '2026-11-04 00:43:27', '2026-11-04 00:44:39'),
(9, 3, 'MAU-TK-01', 'Lê Thu Hà', 'thuky_mau01@example.invalid', NULL, 'thuky_mau01', '$2a$10$V5waMo3jm0Lm3b4QsjxJ7.bNa9LuFpqV6/OMHMOTwWdlcpai3cxlq', 'Thư ký', 'HOAT_DONG', '2026-11-12 15:48:01', NULL),
(10, 2, 'MAU-TK-02', 'Phạm Mai Anh', 'thuky_mau02@example.invalid', NULL, 'thuky_mau02', '$2a$10$V5waMo3jm0Lm3b4QsjxJ7.bNa9LuFpqV6/OMHMOTwWdlcpai3cxlq', 'Thư ký', 'HOAT_DONG', '2026-11-12 15:48:01', NULL),
(11, 3, 'MAU-CB-01', 'Nguyễn Quốc Bảo', 'canbo_mau01@example.invalid', NULL, 'canbo_mau01', '$2a$10$V5waMo3jm0Lm3b4QsjxJ7.bNa9LuFpqV6/OMHMOTwWdlcpai3cxlq', 'Cán bộ', 'HOAT_DONG', '2026-11-12 15:48:01', NULL),
(12, 2, 'MAU-CB-02', 'Trần Ngọc Hân', 'canbo_mau02@example.invalid', NULL, 'canbo_mau02', '$2a$10$V5waMo3jm0Lm3b4QsjxJ7.bNa9LuFpqV6/OMHMOTwWdlcpai3cxlq', 'Cán bộ', 'HOAT_DONG', '2026-11-12 15:48:01', NULL),
(13, 3, 'MAU-TP-01', 'Võ Minh Đức', 'truongphong_mau01@example.invalid', NULL, 'truongphong_mau01', '$2a$10$V5waMo3jm0Lm3b4QsjxJ7.bNa9LuFpqV6/OMHMOTwWdlcpai3cxlq', 'Trưởng phòng Nội vụ', 'HOAT_DONG', '2026-11-12 15:48:01', NULL),
(14, 2, 'MAU-LD-01', 'Đặng Hoàng Nam', 'lanhdao_mau01@example.invalid', NULL, 'lanhdao_mau01', '$2a$10$V5waMo3jm0Lm3b4QsjxJ7.bNa9LuFpqV6/OMHMOTwWdlcpai3cxlq', 'Lãnh đạo', 'HOAT_DONG', '2026-11-12 15:48:01', NULL),
(15, 1, 'aa1', 'aaaa', 'aa@gmail.com', '0357410746', 'aaaa', '$2a$10$YBeeNOTxTaJ0WNXfrcocs.bO4ea7EZYmZR.PBwtkN95W0IqM5AVbu', 'chuyên viên', 'HOAT_DONG', '2026-11-18 21:51:43', NULL),
(16, 1, 'a02', 'luchithanh', 'luchithanh2004@gmail.com', '03574110746', 'luchithanh2004@gmail.com', '$2a$10$ithULQaeBPfnC0QsGDCFz.7FIwVrug.GVkM2.i6ioCrResdOZMcIq', 'chuyên viên', 'HOAT_DONG', '2026-11-18 22:58:27', NULL),
(17, NULL, NULL, 'Cán bộ kiểm thử 01', 'bau_test_01@example.invalid', NULL, 'bau_test_01', '$2a$10$3FpBGmxgn1RjjSbyzQ/95uUKAHxjOcY95sqrvbG19kTxLm93hlcl2', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(18, NULL, NULL, 'Cán bộ kiểm thử 02', 'bau_test_02@example.invalid', NULL, 'bau_test_02', '$2a$10$bxLIqCl4uPv.6qw5ztf3aO6mZWozhVB5wn48UBM7VC/dXEtJlOH0C', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(19, NULL, NULL, 'Cán bộ kiểm thử 03', 'bau_test_03@example.invalid', NULL, 'bau_test_03', '$2a$10$nOfO3wrnjBHoamEcbqftm.7/ApeNYMFD1FCcFM7inIa336214sEnW', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(20, NULL, NULL, 'Cán bộ kiểm thử 04', 'bau_test_04@example.invalid', NULL, 'bau_test_04', '$2a$10$z3yzWlJowJN5yOhE0uRx6u0dtYw1CmON9BGtIYJka7Mf0gHzkB1Xa', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(21, NULL, NULL, 'Cán bộ kiểm thử 05', 'bau_test_05@example.invalid', NULL, 'bau_test_05', '$2a$10$4NYf2dngJyXUxnnNxRQun.tnyByo3.t.RnnTIssQVuWICNsZ7.NVm', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(22, NULL, NULL, 'Cán bộ kiểm thử 06', 'bau_test_06@example.invalid', NULL, 'bau_test_06', '$2a$10$KoIDJz3BCvvz3laQCD2HYOBgeiAVlz/Nf7u1SdWtjqoDyTv44fYru', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(23, NULL, NULL, 'Cán bộ kiểm thử 07', 'bau_test_07@example.invalid', NULL, 'bau_test_07', '$2a$10$cJEw7T0f/IuiEYAFoFHj8.npwviBXtEy2uDm0ZeWYrXTTegScxp4S', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(24, NULL, NULL, 'Cán bộ kiểm thử 08', 'bau_test_08@example.invalid', NULL, 'bau_test_08', '$2a$10$gazKG5eXbuq.QjN1E4eYBuGpqUDj0gTnyw6jS8EnUguxcycwqsSoe', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(25, NULL, NULL, 'Cán bộ kiểm thử 09', 'bau_test_09@example.invalid', NULL, 'bau_test_09', '$2a$10$xYDkAQPpPFl7plvYKP82gOm/8vSmHoCfi11amCKAMXTpqdShT54qu', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(26, NULL, NULL, 'Cán bộ kiểm thử 10', 'bau_test_10@example.invalid', NULL, 'bau_test_10', '$2a$10$PoNmv1zQaj.P.3Me5a4zKeltdZ1TZGqfJvDp3uYWw1HDbkYnWOs22', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(27, NULL, NULL, 'Cán bộ kiểm thử 11', 'bau_test_11@example.invalid', NULL, 'bau_test_11', '$2a$10$uol2wDI4WEwnQLl8pCgkHOadSav61WZ2xMX2DRMDMO4yhLXBP75w2', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(28, NULL, NULL, 'Cán bộ kiểm thử 12', 'bau_test_12@example.invalid', NULL, 'bau_test_12', '$2a$10$caQynoDlTQgQpkRTe6pTVOQghuw3xBDX7iAk5abJRCHXcJiPvOLGO', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(29, NULL, NULL, 'Cán bộ kiểm thử 13', 'bau_test_13@example.invalid', NULL, 'bau_test_13', '$2a$10$8xsyc8G/p9tJ0z5H45C0humckN.951ov1NbxNsBMWn5KWKd86YkB6', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(30, NULL, NULL, 'Cán bộ kiểm thử 14', 'bau_test_14@example.invalid', NULL, 'bau_test_14', '$2a$10$ciX116TYQMUzZJe5F8tsw.dfrn0OYP5eCa30VYFRTOwtULv27h1Um', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(31, NULL, NULL, 'Cán bộ kiểm thử 15', 'bau_test_15@example.invalid', NULL, 'bau_test_15', '$2a$10$iHhS.p.WIyoTAVxHEzkSpOEMMY.R8Ir3UtJLK58HdTH4YX3735G8u', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(32, NULL, NULL, 'Cán bộ kiểm thử 16', 'bau_test_16@example.invalid', NULL, 'bau_test_16', '$2a$10$kMmTtGg5nTVkfep1tKBwNeA9SDJkKtMvvP7NpOf76GblD8hNxzWQy', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(33, NULL, NULL, 'Cán bộ kiểm thử 17', 'bau_test_17@example.invalid', NULL, 'bau_test_17', '$2a$10$LMkupHeF/DS29h9TqC0OP.EfY6XVUt1g4GXnXpx/relDKCZJ4KRPS', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(34, NULL, NULL, 'Cán bộ kiểm thử 18', 'bau_test_18@example.invalid', NULL, 'bau_test_18', '$2a$10$GFdrvllvDMwzzonZqxE69u1U3YCyflkHunvkbgXujxksFwkJ3UtWe', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(35, NULL, NULL, 'Cán bộ kiểm thử 19', 'bau_test_19@example.invalid', NULL, 'bau_test_19', '$2a$10$agiHfoDH7axCFxuBsi14eOfVxWDuu7FeYM/KRWVCdtw65dS4Fcg82', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL),
(36, NULL, NULL, 'Cán bộ kiểm thử 20', 'bau_test_20@example.invalid', NULL, 'bau_test_20', '$2a$10$sS9sYzcMcH7EST/Q14YQ6e3el30kW4dVCw6T0dqqniKHMFne91EBe', 'Cán bộ', 'HOAT_DONG', '2026-11-22 22:41:37', NULL);

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
(9, 2),
(10, 2),
(4, 3),
(14, 3),
(5, 4),
(8, 4),
(11, 4),
(12, 4),
(13, 4),
(15, 4),
(16, 4),
(17, 4),
(18, 4),
(19, 4),
(20, 4),
(21, 4),
(22, 4),
(23, 4),
(24, 4),
(25, 4),
(26, 4),
(27, 4),
(28, 4),
(29, 4),
(30, 4),
(31, 4),
(32, 4),
(33, 4),
(34, 4),
(35, 4),
(36, 4);

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `nhat_ky_he_thong`
--

INSERT INTO `nhat_ky_he_thong` (`nhat_ky_id`, `nguoi_dung_id`, `hanh_dong`, `doi_tuong`, `doi_tuong_id`, `dia_chi_ip`, `chi_tiet`, `ngay_tao`) VALUES
(1, 6, 'TRINH_DUYET', 'KE_HOACH_CUOC_HOP', 103, NULL, '{\"nguon\": \"DU_LIEU_MAU\"}', '2026-11-12 15:45:26');

-- --------------------------------------------------------

--
-- Table structure for table `noi_dung_loi_moi_khach`
--

DROP TABLE IF EXISTS `noi_dung_loi_moi_khach`;
CREATE TABLE IF NOT EXISTS `noi_dung_loi_moi_khach` (
  `khach_moi_cuoc_hop_id` int NOT NULL,
  `tieu_de` varchar(255) NOT NULL,
  `noi_dung` text NOT NULL,
  `ten_tep` varchar(255) DEFAULT NULL,
  `kieu_tep` varchar(120) DEFAULT NULL,
  `noi_dung_tep` mediumblob,
  PRIMARY KEY (`khach_moi_cuoc_hop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `noi_dung_loi_moi_khach`
--

INSERT INTO `noi_dung_loi_moi_khach` (`khach_moi_cuoc_hop_id`, `tieu_de`, `noi_dung`, `ten_tep`, `kieu_tep`, `noi_dung_tep`) VALUES
(11, 'Thư mời họp – Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'Trân trọng kính mời bạn tham dự cuộc họp.', NULL, NULL, NULL),
(12, 'Thư mời họp – Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'Trân trọng kính mời bạn tham dự cuộc họp.', NULL, NULL, NULL);

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
(4, 6, 1, '2026-11-13 23:02:14', NULL),
(11, 10, 1, '2026-11-13 23:19:38', NULL);

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
(501, 401, 6, 1, 'Thống nhất lập bảng theo dõi hồ sơ.', NULL, '2026-11-12 15:45:26'),
(502, 402, 6, 1, 'câcss', 'Tạo bản nháp biên bản', '2026-11-12 17:07:14'),
(503, 402, 6, 2, 'câcssaaaa', 'aaaaaaaaaaaaaaa', '2026-11-12 17:07:21'),
(504, 403, 10, 1, 'a', 'Tạo bản nháp biên bản', '2026-11-14 00:06:04'),
(505, 404, 10, 1, 'này này này này kia', 'Tạo bản nháp biên bản', '2026-11-14 00:40:10'),
(506, 405, 6, 1, 'hhhh', 'Tạo bản nháp biên bản', '2026-11-14 15:27:01'),
(507, 406, 10, 1, 'biểu quyết chưa load đc nên ko có kết quả biểu quyết  , cần tạo thêm lối về từ calendar sang cuộc họp', 'Tạo bản nháp biên bản', '2026-11-17 23:17:49'),
(508, 406, 10, 2, 'biểu quyết chưa load đc nên ko có kết quả biểu quyết  , cần tạo thêm lối về từ calendar sang cuộc họp', 'ko thay đổi', '2026-11-17 23:17:58'),
(509, 407, 13, 1, 'aaaaaaaaa', 'Tạo bản nháp biên bản', '2026-11-18 00:05:56'),
(510, 407, 13, 2, 'aaaaaaaaa', 'aaaaaaaaaaaaaaa', '2026-11-18 00:18:29'),
(511, 407, 13, 3, 'aaaaaaaaa', 'Cập nhật nội dung biên bản', '2026-11-18 00:18:34'),
(512, 407, 13, 4, 'aaaaaaaaa', 'aa', '2026-11-18 00:54:57'),
(513, 408, 6, 1, 'tạm thời chưa có ai nên chưa dùng để hỗ trợ tạo biên bản đc', 'Tạo bản nháp biên bản', '2026-11-18 02:21:46'),
(514, 408, 6, 2, 'tạm thời chưa có ai nên chưa dùng để hỗ trợ tạo biên bản đc\n\nnếu bổ sung thêm file thì sao', 'Cập nhật nội dung biên bản', '2026-11-18 02:23:56'),
(515, 409, 10, 1, 'áaaaaa', 'Tạo bản nháp biên bản', '2026-11-18 15:50:29'),
(516, 410, 12, 1, 'aaaaaaaaaaaaaaaaaaa', 'Tạo bản nháp biên bản', '2026-11-18 16:39:21'),
(517, 411, 5, 1, 'aaaaaaaaaaa', 'Tạo bản nháp biên bản', '2026-11-18 17:14:29'),
(518, 412, 10, 1, 'aaaaaa', 'Tạo bản nháp biên bản', '2026-11-18 20:23:41'),
(519, 413, 10, 1, 'haaihhi tnashd', 'Tạo bản nháp biên bản', '2026-11-18 21:10:16'),
(520, 413, 10, 2, 'haaihhi tnashdaaaaaaaaaaaaa', 'Cập nhật nội dung biên bản', '2026-11-18 21:10:47'),
(521, 414, 15, 1, 'aaaaaaaaaaaa', 'Tạo bản nháp biên bản', '2026-11-20 22:06:33'),
(522, 415, 13, 1, 'aaaaaaaa', 'Tạo bản nháp biên bản', '2026-11-21 14:15:26');

-- --------------------------------------------------------

--
-- Table structure for table `phieu_bau`
--

DROP TABLE IF EXISTS `phieu_bau`;
CREATE TABLE IF NOT EXISTS `phieu_bau` (
  `vong_bau_id` int NOT NULL,
  `nguoi_bo_phieu_id` int NOT NULL,
  `thoi_gian_gui` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`vong_bau_id`,`nguoi_bo_phieu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `phieu_bau`
--

INSERT INTO `phieu_bau` (`vong_bau_id`, `nguoi_bo_phieu_id`, `thoi_gian_gui`) VALUES
(2, 17, '2026-11-22 23:06:20'),
(2, 18, '2026-11-22 23:06:32'),
(2, 19, '2026-11-22 23:06:46'),
(2, 20, '2026-11-22 23:06:57'),
(2, 21, '2026-11-22 23:07:23'),
(2, 22, '2026-11-22 23:04:43'),
(2, 23, '2026-11-22 23:05:02'),
(2, 24, '2026-11-22 23:05:18'),
(2, 25, '2026-11-22 23:05:31'),
(2, 26, '2026-11-22 23:05:46'),
(4, 17, '2026-11-22 23:09:05'),
(4, 19, '2026-11-22 23:09:11'),
(4, 20, '2026-11-22 23:09:15'),
(4, 21, '2026-11-22 23:09:21'),
(4, 23, '2026-11-22 23:08:45'),
(4, 26, '2026-11-22 23:09:00'),
(6, 17, '2026-11-23 00:19:00'),
(6, 18, '2026-11-23 00:19:04'),
(6, 19, '2026-11-23 00:19:09'),
(6, 20, '2026-11-23 00:19:16'),
(6, 21, '2026-11-23 00:19:20'),
(6, 22, '2026-11-23 00:19:28'),
(6, 23, '2026-11-23 00:19:33'),
(6, 24, '2026-11-23 00:19:37'),
(6, 25, '2026-11-23 00:19:41'),
(6, 26, '2026-11-23 00:18:53'),
(7, 17, '2026-11-23 00:21:10'),
(7, 18, '2026-11-23 00:21:12'),
(7, 19, '2026-11-23 00:21:17'),
(7, 20, '2026-11-23 00:21:19'),
(7, 21, '2026-11-23 00:21:22'),
(7, 22, '2026-11-23 00:21:25'),
(7, 23, '2026-11-23 00:21:28'),
(7, 24, '2026-11-23 00:21:30'),
(7, 25, '2026-11-23 00:21:33'),
(7, 26, '2026-11-23 00:21:35'),
(8, 17, '2026-11-23 00:22:41'),
(8, 18, '2026-11-23 00:22:43'),
(8, 19, '2026-11-23 00:22:47'),
(8, 20, '2026-11-23 00:22:49'),
(8, 21, '2026-11-23 00:22:52'),
(8, 22, '2026-11-23 00:22:54'),
(8, 23, '2026-11-23 00:22:56'),
(8, 24, '2026-11-23 00:22:59'),
(8, 25, '2026-11-23 00:23:01'),
(8, 26, '2026-11-23 00:22:39'),
(9, 17, '2026-11-23 00:24:15'),
(9, 18, '2026-11-23 00:24:18'),
(9, 19, '2026-11-23 00:24:21'),
(9, 20, '2026-11-23 00:24:25'),
(9, 21, '2026-11-23 00:24:28'),
(9, 22, '2026-11-23 00:24:31'),
(9, 23, '2026-11-23 00:24:33'),
(9, 24, '2026-11-23 00:24:36'),
(9, 25, '2026-11-23 00:24:39'),
(9, 26, '2026-11-23 00:24:11'),
(10, 17, '2026-11-23 00:25:30'),
(10, 18, '2026-11-23 00:25:34'),
(10, 19, '2026-11-23 00:25:37'),
(10, 20, '2026-11-23 00:25:40'),
(10, 21, '2026-11-23 00:25:42'),
(10, 22, '2026-11-23 00:25:46'),
(10, 23, '2026-11-23 00:25:50'),
(10, 24, '2026-11-23 00:25:54'),
(10, 25, '2026-11-23 00:25:58'),
(10, 26, '2026-11-23 00:25:27'),
(11, 17, '2026-11-23 00:26:47'),
(11, 18, '2026-11-23 00:26:52'),
(11, 19, '2026-11-23 00:26:56'),
(11, 20, '2026-11-23 00:26:59'),
(11, 21, '2026-11-23 00:27:03'),
(11, 22, '2026-11-23 00:27:06'),
(11, 23, '2026-11-23 00:27:09'),
(11, 24, '2026-11-23 00:27:12'),
(11, 25, '2026-11-23 00:27:16'),
(11, 26, '2026-11-23 00:26:42');

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
  `ly_do_khong_dong_y` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`phieu_bieu_quyet_id`),
  UNIQUE KEY `uk_phieu_bieu_quyet_ma_phieu` (`bieu_quyet_id`,`ma_phieu`),
  KEY `idx_phieu_bieu_quyet_lua_chon` (`lua_chon_id`),
  KEY `idx_phieu_bieu_quyet_nguoi_dung` (`bieu_quyet_id`,`nguoi_dung_id`),
  KEY `fk_phieu_bieu_quyet_lua_chon` (`lua_chon_id`,`bieu_quyet_id`),
  KEY `fk_phieu_bieu_quyet_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `phieu_bieu_quyet`
--

INSERT INTO `phieu_bieu_quyet` (`phieu_bieu_quyet_id`, `bieu_quyet_id`, `lua_chon_id`, `nguoi_dung_id`, `ma_phieu`, `thoi_gian_bieu_quyet`, `ly_do_khong_dong_y`) VALUES
(1, 701, 801, 8, NULL, '2026-11-11 16:10:26', NULL),
(2, 705, 813, 13, NULL, '2026-11-13 14:38:59', NULL),
(3, 709, 825, 13, NULL, '2026-11-13 15:19:50', NULL),
(4, 709, 825, 8, NULL, '2026-11-13 15:20:49', NULL),
(5, 710, 828, 8, NULL, '2026-11-13 15:23:12', NULL),
(6, 710, 828, 13, NULL, '2026-11-13 15:23:28', NULL),
(7, 711, 831, 13, NULL, '2026-11-13 15:26:01', NULL),
(8, 711, 831, 8, NULL, '2026-11-13 15:26:21', NULL),
(9, 712, 835, 8, NULL, '2026-11-13 15:43:54', NULL),
(10, 718, 852, 14, NULL, '2026-11-13 16:07:14', NULL),
(11, 720, 858, 4, NULL, '2026-11-14 00:32:24', NULL),
(12, 720, 859, 5, NULL, '2026-11-14 00:32:56', NULL),
(13, 720, 858, 6, NULL, '2026-11-14 00:33:12', NULL),
(14, 720, 860, 11, NULL, '2026-11-14 00:33:45', NULL),
(15, 721, 863, 4, NULL, '2026-11-14 00:37:21', NULL),
(16, 721, 862, 5, NULL, '2026-11-14 00:37:44', NULL),
(17, 721, 861, 11, NULL, '2026-11-14 00:38:33', NULL),
(18, 724, 870, 14, NULL, '2026-11-18 00:02:26', NULL),
(19, 724, 872, 12, NULL, '2026-11-18 00:02:48', NULL),
(20, 724, 870, 11, NULL, '2026-11-18 00:03:07', NULL),
(21, 724, 870, 8, NULL, '2026-11-18 00:04:14', NULL),
(22, 725, 873, 14, NULL, '2026-11-18 02:18:26', NULL),
(23, 725, 874, 12, NULL, '2026-11-18 02:18:40', NULL),
(24, 725, 875, 11, NULL, '2026-11-18 02:18:58', NULL),
(25, 725, 873, 10, NULL, '2026-11-18 02:19:22', NULL),
(26, 725, 874, 9, NULL, '2026-11-18 02:19:44', NULL),
(27, 726, 877, 14, NULL, '2026-11-18 15:48:02', NULL),
(28, 726, 876, 13, NULL, '2026-11-18 15:48:17', NULL),
(29, 726, 876, 12, NULL, '2026-11-18 15:48:39', NULL),
(30, 727, 879, 6, NULL, '2026-11-18 17:04:12', NULL),
(31, 727, 881, 14, NULL, '2026-11-18 17:04:17', NULL),
(32, 727, 880, 13, NULL, '2026-11-18 17:04:21', NULL),
(33, 729, 885, 10, NULL, '2026-11-18 21:07:19', NULL),
(34, 729, 885, 13, NULL, '2026-11-18 21:07:22', NULL),
(35, 729, 886, 14, NULL, '2026-11-18 21:07:27', NULL),
(36, 729, 887, 11, NULL, '2026-11-18 21:07:39', NULL),
(37, 730, 889, 13, NULL, '2026-11-18 21:08:54', NULL),
(38, 730, 888, 14, NULL, '2026-11-18 21:08:57', NULL),
(39, 731, 891, 16, NULL, '2026-11-20 22:05:23', NULL),
(40, 731, 893, 6, NULL, '2026-11-20 22:05:27', NULL),
(41, 731, 891, 15, NULL, '2026-11-20 22:05:32', NULL),
(42, 731, 893, 10, NULL, '2026-11-20 22:05:36', NULL),
(43, 732, 894, 13, NULL, '2026-11-21 14:08:48', NULL),
(44, 733, 897, 22, NULL, '2026-11-22 22:53:37', NULL),
(45, 733, 897, 23, NULL, '2026-11-22 22:53:45', NULL),
(46, 733, 897, 24, NULL, '2026-11-22 22:53:49', NULL),
(47, 733, 897, 25, NULL, '2026-11-22 22:53:52', NULL),
(48, 733, 897, 26, NULL, '2026-11-22 22:53:57', NULL),
(49, 733, 898, 17, NULL, '2026-11-22 22:54:01', NULL),
(50, 733, 898, 18, NULL, '2026-11-22 22:54:05', NULL),
(51, 733, 898, 19, NULL, '2026-11-22 22:54:09', NULL),
(52, 733, 898, 20, NULL, '2026-11-22 22:54:13', NULL),
(53, 733, 898, 21, NULL, '2026-11-22 22:54:17', NULL),
(54, 734, 900, 21, NULL, '2026-11-22 22:58:44', NULL),
(55, 734, 900, 23, NULL, '2026-11-22 22:58:48', NULL),
(56, 734, 901, 24, NULL, '2026-11-22 22:58:52', NULL),
(57, 734, 901, 25, NULL, '2026-11-22 22:58:55', NULL),
(58, 734, 901, 26, NULL, '2026-11-22 22:58:58', NULL),
(59, 734, 901, 17, NULL, '2026-11-22 22:59:02', NULL),
(60, 734, 901, 18, NULL, '2026-11-22 22:59:05', NULL),
(61, 734, 901, 19, NULL, '2026-11-22 22:59:10', NULL),
(62, 734, 901, 20, NULL, '2026-11-22 22:59:14', NULL),
(63, 734, 901, 22, NULL, '2026-11-22 22:59:22', NULL),
(64, 735, 904, 26, NULL, '2026-11-23 00:33:55', 'ko đồng ý , ngị quyết trống'),
(65, 735, 903, 17, NULL, '2026-11-23 00:34:05', NULL),
(66, 735, 904, 18, NULL, '2026-11-23 00:34:18', 'a'),
(67, 735, 905, 19, NULL, '2026-11-23 00:34:24', NULL),
(68, 735, 905, 20, NULL, '2026-11-23 00:34:28', NULL),
(69, 735, 904, 21, NULL, '2026-11-23 00:34:41', 'ko dồng ý  tôi ko thấy đc nội dung'),
(70, 735, 903, 22, NULL, '2026-11-23 00:34:47', NULL),
(71, 735, 904, 23, NULL, '2026-11-23 00:34:54', 'ko oke'),
(72, 735, 905, 24, NULL, '2026-11-23 00:34:59', NULL),
(73, 735, 904, 25, NULL, '2026-11-23 00:35:03', 'aaaaa'),
(74, 737, 909, 26, NULL, '2026-11-23 00:38:18', NULL),
(75, 737, 909, 17, NULL, '2026-11-23 00:38:21', NULL),
(76, 737, 909, 18, NULL, '2026-11-23 00:38:23', NULL),
(77, 737, 909, 19, NULL, '2026-11-23 00:38:27', NULL),
(78, 737, 909, 20, NULL, '2026-11-23 00:38:31', NULL),
(79, 737, 910, 21, NULL, '2026-11-23 00:38:43', 'ko đồng ý tbale thiếu ròi'),
(80, 737, 910, 22, NULL, '2026-11-23 00:38:50', 'tôi cũng dậy'),
(81, 737, 911, 23, NULL, '2026-11-23 00:38:54', NULL),
(82, 737, 911, 24, NULL, '2026-11-23 00:38:56', NULL),
(83, 737, 911, 25, NULL, '2026-11-23 00:38:58', NULL),
(84, 738, 912, 17, NULL, '2026-11-23 00:42:10', NULL),
(85, 738, 912, 18, NULL, '2026-11-23 00:42:12', NULL),
(86, 738, 913, 19, NULL, '2026-11-23 00:42:21', 'ko dồng ý'),
(87, 739, 916, 25, NULL, '2026-11-23 00:44:01', 'ko'),
(88, 739, 916, 26, NULL, '2026-11-23 00:44:13', 'v'),
(89, 739, 915, 24, NULL, '2026-11-23 00:44:17', NULL);

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
(3, 'Phòng Nội vụ', 11, 'Tham mưu và thực hiện công tác nội vụ', 'HOAT_DONG', '2026-10-31 23:17:01');

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
(1, 'Phòng họp A2', 'Tầng 2', 24, NULL, 'SAN_SANG'),
(2, 'Hội trường B', 'Tầng 1', 80, NULL, 'SAN_SANG'),
(3, 'Phòng họp C', 'Tầng 3', 12, NULL, 'BAO_TRI');

-- --------------------------------------------------------

--
-- Table structure for table `tai_lieu_chia_se_khach`
--

DROP TABLE IF EXISTS `tai_lieu_chia_se_khach`;
CREATE TABLE IF NOT EXISTS `tai_lieu_chia_se_khach` (
  `tai_lieu_id` int NOT NULL,
  PRIMARY KEY (`tai_lieu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

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
) ENGINE=InnoDB AUTO_INCREMENT=1060 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tai_lieu_cuoc_hop`
--

INSERT INTO `tai_lieu_cuoc_hop` (`tai_lieu_id`, `cuoc_hop_id`, `nguoi_tai_len_id`, `ten_tai_lieu`, `loai_tai_lieu`, `ten_file_goc`, `duong_dan_file`, `dinh_dang_file`, `dung_luong_byte`, `noi_dung_van_ban`, `trang_thai_xu_ly`, `mo_ta`, `ngay_tai_len`) VALUES
(1001, 107, 6, 'Ghi chú tổng kết', 'TAI_LIEU_DAU_VAO', NULL, NULL, NULL, NULL, 'Thống nhất lập bảng theo dõi hồ sơ và cập nhật hằng tuần.', 'CHUA_XU_LY', NULL, '2026-11-12 15:45:26'),
(1002, 103, 6, 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'KET_LUAN_CUOC_HOP', 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-7759979654954865229.pdf', 'pdf', 84231, NULL, 'CHUA_XU_LY', NULL, '2026-11-12 17:20:41'),
(1003, 103, 6, 'Chuong_1_2_3_Quan_ly_cuoc_hop.docx', 'KET_LUAN_CUOC_HOP', 'Chuong_1_2_3_Quan_ly_cuoc_hop.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-16138866217488685999.docx', 'docx', 77579, NULL, 'CHUA_XU_LY', NULL, '2026-11-12 17:21:03'),
(1004, 111, 10, 'he_thong_quan_ly_va_to_chuc_cuoc_hop_cho_co_quan_hanh_chinh.docx', 'KE_HOACH_CUOC_HOP', 'he_thong_quan_ly_va_to_chuc_cuoc_hop_cho_co_quan_hanh_chinh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\8d0d281c-d91c-459f-84b5-cfbaeb87095a10852525514031625659.docx', 'docx', 60231, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 00:20:24'),
(1005, 111, 10, 'CV_Lu_Chi_Thanh_dieu_chinh.pdf', 'KE_HOACH_CUOC_HOP', 'CV_Lu_Chi_Thanh_dieu_chinh.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\770a243a-9b4b-49c2-b06c-2963417f2a897266605652216838456.pdf', 'pdf', 75757, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 00:21:36'),
(1006, 111, 10, 'Lu-Chi-Thanh-Tester.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-Tester.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-10923677748166360871.pdf', 'pdf', 244344, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 00:40:30'),
(1007, 112, 10, 'Lư Chí Thanh.docx', 'KE_HOACH_CUOC_HOP', 'Lư Chí Thanh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\10eae722-cac3-4864-af30-39c04771aa7c5881491298879340202.docx', 'docx', 62122, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 11:31:54'),
(1008, 101, 6, 'Lư Chí Thanh.docx', 'KE_HOACH_CUOC_HOP', 'Lư Chí Thanh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\994b07c8-b4ed-4356-a753-2a2c2e5142e515908223125421366848.docx', 'docx', 62122, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 11:34:24'),
(1009, 113, 10, 'Lư Chí Thanh.docx', 'KE_HOACH_CUOC_HOP', 'Lư Chí Thanh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\28d6bbb8-6ade-4dfa-918d-2df33f2fd3e39736216004346378734.docx', 'docx', 62122, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 13:14:48'),
(1010, 114, 10, 'post_dslvtn_4.xlsx', 'KE_HOACH_CUOC_HOP', 'post_dslvtn_4.xlsx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\31b4d93a-1f9f-4ff1-bca1-43f5729e58f014019084596093799721.xlsx', 'xlsx', 32095, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 15:16:07'),
(1011, 114, 10, 'Lư Chí Thanh.docx', 'KE_HOACH_CUOC_HOP', 'Lư Chí Thanh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\46cec961-f1ad-4e08-b221-e04be8abcdf8982040069513927085.docx', 'docx', 62122, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 15:16:12'),
(1012, 114, 10, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'KE_HOACH_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\72482155-40e3-4e68-853e-1d9a7b588e9f4725848971966105208.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 15:18:40'),
(1013, 105, 6, 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-4238731256343833722.pdf', 'pdf', 249321, NULL, 'CHUA_XU_LY', NULL, '2026-11-14 15:28:04'),
(1014, 114, 10, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'KE_HOACH_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\26829553-b411-490b-bf35-0b791425f059421691585514436564.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-16 01:31:39'),
(1015, 115, 10, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh.docx', 'KE_HOACH_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\b5f8a16d-f14f-4d8e-abbf-102b5ee9b05416687042681495234053.docx', 'docx', 19291, NULL, 'CHUA_XU_LY', NULL, '2026-11-17 23:05:36'),
(1016, 116, 10, 'TLANDROID (1).docx', 'KE_HOACH_CUOC_HOP', 'TLANDROID (1).docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\50df5fb6-dbfb-4f6c-8e2e-94df497f25017050623534463325422.docx', 'docx', 1390386, NULL, 'CHUA_XU_LY', NULL, '2026-11-17 23:48:24'),
(1017, 116, 10, 'post_dslvtn_4.xlsx', 'KE_HOACH_CUOC_HOP', 'post_dslvtn_4.xlsx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\65aa2644-3a87-47ac-bc28-87d32bebbbd612439478237404941872.xlsx', 'xlsx', 32095, NULL, 'CHUA_XU_LY', NULL, '2026-11-17 23:48:29'),
(1018, 116, 13, 'he_thong_quan_ly_va_to_chuc_cuoc_hop_cho_co_quan_hanh_chinh.docx', 'KET_LUAN_CUOC_HOP', 'he_thong_quan_ly_va_to_chuc_cuoc_hop_cho_co_quan_hanh_chinh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-15561491042811805944.docx', 'docx', 60231, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 00:18:14'),
(1019, 116, 13, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-15827031500240071523.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 00:18:23'),
(1020, 116, 13, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-3009926687446604433.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 00:25:35'),
(1021, 116, 13, 'Lu-Chi-Thanh-BACKEND-.pdf', 'BIEN_BAN_CUOC_HOP', 'Lu-Chi-Thanh-BACKEND-.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-2883584812340626352.pdf', 'pdf', 138148, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 00:55:56'),
(1022, 117, 6, 'Đặc Tả.docx', 'KE_HOACH_CUOC_HOP', 'Đặc Tả.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\d854f083-572e-47f3-9d78-088a6e7535c24995737970343542766.docx', 'docx', 38804, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 01:25:41'),
(1023, 117, 6, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'KE_HOACH_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\a9c5f1f8-d9b2-4e9f-8e67-f8149549d19a8383017649146484341.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 01:43:29'),
(1024, 117, 6, 'Diem.xlsx', 'KE_HOACH_CUOC_HOP', 'Diem.xlsx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\474991a7-25a5-4fa9-90c8-cac5bb3c080812453985543591823416.xlsx', 'xlsx', 44502, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 01:51:02'),
(1025, 117, 6, 'lich-gap-gv-huong-dan-lvtn-t10_2026.xlsx', 'KE_HOACH_CUOC_HOP', 'lich-gap-gv-huong-dan-lvtn-t10_2026.xlsx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\4e71438e-ca0f-4d3b-a9de-1b8fe1a2f34e16942346525675568362.xlsx', 'xlsx', 12784, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 01:58:30'),
(1026, 117, 6, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\1e1ff6e2-afb5-4592-b24f-c853e0bc8ff812279515472182919266.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 02:00:32'),
(1027, 117, 6, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'BIEN_BAN_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-7664683340893020044.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 02:23:32'),
(1028, 115, 10, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'KET_LUAN_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-469966755935427224.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 15:36:21'),
(1029, 114, 10, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'BIEN_BAN_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-12423436035692137027.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 15:50:36'),
(1030, 114, 10, 'Chuong_1_2_3_Quan_ly_cuoc_hop.docx', 'KET_LUAN_CUOC_HOP', 'Chuong_1_2_3_Quan_ly_cuoc_hop.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-11553751666757009824.docx', 'docx', 77579, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 15:51:18'),
(1031, 113, 12, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'BIEN_BAN_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-10082317489106512763.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 16:39:27'),
(1032, 113, 12, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh.docx', 'KET_LUAN_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-12133208924306190191.docx', 'docx', 19291, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 16:40:18'),
(1033, 118, 10, 'Lu-Chi-Thanh-Backend.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-Backend.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\3c1faebb-9e82-4293-a3f2-455a80e68e6b12611370402280463401.pdf', 'pdf', 238194, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 16:56:54'),
(1034, 118, 5, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'BIEN_BAN_CUOC_HOP', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh - Copy.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-8886065973774147964.docx', 'docx', 20703, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 17:14:34'),
(1035, 118, 5, 'he_thong_quan_ly_va_to_chuc_cuoc_hop_cho_co_quan_hanh_chinh.docx', 'BIEN_BAN_CUOC_HOP', 'he_thong_quan_ly_va_to_chuc_cuoc_hop_cho_co_quan_hanh_chinh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-15026861958767629525.docx', 'docx', 60231, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 17:19:21'),
(1036, 118, 5, 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-9020293947831556914.pdf', 'pdf', 249321, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 17:20:09'),
(1037, 119, 10, 'TLANDROID (1).docx', 'KE_HOACH_CUOC_HOP', 'TLANDROID (1).docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\7b85bfe6-d289-40eb-9ee0-1754b18446b618397158512042015071.docx', 'docx', 1390386, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 20:10:37'),
(1038, 119, 10, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-17556947170766347840.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 20:24:57'),
(1039, 120, 10, 'Android.docx', 'KE_HOACH_CUOC_HOP', 'Android.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\f8117114-ed06-4a21-8104-fdc78a57238c11787276892665391883.docx', 'docx', 917791, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 20:49:44'),
(1040, 120, 10, 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'BIEN_BAN_CUOC_HOP', 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-15144341366235430558.pdf', 'pdf', 249321, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 21:10:23'),
(1041, 120, 10, 'Lu-Chi-Thanh-Tester.pdf', 'KET_LUAN_CUOC_HOP', 'Lu-Chi-Thanh-Tester.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ket-luan-cuoc-hop\\tai-lieu-14258982599475036278.pdf', 'pdf', 244344, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 21:12:29'),
(1042, 122, 10, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\34ab7b4a-e790-4ed0-9c27-a49751d7cc469740916820127800975.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-18 23:01:46'),
(1043, 127, 10, 'Lu-Chi-Thanh-BACKEND-.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-BACKEND-.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\70d83c98-f025-4620-8a3e-27e94459ef9a17977517703675475861.pdf', 'pdf', 138148, NULL, 'CHUA_XU_LY', NULL, '2026-11-20 15:23:28'),
(1044, 127, 15, 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'BIEN_BAN_CUOC_HOP', 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\bien-ban-cuoc-hop\\tai-lieu-2026975314473234960.pdf', 'pdf', 249321, NULL, 'CHUA_XU_LY', NULL, '2026-11-20 22:06:40'),
(1045, 129, 6, 'Lu-Chi-Thanh-BACKEND-.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-BACKEND-.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\c0245c7f-4ede-4cb4-8647-14e49e7f4bb75292750747253243044.pdf', 'pdf', 138148, NULL, 'CHUA_XU_LY', NULL, '2026-11-21 00:08:37'),
(1046, 130, 10, 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-TopCV.vn-helpdesk.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\8e97705e-65fa-45f3-a9ef-02bc2315853210255233085762785220.pdf', 'pdf', 249321, NULL, 'CHUA_XU_LY', NULL, '2026-11-21 13:52:41'),
(1047, 132, 6, 'Lu-Chi-Thanh-Tester.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-Tester.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\d6921821-f214-4c1c-af1c-505b7c7c415e242434068853798456.pdf', 'pdf', 244344, NULL, 'CHUA_XU_LY', NULL, '2026-11-21 14:00:38'),
(1048, 134, 6, 'Lư Chí Thanh.docx', 'KE_HOACH_CUOC_HOP', 'Lư Chí Thanh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\82d58dd6-6ba8-4729-84d7-a102c43e85068287484877387293638.docx', 'docx', 62122, NULL, 'CHUA_XU_LY', NULL, '2026-11-22 23:12:33'),
(1049, 135, 6, 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-FULL-STACKpdf.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\d3447564-6b14-4190-bd12-8076ff6a7c2c12781148100788242482.pdf', 'pdf', 243988, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:12:10'),
(1050, 135, 4, 'Unnamed1.PDF', 'NGHI_QUYET', 'Unnamed1.PDF', 'uploads\\nghi-quyet\\tai-lieu-13223465865414485152.pdf', 'pdf', 1203, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:15:42'),
(1051, 135, 6, 'Unnamed1.PDF', 'NGHI_QUYET', 'Unnamed1.PDF', 'uploads\\nghi-quyet\\tai-lieu-8305982190862303205.pdf', 'pdf', 1203, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:32:30'),
(1052, 135, 6, 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'NGHI_QUYET', 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'uploads\\nghi-quyet\\tai-lieu-865591087896741190.pdf', 'pdf', 84231, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:36:36'),
(1053, 135, 6, 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'NGHI_QUYET', 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'uploads\\nghi-quyet\\tai-lieu-15047699125329772080.pdf', 'pdf', 84231, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:37:40'),
(1054, 135, 6, 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'NGHI_QUYET', 'ERD_quan_ly_cuoc_hop_24_bang.drawio.pdf', 'uploads\\nghi-quyet\\tai-lieu-8014788748171396209.pdf', 'pdf', 84231, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:42:52'),
(1055, 136, 6, 'Lu-Chi-Thanh-BACKEND-.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-BACKEND-.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\e86d1eb1-27ee-4a38-91e7-ef71f9e7225f2680731866649039273.pdf', 'pdf', 138148, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:46:57'),
(1056, 136, 6, 'Lư Chí Thanh- DH52201447.doc', 'CHIA_SE_KHACH_NGOAI', 'Lư Chí Thanh- DH52201447.doc', 'uploads\\chia-se-khach-ngoai\\tai-lieu-17297457611976333183.doc', 'doc', 35328, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:47:54'),
(1057, 136, 6, 'Lư Chí Thanh.docx', 'KE_HOACH_CUOC_HOP', 'Lư Chí Thanh.docx', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\e37408b3-653c-47ea-9905-ad5ad4785a6217653218660398044056.docx', 'docx', 62122, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:49:14'),
(1058, 136, 6, 'Lu-Chi-Thanh-Tester.pdf', 'KE_HOACH_CUOC_HOP', 'Lu-Chi-Thanh-Tester.pdf', 'D:\\HE_THONG_QUAN_LY_VA_HO_TRO_TO_CHUC_CUOC_HOP\\BE\\uploads\\ke-hoach-cuoc-hop\\dd661385-13b9-4b1d-ad52-f5ff679e2df97323626964798922373.pdf', 'pdf', 244344, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:49:23'),
(1059, 136, 6, 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh.docx', 'CHIA_SE_KHACH_NGOAI', 'Đề cương đề tài – Hệ thống quản lý và hỗ trợ tổ chức cuộc họp thông minh.docx', 'uploads\\chia-se-khach-ngoai\\tai-lieu-11192700011731466127.docx', 'docx', 19291, NULL, 'CHUA_XU_LY', NULL, '2026-11-23 00:51:07');

-- --------------------------------------------------------

--
-- Table structure for table `thanh_vien_bieu_quyet`
--

DROP TABLE IF EXISTS `thanh_vien_bieu_quyet`;
CREATE TABLE IF NOT EXISTS `thanh_vien_bieu_quyet` (
  `bieu_quyet_id` int NOT NULL,
  `nguoi_dung_id` int NOT NULL,
  PRIMARY KEY (`bieu_quyet_id`,`nguoi_dung_id`),
  KEY `fk_tvbq_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `thanh_vien_bieu_quyet`
--

INSERT INTO `thanh_vien_bieu_quyet` (`bieu_quyet_id`, `nguoi_dung_id`) VALUES
(720, 4),
(721, 4),
(731, 4),
(720, 5),
(721, 5),
(726, 5),
(720, 6),
(721, 6),
(727, 6),
(731, 6),
(701, 8),
(704, 8),
(705, 8),
(706, 8),
(707, 8),
(708, 8),
(709, 8),
(710, 8),
(711, 8),
(712, 8),
(713, 8),
(714, 8),
(715, 8),
(716, 8),
(717, 8),
(718, 8),
(722, 8),
(723, 8),
(724, 8),
(726, 8),
(725, 9),
(722, 10),
(725, 10),
(727, 10),
(728, 10),
(729, 10),
(731, 10),
(720, 11),
(721, 11),
(724, 11),
(725, 11),
(729, 11),
(730, 11),
(732, 11),
(724, 12),
(725, 12),
(726, 12),
(704, 13),
(705, 13),
(706, 13),
(707, 13),
(708, 13),
(709, 13),
(710, 13),
(711, 13),
(712, 13),
(713, 13),
(714, 13),
(715, 13),
(716, 13),
(717, 13),
(718, 13),
(722, 13),
(723, 13),
(726, 13),
(727, 13),
(729, 13),
(730, 13),
(732, 13),
(716, 14),
(717, 14),
(718, 14),
(722, 14),
(723, 14),
(724, 14),
(725, 14),
(726, 14),
(727, 14),
(729, 14),
(730, 14),
(731, 15),
(731, 16),
(732, 16),
(733, 17),
(734, 17),
(735, 17),
(736, 17),
(737, 17),
(738, 17),
(733, 18),
(734, 18),
(735, 18),
(736, 18),
(737, 18),
(738, 18),
(733, 19),
(734, 19),
(735, 19),
(736, 19),
(737, 19),
(738, 19),
(733, 20),
(734, 20),
(735, 20),
(736, 20),
(737, 20),
(733, 21),
(734, 21),
(735, 21),
(736, 21),
(737, 21),
(733, 22),
(734, 22),
(735, 22),
(736, 22),
(737, 22),
(733, 23),
(734, 23),
(735, 23),
(736, 23),
(737, 23),
(733, 24),
(734, 24),
(735, 24),
(736, 24),
(737, 24),
(739, 24),
(733, 25),
(734, 25),
(735, 25),
(736, 25),
(737, 25),
(739, 25),
(733, 26),
(734, 26),
(735, 26),
(736, 26),
(737, 26),
(739, 26);

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
  `thoi_gian_roi_cuoc_hop` datetime DEFAULT NULL,
  `ghi_chu` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thoi_gian_tu_diem_danh` datetime(6) DEFAULT NULL,
  `thoi_gian_doi_chieu_diem_danh` datetime(6) DEFAULT NULL,
  `ngay_moi` datetime DEFAULT NULL,
  PRIMARY KEY (`cuoc_hop_id`,`nguoi_dung_id`),
  KEY `idx_tvc_hop_nguoi_dung` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `thanh_vien_cuoc_hop`
--

INSERT INTO `thanh_vien_cuoc_hop` (`cuoc_hop_id`, `nguoi_dung_id`, `vai_tro_tai_cuoc_hop`, `trang_thai_moi`, `trang_thai_tham_du`, `thoi_gian_diem_danh`, `thoi_gian_roi_cuoc_hop`, `ghi_chu`, `thoi_gian_tu_diem_danh`, `thoi_gian_doi_chieu_diem_danh`, `ngay_moi`) VALUES
(101, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(102, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(103, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(103, 10, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-12 16:51:00', NULL, NULL, NULL, NULL, NULL),
(103, 11, 'THAM_DU', 'CHO_PHAN_HOI', 'VANG_MAT', '2026-11-12 16:51:09', NULL, NULL, NULL, NULL, NULL),
(103, 12, 'THAM_DU', 'CHO_PHAN_HOI', 'CO_MAT', '2026-11-12 16:51:11', NULL, NULL, NULL, NULL, NULL),
(103, 13, 'THAM_DU', 'CHO_PHAN_HOI', 'VANG_MAT', '2026-11-12 16:51:20', NULL, NULL, NULL, NULL, NULL),
(104, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(105, 1, 'THAM_DU', 'CHO_PHAN_HOI', 'VANG_MAT', '2026-11-12 17:59:42', NULL, NULL, NULL, NULL, NULL),
(105, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(105, 8, 'THAM_DU', 'TU_CHOI', 'CO_MAT', '2026-11-12 17:59:43', NULL, NULL, NULL, NULL, NULL),
(105, 13, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-12 17:59:46', NULL, NULL, NULL, NULL, NULL),
(105, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-13 15:59:35', NULL, NULL, NULL, NULL, NULL),
(106, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(107, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(107, 8, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-11 15:45:26', NULL, NULL, NULL, NULL, NULL),
(108, 5, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-11 17:45:26', NULL, NULL, NULL, NULL, NULL),
(108, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(109, 1, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(109, 6, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(109, 8, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(109, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(109, 13, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(110, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(111, 4, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-14 00:28:58', NULL, NULL, NULL, NULL, NULL),
(111, 5, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-14 00:29:08', NULL, NULL, NULL, NULL, NULL),
(111, 6, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-14 00:29:07', '2026-11-14 00:37:01', NULL, NULL, NULL, NULL),
(111, 9, 'THAM_DU', 'TU_CHOI', 'VANG_MAT', '2026-11-14 00:29:03', NULL, NULL, NULL, NULL, NULL),
(111, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(111, 11, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-14 00:29:18', NULL, NULL, NULL, NULL, NULL),
(112, 6, 'THU_KY_LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(112, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(113, 6, 'THU_KY_LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(113, 8, 'THAM_DU', 'CHO_PHAN_HOI', 'VANG_MAT', '2026-11-18 16:35:22', NULL, NULL, NULL, NULL, NULL),
(113, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(113, 13, 'THAM_DU', 'CHO_PHAN_HOI', 'CO_MAT', '2026-11-18 16:35:27', NULL, NULL, NULL, NULL, NULL),
(113, 14, 'THAM_DU', 'CHO_PHAN_HOI', 'CO_MAT', '2026-11-18 16:35:23', NULL, NULL, NULL, NULL, NULL),
(114, 5, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 15:46:25', NULL, NULL, NULL, NULL, NULL),
(114, 6, 'THU_KY_LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(114, 8, 'THAM_DU', 'TU_CHOI', 'CO_MAT', '2026-11-18 15:46:26', NULL, NULL, NULL, NULL, NULL),
(114, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(114, 12, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 15:46:26', NULL, NULL, NULL, NULL, NULL),
(114, 13, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 15:46:27', NULL, NULL, NULL, NULL, NULL),
(114, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 15:46:27', NULL, NULL, NULL, NULL, NULL),
(115, 1, 'THAM_DU', 'CHO_PHAN_HOI', 'VANG_MAT', '2026-11-17 23:11:15', NULL, NULL, NULL, NULL, NULL),
(115, 8, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-17 23:11:22', NULL, NULL, NULL, NULL, NULL),
(115, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(115, 12, 'THAM_DU', 'TU_CHOI', 'VANG_MAT', '2026-11-17 23:11:18', NULL, NULL, NULL, NULL, NULL),
(115, 13, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-17 23:11:23', NULL, NULL, NULL, NULL, NULL),
(115, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-17 23:11:24', NULL, NULL, NULL, NULL, NULL),
(116, 1, 'THAM_DU', 'CHO_PHAN_HOI', 'VANG_MAT', '2026-11-18 00:00:56', NULL, NULL, NULL, NULL, NULL),
(116, 6, 'THU_KY_LANH_DAO', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(116, 8, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 00:01:01', NULL, NULL, NULL, NULL, NULL),
(116, 9, 'THAM_DU', 'DA_XAC_NHAN', 'VANG_MAT', '2026-11-18 00:01:02', NULL, NULL, NULL, NULL, NULL),
(116, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(116, 11, 'THAM_DU', 'TU_CHOI', 'CO_MAT', '2026-11-18 00:01:03', NULL, NULL, NULL, NULL, NULL),
(116, 12, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 00:01:04', NULL, NULL, NULL, NULL, NULL),
(116, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 00:01:05', NULL, NULL, NULL, NULL, NULL),
(117, 5, 'THAM_DU', 'DA_XAC_NHAN', 'VANG_MAT', '2026-11-18 02:16:55', NULL, NULL, NULL, NULL, NULL),
(117, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(117, 9, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 02:16:57', '2026-11-18 02:20:55', NULL, NULL, NULL, NULL),
(117, 10, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 02:16:58', NULL, NULL, NULL, NULL, NULL),
(117, 11, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 02:21:01', '2026-11-18 02:21:06', NULL, NULL, NULL, NULL),
(117, 12, 'THAM_DU', 'DA_XAC_NHAN', 'VANG_MAT', '2026-11-18 02:21:04', NULL, NULL, NULL, NULL, NULL),
(117, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 02:17:00', NULL, NULL, NULL, NULL, NULL),
(118, 6, 'THU_KY_LANH_DAO', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 17:03:27', NULL, NULL, NULL, NULL, NULL),
(118, 10, 'THU_KY', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 17:03:16', NULL, NULL, NULL, NULL, NULL),
(118, 13, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 17:02:19', NULL, NULL, NULL, NULL, NULL),
(118, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 17:02:49', NULL, NULL, NULL, NULL, NULL),
(119, 10, 'THU_KY', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 20:11:49', NULL, NULL, NULL, NULL, NULL),
(120, 8, 'THAM_DU', 'TU_CHOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(120, 10, 'THU_KY', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 20:52:39', NULL, NULL, NULL, NULL, NULL),
(120, 11, 'CHU_TRI', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 21:05:29', NULL, NULL, NULL, NULL, NULL),
(120, 13, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 20:52:44', NULL, NULL, NULL, NULL, NULL),
(120, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-18 20:52:48', NULL, NULL, NULL, NULL, NULL),
(121, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(122, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(122, 14, 'THAM_DU', 'DA_XAC_NHAN', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(122, 15, 'THAM_DU', 'DA_XAC_NHAN', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(122, 16, 'THAM_DU', 'DA_XAC_NHAN', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(123, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(124, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(124, 16, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(125, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(125, 16, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(126, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(126, 16, 'THAM_DU', 'DA_XAC_NHAN', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(127, 4, 'CHU_TRI', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-20 22:03:10', NULL, NULL, '2026-11-20 22:03:09.971959', NULL, NULL),
(127, 6, 'THU_KY_LANH_DAO', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-20 22:03:42', NULL, NULL, '2026-11-20 22:03:41.516200', NULL, NULL),
(127, 10, 'THU_KY', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-20 22:04:11', NULL, NULL, '2026-11-20 22:04:10.802008', NULL, NULL),
(127, 14, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(127, 15, 'THU_KY_CUOC_HOP', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-20 22:04:05', NULL, NULL, '2026-11-20 22:04:04.804818', NULL, NULL),
(127, 16, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-20 22:04:31', NULL, NULL, '2026-11-20 22:04:31.254342', NULL, NULL),
(128, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(128, 15, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, '2026-11-20 22:56:03'),
(128, 16, 'THAM_DU', 'DA_XAC_NHAN', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, '2026-11-20 22:56:03'),
(129, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(129, 16, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, '2026-11-21 00:10:08'),
(130, 6, 'THU_KY_LANH_DAO', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-21 14:07:10', NULL, NULL, '2026-11-21 14:07:09.723804', NULL, NULL),
(130, 10, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(130, 11, 'CHU_TRI', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-21 14:07:01', NULL, NULL, '2026-11-21 14:07:00.852511', NULL, NULL),
(130, 13, 'THU_KY_CUOC_HOP', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-21 14:06:56', NULL, NULL, '2026-11-21 14:06:55.659466', NULL, NULL),
(130, 14, 'THAM_DU', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, '2026-11-21 14:06:12'),
(130, 16, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-21 14:07:28', NULL, NULL, '2026-11-21 14:07:28.113728', NULL, '2026-11-21 13:55:43'),
(131, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(132, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(133, 4, 'CHU_TRI', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:31', NULL, NULL, '2026-11-22 22:51:31.018719', '2026-11-22 22:52:40.400522', NULL),
(133, 6, 'THU_KY', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:30', NULL, NULL, '2026-11-22 22:51:29.835278', '2026-11-22 22:52:41.173403', NULL),
(133, 17, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:50', NULL, NULL, '2026-11-22 22:51:50.002381', '2026-11-22 22:52:41.625144', '2026-11-22 22:46:07'),
(133, 18, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:51', NULL, NULL, '2026-11-22 22:51:50.912781', '2026-11-22 22:52:42.425181', '2026-11-22 22:46:07'),
(133, 19, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:59', NULL, NULL, '2026-11-22 22:51:59.342210', '2026-11-22 22:52:43.112963', '2026-11-22 22:46:07'),
(133, 20, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:52:10', NULL, NULL, '2026-11-22 22:52:10.336288', '2026-11-22 22:52:43.716187', '2026-11-22 22:46:07'),
(133, 21, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:52:20', NULL, NULL, '2026-11-22 22:52:19.584458', '2026-11-22 22:52:44.123397', '2026-11-22 22:46:07'),
(133, 22, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:52:35', NULL, NULL, '2026-11-22 22:52:35.154063', '2026-11-22 22:52:45.053508', '2026-11-22 22:46:07'),
(133, 23, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:44', NULL, NULL, '2026-11-22 22:51:44.164022', '2026-11-22 22:52:45.377025', '2026-11-22 22:46:07'),
(133, 24, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:46', NULL, NULL, '2026-11-22 22:51:45.938346', '2026-11-22 22:52:45.901480', '2026-11-22 22:46:07'),
(133, 25, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:47', NULL, NULL, '2026-11-22 22:51:47.426938', '2026-11-22 22:52:46.366077', '2026-11-22 22:46:07'),
(133, 26, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-22 22:51:49', NULL, NULL, '2026-11-22 22:51:48.785637', '2026-11-22 22:52:46.657196', '2026-11-22 22:46:07'),
(134, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL),
(135, 4, 'CHU_TRI', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:27', NULL, NULL, '2026-11-23 00:16:27.344347', '2026-11-23 00:17:02.540616', NULL),
(135, 6, 'THU_KY', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:33', NULL, NULL, '2026-11-23 00:16:33.328434', '2026-11-23 00:17:03.473760', NULL),
(135, 17, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:40', NULL, NULL, '2026-11-23 00:16:39.945667', '2026-11-23 00:17:04.008652', '2026-11-23 00:12:51'),
(135, 18, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:44', NULL, NULL, '2026-11-23 00:16:44.359928', '2026-11-23 00:17:04.421253', '2026-11-23 00:12:51'),
(135, 19, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:46', NULL, NULL, '2026-11-23 00:16:45.857357', '2026-11-23 00:17:05.224885', '2026-11-23 00:12:51'),
(135, 20, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:47', NULL, NULL, '2026-11-23 00:16:47.078048', '2026-11-23 00:17:05.854878', '2026-11-23 00:12:51'),
(135, 21, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:48', NULL, NULL, '2026-11-23 00:16:48.330827', '2026-11-23 00:17:06.820458', '2026-11-23 00:12:51'),
(135, 22, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:50', NULL, NULL, '2026-11-23 00:16:49.910372', '2026-11-23 00:17:07.233676', '2026-11-23 00:12:51'),
(135, 23, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:51', NULL, NULL, '2026-11-23 00:16:51.249060', '2026-11-23 00:17:07.545409', '2026-11-23 00:12:51'),
(135, 24, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:53', NULL, NULL, '2026-11-23 00:16:52.608961', '2026-11-23 00:17:07.904390', '2026-11-23 00:12:51'),
(135, 25, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:54', NULL, NULL, '2026-11-23 00:16:54.326412', '2026-11-23 00:17:08.977785', '2026-11-23 00:12:51'),
(135, 26, 'THAM_DU', 'DA_XAC_NHAN', 'CO_MAT', '2026-11-23 00:16:37', NULL, NULL, '2026-11-23 00:16:37.313335', '2026-11-23 00:17:09.249580', '2026-11-23 00:12:51'),
(136, 6, 'THU_KY', 'CHO_PHAN_HOI', 'CHUA_DIEM_DANH', NULL, NULL, NULL, NULL, NULL, NULL);

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
(5, 1, 'Máy chiếu', 'MAU-TB-01', 1, 'TOT', NULL),
(6, 1, 'Micro không dây', 'MAU-TB-02', 2, 'TOT', NULL),
(7, 2, 'Màn hình hội trường', 'MAU-TB-03', 1, 'TOT', ''),
(8, 3, 'Camera hội nghị', 'MAU-TB-04', 2, 'BAO_TRI', '');

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
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `thong_bao`
--

INSERT INTO `thong_bao` (`thong_bao_id`, `nguoi_dung_id`, `cuoc_hop_id`, `tieu_de`, `noi_dung`, `loai_thong_bao`, `da_doc`, `thoi_gian_doc`, `ngay_tao`) VALUES
(1, 6, 101, 'Đề xuất mới', 'Có đề xuất nội bộ chờ thư ký tiếp nhận.', 'HE_THONG', 0, NULL, '2026-11-12 15:45:26'),
(2, 5, 103, 'Kế hoạch chờ duyệt', 'Có kế hoạch nội bộ chờ trưởng phòng duyệt.', 'HE_THONG', 0, NULL, '2026-11-12 15:45:26'),
(3, 11, 114, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaa', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-16 01:26:48'),
(4, 10, 114, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'aaa', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-16 01:28:23'),
(5, 6, 114, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'aaa', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-16 01:29:10'),
(6, 4, 114, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'aaa', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-16 01:29:10'),
(7, 10, 114, 'Kế hoạch cần được chỉnh sửa', 'ko đc toàn đơn vị phải do lãnh đạo chủ trì và nguyễn văn bảo làm thư kí', 'KE_HOACH_YEU_CAU_CHINH_SUA', 0, NULL, '2026-11-16 01:30:36'),
(8, 11, 114, 'Kế hoạch cần được chỉnh sửa', 'ko đc toàn đơn vị phải do lãnh đạo chủ trì và nguyễn văn bảo làm thư kí', 'KE_HOACH_YEU_CAU_CHINH_SUA', 0, NULL, '2026-11-16 01:30:36'),
(9, 11, 114, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaa', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-16 01:31:57'),
(10, 10, 114, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'aaa', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-16 01:32:20'),
(11, 6, 114, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'aaa', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-16 02:02:52'),
(12, 4, 114, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'aaa', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-16 02:02:52'),
(13, 4, 114, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'aaa', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-16 02:03:28'),
(14, 11, 114, 'Kế hoạch cuộc họp đã được duyệt', 'aaa', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-16 02:03:36'),
(15, 10, 114, 'Kế hoạch cuộc họp đã được duyệt', 'aaa', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-16 02:03:36'),
(16, 13, 114, 'Kế hoạch cuộc họp đã được duyệt', 'aaa', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-16 02:03:36'),
(17, 11, 114, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaa', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-16 02:04:48'),
(18, 11, 115, 'Có kế hoạch cuộc họp cần bạn xem xét', 'họp bàn giao cuốii tuần', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-17 23:03:15'),
(19, 10, 115, 'Kế hoạch cần được chỉnh sửa', 'file kế hoạch đâuu', 'KE_HOACH_YEU_CAU_CHINH_SUA', 0, NULL, '2026-11-17 23:05:00'),
(20, 11, 115, 'Có kế hoạch cuộc họp cần bạn xem xét', 'họp bàn giao cuốii tuần', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-17 23:05:54'),
(21, 10, 115, 'Kế hoạch và lịch họp đã được duyệt', 'họp bàn giao cuốii tuần', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-17 23:06:14'),
(23, 11, 115, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-17 23:18:05'),
(24, 10, 115, 'Biên bản đã được duyệt và chờ thư ký phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-17 23:19:28'),
(25, 1, 115, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-17 23:20:19'),
(26, 8, 115, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-17 23:20:19'),
(27, 12, 115, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-17 23:20:19'),
(28, 13, 115, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-17 23:20:19'),
(29, 14, 115, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-17 23:20:19'),
(30, 13, 114, 'Kế hoạch cần được chỉnh sửa', 'Chọn lịch đề xuất còn hiệu lực và phù hợp với hình thức họp', 'KE_HOACH_YEU_CAU_CHINH_SUA', 0, NULL, '2026-11-17 23:43:57'),
(31, 11, 116, 'Có kế hoạch cuộc họp cần bạn xem xét', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-17 23:48:41'),
(32, 10, 116, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-17 23:52:27'),
(33, 6, 116, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-17 23:53:10'),
(34, 4, 116, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-17 23:53:10'),
(35, 4, 116, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-17 23:54:28'),
(36, 11, 116, 'Kế hoạch và lịch họp đã được duyệt', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-17 23:54:50'),
(37, 10, 116, 'Kế hoạch và lịch họp đã được duyệt', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-17 23:54:50'),
(38, 13, 116, 'Kế hoạch và lịch họp đã được duyệt', 'họp bàn giao cuốii tuần lần 2', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-17 23:54:50'),
(39, 6, 105, 'Biên bản đã được duyệt và chờ thư ký phát hành', 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 00:00:04'),
(42, 4, 116, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 00:48:24'),
(43, 10, 116, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 00:53:00'),
(44, 1, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(45, 6, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(46, 8, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(47, 9, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(48, 10, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(49, 11, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(50, 12, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(51, 14, 116, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần lần 2', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:53:02'),
(52, 4, 116, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 00:55:07'),
(53, 13, 116, 'Biên bản cần được chỉnh sửa theo ý kiến duyệt', 'aaa', 'BIEN_BAN_TRA_LAI', 0, NULL, '2026-11-18 00:55:46'),
(54, 4, 116, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 00:55:59'),
(55, 13, 116, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 00:56:08'),
(56, 1, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(57, 6, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(58, 8, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(59, 9, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(60, 10, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(61, 11, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(62, 12, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(63, 14, 116, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: họp bàn giao cuốii tuần lần 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 00:56:10'),
(64, 4, 117, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Họp triển khai  dự án lần 1', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 02:00:53'),
(65, 6, 117, 'Kế hoạch và lịch họp đã được duyệt', 'Họp triển khai  dự án lần 1', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 02:02:09'),
(66, 4, 117, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 02:21:58'),
(67, 6, 117, 'Biên bản cần được chỉnh sửa theo ý kiến duyệt', 'chưa đc làm lại đi  bucoocj phải có ai', 'BIEN_BAN_TRA_LAI', 0, NULL, '2026-11-18 02:22:42'),
(68, 4, 117, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 02:25:00'),
(69, 6, 117, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 02:25:24'),
(70, 5, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(71, 6, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(72, 9, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(73, 10, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(74, 11, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(75, 12, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(76, 14, 117, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai  dự án lần 1', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:25:39'),
(77, 4, 117, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 02:26:19'),
(78, 6, 117, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 02:26:26'),
(79, 5, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(80, 6, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(81, 9, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(82, 10, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(83, 11, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(84, 12, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(85, 14, 117, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai  dự án lần 1', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 02:26:27'),
(86, 1, 105, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:30:45'),
(87, 6, 105, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:30:45'),
(88, 8, 105, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:30:45'),
(89, 13, 105, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:30:45'),
(90, 14, 105, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp toàn cơ quan chuẩn bị lập lịch', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:30:45'),
(91, 10, 110, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch laafn 2', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:35:51'),
(92, 11, 115, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 15:36:26'),
(93, 10, 115, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 15:36:34'),
(94, 1, 115, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:36:35'),
(95, 8, 115, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:36:35'),
(96, 10, 115, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:36:35'),
(97, 12, 115, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:36:35'),
(98, 13, 115, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:36:35'),
(99, 14, 115, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: họp bàn giao cuốii tuần', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:36:35'),
(100, 11, 114, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaa', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 15:38:28'),
(101, 10, 114, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'aaa', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-18 15:42:06'),
(102, 6, 114, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'aaa', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-18 15:42:35'),
(103, 4, 114, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'aaa', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-18 15:42:35'),
(104, 4, 114, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'aaa', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-18 15:43:03'),
(105, 11, 114, 'Kế hoạch và lịch họp đã được duyệt', 'aaa', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 15:43:21'),
(106, 10, 114, 'Kế hoạch và lịch họp đã được duyệt', 'aaa', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 15:43:21'),
(107, 11, 114, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 15:50:39'),
(108, 10, 114, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 15:50:54'),
(109, 5, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(110, 6, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(111, 8, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(112, 10, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(113, 12, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(114, 13, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(115, 14, 114, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: aaa', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:50:55'),
(116, 11, 114, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: aaa', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 15:51:19'),
(117, 10, 114, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 15:51:28'),
(118, 5, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(119, 6, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(120, 8, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(121, 10, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(122, 12, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(123, 13, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(124, 14, 114, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: aaa', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 15:51:30'),
(125, 11, 113, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 16:12:23'),
(126, 12, 113, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-18 16:15:09'),
(127, 6, 113, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-18 16:33:08'),
(128, 4, 113, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-18 16:33:08'),
(129, 4, 113, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-18 16:34:05'),
(130, 11, 113, 'Kế hoạch và lịch họp đã được duyệt', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 16:34:24'),
(131, 12, 113, 'Kế hoạch và lịch họp đã được duyệt', 'Họp triển khai kế hoạch lần 5', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 16:34:24'),
(132, 11, 113, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 16:39:30'),
(133, 12, 113, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 16:39:52'),
(134, 6, 113, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:39:54'),
(135, 8, 113, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:39:54'),
(136, 10, 113, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:39:54'),
(137, 13, 113, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:39:54'),
(138, 14, 113, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: Họp triển khai kế hoạch lần 5', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:39:54'),
(139, 11, 113, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 16:40:19'),
(140, 12, 113, 'Kết luận cần được chỉnh sửa', 'sssss', 'KET_LUAN_TRA_LAI', 0, NULL, '2026-11-18 16:40:28'),
(141, 11, 113, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 16:40:35'),
(142, 10, 113, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 16:40:41'),
(143, 6, 113, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:40:42'),
(144, 8, 113, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:40:42'),
(145, 10, 113, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:40:42'),
(146, 13, 113, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:40:42'),
(147, 14, 113, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: Họp triển khai kế hoạch lần 5', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 16:40:42'),
(148, 11, 118, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 16:57:10'),
(149, 10, 118, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-18 16:57:42'),
(150, 6, 118, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-18 16:58:11'),
(151, 4, 118, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-18 16:58:11'),
(152, 4, 118, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-18 16:58:53'),
(153, 11, 118, 'Kế hoạch và lịch họp đã được duyệt', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 16:59:23'),
(154, 10, 118, 'Kế hoạch và lịch họp đã được duyệt', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 16:59:23'),
(155, 5, 118, 'Kế hoạch và lịch họp đã được duyệt', 'test thử cấp 2 và tham gia cuộc họp', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 16:59:23'),
(156, 4, 118, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 17:19:35'),
(157, 4, 118, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 17:20:13'),
(158, 5, 118, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 17:20:20'),
(159, 10, 118, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 17:20:23'),
(160, 6, 118, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:10'),
(161, 10, 118, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:10'),
(162, 13, 118, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:10'),
(163, 14, 118, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:10'),
(164, 6, 118, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:12'),
(165, 10, 118, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:12'),
(166, 13, 118, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:12'),
(167, 14, 118, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test thử cấp 2 và tham gia cuộc họp', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 17:21:12'),
(168, 11, 119, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test họp nội bộ', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 20:10:47'),
(169, 10, 119, 'Kế hoạch và lịch họp đã được duyệt', 'test họp nội bộ', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 20:11:02'),
(170, 11, 119, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: test họp nội bộ', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 20:24:03'),
(171, 10, 119, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: test họp nội bộ', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 20:24:14'),
(172, 11, 119, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: test họp nội bộ', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 20:24:29'),
(173, 10, 119, 'Kết luận cần được chỉnh sửa', 'aaaaaaa', 'KET_LUAN_TRA_LAI', 0, NULL, '2026-11-18 20:24:44'),
(174, 11, 119, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: test họp nội bộ', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 20:25:03'),
(175, 11, 120, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test websocket', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 20:49:59'),
(176, 10, 120, 'Kế hoạch và lịch họp đã được duyệt', 'test websocket', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 20:50:17'),
(177, 10, 119, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: test họp nội bộ', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 20:51:50'),
(178, 11, 120, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: test websocket', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-18 21:11:00'),
(179, 10, 120, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: test websocket', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-18 21:11:17'),
(180, 11, 120, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 21:12:07'),
(181, 10, 120, 'Kết luận cần được chỉnh sửa', 'oke chỉnh lại thử xem', 'KET_LUAN_TRA_LAI', 0, NULL, '2026-11-18 21:12:23'),
(182, 11, 120, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-18 21:12:42'),
(183, 10, 120, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-18 21:12:55'),
(184, 8, 120, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:12:57'),
(185, 10, 120, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:12:57'),
(186, 13, 120, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:12:57'),
(187, 14, 120, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test websocket', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:12:57'),
(188, 8, 120, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test websocket', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:13:25'),
(189, 10, 120, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test websocket', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:13:25'),
(190, 13, 120, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test websocket', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:13:25'),
(191, 14, 120, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test websocket', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-18 21:13:25'),
(192, 11, 122, 'Có kế hoạch cuộc họp cần bạn xem xét', 'kiểm tra gmail', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 23:01:53'),
(193, 10, 122, 'Kế hoạch cần được chỉnh sửa', 'đã duyệt cho kiểm tra mail', 'KE_HOACH_YEU_CAU_CHINH_SUA', 0, NULL, '2026-11-18 23:02:13'),
(194, 11, 122, 'Có kế hoạch cuộc họp cần bạn xem xét', 'kiểm tra gmail', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 23:02:39'),
(195, 10, 122, 'Kế hoạch và lịch họp đã được duyệt', 'kiểm tra gmail', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 23:02:58'),
(196, 11, 124, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaaaaaaaaaaaaa test mail', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 23:37:55'),
(197, 10, 124, 'Kế hoạch và lịch họp đã được duyệt', 'aaaaaaaaaaaaaa test mail', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 23:38:12'),
(198, 11, 125, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaaaaa tetss', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 23:48:52'),
(199, 10, 125, 'Kế hoạch và lịch họp đã được duyệt', 'aaaaaa tetss', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 23:49:03'),
(200, 11, 126, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test hủy cuộc họp', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-18 23:53:51'),
(201, 10, 126, 'Kế hoạch và lịch họp đã được duyệt', 'test hủy cuộc họp', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-18 23:54:00'),
(202, 11, 125, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaaaaa tetss', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-19 00:07:30'),
(203, 10, 125, 'Kế hoạch và lịch họp đã được duyệt', 'aaaaaa tetss', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-19 00:07:57'),
(204, 11, 127, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test khách mời ngoài', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-20 15:24:33'),
(205, 10, 127, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'test khách mời ngoài', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-20 15:41:39'),
(206, 6, 127, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test khách mời ngoài', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-20 15:41:58'),
(207, 4, 127, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test khách mời ngoài', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-20 15:41:58'),
(208, 4, 127, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'test khách mời ngoài', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-20 15:42:21'),
(209, 11, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 15:42:47'),
(210, 10, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 15:42:47'),
(211, 15, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 15:42:47'),
(212, 11, 127, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test khách mời ngoài', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-20 15:50:13'),
(213, 15, 127, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'test khách mời ngoài', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-20 15:51:34'),
(214, 11, 127, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test khách mời ngoài', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-20 21:22:39'),
(215, 10, 127, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'test khách mời ngoài', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-20 21:23:12'),
(216, 6, 127, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test khách mời ngoài', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-20 21:23:59'),
(217, 4, 127, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test khách mời ngoài', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-20 21:23:59'),
(218, 4, 127, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'test khách mời ngoài', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-20 21:24:39'),
(219, 11, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 21:24:56'),
(220, 10, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 21:24:56'),
(221, 15, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 21:24:56'),
(222, 11, 127, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test khách mời ngoài', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-20 21:26:11'),
(223, 10, 127, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'test khách mời ngoài', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-20 21:26:25'),
(224, 15, 127, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'test khách mời ngoài', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-20 21:26:25'),
(225, 6, 127, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test khách mời ngoài', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-20 21:27:03'),
(226, 4, 127, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'test khách mời ngoài', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-20 21:27:03'),
(227, 4, 127, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'test khách mời ngoài', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-20 21:27:17'),
(228, 11, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 21:27:22'),
(229, 10, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 21:27:22'),
(230, 15, 127, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mời ngoài', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 21:27:22'),
(231, 4, 127, 'Biên bản cuộc họp cần được duyệt', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_CHO_DUYET', 0, NULL, '2026-11-20 22:06:43'),
(232, 15, 127, 'Biên bản đã được duyệt và chờ chủ trì phát hành', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_DA_DUYET', 0, NULL, '2026-11-20 22:06:53'),
(233, 6, 127, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:06:54'),
(234, 10, 127, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:06:54'),
(235, 14, 127, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:06:54'),
(236, 15, 127, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:06:54'),
(237, 16, 127, 'Biên bản cuộc họp đã được phát hành', 'Biên bản cuộc họp: test khách mời ngoài', 'BIEN_BAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:06:54'),
(238, 4, 127, 'Kết luận cuộc họp cần được duyệt', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_CHO_DUYET', 0, NULL, '2026-11-20 22:07:09'),
(239, 15, 127, 'Kết luận đã được duyệt và chờ chủ trì phát hành', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_DA_DUYET', 0, NULL, '2026-11-20 22:07:13'),
(240, 6, 127, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:07:14'),
(241, 10, 127, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:07:14'),
(242, 14, 127, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:07:14'),
(243, 15, 127, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:07:14'),
(244, 16, 127, 'Kết luận cuộc họp đã được phát hành', 'Kết luận cuộc họp: test khách mời ngoài', 'KET_LUAN_DA_PHAT_HANH', 0, NULL, '2026-11-20 22:07:14'),
(245, 4, 128, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test khách mail', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-20 22:55:09'),
(246, 6, 128, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mail', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 22:55:22'),
(247, 4, 128, 'Có kế hoạch cuộc họp cần bạn xem xét', 'test khách mail', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-20 23:46:39'),
(248, 6, 128, 'Kế hoạch và lịch họp đã được duyệt', 'test khách mail', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-20 23:46:47'),
(249, 4, 129, 'Có kế hoạch cuộc họp cần bạn xem xét', 'ấccs', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-21 00:08:49'),
(250, 6, 129, 'Kế hoạch và lịch họp đã được duyệt', 'ấccs', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-21 00:09:23'),
(251, 11, 130, 'Có kế hoạch cuộc họp cần bạn xem xét', 'ggg', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-21 13:52:53'),
(252, 10, 130, 'Trưởng phòng đã đồng ý trình kế hoạch lên cấp trên', 'ggg', 'KE_HOACH_DONG_Y_CAP_TREN', 0, NULL, '2026-11-21 13:53:13'),
(253, 6, 130, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'ggg', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-21 13:53:42'),
(254, 4, 130, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'ggg', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-21 13:53:42'),
(255, 11, 130, 'Hồ sơ cuộc họp đang chờ thư ký lãnh đạo tiếp nhận', 'ggg', 'KE_HOACH_CHUYEN_CAP_TREN', 0, NULL, '2026-11-21 13:53:42'),
(256, 11, 130, 'Thư ký lãnh đạo đã tiếp nhận hồ sơ', 'ggg', 'KE_HOACH_DA_TIEP_NHAN', 0, NULL, '2026-11-21 13:54:09'),
(257, 10, 130, 'Thư ký lãnh đạo đã tiếp nhận hồ sơ', 'ggg', 'KE_HOACH_DA_TIEP_NHAN', 0, NULL, '2026-11-21 13:54:09'),
(258, 4, 130, 'Thư ký lãnh đạo đã tiếp nhận hồ sơ', 'ggg', 'KE_HOACH_DA_TIEP_NHAN', 0, NULL, '2026-11-21 13:54:09'),
(259, 4, 130, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'ggg', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-21 13:54:12'),
(260, 10, 130, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'ggg', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-21 13:54:12'),
(261, 11, 130, 'Kế hoạch cuộc họp đang chờ lãnh đạo phê duyệt', 'ggg', 'KE_HOACH_CHO_LANH_DAO', 0, NULL, '2026-11-21 13:54:12'),
(262, 11, 130, 'Kế hoạch và lịch họp đã được duyệt', 'ggg', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-21 13:54:28'),
(263, 10, 130, 'Kế hoạch và lịch họp đã được duyệt', 'ggg', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-21 13:54:28'),
(264, 13, 130, 'Kế hoạch và lịch họp đã được duyệt', 'ggg', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-21 13:54:28'),
(265, 6, 130, 'Kế hoạch và lịch họp đã được duyệt', 'ggg', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-21 13:54:28'),
(266, 4, 132, 'Có kế hoạch cuộc họp cần bạn xem xét', 'aaaa', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-21 14:00:43'),
(267, 6, 132, 'Kế hoạch và lịch họp đã được duyệt', 'aaaa', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-21 14:00:48'),
(268, 11, 130, 'Cuộc họp đã bắt đầu', 'ggg', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-21 14:06:22'),
(269, 6, 130, 'Cuộc họp đã bắt đầu', 'ggg', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-21 14:06:22'),
(270, 10, 130, 'Cuộc họp đã bắt đầu', 'ggg', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-21 14:06:22'),
(271, 14, 130, 'Cuộc họp đã bắt đầu', 'ggg', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-21 14:06:22'),
(272, 16, 130, 'Cuộc họp đã bắt đầu', 'ggg', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-21 14:06:22'),
(273, 4, 133, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Họp triển khai kế hoạch test bầu chọn', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-22 22:45:36'),
(274, 6, 133, 'Kế hoạch và lịch họp đã được duyệt', 'Họp triển khai kế hoạch test bầu chọn', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-22 22:45:49'),
(275, 6, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(276, 17, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(277, 18, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(278, 19, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(279, 20, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(280, 21, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(281, 22, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(282, 23, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(283, 24, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(284, 25, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(285, 26, 133, 'Cuộc họp đã bắt đầu', 'Họp triển khai kế hoạch test bầu chọn', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-22 22:51:21'),
(286, 4, 134, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-22 23:12:52'),
(287, 6, 134, 'Kế hoạch và lịch họp đã được duyệt', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-22 23:12:56'),
(288, 4, 134, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-22 23:15:53'),
(289, 6, 134, 'Kế hoạch và lịch họp đã được duyệt', 'Họp triển khai kế hoạch thông tin mail và dòi lịch', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-22 23:16:07'),
(290, 4, 135, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-23 00:12:15'),
(291, 6, 135, 'Kế hoạch và lịch họp đã được duyệt', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-23 00:12:25'),
(292, 6, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(293, 17, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(294, 18, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(295, 19, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(296, 20, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(297, 21, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(298, 22, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(299, 23, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(300, 24, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(301, 25, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(302, 26, 135, 'Cuộc họp đã bắt đầu', 'Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.', 'CUOC_HOP_BAT_DAU', 0, NULL, '2026-11-23 00:16:11'),
(303, 4, 136, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-23 00:47:29'),
(304, 6, 136, 'Kế hoạch và lịch họp đã được duyệt', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-23 00:47:35'),
(305, 4, 136, 'Có kế hoạch cuộc họp cần bạn xem xét', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'KE_HOACH_CHO_DUYET', 0, NULL, '2026-11-23 00:49:32'),
(306, 6, 136, 'Kế hoạch và lịch họp đã được duyệt', 'Tiếp tục test luồng 3 — Khách mời và cập nhật lịch', 'KE_HOACH_DA_DUYET', 0, NULL, '2026-11-23 00:49:37');

-- --------------------------------------------------------

--
-- Table structure for table `ung_vien_vong_bau`
--

DROP TABLE IF EXISTS `ung_vien_vong_bau`;
CREATE TABLE IF NOT EXISTS `ung_vien_vong_bau` (
  `vong_bau_id` int NOT NULL,
  `nguoi_dung_id` int NOT NULL,
  `ho_ten` varchar(255) NOT NULL,
  `so_luot_chon` int DEFAULT NULL,
  `ket_qua` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`vong_bau_id`,`nguoi_dung_id`),
  KEY `nguoi_dung_id` (`nguoi_dung_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `ung_vien_vong_bau`
--

INSERT INTO `ung_vien_vong_bau` (`vong_bau_id`, `nguoi_dung_id`, `ho_ten`, `so_luot_chon`, `ket_qua`) VALUES
(1, 17, 'Cán bộ kiểm thử 01', NULL, NULL),
(1, 18, 'Cán bộ kiểm thử 02', NULL, NULL),
(1, 19, 'Cán bộ kiểm thử 03', NULL, NULL),
(1, 20, 'Cán bộ kiểm thử 04', NULL, NULL),
(1, 21, 'Cán bộ kiểm thử 05', NULL, NULL),
(1, 22, 'Cán bộ kiểm thử 06', NULL, NULL),
(1, 23, 'Cán bộ kiểm thử 07', NULL, NULL),
(1, 24, 'Cán bộ kiểm thử 08', NULL, NULL),
(1, 25, 'Cán bộ kiểm thử 09', NULL, NULL),
(1, 26, 'Cán bộ kiểm thử 10', NULL, NULL),
(2, 17, 'Cán bộ kiểm thử 01', 5, 'DONG_PHIEU'),
(2, 18, 'Cán bộ kiểm thử 02', 3, 'LOAI'),
(2, 19, 'Cán bộ kiểm thử 03', 5, 'DONG_PHIEU'),
(2, 20, 'Cán bộ kiểm thử 04', 6, 'DUOC_CHON'),
(2, 21, 'Cán bộ kiểm thử 05', 8, 'DUOC_CHON'),
(2, 22, 'Cán bộ kiểm thử 06', 3, 'LOAI'),
(2, 23, 'Cán bộ kiểm thử 07', 8, 'DUOC_CHON'),
(2, 24, 'Cán bộ kiểm thử 08', 3, 'LOAI'),
(2, 25, 'Cán bộ kiểm thử 09', 4, 'LOAI'),
(2, 26, 'Cán bộ kiểm thử 10', 5, 'DONG_PHIEU'),
(3, 17, 'Cán bộ kiểm thử 01', NULL, NULL),
(3, 19, 'Cán bộ kiểm thử 03', NULL, NULL),
(3, 26, 'Cán bộ kiểm thử 10', NULL, NULL),
(4, 17, 'Cán bộ kiểm thử 01', 5, 'DUOC_CHON'),
(4, 19, 'Cán bộ kiểm thử 03', 4, 'LOAI'),
(4, 20, 'Cán bộ kiểm thử 04', 5, 'DUOC_CHON'),
(4, 21, 'Cán bộ kiểm thử 05', 5, 'DUOC_CHON'),
(4, 23, 'Cán bộ kiểm thử 07', 6, 'DUOC_CHON'),
(4, 26, 'Cán bộ kiểm thử 10', 5, 'DUOC_CHON'),
(5, 17, 'Cán bộ kiểm thử 01', NULL, NULL),
(5, 20, 'Cán bộ kiểm thử 04', NULL, NULL),
(5, 21, 'Cán bộ kiểm thử 05', NULL, NULL),
(5, 23, 'Cán bộ kiểm thử 07', NULL, NULL),
(5, 26, 'Cán bộ kiểm thử 10', NULL, NULL),
(6, 17, 'Cán bộ kiểm thử 01', 3, 'LOAI'),
(6, 18, 'Cán bộ kiểm thử 02', 3, 'LOAI'),
(6, 19, 'Cán bộ kiểm thử 03', 3, 'LOAI'),
(6, 20, 'Cán bộ kiểm thử 04', 3, 'LOAI'),
(6, 21, 'Cán bộ kiểm thử 05', 3, 'LOAI'),
(6, 22, 'Cán bộ kiểm thử 06', 3, 'LOAI'),
(6, 23, 'Cán bộ kiểm thử 07', 3, 'DUOC_CHON'),
(6, 24, 'Cán bộ kiểm thử 08', 3, 'LOAI'),
(6, 25, 'Cán bộ kiểm thử 09', 3, 'LOAI'),
(6, 26, 'Cán bộ kiểm thử 10', 3, 'LOAI'),
(7, 17, 'Cán bộ kiểm thử 01', 1, 'LOAI'),
(7, 18, 'Cán bộ kiểm thử 02', 0, 'LOAI'),
(7, 19, 'Cán bộ kiểm thử 03', 1, 'LOAI'),
(7, 20, 'Cán bộ kiểm thử 04', 0, 'LOAI'),
(7, 21, 'Cán bộ kiểm thử 05', 1, 'LOAI'),
(7, 22, 'Cán bộ kiểm thử 06', 1, 'LOAI'),
(7, 23, 'Cán bộ kiểm thử 07', 3, 'DUOC_CHON'),
(7, 24, 'Cán bộ kiểm thử 08', 2, 'LOAI'),
(7, 25, 'Cán bộ kiểm thử 09', 1, 'LOAI'),
(7, 26, 'Cán bộ kiểm thử 10', 0, 'LOAI'),
(8, 17, 'Cán bộ kiểm thử 01', 1, 'LOAI'),
(8, 18, 'Cán bộ kiểm thử 02', 2, 'LOAI'),
(8, 19, 'Cán bộ kiểm thử 03', 2, 'LOAI'),
(8, 20, 'Cán bộ kiểm thử 04', 3, 'LOAI'),
(8, 21, 'Cán bộ kiểm thử 05', 3, 'LOAI'),
(8, 22, 'Cán bộ kiểm thử 06', 2, 'LOAI'),
(8, 23, 'Cán bộ kiểm thử 07', 4, 'DUOC_CHON'),
(8, 24, 'Cán bộ kiểm thử 08', 0, 'LOAI'),
(8, 25, 'Cán bộ kiểm thử 09', 1, 'LOAI'),
(8, 26, 'Cán bộ kiểm thử 10', 2, 'LOAI'),
(9, 4, 'Nguyen Van Test Updated', 2, 'LOAI'),
(9, 6, 'Nguyễn Thị Lan', 1, 'LOAI'),
(9, 17, 'Cán bộ kiểm thử 01', 0, 'LOAI'),
(9, 18, 'Cán bộ kiểm thử 02', 2, 'LOAI'),
(9, 19, 'Cán bộ kiểm thử 03', 4, 'DUOC_CHON'),
(9, 20, 'Cán bộ kiểm thử 04', 1, 'LOAI'),
(9, 21, 'Cán bộ kiểm thử 05', 3, 'LOAI'),
(9, 22, 'Cán bộ kiểm thử 06', 4, 'LOAI'),
(9, 23, 'Cán bộ kiểm thử 07', 3, 'LOAI'),
(9, 24, 'Cán bộ kiểm thử 08', 2, 'LOAI'),
(9, 25, 'Cán bộ kiểm thử 09', 4, 'DUOC_CHON'),
(9, 26, 'Cán bộ kiểm thử 10', 4, 'DUOC_CHON'),
(10, 19, 'Cán bộ kiểm thử 03', 7, 'DUOC_CHON'),
(10, 22, 'Cán bộ kiểm thử 06', 7, 'LOAI'),
(10, 25, 'Cán bộ kiểm thử 09', 9, 'DUOC_CHON'),
(10, 26, 'Cán bộ kiểm thử 10', 7, 'DUOC_CHON'),
(11, 19, 'Cán bộ kiểm thử 03', 9, 'DUOC_CHON'),
(11, 22, 'Cán bộ kiểm thử 06', 3, 'LOAI'),
(11, 26, 'Cán bộ kiểm thử 10', 8, 'DUOC_CHON');

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
-- Table structure for table `vong_bau`
--

DROP TABLE IF EXISTS `vong_bau`;
CREATE TABLE IF NOT EXISTS `vong_bau` (
  `vong_bau_id` int NOT NULL AUTO_INCREMENT,
  `cuoc_bau_id` int NOT NULL,
  `vong_truoc_id` int DEFAULT NULL,
  `so_vong` int NOT NULL,
  `so_lua_chon_moi_phieu` int NOT NULL,
  `so_nguoi_duoc_chon` int NOT NULL,
  `trang_thai` varchar(25) NOT NULL DEFAULT 'NHAP',
  PRIMARY KEY (`vong_bau_id`),
  UNIQUE KEY `vong_truoc_id` (`vong_truoc_id`,`so_vong`),
  KEY `cuoc_bau_id` (`cuoc_bau_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `vong_bau`
--

INSERT INTO `vong_bau` (`vong_bau_id`, `cuoc_bau_id`, `vong_truoc_id`, `so_vong`, `so_lua_chon_moi_phieu`, `so_nguoi_duoc_chon`, `trang_thai`) VALUES
(1, 1, NULL, 1, 5, 5, 'DA_HUY'),
(2, 2, NULL, 1, 5, 5, 'CHO_PHAN_DINH'),
(3, 2, 2, 1, 2, 2, 'NHAP'),
(4, 3, NULL, 1, 5, 5, 'DA_CHOT'),
(5, 3, 4, 2, 3, 3, 'NHAP'),
(6, 4, NULL, 1, 3, 1, 'DA_CHOT'),
(7, 4, 6, 1, 1, 1, 'DA_CHOT'),
(8, 5, NULL, 1, 2, 1, 'DA_CHOT'),
(9, 6, NULL, 1, 3, 3, 'DA_CHOT'),
(10, 6, 9, 1, 3, 3, 'DA_CHOT'),
(11, 6, 10, 1, 2, 2, 'DA_CHOT');

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
) ;

--
-- Dumping data for table `yeu_cau_phe_duyet`
--

INSERT INTO `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`, `loai_yeu_cau`, `cuoc_hop_id`, `lich_hop_id`, `phien_ban_id`, `nguoi_trinh_id`, `thoi_gian_trinh`, `trang_thai`, `ghi_chu_trinh`, `nguoi_duyet_id`, `thu_ky_tiep_nhan_id`, `cap_phe_duyet`, `thu_ky_ban_giao_id`, `ke_hoach_da_trinh`, `yeu_cau_truoc_id`) VALUES
(201, 'KE_HOACH_CUOC_HOP', 103, NULL, NULL, 6, '2026-11-06 15:45:26', 'DA_DUYET', NULL, 5, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(202, 'KE_HOACH_CUOC_HOP', 104, NULL, NULL, 6, '2026-11-06 15:45:26', 'DA_DUYET', NULL, 4, NULL, 'LANH_DAO', NULL, NULL, NULL),
(203, 'KE_HOACH_CUOC_HOP', 105, NULL, NULL, 6, '2026-11-06 15:45:26', 'DA_DUYET', NULL, 4, NULL, 'LANH_DAO', NULL, NULL, NULL),
(204, 'KE_HOACH_CUOC_HOP', 106, NULL, NULL, 6, '2026-11-06 15:45:26', 'DA_DUYET', NULL, 4, NULL, 'LANH_DAO', NULL, NULL, NULL),
(205, 'KE_HOACH_CUOC_HOP', 107, NULL, NULL, 6, '2026-11-06 15:45:26', 'DA_DUYET', NULL, 5, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(206, 'KE_HOACH_CUOC_HOP', 108, NULL, NULL, 6, '2026-11-06 15:45:26', 'DA_DUYET', NULL, 8, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(207, 'BIEN_BAN', 103, NULL, 503, 6, '2026-11-12 17:10:16', 'DA_DUYET', 'hihi', 5, NULL, NULL, NULL, NULL, NULL),
(208, 'KE_HOACH_CUOC_HOP', 109, NULL, NULL, 10, '2026-11-13 23:26:20', 'TRA_LAI', '11', 11, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(209, 'KE_HOACH_CUOC_HOP', 109, NULL, NULL, 10, '2026-11-13 23:27:45', 'DA_DUYET', 'cố gắng thêm', 11, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(210, 'KE_HOACH_CUOC_HOP', 110, NULL, NULL, 10, '2026-11-13 23:52:48', 'DA_DUYET', 'hahaat test', 11, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(211, 'BIEN_BAN', 110, NULL, 504, 10, '2026-11-14 00:06:26', 'TRA_LAI', 'test01', 11, NULL, NULL, NULL, NULL, NULL),
(212, 'BIEN_BAN', 110, NULL, 504, 10, '2026-11-14 00:07:41', 'TRA_LAI', 'hihi', 11, NULL, NULL, NULL, NULL, NULL),
(213, 'BIEN_BAN', 110, NULL, 504, 10, '2026-11-14 00:08:56', 'DA_DUYET', NULL, 11, NULL, NULL, NULL, NULL, NULL),
(214, 'KE_HOACH_CUOC_HOP', 111, NULL, NULL, 10, '2026-11-14 00:20:35', 'TRA_LAI', 'đã tải lên kế hoạch', 11, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(215, 'KE_HOACH_CUOC_HOP', 111, NULL, NULL, 10, '2026-11-14 00:21:51', 'DA_DUYET', 'thêm 2 file lý do quá nhỏ chưa nổi bật', 11, NULL, 'TRUONG_PHONG', NULL, NULL, NULL),
(216, 'BIEN_BAN', 111, NULL, 505, 10, '2026-11-14 00:40:16', 'TRA_LAI', 'abcdefg', 11, NULL, NULL, NULL, NULL, NULL),
(217, 'BIEN_BAN', 111, NULL, 505, 10, '2026-11-14 00:41:15', 'DA_DUYET', 'ko có file', 11, NULL, NULL, NULL, NULL, NULL),
(218, 'KE_HOACH_CUOC_HOP', 112, NULL, NULL, 10, '2026-11-14 11:32:05', 'DA_DUYET', 'hiha', 4, 6, 'LANH_DAO', NULL, NULL, NULL),
(219, 'KE_HOACH_CUOC_HOP', 113, 314, NULL, 10, '2026-11-14 13:16:20', 'TRA_LAI', 'aaaaaaaaaaaaa', 4, 6, 'LANH_DAO', 12, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":113,\"tieuDe\":\"Họp triển khai kế hoạch lần 5\",\"mucTieu\":\"aaaaaaa\",\"noiDungDuKien\":\"ccccccccc\",\"loaiHinh\":\"TRUC_TUYEN\",\"phamViCuocHop\":\"LIEN_PHONG_BAN\",\"lichHopId\":314,\"thoiGianBatDau\":\"2026-11-14T13:17:00\",\"thoiGianKetThuc\":\"2026-11-14T14:16:00\",\"ghiChuTrinh\":\"aaaaaaaaaaaaa\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Trần Ngọc Hân\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":null,\"viTriPhongHop\":null,\"quyetDinh\":null}', NULL),
(220, 'KE_HOACH_CUOC_HOP', 113, 315, NULL, 10, '2026-11-14 13:18:06', 'DA_DUYET', 'aaaaaaaaaaaaaaaaaa', 4, 6, 'LANH_DAO', 12, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":113,\"tieuDe\":\"Họp triển khai kế hoạch lần 5\",\"mucTieu\":\"aaaaaaa\",\"noiDungDuKien\":\"ccccccccc\",\"loaiHinh\":\"TRUC_TUYEN\",\"phamViCuocHop\":\"LIEN_PHONG_BAN\",\"lichHopId\":315,\"thoiGianBatDau\":\"2026-11-14T13:20:00\",\"thoiGianKetThuc\":\"2026-11-14T15:17:00\",\"ghiChuTrinh\":\"aaaaaaaaaaaaaaaaaa\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Trần Ngọc Hân\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":null,\"viTriPhongHop\":null,\"quyetDinh\":null}', NULL),
(221, 'KE_HOACH_CUOC_HOP', 114, 316, NULL, 10, '2026-11-14 15:16:16', 'TRA_LAI', 'bbb', 4, 6, 'LANH_DAO', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":316,\"thoiGianBatDau\":\"2026-11-14T16:13:00\",\"thoiGianKetThuc\":\"2026-11-14T17:13:00\",\"ghiChuTrinh\":\"bbb\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null}', NULL),
(222, 'KE_HOACH_CUOC_HOP', 114, 316, NULL, 10, '2026-11-14 15:19:05', 'DA_DUYET', 'tinhf lan 1', 4, 6, 'LANH_DAO', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":316,\"thoiGianBatDau\":\"2026-11-14T16:13:00\",\"thoiGianKetThuc\":\"2026-11-14T17:13:00\",\"ghiChuTrinh\":\"tinhf lan 1\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null}', NULL),
(223, 'BIEN_BAN', 105, NULL, 506, 6, '2026-11-14 15:27:18', 'DA_DUYET', NULL, 4, NULL, NULL, NULL, NULL, NULL),
(224, 'KE_HOACH_CUOC_HOP', 114, 317, NULL, 10, '2026-11-16 01:26:49', 'DA_DUYET', 'do lịch cũ đã qua , hnay xin phép bổ sung cạap nhật ngày họp mới', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":317,\"thoiGianBatDau\":\"2026-11-16T08:00:00\",\"thoiGianKetThuc\":\"2026-11-16T09:00:00\",\"ghiChuTrinh\":\"do lịch cũ đã qua , hnay xin phép bổ sung cạap nhật ngày họp mới\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":222}', 222),
(225, 'KE_HOACH_CUOC_HOP', 114, 317, NULL, 10, '2026-11-16 01:29:11', 'TRA_LAI', 'xin phép đc tổ chức cuộc họp toàn đon vị do bảo chủ trì', 4, 6, 'LANH_DAO', 10, '{\"yeuCauPheDuyetId\":225,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":317,\"thoiGianBatDau\":\"2026-11-16T08:00:00\",\"thoiGianKetThuc\":\"2026-11-16T09:00:00\",\"ghiChuTrinh\":\"xin phép đc tổ chức cuộc họp toàn đon vị do bảo chủ trì\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":224}', 224),
(226, 'KE_HOACH_CUOC_HOP', 114, 317, NULL, 10, '2026-11-16 01:31:57', 'DA_DUYET', 'xin phép cập nhật lại thư kí', 11, NULL, 'TRUONG_PHONG', 13, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":317,\"thoiGianBatDau\":\"2026-11-16T08:00:00\",\"thoiGianKetThuc\":\"2026-11-16T09:00:00\",\"ghiChuTrinh\":\"xin phép cập nhật lại thư kí\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":225}', 225),
(227, 'KE_HOACH_CUOC_HOP', 114, 317, NULL, 10, '2026-11-16 02:02:53', 'DA_DUYET', NULL, 4, 6, 'LANH_DAO', 13, '{\"yeuCauPheDuyetId\":227,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":317,\"thoiGianBatDau\":\"2026-11-16T08:00:00\",\"thoiGianKetThuc\":\"2026-11-16T09:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":226}', 226),
(228, 'KE_HOACH_CUOC_HOP', 114, 317, NULL, 13, '2026-11-16 02:04:49', 'TRA_LAI', NULL, 11, NULL, 'TRUONG_PHONG', 13, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":317,\"thoiGianBatDau\":\"2026-11-16T08:00:00\",\"thoiGianKetThuc\":\"2026-11-16T09:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Võ Minh Đức\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":227}', 227),
(229, 'KE_HOACH_CUOC_HOP', 115, 320, NULL, 10, '2026-11-17 23:03:16', 'TRA_LAI', 'quá rườm rà', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":115,\"tieuDe\":\"họp bàn giao cuốii tuần\",\"mucTieu\":\"đạt kpi\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":320,\"thoiGianBatDau\":\"2026-11-18T00:00:00\",\"thoiGianKetThuc\":\"2026-11-18T01:00:00\",\"ghiChuTrinh\":\"quá rườm rà\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null}', NULL),
(230, 'KE_HOACH_CUOC_HOP', 115, 320, NULL, 10, '2026-11-17 23:05:55', 'DA_DUYET', 'đã gửi file kế hoach', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":115,\"tieuDe\":\"họp bàn giao cuốii tuần\",\"mucTieu\":\"đạt kpi\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":320,\"thoiGianBatDau\":\"2026-11-18T00:00:00\",\"thoiGianKetThuc\":\"2026-11-18T01:00:00\",\"ghiChuTrinh\":\"đã gửi file kế hoach\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":229}', 229),
(231, 'BIEN_BAN', 115, NULL, 508, 10, '2026-11-17 23:18:06', 'DA_DUYET', 'trình chủ trì duyệt', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(232, 'KE_HOACH_CUOC_HOP', 116, 322, NULL, 10, '2026-11-17 23:48:41', 'DA_DUYET', 'đã chọn lịch 3h- 4h', 11, NULL, 'TRUONG_PHONG', 13, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":116,\"tieuDe\":\"họp bàn giao cuốii tuần lần 2\",\"mucTieu\":\"test liên phòng ban\",\"noiDungDuKien\":\"kiểm tra biểu quyết kết luận\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"LIEN_PHONG_BAN\",\"lichHopId\":322,\"thoiGianBatDau\":\"2026-11-18T03:00:00\",\"thoiGianKetThuc\":\"2026-11-18T04:00:00\",\"ghiChuTrinh\":\"đã chọn lịch 3h- 4h\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null}', NULL),
(233, 'KE_HOACH_CUOC_HOP', 116, 322, NULL, 10, '2026-11-17 23:53:10', 'DA_DUYET', 'nhờ lãnh đạo phê duyệt', 4, 6, 'LANH_DAO', 13, '{\"yeuCauPheDuyetId\":233,\"cuocHopId\":116,\"tieuDe\":\"họp bàn giao cuốii tuần lần 2\",\"mucTieu\":\"test liên phòng ban\",\"noiDungDuKien\":\"kiểm tra biểu quyết kết luận\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"LIEN_PHONG_BAN\",\"lichHopId\":322,\"thoiGianBatDau\":\"2026-11-18T03:00:00\",\"thoiGianKetThuc\":\"2026-11-18T04:00:00\",\"ghiChuTrinh\":\"nhờ lãnh đạo phê duyệt\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":232}', 232),
(236, 'KET_LUAN', 116, NULL, NULL, 13, '2026-11-18 00:48:24', 'DA_DUYET', 'a', 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(237, 'BIEN_BAN', 116, NULL, 512, 13, '2026-11-18 00:55:07', 'TRA_LAI', 'aaaaaaaaa', 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(238, 'BIEN_BAN', 116, NULL, 512, 13, '2026-11-18 00:55:59', 'DA_DUYET', 'aaaaaaa', 4, NULL, 'CHU_TRI', NULL, NULL, 237),
(239, 'KE_HOACH_CUOC_HOP', 117, 323, NULL, 6, '2026-11-18 02:00:53', 'DA_DUYET', 'AAAAAAAAAAAAA', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":117,\"tieuDe\":\"Họp triển khai  dự án lần 1\",\"mucTieu\":\"aaaaaaaaaaa\",\"noiDungDuKien\":\"bbbbbbbbbb\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":323,\"thoiGianBatDau\":\"2026-11-18T05:00:00\",\"thoiGianKetThuc\":\"2026-11-18T06:00:00\",\"ghiChuTrinh\":\"AAAAAAAAAAAAA\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null}', NULL),
(240, 'BIEN_BAN', 117, NULL, 513, 6, '2026-11-18 02:21:59', 'TRA_LAI', 'y như nội dung', 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(241, 'BIEN_BAN', 117, NULL, 514, 6, '2026-11-18 02:25:00', 'DA_DUYET', 'hih hahaa', 4, NULL, 'CHU_TRI', NULL, NULL, 240),
(242, 'KET_LUAN', 117, NULL, NULL, 6, '2026-11-18 02:26:19', 'DA_DUYET', NULL, 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(243, 'KET_LUAN', 115, NULL, NULL, 10, '2026-11-18 15:36:26', 'DA_DUYET', NULL, 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(244, 'KE_HOACH_CUOC_HOP', 114, 324, NULL, 10, '2026-11-18 15:38:28', 'DA_DUYET', NULL, 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":324,\"thoiGianBatDau\":\"2026-11-18T16:00:00\",\"thoiGianKetThuc\":\"2026-11-18T17:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":228,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 228),
(245, 'KE_HOACH_CUOC_HOP', 114, 324, NULL, 10, '2026-11-18 15:42:36', 'DA_DUYET', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaa áccccccccccccccccc', 4, 6, 'LANH_DAO', 10, '{\"yeuCauPheDuyetId\":245,\"cuocHopId\":114,\"tieuDe\":\"aaa\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":324,\"thoiGianBatDau\":\"2026-11-18T16:00:00\",\"thoiGianKetThuc\":\"2026-11-18T17:00:00\",\"ghiChuTrinh\":\"aaaaaaaaaaaaaaaaaaaaaaaaaaaaa áccccccccccccccccc\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":244,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-18T15:42:35.5961314\"}', 244),
(246, 'BIEN_BAN', 114, NULL, 515, 10, '2026-11-18 15:50:40', 'DA_DUYET', 'aaaaaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(247, 'KET_LUAN', 114, NULL, NULL, 10, '2026-11-18 15:51:19', 'DA_DUYET', 'aaaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(248, 'KE_HOACH_CUOC_HOP', 113, 325, NULL, 12, '2026-11-18 16:12:24', 'DA_DUYET', 'aaaaaaaaa', 11, NULL, 'TRUONG_PHONG', 12, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":113,\"tieuDe\":\"Họp triển khai kế hoạch lần 5\",\"mucTieu\":\"aaaaaaa\",\"noiDungDuKien\":\"ccccccccc\",\"loaiHinh\":\"TRUC_TUYEN\",\"phamViCuocHop\":\"LIEN_PHONG_BAN\",\"lichHopId\":325,\"thoiGianBatDau\":\"2026-11-18T17:00:00\",\"thoiGianKetThuc\":\"2026-11-18T18:00:00\",\"ghiChuTrinh\":\"aaaaaaaaa\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Trần Ngọc Hân\",\"tenThuKyCuocHop\":\"Trần Ngọc Hân\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":null,\"viTriPhongHop\":null,\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":220,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 220),
(249, 'KE_HOACH_CUOC_HOP', 113, 325, NULL, 12, '2026-11-18 16:33:08', 'DA_DUYET', '123465', 4, 6, 'LANH_DAO', 12, '{\"yeuCauPheDuyetId\":249,\"cuocHopId\":113,\"tieuDe\":\"Họp triển khai kế hoạch lần 5\",\"mucTieu\":\"aaaaaaa\",\"noiDungDuKien\":\"ccccccccc\",\"loaiHinh\":\"TRUC_TUYEN\",\"phamViCuocHop\":\"LIEN_PHONG_BAN\",\"lichHopId\":325,\"thoiGianBatDau\":\"2026-11-18T17:00:00\",\"thoiGianKetThuc\":\"2026-11-18T18:00:00\",\"ghiChuTrinh\":\"123465\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Trần Ngọc Hân\",\"tenThuKyCuocHop\":\"Trần Ngọc Hân\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":null,\"viTriPhongHop\":null,\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":248,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-18T16:33:08.1337945\"}', 248),
(250, 'BIEN_BAN', 113, NULL, 516, 12, '2026-11-18 16:39:30', 'DA_DUYET', 'aaaaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(251, 'KET_LUAN', 113, NULL, NULL, 12, '2026-11-18 16:40:20', 'TRA_LAI', 'sssss', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(252, 'KET_LUAN', 113, NULL, NULL, 12, '2026-11-18 16:40:36', 'DA_DUYET', 'aaaaaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, 251),
(253, 'KE_HOACH_CUOC_HOP', 118, 326, NULL, 10, '2026-11-18 16:57:10', 'DA_DUYET', 'chưa có phòng theo lịch', 11, NULL, 'TRUONG_PHONG', 5, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":118,\"tieuDe\":\"test thử cấp 2 và tham gia cuộc họp\",\"mucTieu\":\"tự tham gia cuộc họp\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TUYEN\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":326,\"thoiGianBatDau\":\"2026-11-18T18:00:00\",\"thoiGianKetThuc\":\"2026-11-18T19:00:00\",\"ghiChuTrinh\":\"chưa có phòng theo lịch\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Nguyen Van Can Bo\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":null,\"viTriPhongHop\":null,\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(254, 'KE_HOACH_CUOC_HOP', 118, 326, NULL, 10, '2026-11-18 16:58:11', 'DA_DUYET', 'cho ông làm chủ trì', 4, 6, 'LANH_DAO', 5, '{\"yeuCauPheDuyetId\":254,\"cuocHopId\":118,\"tieuDe\":\"test thử cấp 2 và tham gia cuộc họp\",\"mucTieu\":\"tự tham gia cuộc họp\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TUYEN\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":326,\"thoiGianBatDau\":\"2026-11-18T18:00:00\",\"thoiGianKetThuc\":\"2026-11-18T19:00:00\",\"ghiChuTrinh\":\"cho ông làm chủ trì\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Nguyen Van Can Bo\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":null,\"viTriPhongHop\":null,\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":253,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-18T16:58:11.4118113\"}', 253),
(255, 'BIEN_BAN', 118, NULL, 517, 5, '2026-11-18 17:19:36', 'DA_DUYET', 'aaaaaaaaaaa', 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(256, 'KET_LUAN', 118, NULL, NULL, 5, '2026-11-18 17:20:13', 'DA_DUYET', NULL, 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(257, 'KE_HOACH_CUOC_HOP', 119, 327, NULL, 10, '2026-11-18 20:10:47', 'DA_DUYET', 'hi haha', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":119,\"tieuDe\":\"test họp nội bộ\",\"mucTieu\":\"test luồng\",\"noiDungDuKien\":\"luồng oke là đc\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":327,\"thoiGianBatDau\":\"2026-11-18T21:00:00\",\"thoiGianKetThuc\":\"2026-11-18T22:00:00\",\"ghiChuTrinh\":\"hi haha\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(258, 'BIEN_BAN', 119, NULL, 518, 10, '2026-11-18 20:24:03', 'DA_DUYET', 'aaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(259, 'KET_LUAN', 119, NULL, NULL, 10, '2026-11-18 20:24:29', 'TRA_LAI', 'aaaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(260, 'KET_LUAN', 119, NULL, NULL, 10, '2026-11-18 20:25:04', 'DA_DUYET', 'aaaaaaaaaaaaaa', 11, NULL, 'CHU_TRI', NULL, NULL, 259),
(261, 'KE_HOACH_CUOC_HOP', 120, 328, NULL, 10, '2026-11-18 20:50:00', 'DA_DUYET', 'aaaaaaaaaaaa', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":120,\"tieuDe\":\"test websocket\",\"mucTieu\":\"test websocket\",\"noiDungDuKien\":\"test websocket\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":328,\"thoiGianBatDau\":\"2026-11-18T22:00:00\",\"thoiGianKetThuc\":\"2026-11-18T23:00:00\",\"ghiChuTrinh\":\"aaaaaaaaaaaa\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(262, 'BIEN_BAN', 120, NULL, 520, 10, '2026-11-18 21:11:00', 'DA_DUYET', 'oke ròi', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(263, 'KET_LUAN', 120, NULL, NULL, 10, '2026-11-18 21:12:07', 'TRA_LAI', 'oke chỉnh lại thử xem', 11, NULL, 'CHU_TRI', NULL, NULL, NULL),
(264, 'KET_LUAN', 120, NULL, NULL, 10, '2026-11-18 21:12:43', 'DA_DUYET', NULL, 11, NULL, 'CHU_TRI', NULL, NULL, 263),
(265, 'KE_HOACH_CUOC_HOP', 122, 331, NULL, 10, '2026-11-18 23:01:53', 'TRA_LAI', 'test gmail', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":122,\"tieuDe\":\"kiểm tra gmail\",\"mucTieu\":\"kiểm tra mail\",\"noiDungDuKien\":\"abcdefghijk\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":331,\"thoiGianBatDau\":\"2026-11-19T17:00:00\",\"thoiGianKetThuc\":\"2026-11-19T18:00:00\",\"ghiChuTrinh\":\"test gmail\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(266, 'KE_HOACH_CUOC_HOP', 122, 331, NULL, 10, '2026-11-18 23:02:39', 'DA_DUYET', 'oke đã test chỉnh sửa kế hoạch', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":122,\"tieuDe\":\"kiểm tra gmail\",\"mucTieu\":\"kiểm tra mail\",\"noiDungDuKien\":\"abcdefghijk\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":331,\"thoiGianBatDau\":\"2026-11-19T17:00:00\",\"thoiGianKetThuc\":\"2026-11-19T18:00:00\",\"ghiChuTrinh\":\"oke đã test chỉnh sửa kế hoạch\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":265,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 265),
(267, 'KE_HOACH_CUOC_HOP', 124, 332, NULL, 10, '2026-11-18 23:37:56', 'DA_DUYET', 'test mail mời', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":124,\"tieuDe\":\"aaaaaaaaaaaaaa test mail\",\"mucTieu\":\"aaaaa\",\"noiDungDuKien\":\"aaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":332,\"thoiGianBatDau\":\"2026-11-19T02:00:00\",\"thoiGianKetThuc\":\"2026-11-19T03:00:00\",\"ghiChuTrinh\":\"test mail mời\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(268, 'KE_HOACH_CUOC_HOP', 125, 333, NULL, 10, '2026-11-18 23:48:53', 'DA_DUYET', 'a', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":125,\"tieuDe\":\"aaaaaa tetss\",\"mucTieu\":\"a\",\"noiDungDuKien\":\"a\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":333,\"thoiGianBatDau\":\"2026-11-19T10:00:00\",\"thoiGianKetThuc\":\"2026-11-19T11:00:00\",\"ghiChuTrinh\":\"a\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(269, 'KE_HOACH_CUOC_HOP', 126, 334, NULL, 10, '2026-11-18 23:53:52', 'DA_DUYET', 'test lịch', 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":126,\"tieuDe\":\"test hủy cuộc họp\",\"mucTieu\":\"a\",\"noiDungDuKien\":\"a\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":334,\"thoiGianBatDau\":\"2026-11-19T08:00:00\",\"thoiGianKetThuc\":\"2026-11-19T09:00:00\",\"ghiChuTrinh\":\"test lịch\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(270, 'KE_HOACH_CUOC_HOP', 125, 336, NULL, 10, '2026-11-19 00:07:30', 'DA_DUYET', NULL, 11, NULL, 'TRUONG_PHONG', 10, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":125,\"tieuDe\":\"aaaaaa tetss\",\"mucTieu\":\"a\",\"noiDungDuKien\":\"a\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":336,\"thoiGianBatDau\":\"2026-11-19T16:00:00\",\"thoiGianKetThuc\":\"2026-11-19T17:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Phạm Mai Anh\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":268,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 268),
(271, 'KE_HOACH_CUOC_HOP', 127, 337, NULL, 10, '2026-11-20 15:24:34', 'DA_DUYET', 'đã lên kế hoạch , kềm kế hoạch', 11, NULL, 'TRUONG_PHONG', 15, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":337,\"thoiGianBatDau\":\"2026-11-20T16:00:00\",\"thoiGianKetThuc\":\"2026-11-20T17:00:00\",\"ghiChuTrinh\":\"đã lên kế hoạch , kềm kế hoạch\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(272, 'KE_HOACH_CUOC_HOP', 127, 337, NULL, 10, '2026-11-20 15:41:59', 'DA_DUYET', NULL, 4, 6, 'LANH_DAO', 15, '{\"yeuCauPheDuyetId\":272,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":337,\"thoiGianBatDau\":\"2026-11-20T16:00:00\",\"thoiGianKetThuc\":\"2026-11-20T17:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":271,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-20T15:41:58.6862299\"}', 271),
(273, 'KE_HOACH_CUOC_HOP', 127, 338, NULL, 15, '2026-11-20 15:50:13', 'DA_DUYET', NULL, 11, NULL, 'TRUONG_PHONG', 15, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":338,\"thoiGianBatDau\":\"2026-11-20T18:00:00\",\"thoiGianKetThuc\":\"2026-11-20T19:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"aaaa\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":272,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 272),
(274, 'KE_HOACH_CUOC_HOP', 127, 340, NULL, 10, '2026-11-20 21:22:39', 'DA_DUYET', 'do lịch đã trễ nên gấp rút trình lại lịch mới', 11, NULL, 'TRUONG_PHONG', 15, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":340,\"thoiGianBatDau\":\"2026-11-20T22:00:00\",\"thoiGianKetThuc\":\"2026-11-20T23:00:00\",\"ghiChuTrinh\":\"do lịch đã trễ nên gấp rút trình lại lịch mới\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":273,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 273),
(275, 'KE_HOACH_CUOC_HOP', 127, 340, NULL, 10, '2026-11-20 21:23:59', 'DA_DUYET', 'gửi cấp trên', 4, 6, 'LANH_DAO', 15, '{\"yeuCauPheDuyetId\":275,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":340,\"thoiGianBatDau\":\"2026-11-20T22:00:00\",\"thoiGianKetThuc\":\"2026-11-20T23:00:00\",\"ghiChuTrinh\":\"gửi cấp trên\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":274,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-20T21:23:59.3362146\"}', 274),
(276, 'KE_HOACH_CUOC_HOP', 127, 341, NULL, 15, '2026-11-20 21:26:12', 'DA_DUYET', 'lấy lịch mới thư thả thời gian', 11, NULL, 'TRUONG_PHONG', 15, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":341,\"thoiGianBatDau\":\"2026-11-20T23:00:00\",\"thoiGianKetThuc\":\"2026-11-21T00:00:00\",\"ghiChuTrinh\":\"lấy lịch mới thư thả thời gian\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"aaaa\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":275,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', 275),
(277, 'KE_HOACH_CUOC_HOP', 127, 341, NULL, 10, '2026-11-20 21:27:03', 'DA_DUYET', 'oke đã tiếp nhânj', 4, 6, 'LANH_DAO', 15, '{\"yeuCauPheDuyetId\":277,\"cuocHopId\":127,\"tieuDe\":\"test khách mời ngoài\",\"mucTieu\":\"test khách mời ngoài\",\"noiDungDuKien\":\"aaaaaaaaaaaaaaaaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":341,\"thoiGianBatDau\":\"2026-11-20T23:00:00\",\"thoiGianKetThuc\":\"2026-11-21T00:00:00\",\"ghiChuTrinh\":\"oke đã tiếp nhânj\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"aaaa\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":276,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-20T21:27:03.4893963\"}', 276),
(278, 'BIEN_BAN', 127, NULL, 521, 15, '2026-11-20 22:06:44', 'DA_DUYET', NULL, 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(279, 'KET_LUAN', 127, NULL, NULL, 15, '2026-11-20 22:07:09', 'DA_DUYET', NULL, 4, NULL, 'CHU_TRI', NULL, NULL, NULL),
(280, 'KE_HOACH_CUOC_HOP', 128, 342, NULL, 6, '2026-11-20 22:55:10', 'DA_DUYET', '456', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":128,\"tieuDe\":\"test khách mail\",\"mucTieu\":\"123456\",\"noiDungDuKien\":\"123456\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":342,\"thoiGianBatDau\":\"2026-11-23T00:00:00\",\"thoiGianKetThuc\":\"2026-11-23T01:00:00\",\"ghiChuTrinh\":\"456\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Đặng Hoàng Nam\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(281, 'KE_HOACH_CUOC_HOP', 128, 343, NULL, 6, '2026-11-20 23:46:40', 'DA_DUYET', 'aaaaaac s', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":128,\"tieuDe\":\"test khách mail\",\"mucTieu\":\"123456\",\"noiDungDuKien\":\"123456\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":343,\"thoiGianBatDau\":\"2026-11-23T23:46:00\",\"thoiGianKetThuc\":\"2026-11-24T23:46:00\",\"ghiChuTrinh\":\"aaaaaac s\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":280,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', 280),
(282, 'KE_HOACH_CUOC_HOP', 129, 344, NULL, 6, '2026-11-21 00:08:50', 'DA_DUYET', NULL, 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":129,\"tieuDe\":\"ấccs\",\"mucTieu\":\"aaa\",\"noiDungDuKien\":\"aaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":344,\"thoiGianBatDau\":\"2026-11-23T01:00:00\",\"thoiGianKetThuc\":\"2026-11-23T02:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(283, 'KE_HOACH_CUOC_HOP', 130, 345, NULL, 10, '2026-11-21 13:52:54', 'DA_DUYET', NULL, 11, NULL, 'TRUONG_PHONG', 13, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":130,\"tieuDe\":\"ggg\",\"mucTieu\":\"aa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":345,\"thoiGianBatDau\":\"2026-11-23T02:00:00\",\"thoiGianKetThuc\":\"2026-11-23T03:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"TRUONG_PHONG\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyễn Quốc Bảo\",\"thoiGianTrinh\":null}', NULL),
(284, 'KE_HOACH_CUOC_HOP', 130, 345, NULL, 10, '2026-11-21 13:53:42', 'DA_DUYET', NULL, 4, 6, 'LANH_DAO', 13, '{\"yeuCauPheDuyetId\":284,\"cuocHopId\":130,\"tieuDe\":\"ggg\",\"mucTieu\":\"aa\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":345,\"thoiGianBatDau\":\"2026-11-23T02:00:00\",\"thoiGianKetThuc\":\"2026-11-23T03:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Phạm Mai Anh\",\"tenThuKyCuocHop\":\"Võ Minh Đức\",\"tenChuTri\":\"Nguyễn Quốc Bảo\",\"tenNguoiDeXuat\":\"Nguyễn Quốc Bảo\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":283,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":\"2026-11-21T13:53:42.0936011\"}', 283),
(285, 'KE_HOACH_CUOC_HOP', 132, 346, NULL, 6, '2026-11-21 14:00:44', 'DA_DUYET', 'aaaaa', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":132,\"tieuDe\":\"aaaa\",\"mucTieu\":\"aa\",\"noiDungDuKien\":\"aaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":346,\"thoiGianBatDau\":\"2026-11-23T12:00:00\",\"thoiGianKetThuc\":\"2026-11-23T13:00:00\",\"ghiChuTrinh\":\"aaaaa\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(286, 'KE_HOACH_CUOC_HOP', 133, 347, NULL, 6, '2026-11-22 22:45:36', 'DA_DUYET', 'abc', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":133,\"tieuDe\":\"Họp triển khai kế hoạch test bầu chọn\",\"mucTieu\":\"test bầu chọn\",\"noiDungDuKien\":\"aaaa\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"NOI_BO\",\"lichHopId\":347,\"thoiGianBatDau\":\"2026-11-25T00:00:00\",\"thoiGianKetThuc\":\"2026-11-25T01:00:00\",\"ghiChuTrinh\":\"abc\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(287, 'KE_HOACH_CUOC_HOP', 134, 348, NULL, 6, '2026-11-22 23:12:53', 'DA_DUYET', NULL, 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":134,\"tieuDe\":\"Họp triển khai kế hoạch thông tin mail và dòi lịch\",\"mucTieu\":\"Họp triển khai kế hoạch thông tin mail và dòi lịch\",\"noiDungDuKien\":\"Họp triển khai kế hoạch thông tin mail và dòi lịch\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":348,\"thoiGianBatDau\":\"2026-11-25T01:00:00\",\"thoiGianKetThuc\":\"2026-11-25T02:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(288, 'KE_HOACH_CUOC_HOP', 134, 349, NULL, 6, '2026-11-22 23:15:53', 'DA_DUYET', 'thay đổi lịch họp do ng chủ trì yêu câu', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":134,\"tieuDe\":\"Họp triển khai kế hoạch thông tin mail và dòi lịch\",\"mucTieu\":\"Họp triển khai kế hoạch thông tin mail và dòi lịch\",\"noiDungDuKien\":\"Họp triển khai kế hoạch thông tin mail và dòi lịch\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":349,\"thoiGianBatDau\":\"2026-11-25T02:00:00\",\"thoiGianKetThuc\":\"2026-11-25T03:00:00\",\"ghiChuTrinh\":\"thay đổi lịch họp do ng chủ trì yêu câu\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":287,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', 287),
(289, 'KE_HOACH_CUOC_HOP', 135, 350, NULL, 6, '2026-11-23 00:12:15', 'DA_DUYET', 'a', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":135,\"tieuDe\":\"Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên 1.\",\"mucTieu\":\"Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên\\n1.\",\"noiDungDuKien\":\"Bắt đầu test luồng 1 — Bầu 3 người từ 10 ứng viên\\n1.\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"TOAN_CO_QUAN\",\"lichHopId\":350,\"thoiGianBatDau\":\"2026-11-25T03:00:00\",\"thoiGianKetThuc\":\"2026-11-25T04:00:00\",\"ghiChuTrinh\":\"a\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(290, 'KE_HOACH_CUOC_HOP', 136, 351, NULL, 6, '2026-11-23 00:47:30', 'DA_DUYET', NULL, 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":136,\"tieuDe\":\"Tiếp tục test luồng 3 — Khách mời và cập nhật lịch\",\"mucTieu\":\"Tiếp tục test luồng 3 — Khách mời và cập nhật lịch\",\"noiDungDuKien\":\"Tiếp tục test luồng 3 — Khách mời và cập nhật lịch\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":351,\"thoiGianBatDau\":\"2026-11-25T04:00:00\",\"thoiGianKetThuc\":\"2026-11-25T05:00:00\",\"ghiChuTrinh\":null,\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Phòng họp A2\",\"viTriPhongHop\":\"Tầng 2\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":null,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', NULL),
(291, 'KE_HOACH_CUOC_HOP', 136, 352, NULL, 6, '2026-11-23 00:49:32', 'DA_DUYET', 'thay thế lịch', 4, NULL, 'LANH_DAO', 6, '{\"yeuCauPheDuyetId\":null,\"cuocHopId\":136,\"tieuDe\":\"Tiếp tục test luồng 3 — Khách mời và cập nhật lịch\",\"mucTieu\":\"Tiếp tục test luồng 3 — Khách mời và cập nhật lịch\",\"noiDungDuKien\":\"Tiếp tục test luồng 3 — Khách mời và cập nhật lịch\",\"loaiHinh\":\"TRUC_TIEP\",\"phamViCuocHop\":\"BEN_NGOAI\",\"lichHopId\":352,\"thoiGianBatDau\":\"2026-11-26T04:00:00\",\"thoiGianKetThuc\":\"2026-11-26T05:00:00\",\"ghiChuTrinh\":\"thay thế lịch\",\"trangThai\":\"CHO_DUYET\",\"tenNguoiTrinh\":\"Nguyễn Thị Lan\",\"tenThuKyCuocHop\":\"Nguyễn Thị Lan\",\"tenChuTri\":\"Nguyen Van Test Updated\",\"tenNguoiDeXuat\":\"Nguyen Van Test Updated\",\"tenPhongHop\":\"Hội trường B\",\"viTriPhongHop\":\"Tầng 1\",\"quyetDinh\":null,\"capPheDuyet\":\"LANH_DAO\",\"yeuCauTruocId\":290,\"tenNguoiDuyet\":\"Nguyen Van Test Updated\",\"thoiGianTrinh\":null}', 290);

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
(214, 1004, '2026-11-14 00:20:34'),
(215, 1004, '2026-11-14 00:21:51'),
(215, 1005, '2026-11-14 00:21:51'),
(218, 1007, '2026-11-14 11:32:04'),
(219, 1009, '2026-11-14 13:16:20'),
(220, 1009, '2026-11-14 13:18:05'),
(221, 1010, '2026-11-14 15:16:16'),
(221, 1011, '2026-11-14 15:16:16'),
(222, 1010, '2026-11-14 15:19:04'),
(222, 1011, '2026-11-14 15:19:04'),
(222, 1012, '2026-11-14 15:19:04'),
(239, 1022, '2026-11-18 02:00:52'),
(239, 1023, '2026-11-18 02:00:52'),
(239, 1024, '2026-11-18 02:00:52'),
(239, 1025, '2026-11-18 02:00:53'),
(239, 1026, '2026-11-18 02:00:53'),
(244, 1010, '2026-11-18 15:38:28'),
(244, 1014, '2026-11-18 15:38:28'),
(245, 1010, '2026-11-18 15:42:35'),
(245, 1014, '2026-11-18 15:42:35'),
(248, 1009, '2026-11-18 16:12:23'),
(249, 1009, '2026-11-18 16:33:08'),
(253, 1033, '2026-11-18 16:57:10'),
(254, 1033, '2026-11-18 16:58:11'),
(257, 1037, '2026-11-18 20:10:47'),
(261, 1039, '2026-11-18 20:49:59'),
(265, 1042, '2026-11-18 23:01:53'),
(266, 1042, '2026-11-18 23:02:39'),
(271, 1043, '2026-11-20 15:24:33'),
(272, 1043, '2026-11-20 15:41:58'),
(273, 1043, '2026-11-20 15:50:13'),
(274, 1043, '2026-11-20 21:22:39'),
(275, 1043, '2026-11-20 21:23:59'),
(276, 1043, '2026-11-20 21:26:11'),
(277, 1043, '2026-11-20 21:27:03'),
(282, 1045, '2026-11-21 00:08:49'),
(283, 1046, '2026-11-21 13:52:53'),
(284, 1046, '2026-11-21 13:53:42'),
(285, 1047, '2026-11-21 14:00:43'),
(287, 1048, '2026-11-22 23:12:52'),
(288, 1048, '2026-11-22 23:15:53'),
(289, 1049, '2026-11-23 00:12:15'),
(290, 1055, '2026-11-23 00:47:29'),
(291, 1055, '2026-11-23 00:49:32'),
(291, 1057, '2026-11-23 00:49:32'),
(291, 1058, '2026-11-23 00:49:32');

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
  ADD CONSTRAINT `fk_bieu_quyet_nghi_quyet` FOREIGN KEY (`cuoc_hop_id`,`nghi_quyet_id`) REFERENCES `nghi_quyet` (`cuoc_hop_id`, `nghi_quyet_id`),
  ADD CONSTRAINT `fk_bieu_quyet_nguoi_tao` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bieu_quyet_tai_lieu_nghi_quyet` FOREIGN KEY (`tai_lieu_nghi_quyet_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`);

--
-- Constraints for table `cap_nhat_loi_moi_khach`
--
ALTER TABLE `cap_nhat_loi_moi_khach`
  ADD CONSTRAINT `cap_nhat_loi_moi_khach_ibfk_1` FOREIGN KEY (`khach_moi_cuoc_hop_id`) REFERENCES `khach_moi_cuoc_hop` (`khach_moi_cuoc_hop_id`) ON DELETE CASCADE;

--
-- Constraints for table `cau_hinh_vong_bau`
--
ALTER TABLE `cau_hinh_vong_bau`
  ADD CONSTRAINT `cau_hinh_vong_bau_ibfk_1` FOREIGN KEY (`cuoc_bau_id`) REFERENCES `cuoc_bau` (`cuoc_bau_id`) ON DELETE CASCADE;

--
-- Constraints for table `cuoc_bau`
--
ALTER TABLE `cuoc_bau`
  ADD CONSTRAINT `cuoc_bau_ibfk_1` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`),
  ADD CONSTRAINT `cuoc_bau_ibfk_2` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

--
-- Constraints for table `cuoc_hop`
--
ALTER TABLE `cuoc_hop`
  ADD CONSTRAINT `fk_cuoc_hop_chu_tri` FOREIGN KEY (`nguoi_chu_tri_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_nguoi_de_xuat` FOREIGN KEY (`nguoi_de_xuat_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_nguoi_tao` FOREIGN KEY (`nguoi_tao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_phong_ban_chu_tri` FOREIGN KEY (`phong_ban_chu_tri_id`) REFERENCES `phong_ban` (`phong_ban_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_cuoc_hop_thu_ky` FOREIGN KEY (`thu_ky_cuoc_hop_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

--
-- Constraints for table `cu_tri_vong_bau`
--
ALTER TABLE `cu_tri_vong_bau`
  ADD CONSTRAINT `cu_tri_vong_bau_ibfk_1` FOREIGN KEY (`vong_bau_id`) REFERENCES `vong_bau` (`vong_bau_id`),
  ADD CONSTRAINT `cu_tri_vong_bau_ibfk_2` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

--
-- Constraints for table `ket_luan_cuoc_hop`
--
ALTER TABLE `ket_luan_cuoc_hop`
  ADD CONSTRAINT `fk_ket_luan_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`),
  ADD CONSTRAINT `fk_ket_luan_nguoi_phat_hanh` FOREIGN KEY (`nguoi_phat_hanh_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`),
  ADD CONSTRAINT `fk_ket_luan_nguoi_soan` FOREIGN KEY (`nguoi_soan_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

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
-- Constraints for table `lua_chon_phieu_bau`
--
ALTER TABLE `lua_chon_phieu_bau`
  ADD CONSTRAINT `lua_chon_phieu_bau_ibfk_1` FOREIGN KEY (`vong_bau_id`,`nguoi_bo_phieu_id`) REFERENCES `phieu_bau` (`vong_bau_id`, `nguoi_bo_phieu_id`),
  ADD CONSTRAINT `lua_chon_phieu_bau_ibfk_2` FOREIGN KEY (`vong_bau_id`,`ung_vien_id`) REFERENCES `ung_vien_vong_bau` (`vong_bau_id`, `nguoi_dung_id`);

--
-- Constraints for table `nghi_quyet`
--
ALTER TABLE `nghi_quyet`
  ADD CONSTRAINT `fk_nghi_quyet_tai_lieu` FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`),
  ADD CONSTRAINT `nghi_quyet_ibfk_1` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`),
  ADD CONSTRAINT `nghi_quyet_ibfk_2` FOREIGN KEY (`cuoc_hop_id`,`nghi_quyet_cha_id`) REFERENCES `nghi_quyet` (`cuoc_hop_id`, `nghi_quyet_id`);

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
-- Constraints for table `noi_dung_loi_moi_khach`
--
ALTER TABLE `noi_dung_loi_moi_khach`
  ADD CONSTRAINT `noi_dung_loi_moi_khach_ibfk_1` FOREIGN KEY (`khach_moi_cuoc_hop_id`) REFERENCES `khach_moi_cuoc_hop` (`khach_moi_cuoc_hop_id`) ON DELETE CASCADE;

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
-- Constraints for table `phieu_bau`
--
ALTER TABLE `phieu_bau`
  ADD CONSTRAINT `phieu_bau_ibfk_1` FOREIGN KEY (`vong_bau_id`,`nguoi_bo_phieu_id`) REFERENCES `cu_tri_vong_bau` (`vong_bau_id`, `nguoi_dung_id`);

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
-- Constraints for table `tai_lieu_chia_se_khach`
--
ALTER TABLE `tai_lieu_chia_se_khach`
  ADD CONSTRAINT `tai_lieu_chia_se_khach_ibfk_1` FOREIGN KEY (`tai_lieu_id`) REFERENCES `tai_lieu_cuoc_hop` (`tai_lieu_id`) ON DELETE CASCADE;

--
-- Constraints for table `tai_lieu_cuoc_hop`
--
ALTER TABLE `tai_lieu_cuoc_hop`
  ADD CONSTRAINT `fk_tai_lieu_cuoc_hop` FOREIGN KEY (`cuoc_hop_id`) REFERENCES `cuoc_hop` (`cuoc_hop_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tai_lieu_nguoi_tai` FOREIGN KEY (`nguoi_tai_len_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- Constraints for table `thanh_vien_bieu_quyet`
--
ALTER TABLE `thanh_vien_bieu_quyet`
  ADD CONSTRAINT `fk_tvbq_bieu_quyet` FOREIGN KEY (`bieu_quyet_id`) REFERENCES `bieu_quyet` (`bieu_quyet_id`),
  ADD CONSTRAINT `fk_tvbq_nguoi_dung` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

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
-- Constraints for table `ung_vien_vong_bau`
--
ALTER TABLE `ung_vien_vong_bau`
  ADD CONSTRAINT `ung_vien_vong_bau_ibfk_1` FOREIGN KEY (`vong_bau_id`) REFERENCES `vong_bau` (`vong_bau_id`),
  ADD CONSTRAINT `ung_vien_vong_bau_ibfk_2` FOREIGN KEY (`nguoi_dung_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

--
-- Constraints for table `vong_bau`
--
ALTER TABLE `vong_bau`
  ADD CONSTRAINT `vong_bau_ibfk_1` FOREIGN KEY (`vong_truoc_id`) REFERENCES `vong_bau` (`vong_bau_id`),
  ADD CONSTRAINT `vong_bau_ibfk_2` FOREIGN KEY (`cuoc_bau_id`) REFERENCES `cuoc_bau` (`cuoc_bau_id`);

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
  ADD CONSTRAINT `fk_ycpd_yeu_cau_truoc` FOREIGN KEY (`yeu_cau_truoc_id`) REFERENCES `yeu_cau_phe_duyet` (`yeu_cau_phe_duyet_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_yeu_cau_thu_ky_ban_giao` FOREIGN KEY (`thu_ky_ban_giao_id`) REFERENCES `nguoi_dung` (`nguoi_dung_id`);

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
