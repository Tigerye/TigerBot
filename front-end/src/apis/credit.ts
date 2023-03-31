/*
 * @Description: 积分相关接口
 */
import { RootObject } from '@/model/rootObject';
import https from '@/utils/https';
import { CreditModel } from '@/model/creditModel';
import { RequestParams, ContentType, Method } from 'axios-mapper';

export const getCredits = () => {
  return https().request<RootObject<CreditModel>>(
    '/alg/coin/getUserAlgCoinInfo',
    Method.POST,
    undefined,
    ContentType.json
  );
};
