package com.tigerobo.x.pai.biz.aml.service;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.dto.AmlBaseModelDto;
import com.tigerobo.x.pai.api.aml.dto.AmlDatasetDto;
import com.tigerobo.x.pai.api.aml.dto.AmlModelDto;
import com.tigerobo.x.pai.api.aml.enums.ModelServiceStatusEnum;
import com.tigerobo.x.pai.api.aml.vo.MyAmlQueryVo;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.biz.aml.convert.AmlModelConverter;
import com.tigerobo.x.pai.dal.aml.dao.AmlModelDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlModelDo;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmlModelServiceImpl {

    @Autowired
    private AmlModelDao amlModelDao;

    @Autowired
    private AmlDatasetServiceImpl amlDatasetService;

    @Autowired
    private AmlBaseModelServiceImpl amlBaseModelService;
    @Autowired
    private UserDao userDao;

    public AmlModelDto userGetModelById(Integer id,Integer userId){

        AmlModelDo modelDo = amlModelDao.getById(id);
        Preconditions.checkArgument(modelDo!=null,"模型不存在");
        String createBy = modelDo.getCreateBy();
//
//        if (!createBy.equals(String.valueOf(userId))){
//            throw new AuthorizeException(ResultCode.USER_NO_PERMISSION, "用户没权限", null);
//        }
        checkAmlUser(userId,createBy);
        return getModelById(id);
    }

    public AmlModelDto getModelById(Integer id) {
        AmlModelDto modelDto = getAmlModelDto(id);
        if (modelDto==null){
            return null;
        }
        Integer dataId = modelDto.getDataId();

        AmlDatasetDto datasetDto = amlDatasetService.getDataset(dataId);
        modelDto.setDataset(datasetDto);

        AmlBaseModelDto baseModel = amlBaseModelService.getBaseModel(modelDto.getBaseModelId());
        modelDto.setBaseModel(baseModel);

        initParentModel(modelDto);

        return modelDto;
    }

    private void initParentModel(AmlModelDto modelDto) {
        Integer parentModelId = modelDto.getParentModelId();
        if (parentModelId!=null&&parentModelId>0){
            AmlModelDto parentModel = getAmlModelDto(parentModelId);
            modelDto.setParentModel(parentModel);
        }
    }

    AmlModelDto getAmlModelDto(Integer id) {
        AmlModelDo modelDo = amlModelDao.getById(id);
        AmlModelDto modelDto = AmlModelConverter.po2dto(modelDo);
        return modelDto;
    }

    public PageInfo<AmlModelDto> getMyModelList(MyAmlQueryVo queryVo, String userId){
        PageInfo<AmlModelDo> pageInfoDo = amlModelDao.getMyModelList(userId, queryVo.getPageNum(), queryVo.getPageSize(), queryVo.getOrderBy());
        List<AmlModelDo> list = pageInfoDo.getList();
        long total = pageInfoDo.getTotal();
        List<AmlModelDto> dtos = AmlModelConverter.po2dtoList(list);


        if (!CollectionUtils.isEmpty(dtos)){

            List<Integer> dataIds = dtos.stream().map(dto -> dto.getDataId()).collect(Collectors.toList());
            List<AmlDatasetDto> datasetList = amlDatasetService.getByIds(dataIds);

            dtos.stream().forEach(this::initParentModel);

            Map<Integer,AmlDatasetDto> datasetMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(datasetList)){
                for (AmlDatasetDto datasetDo : datasetList) {
                    datasetMap.put(datasetDo.getDatasetId(),datasetDo);
                }
            }
            for (AmlModelDto dto : dtos) {
                AmlDatasetDto datasetDo = datasetMap.get(dto.getDataId());
                if (datasetDo!=null){
                    dto.setLabeledCount(datasetDo.getLabeledCount());
                    dto.setTrainCount(datasetDo.getTrainCount());
                }

                dto.setDataset(datasetDo);
                AmlBaseModelDto baseModel = amlBaseModelService.getBaseModel(dto.getBaseModelId());
                dto.setBaseModel(baseModel);
            }
        }
        PageInfo<AmlModelDto> dtoPageInfo = new PageInfo<>(dtos);

        dtoPageInfo.setTotal(total);
        dtoPageInfo.setPageNum(queryVo.getPageNum());
        dtoPageInfo.setPageSize(queryVo.getPageSize());
        return dtoPageInfo;
    }

    public void offline(Integer id, Integer userId) {

        AmlModelDo model = amlModelDao.getById(id);
        Validate.isTrue(model!=null,"模型不存在");

        String createBy = model.getCreateBy();
//        Validate.isTrue(Objects.equals(createBy,String.valueOf(userId)),"用户没权限");
        checkAmlUser(userId,createBy,false);

        Integer serviceStatus = model.getServiceStatus();
        ModelServiceStatusEnum serviceStatusEnum = ModelServiceStatusEnum.getByStatus(serviceStatus);
        Validate.isTrue(serviceStatusEnum!=null,"当前状态不能操作");

        Validate.isTrue(ModelServiceStatusEnum.ONLINE.getStatus().equals(serviceStatus),"当前状态"+serviceStatusEnum.getName()+",不能点击下线");


        AmlModelDo update = new AmlModelDo();
        update.setId(id);
        update.setServiceStatus(ModelServiceStatusEnum.WAIT_OFFLINE.getStatus());
        amlModelDao.update(update);
    }
    public void online(Integer id, Integer userId) {

        AmlModelDo model = amlModelDao.getById(id);
        Validate.isTrue(model!=null,"模型不存在");

        String createBy = model.getCreateBy();
//        Validate.isTrue(Objects.equals(createBy,String.valueOf(userId)),"用户没权限");
        checkAmlUser(userId,createBy);

        Byte status = model.getStatus();
        AmlStatusEnum amlStatus = AmlStatusEnum.getByStatus(status);
        Validate.isTrue(AmlStatusEnum.TRAIN_SUCCESS== amlStatus,"模型还没有训练成功，不能上线");

        Integer serviceStatus = model.getServiceStatus();
        ModelServiceStatusEnum serviceStatusEnum = ModelServiceStatusEnum.getByStatus(serviceStatus);
        Validate.isTrue(serviceStatusEnum!=null,"当前状态不能操作");

        Validate.isTrue(ModelServiceStatusEnum.OFFLINE.getStatus().equals(serviceStatus),"当前状态"+serviceStatusEnum.getName()+",不能点击上线");

        AmlModelDo update = new AmlModelDo();
        update.setId(id);
        update.setServiceStatus(ModelServiceStatusEnum.WAIT_ONLINE.getStatus());

        amlModelDao.update(update);

    }

    private void checkAmlUser(Integer userId, String createBy) {
        checkAmlUser(userId,createBy,true);
    }
    private void checkAmlUser(Integer userId, String createBy,boolean checkAdmin) {
        if (createBy !=null){
            UserDo user = userDao.getById(userId);
            if (checkAdmin&&user!=null&&user.getRoleType()!=null){
                if (user.getRoleType().equals(1)){
                    return;
                }
            }
            if (!createBy.equals(String.valueOf(userId))){
                throw new AuthorizeException(ResultCode.USER_NO_PERMISSION, "没有权限", null);
            }
        }
    }
}
