package com.tigerobo.x.pai.biz.serving.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class LabelResult extends ArrayList<LabelResult.Label> implements Resultable {
    private final static double THRESHOLD_DEFAULT = 0.5;

    @Override
    public String toText() {
        return this.toText(THRESHOLD_DEFAULT);
    }

    public String toText(double threshold) {
        if (this.isEmpty()){
            return "";
        }
        return this.stream().findFirst().map(LabelResult.Label::toText).get();

//        return String.join(",", this.stream().filter(label -> label.getScore() >= threshold).map(LabelResult.Label::toText).collect(Collectors.toList()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Label {
        private String label;
        private Double score;

        public String toText() {
            return this.label;
        }
    }

}