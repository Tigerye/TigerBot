package com.tigerobo.x.pai.biz.auth.process;

import com.tigerobo.x.pai.api.auth.entity.Account;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.entity.Entity;
import com.tigerobo.x.pai.biz.converter.UserConvert;
import com.tigerobo.x.pai.dal.auth.dao.GroupDao;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.GroupDo;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Slf4j
@Component
public class AccountProcessor {

    @Autowired
    private UserDao userDao;
    @Autowired
    private GroupDao groupDao;

    public Account getAccount(String account, Integer userId){

        UserDo user = userDao.getByAccount(account);
        if (user != null){
            Account build = Account.builder()
                    .id(user.getId())
                    .uuid(user.getUuid())
                    .account(user.getAccount())
                    .type(Entity.Type.USER)
                    .name(user.getName())
                    .intro(user.getIntro())
                    .desc(user.getDesc())
//                    .nameEn(user.getNameEn())
//                    .introEn(user.getIntroEn())
//                    .descEn(user.getDescEn())
                    .image(user.getImage())
                    .website(user.getWebsite())
                    .build();


            if (userId!=null&&userId.equals(user.getId())){

                User userDto = UserConvert.po2dto(user);
                build.setRole(Role.OWNER);
                build.setUser(userDto);
            }else {
                User userDto = UserConvert.memberPo2dto(user);
                build.setUser(userDto);
            }
            return build;
        }

        GroupDo group = groupDao.getByAccount(account);
        if (group!=null){
            Account build = Account.builder()
                    .id(group.getId())
                    .uuid(group.getUuid())
                    .account(group.getAccount())
                    .type(Entity.Type.GROUP)
                    .name(group.getName())
                    .intro(group.getIntro())
                    .desc(group.getDesc())
                    .nameEn(group.getNameEn())
                    .introEn(group.getIntroEn())
                    .descEn(group.getDescEn())
                    .image(group.getImage())
                    .website(group.getWebsite())
                    .build();

            Integer ownerId = group.getOwnerId();
            if (ownerId!=null&&ownerId.equals(userId)){
                build.setRole(Role.OWNER);
            }
            return build;
        }
        return null;
    }

}
