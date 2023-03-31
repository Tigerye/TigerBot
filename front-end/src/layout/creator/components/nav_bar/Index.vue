<template>
  <header
    class="flex items-center justify-between bg-white shadow-sm grid grid-cols-12"
    style="height: 65px"
  >
    <div class="col-span-8">
      <BreadCrumb id="breadcrumb-container" class="breadcrumb-container" />
    </div>
    <NavBar
      class="flex-1 flex items-center justify-end pr-3 col-span-4"
    ></NavBar>
  </header>
</template>

<script lang="ts">
import { defineComponent, watch, ref, computed } from 'vue';
import { useRoute } from 'vue-router';
import { CreateStore } from '@/store/create';
import LangSelect from '@/components/lang_select/Index.vue';
import Screenfull from '@/components/screenfull/Index.vue';
import AvatarLogin from '@/components/avatar_login/Index.vue';
import BreadCrumb from '@/components/bread-crumb/Index.vue';
import NavBar from '@/layout/components/nav/Index.vue';

export default defineComponent({
  components: {
    LangSelect,
    Screenfull,
    AvatarLogin,
    BreadCrumb,
    NavBar,
  },
  setup() {
    const route = useRoute();
    const createStore = CreateStore();

    let path = ref(route.path);

    watch(
      () => route.path,
      () => {
        path.value = route.path;
      }
    );

    return {
      path,
    };
  },
});
</script>
