package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PheDuyetKeHoachRequest {

    private String yKien;

    @JsonProperty("yKien")
    public String getYKien() {
        return yKien;
    }

    @JsonProperty("yKien")
    public void setYKien(String yKien) {
        this.yKien = yKien;
    }
}