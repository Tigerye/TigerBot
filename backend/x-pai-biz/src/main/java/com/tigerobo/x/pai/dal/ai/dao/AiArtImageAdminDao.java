package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.admin.req.ai.ArtImageAdminQueryReq;
import com.tigerobo.x.pai.api.admin.req.ai.ArtTextListReq;
import com.tigerobo.x.pai.api.ai.req.ArtImagePublicPageReq;
import com.tigerobo.x.pai.api.enums.ProcessStatusEnum;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import com.tigerobo.x.pai.dal.ai.mapper.AiArtImageMapper;
import com.tigerobo.x.pai.dal.ai.mapper.ArtImageQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AiArtImageAdminDao {

    @Autowired
    private AiArtImageMapper aiArtImageMapper;



    public Page<AiArtImagePo> getPage(ArtImageAdminQueryReq req) {


        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiArtImagePo.class);

        Example.Criteria criteria = example.createCriteria();

        if (req.getId()!=null&&req.getId()>0){
            criteria.andEqualTo("id",req.getId());
        }

        if (req.getReqId()!=null&&req.getReqId()>0){
            criteria.andEqualTo("reqId",req.getReqId());
        }

        if (req.getUserId()!=null&&req.getUserId()>0){
            criteria.andEqualTo("userId",req.getUserId());
        }
        if (req.getStatus()!=null){
            criteria.andEqualTo("status",req.getStatus());
        }
        if (req.getProcessStatus()!=null){
            criteria.andEqualTo("processStatus",req.getProcessStatus());
        }

        if (req.getIsDeleted()!=null){
            criteria.andEqualTo("isDeleted",req.getIsDeleted());
        }
        if (req.getStartCreateTime()!=null){
            criteria.andGreaterThanOrEqualTo("createTime",req.getStartCreateTime());
        }
        if (req.getEndCreateTime()!=null){
            criteria.andLessThanOrEqualTo("createTime",req.getEndCreateTime());
        }

        if (!StringUtils.isEmpty(req.getText())){
            criteria.andLike("text","%"+req.getText()+"%");
        }

        if (!StringUtils.isEmpty(req.getTitle())){
            criteria.andLike("title","%"+req.getTitle()+"%");
        }
        example.setOrderByClause("id desc");
        return (Page<AiArtImagePo>)aiArtImageMapper.selectByExample(example);
    }

    public void add(AiArtImagePo po) {
        aiArtImageMapper.insertSelective(po);
    }


    public void update(AiArtImagePo po) {
        aiArtImageMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {

        AiArtImagePo up = new AiArtImagePo();
        up.setIsDeleted(true);
        up.setId(id);
        update(up);
    }


    public List<AiArtImagePo> getTextList(ArtTextListReq req,int pageNo,int pageSize){

        Example example = new Example(AiArtImagePo.class);

        example.selectProperties("text");
        final Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("createTime",req.getStartCreateTime());

        criteria.andLessThanOrEqualTo("createTime",req.getEndCreateTime());

        criteria.andEqualTo("processStatus", ProcessStatusEnum.SUCCESS.getStatus());

        criteria.andEqualTo("isDeleted",false);
        PageHelper.startPage(pageNo,pageSize);

        return aiArtImageMapper.selectByExample(example);

    }

}
