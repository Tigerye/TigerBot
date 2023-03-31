import { RouteRecordRaw } from 'vue-router';
import CreatorLayout from '@/layout/creator/Index.vue';

const ImageRouter: Array<RouteRecordRaw> = [
  {
    path: '/image',
    component: CreatorLayout,
    redirect: '/image/create',
    name: 'ArtImage',
    meta: {
      title: 'artImage',
      icon: '',
    },
    children: [
      {
        path: 'create',
        component: () =>
          import(
            /* webpackChunkName: "ai-image-create" */ '@/views/ai-image/create/Index.vue'
          ),
        name: 'CreateImage',
        meta: {
          title: 'createImage',
          icon: '',
        },
      },
      {
        path: 'history',
        component: () =>
          import(
            /* webpackChunkName: "ai-image-history" */ '@/views/ai-image/history/Index.vue'
          ),
        name: 'CreateImageHistory',
        meta: {
          title: 'createImageHistory',
          icon: '',
        },
      },
      {
        path: 'c',
        component: () =>
          import(
            /* webpackChunkName: "ai-image-collection" */ '@/views/ai-image/collection/Index.vue'
          ),
        name: 'CreateImageCollection',
        meta: {
          title: 'createImageCollection',
          icon: '',
        },

        children: [
          {
            path: '',
            component: () =>
              import(
                /* webpackChunkName: "ai-image-collection-all" */ '@/views/ai-image/collection/All.vue'
              ),
            name: 'CreateImageCollectionAll',
            meta: {
              title: 'createImageCollectionAll',
              icon: '',
            },
          },
          {
            path: ':cid',
            component: () =>
              import(
                /* webpackChunkName: "ai-image-collection-detail" */ '@/views/ai-image/collection/Detail.vue'
              ),
            name: 'CreateImageCollectionDetail',
            meta: {
              title: 'createImageCollectionDetail',
              icon: '',
            },
          },
        ],
      },
    ],
  },
];

export default ImageRouter;
