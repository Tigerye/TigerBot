package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BlogCategoryConvert {

    public static BlogCategoryDto convert(BlogCategoryRelDto relDto){

        BlogCategoryDto categoryDto = new BlogCategoryDto();

        categoryDto.setId(relDto.getCategoryId());
        categoryDto.setName(relDto.getCategoryName());

        return categoryDto;
    }

    public static List<BlogCategoryDto> convertList(List<BlogCategoryRelDto> relDtoList){


        if (CollectionUtils.isEmpty(relDtoList)){
            return null;
        }
        return relDtoList.stream().map(BlogCategoryConvert::convert).collect(Collectors.toList());

    }
}
