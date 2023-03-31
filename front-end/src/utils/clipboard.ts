import Clipboard from 'clipboard';
import { ElMessage } from 'element-plus';
export const clipboardSuccess = () =>
  ElMessage({
    message: '复制成功',
    type: 'success',
    duration: 1500,
  });

export const clipboardError = () =>
  ElMessage({
    message: '复制失败',
    type: 'error',
  });

export const handleClipboard = (text: string, el: Element) => {
  const clipboard = new Clipboard(el, {
    text: () => text,
  });
  clipboard.on('success', () => {
    clipboardSuccess();
    clipboard.destroy();
  });
  clipboard.on('error', () => {
    clipboardError();
    clipboard.destroy();
  });
  (clipboard as any).onClick(event);
};
