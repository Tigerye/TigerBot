package com.tigerobo.x.pai.api.vo.biz.req;

import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

@Data
public class TaskDeleteFileReq extends RequestVo {

    private String taskUuid;
    private String datasetUuid;
}
