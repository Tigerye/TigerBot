<template>
  <div>
    <div
      v-if="!token"
      @mouseover="showLoginMenu = true"
      @mouseleave="showLoginMenu = false"
    >
      <div class="relative">
        <a
          class="ml-2 btn relative z-20 bg-white rounded overflow-hidden"
          href="/login"
          @click.prevent="goLogin"
        >
          {{ $t('route.login') }}
        </a>
        <div
          v-show="showLoginMenu"
          class="absolute mt-1 min-w-full w-auto overflow-hidden z-10 pt-4 p-2 top-full -right-1 -mt-1"
        >
          <div class="triangle z-10"></div>
          <div
            class="relative min-w-full border border-gray-100 shadow-lg-black w-85 bg-white p-4 z-20"
          >
            <div class="text-sm mb-4">{{ $t('loginAbility.name') }}</div>
            <div class="grid grid-cols-2">
              <div class="flex items-center mb-4">
                <div class="text-sm ml-1">
                  {{ $t('loginAbility.ability1') }}
                </div>
              </div>
              <div class="flex items-center ml-2 mb-4">
                <div class="text-sm ml-1">
                  {{ $t('loginAbility.ability2') }}
                </div>
              </div>
              <div class="flex items-center">
                <div class="text-sm ml-1">
                  {{ $t('loginAbility.ability3') }}
                </div>
              </div>
              <div class="flex items-center ml-2">
                <div class="text-sm ml-1">
                  {{ $t('loginAbility.ability4') }}
                </div>
              </div>
            </div>
            <div
              class="p-2 w-full bg-indigo-300 hover:bg-indigo-500 text-white text-center mt-4 cursor-pointer"
              @click="goLogin"
            >
              {{ $t('loginAbility.login') }}
            </div>
            <div class="mt-4 text-xs text-center">
              {{ $t('loginAbility.firstUse') }}
              <span class="text-indigo-500 cursor-pointer" @click="goJoin">
                {{ $t('loginAbility.clickToRegister') }}</span
              >
            </div>
          </div>
        </div>
      </div>
    </div>
    <div
      v-if="token"
      @mouseover="showDropMenu = true"
      @mouseleave="showDropMenu = false"
    >
      <div class="relative ml-2 v2-dropdown user-logo-hover">
        <div
          class="cursor-pointer rounded-lg bg-gray-50 border-1 border-gray w-full h-full overflow-hidden relative user-logo"
          @click="goUser"
          style="z-index: 100; padding: 2px"
        >
          <img
            v-if="avatar"
            class="w-9 h-9 rounded-lg object-cover"
            :src="avatar"
            alt=""
          />
        </div>

        <div
          v-show="showDropMenu"
          class="absolute mt-1 min-w-full w-auto bg-white z-10 top-full -right-1 -mt-1 user-drop"
        >
          <div class="min-w-full border border-gray-100 rounded-lg shadow-lg">
            <!-- 昵称 -->
            <div class="flex justify-center mt-2">
              <div class="w-13 h-13 relative" @click="goUserDrop">
                <img
                  v-if="avatar"
                  class="w-13 h-13 cursor-pointer rounded-full object-cover relative"
                  :src="avatar"
                  :alt="name || ''"
                />
              </div>
            </div>
            <div
              class="self-start group-hover:underline text-center"
              style="margin-top: 4px; color: rgb(249, 117, 153)"
            >
              {{ name }}
            </div>

            <div
              class="flex justify-around text-center border-t border-b py-1 border-gray-100 mt-4 pt-2"
            >
              <!-- <div>
                    <div class="text-gray-500 text-xs">作品</div>
                    <div class="font-bold">10</div>
                  </div> -->
              <div @click="goFollow" class="cursor-pointer">
                <div class="text-gray-500 text-xs">
                  {{ $t('my.follow') }}
                </div>
                <div class="font-bold">{{ followCount }}</div>
              </div>
              <div @click="goFollower" class="cursor-pointer">
                <div class="text-gray-500 text-xs">
                  {{ $t('my.followers') }}
                </div>
                <div class="font-bold">{{ followerCount }}</div>
              </div>
            </div>

            <ul class="py-2" style="z-index: 2">
              <li>
                <a
                  class="flex items-center hover:bg-gray-100 cursor-pointer px-3 py-2 whitespace-nowrap v2-dropdown-entry text-sm justify-between"
                >
                  <span class="ml-2"
                    >{{ $t('my.credit') }}（{{ totalCredits }}）</span
                  >
                </a>
              </li>
              <!-- <li>
                    <a
                      class="flex items-center hover:bg-gray-100 cursor-pointer px-3 py-2 whitespace-nowrap v2-dropdown-entry text-sm"
                    >
                      <span class="ml-2">个人中心</span></a
                    >
                  </li> -->
              <!-- <li
                    class="flex items-center hover:bg-gray-100 cursor-pointer px-3 py-2 whitespace-nowrap v2-dropdown-entry text-sm"
                  >
                    <LangSelect class="right-menu-item hover-effect" />
                  </li> -->
            </ul>
            <div class="border-t border-gray-100 py-2">
              <div
                class="flex items-center hover:bg-gray-100 cursor-pointer px-3 py-2 whitespace-nowrap v2-dropdown-entry text-sm"
                @click.prevent="logout"
              >
                <img class="w-4 h-4" src="@/assets/images/logout.png" alt="" />
                <span class="ml-2">{{ $t('login.logout') }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, onMounted, reactive, toRefs } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { UserStore } from '@/store/user';
