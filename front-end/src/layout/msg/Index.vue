<template>
  <div class="page-wrapper overflow-x-hidden">
    <NavBar></NavBar>

    <div class="flex pt-65 bg-white page-wrapper-view">
      <SideBar
        class="sticky border-r"
        style="width: 260px; top: 65px"
      ></SideBar>
      <router-view class="flex-1"></router-view>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import NavBar from './components/nav_bar/Index.vue';
import SideBar from './components/side_bar/Index.vue';

export default defineComponent({
  name: 'Layout',
  components: {
    NavBar,
    SideBar,
  },

  setup() {},
});
</script>
<style scoped>
.page-wrapper {
  background: #fafafc;
  /* min-height: 100vh; */
}
.page-wrapper-view {
  min-height: 100vh;
}
</style>
