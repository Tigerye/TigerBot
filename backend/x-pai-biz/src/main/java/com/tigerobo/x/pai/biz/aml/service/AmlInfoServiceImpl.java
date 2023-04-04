package com.tigerobo.x.pai.biz.aml.service;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.dto.AmlDatasetDto;
import com.tigerobo.x.pai.api.aml.dto.AmlInfoDto;
import com.tigerobo.x.pai.api.aml.dto.AmlStatisticDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlCreateDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlStartTrainDto;
import com.tigerobo.x.pai.api.aml.dto.scene.AmlUploadDataDto;
import com.tigerobo.x.pai.api.aml.service.AmlInfoService;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class AmlInfoServiceImpl implements AmlInfoService {


    @Autowired
    private AmlInfoDao amlInfoDao;
    @Autowired
    private AmlDatasetDao amlDatasetDao;

    @Autowired
    private AmlModelDao amlModelDao;
    @Autowired
    private AmlBaseModelDao amlBaseModelDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private AmlDatasetServiceImpl amlDatasetService;

    @Autowired
    private AmlLakeService amlLakeService;

    private List<AmlStatusEnum> canUploadDataStatusList = Arrays.asList(
            AmlStatusEnum.WAIT_UPLOAD_DATA,
            AmlStatusEnum.PROCESS_DATA_FAIL,
            AmlStatusEnum.PROCESS_DATA_SUCCESS
            , AmlStatusEnum.TRAIN_FAIL
    );


    @Override
    public void deleteAml(Integer id,Integer userId){
        AmlInfoDo amlInfoDo = amlInfoDao.getById(id);

        checkAmlUser(userId, amlInfoDo);

        AmlInfoDo update = new AmlInfoDo();
        update.setId(id);
        update.setHide(true);
        amlInfoDao.updateByPrimaryKey(update);
    }
    @Override
    public AmlInfoDto getAmlInfo(Integer id, Integer userId){
        AmlInfoDo amlInfoDo = amlInfoDao.getById(id);

        checkAmlUser(userId, amlInfoDo);


        return AmlInfoConverter.do2dto(amlInfoDo);
    }


    private UserDo checkAmlUser(Integer userId, AmlInfoDo amlInfoDo) {
        if (amlInfoDo !=null){
            UserDo user = userDao.getById(userId);
            if (user!=null&&user.getRoleType()!=null){
                if (user.getRoleType().equals(1)){
                    return user;
                }
            }
            String createBy = amlInfoDo.getCreateBy();
            if (!createBy.equals(String.valueOf(userId))){
                throw new AuthorizeException(ResultCode.USER_NO_PERMISSION, "没有权限", null);
            }
            return user;
        }
        return null;
    }

    @Override
    public AmlInfoDto addAml(AmlCreateDto createDto, Integer userId){

        Preconditions.checkArgument(createDto!=null,"参数为空");
        Preconditions.checkArgument(createDto.getBaseModelId()!=null,"未选择模型类型");
        Preconditions.checkArgument(StringUtils.isNotBlank(createDto.getName()),"名称为空");
        if (userId!=null){
            createDto.setCreateBy(String.valueOf(userId));
        }

        UserDo user = userDao.getById(userId);
        AmlInfoDo infoDo = AmlInfoConverter.create2do(createDto);

        Integer trainType = createDto.getTrainType();
        if (trainType!=null&&trainType.equals(1)){
            //增量训练
            Integer parentModelId = createDto.getParentModelId();
            Validate.isTrue(parentModelId !=null&& parentModelId >0,
                    "未选择增量原模型id");
            AmlModelDo parentModel = amlModelDao.getById(parentModelId);

            Validate.isTrue(parentModel!=null,"原模型不存在");
            Validate.isTrue(AmlStatusEnum.TRAIN_SUCCESS.getStatus().equals(parentModel.getStatus()),"原模型没有训练成功");

            String modelPath = amlLakeService.getModelPath(parentModelId, user.getAmlArea());
            Validate.isTrue(StringUtils.isNotBlank(modelPath),"未找到模型路径");
            infoDo.setModelPath(modelPath);
        }
        amlInfoDao.insert(infoDo);
        return AmlInfoConverter.do2dto(infoDo);
    }

    @Override
    public void importDataset(AmlUploadDataDto req, Integer userId){
        Preconditions.checkArgument(req.getAmlId()!=null,"没有绑定自主训练任务id");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(req.getDatasetList()),"选择文件路径为空");
        Integer amlId = req.getAmlId();
        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"自主训练任务不存在");
        checkAmlUser(userId,amlInfoDo);

        AmlStatusEnum amlStatusEnum = AmlStatusEnum.getByStatus(amlInfoDo.getStatus());
        Preconditions.checkArgument(amlStatusEnum!=null,"自主训练任务状态不正常");
        Preconditions.checkArgument(canUploadDataStatusList.contains(amlStatusEnum),"当前状态");

        AmlDatasetDo datasetDo = new AmlDatasetDo();
        datasetDo.setAmlId(amlId);
