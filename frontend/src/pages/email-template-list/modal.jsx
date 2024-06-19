import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import Textarea from "@/components/Textarea";
import {validators} from "../../utils/validators";
import Richtext from "../../components/Richtext";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {useRequest} from "ahooks";
import {TYPE } from '@/config/enum';
import {generalAPI} from "../../api/request";

const EmailModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const type = props.type;
  const open = props.open;
  const recordId = props.recordId;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [form] = Form.useForm();
  const [roleList, setRoleList] = useState([]);

  // const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, [props.onCloseCallback ]);
  // const onFinish = useMemo(() => typeof props.onCloseCallback === "function" ? props.onFinishCallback : () => {}, [props.onFinishCallback ]);
  // const onSave = useCallback(async () => {
  //   form.validateFields()
  //     .then((value) => {
  //       console.log(value)
  //       // onFinish()
  //     })
  //     // .then(() => form.resetFields())
  //     .catch((e) => console.error(e))
  // }, []);




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
        includeEmails: values.includeEmails.join(','),
        // roles: values.roles.flatMap((value) => ({ id: value })),
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
          runGeneralAPI('emailTemplateUpdate', id, values)
            .then(() => onFinish());
          break;
        // case TYPE.CREATE:
        //   runGeneralAPI('userCreate', values)
        //     .then(() => onFinish());
        //   break;
      }
    }
  }, [type]);

  const { runAsync: runGeneralAPI } = useRequest(generalAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'emailTemplateGet':
          let data = {
            ...response.data,
            includeEmails: response.data.includeEmails.split(',')
          };
          form.setFieldsValue(data)
          break;
        case 'emailTemplateUpdate':
          messageApi.success('Update successfully.');
          break;
        // case 'userCreate':
        //   messageApi.success('Create successfully.');
        //   break;
        // case 'roleList':
        // {
        //   const data = response.data || {};
        //   const content = data.content || [];
        //   setRoleList(content.flatMap((row) => ({
        //     label: row.description,
        //     value: row.id,
        //   })));
        //   break;
        // }
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
        form.resetFields();
        switch (type) {
          case TYPE.EDIT:
            await runGeneralAPI('emailTemplateGet', recordId)
            break;
        }
        // await runGeneralAPI('roleList');
      }
    })()
  }, [open, type, recordId]);

  return (
    <Modal
      width={1000}
      style={{ top: 20 }}
      title={`${type} Email Template`}
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
        initialValues={{
          includeEmails: []
        }}
        name="form"
      >
        <Text name={"id"} size={50} hidden/>
        <Row gutter={24} justify={'start'}>
          <Col span={24} md={12}>
            <Dropdown
              name={"type"}
              label={'Type'}
              disabled
              required
              size={100}
            />
          </Col>
          <Col span={24} md={12}>
            <Text
              name={"emailKey"}
              label={'Key'}
              disabled
              required
              size={100}
            />
          </Col>
          <Col span={24}>
            <Dropdown
              name={'includeEmails'}
              label={'Include Email Address'}
              size={100}
              mode="tags"
              tokenSeparators={[',']}
              validation={[validators.emailValidator()]}
            />
          </Col>
          <Col span={24}>
            <Text name={'subject'} label={'Subject'} size={100} required/>
          </Col>
          <Col span={24}>
            <Richtext name={'body'} label={'Body'} size={100} row={10} required/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default EmailModal;