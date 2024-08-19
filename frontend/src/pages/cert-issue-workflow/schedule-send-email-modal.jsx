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
import {examProfileAPI, generalAPI} from "../../api/request";
import Richtext from "../../components/Richtext";

const ScheduleSendEmailModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const serialNoValue = props.serialNoValue;
  const open = props.open;
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
      delete values.plannedEmailIssuanceDate;
      delete values.subject;
      delete values.body;
      delete values.examDate;

      runExamProfileAPI('certScheduleSendEmail', serialNoValue, values)
        .then(() => onFinish());

    }
  }, [serialNoValue]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'certScheduleSendEmail':
          messageApi.success('Schedule to send email successfully.');
          break;
        case 'examProfileGet':
          const data = response.data || {};
          console.log(data)
          form.setFieldsValue({
            ...data,
          });
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

  const { runAsync: runGeneralAPI } = useRequest(generalAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'emailTemplateGet':
          const data = response.data || {};
          form.setFieldsValue({
            ...data,
          });
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
    if (serialNoValue && open) {
      runExamProfileAPI('examProfileGet', serialNoValue);
      runGeneralAPI('emailTemplateGet', 4);
    }
  }, [serialNoValue, open])

  return (
    <Modal
      width={800}
      title={'Schedule to Send Email'}
      okText={'Confirm'}
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
        <Row gutter={24}>
          <Col span={12}>
            <Text name={"plannedEmailIssuanceDate"} label={'Planned Email Issuance Date'} size={50} disabled/>
            <Text name={"examDate"} label={'Exam Date'} size={50} disabled hidden/>
            <Text name={"resultLetterDate"} label={'Result Letter Date'} size={50} disabled/>
          </Col>
          <Col span={12}>
            <Date
              required
              name={"scheduledTime"}
              label={'Scheduled Date'}
              dependencies={['examDate']}
              validation={[
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    const examDate = getFieldValue('examDate');
                    const resultLetterDate = getFieldValue('resultLetterDate');
                    const isAfterExamDate = value.isAfter(examDate);
                    const isAfterResultLetterDate = value.isAfter(resultLetterDate);
                    if (value && !isAfterExamDate) {
                      return Promise.reject(new Error('The "Scheduled Date" should be after the "Exam Date"'));
                    } else if (value && !isAfterResultLetterDate) {
                      return Promise.reject(new Error('The "Scheduled Date" should be after the "Result Letter Date"'));
                    }
                    return Promise.resolve();
                    },
                }),
              ]}
            />
          </Col>
          <Col span={24}>
            <Text
              name={"subject"}
              required
              label={"Email's Subject"}
              disabled
              size={50}
            />
          </Col>
          <Col span={24}>
            <Richtext
              name={"body"}
              required
              label={"Email's Body"}
              size={50}
              disabled={true}
            />
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default ScheduleSendEmailModal;