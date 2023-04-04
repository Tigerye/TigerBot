package com.tigerobo.x.pai.biz.biz.service;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.DemandDataset;
import com.tigerobo.x.pai.api.vo.biz.req.DemandDatasetReq;
import com.tigerobo.x.pai.api.enums.UserRoleTypeEnum;
import com.tigerobo.x.pai.biz.converter.DemandDatasetConvert;
import com.tigerobo.x.pai.dal.biz.dao.DemandDao;
import com.tigerobo.x.pai.dal.biz.dao.DemandDatasetDao;
import com.tigerobo.x.pai.dal.biz.entity.DemandDatasetPo;
import com.tigerobo.x.pai.dal.biz.entity.DemandDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class DemandDatasetService {

    @Autowired
    private UserService userService;
    @Autowired
    private DemandDao demandDao;
    @Autowired
    private DemandDatasetDao demandDatasetDao;
    public void uploadDatasetList(DemandDatasetReq datasetReq,Integer userId){
        User user = userService.getById(userId);
        Preconditions.checkArgument(user!=null,"用户未登录");
        Integer roleType = user.getRoleType();

        if (!UserRoleTypeEnum.SUPER_MAN.getBit().equals(roleType)){
            throw new IllegalArgumentException("用户没有提交数据集的权限");
        }

        Preconditions.checkArgument(datasetReq!=null,"参数为空");
        Preconditions.checkArgument(! CollectionUtils.isEmpty(datasetReq.getDatasetList()),"数据集为空");

        Preconditions.checkArgument(!StringUtils.isEmpty(datasetReq.getDemandUuid()),"需求id为空");

        String demandUuid = datasetReq.getDemandUuid();
        DemandDo demandDo = demandDao.getByUuid(demandUuid);
        Preconditions.checkArgument(demandDo!=null,"需求不存在");

        List<DemandDataset> datasetList = datasetReq.getDatasetList();
        int i = 1;
        for (DemandDataset demandDataset : datasetList) {
            String filePath = demandDataset.getFilePath();
            if (StringUtils.isEmpty(filePath)){
                throw new IllegalArgumentException("第"+i+"条文件路径为空");
            }
            demandDataset.setCreateBy(String.valueOf(userId));
            demandDataset.setUpdateBy(String.valueOf(userId));
            demandDataset.setDemandUuid(demandUuid);
        }

        List<DemandDatasetPo> demandDatasetPos = DemandDatasetConvert.dto2poList(datasetList);
        for (DemandDatasetPo demandDatasetPo : demandDatasetPos) {
            demandDatasetDao.insert(demandDatasetPo);
        }

        //todo-demandLog
    }

    public List<DemandDataset> getDemandDatasetList(String demandUuid){
        if (StringUtils.isEmpty(demandUuid)){
            return null;
        }
        List<DemandDatasetPo> demandDatasetList = demandDatasetDao.getDemandDatasetList(demandUuid);

        return DemandDatasetConvert.po2dtoList(demandDatasetList);
    }
}
