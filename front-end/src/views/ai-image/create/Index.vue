<template>
  <div class="create-box grid grid-cols-12 relative">
    <div
      class="flex flex-col flex-col-reserve relative pb-65 md:pb-6"
      :class="[showMobileSetting ? 'col-span-12 md:col-span-8' : 'col-span-12']"
      style="height: calc(100vh - 65px)"
    >
      <PromptForm ref="promptRef" @create="submitPrompt"></PromptForm>

      <OutputImage
        ref="outputImageRef"
        class="output-image-box flex-1 mt-2 overflow-y-auto mt-4"
        :itemWidth="settingData.width"
        :itemHeight="settingData.height"
        :nIter="settingData.nIter"
        :images="outputImages"
        :hasOutput="hasOutput"
        :processStatus="resultStatus"
      ></OutputImage>
    </div>

    <div
      class="setting-style fixed md:relative md:col-span-4 border-l bg-white creator-settting-box overflow-auto"
      :style="{ right: showMobileSetting ? 0 : '-100%' }"
    >
      <icon-close-small
        class="absolute top-1 left-1 cursor-pointer"
        theme="filled"
        size="24"
        fill="#ff99ab"
        @click="showMobileSetting = false"
      />

      <Setting ref="settingRef" @change="settingChange"></Setting>
    </div>

    <div
      class="absolute top-3 right-3 cursor-pointer"
      @click.stop="showMobileSetting = true"
    >
      <icon-setting
        class=""
        style="z-index: 30"
        theme="filled"
        size="25"
        fill="#ff99ab"
      />
    </div>

    <SignIn></SignIn>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  ref,
  computed,
  onBeforeUnmount,
  onBeforeMount,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';
import PromptForm from './components/PromptForm.vue';
import Setting from './components/Setting.vue';
import OutputImage from './components/OutputImage.vue';
import { createImage, imageGetDetailByReqId } from '@/apis/image';
import { CreateStore, SettingDataType } from '@/store/create/index';
import { UserStore } from '@/store/user';
import { ElMessage } from 'element-plus';
import { CreateActionTypes } from '@/store/create/action-types';
import { getImageSizeList } from '@/apis/image';
import SignIn from './components/SignIn.vue';
import { isMobile } from '@/utils';
import { iterImageModel } from '@/model/imageModel';

