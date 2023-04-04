package com.tigerobo.x.pai.common.xls;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import org.junit.Test;

public class CsvTest {

    @Test
    public void csvTest()throws Exception{

        String base = "D:/mnt/xpai/";

//        String first = base +"hongguan-1630660676006.csv";
//        String second = base +"industry-1630660669463.csv";
//        String first = base +"risk_big.csv";

        String path = "/mnt/xpai/risk4.csv";

        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file(path));

//遍历行
        for(CsvRow csvRow : data.getRows()){
//getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）

            System.out.println(csvRow.toString());

            String s = csvRow.get(0);
            System.out.println(s);
        }


    }
}
