package com.tigerobo.x.pai.api.vo.biz;

import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

import java.util.List;

@Data
public class DemandQueryVo extends RequestVo {

    Param params;
    @Data
    public static class Param{
        List<String> domain;
        List<String> industry;
        String keyword;
//        List<String> phase;
        List<Integer> phaseList;
        List<Integer> scopeList;
    }
}
