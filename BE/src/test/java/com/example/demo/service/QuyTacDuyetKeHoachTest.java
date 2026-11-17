package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.demo.entity.CuocHop;

class QuyTacDuyetKeHoachTest {

    @Test
    void onlyDepartmentHeadRequestsOutsideItsInternalMeetingNeedTwoLevels() {
        assertFalse(QuyTacDuyetKeHoach.canTrinhCapTren(false, "NOI_BO", false));
        assertTrue(QuyTacDuyetKeHoach.canTrinhCapTren(false, "LIEN_PHONG_BAN", false));
        assertTrue(QuyTacDuyetKeHoach.canTrinhCapTren(false, "NOI_BO", true));
        assertFalse(QuyTacDuyetKeHoach.canTrinhCapTren(true, "TOAN_CO_QUAN", false));
    }

    @Test
    void minutesAndConclusionsNeedTwoApprovalsOutsideInternalMeetings() {
        assertFalse(QuyTacDuyetKeHoach.canDuyetVanBanHaiCap("NOI_BO", false));
        assertTrue(QuyTacDuyetKeHoach.canDuyetVanBanHaiCap("LIEN_PHONG_BAN", false));
        assertTrue(QuyTacDuyetKeHoach.canDuyetVanBanHaiCap("TOAN_CO_QUAN", false));
        assertTrue(QuyTacDuyetKeHoach.canDuyetVanBanHaiCap("NOI_BO", true));
    }

    @Test
    void approvalAtDepartmentHeadMovesToWaitingForForwardWithoutFinalApproval() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("CHO_TRUONG_PHONG");

        ThaoTacCuocHop.dongYTrinhCapTren(cuocHop);

        assertEquals("DANG_CHUAN_BI", cuocHop.getTrangThai());
        assertEquals("CHO_GUI_CAP_TREN", cuocHop.getBuocXuLyHienTai());
    }

    @Test
    void forwardingIsOnlyAllowedAfterDepartmentHeadAgreement() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("CHO_GUI_CAP_TREN");

        ThaoTacCuocHop.guiKeHoachCapTren(cuocHop);

        assertEquals("DANG_CHUAN_BI", cuocHop.getTrangThai());
        assertEquals("CHO_THU_KY_LANH_DAO", cuocHop.getBuocXuLyHienTai());
        assertThrows(IllegalStateException.class,
                () -> ThaoTacCuocHop.guiKeHoachCapTren(cuocHop));
    }

    @Test
    void waitingForForwardCanReturnToPlanningForANewSchedule() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setTrangThai("DANG_CHUAN_BI");
        cuocHop.setBuocXuLyHienTai("CHO_GUI_CAP_TREN");

        ThaoTacCuocHop.moLaiKeHoachChoChuyenCap(cuocHop);

        assertEquals("DANG_CHUAN_BI", cuocHop.getTrangThai());
        assertEquals("LAP_KE_HOACH", cuocHop.getBuocXuLyHienTai());
        assertThrows(IllegalStateException.class,
                () -> ThaoTacCuocHop.guiKeHoachCapTren(cuocHop));
    }

    @Test
    void approvedMeetingCanReturnToPlanEditingBeforeItStarts() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setTrangThai("DA_DUYET");
        cuocHop.setBuocXuLyHienTai("THU_KY_TO_CHUC");

        ThaoTacCuocHop.moLaiDeCapNhatKeHoach(cuocHop);

        assertEquals("DANG_CHUAN_BI", cuocHop.getTrangThai());
        assertEquals("LAP_KE_HOACH", cuocHop.getBuocXuLyHienTai());
    }

    @Test
    void planCannotBeReopenedAfterMeetingStarts() {
        CuocHop cuocHop = new CuocHop();
        cuocHop.setTrangThai("DA_DUYET");
        cuocHop.setBuocXuLyHienTai("THU_KY_TO_CHUC");
        cuocHop.setThoiGianBatDauThucTe(LocalDateTime.now());

        assertThrows(IllegalStateException.class,
                () -> ThaoTacCuocHop.moLaiDeCapNhatKeHoach(cuocHop));
    }
}
