package com.tigerobo.x.pai.biz.serving.evaluate.entity;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description:
 * @modified By:
 * @version: $
 */
public interface Resultable {

    String toText();

    String toText(double threshold);
}
