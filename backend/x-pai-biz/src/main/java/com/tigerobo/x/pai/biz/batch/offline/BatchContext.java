package com.tigerobo.x.pai.biz.batch.offline;

import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.biz.dto.BatchCallDto;
import com.tigerobo.x.pai.biz.io.ExcelWriteSugar;
import com.tigerobo.x.pai.biz.batch.evaluate.BaseEvaluator;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchContext {

    BatchEnv env = new BatchEnv();

    BatchInput input = new BatchInput();

    BatchResult result = new BatchResult();

    BatchProcess process = new BatchProcess();

    @Data
    public static class BatchEnv {
        String outputPath;
        String outFileName;
        String inputPath;
        Executable executor;

        ExcelWriteSugar excelWriteSugar;
        Object apiResult;


    }

    @Data
    public static class BatchProcess{
        int recordCnt;
        int batchSize = 1;
        int pivotSize = 10;

        BaseEvaluator baseEvaluator;

        int totalNum;

        int currentDealNum;

        int preDealNum;

        String preOssUrl;
        String preResultPath;


        boolean userStop;

        Integer dealTime;

        long startTime;

    }

    @Data
    public static class BatchInput {
        ModelBatchTaskPo po;

        Integer batchId;

        ModelCallTypeEnum typeEnum;
    }

    @Data
    public static class BatchResult {
        List<TextRowItem> list = new ArrayList<>();
        List<TextRowItem> resultPivot = new ArrayList<>();

        BatchCallDto batchCallDto = new BatchCallDto();

        String ossUrl;

        int dealNum;

    }

}
