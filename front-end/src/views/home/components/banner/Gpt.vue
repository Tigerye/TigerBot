<template>
  <div class="relative creation flex">
    <div
      style="z-index: 11; margin-top: -150px"
      class="relative flex-1 flex items-center flex-col justify-center"
    >
      <h1
        class="text-white text-shadow px-2 text-center mb-10"
        style="letter-spacing: 1px"
      >
        AI 实现文本创造自由
        <icon-smiling-face-with-squinting-eyes
          class="pt-1"
          theme="outline"
          size="31"
          fill="#fff"
          :strokeWidth="3"
        />
      </h1>
    </div>

    <div
      class="hidden md:flex absolute bottom-4 w-full justify-center"
      style="z-index: 20"
    >
      <icon-double-down
        class="action-bottom cursor-pointer"
        theme="outline"
        size="40"
        fill="#e2dada"
        @click="scrollToDown"
      />
    </div>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  onBeforeUnmount,
  ref,
  computed,
} from 'vue';
import { debounce } from 'lodash-es';
import { useRoute, useRouter } from 'vue-router';
import { UserStore } from '@/store/user';
import { getScrollInfo } from '@/utils';
import { ImageTips } from '@/constant/imageTips';
import { ElMessage } from 'element-plus';

export default defineComponent({
  name: 'HomeBanner',
  setup() {
    const inputRef = ref(null);
    const router = useRouter();
    const route = useRoute();
    const userStore = UserStore();

    const token = computed(() => {
      return userStore.token;
    });

    const state = reactive({
      activeIndex: 0,
      text: '',
      activeModify: '',
      bannerList: [
        {
          name: '',
          url: 'https://images.nightcafe.studio//assets/beautiful-woman-large.jpg?tr=w-2000,c-at_max',
        },
      ],
      tipData: {
        url: '',
        label: '',
        text: '',
      },
      modifierList: [
        {
          name: '深邃梦幻',
          text: '深邃梦幻',
          classType: '经典',
          imgUrl: 'https://x-pai.algolet.com/model/dict/image/v3/深邃梦幻.jpeg',
        },
        {
          name: '艺术肖像',
          text: '艺术肖像',
          classType: '经典',
          imgUrl: 'https://x-pai.algolet.com/model/dict/image/v3/艺术肖像.jpeg',
        },
        {
          name: 'Bon Voyage',
          text: 'Bon Voyage',
          classType: '经典',
          imgUrl:
            'https://x-pai.algolet.com/model/dict/image/v3/Bon Voyage.jpeg',
        },
      ],
    });
    // https://images.nightcafe.studio//assets/beautiful-woman-large.jpg?tr=w-2000,c-at_max
    const timer = setInterval(() => {
      if (state.activeIndex >= state.bannerList.length - 1) {
        state.activeIndex = 0;
      } else {
        state.activeIndex++;
      }
    }, 20000);

    onBeforeUnmount(() => {
      clearInterval(timer);
    });

    const methods = reactive({
      inputChange: debounce(function (e: any) {
        e.target.style.height = (e.target as any).scrollHeight + 'px';
      }, 200),

      async goCreate() {
        if (!state.text) {
          ElMessage({
            showClose: true,
            message: '请先输入要描绘的文字哦',
          });
          return;
        }

        router.push({
          path: '/image/create',
          query: {
            k: state.text + ',' + state.activeModify,
          },
        });
      },
      scrollToDown() {
        const { clientHeight } = getScrollInfo();
        window.scrollTo({
          top: clientHeight - 65,
          behavior: 'smooth',
        });
      },
      setTipData() {
        const len = ImageTips.length;
        state.tipData = ImageTips[Math.floor(Math.random() * len)];
      },
      randomTips() {
        methods.setTipData();
        state.text = state.tipData.text;
        // (inputRef.value as any).focus();
      },
      addKeyword(name: string) {
        state.activeModify = name;
      },
    });

    methods.setTipData();

    return {
      inputRef,
      token,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.creation {
  width: 100vw;
  height: 100vh;
}
.bgc-img {
  background-size: cover;
  opacity: 1;

  /* -webkit-mask: linear-gradient(rgb(255, 255, 255), rgb(255, 255, 255, 0.4)); */
}
@media (min-width: 1024px) {
  .bgc-img {
    background-position: center;
  }
}
.input-box {
  width: 80%;
  max-width: 500px;
}
.light-textarea-box {
  animation: 1.5s ease-in-out 0s infinite normal none running
    textfield-on-bg-pulse;
  display: block;
  appearance: none;
  width: 100%;
  /* padding: 10px 18px; */
  line-height: 1.2;
  font-size: 16px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica,
    Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';
  min-height: 41px;
  height: 41px;
  max-height: 600px;
  resize: none;
  border: 1px solid rgba(255, 255, 255, 0.8);
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  color: rgb(255, 255, 255);
}

input::-webkit-input-placeholder,
textarea::-webkit-input-placeholder {
  color: rgba(255, 255, 255, 0.665);
  font-size: 14px;
}
input::-moz-placeholder,
textarea::-moz-placeholder {
  /* Mozilla Firefox 19+ */
  color: white;
  font-size: 16px;
}
input:-moz-placeholder,
textarea:-moz-placeholder {
  /* Mozilla Firefox 4 to 18 */
  color: white;
  font-size: 16px;
}
input:-ms-input-placeholder,
textarea:-ms-input-placeholder {
  /* Internet Explorer 10-11 */
  color: white;
  font-size: 16px;
}

.light-textarea-box:focus {
  border: 1px solid rgb(255, 255, 255);
  background: rgba(255, 255, 255, 0.15);
}

.light-textarea-box:hover {
  background: rgba(255, 255, 255, 0.1);
}

@keyframes textfield-on-bg-pulse {
  0% {
    border-color: rgba(255, 255, 255, 0.7);
    box-shadow: 0 0 13px rgb(255 255 255 / 20%);
  }
  50% {
    border-color: rgba(255, 255, 255, 1);
    box-shadow: 0 0 13px rgb(255 255 255 / 70%);
  }
  100% {
    border-color: rgba(255, 255, 255, 0.7);
    box-shadow: 0 0 13px rgb(255 255 255 / 20%);
  }
}

.action-bottom {
  animation: 2s linear 0s infinite top-to-bottom;
}
@keyframes top-to-bottom {
  0% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(15px);
  }
  100% {
    transform: translateY(0px);
  }
}
</style>
