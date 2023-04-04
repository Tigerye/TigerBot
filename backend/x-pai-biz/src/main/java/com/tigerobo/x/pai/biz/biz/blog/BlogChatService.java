package com.tigerobo.x.pai.biz.biz.blog;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.enums.BusinessEnum;
import com.tigerobo.x.pai.api.enums.ThumbAction;
import com.tigerobo.x.pai.api.vo.IBusinessDetailVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogChatVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogMainChatVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.ChatPageReq;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.biz.IBusinessDetailFetchService;
import com.tigerobo.x.pai.biz.biz.log.ShareLogService;
import com.tigerobo.x.pai.biz.converter.BlogChatConvert;
import com.tigerobo.x.pai.biz.user.comment.BusinessCommentService;
import com.tigerobo.x.pai.biz.user.thumb.UserThumbService;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogBigshotReplyDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogChatDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogBigshotReplyPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogChatPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserThumbPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogChatService implements IBusinessDetailFetchService {

    @Autowired
    private BlogChatDao blogChatDao;

    @Autowired
    private BlogBigshotReplyDao blogBigshotReplyDao;

    @Autowired
    private PubBigShotDao pubBigShotDao;
    @Autowired
    private BusinessCommentService businessCommentService;
    @Autowired
    private UserThumbService userThumbService;
    @Autowired
    private ShareLogService shareLogService;
    @Autowired
    private FollowService followService;
    @Autowired
    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;


    public List<BlogChatVo> getBlogChainByBlogId(Integer blogId,String specId) {

        List<BlogBigshotReplyPo> replyPos = blogBigshotReplyDao.getByBlogId(blogId);
        List<BlogChatPo> chatPos;
        if (CollectionUtils.isEmpty(replyPos)&&!StringUtils.isEmpty(specId)){
            chatPos = blogChatDao.getDetailList(specId,10);
        }else {
            List<String> specIds = replyPos.stream().map(r -> r.getSpecId()).collect(Collectors.toList());
            chatPos = blogChatDao.getBySpecIds(specIds);
        }
        if (CollectionUtils.isEmpty(chatPos)){
            return null;
        }
        List<BlogChatVo> blogChatVos = BlogChatConvert.po2vo(chatPos);
        if (!CollectionUtils.isEmpty(blogChatVos)){
            for (BlogChatVo blogChatVo : blogChatVos) {
                blogChatVo.setCurrentBlog(blogId.equals(blogChatVo.getBlogId()));
            }
        }
        return blogChatVos;
    }


    public BlogMainChatVo getChatDetail(String chatId){
        List<BlogChatPo> list = blogChatDao.getDetailList(chatId);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }

        List<BlogChatVo> blogChatVos = BlogChatConvert.po2vo(list);
        initBigShotLogo(blogChatVos);

        BlogChatVo blogChatVo = blogChatVos.get(0);

        BlogMainChatVo mainChatVo = convert(blogChatVo);

        initNum(mainChatVo);
        initUserAck(mainChatVo);

        mainChatVo.setReplyList(blogChatVos);
        mainChatVo.setHasReplyChain(true);
        return mainChatVo;
    }

    private void initUserAck(BlogMainChatVo mainChatVo){
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null||userId ==0){
            return;
        }

        Integer bigShotId = mainChatVo.getBigShotId();
        if (bigShotId!=null&&bigShotId>0){
            boolean follow = followService.isFollow(bigShotId, BlogSourceFromEnum.BIG_SHOT.getType());
            mainChatVo.setFollow(follow);
        }

        String uuid = mainChatVo.getChatId();


        List<UserThumbPo> thumbUpList = userThumbService.getUserList(userId, Arrays.asList(uuid), BusinessEnum.APP.getType(), ThumbAction.THUMB_UP);
        if (!CollectionUtils.isEmpty(thumbUpList)) {
            mainChatVo.setThumbUp(true);
        }

        int commentNum = businessCommentService.countUserCommentNum(userId,uuid, BusinessEnum.BLOG_CHAT.getType());
        mainChatVo.setUserHasComment(commentNum>0);
    }

    private void initNum(BlogMainChatVo blogMainChatVo) {
        String uuid = blogMainChatVo.getChatId();
        int commentNum = businessCommentService.countFromCache(uuid, BusinessEnum.APP.getType());
        blogMainChatVo.setCommentNum(commentNum);
        int thumbUpNum = userThumbService.count(uuid, BusinessEnum.APP.getType());
        blogMainChatVo.setThumbUpNum(thumbUpNum);

        int shareNum = shareLogService.getCount(uuid ,BusinessEnum.APP.getType());
        blogMainChatVo.setShareNum(shareNum);
    }

    private BlogMainChatVo convert(BlogChatVo chat){
        BlogMainChatVo mainChatVo = new BlogMainChatVo();

        mainChatVo.setChatId(chat.getChatId());

        mainChatVo.setBlogId(chat.getBlogId());

        mainChatVo.setContent(chat.getContent());
        mainChatVo.setTranslateContent(chat.getTranslateContent());

        mainChatVo.setSummary(chat.getSummary());
        mainChatVo.setTranslateSummary(chat.getTranslateSummary());
        mainChatVo.setPublishTime(chat.getPublishTime());

        mainChatVo.setHeadImg(chat.getHeadImg());

        mainChatVo.setSourceName(chat.getAuthor());
        mainChatVo.setBigShotId(chat.getBigShotId());
        mainChatVo.setSourceLogo(chat.getLogoOss());

        return mainChatVo;
    }

    public PageVo<BlogChatVo> getChatPage(ChatPageReq req){
        Page<BlogChatPo> page = blogChatDao.getPage(req);

        PageVo<BlogChatVo> pageVo = new PageVo<>();
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setTotal(page.getTotal());

        boolean hasMore = page.getTotal()>(req.getPageNum()*req.getPageSize());
        pageVo.setHasMore(hasMore);

        List<BlogChatVo> blogChatVos = BlogChatConvert.po2vo(page);
        initBigShotLogo(blogChatVos);

        pageVo.setList(blogChatVos);
        return pageVo;
    }

    private void initBigShotLogo(List<BlogChatVo> blogChatVos){

        if (CollectionUtils.isEmpty(blogChatVos)){
            return;
        }
        List<Integer> bigShotIds = blogChatVos.stream().map(b -> b.getBigShotId()).filter(id -> id != null && id > 0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(bigShotIds)){
            return;
        }

        List<PubBigShotPo> bigShotPos = pubBigShotDao.getByIds(bigShotIds);

        if (CollectionUtils.isEmpty(bigShotIds)){
            return;
        }
        Map<Integer,PubBigShotPo> bigShotIdMap = new HashMap<>();
        for (PubBigShotPo bigShotPo : bigShotPos) {
            if (StringUtils.isEmpty(bigShotPo.getLogo())){
                continue;
            }
            bigShotIdMap.put(bigShotPo.getId(), bigShotPo);
        }
        for (BlogChatVo blogChatVo : blogChatVos) {
            Integer bigshotId = blogChatVo.getBigShotId();
            if (bigshotId == null||bigshotId<=0){
                continue;
            }

            PubBigShotPo pubBigShotPo = bigShotIdMap.get(bigshotId);
            if (pubBigShotPo == null){
                continue;
            }
            blogChatVo.setLogoOss(pubBigShotPo.getLogo());
        }
    }

    public void add(List<BlogChatPo> poList){

        if (CollectionUtils.isEmpty(poList)){
            return;
        }
        List<String> specIds = poList.stream().map(po -> po.getSpecId()).collect(Collectors.toList());

        List<BlogChatPo> existPos = blogChatDao.getBySpecIds(specIds);

        Map<String,BlogChatPo> existMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existPos)){
            for (BlogChatPo existPo : existPos) {
                existMap.put(existPo.getSpecId(),existPo);
            }
        }

        for (BlogChatPo chatPo : poList) {
            BlogChatPo existPo = existMap.get(chatPo.getSpecId());

            if(existPo==null){
                blogChatDao.add(chatPo);
            }else {
                BlogChatPo updatePo = new BlogChatPo();
                boolean update =false;
                Integer blogId = existPo.getBlogId();
                if ((blogId == null||blogId==0)&&(chatPo.getBlogId()!=null&&chatPo.getBlogId()>0)){
                    updatePo.setBlogId(chatPo.getBlogId());
                    update = true;
                }

                Integer bigShotId = existPo.getBigShotId();
                if ((bigShotId==null||bigShotId==0)&&chatPo.getBigShotId()!=null&&chatPo.getBigShotId()>0){
                    updatePo.setBigShotId(chatPo.getBigShotId());
                    update = true;
                }
                if (update){
                    updatePo.setId(existPo.getId());
                    blogChatDao.update(updatePo);
                }
            }

        }

        if (poList.size()>1){
            BlogChatPo chatPo = poList.get(0);

            Integer blogId = chatPo.getBlogId();

            if (blogId==null||blogId<=0){
                BlogChatPo existPo = existMap.get(chatPo.getSpecId());
                if (existPo!=null){
                    blogId = existPo.getBlogId();
                }
            }
            if (blogId!=null&&blogId>0){
                BlogInfoPo load = blogInfoDao.load(blogId);
                if (load!=null){
                    Boolean hasReplyChain = load.getHasReplyChain();
                    if (hasReplyChain==null||!hasReplyChain){
                        BlogInfoPo blogUpdate = new BlogInfoPo();
                        blogUpdate.setId(blogId);
                        blogUpdate.setSpecId(chatPo.getSpecId());
                        blogUpdate.setHasReplyChain(true);
                        blogSearchDao.update(blogUpdate);
                    }
                }

            }
        }


    }

    @Override
    public IBusinessDetailVo getBusinessDetail(String id) {

        return getChatDetail(id);
    }
}
