<template>
  <div>
    <h3 class="text-black px-4 pt-6 pb-4">消息中心</h3>
    <el-menu
      :default-active="activeMenu"
      class="el-menu-demo"
      background-color="#fff"
      text-color="#000"
      active-text-color="#ff99ab"
      @select="handleSelect"
    >
      <el-menu-item index="/msg/system"
        >系统通知
        <span class="ml-2" v-if="unReadCountMap[2]">{{
          unReadCountMap[2].count
        }}</span>
      </el-menu-item>
      <el-menu-item index="/msg/reply"
        >回复我的
        <span class="ml-2" v-if="unReadCountMap[3]">{{
          unReadCountMap[3].count
        }}</span></el-menu-item
      >
      <el-menu-item index="/msg/like"
        >收到的赞
        <span class="ml-2" v-if="unReadCountMap[4]">{{
          unReadCountMap[4].count
        }}</span></el-menu-item
      >
    </el-menu>
  </div>
</template>

<script lang="ts">
import {
  defineComponent,
  computed,
  reactive,
  toRefs,
  onBeforeMount,
  watch,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getCountNotifyUnReadGroupByType } from '@/apis/msg';
import { UnReadCountModel } from '@/model/msgModel';

interface UnReadCountMapModel {
  [key: number]: UnReadCountModel;
}

export default defineComponent({
  setup() {
    const route = useRoute();
    const router = useRouter();

    const state = reactive({
      unReadCountArr: [] as UnReadCountModel[],
      unReadCountMap: {} as UnReadCountMapModel,
    });

    const activeMenu = computed(() => {
      const { meta, path } = route;

      return path;
    });

    const methods = reactive({
      handleSelect(key: string, keyPath: string[]) {
        router.push(key);
      },
      async getCountNotifyUnReadGroupByType() {
        const data = {};
        const res = await getCountNotifyUnReadGroupByType(data);
        state.unReadCountArr = res?.data.unReadCount || [];
        state.unReadCountArr.forEach((item) => {
          state.unReadCountMap[item.notifyType] = item;
        });
      },
    });

    onBeforeMount(() => {
      methods.getCountNotifyUnReadGroupByType();
    });

    watch(
      () => route.path,
      () => {
        methods.getCountNotifyUnReadGroupByType();
      }
    );

    return {
      ...toRefs(state),
      ...toRefs(methods),
      activeMenu,
    };
  },
});
</script>
<style scoped lang="scss">
.el-menu {
  border-right: none;
}
</style>
