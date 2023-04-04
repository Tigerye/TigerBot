package com.tigerobo.x.pai.biz.lake;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.tigerobo.x.pai.biz.utils.SplitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class LakeTranslateService {

    @Value("${pai.blog.translate.url}")
    private String aiTranslateUrl;

    int comLength = 2000;

    public String en2cnIgnore(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        List<String> trans = en2cn(Arrays.asList(text));
        if (CollectionUtils.isEmpty(trans)){
            return null;
        }
        return trans.get(0);
    }

    public String en2cn(String text){
        if (StringUtils.isBlank(text)){
            return text;
        }
        List<String> trans = en2cn(Arrays.asList(text));
        if (CollectionUtils.isEmpty(trans)){
            return text;
        }
        return trans.get(0);
    }

    public List<String> en2cn(List<String> textList){
        if (CollectionUtils.isEmpty(textList)){
            return textList;
        }

        Map<Integer,String> longTranMap=new HashMap<>();
        List<String> shortList = new ArrayList<>();

        Map<Integer,Integer> shortIndexMap = new HashMap<>();
        for (int i = 0; i < textList.size(); i++) {
            String text =textList.get(i);
            if (text.length()>comLength){
                List<String> textSplitList = getTextSplitList(text);
                List<String> result = splitTrans(textSplitList);
                String transItem;
                if(!CollectionUtils.isEmpty(result)){
                    transItem = String.join("",result);
                    if (text.length()>transItem.length()*80){
                        transItem = text;
                    }
                }else {
                    transItem = text;
                }
                longTranMap.put(i,transItem);
            }else {
                shortIndexMap.put(i,shortList.size());
                shortList.add(text);
            }
        }
        List<String> shortTrans = splitTrans(shortList);

        if (!CollectionUtils.isEmpty(shortList)){
            if (CollectionUtils.isEmpty(shortTrans)||shortTrans.size()<shortList.size()){
                throw new IllegalArgumentException("翻译失败");
            }
        }
        List<String> transList = new ArrayList<>();
        for (int i = 0; i < textList.size(); i++) {

            String tranText = longTranMap.get(i);
            String text = textList.get(i);

            if (StringUtils.isBlank(tranText)){
                Integer index = shortIndexMap.get(i);
                if (index!=null){
                    tranText = shortTrans.get(index);
                }
            }
            if (StringUtils.isBlank(tranText)){
                tranText = text;
            }
            transList.add(tranText);
        }
        return transList;

    }


    private List<String> getTextSplitList(String text){

        if (StringUtils.isBlank(text)){
            return null;
        }

        List<String> textList = new ArrayList<>();
        String sub = text;
        while (sub.length()>comLength){
            String useText = sub.substring(0,comLength);
            int index = useText.lastIndexOf(" ");
            if (index>comLength*0.8){

            }else {
                index = comLength;
            }
            useText = sub.substring(0,index);
            sub = sub.substring(index);
            textList.add(useText);
        }
        return textList;
    }
    public String doEn2cn(String text){
        if (StringUtils.isBlank(text)){
            return text;
        }
        List<String> result = doEn2cn(Arrays.asList(text));
        if (CollectionUtils.isEmpty(result)){
            return text;
        }
        return result.get(0);
    }

    private List<String> splitTrans(List<String> textList){

        List<String> tranList = new ArrayList<>();
        List<List<String>> lists = SplitUtil.splitList(textList, 4);

        for (List<String> list : lists) {
            if (CollectionUtils.isEmpty(list)){
                continue;
            }
            List<String> subTrans = new ArrayList<>();
            for (String s : list) {
               String tran = doEn2cn(s);
               subTrans.add(tran);
            }

            if (CollectionUtils.isEmpty(subTrans)||subTrans.size()!=list.size()){
                throw new IllegalArgumentException("翻译失败,长度不匹配");
            }
            tranList.addAll(subTrans);
        }

        return tranList;
    }

    public List<String> doEn2cn(List<String> textList){
        if (CollectionUtils.isEmpty(textList)){
            return textList;
        }

//        String aiTranslateUrl = "http://gbox8.aigauss.com:8867/new_trans/en2zh";
        long start = System.currentTimeMillis();

        Map<String,Object> map = new HashMap<>();
        map.put("text_list",textList);
        String post = RestUtil.post(aiTranslateUrl, map);

        long delta = System.currentTimeMillis() - start;
//        if (delta>1500){
//            log.info("trans-time:{}ms", delta);
//        }

        if (StringUtils.isNotBlank(post)){
            JSONObject jsonObject = JSON.parseObject(post);
            if (jsonObject!=null&&jsonObject.get("result")!=null){
                JSONArray result = jsonObject.getJSONArray("result");

                List<String> list = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    list.add(result.getString(i));
                }
                return list;
            }
        }
        return null;
    }
}
