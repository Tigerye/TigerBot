<template>
  <div class="page-wrapper overflow-x-hidden pb-65 md:pb-0">
    <NavBar></NavBar>
    <PersonalIntroduction
      v-if="hasUserInfo && routeName !== 'MyImageDetailView'"
      :userInfo="userInfo"
    ></PersonalIntroduction>

    <!-- <router-view
      v-if="hasUserInfo"
      v-slot="{ Component }"
      :key="key"
      class="page-wrapper-view container relative"
    >
      <keep-alive :include="cacheViews">
        <component :is="Component" :userInfo="userInfo" />
      </keep-alive>
    </router-view> -->

    <router-view
      v-if="hasUserInfo"
      class="page-wrapper-view relative container"
      :userInfo="userInfo"
    ></router-view>
    <MobileNav></MobileNav>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  onBeforeMount,
  onMounted,
  reactive,
  watch,
  toRefs,
  computed,
} from 'vue';
import NavBar from '../img-main/components/nav_bar/Index.vue';
import PersonalIntroduction from './components/Index.vue';
import { useRoute, useRouter } from 'vue-router';
import { UserStore } from '@/store/user';
import { getVisitorInfo } from '@/apis/homepage';
import { VisitorModel } from '@/model/userModel';
import MobileNav from '@/layout/components/mobile_nav/Index.vue';
import { AppStore } from '@/store/app';

export default defineComponent({
  name: 'MyLayout',
  components: {
    NavBar,
    PersonalIntroduction,
    MobileNav,
  },

  setup() {
    const userStore = UserStore();
    const appStore = AppStore();
    const router = useRouter();
    const route = useRoute();
    const state = reactive({
      userInfo: {},
    });

    const cacheViews = computed(() => {
      return appStore.cacheViews;
    });

    const key = computed(() => {
      return route.path;
    });

    const routeName = computed(() => {
      return route.name;
    });

    const hasUserInfo = computed(() => {
      return Object.keys(state.userInfo).length;
    });

    const methods = reactive({
      async getUserInfo() {
        try {
          const account = route.params.account;
          const data = {
            authorization: {
              gid: null,
              uid: userStore.uid,
              token: userStore.token,
            },
            params: {
              account: {
                account: account,
              },
            },
          };
          const res = await getVisitorInfo(data);
          state.userInfo = res?.data as VisitorModel;
        } catch (error) {
          router.push('/404');
        }
      },
    });

    onBeforeMount(async () => {
      await methods.getUserInfo();
    });

    watch(
      () => route.params,
      async () => {
        await methods.getUserInfo();
      },
      {
        deep: true,
      }
    );

    return {
      key,
      cacheViews,
      routeName,
      ...toRefs(state),
      ...toRefs(methods),
      hasUserInfo,
    };
  },
});
</script>
<style scoped>
.page-wrapper {
  background: #fafafc;
  min-height: 100vh;
}
.page-wrapper-view {
  /* min-height: 100vh; */
}
</style>
