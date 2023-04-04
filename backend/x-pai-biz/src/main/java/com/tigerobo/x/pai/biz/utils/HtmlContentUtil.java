package com.tigerobo.x.pai.biz.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HtmlContentUtil {

    public static String str2html(String text){
        if (StringUtils.isEmpty(text)){
            return "";
        }
        String html = "<p>"+text+"</p>";
        return html;
    }
    public static String cleanNBSP(String text) {

        if (StringUtils.isEmpty(text)) {
            return text;
        }

        byte bytes[] = {(byte) 0xC2, (byte) 0xA0};
        try {
            String s = new String(bytes, "UTF-8");
            return text.replaceAll(s, " ");
        } catch (UnsupportedEncodingException e) {
            log.error("cleanNBSP,{}",text,e);
        }
        return text;
    }

    public static String getContent(List<String> list) {

        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (StringUtils.isEmpty(s)) {
                continue;
            }

            if (s.matches("<img\\s+src=.*>")) {
                builder.append("<p>");
                builder.append(s);
                builder.append("</p>");
            } else {
                builder.append("<p id=\"")
                        .append(i)
                        .append("\">")
//                        .append("\" lang=\"en\">")
                        .append(s)
                        .append("</p>");
            }
        }
        return builder.toString();
    }

    public static String getZhenContent(List<String> enList, List<String> cnList) {
        if (CollectionUtils.isEmpty(enList) || CollectionUtils.isEmpty(cnList)) {
            return "";
        }
        if (enList.size() != cnList.size()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < enList.size(); i++) {
            String en = enList.get(i);
            if (StringUtils.isEmpty(en)) {
                continue;
            }
            String zh = cnList.get(i);
            zh = StringUtils.isEmpty(zh) ? en : zh;
            if (en.contains("<img") || en.contains("<video") || en.contains("</video")) {
                boolean addP = !en.startsWith("<p") && !en.endsWith("</p>");
                if (addP) {
                    builder.append("<p>");
                }
                builder.append(en);
                if (addP) {
                    builder.append("</p>");
                }
            } else {
                builder.append("<p id=\"").append(i).append("\" lang=\"en\">").append(en).append("</p>");

                builder.append("<p id=\"").append(i).append("\" lang=\"zh\">").append(zh).append("</p>");
            }
        }

        return builder.toString();
    }


    public static String getSummary(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            if (isImg(s)) {
                continue;
            }
            return s;
        }
        return "";

    }


    private static boolean isImg(String content) {
        if (content.contains("<img") || content.contains("<video")) {
            return true;
        }
        return false;
    }

    public static String getHeadImgUrlFromContent(String htmlContent){
        Document parse = Jsoup.parse(htmlContent);

        Elements select = parse.select("img[src]");
        if (select==null){
            return null;
        }
        for (Element element : select) {
            String src = element.attr("src");
            if (!StringUtils.isEmpty(src)){
                return src;
            }
        }
        return null;
    }

    public static List<String> parseHtmlCleanContent(String htmlContent){

        Document parse = Jsoup.parse(htmlContent);
        Element body = parse.body();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < body.childNodeSize(); i++) {
            try {
                Node node = body.childNode(i);
                if (node instanceof Element){
//                    Element child = body.child(i);
                    Element child = (Element)node;
                    String s = child.getAllElements().get(0).getAllElements().get(0).text();
                    if (org.apache.commons.lang3.StringUtils.isEmpty(s)) {
                        continue;
                    }
                    list.add(s);
                }

            }catch (Exception ex){
                log.error("解析内容失败:{}",htmlContent,ex);
            }
        }


        return list;
    }

    public static void main(String[] args) {

    }
}
