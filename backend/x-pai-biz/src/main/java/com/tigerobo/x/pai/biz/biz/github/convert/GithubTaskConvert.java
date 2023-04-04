package com.tigerobo.x.pai.biz.biz.github.convert;

import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoTaskAdminVo;
import com.tigerobo.x.pai.api.dto.github.GithubRepoTaskDto;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoTaskPo;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GithubTaskConvert {

    public static GithubRepoTaskPo dto2po(GithubRepoTaskDto dto) {

        GithubRepoTaskPo po = new GithubRepoTaskPo();
        BeanUtils.copyProperties(dto, po);
        return po;
    }

    public static GithubRepoTaskDto po2dto(GithubRepoTaskPo po) {
        GithubRepoTaskDto dto = new GithubRepoTaskDto();

        return po2dto(po, dto);
    }

    public static GithubRepoTaskAdminVo po2adminVo(GithubRepoTaskPo po) {
        GithubRepoTaskAdminVo dto = new GithubRepoTaskAdminVo();

        return po2dto(po, dto);
    }

    public static <T extends GithubRepoTaskDto> T po2dto(GithubRepoTaskPo po, T dto) {

        if (po == null) {
            return null;
        }
        BeanUtils.copyProperties(po, dto);
        return dto;
    }


    public static List<GithubRepoTaskAdminVo> po2adminVos(List<GithubRepoTaskPo> poList) {
        if (poList == null) {
            return null;
        }
        return poList.stream().map(GithubTaskConvert::po2adminVo).collect(Collectors.toList());
    }


    public static List<GithubRepoTaskDto> po2dtos(List<GithubRepoTaskPo> poList) {
        if (poList == null) {
            return null;
        }
        return poList.stream().map(GithubTaskConvert::po2dto).collect(Collectors.toList());
    }
}
