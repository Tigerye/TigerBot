package com.tigerobo.pai.biz.test.common.test;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CsvTest {

    @Test
    public void writeTest()throws Exception{
        String outputFilePath = "/mnt/csv/1102.csv";

        File file = new File(outputFilePath);
        if (!file.exists()){
            file.getParentFile().mkdirs();
        }

        try (CSVPrinter csvPrinter = CSVFormat.DEFAULT
                .print(Files.newBufferedWriter(Paths.get(outputFilePath),StandardOpenOption.APPEND,StandardOpenOption.CREATE))) {

            csvPrinter.printRecord("100", "John Doe", "4.5");
            csvPrinter.printRecord("101", "Mark Cooper", "3.8");
        }
    }
}
