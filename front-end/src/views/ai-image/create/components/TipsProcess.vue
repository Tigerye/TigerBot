<template>
  <Transition name="fade">
    <div
      class="absolute left-0 top-0 w-full h-full flex items-center justify-center"
      style="--tw-bg-opacity: 0.2"
    >
      <div class="process-tips bg-white p-4 rounded-lg">
        <el-progress
          :text-inside="true"
          :stroke-width="20"
          :percentage="progress"
          color="orange"
        >
        </el-progress>

        <div
          v-if="processStatus == 3"
          class="text-center text-gray-600 text-xs py-3"
        >
          系统检查存在敏感信息，已转入人工审核，审核完成后会立刻通知您，您可以在
          <span class="text-orange-500 cursor-pointer" @click="goHistory"
            >“历史”</span
          >
          中查看哦～
        </div>

        <div
          v-if="processStatus == 2"
          class="text-center text-red-500 text-sm py-2"
        >
          出现了一个系统错误，请重试QAQ～
        </div>

        <div class="grid grid-cols-5 mt-3 gap-4">
          <div class="col-span-2 flex items-center justify-ceneter">
            <img class="" :src="tipData.url" alt="" />
          </div>
          <div class="col-span-3 flex flex-col justify-between py-4">
            <div>
              <div class="text-gray-700 text-sm">小贴士</div>
              <div class="text-gray-400 text-sm mt-2">
                {{ tipData.label }}
              </div>
            </div>

            <div class="text-sm text-gray-800 mt-2">
              比如：{{ tipData.text }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { useRouter } from 'vue-router';
import { ImageTips } from '@/constant/imageTips';

export default defineComponent({
  name: 'ImageTipProgressComponents',
  props: {
    progress: Number,
    processStatus: Number,
  },
  setup() {
    const router = useRouter();

    const state = reactive({
      tipData: {
        url: '',
        text: '',
        label: '',
      },
    });

    const methods = reactive({
      goHistory() {
        router.push({
          path: '/image/history',
        });
      },

      randomTips() {
        const len = ImageTips.length;
        state.tipData = ImageTips[Math.floor(Math.random() * len)];
      },
    });

    methods.randomTips();

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.process-tips {
  width: 600px;
  max-width: 100%;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
