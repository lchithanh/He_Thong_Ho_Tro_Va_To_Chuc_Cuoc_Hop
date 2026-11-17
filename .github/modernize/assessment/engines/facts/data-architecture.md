# Data Architecture & Persistence Layer

The persistence schema contains 21 MySQL/InnoDB tables defined by the database SQL script. The Java project includes Spring Data JPA and Hibernate dependencies, but entity and repository implementations have not yet been added.

## Database Configuration

| Service/Module | DB Type | Profile | Driver | Connection | Migration Tool |
|---|---|---|---|---|---|
| BE | MySQL 8.x | Default | MySQL Connector/J | Local MySQL database `quan_ly_cuoc_hop`; Hibernate validates the schema | None detected; the SQL script is authoritative |

No separate database profiles, connection pool overrides, seed SQL files, Flyway, or Liquibase configuration were found. The schema SQL includes initial role records.

## Data Ownership per Service

| Service | Tables Owned | ORM Framework | Caching | Notes |
|---|---|---|---|---|
| phongban | phong_ban | Spring Data JPA planned | None detected | Department master data |
| nguoidung | nguoi_dung | Spring Data JPA planned | None detected | Contains account and personal data |
| phanquyen | vai_tro, nguoi_dung_vai_tro | Spring Data JPA planned | None detected | Role assignment; no fine-grained permission table |
| phonghop | phong_hop | Spring Data JPA planned | None detected | Room master data |
| thietbi | thiet_bi | Spring Data JPA planned | None detected | Equipment assigned optionally to a room |
| cuochop | cuoc_hop, lich_hop | Spring Data JPA planned | None detected | Meeting and schedule records |
| thanhvien | thanh_vien_cuoc_hop | Spring Data JPA planned | None detected | Meeting participant join table |
| pheduyet | lich_su_phe_duyet | Spring Data JPA planned | None detected | Approval history |
| thongbao | thong_bao | Spring Data JPA planned | None detected | User notifications |
| tailieu | tai_lieu_cuoc_hop | Spring Data JPA planned | None detected | File metadata and extracted text |
| bienban | bien_ban_cuoc_hop, phien_ban_bien_ban | Spring Data JPA planned | None detected | Minutes and version history |
| ai | ket_qua_xu_ly_ai | Spring Data JPA planned | None detected | AI processing request and result |
| nhiemvu | nhiem_vu, tien_do_nhiem_vu | Spring Data JPA planned | None detected | Tasks and progress events |
| nhatky | nhat_ky_he_thong | Spring Data JPA planned | None detected | Audit history |
| bieuquyet | bieu_quyet, lua_chon_bieu_quyet, phieu_bieu_quyet | Spring Data JPA planned | None detected | Polls, options, ballots |

## Entity Model

The SQL defines the physical data model. No corresponding JPA entity classes or Spring Data repository interfaces were found in the Java source at assessment time. The complete table inventory, key relationships, and ER diagram are documented in [THIET_KE_CSDL.md](../../../../../database/THIET_KE_CSDL.md).

Transaction boundaries and entity association mappings are not yet implemented. Use composite identifiers for `nguoi_dung_vai_tro` and `thanh_vien_cuoc_hop`; `phieu_bieu_quyet` also has a composite foreign key to an option within its poll.

## Key Repository Methods

No repository interfaces or custom query methods were found in the BE source. Standard CRUD and future custom methods should be implemented within their owning feature packages, particularly for room/time availability, unread notifications, meeting members, task progress, and ballot aggregation.

## Caching Strategy

No cache provider, Spring Cache annotations, cache regions, TTL policy, or Hibernate second-level cache configuration was detected.

## Data Ownership Boundaries

The schema is a single shared relational database with logical ownership grouped into feature packages. Foreign keys enforce relationships inside the shared schema; no service-to-service API access or CQRS read model was detected. The current backend is a single Spring Boot application.

### Data Classification & Sensitivity

| Entity | Sensitive Fields | Classification (PII/PHI/PCI/None) | Controls in Place |
|---|---|---|---|
| nguoi_dung | ho_ten, email, so_dien_thoai, ten_dang_nhap, mat_khau_ma_hoa | PII and authentication data | Password is stored in a hash-designated column; application-level hashing and field access controls were not verified |
| tai_lieu_cuoc_hop | duong_dan_file, noi_dung_van_ban | Potentially confidential meeting content | No field-level encryption or masking detected |
| bien_ban_cuoc_hop | noi_dung_goc, noi_dung_chinh_thuc, file paths | Potentially confidential meeting content | No field-level encryption or masking detected |
| ket_qua_xu_ly_ai | noi_dung_dau_vao, ket_qua_dau_ra | Potentially confidential meeting content | No field-level encryption or masking detected |
| nhat_ky_he_thong | dia_chi_ip, chi_tiet | Personal/operational data | No masking configuration detected |

No payment-card data was identified in the schema. Database encryption-at-rest and backup encryption are deployment-level concerns and were not verifiable from the inspected source.
