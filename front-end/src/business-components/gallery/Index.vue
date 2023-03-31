<template>
  <div class="p-3 md:p-6">
    <h1 class="text-center">艺 术 画 廊</h1>
    <div class="flex justify-center">
      <SearchGenetate
        @search="search"
        placeholder="搜索作品名称、作品内容、作者名"
      ></SearchGenetate>
    </div>

    <div class="flex justify-center mb-4 items-center mt-4">
      <span class="text-xs text-gray-600 mr-2">列数:{{ columns }} </span>
      <el-slider
        style="width: 200px"
        v-model="columns"
        :step="1"
        :min="1"
        :max="12"
        size="small"
        @change="columnChange"
      />
    </div>

    <div class="py-2 flex justify-between items-end">
      <tab-row
        v-model="tabName"
        :list="tabList"
        @tab-click="tabChange"
        :size="'large'"
      ></tab-row>

      <tab-row
        v-model="hotType"
        :list="hotTypeList"
        @tab-click="hotTypeChange"
        :size="'medium'"
      ></tab-row>
    </div>

    <ImageWaterfall
      :method="getPublicList"
      :requestParams="requestParams"
      :columns="columns"
    ></ImageWaterfall>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  watch,
  onActivated,
  onMounted,
} from 'vue';
import ImageWaterfall from '@/business-components/image_waterfall/Index.vue';
import { getPublicList } from '@/apis/image';
import TabRow from '@/components/tab/Row.vue';
import SearchGenetate from '@/components/search/Generate.vue';
import { AppStore } from '@/store/app';
import { AppActionTypes } from '@/store/app/action-types';

export default defineComponent({
  name: 'GalleryComponent',
  components: {
    ImageWaterfall,
    TabRow,
    SearchGenetate,
  },
  setup() {
    const appStore = AppStore();
    const state = reactive({
      requestParams: {
        pageNum: 1,
        pageSize: 50,
        tabType: '',
        hotType: 'latest',
        keyword: '',
      },
      tabName: '',
      hotType: 'latest',
      tabList: [
        {
          name: '',
          label: '发现',
        },
        {
          name: 'follow',
          label: '关注',
        },
      ],
      hotTypeList: [
        {
          name: 'latest',
          label: '最新',
        },
        // {
        //   value: 'oneHour',
        //   label: '最热：近一小时',
        // },
        // {
        //   value: 'oneDay',
        //   label: '最热：近一天',
        // },
        // {
        //   value: 'oneWeek',
        //   label: '最热：近一周',
        // },
        {
          name: 'all',
          label: '最热',
        },
      ],
      columns: appStore.imageColumns,
    });

    watch(
      () => appStore.imageColumns,
      () => {
        state.columns = appStore.imageColumns;
      }
    );

    const methods = reactive({
      tabChange(name: string) {
        state.tabName = name;
        state.requestParams.tabType = name;
      },
      hotTypeChange(name: string) {
        state.hotType = name;
        state.requestParams.hotType = name;
      },
      search(text: string) {
        state.requestParams.keyword = text;
      },
      columnChange() {
        appStore[AppActionTypes.ACTION_SET_COLUMN](state.columns);
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      getPublicList,
    };
  },
});
</script>
<style scoped>
.el-slider {
  --el-slider-main-bg-color: rgb(178, 176, 176);
}
</style>
