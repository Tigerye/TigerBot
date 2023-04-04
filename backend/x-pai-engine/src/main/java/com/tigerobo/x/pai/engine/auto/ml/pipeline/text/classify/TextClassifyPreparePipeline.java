package com.tigerobo.x.pai.engine.auto.ml.pipeline.text.classify;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyMetaDto;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyOpContentDto;
import com.tigerobo.x.pai.api.aml.engine.dto.ai.model.text.classify.TextClassifyStatisticDto;
import com.tigerobo.x.pai.api.aml.enums.DatasetMetricEnum;
import com.tigerobo.x.pai.api.enums.AmlTrainDataTypeEnum;
import com.tigerobo.x.pai.engine.auto.ml.io.CsvSugar;
import com.tigerobo.x.pai.engine.auto.ml.io.ExcelSugar;
import com.tigerobo.x.pai.engine.exception.AmlException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 解析数据集
 */
@Slf4j
public class TextClassifyPreparePipeline {

    protected String statisticName = "statistic.json";

    Map<DatasetMetricEnum, Integer> metricCountMap = new HashMap<>();

    Map<DatasetMetricEnum, String> metricPathMap = new HashMap<>();

    Map<DatasetMetricEnum, BufferedWriter> metricWriterMap = new HashMap<>();

    Map<String, Integer> labelCountMap = new HashMap<>();
    Map<String, Integer> trainCountMap = new HashMap<>();
    Map<String, String> labelPathMap = new HashMap<>();
    Map<String, BufferedWriter> labelWriterMap = new HashMap<>();

    List<String> labelList = new ArrayList<>();

    Map<Integer, Integer> sentenceLengthCountMap = new HashMap<>();
    String workRoot = null;
    String labelRoot = null;

    @SneakyThrows
    public TextClassifyStatisticDto run(TextClassifyMetaDto metaDto, TextClassifyOpContentDto opContent) {
        Validate.isTrue(metaDto != null && opContent != null, "参数不能为空");
        String datasetId = metaDto.getDatasetId();
        List<String> inputPathList = metaDto.getInputPathList();
        initPath(opContent);
        String fileType = metaDto.getFileType();

        try {
            initMetricWriter();
            for (String inputPath : inputPathList) {
                boolean exists = Files.exists(Paths.get(inputPath));
                if (!exists) {
                    continue;
                }
//                dealFile(datasetId, inputPath);
                if ("xls".equalsIgnoreCase(fileType)) {
                    ExcelSugar.doDealFile(datasetId, inputPath, k -> dealRecorder(k));
                } else if ("csv".equalsIgnoreCase(fileType)) {
                    CsvSugar.doDealFile(datasetId, inputPath, k -> dealRecorder(k));
                } else {
                    throw new RuntimeException("文件类型不支持");
                }
//
                cleanBuffer();
            }
        }  finally {
            for (BufferedWriter value : metricWriterMap.values()) {
                if (value != null) {
                    try {
                        value.close();
                    } catch (IOException e) {
                        log.error("close,metric,datasetId:{}", datasetId, e);
                    }
                }
            }
            for (BufferedWriter value : labelWriterMap.values()) {
                if (value != null) {
                    try {
                        value.close();
                    } catch (IOException e) {
                        log.error("close,label,datasetId:{}", datasetId, e);
                    }
                }
            }
        }

        return process(datasetId);
    }

