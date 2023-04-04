package com.tigerobo.x.pai.biz.utils;

import com.tigerobo.x.pai.api.entity.Pair;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
public class ImageUtil {

    public static void main(String[] args) {
        String path = "D:\\tmp\\modifiers(2)\\images\\"+"Max Ernst.webp";

        final Pair<Integer, Integer> imageAround = getImageAround(path);
        System.out.println(imageAround);

    }

    public static Pair<Integer, Integer> getImageAround(String path) {
        File file = new File(path);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new FileInputStream(file));
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            Pair<Integer, Integer> pair = new Pair<>();
            pair.setV1(width);
            pair.setV2(height);
//            long size = file.length() / 1024;
            return pair;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public synchronized static String compress(File input,String outputPathName)throws IOException{
        long start = System.currentTimeMillis();
        Thumbnails.of(input)
                .scale(0.7)
                .outputQuality(1)
//                .size()
//                    .size(640, 480)
                .outputFormat("jpg").toFile(outputPathName);

        long deltaTime = System.currentTimeMillis() - start;

        log.info("outName:{},delta:{}ms",outputPathName, deltaTime);
        return outputPathName+".jpg";
    }


    public static int getImageSize(String path) {
        File file = new File(path);
        return (int)(file.length()/1024);

    }

    public static String png2jpg(String path,String outPath) throws IOException{


        long start = System.currentTimeMillis();

        if (outPath == null){
            String preName = path.substring(0, path.lastIndexOf("."));
            outPath = preName +".jpg";
        }

        try(InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(path)));
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(outPath)))
        ){
            // read image file
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            // create a blank, RGB, same width and height, and a white
            // background
            BufferedImage newBufferedImage = new BufferedImage(
                    bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            // TYPE_INT_RGB:创建一个RBG图像，24位深度，成功将32位图转化成24位
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
                    Color.WHITE, null);
            // write to jpeg file
//            ImageIO.write(newBufferedImage, "jpg", new File(outPath));

            ImageIO.write(newBufferedImage, "jpg", outputStream);
        }

        log.error("png-jpg:input:{},delta:{}",path,(System.currentTimeMillis()-start));
        return outPath;

    }


    public static byte[] compressPicForScale(
            byte[] imageBytes, long desFileSize, String imageId, Double quality) {
        if (imageBytes == null || imageBytes.length <= 0 || imageBytes.length < desFileSize * 1024) {
            return imageBytes;
        }
        long srcSize = imageBytes.length;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
            Thumbnails.of(inputStream).scale(1f).outputQuality(quality).toOutputStream(outputStream);
            imageBytes = outputStream.toByteArray();
            log.info(
                    "【图片压缩】imageId={} | 图片原大小={}kb | 压缩后大小={}kb",
                    imageId,
                    srcSize / 1024,
                    imageBytes.length / 1024);
        } catch (Exception e) {
            log.error("【图片压缩】msg=图片压缩失败!", e);
        }
        return imageBytes;
    }


    
}
