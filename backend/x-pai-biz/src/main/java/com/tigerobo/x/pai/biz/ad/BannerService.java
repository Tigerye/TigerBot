package com.tigerobo.x.pai.biz.ad;

import com.tigerobo.x.pai.api.dto.ad.AdBannerDto;
import com.tigerobo.x.pai.biz.converter.AdBannerConvert;
import com.tigerobo.x.pai.dal.biz.dao.ad.BannerDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BannerService {


    @Autowired
    private BannerDao bannerDao;

    public List<AdBannerDto> getBanners() {
        return AdBannerConvert.po2dtos(bannerDao.getBannerList());
    }
}
