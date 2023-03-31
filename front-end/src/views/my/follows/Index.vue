<template>
  <div class="grid md:grid-cols-2 gap-8">
    <UserIntro
      v-for="(item, index) in list"
      :key="index"
      :item="item"
    ></UserIntro>
  </div>
</template>
<script lang="ts">
import { defineComponent, onBeforeMount, reactive, toRefs } from 'vue';
import { getUserFollowList } from '@/apis/homepage';
import UserIntro from '@/business-components/user_intro/Index.vue';

export default defineComponent({
  components: {
    UserIntro,
  },
  props: {
    userInfo: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const state = reactive({
      list: [],
    });

    const data = {
      userId: props.userInfo.user.id,
      bizType: 0,
    };

    onBeforeMount(async () => {
      const res = await getUserFollowList(data);
      state.list = res?.data;
    });

    return {
      ...toRefs(state),
    };
  },
});
</script>
