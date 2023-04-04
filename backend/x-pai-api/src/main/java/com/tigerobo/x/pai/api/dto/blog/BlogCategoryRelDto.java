package com.tigerobo.x.pai.api.dto.blog;

import lombok.Data;

@Data
public class BlogCategoryRelDto {

    private Integer blogId;
    private Integer categoryId;
    private String categoryName;
}
