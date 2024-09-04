import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID, { stringToHKID } from "@/components/HKID";
import Textarea from "@/components/Textarea";
import Checkbox from "@/components/Checkbox";
import {validators} from "../../utils/validators";
import Richtext from "../../components/Richtext";
import {createSearchParams, useNavigate, Link, useParams, useSearchParams} from "react-router-dom";
import {useRequest} from "ahooks";
import { examProfileAPI } from '@/api/request';
import {
  toQueryString
} from "@/utils/util";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {HKIDToString} from "../../components/HKID";

const HkidPassportModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [searchParams, setSearchParams] = useSearchParams();
  const [form] = Form.useForm();
  const hkid = searchParams.get("hkid") || '';
  const passport = searchParams.get("passport") || '';
  const record = props.record;
  const open = props.open;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;

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

  const onConfirm = useCallback(() => {
    form.validateFields().then((values) => {
      modalApi.confirm({
        title:`Are you sure to update candidate's HKID/Passport?`,
        width: 500,
        okText: 'Confirm',
        onOk: () => onSave()
      });
    })
  }, [])

  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => ({
        ...values,
        newHkid: HKIDToString(values.newHkid),
        currentHkid: HKIDToString(values.currentHkid),
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      delete values.currentName;
      delete values.examDate;
      runExamProfileAPI('certBatchUpdatePersonalParticular', values)
        .then(() => onFinish());

    }
  }, []);


  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'certBatchUpdatePersonalParticular':
          messageApi.success('Request successfully.');
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
    if (open) {
      form.setFieldsValue({
        currentName: record.name,
        newName: record.name,
        examDate: record.examDate,
        currentHkid: stringToHKID(record.hkid),
        currentPassport: record.passportNo,
      })
    }
  }, [open, record]);

  return (
    <Modal
      width={1000}
      okText={'Submit for renew (All)'}
      closable={false}
      maskClosable={false}
      onOk={onConfirm}
      onCancel={onClose}
      style={{ top: 20 }}
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
        <Row gutter={24} justify={'center'}>
          <Col span={24} md={12}>
            <Text name={'examDate'} label={'Exam Date'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'currentName'} label={'Last Candidate Name'} size={100} disabled/>
            <Text name={'newName'} label={'New Candidate Name'} size={100} disabled hidden/>
          </Col>
          <Col span={24} md={12}>
            <HKID name={'currentHkid'} label={'Current HKID'} size={100} disabled />
          </Col>
          <Col span={24} md={12}>
            <HKID name={'newHkid'} label={'New HKID'} size={100}/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'currentPassport'} label={'Current Passport'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newPassport'} label={'New Passport'} size={100}/>
          </Col>
          <Col span={24}>
            <Textarea name={'remark'} label={'Remark'} size={100}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default HkidPassportModal;