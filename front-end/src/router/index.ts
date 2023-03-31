import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import ImgLayout from '@/layout/img-main/Index.vue';
import Layout from '@/layout/main/Index.vue';

const routeFiles = import.meta.glob('./routeModules/*.ts', {
  eager: true,
});

let routeModules: Array<RouteRecordRaw> = [];
Object.keys(routeFiles).forEach(async (key: string) => {
  if (key === './index.ts') return;

  const childRoute: any = routeFiles[key];
  routeModules = routeModules.concat(childRoute.default);
});

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/home',
      component: Layout,
      name: 'HomeLayout',
      children: [
        {
          path: '',
          component: () => import('@/views/home/Index.vue'),
          name: 'HomepageView',
          meta: {
            title: 'home',
            icon: '',
            keepAlive: true,
          },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)',
      redirect: '/404',
      name: 'NotFound',
    },
    {
      path: '/404',
      component: Layout,
      children: [
        {
          path: '',
          component: () => import('@/views/error-page/404.vue'),
          name: '404',
          meta: {
            title: '404',
            icon: '#',
          },
        },
      ],
    },
    {
      path: '/login',
      component: Layout,
      children: [
        {
          path: '',
          component: () => import('@/views/login/Index.vue'),
          name: 'Login',
          meta: {
            title: 'login',
            icon: '#',
          },
        },
      ],
    },
    {
      path: '/register',
      component: Layout,
      children: [
        {
          path: '',
          component: () => import('@/views/register/Index.vue'),
          name: 'Register',
          meta: {
            title: 'register',
            icon: '#',
          },
        },
      ],
    },
    {
      path: '/gallery',
      component: ImgLayout,
      children: [
        {
          path: '',
          component: () => import('@/views/gallery/Index.vue'),
          name: 'GalleryView',
          meta: {
            title: 'gallery',
            icon: '#',
            keepAlive: true,
          },
        },
      ],
    },

    {
      path: '/image/:id',
      component: ImgLayout,
      children: [
        {
          path: '',
          component: () =>
            import(
              /* webpackChunkName: "ai-image-detail" */ '@/views/ai-image/detail/Index.vue'
            ),
          name: 'ImageDetailView',
          meta: {
            title: 'imageDetail',
            icon: '#square',
          },
        },
      ],
    },
    ...routeModules,
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  },
});

export default router;
