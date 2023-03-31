<template>
  <div
    v-show="showModel"
    class="share-art-image-container w-full h-full fixed left-0 top-0 bg-gray-500 flex flex-col items-center overflow-y-auto"
    :class="[posterPositionClass]"
    style="--tw-bg-opacity: 0.5; z-index: 999"
  >
    <!-- <div :class="url ? 'text-white' : 'text-red-500'" style="margin-top: 80px">
      {{ remindMsg }}
    </div> -->
    <div
      class="poster-container bg-gradient-to-br from-white to-white rounded overflow-hidden relative mt-2 flex-shrink-0"
      @click.stop
      style="
        width: 620px;
        max-width: 96%;
        margin-top: 10px;
        margin-bottom: 10px;
      "
    >
      <img class="img-box w-full h-full absolute left-0 top-0" :src="image" />

      <div class="p-2 relative">
        <div class="p-2 bg-white rounded-lg relative">
          <img :src="image" class="w-full" alt="" />
          <div
            class="absolute w-full bottom-2 border-0"
            style="
              height: 70px;
              width: 97%;
              background: linear-gradient(transparent, #4a4a4a);
            "
          >
            <div
              class="rounded-b-lg absolute bottom-2 left-2 overflow-hidden"
              style="margin-top: -2px; max-width: 70%"
            >
              <div
                v-if="title"
                class="text-sm font-bold text-white truncate"
                style="--tw-text-opacity: 0.8"
                v-html="title"
              ></div>

              <div
                class="text-xs font-bold mt-0.5 text-gray-300 truncate"
                style="--tw-text-opacity: 0.5"
              >
                —— {{ author }}
              </div>
            </div>

            <div class="absolute right-2 bottom-2" style="width: 15%">
              <img class="w-full" :src="qrcode" alt="" />
            </div>
          </div>
        </div>
      </div>

      <img
        @click.stop
        v-if="url"
        class="absolute left-0 top-0 w-full h-full opacity-0"
        :src="url"
        alt=""
      />
      <div
        v-if="url"
        class="absolute right-1 top-1 cursor-pointer"
        @click="close"
      >
        <i class="el-icon-close text-lg"></i>
      </div>
    </div>
    <div
      v-if="url && !isMobile"
      class="bg-white mt-0 p-2 rounded flex items-center"
      style="width: 620px; max-width: 96%; margin-bottom: 10px"
      @click.stop
    >
      <div class="relative flex-1">
        <el-input v-model="shareUrl" type="text" size="large" disabled />
        <icon-copy-link
          class="absolute right-2 top-1/2 translate-y-1/2"
          theme="outline"
          size="19"
          fill="#4a4a4a"
          @click.stop="copyLink()"
        />
      </div>
      <el-button
        class="ml-2"
        size="large"
        color="#626aef"
        @click.stop="downloadPoster"
        >保存海报</el-button
      >
    </div>
    <div
      v-if="url && isMobile"
      class="bg-white mt-0 p-2 rounded text-center text-sm py-4 text-gray-700"
      style="width: 620px; max-width: 96%; margin-bottom: 10px"
      @click.stop
    >
      长按海报 保存或分享～
    </div>
  </div>
</template>
<script>
import QRCode from 'qrcode';
import miniprogramImg from '@/assets/images/miniprogram.jpg';
import convertToImage from '@/utils/convertToImage.js';
import { ElMessage } from 'element-plus';
import { isMobile } from '@/utils';

export default {
  name: 'ShareImageCard',
  props: {
    image: String,
    title: String,
    author: String,
    id: Number | String,
  },
  computed: {
    shareUrl() {
      return `${location.origin}/image/${this.id}`;
    },
  },
  data() {
    return {
      // shareUrl: `${location.origin}/image/${this.id}`,
      miniprogramImg,
      posterPositionClass: 'justify-center',
      remindMsg: '生成海报大概需要几秒的时间，请稍等一下下～',
      showModel: false,
      url: '',
      qrcode: '',
      data: {},
      isMobile: isMobile(),
    };
  },
  watch: {
    showModel: {
      deep: true,
      async handler() {
        if (this.showModel) {
          this.url = '';
          this.remindMsg = '生成海报大概需要几秒的时间，请稍等一下下～';
          await this.initQrcode();
          this.initEvent();
          this.createCard();
        } else {
          this.destroyEvent();
        }
      },
    },
  },

  beforeUnmount() {
    this.destroyEvent();
  },

  methods: {
    copyLink() {
      const input = document.createElement('textarea');
      document.body.appendChild(input);
      input.value = this.shareUrl;
      input.select();
      try {
        var successful = document.execCommand('copy');

        if (!successful) {
          throw new Error('copy failed');
        }
        document.body.removeChild(input);
        ElMessage({
          message: '复制链接成功～',
          type: 'success',
          duration: 1500,
        });
        return true;
      } catch (err) {}
      return false;
    },
    initEvent() {
      document
        .querySelector('.share-art-image-container')
        .addEventListener('click', this.clickHidden);

      window.addEventListener('keyup', this.keyUpHidden);
    },
    destroyEvent() {
      window.removeEventListener('keyup', this.keyUpHidden);
      document.removeEventListener('click', this.clickHidden);
    },
    clickHidden() {
      if (this.showModel) {
        this.showModel = false;

        this.$emit('close');
      }
    },
    keyUpHidden(e) {
      const escape = e.key.toLowerCase();
      if (escape === 'escape') {
        if (this.showModel) {
          this.showModel = false;
          this.$emit('close');
        }
      }
    },
    async initQrcode() {
      try {
        let qrcode = '';
        qrcode = await QRCode.toDataURL(`${location.origin}/image/${this.id}`);
        this.qrcode = qrcode;
      } catch (error) {
        console.log('error', error);
      }
    },
    createCard() {
      const imgs = document.querySelectorAll('.share-art-image-container img');

      const timer = setInterval(() => {
        const state = Array.prototype.every.call(imgs, (img) => {
          return img.src && img.complete;
        });
        if (state) {
          this.calculatePosition();
          clearInterval(timer);

          setTimeout(() => {
            setTimeout(() => {
              const container = document.querySelector(
                '.share-art-image-container .poster-container'
              );

              const opt = {};
              const scrollTop =
                document.documentElement.scrollTop || document.body.scrollTop;
              window.scroll(0, 0);
              convertToImage(container, opt).then((img) => {
                this.url = img.src;
                window.scrollTo(0, scrollTop);
                // window.scrollTo({
                //   top: scrollTop,
                //   left: 0,
                //   behavior: "smooth",
                // });
                this.remind();
              });
            }, 10);
          }, 20);
        }
      }, 20);
    },
    // 计算生成图片的高度，如果超过屏幕高度-65*2px,就顶部对齐，如果没有超过，就剧中对齐
    calculatePosition() {
      const resultImg = document.querySelector('.poster-container');
      const resultImgHeight = resultImg.offsetHeight;
      const screenHeight =
        document.documentElement.clientHeight || document.body.clientHeight; //可视区高度

      if (screenHeight >= resultImgHeight + 75 + 10 + 10 + 30) {
        this.posterPositionClass = 'justify-center';
      } else {
        this.posterPositionClass = 'justify-start';
      }
    },

    downloadPoster() {
      var alink = document.createElement('a');
      alink.href = this.url;
      alink.download = this.title; //图片名
      alink.click();
    },
    remind() {
      setTimeout(() => {
        this.remindMsg = '长按图片进行分享~';
        // if (!Helper.isMobile()) {
        //   this.remindMsg = '鼠标右键点击图片复制，进行分享！';
        // } else {
        //   this.remindMsg = '长按图片进行分享哟！';
        // }
      }, 10);
    },
    close() {
      this.showModel = false;
    },
  },
};
</script>
<style lang="scss">
.share-art-image-container {
  .img-box {
    transform: scale(20);
    opacity: 0.7;
  }
  .modifiers {
    .el-tag.el-tag--info {
      background-color: rgb(255 255 255 / 50%);
    }
  }
}
</style>
