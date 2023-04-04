package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.api.aml.engine.dto.lake.SingleLabelClassificationLakeReq;
import com.tigerobo.x.pai.api.biz.entity.Demand;
import com.tigerobo.x.pai.api.vo.biz.ModelApiReq;
import com.tigerobo.x.pai.api.dto.FileData;
import com.tigerobo.x.pai.biz.user.TokenService;
import com.tigerobo.x.pai.dal.ai.entity.AiStyleTransferDicPo;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.*;

public class JsonFileTest {

    @Test
    public void barTest(){


        Map<String,Object> pageDemoMap = new HashMap<>();

        List<String> images = Arrays.asList("https://x-pai.algolet.com/model/sample/image_label/umbrella.jpg",
                "https://x-pai.algolet.com/model/sample/image_label/strawberry.jpg",
                "https://x-pai.algolet.com/model/sample/image_label/sunflowler.jpg",
                "https://x-pai.algolet.com/model/sample/image_label/airplanes.jpg");
        List<Map<String,String>> list = new ArrayList<>();
        for (String s : images) {
            Map<String,String> map = new HashMap<>();
            int index = s.lastIndexOf("/");
            int lastIndex = s.lastIndexOf(".");
            String name = s.substring(index+1, lastIndex);
            map.put("label",name);
            map.put("imageUrl",s);
            list.add(map);
        }
        pageDemoMap.put("samples", list);
        System.out.println(JSON.toJSONString(pageDemoMap));
    }

    @Test
    public void categoryTest(){

        String json = "{\"text_list\": [\"At The Impact Seat, we invest in both innovation and in forward-thinking founders. We’re so thrilled to announce Arria NLG’s acquisition of our portfolio company Boost Sport AI — congrats to Boost’s phenomenal leaders: Mustafa Abdul-Hamid (now EVP, Arria) and Inga Nakhmanson (now EVP of Product Engineering, Arria)!In partnering with Arria, the two companies formed a strategic alliance artificial intelligence (AI) platform that empowered content creators to build and deploy data-driven sports stories. Arria Boost empowers brands to maximize audience engagement through personalized, 1:1 conversations delivered by technology experts, proprietary sports analytics, and industry-leading linguistics.\"],\n" +
                "\"candidate_labels\": [\"Predictive Analytics\", \"Robotics\", \"Quantum Computing\", \"Bitcoin\", \"Neural Networks\", \"Workforce\", \"Metaverse\", \"Mobile\", \"NFT\", \"Health Care\", \"Natural Language\", \"Financial Services\", \"Retail\", \"Deep Learning\", \"Cyber Security\", \"Speech Recognition\", \"Transportation\", \"Blockchain\", \"AI Research\", \"Startups\", \"Image Recognition\", \"Emotion Recognition\", \"AI Software\", \"AI Cloud Services\", \"eCommerce\", \"IoT\", \"Energy\", \"Future of AI\", \"Machine Learning\", \"Self Driving Cars\", \"AI in Government\", \"Digital Assistants/Bots\"]\n" +
                "}";

        JSONObject jsonObject = JSONObject.parseObject(json);

        JSONArray candidate_labels = jsonObject.getJSONArray("candidate_labels");

        for (Object candidate_label : candidate_labels) {
            System.out.println(candidate_label);
        }


    }

    @Test
    public void numTest(){
        int day = 20211015;

        System.out.println(day/10000);
        System.out.println(day%10000/100);
        System.out.println(day%10000%100);
    }

