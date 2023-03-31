<template>
  <div class="flex justify-center">
    <div style="width: 95%" class="">
      <!-- <div class="mt-4 flex items-center">
        <span class="text-xs font-medium text-gray-500"
          >用一个详细的描述开始你的绘画</span
        >
        <div
          class="px-2 py-1 ml-1 bg-gray-200 rounded text-xs font-medium hover:text-gray-600 cursor-pointer"
          style="--tw-bg-opacity: 0.8"
          @click="setTipData"
        >
          试一下
        </div>
      </div> -->

      <!-- desktop -->
      <!-- <form v-if="!isMobile" class="image-prompt-form desktop mt-3">
        <input
          type="text"
          v-model="text"
          @focusin="isVisited = true"
          @focusout="isVisited = false"
          :placeholder="placeholder"
          class="text-input text-input-medium text-input-outlined image-prompt-input"
        />

        <button
          class="btn btn-lg btn-secondary image-prompt-btn"
          :class="{
            'btn-loading': isLoading,
            'btn-disabled-style': !text,
            'btn-filled': text,
            'btn-disabled': !text || isLoading,
            'active-style': isVisited,
          }"
          :disabled="!text"
          @click="goCreate"
        >
          <span v-if="!isLoading">创作</span>

          <icon-loading-four
            v-if="isLoading"
            class="to-rotate w-full"
            theme="outline"
            size="18"
            fill="#9b9b9b"
          />
        </button>
      </form> -->

      <!-- mobile -->
      <div class="image-prompt-form mobile mt-3">
        <textarea
          v-model="text"
          @focusin="isVisited = true"
          @focusout="isVisited = false"
          :placeholder="placeholder"
          class="text-input text-input-medium text-input-outlined text-input-full image-prompt-input"
        />
        <button
          class="btn btn-large btn-filled btn-secondary image-prompt-btn"
          :class="{
            'btn-loading': isLoading,
            'btn-filled': text,
            'btn-disabled': isLoading,
            'active-style': isVisited,
          }"
          @click.stop="goCreateOrTry"
        >
          <span v-if="!isLoading" class="text-xl">
            {{ !text ? '体验演示案例' : '创作' }}
          </span>

          <icon-loading-four
            v-if="isLoading"
            class="to-rotate w-full"
            theme="outline"
            size="18"
            fill="#9b9b9b"
          />
        </button>
      </div>

      <!-- <div class="mt-3 flex flex-col flex-col-reverse md:flex-row flex-wrap">
        <div>
          <el-tag
            v-for="item in modifiers"
            :key="item"
            class="mx-1 mt-2"
            effect="plain"
            color="#fff"
            type="info"
          >
            {{ item }}
          </el-tag>
        </div>
        <SetModifier
          ref="setModifierRef"
          @modifierChange="modifierChange"
          :default="modifiers"
          :disabled="isLoading"
        >
        </SetModifier>
      
      </div> -->
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs, ref } from 'vue';
import { useRoute } from 'vue-router';
import { ImageTips } from '@/constant/imageTips';
import SetModifier from './SetModifier.vue';
import { isMobile } from '@/utils';

export default defineComponent({
  name: 'ImageCreatePromptComponents',
  components: {
    SetModifier,
  },
  setup(props, context) {
    const route = useRoute();
    const state = reactive({
      text: '',
      placeholder: '例子：海绵宝宝 柯南 派大星 海底世界 星星 爱心',
      isLoading: false,
      isVisited: false,
      modifiers: [] as Array<string>,
      isMobile: isMobile(),
    });
    const setModifierRef = ref(null);

    const methods = reactive({
      goCreateOrTry() {
        if (state.text) {
          methods.goCreate();
          return;
        } else {
          methods.setTipData();
        }
      },
      goCreate() {
        const data = {
          text: state.text,
          weight: 1,
          // modifiers: state.modifiers,
        };
        context.emit('create', data);
        state.placeholder = state.text;
        state.text = '';
        state.isLoading = true;
      },
      reset() {
        state.isLoading = false;
      },
      setTipData() {
        const len = ImageTips.length;

        state.text = ImageTips[Math.floor(Math.random() * len)].text;
      },
      // modifierChange(list: Array<string>) {
      //   state.modifiers = list;
      // },

      clearModifier() {
        state.modifiers = [];
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      setModifierRef,
    };
  },
});
</script>

<style scoped>
.image-prompt-form.desktop {
  align-items: center;
  display: flex;
  height: 44px;
  height: var(--image-prompt-input-height);
}
.image-prompt-form {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 4px 0 rgb(0 0 82 / 15%);
  overflow: hidden;
  transition: box-shadow 0.2s ease-in-out;
}
.image-prompt-input {
  border: none;
  flex: 1 1;
  height: 100%;
}
.text-input-outlined {
  /* border: 1px solid var(--input-border); */
  border-radius: 6px;
  padding: 13px 14px;
}
.text-input {
  background-clip: padding-box;
  background-color: #fff;
  box-sizing: border-box;
  display: inline-block;
  font-family: var(--sans-serif);
  font-size: 15px;
  font-weight: 400;
  line-height: 1em;
  margin: 0;
  resize: none;
  vertical-align: top;
  width: 200px;
}
.text-input::placeholder {
  color: var(--b40);
}
.desktop .image-prompt-btn {
  align-items: center;
  background-color: #fff;
  border-left: 1px solid rgba(0, 0, 0, 0.05);
  display: flex;
  font-size: 16px;
  height: 100%;
  padding-left: 14px;
  padding-right: 16px;
}
.mobile .image-prompt-btn:hover {
  background-color: pink;
}
.mobile > .image-prompt-input {
  height: 120px;
}
.mobile .image-prompt-btn {
  background-color: var(--b0);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  width: 100%;
}
.text-input-full {
  display: block;
  width: 100%;
}
.btn-secondary.btn-filled,
.btn-secondary.btn-filled:visited {
  color: #000;
}
.image-prompt-btn {
  border-radius: 0;
  cursor: pointer;
  transition: color 0.2s ease-in-out, background-color 0.2s ease-in-out;
}
.btn-disabled-style {
  background-color: #ececf1;
  border-color: transparent;
  color: rgba(0, 0, 0, 0.5);
  font-weight: 500;
}

.btn-disabled {
  cursor: default;
  pointer-events: none;
}
.desktop .image-prompt-btn.active-style:not(.btn-loading):not(.btn-disabled) {
  background-color: var(--b100);
  color: var(--b0);
}

.to-rotate {
  animation: rotate360 2s ease-in infinite;
}
@keyframes rotate360 {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
