package com.tigerobo.x.pai.api.vo.sku;

import com.tigerobo.x.pai.api.product.dto.ApiSkuDto;
import com.tigerobo.x.pai.api.vo.api.AgreementVo;
import lombok.Data;

import java.util.List;

@Data
public class ApiSkuVo {
    String modelName;
    String modelId;
    String image;

    boolean hasAgreement;

    AgreementVo userAgreement;

    List<ApiSkuDto> apiSkuList;

}
