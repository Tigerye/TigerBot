// Parse the time to string
import wx from 'weixin-js-sdk';

export const parseTime = (
  time?: object | string | number | null,
  cFormat?: string
): string | null => {
  if (time === undefined || !time) {
    return null;
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}';
  let date: Date;
  if (typeof time === 'object') {
    date = time as Date;
  } else {
    if (typeof time === 'string') {
      if (/^[0-9]+$/.test(time)) {
        // support "1548221490638"
        time = parseInt(time);
      } else {
        // support safari
        // https://stackoverflow.com/questions/4310953/invalid-date-in-safari
        time = time.replace(new RegExp(/-/gm), '/');
      }
    }
    if (typeof time === 'number' && time.toString().length === 10) {
      time = time * 1000;
    }
    date = new Date(time);
  }
  const formatObj: { [key: string]: number } = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  };
  const timeStr = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key];
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value];
    }
    return value.toString().padStart(2, '0');
  });
  return timeStr;
};

// Format and filter json data using filterKeys array
export const formatJson = (filterKeys: any, jsonData: any) =>
  jsonData.map((data: any) =>
    filterKeys.map((key: string) => {
      if (key === 'timestamp') {
        return parseTime(data[key]);
      } else {
        return data[key];
      }
    })
  );

// Check if an element has a class
export const hasClass = (ele: HTMLElement, className: string) => {
  return !!ele.className.match(new RegExp('(\\s|^)' + className + '(\\s|$)'));
};

// Add class to element
export const addClass = (ele: HTMLElement, className: string) => {
  if (!hasClass(ele, className)) ele.className += ' ' + className;
};

// Remove class from element
export const removeClass = (ele: HTMLElement, className: string) => {
  if (hasClass(ele, className)) {
    const reg = new RegExp('(\\s|^)' + className + '(\\s|$)');
    ele.className = ele.className.replace(reg, ' ');
  }
};

// Toggle class for the selected element
export const toggleClass = (ele: HTMLElement, className: string) => {
  if (!ele || !className) {
    return;
  }
  let classString = ele.className;
  const nameIndex = classString.indexOf(className);
  if (nameIndex === -1) {
    classString += '' + className;
  } else {
    classString =
      classString.substr(0, nameIndex) +
      classString.substr(nameIndex + className.length);
  }
  ele.className = classString;
};

// scrollInfo
export const getScrollInfo = () => {
  const scrollHeight =
    document.documentElement.scrollHeight || document.body.scrollHeight; //document的滚动高度

  const clientHeight =
    document.documentElement.clientHeight || document.body.clientHeight; //可视区高度

  const scrollTop =
    document.documentElement.scrollTop || document.body.scrollTop; //已滚动高度

  const bottomDistance = scrollHeight - clientHeight - scrollTop; //距离底部高度

  return {
    scrollHeight,
    clientHeight,
    scrollTop,
    bottomDistance,
  };
};

// 封装一个函数 抛出去
// 存储滚动条位置
export const setStorageScroll = (name: string, el?: string) => {
  // 根据id存储已读列表
  let listScrollTop = 0;
  if (el) {
    listScrollTop = document.querySelector(el as any).scrollTop;
  } else {
    listScrollTop =
      document.documentElement.scrollTop || document.body.scrollTop;
  }

  sessionStorage.setItem(name, listScrollTop.toString());
};

