<template>
  <div
    class="search-box rounded-lg overflow-hidden relative shadow-sm border border-gray-100"
    :style="{ width: `${width}`, height: `${height}` }"
  >
    <input
      class="pr-7 placeholder-gray-400"
      type="text"
      :placeholder="placeholder"
      v-model="text"
      autofocus
      @keypress.enter="search"
    />
    <icon-search
      v-if="type === 'search'"
      class="absolute right-3 top-1/2 translate-y-1/2 cursor-pointer"
      theme="outline"
      size="19"
      :fill="text ? '#000000' : '#9b9b9b'"
      @click="search"
    />
    <icon-send
      v-if="type === 'send'"
      class="absolute right-3 top-1/2 translate-y-1/2 cursor-pointer"
      theme="outline"
      size="19"
      :fill="text ? '#000000' : '#9b9b9b'"
      @click="search"
    />
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';

export default defineComponent({
  name: 'Search',
  props: {
    type: {
      type: String,
      default: 'search',
    },
    width: {
      type: String,
      default: 450,
    },
    height: {
      type: String,
      default: 40,
    },
    placeholder: {
      type: String,
      default: '',
    },
  },
  setup(props, context) {
    const text = ref('');

    const methods = reactive({
      search() {
        context.emit('search', text);
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
  max-width: 100%;

  input {
    width: 100%;
    border: none;
    height: 100%;
    max-width: 100%;
  }
  &:hover {
    border: 1px solid #ccc;
  }
}
</style>
