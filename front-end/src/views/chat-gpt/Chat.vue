<template>
  <div
    class="flex flex-col justify-center transition-all-8 overflow-hidden p-4"
    style="max-width: 100vw"
    :class="[safari ? 'isSafari' : 'chat-detail-box']"
  >
    <div
      v-if="qaList.length > 0 && qaList[0].length > 0"
      class="qa flex-1 overflow-y transition-all-8 pt-6"
    >
      <div v-for="(list, index) in qaList" :key="index">
        <section>
          <div
            v-for="(item, index) in list"
            :key="index"
            class="flex flex-col items-center mb-8"
          >
            <div
              v-if="item.type === 'question'"
              class="my-questions"
              style="width: 800px; max-width: 100%"
              :data-id="item.id"
            >
              <pre
                v-if="item.text"
                class="question whitespace-pre-line ml-4"
                v-html="item.text"
                style="margin-left: 50px"
              ></pre>
              <img
                class="w-8 h-8 object-cover flex-shrink-0"
                :src="avatar"
                alt=""
              />
            </div>

            <div
              v-if="item.type === 'answer'"
              class="answer-box"
              style="width: 800px; max-width: 100%"
            >
              <img
                class="h-8 flex-shrink-0"
                src="@/assets/images/logo/logo_core.png"
                alt=""
              />
              <div class="answer-result" style="margin-right: 45px">
                <pre
                  v-if="item.text"
                  class="text whitespace-pre-line"
                  v-html="item.text"
                ></pre>
                <span v-else class="text-loading"></span>
              </div>
            </div>
          </div>
        </section>

        <div v-if="index < qaList.length - 1" class="flex justify-center">
          <div
            class="border-t border-gray-700 text-center py-2 text-sm text-gray-500"
            style="width: 800px; max-width: 100%"
          >
            新一轮对话
            <span v-if="list[list.length - 1] && list[list.length - 1].time">{{
              list[list.length - 1].time
            }}</span>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="qaList.length > 0 && qaList[0].length > 0"
      class="flex justify-center mb-1"
    >
      <div
        style="width: 140px; --tw-border-opacity: 0.5"
        class="w-full mb-1 mt-1 flex items-center justify-center cursor-pointer border border-gray-600 rounded"
        @click="clearChat"
      >
        <span class="text-sm ml-1" style="color: #4a4a4a">多轮对话</span>
        <el-switch v-model="useMulti" class="ml-2" />
      </div>

      <div
        style="width: 100px; --tw-border-opacity: 0.5"
        class="w-full mb-1 mt-1 flex items-center justify-center cursor-pointer border border-gray-600 rounded ml-2"
        @click="clearHistory"
      >
        <icon-clear theme="outline" size="18" fill="#4a4a4a" :strokeWidth="3" />
        <span class="text-sm ml-1" style="color: #4a4a4a">删除记录</span>
      </div>

      <div
        style="width: 100px; --tw-border-opacity: 0.5"
        class="w-full mb-1 mt-1 flex items-center justify-center cursor-pointer border border-gray-600 rounded ml-2"
        @click="clearChat"
      >
        <icon-refresh
          theme="outline"
          size="18"
          fill="#4a4a4a"
          :strokeWidth="3"
        />
        <span class="text-sm ml-1" style="color: #4a4a4a">重新对话</span>
      </div>
    </div>
    <div
      class="flex flex-col justify-center items-center transition-all-8"
      :style="{ 'margin-top': qaList.length ? '' : '-100px' }"
    >
      <div
        v-if="!(qaList.length > 0 && qaList[0].length > 0)"
        class="text-4xl mb-10"
        style="font-family: serif"
      >
        Chat with Tiger
      </div>

      <div
        class="send relative w-full shadow rounded-lg overflow-hidden transition-all-8 md:mb-8"
        style="max-width: 800px; min-height: 40px"
      >
        <pre
          class="w-full h-full opacity-0 whitespace-pre-line"
          style="padding: 10px"
        >
          {{ question }}
        </pre>
        <div class="w-full h-full absolute left-0 top-0">
          <textarea
            ref="questionBox"
            class="question-box h-full"
            placeholder="问点啥；或者让我画一幅画？如：画一只狗"
            type="readonly"
            autofocus
            v-model="question"
            @keypress="questionKeyup"
          />

          <icon-send
            v-if="!isLoading"
            class="absolute right-2 bottom-2 cursor-pointer text-shadow"
            theme="outline"
            size="22"
            :fill="question ? '#000000' : '#9b9b9b'"
            @click="getResult"
          />

          <span
            v-if="isLoading"
            class="absolute right-2 bottom-2 cursor-pointer text-loading"
          ></span>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  nextTick,
  watch,
  onMounted,
} from 'vue';
import { UserStore } from '@/store/user';
import { useRoute, useRouter } from 'vue-router';
import moment from 'moment';
import { chatGptExecute } from '@/apis/gpt';
import Search from '@/components/search/Index.vue';
import { isSafari, getUuid } from '@/utils';
import { isMobile } from '@/utils';
import ChatList from './components/ChatList.vue';

