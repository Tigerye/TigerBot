package com.tigerobo.x.pai.biz.biz.blog;

import com.tigerobo.x.pai.api.dto.blog.BlogCategoryRelDto;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogCategoryRelDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogCategoryRelPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogCategoryRelService {

    @Autowired
    private BlogCategoryRelDao blogCategoryRelDao;

    @Autowired
    private BlogCategoryService blogCategoryService;

    public void addOrUpdateCategory(int blogId, List<String> categoryNames){
        blogCategoryRelDao.deleteBlog(blogId);
        if (CollectionUtils.isEmpty(categoryNames)){
            return;
        }
        List<BlogCategoryPo> categoryPos = blogCategoryService.getByNames(categoryNames);
        if (!CollectionUtils.isEmpty(categoryPos)){
            for (BlogCategoryPo categoryPo : categoryPos) {
                BlogCategoryRelPo relPo = new BlogCategoryRelPo();
                relPo.setBlogId(blogId);
                relPo.setCategoryId(categoryPo.getId());
                blogCategoryRelDao.add(relPo);
            }
        }
    }


    public Map<Integer,List<BlogCategoryRelDto>> getBlogCategoryMap(List<Integer> blogIds){
        if (CollectionUtils.isEmpty(blogIds)){
            return new HashMap<>();
        }
        List<BlogCategoryRelPo> blogRelList = blogCategoryRelDao.getBlogsRelList(blogIds);

        if (CollectionUtils.isEmpty(blogRelList)){
            return new HashMap<>();
        }

        List<Integer> categoryIds = blogRelList.stream().map(b -> b.getCategoryId()).distinct().collect(Collectors.toList());
        List<BlogCategoryPo> categoryPos = blogCategoryService.getByIds(categoryIds);
        Map<Integer,BlogCategoryPo> categoryIdMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(categoryPos)){
            for (BlogCategoryPo categoryPo : categoryPos) {
                categoryIdMap.put(categoryPo.getId(),categoryPo);
            }
        }

        Map<Integer,List<BlogCategoryRelDto>> map = new HashMap<>();

        for (BlogCategoryRelPo relPo : blogRelList) {
            List<BlogCategoryRelDto> blogCategoryRelPos = map.computeIfAbsent(relPo.getBlogId(), k -> new ArrayList<>());

            BlogCategoryPo po = categoryIdMap.get(relPo.getCategoryId());
            if (po == null){
                continue;
            }

            blogCategoryRelPos.add(convert(po));
        }
        return map;
    }

    public List<BlogCategoryRelDto> getBlogCategory(Integer blogId){
        List<BlogCategoryRelPo> blogRelList = blogCategoryRelDao.getBlogRelList(blogId);

        if (CollectionUtils.isEmpty(blogRelList)){
            return null;
        }
        List<Integer> categoryIds =
                blogRelList.stream().map(b->b.getCategoryId()).distinct().collect(Collectors.toList());

        List<BlogCategoryPo> blogCategoryPos = blogCategoryService.getByIds(categoryIds);

        if (CollectionUtils.isEmpty(blogCategoryPos)){
            return null;
        }

        return blogCategoryPos.stream().map(b->{
            return convert(b);
        }).collect(Collectors.toList());

    }


    public List<BlogCategoryRelDto> getBlogs(List<Integer> blogsIds,Integer categoryId){
        if (CollectionUtils.isEmpty(blogsIds)||categoryId == null){
            return null;
        }

        List<BlogCategoryRelPo> relPos = blogCategoryRelDao.getByBlogsWithCategoryId(blogsIds, categoryId);
        if (CollectionUtils.isEmpty(relPos)){
            return null;
        }

        return relPos.stream().map(r->convert(r)).collect(Collectors.toList());
    }

    private BlogCategoryRelDto convert(BlogCategoryPo b) {
        BlogCategoryRelDto dto = new BlogCategoryRelDto();

//        dto.setBlogId(blogId);
        dto.setCategoryId(b.getId());
        dto.setCategoryName(b.getName());
        return dto;
    }

    private BlogCategoryRelDto convert(BlogCategoryRelPo po){
        BlogCategoryRelDto dto = new BlogCategoryRelDto();

        dto.setBlogId(po.getBlogId());
        dto.setCategoryId(po.getCategoryId());
        return dto;
    }
}
