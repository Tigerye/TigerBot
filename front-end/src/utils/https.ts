/*
 * @Description: axios 封装网络请求
 */

import { UserStore } from '@/store/user';
import HttpClient, { HttpClientConfig } from 'axios-mapper';
import networkConfig from '@/config/default/net.config';

const https = (hasToken: Boolean = true, headers?: any) => {
  const config: HttpClientConfig = {
    baseURL: networkConfig.host,
    headers: {
      token: hasToken ? UserStore().token : '',
      ...headers,
    },
  };
  return new HttpClient(config);
};

export default https;
