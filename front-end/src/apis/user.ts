/*
 * @Description: 用户相关接口
 */
import { RootObject } from '@/model/rootObject';
import { UserInfoModel } from '@/model/userModel';
import https from '@/utils/https';
import { LoginModel } from '@/model/loginModel';
import { RequestParams, ContentType, Method } from 'axios-mapper';

export const loginByMobilePassword = (data: any) => {
  return https(false).request<RootObject<LoginModel>>(
    '/web/uc/login_by_password',
    Method.POST,
    {
      mobile: data.mobile,
      password: data.password,
    },
    ContentType.json
  );
};

export const loginByMobileCode = (data: any) => {
  return https(false).request<RootObject<LoginModel>>(
    '/web/uc/login_by_mobile',
    Method.POST,
    {
      mobile: data.mobile,
      code: data.code,
      area: data.area,
    },
    ContentType.json
  );
};

export const loginByWechatCode = (codeData: RequestParams) => {
  return https(false).request<RootObject<LoginModel>>(
    '/web/uc/login_by_wechat',
    Method.POST,
    codeData,
    ContentType.json
  );
};

export const sendMobileCode = (data: any) => {
  return https(false).request<RootObject<any>>(
    '/uc/mobile/send_mobile_code',
    Method.POST,
    {
      area: data.area,
      mobile: data.mobile,
    },
    ContentType.json
  );
};

export const userInfoRequest = () => {
  return https().request<RootObject<UserInfoModel>>(
    '/web/uc/get_user_info',
    Method.POST,
    undefined,
    ContentType.json
  );
};

export const register = (data: any) => {
  return https().request<RootObject<any>>(
    '/web/uc/register_by_mobile',
    Method.POST,
    { ...data },
    ContentType.json
  );
};

// 签到
export const signIn = () => {
  return https().request<RootObject<any>>(
    '/activity/signIn',
    Method.POST,
    undefined,
    ContentType.json
  );
};

// 是否签到
export const checkHasSignIn = () => {
  return https().request<RootObject<any>>(
    '/activity/hasSignIn',
    Method.POST,
    undefined,
    ContentType.json
  );
};
