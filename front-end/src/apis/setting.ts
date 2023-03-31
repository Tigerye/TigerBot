/*
 * @Description: 用户设置相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { RequestParams, ContentType, Method } from 'axios-mapper';

// 上传头像 formData支持有问题，暂时无法使用，需优化
export const uploadUserAvatar = (data: FormData) => {
  return https().request<RootObject<any>>(
    '/common/upload',
    Method.POST,
    data,
    ContentType.multipart
  );
};

// 用户信息修改
export const modifyUser = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/user/modify',
    Method.POST,
    data,
    ContentType.json
  );
};

// 密码修改
export const modifyPassword = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/user/pwd-modify',
    Method.POST,
    data,
    ContentType.json
  );
};

// 获取变更手机号token
export const getChangeMobileToken = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/uc/get_change_mobile_token',
    Method.POST,
    data,
    ContentType.json
  );
};

// 手机绑定
export const bindMobile = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/uc/bind_mobile',
    Method.POST,
    data,
    ContentType.json
  );
};

// 手机绑定
export const bindWechat = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/uc/bind_wechat',
    Method.POST,
    data,
    ContentType.json
  );
};

// 手机绑定
export const unbindWechat = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/uc/unbind_wechat',
    Method.POST,
    data,
    ContentType.json
  );
};
