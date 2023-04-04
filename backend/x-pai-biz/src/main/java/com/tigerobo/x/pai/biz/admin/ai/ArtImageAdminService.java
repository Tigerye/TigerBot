package com.tigerobo.x.pai.biz.admin.ai;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.admin.req.ai.ArtImageAdminQueryReq;
import com.tigerobo.x.pai.api.admin.req.ai.ArtTextListReq;
import com.tigerobo.x.pai.api.ai.req.ArtImageOnlineReq;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.auth.service.UserService;
import com.tigerobo.x.pai.api.dto.admin.ai.ArtImageDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.api.vo.user.UserBriefVo;
import com.tigerobo.x.pai.biz.admin.cache.AdminHolder;
import com.tigerobo.x.pai.biz.ai.art.image.ArtImageOperateService;
import com.tigerobo.x.pai.biz.ai.convert.ArtImageConvert;
import com.tigerobo.x.pai.biz.auth.UserServiceImpl;
import com.tigerobo.x.pai.biz.utils.ListConvertUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.admin.entity.SsoUserPo;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageAdminDao;
import com.tigerobo.x.pai.dal.ai.dao.AiArtImageDao;
import com.tigerobo.x.pai.dal.ai.entity.AiArtImagePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArtImageAdminService {

    @Autowired
    private AiArtImageAdminDao aiArtImageAdminDao;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ArtImageOperateService artImageOperateService;

    public PageVo<ArtImageDto> query(ArtImageAdminQueryReq req){
        Integer userId = ThreadLocalHolder.getUserId();

        Page<AiArtImagePo> userPage = aiArtImageAdminDao.getPage( req);

        PageVo<ArtImageDto> pageVo = new PageVo<>();

        pageVo.setTotal(userPage.getTotal());
        pageVo.setPageSize(req.getPageSize());
        pageVo.setPageNum(req.getPageNum());

        List<ArtImageDto> vos = ArtImageConvert.po2dtos(userPage);
        buildListUsers(vos);
        pageVo.setList(vos);
        return pageVo;
    }

    private void buildListUsers(List<ArtImageDto> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return;
        }
        List<Integer> userIds = vos.stream().map(vo -> vo.getUserId()).filter(id -> id != null && id > 0).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        final Map<Integer, UserBriefVo> userBriefMap = userService.getUserBriefMap(userIds);


        for (ArtImageDto vo : vos) {
            Integer userId = vo.getUserId();
            if (userId == null) {
                continue;
            }
            UserBriefVo user = userBriefMap.get(userId);
            vo.setUser(user);
        }
    }
    public void auditPass(Integer id){

        SsoUserPo user = AdminHolder.getUser();
        Integer adminUserId = user.getId();
        artImageOperateService.auditPass(id,adminUserId);
    }

    public void auditRefuse(Integer id,String msg){

        SsoUserPo user = AdminHolder.getUser();
        Integer adminUserId = user.getId();
        artImageOperateService.auditRefuse(id,msg,adminUserId);


    }
    public void online(ArtImageOnlineReq req) {

        SsoUserPo user = AdminHolder.getUser();
        Integer adminUserId = user.getId();
        artImageOperateService.online(req,adminUserId);
    }

    public void offline(Integer id) {

        SsoUserPo user = AdminHolder.getUser();
        Integer adminUserId = user.getId();
        artImageOperateService.offline(id,adminUserId);
    }

    public void delete(Integer id) {
        SsoUserPo user = AdminHolder.getUser();
        Integer adminUserId = user.getId();
        artImageOperateService.delete(id,adminUserId);
    }


    public String getTextList(ArtTextListReq req)throws Exception{

        Validate.isTrue(req.getStartCreateTime()!=null,"开始时间为空");

        Validate.isTrue(req.getEndCreateTime()!=null,"结束时间为空");
        final Page<AiArtImagePo> textList = (Page<AiArtImagePo>)aiArtImageAdminDao.getTextList(req, 1, 1);
        final long total = textList.getTotal();
        if (total ==0){
            return null;
        }
        String tmpdir = "/tmp/art/";
        final String filePath =tmpdir+ System.currentTimeMillis() + ".txt";

        File file = new File(filePath);
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (!file.exists()){
            file.createNewFile();
        }
        int pageSize = 200;

        final long totalPage = total / pageSize + (total % pageSize > 0 ? 1 : 0);

        try(final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));){
            for (int i = 1; i <= totalPage; i++) {
                final Page<AiArtImagePo> pageList = (Page<AiArtImagePo>)aiArtImageAdminDao.getTextList(req, i, pageSize);
                if (CollectionUtils.isEmpty(pageList)){
                    break;
                }
                for (AiArtImagePo aiArtImagePo : pageList) {
                    final String text = aiArtImagePo.getText();
                    bufferedWriter.write(text);
                    bufferedWriter.newLine();
                }

            }
        }


        log.info("path:{}",filePath);

        return filePath;
    }
}
