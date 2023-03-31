<template>
  <div>
    <div
      class="collections-grid-item outlined link-style cursor-pointer"
      @click="showCreateDialog"
    >
      <div
        class="collections-grid-item-header flex gap-2 text-sm items-center font-medium"
      >
        <svg
          stroke="currentColor"
          fill="none"
          stroke-width="2"
          viewBox="0 0 24 24"
          stroke-linecap="round"
          stroke-linejoin="round"
          size="20"
          height="20"
          width="20"
          xmlns="http://www.w3.org/2000/svg"
        >
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        创建收藏夹
      </div>
      <div class="text-gray-500 text-xs">{{ type }}</div>
    </div>
    <Dialog v-model="showModel" title="创建收藏夹">
      <el-form
        label-position="top"
        :model="submitData"
        style="max-width: 460px"
        size="large"
      >
        <el-form-item label="名称">
          <el-input v-model.trim="submitData.name" placeholder="如：动漫角色" />
        </el-form-item>
        <div class="flex justify-between items-center">
          <div class="text-xs text-gray-500">设为私有的</div>
          <el-switch
            v-model="submitData.isPrivate"
            size="default"
            class="ml-2"
          />
        </div>
        <div class="flex mt-6">
          <el-button>取消</el-button>

          <el-button
            class="flex-1"
            :disabled="!submitData.name"
            color="#000"
            type="info"
            >创建</el-button
          >
        </div>
      </el-form>
    </Dialog>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';
import Dialog from '@/components/dialog/Index.vue';

export default defineComponent({
  components: {
    Dialog,
  },
  props: {
    type: {
      type: String,
      default: 'private',
    },
  },
  setup() {
    const showModel = ref(false);
    const state = reactive({
      submitData: {
        name: '',
        isPrivate: true,
      },
    });
    const methods = reactive({
      showCreateDialog() {
        showModel.value = true;
      },
    });
    return {
      showModel,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped>
.collections-grid-item {
  background-color: var(--b20);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
  padding: 20px;
}
.collections-grid-item.outlined {
  background-color: unset;
  border: 2px dashed var(--b30);
}
</style>
