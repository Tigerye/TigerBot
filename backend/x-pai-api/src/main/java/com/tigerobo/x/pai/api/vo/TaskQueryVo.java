package com.tigerobo.x.pai.api.vo;

import lombok.Data;

@Data
public class TaskQueryVo extends QueryVo{

    private String keyword;
    private String industryTagUid;
    private String baseModelUid;
    private String nlpUid;
    private String cvUid;

    
}
