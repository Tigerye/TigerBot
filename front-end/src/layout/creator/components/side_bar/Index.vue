<template>
  <div class="shadow-sm">
    <!-- <SidebarLogo class="mt-10" :collapse="isCollapse"></SidebarLogo> -->

    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :collapse="!isCollapse"
        :unique-opened="false"
        :default-active="activeMenu"
        :background-color="'#fff'"
        :text-color="'#162b64'"
        :active-text-color="'#ff99ab'"
        mode="vertical"
      >
        <SidebarItem
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
          :is-collapse="isCollapse"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import SidebarLogo from './SidebarLogo.vue';
import SidebarItem from './SidebarItem.vue';
import { AppStore } from '@/store/app';
import { useRoute } from 'vue-router';

export default defineComponent({
  components: {
    SidebarLogo,
    SidebarItem,
  },
  setup() {
    const appStore = AppStore();
    const route = useRoute();

    const sidebar = computed(() => {
      return appStore.sidebar;
    });

    const isCollapse = computed(() => {
      return sidebar.value.opened;
    });

    const routes = [
      {
        path: '/image/create',
        name: 'CreateImage',
        meta: {
          title: 'createImage',
          icon: '#iconcopy',
        },
      },
      {
        path: '/image/history',
        name: 'CreateImageHistory',
        meta: {
          title: 'createImageHistory',
          icon: '#iconcopy',
        },
      },
      // {
      //   path: '/image/c',
      //   name: 'CreateImageCollection',
      //   meta: {
      //     title: 'createImageCollection',
      //     icon: '#iconcopy',
      //   },
      // },
    ];

    const activeMenu = computed(() => {
      const { meta, path } = route;
      // if (meta !== null || meta !== undefined) {
      //   if (meta.activeMenu) {
      //     return meta.activeMenu
      //   }
      // }
      return path;
    });

    return {
      sidebar,
      isCollapse,
      routes,
      activeMenu,
    };
  },
});
</script>
<style lang="scss">
.scrollbar-wrapper {
  .el-menu {
    background-color: $menuBg;
    border-right: none;
  }
  .el-menu-item:hover {
    outline: 0 !important;
    color: #409eff !important;
  }

  .el-submenu__title:focus,
  .el-submenu__title:hover {
    outline: 0 !important;
    color: #fff !important;
    background: #ff99ab !important;
    border-radius: 8px !important;
  }
  .el-menu-item:hover {
    outline: 0 !important;
    color: #fff !important;
    background: #ff99ab !important;
    border-radius: 8px !important;
  }
}
</style>