import LangSelect from '@/components/lang_select/Index.vue';
import { getUserFollowerCount, getUserFollowList } from '@/apis/homepage';
import { isMobile } from '@/utils';

export default defineComponent({
  name: 'avatarLogin',
  components: {
    LangSelect,
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const store = UserStore();

    const token = computed(() => {
      return store.token;
    });

    const avatar = computed(() => {
      return store.avatar;
    });

    const name = computed(() => {
      return store.name;
    });

    const id = computed(() => {
      return store.id;
    });

    const account = computed(() => {
      return store.account;
    });

    const totalCredits = computed(() => {
      return store.totalCredits;
    });

    store.getCredits();

    const state = reactive({
      unReadCount: 10,
      showLoginMenu: false,
      showDropMenu: false,
      followerCount: 0,
      followCount: 0,
    });

    const methods = reactive({
      go: (path: string) => {
        router.push(path);
      },
      goLogin() {
        if (route.path.match('login')) return;
        state.showLoginMenu = false;

        router.push(`/login?redirect=${route.path}`).catch((err) => {
          console.warn(err);
        });
      },
      goJoin() {
        if (route.path.match('register')) return;
        state.showLoginMenu = false;
        router.push(`/register?redirect=${route.path}`).catch((err) => {
          console.warn(err);
        });
      },
      goUser() {
        if (isMobile()) {
          return;
        }
        window.open(`${location.origin}/${account.value}`, '_self');
      },
      goUserDrop() {
        if (isMobile()) {
          router.push(`/${account.value}`);
          return;
        }
        window.open(`${location.origin}/${account.value}`, '_self');
      },
      goFollow() {
        router.push({
          path: `/${account.value}/follows`,
        });
      },
      goFollower() {
        router.push({
          path: `/${account.value}/followers`,
        });
      },
      logout() {
        store.logout();
      },
      async getUserFollowerCount() {
        const data = {
          userId: id.value,
        };

        const res = await getUserFollowerCount(data);
        state.followerCount = res?.data.count;
      },
      async getUserFollowCount() {
        const res = await getUserFollowList({ userId: id.value });
        state.followCount = res?.data.length;
      },
    });

    onMounted(() => {
      methods.getUserFollowerCount();
      methods.getUserFollowCount();
    });

    return {
      id,
      token,
      avatar,
      name,
      account,
      totalCredits,
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped lang="scss">
.user-drop {
  right: 0;
  z-index: 99;
  width: 260px;
}
.right-menu-item {
  display: inline-block;
  padding: 0 8px;
  height: 100%;
  font-size: 18px;
  color: #5a5e66;
  vertical-align: text-bottom;

  &.hover-effect {
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }
}
</style>
