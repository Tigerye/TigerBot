<template>
  <div class="image-waterfall-container">
    <Waterfall
      :data="list"
      @getItemWidth="getItemWidth"
      @getColumn="getColumn"
      :columns="columns"
      :elClass="elClass"
    >
      <WaterfallItem v-for="(item, index) in list" :key="index">
        <div
          class="rounded-lg relative overflow-hidden image-box no-select"
          @click="lookDetail(item, index)"
        >
          <div
            v-if="item.processStatus == 5"
            class="w-full bg-image"
            :style="{
              height: (itemWidth / item.width) * item.height + 'px',
            }"
          >
            <img :data-url="item.outputImage" src="" class="w-full h-full" />
          </div>

          <div
            v-if="item.processStatus == 3"
            class="w-full bg-gray-300 p-4 text-sm"
            :style="{
              height: (itemWidth / item.width) * item.height + 'px',
            }"
          >
            <div>审核中...</div>
            <h4 class="mt-4">
              {{ item.text }}
            </h4>
          </div>

          <div
            v-if="item.processStatus == 2"
            class="w-full bg-gray-300 p-4 text-sm"
            :style="{
              height: (itemWidth / item.width) * item.height + 'px',
            }"
          >
            <div>系统错误：{{ item.msg }}</div>
            <h4 class="mt-4">
              {{ item.text }}
            </h4>
          </div>

          <div
            v-if="item.processStatus == 1 || item.processStatus == 0"
            class="w-full bg-gray-300 p-4 text-sm"
            :style="{
              height: (itemWidth / item.width) * item.height + 'px',
            }"
          >
            <div>AI创作中...</div>
            <h4 class="mt-4">
              {{ item.text }}
            </h4>
          </div>

          <div
            v-if="item.processStatus == 5"
            class="absolute bg-white w-full h-full left-0 top-0 p-5 bg-text overflow-hideen"
            style="--tw-bg-opacity: 0.85"
          >
            <h4 class="ellipsis-four">
              {{ item.title || item.text }}
            </h4>
            <div
              v-if="item.user"
              class="absolute bottom-1 left-2 flex items-center cursor-pointer"
              @click.stop="goUser(item.user.account)"
            >
              <img
                class="w-6 h-6 rounded-full object-cover"
                :src="item.user.avatar"
              />
              <span class="text-xs ml-1">{{ item.user.name }}</span>
            </div>
          </div>

          <Like
            v-if="item.processStatus == 5"
            class="absolute bottom-2 right-2"
            :number="item.interact.thumbUpNum"
            :thumbUp="item.interact.thumbUp"
            :bizId="item.interact.bizId"
            :bizType="item.interact.bizType"
          ></Like>

          <div v-if="moreAction" class="absolute top-0 right-0">
            <MoreAction title="更多">
              <div class="cursor-pointer">
                <el-popconfirm
                  confirm-button-text="确定"
                  cancel-button-text="不，点错了"
                  icon-color="#626AEF"
                  title="你确定要删除此图吗?"
                  @confirm="deleteImage(item.id, index)"
                  width="240"
                >
                  <template #reference>
                    <div class="text-sm font-medium">删除</div>
                  </template>
                </el-popconfirm>
              </div>
            </MoreAction>
          </div>
        </div>
      </WaterfallItem>
    </Waterfall>

    <DialogImageDetail
      ref="imageDetailRef"
      v-if="currentItemHasKeys"
      :list="list"
      :item="currentItem"
      :index="currentIndex"
    ></DialogImageDetail>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  nextTick,
  watch,
  computed,
  ref,
  onUnmounted,
  onMounted,
} from 'vue';
import Waterfall from '@/components/waterfall/Index.vue';
import WaterfallItem from '@/components/waterfall/Item.vue';
import { getMyImages, artImageDelete } from '@/apis/image';
import { ImageDetailModel } from '@/model/imageModel';
import { getScrollInfo } from '@/utils';
import { debounce } from 'lodash-es';
import Like from '@/components/like/Index.vue';
import DialogImageDetail from '../image_detail/Index.vue';

import MoreAction from '@/components/more_action/Index.vue';
import { isMobile } from '@/utils';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

