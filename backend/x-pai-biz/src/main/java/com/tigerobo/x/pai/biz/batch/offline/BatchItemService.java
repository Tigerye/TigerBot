package com.tigerobo.x.pai.biz.batch.offline;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.enums.ModelCallSourceEnum;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.es.EsModelCall;
import com.tigerobo.x.pai.biz.aml.AmlModelExecutor;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.data.es.EsService;
import com.tigerobo.x.pai.biz.dto.BatchCallDto;
import com.tigerobo.x.pai.biz.io.IOService;
import com.tigerobo.x.pai.biz.batch.evaluate.BatchEvaluator;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.dal.biz.dao.offline.ModelBatchTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class BatchItemService {


    @Autowired
    private BatchModelEvaluateFactory batchEvaluateFactory;

    @Autowired
    private BatchEvaluator batchEvaluator;
    @Autowired
    private IOService ioService;
    @Autowired
    private OssService ossService;

    @Autowired
    ModelBatchTaskDao modelBatchTaskDao;
    @Autowired
    private EsService esService;

    @Autowired
    private MachineUtil machineUtil;
    @Autowired
    private ApiCountService apiCountService;

    public void deal(ModelBatchTaskPo po){
        String bizId = po.getBizId();

        BatchContext context = startContext(po);
        initModel(context);
        initEnv(context);

        batchEvaluator.evaluate(context);

        final String ossUrl = uploadResult2oss(context);

        context.getResult().setOssUrl(ossUrl);

        if (StringUtils.isNotBlank(ossUrl)){
            dealSuccess(context);
        }else {
            if (!context.getProcess().isUserStop()){
                updateFail(po.getId(),"处理文件失败");
            }
        }
    }

    private void dealSuccess(BatchContext context) {
        updateSuccess(context);

        final int dealNum = context.getResult().getDealNum();
        if (dealNum>0){
            addEs(context);
            final ModelCallTypeEnum typeEnum = context.getInput().getTypeEnum();
            String bizId = context.getInput().getPo().getBizId();
            if (typeEnum == ModelCallTypeEnum.APP){
                apiCountService.incrApiCall(bizId,dealNum);
            }else {
                apiCountService.incrAml(bizId, dealNum);
            }
        }
    }

    private void initModel(BatchContext context) {
        final Executable executor = batchEvaluateFactory.getModelExecutor(context);

        Validate.isTrue(executor!=null,"未找到模型");
        context.getEnv().setExecutor(executor);
        if (executor instanceof AmlModelExecutor){
            context.getProcess().setBatchSize(10);
        }else {
            final String apiStyle = executor.getApiStyle();
            if ("TEXT_TO_LABEL".equalsIgnoreCase(apiStyle)){
                context.getProcess().setBatchSize(5);
            }
        }
    }

    private BatchContext startContext(ModelBatchTaskPo po) {
        BatchContext context = new BatchContext();
        context.getInput().setPo(po);
        context.getInput().setBatchId(po.getId());

        final Integer dealNum = po.getDealNum();
        if (dealNum!=null&&dealNum>0){
            context.getProcess().setPreDealNum(dealNum);
            context.getProcess().setPreOssUrl(po.getOutPath());
        }

        if (po.getTotalNum()!=null){
            context.getProcess().setTotalNum(po.getTotalNum());
        }

        context.getProcess().setStartTime(System.currentTimeMillis());
        return context;
    }


    private void updateSuccess(BatchContext context){
        String ossUrl = context.getResult().getOssUrl();
//        final boolean userStop = context.getProcess().isUserStop();
        final ModelBatchTaskPo po = context.getInput().getPo();
        boolean dealMore = false;

        final int dealNum = getDealNum(context);

        boolean finish = context.getProcess().getCurrentDealNum()>=context.getProcess().getTotalNum();


        final Integer id = context.getInput().getBatchId();
        if (!finish&&dealNum<=0){
            log.warn("batchId:{},hasNoDeal",id);
            return;
        }

        ModelBatchTaskPo update = new ModelBatchTaskPo();

        update.setId(id);
        if (finish){
            update.setStatus(1);
        }


        if (StringUtils.isNotBlank(ossUrl)){
            update.setOutPath(ossUrl);
            final BatchCallDto batchCallDto = context.getResult().getBatchCallDto();
            batchCallDto.setPivot(context.getResult().getResultPivot());
            batchCallDto.setFilePath(ossUrl);
            batchCallDto.setStyle(context.getEnv().getExecutor().getApiStyle());
            update.setResult(JSON.toJSONString(batchCallDto));
        }


        update.setDealNum(context.getProcess().getCurrentDealNum());
        update.setTotalNum(context.getProcess().getTotalNum());


        final long startTime = context.getProcess().getStartTime();
        if (startTime>0){
            final int dealTime =(int) ((System.currentTimeMillis() - startTime)/1000);
            update.setDealTime(dealTime);
        }

        update.setHasAppendCallNum(true);
        modelBatchTaskDao.update(update);
    }

    private void initEnv(BatchContext context ){

        final ModelBatchTaskPo po = context.getInput().getPo();

        final String inputPath = getInputPath(po.getInputPath());

        context.getEnv().setInputPath(inputPath);
        final String outFileName = getOutFileName(context);
        context.getEnv().setOutFileName(outFileName);
        final String outputPath ="/tmp/" + outFileName;
        context.getEnv().setOutputPath(outputPath);

    }

    private String getOutFileName(BatchContext context){

        final String apiKey = context.getEnv().getExecutor().getApiKey();
        final String fileName = apiKey + "-" + System.currentTimeMillis() + ".xlsx";
        final String outPath = "/tmp/" + fileName;
        return outPath;
    }


    private String getInputPath(String filePath){
        if (filePath.startsWith("http")) {
            try {
                return ioService.writeXlsxFile(filePath);
            } catch (Exception e) {
                log.error("path:{}", filePath, e);
                throw new IllegalArgumentException("处理输入文件异常");
            }
        }
        return filePath;
    }

    private String uploadResult2oss(BatchContext context) {

        final String fileName = context.getEnv().getOutFileName();
        // 上传结果文件至OSS
        String ossPath = "application/model/evaluation/result/" + fileName;
        String uri = null;
        final String outputPath = context.getEnv().getOutputPath();
        try {

            final File file = new File(outputPath);
            if (!file.exists()){
                return null;
            }
            final Path path = file.toPath();
            uri = ossService.uploadXls(Files.readAllBytes(path), ossPath);
        } catch (IOException e) {
            log.error("batchId:{}", context.getInput().getBatchId(), e);
            throw new IllegalArgumentException("上传文件失败");
        }
        // 清楚本地文件

        ThreadUtil.executorService.execute(() -> {

            final File file = new File(outputPath);
            if (file.exists()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (file.exists()) {
                    file.delete();
                }
            }
        });
        return uri;
    }


    private void addEs(BatchContext context) {

        final int dealNum = context.getResult().getDealNum();
        final ModelBatchTaskPo po = context.getInput().getPo();
        String bizId = po.getBizId();
        EsModelCall call = new EsModelCall();
        final Long reqId = po.getReqId();

        final int machineId = machineUtil.getMachineId();
        final long id = IdGenerator.getId(machineId);

        call.setId(id);
        call.setModelId(bizId);
        call.setUserId(po.getUserId());
        call.setContent(po.getInputPath());
        call.setSource(ModelCallSourceEnum.API_BATCH_EVALUATE.getType());

        call.setType(context.getInput().getTypeEnum().getType());
        call.setResult(context.getResult().getOssUrl());

        final long dealTime = System.currentTimeMillis() - context.getProcess().getStartTime();

        call.setDealTime(dealTime);
        call.setCallNum(dealNum);
        call.setIp(po.getIp());
        call.setBizId(String.valueOf(reqId));
//            call.setIp(null);
        esService.add(call);
    }

    private int getDealNum(BatchContext context) {
        final int currentDealNum = context.getProcess().getCurrentDealNum();
        final int preDealNum = context.getProcess().getPreDealNum();
        final int dealNum = currentDealNum - preDealNum;

        context.getResult().setDealNum(dealNum);
        return dealNum;
    }


    private void updateFail(Integer id,String msg){

        try {
            ModelBatchTaskPo update = new ModelBatchTaskPo();

            update.setId(id);
            update.setStatus(2);
            update.setErrMsg(msg);
            modelBatchTaskDao.update(update);
        }catch (Exception ex){
            log.error("updateFail-id:{}",id,ex);
        }

    }
}
