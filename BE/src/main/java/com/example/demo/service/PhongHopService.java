package com.example.demo.service;

import com.example.demo.dto.request.*;

import com.example.demo.dto.response.*;
import com.example.demo.entity.PhongHop;
import com.example.demo.repository.PhongHopRepository;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Service
public class PhongHopService {

    private final PhongHopRepository phongHopRepository;
    private final CapNhatHoSoRealtime capNhatRealtime;
    
    public PhongHopService(
            PhongHopRepository phongHopRepository,
            CapNhatHoSoRealtime capNhatRealtime) {
        this.phongHopRepository = phongHopRepository;
        this.capNhatRealtime = capNhatRealtime;
    }

    // Chuyển Entity sang Response DTO
    private PhongHopResponse toResponse(PhongHop phongHop) {
        return new PhongHopResponse(
                phongHop.getPhongHopId(),
                phongHop.getTenPhongHop(),
                phongHop.getViTri(),
                phongHop.getSucChua(),
                phongHop.getMoTa(),
                phongHop.getTrangThai()
        );
    }

    // 1. Lấy danh sách phòng họp
    public List<PhongHopResponse> layDanhSachPhongHop() {
        return phongHopRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. Lấy chi tiết phòng họp
    public PhongHopResponse layChiTietPhongHop(Integer phongHopId) {
        PhongHop phongHop = timPhongHop(phongHopId);
        return toResponse(phongHop);
    }

    // 3. Thêm phòng họp
    @Transactional
    public PhongHopResponse themPhongHop(TaoPhongHopRequest request) {

        if (phongHopRepository.existsByTenPhongHopIgnoreCase(
                request.tenPhongHop().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tên phòng họp đã tồn tại"
            );
        }

        PhongHop phongHop = new PhongHop();

        phongHop.setTenPhongHop(request.tenPhongHop().trim());
        phongHop.setViTri(request.viTri());
        phongHop.setSucChua(request.sucChua());
        phongHop.setMoTa(request.moTa());
        phongHop.setTrangThai("SAN_SANG");

        PhongHop saved = phongHopRepository.save(phongHop);
        
        capNhatRealtime.baoDanhMucPhongVaThietBiThayDoi();
        

        return toResponse(saved);
    }

    // 4. Cập nhật thông tin phòng họp
    @Transactional
    public PhongHopResponse capNhatPhongHop(
            Integer phongHopId,
            CapNhatPhongHopRequest request) {

        PhongHop phongHop = timPhongHop(phongHopId);

        String tenMoi = request.tenPhongHop().trim();

        if (phongHopRepository
                .existsByTenPhongHopIgnoreCaseAndPhongHopIdNot(
                        tenMoi,
                        phongHopId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tên phòng họp đã được sử dụng"
            );
        }

        phongHop.setTenPhongHop(tenMoi);
        phongHop.setViTri(request.viTri());
        phongHop.setSucChua(request.sucChua());
        phongHop.setMoTa(request.moTa());

        PhongHop daLuu = phongHopRepository.save(phongHop);
        capNhatRealtime.baoDanhMucPhongVaThietBiThayDoi();
        return toResponse(daLuu);    }

    // 5. Cập nhật trạng thái phòng họp
    @Transactional
    public PhongHopResponse capNhatTrangThai(
            Integer phongHopId,
            String trangThai) {

        PhongHop phongHop = timPhongHop(phongHopId);

        List<String> trangThaiHopLe = List.of(
                "SAN_SANG",
                "BAO_TRI",
                "NGUNG_SU_DUNG"
        );

        if (!trangThaiHopLe.contains(trangThai)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Trạng thái phòng họp không hợp lệ"
            );
        }

        phongHop.setTrangThai(trangThai);

        PhongHop daLuu = phongHopRepository.save(phongHop);
        capNhatRealtime.baoDanhMucPhongVaThietBiThayDoi();
        return toResponse(daLuu);    }

    // Hàm dùng chung để tìm phòng họp
    private PhongHop timPhongHop(Integer phongHopId) {
        return phongHopRepository.findById(phongHopId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy phòng họp với ID: " + phongHopId
                ));
    }
}