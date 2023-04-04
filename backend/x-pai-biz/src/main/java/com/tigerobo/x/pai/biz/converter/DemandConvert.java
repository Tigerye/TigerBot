package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.biz.entity.DemandDo;
import java.lang.IllegalArgumentException;
import org.apache.commons.lang3.StringUtils;

public class DemandConvert {

    public static DemandDo convert(Demand demand) {

        Group group = demand.getGroup();
        if (group == null){
            group = new Group();
        }
        String uuid = demand.getUuid();
        if (StringUtils.isBlank(uuid)){
            uuid = IdGenerator.getId();
        }
        DemandDo po = new DemandDo();
         po.setId(demand.getId());
         po.setUuid(uuid);
         po.setName(demand.getName());

         po.setIntro(demand.getIntro());
         po.setDesc(demand.getDesc());
         po.setImage(demand.getImage());
         po.setBudget(demand.getBudget());
         po.setBudgetValue(demand.getBudgetValue());
         po.setStartDate(demand.getStartDate());
         po.setDeliveryDate(demand.getDeliveryDate());
         po.setDuration(demand.getDuration());
         po.setPhase(demand.getPhase() != null ? demand.getPhase().getVal() : null);
         po.setGroupId(group.getId());
         po.setGroupUuid(group.getUuid());
         po.setScope(demand.getScope()==null? Group.Scope.PUBLIC.getVal():demand.getScope());
         po.setContractId(demand.getContractId());
         po.setContractCategoryId(demand.getContractCategoryId());

        return po;
    }
    public static Demand convert(DemandDo demandDo) {

        if (demandDo == null){
            return null;
        }
        Group group = new Group();
        group.setId(demandDo.getGroupId());
        group.setUuid(demandDo.getGroupUuid());
        Demand.Phase phase = Demand.Phase.valueOf(demandDo.getPhase());
        return Demand.builder()
                .id(demandDo.getId())
                .createTime(demandDo.getCreateTime())
                .updateTime(demandDo.getUpdateTime())
                .isDeleted(demandDo.getIsDeleted())
                .uuid(demandDo.getUuid())
                .name(demandDo.getName())
                .intro(demandDo.getIntro())
                .desc(demandDo.getDesc())
                .nameEn(demandDo.getNameEn())
                .introEn(demandDo.getIntroEn())
                .descEn(demandDo.getDescEn())
                .image(demandDo.getImage())
                .budget(demandDo.getBudget())
                .budgetValue(demandDo.getBudgetValue())
                .startDate(demandDo.getStartDate())
                .deliveryDate(demandDo.getDeliveryDate())
                .duration(demandDo.getDuration())
                .phase(phase)
                .phaseName(phase==null?"":phase.getName())
                .createBy(demandDo.getCreateBy())
                .group(group)
                .groupUuid(group==null?"":group.getUuid())
                .scope(demandDo.getScope())
                .contractId(demandDo.getContractId())
                .contractSampleUrl(demandDo.getContractSampleUrl())
                .reason(demandDo.getReason())
                .contractCategoryId(demandDo.getContractCategoryId())
                .evaluation(demandDo.getEvaluation())
                .modelUuid(demandDo.getModelUuid())
                .build();
    }
}
