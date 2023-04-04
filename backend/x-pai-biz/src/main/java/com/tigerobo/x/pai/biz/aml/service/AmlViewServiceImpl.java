package com.tigerobo.x.pai.biz.aml.service;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.dto.*;
import com.tigerobo.x.pai.api.aml.dto.dataset.AmlDatasetItemReq;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaSentence;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaluationConfidenceDto;
import com.tigerobo.x.pai.api.aml.engine.dto.train.TrainResultDto;
import com.tigerobo.x.pai.api.aml.req.AmlConfidenceEvaluationReq;
import com.tigerobo.x.pai.api.aml.service.AmlViewService;
import com.tigerobo.x.pai.api.aml.vo.AmlTrainViewVo;
import com.tigerobo.x.pai.api.aml.vo.MyAmlQueryVo;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.aml.convert.AmlBaseModelConverter;
import com.tigerobo.x.pai.biz.aml.convert.AmlInfoConverter;
import com.tigerobo.x.pai.dal.aml.dao.AmlBaseModelDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlDatasetDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlInfoDao;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlBaseModelDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AmlViewServiceImpl implements AmlViewService {

    @Autowired
    private AmlBaseModelDao amlBaseModelDao;
    @Autowired
    private AmlDatasetDao amlDatasetDao;
    @Autowired
    private AmlInfoDao amlInfoDao;
    @Autowired
    private AmlModelDao amlModelDao;

    @Autowired
    private AmlDatasetServiceImpl amlDatasetService;
    @Autowired
    private AmlEvaluationServiceImpl amlTrainEvaluationService;

    @Autowired
    private AmlModelServiceImpl amlModelService;
    @Autowired
    private AmlBaseModelServiceImpl amlBaseModelService;
    @Autowired
    private UserDao userDao;


    public PageInfo<AmlInfoDto> getMyAmlList(MyAmlQueryVo queryVo,String userId){
        PageInfo<AmlInfoDo> pageInfoDo = amlInfoDao.getMyAmlList(userId, queryVo.getPageNum(), queryVo.getPageSize(), queryVo.getOrderBy());
        List<AmlInfoDo> list = pageInfoDo.getList();
        long total = pageInfoDo.getTotal();
        List<AmlInfoDto> dtos = AmlInfoConverter.do2dtoList(list);

        if (!CollectionUtils.isEmpty(dtos)){
            for (AmlInfoDto dto : dtos) {
                completeAmlDetail(dto);
            }
        }

        PageInfo<AmlInfoDto> dtoPageInfo = new PageInfo<>(dtos);
        dtoPageInfo.setTotal(total);
        dtoPageInfo.setPageNum(queryVo.getPageNum());
        dtoPageInfo.setPageSize(queryVo.getPageSize());
        return dtoPageInfo;
    }


    public PageInfo<AmlModelDto> getMyModelList(MyAmlQueryVo queryVo, String userId){

        return amlModelService.getMyModelList(queryVo,userId);
    }


    private void completeAmlDetail(AmlInfoDto amlInfo){

        if (amlInfo == null){
            return ;
        }
        Integer baseModelId = amlInfo.getBaseModelId();

        AmlBaseModelDto baseModelDto = amlBaseModelService.getBaseModel(baseModelId);
        amlInfo.setBaseModel(baseModelDto);

        completeImportData(amlInfo);
        Integer currentModelId = amlInfo.getCurrentModelId();
        if (currentModelId!=null&&currentModelId>0){
            AmlModelDto modelDo = amlModelService.getModelById(currentModelId);
            if (modelDo!=null){
                amlInfo.setEpoch(modelDo.getEpoch());
                amlInfo.setTotalEpoch(modelDo.getTotalEpoch());
                amlInfo.setErrMsg(modelDo.getErrMsg());
            }
            amlInfo.setAmlModel(modelDo);
        }
        if (AmlStatusEnum.PROCESS_DATA_FAIL.getStatus().equals(amlInfo.getStatus())){
            amlInfo.setErrMsg(amlInfo.getMsg());
        }

        if (amlInfo.getParentModelId()!=null&&amlInfo.getParentModelId()>0){
            final AmlModelDto parentModel = amlModelService.getAmlModelDto(amlInfo.getParentModelId());
            amlInfo.setParentModel(parentModel);
        }
    }


    public List<AmlBaseModelDto> getBaseModelList(){
        List<AmlBaseModelDo> all = amlBaseModelDao.getAll();
        return AmlBaseModelConverter.do2dtoList(all);
    }


    public AmlModelDto userGetModelById(Integer id,Integer userId){

        return amlModelService.userGetModelById(id,userId);
    }


    public AmlInfoDto getAmlInfo(Integer id, Integer userId){
        AmlInfoDo amlInfoDo = amlInfoDao.getById(id);

        checkAmlUser(userId, amlInfoDo);

        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        completeAmlDetail(amlInfoDto);
        return amlInfoDto;
    }

    @Deprecated

    public AmlInfoDto getAmlImportView(Integer amlId, Integer userId){

        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);

        completeImportData(amlInfoDto);
        return amlInfoDto;
    }

    private void completeImportData( AmlInfoDto amlInfo) {
        Integer currentDataId = amlInfo.getCurrentDataId();
        if (currentDataId!=null&&currentDataId.compareTo(0)>0){

//            AmlDatasetDo dataset = amlDatasetDao.getById(currentDataId);

            final AmlDatasetDto dataset = amlDatasetService.getDataset(currentDataId);
//            AmlDatasetDto amlDatasetDto = AmlDatasetConverter.po2dto(dataset);
            amlInfo.setDataset(dataset);

            if (dataset != null){
                String path = dataset.getPath();
                amlInfo.setDatasetList(dataset.getDatasetFileList());
                amlInfo.setFileType(dataset.getFileType());
//                if (StringUtils.isNotBlank(path)){
//                    List<FileData> fileData = JSON.parseArray(path, FileData.class);
//                    amlInfo.setDatasetList(fileData);
//                    amlInfo.setFileType(dataset.getFileType());
//                }
            }
        }else {
            AmlDatasetDto datasetDto = new AmlDatasetDto();
            amlInfo.setDataset(datasetDto);
        }
    }


    public List<TrainResultDto.LabelItem> viewStatisticEvaluation(Integer amlId, Integer userId){

        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);

        Byte status = amlInfoDo.getStatus();

        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)){
            return null;
        }
        Integer currentModelId = amlInfoDo.getCurrentModelId();
        if (currentModelId == null){
            return null;
        }
        return amlTrainEvaluationService.viewStatisticEvaluation(currentModelId);
    }



    public EvaluationConfidenceDto viewConfidenceEvaluation(AmlConfidenceEvaluationReq req, Integer userId){

        AmlInfoDo amlInfoDo = amlInfoDao.getById(req.getId());
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);

        Byte status = amlInfoDo.getStatus();

        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)){
            return null;
        }
        Integer currentModelId = amlInfoDo.getCurrentModelId();
        if (currentModelId == null){
            return null;
        }
        return amlTrainEvaluationService.viewConfidenceEvaluation(currentModelId,req.getLabelKey());
    }


    public PageInfo<EvaSentence> viewEvaluationTypePage(AmlConfidenceEvaluationReq req, Integer userId){

        AmlInfoDo amlInfoDo = amlInfoDao.getById(req.getId());
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);

        Byte status = amlInfoDo.getStatus();

        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)){
            return null;
        }
        Integer currentModelId = amlInfoDo.getCurrentModelId();
        if (currentModelId == null){
            return null;
        }
        return amlTrainEvaluationService.viewEvaluationTypePage(currentModelId,req);
    }



    public String downloadEvaluationType(AmlConfidenceEvaluationReq req, Integer userId){

        AmlInfoDo amlInfoDo = amlInfoDao.getById(req.getId());
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);

        Byte status = amlInfoDo.getStatus();

        if (!AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(status)){
            return null;
        }
        Integer currentModelId = amlInfoDo.getCurrentModelId();
        if (currentModelId == null){
            return null;
        }
        String url = amlTrainEvaluationService.downloadEvaluationType(currentModelId, req);
        log.info("id:{},download-url:{}",req.getId(),url);
        return url;
    }



    public AmlStatisticDto getDatasetStatistic(Integer amlId, Integer userId){

        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);


        Integer currentDataId = amlInfoDo.getCurrentDataId();

        AmlDatasetDo dataset = amlDatasetDao.getById(currentDataId);
        AmlStatisticDto amlDatasetStatistic = amlDatasetService.getAmlDatasetStatistic(currentDataId);;

        if (amlDatasetStatistic == null){
            amlDatasetStatistic = new AmlStatisticDto();
            amlDatasetStatistic.setStatisticDesc(dataset==null?"数据集不存在":"数据集没有解析成功");
            amlDatasetStatistic.setCanTrain(false);
        }
        return amlDatasetStatistic;
    }




    public PageInfo<ModelDataItem> getDatasetItemViewPage(AmlDatasetItemReq itemReq, Integer userId){

        Integer amlId = itemReq.getId();


        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        checkAmlUser(userId,amlInfoDo);

        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        checkAmlUser(userId,amlInfoDo);


        Integer currentDataId = amlInfoDo.getCurrentDataId();
        AmlStatisticDto amlDatasetStatistic = null;
        List<ModelDataItem> nasDatasetViewPage = null;
        PageInfo<ModelDataItem> pageInfo = null;
        if (currentDataId!=null&&currentDataId.compareTo(0)>0){

            AmlDatasetDo dataset = amlDatasetDao.getById(currentDataId);

            if (dataset != null){
                pageInfo = amlDatasetService.getNasDatasetViewPage(itemReq, dataset);
            }
        }

        if (pageInfo==null){
            pageInfo = new PageInfo<>();
            pageInfo.setTotal(0);
            pageInfo.setList(new ArrayList<>());
        }
        return pageInfo;
    }


    public AmlTrainViewVo getTrainView(Integer amlId, Integer userId){
        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"自助训练任务不存在");
        AmlInfoDto amlInfoDto = AmlInfoConverter.do2dto(amlInfoDo);

        AmlTrainViewVo trainViewVo = new AmlTrainViewVo();
        trainViewVo.setAmlInfo(amlInfoDto);

        Integer currentModelId = amlInfoDo.getCurrentModelId();
        if (currentModelId!=null&&currentModelId>0){
            AmlModelDo modelDo = amlModelDao.getById(currentModelId);
            if (modelDo!=null){
                trainViewVo.setModelCreateTime(modelDo.getCreateTime());
                if (modelDo.getPrecision()!=null){
                    BigDecimal precision = modelDo.getPrecision().multiply( new BigDecimal(100)).setScale(3);
                    trainViewVo.setPrecision(precision);
                }
                if (modelDo.getRecall()!=null){
                    BigDecimal recall = modelDo.getRecall().multiply( new BigDecimal(100)).setScale(3);
                    trainViewVo.setRecall(recall);
                }
                if (modelDo.getRecall()!=null){
                    BigDecimal avgPrecision = modelDo.getAvgPrecision().multiply( new BigDecimal(100)).setScale(3);
                    trainViewVo.setAvgPrecision(avgPrecision);
                }
            }
        }

        return trainViewVo;
    }

    private void checkAmlUser(Integer userId, AmlInfoDo amlInfoDo) {
        if (amlInfoDo !=null){
            UserDo user = userDao.getById(userId);
            if (user!=null&&user.getRoleType()!=null){
                if (user.getRoleType().equals(1)){
                    return;
                }
            }
            String createBy = amlInfoDo.getCreateBy();
            if (!createBy.equals(String.valueOf(userId))){
                throw new AuthorizeException(ResultCode.USER_NO_PERMISSION, "没有权限!", null);
            }
        }
    }

}
