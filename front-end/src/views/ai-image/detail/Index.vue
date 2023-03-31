<template>
  <div class="">
    <div class="relative">
      <div
        class="bgc-img absolute w-full h-full left-0 top-0"
        :style="{
          'background-image': `url(${detailData.outputImage})`,
        }"
      ></div>
      <div class="pt-12 md:pt-65 container px-0">
        <div class="w-full bg-gray-100 flex justify-center">
          <img :src="detailData.outputImage" alt="" />
        </div>
      </div>
    </div>

    <div class="container bg-white pb-8">
      <div class="py-8 border-b border-gray-100">
        <h3 class="flex-1" style="line-height: 1.5">
          {{ detailData.title || detailData.text }}
        </h3>
        <div v-if="detailData.interact" class="flex items-center gap-5 mt-8">
          <Like
            :number="detailData.interact.thumbUpNum"
            :thumbUp="detailData.interact.thumbUp"
            :bizId="detailData.interact.bizId"
            :bizType="detailData.interact.bizType"
            :theme="'white'"
            :size="'default'"
          ></Like>
          <!-- <Collect
            :bizId="detailData.interact.bizId"
            :bizType="detailData.interact.bizType"
            :theme="'white'"
            :size="'default'"
          ></Collect> -->
          <Share
            v-if="detailData.status == 1"
            :item="detailData"
            :theme="'white'"
            :size="'default'"
          ></Share>
          <Publish
            v-if="detailData.role == 'OWNER'"
            :item="detailData"
            :theme="'white'"
            :size="'default'"
            @success="publishSuccess"
          ></Publish>
        </div>
      </div>

      <!-- 用户信息 -->
      <div
        class="flex justify-between items-center border-gray-100 py-4 border-b"
      >
        <div class="flex items-center mr-4">
          <div>
            <img
              v-if="detailData.user"
              class="source-image w-13 h-13 rounded-full object-cover cursor-pointer"
              :src="detailData.user.avatar"
              @click="goUser"
              alt=""
            />
          </div>
          <div class="ml-3 flex flex-col justify-between flex-1">
            <div v-if="detailData.user">
              {{ detailData.user.name }}
            </div>
            <div class="text-xs text-gray-400 mt-3">
              {{ detailData.createTime }}
            </div>
          </div>
        </div>
        <FollowAction
          v-if="detailData.user && detailData.role != 'OWNER'"
          :biz-id="detailData.user.id"
          :default-status="detailData.follow"
        ></FollowAction>
      </div>

      <!-- 自定义参数 -->
      <h4 class="mt-4">参数设置</h4>
      <SettingDetail class="mt-6" :item="detailData"></SettingDetail>
    </div>

    <div class="container bg-white pb-8 pt-4 mt-4">
      <h4>系列图</h4>
      <div class="grid grid-cols-3 gap-4 mt-4">
        <div v-for="(item, index) in detailData.iterImages" :key="index">
          <!-- <img v-if="item" :src="item" alt="" /> -->

          <el-image
            style="max-width: 350px"
            :src="item"
            :preview-src-list="detailData.iterImages"
            :initial-index="index"
            :hide-on-click-modal="true"
            fit="cover"
            :preview-teleported="true"
          />
        </div>
      </div>
    </div>
    <div v-if="detailData.gridImage" class="container bg-white pb-8 pt-4 mt-4">
      <h4 class="flex items-center">
        <span>系列组合图</span>
      </h4>
      <div class="flex gap-4 mt-4">
        <!-- <img
          v-if="detailData.gridImage"
          :src="detailData.gridImage"
          style="max-width: 600px"
          alt=""
        /> -->
        <el-image
          style="max-width: 600px"
          :src="detailData.gridImage"
          :preview-src-list="[detailData.gridImage]"
          :initial-index="0"
          :hide-on-click-modal="true"
          fit="cover"
          :preview-teleported="true"
        />
      </div>
    </div>
    <div class="container bg-white pb-8 pt-4 mt-4">
      <h4>
        评论 {{ detailData.interact ? detailData.interact.commentNum : 0 }}
      </h4>
      <Comment
        v-if="detailData.interact && detailData.interact.bizId"
        :bizId="detailData.interact && detailData.interact.bizId"
        :bizType="detailData.interact && detailData.interact.bizType"
        :total="detailData.interact && detailData.interact.commentNum"
      ></Comment>
    </div>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  onBeforeMount,
  onMounted,
  reactive,
  toRefs,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { imageGetDetailById } from '@/apis/image';
import { ImageDetailModel } from '@/model/imageModel';
import FollowAction from '@/components/follow_action/Index.vue';
import SettingDetail from '@/business-components/image_detail/components/SettingDetail.vue';
import Like from '@/components/like/Index.vue';
import Share from '@/components/share_action/Index.vue';
import Collect from '@/components/collect_action/Index.vue';
import Publish from '@/components/publish_action/Index.vue';
import Comment from '@/components/comment/Index.vue';
import { formatterDate } from '@/utils';

export default defineComponent({
  name: 'ImageDetailView',
  components: {
    FollowAction,
    SettingDetail,
    Like,
    Share,
    Collect,
    Publish,
    Comment,
  },
  setup() {
    const route = useRoute();
    const router = useRouter();
    const id = route.params.id as string;

    const state = reactive({
      detailData: {} as ImageDetailModel,
    });

    const methods = reactive({
      goUser() {
        window.open(`${location.origin}/${state.detailData.user.account}`);
      },
      publishSuccess(isPublished: boolean) {
        state.detailData.status = isPublished ? 1 : 0;
      },
    });

    onBeforeMount(async () => {
      const data = {
        id,
      };

      const res = await imageGetDetailById(data);

      const result = res?.data;

      // 未完成的，重定向到404
      if (result.processStatus != 5) {
        router.push('/404');
        return;
      }

      // 未发布状态下，仅本人和管理员可见
      if (
        result.status == 0 &&
        result.role != 'OWNER' &&
        result.role != 'ADMIN'
      ) {
        router.push('/404');
        return;
      }

      state.detailData = result;
      state.detailData.createTime = formatterDate(
        state.detailData.createTime
      ) as string;
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.bgc-img {
  background-size: cover;
  opacity: 0.3;
  filter: blur(8px);
  background-position: center;
  -webkit-mask: linear-gradient(rgb(255, 255, 255), transparent);
}
</style>
