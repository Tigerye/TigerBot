package com.tigerobo.x.pai.biz.batch.evaluate;

import com.tigerobo.x.pai.biz.io.ExcelWriteSugar;
import com.tigerobo.x.pai.biz.batch.offline.BatchContext;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Component
@Slf4j
public class BatchEvaluator {

    @Autowired
    private ExcelBatchEvaluator excelBatchEvaluator;

    @Autowired
    private CsvBatchEvaluator csvBatchEvaluator;

    @Autowired
    private PreExcelBatchEvaluator preExcelBatchEvaluator;

    public void evaluate(BatchContext context) {
        // 创建输出SheetWriter
        initExcelWriter(context);

        // 读取Excel文件，并遍历处理

        BaseEvaluator baseEvaluator = null;
        final String fileType = getInputFileType(context);
        if ("csv".equalsIgnoreCase(fileType)) {
            baseEvaluator = csvBatchEvaluator;
        } else {
            baseEvaluator = excelBatchEvaluator;
        }

        baseEvaluator.getTotal(context);

        try {
            copyPre(context);

            baseEvaluator.deal(context);
        }finally {
            final ExcelWriteSugar excelWriteSugar = context.getEnv().getExcelWriteSugar();
            if (excelWriteSugar!=null){
                excelWriteSugar.finish();
            }
        }
    }


    private void copyPre(BatchContext context){

        final int preDealNum = context.getProcess().getPreDealNum();
        if (preDealNum>0){
            preExcelBatchEvaluator.deal(context);
        }

    }


    private String getInputFileType(BatchContext context){

        final ModelBatchTaskPo po = context.getInput().getPo();
        String fileName = po.getFileName();


        if (StringUtils.isBlank(fileName)){
            fileName = po.getInputPath();

        }
        String fileType = null;
        if (fileName!=null){

            int index = fileName.lastIndexOf(".");

            if (index > 0 && index < (fileName.length() - 1)) {
                fileType = fileName.substring(index + 1);
            }
        }

        return fileType;
    }


    private void initExcelWriter(BatchContext context) {


        ExcelWriteSugar excelWriteSugar = new ExcelWriteSugar<>();
        excelWriteSugar.initWriter(context.getEnv().getOutputPath(), TextRowItem.class, "模型结果");

        context.getEnv().setExcelWriteSugar(excelWriteSugar);

    }

}
