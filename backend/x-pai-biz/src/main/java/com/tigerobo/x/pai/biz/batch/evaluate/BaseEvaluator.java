package com.tigerobo.x.pai.biz.batch.evaluate;

import com.google.common.collect.Maps;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.biz.batch.offline.BatchContext;
import com.tigerobo.x.pai.biz.batch.offline.BatchResultHandle;
import com.tigerobo.x.pai.biz.batch.service.BatchProcessService;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.evaluate.entity.TextRowItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseEvaluator {
    @Autowired
    private BatchResultHandle batchResultHandle;

    @Autowired
    private BatchProcessService batchProcessService;

    public abstract void deal(BatchContext context);

    private void batchProcess(BatchContext context) {

        try {
            doBatchProcess(context);
        } catch (Exception e) {
            log.error("batchId:{}",context.getInput().getPo().getId(), e);
            throw new IllegalArgumentException("批量处理");
        }finally {
            context.getResult().getList().clear();
        }
    }

    private void doBatchProcess(BatchContext context) {
        final List<TextRowItem> list = context.getResult().getList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        final Executable apiExecutor = context.getEnv().getExecutor();
        Style style = Style.getByValue(apiExecutor.getApiStyle());
        if (style == null) {
            return;
        }
        Object object = modelReq(list, apiExecutor);

        context.getEnv().setApiResult(object);

        batchResultHandle.dealResult(context);
    }

    private Object modelReq(List<TextRowItem> list, Executable apiExecutor) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("text_list", list.stream().map(TextRowItem::getText).collect(Collectors.toList()));
        return apiExecutor.batchExecute(params);

    }


    protected void contextBatchProcess(BatchContext bc,String text){
        if (bc.getProcess().getCurrentDealNum()<=bc.getProcess().getPreDealNum()){
            return;
        }
        TextRowItem rowItem = new TextRowItem();
        rowItem.setText(text);
        bc.getResult().getList().add(rowItem);
        final int recordCnt = bc.getProcess().getCurrentDealNum();
        if (bc.getResult().getList().size() >= bc.getProcess().getBatchSize()) {
            batchProcess(bc);
            if (recordCnt %100==0){
                log.info("...xls-process: dealNum={}", recordCnt);
            }
        }

        if (recordCnt %100==0){
            final boolean suspend = batchProcessService.isSuspend(bc.getInput().getBatchId());
            if (suspend) {
                log.info("...xls-process: suspend,true,id={}",bc.getInput().getBatchId());
                bc.getProcess().setUserStop(true);
            }
            final Integer ratio = calRatio(bc);
            log.info("...batchId={},: ratio={}",bc.getInput().getBatchId(), ratio);
            batchProcessService.cacheProcessRatio(bc.getInput().getBatchId(),ratio);
        }

    }

    private Integer calRatio(BatchContext bc){
        final int currentDealNum = bc.getProcess().getCurrentDealNum();
        final int totalNum = bc.getProcess().getTotalNum();

        if (totalNum==0||currentDealNum==0){
            return null;
        }
        return currentDealNum*100/totalNum;
    }

    protected void batchDoFinally(BatchContext bc){
        batchProcess(bc);
    }

    public abstract int getTotal(BatchContext context);
}
