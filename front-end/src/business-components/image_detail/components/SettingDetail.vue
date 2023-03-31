<template>
  <div class="">
    <div>
      <!-- 基础信息 -->
      <div>
        <h5 class="text-gray-500">文案</h5>
        <div class="text-sm mt-2">{{ item.text }}</div>
      </div>
      <div class="mt-3">
        <h5 class="text-gray-500">尺寸</h5>
        <div class="text-sm mt-2">{{ item.width }} × {{ item.height }}</div>
      </div>
      <div class="mb-3" v-for="(v, index) in item.inputParam" :key="index">
        <div v-if="v.modifiers && v.modifiers.length > 0" class="mt-4">
          <h5 class="text-gray-500">风格</h5>
          <div class="mt-2">
            <el-tag
              class="mb-2 mr-2"
              size="small"
              :key="i"
              v-for="(tag, i) in v.modifiers"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="mt-3" v-if="item.imageStrength">
        <h5 class="text-gray-500">图片权重</h5>
        <div class="text-sm mt-2">{{ item.imageStrength }}</div>
      </div>
      <div class="mt-3" v-if="item.promptWeight">
        <h5 class="text-gray-500">文本重要度</h5>
        <div class="text-sm mt-2">{{ item.promptWeight }}</div>
      </div>
      <div class="mt-3" v-if="item.steps">
        <h5 class="text-gray-500">图片生成步骤数</h5>
        <div class="text-sm mt-2">{{ item.steps }}</div>
      </div>
      <div class="mt-3" v-if="item.seed">
        <h5 class="text-gray-500">随机数种子</h5>
        <div class="text-sm mt-2">{{ item.seed }}</div>
      </div>
      <!-- <div class="mt-3" v-if="item.nIter">
        <h5 class="text-gray-500">图片生成数量</h5>
        <div class="text-sm mt-2">{{ item.nIter }}</div>
      </div> -->

      <!-- 原图 -->
      <div
        v-if="
          (item.role === 'OWNER' || item.role === 'ADMIN') && item.inputImage
        "
        class="mt-4"
      >
        <h5 class="text-gray-500">初始图片</h5>

        <img
          class="mt-2 cursor-pointer"
          style="max-width: 300px"
          :src="item.inputImage"
          alt=""
        />
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, PropType } from 'vue';
import { ImageDetailModel } from '@/model/imageModel';

export default defineComponent({
  name: 'SettingDetail',
  props: {
    item: {
      type: Object as PropType<ImageDetailModel>,
      required: true,
    },
  },
  setup() {},
});
</script>
