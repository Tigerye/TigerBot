package com.tigerobo.x.pai.biz.batch.evaluate;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tigerobo.x.pai.biz.batch.offline.BatchContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedHashMap;

@Component
public class ExcelBatchEvaluator extends BaseEvaluator {

    @Override
    public void deal(BatchContext context){
        File inputFile = new File(context.getEnv().getInputPath());
        final ExcelListener readListener = new ExcelListener(context);
        EasyExcel.read(inputFile, readListener).headRowNumber(1).sheet(0).doRead();
    }

    @Override
    public int getTotal(BatchContext context){
        final int totalNum = context.getProcess().getTotalNum();
        if (totalNum>0){
            return totalNum;
        }
        File inputFile = new File(context.getEnv().getInputPath());
        final ExcelListener readListener = new ExcelListener(context,true);
        EasyExcel.read(inputFile, readListener).headRowNumber(1).sheet(0).doRead();
        return context.getProcess().getTotalNum();
    }

    private class ExcelListener extends AnalysisEventListener<LinkedHashMap<Integer, String>> {
        BatchContext bc;
        int count;
        boolean countTotal;
        boolean hasNext = true;

        ExcelListener(BatchContext context){
            this.bc = context;
        }
        ExcelListener(BatchContext context,boolean countTotal){
            this.bc = context;
            this.countTotal = countTotal;
        }
        @Override
        public void invoke(LinkedHashMap<Integer, String> input, AnalysisContext context) {

            String sentence = input.get(0);
            if (StringUtils.isBlank(sentence)) {
                return;
            }
            count++;

            if (countTotal){
                return;
            }

            bc.getProcess().setCurrentDealNum(count);
            contextBatchProcess(bc,sentence);

            if (bc.getProcess().isUserStop()){
                hasNext = false;
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {

            batchDoFinally(bc);
            if (countTotal){
                bc.getProcess().setTotalNum(count);
            }
        }


        public boolean hasNext(AnalysisContext context) {
            return hasNext;
        }
    }
}
