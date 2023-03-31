// @ts-nocheck

import html2canvas from 'html2canvas';
import Canvas2Image from './canvas2Image';

const convertToImage = (container, options = {}) => {
  function createBaseCanvas(scale, width, height) {
    const canvas = document.createElement('canvas');
    canvas.width = width * scale;
    canvas.height = height * scale;
    // canvas.getContext("2d").scale(scale, scale);

    const context = canvas.getContext('2d');

    // 关闭抗锯齿
    context.mozImageSmoothingEnabled = false;
    context.webkitImageSmoothingEnabled = false;
    context.msImageSmoothingEnabled = false;
    context.imageSmoothingEnabled = false;

    context.scale(scale, scale);

    return canvas;
  }
  // 设置放大倍数
  const scale = window.devicePixelRatio;

  // 传入节点原始宽高
  const width = container.offsetWidth;
  const height = container.offsetHeight;

  // 创建用于绘制的基础canvas画布
  const canvas = createBaseCanvas(scale, width, height);

  // html2canvas配置项
  const opts = {
    scale,
    width,
    height,
    canvas,
    useCORS: true,
    ...options,
  };

  return html2canvas(container, opts).then((canvas) => {
    const imageEl = Canvas2Image.convertToJPEG(
      canvas,
      canvas.width,
      canvas.height
    );

    return imageEl;
  });
};

export default convertToImage;
