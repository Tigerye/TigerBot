package com.tigerobo.x.pai.biz.biz.blog;

import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogAddReq;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.content.HtmlContentService;
import com.tigerobo.x.pai.biz.converter.BlogConvert;
import com.tigerobo.x.pai.biz.lake.LakeAbstractService;
import com.tigerobo.x.pai.biz.utils.HtmlContentUtil;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogInfoDao;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogSearchDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class BlogOperateService {


    @Value("${pai.blog.oss.retry.time:20}")
    int retryTime;
    @Autowired
    private BlogInfoDao blogInfoDao;
    @Autowired
    private BlogSearchDao blogSearchDao;
    @Autowired
    private OssService ossService;
    @Autowired
    private UserService userService;

    @Autowired
    private HtmlContentService htmlContentService;

    @Autowired
    private LakeAbstractService lakeAbstractService;
    String prefix = "biz/blog/user/";

    public void online(Integer id) {

        if (id == null) {
            return;
        }
        BlogInfoPo update = new BlogInfoPo();

        update.setId(id);
        update.setOnlineStatus(1);
        update.setPublishTime(new Date());
        blogSearchDao.update(update);
    }

    public void offline(Integer id) {

        if (id == null) {
            return;
        }
        BlogInfoPo update = new BlogInfoPo();

        update.setId(id);
        update.setOnlineStatus(0);
        blogSearchDao.update(update);
    }

    public void delete(Integer id) {

        if (id == null) {
            return;
        }
        BlogInfoPo update = new BlogInfoPo();

        update.setId(id);
        update.setIsDeleted(true);
        blogSearchDao.update(update);
    }

    public BlogVo addOrUpdateBlog(BlogAddReq req) {
        Integer userId = ThreadLocalHolder.getUserId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        String htmlContent = req.getContent();
        Preconditions.checkArgument(StringUtils.isNotBlank(htmlContent), "内容不能为空");
        Integer id = req.getId();
        BlogInfoPo dbBlog = blogInfoDao.load(id);

        BlogInfoPo reqPo = BlogConvert.convertAddBlog(req);

        htmlContent = htmlContentService.convertBase64ToUrl(htmlContent);
        reqPo.setContent(htmlContent);

        List<String> contentList = HtmlContentUtil.parseHtmlCleanContent(htmlContent);
        String ossPath = null;
        if (dbBlog == null || StringUtils.isEmpty(dbBlog.getOssUrl())) {
            ossPath = productHtmlPath();
        } else {
            ossPath = buildOssUrlVersion(dbBlog.getOssUrl(),false);
        }

        String url = ossService.uploadHtml(htmlContent.getBytes(), ossPath);
        Preconditions.checkArgument(StringUtils.isNotBlank(url), "保存失败");
        String headImg = HtmlContentUtil.getHeadImgUrlFromContent(htmlContent);
        reqPo.setHeadImg(headImg);

        reqPo.setOssUrl(ossPath);
        String summary = reqPo.getSummary();
        if (StringUtils.isEmpty(summary)) {
            if (!CollectionUtils.isEmpty(contentList)) {
                String algoletAbstract = lakeAbstractService.getAlgoletAbstract(contentList);
                reqPo.setSummary(algoletAbstract);
            }
        }
        boolean online = reqPo.getOnlineStatus() != null && reqPo.getOnlineStatus().equals(1);
        if (online) {
            if (dbBlog == null || (dbBlog.getPublishTime() == null || dbBlog.getOnlineStatus() != 1)) {
                reqPo.setPublishTime(new Date());
            }
        }
        reqPo.setUserId(userId);
        reqPo.setAuthorName(user.getName());

        if (dbBlog == null) {
            blogSearchDao.add(reqPo);
        } else {
            blogSearchDao.update(reqPo);
        }

        BlogVo vo = new BlogVo();
        vo.setId(reqPo.getId());
        return vo;
    }

    private String productHtmlPath() {
        String id = IdGenerator.getId();

        String idPreFix = id.substring(0, 2) + "/" + id.substring(2, 4) + "/" + id + ".html";

        return prefix + idPreFix;
    }

    private String buildOssUrlVersion(String oss,boolean needVersion) {
        if (StringUtils.isBlank(oss)) {
            return productHtmlPath();
        }
        if (!needVersion){
            return oss;
        }
        int index = oss.lastIndexOf(".");

        String substring = oss.substring(index);

        String pre = oss.substring(0, index);

        String[] s = pre.split("_");
        String head = s[0];
        int version = 1;
        if (s.length > 1) {
            String versionStr = s[1];
            if (!StringUtils.isBlank(versionStr) && versionStr.matches("\\d+")) {
                version = Integer.parseInt(versionStr) + 1;
            }
        }

        return head + "_" + version + substring;

    }

}
