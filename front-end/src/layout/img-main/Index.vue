<template>
  <div class="page-wrapper overflow-x-hidden md:pb-0">
    <NavBar></NavBar>

    <router-view
      v-slot="{ Component }"
      :key="key"
      class="page-wrapper-view relative pb-65"
    >
      <keep-alive :include="cacheViews">
        <component :is="Component" />
      </keep-alive>
    </router-view>

    <!-- <router-view class="page-wrapper-view relative pb-65"> </router-view> -->

    <MobileNav></MobileNav>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent } from 'vue';
import NavBar from './components/nav_bar/Index.vue';
import MobileNav from '@/layout/components/mobile_nav/Index.vue';
import { useRoute } from 'vue-router';
import { AppStore } from '@/store/app';

export default defineComponent({
  name: 'Layout',
  components: {
    NavBar,
    MobileNav,
  },

  setup() {
    const route = useRoute();
    const appStore = AppStore();

    const cacheViews = computed(() => {
      return appStore.cacheViews;
    });

    const key = () => {
      return route.path;
    };

    return {
      key,
      cacheViews,
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
  min-height: 100vh;
}
</style>
