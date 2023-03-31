import { defineStore } from 'pinia';
import { CreateActionTypes } from './action-types';
// import { ImageSize } from '@/constant/imageSize';

export interface SettingDataType {
  width: number;
  height: number;
  promptWeight: number;
  imageStrength: number;
  steps: number;
  image: string;
  styleType: string;
  imageNumber: number;
  sizeId: number;
  coinTotal: number;
  advanced: boolean; //高级模式
  isArtstation: boolean; //梦境增强模式
  nIter: number;
  seed: number;
  modelVersion: string;
  modifiers: Array<string>;
}

export const CreateStore = defineStore('createImage', {
  state: () => {
    return {
      currentCreditsUse: 1.0, //生成图片消耗的积分计算结果，注意重置
      settingData: {
        width: 768,
        height: 768,
        modifiers: [] as Array<string>,
        promptWeight: 9,
        imageStrength: 0.8,
        steps: 50,
        image: '',
        styleType: 'stable',
        imageNumber: 1,
        sizeId: 127,
        coinTotal: 1.0,
        advanced: false, //高级模式
        isArtstation: false, //梦境增强模式
        nIter: 1,
        seed: Math.floor(Math.random() * 1000000000),
        modelVersion: 'v2.1',
      },
      imageSize: {},
      modelVersionList: [
        {
          value: 'v1.5',
          label: 'v1.5 更擅长画人物（建议512 x 512）',
        },
        {
          value: 'v2.1',
          label: 'v2.1 更擅长画风景（建议768 x 768）',
        },
      ],
    };
  },
  actions: {
    [CreateActionTypes.ACTION_SAVE_SIZE](sizeMap: object) {
      this.imageSize = sizeMap;

      const sizeInfo =
        this.imageSize[this.settingData.width][this.settingData.height];

      this.settingData.coinTotal = sizeInfo.credit;
      this.settingData.sizeId = sizeInfo.id;
    },
    [CreateActionTypes.ACTION_COUNT_CREDIT_USE](credits: number) {
      this.currentCreditsUse = credits;
    },

    [CreateActionTypes.SET_SETTING_DATA](settingData: SettingDataType) {
      this.settingData = settingData;

      const sizeInfo =
        this.imageSize[this.settingData.width][this.settingData.height];

      // this.settingData.coinTotal = sizeInfo.credit * this.settingData.nIter * 3;
      this.settingData.coinTotal = sizeInfo.credit;
      this.settingData.sizeId = sizeInfo.id;

      this[CreateActionTypes.ACTION_COUNT_CREDIT_USE](sizeInfo.credit);
    },
  },
});
