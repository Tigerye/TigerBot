package com.tigerobo.x.pai.biz.biz.blog;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.BlogTabEnum;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogCategoryQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.blog.base.BlogBaseService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.converter.BlogCategoryConvert;
import com.tigerobo.x.pai.biz.converter.BlogConvert;
import com.tigerobo.x.pai.biz.converter.PubBigShotConvert;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogSearchService {
    @Autowired
    private FollowService followService;
//    @Autowired
//    private BlogInfoDao blogInfoDao;

    @Autowired
    private BlogSearchDao blogSearchDao;
    @Autowired
    private BlogService blogService;
    @Autowired
    private PubSiteService pubSiteService;

    @Autowired
    private PubBigShotDao pubBigShotDao;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private BusinessCommentService businessCommentService;

    @Autowired
    private ShareLogService shareLogService;

    @Autowired
    private UserThumbService userThumbService;

    @Autowired
    private BlogChatService blogChatService;

    @Autowired
    private BlogCategoryRelService blogCategoryRelService;
    @Autowired
    private RoleService roleService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BlogBaseService blogBaseService;
    public int countNew() {
        Integer userId = ThreadLocalHolder.getUserId();

        Date date = null;
        Long lastTimeValue = getUserLastTime(userId);

        if (lastTimeValue != null) {
            final long now = System.currentTimeMillis();
            if (now-lastTimeValue<60_000){
                return 0;
            }
            date = new Date(lastTimeValue);
        }
        return blogSearchDao.countNewView(date);
    }

    private Long getUserLastTime(Integer userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        String key = getUserCountNewKey(userId);
        String value = redisCacheService.get(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (!value.matches("\\d+")) {
            return null;
        }
        return Long.parseLong(value);
    }

    public void setUserLastViewTime(Integer userId) {
        if (userId == null || userId <= 0) {
            return;
        }
        String key = getUserCountNewKey(userId);

        redisCacheService.set(key, String.valueOf(System.currentTimeMillis()), 3600 * 24 * 3);
    }

    private String getUserCountNewKey(Integer userId) {
        if (userId == null || userId == 0) {
            return null;
        }
        return "blog:user:count:new:" + userId;
    }

    public PageVo<BlogVo> getUserBlogs(BlogQueryVo queryVo) {

        Integer queryUserId = queryVo.getUserId();

        if (queryUserId == null || queryUserId <= 0) {
            return new PageVo<>();
        }

        Integer userId = ThreadLocalHolder.getUserId();

        boolean admin = roleService.isAdmin();
        boolean member = memberService.isMember(userId);

        PageVo<BlogVo> pageVo = new PageVo<>();
        pageVo.setPageNum(queryVo.getPageNum());
        pageVo.setPageSize(queryVo.getPageSize());

        Page<BlogInfoPo> poList = blogSearchDao.getMineBlogList(queryVo);
//        List<FollowVo> followList = followService.getFollowList(userId);
        return buildPage(queryVo, poList,admin,member);
    }

    public PageVo<BlogVo> getCategoryBlogs(BlogCategoryQueryVo queryVo) {

        if (queryVo.getCategoryId() == null) {
            return new PageVo<>();
        }
        Integer userId = ThreadLocalHolder.getUserId();

        boolean admin = roleService.isAdmin();
        boolean member = memberService.isMember(userId);
        BlogTabEnum tab = BlogTabEnum.getByName(queryVo.getTabType());
        Page<BlogInfoPo> poList = null;
        if (BlogTabEnum.HOT == (tab)) {

            BlogQueryVo blogQueryVo = new BlogQueryVo();
            BeanUtils.copyProperties(queryVo, blogQueryVo);
            poList = getHotList(blogQueryVo);
        } else {
            poList = blogSearchDao.searchBlogCategoryList(queryVo);
        }
//        List<FollowVo> followList = followService.getFollowList(userId);
        return buildPage(queryVo, poList,admin,member);
    }

    public PageVo<BlogVo> getPageList(BlogQueryVo queryVo) {


        Integer userId = ThreadLocalHolder.getUserId();
        List<FollowVo> followList = followService.getFollowList(userId,Arrays.asList(0,1,2));
        BlogTabEnum tab = BlogTabEnum.getByName(queryVo.getTabType());
        List<BlogInfoPo> poList = getBlogInfoPos(queryVo, followList, tab);

        boolean admin = roleService.isAdmin();
        boolean member = memberService.isMember(userId);
        PageVo<BlogVo> pageVo = buildPage(queryVo, (Page<BlogInfoPo>) poList,admin,member);

        if (queryVo.isViewTab()) {
            setUserLastViewTime(userId);
        }

        return pageVo;
    }

    private List<BlogInfoPo> getBlogInfoPos(BlogQueryVo queryVo, List<FollowVo> followList, BlogTabEnum tab) {
        List<BlogInfoPo> poList = null;


        final Integer startId = blogBaseService.getStartId();


        if (BlogTabEnum.HOT == tab) {
            queryVo.setSourceFromList(Arrays.asList(0, 1));
            return getHotList(queryVo);
        } else if (BlogTabEnum.FOLLOW == tab) {
//            queryVo.setStartId(startId);
            if (!CollectionUtils.isEmpty(followList)){
                return searchByFollow(queryVo, followList);
            }
        } else if (BlogTabEnum.SITE == tab) {
            queryVo.setSourceFromList(Arrays.asList(BlogSourceFromEnum.SITE.getType()));

        } else if (BlogTabEnum.BIGSHOTS == tab) {
            final List<Integer> bigShotIdList = queryVo.getBigShotIdList();
            if (CollectionUtils.isEmpty(bigShotIdList)){
                queryVo.setStartId(startId);
            }
            queryVo.setSourceFromList(Arrays.asList(BlogSourceFromEnum.BIG_SHOT.getType()));

        } else if (BlogTabEnum.ALGOLET == tab) {
            queryVo.setSourceFromList(Arrays.asList(BlogSourceFromEnum.ALGOLET.getType()));

        } else if (BlogTabEnum.NEW == tab) {
            if (StringUtils.isEmpty(queryVo.getKeyword())){
                queryVo.setSourceFromList(Arrays.asList(0,1));
            }

        }
        poList = blogSearchDao.searchBlogList(queryVo);
        return poList;
    }

    public List<BlogInfoPo> searchByFollow(BlogQueryVo queryVo, List<FollowVo> followList) {
        List<BlogInfoPo> poList;
        if (CollectionUtils.isEmpty(followList)) {
            poList = new Page<>();
        } else {
            queryVo.setSourceFromList(Arrays.asList(0, 1, 2));
            List<Integer> followUserIds = followList.stream().filter(fo -> FollowTypeEnum.USER.getType().equals(fo.getBizType())).map(fo -> fo.getId()).collect(Collectors.toList());

            List<Integer> siteIds = followList.stream().filter(fo -> FollowTypeEnum.SITE.getType().equals(fo.getBizType())).map(fo -> fo.getId()).collect(Collectors.toList());

            List<Integer> bigShotIds = followList.stream().filter(fo -> FollowTypeEnum.BIG_SHOT.getType().equals(fo.getBizType())).map(fo -> fo.getId()).collect(Collectors.toList());

            if (followUserIds.size() == 0) {
                followUserIds = null;
            }
            if (siteIds.size() == 0) {
                siteIds = null;
            }

            if (bigShotIds.size() == 0) {
                bigShotIds = null;
            }
            queryVo.setHasFollow(true);
            queryVo.setFollowUserIds(followUserIds);
            queryVo.setFollowBigShotIds(bigShotIds);
            queryVo.setFollowSiteIds(siteIds);
            queryVo.setVipList(null);

            poList = blogSearchDao.searchBlogList(queryVo);
        }
        return poList;
    }



    PageVo<BlogVo> buildPage(PageReqVo queryVo, Page<BlogInfoPo> poList,boolean admin,boolean isMember) {
        Integer userId = ThreadLocalHolder.getUserId();

        Integer pageNum = queryVo.getPageNum();
        boolean ignoreCheckVip = pageNum ==1;
        BlogSearchContent searchContent = new BlogSearchContent();
        searchContent.setQueryVo(queryVo);
        searchContent.setPoList(poList);
        searchContent.setUserId(userId);
        searchContent.setAdmin(admin);
        searchContent.setMember(isMember);
        searchContent.setIgnoreCheckVip(ignoreCheckVip);
        PageVo<BlogVo> pageVo = buildPage(searchContent);
        return pageVo;
    }

    private PageVo<BlogVo> buildPage(BlogSearchContent searchContent) {
        PageReqVo queryVo = searchContent.queryVo; Page<BlogInfoPo> poList = searchContent.getPoList();
        Integer userId = searchContent.getUserId();
        boolean admin = searchContent.isAdmin();boolean isMember = searchContent.isMember;
        boolean ignoreCheckVip = searchContent.ignoreCheckVip;

        long total = poList.getTotal();

        List<Integer> blogIds = poList.stream().map(b -> b.getId()).collect(Collectors.toList());

        Map<Integer, List<BlogCategoryRelDto>> blogCategoryMap = blogCategoryRelService.getBlogCategoryMap(blogIds);

        List<BlogVo> blogVos = poList.parallelStream().map(po -> {
            return getBlogVo(userId, admin, isMember, ignoreCheckVip, blogCategoryMap, po);
        }).collect(Collectors.toList());

        completeCount(blogVos);
        PageVo<BlogVo> pageInfo = new PageVo<>();

        pageInfo.setPageNum(queryVo.getPageNum());
        pageInfo.setPageSize(queryVo.getPageSize());
        pageInfo.setList(blogVos);
        pageInfo.setTotal(total);

        int totalPage = ((int) total) / queryVo.getPageSize() + (total % queryVo.getPageSize() > 0 ? 1 : 0);
        pageInfo.setHasMore(totalPage > queryVo.getPageNum());

        return pageInfo;
    }


    List<BlogVo> pos2vos(List<BlogInfoPo> pos){
        if(CollectionUtils.isEmpty(pos)){
            return null;
        }
        Integer userId = ThreadLocalHolder.getUserId();

        boolean admin = roleService.isAdmin();
        boolean member = memberService.isMember(userId);

        final List<BlogVo> blogVos = pos.stream().map(po -> po2vo(userId, admin, member, false, po)).collect(Collectors.toList());
        completeCount(blogVos);
        return blogVos;
    }

    private BlogVo po2vo(Integer userId, boolean admin, boolean isMember, boolean ignoreCheckVip,BlogInfoPo po){
        return getBlogVo(userId,admin,isMember,ignoreCheckVip,null,po);
    }

    private BlogVo getBlogVo(Integer userId, boolean admin, boolean isMember, boolean ignoreCheckVip, Map<Integer, List<BlogCategoryRelDto>> blogCategoryMap, BlogInfoPo po) {
        BlogVo blogVo = BlogConvert.convert(po);
        Integer id = blogVo.getId();
        PubSiteVo site = pubSiteService.getFromCache(blogVo.getSiteId(), null);
        Role role = (userId != null && userId.equals(blogVo.getUserId())) ? Role.OWNER : Role.GUEST;
        blogVo.setRole(role.toString());
        blogVo.setSite(site);
        if (site != null) {
            blogVo.setSourceLogo(site.getLogoOss());
            blogVo.setSourceName(site.getName());
            blogVo.setFollow(site.isFollow());
        }
        Integer vip = po.getVip();

        boolean canView = canView(admin, isMember, vip, ignoreCheckVip);
        blogVo.setCanView(canView);
        List<BlogCategoryRelDto> blogCategoryRelDtos=null;
        if (blogCategoryMap != null) {
            blogCategoryRelDtos = blogCategoryMap.get(id);

        }
        if (!CollectionUtils.isEmpty(blogCategoryRelDtos)) {
            List<BlogCategoryDto> blogCategoryDtos = BlogCategoryConvert.convertList(blogCategoryRelDtos);
            blogVo.setCategoryList(blogCategoryDtos);
//                blogVo.setCategoryList(blogCategoryRelDtos.stream().map(c -> c.getCategoryName()).collect(Collectors.toList()));
        } else {
//                blogVo.setCategoryList(null);
        }

        BlogSourceFromEnum sourceFrom = BlogSourceFromEnum.getByType(blogVo.getSourceFrom());

        if (BlogSourceFromEnum.SITE == sourceFrom && site != null) {
            blogVo.setSourceLogo(site.getLogoOss());
            blogVo.setSourceName(site.getName());
        } else if (BlogSourceFromEnum.ALGOLET == sourceFrom) {

            User user = userService.getCacheUserByOther(blogVo.getUserId());
            blogVo.setSourceName("algolet");
            if (user != null) {
                blogVo.setSourceLogo(user.getAvatar());
                blogVo.setAuthorName(user.getName());
            }
            blogVo.setUser(user);

        } else if (BlogSourceFromEnum.BIG_SHOT == sourceFrom) {

            Integer bigShotId = blogVo.getBigShotId();

            PubBigShotPo pubBigShotPo = pubBigShotDao.load(bigShotId);
            if (pubBigShotPo != null) {
                blogVo.setBigShot(PubBigShotConvert.convert(pubBigShotPo));
                blogVo.setSourceLogo(pubBigShotPo.getLogo());
                blogVo.setAuthorName(pubBigShotPo.getName());
            }
            blogVo.setSourceName("twitter");

            Boolean hasReplyChain = blogVo.getHasReplyChain();
            if (hasReplyChain != null && hasReplyChain) {
                List<BlogChatVo> chatList = blogChatService.getBlogChainByBlogId(po.getId(), po.getSpecId());
                if (!CollectionUtils.isEmpty(chatList)) {
                    blogVo.setReplyList(chatList);
                } else {
                    blogVo.setHasReplyChain(false);
                }
            }

            blogVo.setTitle(blogVo.getSummary());
            blogVo.setTranslateTitle(blogVo.getTranslateSummary());
        }
        return blogVo;
    }


    private boolean canView(boolean admin, boolean isMember, Integer vip, boolean ignoreCheckVip){
        if (ignoreCheckVip){
            return true;
        }
        if (vip == null||vip==0){
            return true;
        }
        if(admin||isMember){
            return true;
        }
        return false;
    }
    private void completeCount(List<BlogVo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.parallelStream().forEach(blogVo -> {
            try {
//                int view = blogService.getView(blogVo.getId());
//                blogVo.setViewNum(view);
                Integer id = blogVo.getId();

//                blogVo.setThumbUpNum(userThumbService.countBlog(id));
//                blogVo.setThumbDownNum(userThumbService.countBlogThumbDown(id));

//                int commentNum = businessCommentService.countFromCache(String.valueOf(id), BusinessEnum.BLOG.getType());

//                blogVo.setCommentNum(commentNum);
//                int shareNum = shareLogService.getCount(String.valueOf(id), BusinessEnum.BLOG.getType());
//                blogVo.setShareNum(shareNum);
            } catch (Exception ex) {
                log.error("completeCount:{}", blogVo.getId(), ex);
            }
        });

        List<String> idStrList = list.stream().map(vo -> String.valueOf(vo.getId())).collect(Collectors.toList());
        Integer userId = ThreadLocalHolder.getUserId();


        List<UserThumbPo> upList = userThumbService.getUserList(userId, idStrList, BusinessEnum.BLOG.getType());

//        Set<String> downBizIdList = CollectionUtils.isEmpty(downList) ? new HashSet<>() : downList.stream().map(d -> d.getBizId()).collect(Collectors.toSet());
        Set<String> upBizIdList = CollectionUtils.isEmpty(upList) ? new HashSet<>() : upList.stream().map(d -> d.getBizId()).collect(Collectors.toSet());

        for (BlogVo blogVo : list) {
            String id = String.valueOf(blogVo.getId());
//            if (downBizIdList.contains(id)) {
//                blogVo.setThumbDown(true);
//            }
            if (upBizIdList.contains(id)) {
                blogVo.setThumbUp(true);
            }
        }
    }


    private Page<BlogInfoPo> getHotList(BlogQueryVo queryVo) {
        Page<BlogInfoPo> page = new Page<>();

        List<Integer> topList = blogService.getTopIdList();

        if (topList == null) {
            return page;
        }

        List<BlogInfoPo> blogList = blogSearchDao.getOnlineListByIds(topList);

        if (CollectionUtils.isEmpty(blogList)) {
            return page;
        }

        String keyword = queryVo.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            String lower = keyword.toLowerCase();
            blogList = blogList.stream().filter(b -> {
                String title = b.getTitle();
                if (!StringUtils.isEmpty(title) && title.toLowerCase().contains(lower)) {
                    return true;
                }
                if (!StringUtils.isEmpty(b.getTranslateTitle()) && b.getTranslateTitle().toLowerCase().contains(lower)) {
                    return true;
                }
                if (!StringUtils.isEmpty(b.getSourceName()) && b.getSourceName().toLowerCase().contains(lower)) {
                    return true;
                }
                if (!StringUtils.isEmpty(b.getAuthorName()) && b.getAuthorName().toLowerCase().contains(lower)) {
                    return true;
                }

                return false;
            }).collect(Collectors.toList());
        }

        if (!CollectionUtils.isEmpty(queryVo.getSiteIdList())) {

            blogList = blogList.stream().filter(b -> {
                if (b.getSiteId() == null || b.getSiteId() == 0) {
                    return false;
                }
                return queryVo.getSiteIdList().contains(b.getSiteId());
            }).collect(Collectors.toList());
        }

        if (blogList.isEmpty()) {
            return page;
        }
        if (queryVo.getCategoryId() != null && queryVo.getCategoryId() > 0) {

            List<Integer> blogIds = blogList.stream().map(b -> b.getId()).collect(Collectors.toList());

            List<BlogCategoryRelDto> blogs = blogCategoryRelService.getBlogs(blogIds, queryVo.getCategoryId());
            if (CollectionUtils.isEmpty(blogs)) {
                return page;
            }

            List<Integer> filterBlogIds = blogs.stream().map(b -> b.getBlogId()).distinct().collect(Collectors.toList());
            blogList = blogList.stream().filter(b -> filterBlogIds.contains(b.getId())).collect(Collectors.toList());
        }

        if (blogList.isEmpty()) {
            return page;
        }
        Map<Integer, BlogInfoPo> sortMapList = blogList.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));

        List<BlogInfoPo> sortList = topList.stream().map(id -> sortMapList.get(id)).filter(po -> {
            return po != null && !po.getSourceFrom().equals(2);
        }).collect(Collectors.toList());

        int total = blogList.size();
        page.setTotal(total);
        if (total == 0) {
            return page;
        }
        int startIndex = (queryVo.getPageNum() - 1) * queryVo.getPageSize();

        int end = queryVo.getPageNum() * queryVo.getPageSize();
        if (end >= total) {
            end = total;
        }
        List<BlogInfoPo> list = sortList.subList(startIndex, end);

        page.addAll(list);
        return page;
    }


    @Data
    private static class BlogSearchContent{
        PageReqVo queryVo;
        Page<BlogInfoPo> poList;
        Integer userId;
        boolean admin;
        boolean isMember;
        boolean ignoreCheckVip;
    }
}
