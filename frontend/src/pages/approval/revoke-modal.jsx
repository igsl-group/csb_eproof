import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Alert} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";
import Email from "@/components/Email";
import Checkbox from "@/components/Checkbox";
import {validators} from "../../utils/validators";
import Richtext from "../../components/Richtext";
import ResizeableTable from "@/components/ResizeableTable";
import {useRequest} from "ahooks";
import { examProfileAPI } from '@/api/request';
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";

const RevokeModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const informCandidateValue = Form.useWatch('informCandidate', form);
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const record = props.record;
  const open = props.open;
  const data = record.certInfos;

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
  }, [onFinishCallback])



  const onSave = useCallback(async () => {
    form.validateFields()
      .then(() => onFinish())
      .then(() => form.resetFields())
      .catch((e) => console.error(e))
  }, []);

  useEffect(() => {
    form.setFieldsValue({
      email: 'wilfred.lai@igsl-group.com',
      informCandidateEmail: `Hi Wilfred,<br/><br/>Your certificate was revoked. `,
    })
  }, [])

 useEffect(() => {
    if (open) {
      form.setFieldsValue({
        // type: "REVOKE",
        ...record,
        // emailTarget: lastCandidateInfo.email,
        // emailContent: `Hi Wilfred,<br/><br/>Your certificate was revoked. `,
        // certInfoIdList: data.flatMap(row => row.id).join(","),
      })
    }
  }, [record, open])

  const columns = useMemo(() => [
    {
      title: 'Exam Date',
      key: 'examDate',
      dataIndex: 'examDate',
      width: 140,
      sorter: true,
    },
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      width: 100,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passportNo',
      dataIndex: 'passportNo',
      width: 100,
      sorter: true,
    },
    {
      title: 'Name',
      key: 'name',
      dataIndex: 'name',
      width: 160,
      sorter: true,
    },
    {
      title: 'Email',
      key: 'email',
      dataIndex: 'email',
      width: 180,
      sorter: true,
    },
    {
      title: 'Result Letter Date',
      key: 'resultLetterDate',
      render: (row) => row.examProfile?.resultLetterDate,
      width: 180,
      // sorter: true,
    },
    {
      title: 'UE',
      key: 'ueGrade',
      dataIndex: 'ueGrade',
      width: 80,
    },
    {
      title: 'UC',
      key: 'ucGrade',
      dataIndex: 'ucGrade',
      width: 80,
    },
    {
      title: 'AT',
      key: 'atGrade',
      dataIndex: 'atGrade',
      width: 80,
    },
    {
      title: 'BLNST',
      key: 'blnstGrade',
      dataIndex: 'blnstGrade',
      width: 80,
    },
  ], []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'requestCertRevoke':
          messageApi.success('Request successfully.');
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
      width={1000}
      footer={[
        <Button key="back" onClick={() => onClose()}>
          Cancel
        </Button>,
        <Button type="primary" danger onClick={() => {}}>
          Reject
        </Button>,
        <Button
          key="submit"
          type="primary"
          onClick={() => onSave()}
        >

          Approve
        </Button>,
      ]}
      closable={false}
      onOk={onSave}
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
        <ResizeableTable
          size={'big'}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={data}
        />
        <br/>
        <Row gutter={24} justify={'center'}>
          <Col span={24}>
            <Textarea name={'remark'} label={'Remark'} size={100}/>
          </Col>
          <Col span={24}>
            <Email
              name={'emailTarget'}
              label={'Latest Candidate\'s email address'}
              required
              disabled
              size={100}
            />
          </Col>
          <Col span={24}>
            <Richtext name={'emailContent'} label={'Inform candidate’s email content after request is approved'} size={100}/>
          </Col>
        </Row>
        <br />
        {/*<Alert type={'warning'} message={'Cert. renew required after submit.'} showIcon/>*/}
      </Form>
    </Modal>
  )
}

export default RevokeModal;