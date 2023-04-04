package com.tigerobo.pai.biz.test.api;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.vo.sku.ApiSkuVo;
import com.tigerobo.x.pai.biz.product.ProductSkuService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ApiSkuTest extends BaseTest {

    @Autowired
    private ProductSkuService productSkuService;

    @Test
    public void getApiSkusTest(){

        String apiKey = "bf78e192c11b8e138694149fb1bdeee9";
        ThreadLocalHolder.setUserId(18);
        final ApiSkuVo apiSkuVo = productSkuService.getApiSkuVo(apiKey);

        System.out.println(JSON.toJSONString(apiSkuVo));
    }
}
