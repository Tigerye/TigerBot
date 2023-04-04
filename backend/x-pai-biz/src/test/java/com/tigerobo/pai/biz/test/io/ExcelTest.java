package com.tigerobo.pai.biz.test.io;

import com.tigerobo.x.pai.api.aml.dto.evaluate.EvaSentence;
import com.tigerobo.x.pai.biz.io.ExcelWriteSugar;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class ExcelTest {

    @Test
    public void xlsWriteTest(){
        String path = "/tmp/aml/test/1.xlsx";

        ExcelWriteSugar<EvaSentence> sugar = new ExcelWriteSugar<>();

        sugar.initWriter(path,EvaSentence.class,"label");
        EvaSentence sentence = new EvaSentence();
        sentence.setSentence("第一个内容");
        sentence.setScore(new BigDecimal("0.23221"));

        for (int i = 0; i < 100000; i++) {
            sugar.write(Arrays.asList(sentence));
        }


        sugar.finish();
    }

}
