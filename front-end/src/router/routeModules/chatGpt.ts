import { RouteRecordRaw } from 'vue-router';
import GptLayout from '@/layout/chat-gpt/Index.vue';

const MsgRouter: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: GptLayout,
    name: 'chat',
    meta: {
      title: 'Chat',
      icon: '',
    },
    children: [
      {
        path: '/chat',
        component: () =>
          import(
            /* webpackChunkName: "chat-home" */ '@/views/chat-gpt/Chat.vue'
          ),
        name: 'GptHome',
        meta: {
          title: 'gptHome',
          icon: '',
        },
      },
      {
        path: '',
        component: () =>
          import(
            /* webpackChunkName: "chat-detail" */ '@/views/chat-gpt/Index.vue'
          ),
        name: 'GptChat',
        meta: {
          title: 'gptChat',
          icon: '',
        },
      },
      {
        path: '/chat-next',
        component: () =>
          import(
            /* webpackChunkName: "chat-test" */ '@/views/chat-gpt/area/Test.vue'
          ),
        name: 'chatTest',
        meta: {
          title: 'TigerBot',
          icon: '',
        },
      },
    ],
  },
  {
    path: '/chatGPT',
    component: () =>
      import(
        /* webpackChunkName: "chat-gansu" */ '@/views/chat-gpt/area/gansu.vue'
      ),
    name: 'chatGanSu',
    meta: {
      title: 'TigerBot',
      icon: '',
    },
  },
];

export default MsgRouter;
