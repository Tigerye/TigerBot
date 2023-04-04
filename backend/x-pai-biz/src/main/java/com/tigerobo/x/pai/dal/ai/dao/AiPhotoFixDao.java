package com.tigerobo.x.pai.dal.ai.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.ai.enums.AiArtImageProcessEnum;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.dal.ai.entity.AiPhotoFixPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiPhotoFixMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

@Component
public class AiPhotoFixDao {

    @Autowired
    private AiPhotoFixMapper aiPhotoFixMapper;

    public int countPreIdQueue(Integer id){

        Example example = new Example(AiPhotoFixPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThanOrEqualTo("id",id);
        criteria.andEqualTo("processStatus",AiArtImageProcessEnum.PREPARE.getStatus());
        criteria.andEqualTo("isDeleted",0);

        return aiPhotoFixMapper.selectCountByExample(example);

    }

    public AiPhotoFixPo load(Integer id){

        AiPhotoFixPo po = aiPhotoFixMapper.selectByPrimaryKey(id);

        if (po==null||po.getIsDeleted()==null||po.getIsDeleted()){
            return null;
        }
        return po;
    }

    public void add(AiPhotoFixPo po){
        aiPhotoFixMapper.insertSelective(po);
    }

    public int update(AiPhotoFixPo po){
        return aiPhotoFixMapper.updateByPrimaryKeySelective(po);
    }

    public void delete(Integer id) {

        AiPhotoFixPo up = new AiPhotoFixPo();
        up.setIsDeleted(true);
        up.setId(id);
        update(up);
    }


    public Page<AiPhotoFixPo> getUserPage(Integer userId, PageReqVo req) {


        PageHelper.startPage(req.getPageNum(),req.getPageSize());

        Example example = new Example(AiPhotoFixPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id desc");

        return (Page<AiPhotoFixPo>)aiPhotoFixMapper.selectByExample(example);
    }


    public AiPhotoFixPo getByReqId(Long reqId) {
        Example example = new Example(AiPhotoFixPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("reqId",reqId);
        criteria.andEqualTo("isDeleted",0);


        List<AiPhotoFixPo> aiPhotoFixPos = aiPhotoFixMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(aiPhotoFixPos)){
            return null;
        }
        return aiPhotoFixPos.get(0);

    }



    public List<AiPhotoFixPo> getPrepareList(int size) {
        PageHelper.startPage(1,size);
        Example example = new Example(AiPhotoFixPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("processStatus", AiArtImageProcessEnum.PREPARE.getStatus());
        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id asc");
        return aiPhotoFixMapper.selectByExample(example);
    }


    public List<AiPhotoFixPo> getPrepareListTest(int size) {
        PageHelper.startPage(1,size);
        Example example = new Example(AiPhotoFixPo.class);
        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("processStatus", AiArtImageProcessEnum.PREPARE.getStatus());

        criteria.andEqualTo("id",54);

        criteria.andEqualTo("isDeleted",0);

        example.setOrderByClause("id asc");
        return aiPhotoFixMapper.selectByExample(example);
    }
}
