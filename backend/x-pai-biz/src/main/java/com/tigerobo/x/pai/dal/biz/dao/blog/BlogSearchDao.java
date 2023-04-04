package com.tigerobo.x.pai.dal.biz.dao.blog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.tigerobo.x.pai.api.enums.BlogSourceFromEnum;
import com.tigerobo.x.pai.api.vo.biz.PageReqVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogCategoryQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.BlogQueryVo;
import com.tigerobo.x.pai.api.vo.biz.blog.req.BlogHomeTabReq;
import com.tigerobo.x.pai.api.vo.biz.blog.req.UserPageReq;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogInfoPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogSearchPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BloggerPo;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogInfoMapper;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogQueryMapper;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BlogSearchMapper;
import com.tigerobo.x.pai.dal.biz.mapper.blog.BloggerMapper;
import com.tigerobo.x.pai.dal.biz.mapper.pub.SiteQueryMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BlogSearchDao {

    @Autowired
    private BlogQueryMapper blogQueryMapper;


    @Autowired
    private BlogSearchMapper blogSearchMapper;

    @Autowired
    private BlogInfoDao blogInfoDao;


    public int countByType(Integer sourceFrom){

        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        if (sourceFrom!=null){
            criteria.andEqualTo("sourceFrom",sourceFrom);
        }

        return blogSearchMapper.selectCountByExample(example);
    }

    public int count(Date start,Date end,Integer sourceFrom){

        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andGreaterThanOrEqualTo("createTime",start);
        criteria.andLessThan("createTime",end);
        if (sourceFrom!=null){
            criteria.andEqualTo("sourceFrom",sourceFrom);
        }

        return blogSearchMapper.selectCountByExample(example);
    }

    //todo  2022/10/8
    public Integer getMaxThirdId(){
        return blogQueryMapper.getMaxThirdId();
    }


    public List<BlogInfoPo> getOnlineListByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }

        Example example = new Example(BlogInfoPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        criteria.andEqualTo("onlineStatus",1);
        criteria.andEqualTo("isDeleted",false);

        final List<BlogSearchPo> blogSearchPos = blogSearchMapper.selectByExample(example);

        return searchList2infoList(blogSearchPos);

    }

    private List<BlogInfoPo> searchList2infoList(List<BlogSearchPo> blogSearchPos) {
        if (CollectionUtils.isEmpty(blogSearchPos)){
            return null;
        }
        final List<Integer> targetIds = blogSearchPos.stream().map(b -> b.getId()).collect(Collectors.toList());
        return ids2pos(targetIds);
    }


    private List<BlogInfoPo> ids2pos(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        final List<BlogInfoPo> pos = blogInfoDao.getByIds(ids);
        if (CollectionUtils.isEmpty(pos)){
            return pos;
        }
        Map<Integer,BlogInfoPo> map = new HashMap<>();

        for (BlogInfoPo po : pos) {
            map.put(po.getId(),po);
        }


        List<BlogInfoPo> list = new ArrayList<>();

        for (Integer id : ids) {
            final BlogInfoPo blogInfoPo = map.get(id);
            if (blogInfoPo!=null){
                list.add(blogInfoPo);
            }
        }
        return list;
    }

    public int countNewView(Date preViewTime){

        final Integer maxId = blogQueryMapper.getMaxId();

        if (maxId == null){
            return 0;
        }

        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();

        if (maxId.compareTo(10000)>0){
            criteria.andGreaterThan("id",maxId-10000);
        }

        Date date = DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), -2);
        criteria.andGreaterThanOrEqualTo("publishTime",date);
        if (preViewTime!=null&&preViewTime.after(date)){
            criteria.andGreaterThanOrEqualTo("createTime",preViewTime);
        }

        criteria.andEqualTo("onlineStatus",1);
        criteria.andEqualTo("isDeleted",0);


        return blogSearchMapper.selectCountByExample(example);
    }


    public int getBlogInfoCount(){

        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("onlineStatus",1);
        criteria.andEqualTo("isDeleted",0);


        return blogSearchMapper.selectCountByExample(example);
    }
    public int getBloggerCount(){

        Example example = new Example(BloggerPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDeleted",0);
        return blogSearchMapper.selectCountByExample(example);
    }


    public   Page<BlogInfoPo> getBlackTechBlogs(BlogHomeTabReq req,int pageSize,Integer startId){

        Example example = new Example(BlogInfoPo.class);
        Example.Criteria criteria = example.createCriteria();

        if (startId!=null&&startId>0){
            criteria.andGreaterThan("id",startId);
        }

//        criteria.andEqualTo("sourceFrom",1);
//        criteria.andEqualTo("tagType",1);
        criteria.andIn("siteId",Arrays.asList(8,190,206));
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("publish_time desc");
        PageHelper.startPage(req.getPageNum(), pageSize);


        final Page<BlogSearchPo> searchPage = (Page<BlogSearchPo>) blogSearchMapper.selectByExample(example);


        if (searchPage == null){
            return null;
        }
        if((req.getPageNum()-1)*pageSize>=searchPage.getTotal()){
            return null;
        }

        return convertPage(searchPage);
    }

    private Page<BlogInfoPo> convertPage(Page<BlogSearchPo> searchPage) {
        final long total = searchPage.getTotal();
        Page<BlogInfoPo> page = new Page<>();
        page.setTotal(total);

        final List<Integer> ids = searchPage.stream().map(b -> b.getId()).collect(Collectors.toList());

        final List<BlogInfoPo> list = ids2pos(ids);

        page.addAll(list);

        return page;
    }


    public BlogInfoPo loadSiteRel(int id, int siteId, boolean pre){

        PageHelper.startPage(1,1);
        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sourceFrom", BlogSourceFromEnum.SITE.getType());
        criteria.andEqualTo("siteId",siteId);
        if (pre){
            criteria.andLessThan("id",id);
        }else {
            criteria.andGreaterThan("id",id);
        }

        criteria.andEqualTo("onlineStatus",1);
        criteria.andEqualTo("isDeleted",0);

        String orderBy = "";
        if (pre){
            orderBy = "id desc";
        }else {
            orderBy = "id asc";
        }
        example.setOrderByClause(orderBy);
        List<BlogSearchPo> list = blogSearchMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        final Integer targetId = list.get(0).getId();

        return blogInfoDao.load(targetId);
    }

    public Page<BlogInfoPo> searchBlogList(BlogQueryVo queryVo){

        PageHelper.startPage(queryVo.getPageNum(),queryVo.getPageSize());
        List<Integer> ids = blogQueryMapper.query(queryVo);

        Page page = (Page)ids;
        Page<BlogInfoPo> blogPage = new Page<>();
        blogPage.setTotal(page.getTotal());

        final List<BlogInfoPo> list = ids2pos(ids);
        if (!CollectionUtils.isEmpty(list)){
            blogPage.addAll(list);
        }

        return blogPage;
    }

    public Page<BlogInfoPo> searchBlogCategoryList(BlogCategoryQueryVo queryVo){
        PageHelper.startPage(queryVo.getPageNum(),queryVo.getPageSize());
        List<Integer> ids = blogQueryMapper.queryCategory(queryVo);

        Page page = (Page)ids;
        Page<BlogInfoPo> blogPage = new Page<>();
        blogPage.setTotal(page.getTotal());
        final List<BlogInfoPo> list = ids2pos(ids);
        if (!CollectionUtils.isEmpty(list)){
            blogPage.addAll(list);
        }

        blogPage.addAll(list);
        return blogPage;
    }

    public Page<BlogInfoPo> getMineBlogList(BlogQueryVo queryVo){
        PageHelper.startPage(queryVo.getPageNum(),queryVo.getPageSize());
        List<Integer> ids = blogQueryMapper.queryMine(queryVo);
        Page page = (Page)ids;
        Page<BlogInfoPo> blogPage = new Page<>();
        blogPage.setTotal(page.getTotal());
        final List<BlogInfoPo> list = ids2pos(ids);
        if (!CollectionUtils.isEmpty(list)){
            blogPage.addAll(list);
        }
        return blogPage;
    }


    public Page<BlogInfoPo> getRecommendBlogs(PageReqVo req, int pageSize, Integer startId){

        Example example = new Example(BlogInfoPo.class);
        Example.Criteria criteria = example.createCriteria();
        if (startId!=null&&startId>0){
            criteria.andGreaterThan("id",startId);
        }
        criteria.andEqualTo("recommend",1);
        criteria.andEqualTo("isDeleted",0);
        example.setOrderByClause("recommend_time desc");

        PageHelper.startPage(req.getPageNum(),pageSize);
        final Page<BlogSearchPo> blogInfoPos = (Page<BlogSearchPo>) blogSearchMapper.selectByExample(example);


        final long total = blogInfoPos.getTotal();
        if((req.getPageNum()-1)*pageSize>=total){
            return null;
        }

        return convertPage(blogInfoPos);

    }



    public void add(BlogInfoPo infoPo){
        if (infoPo == null){
            return;
        }

        blogInfoDao.add(infoPo);

        BlogSearchPo po = new BlogSearchPo();


        BeanUtils.copyProperties(infoPo,po);

        blogSearchMapper.insertSelective(po);
    }

    public void update(BlogInfoPo infoPo){
        if (infoPo ==null||infoPo.getId() == null){
            return;
        }
        blogInfoDao.update(infoPo);
        BlogSearchPo po = new BlogSearchPo();


        BeanUtils.copyProperties(infoPo,po);
        if (canUpdate(po)){
            blogSearchMapper.updateByPrimaryKeySelective(po);
        }
    }

    private boolean canUpdate(BlogSearchPo po){

        if (po == null||po.getId() == null){
            return false;
        }
        final JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(po));
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            final String key = entry.getKey();
            if ("id".equalsIgnoreCase(key)){
                continue;
            }
            final Object value = entry.getValue();
            if (value!=null){
                return true;
            }
        }
        return false;
    }

    public List<BlogInfoPo> getByThirds(List<Integer> thirdIds){


        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("thirdId",thirdIds);

        final List<BlogSearchPo> list = blogSearchMapper.selectByExample(example);

        return searchList2infoList(list);
    }

    public BlogInfoPo getByThirdId(Integer thirdId,Integer sourceFrom){


        Example example = new Example(BlogInfoPo.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("thirdId",thirdId);
        criteria.andEqualTo("sourceFrom",sourceFrom);
        criteria.andEqualTo("isDeleted",0);

        List<BlogSearchPo> list = blogSearchMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        final Integer id = list.get(0).getId();

        return blogInfoDao.load(id);
    }

    public Integer getMaxId(){
        return blogQueryMapper.getMaxId();
    }
}