interface UserInfoModel {
  type: string;
  text: string;
  time: string;
  id: string | number;
}

export default defineComponent({
  components: {
    Search,
    ChatList,
  },
  setup() {
    const userStore = UserStore();
    const route = useRoute();
    const router = useRouter();

    const safari: any = isSafari();

    const state = reactive({
      useMulti: false,
      uuid: '',
      question: '',
      chatName: '',
      isLoading: false,
      historyChat: {},
      qaList: [[]] as Array<Array<UserInfoModel>>,
      chatType: 'model-qa-16',
    });

    state.chatType = (route.query.model as string) || 'model-qa-16';

    watch(
      () => route.query.model,
      () => {
        state.chatType = route.query.model as string;
      }
    );

    onMounted(() => {
      if (!userStore.token) {
        return;
      }
      state.chatName = (route.query.c as string) || 'tiger';

      let historyChat = sessionStorage.getItem('historyChat');
      if (historyChat) {
        state.historyChat = JSON.parse(historyChat);
        const data = state.historyChat[state.chatName] || {};

        state.qaList = data.qaList || [[]];

        state.uuid = (data?.uuid as string) || getUuid();
        if (state.qaList.length > 0 && state.qaList[0].length > 0) {
          methods.scrollToBottom();
        }
      } else {
        state.qaList = [[]];
        state.uuid = getUuid();
      }

      state.question = (route.query.q as string) || '';
      if (state.question) {
        methods.getResult();
        router.push({
          path: route.path,
          query: {},
        });
      }
    });

    const methods = reactive({
      async getResult() {
        if (!userStore.token) {
          router.push(`/login?redirect=/chat`);
          return;
        }

        if (!state.question || state.isLoading) return;

        const data = {
          text: state.question,
          clientSession: state.uuid,
          useMulti: state.useMulti,
        };

        const id = moment().unix();
        const time = moment().format('MM.DD HH:mm');

        state.qaList[state.qaList.length - 1].push({
          type: 'question',
          text: state.question,
          time,
          id,
        });

        const answerObj = {
          type: 'answer',
          text: '',
          time: '',
          id: '',
        };

        state.qaList[state.qaList.length - 1].push(answerObj);

        try {
          methods.scrollToBottom();
          state.question = '';
          state.isLoading = true;
          const res: any = await chatGptExecute(data);
          state.isLoading = false;

          let result = res.data.result || [];
          const appendInfo = res.data.appendInfo;

          let answer = '';

          if (Object.hasOwn(appendInfo, 'imgs')) {
            const imgs = appendInfo.imgs || [];
            let tipText = `<div class="font-medium">我画了三张，挑一张你最喜欢的吧~</div>`;
            let imgText = '';
            imgs.forEach((imgUrl: string) => {
              imgText += `<div class="mt-2"><img style="width:auto" src='${imgUrl}' alt=""></div>`;
            });
            let htmlText = tipText + imgText;
            answer = htmlText;
          } else {
            answer = result.length > 0 ? result[0] : '';
          }

          const id = moment().unix();
          const time = moment().format('MM.DD HH:mm');
          state.qaList[state.qaList.length - 1].splice(
            state.qaList[state.qaList.length - 1].length - 1,
            1,
            {
              type: 'answer',
              text: answer,
              time,
              id,
            }
          );
          methods.scrollToBottom();

          state.historyChat[state.chatName] = {
            uuid: state.uuid,
            qaList: state.qaList,
          };
          sessionStorage.setItem(
            'historyChat',
            JSON.stringify(state.historyChat)
          );
        } catch (error) {
          state.isLoading = false;
          const id = moment().unix();
          const time = moment().format('MM.DD HH:mm');
          state.qaList[state.qaList.length - 1].splice(
            state.qaList[state.qaList.length - 1].length - 1,
            1,
            {
              type: 'answer',
              text: '这个问题有点难，让我想想哦，下次告诉你～你可以再试试问点别的！',
              time,
              id,
            }
          );
          methods.scrollToBottom();

          state.historyChat[state.chatName] = {
            uuid: state.uuid,
            qaList: state.qaList,
          };
          sessionStorage.setItem(
            'historyChat',
            JSON.stringify(state.historyChat)
          );
        }
      },
      scrollToBottom() {
        nextTick(() => {
          const qaContent: any = document.querySelector('.qa');

          qaContent.scrollTop = qaContent.scrollHeight;
        });
      },
      clearHistory() {
        sessionStorage.removeItem('historyChat');
        state.uuid = getUuid();
        state.qaList = [[]];
        state.isLoading = false;
      },
      clearChat() {
        if (state.isLoading) return;
        if (state.qaList[state.qaList.length - 1].length < 1) return;
        state.uuid = getUuid();
        state.qaList.push([]);
        // state.currentList = state.qaList[state.qaList.length - 1];
        state.historyChat[state.chatName] = {
          uuid: state.uuid,
          qaList: state.qaList,
        };
        sessionStorage.setItem(
          'historyChat',
          JSON.stringify(state.historyChat)
        );
        methods.scrollToBottom();
      },
      getCaret(el: any) {
        if (el.selectionStart) {
          return el.selectionStart;
        } else if ((document as any).selection) {
          el.focus();
          var r = (document as any).selection.createRange();
          if (r == null) {
            return 0;
          }
          var re = el.createTextRange(),
            rc = re.duplicate();
          re.moveToBookmark(r.getBookmark());
          rc.setEndPoint('EndToStart', re);
          return rc.text.length;
        }
        return 0;
      },
      questionKeyup(event: any) {
        if (event.shiftKey && event.keyCode == 13) {
          // let caret = methods.getCaret(event.target) - 1;
          // state.question =
          //   state.question.substring(0, caret) +
          //   '\n' +
          //   +state.question.substring(caret, state.question.length - 1);
        } else if (event.keyCode == 13) {
          event.returnValue = false;
          if (state.isLoading) return;

          if (isMobile()) {
            let caret = methods.getCaret(event.target);
            const frontText = state.question.substring(0, caret);
            let endText = '';

            if (caret == state.question.length) {
              endText = '';
            } else {
              endText = state.question.substring(caret, state.question.length);
            }

            state.question = frontText + '\n' + endText;
            return;
          }

          methods.getResult();
        }
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      safari,
      avatar: userStore.avatar as string,
    };
  },
});
</script>
<style scoped lang="scss">
.qa {
  scrollbar-width: none; 
  flex: 1;
  overflow-y: scroll;
  padding-bottom: 15px;
  .my-questions {
    display: flex;
    justify-content: flex-end;
    .question {
      background: #f25d8ed9;
      border-radius: 7px 0 7px 7px;
      font-size: 16px;
      font-family: PingFangSC-Medium, PingFang SC;
      font-weight: 500;
      color: #ffffff;
      line-height: 27px;
      margin-right: 12px;
      padding: 5px 10px;
      max-width: 100%;
      overflow-wrap: anywhere;
    }
  }
  .answer-box {
    display: flex;
    .answer-result {
      padding: 5px 10px;
      margin-left: 12px;
      max-width: 100%;
      background: #ffffff;
      border-radius: 0rem 7px 7px 7px;
      .search-time {
        .computing-time {
          height: 17px;
          font-size: 12px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #666666;
          line-height: 17px;
          margin-left: 7px;
        }
      }
      .keyword {
        height: 20px;
        font-size: 16px;
        line-height: 20px;
      }
      .text {
        margin-top: 4px;
        font-size: 16px;
        color: rgba(51, 51, 51, 1);
        line-height: 24px;
        margin-bottom: 4px;
      }
      .from-name {
        font-size: 16px;
        font-family: PingFangSC-Medium, PingFang SC;
        font-weight: 500;

        line-height: 24px;
        padding-top: 6px;
        border-top: 1px solid #f8f8f8;
        a {
          color: rgba(94, 139, 255, 1);
        }
      }
    }
  }
}
.send {
  // flex-basis: 100px;
  flex-shrink: 0;
  background: #ffffff;
  .question-box {
    width: 100%;

    border: none;
    padding: 10px;
    font-family: PingFangSC-Medium, PingFang SC;
    color: #646464;
    font-size: 15px;
    resize: none;
  }
  .question-box::-webkit-input-placeholder {
    font-size: 16px;
    color: #cccccc;
  }
}

.chat-detail-box {
  height: calc(100vh);
}
.isSafari {
  height: calc(100vh - 105px) !important;
}
@keyframes isLoading {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
.loading {
  animation: isLoading infinite 2.2s linear;
}

.text-loading {
  color: black;
}
.text-loading:after {
  overflow: hidden;
  display: inline-block;
  /* vertical-align: bottom; */
  animation: ellipsis 1.5s infinite linear;
  content: '\2026'; /* ascii code for the ellipsis character */
  /* \2026 == ... */
  /* content: "..." */
}
@keyframes ellipsis {
  from {
    width: 2px;
  }
  to {
    width: 15px;
  }
}
</style>
