import { RouteRecordRaw } from 'vue-router';
import MsgLayout from '@/layout/msg/Index.vue';

const MsgRouter: Array<RouteRecordRaw> = [
  {
    path: '/msg',
    component: MsgLayout,
    redirect: '/msg/system',
    name: 'Msg',
    meta: {
      title: 'msg',
      icon: '',
    },
    children: [
      {
        path: 'system',
        component: () =>
          import(/* webpackChunkName: "msg-system" */ '@/views/msg/System.vue'),
        name: 'MsgSystem',
        meta: {
          title: 'msgSystem',
          icon: '',
        },
      },
      {
        path: 'reply',
        component: () =>
          import(/* webpackChunkName: "msg-system" */ '@/views/msg/Reply.vue'),
        name: 'MsgReply',
        meta: {
          title: 'msgReply',
          icon: '',
        },
      },
      {
        path: 'like',
        component: () =>
          import(/* webpackChunkName: "msg-system" */ '@/views/msg/Like.vue'),
        name: 'MsgLike',
        meta: {
          title: 'msgLike',
          icon: '',
        },
      },
    ],
  },
];

export default MsgRouter;
