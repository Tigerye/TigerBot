<template>
  <div>
    <Introduction :userInfo="userInfo"></Introduction>
    <PersonalTab class="md:mt-8" :userInfo="userInfo"></PersonalTab>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import Introduction from './introduction/Index.vue';
import PersonalTab from './personal-tab/Index.vue';

export default defineComponent({
  name: 'MyPersonalIntroduction',
  components: {
    Introduction,
    PersonalTab,
  },
  props: {
    userInfo: {
      type: Object,
      required: true,
    },
  },
  setup(props) {},
});
</script>
