package com.tigerobo.x.pai.api.vo.biz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModelApiReq {

    String modelUuid;

    String image;
    String modelName;
    //------
    String taskUuid ;
    String apiUrl;
    Integer userId ;
    String style;
    String demo;
    String pageDemo;

    String sceneIntro;
    String tectIntro;


    Integer groupId;
    String groupUuid;

    String appShortName;

    String baseModelUid;

    Date modelUpdateTime;

    String slogan;
}
