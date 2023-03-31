<template>
  <div>
    <h4>留言吐槽</h4>
    <div class="text-sm mt-2 text-gray-700">
      喜欢or不喜欢，又或者想提出自己的需求，一起来聊两句吧～
    </div>
    <Comment
      class="mt-4"
      bizId="10ad4b705ebb4a8dacce9f58db4b65bd"
      :bizType="2"
    ></Comment>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import Comment from '@/components/comment/Index.vue';

export default defineComponent({
  components: {
    Comment,
  },
  setup() {},
});
</script>
