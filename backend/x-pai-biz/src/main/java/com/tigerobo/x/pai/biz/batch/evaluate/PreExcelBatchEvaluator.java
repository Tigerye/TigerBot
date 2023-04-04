package com.tigerobo.x.pai.biz.batch.evaluate;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.tigerobo.x.pai.biz.io.IOService;
import com.tigerobo.x.pai.biz.batch.offline.BatchContext;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
public class PreExcelBatchEvaluator {

    @Autowired
    private IOService ioService;


    public void deal(BatchContext context){
        final String preOssUrl = context.getProcess().getPreOssUrl();
        if (StringUtils.isBlank(preOssUrl)){
            context.getProcess().setPreDealNum(0);
            return;
        }
        String localPath = null;
        try {
            localPath = ioService.writeOssUrl2local(preOssUrl);
        }catch (Exception ex){
            log.error("batchId:{},ossNotExist",context.getInput().getBatchId(),ex);
        }
        if (StringUtils.isBlank(localPath)){
            context.getProcess().setPreDealNum(0);
            return;
        }
        context.getProcess().setPreResultPath(localPath);
        File inputFile = new File(context.getProcess().getPreResultPath());
        if (!inputFile.exists()||inputFile.length()==0){
            context.getProcess().setPreDealNum(0);
            return;
        }
        final ExcelListener readListener = new ExcelListener(context);
        EasyExcel.read(inputFile, readListener).headRowNumber(1).sheet(0).doRead();
    }

    private class ExcelListener extends AnalysisEventListener<LinkedHashMap<Integer, String>> {
        BatchContext bc;
        int count;

        boolean hasNext = true;

        List<TextRowItem> items = new ArrayList<>();
        ExcelListener(BatchContext context){
            this.bc = context;
        }

        @Override
        public void invoke(LinkedHashMap<Integer, String> input, AnalysisContext context) {

            if(input==null){
                hasNext = false;
                return;
            }


            if (input.isEmpty()){
                hasNext = false;
                return;
            }
            final String s = input.get(0);
            if (StringUtils.isBlank(s)){
                hasNext = false;
                return;
            }
            TextRowItem item = new TextRowItem();
            item.setText(s);

            if (input.size()>1){
                final String result = input.get(1);

                item.setResultText(result);
            }
            if (input.size()>2){
                final String s2 = input.get(2);
                item.setResultJson(s2);
            }

            count++;
            if (count>bc.getProcess().getPreDealNum()){
                hasNext = false;
                return;
            }
            items.add(item);
            batchDeal(bc,items,false);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {

            bc.getProcess().setPreDealNum(count);
            batchDeal(bc,items,true);
        }

        public boolean hasNext(AnalysisContext context) {
            return hasNext;
        }
    }

    private void batchDeal(BatchContext bc,List<TextRowItem> items,boolean isLast){

        if (items.size()>=10||isLast){
            if (items.size()>0){
                bc.getEnv().getExcelWriteSugar().write(items);

                final List<TextRowItem> resultPivot = bc.getResult().getResultPivot();

                for (TextRowItem item : items) {
                    if (resultPivot.size()<10){
                        resultPivot.add(item);
                    }
                }
                items.clear();
            }
        }
    }
}
