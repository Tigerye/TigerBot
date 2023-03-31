import { defineConfig } from 'vite';
import { resolve, dirname } from 'node:path';
import vue from '@vitejs/plugin-vue';
import { fileURLToPath } from 'url';
import vueJsx from '@vitejs/plugin-vue-jsx';
import vueI18n from '@intlify/vite-plugin-vue-i18n';

export default defineConfig({
  server: {
    host: '0.0.0.0',
  },
  plugins: [
    vue(),
    vueJsx(),
    vueI18n({
      include: resolve(
        dirname(fileURLToPath(import.meta.url)),
        './path/to/src/locales/**'
      ),
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import './src/styles/_mixins.scss';@import './src/styles/_variables.scss';`,
      },
    },
  },
});