    @Test
    public void decodeTest(){
        String text = "signature=5dd347833355693024df41d1a06e5b6a&timestamp=1632749392808&content=%7B%22contractStatus%22%3A%22SIGNING%22%2C%22contractId%22%3A%222880100175668314968%22%2C%22bizId%22%3A%2212325%22%2C%22callbackType%22%3A%22SEND_SIGNING%22%7D";

        String decode = URLDecoder.decode(text);
        System.out.println(decode);
    }
    @Test
    public void initPhaseTest(){

        Demand.Phase[] values = Demand.Phase.values();

        List<Map<String,Object>> list = new ArrayList<>();

        for (Demand.Phase value : values) {
            if (value == Demand.Phase.CANCEL){
                continue;
            }
            Map<String,Object> map = new HashMap<>();
            map.put("label",value.getVal());
            map.put("value",value.getName());
            list.add(map);
        }
        System.out.println(JSON.toJSONString(list));
    }
    @Test
    public void modelCompleteTest(){
        String taskUuid = "a77051b1186913d2ce3fbf2e922d2ebe";
        Integer userId = 105;
        userId = 3;
        String apiUrl = "http://gbox8.aigauss.com:9503/ch_classify/classify";

        String style = "TEXT_TO_LABEL";
        String demo = "任总向顾客道千，保证不会出先损失";

        Map<String,Object> demoMap = new HashMap<>();
        demoMap.put("text",demo);

        ModelApiReq req = ModelApiReq.builder().taskUuid(taskUuid)
                .apiUrl(apiUrl)
                .demo(JSON.toJSONString(demoMap))
                .style(style)
                .userId(userId).build();

        String s = JSON.toJSONString(req);
        System.out.println(s);
    }
    @Test
    public void tokenDecodeTest(){
        TokenService tokenService = new TokenService();
        String token = "23057a95a41ac6294ed617bee26e4055";
        Integer id = tokenService.getUserIdByToken(token);
        System.out.println(id);
    }

    @Test
    public void tokenTest()throws Exception{
        String json = "{%22token%22:%22a505df879b6e81497007e8d56c80a88c-3039e05ca2c09cd8c2006559f75f5834-1000%22%2C%22uid%22:%22d2c1c4f00697ac39a4d8b9a4ca189d11%22%2C%22gid%22:%2294b351225d793977cc93897771e35209%22%2C%22group%22:{%22id%22:3%2C%22uuid%22:%2294b351225d793977cc93897771e35209%22%2C%22type%22:%22GROUP%22%2C%22name%22:%22%E7%8E%8B%E7%84%95%E5%8B%87%22%2C%22nameEn%22:%22Huanyong%20Wang%22%2C%22image%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22createTime%22:%222021-07-21%2022:27:09%22%2C%22updateTime%22:%222021-07-31%2016:16:49%22%2C%22account%22:%22huanyong.wang%22%2C%22scope%22:%22PERSONAL%22%2C%22logo%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22mobile%22:%2218301966691%22%2C%22owner%22:{%22id%22:3%2C%22uuid%22:%22d2c1c4f00697ac39a4d8b9a4ca189d11%22%2C%22type%22:%22USER%22%2C%22name%22:%22%E7%8E%8B%E7%84%95%E5%8B%87%22%2C%22account%22:%22huanyong.wang%22%2C%22avatar%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22wechat%22:%22ob4350nvI-DzmySB5QwQ6liCDPTc%22%2C%22roleType%22:1%2C%22deleted%22:false}%2C%22deleted%22:false}%2C%22user%22:{%22id%22:3%2C%22uuid%22:%22d2c1c4f00697ac39a4d8b9a4ca189d11%22%2C%22type%22:%22USER%22%2C%22name%22:%22%E7%8E%8B%E7%84%95%E5%8B%87%22%2C%22nameEn%22:%22Huanyong%20Wang%22%2C%22image%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22createTime%22:%222021-07-21%2022:27:09%22%2C%22updateTime%22:%222021-08-27%2019:35:56%22%2C%22account%22:%22huanyong.wang%22%2C%22mobile%22:%2218301966691%22%2C%22avatar%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22wechat%22:%22ob4350nvI-DzmySB5QwQ6liCDPTc%22%2C%22email%22:%22huanyong.wang@tigerobo.com%22%2C%22roleType%22:1%2C%22deleted%22:false}%2C%22role%22:%22OWNER%22%2C%22extras%22:null}";

        json = "{%22token%22:%22a505df879b6e81497007e8d56c80a88c-3039e05ca2c09cd8c2006559f75f5834-1000%22%2C%22uid%22:%22d2c1c4f00697ac39a4d8b9a4ca189d11%22%2C%22gid%22:%2294b351225d793977cc93897771e35209%22%2C%22group%22:{%22id%22:3%2C%22uuid%22:%2294b351225d793977cc93897771e35209%22%2C%22type%22:%22GROUP%22%2C%22name%22:%22%E7%8E%8B%E7%84%95%E5%8B%87%22%2C%22nameEn%22:%22Huanyong%20Wang%22%2C%22image%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22createTime%22:%222021-07-21%2022:27:09%22%2C%22updateTime%22:%222021-07-31%2016:16:49%22%2C%22account%22:%22huanyong.wang%22%2C%22scope%22:%22PERSONAL%22%2C%22logo%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22mobile%22:%2218301966691%22%2C%22owner%22:{%22id%22:3%2C%22uuid%22:%22d2c1c4f00697ac39a4d8b9a4ca189d11%22%2C%22type%22:%22USER%22%2C%22name%22:%22%E7%8E%8B%E7%84%95%E5%8B%87%22%2C%22account%22:%22huanyong.wang%22%2C%22avatar%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22wechat%22:%22ob4350nvI-DzmySB5QwQ6liCDPTc%22%2C%22roleType%22:1%2C%22deleted%22:false}%2C%22deleted%22:false}%2C%22user%22:{%22id%22:3%2C%22uuid%22:%22d2c1c4f00697ac39a4d8b9a4ca189d11%22%2C%22type%22:%22USER%22%2C%22name%22:%22%E7%8E%8B%E7%84%95%E5%8B%87%22%2C%22nameEn%22:%22Huanyong%20Wang%22%2C%22image%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22createTime%22:%222021-07-21%2022:27:09%22%2C%22updateTime%22:%222021-08-27%2019:35:56%22%2C%22account%22:%22huanyong.wang%22%2C%22mobile%22:%2218301966691%22%2C%22avatar%22:%22https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/user/avatar/tmp/1627699965924-cat.png%22%2C%22wechat%22:%22ob4350nvI-DzmySB5QwQ6liCDPTc%22%2C%22email%22:%22huanyong.wang@tigerobo.com%22%2C%22roleType%22:1%2C%22deleted%22:false}%2C%22role%22:%22OWNER%22%2C%22extras%22:null}";

        System.out.println(URLDecoder.decode(json));


    }

