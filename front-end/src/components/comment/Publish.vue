<template>
  <div class="publish-comment flex justify-between items-center mt-4">
    <div
      v-if="avatar"
      class="cursor-pointer rounded-full overflow-hidden user-logo w-10 h-10 md:w-12 md:h-12 flex-shrink-0"
    >
      <img :src="avatar" alt="" class="rounded-full object-cover w-12 h-12" />
    </div>
    <div class="flex-1 flex items-center ml-4">
      <div class="flex-1">
        <el-input
          class="block bg-gray-100"
          type="textarea"
          :rows="2"
          :placeholder="placehold"
          v-model="commentText"
          @focus="checkLogin"
          resize="none"
        >
        </el-input>
      </div>
      <div
        class="relative comment-btn ml-2 p-2 rounded-lg flex flex-col items-center justify-center font-bold cursor-pointer"
        @click="publish"
      >
        <div class="text-center">发表</div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, reactive, toRefs } from 'vue';
import { UserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';

export default defineComponent({
  name: 'PublishComment',
  props: {
    type: {
      type: Number, //1一级评论  2二级评论
      required: true,
    },
    placehold: {
      type: String,
      default: '来说点什么吧～',
    },
  },
  setup(props, context) {
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();
    const avatar = computed(() => {
      return userStore.avatar;
    });
    const token = computed(() => {
      return userStore.token;
    });

    const state = reactive({
      commentText: '',
    });

    const methods = reactive({
      checkLogin() {
        if (!token) {
          // 未登录状态要求去登录
          router.push({
            path: '/login',
            query: {
              redireact: route.path,
              ...route.query,
            },
          });
        }
      },
      publish() {
        if (!state.commentText) {
          return;
        }
        context.emit('publish', state.commentText, props.type);
        state.commentText = '';
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      avatar,
      token,
    };
  },
});
</script>
<style scoped>
.comment-btn {
  background: #ff99ab;
  height: 56px;
  width: 75px;
  color: #fff;
  font-size: 14px;
  opacity: 0.8;
  transition: all 0.2 linear;
}
.comment-btn:hover {
  opacity: 1;
}
</style>
