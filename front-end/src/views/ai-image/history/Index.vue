<template>
  <div class="p-6 history-box">
    <div class="flex items-center mb-6 text-gray-400 gap-3 text-sm">
      <div>
        <div class="flex items-center gap-2">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 20 20"
          >
            <path
              fill-rule="evenodd"
              clip-rule="evenodd"
              d="M10 3.25A3.75 3.75 0 0 0 6.25 7v1.25h-1v7.5h9.5v-7.5h-1V7A3.75 3.75 0 0 0 10 3.25Zm2.25 5V7a2.25 2.25 0 0 0-4.5 0v1.25h4.5Z"
              fill="currentColor"
            ></path>
          </svg>
          <div>仅自己可见的</div>
        </div>
      </div>
      <div class="">|</div>
      <div>{{ total }} 作品</div>
    </div>

    <EmptyC
      v-if="total <= 0"
      title="您生成的全部图片都在这里"
      subtitle=""
    ></EmptyC>

    <ImageWaterfall :method="getMyImages" :moreAction="true"></ImageWaterfall>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { getMyImages } from '@/apis/image';
import EmptyC from '../collection/components/EmptyC.vue';
import ImageWaterfall from '@/business-components/image_waterfall/Index.vue';

export default defineComponent({
  name: 'ImageHistoryView',
  components: {
    ImageWaterfall,
    EmptyC,
  },
  setup() {
    const state = reactive({
      scrollEl: null as unknown,
      total: 0,
      requestParams: {
        pageNum: 1,
        pageSize: 1,
      },
    });

    const methods = reactive({
      async getImages() {
        const res: any = await getMyImages(state.requestParams);

        const result = res.data || {};

        state.total = result.total;
      },
    });

    methods.getImages();

    return {
      getMyImages,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
