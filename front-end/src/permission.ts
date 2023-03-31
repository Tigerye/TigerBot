// import NProgress from 'nprogress';
// import 'nprogress/nprogress.css';
import router from '@/router';
// import { useI18n } from 'vue-i18n';

import { RouteLocationNormalized, LocationQuery } from 'vue-router';
import { UserStore } from './store/user';
import { UserActionTypes } from './store/user/action-types';
import { AppStore } from './store/app';
import { AppActionTypes } from './store/app/action-types';

// NProgress.configure({ showSpinner: false });
// const { t } = useI18n();

// const getPageTitle = (key: string) => {
//   console.log('key', key);
//   console.log('t', t);
//   const hasKey = t(`route.${key}`);
//   console.log('hasKey', hasKey);

//   if (hasKey) {
//     const pageName = t(`route.${key}`);
//     return `${pageName}`;
//   }
//   return `小算法`;
// };

function getOtherQuery(query: LocationQuery) {
  return Object.keys(query).reduce((acc, cur) => {
    if (cur !== 'code') {
      acc[cur] = query[cur];
    }
    return acc;
  }, {} as LocationQuery);
}

router.beforeEach(
  async (
    to: RouteLocationNormalized,
    _: RouteLocationNormalized,
    next: any
  ) => {
    // 需要缓存的路由
    const appStore = AppStore();
    appStore[AppActionTypes.ACTION_ADD_CACHE_VIEW](to);

    // Start progress bar
    // NProgress.start();
    const store = UserStore();
    // Determine whether the user has logged in

    if (store.token) {
      if (to.path === '/login') {
        // If is logged in, redirect to the home page
        next({ path: '/' });
        // NProgress.done();
      } else {
        try {
          await store[UserActionTypes.ACTION_GET_USER_INFO]();
          next();
        } catch (err) {
          // Remove token and redirect to login page
          store.resetToken();
          next(`/login?redirect=${to.path}`);
          // NProgress.done();
        }
      }
    } else {
      const otherQuery = getOtherQuery(to.query);

      const code = to.query.code;
      if (code) {
        await store.loginByWechatCode(code as string);
        next({ path: to.path, query: otherQuery });
      }

      next();
    }
  }
);

router.afterEach((to: RouteLocationNormalized) => {
  // Finish progress bar
  // hack: https://github.com/PanJiaChen/vue-element-admin/pull/2939
  // NProgress.done();
  // set page title
  // if (to.meta.title) {
  //   document.title = getPageTitle(to.meta.title as string);
  // }
});
