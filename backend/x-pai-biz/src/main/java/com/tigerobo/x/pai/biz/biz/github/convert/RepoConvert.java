package com.tigerobo.x.pai.biz.biz.github.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tigerobo.x.pai.api.dto.admin.github.GithubRepoAdminVo;
import com.tigerobo.x.pai.api.dto.github.GithubRepoDto;
import com.tigerobo.x.pai.api.dto.model.ModelCategoryDto;
import com.tigerobo.x.pai.api.vo.github.GithubRepoVo;
import com.tigerobo.x.pai.biz.biz.model.ModelCategoryService;
import com.tigerobo.x.pai.dal.biz.entity.github.GithubRepoInfoPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class RepoConvert implements ApplicationContextAware {
    private static ModelCategoryService modelCategoryService;

    public static GithubRepoInfoPo dto2po(GithubRepoDto dto){

        if (dto == null){
            return null;
        }
        GithubRepoInfoPo po = new GithubRepoInfoPo();
        BeanUtils.copyProperties(dto,po);
        final List<String> aiTagUidList = dto.getAiTagUidList();

        if (dto.getAiTagUidList() == null){
            po.setAiTag(null);
        }else if (aiTagUidList.isEmpty()){
            po.setAiTag("");
        }else {
            po.setAiTag(JSON.toJSONString(aiTagUidList));
        }

        return po;
    }

    public static GithubRepoDto  po2dto(GithubRepoInfoPo po){
        GithubRepoDto dto = new GithubRepoDto();

        return po2dto(po,dto);
    }

    public static GithubRepoVo  po2vo(GithubRepoInfoPo po){
        GithubRepoVo dto = new GithubRepoVo();

        final GithubRepoVo vo = po2dto(po, dto);

        final List<String> aiTagUidList = vo.getAiTagUidList();
        final List<ModelCategoryDto> aiTagList = modelCategoryService.getByUids(aiTagUidList);
        vo.setAiTagList(aiTagList);
        return vo;
    }


    public static GithubRepoAdminVo po2adminVo(GithubRepoInfoPo po){
        GithubRepoAdminVo dto = new GithubRepoAdminVo();

        final GithubRepoAdminVo vo = po2dto(po, dto);

        final List<String> aiTagUidList = vo.getAiTagUidList();
        final List<ModelCategoryDto> aiTagList = modelCategoryService.getByUids(aiTagUidList);
        vo.setAiTagList(aiTagList);
        return vo;
    }
    public static <T extends GithubRepoDto> T po2dto(GithubRepoInfoPo po,T dto){

        if (po == null){
            return null;
        }

        BeanUtils.copyProperties(po,dto);

        final String tags = po.getTags();
        List<String> tagList = new ArrayList<String>();
        if (StringUtils.isNotBlank(tags)){

            final JSONArray objects = JSON.parseArray(tags);

            for (int i = 0; i < objects.size(); i++) {
                tagList.add(objects.getString(i));
            }
        }

        final String aiTag = po.getAiTag();
        List<String> aiTagUidList = new ArrayList<>();
        if (StringUtils.isNotBlank(aiTag)&&aiTag.startsWith("[")){
            final JSONArray objects = JSON.parseArray(aiTag);
            for (int i = 0; i < objects.size(); i++) {
                final String uid = objects.getString(i);
                aiTagUidList.add(uid);
            }
        }
        dto.setAiTagUidList(aiTagUidList);
        return dto;
    }

    public static List<GithubRepoVo> po2vos(List<GithubRepoInfoPo> poList){
        if (poList == null){
            return null;
        }
        return poList.stream().map(RepoConvert::po2vo).collect(Collectors.toList());
    }

    public static List<GithubRepoAdminVo> po2adminVos(List<GithubRepoInfoPo> poList){
        if (poList == null){
            return null;
        }
        return poList.stream().map(RepoConvert::po2adminVo).collect(Collectors.toList());
    }


    public static List<GithubRepoDto> po2dtos(List<GithubRepoInfoPo> poList){
        if (poList == null){
            return null;
        }
        return poList.stream().map(RepoConvert::po2dto).collect(Collectors.toList());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        modelCategoryService = applicationContext.getBean(ModelCategoryService.class);
    }
}