    private void initMetricWriter() throws FileNotFoundException {
        for (DatasetMetricEnum value : DatasetMetricEnum.values()) {
            String filePath = workRoot + value.getDefaultFileName();
            BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));
            metricWriterMap.put(value, fileWriter);
            metricPathMap.put(value, filePath);
            metricCountMap.put(value, 0);
        }
    }


    private void initPath(TextClassifyOpContentDto opContent) {
        String outputRoot = opContent.getOutputRoot();

        workRoot = outputRoot + "process/";

        File root = new File(workRoot);
        if (!root.exists()) {
            root.mkdirs();
        }

        labelRoot = workRoot + "label/";
        File labelFileRoot = new File(labelRoot);
        if (!labelFileRoot.exists()) {
            labelFileRoot.mkdirs();
        }
    }

    private TextClassifyStatisticDto process(String datasetId) {
        TextClassifyStatisticDto prepareDto = new TextClassifyStatisticDto();
        List<TextClassifyStatisticDto.Item> metricList = new ArrayList<>();
        for (DatasetMetricEnum value : DatasetMetricEnum.values()) {
            Integer count = metricCountMap.get(value);

            if (count == null) {
                count = 0;
            }

            String path = metricPathMap.get(value);
            TextClassifyStatisticDto.Item item = new TextClassifyStatisticDto.Item();
            item.setKey(value.getKey());
            item.setCount(count);
            item.setPath(path);
            metricList.add(item);
        }
        List<TextClassifyStatisticDto.Item> labelItemList = new ArrayList<>();
        for (String label : labelList) {
            Integer count = labelCountMap.get(label);
            if (count == null || count == 0) {
                continue;
            }
            String path = labelPathMap.get(label);
            TextClassifyStatisticDto.Item item = new TextClassifyStatisticDto.Item();
            item.setKey(label);
            item.setPath(path);
            item.setCount(count);
            labelItemList.add(item);
        }

        prepareDto.setMetricList(metricList);
        prepareDto.setLabelList(labelItemList);
        prepareDto.setLabelNameList(labelList);

        String statisticPath = workRoot + statisticName;
        prepareDto.setStatisticPath(statisticPath);


        List<TextClassifyStatisticDto.SentenceLengthCount> sentenceRegionList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : sentenceLengthCountMap.entrySet()) {
            TextClassifyStatisticDto.SentenceLengthCount region = new TextClassifyStatisticDto.SentenceLengthCount();
            region.setStart(entry.getKey());
            region.setCount(entry.getValue());

            sentenceRegionList.add(region);
        }
        if (sentenceRegionList.size() > 1) {
            sentenceRegionList.sort(Comparator.comparing(TextClassifyStatisticDto.SentenceLengthCount::getStart));
        }
        prepareDto.setSentenceLengthCountList(sentenceRegionList);


        try (BufferedWriter fileWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(statisticPath)));) {
            fileWriter.write(JSON.toJSONString(prepareDto));
            fileWriter.flush();
        } catch (Exception ex) {
            log.error("dataset:{},statisticPath-write:{}", datasetId, statisticPath, ex);
        }

        return prepareDto;
    }


    private void cleanBuffer() throws IOException {
        for (BufferedWriter value : metricWriterMap.values()) {
            value.flush();
        }

        for (BufferedWriter value : labelWriterMap.values()) {
            value.flush();
        }
    }

