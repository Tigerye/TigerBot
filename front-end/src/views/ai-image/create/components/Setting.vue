<template>
  <div class="p-4 pt-8">
    <div class="text-gray-400 text-sm pb-4">
      <div class="text-center">
        <!-- <span class="border p-1"
          >{{ currentCreditsUse }} * {{ settingData.nIter * 3 }} =
          {{ currentCreditsUse * settingData.nIter * 3 }}</span
        > -->
        <span class="border p-1">{{ currentCreditsUse }} </span>
        积分
      </div>
    </div>

    <!-- 模型选择 -->
    <div class="mt-4">
      <div class="flex justify-between items-center">
        <div class="text-gray-800">模型选择</div>
        <span class="text-sm text-gray-500 border p-1">{{
          settingData.modelVersion
        }}</span>
      </div>
      <div class="text-xs text-gray-400 mt-2">不同的版本擅长不同的领域</div>
      <div class="mt-2">
        <el-select
          class="w-full"
          v-model="settingData.modelVersion"
          placeholder="Select"
          size="large"
          @change="modelVersionChange"
        >
          <el-option
            v-for="item in modelVersionList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
    </div>
    <!-- 模型选择 -->
    <div class="mt-4">
      <div class="flex justify-start items-center">
        <div class="text-gray-800">风格</div>
        <SetModifier
          ref="setModifierRef"
          class="w-20 ml-4"
          @modifierChange="modifierChange"
          :default="settingData.modifiers"
          :disabled="isLoading"
        >
        </SetModifier>
      </div>
      <div class="mt-2 flex items-center">
        <div>
          <el-tag
            v-for="item in settingData.modifiers"
            :key="item"
            class="mx-1 mt-2"
            effect="plain"
            color="#fff"
            type="info"
          >
            {{ item }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 宽 -->
    <div class="mt-4">
      <div class="flex justify-between items-center">
        <div class="text-gray-800">宽</div>
        <span class="text-sm text-gray-500 border p-1">{{
          settingData.width
        }}</span>
      </div>
      <div class="text-xs text-gray-400 mt-2">生成图片的宽度</div>
      <div>
        <el-slider
          :disabled="isLoading"
          v-model="settingData.width"
          :step="64"
          :min="512"
          :max="1024"
          :marks="widthHeightMarks"
          tooltip-class="slider-style"
          @change="settingChange"
        />
      </div>
    </div>

    <!-- 高 -->
    <div class="mt-4">
      <div class="flex justify-between items-center">
        <div class="text-gray-800">高</div>
        <span class="text-sm text-gray-500 border p-1">{{
          settingData.height
        }}</span>
      </div>
      <div class="text-xs text-gray-400 mt-2">生成图片的高度</div>
      <div>
        <el-slider
          v-model="settingData.height"
          :disabled="isLoading"
          :step="64"
          :min="512"
          :max="1024"
          :marks="widthHeightMarks"
          @change="settingChange"
          tooltip-class="slider-style"
        />
      </div>
    </div>

    <!-- 初始图片 -->
    <div class="mt-4">
      <div class="flex justify-between items-center">
        <div class="text-gray-800">初始图片</div>
      </div>
      <div class="text-xs text-gray-400 mt-2">
        AI会在初始图片的基础上，结合文本以及相关参数进行创作；建议尽量选择与目标尺寸接近的参考图
      </div>
      <div class="mt-2">
        <!-- <ImageWall :disabled="isLoading"></ImageWall> -->
        <UploadImage
          class="mt-3"
          :width="settingData.width"
          :height="settingData.height"
          :disabled="isLoading"
          @imageChange="imageChange"
        ></UploadImage>
      </div>
    </div>

    <!-- 图片生成数量 -->
    <div class="mt-6">
      <div class="flex justify-between items-center">
        <div class="text-gray-800">图片生成数量</div>
        <span class="text-sm text-gray-500 border p-1">{{
          settingData.nIter * 3
        }}</span>
      </div>
      <div class="text-xs text-gray-400 mt-2">一次性生成的图片数量</div>
      <div>
        <el-slider
          :disabled="isLoading"
          v-model="settingData.nIter"
          :step="1"
          :min="1"
          :max="3"
          :marks="nIterMarks"
          tooltip-class="slider-style"
          @change="settingChange"
          :format-tooltip="formatNIter"
        />
      </div>
    </div>

    <!-- 艺术倾向 -->
    <div class="mt-10">
      <div class="flex justify-between items-center">
        <div class="text-gray-800 text-lg font-medium">艺术倾向</div>
        <el-switch
          v-model="settingData.isArtstation"
          size="large"
          active-text="开启"
          inactive-text="关闭"
        />
      </div>
    </div>

    <!-- 高阶设置 -->
    <div class="mt-10">
      <div class="flex justify-between items-center">
        <div class="text-gray-800 text-lg font-medium">高阶设置</div>
        <el-switch
          v-model="settingData.advanced"
          size="large"
          active-text="开启"
          inactive-text="关闭"
        />
      </div>
    </div>

    <div v-if="settingData.advanced">
      <!-- 图片权重 -->
      <div class="mt-4">
        <div class="flex justify-between items-center">
          <div class="text-gray-800">图片权重</div>
          <span class="text-sm text-gray-500 border p-1"
            >{{ Math.floor(settingData.imageStrength * 100) }}%</span
          >
        </div>
        <div class="text-xs text-gray-400 mt-2">
          初识图片的权重，权重越高，生成过程图片增加的噪音越少
        </div>
        <div>
          <el-slider
            v-model="settingData.imageStrength"
            :disabled="isLoading"
            :step="0.01"
            :min="0.01"
            :max="0.99"
            tooltip-class="slider-style"
            @change="settingChange"
            :format-tooltip="formatImageStrength"
          />
        </div>
      </div>

      <!-- 文本重要度 -->
      <div class="mt-4">
        <div class="flex justify-between items-center">
          <div class="text-gray-800">文本重要度</div>
          <span class="text-sm text-gray-500 border p-1">{{
            settingData.promptWeight
          }}</span>
        </div>
        <div class="text-xs text-gray-400 mt-2">
          较高的值使生成的图像更接近你的输入的文本，但是值过高会“过度生成”图像
        </div>
        <div>
          <el-slider
            v-model="settingData.promptWeight"
            :disabled="isLoading"
            :step="1"
            :min="1"
            :max="20"
            tooltip-class="slider-style"
            @change="settingChange"
          />
        </div>
      </div>

      <!-- 图片生成步骤数 -->
      <div class="mt-4">
        <div class="flex justify-between items-center">
          <div class="text-gray-800">图片生成步骤数</div>
          <span class="text-sm text-gray-500 border p-1">{{
            settingData.steps
          }}</span>
        </div>
        <div class="text-xs text-gray-400 mt-2">步骤数越大，生成时间越长</div>
        <div>
          <el-slider
            v-model="settingData.steps"
            :disabled="isLoading"
            :step="1"
            :min="35"
            :max="150"
            tooltip-class="slider-style"
            @change="settingChange"
          />
        </div>
      </div>

      <!-- 随机数种子	 -->
      <div class="mt-10">
        <div class="flex justify-between items-center">
          <div class="text-gray-800">随机数种子</div>
          <span class="text-sm text-gray-500 border p-1">{{
            settingData.seed
          }}</span>
        </div>
        <div class="text-xs text-gray-400 mt-2">
          同样的随机数可使同样的输入生成同一样的，保证图片生成可复现。默认为任意随机数
        </div>
        <div class="mt-4 flex items-center">
          <el-input-number
            v-model="settingData.seed"
            :min="1"
            :max="10000000000"
            @change="settingChange"
            class="flex-1"
          />
          <el-button class="ml-2" type="info" color="#4a4a4a" @click="setSeed"
            >随机</el-button
          >
          <!-- <el-input
            size="large"
            v-model="settingData.seed"
            placeholder="Please input"
            type="number"
          /> -->
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs, computed } from 'vue';
import { CreateStore } from '@/store/create';
import { CreateActionTypes } from '@/store/create/action-types';
import ImageWall from './ImageWall.vue';
import UploadImage from './UploadImage.vue';
import SetModifier from './SetModifier.vue';

