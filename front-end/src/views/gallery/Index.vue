<template>
  <div class="pt-65 relative">
    <div
      class="bgc-img fixed w-full h-40 left-0 top-0"
      :style="{
        'background-image': `url(https://x-pai.algolet.com/model/art_image/1520767362072139.png?Expires=1819937590&OSSAccessKeyId=LTAI5t8HoYusAPr5MffHTauz&Signature=yCthGfd8RGfP2YMAmKpN37%2BZy5w%3D)`,
      }"
    ></div>
    <Gallery class="relative"></Gallery>
  </div>
</template>
<script lang="ts">
import { defineComponent } from 'vue';
import Gallery from '@/business-components/gallery/Index.vue';

export default defineComponent({
  name: 'GalleryView',
  components: {
    Gallery,
  },
  setup() {},
});
</script>
<style scoped>
.bgc-img {
  z-index: 0;
  height: 500px;
  width: 100%;
  background-repeat: no-repeat;
  background-size: cover;
  opacity: 0.4;
  filter: blur(6px);

  background-position: center;

  -webkit-mask: linear-gradient(rgb(255, 255, 255), transparent);
}
</style>
