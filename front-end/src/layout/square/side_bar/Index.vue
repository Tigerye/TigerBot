<template>
  <div>
    <h3 class="text-black px-4 pt-6 pb-4" style="l">广场</h3>
    <el-menu
      :default-active="activeMenu"
      class="el-menu-demo"
      background-color="#fff"
      text-color="#000"
      active-text-color="#ff99ab"
      @select="handleSelect"
    >
      <el-menu-item index="/square/suggest">留言吐槽 </el-menu-item>
      <el-menu-item index="/square/qa">Q & A </el-menu-item>
    </el-menu>
  </div>
</template>

<script lang="ts">
import {
  defineComponent,
  computed,
  reactive,
  toRefs,
  onBeforeMount,
  watch,
} from 'vue';
import { useRoute, useRouter } from 'vue-router';

export default defineComponent({
  setup() {
    const route = useRoute();
    const router = useRouter();

    const state = reactive({});

    const activeMenu = computed(() => {
      const { meta, path } = route;

      return path;
    });

    const methods = reactive({
      handleSelect(key: string, keyPath: string[]) {
        router.push(key);
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      activeMenu,
    };
  },
});
</script>
<style scoped lang="scss">
.el-menu {
  border-right: none;
}
</style>
