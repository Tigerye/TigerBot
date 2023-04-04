package com.tigerobo.x.pai.api.aml.req;

import com.tigerobo.x.pai.api.aml.enums.EvaluationDataTypeEnum;
import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AmlConfidenceEvaluationReq extends RequestVo {

    Integer id;
    String labelKey;
    String labelName;
    BigDecimal threshold;
    /**@see EvaluationDataTypeEnum
     *
     */
    String evaluationDataType;
}
