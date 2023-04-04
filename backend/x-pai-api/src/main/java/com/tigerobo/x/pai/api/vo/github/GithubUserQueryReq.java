package com.tigerobo.x.pai.api.vo.github;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class GithubUserQueryReq extends PageReqVo {

    String keyword;

}
