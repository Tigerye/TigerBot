<template>
  <div>
    <div class="flex justify-between">
      <h1 class="text-xl font-bold mb-6">基本信息设置</h1>
    </div>
    <form class="flex flex-col space-y-5 mb-8" ref="baseForm">
      <label class="md:pr-36"
        >用户名
        <input
          autocomplete="off"
          class="form-input"
          disabled
          name="username"
          placeholder="用户名"
          type="text"
          v-model="formData.account"
        />
        <!-- <p class="text-xs mt-2 text-gray-400">用户名不可修改</p> -->
      </label>
      <label class="md:pr-36"
        >昵称
        <el-input
          class="mt-2"
          placeholder="昵称"
          name="name"
          type="text"
          size="large"
          v-model="formData.name"
      /></label>
      <label class="md:pr-36" for="avatar"
        >头像
        <span class="pl-2 text-gray-400">(可选) </span>
        <div class="v2-file-upload mt-1 flex items-center">
          <el-upload
            ref="uploadRef"
            class="mr-4"
            action="https://pai.tigerobo.com/x-pai-biz/common/upload"
            :on-success="handleAvatarSuccess"
            :show-file-list="false"
          >
            <template #trigger>
              <el-button type="info" plain>上传头像</el-button>
            </template>
          </el-upload>

          <div
            class="v2-file-upload-info text-gray-500 whitespace-nowrap overflow-ellipsis overflow-hidden flex-1"
          >
            <img
              v-if="formData.avatar"
              class="w-40 h-40 rounded-full object-cover"
              :src="formData.avatar"
              alt=""
            />
          </div>
        </div>
      </label>

      <label class="md:pr-36"
        >简介
        <span class="pl-2 text-gray-400">(可选) </span>

        <el-input
          class="mt-2"
          type="textarea"
          cols="3"
          name="details"
          placeholder="个人简介"
          v-model="formData.intro"
        ></el-input>
      </label>

      <div>
        <el-button size="large" class="w-30" color="#000" @click="save"
          >保存</el-button
        >
      </div>
    </form>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs } from 'vue';
import { UserStore } from '@/store/user';
import { UserActionTypes } from '@/store/user/action-types';
import { uploadUserAvatar, modifyUser } from '@/apis/setting';
import { ElMessage } from 'element-plus';

export default defineComponent({
  setup() {
    const userStore = UserStore();

    const state = reactive({
      formData: {
        account: userStore.account,
        name: userStore.name,
        avatar: userStore.avatar,
        intro: userStore.intro,
      },
      async save() {
        const data = {
          params: {
            user: {
              ...state.formData,
            },
          },
          authorization: {
            token: userStore.token,
            uid: userStore.uid,
          },
        };
        try {
          await modifyUser(data);
          userStore[UserActionTypes.ACTION_GET_USER_INFO]();
          ElMessage({
            showClose: true,
            message: '修改保存成功',
            type: 'success',
          });
        } catch (error) {}
      },
      handleAvatarSuccess: (response: any, uploadFile: any) => {
        state.formData.avatar = response.data.data;
      },
    });

    return {
      ...toRefs(state),
    };
  },
});
</script>
