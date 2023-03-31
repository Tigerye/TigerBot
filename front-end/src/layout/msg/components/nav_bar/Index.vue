<template>
  <header
    class="flex items-center justify-between fixed left-0 top-0 w-full bg-white border-b"
    :class="{ 'shadow-sm': true }"
    style="
      --tw-shadow: 0 1px 2px 0 rgb(255 255 255 / 12%);
      transition: all 0.3s ease;
    "
    :style="{ '--tw-bg-opacity': 1, 'z-index': 999 }"
  >
    <div class="px-4 lg:px-6 flex items-center h-12 md:h-16">
      <!-- logo -->
      <div class="flex-1 flex flex-col justify-center">
        <a
          class="web-name flex items-center text-xl md:text-2xl font-bold text-white text-shadow"
          href="/"
          @click.prevent="go('/')"
        >
          {{ $t('website') }}
        </a>
      </div>
    </div>

    <NavBar class="flex-1 flex items-center justify-end pr-3"> </NavBar>
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
import NavBar from '@/layout/components/nav/Index.vue';

export default defineComponent({
  components: {
    LangSelect,
    AvatarLogin,
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
