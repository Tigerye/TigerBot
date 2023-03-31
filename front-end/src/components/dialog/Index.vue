<template>
  <el-dialog
    v-model="dialogVisible"
    :title="title"
    width="95%"
    :style="{ 'max-width': maxWidth + 'px' }"
    :before-close="handleClose"
    :append-to-body="true"
  >
    <slot></slot>
  </el-dialog>
</template>
<script lang="ts">
import { defineComponent, ref, reactive, toRefs, watch } from 'vue';

export default defineComponent({
  props: {
    title: String,
    maxWidth: {
      type: Number,
      default: 600,
    },
    modelValue: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, context) {
    const dialogVisible = ref(props.modelValue);
    const methods = reactive({
      handleClose() {
        dialogVisible.value = false;
        context.emit('update:modelValue', dialogVisible.value);
      },
    });

    watch(
      () => props.modelValue,
      () => {
        dialogVisible.value = props.modelValue;
      }
    );

    return {
      dialogVisible,
      ...toRefs(methods),
    };
  },
});
</script>
<style lang="scss">
.el-dialog {
  border-radius: 10px;
}
.el-dialog__title {
  font-size: 16px;
  font-weight: 500;
}
</style>
