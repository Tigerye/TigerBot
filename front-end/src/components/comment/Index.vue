<template>
  <div class="w-full overflow-hidden">
    <!-- 输入框 -->
    <Publish @publish="publishAction" :type="1"></Publish>

    <!-- 评论展示 -->
    <div v-if="commentList && commentList.length > 0" class="mt-10">
      <!-- 用户评论盒子 -->
      <div class="flex mb-6" v-for="(item, index) in commentList" :key="index">
        <!-- 头像 -->
        <div class="flex-shrink-0">
          <img
            class="w-10 h-10 md:w-12 md:h-12 rounded-full object-cover cursor-pointer"
            @click="goUser(item)"
            :src="item.userAvatar"
            alt=""
          />
        </div>
        <!-- 一级用户昵称，评论内容 -->
        <div class="flex-1 ml-4">
          <div class="">
            <span class="text-gray-500 font-bold text-xs">{{
              item.userName
            }}</span>
            <span class="text-gray-400 ml-1 text-xs">{{
              item.createTime
            }}</span>
          </div>
          <div class="comment-text mt-2 text-xs" v-html="item.content"></div>
          <div class="text-gray-400 text-xs mt-2">
            <span>
              <icon-good-one
                class="cursor-pointer"
                :theme="item.thumbUp ? 'filled' : 'outline'"
                size="14"
                fill="#4a4a4a"
                @click="toggleThumbUp(item.id, 5, item.thumbUp, item)"
              />
              {{ item.thumbUpNum }}
            </span>
            <span class="ml-2">
              <icon-bad-one
                class="cursor-pointer"
                :theme="item.thumbDown ? 'filled' : 'outline'"
                size="14"
                fill="#4a4a4a"
                @click="toggleThumbDown(item.id, 5, item.thumbDown, item)"
              />
              {{ item.thumbDownNum }}
            </span>
            <span class="ml-4 cursor-pointer text-gray-700" @click="reply(item)"
              >回复</span
            >

            <el-popconfirm
              v-if="item.userId == userId"
              title="确定要删除此评论吗?"
              confirm-button-text="确定"
              cancel-button-text="再想想"
              @confirm="deleteComment(item.id)"
            >
              <template #reference>
                <span class="ml-2 cursor-pointer">
                  <icon-delete
                    theme="outline"
                    size="12"
                    fill="rgba(156, 163, 175)"
                  />
                </span>
              </template>
            </el-popconfirm>
          </div>

          <!-- 二级评论@回复信息 -->
          <div class="flex mt-4" v-for="(v, i) in item.subCommentList" :key="i">
            <!-- 头像 -->
            <div class="flex-shrink-0" style="margin-top: 1px">
              <img
                class="w-6 h-6 rounded-full object-cover"
                :src="v.userAvatar"
                @click="goUser(v)"
                alt=""
              />
            </div>
            <!-- 用户昵称，评论内容 -->
            <div class="flex-1 ml-2">
              <div class="flex flex-wrap">
                <div class="">
                  <span class="text-gray-500 font-bold text-xs">{{
                    v.userName
                  }}</span>
                  <span
                    v-if="item.userId !== v.replyUserId"
                    class="comment-text"
                    >回复</span
                  >
                  <el-link v-if="v.userName !== v.replyUserName" type="primary"
                    >@{{ v.replyUserName }}</el-link
                  >:
                  <span class="comment-text text-xs" v-html="v.content"></span>
                  <span class="ml-1 text-xs text-gray-400">{{
                    v.createTime
                  }}</span>
                </div>
              </div>
              <div class="text-gray-400 text-xs mt-2">
                <span>
                  <icon-good-one
                    class="cursor-pointer"
                    :theme="v.thumbUp ? 'filled' : 'outline'"
                    size="14"
                    fill="#4a4a4a"
                    @click="toggleThumbUp(v.id, 5, v.thumbUp, v)"
                  />
                  {{ v.thumbUpNum }}
                </span>
                <span class="ml-2">
                  <icon-bad-one
                    class="cursor-pointer"
                    :theme="v.thumbDown ? 'filled' : 'outline'"
                    size="14"
                    fill="#4a4a4a"
                    @click="toggleThumbDown(v.id, 5, v.thumbDown, v)"
                  />
                  {{ v.thumbDownNum }}
                </span>
                <span
                  class="ml-3 cursor-pointer text-gray-700"
                  @click="secReply(item, v)"
                  >回复</span
                >
                <!-- <span
                  v-if="v.userId == userId"
                  class="ml-4"
                  @click="deleteComment(v.id)"
                >
                  <icon-delete theme="outline" size="14" fill="#4a4a4a" />
                </span> -->
                <el-popconfirm
                  v-if="item.userId == userId"
                  title="确定要删除此评论吗?"
                  confirm-button-text="确定"
                  cancel-button-text="再想想"
                  @confirm="deleteComment(v.id)"
                >
                  <template #reference>
                    <span class="ml-2 cursor-pointer">
                      <icon-delete
                        theme="outline"
                        size="12"
                        fill="rgba(156, 163, 175)"
                      />
                    </span>
                  </template>
                </el-popconfirm>
              </div>

              <!-- 评论回复信息 -->
            </div>
          </div>

          <!-- 二级评论发布框 -->
          <Publish
            v-if="item.id === currentCommentBoxId && showCommentBox"
            @publish="publishAction"
            :placehold="placeholdUser"
            :type="2"
          ></Publish>
          <div class="border-b border-gray-100 pb-6"></div>
        </div>
      </div>
    </div>

    <div v-if="hasMore" class="text-center">
      <span
        class="underline cursor-pointer text-sm text-gray-700"
        @click="getMore"
        >加载更多</span
      >
    </div>
  </div>
