import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";
import Email from "@/components/Email";
import {useRequest} from "ahooks";
import { userRoleAPI } from '@/api/request';
import {TYPE } from '@/config/enum';
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {
  toQueryString
} from "@/utils/util";
import {examProfileAPI} from "../../api/request";
import {stringToHKID} from "../../components/HKID";

const VoidModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const type = props.type;
  const open = props.open;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [form] = Form.useForm();
  const record = props.record;

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        ...record,
        hkid: stringToHKID(record.hkid),
      })
    }
  }, [open, record]);

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
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      if ([false].includes(record.valid)) {
        runExamProfileAPI('historicalResultValid', record.id, {
          remark: values.remark
        })
          .then(() => onFinish());
      } else {
        runExamProfileAPI('historicalResultInvalid', record.id, {
          remark: values.remark
        })
          .then(() => onFinish());
      }

    }
  }, [record]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'historicalResultValid':
          messageApi.success('Update successfully.');
          break;
        case 'historicalResultInvalid':
          messageApi.success('Update successfully.');
          break;
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

  return (
    <Modal
      width={600}
      style={{ top: 20 }}
      title={`${[false].includes(record.valid) ? 'Un-void' : 'Void'} Whole Result`}
      okText={`${[false].includes(record.valid) ? 'Un-void' : 'Void'}`}
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
        initialValues={{
          status: 'ACTIVE'
        }}
      >
        <Text name={"id"} size={50} hidden/>
        <Row gutter={24} justify={'center'}>
          <Col span={12}>
            <HKID name={"hkid"} label={'HKID'} size={50} disabled/>
          </Col>
          <Col span={12}>
            <Text name={'passport'} label={'Passport'} size={50} disabled/>
          </Col>
          <Col span={24}>
            <Text name={'name'} label={'Name'} size={50} disabled/>
          </Col>
          <Col span={24}>
            <Textarea name={'remark'} label={'Remark'} size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default VoidModal;