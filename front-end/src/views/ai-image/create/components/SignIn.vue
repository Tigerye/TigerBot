<template>
  <div class="absolute">
    <!-- 进去创作页面后，如果没有签到，系统自动签到 -->
  </div>
</template>
<script lang="ts">
import { defineComponent, onBeforeMount, reactive, toRefs } from 'vue';
import { UserStore } from '@/store/user';
import { signIn, checkHasSignIn } from '@/apis/user';

export default defineComponent({
  name: 'ImageSigninComponents',
  setup() {
    const userStore = UserStore();
    const state = reactive({
      hasSignIn: false,
    });

    onBeforeMount(async () => {
      const res = await checkHasSignIn();
      state.hasSignIn = res?.data.hasSignIn;
      if (state.hasSignIn) return;

      await signIn();
      await checkHasSignIn();
      userStore.getCredits();
    });

    return {
      ...toRefs(state),
    };
  },
});
</script>
