package com.tigerobo.x.pai.biz.batch.offline;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.biz.batch.service.BatchApiService;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.dto.BatchCallDto;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.dal.biz.dao.offline.ModelBatchTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ModelBatchOfflineService {

    @Autowired
    private ModelBatchTaskDao modelBatchTaskDao;
    @Autowired
    private BatchApiService batchApiService;
    @Autowired
    private EsService esService;

    @Autowired
    private ApiCountService apiCountService;

    @Autowired
    private BatchItemService batchItemService;
    ExecutorService executorService = Executors.newFixedThreadPool(6);

    public void dealUnHandel(boolean test) {

        List<ModelBatchTaskPo> waitDealList = null;
        if (test) {
            waitDealList = modelBatchTaskDao.getWaitDealListTest();
        } else {
            waitDealList = modelBatchTaskDao.getWaitDealList();
        }

        if (CollectionUtils.isEmpty(waitDealList)) {
            return;
        }
        CountDownLatch latch = new CountDownLatch(waitDealList.size());
        waitDealList.stream().forEach(taskPo -> {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        batchItemService.deal(taskPo);
                    } catch (IllegalArgumentException ex) {
                        log.error("taskPo:id：{}", taskPo.getId(), ex);
                        updateFail(taskPo.getId(), ex.getMessage());
                    } catch (Exception ex) {
                        log.error("taskPo:id：{}", taskPo.getId(), ex);
                        updateFail(taskPo.getId(), "服务异常");
                    } finally {
                        latch.countDown();
                    }
                }
            });
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("batch-taskPo error", e);
        }
    }

    public void deal(ModelBatchTaskPo po) {
        String inputPath = po.getInputPath();
        String bizId = po.getBizId();
        Integer bizType = po.getBizType();

        ModelCallTypeEnum typeEnum = ModelCallTypeEnum.getByType(bizType);
        if (typeEnum == null) {
            throw new IllegalArgumentException("类型为空");
        }
        BatchCallDto evaluate = null;
        if (typeEnum == ModelCallTypeEnum.APP) {
            evaluate = batchApiService.evaluate(inputPath, bizId);
        } else {
            evaluate = batchApiService.amlBatchEvaluate(inputPath, bizId);
        }

        if (evaluate != null && StringUtils.isNotBlank(evaluate.getFilePath())) {

            updateSuccess(po.getId(), evaluate);
            addEs(po, typeEnum, evaluate);
            if (typeEnum == ModelCallTypeEnum.APP) {
                apiCountService.incrApiCall(bizId, evaluate.getDealNum());
            } else {
                apiCountService.incrAml(bizId, evaluate.getDealNum());
            }
        } else {
            updateFail(po.getId(), "处理文件失败");
        }
    }

    public void addEs(ModelBatchTaskPo po, ModelCallTypeEnum typeEnum, BatchCallDto evaluate) {
        String bizId = po.getBizId();
        EsModelCall call = new EsModelCall();
        call.setId(po.getReqId());
        call.setModelId(bizId);
        call.setUserId(po.getUserId());
        call.setContent(po.getInputPath());
        call.setSource(ModelCallSourceEnum.API_BATCH_EVALUATE.getType());

        call.setType(typeEnum.getType());
        call.setResult(evaluate.getFilePath());
        call.setDealTime(evaluate.getDealTime() * 1000L);
        call.setCallNum(evaluate.getDealNum());
        call.setIp(po.getIp());
//            call.setIp(null);
        esService.add(call);
    }

    private void updateSuccess(Integer id, BatchCallDto callDto) {

        ModelBatchTaskPo update = new ModelBatchTaskPo();

        update.setId(id);
        update.setStatus(1);

        update.setOutPath(callDto.getFilePath());
        update.setResult(JSON.toJSONString(callDto));

        update.setDealNum(callDto.getDealNum());
        update.setDealTime(callDto.getDealTime());
        update.setHasAppendCallNum(true);
        modelBatchTaskDao.update(update);
    }

    private void updateFail(Integer id, String msg) {

        try {
            ModelBatchTaskPo update = new ModelBatchTaskPo();

            update.setId(id);
            update.setStatus(2);
            update.setErrMsg(msg);
            modelBatchTaskDao.update(update);
        } catch (Exception ex) {
            log.error("updateFail-id:{}", id, ex);
        }

    }
}
