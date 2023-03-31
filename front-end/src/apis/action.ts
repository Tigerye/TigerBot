/*
 * @Description: 点赞、关注、评论等相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { RequestParams, ContentType, Method } from 'axios-mapper';

export const follow = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/follow/add',
    Method.POST,
    data,
    ContentType.json
  );
};

export const cancelFollow = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/web/follow/cancel',
    Method.POST,
    data,
    ContentType.json
  );
};

export const thumbUp = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/thumb/thumbUp',
    Method.POST,
    data,
    ContentType.json
  );
};

export const cancelThumbUp = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/thumb/cancelThumbUp',
    Method.POST,
    data,
    ContentType.json
  );
};

export const thumbDown = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/thumb/thumbDown',
    Method.POST,
    data,
    ContentType.json
  );
};

export const cancelThumbDown = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/thumb/cancelThumbDown',
    Method.POST,
    data,
    ContentType.json
  );
};
