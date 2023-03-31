<template>
  <div v-if="userData && userData.user">
    <div
      class="bgc-img w-full h-40 left-0 top-0"
      :style="{
        'background-image': `url(https://x-pai.algolet.com/model/art_image/1520767362072139.png?Expires=1819937590&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=yCthGfd8RGfP2YMAmKpN37%2BZy5w%3D)`,
      }"
    ></div>
    <div class="container">
      <div
        class="avatar-user-info flex justify-between flex-col md:flex-row items-center"
      >
        <div class="flex items-start">
          <div class="w-20 h-20 md:w-30 md:h-30 relative flex-shrink-0">
            <img
              class="w-full h-full rounded-full object-cover -mt-8 md:-mt-12"
              :src="userData.user.avatar"
              alt=""
            />
            <div
              v-if="userData.role == 'OWNER'"
              class="avatar-set absolute w-full h-full left-0 top-0 bg-black rounded-full text-center flex justify-center items-center text-white text-base cursor-pointer"
              style="margin-top: -40px; --tw-bg-opacity: 0.6"
              @click="goSetting"
            >
              更换头像
            </div>
          </div>
          <div class="ml-4 mt-2">
            <div class="text-sm md:text-lg font-medium">
              {{ userData.user.name }}
            </div>
            <div class="text-xs md:text-sm mt-2">
              {{ userData.user.intro || '此作者很懒，暂无介绍～～' }}
            </div>
          </div>
        </div>
        <div v-if="userData.role != 'OWNER'">
          <FollowAction
            :biz-id="userData.user.id"
            :default-status="userData.follow"
          ></FollowAction>

          <el-button color="#0b0f19a1">
            <icon-send-email theme="outline" size="17" fill="#fff" />
            <span class="ml-2">发信息</span>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import FollowAction from '@/components/follow_action/Index.vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'MyIntroductionComponent',
  components: {
    FollowAction,
  },
  props: {
    userInfo: Object,
  },
  setup(props) {
    const router = useRouter();
    const state = reactive({
      userData: props.userInfo,
    });

    const methods = reactive({
      goSetting() {
        router.push('/settings');
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped>
.bgc-img {
  z-index: 0;
  height: 240px;
  width: 100%;
  background-repeat: no-repeat;
  background-size: cover;
  background-position: center;
}
.avatar-set {
  opacity: 0;
  transition: all 0.2s linear;
}
.avatar-set:hover {
  opacity: 1;
}
</style>
