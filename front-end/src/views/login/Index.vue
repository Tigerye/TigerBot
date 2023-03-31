<template>
  <div
    class="login-container bg-gradient-to-b from-indigo-100 via-orange-50 to-white"
  >
    <section class="pt-16 border-gray-100 pb-16 md:pb-0">
      <div class="v2-join-page" style="margin-top: 30px">
        <div
          class="v2-join-page-form-1 md:w-110 mx-auto shadow rounded-xl p-4 border bg-white z-10"
          style="max-width: 90%"
        >
          <!-- <img
            alt="algolet's logo"
            class="mx-auto w-20 -mt-14 mb-2"
            src="@/assets/images/logo/logo_core.png"
          /> -->
          <h1 class="text-3xl font-bold text-center mb-8">
            {{ t('login.title') }}
          </h1>

          <div class="flex items-center mb-2 text-sm">
            <div
              class="cursor-pointer mr-4"
              :class="{ 'text-lightBlue-500': loginType === 'codeForm' }"
              @click="loginTypeChange('codeForm')"
            >
              {{ t('login.mobileCodeLogin') }}
            </div>
            <div
              class="cursor-pointer"
              :class="{ 'text-lightBlue-500': loginType === 'passForm' }"
              @click="loginTypeChange('passForm')"
            >
              {{ t('login.passwordLogin') }}
            </div>
          </div>

          <!-- 密码登录 -->

          <el-form
            v-if="loginType === 'passForm'"
            :model="formData"
            :rules="rules"
            ref="passFormRef"
            size="large"
          >
            <el-form-item prop="mobile" required>
              <el-input
                type="tel"
                v-model="formData.mobile"
                :placeholder="t('login.mobile')"
                autocomplete="off"
              ></el-input>
            </el-form-item>
            <el-form-item prop="password" required>
              <el-input
                type="password"
                v-model="formData.password"
                :placeholder="t('login.password')"
                autocomplete="off"
              ></el-input>
            </el-form-item>
          </el-form>

          <!-- 短信登录 -->
          <el-form
            v-if="loginType === 'codeForm'"
            :model="formDataCode"
            :rules="rules"
            ref="codeFormRef"
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
                  >
                  </el-option>
                </el-select>
                <el-input
                  type="tel"
                  class="ml-1 w-full"
                  v-model="formDataCode.mobile"
                  :placeholder="t('login.mobile')"
                  :validate-event="false"
                  size="large"
                ></el-input>
              </div>
            </el-form-item>
            <el-form-item prop="code">
              <div class="flex items-center w-full">
                <el-input
                  v-model="formDataCode.code"
                  :validate-event="false"
                  :placeholder="t('login.code')"
                  size="large"
                  maxlength="4"
                  type="tel"
                >
                </el-input>
                <el-button
                  style="margin-left: -94px; z-index: 1000; width: 90px"
                  :type="codeIsSend ? 'info' : 'primary'"
                  plain
                  size="default"
                  :disabled="codeIsSend"
                  @click="getMobileCode"
                  >{{
                    codeIsSend ? timing : t('login.getMobileCode')
                  }}</el-button
                >
              </div>
            </el-form-item>
          </el-form>

          <div class="mt-4">
            <div class="flex items-center">
              <button
                class="btn btn-primary border-rounded w-full bg-lightBlue-300"
                data-ga-category="register"
                data-ga-action="clicked on next"
                data-ga-label="register"
                type="button"
                @click="login"
              >
                {{ t('login.logIn') }}
              </button>
              <button
                class="btn btn-base border-rounded w-full ml-8"
                data-ga-category="register"
                data-ga-action="clicked on next"
                data-ga-label="register"
                type="button"
                @click="goRegister"
              >
                {{ t('register.title') }}
              </button>
            </div>

            <div class="flex justify-center items-center mt-4 mb-1 text-xs">
              <span> {{ t('login.agree') }}</span>
              <span
                ><a
                  class="text-lightBlue-500"
                  href="https://www.algolet.com/user-agreement.html"
                  target="_blank"
                >
                  {{ t('register.userAgreement') }}</a
                >和
                <a
                  class="text-lightBlue-500"
                  href="https://www.algolet.com/private-agreement.html"
                  target="_blank"
                  >{{ t('register.privateAgreement') }}</a
                ></span
              >
            </div>

            <div
              v-if="wechatEnv == 'wechat' || !mobileEnv"
              class="mt-4 pt-2 border-t"
            >
              <div class="text-center text-gray-500">
                {{ t('login.thirdparty') }}
              </div>
              <div class="flex items-center justify-center mt-2">
                <img
                  class="w-8 cursor-pointer"
                  src="@/assets/images/wechat.png"
                  alt="wechat"
                  @click="loginTypeChange('wechat')"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs, nextTick, ref, watch } from 'vue';
import { useRoute, useRouter, LocationQuery } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { mobileCode } from '@/constant/mobile';
import { sendMobileCode } from '@/apis/user';
import { UserStore } from '@/store/user';
import { UserActionTypes } from '@/store/user/action-types';
import { useI18n } from 'vue-i18n';
import { isMobile, getWeixinEnv } from '@/utils';
import { ElMessage } from 'element-plus';

