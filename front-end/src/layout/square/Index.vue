<template>
  <div class="page-wrapper">
    <NavBar></NavBar>

    <div
      class="pt-65 container flex gap-3 mt-0 md:mt-3"
      style="max-width: 1200px"
    >
      <SideBar
        class="side-bar flex-shrink-0 bg-white sticky shadow"
        style="top: 65px; width: 200px"
      ></SideBar>
      <router-view
        class="page-wrapper-view bg-white p-4 flex-1 shadow"
      ></router-view>
      <div class="w-40">
        <div class="bg-white p-2 shadow">
          <div class="text-center mb-2">客服微信</div>
          <img src="@/assets/images/contact/kefu.jpg" alt="" />
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import NavBar from '../msg/components/nav_bar/Index.vue';
import SideBar from './side_bar/Index.vue';

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
  min-height: 100vh;
}
.side-bar {
  height: calc(100vh - 65px);
}
.page-wrapper-view {
  min-height: calc(100vh - 65px);
}
</style>
