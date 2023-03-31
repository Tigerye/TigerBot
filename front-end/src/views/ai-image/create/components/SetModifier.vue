<template>
  <div class="text-gray-700">
    <div class="text-center w-full">
      <!-- <span class="text-xs font-meidum text-gray-500"
        >选择风格为AI提供更多可能性</span
      > -->
      <el-button
        class="w-full"
        size="default"
        plain
        @click="showModifierDialog"
        :disabled="disabled"
        >选择风格</el-button
      >
    </div>

    <Dialog v-model="showModel" title="选择风格（支持多选）" :maxWidth="900">
      <div>
        <h5>已选择的画风：</h5>
        <div class="flex items-center flex-wrap mt-2">
          <el-tag
            class="mb-2 mr-2"
            size="small"
            :key="index"
            v-for="(tag, index) in list"
            closable
            type="info"
            :disable-transitions="false"
            @close="handleClose(index)"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <div class="mt-4">
        <!-- <h5>添加修饰词</h5> -->
        <el-tabs v-model="modifierType" @tab-click="modifyTypeChange">
          <el-tab-pane
            v-for="(item, index) in classTypeList"
            :key="index"
            :label="item.classType"
            :name="item.classType"
          >
          </el-tab-pane>
        </el-tabs>
        <div style="max-height: 500px" class="overflow-auto">
          <div
            class="grid grid-cols-3 lg:grid-cols-4 lg:grid-cols-5 gap-1 md:gap-3"
          >
            <div
              class="relative border border-4 rounded-lg truncate cursor-pointer"
              :class="[
                currenSelectedModiferMap[v.name]
                  ? 'border border-indigo-300 border-4'
                  : '',
              ]"
              v-for="(v, index) in modifiersMap.modifierDtos"
              :key="index"
              @click="addKeyword(v.name)"
            >
              <div v-if="v.imgUrl">
                <img
                  class="relative w-full h-20 object-cover"
                  :src="v.imgUrl"
                  alt=""
                />
                <div class="text-xs w-full py-1 truncate px-1">
                  {{ v.name }}
                </div>
              </div>
              <div
                v-else
                class="text-white flex items-center justify-center px-2 py-1"
                :class="[
                  currenSelectedModiferMap[v.name] == v.name
                    ? 'bg-indigo-500 '
                    : 'bg-black',
                ]"
                style="--tw-bg-opacity: 0.6"
              >
                {{ v.name }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="text-right mt-4">
        <el-button class="w-full" type="info" @click="complete">完成</el-button>
      </div>
    </Dialog>
  </div>
</template>
<script lang="ts">
import {
  defineComponent,
  nextTick,
  reactive,
  toRefs,
  PropType,
  watch,
} from 'vue';
import Dialog from '@/components/dialog/Index.vue';
import { getModifierClassTypes, getModifierListByType } from '@/apis/image';

interface ClassModel {
  classType: string;
  classTypeName: string;
  modifierDtos: Array<DtosModel>;
}

interface DtosModel {
  classType: string;
  imgUrl: string;
  name: string;
  text: string;
}

export default defineComponent({
  name: 'ImageSetModifierComponents',
  components: {
    Dialog,
  },
  props: {
    default: {
      type: Array as PropType<Array<string>>,
      default: [],
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, context) {
    const state = reactive({
      showModel: false,
      classTypeList: [] as Array<ClassModel>,
      currenSelectedModiferMap: {}, //当前被选中的modifier
      modifierType: '',
      modifiersMap: {} as ClassModel,
      list: props.default,
      currentPromptIndex: 0,
    });

    const methods = reactive({
      async showModifierDialog() {
        state.showModel = true;
        await methods.getModifierClassTypes();
        await methods.getModifierListByType();
      },
      async getModifierClassTypes() {
        const res = await getModifierClassTypes();

        state.classTypeList = res?.data || [];
        state.modifierType = (state.classTypeList[0] as any).classType;
      },
      async getModifierListByType() {
        const data = {
          classType: state.modifierType,
        };
        const res = await getModifierListByType(data);

        state.modifiersMap = res?.data || {};
      },
      handleClose(i: number) {
        state.list.splice(i, 1);
        context.emit('modifierChange', state.list);
      },
      modifyTypeChange() {
        nextTick(async () => {
          await methods.getModifierListByType();
        });
      },
      addKeyword(name: string) {
        if (state.currenSelectedModiferMap[name]) {
          delete state.currenSelectedModiferMap[name];

          const index = state.list.indexOf(name);
          this.handleClose(index);
          return;
        }
        state.currenSelectedModiferMap[name] = true;
        state.list.push(name);
        context.emit('modifierChange', state.list);
      },
      complete() {
        state.showModel = false;
        context.emit('modifierChange', state.list);
      },
    });

    watch(
      () => props.default,
      () => {
        state.list = props.default;
        state.currenSelectedModiferMap = {};
        state.list.forEach((item) => {
          state.currenSelectedModiferMap[item] = true;
        });
      }
    );

    return {
      ...toRefs(state),
      ...toRefs(methods),
    };
  },
});
</script>
