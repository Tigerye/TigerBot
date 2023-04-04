package com.tigerobo.x.pai.biz.ai.art.image;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.ai.vo.AiArtImageVo;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.ai.ArtModifierDto;
import com.tigerobo.x.pai.api.dto.ai.ArtModifierModel;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.utils.SortUtil;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.interact.InteractVo;
import com.tigerobo.x.pai.biz.ai.convert.ArtImageConvert;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.InteractionService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.ViewCountService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.hot.HotBusinessService;
import com.tigerobo.x.pai.biz.utils.ListConvertUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.dao.AiParamDictDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.entity.AiParamDictPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 线上接口服务
 */
@Slf4j
@Service
public class ArtImageService implements IBusinessDetailFetchService {

    @Autowired
    private AiArtImageDao aiArtImageDao;


    @Autowired
    private AiParamDictDao aiParamDictDao;

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

    BusinessEnum businessEnum = BusinessEnum.ART_IMAGE;

    @Autowired
    private HotBusinessService hotBusinessService;

    @Autowired
    private RoleService roleService;


    final List<String> sortSeq = Arrays.asList("Artists","Classic");



    @Deprecated
    public List<ArtModifierModel> getModifierWithImgList() {

        List<AiParamDictPo> list = aiParamDictDao.getFromCache(2);


        if (list == null||list.isEmpty()){
            return new ArrayList<>();
        }

        final List<ArtModifierDto> collect = list.stream().map(po -> {
            return ArtModifierDto.builder()
                    .name(po.getName())
                    .imgUrl(po.getImgUrl())
                    .text(po.getText())
                    .classType(po.getClassType())
                    .build();
        }).collect(Collectors.toList());

        final Map<String, List<ArtModifierDto>> map = collect.stream().collect(Collectors.groupingBy(ArtModifierDto::getClassType));

        List<ArtModifierModel> modifierModels = new ArrayList<>();
        for (String s : sortSeq) {
            final List<ArtModifierDto> artModifierDtos = map.get(s);
            if (!CollectionUtils.isEmpty(artModifierDtos)){
                modifierModels.add(ArtModifierModel.builder().classType(s).modifierDtos(artModifierDtos).build());
            }
        }

        for (Map.Entry<String, List<ArtModifierDto>> entry : map.entrySet()) {
            if (!sortSeq.contains(entry.getKey())){
                modifierModels.add(ArtModifierModel.builder().classType(entry.getKey()).modifierDtos(entry.getValue()).build());
            }
        }

        return modifierModels;
    }


    public PageVo<AiArtImageVo> getPublishList(ArtImagePublicPageReq req) {

        Integer userId = ThreadLocalHolder.getUserId();
        PageVo<AiArtImageVo> pageVo = new PageVo<>();
        final String tabType = req.getTabType();
        if (!StringUtils.isEmpty(tabType)){
            if (TabType.follow.toString().equalsIgnoreCase(tabType)){
                if (userId == null||userId<=0){
                    return pageVo;
                }
                final List<Integer> followUserIds = followService.getFollowUserIdsByUserId(userId);
                if (CollectionUtils.isEmpty(followUserIds)){
                    return pageVo;
                }
                req.setUserIds(followUserIds);
            }
        }

        String order = initOrderAndHot(req);
        Page<AiArtImagePo> userPage = aiArtImageDao.getPublishListQuery(req,order);



        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<AiArtImageVo> vos = ArtImageConvert.po2vos(userPage,true);

        buildListUsers(vos, true);
        initOwner(vos, userId);
        pageVo.setList(vos);
        return pageVo;
    }