export default defineComponent({
  name: 'LoginView',
  setup() {
    const { t } = useI18n();

    const wechatEnv = getWeixinEnv();
    const mobileEnv = isMobile();

    const passFormRef = ref<FormInstance>();
    const codeFormRef = ref<FormInstance>();

    const userStore = UserStore();
    const router = useRouter();
    const route = useRoute();

    const rules = reactive<FormRules>({
      password: [
        { required: true, message: '密码不能小于6位' },
        {
          min: 6,
          max: 16,
          message: '长度在 6 到 16 个字符',
        },
      ],
      mobile: [
        {
          required: true,
          message: '请输入手机号',
        },
      ],
      code: [{ required: true, message: '请输入验证码' }],
    });
    const state = reactive({
      loginType: 'codeForm',
      codeIndex: 0,
      sendMsg: '获取验证码',
      codeIsSend: false,
      timing: 0,
      errMsg: '',
      formData: {
        mobile: '', // 手机号密码登录
        password: '',
      },
      formDataCode: {
        mobile: '', // 手机号验证码登录
        code: '',
        area: '+86',
      },
      redirect: '',
      otherQuery: {},
    });

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

    const resetForm = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      formEl.resetFields();
    };

    function getOtherQuery(query: LocationQuery) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur];
        }
        return acc;
      }, {} as LocationQuery);
    }

    function initQuery() {
      const query = route.query;
      state.redirect = route.query.redirect?.toString() ?? '';
      state.otherQuery = getOtherQuery(query);
    }

    initQuery();

    const methods = reactive({
      loginTypeChange(type: 'passForm' | 'codeForm' | 'wechat') {
        if (type == 'wechat') {
          if (isMobile()) {
            console.log(22222);

            methods.wechatMobileLogin();
            return;
          }

          methods.wechatPcLogin();
          return;
        }
        state.loginType = type;
        nextTick(() => {
          if (type == 'passForm') {
            resetForm(passFormRef.value);
            return;
          }
          if (type == 'codeForm') {
            resetForm(codeFormRef.value);
          }
        });
      },
      wechatPcLogin() {
        let queryStr = methods.getQueryStr();

        location.href = `https://open.weixin.qq.com/connect/qrconnect?appid=wx1932de2133a4a2ba&redirect_uri=https://hubouc.tigerobo.com/api/login/mp/codeRedirect?backUrl=${
          location.origin + (state.redirect || '/') + '?' + queryStr
        }&response_type=code&scope=snsapi_login&state=STATE`;
      },
      getQueryStr() {
        let queryStr = '';
        const len = Object.keys(state.otherQuery).length;
        Object.keys(state.otherQuery).forEach((key, index) => {
          if (index == len - 1) {
            queryStr += `${key}=${state.otherQuery[key]}`;
          } else {
            queryStr += `${key}=${state.otherQuery[key]}&`;
          }
        });
        return queryStr;
      },
      wechatMobileLogin() {
        let queryStr = methods.getQueryStr();

        let url = 'https://open.weixin.qq.com/connect/oauth2/authorize';

        const appid = 'wx7dce890fbc536f39';

        let redirect_server_uri =
          'https://hubouc.tigerobo.com/api/login/mp/codeRedirect?backUrl=';

        if (queryStr) {
          redirect_server_uri = encodeURIComponent(
            `${redirect_server_uri}${
              location.origin + (state.redirect || '/') + '?' + queryStr
            }`
          );
        } else {
          redirect_server_uri = encodeURIComponent(
            `${redirect_server_uri}${location.origin + (state.redirect || '/')}`
          );
        }

        location.replace(
          `${url}?appid=${appid}&redirect_uri=${redirect_server_uri}&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect`
        );
      },

      goRegister() {
        router.push({
          path: '/register',
          query: state.otherQuery,
        });
      },
      async login() {
        try {
          if (state.loginType === 'passForm') {
            // 密码登录
            await submitForm(passFormRef.value, methods.loginByMobilePass);
          } else {
            // 验证码登录
            await submitForm(codeFormRef.value, methods.loginByMobileCode);
          }
        } catch (error) {
          console.warn(error);
        }
      },
      async loginByMobilePass() {
        try {
          await userStore[UserActionTypes.ACTION_LOGIN_BY_MOBILE_PASS](
            state.formData.mobile,
            state.formData.password
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
      async loginByMobileCode() {
        try {
          await userStore[UserActionTypes.ACTION_LOGIN_BY_MOBILE_CODE](
            state.formDataCode.mobile,
            state.formDataCode.code,
            state.formDataCode.area
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
      getMobileCode() {
        (codeFormRef.value as any).validateField(
          'mobile',
          async (valid: boolean) => {
            if (valid) {
              const data = {
                area: state.formDataCode.area,
                mobile: state.formDataCode.mobile,
              };
              try {
                await sendMobileCode(data);
              } catch (error: any) {
                ElMessage({
                  showClose: true,
                  message: error.response.data.msg,
                  type: 'error',
                });
              }

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
        state.formDataCode.area = mobileCode[state.codeIndex].areaCode;
      },
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
      mobileCode,
      passFormRef,
      codeFormRef,
      rules,
      wechatEnv,
      mobileEnv,
      t,
    };
  },
});
</script>

<style lang="scss">
.login-container {
  .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #000;
  }
}
</style>