//    @Deprecated
//    protected void dealFile(String datasetId, String inputPath) {
//        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)))) {
//            CSVParser parse = CSVFormat.DEFAULT.parse(fileReader);
//
////        List<CSVRecord> records = parse.getRecords();
//            for (CSVRecord record : parse) {
//                int size = record.size();
//                if (size == 0) {
//                    continue;
//                }
//                String sentence = record.get(0);
//                String label = null;
//
//                if (size >= 2) {
//                    label = record.get(1);
//                }
//                dealRecorder(sentence, label);
//            }
//
//        } catch (Exception ex) {
//            log.error("dealCsv-error:datasetId:{},path:{}", datasetId, inputPath, ex);
//        }
//    }

    protected void dealRecorder(InputElement element) {

        try {
            doDealRecorder(element);
        } catch (Exception e) {
            log.error("{}",JSON.toJSONString(element),e);
            throw new RuntimeException(e);
        }
    }

    protected void doDealRecorder(InputElement element) throws Exception {
        String sentence=element.getSentence();
        String label = element.getLabel();
        AmlTrainDataTypeEnum type = element.getType();

        boolean senBlank = StringUtils.isBlank(sentence);
        boolean labelBlank = StringUtils.isBlank(label);
        if (senBlank && labelBlank) {
            return;
        }
        if (!labelBlank) {
            label = label.trim();
        }

        String dataJson = getDataJson(sentence, label);
        writeMetric(DatasetMetricEnum.ALL_ITEM, dataJson);

        if (!senBlank && !labelBlank) {
            countSentenceLength(sentence);
            writeMetric(DatasetMetricEnum.LABELED, dataJson);
            writeLabel(label, dataJson);

            if (type!=null){
                if (AmlTrainDataTypeEnum.TEST ==type){
                    writeMetric(DatasetMetricEnum.TEST, dataJson);
                }else {
                    int labelCount = getTrainLabelCount(label);
                    int target = labelCount % 9;
                    if (target == 3) {
                        writeMetric(DatasetMetricEnum.VALIDATION, dataJson);
                        incrTrain(label);
                    } else {
                        writeMetric(DatasetMetricEnum.TRAIN, dataJson);
                        incrTrain(label);
                    }
                }
            }else {
                int labelCount = getCount(label);
                int target = labelCount % 10;
                if (target == 2) {
                    writeMetric(DatasetMetricEnum.TEST, dataJson);
                } else if (target == 3) {
                    writeMetric(DatasetMetricEnum.VALIDATION, dataJson);
                } else {
                    writeMetric(DatasetMetricEnum.TRAIN, dataJson);
                }
            }

        } else {
            writeMetric(DatasetMetricEnum.UN_LABELED, dataJson);
        }
    }


    private static int REGION_1000 = 1000;
    private static int REGION_500 = 500;
    private static int REGION_STEP = 20;

    private void countSentenceLength(String text) {
        int length = text.trim().length();
        if (length == 0) {
            return;
        }
        if (length > REGION_1000) {
            Integer count = sentenceLengthCountMap.computeIfAbsent(REGION_1000, k -> 0);
            sentenceLengthCountMap.put(REGION_1000, count + 1);
        } else if (length > REGION_500) {
            Integer count = sentenceLengthCountMap.computeIfAbsent(REGION_500, k -> 0);
            sentenceLengthCountMap.put(REGION_500, count + 1);
        } else {
            int region = length / REGION_STEP;
            int sub = length % REGION_STEP;
            int regionLength;
            if (sub == 0) {
                regionLength = (region - 1) * REGION_STEP;
            } else {
                regionLength = region * REGION_STEP;
            }
            Integer count = sentenceLengthCountMap.computeIfAbsent(regionLength, k -> 0);
            sentenceLengthCountMap.put(regionLength, count + 1);
        }
    }

    private void incrMetric(DatasetMetricEnum metricEnum) {
        Integer count = metricCountMap.get(metricEnum);
        metricCountMap.put(metricEnum, count + 1);

    }

    private void writeLabel(String label, String dataJson) throws Exception {
        String clean = label.trim();
        BufferedWriter bufferedWriter = labelWriterMap.get(clean);
        if (bufferedWriter == null) {
            labelList.add(clean);
            Preconditions.checkArgument(labelList.size()<1000,"不能大于1000个标签");
            int size = labelList.size();
            String path = labelRoot + size + ".json";
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            labelWriterMap.put(clean, bufferedWriter);
            labelPathMap.put(clean, path);
        }
        bufferedWriter.write(dataJson);
        bufferedWriter.newLine();
        incr(clean);

    }

    private void writeMetric(DatasetMetricEnum metricEnum, String dataJson) throws Exception {
        BufferedWriter bufferedWriter = metricWriterMap.get(metricEnum);
        bufferedWriter.write(dataJson);
        bufferedWriter.newLine();


        incrMetric(metricEnum);

    }

    private void incr(String label) {
        Integer count = labelCountMap.get(label);

        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        labelCountMap.put(label, count);
    }
    private void incrTrain(String label) {
        Integer count = trainCountMap.get(label);

        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        trainCountMap.put(label, count);
    }

    private int getTrainLabelCount(String label) {
        String trim = label.trim();
        Integer count = trainCountMap.get(trim);
        return count == null ? 0 : count;
    }

    private int getCount(String label) {
        String trim = label.trim();
        Integer count = labelCountMap.get(trim);
        return count == null ? 0 : count;
    }

    private String getDataJson(String sentence, String label) {
        Map<String, Object> data = new HashMap<>();
        sentence = sentence!=null?sentence.trim():null;
        data.put("sentence1", sentence);
        data.put("label", label);
        return JSON.toJSONString(data);
    }

    @Data
    public static class InputElement {
        String sentence;
        String label;
        AmlTrainDataTypeEnum type;
    }
}
