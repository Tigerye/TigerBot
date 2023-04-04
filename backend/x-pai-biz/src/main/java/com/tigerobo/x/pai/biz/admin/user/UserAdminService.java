package com.tigerobo.x.pai.biz.admin.user;

import com.github.pagehelper.Page;
import com.tigerobo.x.pai.api.admin.req.user.UserSearchReq;
import com.tigerobo.x.pai.api.dto.user.UserDto;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.user.BlackUserService;
import com.tigerobo.x.pai.dal.admin.dao.UserAdminDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAdminService {

    @Autowired
    private UserAdminDao userAdminDao;

    @Autowired
    private BlackUserService blackUserService;


    public PageVo<UserDto> query(UserSearchReq req){

        PageVo pageVo = new PageVo();
        List<Integer> blackUserIds = null;
        if (req.getIsBlackUser()!=null&&req.getIsBlackUser()){
            blackUserIds = blackUserService.getBlackUserIds();
            if (CollectionUtils.isEmpty(blackUserIds)){
                return pageVo;
            }
        }
        final List<UserDo> query = userAdminDao.query(req,blackUserIds);

        Page<UserDo> page = (Page<UserDo>) query;
        final List<UserDto> dtos = convert(query);


        pageVo.setTotal(page.getTotal());
        pageVo.setList(dtos);
        return pageVo;
    }

    private List<UserDto> convert(List<UserDo> pos){
        if (CollectionUtils.isEmpty(pos)){
            return null;
        }

        return pos.stream().map(this::convert).collect(Collectors.toList());
    }
    private UserDto convert(UserDo po){

        if (po == null){
            return null;
        }

        UserDto dto = new UserDto();
        BeanUtils.copyProperties(po,dto);


        final Integer id = po.getId();

        final boolean blackUser = blackUserService.isBlackUser(id);
        dto.setIsBlackUser(blackUser);
        return dto;
    }


}
