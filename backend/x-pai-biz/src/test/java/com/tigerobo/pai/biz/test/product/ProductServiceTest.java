package com.tigerobo.pai.biz.test.product;

import com.alibaba.fastjson.JSON;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.product.dto.ProductSkuDto;
import com.tigerobo.x.pai.biz.product.ProductSkuService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductServiceTest extends BaseTest {
    @Autowired
    private ProductSkuService productSkuService;

    @Test
    public void loadTest(){


        List<ProductSkuDto> memberSkuList = productSkuService.getMemberSkuList();

        String s = JSON.toJSONString(memberSkuList);

        System.out.println(s);
    }
}
