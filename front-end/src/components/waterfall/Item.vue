<template>
  <div
    class="item-box absolute cursor-pointer overflow-hidden left-0 top-0"
    style="max-width: 100%"
  >
    <slot></slot>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'WaterfallItem',
  setup() {},
});
</script>
