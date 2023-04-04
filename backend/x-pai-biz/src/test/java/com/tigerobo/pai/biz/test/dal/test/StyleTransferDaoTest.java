package com.tigerobo.pai.biz.test.dal.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.dal.ai.dao.AiStyleTransferDicDao;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferDicPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiStyleTransferDicMapper;
import com.tigerobo.x.pai.dal.ai.mapper.AiStyleTransferMapper;
import org.apache.commons.lang3.Validate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StyleTransferDaoTest extends BaseTest {

    @Autowired
    private AiStyleTransferDicMapper aiStyleTransferDicMapper;


    @Autowired
    private AiStyleTransferDicDao aiStyleTransferDicDao;


    @Test
    public void getListTest(){
        final List<AiStyleTransferDicPo> list = aiStyleTransferDicDao.getList();

        System.out.println(JSON.toJSONString(list));


    }
    @Test
    public void initBaseStyleTest(){
        String dir = "D:\\tmp\\ai_style";
        final File fileDir = new File(dir);

        List<AiStyleTransferDicPo> poList = new ArrayList<>();
        for (File file : fileDir.listFiles()) {
            final String name = file.getName();
            System.out.println(name);

            final int i = name.indexOf(".");
            String type = name.substring(0,i);

            String url = "https://x-pai.algolet.com/"+"model/style_transfer/base/"+name;

            AiStyleTransferDicPo po = new AiStyleTransferDicPo();
            po.setName(type);
            po.setStyleImage(url);
            poList.add(po);
            final int add = aiStyleTransferDicMapper.insertSelective(po);
            Validate.isTrue(add==1,"添加失败"+name);
        }


    }
}