</template>
<script lang="ts">
import {
  computed,
  defineComponent,
  onBeforeMount,
  reactive,
  toRefs,
  watch,
} from 'vue';
import { UserStore } from '@/store/user';
import { useRouter, useRoute } from 'vue-router';
import Publish from './Publish.vue';
import { commentModel } from '@/model/comment';
import { getCommentList, addComment, deleteComment } from '@/apis/comment';
import {
  thumbUp,
  cancelThumbUp,
  thumbDown,
  cancelThumbDown,
} from '@/apis/action';
import { ElMessage } from 'element-plus';
import { formatterDate } from '@/utils';

export default defineComponent({
  name: 'CommentBox',
  components: {
    Publish,
  },
  props: {
    total: Number,
    bizId: {
      type: String,
      required: true,
    },
    bizType: {
      type: Number,
      required: true,
    },
  },
  setup(props, context) {
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();

    const userId = computed(() => {
      return userStore.id;
    });

    const token = computed(() => {
      return userStore.token;
    });

    const state = reactive({
      commentList: [] as Array<commentModel>,
      currentCommentBoxId: 0,
      showCommentBox: false,
      commentId: 0,
      placeholdUser: '',
      pageNum: 1,
      hasMore: true,
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
          return false;
        }
        return true;
      },
      async getCommentList() {
        const data = {
          bizId: props.bizId,
          bizType: props.bizType,
          pageNum: state.pageNum,
          pageSize: 10,
        };
        const res = await getCommentList(data);
        const result = res?.data;
        let list = result.list || [];
        list.forEach((item: any) => {
          item.createTime = formatterDate(item.createTime);
          if (item.subCommentList) {
            item.subCommentList.forEach((v: any) => {
              v.createTime = formatterDate(v.createTime);
            });
          }
        });
        state.hasMore = result.hasMore;
        state.commentList = state.commentList.concat(list);
      },
      async getMore() {
        if (state.hasMore) {
          state.pageNum++;
          await methods.getCommentList();
        }
      },
      async publishAction(content: string, type: number) {
        if (type == 1) {
          // 一级评论没有replyCommentId
          state.commentId = 0;
        }
        const data = {
          replyCommentId: state.commentId,
          content,
          bizId: props.bizId, //
          bizType: props.bizType, //
        };
        try {
          await addComment(data);
          // if (type == 1) {
          //   ElMessage('评论成功，刷新后查看');

          //   return;
          // }
          methods.reset();
          ElMessage('评论成功');
        } catch (error) {}
      },
      goUser(item: commentModel) {
        window.open(`${location.origin}/${item.user.account}`);
      },
      async toggleThumbUp(
        bizId: number,
        bizType: number,
        state: boolean,
        item: commentModel
      ) {
        if (!methods.checkLogin()) return;
        const data = {
          bizId,
          bizType,
        };
        if (state) {
          await cancelThumbUp(data);
          item.thumbUp = false;
          item.thumbUpNum -= 1;
          return;
        }

        await thumbUp(data);
        item.thumbUp = true;
        item.thumbUpNum += 1;
      },
      async toggleThumbDown(
        bizId: number,
        bizType: number,
        state: boolean,
        item: commentModel
      ) {
        if (!methods.checkLogin()) return;
        const data = {
          bizId,
          bizType,
        };
        if (state) {
          await cancelThumbDown(data);
          item.thumbDown = false;
          item.thumbDownNum -= 1;
          return;
        }

        await thumbDown(data);
        item.thumbDown = true;
        item.thumbDownNum += 1;
      },
      secReply(item: commentModel, v: commentModel) {
        if (!methods.checkLogin()) return;
        state.currentCommentBoxId = item.id;
        state.commentId = v.id;
        state.placeholdUser = `回复 @${v.userName}：`;
        state.showCommentBox = true;
      },
      reply(item: commentModel) {
        if (!methods.checkLogin()) return;

        if (
          state.currentCommentBoxId === state.commentId &&
          state.currentCommentBoxId === item.id
        ) {
          state.showCommentBox = false;
          state.currentCommentBoxId = 0;
          return;
        }

        state.currentCommentBoxId = item.id;
        state.commentId = item.id;
        state.placeholdUser = `回复 @${item.userName}：`;
        state.showCommentBox = true;
      },
      reset() {
        state.commentList = [] as Array<commentModel>;
        state.currentCommentBoxId = 0;
        state.showCommentBox = false;
        state.commentId = 0;
        state.placeholdUser = '';
        state.pageNum = 1;
        methods.getCommentList();
      },
      async deleteComment(id: number) {
        const data = {
          id,
        };
        try {
          await deleteComment(data);
          methods.reset();
        } catch (error) {}
      },
    });

    onBeforeMount(() => {
      methods.getCommentList();
    });

    watch(
      () => props.bizId,
      () => {
        methods.reset();
      }
    );

    return {
      userId,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped></style>
