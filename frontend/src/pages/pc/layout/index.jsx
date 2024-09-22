import React, { useState, useEffect } from 'react';
import { NavLink, useLocation, Outlet, useNavigate, createSearchParams, Link } from "react-router-dom";
import {MainContext} from "../../../context/mainContext";
import { localRouters } from '@/routes';
import IsMobile from '@/hook/isMobile';
import styles from './style/index.module.less';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DownOutlined
} from '@ant-design/icons';
import { getUsername } from "@/api/auth";
import { useRequest } from "ahooks";
import { onlineUserAPI } from "../../../api/request";
import { getHeartInterval, removeAll } from "@/api/auth";
import logo from "@/assets/header/logo_en.png";
import { Menu, Space, Avatar, Layout, Button, Row, Col, Image, Dropdown } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import Icon from "@ant-design/icons";
import {
  IconLogo
} from "@/assets";
import Logo from "@/assets/logo.png"
import {useAuth} from "../../../context/auth-provider";
import PermissionControl from "../../../components/PermissionControl";
const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;

function getItem(routeInfo) {
  const {
    key,
    icon,
    children,
    label,
  } = routeInfo
  return {
    key,
    icon,
    children,
    label,
  };
}

function Layouts () {
  const isMobile = IsMobile();
  // const [collapsed, setCollapsed] = useState(isMobile);
  const [currKey, setCurrKey] = useState('');
  const [routers, setRouters] = useState([]);
  const [uName, setUName] = useState("");
  const [searchShow, setSearchShow] = useState('block');
  const [search, setSearch] = useState('');
  const [collapsed, setCollapsed] = useState(false);
  const [menuItems, setMenuItems] = useState([]);
  const auth = useAuth();
  const toggleCollapsed = () => {
    setCollapsed(!collapsed);
  };
  const {
    userInfo
  } = React.useContext(MainContext);

  const { pathname } = useLocation();
  const navigate = useNavigate();
  const newRoutes = localRouters.filter((item) => {
    return item.children !== undefined;
  });
  const permissions = JSON.parse(sessionStorage.getItem('permissions')) || [];
  const passwordRemind = React.useMemo(()=> {
    return JSON.parse(sessionStorage.getItem('PMS-User'))?.passwordRemind;
  }, [userInfo])

  // const radio = React.useMemo(( ) => {
  //   if (window.navigator.userAgent.indexOf("Windows NT")) {
  //     if (window.devicePixelRatio > 1.5) {
  //       return Number(window.devicePixelRatio/ 1.5).toFixed(2);
  //     }
  //   }
  //   return 1;
  // }, [])

  useEffect(() => {
    // if (!auth.user) {
    //   auth.getProfile();
    // }
  }, []);

  const includesAll = (arr1, arr2) => {
    return arr1 && arr2.every(item => arr1.includes(item))
  };
  window.inn = includesAll;
  useEffect(() => {
    const items = [];
    window.routers = routers;
    const mainItems = routers
      .filter((child) => {
        let flag = false;
        if (child.permission && child.permission.length > 0) {
          child.permission.forEach((permission) => {
            if (auth.permissions.includes(permission)) {
              flag = true;
            }
          })
        } else if (child.permission && child.permission.length === 0) {
          flag = true;
        }
        return flag;
      })
      .flatMap((row) => ({
        ...row,
        children: row.children?.filter((child) => {
          let flag = false;
          if (child.permission && child.permission.length > 0) {
            child.permission.forEach((permission) => {
              if (auth.permissions.includes(permission)) {
                flag = true;
              }
            })
          } else if (child.permission && child.permission.length === 0) {
            flag = true;
          }
          return flag;
        })
      }))

    setMenuItems(mainItems)

    // {
    //   routers?.map((item) => {
    //     return (item.element ? item.ignore !== true ? <MenuItem key={item.path} icon={item.icon} title={item.name}><NavLink to={item.path}>{item.name}</NavLink></MenuItem> : null :
    //         <SubMenu key={item.path} title={item.name} icon={item.icon}>
    //           {
    //             item.children?.map((pop) => {
    //               console.log(pop.permission)
    //               return (
    //                 <PermissionControl permissionRequired={pop.permission}>
    //                   <MenuItem key={pop.path} icon={pop.icon} ><NavLink to={pop.path}>{pop.name}</NavLink></MenuItem>
    //                 </PermissionControl>
    //               )
    //             })
    //           }
    //         </SubMenu>
    //     )
    //   })
    // }

  }, [auth?.permissions, routers]);

  useEffect(() => {
    setUName(getUsername())

    let routeList = newRoutes[0].children;
    let permission = [];
    let filterArr = [];
    let mapArr = [];
    permissions.map(pop => {
      permission.push(pop.code)
    });
    function compare (arr1, arr2) {
      return arr1.filter((v) => {
        return arr2.includes(v);
      });
    }
    function treeToArray (tree) {
      return tree.reduce((res, item) => {
        const { children, ...i } = item
        return res.concat(i, children && children.length ? treeToArray(children) : [])
      }, [])
    }
    let routeArr = treeToArray(routeList);
    const mapTree = (route, permission) => {
      route.map(item => {
        if (item.role?.length > 0) {
          let pop = permission.some(val => item.role?.includes(val));
          if (pop === false) {
            (item.name === 'Meeting' || item.name === 'Meeting Som') ? sessionStorage.setItem('PMS-Meet', 'none') : filterArr.push(item);
          } else {
            item.role = compare(permission, item.role);
            if (item.name === 'Meeting' || item.name === 'Meeting Som') {
              if (item.role.toString().includes('MAINTENANCE')) {
                item.role.toString().includes('APPROVE') ? sessionStorage.setItem('PMS-Meet', 'approve') : sessionStorage.setItem('PMS-Meet', 'add')
              } else {
                sessionStorage.setItem('PMS-Meet', 'none');
              }
            } else {
              item.show = item.role.toString().includes('MAINTENANCE') ? true : false
            }
            mapArr.push(item);
          }
        }
      })
    }
    mapTree(routeArr, permission)
    let filterRoute = routeArr.filter(x => !filterArr.some(y => y.name === x.name))
    function reassign (a1, a2) {
      for (let i = 0; i < a1.length; i++) {
        for (let j = 0; j < a2.length; j++) {
          if (a1[i].id == a2[j].id) {
            a1[i] = a2[j];
          }
        }
      }
    }
    reassign(filterRoute, mapArr);
    function arrayToTree (items) {
      const result = [];
      const itemMap = {};
      for (const item of items) {
        const id = item.id;
        const pid = item.pid;

        if (!itemMap[id]) {
          itemMap[id] = {
            children: [],
          }
        }

        itemMap[id] = {
          ...item,
          children: itemMap[id]['children']
        }

        const treeItem = itemMap[id];

        if (pid === 0) {
          result.push(treeItem);
        } else {
          if (!itemMap[pid]) {
            itemMap[pid] = {
              children: [],
            }
          }
          itemMap[pid].children.push(treeItem)
        }

      }
      return result;
    }
    let newRouteList = arrayToTree(filterRoute);
    setRouters(newRouteList);
  }, [])

  useEffect(() => {
    if (pathname) {
      setCurrKey(pathname);
    }
    if (/\/ExamProfile\/((.)*)/.test(pathname)) {
      setCurrKey('/ExamProfile');
    } else if (/\/CertificateManagement\/Valid\/Candidate((.)*)/.test(pathname)) {
      setCurrKey('/CertificateManagement/Valid');
      console.log('@@@@@@function');
    }
    console.log(pathname)
  }, [pathname])
  //Shrink Menu
  const handleCollapsed = () => {
    setCollapsed(!collapsed)
  };
  // click Menu
  const onClickMenuItem = (key, event, keyPath) => {
    // console.log(key, event, keyPath, '29------------')
  }
  const clickGo = (type, value) => {
    switch (type) {
      case 'search':
        setSearch('');
        navigate({
          pathname: '/Search',
          search: `?${createSearchParams({
            filter: value
          })}`
        })
        break;
      case 'logout':
        run('logout');

        break;
      case 'change':
        navigate('/Profile')
        break;
      default:
        break;
    }
  }

  const { run } = useRequest(onlineUserAPI, {
    manual: true,
    onSuccess: (result, params) => {
      switch (params[0]) {
        case 'logout':
          break;
        case 'heartAPI':
          break;
      }
    },
    onError: (error) => {
    },
    onFinally: (params, result, error) => {
      switch (params[0]) {
        case 'logout':
          removeAll();
          sessionStorage.removeItem('PMS-Meet');
          navigate('/');
          break;
        case 'heartAPI':
          break;
      }
    },
  });
  function getItem(label, key, icon, children) {
    return {
      key,
      icon,
      children,
      label,
    };
  }
  const items = [
    getItem('Option 1', '1'),
    getItem('Option 2', '2'),
    getItem('User', 'sub1', null, [
      getItem('Tom', '3'),
      getItem('Bill', '4'),
      getItem('Alex', '5'),
    ]),
    getItem('Team', 'sub2', null, [getItem('Team 1', '6'), getItem('Team 2', '8')]),
    getItem('Files', '9', null),
  ];


  return (
    <div className={styles['layout']}>
      <Layout style={{ height: '100%', overflowY: 'hidden' }}>
        <Header className={styles['layout-header']} >
          <Row span={24} size={16} align={'middle'} justify={'space-between'} style={{ height: '100%' }}>
            <Col span={14}>
              <Row gutter={8} span={24} align={'middle'}>
                <Col><Image src={Logo} width={60} height={60} preview={false}/></Col>
                <Col span={16}><div style={{}}><b>Common Recruitment Examination and Basic Law and National Security Law Test E-Proof System [Restricted]</b></div></Col>
              </Row>
            </Col>
            <Col>
              {/*<Dropdown*/}
              {/*  menu={{ item: []}}*/}
              {/*  placement="bottomLeft"*/}
              {/*  arrow*/}
              {/*>*/}

              {/*</Dropdown>*/}
              <Row gutter={8} align={'middle'} justify={'end'}>
                <Col><Avatar size={44} icon={<UserOutlined />} /></Col>
                <Col style={{fontSize: '11px', color: 'rgb(81, 90, 106)'}}>
                  <div style={{fontSize: '11px', color: '#152B47', paddingBottom: 5}}><b>{auth?.user}</b></div>
                  <Space style={{fontSize: '11px', color: '#152B47', paddingBottom: 5}}>
                    <div>{auth?.post}</div>
                    <Link onClick={() => auth.logOut()}>Logout</Link>
                  </Space>
                  <div style={{fontSize: '11px', color: '#152B47'}}><span>{window._env_.VERSION}</span></div>

                </Col>
              </Row>
            </Col>
          </Row>
        </Header>
        <Layout>
          <Sider

            width={260}
            breakpoint='xl'
            collapsible
            collapsed={collapsed}
            onCollapse={toggleCollapsed}
            collapsedWidth={50}
            // trigger={null}
            className={styles['layout-menu']}
          >
            <Menu
              defaultOpenKeys={[
                '/CertificateManagement',
                '/Workflow',
              ]}
              defaultSelectedKeys={['/Home']}
              selectedKeys={[currKey]}
              style={{
                width: '100%',
                // height: `calc(100vh - 70px - 48px)`
              }}
              mode={'inline'}
            >
              {
                menuItems?.map((item) => {
                  return (item.element ? item.ignore !== true ? <MenuItem key={item.path} icon={item.icon} title={item.name}><NavLink to={item.path}>{item.name}</NavLink></MenuItem> : null :
                      <SubMenu key={item.path} title={item.name} icon={item.icon}>
                        {
                          item.children?.map((pop) => {
                            return (
                              <MenuItem key={pop.path} icon={pop.icon} ><NavLink to={pop.path}>{pop.name}</NavLink></MenuItem>

                            )
                          })
                        }
                      </SubMenu>
                  )
                })
              }
            </Menu>
          </Sider>
          <Layout
            id={'layout-content'}
            style={{
              height: `calc(${100}vh - 70px)`
            }}
            className={styles['layout-container']}
          >

            <Content className={styles['layout-content']}>
              <Outlet />
            </Content>
          </Layout>
        </Layout>
      </Layout>
    </div >
  );
}

export default Layouts;
