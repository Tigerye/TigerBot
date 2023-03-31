<template>
  <div class="">
    <div>
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/settings/account' }"
          >账号</el-breadcrumb-item
        >
        <el-breadcrumb-item>设置手机</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="flex justify-center flex-col items-center">
      <div class="mt-12" style="width: 600px">
        <el-steps
          :active="active"
          finish-status="success"
          :space="200"
          :align-center="true"
        >
          <el-step title="验证身份"></el-step>
          <el-step title="绑定手机"></el-step>
          <el-step title="绑定成功"></el-step>
        </el-steps>
      </div>

      <div class="mt-14" style="width: 400px; max-width: 100%">
        <!-- 原手机号 -->
        <el-form
          v-if="active === 0"
          :model="formData1"
          :rules="rules"
          ref="codeForm"
          size="large"
        >
          <el-form-item prop="mobile">
            <div class="flex items-center w-full">
              <el-input
                style="width: 126px"
                type="tel"
                class="ml-1"
                value="原手机号"
                disabled
              ></el-input>
              <el-input
                type="tel"
                class="ml-1 flex-1"
                v-model="formData1.mobile"
                disabled
                placeholder="填写常用手机号"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item prop="code">
            <div class="flex items-center w-full">
              <el-input
                v-model="formData1.code"
                placeholder="请输入短信验证码"
              ></el-input>
              <el-button
                style="margin-left: -94px; z-index: 1000; width: 90px"
                :type="codeIsSend ? 'info' : 'primary'"
                plain
                size="default"
                :disabled="codeIsSend"
                @click="getMobileCode"
                >{{ codeIsSend ? timing : sendMsg }}</el-button
              >
            </div>
          </el-form-item>
          <div>
            <el-button type="primary" style="width: 100%" @click="next"
              >下一步</el-button
            >
          </div>
        </el-form>

        <!-- 绑定手机 -->
        <el-form
          v-if="active === 1"
          :model="formData2"
          :rules="rules"
          ref="codeForm2"
          size="large"
        >
          <el-form-item prop="mobile">
            <div class="flex items-center w-full">
              <el-select
                v-model="codeIndex"
                @change="codeChange"
                placeholder="请选择"
              >
                <el-option
                  v-for="(item, index) in mobileCode"
                  :key="index"
                  :value="index"
                  :label="item.areaName"
                ></el-option>
              </el-select>
              <el-input
                type="tel"
                class="ml-1"
                v-model="formData2.mobile"
                placeholder="填写常用手机号"
              ></el-input>
            </div>
          </el-form-item>
          <el-form-item prop="code">
            <div class="flex items-center w-full">
              <el-input
                v-model="formData2.code"
                placeholder="请输入短信验证码"
              ></el-input>
              <el-button
                style="margin-left: -94px; z-index: 1000; width: 90px"
                :type="codeIsSend ? 'info' : 'primary'"
                plain
                size="default"
                :disabled="codeIsSend"
                @click="getMobileCode2"
                >{{ codeIsSend ? timing : sendMsg }}</el-button
              >
            </div>
          </el-form-item>
          <div>
            <el-button type="primary" style="width: 100%" @click="next2"
              >下一步</el-button
            >
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue';
import { mobileCode } from '@/constant/mobile';
import { sendMobileCode } from '@/apis/user';
import { getChangeMobileToken, bindMobile } from '@/apis/setting';
import { UserStore } from '@/store/user';
import { UserActionTypes } from '@/store/user/action-types';
import type { FormInstance } from 'element-plus';
import { ElMessage } from 'element-plus';

export default defineComponent({
  setup() {
    const userStore = UserStore();

    const codeForm = ref<FormInstance>();
    const codeForm2 = ref<FormInstance>();

    const state = reactive({
      active: 0,
      timing: 0,
      sendMsg: '获取验证码',
      codeIsSend: false,
      codeIndex: 0,
      formData1: {
        mobile: userStore.mobile, // 手机号验证码登录
        code: '',
        area: '+86',
      },
      formData2: {
        area: '',
        changeToken: '',
        code: '',
        mobile: '',
      },
    });

    const rules = reactive({
      mobile: [
        {
          required: true,
          message: '请输入手机号',
          trigger: 'blur',
        },
      ],
      code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
    });

    const methods = reactive({
      getMobileCode() {
        if (!codeForm.value) return;
        codeForm.value.validateField('mobile', (isValid) => {
          if (isValid) {
            state.codeIsSend = true;
            state.timing = 60;
            const data = {
              area: state.formData1.area,
              mobile: state.formData1.mobile,
            };
            sendMobileCode(data);
            const timer = setInterval(() => {
              state.timing--;
              if (state.timing <= 0) {
                clearInterval(timer);
                state.codeIsSend = false;
                state.sendMsg = '重新获取';
              }
            }, 1000);
          }
        });
      },
      getMobileCode2() {
        if (!codeForm2.value) return;
        codeForm2.value.validateField('mobile', (isValid) => {
          if (isValid) {
            state.codeIsSend = true;
            state.timing = 60;
            const data = {
              area: state.formData2.area,
              mobile: state.formData2.mobile,
            };
            sendMobileCode({ data });
            const timer = setInterval(() => {
              state.timing--;
              if (state.timing <= 0) {
                clearInterval(timer);
                state.codeIsSend = false;
                state.sendMsg = '重新获取';
              }
            }, 1000);
          }
        });
      },
      codeChange() {
        state.formData2.area = mobileCode[state.codeIndex].areaCode;
      },
      next() {
        if (!codeForm.value) return;
        codeForm.value.validate((valid) => {
          if (valid) {
            const data = {
              ...state.formData1,
            };

            getChangeMobileToken(data).then((res: any) => {
              const result = res.data || {};
              state.formData2.changeToken = result.data || '';
              state.active = 1;
            });
          } else {
            return false;
          }
        });
      },
      next2() {
        if (!codeForm2.value) return;
        codeForm2.value.validate((valid) => {
          if (valid) {
            const data = {
              ...state.formData2,
            };

            bindMobile(data)
              .then((res) => {
                userStore[UserActionTypes.ACTION_GET_USER_INFO]();
                state.active = 3;
              })
              .catch((err) => {
                ElMessage({
                  showClose: true,
                  message: err.msg,
                  type: 'error',
                });
              });
          } else {
            return false;
          }
        });
      },
    });

    if (!state.formData1.mobile) {
      state.active = 1;
    }

    return {
      rules,
      codeForm,
      codeForm2,
      mobileCode,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
