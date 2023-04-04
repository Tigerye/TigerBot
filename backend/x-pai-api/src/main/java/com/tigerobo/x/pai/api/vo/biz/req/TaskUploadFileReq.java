package com.tigerobo.x.pai.api.vo.biz.req;

import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

import java.util.List;

@Data
public class TaskUploadFileReq extends RequestVo {

    String taskUuid;

    List<FileData> datasetList;

}
