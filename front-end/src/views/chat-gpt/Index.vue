<template>
  <div class="chat-box flex flex-col items-center justify-center" style="">
    <div
      class="flex flex-col items-center md:lex-none w-full pb-4"
      style="margin-top: -10%"
    >
      <div class="text-4xl font-medium">Chat with Tiger</div>
      <div
        class="w-full px-2 flex justify-center mb-2 mt-14"
        style="max-width: 100vw"
      >
        <Search
          :width="'800px'"
          height="50px"
          type="send"
          :placeholder="'问点啥；或者让我画一幅画？如：画一只狗'"
          @search="search"
        ></Search>
      </div>
    </div>
    
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { UserStore } from '@/store/user';
import { useRouter } from 'vue-router';
import Search from '@/components/search/Index.vue';

export default defineComponent({
  components: {
    Search,
  },
  setup() {
    const userStore = UserStore();
    const router = useRouter();
    const state = reactive({
      examples: [
        {
          text: '怎么能找到好工作',
        },
        {
          text: '推荐几个适合旅游的城市',
        },
        {
          text: '我想要一个装修设计方案',
        },
      ],
      capabilities: [
        {
          text: '记住用户之前在谈话中所说的话',
        },
        {
          text: '允许用户提出后续更正',
        },
        {
          text: '保持多轮对话，像聊天一样记录内容',
        },
      ],
      limitations: [
        {
          text: '可能偶尔生成不正确的信息',
        },
        {
          text: '可能偶尔产生有害说明或有偏见的内容',
        },
        // {
        //   text: '对于2021年以后的世界了解有限',
        // },
      ],
    });

    const methods = reactive({
      goChat(text: string) {
        if (!text) return;
        if (!userStore.token) {
          router.push(`/login?redirect=/chat/tiger?q=${text}`);
          return;
        }
        router.push({
          path: '/chat',
          query: {
            q: text,
          },
        });
      },
      search(text: any) {
        if (!text.value) return;
        if (!userStore.token) {
          router.push(`/login?redirect=/chat?q=${text.value}`);
          return;
        }
        router.push({
          path: '/chat',
          query: {
            q: text.value,
          },
        });
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped lang="scss">
.chat-box {
  height: calc(100vh - 54px);
}
.c-width {
  max-width: 260px;
}
</style>
