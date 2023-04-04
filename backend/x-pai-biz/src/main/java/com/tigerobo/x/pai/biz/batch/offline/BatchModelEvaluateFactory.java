package com.tigerobo.x.pai.biz.batch.offline;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.enums.Style;
import com.tigerobo.x.pai.biz.aml.AmlModelExecutor;
import com.tigerobo.x.pai.biz.lake.LakeInferService;
import com.tigerobo.x.pai.biz.serving.Executable;
import com.tigerobo.x.pai.biz.serving.ExecutorFactory;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BatchModelEvaluateFactory {

    @Autowired
    private AmlModelDao amlModelDao;
    @Autowired
    private LakeInferService lakeInferService;
    @Autowired
    private ExecutorFactory executorFactory;


    public Executable getModelExecutor(BatchContext context){

        final ModelBatchTaskPo po = context.getInput().getPo();
        final Integer bizType = po.getBizType();

        ModelCallTypeEnum typeEnum = ModelCallTypeEnum.getByType(bizType);
        if (typeEnum==null){
            throw new IllegalArgumentException("类型为空");
        }

        context.getInput().setTypeEnum(typeEnum);
        final String modelId = po.getBizId();
        if (typeEnum == ModelCallTypeEnum.APP){
            return evaluate(modelId);
        }else {
            return amlBatchEvaluate(modelId);
        }
    }

    private AmlModelExecutor amlBatchEvaluate(String apiKey) {
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
        return executor;
    }

    private Executable evaluate(String modelKey) {

        Preconditions.checkArgument(StringUtils.isNotBlank(modelKey), "模型id为空");
        Executable executable = executorFactory.get(modelKey);
        if (executable == null) {
            throw new IllegalArgumentException("模型不可用");
        }

        return executable;
    }


}
