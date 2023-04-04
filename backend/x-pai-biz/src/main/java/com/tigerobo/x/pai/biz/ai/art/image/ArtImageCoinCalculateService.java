package com.tigerobo.x.pai.biz.ai.art.image;

import com.tigerobo.x.pai.api.ai.req.AiArtImageGenerateReq;
import com.tigerobo.x.pai.biz.ai.ImageSizeService;
import com.tigerobo.x.pai.biz.biz.customer.ModelConsumeCheckService;
import com.tigerobo.x.pai.biz.coin.CoinPriceService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.entity.AiImageSizePo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ArtImageCoinCalculateService {

    @Autowired
    private CoinPriceService coinPriceService;
    @Autowired
    ImageSizeService imageSizeService;

    @Autowired
    private ModelConsumeCheckService modelConsumeCheckService;

    public CalData calData(AiArtImageGenerateReq req){


        final Integer userId = ThreadLocalHolder.getUserId();
//        final int remainCount = modelConsumeCheckService.countUserArtImgRemainCall(userId);
        int remainCount = 0;

        CalData calData = new CalData();
        if (remainCount>0){
            calData.setCoinTotal(0);
            calData.setUseFree(true);
        }else {
            final Integer cal = cal(req);
            calData.setCoinTotal(cal);
            calData.setUseFree(false);
        }

        return calData;
    }

    public Integer cal(AiArtImageGenerateReq req){
//        if (req.isUseFree()){
//            return 0;
//        }
        final Integer sizeId = req.getSizeId();
        if (sizeId == null){
            return 1;
        }
        final AiImageSizePo sizePo = imageSizeService.getById(sizeId);

        Integer total = 0;

//        final Integer artImageBaseCoinPrice = coinPriceService.getArtImageBaseCoinPrice();

        if (sizePo!=null&&sizePo.getAlgCoin()!=null&&sizePo.getAlgCoin()>0){
            total+=sizePo.getAlgCoin();
        }

        return total;
    }


    @Data
    public static class CalData{
        Boolean useFree;
        Integer coinTotal;
    }
}
