package com.tigerobo.pai.biz.test.service.test.oss;

import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.dal.config.oss.OSSApi;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OssTest extends BaseTest {

    @Autowired
    private OSSApi ossApi;

    @Autowired
    private OSSHome ossHome;

    @Autowired
    private OssService ossService;


    @Test
    public void ossUrlTest(){
        String url = "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJSeJaqXKia99pPcZMWSSR002XaZBdhsjEibAdfeCRMZLjxX1TMsvQ7LZkp00f2YgTEhk1acqvzS47Q/132";
        String key = "user/avatar/3/1.png";
        final String s = ossService.uploadUrlFile(url, key,true);
        System.out.println(s);
    }

    @Test
    public void urlResizeTest(){

        String key = "";
    }
    @Test
    public void downloadTest()throws Exception{
        String url = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/risk-1630493739764.csv";


        InputStream download = ossHome.download(new URL(url));

        String path = "/tmp/oss/is/"+url.substring(url.lastIndexOf("/")+1);

        File file = new File(path);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        Files.copy(download,Paths.get(path));

    }
    @Test
    public void uploadExcelTest()throws Exception{
        String path = "/mnt/xpai/黑产.xlsx";
        String key = "tmp/aml/"+new File(path).getName();
        String s = ossService.uploadXls(Files.readAllBytes(Paths.get(path)), key);
        System.out.println(s);
    }

    @Test
    public void uploadImgFileListTest()throws Exception{

        String path = "E:\\pai\\blog\\site\\logo\\1103";
        File file = new File(path);

        for (File listFile : file.listFiles()) {

            byte[] bytes = Files.readAllBytes(listFile.toPath());
            String url = ossService.uploadFile(bytes, listFile.getName());
            System.out.println(listFile.getName());
            System.out.println("url\t"+url);

        }
    }
    @Test
    public void test(){

    }



    @Test
    public void ossTest(){

        String path = "test/blog/test3.html";
        String content = "<p>内容test3</p>";
        String s = ossService.uploadHtml(content.getBytes(), path);
        System.out.println(s);
    }

    @Test
    public void uploadTest(){

        String base = "test/b/";

        List<Integer> ids = new ArrayList<>(20_000);
        for (int i = 0; i < 20_000; i++) {
            ids.add(i);

        }
        ids.parallelStream().forEach(i->{
            System.out.println(i);

            String ossPath = base +i+".html";
            ossApi.upload(ossPath,String.valueOf(i).getBytes(),"text/html; charset=utf-8");
        });
    }

    @Test
    public void updateTest(){

        String path = "test/blog/test2.html";
        String content = "<p>内容test</p>";
        OSSObject upload = ossApi.upload(path, content.getBytes(),"text/html; charset=utf-8" );
        System.out.println(upload.getResponse().getUri());
    }
}
