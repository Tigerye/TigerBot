import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';

import './styles/index.scss';

import 'normalize.css';

import { loadAllPlugins } from './plugins';

import '@/permission';

const app = createApp(App);

// 加载所有插件
loadAllPlugins(app);

app.use(createPinia());
app.use(router);

app.mount('#app');
