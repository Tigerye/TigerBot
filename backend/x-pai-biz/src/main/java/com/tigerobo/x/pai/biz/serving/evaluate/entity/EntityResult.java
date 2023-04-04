package com.tigerobo.x.pai.biz.serving.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
@Data
public class EntityResult extends ArrayList<EntityResult.Entity>  {
    private final static double THRESHOLD_DEFAULT = 0.5;

    public String toText(String oriText) {
        return this.toText(THRESHOLD_DEFAULT,oriText);
    }

    public String toText(double threshold,String oriText) {
        return String.join(",", this.stream().filter(entity -> entity.getScore() >= threshold).map(e->e.toText(oriText)).collect(Collectors.toList()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entity {
        private String entityGroup;
        private String text;
        private Integer start;
        private Integer end;
        private Double score;

        public String toText(String oriText) {
            String showText = text;
            if (StringUtils.isBlank(showText)){
                if (StringUtils.isBlank(oriText)){
                    return "";
                }
                if (start==null||end==null||start>end||end>oriText.length()){
                    return "";
                }
                showText = oriText.substring(start,end);
            }
            return showText + ":" + this.entityGroup;
        }
    }
}


