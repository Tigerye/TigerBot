package com.tigerobo.x.pai.dal.biz.dao.pub;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.admin.req.PubSiteAdminReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.dal.admin.mapper.PubAdminQueryMapper;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubSitePo;
import com.tigerobo.x.pai.dal.biz.mapper.PubSiteMapper;
import com.tigerobo.x.pai.dal.biz.mapper.pub.SiteQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PubSiteDao {


    @Autowired
    private PubSiteMapper pubSiteMapper;


    @Autowired
    private SiteQueryMapper siteQueryMapper;
    @Autowired
    private PubAdminQueryMapper pubAdminQueryMapper;

    public Page<Integer> getPage(BigShotQueryReq req){

        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        return (Page<Integer>) siteQueryMapper.getSitePage(req);
    }

    public PubSitePo findOnlyById(Integer id){
        if (id == null){
            return null;
        }

        PubSitePo pubSitePo = pubSiteMapper.selectByPrimaryKey(id);
        if (pubSitePo == null){
            return null;
        }
        return pubSitePo;
    }
    public List<PubSitePo> findOnlyByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(PubSitePo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);
        example.setOrderByClause("id desc");
        return pubSiteMapper.selectByExample(example);
    }

    public PubSitePo get(Integer id){
        if (id == null){
            return null;
        }

        PubSitePo pubSitePo = pubSiteMapper.selectByPrimaryKey(id);
        if (pubSitePo == null){
            return null;
        }

        if (pubSitePo.getIsDeleted()!=null&&pubSitePo.getIsDeleted()){
            return null;
        }
        return pubSitePo;
    }
    public List<PubSitePo> getByIds(List<Integer> ids){
        return getByIds(ids,null);
    }
    public List<PubSitePo> getByIds(List<Integer> ids,String sort){

        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        Example example = new Example(PubSitePo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andIn("id",ids);
        criteria.andEqualTo("isDeleted",0);


        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return pubSiteMapper.selectByExample(example);
    }

    public List<PubSitePo> getList(){
        List<PubSitePo> pubSitePos = pubSiteMapper.selectAll();

        if (pubSitePos==null){
            return null;
        }
        pubSitePos.sort(Comparator.comparing(PubSitePo::getScore));
        return pubSitePos.stream().filter(p->p.getIsDeleted()!=null&&!p.getIsDeleted()).collect(Collectors.toList());
    }

    public void update(PubSitePo sitePo){

        pubSiteMapper.updateByPrimaryKeySelective(sitePo);
    }

    public void add(PubSitePo sitePo){
        pubSiteMapper.insertSelective(sitePo);
    }

    public Page<Integer> getPubSiteIds(PubSiteAdminReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        return (Page<Integer>) pubAdminQueryMapper.getPubSitePage(req);
    }


}
