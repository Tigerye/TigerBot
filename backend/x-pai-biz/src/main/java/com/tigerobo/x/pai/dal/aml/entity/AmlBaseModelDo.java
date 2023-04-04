package com.tigerobo.x.pai.dal.aml.entity;

import com.tigerobo.x.pai.dal.base.BaseDo;
import lombok.Data;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
//@NoArgsConstructor
//@AllArgsConstructor
@Data
//@SuperBuilder
@Table(name = "`aml_base_model`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class AmlBaseModelDo extends BaseDo {

    String name;
    String intro;
    String img;
    String style;
}