    @Test
    public void lengthTest(){
        String text = "http://x-pai.oss-cn-shanghai.aliyuncs.com/biz/submission/tmp/%E7%A0%94%E7%A9%B6%E6%8A%A5%E5%91%8A%E5%90%88%E8%A7%84%E9%9C%80%E6%B1%82%E4%B8%AD%E9%9C%80%E8%A6%81%E6%98%8E%E7%A1%AE%E7%9A%84%E9%97%AE%E9%A2%98-202109142209.docx";
        System.out.println(text.length());
    }
    @Test
    public void demandTest(){
        String s = UUID.randomUUID().toString().replaceAll("-","");
        System.out.println(s);
        System.out.println(s.length());
    }

    @Test
    public void test()throws Exception{
        String root = "E:\\pai\\engine\\202108\\";
        String evaluation = root + "evaluation.json";
        String result = root +"test_results.json";
        String readJson = read(evaluation);
        String resultJson = read(result);

        JSONObject jsonObject = JSON.parseObject(readJson);

        JSONObject resultObject = JSON.parseObject(resultJson);

        System.out.println();
    }


    @Test
    public void jsonStEST(){
        String json = "[[{\"label\":\"违规\",\"score\":0.9433},{\"label\":\"合规\",\"score\":0.041},{\"label\":\"高风险\",\"score\":0.0157}],[{\"label\":\"违规\",\"score\":0.8179},{\"label\":\"高风险\",\"score\":0.1401},{\"label\":\"合规\",\"score\":0.042}],[{\"label\":\"违规\",\"score\":0.9264},{\"label\":\"合规\",\"score\":0.058},{\"label\":\"高风险\",\"score\":0.0156}],[{\"label\":\"违规\",\"score\":0.923},{\"label\":\"合规\",\"score\":0.0611},{\"label\":\"高风险\",\"score\":0.0159}],[{\"label\":\"违规\",\"score\":0.9111},{\"label\":\"合规\",\"score\":0.0696},{\"label\":\"高风险\",\"score\":0.0193}],[{\"label\":\"高风险\",\"score\":0.8631},{\"label\":\"违规\",\"score\":0.1012},{\"label\":\"合规\",\"score\":0.0356}],[{\"label\":\"高风险\",\"score\":0.6702},{\"label\":\"违规\",\"score\":0.259},{\"label\":\"合规\",\"score\":0.0708}],[{\"label\":\"高风险\",\"score\":0.4809},{\"label\":\"合规\",\"score\":0.28},{\"label\":\"违规\",\"score\":0.2391}],[{\"label\":\"高风险\",\"score\":0.4687},{\"label\":\"违规\",\"score\":0.3747},{\"label\":\"合规\",\"score\":0.1566}],[{\"label\":\"高风险\",\"score\":0.8518},{\"label\":\"违规\",\"score\":0.1027},{\"label\":\"合规\",\"score\":0.0454}],[{\"label\":\"高风险\",\"score\":0.8887},{\"label\":\"违规\",\"score\":0.0684},{\"label\":\"合规\",\"score\":0.0429}],[{\"label\":\"高风险\",\"score\":0.8998},{\"label\":\"违规\",\"score\":0.0607},{\"label\":\"合规\",\"score\":0.0395}],[{\"label\":\"高风险\",\"score\":0.6783},{\"label\":\"合规\",\"score\":0.2184},{\"label\":\"违规\",\"score\":0.1033}],[{\"label\":\"高风险\",\"score\":0.9284},{\"label\":\"违规\",\"score\":0.0399},{\"label\":\"合规\",\"score\":0.0317}],[{\"label\":\"高风险\",\"score\":0.8075},{\"label\":\"违规\",\"score\":0.1168},{\"label\":\"合规\",\"score\":0.0757}],[{\"label\":\"高风险\",\"score\":0.9254},{\"label\":\"违规\",\"score\":0.0471},{\"label\":\"合规\",\"score\":0.0276}],[{\"label\":\"高风险\",\"score\":0.8624},{\"label\":\"违规\",\"score\":0.0993},{\"label\":\"合规\",\"score\":0.0383}],[{\"label\":\"高风险\",\"score\":0.9067},{\"label\":\"违规\",\"score\":0.061},{\"label\":\"合规\",\"score\":0.0323}],[{\"label\":\"高风险\",\"score\":0.9105},{\"label\":\"违规\",\"score\":0.0557},{\"label\":\"合规\",\"score\":0.0337}],[{\"label\":\"合规\",\"score\":0.949},{\"label\":\"高风险\",\"score\":0.0313},{\"label\":\"违规\",\"score\":0.0197}],[{\"label\":\"合规\",\"score\":0.973},{\"label\":\"高风险\",\"score\":0.02},{\"label\":\"违规\",\"score\":0.0071}],[{\"label\":\"合规\",\"score\":0.8545},{\"label\":\"违规\",\"score\":0.1273},{\"label\":\"高风险\",\"score\":0.0182}],[{\"label\":\"合规\",\"score\":0.9869},{\"label\":\"违规\",\"score\":0.0069},{\"label\":\"高风险\",\"score\":0.0061}],[{\"label\":\"合规\",\"score\":0.6103},{\"label\":\"高风险\",\"score\":0.3092},{\"label\":\"违规\",\"score\":0.0805}]]\n";

        JSONArray objects = JSON.parseArray(json);

        for (int i = 0; i < objects.size(); i++) {
            System.out.println(i+JSON.toJSONString(objects.get(i)));
        }

    }


