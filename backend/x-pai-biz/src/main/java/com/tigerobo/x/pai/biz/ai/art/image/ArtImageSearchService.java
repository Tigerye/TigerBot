package com.tigerobo.x.pai.biz.ai.art.image;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.biz.ai.convert.ArtImageConvert;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.InteractionService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArtImageSearchService {

    @Autowired
    private AiArtImageDao aiArtImageDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private FollowService followService;
    @Autowired
    private InteractionService interactionService;
    public PageVo<AiArtImageVo> getMyHomeList(ArtImagePublicPageReq req) {

        final Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            return new PageVo<>();
        }
        req.setUserId(userId);
        return getUserHomeList(req);
    }
    public PageVo<AiArtImageVo> getUserHomeList(ArtImagePublicPageReq req) {
        final Integer reqUserId = req.getUserId();
        Validate.isTrue(reqUserId!=null,"未传用户");

        Integer userId = ThreadLocalHolder.getUserId();

        boolean owner = reqUserId.equals(userId);
        Role role = getRole(userId, owner);

        final boolean isGuest = Role.GUEST == role;
        if (isGuest){
            req.setStatus(1);
        }

        Page<AiArtImagePo> userPage = aiArtImageDao.getUserPage(reqUserId, req);

        PageVo<AiArtImageVo> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<AiArtImageVo> vos = ArtImageConvert.po2vos(userPage,isGuest);

        if (CollectionUtils.isEmpty(vos)){
            return pageVo;
        }

        boolean follow = followService.isFollow(reqUserId, FollowTypeEnum.USER.getType(), userId);


        final User fromCache = userService.getFromCache(reqUserId);

        List<String> bizIds = vos.stream().map(vo -> String.valueOf(vo.getId())).collect(Collectors.toList());

        Map<String, InteractVo> interactVos = interactionService.completeCount(bizIds, BusinessEnum.ART_IMAGE.getType());
        if (!CollectionUtils.isEmpty(vos)){
            for (AiArtImageVo vo : vos) {
                vo.setUser(fromCache);
                vo.setRole(role.toString());
                vo.setFollow(follow);

                final InteractVo interactVo = interactVos.get(String.valueOf(vo.getId()));
                vo.setInteract(interactVo);
            }
        }
        pageVo.setList(vos);
        return pageVo;
    }

    private Role getRole(Integer userId, boolean owner) {
        Role role = null;
        if (owner){
            role = Role.OWNER;
        }else {
            final boolean admin = roleService.isAdmin(userId);
            if (admin){
                role = Role.ADMIN;
            }else {
                role = Role.GUEST;
            }
        }
        return role;
    }

}
