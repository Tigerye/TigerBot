package com.tigerobo.pai.biz.test.service.test.blog;

import com.tigerobo.pai.common.util.DownloadUtil;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.constants.OssConstant;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.dal.biz.dao.blog.BlogTwitterAcademicDao;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterAcademicPo;
import com.tigerobo.x.pai.dal.biz.entity.blog.BlogTwitterNoAcademicPo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class UrlTransferServiceTest extends BaseTest {
    private String domain= OssConstant.domainUrl;
    @Autowired
    private BlogTwitterAcademicDao blogTwitterAcademicDao;

    @Autowired
    private OssService ossService;


    @Test
    public void transferImgTest(){
        List<BlogTwitterAcademicPo> allImg = blogTwitterAcademicDao.getAll();
        String savePath = "/tmp/blog/img/";
        if(!CollectionUtils.isEmpty(allImg)){
            for (BlogTwitterAcademicPo po : allImg) {
                String imgId = po.getId().toString()+".jpg";
                String imgUrl = po.getTwitterImg();
                try {
                    DownloadUtil.downLoadByUrl(imgUrl,imgId,savePath,true);
                    log.info("twitterId,imgId={}",imgId);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        }

    }

    @Test
    public void upLoadImg(){
        List<BlogTwitterAcademicPo> allImg = blogTwitterAcademicDao.getAll();
        String savePath = "/tmp/blog/img/";
        if (!CollectionUtils.isEmpty(allImg)) {
            for (BlogTwitterAcademicPo po : allImg) {
                String name = po.getCrawlerId()+".jpg";
                String img=savePath+name;
                byte[] bytes = new byte[0];
                try {
                    bytes = Files.readAllBytes(new File(img).toPath());
                    String key = "biz/bigshot/academic/img/"+name;
                    String s = ossService.uploadImg(bytes, key);
                } catch (IOException e) {
                    log.info("imgID={},e={}",name,e);
                }

            }
        }

    }
    @Test
    public void paraUploadImgTest(){
        List<BlogTwitterAcademicPo> allImg = blogTwitterAcademicDao.getAll();
        String savePath = "/tmp/blog/img/";

        List<Integer> ids = allImg.stream().map(po -> po.getId()).collect(Collectors.toList());
        ids.parallelStream().forEach(id->{
            String name=id+".jpg";
            String img=savePath+name;
            byte[] bytes = new byte[0];
            try {
                bytes = Files.readAllBytes(new File(img).toPath());
                String key = "biz/bigshot/academic/img/"+name;
                String s = ossService.uploadImg(bytes, key);
                Thread.sleep(200);
            } catch (Exception e) {
                log.info("imgID={}",id);
            }
        });
    }
    @Test
    public void paraUploadNoAcademicImgTest(){
        List<BlogTwitterNoAcademicPo> allImg = blogTwitterAcademicDao.getAllNoAcademic();

        String savePath = "/tmp/blog/noAcademicImg/";
        allImg.parallelStream().forEach(po->{
            String name=po.getTwitterId().toString()+".jpg";
            String img=savePath+name;
            byte[] bytes = new byte[0];
            try {
                DownloadUtil.downLoadByUrl(po.getTwitterImg(),name,savePath,true);
                bytes = Files.readAllBytes(new File(img).toPath());
                String key = "biz/bigshot/noacademic/img/"+name;
                String s = ossService.uploadImg(bytes, key);
                Thread.sleep(100);
            } catch (Exception e) {
                log.info("imgID={}",name);
            }
        });



    }
    @Test
    public void img2db(){
        List<BlogTwitterAcademicPo> allImg = blogTwitterAcademicDao.getAll();
        String savePath = "/tmp/blog/img/";

        List<Integer> ids = allImg.stream().map(po -> po.getId()).collect(Collectors.toList());
        ids.parallelStream().forEach(id->{
            String name=id+".jpg";
            String img=savePath+name;
            byte[] bytes = new byte[0];
            try {
                bytes = Files.readAllBytes(new File(img).toPath());
                if(bytes.length>0){
                    String key =domain+ "biz/bigshot/academic/img/"+name;
                        blogTwitterAcademicDao.updateAcademicUrl(id,key);
                    Thread.sleep(10);
                }

            } catch (Exception e) {
                log.info("imgID={}",id);
            }
        });
    }
    @Test
    public void noacdemicimg2db(){
        List<BlogTwitterNoAcademicPo> allImg = blogTwitterAcademicDao.getAllNoAcademic();
        String savePath = "/tmp/blog/noAcademicImg/";

        List<Long> ids = allImg.stream().map(po -> po.getTwitterId()).collect(Collectors.toList());
        ids.parallelStream().forEach(id->{
            String name=id+".jpg";
            String img=savePath+name;
            byte[] bytes = new byte[0];
            try {
                bytes = Files.readAllBytes(new File(img).toPath());
                if(bytes.length>0){
                    String key =domain+ "biz/bigshot/noacademic/img/"+name;
                    blogTwitterAcademicDao.updateNoAcademicUrl(id,key);
                    Thread.sleep(10);
                }

            } catch (Exception e) {
                log.info("imgID={}",id);
            }
        });
    }
    @Test
    public void threadImgTransferTest(){
        ThreadPoolExecutor executor=new ThreadPoolExecutor(10, 20, 1L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    }
}
