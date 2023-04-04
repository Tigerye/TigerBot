package com.tigerobo.x.pai.biz.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:Wsen
 * @time: 2019/12/20
 **/
public class SplitUtil {

    public static <T> List<List<T>> splitEqualDivisionList(List<T> input, int division) {
        if (input == null||input.isEmpty()){
            return null;
        }
        if ( division<=0){
            division = 1;
        }
        int size = input.size();
        int step = size / division + (size % division > 0 ? 1 : 0);
        return splitList(input,step);
    }

    public static <T> List<List<T>> splitNearStepList(List<T> input, int step) {
        if (input==null||input.isEmpty()){
            return null;
        }
        if (step<=0){
            step = 1;
        }
        int size = input.size();
        int division = size /step+((size%step)>0?1:0);
        return splitEqualDivisionList(input,division);
    }
    public static <T> List<List<T>> splitList(List<T> input, int step) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        List<List<T>> outputs = new ArrayList<>();

        int inputSize = input.size();
        int start = 0;
        while (start < inputSize) {
            int regionEnd = start + step;
            int end = regionEnd >= inputSize ? inputSize : regionEnd;
            List<T> ts = input.subList(start, end);
            outputs.add(ts);
            start = end;
        }
        return outputs;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i=1;i<=102;i++){
            list.add(i);
        }
        List<List<Integer>> lists = splitNearStepList(list, 100);

        for (List<Integer> sub : lists) {
            System.out.println(sub);
        }

    }
}
