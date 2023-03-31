<template>
  <div class="p-4">
    <div v-if="list && list.length > 0">
      <div
        v-for="(item, index) in list"
        :key="index"
        class="flex items-center justify-between py-4 border-b border-gray-100"
      >
        <div class="flex items-center">
          <img
            class="w-10 h-10 object-cover rounded-full cursor-pointer"
            @click="goUser(item)"
            :src="item.comment.user.avatar"
            alt=""
          />
          <div class="ml-3">
            <div
              class="font-medium text-sm cursor-pointer"
              @click="goUser(item)"
            >
              {{ item.comment.user.name }}
              <span class="text-gray-500">评论了我：</span>
            </div>
            <div class="text-sm mt-1">
              {{ item.comment.content }}
            </div>
            <div class="text-xs text-gray-500 mt-1">
              {{ item.comment.createTime }}
            </div>
          </div>
        </div>
        <div
          class="text-gray-600 cursor-pointer"
          style="max-width: 300px"
          @click="goDetail(item)"
        >
          {{ item.commentOn ? item.commentOn.content : item.sourceData.title }}
        </div>
      </div>

      <div v-if="hasMore" class="text-center mt-4 mb-6">
        <span class="underline cursor-pointer text-sm" @click="getMore"
          >查看更多</span
        >
      </div>
    </div>
    <div v-else class="text-gray-500">暂无回复信息～～</div>
  </div>
</template>
<script lang="ts">
import { defineComponent, onBeforeMount, reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';
import { SystemMsgModel } from '@/model/msgModel';
import { getCommentMeList } from '@/apis/msg';

export default defineComponent({
  setup() {
    const router = useRouter();
    const state = reactive({
      list: [] as any[],
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
      async getCommentMeList() {
        const data = {
          pageNum: state.pageNum,
          pageSize: state.pageSize,
        };
        const res = await getCommentMeList(data);
        const result = res?.data;
        let list = result.list.filter((item: any) => {
          return item.businessDetail && item.businessDetail.bizType == 31;
        });
        list = list.map((item: any) => {
          const bizType = item.businessDetail.bizType;

          let sourceData = {};
          switch (bizType) {
            case 31: //艺术图
              sourceData = {
                link: `${location.origin}/image/${item.businessDetail.id}`,
                title: item.businessDetail.title,
              };
              break;
            default:
              break;
          }
          item.sourceData = sourceData;
          return item;
        });
        state.list = state.list.concat(list);

        state.hasMore = result.hasMore;
      },
      getMore() {
        state.pageNum++;
        methods.getCommentMeList();
      },
      goUser(item: any) {
        window.open(`${location.origin}/${item.comment.user.account}`);
      },
      goDetail(item: any) {
        window.open(item.sourceData.link);
      },
    });

    onBeforeMount(() => {
      methods.getCommentMeList();
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
