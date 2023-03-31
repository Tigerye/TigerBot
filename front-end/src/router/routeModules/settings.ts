import { RouteRecordRaw } from 'vue-router';
import SettingLayout from '@/layout/settings/Index.vue';

const MsgRouter: Array<RouteRecordRaw> = [
  {
    path: '/settings',
    component: SettingLayout,
    name: 'Setting',
    meta: {
      title: 'setting',
      icon: '',
    },
    redirect: '/settings/profile',
    children: [
      {
        path: 'profile',
        component: () =>
          import(
            /* webpackChunkName: "setting-profile" */ '@/views/settings/Index.vue'
          ),
        name: 'SettingProfile',
        meta: {
          title: 'settingProfile',
          icon: '',
        },
      },
      {
        path: 'account',
        component: () =>
          import(
            /* webpackChunkName: "setting-account" */ '@/views/settings/Account.vue'
          ),
        name: 'SettingAccount',
        meta: {
          title: 'settingAccount',
          icon: '',
        },
      },
      {
        path: 'bind-mobile',
        component: () =>
          import(
            /* webpackChunkName: "setting-bind-mobile" */ '@/views/settings/BindMobile.vue'
          ),
        name: 'BindMobile',
        meta: {
          title: 'bindMobile',
          icon: '',
        },
      },
    ],
  },
];

export default MsgRouter;
