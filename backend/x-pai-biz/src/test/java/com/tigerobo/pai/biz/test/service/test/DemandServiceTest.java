package com.tigerobo.pai.biz.test.service.test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerobo.pai.biz.test.BaseTest;

import com.tigerobo.x.pai.api.dto.DemandSuggest;
import com.tigerobo.x.pai.api.vo.biz.DemandDetailVo;
import com.tigerobo.x.pai.api.vo.biz.DemandQueryVo;
import com.tigerobo.x.pai.api.vo.biz.DemandVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.QueryVo;
import com.tigerobo.x.pai.api.vo.WebRepVo;
import com.tigerobo.x.pai.biz.biz.DemandSuggestServiceImpl;
import com.tigerobo.x.pai.biz.biz.service.DemandServiceImpl;
import com.tigerobo.x.pai.biz.biz.service.WebDemandService;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class DemandServiceTest extends BaseTest {



    @Autowired
    private DemandSuggestServiceImpl demandSuggestService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private DemandServiceImpl demandServiceV2;

    @Autowired
    private WebDemandService webDemandService;

    @Test
    public void addSuggestTest(){
        int userId = 3;
        UserDo userDo = userDao.getById(userId);
        DemandSuggest demandSuggest = new DemandSuggest();
        demandSuggest.setDemandUuid("117738f3426e25245345f158ec487403");
        demandSuggest.setTitle("建议");
        demandSuggest.setDocUrl("https://x-pai.oss-cn-shanghai.aliyuncs.com/auth/user/avatar_default.png");
        demandSuggestService.addSuggest(demandSuggest,userDo);
    }


    @Test
    public void demandDetailTest(){
        String uuid = "4117e6fd1c344c8e7b0dedbb96b2d03a";

        DemandDetailVo detail = webDemandService.getDetail(uuid);

        System.out.println(JSON.toJSONString(detail));
    }

    @Test
    public void loadTest(){

        String uuid = "117738f3426e25245345f158ec487403";
        List<DemandSuggest> demandSuggestList = demandSuggestService.getDemandSuggestList(uuid);

        System.out.println(JSON.toJSONString(demandSuggestList));
    }


    @Test
    public void demandQueryTest(){
        QueryVo queryVo = new QueryVo();

        queryVo.setOrderBy(null);
//        PageInfo<DemandVo> query = demandService.query(queryVo);

//        System.out.println(JSON.toJSONString(query.getList()));
    }

    @Test
    public void argTest(){
        String json = "{\"orderBy\":\"create_time desc\",\"params\":{\"type\":\"DEMAND\",\"industry\":[\"\"],\"domain\":[\"\"],\"phase\":[\"\"],\"keyword\":\"\"},\"authorization\":{\"token\":\"c4d67d898c9cc8ce77ee610854aafd92-75ddb5778e9fe46fd17360dc73fd2afd-1000\",\"uid\":\"d2c1c4f00697ac39a4d8b9a4ca189d11\"}}";
        json = "{\"orderBy\":\"create_time desc\",\"params\":{\"type\":\"DEMAND\",\"industry\":[\"\"],\"domain\":[\"\"],\"phase\":[\"UNCOMPLETED\"],\"keyword\":\"\"},\"authorization\":{\"token\":\"b66bb0ea7e2e6993e03b87f880a22e0d-368293f44d2e3665ac259334c82e2cc6-1000\",\"uid\":\"ba78b5ba8483a0a40ad0b480b82b2916\"}}";

        DemandQueryVo demandQueryVo = JSON.parseObject(json, DemandQueryVo.class);

//        demandQueryVo.getParams().setPhase(Arrays.asList("UNCOMPLETED"));
        long start = System.currentTimeMillis();
        PageVo<DemandVo> pageInfo = demandServiceV2.demandList(demandQueryVo);

        System.out.println(JSON.toJSONString(pageInfo.getList()));
        System.out.println(pageInfo.getTotal());
        System.out.println("delta-total:"+(System.currentTimeMillis()-start));
    }


    @Test
    public void createTest()throws Exception{

        String json = "{\"params\":{\"demand\":{\"name\":\"中金所智能问答\",\"desc\":\"\",\"intro\":\"如word附件，中金所提供了相关问句及答案，需要将问句转换成sql中的问题，并能对相关sql中的字段信息进行运算和提取工作。相关实体客户已经标注，问题样例和SQL也已提供，需要我司实现相关能力。\",\"budget\":\"¥50-100万\",\"scope\":30,\"deliveryDate\":\"2021-09-14T16:00:00.000Z\"},\"datasetList\":[{\"filePath\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/dataset/tmp/Text2Sql%E8%A1%A8%E7%BB%93%E6%9E%84%E5%92%8C%E9%97%AE%E9%A2%98%EF%BC%88%E7%AD%94%E6%A1%88%E5%B7%B2%E9%AA%8C%E8%AF%81%EF%BC%89-1631106885372.docx\",\"scene\":\"OTHER\"},{\"filePath\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/dataset/tmp/t_daily_marketdata_basis-1631106889616.sql\",\"scene\":\"OTHER\"}],\"tagList\":[{\"uid\":\"domain-others\",\"text\":\"其他\",\"textEn\":\"others\",\"type\":\"DOMAIN\"},{\"uid\":\"finance\",\"text\":\"金融\",\"textEn\":\"Finance\",\"icon\":\"https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/tag/icon/industry-finance.png\",\"type\":\"INDUSTRY\"}]},\"authorization\":{\"token\":\"a4c391deccd709d839bfc745d3bc1ba9-f7f333a59270b53ac283b2fa0ac1bdd0-1000\",\"uid\":\"6a35eb6c592259b0e5a64cb51d289bb4\",\"gid\":\"6a35eb6c592259b0e5a64cb51d289bb4\"}}\n";
        ObjectMapper objectMapper = new ObjectMapper();
        WebRepVo requestVo = objectMapper.readValue(json,WebRepVo.class);
        demandServiceV2.createOrUpdate(requestVo,3);

//        System.out.println(JSON.toJSONString(demandVo));

    }


}
