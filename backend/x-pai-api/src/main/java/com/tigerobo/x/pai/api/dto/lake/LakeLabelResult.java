package com.tigerobo.x.pai.api.dto.lake;

import lombok.Data;

import java.util.List;

@Data
public class LakeLabelResult {
    Integer status;
    String msg;
    List<List<LakeLabelDto>> result;
}
