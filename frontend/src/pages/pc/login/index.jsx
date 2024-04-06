import React, {useRef, useCallback, useEffect, useState, useMemo} from 'react';
// import styles from './style/index.module.less';
import {
  loginAPI
} from '@/api/request';
import { useRequest } from 'ahooks';
import PassPasswordRules from "@/hook/passPasswordRules";
import {useNavigate, Link} from "react-router-dom";
import { sectionOptions} from "@/config/config";
import Dropdown from "@/components/Dropdown";
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Input,
  Button,
  Space,
  Tag,
  Popconfirm,
  Row,
  Col,
  Pagination,
  Table,
  Modal
} from 'antd';
import {useAuth} from "../../../context/auth-provider";
import {setToken} from "../../../utils/storage";

export default function Login() {


  const [form] = Form.useForm();
  const auth = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [postOptions, setPostOptions] = useState([]);

  const { runAsync: runLoginAPI } = useRequest(loginAPI, {
    manual: true,
    onSuccess: (response, params) => {
    },
    onError: (response, params) => {
    },
    onFinally: (params, result, error) => {
    },
  });

  const loginAction = async (name = '') => {
    return runLoginAPI('authenticate', '');
  };

  useEffect(() => {
    (async () => {
      await loginAction()
        .then(async () => {
          await auth.getProfile();
          navigate(`/Case/All`);

        })
        .catch((response) => {
          switch (response.status) {
            case 300:
              const data = response.data?.result?.data || [];
              setPostOptions(data.flatMap((row) => ({
                value: row.id,
                label: row.name,
              })));
              setOpen(true);
              break;
            default:
              navigate(`/Unauthorized`)

          }
        });
    })();
  }, []);

  const onConfirmClicked = useCallback(async () => {
    const values = await form
      .validateFields()
      .then((values) => values)
      .catch((e) => false);

    if (values.post) {
      setOpen(false);
      const post = values.post;
      await auth.changePostAction(post)
        .catch(() => navigate(`/Unauthorized`));

      await auth.getProfile()
        .then(() => navigate(`/Case/All`))
        .catch(() => navigate(`/Unauthorized`))
    }
  }, []);

  return (
    <div>
      <Modal
        title={'Select Post'}
        open={open}
        okText={'Confirm'}
        closable={false}
        maskClosable={false}
        footer={(<Button type={'primary'} onClick={onConfirmClicked}>Confirm</Button>)}
      >
        <Form
          layout="vertical"
          autoComplete="off"
          form={form}
          name="form"
        >
          <Dropdown name={'post'} label={'Post'} options={postOptions} required/>
        </Form>
      </Modal>
    </div>
  )
}
