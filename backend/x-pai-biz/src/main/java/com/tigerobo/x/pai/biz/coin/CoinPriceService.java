package com.tigerobo.x.pai.biz.coin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CoinPriceService {

    @Value("${coin.price.artImage:3}")
    Integer artImageBasePrice;
    public Integer getArtImageBaseCoinPrice(){
        return artImageBasePrice;
    }
}
