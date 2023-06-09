<!--
 * @Description: 侧边栏item
-->

<template>
  <div
    v-if="!item.meta || !item.meta.hidden"
    :class="[isCollapse ? 'simple-mode' : 'full-mode']"
  >
    <template v-if="theOnlyOneChild && !theOnlyOneChild.children">
      <SidebarItemLink
        v-if="theOnlyOneChild.meta"
        :to="resolvePath(theOnlyOneChild.path)"
      >
        <el-menu-item :index="resolvePath(theOnlyOneChild.path)">
          <!-- <svg
            v-if="theOnlyOneChild.meta.icon"
            class="icon"
            aria-hidden="true"
            font-size="17px"
          >
            <use :xlink:href="theOnlyOneChild.meta.icon" />
          </svg> -->

          <span v-if="theOnlyOneChild.meta.title">{{
            $t('route.' + theOnlyOneChild.meta.title)
          }}</span>
        </el-menu-item>
      </SidebarItemLink>
    </template>
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <!-- popper-append-to-body -->
      <template #title>
        <svg
          v-if="item.meta && item.meta.icon"
          class="icon"
          aria-hidden="true"
          font-size="16px"
        >
          <use :xlink:href="item.meta.icon" />
        </svg>
        <span v-if="item.meta && item.meta.title">{{
          $t('route.' + item.meta.title)
        }}</span>
      </template>
      <template v-if="item.children">
        <sidebar-item
          v-for="child in item.children"
          :key="child.path"
          :item="child"
          :is-collapse="isCollapse"
          :is-first-level="false"
          :base-path="resolvePath(child.path)"
          class="nest-menu"
        />
      </template>
    </el-sub-menu>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import { isExternal } from '@/utils/validate';
import SidebarItemLink from './SidebarItemLink.vue';

export default defineComponent({
  props: {
    item: {
      type: Object,
      required: true,
    },
    isCollapse: {
      type: Boolean,
      required: false,
    },

    basePath: {
      type: String,
      required: true,
    },
  },
  components: {
    SidebarItemLink,
  },
  setup(props) {
    const alwaysShowRootMenu = computed(() => {
      if (props.item.meta && props.item.meta.alwaysShow) {
        return true;
      } else {
        return false;
      }
    });

    const showingChildNumber = computed(() => {
      if (props.item.children) {
        const showingChildren = props.item.children.filter((item: any) => {
          if (item.meta && item.meta.hidden) {
            return false;
          } else {
            return true;
          }
        });
        return showingChildren.length;
      }
      return 0;
    });

    const theOnlyOneChild = computed(() => {
      if (showingChildNumber.value > 1) {
        return null;
      }
      if (props.item.children) {
        for (const child of props.item.children) {
          if (!child.meta || !child.meta.hidden) {
            return child;
          }
        }
      }
      // If there is no children, return itself with path removed,
      // because this.basePath already conatins item's path information
      return { ...props.item, path: '' };
    });

    const resolvePath = (routePath: string) => {
      if (isExternal(routePath)) {
        return routePath;
      }
      if (isExternal(props.basePath)) {
        return props.basePath;
      }

      return props.basePath + routePath;
    };

    return {
      alwaysShowRootMenu,
      showingChildNumber,
      theOnlyOneChild,
      resolvePath,
    };
  },
});
</script>

<style lang="scss" scoped>
.el-submenu.is-active > .el-submenu__title {
  color: $subMenuActiveText !important;
}

.full-mode {
  .nest-menu .el-submenu > .el-submenu__title,
  .el-submenu .el-menu-item {
    min-width: $sideBarWidth !important;
    background-color: $subMenuBg !important;

    &:hover {
      background-color: $subMenuHover !important;
    }
  }
  .el-menu-item {
    & > span {
      display: inline-block;
      padding-left: 5px;
    }
  }
  .el-submenu {
    overflow: hidden;

    & > .el-submenu__title {
      .el-submenu__icon-arrow {
        display: none;
      }

      & > span {
        padding-left: 5px;
      }
    }
  }
}

.simple-mode {
  &.first-level {
    .submenu-title-noDropdown {
      padding: 0 !important;
      position: relative;

      .el-tooltip {
        padding: 0 !important;
      }
    }

    .el-submenu {
      overflow: hidden;

      & > .el-submenu__title {
        padding: 0px !important;

        .el-submenu__icon-arrow {
          display: none;
        }

        & > span {
          visibility: hidden;
        }
      }
    }
  }
}
</style>

<style lang="scss" scoped>
svg {
  margin-right: 16px;
}

.simple-mode {
  svg {
    margin-left: 20px;
  }
}
</style>
