package com.tigerobo.x.pai.engine.auto.ml.manager;

import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.enums.AmlStatusEnum;
import com.tigerobo.x.pai.dal.aml.dao.AmlDatasetDao;
import com.tigerobo.x.pai.dal.aml.entity.AmlDatasetDo;
import com.tigerobo.x.pai.dal.aml.entity.AmlInfoDo;
import com.tigerobo.x.pai.dal.aml.mapper.AmlInfoMapper;
import com.tigerobo.x.pai.engine.auto.ml.service.AmlDatasetProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Component
public class DatasetPrepareManager {


    @Autowired
    private AmlInfoMapper amlInfoMapper;
    @Autowired
    private AmlDatasetDao amlDatasetDao;
    @Autowired
    private AmlDatasetProcessService amlDatasetProcessService;

    public void dealUnhandled(boolean test){
        int pageNum = 1;
        int pageSize = 20;
        List<AmlInfoDo> dataWaitProcessList = null;
        if (test){
            dataWaitProcessList = getDataWaitProcessListTest(pageNum,pageSize);
        }else {
            dataWaitProcessList = getDataWaitProcessList(pageNum,pageSize);
        }


        if (CollectionUtils.isEmpty(dataWaitProcessList)){
            return;
        }
        for (AmlInfoDo infoDo : dataWaitProcessList) {
            deal(infoDo);
        }
    }

    private List<AmlInfoDo> getDataWaitProcessList(Integer pageNum,Integer pageSize){

        Example example = new Example(AmlInfoDo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", AmlStatusEnum.WAIT_PROCESS_DATA.getStatus());

        example.selectProperties("id","currentDataId","createBy");
        PageHelper.startPage(pageNum,pageSize);
        return amlInfoMapper.selectByExample(example);
    }

    private List<AmlInfoDo> getDataWaitProcessListTest(Integer pageNum,Integer pageSize){

        Example example = new Example(AmlInfoDo.class);
        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("status", AmlStatusEnum.WAIT_PROCESS_DATA.getStatus());
        criteria.andEqualTo("id",430804);
        example.selectProperties("id","currentDataId","createBy");
        PageHelper.startPage(pageNum,pageSize);
        return amlInfoMapper.selectByExample(example);
    }


    private void deal(AmlInfoDo amlInfoDo){

        if (amlInfoDo == null){
            return;
        }
        Integer currentDataId = amlInfoDo.getCurrentDataId();
        AmlDatasetDo dataset = amlDatasetDao.getById(currentDataId);

        amlDatasetProcessService.dealWaitProcessData(amlInfoDo,dataset);
    }
}