export default defineComponent({
  name: 'ImageCreateSettingComponents',
  components: {
    ImageWall,
    UploadImage,
    SetModifier,
  },
  setup(props, context) {
    const createStore = CreateStore();
    const state = reactive({
      isLoading: false,
      settingData: createStore.settingData,
      sizeMap: createStore.imageSize,
      modelVersionList: createStore.modelVersionList,
    });

    const widthHeightMarks = reactive({
      512: '',
      576: '',
      640: '',
      704: '',
      768: '',
      832: '',
      896: '',
      960: '',
      1024: '',
    });

    const nIterMarks = reactive({
      1: '',
      2: '',
      3: '',
    });

    const currentCreditsUse = computed(() => {
      return createStore.currentCreditsUse;
    });

    const methods = reactive({
      settingChange() {
        context.emit('change', state.settingData);
        createStore[CreateActionTypes.SET_SETTING_DATA](state.settingData);
      },
      modifierChange(list: Array<string>) {
        state.settingData.modifiers = list;
      },
      imageChange(url: string) {
        state.settingData.image = url;
        context.emit('change', state.settingData);
      },
      reset() {
        state.isLoading = false;
      },
      formatNIter(value: any) {
        return `${value * 3}张`;
      },
      formatImageStrength(value: any) {
        return `${Math.floor(value * 100)}%`;
      },
      setSeed() {
        console.log(
          'Math.floor(Math.random() * 1000000000)',
          Math.floor(Math.random() * 1000000000)
        );

        state.settingData.seed = Math.floor(Math.random() * 1000000000);
      },
      modelVersionChange() {
        if (state.settingData.modelVersion == 'v1.5') {
          state.settingData.width = 512;
          state.settingData.height = 512;
          return;
        }
        if (state.settingData.modelVersion == 'v2.1') {
          state.settingData.width = 768;
          state.settingData.height = 768;
        }
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      widthHeightMarks,
      currentCreditsUse,
      nIterMarks,
    };
  },
});
</script>
<style lang="scss">
.slider-style {
  color: black;
}
.el-slider {
  --el-slider-main-bg-color: #ff99ab;
}
</style>
