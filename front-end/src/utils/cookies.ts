/*
 * @Description: cookies封装
 */
import Keys from '@/constant/key';
import Cookies from 'js-cookie';

export const getSidebarStatus = () => Cookies.get(Keys.sidebarStatusKey);
export const setSidebarStatus = (sidebarStatus: string) =>
  Cookies.set(Keys.sidebarStatusKey, sidebarStatus);

export const getLanguage = () => Cookies.get(Keys.languageKey);
export const setLanguage = (language: string) =>
  Cookies.set(Keys.languageKey, language, {
    expires: 1000,
  });

// export const getSize = () => Cookies.get(Keys.sizeKey)
// export const setSize = (size: string) => Cookies.set(Keys.sizeKey, size)

export const getToken = () => Cookies.get(Keys.tokenKey);

export const setToken = (token: string) => {
  Cookies.set(Keys.tokenKey, token, {
    expires: 1000,
  });
  Cookies.set(Keys.tokenKey, token, {
    expires: 1000,
    domain: '.algolet.com',
  });
};
export const removeToken = () => {
  Cookies.remove(Keys.tokenKey);
  Cookies.remove(Keys.tokenKey, {
    domain: '.algolet.com',
  });
};
