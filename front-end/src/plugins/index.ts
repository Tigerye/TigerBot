/*
 * @Description: 加载插件文件
 */

// import { createApp } from "vue";

// /**
//  * @description 加载所有 Plugins
//  * @param  {ReturnType<typeofcreateApp>} app 整个应用的实例
//  */

// export function loadAllPlugins(app: any) {
//   const modules = import.meta.glob('./*.ts')
//   console.log('files-key', modules);

//   for (const key in modules) {
//     if (Object.prototype.hasOwnProperty.call(modules, key)) {
//       const module = modules[key];
//       module().then((mod: any): void => {
//         console.log('mod', mod);
//         mod.default(app)
//       })
//     }
//   }
// }

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

import i18n from '@/locales';

// import './vconsole';

import { install } from '@icon-park/vue-next/es/all';
import '@icon-park/vue-next/styles/index.css';

export function loadAllPlugins(app: any) {
  app.use(i18n);
  app.use(ElementPlus);

  install(app);
}