//        datasetDo.setPath(String.join(",",req.getFileUrlList()));

        datasetDo.setPath(JSON.toJSONString(req.getDatasetList()));
        datasetDo.setStatus(AmlStatusEnum.WAIT_PROCESS_DATA.getStatus());
        datasetDo.setFileType(req.getFileType());
        datasetDo.setBaseModelId(amlInfoDo.getBaseModelId());
        if (userId!=null){
            datasetDo.setCreateBy(String.valueOf(userId));
            datasetDo.setUpdateBy(String.valueOf(userId));
        }

        amlDatasetDao.insert(datasetDo);
        Preconditions.checkArgument(datasetDo.getId()!=null,"创建数据集失败");

        Integer datasetDoId = datasetDo.getId();

        AmlInfoDo updateInfo = new AmlInfoDo();
        updateInfo.setCurrentDataId(datasetDoId);
        updateInfo.setStatus(AmlStatusEnum.WAIT_PROCESS_DATA.getStatus());
        updateInfo.setId(amlId);
        updateInfo.setCurrentModelId(0);

        amlInfoDao.updateByPrimaryKey(updateInfo);
    }

    @Override
    public void startTrain(AmlStartTrainDto req, Integer userId){
        Integer amlId = req.getId();
        Preconditions.checkArgument(amlId!=null,"参数不正确");

        AmlInfoDo amlInfoDo = amlInfoDao.getById(amlId);
        Preconditions.checkArgument(amlInfoDo!=null,"训练任务不存在");

        UserDo userDo = checkAmlUser(userId, amlInfoDo);
        Preconditions.checkArgument(AmlStatusEnum.PROCESS_DATA_SUCCESS.getStatus().equals( amlInfoDo.getStatus()),
                "当前状态不能点击训练");

        String createBy = amlInfoDo.getCreateBy();
        UserDo modelUser = null;
        if (StringUtils.isNotBlank(createBy)&&createBy.matches("\\d+")){
            modelUser = userDao.getById(Integer.parseInt(createBy));
        }


        Integer currentDataId = amlInfoDo.getCurrentDataId();

        AmlDatasetDto dataset = amlDatasetService.getDataset(currentDataId);

        Preconditions.checkArgument(dataset!=null,"没有数据集");

        AmlStatisticDto amlDatasetStatistic = amlDatasetService.getAmlDatasetStatistic(currentDataId);

        Preconditions.checkArgument(amlDatasetStatistic!=null,"没有完成解析的数据集，请重传数据解析");

        Preconditions.checkArgument(StringUtils.isEmpty(amlDatasetStatistic.getStatisticDesc()),amlDatasetStatistic.getStatisticDesc());

        Integer baseModelId = amlInfoDo.getBaseModelId();
        AmlBaseModelDo baseModel = amlBaseModelDao.getFromCache(baseModelId);
        AmlModelDo modelDo = new AmlModelDo();
        modelDo.setAmlId(amlId);
        modelDo.setDataId(amlInfoDo.getCurrentDataId());
        if (modelUser!=null&&StringUtils.isNotBlank(modelUser.getAmlUrl())){
            modelDo.setModelUrl(modelUser.getAmlUrl());
        }


        modelDo.setBaseModelId(baseModelId);
        if (baseModel!=null){
            modelDo.setStyle(baseModel.getStyle());
        }
        modelDo.setName(amlInfoDo.getName());
        modelDo.setStatus(AmlStatusEnum.WAIT_TRAIN.getStatus());
        modelDo.setCreateBy(String.valueOf(userId));
        modelDo.setUpdateBy(String.valueOf(userId));

        modelDo.setParentModelId(amlInfoDo.getParentModelId());

        amlModelDao.insert(modelDo);
        Preconditions.checkArgument(modelDo.getId()!=null,"开始训练服务异常");

        AmlInfoDo updateStart = new AmlInfoDo();
        updateStart.setId(amlId);
        updateStart.setStatus(AmlStatusEnum.WAIT_TRAIN.getStatus());
        updateStart.setCurrentModelId(modelDo.getId());
        amlInfoDao.updateByPrimaryKey(updateStart);
    }
}
