<!--
 * @Description: 主视图
-->
<template>
  <section class="app-main">
    <router-view :key="key" />
  </section>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useRoute } from 'vue-router';

export default defineComponent({
  setup() {
    const route = useRoute();

    const key = () => {
      return route.path;
    };
    return {
      key,
    };
  },
});
</script>

<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh - 65px);
  width: 100%;
  position: relative;
  z-index: 1;
  background: #f2f7ff;
  overflow-y: auto;
  // background: var(--b10);
}
</style>
