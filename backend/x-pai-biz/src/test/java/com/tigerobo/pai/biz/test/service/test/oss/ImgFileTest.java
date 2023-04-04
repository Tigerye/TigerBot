package com.tigerobo.pai.biz.test.service.test.oss;

import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImgFileTest {

    @Test
    public void getFileImgSuffixTest(){
        String path = "E:\\pai\\模型\\艺术图\\images\\images";

        final File dir = new File(path);

//        final String[] list = dir.list();

        final File[] files = dir.listFiles();


        String baseImg = "update ai_param_dict set img_url = '%s' where type = 3 and name = '%s'";
        Map<String,String> map = new LinkedHashMap<>();
        for (File file : files) {
            final String name = file.getName().trim();
            if (name.endsWith(".jpeg")){
                continue;
            }
            final int i = name.lastIndexOf(".");
            final String clean = name.substring(0, i);
            map.put(clean,name);

            final String format = String.format(baseImg, name, clean);
            System.out.println(format);

        }

//        System.out.println(JSON.toJSONString(map));



    }
}
