package com.tigerobo.x.pai.biz.content;


import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.utils.ContentTypeUtil;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class HtmlContentService {

    @Autowired
    private OssService ossService;

    public String convertBase64ToUrl(String htmlContent){
        if (StringUtils.isEmpty(htmlContent)){
            return htmlContent;
        }
        Document parse = Jsoup.parse(htmlContent);
        Elements select = parse.select("img[src]");
        boolean hasReplace = false;
        if (select!=null){
            for (Element element : select) {
                String src = element.attr("src");

                if (!StringUtils.isEmpty(src)&&src.matches("data:image/.+;base64,.+")){
                    String ossUrl = uploadBase64toOss(src);
                    element.attr("src",ossUrl);
                    hasReplace =true;
                }
            }
        }
        return hasReplace?parse.select("body").html():htmlContent;
    }

    private String uploadBase64toOss(String text){

        try {
            String regex = "data:(image/.+);base64,(.+)";

            Pattern pattern = Pattern.compile(regex);

            Matcher matcher = pattern.matcher(text);

            if (!matcher.find()) {
                return null;
            }
            String contentType = matcher.group(1);
            String suffix = ContentTypeUtil.getContentExtension(contentType);
            if (StringUtils.isEmpty(suffix)) {
                suffix = ".png";
            }

            String key = Md5Util.getMd5(text);
            String base64Content = matcher.group(2);
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

            String name = key + suffix;
            String url = ossService.uploadFile(decodedBytes, name);
            return url;
        }catch (Exception ex){
            log.error("base64上传图片失败text:{}",text,ex);
            throw new IllegalArgumentException("上传图片失败");
        }
    }

}