    @Test
    public void jsonTest(){
        List<FileData> dataList = new ArrayList<>();
        FileData fileData = new FileData();
        fileData.setFilePath("http://x-pai.oss-cn-shanghai.aliyuncs.com/engine/test/hm.csv");
        fileData.setName("hm.csv");
        dataList.add(fileData);

        String json = JSON.toJSONString(dataList);
        System.out.println(json);

        List<FileData> parseList = JSON.parseArray(json, FileData.class);

        System.out.println(parseList.get(0).getFilePath());
    }
    @Test
    public void jsonFileReadTest()throws Exception {
        String name = "test_results_v2.json";
        name = "test_results_v3.json";
        name = "\\risk\\test_results_v2.json";
//        name = "\\risk\\0831\\test_results.json";
        String path = "E:\\pai\\engine\\demo\\" + name;

        String read = read(path);

        JSONObject jsonObject = JSON.parseObject(read);
        System.out.println(read.length());
    }

    private String read(String path) throws Exception {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
            String line = null;
            int i=0;
            while ((line = reader.readLine())!=null){
                builder.append(line);
                i++;
            }
            System.out.println("line:"+i);
        }
        return builder.toString();
    }

    @Test
    public void jsonReqTest(){
        SingleLabelClassificationLakeReq req = new SingleLabelClassificationLakeReq();
        System.out.println(JSON.toJSONString(req));
    }
}
