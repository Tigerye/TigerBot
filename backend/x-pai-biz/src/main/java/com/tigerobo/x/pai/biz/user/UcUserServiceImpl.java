package com.tigerobo.x.pai.biz.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.tigerobo.x.pai.api.auth.entity.Authorization;
import com.tigerobo.x.pai.api.auth.entity.Group;
import com.tigerobo.x.pai.api.auth.entity.User;
import com.tigerobo.x.pai.api.constants.ImageConstant;
import com.tigerobo.x.pai.api.dto.MemberDto;
import com.tigerobo.x.pai.api.exception.AuthorizeException;
import com.tigerobo.x.pai.api.exception.ResultCode;
import com.tigerobo.x.pai.api.uc.dto.BaseMobileDto;
import com.tigerobo.x.pai.api.uc.dto.ChangeMobileDto;
import com.tigerobo.x.pai.api.uc.dto.UserMobileRegisterDto;
import com.tigerobo.x.pai.api.uc.dto.WechatUserDto;
import com.tigerobo.x.pai.biz.base.EnvService;
import com.tigerobo.x.pai.biz.biz.OssService;
import com.tigerobo.x.pai.biz.biz.member.MemberService;
import com.tigerobo.x.pai.biz.converter.UserConvert;
import com.tigerobo.x.pai.biz.biz.service.WebGroupService;
import com.tigerobo.x.pai.biz.message.service.ConfirmCodeService;
import com.tigerobo.x.pai.biz.utils.*;
import com.tigerobo.x.pai.dal.auth.dao.UserDao;
import com.tigerobo.x.pai.dal.auth.dao.WechatInfoDao;
import com.tigerobo.x.pai.dal.auth.entity.UserDo;
import com.tigerobo.x.pai.dal.auth.entity.WechatInfoPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UcUserServiceImpl {


    @Autowired
    private ConfirmCodeService confirmCodeService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private WechatInfoDao wechatInfoDao;
    @Autowired
    private WechatHandler wechatHandler;

    @Autowired
    private WebGroupService webGroupService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OssService ossService;
    @Autowired
    private EnvService envService;

    public Group getUserGroup(Integer userId) {

        UserDo userDo = userDao.getById(userId);
        if (userDo == null) {
            return null;
        }
        String currGroupUuid = userDo.getCurrGroupUuid();
        Group group = null;
        if (StringUtils.isNotBlank(currGroupUuid)) {
            group = webGroupService.getById(currGroupUuid);
        }
        if (group != null) {
            return group;
        }
        return UserConvert.po2dto(userDo).toPersonalGroup();

    }

    public Authorization mobileRegister(UserMobileRegisterDto req) {
        Validate.isTrue(req != null, "参数为空");
        Validate.isTrue(StringUtils.isNoneBlank(req.getMobile()), "手机号为空");
        Validate.isTrue(StringUtils.isNoneBlank(req.getCode()), "验证码为空");
        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(req.getArea(), req.getMobile());

        String mobile = baseMobile.getMobile();
        //todo 加入 area_code
        UserDo userDb = userDao.getByMobile(mobile);

        Preconditions.checkArgument(userDb == null, "用户已存在，请直接登录");

        confirmCodeService.checkConfirmCode(baseMobile, req.getCode());

        UserDo createUser = new UserDo();
        String uuid = IdGenerator.getId();
        createUser.setUuid(uuid);
        createUser.setName(req.getNickName());
//        createUser.setNameEn(req.getNickName());
        createUser.setMobile(req.getMobile());
        createUser.setAreaCode(baseMobile.getAreaCode());
        createUser.setImage(ImageConstant.DEFAULT_AVATAR);
        createUser.setAvatar(ImageConstant.DEFAULT_AVATAR);
//        createUser.setAccount(id);
        createUser.setAccount(uuid);
        createUser.setPassword(Md5Util.getPassword(req.getPassword()));
        String ip = ThreadLocalHolder.getIp();
        createUser.setIp(ip);
        if (StringUtils.isNotBlank(req.getAccessSource())){
            createUser.setAccessSource(req.getAccessSource());
        }

        int insert = userDao.insert(createUser);
        if (insert!=1){
            log.error("创建用户失败:req,{}",JSON.toJSONString(req));
            throw new IllegalArgumentException("创建用户失败");
        }


        Preconditions.checkArgument(createUser.getId() != null && createUser.getId() > 0, "创建用户失败");

        Authorization login = login(createUser);

        confirmCodeService.deleteConfirmCodeKey(baseMobile);
        //log---登录日志,用户日志

        return login;
    }

    public Authorization mobileCodeLogin(UserMobileRegisterDto req) {
        Validate.isTrue(req != null, "参数为空");
        Validate.isTrue(StringUtils.isNoneBlank(req.getMobile()), "手机号为空");
        Validate.isTrue(StringUtils.isNoneBlank(req.getCode()), "验证码为空");
        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(req.getArea(), req.getMobile());
        confirmCodeService.checkConfirmCode(baseMobile, req.getCode());

        String mobile = baseMobile.getMobile();
        UserDo userDb = userDao.getByMobile(mobile);

        Preconditions.checkArgument(userDb != null && userDb.getId() != null, "用户不存在，请先注册");

        Authorization login = login(userDb);
        confirmCodeService.deleteConfirmCodeKey(baseMobile);
        //log---登录日志,用户日志

        return login;
    }

    public Authorization passwordLogin(UserMobileRegisterDto req) {
        Validate.isTrue(req != null, "参数为空");
        Validate.isTrue(StringUtils.isNoneBlank(req.getMobile()), "手机号为空");
        Validate.isTrue(StringUtils.isNoneBlank(req.getPassword()), "密码为空");
        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(req.getArea(), req.getMobile());

        String mobile = baseMobile.getMobile();
        UserDo userDb = userDao.getByMobile(mobile);

        Preconditions.checkArgument(userDb != null && userDb.getId() != null, "用户不存在，请先注册");

        Preconditions.checkArgument(StringUtils.isNotBlank(userDb.getPassword()), "用户还没设置密码");


        String password = Md5Util.getPassword(req.getPassword());
        Preconditions.checkArgument(password.equals(userDb.getPassword()), "密码不正确");
        Authorization login = login(userDb);
//        confirmCodeService.deleteConfirmCodeKey(baseMobile);
        //log---登录日志,用户日志
        return login;
    }

    //todo上传微信头像到oss
    public Authorization wechatLogin(String appId,String wechatCode,String accessSource,UserMobileRegisterDto req) {
        log.info("wechatLogin,appId:{},code:{}",appId,wechatCode);
        Preconditions.checkArgument(StringUtils.isNotBlank(wechatCode), "微信未授权code");
        Preconditions.checkArgument(StringUtils.isNotBlank(appId), "appId为空");

        JSONObject wechatInfo = this.wechatHandler.getWechatInfoByCode(appId,wechatCode);
        if (wechatInfo == null || StringUtils.isBlank(wechatInfo.getString("unionId"))) {
            throw new AuthorizeException(ResultCode.USER_WECHAT_LOGIN_FAIL);
        }

        WechatUserDto wechatUserDto = JSON.parseObject(wechatInfo.toJSONString(), WechatUserDto.class);
        String unionId = wechatUserDto.getUnionId();

        transferFront2Wechat(req,wechatUserDto);

        String wechatUrl = wechatUserDto.getHeadImgUrl();

        UserDo userDo = userDao.getByWechatUnionId(unionId);
        // 用户不存在即为新用户，返回用户微信信息
        if (userDo == null) {
            userDo = new UserDo();
            userDo.setWechat(unionId);
            String id = IdGenerator.getId();
            userDo.setUuid(id);

            String headImgUrl = wechatUrl;
            if (StringUtils.isBlank(headImgUrl)) {
                headImgUrl = ImageConstant.DEFAULT_AVATAR;
            }else {
                final long delta = System.currentTimeMillis() % 10000;
                final boolean prod = envService.isProd();
                String key = "user/"+(prod?"":"t")+"avatar/"+id+"/"+delta+".png";
                final String ossHeadUrl = ossService.uploadUrlFile(headImgUrl, key, true);

                if (StringUtils.isNotBlank(ossHeadUrl)){
                    headImgUrl = ossHeadUrl;
                }
            }
            userDo.setImage(headImgUrl);
            //线上用avatar
            userDo.setAvatar(headImgUrl);
            userDo.setImage(wechatUserDto.getHeadImgUrl());
            userDo.setName(wechatUserDto.getNickName());
            userDo.setWechatName(wechatUserDto.getNickName());

            userDo.setAccount(id);
            String ip = ThreadLocalHolder.getIp();
            userDo.setIp(ip);
            if (StringUtils.isNotBlank(accessSource)){
                userDo.setAccessSource(accessSource);
            }
            userDao.insert(userDo);
            Preconditions.checkArgument(userDo.getId() != null && userDo.getId() > 0, "创建微信用户失败");
        }else {

            UserDo update = new UserDo();
            update.setId(userDo.getId());
            update.setWechat(userDo.getWechat());
            boolean hasUpdate =false;
            if (StringUtils.isNotBlank(wechatUserDto.getNickName())){
                update.setWechatName(wechatUserDto.getNickName());
                hasUpdate = true;
            }
//            if (StringUtils.isNotBlank(wechatUserDto.getHeadImgUrl())){
//                update.setImage(wechatUserDto.getHeadImgUrl());
//                hasUpdate = true;
//            }
            if (hasUpdate){
                userDao.update(update);
            }

        }
        updateWechatInfoTable(wechatUserDto);

        Authorization login = login(userDo);

        return login;
    }

    private void transferFront2Wechat(UserMobileRegisterDto req,WechatUserDto userdto){

        if (StringUtils.isNotBlank(userdto.getNickName())){
            return;
        }
        final UserMobileRegisterDto.WechatInfo wechatInfo = req.getWechatInfo();
        if (wechatInfo ==null||StringUtils.isBlank(wechatInfo.getNickName())){
            return;
        }
        userdto.setNickName(wechatInfo.getNickName());
        userdto.setHeadImgUrl(wechatInfo.getAvatarUrl());
        userdto.setCity(wechatInfo.getCity());
        userdto.setCountry(wechatInfo.getCountry());
        userdto.setProvince(wechatInfo.getProvince());
        userdto.setSex(wechatInfo.getGender());
        userdto.setLanguage(wechatInfo.getLanguage());
    }




    private Authorization login(UserDo user) {

        Integer uid = user.getId();
        String token =tokenService.produceToken(uid);

        return buildLoginInfo(user, token, uid);
    }

    private Authorization buildLoginInfo(UserDo user, String token, Integer uid) {
        String currGroupUuid = user.getCurrGroupUuid();
        User userDto = UserConvert.po2dto(user);

        Group group = null;
        if (StringUtils.isNotBlank(currGroupUuid)) {
            group = webGroupService.getByUuidFromCache(currGroupUuid);
        } else {
            group = userDto.toPersonalGroup();
        }
        MemberDto memberDto = memberService.getMember(uid);
        return Authorization.builder()
                .token(token)
                .uid(user.getUuid())
                .user(userDto)
                .group(group)
                .member(memberDto)
                .build();
    }

    public void unbindWechat() {
        Integer userId = ThreadLocalHolder.getUserId();
        UserDo user = userDao.getById(userId);
        if (user == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        if (StringUtils.isEmpty(user.getWechat())){
            throw new IllegalArgumentException("用户未绑定微信");
        }
        if (StringUtils.isEmpty(user.getMobile())){
            throw new IllegalArgumentException("当前账号未绑定手机号，不能解绑");
        }
        if (StringUtils.isEmpty(user.getMobile())){
            throw new IllegalArgumentException("当前账号未绑定手机号，不能解绑");
        }
        UserDo update = new UserDo();
        update.setId(userId);
        update.setWechat("");
        update.setWechatName("");

        userDao.update(update);

        //todo log
    }
    public void bindWechat(String appId,String wechatCode) {

        Preconditions.checkArgument(StringUtils.isNotBlank(wechatCode), "微信未授权code");
        Preconditions.checkArgument(StringUtils.isNotBlank(appId), "appId为空");
        Integer userId = ThreadLocalHolder.getUserId();
        UserDo user = userDao.getById(userId);
        if (user == null) {
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        String mobile = user.getMobile();

        Preconditions.checkArgument(StringUtils.isNotBlank(mobile), "当前账号未绑定手机号");
        JSONObject wechatInfo = this.wechatHandler.getWechatInfoByCode(appId,wechatCode);
        if (wechatInfo == null || StringUtils.isBlank(wechatInfo.getString("unionId"))) {
            throw new IllegalArgumentException("微信授权失败");
        }

        WechatUserDto wechatUserDto = JSON.parseObject(wechatInfo.toJSONString(), WechatUserDto.class);
        String unionId = wechatUserDto.getUnionId();

        UserDo wechatUser = userDao.getByWechatUnionId(unionId);

        if (wechatUser == null) {
            doBindWeChat(userId,wechatUserDto,user);
        } else if (userId.equals(wechatUser.getId())) {
            throw new IllegalArgumentException("微信已绑定,请刷新重试");
        } else {
            throw new IllegalArgumentException("微信已绑定账户,请解绑后重试");
            //合并账号
//            String wechatUserMobile = wechatUser.getMobile();
//            if (!StringUtils.isEmpty(wechatUserMobile)) {
//
//            }
//            doBindWeChat(userId,unionId);
//            userDao.delete(userId);

        }

    }

    private void doBindWeChat(Integer userId, WechatUserDto wechatUserDto, UserDo user){

        UserDo update = new UserDo();
        update.setId(userId);
        update.setWechat(wechatUserDto.getUnionId());
        update.setImage(wechatUserDto.getHeadImgUrl());
        update.setWechatName(wechatUserDto.getNickName());

        if (StringUtils.isEmpty(user.getAvatar())||ImageConstant.DEFAULT_AVATAR.equalsIgnoreCase(user.getAvatar())){
            update.setAvatar(wechatUserDto.getHeadImgUrl());
        }

        userDao.update(update);

        updateWechatInfoTable(wechatUserDto);
    }

    private void updateWechatInfoTable(WechatUserDto wechatUserDto) {
        WechatInfoPo wechatInfoPo = new WechatInfoPo();

        wechatInfoPo.setUnionId(wechatUserDto.getUnionId());
        wechatInfoPo.setContent(JSON.toJSONString(wechatUserDto));
        wechatInfoDao.insertOrUpdate(wechatInfoPo);
    }

    public String getChangeMobileToken(ChangeMobileDto req){
        Integer userId = ThreadLocalHolder.getUserId();
        UserDo user = userDao.getById(userId);
        if (user ==null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        String mobile = req.getMobile();
        Preconditions.checkArgument(StringUtils.isNotBlank(mobile),"手机号为空");

        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(req.getArea(), mobile);
        confirmCodeService.checkConfirmCode(baseMobile, req.getCode());

        String value = userId+":"+System.currentTimeMillis()/1000;
        String token = EncryptionUtil.encrypt4NetAndJava(value);
        return token;
    }

    public void bindMobile(ChangeMobileDto req){

        Integer userId = ThreadLocalHolder.getUserId();
        UserDo userDo = userDao.getById(userId);
        if (userDo == null){
            throw new AuthorizeException(ResultCode.USER_NOT_LOGIN);
        }
        Preconditions.checkArgument(req!=null,"参数为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getMobile()),"手机号为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(req.getCode()),"验证码不能为空");
        if (StringUtils.isNotBlank(userDo.getMobile())){
            Preconditions.checkArgument(StringUtils.isNotBlank(req.getChangeToken()),"认证token为空");
            String value = EncryptionUtil.decrypt4NetAndJava(req.getChangeToken());
            String[] split = value.split(":");
            if (split.length<2){
                throw new IllegalArgumentException("认证token无效");
            }
            Preconditions.checkArgument(String.valueOf(userId).equals(split[0]),"认证token无效");
            Preconditions.checkArgument(split[1].matches("\\d+"),"认证token无效");
            if (System.currentTimeMillis()/1000-Long.parseLong(split[1])>60*10){
                throw new IllegalArgumentException("认证token已失效");
            }
        }

        BaseMobileDto baseMobile = MobileUtil.getBaseMobile(req.getArea(), req.getMobile());

        String mobile = baseMobile.getMobile();
        //todo 加入 area_code
        UserDo userDb = userDao.getByMobile(mobile);

        Preconditions.checkArgument(userDb == null, "手机号已绑定用户");

        confirmCodeService.checkConfirmCode(baseMobile, req.getCode());
        doBindMobile(baseMobile,userId);

    }

    private void doBindMobile(BaseMobileDto baseMobileDto,Integer userId){
        UserDo update = new UserDo();
        update.setId(userId);

        update.setMobile(baseMobileDto.getMobile());
        update.setAreaCode(baseMobileDto.getAreaCode());
        userDao.update(update);
    }

    public Authorization getUserLoginInfo(){
        Integer userId = ThreadLocalHolder.getUserId();
        String token = ThreadLocalHolder.getToken();

        UserDo userDo = userDao.getById(userId);
        if (userDo == null){
            log.error("nologin:getUserInfo");
            return null;
        }
        Authorization authorization = buildLoginInfo(userDo, token, userDo.getId());

        User user = UserConvert.po2dto(userDo);
        authorization.setUser(user);
        return authorization;
    }
}