    private String initOrderAndHot(ArtImagePublicPageReq req) {
        String tabType = req.getTabType();

        String order = "ai.publish_time desc";

        final String hotType = req.getHotType();
        boolean ifTabHot = (!StringUtils.isEmpty(hotType)&&!HotType.latest.toString().equalsIgnoreCase(hotType))||"hot".equals(tabType);
        if (ifTabHot){
            order = "ai.thumb_num desc,ai.publish_time desc";
            final HotType hotTypeEnum = HotType.getByType(hotType);
            Date startCreateTime = null;
            Date now = new Date();
            if (hotTypeEnum!=null){
                switch (hotTypeEnum){
                    case oneHour:startCreateTime = DateUtils.addHours(now,-1);break;
                    case oneDay:startCreateTime = DateUtils.addDays(now,-1);break;
                    case oneWeek:startCreateTime = DateUtils.addWeeks(now,-1);break;
                    case oneMonth:startCreateTime = DateUtils.addMonths(now,-1);break;
                }
            }
            if (startCreateTime!=null){
                req.setStartCreateTime(startCreateTime);
            }
        }

        return order;
    }

    public List<AiArtImageVo> getByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        final List<AiArtImagePo> pos = aiArtImageDao.getByIds(ids);
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        List<AiArtImageVo> vos = ArtImageConvert.po2vos(pos);

        buildListUsers(vos, true);
        final Integer userId = ThreadLocalHolder.getUserId();
        initOwner(vos, userId);
        return vos;
    }

    public PageVo<AiArtImageVo> getMyList(ArtImagePublicPageReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        Page<AiArtImagePo> userPage = aiArtImageDao.getUserPage(userId, req);

        PageVo<AiArtImageVo> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<AiArtImageVo> vos = ArtImageConvert.po2vos(userPage);
        buildListUsers(vos, false);
        initOwner(vos, userId);
        pageVo.setList(vos);
        return pageVo;
    }

    public PageVo<AiArtImageVo> getHotList(ArtImagePublicPageReq req) {
        Integer userId = ThreadLocalHolder.getUserId();


        List<String> topIdList = hotBusinessService.getTopIdList(BusinessEnum.ART_IMAGE.getType());


        PageVo<AiArtImageVo> hotPage = new PageVo<>();

        if (CollectionUtils.isEmpty(topIdList)) {
            return hotPage;
        }

        List<Integer> ids = topIdList.stream().map(topId -> Integer.parseInt(topId)).collect(Collectors.toList());

        List<Integer> effectIds = aiArtImageDao.getEffectByIds(ids, req.getKeyword());
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

        List<AiArtImagePo> pos = aiArtImageDao.getByIds(pageIds);
        hotPage.setTotal(hotPage.getTotal());
        hotPage.setPageSize(req.getPageSize());
        hotPage.setPageNum(req.getPageNum());

        List<AiArtImageVo> vos = ArtImageConvert.po2vos(pos,true);

        List<AiArtImageVo> sortVos = SortUtil.sortByIdIndex(pageIds, vos);
        buildListUsers(sortVos, false);
        initOwner(sortVos, userId);

        hotPage.setTotal(total);
        hotPage.setList(sortVos);
        return hotPage;
    }

    private void initOwner(List<AiArtImageVo> vos, Integer loginUserId) {

        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        final boolean admin = roleService.isAdmin();

        for (AiArtImageVo vo : vos) {
            Integer userId = vo.getUserId();
            boolean owner = loginUserId != null && loginUserId.equals(userId);
            Role role = Role.GUEST;
            if (owner){
                role = Role.OWNER;
            }else if (admin){
                role = Role.ADMIN;
            }
            vo.setRole(role.toString());
        }

    }

    private void buildListUsers(List<AiArtImageVo> vos, boolean needFollowProperty) {
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

        for (AiArtImageVo vo : vos) {
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
                for (AiArtImageVo vo : vos) {
                    if (vo.getUserId() != null) {
                        vo.setFollow(followUserIds.contains(vo.getUserId()));
                    }
                }
            }
        }

        List<String> bizIds = vos.stream().map(vo -> String.valueOf(vo.getId())).collect(Collectors.toList());
        Map<String, InteractVo> interactVos = interactionService.completeCount(bizIds, businessEnum.getType());

        for (AiArtImageVo vo : vos) {
            InteractVo interactVo = interactVos.get(String.valueOf(vo.getId()));
            vo.setInteract(interactVo);
        }


    }
    public AiArtImageVo getByReqId(String userUuid,Long reqId) {
        Validate.isTrue(reqId != null, "参数不正确");
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            userId = userService.getIdByUuidCache(userUuid);
        }
