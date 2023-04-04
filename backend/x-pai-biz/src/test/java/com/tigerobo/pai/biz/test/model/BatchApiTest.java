package com.tigerobo.pai.biz.test.model;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.ModelCallTypeEnum;
import com.tigerobo.x.pai.api.serving.vo.ApiReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.api.vo.model.ModelBatchQueryReq;
import com.tigerobo.x.pai.api.vo.model.ModelBatchTaskVo;
import com.tigerobo.x.pai.biz.dto.BatchCallDto;
import com.tigerobo.x.pai.biz.batch.offline.ModelBatchOfflineService;
import com.tigerobo.x.pai.biz.serving.ApiCountService;
import com.tigerobo.x.pai.biz.batch.service.BatchApiService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.offline.ModelBatchTaskDao;
import com.tigerobo.x.pai.dal.biz.entity.offline.ModelBatchTaskPo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class BatchApiTest extends BaseTest {

    @Autowired
    private ModelBatchTaskDao modelBatchTaskDao;

    @Autowired
    private BatchApiService batchApiService;

    @Autowired
    private ModelBatchOfflineService modelBatchOfflineService;
    @Autowired
    private ApiCountService apiCountService;

    @Test
    public void incrBillNumTest(){
        long reqId = 104963131828000L;
        reqId = 104963131828001L;
        ModelBatchTaskPo po = modelBatchTaskDao.getByReqId(reqId);
        String result = po.getResult();
        String bizId = po.getBizId();
        BatchCallDto evaluate = JSON.parseObject(result, BatchCallDto.class);
        modelBatchOfflineService.addEs(po, ModelCallTypeEnum.APP,evaluate);

        apiCountService.incrApiCall(bizId,evaluate.getDealNum());
    }

    @Test
    public void addTest() {

        ApiReqVo reqVo = new ApiReqVo();

        reqVo.setUserId(3);
        reqVo.setBizType(2);

        String apiKey = "81079";
        reqVo.setApiKey(apiKey);

        Map<String, Object> map = new HashMap<>();
        String path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/111111-202111131811-202111162011.xlsx";
        map.put("filePath", path);

        reqVo.setParams(map);
        ResultVO<Map> resultVO = batchApiService.addTask(reqVo);

        System.out.println(JSON.toJSONString(resultVO.getData()));

    }


    @Test
    public void pageTest() {

        ModelBatchQueryReq req = new ModelBatchQueryReq();
        String apiKey;

        Integer bizType;

        req.setBizType(2);
        req.setApiKey("81079");

        ThreadLocalHolder.setUserId(3);
        PageVo<ModelBatchTaskVo> userPage = batchApiService.getUserPage(req);

        System.out.println(JSON.toJSONString(userPage));
    }

    @Test
    public void offlineTest() {

        modelBatchOfflineService.dealUnHandel(true);
    }

    @Test
    public void getByReqIdTest() {
        Long reqId = 1140768031007621L;

        ModelBatchTaskVo taskVo = batchApiService.getByReqId(reqId);

        System.out.println(JSON.toJSONString(taskVo));
    }
}
