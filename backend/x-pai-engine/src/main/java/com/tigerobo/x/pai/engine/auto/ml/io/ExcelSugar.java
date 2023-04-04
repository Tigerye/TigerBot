package com.tigerobo.x.pai.engine.auto.ml.io;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.enums.AmlDataStatisticTypeEnum;
import com.tigerobo.x.pai.api.enums.AmlTrainDataTypeEnum;
import com.tigerobo.x.pai.engine.auto.ml.pipeline.text.classify.TextClassifyPreparePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
public class ExcelSugar {


    public static void doDealFile(String datasetId, String inputPath, Consumer<TextClassifyPreparePipeline.InputElement> binaryOperator){

        EasyExcel.read(inputPath, new AnalysisEventListener<LinkedHashMap<Integer,String>>(){

            boolean hasType;
            int count=0;

            public void onException(Exception exception, AnalysisContext context) throws Exception {
                log.error("datasetId:{},path:{},errorCount:{}",datasetId,inputPath,count);
                throw exception;
            }
            @Override
            public void invoke(LinkedHashMap<Integer,String> input, AnalysisContext analysisContext) {

                if (count == 0){
                    String type = input.get(2);
                    if (!StringUtils.isBlank(type)){
                        hasType = true;
                        AmlTrainDataTypeEnum typeEnum = AmlTrainDataTypeEnum.getByName(type);
                        Preconditions.checkArgument(typeEnum!=null,"第三列类型错误，只支持(test,train)");
                    }
                }

                String sentence = input.get(0);
                String label = input.get(1);

                AmlTrainDataTypeEnum type = null;
                if (hasType){
                    AmlTrainDataTypeEnum typeEnum = AmlTrainDataTypeEnum.getByName(input.get(2));
                    if (typeEnum == null){
                        type = AmlTrainDataTypeEnum.TRAIN;
                    }else {
                        type = typeEnum;
                    }
                }

                TextClassifyPreparePipeline.InputElement inputElement = new TextClassifyPreparePipeline.InputElement();
                inputElement.setLabel(label);
                inputElement.setSentence(sentence);
                inputElement.setType(type);
                binaryOperator.accept(inputElement);
                count++;
            }
//            @Override
//            public void invokeHeadMap(Map<Integer, String> input, AnalysisContext context) {
//                String sentence = input.get(0);
//                String label = input.get(1);
//
//
//
//            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                log.info("datasetId:{},xls-total:{}",datasetId,count);
            }

        }).ignoreEmptyRow(true).headRowNumber(0).sheet(0).doRead();
    }

}
