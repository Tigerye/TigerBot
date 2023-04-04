package com.tigerobo.x.pai.engine.auto.ml.service;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.common.util.DownloadUtil;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyMetaDto;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyOpContentDto;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import com.tigerobo.x.pai.api.aml.enums.DatasetMetricEnum;
import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.biz.io.IOService;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import com.tigerobo.x.pai.dal.aml.dao.AmlDatasetDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.engine.auto.ml.pipeline.text.classify.TextClassifyPreparePipeline;
import com.tigerobo.x.pai.engine.exception.AmlException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmlDatasetProcessService {


    @Value("${engine.aml.nas.work.root}")
    private String OUT_ROOT_PATH;// = "/mnt/xpai/engine/test/aml/";

    @Autowired
    private AmlDatasetDao amlDatasetDao;
    @Autowired
    private AmlInfoDao amlInfoDao;

    @Autowired
    private IOService ioService;

    public void dealWaitProcessData(AmlInfoDo amlInfoDo,AmlDatasetDo amlDatasetDo){

        try {
            TextClassifyStatisticDto result = process(amlInfoDo, amlDatasetDo);

            if (result != null && !CollectionUtils.isEmpty(result.getLabelList())) {
                updateSuccess(amlInfoDo, amlDatasetDo, result);
            }else {
                updateFail(amlInfoDo,amlDatasetDo,"没有解析到数据");
            }
        }catch (Exception ex){
            log.error("amlId:{},dealWaitProcessData-error",amlInfoDo.getId(),ex);
            try {
                String msg = null;
                if (ex instanceof IllegalArgumentException | ex instanceof AmlException){
                    msg = ex.getMessage();
                }else {
                    msg = "解析异常";
                }
                updateFail(amlInfoDo, amlDatasetDo,msg);
            }catch (Exception subEx) {
                log.error("amlId:{},finally-updateFail",amlInfoDo.getId(),subEx);
            }
        }
    }

    private void updateSuccess(AmlInfoDo amlInfoDo, AmlDatasetDo amlDatasetDo, TextClassifyStatisticDto result) {
        AmlDatasetDo updateDataDo = new AmlDatasetDo();
        updateDataDo.setId(amlDatasetDo.getId());
        updateDataDo.setStatus(AmlStatusEnum.PROCESS_DATA_SUCCESS.getStatus());
        updateDataDo.setStatistic(JSON.toJSONString(result));

        List<TextClassifyStatisticDto.Item> metricList = result.getMetricList();

        if (metricList!=null){
            for (TextClassifyStatisticDto.Item item : metricList) {
                if (DatasetMetricEnum.TEST.getKey().equals(item.getKey())){
                    updateDataDo.setTestCount(item.getCount());
                }else if (DatasetMetricEnum.TRAIN.getKey().equals(item.getKey())){
                    updateDataDo.setTrainCount(item.getCount());
                }else if (DatasetMetricEnum.VALIDATION.getKey().equals(item.getKey())){
                    updateDataDo.setValidationCount(item.getCount());
                }else if (DatasetMetricEnum.LABELED.getKey().equals(item.getKey())){
                    updateDataDo.setLabeledCount(item.getCount());
                }else if (DatasetMetricEnum.ALL_ITEM.getKey().equals(item.getKey())){
                    updateDataDo.setAllItemCount(item.getCount());
                }else if (DatasetMetricEnum.UN_LABELED.getKey().equals(item.getKey())){
                    updateDataDo.setUnlabeledCount(item.getCount());
                }
            }

        }

        amlDatasetDao.update(updateDataDo);

        AmlInfoDo updateAmlInfo = new AmlInfoDo();
        updateAmlInfo.setId(amlInfoDo.getId());
        updateAmlInfo.setStatus(AmlStatusEnum.PROCESS_DATA_SUCCESS.getStatus());
        amlInfoDao.updateByPrimaryKey(updateAmlInfo);
    }

    private TextClassifyStatisticDto.Item getItem(List<TextClassifyStatisticDto.Item> list,String key){
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        for (TextClassifyStatisticDto.Item item : list) {
            if (item.getKey().equals(key)){
                return item;
            }
        }
        return null;
    }

    private void updateFail(AmlInfoDo amlInfoDo, AmlDatasetDo amlDatasetDo,String message) {
        if (amlInfoDo!=null){
            AmlDatasetDo updateDataDo = new AmlDatasetDo();
            updateDataDo.setId(amlDatasetDo.getId());
            updateDataDo.setStatus(AmlStatusEnum.PROCESS_DATA_FAIL.getStatus());
            if (StringUtils.isNotBlank(message)){
                updateDataDo.setMsg(message);
            }
            amlDatasetDao.update(updateDataDo);
        }

        AmlInfoDo updateAmlInfo = new AmlInfoDo();
        updateAmlInfo.setId(amlInfoDo.getId());
        updateAmlInfo.setStatus(AmlStatusEnum.PROCESS_DATA_FAIL.getStatus());

        if (StringUtils.isNotBlank(message)){
            updateAmlInfo.setMsg(message);
        }

        amlInfoDao.updateByPrimaryKey(updateAmlInfo);
    }


    private TextClassifyStatisticDto process(AmlInfoDo amlInfoDo, AmlDatasetDo amlDatasetDo) {
        if (amlDatasetDo == null|| amlInfoDo == null){
            return null;
        }
        Integer baseModelId = amlDatasetDo.getBaseModelId();
        String userId = String.valueOf(amlInfoDo.getCreateBy());
        String path = amlDatasetDo.getPath();
        AmlException.checkArgument(StringUtils.isNotBlank(path),"文件路径为空");

        List<FileData> fileDataList = JSON.parseArray(path, FileData.class);
        AmlException.checkArgument(!CollectionUtils.isEmpty(fileDataList),"文件路径为空");

        List<String> urlList = fileDataList.stream().filter(f -> StringUtils.isNotBlank(f.getFilePath())).map(f -> f.getFilePath()).distinct().collect(Collectors.toList());

        Integer datasetId = amlDatasetDo.getId();

        String workPath = getWorkPath(userId, datasetId);
        String oriDataPath = workPath+"origin/";
        String fileType = amlDatasetDo.getFileType();

        List<String> inputPathList = downloadUrls(urlList, oriDataPath, fileType);
        TextClassifyStatisticDto result = null;

        result = doProcess(baseModelId, datasetId, workPath, fileType, inputPathList);
        return result;
    }

    private List<String> downloadUrls(List<String> urlList, String oriDataPath, String fileType) {
        List<String> inputPathList = new ArrayList<>();
        for (String url : urlList) {
            String md5name = DigestUtils.md5Hex(url);
            String name = OssCombineUtil.getNameByUrl(url);
            String suffix = "";
            if (StringUtils.isNotBlank(name)){
                int index = name.lastIndexOf(".");
                if (index>0){
                    suffix = name.substring(index);
                }

            }

            if (StringUtils.isBlank(suffix)){
                if ("csv".equalsIgnoreCase(fileType)){
                    suffix = ".csv";
                }else if ("xls".equalsIgnoreCase(fileType)){
                    suffix = ".xlsx";
                }
            }
            String savePath = oriDataPath + md5name+suffix;
            ioService.writeOssUrl2local(url, savePath);
            inputPathList.add(savePath);
        }
        return inputPathList;
    }

    private TextClassifyStatisticDto doProcess(Integer baseModelId, Integer datasetId, String workPath, String fileType, List<String> inputPathList) {
        if (baseModelId == 1){
            TextClassifyPreparePipeline pipeline = new TextClassifyPreparePipeline();

            TextClassifyMetaDto metaDto = new TextClassifyMetaDto();
            metaDto.setDatasetId(String.valueOf(datasetId));
            metaDto.setInputPathList(inputPathList);
            metaDto.setFileType(fileType);

            TextClassifyOpContentDto opContent = new TextClassifyOpContentDto();
            opContent.setOutputRoot(workPath);

            return pipeline.run(metaDto, opContent);
//            if ("csv".equalsIgnoreCase(fileType)){
//
//            }else {
//                throw new AmlException("当前文件类型未支持");
//            }
        }else {
            throw new AmlException("当前模型尚未支持");
        }
    }

    private String getWorkPath(String userId,Integer datasetId){
        return OUT_ROOT_PATH+"u"+userId + "/dataset/"+datasetId+"/";
    }
}
