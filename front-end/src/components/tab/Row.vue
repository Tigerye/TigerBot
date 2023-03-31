<template>
  <div class="tab-row">
    <div
      v-for="item in list"
      :key="item.name"
      class="tab-row-tab link-style"
      :class="{
        selected: modelValue == item.name,
        'text-sm': size == 'small',
        'text-base': size == 'default',
        'text-lg': size == 'medium',
        'text-2xl': size == 'large',
        'cursor-pointer': !item.disabled,
      }"
      @click="tabClick(item)"
    >
      {{ item.label }}

      <span v-if="typeof item.number == 'number'">{{ item.number }}</span>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs, ref, PropType } from 'vue';

interface TabMap {
  name: string;
  label: string;
  disabled?: boolean;
  number?: number;
}

export default defineComponent({
  name: 'TabRow',
  props: {
    modelValue: {
      type: String,
      required: true,
    },
    size: {
      type: String,
      default: 'small',
    },
    list: {
      type: Array as PropType<Array<TabMap>>,
      required: true,
    },
  },
  setup(props, context) {
    const activeName = ref(props.list[0].name);
    const methods = reactive({
      tabClick(item: TabMap) {
        if (item.disabled) return;
        context.emit('update:modelValue', item.name);
        context.emit('tabClick', item.name);
      },
    });

    return {
      activeName,
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped>
.tab-row {
  display: flex;
  gap: 24px;
}
.tab-row-tab.selected,
.tab-row-tab.selected:hover {
  color: var(--b100);
}

.link-style,
a {
  color: #777;
  color: var(--b60);
  text-decoration: none;
  transition: color 0.2s ease;
}

.tab-row-tab {
  font-weight: 500;
  padding: 8px 0;
  transition: all 0.1s ease-in-out;
}
</style>
