package com.tigerobo.x.pai.biz.biz.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tigerobo.x.pai.api.biz.entity.Tag;
import com.tigerobo.x.pai.biz.converter.TagConvert;
import com.tigerobo.x.pai.dal.biz.dao.TagDao;
import com.tigerobo.x.pai.dal.biz.entity.TagPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TagService {

    @Autowired
    private TagDao tagDao;

    Cache<String, List<Tag>> tagCache = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.MINUTES)
            .maximumSize(600)
            .build();

    public Tag get(@NotNull String uid) {
//        TagPo po = tagDao.getByUid(uid);
//        return TagConvert.convert(po);
        if (StringUtils.isEmpty(uid)){
            return null;
        }
        return getAllFromCache().stream().filter(t->uid.equalsIgnoreCase(t.getUid())).findFirst().get();
    }

    public List<Tag> get(List<String> tagUids){
        if (CollectionUtils.isEmpty(tagUids)){
            return new ArrayList<>();
        }

//        List<TagPo> tagPoList = tagDao.getByUids(tagUids);
//        List<Tag> tags = TagConvert.convert2dtoList(tagPoList);

        return getAllFromCache().stream().filter(t->tagUids.contains(t.getUid())).collect(Collectors.toList());
//        return tags;
    }

    public List<Tag> get(@NotNull Tag.Type type) {

        if (type == null){
            return new ArrayList<>();
        }
//        List<TagPo> pos = tagDao.getByType(type.getVal());
//        return TagConvert.convert2dtoList(pos);
        List<Tag> allFromCache = getAllFromCache();
        return allFromCache.stream().filter(t->type==t.getType()).collect(Collectors.toList());
    }

    private List<Tag> getAllFromCache(){
        return tagCache.get("all", new Function<String, List<Tag>>() {
            @Override
            public List<Tag> apply(String s) {
                return getAll();
            }
        });
    }
    private List<Tag> getAll(){
        List<TagPo> all = tagDao.getAll();
        return TagConvert.convert2dtoList(all);
    }
}
