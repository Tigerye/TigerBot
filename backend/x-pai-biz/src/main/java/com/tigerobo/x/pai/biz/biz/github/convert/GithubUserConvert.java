package com.tigerobo.x.pai.biz.biz.github.convert;

import com.tigerobo.x.pai.api.dto.admin.github.GithubUserAdminVo;
import com.tigerobo.x.pai.api.dto.github.GithubUserDto;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.github.GithubUserVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubUserPo;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GithubUserConvert {

    public static GithubUserPo dto2po(GithubUserDto dto){
        if (dto == null){
            return null;
        }
        GithubUserPo po = new GithubUserPo();


        BeanUtils.copyProperties(dto,po);
        return po;
    }

    public static GithubUserVo po2vo(GithubUserPo po){
        if (po == null){
            return null;
        }
        GithubUserVo vo = new GithubUserVo();


        BeanUtils.copyProperties(po,vo);
        return vo;
    }

    public static GithubUserAdminVo po2adminVo(GithubUserPo po){
        if (po == null){
            return null;
        }
        GithubUserAdminVo vo = new GithubUserAdminVo();


        BeanUtils.copyProperties(po,vo);
        return vo;
    }

    public static List<GithubUserAdminVo> po2adminVos(List<GithubUserPo> pos){
        if (pos == null){
            return null;
        }

        return pos.stream().map(p->po2adminVo(p)).collect(Collectors.toList());
    }
    public static List<GithubUserVo> po2vos(List<GithubUserPo> pos){
        if (pos == null){
            return null;
        }

        return pos.stream().map(p->po2vo(p)).collect(Collectors.toList());
    }

    public static FollowVo dto2follow(GithubUserDto dto){
        if (dto == null){
            return null;
        }

        FollowVo followVo = new FollowVo();

        followVo.setId(dto.getUserId());
        followVo.setBizType(FollowTypeEnum.GITHUB_USER.getType());
        followVo.setLogo(dto.getAvatarUrl());
        followVo.setLogoOss(dto.getAvatarUrl());

        followVo.setName(dto.getUserName());
        followVo.setAlias(dto.getCompany());
        followVo.setIntro(dto.getSubTile());
        followVo.setPlatformName("github");
        return followVo;
    }

}
