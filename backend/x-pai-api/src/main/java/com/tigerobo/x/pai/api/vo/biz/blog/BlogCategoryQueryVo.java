package com.tigerobo.x.pai.api.vo.biz.blog;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class BlogCategoryQueryVo extends PageReqVo {
    String keyword;
    Integer categoryId;
    String tabType;
}
