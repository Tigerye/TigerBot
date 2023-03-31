<template>
  <h1 class="text-xl font-bold mb-6">账号设置</h1>
  <el-form
    ref="ruleFormRef"
    :model="ruleForm"
    :rules="rules"
    label-width="120px"
    class="demo-ruleForm"
    size="large"
    status-icon
    label-position="top"
  >
    <el-form-item label="手机号" prop="mobile">
      <div class="flex items-center gap-5 w-full">
        <el-input v-model="ruleForm.mobile" disabled />
        <el-button type="info" @click="goBindMobile">更换手机号</el-button>
      </div>
    </el-form-item>
    <el-form-item label="微信" prop="wechat">
      <div class="flex items-center gap-5 w-full">
        <el-input v-model="ruleForm.wechat" disabled />
        <el-button type="info" @click="goUnbindWechat">{{
          ruleForm.wechat ? '解除绑定' : '绑定微信'
        }}</el-button>
      </div>
    </el-form-item>

    <el-form-item label="密码" prop="password">
      <el-input
        v-model="ruleForm.password"
        placeholder="大小写字母,数字,特殊字符至少三种组合且至少8位"
      />
    </el-form-item>
    <el-form-item label="确认密码" prop="secPassword">
      <el-input
        v-model="ruleForm.secPassword"
        placeholder="大小写字母,数字,特殊字符至少三种组合且至少8位"
      />
    </el-form-item>

    <el-form-item>
      <el-button
        size="large"
        class="w-30"
        color="#000"
        @click="save(ruleFormRef)"
        >保存</el-button
      >
    </el-form-item>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, nextTick, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import { UserStore } from '@/store/user';
import { UserActionTypes } from '@/store/user/action-types';
import { modifyPassword, unbindWechat, bindWechat } from '@/apis/setting';
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';

const userStore = UserStore();
const router = useRouter();
const route = useRoute();

const ruleFormRef = ref<FormInstance>();

const ruleForm = reactive({
  account: userStore.account,
  mobile: userStore.mobile,
  wechat: userStore.wechat,
  password: '',
  secPassword: '',
});

const rules = reactive<FormRules>({
  password: [
    {
      required: true,
      message: '请输入密码',
    },
  ],
  secPassword: [
    {
      required: true,
      message: '请确认密码',
    },
  ],
});

const goBindMobile = () => {
  router.push('/settings/bind-mobile');
};

const goUnbindWechat = () => {
  if (ruleForm.wechat) {
    // 解除绑定

    const data = {};
    unbindWechat(data)
      .then(() => {
        nextTick(() => {
          ElMessage({
            showClose: true,
            message: '解除绑定成功',
            type: 'success',
          });
          userStore[UserActionTypes.ACTION_GET_USER_INFO]();
          ruleForm.wechat = '';
        });
      })
      .catch((err) => {
        nextTick(() => {
          ElMessage({
            showClose: true,
            message: err.msg,
            type: 'error',
          });
        });
      });
  } else {
    // 绑定
    window.open(
      `https://open.weixin.qq.com/connect/qrconnect?appid=wx1932de2133a4a2ba&redirect_uri=https://hubouc.tigerobo.com/api/login/mp/codeRedirect?backUrl=${location.href}&response_type=code&scope=snsapi_login&state=STATE`
    );
  }
};

const goBindWechat = (wechatCode: string) => {
  const data = {
    appId: 'wx1932de2133a4a2ba',
    wechatCode,
  };

  bindWechat(data)
    .then(() => {
      nextTick(() => {
        userStore[UserActionTypes.ACTION_GET_USER_INFO]();
        ElMessage({
          showClose: true,
          message: '绑定成功',
          type: 'success',
        });
        router.push({
          path: route.path,
          query: {},
        });
      });
    })
    .catch((err) => {
      nextTick(() => {
        ElMessage({
          showClose: true,
          message: '绑定失败',
          type: 'error',
        });
      });
    });
};

const save = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      if (ruleForm.secPassword != ruleForm.password) {
        ElMessage({
          showClose: true,
          message: '两次密码不一致',
          type: 'error',
        });
        return;
      }
      const data = {
        params: {
          user: {
            account: ruleForm.account,
            password: ruleForm.password,
          },
        },
        authorization: {
          token: userStore.token,
          uid: userStore.uid,
        },
      };
      try {
        await modifyPassword(data);
        userStore[UserActionTypes.ACTION_GET_USER_INFO]();
        ElMessage({
          showClose: true,
          message: '保存成功',
          type: 'success',
        });
      } catch (error: any) {
        ElMessage({
          showClose: true,
          message: error.msg,
          type: 'error',
        });
      }
    } else {
      console.log('error submit!', fields);
    }
  });
};

watch(
  () => route.query,
  () => {
    if (route.query.code) {
      goBindWechat((route.query as any).code);
    }
  }
);

if (route.query.code) {
  goBindWechat((route.query as any).code);
}
</script>
