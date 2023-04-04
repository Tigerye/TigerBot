package com.tigerobo.x.pai.biz.biz.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.auth.vo.GroupVo;
import com.tigerobo.x.pai.api.constants.ImageConstant;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.biz.cache.biz.MachineUtil;
import com.tigerobo.x.pai.biz.converter.GroupConvert;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.dal.auth.dao.GroupDao;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.GroupDo;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebGroupService {

    @Autowired
    GroupDao groupDao;
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;
    @Autowired
            private MachineUtil machineUtil;
    Cache<String, Group> groupCache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .maximumSize(200)
            .build();


    public Group getByUuidFromCache(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            return null;
        }
        return groupCache.get(uuid, this::getById);
    }

    public PageInfo<GroupVo> getPublic() {
        List<GroupDo> publicList = groupDao.getPublicList();
        PageInfo<GroupVo> pageInfo = new PageInfo<>();
        if (CollectionUtils.isEmpty(publicList)) {
            return pageInfo;
        }

        List<Group> collect = publicList.stream().map(p -> GroupConvert.convert(p)).collect(Collectors.toList());

        List<GroupVo> list = collect.stream().map(c -> GroupVo.builder().group(c).build()).collect(Collectors.toList());
        pageInfo.setList(list);
        return pageInfo;
    }

    public Group getCreateByOrGroup(String createBy, String groupUuid) {
        Group group = doGetCreateByOrGroup(createBy, groupUuid);

        if (group != null) {
            if (StringUtils.isBlank(group.getLogo())) {
                group.setLogo(ImageConstant.DEFAULT_AVATAR);
            }
        }
        return group;
    }

    public Group doGetCreateByOrGroup(String createBy, String groupUuid) {
        if (StringUtils.isNotBlank(groupUuid)) {
            Group group = getByUuidFromCache(groupUuid);
            if (group != null) {
                return group;
            }
        }
        if (StringUtils.isNotBlank(createBy) && createBy.matches("\\d+")) {
            int userId = Integer.parseInt(createBy);

            User user = userService.getFromCache(userId);
            if (user != null) {
                return user.toPersonalGroup();
            }
        }
        return null;
    }

    public Authorization joinGroup(Group group, Integer userId) {

        if (userId == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }

        Preconditions.checkArgument(group != null, "参数为空");
        String uuid = group.getUuid();
//        String account = group.getAccount();
//        if ((StringUtils.isEmpty(uuid) && StringUtils.isEmpty(account))) {
//            throw new IllegalArgumentException("请选择组织");
//        }
        UserDo userDo = userDao.getById(userId);
        Preconditions.checkArgument(userDo != null, "用户不存在");

        GroupDo groupDo = null;
        if (StringUtils.isNotBlank(uuid)) {
            groupDo = groupDao.getByUuid(uuid);
            if (groupDo == null) {
                throw new IllegalArgumentException("组织不存在");
            }
        } else {
//            GroupDo accountGroup = groupDao.getByAccount(group.getAccount());
//            Preconditions.checkArgument(accountGroup == null, "账号:" + group.getAccount() + "已存在");

            String groupUuid = String.valueOf(IdGenerator.getId(machineUtil.getMachineId()));

            groupDo = new GroupDo();
            groupDo.setAccount(groupUuid);
            groupDo.setName(group.getName());
            groupDo.setDescEn(group.getNameEn());
            String logo = StringUtils.isEmpty(group.getLogo())?ImageConstant.DEFAULT_AVATAR:group.getLogo();
            groupDo.setLogo(logo);
            String image = StringUtils.isEmpty(group.getImage())?ImageConstant.DEFAULT_AVATAR:group.getImage();

            groupDo.setImage(image);
            groupDo.setScope(Group.Scope.PUBLIC.getVal());
            groupDo.setOwnerId(userId);
            groupDo.setCreateBy(String.valueOf(userId));
            groupDo.setUpdateBy(String.valueOf(userId));
            groupDo.setUuid(groupUuid);
            groupDao.insert(groupDo);
            Preconditions.checkArgument(groupDo.getId() != null, "创建组织失败");
        }
        //todo,日志
        UserDo userUpdateGroup = new UserDo();
        userUpdateGroup.setId(userId);
        userUpdateGroup.setCurrGroupId(groupDo.getId());
        userUpdateGroup.setCurrGroupUuid(groupDo.getUuid());
        userDao.update(userUpdateGroup);

        Authorization authorization = new Authorization();
        group.setScope(Group.Scope.PUBLIC);
        Group convert = GroupConvert.convert(groupDo);
        authorization.setGroup(convert);
        Role role = userId.equals(groupDo.getOwnerId()) ? Role.OWNER : Role.DEVELOPER;
        authorization.setRole(role);
        return authorization;

    }

    public List<Group> getByUuids(List<String> groupUuids){
        if (CollectionUtils.isEmpty(groupUuids)){
            return null;
        }
        List<GroupDo> groupList = groupDao.getByUuids(groupUuids);
        if (CollectionUtils.isEmpty(groupList)){
            return null;
        }
        return groupList.stream().map(g->GroupConvert.convert(g)).collect(Collectors.toList());
    }

    public Group getById(String uuid) {
        GroupDo po = groupDao.getByUuid(uuid);
        return GroupConvert.convert(po);
    }

}
