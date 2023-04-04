package com.tigerobo.pai.common.util;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;


public class DownloadUtil {


    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */

    public static String downLoadByUrl(String urlStr, String fileName, String savePath)throws IOException{
        return downLoadByUrl(urlStr,fileName,savePath,false);
    }
    public static String downLoadByUrl(String urlStr, String fileName, String savePath,boolean useProxy) throws IOException {
        if (urlStr==null||urlStr.isEmpty()||!urlStr.toLowerCase().startsWith("http")){
            return null;
        }
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        String filePath = savePath + fileName;
        File file = new File(filePath);
        if (file.exists()&&file.length()>3_1000){
            return filePath;
        }

        URL url = new URL(urlStr);
//
        HttpURLConnection conn = null;
        if (useProxy) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.01", 1080));
            conn = (HttpURLConnection) url.openConnection(proxy);
        }else {
            conn = (HttpURLConnection) url.openConnection();
        }


        //设置超时间为3秒
        conn.setConnectTimeout(5 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);
        //文件保存位置

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
//        System.out.println("info:" + getUrl + " download success");
        return filePath;

    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) throws Exception{

        String url = "https://passport-img.oss-cn-hangzhou.aliyuncs.com/pdf_img/base/wei_senti.zip";

//        url = "https://pbs.twimg.com/profile_images/841385099799085056/R1iX4QGX_400x400.jpg";
        url="http://pbs.twimg.com/profile_images/1458888192606806017/q1ZiUHN6_400x400.jpg";
        url = "http://pbs.twimg.com/profile_images/246833051/mz_400x400.jpg";
//        url = "https://x-pai.oss-cn-shanghai.aliyuncs.com/biz/evaluation/tmp/666-202111021711%20(2)-202111151911.csv";
        String name = url.substring(url.lastIndexOf("/")+1);

        url = "https://x-pai.algolet.com/model/spatio_action/1313966051004631.mp4";
        long startTime = System.currentTimeMillis();
        String workPath = "/tmp/20220602/";
        String fileName = "1313966051004631.mp4";
        downLoadByUrl(url,fileName,workPath,true);
        System.out.println("delta:"+(System.currentTimeMillis()-startTime));
    }




}
