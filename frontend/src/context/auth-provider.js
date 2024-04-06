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
  const [user, setUser] = useState('wilfred.lai');
  const [role, setRole] = useState('Admin');
  const [post, setPost] = useState('SA');
  const [section, setSection] = useState('');
  const [availablePosts, setAvailablePosts] = useState([]);
  // const [token, setToken] = useState(localStorage.getItem("site") || "");
  const navigate = useNavigate();

  const { runAsync: runLoginAPI } = useRequest(loginAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'authenticate':
          break;
        case 'profile':
          const data  = response.result?.data || {};
          setUser(data.user_name);
          setSection(data.section?.label || '');
          setRole(data.role?.label || '');
          setPost(data.selected_post?.name || '');
          setAvailablePosts(data.all_available_posts || []);
          break;
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
    },
  });

  const getProfile = async () => {
    return runLoginAPI('profile');
  };

  const loginAction = async (name = '') => {
    return runLoginAPI('authenticate', '');
  };

  const changePostAction = async (postId = '', name = '') => {
    return runLoginAPI('authenticate', toQueryString({}, { postId }));
  };

  useEffect(() => {
    window.loginAction = loginAction;
    window.getProfile = getProfile;
  }, []);

  const logOut = () => {
    setUser(null);
    navigate("/login");
  };

  return (
    <AuthContext.Provider value={{
      changePostAction,
      user,
      role,
      post,
      availablePosts,
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