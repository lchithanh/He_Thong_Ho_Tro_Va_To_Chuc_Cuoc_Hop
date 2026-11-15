package com.example.demo.entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
@NoArgsConstructor
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vai_tro_id")
    private Integer vaiTroId;

    @Column(name = "ma_vai_tro", nullable = false, length = 30)
    private String maVaiTro;

    @Column(name = "ten_vai_tro", nullable = false, length = 100)
    private String tenVaiTro;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai = "HOAT_DONG";
 // Danh sách người dùng được gán vai trò này
    @OneToMany(mappedBy = "vaiTro", fetch = FetchType.LAZY)
    private List<NguoiDungVaiTro> danhSachNguoiDungVaiTro = new ArrayList<>();
}