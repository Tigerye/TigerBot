package com.tigerobo.x.pai.biz.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;

@Slf4j
public class NasFileUtil {


    public static void chmodDir(String path){
        String systemName = System.getProperty("os.name").toLowerCase();
        if (systemName!=null&&systemName.contains("win")){
            return;
        }
        if (StringUtils.isEmpty(path)){
            return;
        }

        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        doChmod(file);
        doChmod(file.getParentFile());
    }

    public static void doChmod(File file){
        if (file==null||!file.exists()){
            return;
        }

        try {

            file.setReadable(true,false);
            file.setWritable(true, false);

        }catch (Exception ex){
            log.error("chmod,path-{}",file.getPath(),ex);
        }
    }

    public static void delDir(String path){

        if (StringUtils.isEmpty(path)){
            return;
        }
        File file = new File(path);

        if (!file.exists()){
            return;
        }

        for (File sub : file.listFiles()) {
            if (sub.isDirectory()){
                continue;
            }
            sub.delete();
        }

    }

}
