<template>
  <header
    class="flex items-center justify-between fixed left-0 top-0 w-full"
    :class="{ 'shadow-sm': true, scrolled: scrollHeader }"
    style="
      --tw-shadow: 0 1px 2px 0 rgb(255 255 255 / 12%);
      transition: all 0.3s ease;
    "
    :style="{ '--tw-bg-opacity': 1, 'z-index': 999 }"
  >
    <div class="px-4 lg:px-6 flex items-center h-12 md:h-16">
      <!-- logo -->
      <div class="flex-1 flex">
        <a
          class="web-name flex items-center text-lg font-bold text-white text-shadow"
          href="/"
        >
          <!-- <img
            class="h-6 object-cover mr-1.5"
            src="@/assets/images/logo/logo_core.png"
          /> -->
          首页
          <!-- {{ $t('website') }} -->
        </a>
        <a
          class="web-name flex items-center text-lg font-bold text-white text-shadow ml-4"
          href="/"
          target="_blank"
        >
          <!-- <img
            class="h-6 object-cover mr-1.5"
            src="@/assets/images/logo/logo_core.png"
          />
          首页 -->
          TigerBot
        </a>
        <a
          class="web-name flex items-center text-lg font-bold text-white text-shadow ml-4"
          href="/image/create"
        >
          <!-- <img
            class="h-6 object-cover mr-1.5"
            src="@/assets/images/logo/logo_core.png"
          />
          首页 -->
          AI绘画
        </a>
      </div>
    </div>
    <NavBar class="flex-1 flex items-center justify-end pr-3"> </NavBar>
    <!-- <AiTools class="flex-1 flex items-center justify-end pr-3"> </AiTools> -->
  </header>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, toRefs, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { UserStore } from '@/store/user';
import LangSelect from '@/components/lang_select/Index.vue';
import AvatarLogin from '@/components/avatar_login/Index.vue';
import { getScrollInfo } from '@/utils';
import { debounce } from 'lodash-es';
import AiTools from './AiTools.vue';
import NavBar from '@/layout/components/nav/Index.vue';

export default defineComponent({
  name: 'MainNavbar',
  components: {
    LangSelect,
    AvatarLogin,
    AiTools,
    NavBar,
  },
  setup() {
    const router = useRouter();
    const store = UserStore();

    const token = computed(() => {
      return store.token;
    });

    const state = reactive({
      unReadCount: 10,
      showLoginMenu: false,
      showDropMenu: false,
      scrollHeader: false,
    });

    const scrollFnc = debounce(function () {
      const { scrollTop, clientHeight } = getScrollInfo();
      if (scrollTop >= 200) {
        state.scrollHeader = true;
      } else {
        state.scrollHeader = false;
      }
    }, 200);

    window.addEventListener('scroll', scrollFnc);

    onUnmounted(() => {
      window.removeEventListener('scroll', scrollFnc);
    });

    const methods = reactive({
      go: (path: string) => {
        router.push(path);
      },
    });

    return {
      token,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped lang="scss">
.user-drop {
  right: 0;
  z-index: 99;
  width: 260px;
}
.right-menu-item {
  display: inline-block;
  padding: 0 8px;
  height: 100%;
  font-size: 18px;
  color: #5a5e66;
  vertical-align: text-bottom;

  &.hover-effect {
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }
}
.scrolled {
  background: hsla(0, 0%, 100%, 0.8);
  -webkit-backdrop-filter: blur(30px);
  backdrop-filter: blur(30px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}
</style>
