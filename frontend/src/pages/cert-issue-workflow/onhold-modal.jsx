import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import Textarea from "@/components/Textarea";
import {useRequest} from "ahooks";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {examProfileAPI} from "../../api/request";

const OnHoldModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const isOnHold = props.isOnHold;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [form] = Form.useForm();
  const record = props.record;

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
      const certInfoId = values.id;
      delete values.id;
      delete values.name;
      delete values.email;
      delete values.hkid;
      delete values.passportNo;

      if (isOnHold) {
        runExamProfileAPI('certIssuanceHold', certInfoId, values)
          .then(() => onFinish());
      } else {
        runExamProfileAPI('certIssuanceResume', certInfoId, values)
          .then(() => onFinish());
      }

    }
  }, [isOnHold]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'certIssuanceHold':
          messageApi.success('On hold case successfully.');
          break;
          case 'certIssuanceResume':
          messageApi.success('Resume case successfully.');
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


  useEffect(() => {
    const record = props.record || {};
    if (typeof record.hkid === 'string') {
      record.hkid = {
        id: record.hkid.substring(0,  record.hkid.length - 1),
        checkDigit: record.hkid.charAt(record.hkid.length - 1)
      }
    }

    form.setFieldsValue(record);
  }, [record]);

  return (
    <Modal
      width={800}
      title={`${isOnHold ? 'On-hold' : 'Resume' } Case`}
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
        <Text name={"id"} label={'id'} size={50} disabled hidden/>
        <Row gutter={24} justify={'center'}>
          <Col span={12}>
            <Text name={"name"} label={'Name'} size={50} disabled/>
          </Col>
          <Col span={12}>
            <Email name={"email"} label={'Email'} disabled/>
          </Col>
          <Col span={12}>
            <Text name={"hkid"} disabled/>
          </Col>
          <Col span={12}>
            <Text name={"passportNo"} label={'Passport'} disabled/>
          </Col>
          <Col span={24}>
            <Textarea name={"remark"} label={'Remark'} size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default OnHoldModal;