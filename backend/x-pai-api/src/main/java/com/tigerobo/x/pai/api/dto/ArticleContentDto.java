package com.tigerobo.x.pai.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleContentDto {

    private List<String> contentList;

    private String headImg;
}
