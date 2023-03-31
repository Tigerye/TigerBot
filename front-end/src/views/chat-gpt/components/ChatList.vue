<template>
  <div class="flex">
    <div>
      <!-- 索引筛选 -->
      <div class="relative">
        <div
          class="inline-flex items-center p-0 mt-1 rounded cursor-pointer text-gray-500 hover:border-gray-500"
          @click.stop="showMyIndex = !showMyIndex"
        >
          <svg
            class="mt-0.5"
            xmlns="http://www.w3.org/2000/svg"
            width="1rem"
            height="1rem"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <rect x="2" y="2" width="20" height="8" rx="2" ry="2"></rect>
            <rect x="2" y="14" width="20" height="8" rx="2" ry="2"></rect>
            <line x1="6" y1="6" x2="6.01" y2="6"></line>
            <line x1="6" y1="18" x2="6.01" y2="18"></line>
          </svg>
          <div class="text-lg font-medium ml-2 mr-2 text-black">
            {{ currentName }}
          </div>
          <div class="flex flex-col items-center">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="11"
              height="11"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <polyline points="18 15 12 9 6 15"></polyline>
            </svg>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="11"
              height="11"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
          </div>
        </div>
        <div
          class="absolute shadow-lg border mt-2 bg-white overflow-auto"
          :class="{ hidden: !showMyIndex }"
          style="z-index: 100; width: 400px"
          @click.stop
        >
          <div class="overflow-auto" style="max-height: 450px">
            <div
              class="flex items-center justify-between p-3 cursor hover:bg-gray-100 cursor-pointer"
              v-for="(item, index) in list"
              :key="index"
              @click="indexChange(item)"
            >
              <div class="flex items-center">
                <div class="p-3 rounded-full bg-gray-200">
                  <svg
                    class="mt-0.5"
                    xmlns="http://www.w3.org/2000/svg"
                    width="1rem"
                    height="1rem"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <rect
                      x="2"
                      y="2"
                      width="20"
                      height="8"
                      rx="2"
                      ry="2"
                    ></rect>
                    <rect
                      x="2"
                      y="14"
                      width="20"
                      height="8"
                      rx="2"
                      ry="2"
                    ></rect>
                    <line x1="6" y1="6" x2="6.01" y2="6"></line>
                    <line x1="6" y1="18" x2="6.01" y2="18"></line>
                  </svg>
                </div>
                <div v-if="item.name" class="ml-4">
                  <div class="text-base font-semibold">
                    {{ item.name }}
                  </div>
                </div>
              </div>
              <div class="flex items-center">删除</div>
            </div>
          </div>

          <div class="py-4 px-5 border-t bg-gray-100">
            <div class="text-sm cursor-pointer" @click="showAddIndex">
              <span>+</span> 新建聊天
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建索引 -->
    <el-dialog
      class="dialog-create-index"
      title="创建对话"
      v-model="createDialog"
      :append-to-body="true"
      :width="'500px'"
    >
      <div class="mb-1">对话名称</div>
      <div>
        <el-input
          v-model="createForm.name"
          placeholder="输入对话名称"
        ></el-input>
      </div>
      <div class="mt-6">
        <el-button class="w-full" type="info" plain @click="createIndex"
          >创建</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';

interface ListModel {
  name: string;
}

export default defineComponent({
  setup() {
    const state = reactive({
      indexForm: {
        pageNum: 1,
        pageSize: 5,
      },
      currentName: 'tiger',
      list: [] as Array<ListModel>,
      showMyIndex: false,
      createDialog: false,
      createForm: {
        name: '',
      },
    });

    const methods = reactive({
      indexChange(item: any) {},
      createIndex() {},
      showAddIndex() {
        state.createDialog = true;
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
