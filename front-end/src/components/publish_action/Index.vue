<template>
  <div
    class="like-box rounded-lg cursor-pointer flex items-center"
    style="--tw-bg-opacity: 0.6"
    :class="{
      'bg-black': theme == 'black',
      'size-small': size == 'small',
      'size-default': size == 'default',
    }"
    title="分享"
  >
    <div @click.stop="showPublishDialog" class="inline-flex items-center">
      <icon-preview-open
        v-if="!isPublished"
        :theme="'outline'"
        :size="size == 'small' ? 16 : 30"
        fill="#f08383"
        title="公开画作"
      />
      <icon-preview-close-one
        v-else
        :theme="'outline'"
        :size="size == 'small' ? 16 : 28"
        fill="#f08383"
        title="隐藏画作"
      />
    </div>

    <Dialog
      v-model="showModel"
      :title="isPublished ? '画作：公开的' : '画作：私有的'"
    >
      <el-form
        :model="formData"
        :rules="rules"
        ref="publishFormRef"
        class="form"
        size="large"
        label-position="top"
      >
        <el-form-item label="作品名" prop="title">
          <el-input
            id="title"
            v-model="formData.title"
            placeholder="给这幅作品起一个合适的名字吧～"
          ></el-input>
        </el-form-item>
        <el-form-item label="复制保护">
          <div class="flex items-center">
            <el-switch v-model="formData.copyProtect" />
            <div class="text-xs text-gray-500 ml-2">
              (防止其他用户复制作品，设置参数将会隐藏)
            </div>
          </div>
        </el-form-item>

        <!-- <el-form-item label="描述" prop="desc">
          <el-input
            id="desc"
            v-model="formData.desc"
            placeholder="分享给大家这幅图的意思"
          ></el-input>
        </el-form-item> -->

        <el-form-item class="flex justify-center mt-8">
          <el-button
            type="primary"
            @click="togglePublish(true)"
            plain
            color="#f08383"
            class="w-30"
            >{{ isPublished ? '更新' : '公开' }}</el-button
          >
          <el-button v-if="isPublished" @click="togglePublish(false)" plain
            >隐藏</el-button
          >
        </el-form-item>
      </el-form>
    </Dialog>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs, PropType, watch } from 'vue';

import { ImageDetailModel } from '@/model/imageModel';
import Dialog from '@/components/dialog/Index.vue';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { artImageOnline, artImageOffline } from '@/apis/image';

export default defineComponent({
  name: 'PublishAction',
  components: {
    Dialog,
  },
  props: {
    item: {
      type: Object as PropType<ImageDetailModel>,
      required: true,
    },
    theme: {
      type: String,
      default: 'black',
    },
    size: {
      type: String,
      default: 'small',
    },
  },
  setup(props, context) {
    const publishFormRef = ref<FormInstance>();
    const state = reactive({
      showModel: false,
      isPublished: props.item.status == 1 ? true : false,
    });

    const formData = reactive({
      id: props.item.id,
      title: props.item.title || props.item.text,
      desc: props.item.desc || '',
      copyProtect: false,
    });

    const rules = reactive<FormRules>({
      title: [{ required: true, message: '作品名必填哦～', trigger: 'blur' }],
    });

    const methods = reactive({
      showPublishDialog() {
        state.showModel = true;
      },
      async togglePublish(isPublished: boolean) {
        const data = {
          ...formData,
        };
        if (isPublished) {
          (publishFormRef.value as any).validate(async (valid: boolean) => {
            if (valid) {
              await artImageOnline(data);
              state.isPublished = true;
              ElMessage({
                message: '画作公开成功',
                type: 'success',
              });
              context.emit('success', true);
              methods.close();
            } else {
              return false;
            }
          });
        } else {
          await artImageOffline(data);
          state.isPublished = false;
          context.emit('success', false);
          ElMessage({
            message: '画作隐藏成功',
            type: 'success',
          });
          methods.close();
        }
      },
      close() {
        state.showModel = false;
      },
    });

    watch(
      () => props.item.id,
      () => {
        state.isPublished = props.item.status == 1 ? true : false;
        formData.id = props.item.id;
        formData.title = props.item.title || props.item.text;
        formData.desc = props.item.desc || '';
        formData.copyProtect = false;
      }
    );

    return {
      publishFormRef,
      rules,
      formData,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style scoped>
.like-box:hover {
  color: #f0aaaa;
}
.size-small {
  padding-left: 0.325rem;
  padding-right: 0.325rem;
  padding-top: 0.225rem;
  padding-bottom: 0.225rem;

  font-size: 12px;
}

.is-thumbup {
  animation: size-change 0.5s 0s ease-in-out;
}
@keyframes size-change {
  0% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.25);
  }
  50% {
    transform: scale(1);
  }
  75% {
    transform: scale(0.75);
  }
  100% {
    transform: scale(1);
  }
}
</style>
