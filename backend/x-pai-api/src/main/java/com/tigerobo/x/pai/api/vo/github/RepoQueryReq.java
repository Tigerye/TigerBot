package com.tigerobo.x.pai.api.vo.github;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RepoQueryReq extends PageReqVo {

    @ApiModelProperty("tab:new,follow")
    String tabType;
    String keyword;
    Integer userId;

    @ApiModelProperty(hidden = true)
    List<Integer> followUserIds;
    @ApiModelProperty(hidden = true)
    List<String> aiTagList;
    String nlpUid;
    String cvUid;

}