export default defineComponent({
  name: 'ImageCreateView',
  components: {
    PromptForm,
    Setting,
    OutputImage,
    SignIn,
  },

  setup() {
    // interface ImgModel {
    //   url: string;
    // }
    const router = useRouter();
    const route = useRoute();
    const createStore = CreateStore();
    const userStore = UserStore();
    const promptRef = ref(null);
    const outputImageRef = ref(null);
    const settingRef = ref(null);

    const state = reactive({
      inputParam: [
        {
          text: '',
          weight: 1,
          modifiers: [] as string[],
        },
      ],
      resultStatus: 0,
      imageDetail: {},
      outputImages: [] as Array<iterImageModel>,
      hasOutput: false,
      showMobileSetting: isMobile() ? false : true, //settting按钮显示控制
    });

    const settingData = computed(() => {
      return createStore.settingData;
    });

    const sizeFormat = async () => {
      const data = {
        styleType: 'stable',
      };
      const res = await getImageSizeList(data);
      const ratioList = res?.data.ratioList || [];
      const sizeMap = {};
      ratioList.forEach((item: any) => {
        if (item.sizes) {
          item.sizes.forEach((sizeData: any) => {
            if (sizeMap[sizeData.width]) {
              sizeMap[sizeData.width][sizeData.height] = {
                credit: sizeData.algCoin,
                id: sizeData.id,
              };
            } else {
              sizeMap[sizeData.width] = {};
              sizeMap[sizeData.width][sizeData.height] = {
                credit: sizeData.algCoin,
                id: sizeData.id,
              };
            }
          });
        }
      });
      createStore[CreateActionTypes.ACTION_SAVE_SIZE](sizeMap);
    };

    let timerGetStatus = null as any;
    let timerProcess = null as any;

    const reset = () => {
      clearInterval(timerGetStatus);
      clearInterval(timerProcess);

      (promptRef.value as any).reset();
      (outputImageRef.value as any).reset();
      (settingRef.value as any).reset();
    };

    const setLoading = () => {
      (promptRef.value as any).isLoading = true;
      (outputImageRef.value as any).isLoading = true;
      (settingRef.value as any).isLoading = true;
    };

    const getDetail = async (reqId: string | number) => {
      try {
        const data = {
          reqId,
        };

        state.resultStatus = 1;
        const res: any = await imageGetDetailByReqId(data);
        const processStatus = res.data.processStatus;
        state.imageDetail = res.data || {};
        if (processStatus == 5) {
          // 生成成功 去除轮询 去除进度条

          // const outputImage = (state.imageDetail as any).outputImage as string;

          state.outputImages = (state.imageDetail as any).iterImages.map(
            (item: string) => {
              return {
                url: item,
              };
            }
          );

          state.hasOutput = true;
          state.resultStatus = 5;
          router.push({
            path: route.path,
            query: {
              r: reqId,
            },
          });
          reset();
        }

        if (processStatus == 3) {
          // 系统审核异常，需人工审核
          state.resultStatus = 3;

          clearInterval(timerGetStatus);
          clearInterval(timerProcess);
          // setTimeout(() => {
          //   reset();
          // }, 5000);
        }

        if (processStatus == 2) {
          // 系统错误
          state.resultStatus = 2;

          clearInterval(timerGetStatus);
          clearInterval(timerProcess);
          setTimeout(() => {
            reset();
          }, 5000);
        }
      } catch (error) {
        clearInterval(timerGetStatus);
        clearInterval(timerProcess);
        ElMessage('出现了一个系统错误～');
        setTimeout(() => {
          reset();
        }, 5000);
      }
    };

    const checkTaskQueue = async (reqId: number | string) => {
      timerGetStatus = setInterval(async () => {
        await getDetail(reqId);
      }, 2000);

      timerProcess = setInterval(() => {
        if ((outputImageRef.value as any).progress >= 100) {
          (outputImageRef.value as any).progress = 100;
        } else {
          (outputImageRef.value as any).progress++;
        }
      }, 600);
    };

    const methods = reactive({
      settingChange(data: SettingDataType) {
        state.hasOutput = false;
      },
      submitPrompt(data: {
        text: string;
        weight: number;
        modifiers: string[];
      }) {
        state.hasOutput = false;

        if (settingData.value.isArtstation) {
          data.text = data.text + ' trending on artstation';
        }

        state.inputParam = [data];

        methods.create();
      },
      async create() {
        setLoading();

        const data = {
          params: {
            ...settingData.value,
            inputParam: [
              {
                ...state.inputParam[0],
                modifiers: settingData.value.modifiers,
              },
            ],
            styleType: 'stable',
          },
        };

        const res = await createImage(data);

        const reqId = res?.data.result.reqId;

        // 查看图片生成状态
        checkTaskQueue(reqId);
      },
    });

    onBeforeUnmount(() => {
      reset();
    });

    onBeforeMount(async () => {
      if (!userStore.token) {
        router.push({
          path: '/login',
          query: {
            redirect: route.path,
            ...route.query,
          },
        });
        return;
      }

      //获取图片的宽高、对应的积分数据
      await sizeFormat();

      // 从首页过来的keyword开始画图
      if (route.query.k) {
        (promptRef.value as any).placeholder = route.query.k;
        state.inputParam = [
          {
            text: route.query.k as string,
            weight: 1,
            modifiers: [],
          },
        ];
        methods.create();
        return;
      }

      // 已有状态的reqId
      if (route.query.r) {
        // 初始化，路由有reqId直接请求获取结果
        // 如果processStatus为1，则是处理中，需进行轮询
        // 如果processStatus为5，则是已完成状态，不需要轮询
        // 如果是2（错误）或3（审核中）则通知用户，不进行轮询
        await getDetail(route.query.r as string);
        (promptRef.value as any).placeholder = (state.imageDetail as any).text;

        if (state.resultStatus == 1) {
          setLoading();
          checkTaskQueue(route.query.r as string);
        }
      }
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      outputImageRef,
      promptRef,
      settingRef,
      settingData,
    };
  },
});
</script>

<style scoped>
.create-box {
  height: calc(100vh - 65px);
  overflow: hidden;
}
.creator-settting-box {
  height: calc(100vh - 65px);
}
.output-image-box {
  /* height: calc(100vh - 65px - 104px); */
}

.setting-style {
  /* position: fixed; */

  z-index: 12;
  transition: right 0.3s linear;
  width: 100%;
  padding-bottom: 65px;
}
</style>
