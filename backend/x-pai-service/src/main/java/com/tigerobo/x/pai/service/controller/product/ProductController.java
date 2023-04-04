package com.tigerobo.x.pai.service.controller.product;


import com.tigerobo.x.pai.api.product.dto.ProductSkuDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.sku.ApiSkuVo;
import com.tigerobo.x.pai.biz.product.ProductSkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "product", tags = "产品服务")
@Slf4j
@RestController
@RequestMapping(value = "/product/")
public class ProductController {


    @Autowired
    private ProductSkuService productSkuService;

    @ApiOperation(value = "获取算法币sku列表")
    @PostMapping(path = "/getAlgCoinSkuList", produces = "application/json")
    public List<ProductSkuDto> getAlgCoinSku() {
        return productSkuService.getAlgCoinSkuList();
    }
    @ApiOperation(value = "获取sku明细")
    @PostMapping(path = "/getSkuDetail", produces = "application/json")
    public ProductSkuDto getSkuDetail(@RequestBody IdReqVo req) {
        final Integer id = req.getId();
        return productSkuService.getSku(id);
    }

    @ApiOperation(value = "获取会员sku列表")
    @PostMapping(path = "/getVipSkuList", produces = "application/json")
    public List<ProductSkuDto> getVipSkuList() {
        return productSkuService.getMemberSkuList();
    }

    @ApiOperation(value = "获取模型sku列表")
    @PostMapping(path = "/getApiSkuList", produces = "application/json")
    public ApiSkuVo getApiSkuList(@RequestBody ApiKey req) {
        final String apiKey = req.getApiKey();
        return productSkuService.getApiSkuVo(apiKey);
    }

    @ApiOperation(value = "获取模型sku明细")
    @PostMapping(path = "/getApiSku", produces = "application/json")
    public ApiSkuVo getApiSku(@RequestBody ApiKey req) {
        final String apiKey = req.getApiKey();
        return productSkuService.getApiSkuVo(apiKey);
    }

    @Data
    private static class ApiKey{
        String apiKey;
    }
}
