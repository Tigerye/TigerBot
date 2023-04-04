package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.biz.github.convert.GithubUserConvert;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;

public class FollowBizConvert {

    public static FollowVo convert(User user){
        if (user == null){
            return null;
        }
        FollowVo vo = new FollowVo();
        vo.setId(user.getId());
        vo.setBizType(FollowTypeEnum.USER.getType());
        vo.setLogoOss(user.getAvatar());
        vo.setUserAccount(user.getAccount());
        vo.setName(user.getName());
        vo.setIntro(user.getIntro());
        vo.setFollow(user.isFollow());
        return vo;
    }


    public static FollowVo convertSite(PubSitePo site){
        if (site == null){
            return null;
        }
        FollowVo vo = new FollowVo();

        vo.setId(site.getId());
        vo.setBizType(FollowTypeEnum.SITE.getType());

        vo.setName(site.getName());
        vo.setLogoOss(site.getLogoOss());
        vo.setLogo(site.getLogoOss());

        vo.setIntro(site.getIntro());
        vo.setVip(site.getVip());
//        vo.setFollow(site.isFollow());
        return vo;
    }

    public static FollowVo convert(PubSiteVo site){
        if (site == null){
            return null;
        }
        FollowVo vo = new FollowVo();

        vo.setId(site.getId());
        vo.setBizType(FollowTypeEnum.SITE.getType());

        vo.setName(site.getName());
        vo.setLogoOss(site.getLogoOss());

        vo.setIntro(site.getIntro());
        vo.setFollow(site.isFollow());
        vo.setVip(site.getVip());
        return vo;
    }

    public static FollowVo convert(PubBigShotVo big){
        if (big == null){
            return null;
        }
        FollowVo vo = PubBigShotConvert.convert2FollowVo(big);
        vo.setFollow(big.isFollow());
        return vo;
    }


    public static FollowVo convert(GithubUserDto dto){
        if (dto == null){
            return null;
        }
        FollowVo vo = GithubUserConvert.dto2follow(dto);
        return vo;
    }
}
