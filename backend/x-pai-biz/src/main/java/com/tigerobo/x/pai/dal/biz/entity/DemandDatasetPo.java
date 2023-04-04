package com.tigerobo.x.pai.dal.biz.entity;

import com.tigerobo.x.pai.dal.base.ComDo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
//@EqualsAndHashCode(callSuper = false)
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "`demand_dataset`")
public class DemandDatasetPo extends ComDo {
    private String demandUuid;
    private String filePath;
    @Column(name = "name")
    private String name;
}
