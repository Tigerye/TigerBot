package com.tigerobo.x.pai.common.xls;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Data;
import org.junit.Test;

import java.util.LinkedHashMap;

public class ExcelTest {

    @Test
    public void easyTest(){

        String path = "/mnt/xpai/risk.xlsx";
        EasyExcel.read(path, new AnalysisEventListener<LinkedHashMap<Integer,String>>(){

            int count = 0;
            @Override
            public void invoke(LinkedHashMap<Integer,String> o, AnalysisContext analysisContext) {
//
//                for (Map.Entry<Integer, String> entry : o.entrySet()) {
//                    System.out.print(entry.getKey());
//                    System.out.println("\t");
//                    System.out.println(entry.getValue());
//                }

                count++;
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                System.out.println("total:"+count);
            }
        }).ignoreEmptyRow(true).headRowNumber(0).sheet(0).doRead();
    }


    @Data
    private static class DemoData{
        @ExcelProperty(index = 0)
        String content;
        @ExcelProperty(index = 1)
        String label;

    }

    private class DemoDataListener implements ReadListener<DemoData> {

        @Override
        public void invoke(DemoData demoData, AnalysisContext analysisContext) {
            System.out.println(demoData.getContent());
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        }
    }
}
