package com.example.demo.dto.request;

import java.util.List;

public record GuiPhieuBieuQuyetRequest(Integer luaChonId, String lyDoKhongDongY, List<Integer> luaChonIds) {
}