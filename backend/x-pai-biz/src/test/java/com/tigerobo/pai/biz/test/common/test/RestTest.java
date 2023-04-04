package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import com.tigerobo.x.pai.biz.lake.LakeBlogCategoryService;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestTest {
    @Test
    public void formPostTest(){

        String url = "http://gbox9.aigauss.com:9500/infer";

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("text","鸣人");
        map.add("output_path","/mnt/xpai/application/test/4");

        map.add("modifiers", Arrays.asList("梵高","虚拟现实"));

        String filePath = "E:\\pai\\blog\\bigshot\\2.png";
        FileSystemResource fileSystemResource = new FileSystemResource(filePath);
        map.add("image",fileSystemResource);

        String s = RestUtil.postWithFile(url, map);

        System.out.println(s);

    }
    @Test
    public void blogCategoryTest(){
        LakeBlogCategoryService service = new LakeBlogCategoryService();
        String content = "The High Carbon Footprint of German Auto-Translation Models .New research into the carbon footprint created by machine learning translation models indicates that German may be the most carbon-intensive popular language to train, though it is not entirely clear why. The new report is intended to open up additional avenues of research into more carbon-efficient AI training methods, in the context of growing awareness of the extent to which machine learning systems consume electricity. The preprint paper is titled Curb Your Carbon Emissions: Benchmarking Carbon Emissions in Machine Translation, and comes from researchers at India’s Manipal Institute of Technology.The authors tested training times and calculated carbon emission values for a variety of possible inter-language translation models, and found ‘a notable disparity’ between time taken to translate the three most carbon-intensive language pairings, and the three most carbon-economical models.";


        List<String> candidate = Arrays.asList("Predictive Analytics", "Robotics", "Quantum Computing", "Bitcoin", "Neural Networks", "Workforce", "Metaverse", "Mobile", "NFT", "Health Care", "Natural Language", "Financial Services", "Retail", "Deep Learning", "Cyber Security", "Speech Recognition", "Transportation", "Blockchain", "AI Research", "Startups", "Image Recognition", "Emotion Recognition", "AI Software", "AI Cloud Services", "eCommerce", "IoT", "Energy", "Future of AI", "Machine Learning", "Self Driving Cars", "AI in Government", "Digital Assistants/Bots");
        List<String> list = service.callCategory(0, content,candidate);
        System.out.println(JSON.toJSONString(list));

    }
    @Test
    public void testPost() {

        String url = "http://gbox5.aigauss.com:9501/infer";

        String json = "{\"text_list\": [\"任总向顾客道千，保证不会出先损失\"]} ";

        RestTemplate rest = RestUtil.getRestTemplate();

        String result = rest.postForObject(url, json,String.class);

        System.out.println(result);
    }

    @Test
    public void getTest(){

        String uri = "http://10.0.19.103:8002/export/v1/roll";


        Map<String,Object> params = new HashMap<>();

        params.put("type","news.aiblog.930");
        params.put("app","c01");
        params.put("sign","e1fe4fc470b694994eba0f691c56a033");
        params.put("offset",0);
        params.put("size",100);

        String s = RestUtil.get(uri, params);
        System.out.println(s);

    }


    @Test
    public void htmlTest(){
//        String url = "https://ipo-oss.aigauss.com/news_image/0da07251f0a2b5a10bcf478b8c733adf/content.html";
//        String bizUrl = OSSApi.domainUrl +"biz/blog/source/80caa564b5ee2789914788a6cacdf4cc.html";
//        System.out.println(bizUrl);
//        String s = RestUtil.get(url, null);
//        String other = s.replaceFirst("<link type=\"text/css\".*?/>", "");
//        System.out.println(s);
//        System.out.println(other);
    }
    @Test
    public void postTest(){

        String url = "http://gbox9.aigauss.com:8000/query";

        Map<String,Object> map = new HashMap<>();


//        map.put("text_list", Arrays.asList("任总向顾客道千，保证不会出先损失"));
        map.put("question","地球是圆的吗?");

        long start = System.currentTimeMillis();
        try {
            String post = RestUtil.post(url, map);
            System.out.println(post);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("delta:"+(System.currentTimeMillis()-start));

    }
}
