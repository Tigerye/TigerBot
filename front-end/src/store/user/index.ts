import { defineStore } from 'pinia';
import { getToken, removeToken, setToken } from '@/utils/cookies';
import {
  loginByWechatCode,
  loginByMobilePassword,
  loginByMobileCode,
  userInfoRequest,
  register,
} from '@/apis/user';
import { getCredits } from '@/apis/credit';
import { UserActionTypes } from './action-types';
import { isMobile } from '@/utils';

export interface UserState {
  token: string | null | undefined;
  name: string | null;
  avatar: string | null;
  account: string | null;
  role: string | null;
  totalCredits: number;
  uid: string;
  intro: string;
  mobile: string;
  wechat: string;
  id: number;
}

const userState: UserState = {
  token: getToken() || '',
  name: '',
  avatar: '',
  role: '',
  account: '',
  totalCredits: 0,
  uid: '',
  intro: '',
  mobile: '',
  wechat: '',
  id: 0,
};

export const UserStore = defineStore('user', {
  state: () => {
    return userState;
  },
  actions: {
    async loginByWechatCode(code: String) {
      try {
        const codeData = {
          appId: isMobile() ? 'wx7dce890fbc536f39' : 'wx1932de2133a4a2ba',
          wechatCode: code,
        };

        const res = await loginByWechatCode(codeData);
        this.token = res?.data.token;

        if (this.token) {
          setToken(this.token);
          this[UserActionTypes.ACTION_GET_USER_INFO]();
        }
      } catch (error) {}
    },
    async [UserActionTypes.ACTION_LOGIN_BY_MOBILE_PASS](
      mobile: string,
      password: string
    ) {
      const data = {
        mobile,
        password,
      };
      const res = await loginByMobilePassword(data);

      this.token = res?.data.token;
      if (this.token) {
        setToken(this.token);
        this[UserActionTypes.ACTION_GET_USER_INFO]();
      }
    },
    async [UserActionTypes.ACTION_LOGIN_BY_MOBILE_CODE](
      mobile: string,
      code: string,
      area = '+86'
    ) {
      const data = {
        mobile,
        code,
        area,
      };
      const res = await loginByMobileCode(data);

      this.token = res?.data.token;
      if (this.token) {
        setToken(this.token);
        this[UserActionTypes.ACTION_GET_USER_INFO]();
      }
    },
    async [UserActionTypes.ACTION_GET_USER_INFO]() {
      try {
        const res = await userInfoRequest();
        const userData = res!.data;

        this.token = userData.token;
        this.name = userData.user.name;
        this.avatar = userData.user.avatar;
        this.account = userData.user.account;
        this.uid = userData.uid;
        this.intro = userData.user.intro;
        this.mobile = userData.user.mobile;
        this.wechat = userData.user.wechat;
        this.id = userData.user.id;
      } catch (error) {
        throw Error('Verification failed, please Login again.');
      }
    },
    async [UserActionTypes.ACTION_REGISTER](
      mobile: string,
      nickName: string,
      code: string,
      area = '+86'
    ) {
      const data = {
        mobile,
        nickName,
        code,
        area,
      };
      const res = await register(data);
      this.token = res?.data.token;
      if (this.token) {
        setToken(this.token);
        this[UserActionTypes.ACTION_GET_USER_INFO]();
      }
    },
    logout() {
      removeToken();
      location.href = location.origin + '/login';
    },
    resetToken() {
      removeToken();
      this.token = '';
    },
    async getCredits() {
      const res = await getCredits();

      if (res?.data.totalNum) {
        this.totalCredits = res?.data.totalNum;
      }
    },
  },
});
