/*
 * @Description: 个人主页相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { RequestParams, ContentType, Method } from 'axios-mapper';
import { VisitorModel } from '@/model/userModel';

export const getVisitorInfo = (data: RequestParams) => {
  return https().request<RootObject<VisitorModel>>(
    '/web/mine',
    Method.POST,
    data,
    ContentType.json
  );
};

export const getUserImages = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/getUserImages',
    Method.POST,
    data,
    ContentType.json
  );
};

// 获取访问用户的关注列表
export const getUserFollowList = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/follow/getUserFollowList',
    Method.POST,
    data,
    ContentType.json
  );
};

// 用户粉丝数
export const getUserFollowerCount = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/fan/getUserFanCount',
    Method.POST,
    data,
    ContentType.json
  );
};

// 用户粉丝列表
export const getUserFollowersPage = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/fan/getUserFanPage',
    Method.POST,
    data,
    ContentType.json
  );
};
