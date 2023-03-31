<template>
  <nav class="flex-1 flex items-center justify-end pr-3">
    <ul class="flex items-center space-x-2">
      <li class="hidden md:block">
        <a
          class="flex items-center font-medium text-sm group px-2 py-0.5 text-black"
          href="/gallery"
          @click.prevent="go('/gallery')"
        >
          {{ $t('route.gallery') }}
        </a>
      </li>
      <li class="hidden md:block">
        <a
          class="flex items-center font-medium text-sm group px-2 py-0.5 text-black"
          href="/square"
          @click.prevent="go('/square')"
        >
          {{ $t('route.square') }}
        </a>
      </li>
      <li class="hidden md:block">
        <hr class="mx-2 w-0.5 h-5 border-none bg-gray-300" />
      </li>

      <Miniprogram class="hidden md:block" />
      <MsgTip class="hidden md:block" v-if="token"></MsgTip>

      <li class="relative hidden md:block">
        <a
          class="font-medium text-sm group px-2 py-0.5 block"
          href="/ai-image/create"
          @click.prevent="goCreate('/image/create')"
        >
          <div class="flex items-center">{{ $t('route.create') }}</div>
        </a>
      </li>

      <AvatarLogin></AvatarLogin>
    </ul>
  </nav>
</template>
<script lang="ts">
import { computed, defineComponent, reactive, toRefs, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { UserStore } from '@/store/user';
import LangSelect from '@/components/lang_select/Index.vue';
import AvatarLogin from '@/components/avatar_login/Index.vue';
import MsgTip from './MsgTip.vue';
import Miniprogram from './Miniprogram.vue';

export default defineComponent({
  name: 'BaseNavbar',
  components: {
    LangSelect,
    AvatarLogin,
    MsgTip,
    Miniprogram,
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const store = UserStore();

    const token = computed(() => {
      return store.token;
    });

    const state = reactive({
      unReadCount: 0,
      showLoginMenu: false,
      showDropMenu: false,
    });

    const methods = reactive({
      go: (path: string) => {
        router.push(path);
      },
      goCreate(path: string) {
        if (!store.token) {
          router.push(`/login?redirect=${path}`);
          return;
        }
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
