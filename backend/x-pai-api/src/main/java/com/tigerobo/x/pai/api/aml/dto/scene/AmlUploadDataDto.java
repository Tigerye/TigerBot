package com.tigerobo.x.pai.api.aml.dto.scene;

import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.api.vo.RequestVo;
import lombok.Data;

import java.util.List;

@Data
public class AmlUploadDataDto extends RequestVo {
    private Integer amlId;
//    private List<String> fileUrlList;
    private String fileType;

    private List<FileData> datasetList;
}
