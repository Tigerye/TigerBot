<template>
  <div>
    <div class="chat-gpt-bgc"></div>
    <div class="relative">
      <nav
        class="flex items-center justify-between px-2 py-1.5 md:px-2 fixed w-full"
      >
        <a
          class="flex items-center font-medium group py-0.5 text-xl md:text-2xl font-bold text-white text-shadow"
          href="/"
        >
          TigerBot
        </a>
        <!-- <ChatList></ChatList> -->
        <div class="flex items-center">
          <div
            class="flex items-center flex-col justify-center mr-2 cursor-pointer"
            @click.capture.stop="copyLink"
          >
            <icon-share-two
              theme="outline"
              size="19"
              fill="#4a4a4a"
              :strokeWidth="3"
            />
            <span class="copyLink text-xs mt-1" style="color: #4a4a4a"
              >分享</span
            >
          </div>
          <AvatarLogin></AvatarLogin>
        </div>
      </nav>
      <router-view class="pt-65"> </router-view>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import AvatarLogin from '@/components/avatar_login/Index.vue';
import ChatList from '@/views/chat-gpt/components/ChatList.vue';
import { handleClipboard } from '@/utils/clipboard';

export default defineComponent({
  components: {
    AvatarLogin,
    ChatList,
  },
  setup() {
    document.title = 'TigerBot';

    const copyLink = () => {
      handleClipboard(
        `【TigerBot, for a better world】${location.href}`,
        document.querySelector('.copyLink') as Element
      );
    };
    return {
      copyLink,
    };
  },
});
</script>

<style scoped>
.chat-gpt-bgc {
  width: 100vw;
  height: 100vh;
  background: url('https://x-pai.algolet.com/model/art_image/1675477015870110.png?Expires=1833157070&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=fcaGjucFuYpANJvYFadu6gTdAYw%3D')
    no-repeat;
  background-repeat: no-repeat;
  background-size: cover;
  opacity: 0.4;
  filter: blur(6px);
  position: absolute;
  left: 0;
  top: 0;
  z-index: -1;
  background-position: center;

  /* -webkit-mask: linear-gradient(rgb(255, 255, 255), transparent); */
}
</style>
