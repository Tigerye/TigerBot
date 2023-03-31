<template>
  <div class="max-w-full">
    <div
      class="search-box rounded-lg overflow-hidden relative shadow-sm border border-gray-100"
    >
      <input
        class="pl-8 placeholder-gray-400"
        type="text"
        :placeholder="placeholder"
        v-model="text"
        @keyup.enter="search"
      />
      <icon-search
        class="absolute left-2 top-1/2 translate-y-1/2 cursor-pointer"
        style="margin-top: 1px"
        theme="outline"
        size="16"
        fill="#9b9b9b"
      />
    </div>
    <div class="mt-4 flex justify-center">
      <el-button round color="#626aef" style="width: 80px" @click="search"
        >搜索</el-button
      >
      <el-button
        style="width: 80px"
        color="#626aef"
        plain
        type="info"
        round
        @click="goCreate"
        >创作</el-button
      >
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'SearchGenerate',
  props: {
    placeholder: {
      type: String,
      default: '',
    },
  },
  setup(props, context) {
    const text = ref('');

    const router = useRouter();

    const methods = reactive({
      search() {
        context.emit('search', text);
      },
      goCreate() {
        if (!text) return;
        router.push({
          path: '/image/create',
          query: {
            k: text.value,
          },
        });
      },
    });

    return {
      text,
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped lang="scss">
.search-box {
  width: 450px;
  max-width: 100%;
  height: 40px;
  input {
    width: 100%;
    border: none;
    height: 100%;
  }
  &:hover {
    border: 1px solid #ccc;
  }
}

input::placeholder {
  color: var(--b40);
  font-size: 14px;
}
</style>
