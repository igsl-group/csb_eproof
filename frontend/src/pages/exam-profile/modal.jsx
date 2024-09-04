import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {useRequest} from "ahooks";
import { examProfileAPI } from '@/api/request';
import {TYPE} from '@/config/enum';
import dayjs from "dayjs";

const ExamProfileFormModal = (props) =>  {

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
        effectiveDate: dayjs(values.effectiveDate).format('YYYY-MM-DD'),
        examDate: dayjs(values.examDate).format('YYYY-MM-DD'),
        plannedEmailIssuanceDate: dayjs(values.plannedEmailIssuanceDate).format('YYYY-MM-DD'),
        resultLetterDate: dayjs(values.resultLetterDate).format('YYYY-MM-DD'),
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      switch (type) {
        case TYPE.EDIT:
          const serialNo = values.serialNo;
          delete values.serialNo;
          runExamProfileAPI('examProfileUpdate', serialNo, values)
            .then(() => onFinish());
          break;
      }
    }
  }, [type]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'examProfileGet':

          let data = response.data;

          data.effectiveDate = dayjs(data.effectiveDate, 'YYYY-MM-DD');
          data.examDate = dayjs(data.examDate, 'YYYY-MM-DD');
          data.plannedEmailIssuanceDate = dayjs(data.plannedEmailIssuanceDate, 'YYYY-MM-DD');
          data.resultLetterDate = dayjs(data.resultLetterDate, 'YYYY-MM-DD');
          form.setFieldsValue(data);
          break;
        case 'examProfileUpdate':
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

  useEffect(() => {
    (async() => {
      if (open) {
        switch (type) {
          case TYPE.EDIT:
            await runExamProfileAPI('examProfileGet', recordId)
            break;
        }
      }
    })()
  }, [open, type, recordId]);
  return (
    <Modal
      width={600}
      style={{ top: 20 }}
      title={`Edit Exam Profile`}
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
        <Text name={"serialNo"} size={50} hidden/>
        <Row gutter={24} justify={'start'}>
          <Col span={24} md={12}>
            <Text name={"serialNo"} label={'Serial No.'} required size={50} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Date name={'examDate'} label={'Exam Date'} required placeholder={'YYYY-MM-DD'} required size={50}/>
          </Col>
          <Col span={24} md={12}>
            <Date
              name={'plannedEmailIssuanceDate'}
              required
              label={'Planned Email Issuance Date'}
              placeholder={'YYYY-MM-DD'}
              size={50}
              dependencies={['effectiveDate']}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const effectiveDate = getFieldValue('effectiveDate');
                    const isSameOrAfter = value.isSame(effectiveDate, 'day') || value.isAfter(effectiveDate, 'day');
                    if (!value || isSameOrAfter) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('The "Planned Email Issuance Date" should be no earlier than the "Effective Date".'));
                  },
                }),
              ]}
            />
          </Col>
          <Col span={24} md={12}>
            <Date
              name={'effectiveDate'}
              label={'Effective Date'}
              required
              placeholder={'YYYY-MM-DD'}
              size={50}
              dependencies={['examDate']}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const examDate = getFieldValue('examDate');
                    const isSameOrAfter = value.isSame(examDate, 'day') || value.isAfter(examDate, 'day');
                    if (!value || isSameOrAfter) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('The "Result Letter Date" should be no earlier than the "Exam Date".'));
                  },
                }),
              ]}
            />
          </Col>
          <Col span={24} md={12}>
            <Date
              name={'resultLetterDate'}
              required label={'Result Letter Date'}
              placeholder={'YYYY-MM-DD'}
              size={50}
              dependencies={['examDate']}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const examDate = getFieldValue('examDate');
                    const isAfter = value.isAfter(examDate);
                    if (!value || isAfter) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('The "Result Letter Date" should be no earlier than the "Exam Date".'));
                  },
                }),
              ]}
            />          </Col>
          <Col span={24}>
            <Text name={'location'} label={'Location'} size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default ExamProfileFormModal;