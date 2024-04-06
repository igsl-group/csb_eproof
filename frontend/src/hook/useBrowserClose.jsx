import React, {
  useEffect,
} from 'react';
import {
  loginAPI,
} from "@/api/request";
import {
  removeAll
} from "@/api/auth";
import {useRequest} from "ahooks";

export default function useBrowserClose() {
  useEffect(() => {
    window.addEventListener('beforeunload', handleLogout);

    return () => {
      window.removeEventListener('beforeunload', handleLogout);
    };
  }, []);

  const handleLogout = () => {
    run('logout')
    removeAll();
    sessionStorage.removeItem('PMS-Meet');
    localStorage.removeItem('PMS-Token');
    sessionStorage.removeItem('PMS-User');
  };

  const { run } = useRequest(loginAPI, {
    manual: true,
    onSuccess: (result, params) => {
      switch (params[0]) {
        case 'logout':
          break;
      }
    },
    onError: (error) => {

    },
    onFinally: (params, result, error) => {
      switch (params[0]) {
        case 'logout':
          break;
      }
    },
  });

}
