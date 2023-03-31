/*
 * @Description: 评论相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { commentModel } from '@/model/comment';
import { RequestParams, ContentType, Method } from 'axios-mapper';

// 评论列表
export const getCommentList = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/comment/getCommentList',
    Method.POST,
    data,
    ContentType.json
  );
};

// 添加评论
export const addComment = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/comment/addComment',
    Method.POST,
    data,
    ContentType.json
  );
};

// 删除评论
export const deleteComment = (data: RequestParams) => {
  return https().request<RootObject<any>>(
    '/comment/deleteComment',
    Method.POST,
    data,
    ContentType.json
  );
};
