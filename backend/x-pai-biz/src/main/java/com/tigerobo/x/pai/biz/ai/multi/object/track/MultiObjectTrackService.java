package com.tigerobo.x.pai.biz.ai.multi.object.track;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.vo.AiMultiObjectTrackVo;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.utils.SortUtil;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.biz.ai.convert.MultiObjectTractConvert;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.InteractionService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.ViewCountService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.hot.HotBusinessService;
import com.tigerobo.x.pai.biz.utils.ListConvertUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiMultiObjectTrackDao;
import com.tigerobo.x.pai.dal.ai.entity.AiMultiObjectTrackPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MultiObjectTrackService implements IBusinessDetailFetchService {
    BusinessEnum businessEnum = BusinessEnum.MULTI_OBJECT_TRACK;
    @Autowired
    private AiMultiObjectTrackDao aiMultiObjectTrackDao;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;
    @Autowired
    private ShareLogService shareLogService;


    @Autowired
    private ViewCountService viewCountService;

    @Autowired
    private InteractionService interactionService;

    

    @Autowired
    private HotBusinessService hotBusinessService;


    public PageVo<AiMultiObjectTrackVo> getPublishList(ArtImagePublicPageReq req) {

        String tabType = req.getTabType();
        if ("hot".equalsIgnoreCase(tabType)) {
            return getHotList(req);
        }
        return getNewPublishList(req);
    }


    public PageVo<AiMultiObjectTrackVo> getNewPublishList(ArtImagePublicPageReq req) {

        Integer userId = ThreadLocalHolder.getUserId();
        Page<AiMultiObjectTrackPo> userPage = aiMultiObjectTrackDao.getPublishListQuery(req);

        PageVo<AiMultiObjectTrackVo> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<AiMultiObjectTrackVo> vos = MultiObjectTractConvert.po2vos(userPage);

        buildListUsers(vos, true);
        initOwner(vos, userId);
        pageVo.setList(vos);
        return pageVo;
    }

    public PageVo<AiMultiObjectTrackVo> getMyList(ArtImagePublicPageReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        Page<AiMultiObjectTrackPo> userPage = aiMultiObjectTrackDao.getUserPage(userId, req);

        PageVo<AiMultiObjectTrackVo> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<AiMultiObjectTrackVo> vos = MultiObjectTractConvert.po2vos(userPage);
        buildListUsers(vos, false);
        initOwner(vos, userId);
        pageVo.setList(vos);
        return pageVo;
    }

    public PageVo<AiMultiObjectTrackVo> getHotList(ArtImagePublicPageReq req) {
        Integer userId = ThreadLocalHolder.getUserId();


        List<String> topIdList = hotBusinessService.getTopIdList(BusinessEnum.STYLE_TRANSFER.getType());


        PageVo<AiMultiObjectTrackVo> hotPage = new PageVo<>();

        if (CollectionUtils.isEmpty(topIdList)) {
            return hotPage;
        }

        List<Integer> ids = topIdList.stream().map(topId -> Integer.parseInt(topId)).collect(Collectors.toList());

        List<Integer> effectIds = aiMultiObjectTrackDao.getEffectByIds(ids, req.getKeyword());
        if (CollectionUtils.isEmpty(effectIds)) {
            return hotPage;
        }

        List<Integer> effectSortIds = ids.stream().filter(effectIds::contains).collect(Collectors.toList());


        int total = effectSortIds.size();

        int startIndex = (req.getPageNum() - 1) * req.getPageSize();

        int end = req.getPageNum() * req.getPageSize();
        if (end >= total) {
            end = total;
        }

        List<Integer> pageIds = effectSortIds.subList(startIndex, end);

        List<AiMultiObjectTrackPo> pos = aiMultiObjectTrackDao.getByIds(pageIds);
        hotPage.setTotal(hotPage.getTotal());
        hotPage.setPageSize(req.getPageSize());
        hotPage.setPageNum(req.getPageNum());

        List<AiMultiObjectTrackVo> vos = MultiObjectTractConvert.po2vos(pos);

        List<AiMultiObjectTrackVo> sortVos = SortUtil.sortByIdIndex(pageIds, vos);
        buildListUsers(sortVos, false);
        initOwner(sortVos, userId);

        hotPage.setTotal(total);
        hotPage.setList(sortVos);
        return hotPage;
    }

    private void initOwner(List<AiMultiObjectTrackVo> vos, Integer loginUserId) {

        if (CollectionUtils.isEmpty(vos)) {
            return;
        }

        for (AiMultiObjectTrackVo vo : vos) {
            Integer userId = vo.getUserId();
            boolean owner = loginUserId != null && loginUserId.equals(userId);
            vo.setRole(owner ? Role.OWNER.toString() : Role.GUEST.toString());
        }

    }

    private void buildListUsers(List<AiMultiObjectTrackVo> vos, boolean needFollowProperty) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        List<Integer> userIds = vos.stream().map(vo -> vo.getUserId()).filter(id -> id != null && id > 0).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        List<User> userList = userService.getBaseUserByIds(userIds);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        Map<Integer, User> userMap = ListConvertUtil.list2map(userList);

        for (AiMultiObjectTrackVo vo : vos) {
            Integer userId = vo.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userMap.get(userId);
            vo.setUser(user);
        }
        if (needFollowProperty) {
            Integer userId = ThreadLocalHolder.getUserId();
            List<Integer> followUserIds = followService.getUserBizFollowsByIds(userIds, FollowTypeEnum.USER.getType(), userId);
            if (!CollectionUtils.isEmpty(followUserIds)) {
                for (AiMultiObjectTrackVo vo : vos) {
                    if (vo.getUserId() != null) {
                        vo.setFollow(followUserIds.contains(vo.getUserId()));
                    }
                }
            }
        }

        List<String> bizIds = vos.stream().map(vo -> String.valueOf(vo.getId())).collect(Collectors.toList());
        Map<String, InteractVo> interactVos = interactionService.completeCount(bizIds, businessEnum.getType());

        for (AiMultiObjectTrackVo vo : vos) {
            InteractVo interactVo = interactVos.get(String.valueOf(vo.getId()));
            vo.setInteract(interactVo);
        }


    }

    public AiMultiObjectTrackVo getByReqId(String userUuid, Long reqId) {
        Validate.isTrue(reqId != null, "参数不正确");
        Integer userId = userService.getIdByUuidCache(userUuid);
        Validate.isTrue(userId != null && userId > 0);
        AiMultiObjectTrackPo imagePo = aiMultiObjectTrackDao.getByReqId(reqId);

        if (imagePo != null) {
            Validate.isTrue(userId.equals(imagePo.getUserId()), "用户没权限");
        }
        return buildDetailVo(imagePo);
    }


    public AiMultiObjectTrackVo getByReqId(Long reqId) {
        Validate.isTrue(reqId != null, "参数不正确");
        AiMultiObjectTrackPo imagePo = aiMultiObjectTrackDao.getByReqId(reqId);

        return buildDetailVo(imagePo);
    }
    public AiMultiObjectTrackVo getDetail(Integer id) {
        IdReqVo idReqVo = new IdReqVo();
        idReqVo.setId(id);
        return getDetail(idReqVo);
    }
    public AiMultiObjectTrackVo getDetail(IdReqVo req) {

        AiMultiObjectTrackVo vo = getById(req.getId());

        if (vo != null && vo.getId() != null) {

            if (req.getShareUserId() != null && req.getShareUserId() > 0) {

                shareLogService.addLog(String.valueOf(vo.getId()), businessEnum.getType(), req.getShareUserId());
            }
            viewCountService.incrView(String.valueOf(vo.getId()), businessEnum.getType());

            InteractVo interactVo = interactionService.completeCount(String.valueOf(vo.getId()), businessEnum.getType());
            vo.setInteract(interactVo);
        }

        return vo;
    }

    public AiMultiObjectTrackVo getById(Integer id) {
        Validate.isTrue(id != null, "参数不正确");
        AiMultiObjectTrackPo imagePo = aiMultiObjectTrackDao.load(id);
        return buildDetailVo(imagePo);
    }

    private AiMultiObjectTrackVo buildDetailVo(AiMultiObjectTrackPo imagePo) {
        Validate.isTrue(imagePo != null, "图片生成记录不存在");

        Integer userId = ThreadLocalHolder.getUserId();
        boolean isOwner = Objects.equals(imagePo.getUserId(), userId);


        AiMultiObjectTrackVo aiArtImageVo = MultiObjectTractConvert.po2vo(imagePo);

        if (!isOwner && !aiArtImageVo.getStatus().equals(1)) {
//            return null;
        }

        String role = isOwner ? Role.OWNER.toString() : Role.GUEST.toString();

        if (!isOwner) {
            boolean follow = followService.isFollow(aiArtImageVo.getUserId(), FollowTypeEnum.USER.getType());
            aiArtImageVo.setFollow(follow);
        }
        User user = userService.getCacheUserByOther(aiArtImageVo.getUserId());
        aiArtImageVo.setUser(user);
        aiArtImageVo.setRole(role);
        return aiArtImageVo;
    }


    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {

        final Integer intId = getIntId(id);
        if (intId == null){
            return null;
        }
        return getById(intId);
    }
}
