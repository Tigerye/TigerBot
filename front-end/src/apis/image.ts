/*
 * @Description: 图片相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { RequestParams, ContentType, Method } from 'axios-mapper';

export const createImage = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/api/call/artImage',
    Method.POST,
    data,
    ContentType.json
  );
};

export const imageGetDetailByReqId = (data: { reqId: number | string }) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/getDetailByReqId',
    Method.POST,
    data,
    ContentType.json
  );
};

export const imageGetDetailById = (data: { id: string }) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/getDetailById',
    Method.POST,
    data,
    ContentType.json
  );
};

export const getMyImages = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/getMyImages',
    Method.POST,
    data,
    ContentType.json
  );
};

export const getImageSizeList = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/getSizeList',
    Method.POST,
    data,
    ContentType.json
  );
};

export const getPublicList = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/getPublishList',
    Method.POST,
    data,
    ContentType.json
  );
};

// 图片发布
export const artImageOnline = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/online',
    Method.POST,
    data,
    ContentType.json
  );
};

// 图片下线
export const artImageOffline = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/offline',
    Method.POST,
    data,
    ContentType.json
  );
};

// 图片删除
export const artImageDelete = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/artImage/delete',
    Method.POST,
    data,
    ContentType.json
  );
};

// 查看modifier类型列表
export const getModifierClassTypes = () => {
  return https().request<RootObject<any>>(
    '/ai/dict/getModifierClassTypes',
    Method.POST,
    undefined,
    ContentType.json
  );
};

// 按类型查看modifier列表
export const getModifierListByType = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/ai/dict/getModifierListByType',
    Method.POST,
    data,
    ContentType.json
  );
};
