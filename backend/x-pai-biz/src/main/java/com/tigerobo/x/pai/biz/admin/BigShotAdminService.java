package com.tigerobo.x.pai.biz.admin;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.admin.req.AdminOnlineStatusReq;
import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.dto.admin.BigShotDto;
import com.tigerobo.x.pai.api.dto.admin.PubSiteDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.biz.biz.pub.PubBigShotService;
import com.tigerobo.x.pai.biz.converter.PubBigShotConvert;
import com.tigerobo.x.pai.biz.converter.SiteConvert;
import com.tigerobo.x.pai.dal.biz.dao.pub.PubBigShotDao;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BigShotAdminService {
    @Autowired
    private PubBigShotService pubBigShotService;
    @Autowired
    private PubBigShotDao pubBigShotDao;

    public PageVo<BigShotDto> bigShotQuery(BigShotAdminReq req){
        Preconditions.checkArgument(req!=null,"请求参数为空");
        if (!StringUtils.isEmpty(req.getKeyword())) {
            if (req.getKeyword().length() > 20) {
                log.warn("length long,keyword:{}", req.getKeyword());
                req.setKeyword(req.getKeyword().substring(0, 20));
            }
        }

         Page<Integer> bigShotIds = pubBigShotDao.getBigShotIds(req);
        List<PubBigShotPo> pos = pubBigShotDao.findOnlyByIds(bigShotIds);
        PageVo<BigShotDto> bigShotDtos=new PageVo<>();
        if(!CollectionUtils.isEmpty(pos)){
             List<BigShotDto> collect = pos.stream().map(po->{
                 BigShotDto dto = PubBigShotConvert.po2dto(po);
                 if(po.getIsDeleted()){
                     dto.setIsDeleted(1);
                 }else {
                     dto.setIsDeleted(0);
                 }
                 return dto;
             }).collect(Collectors.toList());
             bigShotDtos.setList(collect);
        }
        bigShotDtos.setPageNum(req.getPageNum());
        bigShotDtos.setPageSize(req.getPageSize());
        bigShotDtos.setTotal((int)bigShotIds.getTotal());
        return bigShotDtos;

    }


    public void recover(IdReqVo req) {
        Integer bigShotId = req.getId();
        Preconditions.checkArgument(bigShotId!=null&&bigShotId>0,"请求id异常");
        PubBigShotPo load = pubBigShotDao.findOnlyById(bigShotId);
        if(load==null){
            return ;
        }
        if(!load.getIsDeleted()){
            return ;
        }
        PubBigShotPo po=new PubBigShotPo();
        po.setId(bigShotId);
        po.setIsDeleted(false);
        pubBigShotDao.update(po);
    }

    public void bigShotDelete(Integer bigShotId) {

        Preconditions.checkArgument(bigShotId!=null,"请求id为空");
        PubBigShotPo load = pubBigShotDao.load(bigShotId);
        if(load==null){
            return ;
        }
        if(load.getIsDeleted()){
            return;
        }
        PubBigShotPo po=new PubBigShotPo();
        po.setId(bigShotId);
        po.setIsDeleted(true);
        pubBigShotDao.update(po);

    }
}
