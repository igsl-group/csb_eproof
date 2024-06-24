import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {useRequest} from "ahooks";
import { TYPE } from '@/config/enum';
import { userRoleAPI } from '@/api/request';

const RoleModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const type = props.type;
  const open = props.open;
  const recordId = props.recordId;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [permissionList, setPermissionList] = useState([]);
  const [form] = Form.useForm();

  const onClose = useCallback(() => {
    if (typeof onCloseCallback === "function") {
      form.resetFields();
      onCloseCallback();
    }
  }, [onCloseCallback]);

  const onFinish = useCallback(() => {
    if (typeof onFinishCallback === "function") {
      form.resetFields();
      onFinishCallback();
    }
  }, [onFinishCallback]);

  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => ({
        ...values,
        permissions: values.permissions.flatMap((value) => ({ id: value })),
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
          runUserRoleAPI('roleUpdate', id, values)
            .then(() => onFinish());
          break;
        case TYPE.CREATE:
          runUserRoleAPI('roleCreate', values)
            .then(() => onFinish());
          break;
      }
    }
  }, [type]);

  const { runAsync: runUserRoleAPI } = useRequest(userRoleAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'roleGet':
        {
          let data = {
            ...response.data,
            permissions: response.data.permissions.flatMap((row) => row.id),
          };
          form.setFieldsValue(data)
          break;
        }
        case 'roleUpdate':
        {
          messageApi.success('Update successfully.');
          break;
        }
        case 'roleCreate':
        {
          messageApi.success('Create successfully.');
          break;
        }
        case 'permissionList':
        {
          const data = response.data || [];
          const options = data.flatMap((row) => ({
            value: row.id,
            label: row.description,
          }))
          setPermissionList(options);
          // messageApi.success('Create successfully.');
          break;
        }
        default:
          break;
      }

    },
    onError: (error) => {
      const message = error.data?.properties?.message || '';
      messageApi.error(message);
    },
    onFinally: (params, result, error) => {
    },
  });

  useEffect(() => {
     (async() => {
      if (open) {
        switch (type) {
          case TYPE.EDIT:
            await runUserRoleAPI('roleGet', recordId)
            break;
        }
        await runUserRoleAPI('permissionList');
      }
    })()
  }, [open, type, recordId]);

  return (
    <Modal
      width={600}
      style={{ top: 20 }}
      title={`${type} Role`}
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
            <Text name={"name"} label={'Name'} required size={50}/>
          </Col>
          <Col span={24}>
            <Text name={"description"} label={'Description'} required size={50}/>
          </Col>
          <Col span={24}>
            <Dropdown name={'permissions'} label={'Permission'} mode={"multiple"} options={permissionList} required size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default RoleModal;