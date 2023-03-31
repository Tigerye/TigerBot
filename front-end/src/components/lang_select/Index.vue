<!--
 * @Description: 语言选择
 * @Author: ZX
 * @Date: 2022-11-15 
 * @LastEditors: ZX
 * @LastEditTime: 2021-04-02 14:33:32
-->
<template>
  <div>
    <el-dropdown>
      <!-- <svg
        class="icon"
        aria-hidden="true"
        font-size="45px"
        :class="{ 'svg-color': isWhite }"
      >
        <use xlink:href="#iconzhongyingwen" />
      </svg> -->

      <div>{{ t('language') }}</div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="item in languages"
            :key="item.value"
            :disabled="language === item.value"
          >
            <span @click="handleSetLanguage(item.value)">{{ item.name }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
import { AppStore } from '@/store/app';
import { computed, defineComponent, reactive, toRefs } from 'vue';
import { AppActionTypes } from '@/store/app/action-types';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
type Language = {
  name: string;
  value: string;
};

export default defineComponent({
  props: {
    isWhite: {
      type: Boolean,
      default: false,
    },
  },
  setup() {
    const store = AppStore();
    const { locale, t } = useI18n();

    const state = reactive({
      languages: [
        { name: 'en', value: 'en' },
        { name: '中文', value: 'zh-cn' },
      ] as Array<Language>,
      handleSetLanguage: (lang: string) => {
        locale.value = lang;
        store[AppActionTypes.ACTION_SET_LANGUAGE](lang);
        // ElMessage({
        //   message: 'Switch Language Success',
        //   type: 'success',
        // });
      },
    });
    const language = computed(() => {
      return store.language;
    });
    return {
      ...toRefs(state),
      language,
      t,
    };
  },
});
</script>

<style lang="scss" scoped>
.svg-color {
  fill: white;
}
</style>
