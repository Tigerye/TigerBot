<template>
  <div
    class="like-box rounded-lg cursor-pointer flex items-center"
    style="--tw-bg-opacity: 0.6"
    :style="{
      color: isShared ? '#f0aaaa' : theme == 'black' ? '#fff' : '#f0aaaa',
    }"
    :class="{
      'bg-black': theme == 'black',
      'size-small': size == 'small',
      'size-default': size == 'default',
    }"
    title="分享"
  >
    <div @click.stop="share" class="inline-flex items-center">
      <icon-share-two
        :theme="'filled'"
        :size="size == 'small' ? 16 : 24"
        fill="#f08383"
      />
      <span v-if="shareNumber" class="ml-1">{{ shareNumber }}</span>
    </div>

    <ShareImage
      v-if="item"
      ref="shareImageRef"
      :image="item[targetImgKey || 'outputImage']"
      :title="item.title || item.text"
      :author="item.user.name"
      :id="item.id"
    ></ShareImage>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  ref,
  toRefs,
  PropType,
  nextTick,
  watch,
} from 'vue';
import { UserStore } from '@/store/user';
import { useRouter, useRoute } from 'vue-router';
import ShareImage from './share_image/Index.vue';
import { ImageDetailModel } from '@/model/imageModel';

export default defineComponent({
  name: 'ShareAction',
  components: {
    ShareImage,
  },
  props: {
    bizId: String,
    bizType: Number,
    number: Number,
    item: Object as PropType<ImageDetailModel>,
    theme: {
      type: String,
      default: 'black',
    },
    size: {
      type: String,
      default: 'small',
    },
    shared: Boolean,
    targetImgKey: String,
  },
  setup(props) {
    const shareImageRef = ref(null);
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();
    const state = reactive({
      isShared: props.shared,
      shareNumber: (props.number as number) || 0,
    });

    const methods = reactive({
      async share() {
        if (!shareImageRef.value) return;
        (shareImageRef.value as any).showModel = true;

        nextTick(() => {
          methods.countShare();
        });
      },
      countShare() {
        const data = {
          bizId: props.bizId,
          bizType: props.bizType,
        };
        if (state.isShared) {
          // 接口：取消分享
          state.isShared = false;
          state.shareNumber--;
        } else {
          // 接口：分享，只要点击就分享次数+1
          state.isShared = true;
          state.shareNumber++;
        }
      },
    });

    watch(
      () => props.bizId,
      () => {
        state.isShared = props.shared;
        state.shareNumber = props.number as number;
      }
    );

    return {
      shareImageRef,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.like-box:hover {
  color: #f0aaaa;
}
.size-small {
  padding-left: 0.325rem;
  padding-right: 0.325rem;
  padding-top: 0.225rem;
  padding-bottom: 0.225rem;

  font-size: 12px;
}

.is-thumbup {
  animation: size-change 0.5s 0s ease-in-out;
}
@keyframes size-change {
  0% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.25);
  }
  50% {
    transform: scale(1);
  }
  75% {
    transform: scale(0.75);
  }
  100% {
    transform: scale(1);
  }
}
</style>
