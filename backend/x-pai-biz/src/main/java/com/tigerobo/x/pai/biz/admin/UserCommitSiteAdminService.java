package com.tigerobo.x.pai.biz.admin;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.admin.req.UserCommitFailReq;
import com.tigerobo.x.pai.api.admin.req.UserCommitSiteAdminReq;
import com.tigerobo.x.pai.api.admin.req.UserCommitSuccessReq;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.converter.UserCommitConvert;
import com.tigerobo.x.pai.biz.user.UserCommitService;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.biz.dao.user.UserCommitSiteDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommitSitePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class UserCommitSiteAdminService {

    @Autowired
    private UserCommitService userCommitService;
    @Autowired
    private UserCommitSiteDao userCommitSiteDao;
    @Autowired
    private UserDao userDao;
    public PageVo<UserCommitSiteDto> getPageList (UserCommitSiteAdminReq req){

        Preconditions.checkArgument(req!=null,"请求参数为空");
        if (!StringUtils.isEmpty(req.getKeyword())) {
            if (req.getKeyword().length() > 20) {
                log.warn("length long,keyword:{}", req.getKeyword());
                req.setKeyword(req.getKeyword().substring(0, 20));
            }
        }
        Page<Integer> ids = userCommitSiteDao.getUserComSiteIds(req);
        List<UserCommitSitePo> pos = userCommitSiteDao.getOnlyByIds(ids);
        PageVo<UserCommitSiteDto> userCommitSiteDtos=new PageVo<>();
        if(!CollectionUtils.isEmpty(pos)){
            List<UserCommitSiteDto> collect = pos.stream().map(po->{
               UserCommitSiteDto dto = UserCommitConvert.po2dto(po);
                UserDo user = userDao.getById(po.getUserId());
               if(user!=null){
                   String userName = user.getName();
                   dto.setUserName(userName);
               }

                if(po.getIsDeleted()){
                    dto.setIsDeleted(1);
                }else {
                    dto.setIsDeleted(0);
                }
                return dto;
            }).collect(Collectors.toList());
            userCommitSiteDtos.setList(collect);
        }
        userCommitSiteDtos.setPageNum(req.getPageNum());
        userCommitSiteDtos.setPageSize(req.getPageSize());
        userCommitSiteDtos.setTotal((int)ids.getTotal());
        return userCommitSiteDtos;
    }
    public void userCommitAdd(UserCommitSiteReq req){
        Preconditions.checkArgument(req!=null,"请求参数为空");
        userCommitService.addCommit(req);
    }
    public void userCommitFail(UserCommitFailReq req){
        Preconditions.checkArgument(req!=null,"请求参数为空");
        userCommitService.commitFail(req.getId(),req.getMsg());
    }
    public void userCommitSuccess(UserCommitSuccessReq req){
        Preconditions.checkArgument(req!=null,"请求参数为空");
        userCommitService.commitSuccess(req.getId(), req.getMediaType(), req.getMediaId());
    }
}
