package com.tigerobo.x.pai.biz.biz.blog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.utils.SortUtil;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.SiteRelBlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogDetailReq;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.constant.RedisConstants;
import com.tigerobo.x.pai.biz.converter.*;
import com.tigerobo.x.pai.biz.oss.OssCacheService;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.biz.utils.TimeUtil;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCrawlerDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogService implements IBusinessDetailFetchService {

    @Autowired
    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;

    @Autowired
    private UserService userService;
    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    BlogCrawlerDao blogCrawlerDao;

    @Autowired
    private PubSiteService pubSiteService;
    @Autowired
    private FollowService followService;
    @Autowired
    private PubBigShotDao pubBigShotDao;
    @Autowired
    private ShareLogService shareLogService;

    @Autowired
    private UserThumbService userThumbService;

    @Autowired
    private BusinessCommentService businessCommentService;

    @Autowired
    private BlogChatService blogChatService;

    @Autowired
    private BlogCategoryRelService blogCategoryRelService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private MemberService memberService;

    @Value("${pai.blog.vipViewLimit:5}")
    private int vipViewLimit;

    @Autowired
    private OssCacheService ossCacheService;

    public BlogVo getPageDetail(BlogDetailReq req) {

        req.setViewDetail(true);

        return getDetail(req);
    }

    public BlogVo getDetail(BlogDetailReq req) {

        if (req == null || req.getId() == null) {
            return null;
        }
        BlogVo detail = getDetail(req.getId());

        if (detail != null && req.isViewDetail()) {

            completeCount(detail);
        }
        if (detail != null && detail.getId() != null && req.getShareUserId() != null && req.getShareUserId() > 0) {
            BusinessEnum businessEnum = BusinessEnum.BLOG;
            shareLogService.addLog(String.valueOf(detail.getId()), businessEnum.getType(), req.getShareUserId());
        }
        return detail;
    }
    public BlogVo getDetail(Integer id) {
        return getDetail(id,new ViewConfig());
    }
    public BlogVo getDetail(Integer id,ViewConfig config) {
        Integer userId = ThreadLocalHolder.getUserId();
        BlogInfoPo poInDb = blogInfoDao.load(id);
        if (poInDb == null) {
            return null;
        }
        boolean owner = userId != null && userId.equals(poInDb.getUserId());
        if (!poInDb.getOnlineStatus().equals(1)) {
            if (!owner) {
                return null;
            }
        }
        BlogVo blogVo = BlogConvert.convert(poInDb);

        if (config.brief){
            return blogVo;
        }

        int view = getView(blogVo.getId());
        blogVo.setViewNum(view);


        boolean admin = roleService.isAdmin();
        boolean member = memberService.isMember(userId);

        List<Integer> followIds = followService.getBizFollowIdList(userId, FollowTypeEnum.SITE.getType());
        PubSiteVo site = pubSiteService.getFromCache(blogVo.getSiteId(), followIds);
        if (site != null) {
            blogVo.setSourceName(site.getName());
        }

        Integer bigShotId = poInDb.getBigShotId();
        if (bigShotId != null && bigShotId > 0) {
            PubBigShotPo bigShotPo = pubBigShotDao.load(bigShotId);

            if (bigShotPo == null) {
                return null;
            }
            blogVo.setBigShot(PubBigShotConvert.convert(bigShotPo));

        }

        User userByOther = userService.getUserByOther(blogVo.getUserId());
        Role role = owner ? Role.OWNER : Role.GUEST;
        blogVo.setSite(site);

        BlogSourceFromEnum sourceFrom = BlogSourceFromEnum.getByType(blogVo.getSourceFrom());
        boolean follow = false;
        Integer sourceId = null;
        if (BlogSourceFromEnum.SITE == sourceFrom && site != null) {
            follow = followService.isFollow(blogVo.getSiteId(), blogVo.getSourceFrom());
            blogVo.setSourceLogo(site.getLogoOss());
            blogVo.setSourceName(site.getName());
            sourceId = blogVo.getSiteId();
        } else if (BlogSourceFromEnum.ALGOLET == sourceFrom && userByOther != null) {
            follow = followService.isFollow(blogVo.getUserId(), blogVo.getSourceFrom());
            blogVo.setSourceLogo(userByOther.getAvatar());
            sourceId = blogVo.getUserId();
        } else if (BlogSourceFromEnum.BIG_SHOT == sourceFrom && blogVo.getBigShot() != null) {
            follow = followService.isFollow(blogVo.getBigShotId(), blogVo.getSourceFrom());
            blogVo.setSourceLogo(blogVo.getBigShot().getLogo());
            blogVo.setAuthorName(blogVo.getBigShot().getName());
            blogVo.setSourceName("twitter");
            sourceId = blogVo.getBigShotId();
            Boolean hasReplyChain = blogVo.getHasReplyChain();
            if (hasReplyChain != null && hasReplyChain) {
//                List<BlogBigshotReplyPo> replyPos = blogBigshotReplyDao.getByBlogId(blogVo.getId());
//                List<BlogChatVo> replyVos = BlogConvert.convert2reply(replyPos);

                List<BlogChatVo> chatList = blogChatService.getBlogChainByBlogId(poInDb.getId(), poInDb.getSpecId());
                if (CollectionUtils.isEmpty(chatList)) {
                    blogVo.setHasReplyChain(false);
                } else {
                    blogVo.setReplyList(chatList);
                }

            }
        }

        boolean canView = canView(admin, member, poInDb.getVip());

        if (!canView) {
            String ip = ThreadLocalHolder.getIp();
            boolean canViewOnVipViewLimit = canViewOnVipViewLimit(userId, ip, blogVo.getSourceFrom(), sourceId);
            if (canViewOnVipViewLimit) {
                canView = true;
            }
        }
        blogVo.setCanView(canView);


        List<BlogCategoryRelDto> blogCategoryList = blogCategoryRelService.getBlogCategory(id);

        List<BlogCategoryDto> blogCategoryDtos = BlogCategoryConvert.convertList(blogCategoryList);
        blogVo.setCategoryList(blogCategoryDtos);
//
//        if (!CollectionUtils.isEmpty(blogCategoryList)) {
//            blogVo.setCategoryList(blogCategoryList.stream().map(BlogCategoryRelDto::getCategoryName).collect(Collectors.toList()));
//        } else {
//            blogVo.setCategoryList(null);
//        }

        blogVo.setFollow(follow);
        blogVo.setRole(role.toString());
        blogVo.setUser(userByOther);

        initPrivateOss(blogVo,poInDb);
        return blogVo;
    }

    private void initPrivateOss(BlogVo blogVo, BlogInfoPo po){

        Integer sourceFrom = po.getSourceFrom();
        if (!BlogSourceFromEnum.SITE.getType().equals(sourceFrom)){
            return;
        }
        String ossUrlKey = po.getOssUrl();
        String translateUrlKey = po.getTranslateUrl();
        String zhenUrlKey = po.getZhenUrl();

        String ossUrl = ossCacheService.getPrivateKeyUrl(ossUrlKey);
            String translateUrl = ossCacheService.getPrivateKeyUrl(translateUrlKey);
        String zhenUrl = ossCacheService.getPrivateKeyUrl(zhenUrlKey);

        blogVo.setOssUrl(ossUrl);
        blogVo.setTranslateUrl(translateUrl);
        blogVo.setZhenUrl(zhenUrl);

    }

    private boolean canView(boolean admin, boolean isMember, Integer vip) {

        if (vip == null || vip == 0) {
            return true;
        }
        if (admin || isMember) {
            return true;
        }
        return false;
    }

    private void completeCount(BlogVo blogVo) {
        Integer id = blogVo.getId();
        if (id == null) {
            return;
        }

        int thumbUpCount = userThumbService.countBlog(id);
        blogVo.setThumbUpNum(thumbUpCount);

        int blogCount = businessCommentService.countOnBizId(String.valueOf(id), BusinessEnum.BLOG.getType());

        blogVo.setCommentNum(blogCount);
        int shareNum = shareLogService.getCount(String.valueOf(id), BusinessEnum.BLOG.getType());
        blogVo.setShareNum(shareNum);

        Integer userId = ThreadLocalHolder.getUserId();

        List<UserThumbPo> thumbUpList = userThumbService.getUserList(userId, Arrays.asList(String.valueOf(id)), BusinessEnum.BLOG.getType());
        if (!CollectionUtils.isEmpty(thumbUpList)) {
            blogVo.setThumbUp(true);
        }

        int commentNum = businessCommentService.countUserCommentNum(userId, String.valueOf(id), BusinessEnum.BLOG.getType());
        blogVo.setUserHasComment(commentNum > 0);

    }

    public int getBlogCount() {


        String key = "pai:blog:total";
        int num = redisCacheService.getNum(key);
        if (num == 0) {
            num = blogSearchDao.getBlogInfoCount();
            redisCacheService.set(key, String.valueOf(num), 80);
        }
        return num;
    }

    public int getBloggerCount() {
        return blogInfoDao.getBloggerCount();
    }

    public List<User> getTopUsers() {
        List<Integer> bloggers = blogInfoDao.getTopBloggers();
        if (CollectionUtils.isEmpty(bloggers)) {
            return new ArrayList<>();
        }

        List<User> users = bloggers.stream().map(userId -> UserConvert.member2dto(userService.getFromCache(userId), false)).filter(Objects::nonNull).collect(Collectors.toList());

        return users;
    }

    public PageVo<FollowVo> getBloggerPageList(UserPageReq req) {
        return getBloggerPageList(req, true);
    }

    public PageVo<FollowVo> getBloggerPageList(UserPageReq req, boolean checkFollow) {

        Page<Integer> bloggers = blogInfoDao.getBloggerPageList(req);
        if (CollectionUtils.isEmpty(bloggers)) {
            return new PageVo<>();
        }

        PageVo<FollowVo> pageVo = new PageVo<>();
        pageVo.setTotal(bloggers.getTotal());
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());

        List<User> userList = userService.getBaseUserByIds(bloggers);
        if (CollectionUtils.isEmpty(userList)) {
            return pageVo;
        }

        userList = SortUtil.sortByIdIndex(bloggers, userList);
        if (checkFollow) {
            List<Integer> followIds = followService.getFollowIds(bloggers, FollowTypeEnum.USER.getType());
            if (!CollectionUtils.isEmpty(followIds)) {
                userList.stream().forEach(user -> {
                    user.setFollow(followIds.contains(user.getId()));
                });

            }
        }

        List<FollowVo> userVoList = userList.stream().map(u -> FollowBizConvert.convert(u)).collect(Collectors.toList());
        pageVo.setList(userVoList);
        return pageVo;
    }

    public List<User> getRecommendUsers() {

        List<Integer> bloggers = blogInfoDao.getRecommendBloggers();
        if (CollectionUtils.isEmpty(bloggers)) {
            return new ArrayList<>();
        }

        List<User> users = bloggers.stream()
                .map(userId -> UserConvert.member2dto(userService.getFromCache(userId), false))
                .filter(k -> k != null)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(users)) {
            return users;
        }

        List<Integer> userIds = users.stream().map(u -> u.getId()).collect(Collectors.toList());
        List<Integer> followIds = followService.getFollowIds(userIds, FollowTypeEnum.USER.getType());
        if (CollectionUtils.isEmpty(followIds)) {
            return users;
        }
        users.stream().forEach(user -> {
            user.setFollow(followIds.contains(user.getId()));
        });
        return users;
    }

    public int getView(int blogId) {
        String key = RedisConstants.getBlogViewKey(blogId);
        return redisCacheService.getNum(key);
    }

    public void incrView(Integer blogId, Integer sourceFrom) {
        if (blogId == null) {
            return;
        }
        try {
            String key = RedisConstants.getBlogViewKey(blogId);
            Long incr = redisCacheService.incr(key);
            if (!BlogSourceFromEnum.BIG_SHOT.getType().equals(sourceFrom) && incr != null && incr > 0) {
                String sortListKey = getSortListKey();
                redisCacheService.zadd(sortListKey, incr.doubleValue(), String.valueOf(blogId));
                if (incr % 10 == 0) {
                    //todo update db
                }
            }

        } catch (Exception ex) {
            log.error("incrView,id={}", blogId);
            //todo,按人记表
        }
    }

    public List<Integer> getTopIdList() {
        String sortListKey = getSortListKey();
        Map<String, Object> map = redisCacheService.zgetPipe(Arrays.asList(sortListKey), 0, 200);

        if (map == null) {
            return null;
        }

        Object o = map.get(sortListKey);
        if (o == null) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        JSONArray objects = JSON.parseArray(JSON.toJSONString(o));

        if (objects == null) {
            return null;
        }
        for (int i = 0; i < objects.size(); i++) {

            Integer id = objects.getInteger(i);
            list.add(id);
        }


        return list;
    }

    private boolean canViewOnVipViewLimit(Integer userId, String ip, Integer sourceFrom, Integer sourceId) {
        String key = vipViewKey(userId, ip, sourceFrom, sourceId);
        if (key == null) {
            return false;
        }
        Long num = redisCacheService.incr(key);
        int todayRemainSecond = TimeUtil.getTodayRemainSecond();

        redisCacheService.expire(key, todayRemainSecond);
        if (num == null) {
            return false;
        }
        return num.intValue() <= vipViewLimit;
    }

    private String vipViewKey(Integer userId, String ip, Integer sourceFrom, Integer sourceId) {
        if (userId == null && StringUtils.isEmpty(ip)) {
            return null;
        }
        String key = "";
        if (userId != null && userId > 0) {
            key = String.valueOf(userId);
        } else {
            key = Md5Util.getMd5(ip);
        }
        return "pai:blog:v:" + key + ":" + sourceFrom + ":" + sourceId;
    }

    private String getSortListKey() {
        return "pai:blog:sort:list";
    }


    public SiteRelBlogVo getSiteRelBlog(int blogId) {

        final long start = System.currentTimeMillis();
        BlogInfoPo load = blogInfoDao.load(blogId);
        if (load == null) {
            return null;
        }
        Integer sourceFrom = load.getSourceFrom();
        if (!BlogSourceFromEnum.SITE.getType().equals(sourceFrom)) {
            return null;
        }
        Integer siteId = load.getSiteId();
        if (siteId == null) {
            return null;
        }
        BlogInfoPo nextPo = blogSearchDao.loadSiteRel(blogId, siteId, true);

        BlogInfoPo prePo = blogSearchDao.loadSiteRel(blogId, siteId, false);

        BlogVo preVo = BlogConvert.convert(prePo);

        BlogVo nextVo = BlogConvert.convert(nextPo);

        SiteRelBlogVo siteRelBlogVo = new SiteRelBlogVo();

        siteRelBlogVo.setPre(preVo);
        siteRelBlogVo.setNext(nextVo);
        return siteRelBlogVo;
    }


    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {
        if (id==null){
            return null;
        }
        if (!id.matches("\\d+")) {
            return null;
        }
        final int i = Integer.parseInt(id);
        ViewConfig config = new ViewConfig();
        config.brief = true;
        return getDetail(i,config);
    }

    static class ViewConfig{

        boolean brief;
    }
}
