<template>
  <div class="container">
    <div
      class="border-b flex items-center flex-col flex-col-reverse md:flex-row justify-between"
    >
      <tab-row
        v-model="activePath"
        :list="mainList"
        @tab-click="tabChange"
        :size="'medium'"
      ></tab-row>

      <tab-row
        v-model="activePath"
        :list="ortherList"
        :size="'small'"
        @tab-click="tabChange"
      ></tab-row>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, onBeforeMount, reactive, toRefs, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import TabRow from '@/components/tab/Row.vue';
import { getUserFollowerCount, getUserFollowList } from '@/apis/homepage';
import { getUserImages } from '@/apis/homepage';

export default defineComponent({
  name: 'MyPersonalTabComponent',
  components: {
    TabRow,
  },
  props: {
    userInfo: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const route = useRoute();
    const router = useRouter();

    const state = reactive({
      userData: props.userInfo,
      activePath: '',
      mainList: [
        {
          name: '',
          label: '发布作品',
        },
        {
          name: 'history',
          label: '历史',
        },
        {
          name: 'life',
          label: '动态',
        },
        {
          name: 'collections',
          label: '收藏',
        },
      ],
      ortherList: [
        {
          name: 'follows',
          label: '关注数',
        },
        {
          name: 'followers',
          label: '粉丝数',
          number: 0,
        },
        {
          name: 'likes',
          label: '喜欢',
          number: 0,
        },
        {
          name: 'likes',
          label: '获赞数',
          number: 0,
          disabled: true,
        },
      ],
    });

    if (props.userInfo.role != 'OWNER' && props.userInfo.role == 'ADMIN') {
      state.mainList.splice(1, 1);
    }

    const methods = reactive({
      tabChange() {
        router.push({
          path: `/${state.userData.account}/${state.activePath}`,
        });
      },
      async getUserFollowerCount() {
        const data = {
          userId: props.userInfo.user.id,
        };
        const res = await getUserFollowerCount(data);
        state.ortherList[1].number = res?.data.count;
      },
      async getUserFollowCount() {
        const res = await getUserFollowList({ userId: props.userInfo.user.id });
        let list = res?.data;
        list = list.filter((item: any) => {
          return item.bizType == 0;
        });
        state.ortherList[0].number = list.length;
      },
      // async getUserImageCount() {
      //   const res = await getUserImages({
      //     status: 1, // 1已发布 //''全部
      //     keyword: '',
      //     processStatus: 5,
      //     userId: props.userInfo.user.id,
      //   });
      //   state.mainList[0].number = res?.data.total;
      // },
    });

    state.activePath = route.path.split('/')[2] || '';

    watch(
      () => route.path,
      () => {
        state.activePath = route.path.split('/')[2] || '';
      }
    );

    onBeforeMount(() => {
      // methods.getUserImageCount();
      methods.getUserFollowerCount();
      methods.getUserFollowCount();
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
<style>
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 62px;
  font-weight: 600;
}
</style>
