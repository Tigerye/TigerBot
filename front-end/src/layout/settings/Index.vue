<template>
  <div class="page-wrapper overflow-x-hidden">
    <NavBar></NavBar>
    <div class="flex flex-col flex-1 pt-65">
      <div
        class="container relative space-y-4 flex flex-col md:grid md:space-y-0 w-full md:grid-cols-12 md:gap-6 pt-6"
      >
        <section
          class="pt-6 border-gray-100 md:col-span-4 lg:col-span-3 border rounded-xl bg-gradient-to-l from-gray-50-to-white"
        >
          <a
            class="cols-span-9 flex md:block lg:flex pb-4 px-4 mb-4 items-center"
            :href="`/${account}`"
            @click.prevent="go(`/${account}`)"
          >
            <img
              v-if="avatar"
              alt=""
              class="rounded-full overflow-hidden w-12 h-12 flex-none mr-2 object-cover"
              :src="avatar"
            />
            <div>
              <h1
                class="text-lg md:text-base lg:text-xl font-bold leading-none"
              >
                {{ name }}
              </h1>
            </div>
          </a>
          <nav role="navigation">
            <ul
              class="text-lg text-gray-600 border-t border-gray-100 divide-y divide-gray-100 dark:divide-gray-900"
            >
              <li v-for="(item, index) in menu" :key="index">
                <a
                  class="flex items-center relative overflow-hidden px-3 py-2 group dark:from-gray-900 dark:to-transparent text-gray-900 dark:text-gray-200"
                  :class="{
                    'bg-gradient-to-r': item.pathName === pathName,
                    'from-gray-100': item.pathName === pathName,
                    'font-semibold': item.pathName === pathName,
                  }"
                  :href="`/settings/${item.path}`"
                  @click.prevent="jump(item.path)"
                  ><span
                    class="block group-hover:bg-gray-100 dark:group-hover:bg-gray-900 px-2 py-0.5 rounded"
                    >{{ item.name }}</span
                  ></a
                >
              </li>
            </ul>
          </nav>
        </section>
        <section
          class="border-gray-100 md:col-span-8 lg:col-span-9 xl:col-span-9 md:px-4"
        >
          <router-view></router-view>
        </section>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  onBeforeMount,
  reactive,
  toRefs,
  computed,
} from 'vue';
import NavBar from '../img-main/components/nav_bar/Index.vue';
import { useRoute, useRouter } from 'vue-router';
import { UserStore } from '@/store/user';

export default defineComponent({
  name: 'Layout',
  components: {
    NavBar,
  },

  setup() {
    const userStore = UserStore();
    const route = useRoute();
    const router = useRouter();

    const state = reactive({
      pathName: 'profile',
      menu: [
        {
          pathName: 'profile',
          path: 'profile',
          name: '基本信息',
        },
        {
          pathName: 'account',
          path: 'account',
          name: '账号',
        },
      ],
    });

    const account = computed(() => {
      return userStore.account;
    });
    const avatar = computed(() => {
      return userStore.avatar;
    });

    const name = computed(() => {
      return userStore.name;
    });

    const methods = reactive({
      go(path: string) {
        router.push({
          path,
        });
      },
      jump(path: string) {
        state.pathName = path;
        router.push({
          path: `/settings/${path}`,
        });
      },
    });

    onBeforeMount(async () => {
      state.pathName = route.path.split('/')[2] || 'profile';
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
      account,
      avatar,
      name,
    };
  },
});
</script>
<style scoped>
.page-wrapper {
  background: #fafafc;
  min-height: 100vh;
}
</style>
