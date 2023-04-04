package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.biz.entity.Dataset;
import com.tigerobo.x.pai.dal.biz.entity.DatasetDo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatasetConvert {

    public  static List<Dataset> po2dtoList(List<DatasetDo> poList){

        if (CollectionUtils.isEmpty(poList)){
            return new ArrayList<>();
        }
        return poList.stream().map(po->po2dto(po)).collect(Collectors.toList());
    }
    public static Dataset po2dto(DatasetDo datasetDo) {
        if (datasetDo == null) {
            return null;
        }

        return Dataset.builder()
                .id(datasetDo.getId())
                .createTime(datasetDo.getCreateTime())
                .updateTime(datasetDo.getUpdateTime())
                .isDeleted(datasetDo.getIsDeleted())
                .uuid(datasetDo.getUuid())
                .name(datasetDo.getName())
                .intro(datasetDo.getIntro())
                .desc(datasetDo.getDesc())
                .nameEn(datasetDo.getNameEn())
                .introEn(datasetDo.getIntroEn())
                .descEn(datasetDo.getDescEn())
                .image(datasetDo.getImage())
                .format(Dataset.Format.valueOf2(datasetDo.getFormat()))
                .filePath(datasetDo.getFilePath())
//                .group(this.groupProcessor.get(datasetDo.getGroupId(), datasetDo.getGroupUuid()))
                .build();
    }

}
