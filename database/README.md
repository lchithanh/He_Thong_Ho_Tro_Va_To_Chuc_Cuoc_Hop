# Cơ sở dữ liệu quản lý cuộc họp

[quan_ly_cuoc_hop_schema.sql](quan_ly_cuoc_hop_schema.sql) dùng để tạo một cơ sở dữ liệu MySQL 8.4 mới. File gồm 26 bảng từ bản xuất ngày 20/11/2026, hai bảng nhiệm vụ mà backend ánh xạ và bốn vai trò hệ thống. Kết quả là 28 bảng.

## Chạy với dữ liệu hiện có

Nếu MySQL đã có dữ liệu làm việc, hãy trỏ Spring Boot tới cơ sở dữ liệu đó qua `spring.datasource.url` hoặc `SPRING_DATASOURCE_URL`. Nếu cần phục hồi dữ liệu từ bản xuất riêng, hãy sao lưu rồi nhập bản xuất vào một cơ sở dữ liệu trống được chọn trong phpMyAdmin. Một số bản xuất có lệnh `DROP TABLE`.

## Khởi tạo từ mã nguồn

1. Trên MySQL 8.4, dùng phpMyAdmin hoặc MySQL Workbench để chạy `quan_ly_cuoc_hop_schema.sql` khi cơ sở dữ liệu `quan_ly_cuoc_hop` còn trống. Nếu dùng tên khác, đổi tên ở hai lệnh đầu file và trong cấu hình backend.
2. Dùng tài khoản MySQL của máy qua `SPRING_DATASOURCE_USERNAME` và `SPRING_DATASOURCE_PASSWORD`.
3. Tạo tài khoản quản trị cục bộ. Mật khẩu của tài khoản quản trị được lưu dưới dạng BCrypt trong `nguoi_dung.mat_khau_ma_hoa`. Gán vai trò theo `vai_tro.ma_vai_tro = 'ADMIN'`.

Ví dụ sau tạo quản trị viên đầu tiên trên cơ sở dữ liệu vừa khởi tạo. Thay chuỗi mẫu bằng hash BCrypt của mật khẩu mới trước khi chạy. Đặt mật khẩu và tạo hash trên máy của bạn.

```sql
START TRANSACTION;

INSERT INTO nguoi_dung (ten_dang_nhap, ho_ten, email, mat_khau_ma_hoa, trang_thai)
VALUES ('admin', 'Quản trị viên', 'admin@example.invalid', '<HASH_BCRYPT_CUA_BAN>', 'HOAT_DONG');

SET @admin_id = LAST_INSERT_ID();

INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id, vai_tro_id)
SELECT @admin_id, vai_tro_id
FROM vai_tro
WHERE ma_vai_tro = 'ADMIN';

COMMIT;
```
