package com.tigerobo.pai.admin.controller;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.model.OSSObject;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.oss.OSSHome;
import com.tigerobo.x.pai.biz.oss.OssCombineUtil;
import com.tigerobo.x.pai.biz.utils.ContentTypeUtil;
import com.tigerobo.x.pai.biz.utils.Md5Util;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/common/")
@Api(value = "通用接口", tags = "通用接口")
public class CommonController {

    @Autowired
    private OssService ossService;

    @Autowired
    private OSSHome ossHome;

    @AdminAuthorize
    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<String> upload(@RequestParam(value="file") MultipartFile file,String bizName)  {
        Preconditions.checkArgument(file != null && !file.isEmpty(), "上传文件为空");
        String name = "";
        try {
            name = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            if (bytes.length==0){
                throw new IllegalArgumentException("文件内容为空");
            }
            String suffix = ContentTypeUtil.getSuffix(file.getContentType(), name);
            if (!StringUtils.isEmpty(name)){
                log.warn("上传文件名为空");
                String key = Md5Util.getMd5ByBytes(bytes);
                name = key+suffix;
            }
            String fileUrl = ossService.uploadFile(bytes, name,null,bizName);
            ResultVO<String> resultVO = new ResultVO<>();
            resultVO.setData(fileUrl);
            return resultVO;
        }catch (Exception ex){
            log.error("name:{}",name,ex);
        }
        return ResultVO.fail("上传文件失败");
    }

//    @Authorize
    @AdminAuthorize
    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "uploadPub", method = RequestMethod.POST)
    @ResponseBody
    public ResultVO<String> uploadPub(@RequestParam(value="file") MultipartFile file,String bizName)  {
        Preconditions.checkArgument(file != null && !file.isEmpty(), "上传文件为空");
        String name = "";
        try {
            name = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            if (bytes.length==0){
                throw new IllegalArgumentException("文件内容为空");
            }
            String suffix = ContentTypeUtil.getSuffix(file.getContentType(), name);
            if (!StringUtils.isEmpty(name)){
                log.warn("上传文件名为空");
                String key = Md5Util.getMd5ByBytes(bytes);
                name = key+suffix;
            }
            String fileUrl = ossService.uploadFilePub(bytes, name,bizName);
            ResultVO<String> resultVO = new ResultVO<>();
            resultVO.setData(fileUrl);
            return resultVO;
        }catch (Exception ex){
            log.error("name:{}",name,ex);
        }
        return ResultVO.fail("上传文件失败");
    }

    @ApiOperation(value = "图片下载")
    @AdminAuthorize
    @RequestMapping(value="/download/img", method = RequestMethod.POST)
    public ResultVO downloadImg(HttpServletResponse response,
                                @NotNull @RequestBody UrlReq req ) throws IOException {

        Validate.isTrue(req.getBizId()!=null&&req.getBizType()!=null,"参数不正确");

        log.info("download,userId:{},url:{}", ThreadLocalHolder.getUserId(), JSON.toJSONString(req));
//        URL url = new URL(req.getUrl());
        String key = OssCombineUtil.getKeyByUrl(req.getUrl());
        OSSObject ossObject = ossHome.get(key);
        InputStream download = ossObject.getObjectContent();

        ServletOutputStream outputStream = response.getOutputStream();

        String contentType = ossObject.getObjectMetadata().getContentType();
        if (!StringUtils.isEmpty(contentType)){
            response.setContentType(contentType);
        }

        int index = key.lastIndexOf("/");
        String fileName = key;
        if (index>0){
            fileName = key.substring(index+1);
        }
        response.setHeader("Content-disposition","attachment;filename="+fileName);

        IOUtils.copy(download, outputStream);

        return ResultVO.success();
    }

    @Data
    private static class UrlReq{

        Integer bizType;
        String bizId;
        String url;

    }
}
