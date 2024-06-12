import axios from "axios";
import { getToken, setToken, removeToken } from '@/api/auth';
// import { Message } from '@arco-design/web-react';
import { API_ERROR_MESSAGE, LOGIN_ERROR_MESSAGE } from "../utils/util";
import { showLoading, hideLoading } from "@/components/Loading";

let baseURL = `/api/v1`;
const token = "eyJhbGciOiJIUzI1NiJ9.eyJ1bmFtZSI6ImFkbWluX3Rlc3QiLCJkcHVzZXJpZCI6ImFkbWluX3Rlc3QiLCJzaWQiOjEsInN1YiI6ImFkbWluX3Rlc3QiLCJpYXQiOjE3MTgxNjA4NTB9.RYt95Y3feJC61CGnIMYW6JLAhOl9chkY0qpc6YiyaSs";
const headers = {};

if (process.env.NODE_ENV === 'development') {
  // baseURL = 'http://192.168.2.227:8080/api/v1';
  baseURL = 'http://192.168.2.227:8081/api/v1';
  headers.Authorization = `Bearer ${token}`;
}

const signRequest = axios.create({
  baseURL: 'http://192.168.2.177:8081',
  timeout: 1000 * 60 * 5,
  headers: {
    "Content-Type": "multipart/form-data",
    ...headers,

  }
});

const normalRequest = axios.create({
  baseURL,
  timeout: 5000000,
  withCredentials: true,
  headers: {
    Accept: 'application/json, text/plain, */*',
    "Content-Type": "application/json;charset=UTF-8",
    ...headers,
  }
});

normalRequest.interceptors.request.use(
  config => {
    // do something before request is sent
    // if (getToken() != null) {
    //   config.headers['Authorization'] = getToken()
    // }
    return config;
  },
  error => {
    // do something with request error
    return Promise.reject(error.response);
  }
);

normalRequest.interceptors.response.use(
  response => {

    // if the custom code is not 20000, it is judged as an error.
    if (response.status !== 200) {
      return Promise.reject(new Error(response.data.msg || 'Error'))
    } else {
      return response
    }
  },
  error => {
    if (error.response.status === 403 && (error.response.request.responseURL.includes("/token"))) {
      // window.location.href = "/Unauthorized";
    }

    // else if (error.response.status === 403 && (error.response.request.responseURL.includes("authenticate"))) {
    //   // Message.warning(LOGIN_ERROR_MESSAGE);
    //   if (error.response.data.properties.code && error.response.data.properties.code === "password.expire.exception") {
    //     window.location.href = "/SOM/ChangePassword"
    //   }
    // } else if (error.response.status === 400 && error.response.data.properties) {
    //   if (["jwt.token.invalid.exception", "jwt.token.replaced.exception"].includes(error.response.data.properties.code)) {
    //     window.location.href = `/SOM`;
    //   } else {
    //     // Message.warning(error.response.data.properties.message);
    //   }
    // } else {
    //   // Message.warning(API_ERROR_MESSAGE);
    // }
    return Promise.reject(error.response);
  }
);

const fileRequest = axios.create({
  // baseURL: 'http://127.0.0.1:8080',
  // baseURL: 'http://192.168.2.234',
  baseURL,
  timeout: 5000000,
  headers: {
    "Content-Type": "multipart/form-data",
    ...headers,
  }
});

fileRequest.interceptors.request.use(
  config => {
    // do something before request is sent
    // if (getToken() != null) {
    //   config.headers['Authorization'] = getToken()
    // }
    return config;
  },
  error => {
    // do something with request error
    return Promise.reject(error);
  }
);

fileRequest.interceptors.response.use(
  response => {
    // if (response.headers.authorization != null) {
    //   setToken(response.headers.authorization);
    // }
    return response;
  },
  error => {

    if (error.response.status === 403 && !(error.response.request.responseURL.includes("authenticate"))) {
      // window.location.href = "/login";
    } else if (error.response.status === 403 && (error.response.request.responseURL.includes("authenticate"))) {
      // Message.warning(LOGIN_ERROR_MESSAGE);
      if (error.response.data.properties.code && error.response.data.properties.code === "password.expire.exception") {
        window.location.href = "/SOM/ChangePassword"
      }
    } else if (error.response.status === 400 && error.response.data.properties) {
      if (["jwt.token.invalid.exception", "jwt.token.replaced.exception"].includes(error.response.data.properties.code)) {
        window.location.href = `/SOM`;
      } else {
        // Message.warning(error.response.data.properties.message);
      }
    } else {
      // Message.warning(API_ERROR_MESSAGE);
    }
    return Promise.reject(error.response);
  }
);
const downloadRequest = axios.create({
  // baseURL: 'http://127.0.0.1:8080',
  // baseURL: `http://192.168.2.234`,
  baseURL,
  timeout: 5000000,
  headers: {
    "Content-Type": "application/x-www-form-urlencoded",
    ...headers,
  }
});
downloadRequest.interceptors.request.use(
  config => {
    // do something before request is sent
    // if (getToken() != null) {
    //   config.headers['Authorization'] = getToken()
    // }
    return config;
  },
  error => {
    // do something with request error
    return Promise.reject(error.response);
  }
);

downloadRequest.interceptors.response.use(
  response => {
    // if (response.headers.authorization != null) {
    //   setToken(response.headers.authorization);
    // }
    // console.log(response);
    return response;
  },
  error => {
    // message.error(error);
    // console.log(error.response, 'error');
    return Promise.reject(error.response);
  }
);

export default function sendRes (url, method, data) {
  switch (method) {
    case 'authenticate':
      showLoading();
      return new Promise((resolve, reject) => {
        normalRequest.request({
          url,
          method: "get",
          data: {},
          headers: data || {},
        })
          .then(res => resolve(res))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'formData':
      showLoading();
      return new Promise((resolve, reject) => {
        fileRequest.request({
          url,
          method: "post",
          data: data || {},
        })
          .then(res => resolve(res))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'form-data-put':
      showLoading();
      return new Promise((resolve, reject) => {
        fileRequest.request({
          url,
          method: "put",
          data: data || {},
        })
          .then(res => resolve(res))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'download':
      showLoading();
      return new Promise((resolve, reject) => {
        downloadRequest.request({
          url,
          method: "get",
          responseType: "blob",
          data: data || {},
        })
          .then(res => resolve(res))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'download-put':
      showLoading();
      return new Promise((resolve, reject) => {
        normalRequest.request({
          url,
          method: "put",
          responseType: "blob",
          data: data || {},
        })
          .then(res => resolve(res))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'sign':
      showLoading();
      return new Promise((resolve, reject) => {
        signRequest.request({
          url,
          method: "post",
          responseType: "blob",
          data: data || {},
        })
          .then(res => resolve(res))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'sign-string':
      showLoading();
      return new Promise((resolve, reject) => {
        signRequest.request({
          url,
          method: "post",
          data: data || {},
        })
          .then(res => resolve(res.data))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'get-signing-cert':
      showLoading();
      return new Promise((resolve, reject) => {
        signRequest.request({
          url,
          method: "get",
          data: data || {},
        })
          .then(res => resolve(res.data))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    case 'signing-cert':
      showLoading();
      return new Promise((resolve, reject) => {
        signRequest.request({
          url,
          method: "get",
          data: data || {},
        })
          .then(res => resolve(res.data))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
    default:
      showLoading();
      // console.log('常规', url, method, data)
      return new Promise((resolve, reject) => {
        normalRequest.request({
          url,
          method,
          data: data || {}
        })
          .then(res => resolve(res.data))
          .catch(err => reject(err))
          .finally(() => hideLoading());
      });
  }
};
