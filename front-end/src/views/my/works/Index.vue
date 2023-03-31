<template>
  <div class="pt-6">
    <ImageWaterfall
      :method="getUserImages"
      :requestParams="searchForm"
      linkTarget="my"
    ></ImageWaterfall>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { getUserImages } from '@/apis/homepage';
import ImageWaterfall from '@/business-components/image_waterfall/Index.vue';

export default defineComponent({
  name: 'MyWorksView',
  components: {
    ImageWaterfall,
  },
  props: {
    userInfo: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const state = reactive({
      searchForm: {
        status: 1, // 1已发布 //''全部
        keyword: '',
        processStatus: 5,
        userId: props.userInfo.user.id,
      },
    });

    return {
      ...toRefs(state),
      getUserImages,
    };
  },
});
</script>

<style></style>
