import { useContext, createContext, useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {useRequest} from "ahooks";
import {
  loginAPI
} from "@/api/request";
import {toQueryString} from "../utils/util";
import { setToken } from "../utils/storage";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState('');
  const [role, setRole] = useState([]);
  const [permissions, setPermissions] = useState([]);
  const [post, setPost] = useState('');
  const [section, setSection] = useState('');
  const [availablePosts, setAvailablePosts] = useState([]);
  // const [token, setToken] = useState(localStorage.getItem("site") || "");
  const navigate = useNavigate();

  const { runAsync: runLoginAPI } = useRequest(loginAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'authenticate':
        {
          const token = response.data;
          console.log(`Token: ${token}`);
          localStorage.setItem('eproof-token', token);
          break;
        }
        case 'profile':
        {
          const data  = response.data || {};
          let uniquePermissionCodes = [];



          if (data.roles) {
            let permissionCodes = data.roles.flatMap((row) => row.permissions.flatMap((permission) => permission.code));
            uniquePermissionCodes =  [...new Set(permissionCodes)];
           // console.log(uniquePermissionCodes)
          }

          setUser(data.dpUserId);
          setSection(data.section?.label || '');
          setRole(data.roles);
          setPermissions(uniquePermissionCodes);
          setPost(data.post || '');
          setAvailablePosts(data.all_available_posts || []);
          break;
        }
        default:
          break;
      }

    },
    onError: (response, params) => {
      switch (params[0]) {
        case 'authenticate':
          break;
        default:
          break;
      }
    },
    onFinally: (params, result, error) => {
      switch (params[0]) {
        case 'logout':
          localStorage.removeItem('eproof-token');
          setUser('');
          setRole([]);
          setPermissions([]);
          setPost("");
          setSection("");
          setAvailablePosts([]);
          navigate("/Logout");
          break;
        default:
          break;
      }
    },
  });

  const getProfile = async () => {
    return runLoginAPI('profile');
  };

  const loginAction = async (values = '') => {
    return runLoginAPI('authenticate', values);
  };

  const changePostAction = async (postId = '', name = '') => {
    return runLoginAPI('authenticate', toQueryString({}, { postId }));
  };

  useEffect(() => {
    window.loginAction = loginAction;
    window.getProfile = getProfile;
  }, []);

  const logOut = () => {
    runLoginAPI('logout');

  };

  return (
    <AuthContext.Provider value={{
      changePostAction,
      user,
      role,
      post,
      availablePosts,
      permissions,
      section,
      loginAction,
      logOut,
      getProfile
    }}>
      {children}
    </AuthContext.Provider>
  );

};

export default AuthProvider;

export const useAuth = () => {
  return useContext(AuthContext);
};