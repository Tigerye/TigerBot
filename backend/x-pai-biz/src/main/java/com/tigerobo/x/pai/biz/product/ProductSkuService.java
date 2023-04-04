package com.tigerobo.x.pai.biz.product;

import com.tigerobo.x.pai.api.product.dto.ApiSkuDto;
import com.tigerobo.x.pai.api.product.dto.ProductSkuDto;
import com.tigerobo.x.pai.api.vo.api.AgreementVo;
import com.tigerobo.x.pai.api.vo.sku.ApiSkuVo;
import com.tigerobo.x.pai.biz.pay.api.ApiAgreementService;
import com.tigerobo.x.pai.biz.product.convert.SkuConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.ApiDao;
import com.tigerobo.x.pai.dal.biz.entity.ApiDo;
import com.tigerobo.x.pai.dal.pay.dao.SkuDao;
import com.tigerobo.x.pai.dal.pay.entity.ProductSkuPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductSkuService {

    @Autowired
    private SkuDao skuDao;

    @Autowired
    private ApiDao apiDao;

    @Autowired
    private ApiAgreementService apiAgreementService;

    public List<ProductSkuDto> getMemberSkuList(){
        List<ProductSkuPo> memberProducts = skuDao.getMemberProducts();
        return SkuConvert.convert(memberProducts);
    }

    public List<ProductSkuDto> getAlgCoinSkuList(){
        List<ProductSkuPo> memberProducts = skuDao.getProductsByType(3);
        return SkuConvert.convert(memberProducts);
    }


    public ProductSkuDto getSku(Integer skuId){
        final ProductSkuPo load = skuDao.load(skuId);
        if (load == null){
            return null;
        }
        return SkuConvert.convert(load);
    }


    public ApiSkuDto getApiSku(Integer skuId){
        if (skuId == null||skuId==0){
            return null;
        }

        final ProductSkuPo load = skuDao.load(skuId);

        if (load==null||load.getProductId()!=2){
            return null;
        }
        return SkuConvert.convertApiSku(load);
    }


    public ApiSkuVo getApiSkuVo(String modelId){

        final ApiDo apiDo = apiDao.getByModelUuid(modelId);

        Validate.isTrue(apiDo!=null,"模型不存在");

        ApiSkuVo apiSkuVo = new ApiSkuVo();
        apiSkuVo.setModelId(modelId);
        apiSkuVo.setImage(apiDo.getImage());
        apiSkuVo.setModelName(apiDo.getName());

        final Integer skuId = apiDo.getSkuId();

        List<ApiSkuDto> skuDtoList = new ArrayList<>();

        final ApiSkuDto apiSku = getApiSku(skuId);

        if (apiSku!=null){
            skuDtoList.add(apiSku);
        }
        apiSkuVo.setApiSkuList(skuDtoList);
        final Integer userId = ThreadLocalHolder.getUserId();

        boolean hasAgreement = false;
        if (userId!=null){
            final AgreementVo userAgreement = apiAgreementService.getUserAgreement(userId, modelId);
            apiSkuVo.setUserAgreement(userAgreement);
            hasAgreement = userAgreement!=null;
        }
        apiSkuVo.setHasAgreement(hasAgreement);
        return apiSkuVo;
    }


}
