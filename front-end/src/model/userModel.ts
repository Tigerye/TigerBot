/*
 * @Description: 用户信息接口
 */

export interface UserInfoModel {
  gid: string;
  role: string;
  token: string;
  uid: string;
  user: User;
}

export interface VisitorModel {
  account: string;
  admin: boolean;
  follow: boolean;
  permissionList: Array<string>;
  role: string;
  uuid: string;
  user: User;
}

interface Group {
  account: string;
  createTime: string;
  id: number;
  image: string;
  logo: string;
  name: string;
  nameEn: string;
  scope: string;
  type: string;
  updateTime: string;
  uuid: string;
  website: string;
}

interface User {
  accessSource: string;
  account: string;
  avatar: string;
  createTime: string;
  currentGroupId: number;
  currentGroupUuid: string;
  follow: false;
  id: number;
  image: string;
  intro: string;
  mobile: string;
  name: string;
  roleType: number;
  type: string;
  uuid: string;
  website: string;
  wechat: string;
  wechatName: string;
}
