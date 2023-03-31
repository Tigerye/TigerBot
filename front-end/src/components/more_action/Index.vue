<template>
  <button
    ref="buttonRef"
    tabindex="0"
    class="btn btn-icon btn-minimal btn-primary gen-detail-more-actions-button"
    type="button"
    aria-haspopup="true"
    aria-expanded="false"
    @click.stop="onClickOutside"
  >
    <span class="btn-label-wrap"
      ><span class="btn-node"
        ><svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 16 16"
        >
          <path
            d="M 2.5 6.5 C 3.328 6.5 4 7.172 4 8 C 4 8.828 3.328 9.5 2.5 9.5 C 1.672 9.5 1 8.828 1 8 C 1 7.172 1.672 6.5 2.5 6.5 Z M 8 6.5 C 8.828 6.5 9.5 7.172 9.5 8 C 9.5 8.828 8.828 9.5 8 9.5 C 7.172 9.5 6.5 8.828 6.5 8 C 6.5 7.172 7.172 6.5 8 6.5 Z M 13.5 6.5 C 14.328 6.5 15 7.172 15 8 C 15 8.828 14.328 9.5 13.5 9.5 C 12.672 9.5 12 8.828 12 8 C 12 7.172 12.672 6.5 13.5 6.5 Z"
            fill="currentColor"
          ></path>
        </svg>
      </span>
    </span>
  </button>
  <el-popover
    ref="popoverRef"
    :virtual-ref="buttonRef"
    trigger="click"
    :title="title"
    virtual-triggering
  >
    <slot></slot>
  </el-popover>
</template>
<script lang="ts">
import { defineComponent, ref, unref } from 'vue';

export default defineComponent({
  props: {
    title: String,
  },
  setup() {
    const buttonRef = ref();
    const popoverRef = ref();
    const onClickOutside = () => {
      unref(popoverRef).popperRef?.delayHide?.();
    };

    return {
      buttonRef,
      popoverRef,
      onClickOutside,
    };
  },
});
</script>

<style scoped>
.btn:not(.btn-full) + .btn {
  margin-left: 8px;
}
.btn-primary.btn-minimal {
  color: var(--b100);
}
.btn-minimal {
  background-color: transparent;
}
.btn-icon {
  align-items: center;
  height: 35px;
  height: var(--btn-icon-size);
  justify-content: center;
  width: 35px;
  width: var(--btn-icon-size);
}
.btn-label-wrap {
  align-items: center;
  justify-content: center;
  opacity: 1;
}

.btn-full,
.btn-label-wrap {
  display: flex;
  width: 100%;
}

.btn-node {
  display: flex;
  line-height: 1;
}
</style>
<style>
.el-popover__title {
  font-size: 12px;
}
</style>
