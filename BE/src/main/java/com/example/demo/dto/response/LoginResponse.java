package com.example.demo.dto.response;

public class LoginResponse {

    private Integer nguoiDungId;
    private String hoTen;
    private String email;
    private String tenDangNhap;
    private String tenPhongBan;
    private String maVaiTro;
    private String tenVaiTro;
    private String token;

    private Integer phongBanId;
    private boolean truongPhong;
    private String tenThuKyMacDinh;

    public Integer getPhongBanId() {
        return phongBanId;
    }

    public void setPhongBanId(Integer phongBanId) {
        this.phongBanId = phongBanId;
    }

    public boolean isTruongPhong() {
        return truongPhong;
    }

    public void setTruongPhong(boolean truongPhong) {
        this.truongPhong = truongPhong;
    }

    public String getTenThuKyMacDinh() {
        return tenThuKyMacDinh;
    }

    public void setTenThuKyMacDinh(String tenThuKyMacDinh) {
        this.tenThuKyMacDinh = tenThuKyMacDinh;
    }
    public Integer getNguoiDungId() {
        return nguoiDungId;
    }

    public void setNguoiDungId(Integer nguoiDungId) {
        this.nguoiDungId = nguoiDungId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public String getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(String maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
}