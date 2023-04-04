package com.tigerobo.x.pai.biz.biz.pub;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.auth.entity.Role;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.api.enums.FollowTypeEnum;
import com.tigerobo.x.pai.api.vo.user.follow.FollowVo;
import com.tigerobo.x.pai.biz.auth.FollowService;
import com.tigerobo.x.pai.biz.converter.FollowBizConvert;
import com.tigerobo.x.pai.biz.converter.SiteConvert;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubSiteDao;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PubSiteService {

    Cache<Integer, PubSitePo> localCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(300)
            .build();
    @Autowired
    private PubSiteDao pubSiteDao;

    @Autowired
    private FollowService followService;

    public PubSiteVo getDetail(Integer id){
        if (id == null){
            return null;
        }
        Integer userId = ThreadLocalHolder.getUserId();
        List<Integer> bizFollowIdList = followService.getBizFollowIdList(userId,FollowTypeEnum.SITE.getType());
        return getFromCache(id,bizFollowIdList);
    }

    public List<PubSiteVo> getByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        List<PubSitePo> pos = pubSiteDao.getByIds(ids);
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        List<PubSiteVo> vos = pos.stream().map(po -> SiteConvert.convert(po,null)).collect(Collectors.toList());

        return vos;
    }

    public List<PubSiteDto> getDtoByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        List<PubSitePo> pos = pubSiteDao.findOnlyByIds(ids);
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }
        List<PubSiteDto> dtos = pos.stream().map(po ->{
             PubSiteDto dto = SiteConvert.po2dto(po);
             if(po.getIsDeleted()){
                 dto.setIsDeleted(1);
             }else {
                 dto.setIsDeleted(0);
             }
            return dto;
        } ).collect(Collectors.toList());

        return dtos;
    }

    public PubSiteVo getFromCache(Integer id, Collection<Integer> followIds) {
        if (id == null||id ==0) {
            return null;
        }
        PubSitePo pubSitePo = localCache.get(id, pubSiteDao::get);

        return SiteConvert.convert(pubSitePo,followIds);
    }


    public List<PubSiteVo> getList(){
        Integer userId = ThreadLocalHolder.getUserId();
        return getList(userId);
    }

    public List<PubSiteVo> getFollowList(Integer userId){
        if (userId == null){
            return new ArrayList<>();
        }
        List<Integer> bizFollowList = followService.getBizFollowIdList(userId, FollowTypeEnum.SITE.getType());
        if (CollectionUtils.isEmpty(bizFollowList)){
            return new ArrayList<>();
        }
        Integer loginUserId = ThreadLocalHolder.getUserId();
        boolean sameUser = userId.equals(loginUserId);

        List<PubSitePo> list = pubSiteDao.getList();
        List<PubSiteVo> siteVos = new ArrayList<>();
        for (PubSitePo pubSitePo : list) {
            PubSiteVo vo = SiteConvert.convert(pubSitePo, bizFollowList);

            if (vo == null||!vo.isFollow()){
                continue;
            }
            if (sameUser){
                vo.setRole(Role.OWNER.toString());
            }else {
                vo.setRole(Role.GUEST.toString());
            }
            siteVos.add(vo);
        }
        return siteVos;
    }

    public List<PubSiteVo> getList(Integer userId) {
        List<Integer> bizFollowList = followService.getBizFollowIdList(userId, FollowTypeEnum.SITE.getType());

        List<PubSitePo> list = pubSiteDao.getList();
        if (CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }

        List<PubSiteVo> siteVos = new ArrayList<>();

        for (PubSitePo pubSitePo : list) {
            PubSiteVo vo = SiteConvert.convert(pubSitePo, bizFollowList);
            siteVos.add(vo);
        }
        return siteVos;
    }
    public PageVo<FollowVo> getPage(BigShotQueryReq req) {
        return getPage(req,true);
    }
    public PageVo<FollowVo> getPage(BigShotQueryReq req,boolean checkFollow) {

        Page<Integer> idPage = pubSiteDao.getPage(req);
        if (CollectionUtils.isEmpty(idPage)){
            return new PageVo<>();
        }

        List<PubSitePo> pos = pubSiteDao.getByIds(idPage, "score desc");
        PageVo<FollowVo> pageVo = new PageVo<>();
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());
        pageVo.setTotal(idPage.getTotal());

        if (!CollectionUtils.isEmpty(pos)){
            List<FollowVo> siteVos = pos.stream().map(FollowBizConvert::convertSite).collect(Collectors.toList());

            if (checkFollow){
                List<Integer> ids = pos.stream().map(PubSitePo::getId).collect(Collectors.toList());
                List<Integer> followIds = followService.getFollowIds(ids, FollowTypeEnum.SITE.getType());
                if (!CollectionUtils.isEmpty(followIds)){
                    for (FollowVo siteVo : siteVos) {
                        siteVo.setFollow(followIds.contains(siteVo.getId()));
                    }
                }
            }

            pageVo.setList(siteVos);
        }

        return pageVo;
    }


}
