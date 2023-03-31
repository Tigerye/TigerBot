<template>
  <el-upload
    ref="uploadRef"
    class="upload-demo"
    action="https://pai.tigerobo.com/x-pai-biz/common/upload"
    :on-success="uploadSuccess"
    :disabled="disabled"
    :show-file-list="false"
  >
    <template #trigger>
      <div
        class="border border-dotted border-2 text-3xl flex items-center justify-center p-4 relative"
        :style="{
          width: currentWidth + 'px',
          height: currentHeight + 'px',
        }"
      >
        <img
          v-if="imageUrl"
          :src="imageUrl"
          class="w-full h-full object-contain"
          alt=""
        />
        <icon-add-picture v-else theme="filled" size="30" fill="#9b9b9b" />

        <icon-close-one
          v-if="imageUrl"
          class="absolute right-1 top-1 cursor-pointer"
          theme="filled"
          size="14"
          fill="#9b9b9b"
          @click.stop="deleteImg"
        />
      </div>
    </template>

    <!-- <template #tip>
      <div class="el-upload__tip">
        jpg/png files with a size less than 500kb
      </div>
    </template> -->
  </el-upload>
</template>
<script lang="ts" setup>
import { ref, computed } from 'vue';
import type { UploadInstance } from 'element-plus';
import type { UploadProps } from 'element-plus';

const props = defineProps({
  disabled: Boolean,
  width: {
    type: Number,
    required: true,
  },
  height: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(['imageChange']);

const currentWidth = computed(() => {
  if (props.width >= props.height) {
    return 180 * (props.width / props.height);
  }
  return 180;
});

const currentHeight = computed(() => {
  if (props.height >= props.width) {
    return 180 * (props.height / props.width);
  }
  return 180;
});

const imageUrl = ref('');

const uploadSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  imageUrl.value = response.data.data;

  emit('imageChange', imageUrl.value);
};

const deleteImg = () => {
  imageUrl.value = '';

  emit('imageChange', imageUrl.value);
};

const uploadRef = ref<UploadInstance>();
</script>
