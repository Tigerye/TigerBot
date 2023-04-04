package com.tigerobo.x.pai.biz.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.dal.ai.dao.AiImageSizeDao;
import com.tigerobo.x.pai.dal.ai.entity.AiImageSizePo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImageSizeService {

    @Autowired
    private AiImageSizeDao aiImageSizeDao;


    @Autowired
    private RedisCacheService redisCacheService;

    String key = "pai:img:size:a";
    String idKey = "pai:img:size:id:";

    public AiImageSizePo getById(Integer id){
        if (id == null){
            return null;
        }
        String key = idKey+id;

        final String s = redisCacheService.get(key);
        if (!StringUtils.isEmpty(s)){
            return JSON.parseObject(s,AiImageSizePo.class);
        }

        final AiImageSizePo load = aiImageSizeDao.load(id);

        if (load == null){
            return null;
        }
        redisCacheService.set(key,JSON.toJSONString(load),100);
        return load;
    }
    public ImageSizTypeVo getCache() {

        final String s = redisCacheService.get(key);
        if (!StringUtils.isEmpty(s)){
            return JSONArray.parseObject(s,ImageSizTypeVo.class);
        }
        final ImageSizTypeVo all = getAll();
        redisCacheService.set(key, JSON.toJSONString(all),100);
        return all;
    }



    public void removeCache(){
        redisCacheService.del(key);
    }


    public ImageSizTypeVo getAll() {
        final List<AiImageSizePo> all = aiImageSizeDao.getAll();

        final List<AiImageSizePo> discoPos = all.stream().filter(p -> p.getIsDisco()).collect(Collectors.toList());
        final List<AiImageSizePo> stablePos = all.stream().filter(p -> p.getIsStable()).collect(Collectors.toList());
        final List<ImageSizeVo> discoVos = getImageSizeVos(discoPos);
        final List<ImageSizeVo> stableVos = getImageSizeVos(stablePos);
        ImageSizTypeVo typeVo = new ImageSizTypeVo();

        typeVo.setDiscoRatioList(discoVos);
        typeVo.setStableRatioList(stableVos);

        return typeVo;
    }

    private List<ImageSizeVo> getImageSizeVos(List<AiImageSizePo> all) {
        List<ImageSizeVo> vos = new ArrayList<>();

        if (CollectionUtils.isEmpty(all)) {
            return vos;
        }
        Map<Integer, List<AiImageSizePo>> ratioMap = new LinkedHashMap<>();
        for (AiImageSizePo po : all) {
            final Integer ratioId = po.getRatioId();
            final List<AiImageSizePo> sizeIds = ratioMap.computeIfAbsent(ratioId, k -> new ArrayList<>());
            sizeIds.add(po);
        }


        for (Map.Entry<Integer, List<AiImageSizePo>> entry : ratioMap.entrySet()) {

            final List<AiImageSizePo> value = entry.getValue();
            if (CollectionUtils.isEmpty(value)) {
                continue;
            }

            final AiImageSizePo first = value.get(0);
            ImageSizeVo imageSizeVo = new ImageSizeVo();

            imageSizeVo.setRatio(first.getRatio());
            imageSizeVo.setRatioId(first.getRatioId());
            imageSizeVo.setSeq(first.getSeq());

            List<SizeVo> sizes = new ArrayList<>();
            for (AiImageSizePo po : value) {

                final Integer height = po.getHeight();
                final Integer width = po.getWidth();

                String size = width + " * " + height;
                SizeVo sizeVo = new SizeVo();
                sizeVo.setHeight(height);
                sizeVo.setWidth(width);
                sizeVo.setSize(size);
                sizeVo.setId(po.getId());
                sizeVo.setSeq(po.getSeq());
                sizeVo.setAlgCoin(po.getAlgCoin());
                sizes.add(sizeVo);
            }

            imageSizeVo.setSizes(sizes);

            vos.add(imageSizeVo);
        }

        if (vos.size()>1){
            vos.sort(Comparator.comparing(ImageSizeVo::getSeq));
        }
        return vos;
    }

    @Data
    public static class ImageSizTypeVo{
        List<ImageSizeVo> discoRatioList;
        List<ImageSizeVo> stableRatioList;
    }

    @Data
    public static class ImageSizeVo {

        Integer ratioId;
        String ratio;

        List<SizeVo> sizes;

        Integer seq;
    }

    @Data
    public static class SizeVo {
        Integer id;
        String size;
        Integer width;
        Integer height;

        Integer algCoin;
        Integer seq;
    }

}
