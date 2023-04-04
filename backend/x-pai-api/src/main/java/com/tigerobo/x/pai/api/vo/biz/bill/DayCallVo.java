package com.tigerobo.x.pai.api.vo.biz.bill;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DayCallVo {
    private String date;
    private Integer day;
    private Integer num;
}
