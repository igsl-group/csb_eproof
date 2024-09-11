import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams, useSearchParams} from "react-router-dom";
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
  Button
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

const Login = () =>  {
  const auth = useAuth();
  const navigate = useNavigate();
  const modalApi = useModal();
  const [open, setOpen] = useState(false);
  const [freezeExamProfile, setFreezeExamProfile] = useState(false);
  const [options, setOptions] = useState([]);
  const [form] = Form.useForm();
  const [searchParams, setSearchParams] = useSearchParams();
  const token = searchParams.get("token");

  useEffect(()=>{
    if (token) {
      auth.setToken(token);
      auth.getProfile()
        .then(() => navigate('/Restricted'));
    }
  },[token]);

  const onFinish = async (values) => {
    console.log('Success:', values);
    await auth.loginAction(values);
    await auth.getProfile();
    navigate('/Restricted');
  };
  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  useEffect(() => {
    const defaultPaginationInfo = {
      sizeOptions: [10, 20, 40],
      pageSize: 999,
      page: 1,
      sortBy: 'id',
      orderBy: 'descend',
    };

    const pagination = {
      total: 0,
      page: defaultPaginationInfo.page,
      pageSize: defaultPaginationInfo.pageSize,
      sortBy: defaultPaginationInfo.sortBy,
      orderBy: defaultPaginationInfo.orderBy,
    };

    userRoleAPI('userList', toQueryString(pagination, {}))
      .then((response) => response.data)
      .then((data) => data.content.flatMap((row) => ({
        label: row.post,
        value: row.dpUserId,
      })))
      .then((list) => setOptions(list))
  }, []);

  return (
    <div className={styles['login']}>
      {
        !token ? (
          <Row style={{ marginTop: 50}} gutter={[16, 16]} justify="center" align="middle">
            <Col span={12}>
              <Card>
                <Form
                  layout="vertical"
                  form={form}
                  colon={false}
                  onFinish={onFinish}
                  onFinishFailed={onFinishFailed}
                  autoComplete="off"
                  initialValues={{
                    tmpUid:'system.administrator',
                    tmpDpDeptId:'csb',
                  }}
                >
                  <Dropdown name={'tmpUid'} label={'DP User Id (User)'} options={options} size={50}/>
                  <Text name={'tmpDpDeptId'} label={'DP Dept Id'} disabled={true} size={50}/>
                  <Button type={'primary'} htmlType={'submit'}>Login</Button>
                </Form>
              </Card>
            </Col>
          </Row>
        ) : null
      }
    </div>

  )
}

export default Login;