package com.tigerobo.x.pai.biz.io;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaSentence;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class ExcelWriteSugar<T> {
    private ExcelWriter excelWriter;
    WriteSheet writeSheet;

    public void initWriter(String path,Class<T> tClass,String sheetName){
        File file = new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        excelWriter = EasyExcel.write(path, tClass).build();
        writeSheet = EasyExcel.writerSheet(sheetName).build();

    }

    public void write(List<T> dataList){
        if (excelWriter == null){
            throw new IllegalArgumentException("写入流未初始化");
        }
        if (CollectionUtils.isEmpty(dataList)){
            return;
        }
        excelWriter.write(dataList,writeSheet);
    }
    public void finish(){
        if (excelWriter!=null){
            excelWriter.finish();
        }
    }

}
