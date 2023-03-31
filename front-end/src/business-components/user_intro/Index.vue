<!-- 用户基本信息介绍 -->
<template>
  <div
    class="flex justify-between gap-5 items-center border-b"
    style="height: 120px"
  >
    <div class="flex overflow-hidden">
      <img
        class="w-13 h-13 rounded-full object-cover cursor-pointer"
        :src="item.logoOss"
        alt=""
        @click="goUser"
      />
      <div class="ml-2 flex flex-col justify-between py-1 overflow-hidden">
        <div class="text-pink-400 cursor-pointer" @click="goUser">
          {{ item.name }}
        </div>
        <div class="text-gray-500 text-sm truncate">
          {{ item.intro || '此作者很懒，啥也没写～～' }}
        </div>
      </div>
    </div>
    <div>
      <FollowAction
        :biz-id="item.id"
        :default-status="item.follow"
      ></FollowAction>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import FollowAction from '@/components/follow_action/Index.vue';

export default defineComponent({
  components: {
    FollowAction,
  },
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const methods = reactive({
      goUser() {
        window.open(`${location.origin}/${props.item.userAccount}`);
      },
    });

    return {
      ...toRefs(methods),
    };
  },
});
</script>