//        Integer userId = userService.getIdByUuidCache(userUuid);
        Validate.isTrue(userId!=null&&userId>0);
        AiArtImagePo imagePo = aiArtImageDao.getByReqId(reqId);

        if (imagePo!=null){
            Validate.isTrue(userId.equals(imagePo.getUserId()),"用户没权限");
        }
        ThreadLocalHolder.setUserId(userId);
        return buildDetailVo(imagePo);
    }
    public AiArtImageVo getByReqId(Long reqId) {
        Validate.isTrue(reqId != null, "参数不正确");
        AiArtImagePo imagePo = aiArtImageDao.getByReqId(reqId);
        return ArtImageConvert.po2vo(imagePo);
    }
//
//    public AiArtImageVo getByReqId(Long reqId) {
//        Validate.isTrue(reqId != null, "参数不正确");
//        AiArtImagePo imagePo = aiArtImageDao.getByReqId(reqId);
//
//        return buildDetailVo(imagePo);
//    }

    public AiArtImageVo getDetail(Integer id){
        IdReqVo reqVo = new IdReqVo();
        reqVo.setId(id);
        return getDetail(reqVo);
    }

    public AiArtImageVo getDetail(IdReqVo req) {

        AiArtImageVo vo = getById(req.getId());

        if (vo != null && vo.getId() != null) {
            BusinessEnum artImage = BusinessEnum.ART_IMAGE;
            if (req.getShareUserId() != null && req.getShareUserId() > 0) {

                shareLogService.addLog(String.valueOf(vo.getId()), businessEnum.getType(), req.getShareUserId());
            }
            viewCountService.incrView(String.valueOf(vo.getId()), artImage.getType());

            InteractVo interactVo = interactionService.completeCount(String.valueOf(vo.getId()), businessEnum.getType());
            vo.setInteract(interactVo);
        }

        return vo;
    }

    public AiArtImageVo getById(Integer id) {
        Validate.isTrue(id != null, "参数不正确");
        AiArtImagePo imagePo = aiArtImageDao.load(id);
        return buildDetailVo(imagePo);
    }

    private AiArtImageVo getBase(Integer id){
        if (id == null){
            return null;
        }
        AiArtImagePo imagePo = aiArtImageDao.load(id);

        return ArtImageConvert.po2vo(imagePo);
    }
    private AiArtImageVo buildDetailVo(AiArtImagePo imagePo) {
        Validate.isTrue(imagePo != null, "图片生成记录不存在");

        Integer userId = ThreadLocalHolder.getUserId();
        boolean isOwner = Objects.equals(imagePo.getUserId(), userId);

        final boolean admin = roleService.isAdmin(userId);
        String role = isOwner ? Role.OWNER.toString() : (admin?Role.ADMIN.toString():Role.GUEST.toString());
        boolean canViewPermit = isOwner||admin;

        AiArtImageVo aiArtImageVo = ArtImageConvert.po2vo(imagePo,!canViewPermit);


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

        if (id == null){
            return null;
        }
        if (!id.matches("\\d+")){
            return null;
        }
        final int i = Integer.parseInt(id);

        return getBase(i);

    }

    public List<String> getHistoryImages() {

        final Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null){
            return new ArrayList<>();
        }
        final List<AiArtImagePo> userInputImageHistory = aiArtImageDao.getUserInputImageHistory(userId);

        if (CollectionUtils.isEmpty(userInputImageHistory)){
            return new ArrayList<>();
        }

        return userInputImageHistory.stream().map(AiArtImagePo::getInputImage).distinct().limit(5).collect(Collectors.toList());
    }

    enum TabType{
        fresh,hot,follow;
    }

    enum HotType{
        latest,oneHour,oneDay,oneWeek,oneMonth,all;

        static HotType getByType(String type){
            if (StringUtils.isEmpty(type)){
                return null;
            }
            for (HotType value : values()) {
                if (value.toString().equalsIgnoreCase(type)){
                    return value;
                }
            }
            return null;
        }
    }
}
