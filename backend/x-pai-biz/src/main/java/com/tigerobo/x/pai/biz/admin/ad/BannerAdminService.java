package com.tigerobo.x.pai.biz.admin.ad;

import com.tigerobo.x.pai.api.dto.ad.AdBannerDto;
import com.tigerobo.x.pai.api.vo.IdVo;
import com.tigerobo.x.pai.biz.converter.AdBannerConvert;
import com.tigerobo.x.pai.dal.biz.dao.ad.BannerDao;
import com.tigerobo.x.pai.dal.biz.entity.ad.AdBannerPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BannerAdminService {

    @Autowired
    private BannerDao bannerDao;


    public List<AdBannerDto> getList(){

        List<AdBannerPo> bannerList = bannerDao.getBannerList();

        return AdBannerConvert.po2dtos(bannerList);

    }

    public void add(AdBannerDto dto){

        Validate.isTrue(dto!=null,"参数为空");
        Validate.isTrue(StringUtils.isNotBlank(dto.getName()),"name为空");
        Validate.isTrue(StringUtils.isNotBlank(dto.getEnName()),"enname为空");

        Validate.isTrue(StringUtils.isNotBlank(dto.getPath()),"path为空");

        AdBannerPo po = AdBannerConvert.dto2po(dto);
        bannerDao.add(po);

    }

    public void update(AdBannerDto dto){

        Validate.isTrue(dto!=null,"参数为空");
        Validate.isTrue(dto.getId()!=null,"id为空");
        Validate.isTrue(StringUtils.isNotBlank(dto.getName()),"name为空");
        Validate.isTrue(StringUtils.isNotBlank(dto.getEnName()),"enname为空");

        Validate.isTrue(StringUtils.isNotBlank(dto.getPath()),"path为空");

        AdBannerPo po = AdBannerConvert.dto2po(dto);
        bannerDao.update(po);

    }

    public void delete(IdVo idVo){

        AdBannerPo po = new AdBannerPo();
        po.setId(idVo.getId());
        po.setIsDeleted(true);
        bannerDao.update(po);
    }
//
//    public void online(IdVo idVo){
//        AdBannerPo po = new AdBannerPo();
//        po.setId(idVo.getId());
//        po.setIsDeleted(false);
//        bannerDao.update(po);
//    }
}
