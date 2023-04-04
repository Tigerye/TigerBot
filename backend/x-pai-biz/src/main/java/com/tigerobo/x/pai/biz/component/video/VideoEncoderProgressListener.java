//package com.tigerobo.x.pai.biz.component.video;
//
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import ws.schild.jave.EncoderProgressListener;
//import ws.schild.jave.MultimediaInfo;
//
///**
// * @author:Wsen
// * @time: 2021/1/14
// **/
//@Getter
//@Slf4j
//public class VideoEncoderProgressListener implements EncoderProgressListener {
//
//    long duration;
//    int height;
//    int width;
//    @Override
//    public void sourceInfo(MultimediaInfo info) {
//        if (info!=null){
//            duration = info.getDuration();
//            if (info.getVideo()!=null&&info.getVideo().getSize()!=null){
//                height = info.getVideo().getSize().getHeight();
//                width = info.getVideo().getSize().getWidth();
//            }
//        }
//    }
//
//    @Override
//    public void progress(int permil) {
////        log.info("VideoEncoderProgressListener#progress--->{}",permil);
//    }
//
//    @Override
//    public void message(String message) {
//
////        log.info("message:{}",message);
//    }
//}
