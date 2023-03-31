<template>
  <div class="collection-page h-full p-6">
    <tab-row v-model="tabName" :list="tabList" @tab-click="tabChange"></tab-row>

    <PrivateC
      v-if="tabName == 'all' || tabName == 'private'"
      class="mt-8"
    ></PrivateC>

    <PublicC
      v-if="tabName == 'all' || tabName == 'public'"
      :class="{ 'mt-14': tabName == 'all', 'mt-8': tabName == 'public' }"
    ></PublicC>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import TabRow from '@/components/tab/Row.vue';
import PrivateC from './components/private-c/Index.vue';
import PublicC from './components/public-c/Index.vue';

export default defineComponent({
  components: {
    TabRow,
    PublicC,
    PrivateC,
  },
  setup() {
    const state = reactive({
      tabName: 'all',
      tabList: [
        {
          name: 'all',
          label: '全部收藏',
        },
        {
          name: 'private',
          label: '私有的',
        },
        {
          name: 'public',
          label: '公开的',
        },
      ],
    });

    const methods = reactive({
      tabChange(name: string) {
        state.tabName = name;
        methods.getList(name);
      },
      getList(name: string) {
        console.log('更新列表');
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped>
.collection-page {
  /* background: var(--b10); */
}
/* .h3-sansserif {
  font-size: 16px;
  line-height: 32px;
  color: var(--b100);
  font-weight: 400;
} */
</style>
