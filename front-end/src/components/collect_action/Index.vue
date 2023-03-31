<template>
  <div
    @click.stop="like"
    class="like-box inline-flex items-center rounded-lg cursor-pointer"
    style="--tw-bg-opacity: 0.6"
    :style="{
      color: isCollected ? '#f0aaaa' : theme == 'black' ? '#fff' : '#f0aaaa',
    }"
    :class="{
      'is-thumbup': isCollected,
      'bg-black': theme == 'black',
      'size-small': size == 'small',
      'size-default': size == 'default',
    }"
    title="喜欢"
  >
    <icon-star
      :theme="isCollected ? 'filled' : 'outline'"
      :size="size == 'small' ? 16 : 24"
      fill="#f08383"
    />
    <span v-if="collectedNumber" class="ml-1">{{ collectedNumber }}</span>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs, watch } from 'vue';
import { thumbUp, cancelThumbUp } from '@/apis/action';
import { UserStore } from '@/store/user';
import { useRouter, useRoute } from 'vue-router';

export default defineComponent({
  name: 'CollectionAction',
  props: {
    bizId: String,
    bizType: Number,
    collected: Boolean,
    number: Number,
    theme: {
      type: String,
      default: 'black',
    },
    size: {
      type: String,
      default: 'small',
    },
  },
  setup(props) {
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();
    const state = reactive({
      isCollected: props.collected,
      collectedNumber: props.number as number,
    });

    const methods = reactive({
      async like() {
        if (!userStore.token) {
          router.push(`/login?redirect=${route.path}`);
          return;
        }
        const data = {
          bizId: props.bizId,
          bizType: props.bizType,
        };
        if (state.isCollected) {
          // 接口：取消收藏
          state.isCollected = false;
          state.collectedNumber--;
        } else {
          // 接口：收藏
          state.isCollected = true;
          state.collectedNumber++;
        }
      },
    });

    watch(
      () => props.bizId,
      () => {
        state.isCollected = props.collected;
        state.collectedNumber = props.number as number;
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
