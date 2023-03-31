<template>
  <div class="flex justify-center w-full">
    <div
      class="item-boxs relative w-full"
      :style="{ visibility: moveSuccess ? 'visible' : 'visible' }"
    >
      <slot></slot>
    </div>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  reactive,
  toRefs,
  onMounted,
  onUnmounted,
  watch,
  nextTick,
  onActivated,
} from 'vue';
import { AppStore } from '@/store/app';
import { AppActionTypes } from '@/store/app/action-types';
import { debounce } from 'lodash-es';
import { useRoute } from 'vue-router';

export default defineComponent({
  name: 'Waterfall',
  props: {
    data: {
      type: Array,
    },
    columns: Number,
    elClass: {
      // 瀑布流滚动的父盒子，如果是局部瀑布流，就传对应盒子的class，如果是全屏的，则window触发scroll事件
      type: String,
    },
  },
  setup(props, context) {
    const appStore = AppStore();
    const route = useRoute();

    let spacing = 12; //格扇宽
    const state = reactive({
      moveSuccess: false,
      cardWidth: 300,
      column: 5,
      columnHeightArr: [] as number[],
    });

    onActivated(() => {
      // keepalive缓存会影响卡片加载，每次onActivated的时候自动初始化
      moveCard();
      addOptimizeScrollListener();
    });

    watch(
      () => props.data,
      () => {
        nextTick(() => {
          moveCard();
          addOptimizeScrollListener();
        });
      },
      { deep: true }
    );
    watch(
      () => props.columns,
      () => {
        methods.dynamicChangeColumn(); //动态控制列数

        addOptimizeScrollListener();
      }
    );

    // 1 网页第一次打开的时候，根据屏幕宽度自适应展示瀑布流
    // 2 如果手动设置column，则在store中存储，网页共享column
    // 3 手动改变屏幕宽度，column依旧会自适应展示瀑布流
    onMounted(() => {
      if (appStore.columnIsfirstSet) {
        resizeFnc();
      } else {
        methods.dynamicChangeColumn(); //动态控制列数
      }

      addOptimizeScrollListener();
      window.addEventListener('resize', resizeFnc);

      if (props.elClass) {
        const scrollEl = document.querySelector(props.elClass);
        if (scrollEl) {
          scrollEl.addEventListener('scroll', methods.debounceOpt);
        }
      } else {
        window.addEventListener('scroll', methods.debounceOpt);
      }
    });

    onUnmounted(() => {
      window.removeEventListener('resize', resizeFnc);

      if (props.elClass) {
        const scrollEl = document.querySelector(props.elClass);
        if (scrollEl) {
          scrollEl.removeEventListener('scroll', methods.debounceOpt);
        }
      } else {
        window.removeEventListener('scroll', methods.debounceOpt);
      }
    });

    function resizeFnc() {
      const waterfallBox = document.querySelector('.item-boxs') as HTMLElement;
      const waterfallBoxWidth = waterfallBox.offsetWidth;

      if (waterfallBoxWidth <= 440) {
        // 两列，宽度自适应
        state.column = 2;
        spacing = 8;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 440 && waterfallBoxWidth <= 768) {
        // 两列，宽度自适应
        state.column = 3;
        spacing = 8;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 768 && waterfallBoxWidth <= 950) {
        // 两列，宽度自适应
        state.column = 4;
        spacing = 12;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 950 && waterfallBoxWidth <= 1440) {
        // 两列，宽度自适应
        state.column = 5;
        spacing = 12;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 1440 && waterfallBoxWidth <= 1536) {
        // 两列，宽度自适应
        state.column = 5;
        spacing = 12;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 1560 && waterfallBoxWidth <= 1780) {
        // 两列，宽度自适应
        state.column = 5;
        spacing = 18;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 1780 && waterfallBoxWidth <= 2000) {
        // 两列，宽度自适应
        state.column = 6;
        spacing = 20;

        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 2000 && waterfallBoxWidth <= 2556) {
        // 定宽
        state.column = 7;
        spacing = 22;
        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );
      } else if (waterfallBoxWidth > 2556) {
        // 定宽
        state.column = 8;
        spacing = 24;
        state.cardWidth = 300;

        // getColumnHeightArr(state.column);
      }

      context.emit('getItemWidth', state.cardWidth);
      context.emit('getColumn', state.column);

      methods.columnChange();

      nextTick(() => {
        moveCard();
      });
    }

    function moveCard() {
      const waterfallBox = document.querySelector('.item-boxs') as HTMLElement;
      state.moveSuccess = false;

      // 瀑布流卡片
      const cards = document.querySelectorAll('.item-box');

      if (cards.length < 1) return;
      // 存储格栅的高度,如果已经存在，则复用
      let columnHeightArr = Array.from(
        {
          length: state.column,
        },
        () => {
          return 0;
        }
      );

      Array.prototype.forEach.call(cards, (card) => {
        card.style.width = state.cardWidth + 'px';
        const cardHeight = card.offsetHeight; //卡片高度
        const minHeight = Math.min(...columnHeightArr);
        const minIndex = columnHeightArr.indexOf(minHeight); // 获取高度最小格栅的索引

        // 处理卡片top的位置
        if (columnHeightArr[minIndex] === 0) {
          card.style.top = columnHeightArr[minIndex] + 'px';
        } else {
          card.style.top = columnHeightArr[minIndex] + spacing + 'px';
        }

        // 处理卡片左移位置
        card.style.left =
          state.cardWidth * minIndex + minIndex * spacing + 'px';

        // 重新计算格栅数组的高度
        if (columnHeightArr[minIndex] === 0) {
          columnHeightArr[minIndex] = columnHeightArr[minIndex] + cardHeight;
        } else {
          columnHeightArr[minIndex] =
            columnHeightArr[minIndex] + spacing + cardHeight;
        }
      });

      const articleHeight = Math.max(...columnHeightArr);

      waterfallBox.style.height = articleHeight + 60 + 'px';

      state.moveSuccess = true;
    }

    function getCardWidth(
      parentWidth: number,
      column: number,
      spacing: number
    ) {
      const cardWidth = (parentWidth - (column - 1) * spacing) / column;
      return cardWidth;
    }

    // 可是区域加载图片，非可视区域去除加载的图片，降低性能消耗
    function addOptimizeScrollListener() {
      const windowHeight = window.innerHeight;
      const TOLERANCE_HEIGHT = 200;

      optimizeListener(windowHeight, TOLERANCE_HEIGHT);
    }

    function optimizeListener(windowHeight: number, TOLERANCE_HEIGHT: number) {
      const cards = document.querySelectorAll('.item-box');

      Array.prototype.forEach.call(cards, (card, index) => {
        const rect = card.getBoundingClientRect();
        const img = card.querySelector('img');

        if (!img) return;

        if (
          !(rect.top > windowHeight + TOLERANCE_HEIGHT) &&
          !(rect.bottom < 0 - TOLERANCE_HEIGHT)
        ) {
          card.isOptimized = false;

          img.setAttribute('src', img.dataset.url);
        } else {
          if (card.isOptimized) return;

          card.isOptimized = true;

          img.setAttribute('src', ' ');
        }
      });
    }

    const methods = reactive({
      debounceOpt: debounce(function () {
        addOptimizeScrollListener();
      }, 100),
      dynamicChangeColumn() {
        const waterfallBox = document.querySelector(
          '.item-boxs'
        ) as HTMLElement;
        const waterfallBoxWidth = waterfallBox.offsetWidth;

        state.column = props.columns as number;
        state.cardWidth = getCardWidth(
          waterfallBoxWidth,
          state.column,
          spacing
        );

        methods.columnChange();
        context.emit('getItemWidth', state.cardWidth);

        nextTick(() => {
          moveCard();
        });
      },
      columnChange() {
        appStore[AppActionTypes.ACTION_SET_COLUMN](state.column);
      },
    });

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>

<style scoped>
img[src=''] {
  opacity: 0;
}
img[src=' '] {
  opacity: 0;
}
img[src='_'] {
  opacity: 0;
}
</style>
