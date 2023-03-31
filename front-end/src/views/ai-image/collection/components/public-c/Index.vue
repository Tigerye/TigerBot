<template>
  <div>
    <div class="flex gap-2 items-center mb-6 text-gray-500">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 20 20"
      >
        <path
          fill-rule="evenodd"
          clip-rule="evenodd"
          d="M10 3.25A3.75 3.75 0 0 0 6.25 7v1.25h-1v7.5h9.5v-7.5h-1V7A3.75 3.75 0 0 0 10 3.25Zm2.25 5V7a2.25 2.25 0 0 0-4.5 0v1.25h4.5Z"
          fill="currentColor"
        ></path>
      </svg>
      <div class="text-sm">公开的</div>
    </div>

    <div class="collections-grid">
      <ItemC
        v-for="item in collections"
        :key="item.id"
        :id="item.id"
        :name="item.name"
        :type="item.type"
      ></ItemC>

      <CreateC type="Public"></CreateC>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import ItemC from '../ItemC.vue';
import CreateC from '../CreateC.vue';

export default defineComponent({
  components: {
    ItemC,
    CreateC,
  },
  setup() {
    const state = reactive({
      collections: [
        {
          id: '1',
          name: '收藏1',
          type: 'public',
        },
        {
          id: '1',
          name: '收藏1',
          type: 'public',
        },
        {
          id: '1',
          name: '收藏1',
          type: 'public',
        },
      ],
    });

    return {
      ...toRefs(state),
    };
  },
});
</script>

<style scoped lang="scss">
@media (max-width: 767px) {
  .collections-grid {
    grid-auto-rows: 128px;
    grid-template-columns: repeat(2, 1fr);
  }
  .large {
    grid-column: span 2;
  }
}
@media (min-width: 768px) {
  .collections-grid {
    grid-auto-rows: 120px;
    grid-template-columns: repeat(auto-fill, 252px);
  }
  .large {
    grid-row: span 2;
  }
}

.collections-grid {
  grid-gap: 12px;
  display: grid;
  gap: 12px;
}

.collections-grid-item {
  background-color: var(--b20);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
  padding: 20px;
}
</style>
