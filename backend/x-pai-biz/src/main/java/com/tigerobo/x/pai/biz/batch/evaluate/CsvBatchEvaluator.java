package com.tigerobo.x.pai.biz.batch.evaluate;

import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.tigerobo.x.pai.biz.batch.offline.BatchContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CsvBatchEvaluator extends BaseEvaluator {

    public void deal(BatchContext batchContext) {
        File inputFile = new File(batchContext.getEnv().getInputPath());
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(inputFile);

        if (data != null) {
            int count = 0;
            boolean dealFirst = false;
            for (CsvRow item : data) {

                if (!dealFirst){
                    dealFirst = true;
                    continue;
                }

                int size = item.size();
                if (size <= 0) {
                    continue;
                }
                String text = item.get(0);
                if (StringUtils.isBlank(text)) {
                    continue;
                }
                count++;
                batchContext.getProcess().setCurrentDealNum(count);
                contextBatchProcess(batchContext,text);
                if (batchContext.getProcess().isUserStop()){
                    break;
                }
            }

            batchDoFinally(batchContext);
        }
    }

    public int getTotal(BatchContext batchContext){
        final int totalNum = batchContext.getProcess().getTotalNum();

        if (totalNum>0){
            return totalNum;
        }
        File inputFile = new File(batchContext.getEnv().getInputPath());
        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(inputFile);
        int count = 0;
        if (data != null) {
            boolean dealFirst = false;
            for (CsvRow item : data) {
                if (!dealFirst){
                    dealFirst = true;
                    continue;
                }
                int size = item.size();
                if (size <= 0) {
                    continue;
                }
                String text = item.get(0);
                if (StringUtils.isBlank(text)) {
                    continue;
                }
                count++;
            }
        }
        batchContext.getProcess().setTotalNum(count);
        return count;
    }
}
