<template>
  <div
    class="register-container bg-gradient-to-b from-indigo-100 via-orange-50 to-white"
  >
    <section class="pt-16 border-gray-100 col-span-full flex-1 pb-16 md:pb-0">
      <div class="v2-join-page" style="margin-top: 30px">
        <el-form
          :model="formData"
          :rules="rules"
          ref="registerFormRef"
          size="large"
          class="v2-join-page-form-1 md:w-110 mx-auto shadow rounded-xl p-4 border bg-white z-10"
          style="max-width: 90%"
        >
          <!-- <img
                alt="algolet's logo"
                class="mx-auto w-20 -mt-14 mb-2"
                src="@/assets/imgs/logo/logo_1.png"
              /> -->
          <h1 class="text-3xl font-bold text-center mb-8">注册</h1>

          <!-- 密码登录 -->
          <el-form-item prop="nickName">
            <el-input
              placeholder="昵称"
              v-model="formData.nickName"
              autocomplete="off"
              :validate-event="false"
            ></el-input>
          </el-form-item>

          <!-- 短信登录 -->
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
                class="ml-1"
                v-model="formData.mobile"
                :validate-event="false"
                placeholder="填写常用手机号"
              ></el-input>
            </div>
          </el-form-item>

          <el-form-item prop="code">
            <div class="flex items-center w-full">
              <el-input
                v-model="formData.code"
                placeholder="请输入短信验证码"
                :validate-event="false"
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
          <div class="mt-2">
            <div class="flex justify-center items-center mt-2 mb-2 text-xs">
              <el-checkbox v-model="agree">我已阅读并同意</el-checkbox>
              <span
                ><a
                  class="text-lightBlue-500"
                  href="https://www.algolet.com/user-agreement.html"
                  target="_blank"
                  >《用户协议》</a
                >和
                <a
                  class="text-lightBlue-500"
                  href="https://www.algolet.com/private-agreement.html"
                  target="_blank"
                  >《隐私协议》</a
                ></span
              >
            </div>
            <div class="flex items-center">
              <button
                class="btn btn-base w-full rounded"
                data-ga-category="register"
                data-ga-action="clicked on next"
                data-ga-label="register"
                type="button"
                :disabled="!agree"
                @click="register"
              >
                注册
              </button>
            </div>
            <div class="text-lightBlue-500 text-right text-xs mt-2">
              <span class="cursor-pointer" @click="goLogin">
                已有账号，直接登录></span
              >
            </div>
          </div>
        </el-form>
      </div>
    </section>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, watch, ref } from 'vue';
import { useRoute, useRouter, LocationQuery } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { UserStore } from '@/store/user';
import { UserActionTypes } from '@/store/user/action-types';
import { sendMobileCode } from '@/apis/user';
import { mobileCode } from '@/constant/mobile';
import { ElMessage } from 'element-plus';

export default defineComponent({
  setup() {
    const registerFormRef = ref<FormInstance>();
    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();

    const state = reactive({
      codeIndex: 0,
      sendMsg: '获取验证码',
      rules: {
        nickName: [{ required: true, message: '请告诉我你的昵称吧' }],
        mobile: [
          {
            required: true,
            message: '请输入手机号',
          },
        ],
        code: [{ required: true, message: '请输入验证码' }],
      },
      agree: false,
      codeIsSend: false,
      timing: 0,
      formData: {
        mobile: '', // 手机号密码登录
        nickName: '',
        code: '',
        area: '+86',
      },
      redirect: '',
      otherQuery: {},
    });

    const rules = reactive<FormRules>({
      nickName: [{ required: true, message: '请告诉我你的昵称吧' }],
      mobile: [
        {
          required: true,
          message: '请输入手机号',
        },
      ],
      code: [{ required: true, message: '请输入验证码' }],
    });

    const getOtherQuery = (query: LocationQuery) => {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur];
        }
        return acc;
      }, {} as LocationQuery);
    };

    const submitForm = async (
      formEl: FormInstance | undefined,
      callback: Function
    ) => {
      if (!formEl) return;
      await formEl.validate(async (valid, fields) => {
        if (valid) {
          await callback();
        } else {
          console.log('error submit!', fields);
        }
      });
    };

    function initQuery() {
      const query = route.query;
      state.redirect = route.query.redirect?.toString() ?? '';
      state.otherQuery = getOtherQuery(query);
    }

    initQuery();

    const methods = reactive({
      register() {
        submitForm(registerFormRef.value, methods.mobileCodeRegister);
      },
      getMobileCode() {
        (registerFormRef.value as any).validateField(
          'mobile',
          async (valid: boolean) => {
            if (valid) {
              const data = {
                area: state.formData.area,
                mobile: state.formData.mobile,
              };
              await sendMobileCode(data);
              state.codeIsSend = true;
              state.timing = 60;
              const timer = setInterval(() => {
                state.timing--;
                if (state.timing <= 0) {
                  clearInterval(timer);
                  state.codeIsSend = false;
                  state.sendMsg = '重新获取';
                }
              }, 1000);
            }
          }
        );
      },
      codeChange() {
        state.formData.area = mobileCode[state.codeIndex].areaCode;
      },
      async mobileCodeRegister() {
        try {
          await userStore[UserActionTypes.ACTION_REGISTER](
            state.formData.mobile,
            state.formData.nickName,
            state.formData.code,
            state.formData.area
          );

          router.push({
            path: state.redirect || '/',
            query: state.otherQuery,
          });
        } catch (error: any) {
          ElMessage({
            showClose: true,
            message: error.response.data.msg,
            type: 'error',
          });
        }
      },
      goLogin() {
        const query = Object.create(route.query);
        router.push({
          path: '/login',
          query,
        });
      },
      jumpToLogin() {},
    });

    watch(
      () => route.query,
      (query) => {
        if (query) {
          initQuery();
        }
      }
    );

    return {
      ...toRefs(state),
      ...toRefs(methods),
      registerFormRef,
      rules,
      mobileCode,
    };
  },
});
</script>

<style lang="scss">
.register-container {
  .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #000;
  }
  .el-checkbox__label {
    font-size: 0.75rem;
    line-height: 1rem;
  }
}
</style>
