<template>
  <div class="p-4">
    <div v-if="list && list.length > 0">
      <div>
        <div
          v-for="(item, index) in list"
          :key="index"
          class="p-4 border-b border-gray-100"
        >
          <div class="text-sm">
            <span>{{ item.title }}</span>
            <span class="ml-2 text-gray-500">{{ item.createTime }}</span>
          </div>
          <div class="mt-4 text-sm text-gray-500">
            <span>{{ item.messageEntity }}</span>
            <a
              class="underline text-blue-400 text-sm cursor-pointer ml-2"
              @click="go(item)"
              target="_blank"
              >查看详情>></a
            >
          </div>
        </div>
      </div>

      <div v-if="hasMore" class="text-center mt-4 mb-6">
        <span class="underline cursor-pointer text-sm" @click="getMore"
          >查看更多</span
        >
      </div>
    </div>
    <div v-else class="text text-gray-500">暂无系统通知</div>
  </div>
</template>
<script lang="ts">
import { defineComponent, onBeforeMount, reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';
import { SystemMsgModel } from '@/model/msgModel';
import { getNotifyPage } from '@/apis/msg';

export default defineComponent({
  setup() {
    const router = useRouter();
    const state = reactive({
      list: [] as SystemMsgModel[],
      pageNum: 1,
      pageSize: 20,
      hasMore: true,
    });

    const methods = reactive({
      go(item: SystemMsgModel) {
        router.push({
          path: '/image/' + item.bizId,
        });
      },
      async getNotifyPage() {
        const data = {
          notifyType: 2,
          pageNum: state.pageNum,
          pageSize: state.pageSize,
        };
        const res = await getNotifyPage(data);
        const result = res?.data;
        state.list = state.list.concat(result.list) || [];
        state.hasMore = result.hasMore;
      },
      getMore() {
        state.pageNum++;
        methods.getNotifyPage();
      },
    });

    onBeforeMount(() => {
      methods.getNotifyPage();
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
