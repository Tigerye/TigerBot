import { RouteRecordRaw } from 'vue-router';
import SquareLayout from '@/layout/square/Index.vue';

const SquareRouter: Array<RouteRecordRaw> = [
  {
    path: '/square',
    component: SquareLayout,
    redirect: '/square/qa',
    name: 'Square',
    meta: {
      title: 'square',
      icon: '',
    },
    children: [
      {
        path: 'qa',
        component: () =>
          import(
            /* webpackChunkName: "square-qa" */ '@/views/square/qa/Index.vue'
          ),
        name: 'SquareQa',
        meta: {
          title: 'squareQa',
          icon: '',
        },
      },
      {
        path: 'suggest',
        component: () =>
          import(
            /* webpackChunkName: "square-suggest" */ '@/views/square/suggest/Index.vue'
          ),
        name: 'SquareSuggest',
        meta: {
          title: 'squareSuggest',
          icon: '',
        },
      },
    ],
  },
];

export default SquareRouter;
