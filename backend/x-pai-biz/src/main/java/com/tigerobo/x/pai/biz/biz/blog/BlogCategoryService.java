package com.tigerobo.x.pai.biz.biz.blog;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryDto;
import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.biz.constant.RedisConstants;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCategoryDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogCategoryService {


    @Autowired
    private BlogCategoryDao blogCategoryDao;

    @Autowired
    private RedisCacheService redisCacheService;

    Cache<Integer, List<BlogCategoryPo>> localCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES)
//            .maximumSize(300)
            .build();


    public BlogCategoryDto getDetail(Integer id){
        if (id ==null){
            return null;
        }
        List<BlogCategoryPo> all = getAll();
        if (CollectionUtils.isEmpty(all)){
            return null;
        }
        BlogCategoryPo po = all.stream().filter(a -> id.equals(a.getId())).findFirst().orElse(null);
        return convert(po);
    }
    public List<String> getAllLabelNameList(){
        List<BlogCategoryPo> all = getAll();
        if (CollectionUtils.isEmpty(all)){
            return null;
        }
        return all.stream().map(p->p.getName()).distinct().collect(Collectors.toList());
    }

    public List<BlogCategoryPo> getAll(){
        return localCache.get(0, k -> blogCategoryDao.getAll());
    }

    public List<BlogCategoryDto> getAllShow(){
        List<BlogCategoryPo> all = blogCategoryDao.getAll();
        if (CollectionUtils.isEmpty(all)){
            return new ArrayList<>();
        }
        return all.stream().map(a->convert(a)).collect(Collectors.toList());

    }


    public void cleanCache(){
        String cacheKey = RedisConstants.BLOG_CATEGORY_LIST;
        redisCacheService.expire(cacheKey,1);
    }

    public BlogCategoryPo  getByName(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }
        List<BlogCategoryPo> all = getAll();

        for (BlogCategoryPo blogCategoryPo : all) {
            if (name.trim().equalsIgnoreCase(blogCategoryPo.getName())){
                return blogCategoryPo;
            }
        }
        return null;
    }

    public List<BlogCategoryPo>  getByNames(List<String> names){
        if (CollectionUtils.isEmpty(names)){
            return null;
        }
        names = names.stream().distinct().collect(Collectors.toList());
        List<BlogCategoryPo> all = getAll();

        List<BlogCategoryPo> targetList = new ArrayList<>();
        for (String name : names) {
            BlogCategoryPo target = null;
            for (BlogCategoryPo blogCategoryPo : all) {
                if (name.trim().equalsIgnoreCase(blogCategoryPo.getName())){
                    target = blogCategoryPo;
                }
            }
            if (target ==null){
                log.error("name-{},找不到对应分类",name);
            }else {
                targetList.add(target);
            }
        }
        return targetList;
    }

    public List<BlogCategoryPo>  getByIds(List<Integer> ids){
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        List<BlogCategoryPo> all = getAll();

        List<BlogCategoryPo> targetList = new ArrayList<>();
        for (Integer id : ids) {
            for (BlogCategoryPo blogCategoryPo : all) {
                if (id.equals(blogCategoryPo.getId())){
                    targetList.add(blogCategoryPo);
                }
            }
        }
        return targetList;
    }



    private BlogCategoryDto convert(BlogCategoryPo b) {
        if (b == null){
            return null;
        }
        BlogCategoryDto dto = new BlogCategoryDto();

//        dto.setBlogId(blogId);
        dto.setId(b.getId());
        dto.setName(b.getName());
        return dto;
    }
}
