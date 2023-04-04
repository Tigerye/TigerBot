package com.tigerobo.x.pai.biz.biz.pub;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.pai.common.util.DownloadUtil;
import com.tigerobo.x.pai.api.enums.ProcessStatusEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.converter.PubBigShotConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubSearchLogDao;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSearchLogPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PubBigShotService {

    @Autowired
    private PubBigShotDao pubBigShotDao;

    @Autowired
    private PubSearchLogDao pubSearchLogDao;

    @Autowired
    private OssService ossService;

    @Autowired
    private FollowService followService;

    public PubBigShotPo subscribe(Integer userId, Integer bigShotId) {

        if (userId == null) {
            throw new AuthorizeException("用户未登录");
        }
        Preconditions.checkArgument(bigShotId!=null,"bigShotId不存在");

        PubBigShotPo po = pubBigShotDao.load(bigShotId);
        if (po==null){
            log.error("subscribe:id:{},不存在",bigShotId);
            throw new IllegalArgumentException("bigshot不存在");
        }
        Integer srcId = po.getSrcId();
        if ((srcId==null||srcId == 0)&& ProcessStatusEnum.ON_QUEUE.getStatus().equals(po.getSubscribeStatus())){
            PubBigShotPo update = new PubBigShotPo();
            update.setId(po.getId());
            update.setSubscribeStatus(ProcessStatusEnum.WAIT_PROCESS.getStatus());
            update.setUserId(userId);
            pubBigShotDao.update(update);
        }
        return po;

    }

    public List<PubBigShotVo> getByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<PubBigShotPo> pos = pubBigShotDao.getByIds(ids);

        return PubBigShotConvert.convert(pos);
    }

    public FollowVo getDetail(Integer id) {

        PubBigShotPo pubBigShotPo = pubBigShotDao.load(id);
        FollowVo convert = PubBigShotConvert.convert2follow(pubBigShotPo);
        if (convert == null) {
            return null;
        }

        boolean follow = followService.isFollow(id, FollowTypeEnum.BIG_SHOT.getType());
        convert.setFollow(follow);
        return convert;
    }

    public List<FollowVo> getTopBigShotList() {

        List<PubBigShotPo> poList = pubBigShotDao.getTopList();

        if (CollectionUtils.isEmpty(poList)) {
            return new ArrayList<>();
        }
        return poList.stream().map(PubBigShotConvert::convert2follow).collect(Collectors.toList());
    }
    public PageVo<FollowVo> getBigShotPage(BigShotQueryReq req) {
        return getBigShotPage(req,true);
    }
    public PageVo<FollowVo> getBigShotPage(BigShotQueryReq req,boolean checkFollow) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (!StringUtils.isEmpty(req.getKeyword())) {
            if (req.getKeyword().length() > 20) {
                log.warn("length long,keyword:{}", req.getKeyword());
                req.setKeyword(req.getKeyword().substring(0, 20));
            }
        }

        Page<Integer> pageIdList = pubBigShotDao.getPageList(req);

        List<PubBigShotPo> pos = pubBigShotDao.getByIds(pageIdList, "score desc");

        PageVo<FollowVo> pageVo = new PageVo<>();

        addSearchLog(req, (int)pageIdList.getTotal());
        if (!CollectionUtils.isEmpty(pos)) {
            List<FollowVo> collect = pos.stream().map(PubBigShotConvert::convert2follow).collect(Collectors.toList());

            if (checkFollow){
                initFollow(userId, pos, collect);
            }
            pageVo.setList(collect);
        }
        pageVo.setTotal(pageIdList.getTotal());
        pageVo.setPageNum(req.getPageNum());
        pageVo.setPageSize(req.getPageSize());

        int i = req.getPageNum() * req.getPageSize();
        boolean hasMore = pageIdList.getTotal()> i;
        pageVo.setHasMore(hasMore);

        return pageVo;

    }

    private void initFollow(Integer userId, List<PubBigShotPo> pageList, List<FollowVo> collect) {
        List<Integer> ids = pageList.stream().map(p -> p.getId()).collect(Collectors.toList());
        List<Integer> followIds = followService.getUserBizFollowsByIds(ids,
                FollowTypeEnum.BIG_SHOT.getType(), userId);

        if (!CollectionUtils.isEmpty(followIds)) {
            for (FollowVo followVo : collect) {
                followVo.setFollow(followIds.contains(followVo.getId()));
            }
        }
    }

    private void addSearchLog(BigShotQueryReq req, int resultNum) {
        boolean addLog = !StringUtils.isEmpty(req.getKeyword());
        if (addLog) {
            PubSearchLogPo po = new PubSearchLogPo();
            po.setIp(ThreadLocalHolder.getIp());
            po.setKeyword(req.getKeyword());
            po.setUserId(ThreadLocalHolder.getUserId());
            po.setResultNum(resultNum);

            pubSearchLogDao.add(po);
        }
    }


    public void initImg() throws Exception {
//        List<PubBigShotPo> list = pubBigShotDao.getAll();
        List<PubBigShotPo> list = new ArrayList<>();
        String root = "/mnt/blog/twitter/";
        for (PubBigShotPo pubSitePo : list) {


            String logo = pubSitePo.getLogo();
            if (StringUtils.isEmpty(logo)) {
                continue;
            }
            if (!logo.startsWith("https://pbs.twimg.com")) {
                continue;
            }
            String imgName = String.valueOf(pubSitePo.getId()) + ".jpg";
            String img = DownloadUtil.downLoadByUrl(logo, imgName, root);

            byte[] bytes = Files.readAllBytes(new File(img).toPath());
            String key = "biz/bigshot/img/" + pubSitePo.getId() + ".jpg";
            String s = ossService.uploadImg(bytes, key);

//            OSSObject upload = ossApi.upload(key, url.openStream(), null);
            String path = OssConstant.domainUrl + key;
            PubBigShotPo update = new PubBigShotPo();
            update.setId(pubSitePo.getId());
            update.setLogo(path);
            pubBigShotDao.update(update);

        }


    }
}
