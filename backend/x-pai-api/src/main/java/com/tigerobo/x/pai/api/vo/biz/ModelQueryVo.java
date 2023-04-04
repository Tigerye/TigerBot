package com.tigerobo.x.pai.api.vo.biz;

import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

import java.util.Map;

@Data
public class ModelQueryVo extends RequestVo {
    Map<String,Object> params;
    Integer pageNum = 1;
    Integer pageSize = 200;
}
