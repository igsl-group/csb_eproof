import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import Textarea from "@/components/Textarea";
import {validators} from "../../utils/validators";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {useRequest} from "ahooks";
import {examProfileAPI, generalAPI} from "../../api/request";
import Richtext from "../../components/Richtext";
import dayjs from "dayjs";

const EmailModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const informCandidateValue = Form.useWatch('informCandidate', form);
  const [file, setFile] = useState(null);
  const [openImportModal, setOpenImportModal] = useState(null);
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const [dataMsg, setDataMsg] = useState([]);
  const open = props.open;
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
      // runExamProfileAPI('sendEmail', record.id, values)
      //   .then(() => onFinish());
      onFinish()
    }
  }, []);

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        ...record,
        examDate: record?.certInfo?.examProfile?.examDate,
      })
      runGeneralAPI('emailTemplateGet', 4);
    }
  }, [open, record]);

  const onConfirm = useCallback(() => {
    form.validateFields().then(() => {
      modalApi.confirm({
        title:`Are you sure to resend the email notification?`,
        width: 500,
        okText: 'Confirm',
        onOk: () => onSave()
      });
    });
  }, [])


  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: async (response, params) => {
      switch (params[0]) {
        case 'certSingleUpdateResult':
          messageApi.success('Request successfully.');
          break;
        case 'certLatestCandidateInfo':
          const data = response.data || {};
          const content = data.content || [];
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
          let body = data.body;
          body = body.replaceAll('{{application_name}}', record.newName);
          body = body.replaceAll('{{examination_date}}', dayjs(record?.certInfo?.examProfile?.examDate, 'YYYY-MM-DD').format('DD MMM YYYY'));
          body = body.replaceAll('{{eproof_document_url}}', record.certEproof?.url);
          form.setFieldsValue({
            ...data,
            body,
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

  console.log(record);

  return (
    <Modal
      width={1000}
      okText={'Send'}
      closable={false}
      maskClosable={false}
      onOk={onConfirm}
      onCancel={onClose}
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
            <Text
              name={"newName"}
              label={'Name'}
              size={100}
              disabled
            />
          </Col>
          <Col span={12}>
            <Text
              name={"examDate"}
              label={'Exam Date'}
              size={100}
              disabled
            />
          </Col>
          <Col span={12}>
            <Text
              name={"newHkid"}
              label={'HKID'}
              size={100}
              disabled
            />
          </Col>
          <Col span={12}>
            <Text
              name={"newPassport"}
              label={'Passport'}
              size={100}
              disabled
            />
          </Col>
          <Col span={24}>
            <Text name={'subject'} label={'Subject'} size={100} required/>
          </Col>
          <Col span={24}>
            <Richtext name={'body'} label={'Body'} size={100} required/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default EmailModal;