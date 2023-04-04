package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.dal.auth.entity.GroupDo;

public class GroupConvert {

    public static Group convert(GroupDo groupDo) {
        if (groupDo == null) {
            return null;
        }
        return Group.builder()
                .id(groupDo.getId())
                .uuid(groupDo.getUuid())
                .account(groupDo.getAccount())
                .name(groupDo.getName())
                .nameEn(groupDo.getNameEn())
                .intro(groupDo.getIntro())
                .introEn(groupDo.getIntroEn())
                .desc(groupDo.getDesc())
                .descEn(groupDo.getDescEn())
                .image(groupDo.getImage())
                .scope(Group.Scope.valueOf(groupDo.getScope()))
                .logo(groupDo.getLogo())
//                .mobile(groupDo.getMobile())
                .website(groupDo.getWebsite())
//                .owner(this.userProcessor.get(groupDo.getOwnerId(), groupDo.getOwnerUuid()))
                .updateTime(groupDo.getUpdateTime())
                .createTime(groupDo.getCreateTime())
                .build();
    }
}
