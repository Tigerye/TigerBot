<template>
  <div class="output-image-box flex justify-center flex-shrink-0">
    <div class="relative" style="width: 95%">
      <div
        v-if="!hasOutput && !isLoading"
        class="grid grid-cols-1 gap-2 flex-wrap h-full"
        :class="{
          'grid-cols-2': imageNumber == 6,
          'md:grid-cols-3': imageNumber == 3 || imageNumber == 6,
          'grid-cols-3': imageNumber == 3 || imageNumber == 9,
        }"
      >
        <div
          class="flex items-center justify-center"
          style="-tw-bg-opacity: 0.6"
          v-for="(item, index) in imageNumber"
          :key="index"
        >
          <img
            class="dream-empty-image"
            :style="{
              'aspect-ratio': itemWidth / itemHeight,
            }"
            :src="emptyImg"
            alt=""
          />
        </div>
      </div>

      <div
        v-if="hasOutput && images && images.length > 0"
        class="grid grid-cols-1 gap-2 flex-wrap h-full"
        :class="{
          'grid-cols-2': imageNumber == 6,
          'md:grid-cols-3': imageNumber == 3 || imageNumber == 6,
          'grid-cols-3': imageNumber == 3 || imageNumber == 9,
        }"
      >
        <div
          class="flex items-center justify-center my-2 mx-2 cursor-pointer"
          style="-tw-bg-opacity: 0.6"
          v-for="(item, index) in images"
          :key="index"
          @click="showCurrentImage(item, index)"
        >
          <!-- <img
            v-if="item"
            :style="{
              'aspect-ratio': itemWidth / itemHeight,
            }"
            :src="item.url"
            alt=""
          /> -->
          <el-image
            v-if="item"
            :style="{
              'aspect-ratio': itemWidth / itemHeight,
            }"
            :src="item.url"
            :hide-on-click-modal="true"
            :preview-src-list="previewList"
            :initial-index="index"
            fit="cover"
            :preview-teleported="true"
          />
        </div>
      </div>
      <TipProcess
        v-if="isLoading"
        :progress="progress"
        :processStatus="processStatus"
      ></TipProcess>
    </div>

    <!-- <DialogImageDetail
      v-if="currentItemHasKeys"
      ref="imageDetailRef"
      :list="images"
      :item="currentImage"
      :index="currentIndex"
    ></DialogImageDetail> -->
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  watch,
  PropType,
  computed,
  ref,
  nextTick,
} from 'vue';
import emptyImg from '@/assets/images/create/dream-empty.png';
import TipProcess from './TipsProcess.vue';
import DialogImageDetail from '@/business-components/image_detail/Index.vue';
import { iterImageModel } from '@/model/imageModel';

// interface ImgModel {
//   url: string;
// }

export default defineComponent({
  name: 'ImageCreateOutputImageComponents',
  components: {
    TipProcess,
    DialogImageDetail,
  },
  props: {
    itemWidth: {
      type: Number,
      default: 1,
    },
    itemHeight: {
      type: Number,
      default: 1,
    },
    // 预置生成图片的数量
    nIter: {
      type: Number,
      default: 1,
    },
    // 生成的图片列表
    images: {
      type: Array as PropType<Array<iterImageModel>>,
      default: () => {
        return [] as Array<iterImageModel>;
      },
    },
    // 是否有生成的图，如果有则展示，否则展示默认图
    hasOutput: {
      type: Boolean,
    },
    // 图片生成状态
    processStatus: Number,
  },
  setup(props) {
    const imageDetailRef = ref(null);
    const nIterMarks = reactive({
      1: 3,
      2: 6,
      3: 9,
    });

    const previewList = computed(() => {
      return props.images.map((item) => {
        return item.url;
      });
    });

    const currentItemHasKeys = computed(() => {
      return Object.keys(state.currentImage).length > 0;
    });

    const imageNumber = computed(() => {
      return nIterMarks[props.nIter];
    });
    const state = reactive({
      currentIndex: 0,
      currentImage: {} as iterImageModel,
      progress: 0,
      isLoading: false,
      outputNumber: 4,
      imageWidth: props.itemWidth >= props.itemHeight ? '100%' : 'unset',
      imageHeight: props.itemWidth > props.itemHeight ? 'unset' : '100%',
      imgStyle: {
        1: {
          width: '90%',
          height: '90%',
          padding: '0',
        },
        2: {
          width: '45%',
          height: '90%',
          padding: '8px',
        },
        3: {
          width: '30%',
          height: '90%',
          padding: '8px',
        },
        4: {
          width: '45%',
          height: '40%',
          padding: '8px',
        },
        5: {
          width: '30%',
          height: '40%',
          padding: '8px',
        },
        6: {
          width: '30%',
          height: '45%',
          padding: '8px',
        },
        7: {
          width: '30%',
          height: '33%',
          padding: '8px',
        },
        8: {
          width: '30%',
          height: '33%',
          padding: '8px',
        },
        9: {
          width: '30%',
          height: '33%',
          padding: '8px',
        },
      },
    });

    const methods = reactive({
      reset() {
        state.isLoading = false;
        state.progress = 0;
      },
      showCurrentImage(item: iterImageModel, index: number) {
        state.currentImage = item;
        state.currentIndex = index;
        nextTick(() => {
          if (imageDetailRef.value) {
            (imageDetailRef.value as any).showModel = true;
          }
        });
      },
    });

    watch(
      () => props.itemWidth,
      () => {
        state.imageWidth =
          props.itemWidth >= props.itemHeight ? '100%' : 'unset';
        state.imageHeight =
          props.itemWidth > props.itemHeight ? 'unset' : '100%';
      }
    );

    watch(
      () => props.itemHeight,
      () => {
        state.imageHeight =
          props.itemWidth > props.itemHeight ? 'unset' : '100%';
        state.imageWidth =
          props.itemWidth >= props.itemHeight ? '100%' : 'unset';
      }
    );

    return {
      ...toRefs(state),
      ...toRefs(methods),
      emptyImg,
      nIterMarks,
      imageNumber,
      imageDetailRef,
      currentItemHasKeys,
      previewList,
    };
  },
});
</script>

<style scoped>
.output-image-box {
  /* padding-bottom: 65px; */
  /* height: calc(100vh - 65px - 124px); */
  /* overflow: hidden; */
}
.dream-empty-image {
  object-fit: contain;
  background-color: #373737;
  /* opacity: 0.35; */
  transition: aspect-ratio 0.1s ease;
}
</style>
