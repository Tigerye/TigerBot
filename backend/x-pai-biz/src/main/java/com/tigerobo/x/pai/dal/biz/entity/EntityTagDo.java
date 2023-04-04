package com.tigerobo.x.pai.dal.biz.entity;

import com.tigerobo.x.pai.dal.base.BaseDo;
import lombok.Data;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 业务系统-实体标签信息表
 * @modified By:
 * @version: $
 */
@Data
//@EqualsAndHashCode(callSuper = false)
//@SuperBuilder
//@NoArgsConstructor
//@AllArgsConstructor
@Table(name = "`xpai-biz-entity-tag`", uniqueConstraints = {@UniqueConstraint(columnNames = {"id", "uuid"})})
public class EntityTagDo extends BaseDo {
    /*
    -- -----------------------------------------------------
    -- Table `x_pai`.`xpai-biz-entity-tag`
    -- -----------------------------------------------------
    DROP TABLE IF EXISTS `x_pai`.`xpai-biz-entity-tag`;

    CREATE TABLE IF NOT EXISTS `x_pai`.`xpai-biz-entity-tag` (
        `id`          BIGINT UNIQUE       NOT NULL AUTO_INCREMENT,
        `create_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        `create_by`   VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '创建者',
        `update_time` DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
        `update_by`   VARCHAR(64)         NOT NULL DEFAULT '' COMMENT '修改者',
        `is_deleted`  TINYINT(1) ZEROFILL NOT NULL DEFAULT 0,
        `uuid`        VARCHAR(32)         NOT NULL COMMENT '唯一键UUID',
        `entity_id`   BIGINT              NOT NULL COMMENT '对象ID',
        `entity_uuid` VARCHAR(32)         NOT NULL COMMENT '对象UUID',
        `entity_type` INT                 NOT NULL COMMENT '对象类型',
        `tag_type`    INT                 NOT NULL COMMENT '标签类型',
        `tag_uid`     VARCHAR(64)         NOT NULL COMMENT '标签UID',
        `status`      INT                 NOT NULL DEFAULT 0 COMMENT '标签状态',
        PRIMARY KEY (`id`),
        UNIQUE INDEX `uuid_UNIQUE`(`uuid` ASC),
        UNIQUE INDEX `entity_tag_UNIQUE`(`entity_id` ASC, `entity_type` ASC, `tag_type` ASC, `tag_uid` ASC),
        UNIQUE INDEX `entity_tag_uk`(`entity_id` ASC, `entity_uuid` ASC, `entity_type` ASC, `tag_type` ASC, `tag_uid` ASC),
        INDEX `entity_tag_entity_idx`(`entity_id` ASC, `entity_uuid` ASC, `entity_type` ASC),
        INDEX `entity_tag_tag_idx`(`tag_type` ASC, `tag_uid` ASC, `status` ASC)
    ) ENGINE = InnoDB COMMENT = 'XPAI-业务系统-实体标签信息表';
     */
    private Integer entityId;
    private String entityUuid;
    private Integer entityType;
    private Integer tagType;
    private String tagUid;
    private Integer status;
}
