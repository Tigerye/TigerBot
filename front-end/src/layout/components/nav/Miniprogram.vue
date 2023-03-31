<template>
  <li class="relative">
    <el-tooltip placement="top" effect="light">
      <template #content>
        <div class="p-4" style="width: 180px">
          <img :src="miniQrcode" alt="" />
        </div>
      </template>
      <a
        class="flex items-center font-medium text-sm group px-2 py-0.5 text-black cursor-pointer"
        @click.stop
      >
        小程序
      </a>
    </el-tooltip>
  </li>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import miniQrcode from '@/assets/images/miniprogram.jpg';

export default defineComponent({
  name: 'MsgTip',
  setup() {
    return {
      miniQrcode,
    };
  },
});
</script>
<style scoped>
.el-badge {
  display: flex;
}
.msg-box {
  right: 0;
  z-index: 99;
  width: 260px;
}
</style>
