package com.example.demo.service;

import java.util.Set;

public final class QuyTacDuyetKeHoach {

    public enum CapPheDuyet {
        TRUONG_PHONG,
        LANH_DAO
    }

    private static final Set<String> PHAM_VI = Set.of(
            "NOI_BO", "LIEN_PHONG_BAN", "TOAN_CO_QUAN", "BEN_NGOAI");

    private QuyTacDuyetKeHoach() {
    }

    public static CapPheDuyet xacDinhCapDuyet(
            boolean doLanhDaoDeXuat, String phamVi, boolean coKhachNgoai) {
        if (phamVi == null || !PHAM_VI.contains(phamVi)) {
            throw new IllegalArgumentException("Phạm vi cuộc họp không hợp lệ");
        }
        if (doLanhDaoDeXuat || coKhachNgoai || !"NOI_BO".equals(phamVi)) {
            return CapPheDuyet.LANH_DAO;
        }
        return CapPheDuyet.TRUONG_PHONG;
    }

    public static boolean canTrinhCapTren(
            boolean doLanhDaoDeXuat, String phamVi, boolean coKhachNgoai) {
        if (phamVi == null || !PHAM_VI.contains(phamVi)) {
            throw new IllegalArgumentException("Phạm vi cuộc họp không hợp lệ");
        }
        return !doLanhDaoDeXuat && (coKhachNgoai || !"NOI_BO".equals(phamVi));
    }

    public static boolean canDuyetVanBanHaiCap(
            String phamVi, boolean coKhachNgoai) {
        if (phamVi == null || !PHAM_VI.contains(phamVi)) {
            throw new IllegalArgumentException("Phạm vi cuộc họp không hợp lệ");
        }
        return coKhachNgoai || !"NOI_BO".equals(phamVi);
    }
}