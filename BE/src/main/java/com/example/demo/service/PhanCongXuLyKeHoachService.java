package com.example.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.CuocHop;
import com.example.demo.entity.NguoiDung;
import com.example.demo.entity.YeuCauPheDuyet;
import com.example.demo.entity.YeuCauPheDuyet.LoaiYeuCau;
import com.example.demo.entity.YeuCauPheDuyet.TrangThai;
import com.example.demo.repository.BienBanCuocHopRepository;
import com.example.demo.repository.CuocHopRepository;
import com.example.demo.repository.KetLuanCuocHopRepository;
import com.example.demo.repository.NguoiDungVaiTroRepository;
import com.example.demo.repository.ThanhVienCuocHopRepository;
import com.example.demo.repository.YeuCauPheDuyetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhanCongXuLyKeHoachService {

    private final CuocHopRepository cuocHopRepository;
    private final YeuCauPheDuyetRepository yeuCauPheDuyetRepository;
    private final ThanhVienCuocHopRepository thanhVienCuocHopRepository;
    private final NguoiDungHienTaiService nguoiDungHienTaiService;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;
    private final BienBanCuocHopRepository bienBanCuocHopRepository;
    private final KetLuanCuocHopRepository ketLuanCuocHopRepository;
    private final NhomThamDuCuocHopService nhomThamDu;

    public boolean laNguoiCoTheLamThuKy(NguoiDung nguoiDung) {
        if (nguoiDung == null || !NguoiDungHienTaiService.dangHoatDong(nguoiDung)) {
            return false;
        }
        return nguoiDungVaiTroRepository.findByNguoiDungId(nguoiDung.getNguoiDungId())
                .map(vaiTro -> Set.of("CAN_BO", "THU_KY")
                        .contains(vaiTro.getVaiTro().getMaVaiTro()))
                .orElse(false);
    }

    public NguoiDung chonThuKyCuocHop(CuocHop cuocHop, Integer nguoiDungId) {
        NguoiDung thuKyCoDinh = layThuKyCoDinhCuaNhom(cuocHop);
        if (thuKyCoDinh != null) {
            Integer idCoDinh = thuKyCoDinh.getNguoiDungId();
            if (nguoiDungId != null && !idCoDinh.equals(nguoiDungId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cuộc họp từ nhóm do thư ký nhóm được giao hồ sơ tổ chức");
            }
            NguoiDung thuKy = nguoiDungHienTaiService.layTaiKhoanHoatDong(idCoDinh);
            if (!laNguoiCoTheLamThuKy(thuKy) || ThaoTacCuocHop.laNguoiChuTri(cuocHop, idCoDinh)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Thư ký nhóm không còn phù hợp để tổ chức cuộc họp");
            }
            return thuKy;
        }
        if (nguoiDungId == null) {
            return null;
        }

        NguoiDung nguoiDung = nguoiDungHienTaiService.layTaiKhoanHoatDong(nguoiDungId);
        if (!laNguoiCoTheLamThuKy(nguoiDung)
                || ThaoTacCuocHop.laNguoiChuTri(cuocHop, nguoiDungId)
                || ThaoTacCuocHop.laNguoiDeXuat(cuocHop, nguoiDungId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chọn cán bộ hoặc thư ký đang hoạt động, khác chủ trì và người đề xuất");
        }

        return nguoiDung;
    }

    NguoiDung layThuKyCoDinhCuaNhom(CuocHop cuocHop) {
        return nhomThamDu.laDeXuatTuNhom(cuocHop.getCuocHopId())
                ? layThuKyBanDau(cuocHop.getCuocHopId()) : null;
    }

    public NguoiDung layThuKyCuocHop(CuocHop cuocHop) {
        if (cuocHop.getThuKyCuocHop() != null) {
            return cuocHop.getThuKyCuocHop();
        }
        return layThuKyBanDau(cuocHop.getCuocHopId());
    }

    public NguoiDung layThuKyBanDau(Integer cuocHopId) {
        return thanhVienCuocHopRepository
                .findByCuocHop_CuocHopIdAndVaiTroTaiCuocHop(cuocHopId, "THU_KY")
                .stream()
                .map(thanhVien -> thanhVien.getNguoiDung())
                .findFirst()
                .orElse(null);
    }

    public YeuCauPheDuyet layLanTrinhMoiNhat(Integer cuocHopId) {
        if (cuocHopId == null) {
            return null;
        }

        return yeuCauPheDuyetRepository
                .findFirstByCuocHop_CuocHopIdAndLoaiYeuCauOrderByYeuCauPheDuyetIdDesc(
                        cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP)
                .orElse(null);
    }

    public YeuCauPheDuyet layLanDuyetCoLichDaXacNhan(Integer cuocHopId) {
        return yeuCauPheDuyetRepository
                .timLanDuyetCoLichDaXacNhan(cuocHopId, LoaiYeuCau.KE_HOACH_CUOC_HOP,
                        TrangThai.DA_DUYET, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);
    }

    public YeuCauPheDuyet layBanGiaoHoSo(CuocHop cuocHop, YeuCauPheDuyet lanMoiNhat) {
        return daDenGiaiDoanToChuc(cuocHop)
                ? lanMoiNhat
                : layLanDuyetCoLichDaXacNhan(cuocHop.getCuocHopId());
    }

    public boolean daDenGiaiDoanToChuc(CuocHop cuocHop) {
        String trangThai = cuocHop.getTrangThai();
        return "DA_DUYET".equals(trangThai) || "DANG_DIEN_RA".equals(trangThai)
                || "DA_KET_THUC".equals(trangThai);
    }

    public boolean laThuKyDaNhanBanGiao(YeuCauPheDuyet yeuCau, Integer nguoiDungId) {
        return yeuCau != null
                && yeuCau.getTrangThai() == TrangThai.DA_DUYET
                && yeuCau.getThuKyBanGiao() != null
                && Objects.equals(yeuCau.getThuKyBanGiao().getNguoiDungId(), nguoiDungId);
    }

    public boolean daHoanTatHoSo(CuocHop cuocHop) {
        if (!ThaoTacCuocHop.daKetThuc(cuocHop)) {
            return false;
        }

        Integer cuocHopId = cuocHop.getCuocHopId();
        return bienBanCuocHopRepository.findByCuocHop_CuocHopId(cuocHopId)
                .filter(bienBan -> "DA_PHAT_HANH".equals(bienBan.getTrangThai()))
                .isPresent()
                && ketLuanCuocHopRepository.findByCuocHop_CuocHopId(cuocHopId)
                        .filter(ketLuan -> "DA_PHAT_HANH".equals(ketLuan.getTrangThai()))
                        .isPresent();
    }

    public boolean laThuKyToChuc(CuocHop cuocHop, Integer nguoiDungId) {
        if (!daDenGiaiDoanToChuc(cuocHop)) return false;

        YeuCauPheDuyet yeuCau = layLanTrinhMoiNhat(cuocHop.getCuocHopId());
        if (yeuCau == null || yeuCau.getTrangThai() != TrangThai.DA_DUYET) {
            return false;
        }
        boolean duocGiao = yeuCau.getThuKyBanGiao() != null
                ? laThuKyDaNhanBanGiao(yeuCau, nguoiDungId)
                : laThuKyBanDau(cuocHop.getCuocHopId(), nguoiDungId);
        return duocGiao && !daHoanTatHoSo(cuocHop);
    }

    public boolean laThuKyBanDau(Integer cuocHopId, Integer nguoiDungId) {
        return coVaiTro(cuocHopId, nguoiDungId, "THU_KY");
    }

    public Integer layYeuCauChoChuyenCap(CuocHop cuocHop, YeuCauPheDuyet lanMoiNhat, Integer nguoiDungId) {
        if (!"DANG_CHUAN_BI".equals(cuocHop.getTrangThai())
                || !"CHO_GUI_CAP_TREN".equals(cuocHop.getBuocXuLyHienTai())
                || lanMoiNhat == null || lanMoiNhat.getTrangThai() != TrangThai.DA_DUYET
                || !List.of("TRUONG_PHONG", "TRUONG_NHOM").contains(lanMoiNhat.getCapPheDuyet())
                || !laThuKyBanDau(cuocHop.getCuocHopId(), nguoiDungId)
                || ("TRUONG_PHONG".equals(lanMoiNhat.getCapPheDuyet())
                    && nguoiDungVaiTroRepository.findByNguoiDungIdAndVaiTro_MaVaiTro(nguoiDungId, "THU_KY").isEmpty())) {
            return null;
        }
        return lanMoiNhat.getYeuCauPheDuyetId();
    }

    public boolean laThuKyThamGia(Integer cuocHopId, Integer nguoiDungId) {
        if (cuocHopId == null || nguoiDungId == null) return false;

        return laThuKyBanDau(cuocHopId, nguoiDungId)
                || coVaiTro(cuocHopId, nguoiDungId, "THU_KY_LANH_DAO")
                || cuocHopRepository.findById(cuocHopId)
                        .map(cuocHop -> laThuKyDaNhanBanGiao(
                                layBanGiaoHoSo(cuocHop, layLanTrinhMoiNhat(cuocHopId)), nguoiDungId))
                        .orElse(false);
    }

    public boolean laThuKyDangXuLy(Integer cuocHopId, Integer nguoiDungId) {
        if (cuocHopId == null || nguoiDungId == null) {
            return false;
        }
        CuocHop cuocHop = cuocHopRepository.findById(cuocHopId).orElse(null);
        if (cuocHop == null) {
            return false;
        }
        if (ThaoTacCuocHop.laDeXuatNhap(cuocHop)
                || ThaoTacCuocHop.dangChoTiepNhan(cuocHop)) {
            return laThuKyBanDau(cuocHopId, nguoiDungId);
        }

        YeuCauPheDuyet yeuCau = layLanTrinhMoiNhat(cuocHopId);
        if (ThaoTacCuocHop.dangLapKeHoach(cuocHop)) {
            return laThuKyBanDau(cuocHopId, nguoiDungId)
                    || laThuKyDuocGiao(yeuCau, nguoiDungId)
                    || laThuKyDaNhanBanGiao(layLanDuyetCoLichDaXacNhan(cuocHopId), nguoiDungId);
        }
        if ("CHO_GUI_CAP_TREN".equals(cuocHop.getBuocXuLyHienTai())) {
            return laThuKyBanDau(cuocHopId, nguoiDungId);
        }
        if (yeuCau == null) {
            return false;
        }
        if (yeuCau.getTrangThai() == TrangThai.DA_DUYET) {
            return laThuKyToChuc(cuocHop, nguoiDungId);
        }
        return yeuCau.getTrangThai() == TrangThai.CHO_DUYET
                && yeuCau.getThuKyTiepNhan() != null
                && nguoiDungId.equals(yeuCau.getThuKyTiepNhan().getNguoiDungId());
    }

    private boolean laThuKyDuocGiao(YeuCauPheDuyet yeuCau, Integer nguoiDungId) {
        return yeuCau != null
                && yeuCau.getThuKyBanGiao() != null
                && nguoiDungId.equals(yeuCau.getThuKyBanGiao().getNguoiDungId());
    }

    public boolean laNguoiDuyetMoiNhat(Integer cuocHopId, Integer nguoiDungId) {
        if (cuocHopId == null || nguoiDungId == null) {
            return false;
        }
        YeuCauPheDuyet yeuCau = layLanTrinhMoiNhat(cuocHopId);
        return yeuCau != null
                && yeuCau.getNguoiDuyet() != null
                && nguoiDungId.equals(yeuCau.getNguoiDuyet().getNguoiDungId());
    }

    private boolean coVaiTro(Integer cuocHopId, Integer nguoiDungId, String vaiTro) {
        return cuocHopId != null && nguoiDungId != null
                && thanhVienCuocHopRepository
                        .existsByCuocHop_CuocHopIdAndNguoiDung_NguoiDungIdAndVaiTroTaiCuocHop(
                                cuocHopId, nguoiDungId, vaiTro);
    }
}