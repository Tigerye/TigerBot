/*
 * @Description: gpt相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';

import { RequestParams, ContentType, Method } from 'axios-mapper';

export const chatGptExecute = (data: RequestParams, headers?: any) => {
  return https(true, headers).request<RootObject<any>>(
    'chatGpt/execute',
    Method.POST,
    data,
    ContentType.json
  );
};

export const chatGptExecuteB2 = (data: RequestParams, headers?: any) => {
  return https(true, headers).request<RootObject<any>>(
    '/chatGpt/b2/execute',
    Method.POST,
    data,
    ContentType.json
  );
};
