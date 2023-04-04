package com.tigerobo.x.pai.biz.user;

import com.github.pagehelper.Page;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.dto.UserCommitSiteDto;
import com.tigerobo.x.pai.api.enums.CommitSiteStatusEnum;
import com.tigerobo.x.pai.api.enums.NotifyMessageTypeEnum;
import com.tigerobo.x.pai.api.enums.NotifyTypeEnum;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.req.UserCommitPageReq;
import com.tigerobo.x.pai.api.req.UserCommitSiteReq;
import com.tigerobo.x.pai.api.vo.PageVo;
import com.tigerobo.x.pai.biz.auth.RoleService;
import com.tigerobo.x.pai.biz.biz.NotifyService;
import com.tigerobo.x.pai.biz.converter.UserCommitConvert;
import com.tigerobo.x.pai.biz.utils.JacksonUtil;
import com.tigerobo.x.pai.biz.utils.ThreadLocalHolder;
import com.tigerobo.x.pai.dal.biz.dao.user.UserCommitSiteDao;
import com.tigerobo.x.pai.dal.biz.entity.user.UserCommitSitePo;
import com.tigerobo.x.pai.dal.biz.entity.user.UserNotifyPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserCommitService {

    @Autowired
    private UserCommitSiteDao userCommitSiteDao;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotifyService notifyService;

    public void commitFail(Integer id, String msg) {
        Preconditions.checkArgument(id != null, "参数不正确");
        UserCommitSitePo load = userCommitSiteDao.load(id);
        if (load == null) {
            throw new IllegalArgumentException("用户提交不存在");
        }
        Integer status = load.getStatus();
        if (status.equals(3)) {
            throw new IllegalArgumentException("用户已取消");
        }
        UserCommitSitePo update = new UserCommitSitePo();
        update.setId(id);
        update.setMsg(msg);
        update.setStatus(CommitSiteStatusEnum.FAIL.getStatus());
        userCommitSiteDao.update(update);

        UserCommitSiteDto dto = UserCommitConvert.po2dto(update);
        dto.setName(load.getName());
        notify(load.getUserId(), load.getId(), NotifyMessageTypeEnum.COMMIT_FAIL,dto);
    }

    public void commitSuccess(Integer id, Integer mediaType, Integer mediaId) {
        Preconditions.checkArgument(id != null, "参数不正确");
        UserCommitSitePo load = userCommitSiteDao.load(id);
        if (load == null) {
            throw new IllegalArgumentException("用户提交不存在");
        }
        Integer status = load.getStatus();
        if (status.equals(3)) {
            throw new IllegalArgumentException("用户已取消");
        }
        UserCommitSitePo update = new UserCommitSitePo();
        update.setId(id);
        update.setMediaId(mediaId);
        update.setMediaType(mediaType);
        update.setStatus(CommitSiteStatusEnum.SUCCESS.getStatus());
        userCommitSiteDao.update(update);


        UserCommitSiteDto dto = UserCommitConvert.po2dto(update);
        dto.setName(load.getName());
        notify(load.getUserId(), load.getId(), NotifyMessageTypeEnum.COMMIT_SUCCESS,dto);
    }

    private void notify(Integer userId, Integer id, NotifyMessageTypeEnum notifyMessageTypeEnum,Object messageEntity) {
        UserNotifyPo notifyPo = new UserNotifyPo();
        notifyPo.setUserId(userId);
        notifyPo.setBizId(String.valueOf(id));
        notifyPo.setNotifyType(NotifyTypeEnum.COMMIT_MEDIA.getType());


        notifyPo.setMessageType(notifyMessageTypeEnum.getType());
        notifyPo.setTitle(notifyMessageTypeEnum.getText());
        notifyPo.setMessageEntity(JacksonUtil.bean2Json(messageEntity));

        notifyService.addNotify(notifyPo);
    }

    public Integer addCommit(UserCommitSiteReq dto) {
        Integer userId = ThreadLocalHolder.getUserId();
        if (userId == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        Preconditions.checkArgument(!StringUtils.isEmpty(dto.getUrl()) || !StringUtils.isEmpty(dto.getName()), "名称与网站地址都为空");

        UserCommitSitePo po = UserCommitConvert.addReq2po(dto);
        po.setUserId(userId);

        userCommitSiteDao.add(po);
        return po.getId();
    }

    public PageVo<UserCommitSiteDto> getUserCommitPage(UserCommitPageReq reqVo) {
        PageVo<UserCommitSiteDto> pageVo = new PageVo<>();
        pageVo.setPageNum(reqVo.getPageNum());
        pageVo.setPageSize(reqVo.getPageSize());

        Integer userId = reqVo.getUserId();
        if (userId == null) {
            return pageVo;
        }
        Integer loginUser = ThreadLocalHolder.getUserId();
        if (loginUser == null || loginUser <= 0) {
            return pageVo;
        }
        if (!canView(loginUser, userId)) {
            return pageVo;
        }

        Page<UserCommitSitePo> page = userCommitSiteDao.getPage(reqVo, userId);

        List<UserCommitSiteDto> dtos = UserCommitConvert.po2dto(page);
        pageVo.setList(dtos);
        pageVo.setTotal(page.getTotal());
        return pageVo;
    }

    public List<UserCommitSiteDto> getByIds(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        List<UserCommitSitePo> pos = userCommitSiteDao.getByIds(ids);
        return UserCommitConvert.po2dto(pos);
    }

    private boolean canView(Integer loginUserId, Integer reqUserId) {
        if (loginUserId.equals(reqUserId)) {
            return true;
        }

        return roleService.isAdmin();
    }


}
