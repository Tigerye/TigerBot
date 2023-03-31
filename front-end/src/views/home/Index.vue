<template>
  <div>
    <!-- <div class="pt-65" style="position: relative; z-index: 12">
      <GptBanner></GptBanner>
    </div> -->
    <Banner></Banner>

    <Gallery></Gallery>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import Gallery from '@/business-components/gallery/Index.vue';
import Banner from './components/banner/Index.vue';
import GptBanner from '@/views/chat-gpt/Index.vue';

export default defineComponent({
  name: 'HomepageView',
  components: {
    Gallery,
    Banner,
    GptBanner,
  },
});
</script>
