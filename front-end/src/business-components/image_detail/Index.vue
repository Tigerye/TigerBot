<template>
  <Teleport to="body">
    <div
      v-show="showModel"
      class="big-image-container w-full h-full fixed left-0 top-0 bg-gray-500 flex relative justify-center"
      :class="[posterPositionClass]"
      style="
        --tw-bg-opacity: 0.9;
        z-index: 1000;
        padding-top: 2%;
        padding-bottom: 2%;
      "
    >
      <!-- 关闭 -->
      <icon-close-one
        class="fixed right-4 top-4 text-white text-3xl cursor-pointer"
        theme="outline"
        size="25"
        fill="#ffffff"
        :strokeWidth="3"
        @click.stop="showModel = false"
      />

      <div
        v-if="currentIndex != 0 && list && list.length > 0"
        class="p-3 rounded-full flex-shrink-0 fixed cursor-pointer left-4 md:left-8"
        @click.stop.prevent.capture="prev"
        style="top: 50%; transform: translateY(-50%)"
        :style="{ opacity: opacity }"
        @mouseover="opacity = 1"
        @mouseleave="opacity = 0.4"
      >
        <icon-left-c
          theme="outline"
          size="45"
          fill="#ffffff"
          :strokeWidth="3"
        />
      </div>

      <div
        v-if="list && list.length > 0 && currentIndex < list.length - 1"
        class="p-3 rounded-full flex-shrink-0 fixed cursor-pointer right-4 md:right-8"
        @click.stop.prevent.capture="next"
        style="top: 50%; transform: translateY(-50%)"
        :style="{ opacity: opacity }"
        @mouseover="opacity = 1"
        @mouseleave="opacity = 0.4"
      >
        <icon-right-c
          theme="outline"
          size="45"
          fill="#ffffff"
          :strokeWidth="3"
        />
      </div>

      <div v-if="currentItem" class="creation-detail-box flex justify-center">
        <!-- 图 -->
        <div
          class="bg-black flex-1 no-select flex flex-col justify-between p-2"
        >
          <div
            class="flex flex-1 flex-col justify-center items-center overflow-auto"
          >
            <el-image
              :src="currentItem.outputImage"
              :preview-src-list="currentItem.iterImages"
              :initial-index="viewIndex"
              fit="contain"
              :hide-on-click-modal="true"
              style="
                max-width: 100%;
                width: auto;
                height: auto;
                max-height: 100%;
              "
            />
          </div>
          <div
            v-if="currentItem.iterImages && currentItem.iterImages.length > 0"
            class="flex justify-center mt-4"
            style="max-height: 200px"
          >
            <div class="flex gap-4 h-full" style="max-width: 90%">
              <div
                v-for="(item, index) in currentItem.iterImages"
                :key="index"
                class="max-h-full cursor-pointer"
                @click="viewChange(item, index)"
              >
                <img v-if="item" :src="item" alt="" />
              </div>
            </div>
          </div>
        </div>
        <!-- 详情信息 -->
        <div
          class="flex-shrink-0 bg-white h-full max-w-full flex flex-col"
          style="width: 350px"
        >
          <!-- 用户信息 -->
          <div
            class="flex justify-between items-center border-b border-gray-100 p-2"
          >
            <div class="flex items-center mr-4">
              <div class="no-select">
                <img
                  class="source-image w-12 h-12 rounded-full object-cover cursor-pointer"
                  :src="currentItem.user.avatar"
                  @click="goUser"
                  alt=""
                />
              </div>
              <div class="ml-2 flex flex-col justify-between flex-1">
                <div class="text-sm font-medium">
                  {{ currentItem.user.name }}
                </div>
                <div class="text-xs text-gray-400 mt-2">
                  {{ currentItem.createTime }}
                </div>
              </div>
            </div>
            <FollowAction
              :biz-id="currentItem.user.id"
              :default-status="currentItem.follow"
            ></FollowAction>
          </div>

          <div class="flex py-2 px-2 gap-5">
            <Like
              :number="currentItem.interact.thumbUpNum"
              :thumbUp="currentItem.interact.thumbUp"
              :bizId="currentItem.interact.bizId"
              :bizType="currentItem.interact.bizType"
              :theme="'white'"
              :size="'default'"
            ></Like>
            <!-- <Collect
              :bizId="currentItem.interact.bizId"
              :bizType="currentItem.interact.bizType"
              :theme="'white'"
              :size="'default'"
            ></Collect> -->
            <Share
              v-if="currentItem.status == 1"
              :item="currentItem"
              :theme="'white'"
              :size="'default'"
            ></Share>
            <Publish
              v-if="currentItem.role == 'OWNER'"
              :item="currentItem"
              :theme="'white'"
              :size="'default'"
              @success="publishSuccess"
            ></Publish>
          </div>

          <div
            class="overflow-y-auto flex-1 p-2"
            style="word-break: break-word"
          >
            <!-- 标题 -->
            <div
              class="cursor-pointer mt-2 font-medium"
              style="line-height: 1.5"
              @click="goDetail"
            >
              {{ currentItem.title || currentItem.text }}
            </div>

            <div class="border-b mt-10 mb-4">
              <tab-row
                v-model="activePath"
                :list="tabList"
                :size="'small'"
                @tab-click="tabChange"
              ></tab-row>
            </div>

            <div v-if="activePath == 'comment'" class="text-sm">
              <Comment
                :bizId="currentItem.interact && currentItem.interact.bizId"
                :bizType="currentItem.interact && currentItem.interact.bizType"
                :total="currentItem.interact && currentItem.interact.commentNum"
              ></Comment>
            </div>

            <div v-if="activePath == 'settings'">
              <!-- 作品生成信息 -->
              <SettingDetail :item="currentItem"></SettingDetail>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  PropType,
  watch,
  onMounted,
} from 'vue';
import { useRouter } from 'vue-router';
import { ImageDetailModel } from '@/model/imageModel';
import FollowAction from '@/components/follow_action/Index.vue';
import TabRow from '@/components/tab/Row.vue';
import Like from '@/components/like/Index.vue';
import Share from '@/components/share_action/Index.vue';
import Collect from '@/components/collect_action/Index.vue';
import Publish from '@/components/publish_action/Index.vue';
import SettingDetail from './components/SettingDetail.vue';
import Comment from '@/components/comment/Index.vue';
import { formatterDate } from '@/utils';

