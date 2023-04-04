package com.tigerobo.x.pai.biz.ai.art.image;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.api.dto.ai.ArtModifierDto;
import com.tigerobo.x.pai.api.dto.ai.ArtModifierModel;
import com.tigerobo.x.pai.biz.cache.RedisCacheService;
import com.tigerobo.x.pai.dal.ai.dao.AiParamDictDao;
import com.tigerobo.x.pai.dal.ai.entity.AiParamDictPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiParamDictService {


    @Autowired
    private AiParamDictDao aiParamDictDao;

    final List<String> sortSeq = Arrays.asList("艺术家","经典","色彩","艺术运动和风格","文化","常用描述","设计相关","媒介技术","其他");

    @Autowired
    private RedisCacheService redisCacheService;


    public List<ArtModifierModel> getClassTypeDicts(){
        List<ArtModifierModel> dicts = new ArrayList<>();
        for (String s : sortSeq) {
            ArtModifierModel model = ArtModifierModel.builder().classType(s).classTypeName(s).build();
            dicts.add(model);
        }
        return dicts;
    }

    public ArtModifierModel getByClassType(String classType) {

        if (!sortSeq.contains(classType)){
            return  null;
        }

        final List<ArtModifierModel> modifierWithImgList = getCache();
        if (modifierWithImgList==null){
            return null;
        }
        return modifierWithImgList.stream().filter(p->p.getClassType().equalsIgnoreCase(classType)).findFirst().orElse(null);
    }

    public List<ArtModifierModel> getCache() {

        String key = "pai:ai:art:params";

        final String s = redisCacheService.get(key);
        if (!StringUtils.isEmpty(s)){
            try {
                return JSON.parseArray(s, ArtModifierModel.class);
            }catch (Exception ex){
                //ignore
            }
        }
        final List<ArtModifierModel> modifierWithImgList = getModifierWithImgList();
        redisCacheService.set(key,JSON.toJSONString(modifierWithImgList),3600);
        return modifierWithImgList;
    }

    private List<ArtModifierModel> getModifierWithImgList() {

        List<AiParamDictPo> list = aiParamDictDao.getByType(3);


        if (list == null||list.isEmpty()){
            return new ArrayList<>();
        }

        final List<ArtModifierDto> collect = list.stream().map(po -> {
            String text = po.getText();
            if (StringUtils.isEmpty(text)){
                text = po.getName();
            }
            return ArtModifierDto.builder()
                    .name(po.getName())
                    .imgUrl(po.getImgUrl())
                    .text(text)
                    .classType(po.getClassType())
                    .build();
        }).collect(Collectors.toList());

        final Map<String, List<ArtModifierDto>> map = collect.stream().collect(Collectors.groupingBy(ArtModifierDto::getClassType));

        List<ArtModifierModel> modifierModels = new ArrayList<>();
        for (String s : sortSeq) {
            final List<ArtModifierDto> artModifierDtos = map.get(s);
            if (!CollectionUtils.isEmpty(artModifierDtos)){
                modifierModels.add(ArtModifierModel.builder().classType(s).modifierDtos(artModifierDtos).build());
            }
        }

        for (Map.Entry<String, List<ArtModifierDto>> entry : map.entrySet()) {
            if (!sortSeq.contains(entry.getKey())){
                modifierModels.add(ArtModifierModel.builder().classType(entry.getKey()).modifierDtos(entry.getValue()).build());
            }
        }

        return modifierModels;
    }


}
