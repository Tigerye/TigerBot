package com.tigerobo.x.pai.dal.biz.entity;

import com.tigerobo.x.pai.dal.base.BaseDo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务系统-API接口信息表
 * @modified By:
 * @version: $
 */
@Data
@Table(name = "`xpai-biz-api`")
public class ApiDo extends BaseDo{
//

    protected String name;
    protected String intro;
    @Column(name = "`desc`")
    protected String desc;
    protected String image;

    private String uri;
    private String style;
    private String demo;
    private String pageDemo;
    private Integer status;

    private Integer modelId;
    private String modelUuid;

    private Integer amlModelId;

    String amlRelIds;
    Integer relUserId;
    String baseModelUid;

    Integer originCallCount;

    Integer skuId;

    Boolean showApi;
}
