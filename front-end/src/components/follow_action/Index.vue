<template>
  <el-button
    :color="isFollow ? '' : '#f25d8ed9'"
    :style="{ color: isFollow ? '#807c7c' : '#fff' }"
    @click="actionFollow"
  >
    <icon-concern
      theme="outline"
      size="19"
      :fill="isFollow ? '#807c7c' : '#fff'"
    />
    <span class="ml-2">
      {{ isFollow ? '已关注' : '关注' }}
    </span>
  </el-button>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';
import { follow, cancelFollow } from '@/apis/action';
import { UserStore } from '@/store/user';
import { useRouter, useRoute } from 'vue-router';

export default defineComponent({
  name: 'FollowAction',
  props: {
    defaultStatus: Boolean,
    bizId: {
      type: Number,
      required: true,
    },
  },
  setup(props) {
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();

    const isFollow = ref(props.defaultStatus);

    const methods = reactive({
      async actionFollow() {
        if (!userStore.token) {
          router.push(`/login?redirect=${route.path}`);
          return;
        }
        const data = {
          bizId: props.bizId,
          bizType: 0,
        };

        if (isFollow.value) {
          await cancelFollow(data);
          isFollow.value = false;
        } else {
          await follow(data);
          isFollow.value = true;
        }
      },
    });

    return {
      isFollow,
      ...toRefs(methods),
    };
  },
});
</script>
