package com.tigerobo.x.pai.engine.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CSVTest {


    @Test
    public void printTest(){

        String text = "{\"status\": 0, \"result\": [[{\"start\": 6, \"end\": 7, \"text\": \"\\u5343\", \"corrected_text\": \"\\u6b49\"}, {\"start\": 13, \"end\": 14, \"text\": \"\\u5148\", \"corrected_text\": \"\\u73b0\"}]]}";
        System.out.println(text);

        JSONObject jsonObject = JSON.parseObject(text);
        JSONArray result = jsonObject.getJSONArray("result");

        for (int i = 0; i < result.size(); i++) {
            JSONArray jsonArray = result.getJSONArray(i);

            for (int j = 0; j < jsonArray.size(); j++) {

                JSONObject sub = jsonArray.getJSONObject(j);

                System.out.println(sub.getString("corrected_text"));
            }
        }

    }
    @Test
    public void csvStringTest()throws Exception{

        String base = "E:\\pai\\engine\\";
        String csvFile = base + "hm.csv";

        long startTime = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));
        String line = null;

        String fileWritePath = base + "prepare\\" + "b.csv";
        Writer writer = new FileWriter(fileWritePath);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
        int err = 0;
        CSVParser parser = CSVFormat.DEFAULT.parse(reader);

//        Iterator<CSVRecord> iterator = parser.iterator();
        List<CSVRecord> records = parser.getRecords();
//        CSVRecord next = null;
        for (CSVRecord record : records) {
            printer.printRecord(record);
        }
        printer.flush();

        System.out.println("err:"+err+",delta:"+(System.currentTimeMillis()-startTime));
    }
    @Test
    public void csvFileTest()throws Exception{

        String base = "E:\\pai\\engine\\";
        String csvFile = base + "hm.csv";

        long start = System.currentTimeMillis();
        Reader reader = new BufferedReader(new FileReader(csvFile));
        CSVParser parse = CSVFormat.DEFAULT.parse(reader);


        List<CSVRecord> records = parse.getRecords();
        System.out.println(records.size());

        String fileWritePath = base + "prepare\\" + "a.csv";
        Writer writer = new FileWriter(fileWritePath);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

        for (int i = 0; i < records.size() ; i++) {
            CSVRecord record = records.get(i);

            printer.printRecord(record);
        }
        printer.flush();
        System.out.println("delta:" + (System.currentTimeMillis() - start));
    }

    @Test
    public void jsonReadTest()throws Exception{

        String path = "E:\\pai\\engine\\demo\\"+"train.json";
        long startTime = System.currentTimeMillis();
        int count = 0;
        Set<String> labels = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));){
            String line = null;
            while ((line = reader.readLine())!=null){
                JSONObject jsonObject = JSON.parseObject(line);
                String label = jsonObject.getString("label");
                if (label!=null&&label.length()>0){
                    labels.add(label);
                }

            }
        }
        System.out.println("count:"+count);

        System.out.println("delta:"+(System.currentTimeMillis()-startTime));

        System.out.println("label-"+JSON.toJSONString(labels));
    }

    @Test
    public void jsonListFileTest()throws Exception{
        String path = "/mnt/xpai/engine/test/aml/u3/train/3/evaluation/predict.json";

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))){

//            while ()
        }
    }
}
