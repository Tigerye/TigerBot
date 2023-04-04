package com.tigerobo.pai.biz.test.model;

import com.tigerobo.pai.common.util.DownloadUtil;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.utils.IdGenerator;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class RestTest extends BaseTest {


    @Autowired
    private EnvService envService;

    @Test
    public void envTest() {
        boolean prod = envService.isProd();
        System.out.println(prod);
    }

    @Test
    public void imageTest() throws Exception{
        String url = "http://gbox5.aigauss.com:9505/infer";
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        // 设置请求的格式类型
        headers.setContentType(type);
        String path = "E:\\pai\\blog\\bigshot\\5.png";
        String img = "https://x-pai.algolet.com/biz/bigshot/img/10.jpg";
        String id = IdGenerator.getId()+".png";
        String rootPath = "/tmp/";
        String s = DownloadUtil.downLoadByUrl(img, id, rootPath);
        FileUrlResource fileUrlResource = new FileUrlResource(img);
        FileSystemResource fileSystemResource = new FileSystemResource(s);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("images", fileSystemResource);
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        RestTemplate restTemplate = RestUtil.getRestTemplate();
        String output = restTemplate.postForObject(url, files, String.class);

        System.out.println(output);
    }

    @Test
    public void postTest() {


        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        long start = System.currentTimeMillis();
        ExecutorService readCachePool = new ThreadPoolExecutor(50, 50,
                120L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(1000));
        CountDownLatch latch = new CountDownLatch(list.size());
        list.stream().forEach(s -> {
            readCachePool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
//                        Thread.sleep(s*1l);
                        deal(s);
                    } catch (Exception e) {
                        log.warn("{},failed to getArticles", s, e);
                    } finally {
                        latch.countDown();
                    }
                }
            });

        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("getArticles error", e);
        }

        System.out.println("delta:" + (System.currentTimeMillis() - start));

    }

    private void deal(int i) {
        String url = "https://pai.tigerobo.com/x-pai-biz/aml/invoke?appId=ba78b5ba8483a0a40ad0b480b82b2916&apiKey=610762&accessToken=6dca8f4d1e0cef4cc5806a61cbf25f9f";

//        url = "https://pai.tigerobo.com/x-pai-serving/aml/invoke?appId=ba78b5ba8483a0a40ad0b480b82b2916&apiKey=610771&accessToken=6dca8f4d1e0cef4cc5806a61cbf25f9f";
        long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        map.put("text", "Unikey四周边缘保护系统为边缘提供舒适承托可将睡眠面积增大10%，增加整张床垫稳定性降低伴侣辗转反侧是的震荡传递");
        String post = RestUtil.post(url, map);
        System.out.println("i=" + i + "\t:" + (System.currentTimeMillis() - start) + "\t" + post);
    }


}
