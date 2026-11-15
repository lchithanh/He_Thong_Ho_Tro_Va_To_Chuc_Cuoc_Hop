package com.example.demo.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrinhKeHoachRequest {

	private Integer lichHopId;
    private String ghiChuTrinh;
    private Integer lanhDaoDuyetId;
    private List<Integer> taiLieuIds;
}