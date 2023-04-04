package com.tigerobo.x.pai.biz.aml.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.tigerobo.x.pai.api.aml.constant.AmlDatasetConstant;
import com.tigerobo.x.pai.api.aml.dto.AmlDatasetDto;
import com.tigerobo.x.pai.api.aml.dto.AmlStatisticDto;
import com.tigerobo.x.pai.api.aml.dto.ModelDataItem;
import com.tigerobo.x.pai.api.aml.dto.dataset.AmlDatasetItemReq;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import com.tigerobo.x.pai.api.aml.enums.AmlDataStatisticTypeEnum;
import com.tigerobo.x.pai.api.aml.enums.DatasetMetricEnum;
import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.biz.aml.convert.AmlDatasetConverter;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.biz.oss.OssCacheService;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import com.tigerobo.x.pai.dal.aml.dao.AmlDatasetDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmlDatasetServiceImpl {


    @Autowired
    private AmlDatasetDao amlDatasetDao;
    @Autowired
    private OssCacheService ossCacheService;
    public AmlDatasetDto getDataset(Integer id){

        AmlDatasetDo datasetDo = amlDatasetDao.getById(id);

        AmlDatasetDto amlDatasetDto = AmlDatasetConverter.po2dto(datasetDo);

        if (amlDatasetDto!=null){
            final List<FileData> datasetFileList = amlDatasetDto.getDatasetFileList();
            if (!CollectionUtils.isEmpty(datasetFileList)){
                for (FileData fileData : datasetFileList) {
                    final String filePath = fileData.getFilePath();
                    final String keyByUrl = OssCombineUtil.getKeyByUrl(filePath);
                    if (StringUtils.isNotBlank(keyByUrl)){
                        final String privateKeyUrl = ossCacheService.getPrivateKeyUrl(keyByUrl);
                        if (StringUtils.isNotBlank(privateKeyUrl)){
                            fileData.setFilePath(privateKeyUrl);
                        }
                    }
                }

            }
        }
        return amlDatasetDto;
    }



    public AmlDatasetDo get(Integer id){

        return amlDatasetDao.getById(id);
    }


    public AmlStatisticDto getAmlDatasetStatistic(Integer datasetId){
        if (datasetId == null){
            return null;
        }
        AmlDatasetDo dataset = amlDatasetDao.getById(datasetId);
        if (dataset == null){
            return null;
        }
        if (dataset.getStatus() == null||!AmlStatusEnum.PROCESS_DATA_SUCCESS.getStatus().equals(dataset.getStatus())){
            return null;
        }

        String statisticJson = dataset.getStatistic();
        TextClassifyStatisticDto dbStatistic = null;
        if (StringUtils.isNotBlank(statisticJson)){
            dbStatistic = JSON.parseObject(statisticJson, TextClassifyStatisticDto.class);
        }

        AmlStatisticDto statisticDto = new AmlStatisticDto();
        List<AmlStatisticDto.Item> targetMetricList = new ArrayList<>();
        statisticDto.setMetricList(targetMetricList);
        List<AmlStatisticDto.Item> targetLabelList = new ArrayList<>();
        statisticDto.setLabelList(targetLabelList);
        String statisticDesc = completeDetail(dbStatistic, statisticDto, targetMetricList, targetLabelList);
        boolean canTrain = StringUtils.isEmpty(statisticDesc);
        statisticDto.setCanTrain(canTrain);
        return statisticDto;
    }

    private String completeDetail(TextClassifyStatisticDto dbStatistic, AmlStatisticDto statisticDto,
                                  List<AmlStatisticDto.Item> targetMetricList,
                                  List<AmlStatisticDto.Item> targetLabelList) {
        String statisticDesc = "";
        if (dbStatistic !=null){
            Integer labeledCount = completeMetric(dbStatistic, targetMetricList);
            completeStatisticLabel(dbStatistic, targetLabelList, labeledCount);
            statisticDesc = checkStatistic(dbStatistic);
            List<TextClassifyStatisticDto.SentenceLengthCount> sentenceLengthCountList = dbStatistic.getSentenceLengthCountList();

            statisticDto.setSentenceLengthRegionCountList(sentenceLengthCountList);

            statisticDto.setRegionList(AmlDatasetConstant.getSentenceLengthCountList());
            ArrayList<AmlStatisticDto.SentenceLengthRegion> sentenceLengthRegions = getSentenceLengthRegions(statisticDto, sentenceLengthCountList);
            statisticDto.setSentenceLengthCountList(sentenceLengthRegions);
        }
        statisticDto.setStatisticDesc(statisticDesc);
        return statisticDesc;
    }


    private ArrayList<AmlStatisticDto.SentenceLengthRegion> getSentenceLengthRegions(AmlStatisticDto statisticDto, List<TextClassifyStatisticDto.SentenceLengthCount> sentenceLengthCountList) {
        ArrayList<AmlStatisticDto.SentenceLengthRegion> sentenceLengthRegions = new ArrayList<>();

        if (CollectionUtils.isEmpty(sentenceLengthCountList)){
            return sentenceLengthRegions;
        }
        for (TextClassifyStatisticDto.SentenceLengthCount sentenceLengthCount : sentenceLengthCountList) {
            String name = "";
            int start = sentenceLengthCount.getStart();
            if (start<500){
                name = start+"-"+(start+20);
            }else if (start == 500){
                name = 500+"-"+1000;
            }else {
                name =">"+1000;
            }
            AmlStatisticDto.SentenceLengthRegion lengthRegion = statisticDto.new SentenceLengthRegion();
            lengthRegion.setCount(sentenceLengthCount.getCount());
            lengthRegion.setName(name);
            sentenceLengthRegions.add(lengthRegion);
        }
        return sentenceLengthRegions;
    }

    private Integer completeMetric(TextClassifyStatisticDto dbStatistic,
                                   List<AmlStatisticDto.Item> targetMetricList) {
        List<TextClassifyStatisticDto.Item> metricList = dbStatistic.getMetricList();
        Integer labeledCount = null;
        if (!CollectionUtils.isEmpty(metricList)){

            for (TextClassifyStatisticDto.Item item : metricList) {
                String key = item.getKey();
                DatasetMetricEnum metricEnum = DatasetMetricEnum.getByKey(key);
                if (metricEnum != null){
                    AmlStatisticDto.Item metricItem = new AmlStatisticDto.Item();
                    metricItem.setKey(metricEnum.getKey());
                    metricItem.setName(metricEnum.getName());
                    Integer count = item.getCount();
                    metricItem.setCount(count==null?0:count);
                    targetMetricList.add(metricItem);
                    if (DatasetMetricEnum.LABELED==metricEnum){
                        labeledCount = count;
                    }
                }
            }
            if (labeledCount!=null){
                for (AmlStatisticDto.Item item : targetMetricList) {
                    String key = item.getKey();
                    if (DatasetMetricEnum.ALL_ITEM.getKey().equals(key)||DatasetMetricEnum.UN_LABELED.getKey().equals(key)){
                        continue;
                    }
                    int count = item.getCount();
                    String rate = calRate(count, labeledCount);
                    item.setRate(rate);
                }
            }
        }
        return labeledCount;
    }

    private void completeStatisticLabel(TextClassifyStatisticDto dbStatistic, List<AmlStatisticDto.Item> targetLabelList, Integer labeledCount) {
        List<TextClassifyStatisticDto.Item> labelList = dbStatistic.getLabelList();
        if (!CollectionUtils.isEmpty(labelList)){
            for (TextClassifyStatisticDto.Item item : labelList) {
                if (item!=null){
                    AmlStatisticDto.Item labelItem = new AmlStatisticDto.Item();
                    labelItem.setKey(item.getKey());
                    labelItem.setName(item.getKey());
                    Integer count = item.getCount();
                    labelItem.setCount(count==null?0:count);
                    String rate = calRate(count, labeledCount);
                    labelItem.setRate(rate);
                    targetLabelList.add(labelItem);
                }
            }
            targetLabelList.sort(Comparator.comparing(AmlStatisticDto.Item::getCount).reversed());
        }
    }


    private String checkStatistic(TextClassifyStatisticDto statisticDto){
        if (statisticDto == null){
            return "没有数据";
        }

        List<TextClassifyStatisticDto.Item> metricList = statisticDto.getMetricList();

        if (CollectionUtils.isEmpty(metricList)){
            return "没有数据";
        }
        Map<String, TextClassifyStatisticDto.Item> keyMap = metricList.stream()
                .collect(Collectors.toMap(m -> m.getKey(), m -> m));

        TextClassifyStatisticDto.Item labelItem = keyMap.get(DatasetMetricEnum.LABELED.getKey());
        if (labelItem == null||labelItem.getCount()==null||labelItem.getCount()<100){
            return "打标数量不能少于100条";
        }
        List<TextClassifyStatisticDto.Item> labelList = statisticDto.getLabelList();
        if (CollectionUtils.isEmpty(labelList)){
            return "打标数量不能少于100条";
        }

        for (TextClassifyStatisticDto.Item item : labelList) {
            String key = item.getKey();
            Integer count = item.getCount();
            if (count==null||count<10){
                return "每个标签下的文本数量不少于10条，如标签："+key+" "+(count==null?0:count)+"条";
            }
        }
        return null;
    }

    private String calRate(Integer preCount,Integer totalCount){
        if (preCount == null||totalCount == null){
            return null;
        }
        int value = BigDecimal.valueOf(preCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount),0,RoundingMode.HALF_UP).intValue();
        String rate = value+"%";
        return rate;
    }


    public PageInfo<ModelDataItem> getNasDatasetViewPage(AmlDatasetItemReq requestVo, AmlDatasetDo dataset){

        if (dataset == null){
            return null;
        }
        if (dataset.getStatus() == null||AmlStatusEnum.PROCESS_DATA_SUCCESS.getStatus()!=dataset.getStatus()){
            return null;
        }
        String jsonPath = null;
        String statisticJson = dataset.getStatistic();
        TextClassifyStatisticDto dbStatistic = null;
        if (StringUtils.isNotBlank(statisticJson)){
            dbStatistic = JSON.parseObject(statisticJson, TextClassifyStatisticDto.class);
        }
        int total=0;
        if (dbStatistic!=null){
            String statisticType = requestVo.getStatisticTypeEnum();

            AmlDataStatisticTypeEnum statisticTypeEnum = AmlDataStatisticTypeEnum.valueOf(statisticType);
            String searchKey = requestVo.getSearchKey();
            if (StringUtils.isEmpty(searchKey)){
                searchKey = DatasetMetricEnum.ALL_ITEM.getKey();
            }
            if (statisticTypeEnum == null){
                statisticTypeEnum = AmlDataStatisticTypeEnum.METRIC;
            }


            if (AmlDataStatisticTypeEnum.METRIC== statisticTypeEnum){
                List<TextClassifyStatisticDto.Item> metricList = dbStatistic.getMetricList();
                if (!CollectionUtils.isEmpty(metricList)){
                    for (TextClassifyStatisticDto.Item item : metricList) {
                        if (searchKey.equals(item.getKey())){
                            jsonPath = item.getPath();
                            total = item.getCount();
                        }
                    }
                }
            }else {
                List<TextClassifyStatisticDto.Item> labelList = dbStatistic.getLabelList();
                if (!CollectionUtils.isEmpty(labelList)){
                    for (TextClassifyStatisticDto.Item item : labelList) {
                        if (searchKey.equals(item.getKey())){
                            jsonPath = item.getPath();
                            total = item.getCount();
                        }
                    }
                }
            }
        }

        List<ModelDataItem> modelDataItems = new ArrayList<>();
        if (StringUtils.isNotBlank(jsonPath)){
            Integer pageNum = requestVo.getPageNum();
            Integer pageSize = requestVo.getPageSize();
            int start = (pageNum-1)*pageSize;
            int end = pageNum*pageSize;
            List<String> itemList = readFile(dataset.getId(), jsonPath, start, end);

            if (!CollectionUtils.isEmpty(itemList)){
                for (String s : itemList) {
                    ModelDataItem modelDataItem = JSON.parseObject(s,ModelDataItem.class);
                    if (modelDataItem!=null){
                        modelDataItems.add(modelDataItem);
                    }
                }
            }
        }
        PageInfo<ModelDataItem> pageInfo = new PageInfo<>(modelDataItems);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(requestVo.getPageNum());
        pageInfo.setPageSize(requestVo.getPageSize());
        return pageInfo;
    }

    private List<String> readFile(int datasetId,String path,int start,int end){
        File file = new File(path);
        if (!file.exists()){
            return null;
        }
        List<String> list = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))){

            int index=0;
            String line = null;
            while ((line=reader.readLine())!=null){
                if (index>=start&&index<end){
                    list.add(line);
                }
                index++;
                if (index>=end){
                    break;
                }
            }
        }catch (Exception ex){
            log.error("Aml-dataset:{},read-file:{},start:{},end:{}",datasetId,path,start,end,ex);
        }
        return list;
    }

    public List<AmlDatasetDto> getByIds(List<Integer> dataIds) {
        if (CollectionUtils.isEmpty(dataIds)){
            return null;
        }
        List<AmlDatasetDo> doList = amlDatasetDao.getByIds(dataIds);

        return AmlDatasetConverter.po2dtoList(doList);
    }
}
