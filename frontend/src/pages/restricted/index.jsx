import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Grid,
  Space,
  Tabs,
  Col,
  Row,
  Descriptions,
  Modal,
  Pagination,
  Button, Alert
} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  ProfileOutlined,
  SettingOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  FileDoneOutlined,
  ScheduleOutlined,
  AreaChartOutlined, SearchOutlined,
} from '@ant-design/icons';
import {
  toQueryString
} from "@/utils/util";
import Text from "@/components/Text";
import Dropdown from "@/components/Dropdown";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useAuth} from "../../context/auth-provider";
import {
  userRoleAPI
} from "@/api/request";

const Home = () =>  {
  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Restricted',
    },
  ], []);

  return (
    <div className={styles['login']}>
      <Typography.Title level={3}>Restricted</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br />
      <Alert type={'warning'} description={'Remember to log out from your account once you have completed your tasks to maintain the confidentiality and integrity of our data. Thank you for understanding and adhering to the guidelines.'} showIcon/>
    </div>

  )
}

export default Home;