package com.tigerobo.x.pai.api.req;

import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import lombok.Data;

@Data
public class UserCommitPageReq extends PageReqVo {
    Integer userId;
}
