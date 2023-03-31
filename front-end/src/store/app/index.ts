import { defineStore } from 'pinia';
import { AppActionTypes } from './action-types';
import { getLocale } from '@/locales';
import { setLanguage, getSidebarStatus } from '@/utils/cookies';
import { RouteLocationNormalized } from 'vue-router';

export interface CacheView extends Partial<RouteLocationNormalized> {
  title?: string;
}
type MatchPattern = string | RegExp | (string | RegExp)[];

export const AppStore = defineStore('app', {
  state: () => {
    return {
      language: getLocale(),
      sidebar: {
        opened: getSidebarStatus() !== 'closed',
        withoutAnimation: false,
      },
      imageColumns: 1,
      columnIsfirstSet: true, //是否首次手动设置过column
      cacheViews: [] as MatchPattern,
    };
  },
  actions: {
    [AppActionTypes.ACTION_SET_LANGUAGE](language: string) {
      this.language = language;
      setLanguage(language);
    },
    [AppActionTypes.ACTION_SET_COLUMN](column: number) {
      this.imageColumns = column;
      if (this.columnIsfirstSet) return;
      this.columnIsfirstSet = false;
    },
    [AppActionTypes.ACTION_ADD_CACHE_VIEW](view: CacheView) {
      if (
        (this.cacheViews as Array<string | undefined>).includes(
          view.name?.toString()
        )
      ) {
        return;
      }

      if (view.meta?.keepAlive) {
        (this.cacheViews as Array<string | undefined>).push(
          view.name?.toString()
        );
      }
    },
    [AppActionTypes.ACTION_DELETE_CACHE_VIEW](view: CacheView) {
      if (view.name === null) return;
      const index = (this.cacheViews as Array<string | undefined>).indexOf(
        view.name?.toString()
      );
      index > -1 &&
        (this.cacheViews as Array<string | undefined>).splice(index, 1);
    },
  },
});
