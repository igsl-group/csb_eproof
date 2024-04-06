/*
 * @Author: your name
 * @Date: 2023-02-08 09:49:54
 * @LastEditTime: 2023-02-24 18:12:07
 * @Description: In User Settings Edit
 * @FilePath: \frontend\src\api\auth.js
 */
// import Cookies from 'js-cookie';

const tokenKey = 'e-token'
export const TokenUser = 'PMS-User'
export const HEARTAPIINTE = 'heart-interval';


export function getToken() {
  return localStorage.getItem(tokenKey)
}

export function setToken(token) {
  return localStorage.setItem(tokenKey, token)
}

export function removeToken() {
    return localStorage.removeItem(tokenKey);
    // return Cookies.remove(TokenKey);
}
export function setUser(user) {
    return sessionStorage.setItem(TokenUser, user);
}

export function getUser() {
    return JSON.parse(sessionStorage.getItem(TokenUser))
}

export function setUsername(userId, res) {
    return sessionStorage.setItem(userId, res)
    // return Cookies.set(TokenKey, token);
}
export function setOffice(office, res) {
    return sessionStorage.setItem(office, res)
    // return Cookies.set(TokenKey, token);
}
export function getUsername() {
    if (getUser() != null) {
        return getUser().name;
    }
    return "";
}

export function getPhone() {
    if (getUser() != null) {
        return getUser().phoneNumber;
    }
    return "";
}

export function getPost() {
    if (getUser() != null) {
        return getUser().post.postCode;
    }
    return "";
}

export function getOffice() {
    if (getUser() != null) {
        return getUser().office;
    }
    return "";
}

export function removeAll() {
    return localStorage.clear()
}

export function setHeartInterval(interval) {
    return localStorage.setItem(HEARTAPIINTE, interval)
}

export function getHeartInterval() {
    return localStorage.getItem(HEARTAPIINTE)
}
