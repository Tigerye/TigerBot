package com.tigerobo.pai.admin.controller;

import com.tigerobo.x.pai.api.admin.auth.AdminAuthorize;
import com.tigerobo.x.pai.api.admin.req.ai.ArtImageAdminQueryReq;
import com.tigerobo.x.pai.api.admin.req.ai.ArtTextListReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.auth.aspect.Authorize;
import com.tigerobo.x.pai.api.config.ResponseIgnore;
import com.tigerobo.x.pai.api.dto.admin.ai.ArtImageDto;
import com.tigerobo.x.pai.api.vo.IdReqVo;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.ResultVO;
import com.tigerobo.x.pai.biz.admin.ai.ArtImageAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@Api(value = "艺术图", tags = "艺术图")
@RequestMapping("/artImage/")
public class ArtImageAdminController {
    @Autowired
    private ArtImageAdminService artImageAdminService;
//
//    @AdminAuthorize
//    @ApiOperation(value = "示例列表")
//    @RequestMapping(value = "/getPublishList", method = POST)
//    public PageVo<ArtImageDto> getPublishList(HttpServletRequest request, @Valid @RequestBody ArtImageAdminQueryReq req) {
//        return artImageAdminService.query(req);
//    }


    @AdminAuthorize
    @ApiOperation(value = "列表")
    @RequestMapping(value = "/getList", method = POST)
    public PageVo<ArtImageDto> getList(HttpServletRequest request, @Valid @RequestBody ArtImageAdminQueryReq req) {
        return artImageAdminService.query(req);
    }



    @AdminAuthorize
    @ApiOperation(value = "offline")
    @RequestMapping(value = "/offline", method = POST)
    public ResultVO offline(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        artImageAdminService.offline(reqVo.getId());

        return ResultVO.success();
    }

    @AdminAuthorize
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = POST)
    public ResultVO delete(HttpServletRequest request, @Valid @RequestBody IdReqVo reqVo) {

        artImageAdminService.delete(reqVo.getId());

        return ResultVO.success();
    }

    @AdminAuthorize
    @ApiOperation(value = "online")
    @RequestMapping(value = "/online", method = POST)
    public ResultVO online(HttpServletRequest request, @Valid @RequestBody ArtImageOnlineReq reqVo) {
        artImageAdminService.online(reqVo);
        return ResultVO.success();
    }

    @AdminAuthorize
    @ApiOperation(value = "审核通过")
    @RequestMapping(value = "/auditPass", method = POST)
    public ResultVO auditPass(HttpServletRequest request, @RequestBody IdReqVo reqVo ) {
        artImageAdminService.auditPass(reqVo.getId());
        return ResultVO.success();
    }

    @AdminAuthorize
    @ApiOperation(value = "审核拒绝")
    @RequestMapping(value = "/auditRefuse", method = POST)
    public ResultVO auditRefuse(HttpServletRequest request, @RequestBody AuditRefuse reqVo ) {
        artImageAdminService.auditRefuse(reqVo.getId(),reqVo.getMsg());
        return ResultVO.success();
    }

    @ApiOperation(value = "下载通过的文本列表")
    @AdminAuthorize
    @ResponseIgnore(ignore = true)
    @PostMapping(value = "downloadTextList")
    public void downloadTextList(HttpServletResponse response, @RequestBody ArtTextListReq req)throws Exception{


        final String filePath = artImageAdminService.getTextList(req);

        if (StringUtils.isBlank(filePath)){
            return;
        }
        final Date startCreateTime = req.getStartCreateTime();
        final Date endCreateTime = req.getEndCreateTime();

        final String start = DateFormatUtils.format(startCreateTime, "yyyyMMdd");
        final String end = DateFormatUtils.format(endCreateTime, "yyyyMMdd");
        String name = start+"_"+end+".txt";
        final Path path = Paths.get(filePath);
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        try (FileChannel fileChannel = FileChannel.open(path)) {

            long size = fileChannel.size();

            log.info("文件大小: {}", size);

            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + name);
            response.setContentLengthLong(size);

            /**
             * transferTo 一次性最多只能处理2147483647字节数据。
             * 所以需要多次调用
             */

            long position = 0;

            while (size > 0) {
                long count = fileChannel.transferTo(position, size, Channels.newChannel(response.getOutputStream()));
                if (count > 0) {
                    position += count;
                    size -= count;
                }
            }
        }

    }

    @Data
    static class AuditRefuse{
        Integer id;
        String msg;
    }
}
