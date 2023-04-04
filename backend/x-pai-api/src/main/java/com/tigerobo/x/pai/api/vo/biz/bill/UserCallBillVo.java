package com.tigerobo.x.pai.api.vo.biz.bill;

import com.tigerobo.x.pai.api.auth.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户模型调用账单
 */
@Data
public class UserCallBillVo {
    Integer userId;
    User user;
    long callModelTotalNum;

    List<UserModelBillVo> modelCallList;

    @Data
    public static class UserModelBillVo{
        /**
         * 用于跳到自主训练卡片
         */
        private Integer amlId;
        /**
         * 用于跳到应用卡片
         */
        private String taskId;

        private String appShortName;

        private String modelId;
        private String modelName;

        private Integer type;
        @ApiModelProperty(value = "1,页面调用;2,接口调用;4,批量预测")
        private Integer callSource;
        private long num;
        List<DayCallVo> dayDetailList;
    }
}
