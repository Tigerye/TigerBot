package com.tigerobo.x.pai.dal.biz.dao.pub;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.admin.req.BigShotAdminReq;
import com.tigerobo.x.pai.api.enums.ProcessStatusEnum;
import com.tigerobo.x.pai.api.vo.biz.pub.BigShotQueryReq;
import com.tigerobo.x.pai.api.vo.biz.pub.PubBigShotVo;
import com.tigerobo.x.pai.api.vo.biz.pub.PubSiteVo;
import com.tigerobo.x.pai.dal.admin.mapper.PubAdminQueryMapper;
import com.tigerobo.x.pai.dal.biz.entity.pub.PubBigShotPo;
import com.tigerobo.x.pai.dal.biz.mapper.pub.SiteQueryMapper;
import com.tigerobo.x.pai.dal.biz.mapper.pub.PubBigShotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class PubBigShotDao {

    @Autowired
    private PubBigShotMapper pubBigShotMapper;

    @Autowired
    private SiteQueryMapper siteQueryMapper;
    @Autowired(required = false)
    private PubAdminQueryMapper pubAdminQueryMapper;
    public PubBigShotPo findOnlyById(Integer id){
        if (id == null || id == 0) {
            return null;
        }

        return pubBigShotMapper.selectByPrimaryKey(id);
    }
    public PubBigShotPo load(Integer id) {
        if (id == null || id == 0) {
            return null;
        }

        PubBigShotPo pubBigShotPo = pubBigShotMapper.selectByPrimaryKey(id);
        if (pubBigShotPo == null || pubBigShotPo.getIsDeleted() == null || pubBigShotPo.getIsDeleted()) {
            return null;
        }
        return pubBigShotPo;
    }

    public void update(PubBigShotPo po) {
        if (po == null || po.getId() == null) {
            return;
        }
        int i=pubBigShotMapper.updateByPrimaryKeySelective(po);
        if (i!=1){
            log.error("id="+po.getId()+",update i="+i);
        }
    }


    public Page<Integer> getPageList(BigShotQueryReq req) {

        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        return (Page<Integer>) siteQueryMapper.getBigShotPage(req);
//        PageInfo<Integer> ids = (PageInfo<Integer>)bigShotQueryMapper.getBigShotPage(req);
    }


    public List<PubBigShotPo> getTopList() {

        Example example = new Example(PubBigShotPo.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("type", 0);
        criteria.andEqualTo("isDeleted", 0);

        example.setOrderByClause("score desc");
        PageHelper.startPage(1, 20);

        return pubBigShotMapper.selectByExample(example);
    }

    //
    public List<PubBigShotPo> getByIds(List<Integer> ids) {
        return getByIds(ids,null);
    }

    public List<PubBigShotPo> getByIds(List<Integer> ids,String sort) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        Example example = new Example(PubBigShotPo.class);


        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        criteria.andEqualTo("isDeleted", 0);

        if (!StringUtils.isEmpty(sort)){
            example.setOrderByClause(sort);
        }
        return pubBigShotMapper.selectByExample(example);
    }

    public List<PubBigShotPo> findOnlyByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        Example example = new Example(PubBigShotPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        example.setOrderByClause("id desc");
        return pubBigShotMapper.selectByExample(example);
    }
    public Integer getBigShotIdBySrcId(Integer srcId) {
        if (srcId == null || srcId == 0) {
            return null;
        }

        PubBigShotPo pubBigShotPo = loadBySrcId(srcId);
        if (pubBigShotPo == null) return null;
        return pubBigShotPo.getId();
    }


    public PubBigShotPo loadBySrcId(Integer srcId) {
        Example example = new Example(PubBigShotPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("srcId", srcId);
        criteria.andEqualTo("isDeleted", 0);

        List<PubBigShotPo> poList = pubBigShotMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(poList)) {
            return null;
        }
        PubBigShotPo pubBigShotPo = poList.get(0);
        return pubBigShotPo;
    }

    public List<PubBigShotPo> getWaitDealList() {
        Example example = new Example(PubBigShotPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("subscribeStatus", ProcessStatusEnum.WAIT_PROCESS.getStatus());
        criteria.andEqualTo("isDeleted", 0);

        return pubBigShotMapper.selectByExample(example);
    }


    public List<PubBigShotPo> getTestWaitDealList() {
        Example example = new Example(PubBigShotPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("subscribeStatus", ProcessStatusEnum.WAIT_PROCESS.getStatus());
        criteria.andEqualTo("isDeleted", 0);

        return pubBigShotMapper.selectByExample(example);
    }


    public PubBigShotPo getById(Integer bigShotId) {
        Example example = new Example(PubBigShotPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", bigShotId);
        criteria.andEqualTo("isDeleted", 0);
         List<PubBigShotPo> pubBigShotPos = pubBigShotMapper.selectByExample(example);
         if (CollectionUtils.isEmpty(pubBigShotPos)){
             return null;
         }
        return pubBigShotPos.get(0);

    }
    public  Page<Integer> getBigShotIds(BigShotAdminReq req){
        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        return (Page<Integer>) pubAdminQueryMapper.getBigShotPage(req);
    }
}
