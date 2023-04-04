package com.tigerobo.pai.biz.test.service.test.oss;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.tigerobo.x.pai.biz.utils.SplitUtil;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ImageOssTest extends OssBaseTest{

    @Test
    public void dealSampleImageTest(){

        ossHome.init();
        final OSSClient ossClient = ossHome.getOssClient();
        final String keyPrefix = "search/dataset/image/sample/images/";

        int total = 0;
        String marked = "";
        while (true){

            ListObjectsRequest request = new ListObjectsRequest();

            request.setBucketName("x-pai");
            request.setPrefix(keyPrefix);
            if (StringUtils.isNotBlank(marked)){
                request.setMarker(marked);
            }
            request.setMaxKeys(1000);
            ObjectListing objectListing = ossClient.listObjects(request);
            final List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            if (objectSummaries == null||objectSummaries.isEmpty()){
                break;
            }

            marked = objectListing.getNextMarker();
            total += objectSummaries.size();
            if (StringUtils.isBlank(marked)){
                break;
            }
        }

        System.out.println("total="+total);

    }


    @Test
    public void uploadPublicTest()throws Exception{
        String path = "E:\\pai\\模型\\艺术图\\images\\images";
        String prefix = "model/dict/image/v3/";
        final File dir = new File(path);

//        final String[] list = dir.list();

        final File[] files = dir.listFiles();


        final List<List<File>> lists = SplitUtil.splitList(Arrays.asList(files), 100);

        final int totalBatch = lists.size();
        System.out.println("总批量数"+ totalBatch);

        Map<String,String> nameUrlMap = new HashMap<>();
        for (int i = 0; i < totalBatch; i++) {
            final List<File> fileList = lists.get(i);

            System.out.println("batch,i/t:"+i+"/"+totalBatch);
            fileList.parallelStream().forEach(f->{
                final File file = f;
                String name = file.getName().trim();
                String ossKey = prefix + name;
                nameUrlMap.put(name,ossKey);
                final byte[] bytes;
                try {
                    bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                } catch (IOException e) {
                    System.out.println("file,key:"+ossKey);
                    throw new RuntimeException(e);
                }
                try {
                    final String upload = ossHome.upload(ossKey, bytes, null);
                    ossHome.setAclPublic(ossKey);
                }catch (Exception ex){
                    System.out.println("key:"+ossKey);
                    ex.printStackTrace();
                }
            });
        }

        System.out.println(JSON.toJSONString(nameUrlMap));
    }


}
