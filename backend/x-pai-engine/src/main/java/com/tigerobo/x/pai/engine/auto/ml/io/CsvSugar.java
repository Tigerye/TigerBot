package com.tigerobo.x.pai.engine.auto.ml.io;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.enums.AmlTrainDataTypeEnum;
import com.tigerobo.x.pai.engine.auto.ml.pipeline.text.classify.TextClassifyPreparePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

@Slf4j
public class CsvSugar {




    public static void doDealFile(String datasetId, String inputPath, Consumer<TextClassifyPreparePipeline.InputElement> binaryOperator){


        CsvReader reader = CsvUtil.getReader();
        CsvData data = reader.read(FileUtil.file(inputPath));


        int count=0;
        boolean hasType = false;

        for(CsvRow record : data.getRows()){
//getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            int size = record.size();
            if (size == 0) {
                continue;
            }

            if (count == 0){
                String typeValue = null;
                if (size>2){
                    typeValue = record.get(2);
                }
                if (!StringUtils.isBlank(typeValue)){
                    hasType = true;
                    AmlTrainDataTypeEnum typeEnum = AmlTrainDataTypeEnum.getByName(typeValue);
                    Preconditions.checkArgument(typeEnum!=null,"第三列类型错误，只支持(test,train)");
                }
            }
            count++;


            String sentence = record.get(0);
            String label = null;

            if (size >= 2) {
                label = record.get(1);
            }
            AmlTrainDataTypeEnum type = null;
            if (hasType){
                String typeValue = size>2?record.get(2):null;
                AmlTrainDataTypeEnum typeEnum = AmlTrainDataTypeEnum.getByName(typeValue);
                if (typeEnum == null){
                    type = AmlTrainDataTypeEnum.TRAIN;
                }else {
                    type = typeEnum;
                }
            }

            TextClassifyPreparePipeline.InputElement element = new TextClassifyPreparePipeline.InputElement();
            element.setSentence(sentence);
            element.setLabel(label);
            element.setType(type);
            binaryOperator.accept(element);
        }

    }

}
