package com.tigerobo.x.pai.biz.aml;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.aml.enums.EvaluationDataTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class EvaluationDataTypeUtil {



    public static EvaluationDataTypeEnum calEvaluationDataTypeEnum(boolean real, boolean predict){

        if (real&&predict){
            return EvaluationDataTypeEnum.TP;
        }
        if (real&&(!predict)){
            return EvaluationDataTypeEnum.FN;
        }
        if (!real&&predict){
            return EvaluationDataTypeEnum.FP;
        }

        return null;
    }

    public static void main(String[] args) {
        EvaluationDataTypeEnum evaluationDataTypeEnum = calEvaluationDataTypeEnum(true, true);
        System.out.println(evaluationDataTypeEnum);
    }

}
