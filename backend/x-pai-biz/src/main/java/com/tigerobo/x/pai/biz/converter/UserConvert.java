package com.tigerobo.x.pai.biz.converter;

import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.constants.ImageConstant;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import org.apache.commons.lang3.StringUtils;

public class UserConvert {

    public static User po2dto(UserDo userPo) {

        if (userPo == null) {
            return null;
        }

        Group group = new Group();
        group.setId(userPo.getCurrGroupId());
        group.setUuid(userPo.getCurrGroupUuid());
        User user = User.builder()
                .id(userPo.getId())
                .uuid(userPo.getUuid())
                .account(userPo.getAccount())
                .name(userPo.getName())
//                .nameEn(userPo.getNameEn())
                .intro(userPo.getIntro())
//                .introEn(userPo.getIntroEn())
                .desc(userPo.getDesc())
//                .descEn(userPo.getDescEn())
                .image(userPo.getImage())
                .mobile(userPo.getMobile())
                .avatar(userPo.getAvatar())
                .wechat(userPo.getWechat())
//                .email(userPo.getEmail())
                .website(userPo.getWebsite())
                .createTime(userPo.getCreateTime())
//                .updateTime(userPo.getUpdateTime())
                .roleType(userPo.getRoleType())
                .wechatName(userPo.getWechatName())
                .currentGroupId(userPo.getCurrGroupId())
                .currentGroupUuid(userPo.getCurrGroupUuid())
                .currGroup(group)
                .group(group)
                .accessSource(userPo.getAccessSource())
                .build();
        if (StringUtils.isBlank(user.getAvatar())) {
            user.setAvatar(ImageConstant.DEFAULT_AVATAR);
        }
        return user;
    }


    public static User memberPo2dto(UserDo userDo) {

        if (userDo == null) {
            return null;
        }

        User user = User.builder()
                .id(userDo.getId())
                .uuid(userDo.getUuid())
                .account(userDo.getAccount())
                .name(userDo.getName())
//                .nameEn(userDo.getNameEn())
                .intro(userDo.getIntro())
//                .introEn(userDo.getIntroEn())
                .desc(userDo.getDesc())
//                .descEn(userDo.getDescEn())
                .image(userDo.getImage())
//                .mobile(userDo.getMobile())
                .avatar(userDo.getAvatar())
//                .wechat(userDo.getWechat())
//                .email(userDo.getEmail())
                .website(userDo.getWebsite())
                .createTime(userDo.getCreateTime())
//                .updateTime(userDo.getUpdateTime())
                .roleType(userDo.getRoleType())
                .currGroup(Group.builder().id(userDo.getCurrGroupId()).uuid(userDo.getCurrGroupUuid()).build())
                .build();
        return user;
    }

    public static User member2dto(User baseUser) {

        if (baseUser == null) {
            return null;
        }

        User user = User.builder()
                .id(baseUser.getId())
                .uuid(baseUser.getUuid())
                .account(baseUser.getAccount())
                .name(baseUser.getName())
//                .nameEn(baseUser.getNameEn())
                .intro(baseUser.getIntro())
//                .introEn(baseUser.getIntroEn())
                .desc(baseUser.getDesc())
//                .descEn(baseUser.getDescEn())
                .image(baseUser.getImage())
//                .mobile(userDo.getMobile())
                .avatar(baseUser.getAvatar())
//                .wechat(userDo.getWechat())
//                .email(userDo.getEmail())
                .website(baseUser.getWebsite())
                .createTime(baseUser.getCreateTime())
                .updateTime(baseUser.getUpdateTime())
                .roleType(baseUser.getRoleType())
                .currGroup(baseUser.getGroup())
                .build();
        return user;
    }

    public static UserBriefVo user2publicVo(User bu) {

        if (bu == null) {
            return null;
        }
        UserBriefVo vo = new UserBriefVo();
        vo.setId(bu.getId());
        vo.setAvatar(bu.getAvatar());
        vo.setAccount(bu.getAccount());
        vo.setName(bu.getName());


        return vo;
    }

    public static User member2dto(User input, boolean needGroup) {

        if (input == null) {
            return null;
        }

        User user = User.builder()
                .id(input.getId())
                .uuid(input.getUuid())
                .account(input.getAccount())
                .name(input.getName())
                .nameEn(input.getNameEn())
                .intro(input.getIntro())
                .introEn(input.getIntroEn())
                .desc(input.getDesc())
                .descEn(input.getDescEn())
                .image(input.getImage())
//                .mobile(userDo.getMobile())
                .avatar(input.getAvatar())
//                .wechat(userDo.getWechat())
//                .email(userDo.getEmail())
                .website(input.getWebsite())
                .createTime(input.getCreateTime())
                .updateTime(input.getUpdateTime())
                .roleType(input.getRoleType())
                .build();
        if (needGroup) {
            user.setGroup(input.getGroup());
        }
        return user;
    }

    public static UserDo dto2po(User user) {
        if (user == null) {
            return null;
        }

        UserDo userDo = new UserDo();
        userDo.setId(user.getId());
        userDo.setAccount(user.getAccount());
        userDo.setName(user.getName());
//        userDo.setNameEn(user.getNameEn() != null ? user.getNameEn() : user.getName());
        userDo.setIntro(user.getIntro());
//        userDo.setIntroEn(user.getIntroEn());
        userDo.setDesc(user.getDesc());
//        userDo.setDescEn(user.getDescEn());
        userDo.setImage(user.getImage());
        userDo.setMobile(user.getMobile());
        userDo.setAreaCode(user.getAreaCode());
        userDo.setAvatar(user.getAvatar() == null ? user.getImage() : user.getAvatar());
        userDo.setWechat(user.getWechat());
//        userDo.setEmail(user.getEmail());
        userDo.setWebsite(user.getWebsite());
        userDo.setPassword(user.getPassword());
        userDo.setCurrGroupId(user.getCurrGroup() != null ? user.getCurrGroup().getId() : user.getId());
        userDo.setRoleType(user.getRoleType());
        userDo.setWechatName(user.getWechatName());

        if (StringUtils.isNotBlank(user.getUuid())) {
            userDo.setUuid(user.getUuid());
        } else {
            String uuid = IdGenerator.getId();
            userDo.setUuid(uuid);
        }

        userDo.setCurrGroupUuid(user.getCurrGroup() != null ? user.getCurrGroup().getUuid() : user.getUuid());
        userDo.setAccessSource(user.getAccessSource());
        return userDo;
    }
}
