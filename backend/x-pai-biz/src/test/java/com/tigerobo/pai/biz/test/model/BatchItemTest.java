package com.tigerobo.pai.biz.test.model;

import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.biz.batch.offline.BatchItemService;
import com.tigerobo.x.pai.biz.batch.service.BatchProcessService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.offline.ModelBatchTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchItemTest extends BaseTest {

    @Autowired
    private ModelBatchTaskDao modelBatchTaskDao;

    @Autowired
    private BatchItemService batchItemService;

    @Autowired
    private BatchProcessService batchProcessService;

    @Test
    public void dealTest(){

        final ModelBatchTaskPo load = modelBatchTaskDao.load(2);
        batchItemService.deal(load);
    }

    @Test
    public void suspendTest(){
        IdReqVo reqVo = new IdReqVo();
        reqVo.setId(32);

        batchProcessService.suspend(32);

    }

    @Test
    public void continueTest(){
        IdReqVo reqVo = new IdReqVo();
        reqVo.setId(32);
        ThreadLocalHolder.setUserId(18);

        batchProcessService.update2waitDeal(reqVo);

    }

}
