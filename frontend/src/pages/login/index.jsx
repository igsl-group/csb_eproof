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
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useAuth} from "../../context/auth-provider";

const Login = () =>  {
  const auth = useAuth();
  const navigate = useNavigate();
  const modalApi = useModal();
  const [open, setOpen] = useState(false);
  const [freezeExamProfile, setFreezeExamProfile] = useState(false);
  const [form] = Form.useForm();
  const {
    serialNo,
  } = useParams();

  const onFinish = async (values) => {
    console.log('Success:', values);
    await auth.loginAction(values);
    navigate('/ExamProfile');
  };
  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo);
  };

  return (
    <div className={styles['login']}>
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
                uid:'admin_test',
                dpDeptId:'csb',
              }}
            >
              <Text name={'uid'} label={'DP User Id'} size={50}/>
              <Text name={'dpDeptId'} label={'DP Dept Id'} size={50}/>
              <Button type={'primary'} htmlType={'submit'}>Login</Button>
            </Form>
          </Card>
        </Col>
      </Row>
    </div>

  )
}

export default Login;