/*
 * @Description: 通用接口返回结构
 */

// {
//     "code": 0,
//     "msg": "success",
//     "data": {
//         "accessToken": "admin-token"
//     }
// }

export interface RootObject<T> {
  code: number;
  msg: string;
  data: T;
}
