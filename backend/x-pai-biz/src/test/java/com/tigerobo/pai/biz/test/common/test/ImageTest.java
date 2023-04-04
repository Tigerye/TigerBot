package com.tigerobo.pai.biz.test.common.test;

import com.tigerobo.x.pai.biz.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ImageTest {


    @Test
    public void renameTest(){

        String path = "/tmp/oss2";

        File file = new File(path);

        for (File listFile : file.listFiles()) {
            String name = listFile.getName();
            if (!name.contains(".")){
                listFile.renameTo(new File(listFile.getAbsolutePath()+".png"));
            }
        }


    }

    @Test
    public void batchPng2jpgTest()throws Exception{


        String path = "/tmp/oss2";

        File file = new File(path);


        List<File> files = new ArrayList<>();

        for (File listFile : file.listFiles()) {
            if (listFile.getName().endsWith(".png")){
                files.add(listFile);
            }
        }


        files.stream().parallel().forEach(sub->{
            System.out.println(sub.getName()+"---start");
            try {
                ImageUtil.png2jpg(sub.getPath(),null);
            } catch (IOException e) {
                log.error("name:{},",file.getName(),e);
            }
        });
    }
    @Test
    public void png2jpgTest()throws Exception{

        String name = "2_22.png";
        name = "64_40.png";
        name = "fix.png";
        String path = "/tmp/local/"+name;

//
//        String preName = path.substring(0, path.lastIndexOf("."));
//        String out = preName +".jpg";
        ImageUtil.png2jpg(path,null);
//
//        String compressOut = preName+"_cpr";
//        ImageUtil.compress(new File(out),compressOut);
    }

    @Test
    public void imageTest() throws Exception {

        String path = "D:\\mnt\\photoFix\\mona_lisa.jpeg";
        File input = new File(path);
        BufferedImage image = ImageIO.read(input);

        File compressedImageFile = new File("/tmp/compressed_image.jpg");
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.05f);  // Change the quality value you prefer
        writer.write(null, new IIOImage(image, null, null), param);

        os.close();
        ios.close();
        writer.dispose();
    }


    @Test
    public void imageSizeTest() {
        String path = "";
        File file = new File(path);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new FileInputStream(file));
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();

            System.out.println(height + "\t" + width);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void thumbnailatorTest() throws IOException {

        List<Integer> ids = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            ids.add(i);
        }
        ids.parallelStream().forEach(id -> {
            deal(id);
        });
        ;
    }

    @Test
    public void simpleTest() throws Exception {
        String path = "D:\\mnt\\photoFix\\subnail\\64_40.png";
        String outPath = "/mnt/photoFix/subnail/60_40_out";

        File file = new File(path);
        System.out.println(file.exists());
        Thumbnails.of(file)
                .scale(0.7)
                .outputQuality(0.8)
//                .size()
//                    .size(640, 480)
                .outputFormat("jpg").toFile(new File(outPath));
        ;
    }

    private void deal(int id) {
        long start = System.currentTimeMillis();
        String name = "mona_lisa.jpeg";
        String img = "/mnt/photoFix/" + name;
        String outName = name.substring(0, name.lastIndexOf(".")) + 2;
        String outPath = "/mnt/photoFix/" + outName + "_" + id;
        int height = 2;
        int width = 2;
        try {
            Thumbnails.of(new File(img))
                    .scale(0.7)
                    .outputQuality(0.8)
//                .size()
//                    .size(640, 480)
                    .outputFormat("jpg").toFile(new File(outPath));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println(System.currentTimeMillis() - start);
    }
}
