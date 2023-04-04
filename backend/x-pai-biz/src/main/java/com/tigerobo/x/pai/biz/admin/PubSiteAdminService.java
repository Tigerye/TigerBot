package com.tigerobo.x.pai.biz.admin;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.admin.req.AdminOnlineStatusReq;
import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.admin.req.IdListReq;
import com.tigerobo.x.pai.api.admin.req.PubSiteAdminReq;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.biz.biz.pub.PubSiteService;
import com.tigerobo.x.pai.biz.converter.SiteConvert;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubSiteDao;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class PubSiteAdminService {
    @Autowired
    private PubSiteService pubSiteService;
    @Autowired
    private PubSiteDao pubSiteDao;

    public PageVo<PubSiteDto> pubSiteQuery(PubSiteAdminReq req){
        Preconditions.checkArgument(req!=null,"请求参数为空");
        Page<Integer> ids = pubSiteDao.getPubSiteIds(req);
        PageVo<PubSiteDto> pubSiteDtos=new  PageVo<>();
        if(!CollectionUtils.isEmpty(ids)){
            List<PubSiteDto> vos = pubSiteService.getDtoByIds(ids);
            pubSiteDtos.setList(vos);
        }
        pubSiteDtos.setPageNum(req.getPageNum());
        pubSiteDtos.setPageSize(req.getPageSize());
        pubSiteDtos.setTotal((int)ids.getTotal());
        return pubSiteDtos;
    }

    public void recover(IdReqVo req) {
        Integer pubSiteId = req.getId();
        Preconditions.checkArgument(pubSiteId!=null&&pubSiteId>0,"请求id异常");
        PubSitePo load = pubSiteDao.findOnlyById(pubSiteId);
        if(load==null){
            return ;
        }
        if(!load.getIsDeleted()){
            return ;
        }
        PubSitePo po=new PubSitePo();
        po.setId(pubSiteId);
        po.setIsDeleted(false);
        pubSiteDao.update(po);
    }

    public void pubSiteDelete(Integer pubSiteId) {
        Preconditions.checkArgument(pubSiteId!=null,"请求id为空");
        PubSitePo load = pubSiteDao.get(pubSiteId);
        if(load==null){
            return ;
        }
        if(load.getIsDeleted()){
            return;
        }
        PubSitePo po=new PubSitePo();
        po.setId(pubSiteId);
        po.setIsDeleted(true);
        pubSiteDao.update(po);
    }
}
