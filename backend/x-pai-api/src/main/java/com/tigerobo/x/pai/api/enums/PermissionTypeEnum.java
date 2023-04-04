package com.tigerobo.x.pai.api.enums;

import lombok.Getter;

@Getter
public enum  PermissionTypeEnum {

    ADMIN(10,"管理员"),
    DEMAND_MODIFY(110,"修改需求"),

    ADD_FILE(120,"添加附件"),

    ADD_DEMAND_SUGGEST(130,"添加需求建议"),
    VIEW_DEMAND_SUGGEST(131,"添加需求建议"),

    ADD_DATASET(140,"添加数据集"),


    AUDIT(150,"审核通过"),
    AUDIT_DECLINE(150,"评审拒绝"),

    TASK_JOIN(200,"领取任务"),

    TASK_SUBMIT(210,"任务上传"),

    UPLOAD_CONTRACT_SAMPLE(290,"上传合同样例"),
    TEST_PASS(300,"测试通过"),
    SIGN_CONTRACT(350,"签约"),
    VIEW_CONTRACT(355,"查看合同"),
    COMPLETE(400,"需求完成"),

    VIEW_STATISTIC_BOARD(500,"统计看板"),
    ;

    private Integer id;
    private String name;
    PermissionTypeEnum(Integer id,String name){
        this.id = id;
        this.name = name;
    }
}
