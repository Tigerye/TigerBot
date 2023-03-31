import { RouteRecordRaw } from 'vue-router';
import MyLayout from '@/layout/my/Index.vue';

const MsgRouter: Array<RouteRecordRaw> = [
  {
    path: '/:account',
    component: MyLayout,
    name: 'MyLayout',
    meta: {
      title: 'myLayout',
      icon: '',
    },
    children: [
      {
        path: '',
        component: () =>
          import(
            /* webpackChunkName: "my-works-view" */ '@/views/my/works/Index.vue'
          ),
        name: 'MyWorksView',
        meta: {
          title: 'myWorksView',
          icon: '',
          keepAlive: true,
        },
      },
      {
        path: ':id',
        component: () =>
          import(
            /* webpackChunkName: "my-image-detail" */ '@/views/my/works/Detail.vue'
          ),
        name: 'MyImageDetailView',
        meta: {
          title: 'myImageDetail',
          icon: '',
        },
      },
      {
        path: 'life',
        component: () =>
          import(
            /* webpackChunkName: "my-collections" */ '@/views/my/life/Index.vue'
          ),
        name: 'MyLife',
        meta: {
          title: 'myLife',
          icon: '',
        },
      },
      {
        path: 'collections',
        component: () =>
          import(
            /* webpackChunkName: "my-collections" */ '@/views/my/collections/Index.vue'
          ),
        name: 'MyCollections',
        meta: {
          title: 'myCollections',
          icon: '',
        },
      },
      {
        path: 'likes',
        component: () =>
          import(
            /* webpackChunkName: "my-likes" */ '@/views/my/likes/Index.vue'
          ),
        name: 'MyLikes',
        meta: {
          title: 'myLikes',
          icon: '',
        },
      },
      {
        path: 'follows',
        component: () =>
          import(
            /* webpackChunkName: "my-follows" */ '@/views/my/follows/Index.vue'
          ),
        name: 'MyFollows',
        meta: {
          title: 'myFollows',
          icon: '',
        },
      },
      {
        path: 'followers',
        component: () =>
          import(
            /* webpackChunkName: "my-followers" */ '@/views/my/followers/Index.vue'
          ),
        name: 'MyFollowers',
        meta: {
          title: 'myFollowers',
          icon: '',
        },
      },
      {
        path: 'history',
        component: () =>
          import(
            /* webpackChunkName: "my-history" */ '@/views/my/history/Index.vue'
          ),
        name: 'MyHistory',
        meta: {
          title: 'myHistory',
          icon: '',
        },
      },
    ],
  },
];

export default MsgRouter;
