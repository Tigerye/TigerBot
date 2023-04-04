package com.tigerobo.x.pai.biz.io;

import com.aliyun.oss.model.OSSObject;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

@Slf4j
@Service
public class IOService {
    @Autowired
    private OSSHome ossHome;

    public String writeXlsxFile(String filePath) {

        return writeOssUrl2local(filePath,null,".xlsx");
    }

    public String writeOssUrl2local(String ossUrl)  {
        return writeOssUrl2local(ossUrl,null,null);
    }


    public String writeOssUrl2local(String ossUrl,String inputPath)  {
        return writeOssUrl2local(ossUrl,inputPath,null);
    }
    public String writeOssUrl2local(String ossUrl,String inputPath,String suffix)  {
        if (StringUtils.isBlank(inputPath)){
            inputPath = getInputPath(ossUrl, suffix);
        }

        File file = new File(inputPath);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }


        String key = OssCombineUtil.getKeyByUrl(ossUrl);
        OSSObject ossObject = this.ossHome.get(key);
        try(BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(inputPath));) {
//            Files.copy(download, Paths.get(inputPath));
            IOUtils.copy(ossObject.getObjectContent(), outputStream);
        } catch (IOException e) {
            log.error("path:{}",ossUrl,e);
            throw new IllegalArgumentException("处理输入文件失败");
        }
        return inputPath;
    }

    private String getInputPath(String ossUrl, String suffix) {

        String key = OssCombineUtil.getKeyByUrl(ossUrl);
        String sub = key;

        int index = sub.lastIndexOf("/");

        String name = null;
        if (index>0&&index<sub.length()-1){
            name = sub.substring(index+1);
        }
        if (name == null){
            name = System.currentTimeMillis()+ suffix;
        }
        String inputPath = "/tmp/aml/evaluation/input/"+name;
        return inputPath;
    }
}
