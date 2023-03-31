<template>
  <div class="empty-collection relative flex items-center justify-center">
    <img
      alt="Empty collection"
      src="@/assets/images/create/empty_collection_desktop.webp"
    />
    <div
      class="empty-collection-content absolute left-1/2 top-1/2 translate-1/2"
    >
      <h4 class="text-base text-center">{{ title }}</h4>
      <div class="mb-8 text-sm text-gray-600 mt-2 text-center">
        {{ subTitle }}
      </div>
      <div class="text-center">
        <el-button color="#000" size="large" class="w-40" @click="goCreate"
          >去创作</el-button
        >
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  props: {
    title: {
      type: String,
      required: true,
    },
    subTitle: String,
  },
  setup() {
    const router = useRouter();
    const methods = reactive({
      goCreate() {
        router.push({
          path: '/image/create',
        });
      },
    });

    return {
      ...toRefs(methods),
    };
  },
});
</script>
