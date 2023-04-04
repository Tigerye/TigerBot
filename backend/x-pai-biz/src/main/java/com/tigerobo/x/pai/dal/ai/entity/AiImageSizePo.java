package com.tigerobo.x.pai.dal.ai.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "ai_image_size")
@Data
public class AiImageSizePo {

    @Id
//    @KeySql(useGeneratedKeys = true)
    private Integer id;

    Integer ratioId;
    String ratio;
    Integer width;
    Integer height;
    Integer seq;
    Boolean isDeleted;

    Boolean isDisco;
    Boolean isStable;

    Integer algCoin;

}
