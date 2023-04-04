package com.tigerobo.pai.biz.test.service.test.oss;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.io.IOService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.biz.oss.OssCacheService;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import com.tigerobo.x.pai.biz.utils.ImageUtil;
import com.tigerobo.x.pai.dal.config.oss.OSSApi;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OssBaseTest extends BaseTest {

    @Autowired
    OSSHome ossHome;

    @Autowired
    private OSSApi ossApi;
    @Autowired
    private OssService ossService;

    @Autowired
    private OssCacheService ossCacheService;

    @Autowired
    private IOService ioService;
    @Test
    public void resizeTest()throws Exception{
        String key = "model/photo_fix/20220125/60_35.png";


        int orW= 194;int orH = 259;

        int rate = 3;
        int height = (int)(orH*3);
        int width = (int)(orW*rate);

        key = "model/sample/app/mona_lisa.jpeg";

        key = "model/photo_fix/20220126/27_50.png";
        String out = "/tmp/oss"+key.substring(key.lastIndexOf("/"));

        try {
            ossHome.resizeImg(key, new File(out), width, height);
        }catch (Exception ex){
            System.out.println("异常");
        }
    }

    @Test
    public void downloadTest()throws IOException{

        String url = "https://x-pai.algolet.com/model/photo_fix/20220126/64_40.png?Expires=1800856051&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=a3o2Bad2O%2F%2F4ZvMpicCr4n9CPi0%3D";
        String key = OssCombineUtil.getKeyByUrl(url);
        String name = key.substring(key.lastIndexOf("/"));

        String outPath = "/mnt/photoFix/id/40.png";

        File file = new File(outPath);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(FileOutputStream outputStream = new FileOutputStream(new File(outPath))){
            ossHome.download(key,outputStream);
        }

        String comOutPath = "/mnt/photoFix/id/40_com.png";

        File outFile = new File(comOutPath);
        if (!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        ImageUtil.compress(file,comOutPath);
    }
//    @Test
//    public void imgTest()throws Exception{
//        String key = "model/photo_fix/20220125/60_35.png";
//
//        key = "model/sample/app/mona_lisa.png";
//        compressKey(key);
//
//
//    }

    @Test
    public void ossTokenTest()throws Exception{

        String role = "dataset-upload";
        OSSApi.OssToken ossToken = ossApi.ossToken(role);
        System.out.println(JSON.toJSONString(ossToken));

    }

    @Test
    public void writeLocalTest()throws Exception{

        String path = "https://x-pai.algolet.com/application/model/evaluation/result/bf78e192c11b8e138694149fb1bdeee9-1639728445030.xlsx?OSSAccessKeyId=sauBoVUnLI0kHXam&Expires=1642065138&Signature=0eCFGLc24hbRSkSi%2FkRgLreDY74%3D";

        path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/a-202201131401.xlsx";
        path = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/%E6%96%B0%E5%BB%BA%20Microsoft%20Excel%20%E5%B7%A5%E4%BD%9C%E8%A1%A8-202109271209.csv";
        String decode = URLDecoder.decode(path);
        System.out.println(decode);
        String s = ioService.writeXlsxFile(decode);

        System.out.println(s);

    }

    @Test
    public void test() {

        String key = "biz/dataset/tmp/README-1630046728449.md";

        ossHome.setAclPrivate(key);

        String url = ossHome.getUrl(key);
        System.out.println(url.replaceFirst("-internal", ""));

    }

    @Test
    public void setPrivateTest(){
        String path = "biz/dataset/tmp/";
        path = "biz/dataset/5ffd714fc9dd4d6a145de78237dbd665/";
        path = "aml/evaluation/result/";
        path = "application/model/evaluation/result/";
        path = "common/file/tmp/aml/evaluation/label/";
        path = "engine/test/";

        path = "hand/batch/";
        path = "tmp/aml/evaluation/label/";

        path = "biz/submission/tmp/";
        path = "biz/evaluation/format/";
        path = "biz/evaluation/tmp/";
        path = "biz/evaluation/result/";

        dealDirPrivate(path);
    }
    @Test
    public void setPublicTest(){
        String path = "biz/user/avatar/tmp/";//头像
        path = "model/img/art_image/";
        dealDirPublic(path);
    }

    @Test
    public void setPub(){

        String path = "model/style_transfer/base/";
        dealDirPublic(path);
    }


    @Test
    public void setBigShot(){
        String path = "biz/bigshot/academic/img/";
        path = "biz/bigshot/noacademic/img";


        for (int i = 900; i <1000; i++) {
            System.out.println("i="+i+"-start");
            dealDirPublic(path+i);
        }

    }

    @Test
    public void getListTest(){
        String path = "model/style_transfer/style_image/";

        path="search/dataset/image/sample/images/";
        final List<OSSObjectSummary> list = ossHome.getList(path);
        List<HashMap<String,Object>> targetList = new ArrayList<>();
        int id = 1;
        for (OSSObjectSummary ossObjectSummary : list) {
            String url = this.ossHome.getBaseUrl(ossObjectSummary.getKey());

            HashMap<String,Object> map = new HashMap<>();
            map.put("id",id++);
            map.put("styleImage",url);

            targetList.add(map);

        }
        System.out.println(JSON.toJSONString(targetList));
    }

    @Test
    public void setNotAcademicBigShot(){
        String path = "biz/bigshot/academic/img/";
        path = "biz/bigshot/noacademic/img/";


        for (int i = 100; i <1000; i++) {
            System.out.println("i="+i+"-start");
            dealDirPublic(path+i);
        }

    }

    @Test
    public void getOssUrlTest(){

        String key = "biz/blog/source/9875f9447dd4a0f71a5d8caafbd0f028.html";

        int second = 4*60;
        String urlWithTime = ossCacheService.getPrivateKeyUrl(key,second);

        System.out.println(urlWithTime);

    }


    @Test
    public void setBankPublic() {
        String path;
        path = "open_qa/ls_bank/";
        String[] arr = new String[]{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};

        for (String s : arr) {
            dealDirPublic(path+s);
        }
    }


    @Test
    public void  dealOneFile(){
        String key = "tmp/aml/evaluation/label/黑产.xlsx";
        ossHome.setAclPrivate(key);
    }

    private void dealDirPrivate(String path) {
        List<OSSObjectSummary> ossObjectSummaryList = ossHome.getList(path);
        for (OSSObjectSummary ossObjectSummary : ossObjectSummaryList) {
            ossHome.setAclPrivate(ossObjectSummary.getKey());
        }
    }

    private void dealDirPublic(String path) {
        List<OSSObjectSummary> ossObjectSummaryList = ossHome.getList(path);
        for (OSSObjectSummary ossObjectSummary : ossObjectSummaryList) {
            try {
                ossHome.setAclPublic(ossObjectSummary.getKey());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
