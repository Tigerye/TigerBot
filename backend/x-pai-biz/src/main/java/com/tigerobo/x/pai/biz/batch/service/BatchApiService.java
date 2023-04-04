package com.tigerobo.x.pai.biz.batch.service;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.BatchTaskEnum;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.model.ModelBatchQueryReq;
import com.tigerobo.x.pai.api.vo.model.ModelBatchTaskVo;
import com.tigerobo.x.pai.biz.aml.AmlModelExecutor;
import com.tigerobo.x.pai.biz.batch.offline.TextEvaluator;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.converter.ModelBatchConvert;
import com.tigerobo.x.pai.biz.dto.BatchCallDto;
import com.tigerobo.x.pai.biz.io.IOService;
import com.tigerobo.x.pai.biz.lake.LakeInferService;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.ThreadUtil;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.biz.dao.offline.ModelBatchTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BatchApiService {
    @Autowired
    private ExecutorFactory executorFactory;

    @Autowired
    private IOService ioService;

    @Autowired
    private OssService ossService;
    @Autowired
    private ModelBatchTaskDao modelBatchTaskDao;
    @Autowired
    private MachineUtil machineUtil;
    @Autowired
    private AmlModelDao amlModelDao;
    @Autowired
    private LakeInferService lakeInferService;

    @Autowired
    private BatchProcessService batchProcessService;

    public PageVo<ModelBatchTaskVo> getUserPage(ModelBatchQueryReq req) {


        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        Integer bizType = req.getBizType();
        if (bizType == null || bizType < 1 || bizType > 2) {
            throw new IllegalArgumentException("bizType不正确");
        }
        Page<ModelBatchTaskPo> taskPos = (Page<ModelBatchTaskPo>) modelBatchTaskDao.getUserList(userId, req.getApiKey(), bizType, req.getPageNum(), req.getPageSize());
        PageVo<ModelBatchTaskVo> pageVo = new PageVo<>();

        if (CollectionUtils.isEmpty(taskPos)) {
            return pageVo;
        }

        List<ModelBatchTaskVo> vos =buildVos(taskPos);
        pageVo.setTotal(pageVo.getTotal());
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setList(vos);
        return pageVo;
    }

    private List<ModelBatchTaskVo> buildVos(List<ModelBatchTaskPo> pos) {
        if (CollectionUtils.isEmpty(pos)) {
            return new ArrayList<>();
        }

        List<ModelBatchTaskVo> vos = new ArrayList<>();
        for (ModelBatchTaskPo po : pos) {
            final ModelBatchTaskVo vo = ModelBatchConvert.convert(po);
            final Integer status = vo.getStatus();
            final BatchTaskEnum batchTaskEnum = BatchTaskEnum.getByStatus(status);

            final Integer id = po.getId();

            String processText = "";

            final Integer dbRatio = calRate(po.getDealNum(), po.getTotalNum());
            if (batchTaskEnum == BatchTaskEnum.WAIT_DEAL) {
                final Integer processRatio = batchProcessService.getProcessRatio(id);
                final Integer max = getMax(dbRatio, processRatio);
                if (max == null || max.equals(0)) {
                    processText = BatchTaskEnum.WAIT_DEAL.getText();
                } else {
                    processText = BatchTaskEnum.WAIT_DEAL.getText() + ":" + max + "%";
                }
            } else if (batchTaskEnum == BatchTaskEnum.SUSPEND) {
                if (dbRatio == null || dbRatio.equals(0)) {
                    processText = BatchTaskEnum.SUSPEND.getText();
                } else {
                    processText = BatchTaskEnum.SUSPEND.getText() + ":" + dbRatio + "%";
                }
            } else if (batchTaskEnum == BatchTaskEnum.ERROR) {
                if (StringUtils.isBlank(vo.getErrMsg())) {
                    processText = BatchTaskEnum.ERROR.getText();
                } else {
                    processText = BatchTaskEnum.ERROR.getText() + vo.getErrMsg();
                }
            } else if (batchTaskEnum == BatchTaskEnum.FINISH) {
                processText = BatchTaskEnum.FINISH.getText();
            }
            vo.setProcessText(processText);
            vos.add(vo);
        }
        return vos;

    }

    private Integer getMax(Integer pre, Integer next) {
        if (pre == null && next == null) {
            return null;
        }
        if (pre == null) {
            return next;
        }
        if (next == null) {
            return pre;
        }
        return Math.max(pre, next);
    }

    private Integer calRate(Integer dbDealNum, Integer totalNum) {

        if (totalNum == null || totalNum.equals(0)) {
            return null;
        }

        if (dbDealNum == null || dbDealNum.equals(0)) {
            return null;
        }

        return dbDealNum * 100 / totalNum;
    }

    public ModelBatchTaskVo getByReqId(Long reqId) {

        ModelBatchTaskPo taskPo = modelBatchTaskDao.getByReqId(reqId);
        return ModelBatchConvert.convert(taskPo);
    }

    public ResultVO addTask(ApiReqVo reqVo) {
        Integer userId = reqVo.getUserId();
        if (userId == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        Integer bizType = reqVo.getBizType();
        Preconditions.checkArgument(bizType != null, "bizType未设定");
        Preconditions.checkArgument(bizType >= 1 && bizType <= 2, "bizType不正确");


        String filePath = reqVo.getString("filePath", null);

        String fileName = reqVo.getString("fileName", null);

        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("未传文件");
        }
        // 构造评估器 TextExcelEvaluator
        String apiKey = reqVo.getApiKey();
        String modelName = "";
        if (bizType.equals(1)) {
            Executable executable = this.executorFactory.get(apiKey);
            if (executable == null) {
                throw new IllegalArgumentException("没有可用模型");
            }
            modelName = executable.getName();
        } else if (bizType.equals(2)) {
            Preconditions.checkArgument(apiKey.matches("\\d+"), "模型不存在");
            int modelId = Integer.parseInt(apiKey);
            AmlModelDo model = amlModelDao.getById(modelId);
            Preconditions.checkArgument(model != null, "模型不存在");
            Preconditions.checkArgument(model.getServiceStatus() == 1, "模型服务不可用");
            modelName = model.getName();
        }

        ModelBatchTaskPo po = new ModelBatchTaskPo();
        po.setUserId(userId);

        po.setBizName(modelName);
        po.setBizId(apiKey);
        po.setBizType(bizType);
        po.setInputPath(filePath);
        po.setFileName(fileName);

        long reqId = IdGenerator.getId(machineUtil.getMachineId());
        po.setReqId(reqId);

        String ip = ThreadLocalHolder.getIp();
        po.setIp(ip);
        modelBatchTaskDao.add(po);
        //todo

        Map<String, Object> data = new HashMap<>();
        data.put("reqId", po.getReqId());
        return new ResultVO(data);
    }


    public BatchCallDto amlBatchEvaluate(String filePath, String apiKey) {

        Preconditions.checkArgument(apiKey.matches("\\d+"), "模型不存在");
        //todo 校验appId与模型用户相同
        int modelId = Integer.parseInt(apiKey);
        AmlModelDo model = amlModelDao.getById(modelId);
        Preconditions.checkArgument(model != null, "模型不存在");
        Preconditions.checkArgument(model.getServiceStatus() == 1, "模型服务不可用");

        Preconditions.checkArgument(!org.springframework.util.StringUtils.isEmpty(model.getStyle()), "模型服务不可用");
        AmlModelExecutor executor = new AmlModelExecutor();

        executor.setModelUrl(model.getModelUrl());
        executor.setApiKey(String.valueOf(modelId));
        executor.setName(model.getName());
        Style apiStyle = Style.valueOf(model.getStyle());
        Preconditions.checkArgument(apiStyle != Style.UNKNOWN, "模型服务不可用");
        executor.setApiStyle(model.getStyle());
        executor.setLakeInferService(lakeInferService);

        return doBatchCall(filePath, executor);
    }

    public BatchCallDto evaluate(String filePath, String modelKey) {

        if (StringUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("输入文件为空");
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(modelKey), "模型id为空");
        Executable executable = executorFactory.get(modelKey);
        if (executable == null) {
            throw new IllegalArgumentException("模型不可用");
        }

        return doBatchCall(filePath, executable);
    }

    private BatchCallDto doBatchCall(String filePath, Executable executable) {
        TextEvaluator evaluator = TextEvaluator.create(executable);

        // 获取文件并评估
        long startTime = System.currentTimeMillis();
        final String fileName = executable.getApiKey() + "-" + System.currentTimeMillis() + ".xlsx";
        final String outPath = "/tmp/" + fileName;
        File outputFile = new File(outPath);
        if (filePath.startsWith("http")) {

            String inputPath = null;
            try {
                inputPath = ioService.writeXlsxFile(filePath);
            } catch (Exception e) {
                log.error("path:{}", filePath, e);
                throw new IllegalArgumentException("处理输入文件异常");
            }
//            pivot = evaluator.evaluate(this.ossApi.download(new URL(filePath)), outputFile);

            evaluator.evaluate(new File(inputPath), outputFile);
        } else if (filePath.startsWith("/") && new File(filePath).exists()) {
            // 本地文件
            evaluator.evaluate(new File(filePath), outputFile);
        }

        long t2 = System.currentTimeMillis();
        log.info("evaluate: execute cost {} ms", t2 - startTime);
        // 上传结果文件至OSS
        String ossPath = "application/model/evaluation/result/" + fileName;
        String uri = null;
        try {
            uri = ossService.uploadXls(Files.readAllBytes(outputFile.toPath()), ossPath);
        } catch (IOException e) {
            log.error("batch,key-{},-outPath:{}", executable.getApiKey(), outputFile, e);
            throw new IllegalArgumentException("上传文件失败");
        }

        long t3 = System.currentTimeMillis();
        log.info("evaluate: upload cost {} ms", t3 - t2);
        // 清楚本地文件

        ThreadUtil.executorService.execute(new Runnable() {
            @Override
            public void run() {

                final File file = new File(outPath);
                if (file.exists()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        });

//        uri = uri.replaceAll("oss-cn-shanghai-internal", "oss-cn-shanghai");
        BatchCallDto batchCallDto = new BatchCallDto();

        batchCallDto.setFilePath(uri);
        batchCallDto.setStyle(executable.getApiStyle());
        batchCallDto.setPivot(evaluator.getResultPivot());

        int recordCnt = evaluator.getRecordCnt();
        batchCallDto.setDealNum(recordCnt);

        int dealTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        batchCallDto.setDealTime(dealTime);
        return batchCallDto;
    }

}