export default defineComponent({
  components: {
    FollowAction,
    TabRow,
    Like,
    Share,
    Collect,
    Publish,
    SettingDetail,
    Comment,
  },

  props: {
    list: {
      type: Array as PropType<Array<ImageDetailModel>>,
      required: true,
    },
    item: {
      type: Object as PropType<ImageDetailModel>,
      required: true,
    },
    index: {
      type: Number,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter();
    const state = reactive({
      infoType: 'comment',
      detail: {},
      opacity: 0.4,
      posterPositionClass: 'items-center',
      showModel: false,
      currentItem: props.item,
      currentIndex: props.index,
      viewIndex: 0, //预览试图索引
      activePath: 'comment',
      tabList: [
        {
          name: 'comment',
          label: '评论',
          number: props.item.interact.commentNum,
        },
        {
          name: 'settings',
          label: '参数设置',
        },
      ],
    });

    const methods = reactive({
      setCommentNumber() {
        state.tabList[0].number = state.currentItem.interact.commentNum;
      },
      prev() {
        if (state.currentIndex > 1) {
          state.currentIndex--;
        } else {
          state.currentIndex = 0;
        }
        methods.formatTime(props.list[state.currentIndex]);
        // state.currentItem = props.list[state.currentIndex];
        // state.currentItem.createTime = formatterDate(
        //   state.currentItem.createTime
        // ) as string;
        methods.setCommentNumber();
      },
      next() {
        if (state.currentIndex > props.list.length - 10) {
          // 倒数第十张的时候，触发滚动加载，更新更多图片
          const scrollHeight = document.documentElement.scrollHeight;
          const screenHeight =
            document.documentElement.clientHeight || document.body.clientHeight; //可视区高度

          const needSrollHeight = scrollHeight - screenHeight;

          window.scrollTo(0, needSrollHeight);
        }

        if (state.currentIndex >= props.list.length - 1) {
          state.currentIndex = props.list.length - 1;
        } else {
          state.currentIndex++;
        }
        methods.formatTime(props.list[state.currentIndex]);
        // state.currentItem = props.list[state.currentIndex];
        // state.currentItem.createTime = formatterDate(
        //   state.currentItem.createTime
        // ) as string;
        methods.setCommentNumber();
      },
      goUser() {
        window.open(`${location.origin}/${state.currentItem.user.account}`);
      },
      goDetail() {
        window.open(`${location.origin}/image/${state.currentItem.id}`);
      },
      publishSuccess(isPublished: boolean) {
        state.currentItem.status = isPublished ? 1 : 0;
      },
      tabChange() {},
      viewChange(item: any, index: number) {
        state.currentItem.outputImage = item;
        state.viewIndex = index;
      },
      formatTime(item: ImageDetailModel) {
        state.currentItem = Object.assign({}, item);
        state.currentItem.createTime = formatterDate(
          state.currentItem.createTime
        ) as string;
      },
    });

    methods.formatTime(props.item);

    watch(
      () => props.item.id,
      () => {
        methods.formatTime(props.item);

        state.currentIndex = props.index;
        // state.currentItem = props.item;
        // state.currentItem.createTime = formatterDate(
        //   state.currentItem.createTime
        // ) as string;
        methods.setCommentNumber();
      }
    );

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.creation-detail-box {
  width: calc(100% - 150px);
  height: 100%;
}
</style>
