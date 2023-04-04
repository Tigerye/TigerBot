package com.tigerobo.pai.biz.test.hot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.biz.hot.HotBusinessService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class HotServiceTest extends BaseTest {

    @Autowired
    private HotBusinessService hotBusinessService;

    @Test
    public void initScoreTest() {
        String json = "[{\"biz_id\":\"148\",\"n\":1},{\"biz_id\":\"160\",\"n\":1},{\"biz_id\":\"162\",\"n\":2},{\"biz_id\":\"167\",\"n\":1},{\"biz_id\":\"233\",\"n\":7},{\"biz_id\":\"268\",\"n\":7},{\"biz_id\":\"288\",\"n\":1},{\"biz_id\":\"346\",\"n\":1},{\"biz_id\":\"355\",\"n\":1},{\"biz_id\":\"357\",\"n\":1},{\"biz_id\":\"358\",\"n\":1},{\"biz_id\":\"366\",\"n\":1},{\"biz_id\":\"390\",\"n\":16}]";


        json = "[{\"biz_id\":\"53175\",\"n\":4},{\"biz_id\":\"53461\",\"n\":4},{\"biz_id\":\"53297\",\"n\":4},{\"biz_id\":\"53084\",\"n\":4},{\"biz_id\":\"53346\",\"n\":4},{\"biz_id\":\"357\",\"n\":4},{\"biz_id\":\"332\",\"n\":3},{\"biz_id\":\"53060\",\"n\":3},{\"biz_id\":\"53433\",\"n\":3},{\"biz_id\":\"53046\",\"n\":3},{\"biz_id\":\"53306\",\"n\":3},{\"biz_id\":\"53390\",\"n\":3},{\"biz_id\":\"53301\",\"n\":2},{\"biz_id\":\"53648\",\"n\":2},{\"biz_id\":\"53108\",\"n\":2},{\"biz_id\":\"53173\",\"n\":2},{\"biz_id\":\"53156\",\"n\":2},{\"biz_id\":\"53330\",\"n\":2},{\"biz_id\":\"53117\",\"n\":2},{\"biz_id\":\"53183\",\"n\":2},{\"biz_id\":\"53080\",\"n\":2},{\"biz_id\":\"53178\",\"n\":2},{\"biz_id\":\"53463\",\"n\":2},{\"biz_id\":\"53076\",\"n\":1},{\"biz_id\":\"53373\",\"n\":1},{\"biz_id\":\"53635\",\"n\":1},{\"biz_id\":\"53090\",\"n\":1},{\"biz_id\":\"53351\",\"n\":1},{\"biz_id\":\"53439\",\"n\":1},{\"biz_id\":\"53243\",\"n\":1},{\"biz_id\":\"53631\",\"n\":1},{\"biz_id\":\"53051\",\"n\":1},{\"biz_id\":\"53208\",\"n\":1},{\"biz_id\":\"53608\",\"n\":1},{\"biz_id\":\"53048\",\"n\":1},{\"biz_id\":\"53082\",\"n\":1},{\"biz_id\":\"53393\",\"n\":1},{\"biz_id\":\"53487\",\"n\":1},{\"biz_id\":\"53115\",\"n\":1}]";
        JSONArray objects = JSONArray.parseArray(json);
        for (int i = 0; i < objects.size(); i++) {

            JSONObject jsonObject = objects.getJSONObject(i);
            String biz_id = jsonObject.getString("biz_id");
            int count = jsonObject.getInteger("n");

            hotBusinessService.setBizScore(biz_id, BusinessEnum.ART_IMAGE.getType(), (double) count);
        }


    }

    @Test
    public void scoreSortTest(){

        List<String> topIdList = hotBusinessService.getTopIdList(31);

    }

}
