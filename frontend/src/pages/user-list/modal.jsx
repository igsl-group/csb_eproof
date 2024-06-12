import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import Email from "@/components/Email";
import {useRequest} from "ahooks";
import { userRoleAPI } from '@/api/request';
import {TYPE } from '@/config/enum';
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";



const UserModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const type = props.type;
  const open = props.open;
  const recordId = props.recordId;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [form] = Form.useForm();
  const [roleList, setRoleList] = useState([]);

  const statusOptions = useMemo(() => [
    {
      label: 'DISABLED',
      value: 'DISABLED',
    },
    {
      label: 'ACTIVE',
      value: 'ACTIVE',
    }
  ])

  const onClose = useCallback(() => {
    if (typeof onCloseCallback === "function") {
      onCloseCallback();
    }
  }, [onCloseCallback]);

  const onFinish = useCallback(() => {
    if (typeof onFinishCallback === "function") {
      onFinishCallback();
    }
  }, [onFinishCallback]);

  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => ({
        ...values,
        roles: values.roles.flatMap((value) => ({ id: value })),
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      switch (type) {
        case TYPE.EDIT:
          const id = values.id;
          delete values.id;
          runUserRoleAPI('userUpdate', id, values)
            .then(() => onFinish());
          break;
        case TYPE.CREATE:
          runUserRoleAPI('userCreate', values)
            .then(() => onFinish());
          break;
      }
    }
  }, [type]);

  const { runAsync: runUserRoleAPI } = useRequest(userRoleAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'userGet':
          const data = response.data;
          form.setFieldsValue(data)
          break;
        case 'userUpdate':
          messageApi.success('Update successfully');
          break;
        case 'roleList':
        {
          const data = response.data || {};
          const content = data.content || [];
          setRoleList(content.flatMap((row) => ({
            label: row.description,
            value: row.id,
          })));
          break;
        }
        default:
          break;
      }

    },
    onError: (error) => {
      //Message.error('');
    },
    onFinally: (params, result, error) => {
    },
  });

  useEffect(() => {
    (async() => {
      if (open) {
        form.resetFields();
        switch (type) {
          case TYPE.EDIT:
            await runUserRoleAPI('userGet', recordId)
            break;
        }
        await runUserRoleAPI('roleList');
      }
    })()
  }, [open, type]);

  return (
    <Modal
      width={600}
      style={{ top: 20 }}
      title={`${type} User`}
      okText={'Save'}
      closable={false}
      maskClosable={false}
      onCancel={onClose}
      onOk={onSave}
      {...props}
    >
      <Form
        layout="vertical"
        autoComplete="off"
        form={form}
        colon={false}
        scrollToFirstError={{
          behavior: 'smooth',
          block: 'center',
          inline: 'center',
        }}
        name="form"
      >
        <Text name={"id"} size={50} hidden/>
        <Row gutter={24} justify={'center'}>
          <Col span={24}>
            <Text name={"dpUserId"} label={'DP User ID'} required size={50}/>
          </Col>
          <Col span={24}>
            <Text name={'name'} label={'Name'} required size={50}/>
          </Col>
          <Col span={24}>
            <Text name={'post'} label={'Post'} required size={50}/>
          </Col>
          <Col span={24}>
            <Email name={'email'} label={'Email'} required size={50}/>
          </Col>
          <Col span={24}>
            <Dropdown name={'roles'} label={'Role'} options={roleList} mode={"multiple"} size={50}/>
          </Col>
          <Col span={24}>
            <Dropdown name={'status'} label={'Status'} options={statusOptions} required size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default UserModal;