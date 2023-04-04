//package com.tigerobo.x.pai.biz.component.video;
//
//import com.alibaba.fastjson.JSON;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import ws.schild.jave.*;
//
//import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.List;
//
///**
// * @author:Wsen
// * @time: 2021/1/14
// **/
//@Slf4j
//@Component
//public class VideoProcessor {
////    @Value("${video.path.snap.img}")
////    private String snapImgPath;
////    @Value("${video.path.encode}")
////    private String encodePath;
////
////    public VideoProcessor(){
////        System.setProperty("java.io.tmpdir","/data/video");
////    }
//
//    public VideoProcessInfo getInfoByUrl(String url)throws Exception{
//        URL urlSource = new URL(url);
//        MultimediaObject multimediaObject = new MultimediaObject(urlSource);
//        return getVideoProcessInfo(multimediaObject);
//    }
//
//    public VideoProcessInfo getInfoByFile(File file)throws Exception{
//
//        MultimediaObject multimediaObject = new MultimediaObject(file);
//        return getVideoProcessInfo(multimediaObject);
//    }
//    private VideoProcessInfo getVideoProcessInfo(MultimediaObject multimediaObject) throws EncoderException {
//        MultimediaInfo info = multimediaObject.getInfo();
//        if (info == null){
//            return null;
//        }
//        VideoProcessInfo baseInfo = new VideoProcessInfo();
//        baseInfo.setDuration(info.getDuration());
//        VideoInfo videoInfo= info.getVideo();
//        if (videoInfo!=null){
//            VideoSize size = videoInfo.getSize();
//            if (size!=null){
//                baseInfo.setWidth(size.getWidth());
//                baseInfo.setHeight(size.getHeight());
//            }
//            int bitRate = videoInfo.getBitRate();
//            baseInfo.setVideoBitRate(bitRate);
//        }
//        String format = info.getFormat();
//        baseInfo.setVideoFormat(format);
//        return baseInfo;
//    }
//
//    public VideoProcessInfo encode(String url,String targetPath)throws Exception{
//        return encode(url,targetPath,"mov");
//    }
//    public VideoProcessInfo encode(String url,String targetPath,String format)throws Exception{
//        File target = new File(targetPath);
//        if(!target.getParentFile().exists()){
//            target.getParentFile().mkdirs();
//        }
//        MultimediaObject multimediaObject = getUrlMultimediaObject(url);
//
//        return encode(multimediaObject, target, format);
//    }
//
//
//    public VideoProcessInfo fileEncode(String inputFile,String targetPath)throws Exception{
//        File target = new File(targetPath);
//        if(!target.getParentFile().exists()){
//            target.getParentFile().mkdirs();
//        }
//        MultimediaObject multimediaObject = new MultimediaObject(new File(inputFile));
//
//        return encode(multimediaObject, target, "mov");
//    }
//    private VideoProcessInfo encode(MultimediaObject multimediaObject,File target,String format) throws EncoderException {
//        EncodingAttributes attrs = new EncodingAttributes();
//        attrs.setFormat(format);
//
//        attrs.setAudioAttributes(new AudioAttributes());
//
//        VideoAttributes video = new VideoAttributes();
//        video.setQuality(1);
//        MultimediaInfo info = multimediaObject.getInfo();
//        if (info !=null&&info.getVideo()!=null){
//            if (info.getVideo().getBitRate()>0){
//                int bitRate = info.getVideo().getBitRate();
//                video.setBitRate(bitRate);
//            }
//        }
//
//        attrs.setVideoAttributes(video);
//        //Encode
//        Encoder encoder = new Encoder();
//        VideoEncoderProgressListener listener = new VideoEncoderProgressListener();
//
//
////        System.out.println("bitRate:"+bitRate);
//
//        encoder.encode(multimediaObject, target, attrs,listener);
//
//        VideoProcessInfo processInfo = new VideoProcessInfo();
//        processInfo.setDuration(listener.getDuration());
//        processInfo.setWidth(listener.getWidth());
//        processInfo.setHeight(listener.getHeight());
//        return processInfo;
//    }
//
//    private MultimediaObject getUrlMultimediaObject(String url) throws MalformedURLException {
//        URL urlSource = new URL(url);
//        return new MultimediaObject(urlSource,false);
//    }
//
//
//
//    @Data
//    public static class VideoProcessInfo {
//        private Integer width;
//        private Integer height;
//        private Long duration;
//        private List<String> picList;
//        //    private List<File> picFileList;
//        private String videoFormat;
//        private Integer videoBitRate;
//        private File encodeFile;
//        private String targetUrl;
//    }
//
//    public static void main(String[] args) throws Exception{
//
//        VideoProcessor processor = new VideoProcessor();
//        String url = "https://oss.hubokan.com/video/hand/a4e9e8065fb7cf2ccacc1282e9b01c19.MOV";
//        String targetPth = "E:\\business\\2021\\video\\encode\\"+"a4e9e8065fb7cf2ccacc1282e9b01c19.mp4";
////        processor.encode(url,targetPth);
//
//        String filePath = "D:\\tmp\\0602\\1313966051004631_1.mp4";
//
//        final VideoProcessInfo processInfo = processor.getInfoByFile(new File(filePath));
//
//        System.out.println(JSON.toJSONString(processInfo));
//
//        String inputPath = "C:\\Users\\why19\\Desktop\\tmp\\0529\\1313966051004631.mp4";
//
//        String outPath = "/tmp/0602/1313966051004631_v2.mp4";
//
//        final VideoProcessInfo processInfo1 = processor.fileEncode(inputPath, outPath);
//
//        System.out.println(JSON.toJSONString(processInfo1));
//
//
//    }
//}
