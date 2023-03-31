<template>
  <div
    class="block md:hidden h-16 w-full bg-white fixed bottom-0 left-0"
    style="z-index: 11"
  >
    <div class="grid grid-cols-4 h-full border-t border-gray-100">
      <div
        class="flex items-center flex-col justify-center h-full"
        @click="go('/gallery')"
      >
        <div>
          <!-- <upside-down-face theme="filled" size="19" fill="#ff99ab"/> -->
          <icon-upside-down-face
            theme="outline"
            size="22"
            :fill="activeMenu == '/gallery' ? '#ff99ab' : '#777'"
          />
        </div>
        <div
          class="default-index text-xs mt-0.5"
          :style="{ color: activeMenu == '/gallery' ? '#ff99ab' : '#777' }"
        >
          发现
        </div>
      </div>

      <div
        class="flex items-center flex-col justify-center h-full"
        @click="go('/chat')"
      >
        <div>
          <!-- <upside-down-face theme="filled" size="19" fill="#ff99ab"/> -->
          <icon-send
            theme="outline"
            size="22"
            :fill="activeMenu == '/chat' ? '#ff99ab' : '#777'"
          />
        </div>

        <div
          class="default-index text-xs mt-0.5"
          :style="{ color: activeMenu == '/chat' ? '#ff99ab' : '#777' }"
        >
          聊聊
        </div>
      </div>

      <div
        class="flex items-center flex-col justify-center h-full"
        style="margin-top: -17px"
        @click="go('/image/create')"
      >
        <div>
          <icon-add-one theme="filled" size="55" fill="#ff99ab" />
        </div>
        <div
          class="default-index text-xs mt-0.5"
          :style="{
            color: activeMenu == '/image/create' ? '#ff99ab' : '#777',
          }"
        >
          画图
        </div>
      </div>
      <div
        class="flex items-center flex-col justify-center h-full"
        @click="goMy"
      >
        <div>
          <!-- <green-house theme="filled" size="19" fill="#ff99ab"/> -->
          <!-- <palace theme="filled" size="19" fill="#4a4a4a" :strokeWidth="1"/> -->
          <!-- <emotion-happy theme="filled" size="19" fill="#4a4a4a" :strokeWidth="3"/> -->
          <icon-emotion-happy
            theme="outline"
            size="22"
            :fill="activeMenu == `/${account}` ? '#ff99ab' : '#777'"
          />
        </div>
        <div
          class="default-index text-xs mt-0.5"
          :style="{
            color: activeMenu == `/${account}` ? '#ff99ab' : '#777',
          }"
        >
          我的
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, reactive, toRefs } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { UserStore } from '@/store/user';

export default defineComponent({
  name: 'MobileNavbar',
  setup() {
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();
    const methods = reactive({
      go(path: string) {
        router.push(path);
      },
      goMy() {
        if (!userStore.account) {
          router.push('/login');
          return;
        }
        router.push({
          path: `/${userStore.account}`,
        });
      },
    });

    const account = computed(() => {
      return userStore.account;
    });

    const activeMenu = computed(() => {
      return route.path;
    });

    return {
      activeMenu,
      account,
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.default-index {
  color: #4a4a4a;
}
.active-index {
  color: #000;
}
</style>