export default defineComponent({
  name: 'ImageWaterfall',
  components: {
    Waterfall,
    WaterfallItem,
    Like,
    DialogImageDetail,
    MoreAction,
  },
  props: {
    method: {
      type: Function,
      default: getMyImages,
    },
    requestParams: {
      type: Object,
      default: () => {
        return {};
      },
    },
    columns: Number,
    moreAction: Boolean,
    elClass: String,
    linkTarget: String, //跳转来源： “我的” 或者 “广场”；注：不同的layout，keepalive会丢失，详情页和列表页需保持在一个layout下
  },
  setup(props, context) {
    const router = useRouter();

    const imageDetailRef = ref(null);
    const state = reactive({
      hasMore: true,
      isLoading: false,
      requestParams: {
        ...props.requestParams,
        pageNum: 1,
        pageSize: 60,
      },
      itemWidth: 300,
      column: 5,
      list: [] as ImageDetailModel[],
      currentItem: {} as ImageDetailModel,
      currentIndex: 0,
    });

    const currentItemHasKeys = computed(() => {
      return Object.keys(state.currentItem).length > 0;
    });

    const scrollFnc = debounce(function () {
      const { bottomDistance } = getScrollInfo();
      if (bottomDistance <= 120 && state.hasMore && !state.isLoading) {
        state.requestParams.pageNum++;
        methods.getImages();
      }
    }, 200);

    window.addEventListener('scroll', scrollFnc);

    onUnmounted(() => {
      window.removeEventListener('scroll', scrollFnc);
    });

    const methods = reactive({
      async getImages() {
        state.isLoading = true;
        const res: any = await props.method(state.requestParams);

        const result = res.data || {};
        const list = result.list || [];

        state.list = state.list.concat(list || []);

        state.hasMore = result.hasMore;

        context.emit('sendTotal', result.total);

        nextTick(() => {
          state.isLoading = false;
        });
      },
      getItemWidth(width: number) {
        state.itemWidth = width;
      },
      getColumn(column: number) {
        state.column = column;
      },
      goUser(account: string) {
        window.open(`${location.origin}/${account}`);
      },
      lookDetail(item: ImageDetailModel, index: number) {
        if (item.processStatus <= 1) {
          console.log('item', `/image/create?r=${item.reqId}`);

          location.href = `${location.origin}/image/create?r=${item.reqId}`;
          return;
        }
        if (item.processStatus != 5) return;
        state.currentItem = item;
        state.currentIndex = index;

        if (isMobile()) {
          if (props.linkTarget == 'my') {
            router.push({
              path: `/${item.user.account}/${state.currentItem.id}`,
            });
          } else {
            router.push({
              path: `/image/${state.currentItem.id}`,
            });
          }
          return;
        }

        nextTick(() => {
          if (imageDetailRef.value) {
            (imageDetailRef.value as any).showModel = true;
          }
        });
      },
      async deleteImage(id: number, index: number) {
        const data = {
          id,
        };

        try {
          await artImageDelete(data);
          state.list.splice(index, 1);
        } catch (error: any) {
          ElMessage({
            showClose: true,
            message: error.response.data.msg,
            type: 'error',
          });
        }
      },
    });

    // onMounted(() => {
    //   methods.getImages();
    // });
    methods.getImages();

    watch(
      () => props.requestParams,
      (newValue, OldValue) => {
        state.requestParams = {
          ...props.requestParams,
          pageNum: 1,
          pageSize: 50,
        };
        state.list = [];

        methods.getImages();
      },
      {
        deep: true,
      }
    );

    return {
      imageDetailRef,
      currentItemHasKeys,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.bg-image {
  background-color: #373737;
  background-image: url('@/assets/images/create/dream-empty.png');
  background-position: center center;
  background-size: 50%;
  background-repeat: no-repeat;
}
.bg-text {
  opacity: 0;
}
.image-box:hover .bg-text {
  opacity: 1;
  transition: all linear 0.2s;
}
h4 {
  font-size: 16px;
  line-height: 1.3em;
  font-weight: 400;
  font-family: Charter, Georgia, serif;
  color: var(--b100);
}

.demo {
  background: #099;
  max-height: 40px;
  line-height: 20px;
  overflow: hidden;
}
.demo::before {
  float: left;
  content: '';
  width: 20px;
  height: 40px;
}

.demo .text {
  float: right;
  width: 100%;
  margin-left: -20px;
  word-break: break-all;
}
.demo::after {
  float: right;
  content: '...';
  width: 20px;
  height: 20px;
  position: relative;
  left: 100%;
  transform: translate(-100%, -100%);
}
</style>
