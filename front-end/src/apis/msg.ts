/*
 * @Description: 消息相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { RequestParams, ContentType, Method } from 'axios-mapper';

// 订阅通知数量
export const getCountNotifyUnRead = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/notify/countNotifyUnRead',
    Method.POST,
    data,
    ContentType.json
  );
};

// 获取各类通知未读数
export const getCountNotifyUnReadGroupByType = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/notify/countNotifyUnReadGroupByType',
    Method.POST,
    data,
    ContentType.json
  );
};

// 我收到的赞的列表
export const getThumb2mePage = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/thumb/getThumb2mePage',
    Method.POST,
    data,
    ContentType.json
  );
};

// 我喜欢的
export const getMyThumbPage = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/thumb/getMyThumbPage',
    Method.POST,
    data,
    ContentType.json
  );
};

// 系统通知列表
export const getNotifyPage = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/notify/getNotifyPage',
    Method.POST,
    data,
    ContentType.json
  );
};

// 评论我的列表
export const getCommentMeList = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/comment/getCommentMeList',
    Method.POST,
    data,
    ContentType.json
  );
};