export const formatterDate = (historyTime: string | number | undefined) => {
  if (!historyTime) return;
  const currentTime = new Date(historyTime).getTime();
  const timeDiff = new Date().getTime() - currentTime;
  let time = new Date().getTime() - timeDiff;
  let min = 60 * 1000;
  let hour = min * 60;
  let day = hour * 24;
  let twoDay = hour * 48;
  let threeDay = hour * 72;
  let fourDay = hour * 96;
  let fiveDay = hour * (24 * 5);
  let sixDay = hour * (24 * 6);
  let sevenDay = hour * (24 * 7);
  let eightDay = hour * (24 * 8);
  let nineDay = hour * (24 * 9);
  let tenDay = hour * (24 * 10);
  let elevenDay = hour * (24 * 10);
  let halfMonth = hour * (24 * 15);
  let oneMonth = hour * (24 * 30);
  let twoMonth = hour * (24 * 60);
  let threeMonth = hour * (24 * 90);

  let exceedHour = Math.floor(timeDiff / hour);
  let exceedMin = Math.floor(timeDiff / min);
  if (timeDiff < 0) {
    return '刚刚';
  }
  if (timeDiff < hour) {
    return exceedMin + '分钟前';
  }
  if (timeDiff < day) {
    return exceedHour + '小时前';
  }
  if (timeDiff >= day && timeDiff < twoDay) {
    return '1天前';
  }
  if (timeDiff >= twoDay && timeDiff < threeDay) {
    return '2天前';
  }
  if (timeDiff >= threeDay && timeDiff < fourDay) {
    return '3天前';
  }
  if (timeDiff >= fourDay && timeDiff < fiveDay) {
    return '4天前';
  }
  if (timeDiff >= fiveDay && timeDiff < sixDay) {
    return '5天前';
  }
  if (timeDiff >= sixDay && timeDiff < sevenDay) {
    return '6天前';
  }
  if (timeDiff >= sevenDay && timeDiff < eightDay) {
    return '7天前';
  }
  if (timeDiff >= eightDay && timeDiff < nineDay) {
    return '8天前';
  }
  if (timeDiff >= nineDay && timeDiff < tenDay) {
    return '9天前';
  }
  if (timeDiff >= tenDay && timeDiff < elevenDay) {
    return '10天前';
  }
  if (timeDiff >= elevenDay && timeDiff < halfMonth) {
    return '半月前';
  }
  if (timeDiff >= halfMonth && timeDiff < oneMonth) {
    return '1月前';
  }
  if (timeDiff >= oneMonth && timeDiff < twoMonth) {
    return '2月前';
  }
  if (timeDiff >= twoMonth && timeDiff < threeMonth) {
    return '3月前';
  }

  if (timeDiff > threeMonth) {
    let date = new Date(time);
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let day = date.getDate();
    let hour = date.getHours();
    let min = date.getMinutes();

    if (hour < 10) {
      // @ts-ignore
      hour = '0' + hour;
    }
    if (min < 10) {
      // @ts-ignore
      min = '0' + min;
    }
    return year + '-' + month + '-';
    // return year + '-' + month + '-' + day + ' ' + hour + ':' + min;
  }
};

export const isMobile = () => {
  return navigator.userAgent.match(
    /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
  );
};

export const isIphonex = () =>
  /iphone/gi.test(navigator.userAgent) &&
  window.screen &&
  window.screen.height === 812 &&
  window.screen.width === 375;

export const isSafari = () => {
  return (
    /Safari/.test(navigator.userAgent) && !/Chrome/.test(navigator.userAgent)
  );
};

export const getPlatForm = () => {
  let userAgentInfo = navigator.userAgent;
  let Agents = [
    'Android',
    'IOS',
    'iPhone',
    'SymbianOS',
    'Windows Phone',
    'iPad',
    'iPod',
  ];

  if (userAgentInfo.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)) {
    return 'ios';
  }

  for (let v = 0; v < Agents.length; v++) {
    if (userAgentInfo.toLowerCase().indexOf(Agents[v].toLowerCase()) > 0) {
      return Agents[v];
    }
  }
  return 'PC';
};

export const isPC = () => {
  return getPlatForm() == 'PC';
};
export const isIOS = () => {
  return !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
};
export const isAndroid = () => {
  let u = navigator.userAgent;
  return !!(u.indexOf('Android') > -1 || u.indexOf('Adr') > -1);
};
export const getWeixinEnv = () => {
  let env = null;
  wx.miniProgram.getEnv((res: any) => {
    if (res.miniprogram) {
      env = 'miniprogram';
    } else {
      env = 'wechat';
    }
  });
  return env;
};

export const getUuid = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = (Math.random() * 16) | 0,
      v = c == 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};
