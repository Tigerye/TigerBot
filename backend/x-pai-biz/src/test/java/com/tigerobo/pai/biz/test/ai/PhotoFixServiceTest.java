package com.tigerobo.pai.biz.test.ai;


import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.tigerobo.pai.biz.test.BaseTest;
import com.tigerobo.x.pai.api.ai.req.photo.PhotoFixPageReq;
import com.tigerobo.x.pai.api.ai.req.photo.PhotoFixReq;
import com.tigerobo.x.pai.api.ai.vo.PhotoFixVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixAiService;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixOperateService;
import com.tigerobo.x.pai.biz.ai.photo.PhotoFixService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import com.tigerobo.x.pai.biz.utils.ImageUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.ai.dao.AiPhotoFixDao;
import com.tigerobo.x.pai.dal.ai.entity.AiPhotoFixPo;
import com.tigerobo.x.pai.dal.ai.mapper.AiPhotoFixMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;
@Slf4j
public class PhotoFixServiceTest extends BaseTest {

    @Autowired
    private PhotoFixAiService photoFixAiService;

    @Autowired
    private PhotoFixService photoFixService;
    @Autowired
    private PhotoFixOperateService photoFixOperateService;

    @Autowired
    private AiPhotoFixMapper aiPhotoFixMapper;

    @Autowired
    private OSSHome ossHome;

    @Autowired
    private AiPhotoFixDao aiPhotoFixDao;



    @Test
    public void reqTest(){

        ThreadLocalHolder.setUserId(18);
        PageVo<PhotoFixVo> myList = photoFixService.getMyList(new PhotoFixPageReq());

        System.out.println(myList);

    }


    @Test
    public void appendColorTest(){

        String root = "D:\\mnt\\photoFix\\";
        String path = "D:\\mnt\\photoFix\\ansel_adams.jpg";
        path = "D:\\mnt\\photoFix\\mona_lisa.jpeg";
        path = root+"compressed_image.jpg";

        String out = photoFixAiService.appendColor(1, 1, path);
        System.out.println(out);

    }

    @Test
    public void initWidthTest(){

        List<AiPhotoFixPo> aiPhotoFixPos = aiPhotoFixMapper.selectAll();


        aiPhotoFixPos.forEach(po->{
            log.info("id-{},start",po.getId());
            try {
                refreshCompressImg(po);
            }catch (OutOfMemoryError error){
                log.error("OutOfMemoryError,id:{}",po.getId(),error);
            }catch (Exception ex){

                log.error("id:{}",po.getId(),ex);
            }
        });
    }

    private void refreshCompressImg(AiPhotoFixPo aiPhotoFixPo) {
        Integer processStatus = aiPhotoFixPo.getProcessStatus();
        if (processStatus==5&&StringUtils.isNotBlank(aiPhotoFixPo.getOutputPhoto())
                &&StringUtils.isBlank(aiPhotoFixPo.getCompressOutputPhoto())){
            String outputPhoto = aiPhotoFixPo.getOutputPhoto();
            String key = OssCombineUtil.getKeyByUrl(outputPhoto);

            String name = OssCombineUtil.getNameByUrl(outputPhoto);

            String path = "/tmp/osst/";
            String fileName = path + name;
            try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(fileName)))){
                ossHome.download(key,outputStream);
            }catch (IOException e) {
                log.error("id:{}", aiPhotoFixPo.getId(),e);
            }
            File imageFile = new File(fileName);
            if (imageFile.exists()){
                String compress = photoFixAiService.compress(fileName, null);
                if (StringUtils.isNotBlank(compress)){

                    AiPhotoFixPo update = new AiPhotoFixPo();
                    update.setId(aiPhotoFixPo.getId());
                    update.setCompressOutputPhoto(compress);
                    aiPhotoFixDao.update(update);
                }
            }
        }
    }


    private void deal() {
        PhotoFixReq req = new PhotoFixReq();

        String image = "https://x-pai.oss-cn-shanghai.aliyuncs.com/model/photo_fix/sample-01.jpg";
        req.setUserId(3);
        req.setInferUrl("http://gbox5.aigauss.com:9512/infer");
        req.setInputPhotoUrl(image);
        long reqId = System.currentTimeMillis() % 100000000;
        req.setReqId(reqId);
        photoFixAiService.userReq(req);
    }

    @Test
    public void taskTest(){

        boolean test = true;
        photoFixAiService.dealWaitDealList(test);
    }

    @Test
    public void deleteTest(){

        int id = 26;

        ThreadLocalHolder.setUserId(18);

        photoFixOperateService.delete(id);
    }
}
