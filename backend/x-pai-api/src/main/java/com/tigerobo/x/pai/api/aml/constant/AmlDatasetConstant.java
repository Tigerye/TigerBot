package com.tigerobo.x.pai.api.aml.constant;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AmlDatasetConstant {

    private static List<Integer> SENTENCE_LENGTH_COUNT_LIST = null;


    public static List<Integer> getSentenceLengthCountList(){
        if (SENTENCE_LENGTH_COUNT_LIST==null){
            synchronized (AmlDatasetConstant.class){
                if (SENTENCE_LENGTH_COUNT_LIST == null){
                    List<Integer> list = new ArrayList<>();
                    for (int i=0;i<=500;i=i+20){
                        list.add(i);
                    }
                    list.add(1000);
                    SENTENCE_LENGTH_COUNT_LIST = list;
                }
            }
        }
        return SENTENCE_LENGTH_COUNT_LIST;
    }

    public static void main(String[] args) {
        System.out.println(getSentenceLengthCountList());
    }

}
