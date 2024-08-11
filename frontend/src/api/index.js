import axios from "axios";
import { getToken, setToken, removeToken } from '@/api/auth';
// import { Message } from '@arco-design/web-react';
import { API_ERROR_MESSAGE, LOGIN_ERROR_MESSAGE } from "../utils/util";
import { showLoading, hideLoading } from "@/components/Loading";

let baseURL = `/api/v1`;
// let token = "eyJhbGciOiJIUzI1NiJ9.eyJ1bmFtZSI6ImFkbWluX3Rlc3QiLCJkcHVzZXJpZCI6ImFkbWluX3Rlc3QiLCJzaWQiOjEsInN1YiI6ImFkbWluX3Rlc3QiLCJpYXQiOjE3MjIyMjcyMzF9.se9fvaAORkYtJQceKx0EzNcnhw31oXHKsZb3XaCnLW0";
// let token = "eyJhbGciOiJIUzI1NiJ9.eyJ1bmFtZSI6ImFkbWluX3Rlc3QiLCJkcHVzZXJpZCI6ImFkbWluX3Rlc3QiLCJzaWQiOjQsInN1YiI6ImFkbWluX3Rlc3QiLCJpYXQiOjE3MjI1OTI4MjN9.YsSD5V9IC137AWTbTG_QiiqorR21aWbDp9uSH3FnT3w";
const headers = {};

if (process.env.NODE_ENV === 'development') {
  // baseURL = 'http://192.168.26.130:8080/api/v1';
  // baseURL = 'http://192.168.1.170:8080/api/v1';
  baseURL = 'http://192.168.2.227:8081/api/v1';
  // baseURL = 'https://localhost:9001/api/v1';
  // headers.Authorization = `Bearer ${token}`;
}

const signRequest = axios.create({
  baseURL: 'http://localhost:9999',
  timeout: 1000 * 60 * 5,
  headers: {
    Accept: 'application/json, text/plain, */*',
    "Content-Type": "application/json;charset=UTF-8",
    ...headers,
  }
});

signRequest.interceptors.request.use(
  config => {
    // do something before request is sent
    // if (process.env.NODE_ENV === 'development') {
    config.headers['Authorization'] = `Bearer ${localStorage.getItem('eproof-token')}`;
    // }
    return config;
  },
  error => {
    // do something with request error
    return Promise.reject(error.response);
  }
);

const normalRequest = axios.create({
  baseURL,
  timeout: 1000 * 60 * 5,
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
    config.headers['Authorization'] = `Bearer ${localStorage.getItem('eproof-token')}`;

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
    //if (process.env.NODE_ENV === 'development') {
      config.headers['Authorization'] = `Bearer ${localStorage.getItem('eproof-token')}`;
    //}
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
    //if (process.env.NODE_ENV === 'development') {
      config.headers['Authorization'] = `Bearer ${localStorage.getItem('eproof-token')}`;
    //}
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

export default function sendRes (url, method, data, loading = true) {
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
          .then(res => resolve(res.data))
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
          method: "post",
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
    case 'signing-cert':
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
    default:
      if (loading) showLoading();
      // console.log('常规', url, method, data)
      return new Promise((resolve, reject) => {
        normalRequest.request({
          url,
          method,
          data: data || {}
        })
          .then(res => resolve(res.data))
          .catch(err => reject(err))
          .finally(() => {
            if (loading) hideLoading()
          });
      });
  }
};
