<template>
  <div class="creator-wrapper overflow-x-hidden pt-12 md:pt-65 md:pt-0">
    <!-- MainNavBar 移动端兼容 -->
    <MainNavBar></MainNavBar>

    <SideBar
      class="sidebar-container hidden md:block"
      style="top: 65px"
    ></SideBar>

    <div class="main-container">
      <!-- <div class="hidden md:block" :class="{ 'fixed-header': true }">
        <NavBar></NavBar>
      </div> -->
      <AppMain class="pb-0"></AppMain>
    </div>

    <!-- MobileNav 移动端使用 -->
    <MobileNav></MobileNav>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import SideBar from './components/side_bar/Index.vue';
import NavBar from './components/nav_bar/Index.vue';
import AppMain from './components/AppMain.vue';
import MobileNav from '@/layout/components/mobile_nav/Index.vue';
import MainNavBar from '../msg/components/nav_bar/Index.vue';

export default defineComponent({
  name: 'Layout',
  components: {
    NavBar,
    SideBar,
    AppMain,
    MobileNav,
    MainNavBar,
  },

  setup() {},
});
</script>
<style scoped lang="scss">
.creator-wrapper {
  background: #fff;
  min-height: 100vh;
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;
}
.creator-wrapper-view {
  min-height: 100vh;
}

.sidebar-container {
  transition: width 0.28s;
  width: $sideBarWidth !important;
  height: 100%;
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1;
  overflow: hidden;
  // background-color: #ffffff !important;
}

.main-container {
  min-height: 100%;
  transition: margin-left 0.28s;
  // margin-left: $sideBarWidth;
  position: relative;
}

.fixed-header {
  position: fixed;
  top: 0;
  right: 0;
  z-index: 2;
  width: calc(100% - #{$sideBarWidth});
  transition: width 0.28s;
}

@media (max-width: 950px) {
  .main-container {
    margin-left: 0;
  }
}

@media (min-width: 950px) {
  .main-container {
    margin-left: $sideBarWidth;
  }
}
</style>
