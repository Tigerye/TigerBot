<template>
  <!-- <router-view v-slot="{ Component }" :key="key">
    <keep-alive>
      <component :is="Component" />
    </keep-alive>
  </router-view> -->
  <router-view />
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import { useRoute } from 'vue-router';
import { AppStore } from '@/store/app';

export default defineComponent({
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
<style lang="scss"></style>
