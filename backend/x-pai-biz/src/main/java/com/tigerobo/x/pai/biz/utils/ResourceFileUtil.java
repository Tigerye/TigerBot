package com.tigerobo.x.pai.biz.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceFileUtil {
    private static final Logger logger = LoggerFactory.getLogger(ResourceFileUtil.class);

    public static List<String> readToList(String resourceFileName) {
        List<String> lineList = new ArrayList<>();

        try (InputStream inputStream = ResourceFileUtil.class.getClassLoader().getResourceAsStream(resourceFileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lineList.add(line);
            }

        } catch (Exception e) {
            logger.error("read resource file failed!", e);
        }

        return lineList;
    }
}
