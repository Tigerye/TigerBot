package com.tigerobo.x.pai.api.pay.vo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ApiBillDetailStatisticVo {

    Integer period;

    ApiBillVo periodBill;


    List<ModelBill> modelBillList =new ArrayList<>();

    List<DayBill> dayBillList = new ArrayList<>();


    @Data
    public static class ModelBill {
        Integer agreementId;
        String modelName;
        BigDecimal totalFee;
        Long callNum;
    }

    @Data
    public static class DayBill {

        @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
        Date dayFormat;
        int day;
        BigDecimal totalFee;
        List<DayBillDetail> detailList;
    }

    @Data
    public static class DayBillDetail{
        Integer agreementId;
        String modelName;
        Long callNum;
        BigDecimal totalFee;
    }
}
