package com.tigerobo.x.pai.api.vo.biz;

import lombok.Data;

@Data
public class PageReqVo {
    protected Integer pageNum = 1;
    protected Integer pageSize = 10;
}
