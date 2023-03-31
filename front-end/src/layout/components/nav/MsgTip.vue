<template>
  <li class="relative">
    <!-- <a
      class="flex items-center font-medium text-sm group px-2 py-0.5 text-black cursor-pointer"
      href="/msg"
    >
      <el-badge
        v-if="unReadCount > 0"
        :value="unReadCount > 99 ? '99+' : unReadCount"
        class="flex items-center"
        size="small"
      >
        <icon-remind theme="outline" size="18" fill="#000" :strokeWidth="3" />
      </el-badge>
      <icon-remind
        v-else
        theme="outline"
        size="18"
        fill="#000"
        :strokeWidth="3"
      />
    </a> -->
    <el-tooltip v-if="unReadCountMap" placement="top" effect="light">
      <template #content>
        <div class="p-4" style="width: 120px">
          <div
            class="flex items-center cursor-pointer"
            @click="go('/msg/reply')"
          >
            <div class="col-span-2 text-sm" style="width: 80px">回复我的</div>
            <div v-if="unReadCountMap[3]" class="text-center text-base">
              {{ unReadCountMap[3].count }}
            </div>
          </div>
          <div
            class="flex items-center mt-4 cursor-pointer"
            @click="go('/msg/like')"
          >
            <div class="col-span-2 text-sm" style="width: 80px">收到的赞</div>
            <div v-if="unReadCountMap[4]" class="text-center text-base">
              {{ unReadCountMap[4].count }}
            </div>
          </div>
          <div
            class="flex items-center mt-4 cursor-pointer"
            @click="go('/msg/system')"
          >
            <div class="col-span-2 text-sm" style="width: 80px">系统通知</div>
            <div v-if="unReadCountMap[2]" class="text-center text-base">
              {{ unReadCountMap[2].count }}
            </div>
          </div>
        </div>
      </template>
      <a
        class="flex items-center font-medium text-sm group px-2 py-0.5 text-black cursor-pointer"
        href="/msg"
      >
        <el-badge
          v-if="unReadCount > 0"
          :value="unReadCount > 99 ? '99+' : unReadCount"
          class="flex items-center"
          size="small"
        >
          <icon-remind theme="outline" size="18" fill="#000" :strokeWidth="3" />
        </el-badge>
        <icon-remind
          v-else
          theme="outline"
          size="18"
          fill="#000"
          :strokeWidth="3"
        />
      </a>
    </el-tooltip>
    <!-- <div
      class="absolute mt-1 bg-white z-10 top-full -right-1 -mt-1 h-20 msg-box"
    ></div> -->
  </li>
</template>
<script lang="ts">
import {
  defineComponent,
  onBeforeMount,
  onBeforeUnmount,
  reactive,
  toRefs,
  watch,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  getCountNotifyUnReadGroupByType,
  getCountNotifyUnRead,
} from '@/apis/msg';
import { UnReadCountModel } from '@/model/msgModel';

interface UnReadCountMapModel {
  [key: number]: UnReadCountModel;
}

export default defineComponent({
  name: 'MsgTip',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      unReadCount: 10,
      unReadCountArr: [] as UnReadCountModel[],
      unReadCountMap: {} as UnReadCountMapModel,
    });

    const methods = reactive({
      async getCountNotifyUnRead() {
        const data = {};
        const res = await getCountNotifyUnRead(data);
        state.unReadCount = res?.data.unReadCount || [];
      },
      async getCountNotifyUnReadGroupByType() {
        const data = {};
        const res = await getCountNotifyUnReadGroupByType(data);
        state.unReadCountArr = res?.data.unReadCount || [];
        state.unReadCountArr.forEach((item) => {
          state.unReadCountMap[item.notifyType] = item;
        });
      },
      go(path: string) {
        router.push(path);
      },
    });

    onBeforeMount(() => {
      methods.getCountNotifyUnRead();
      methods.getCountNotifyUnReadGroupByType();
    });
    let timer = setInterval(() => {
      methods.getCountNotifyUnRead();
      methods.getCountNotifyUnReadGroupByType();
    }, 20000);

    onBeforeUnmount(() => {
      clearInterval(timer);
    });

    // watch(
    //   () => route.path,
    //   () => {
    //     methods.getCountNotifyUnRead();
    //     methods.getCountNotifyUnReadGroupByType();
    //   }
    // );

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.el-badge {
  display: flex;
}
.msg-box {
  right: 0;
  z-index: 99;
  width: 260px;
}
</style>
