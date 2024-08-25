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
import {useAuth} from "../../context/auth-provider";
import PermissionControl from "../../components/PermissionControl";
import _ from "lodash";
import dayjs from "dayjs";

const RevokeModal = (props) =>  {

  const auth = useAuth();
  const modalApi = useModal();
  const messageApi = useMessage();
  const [form] = Form.useForm();
  const informCandidateValue = Form.useWatch('informCandidate', form);
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const record = props.record;
  const open = props.open;
  const data = record.certInfos;
  const disabled = useMemo(() => {
    if (auth.permissions.includes("Revoke_Submit") && record.status === "REJECTED") {
      return false;
    } else if (auth.permissions.includes("Revoke_Approve") && record.status === "PENDING") {
      return false;
    }
    return true;
  }, [record.status, auth.permissions])

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

  const onClickApprove = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke the certificate(s) and send the email notification to candidate?',
      width: 500,
      okText: 'Confirm',
      onOk: () => onSave("Approve"),
    });
  },[]);

  const onClickReject = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to reject the case?',
      width: 500,
      okText: 'Confirm',
      onOk: () => onSave("Reject"),
    });
  },[]);

  const onClickResubmit = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to resubmit the case?',
      width: 500,
      okText: 'Confirm',
      onOk: () => onSave("Resubmit"),
    });
  },[]);


  const onSave = useCallback(async (action) => {
    const values = await form.validateFields()
      .then((values) => ({
        ...values,
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      const recordId = values.id;
      delete values.id;
      switch (action) {
        case "Approve":
          runExamProfileAPI('approveCertRevoke', recordId, values)
            .then(() => onFinish());
          break;
        case "Reject":
          runExamProfileAPI("rejectCertRevoke", recordId,  values)
            .then(() => onFinish());
          break;
        case "Resubmit":
          runExamProfileAPI("resubmitCertRevoke", recordId,  values)
            .then(() => onFinish());
          break;
      }
    }
  }, []);

 useEffect(() => {
    if (open) {
      const _record = _.cloneDeep(record);
      form.setFieldsValue({
        // type: "REVOKE",
        ..._record,
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
        <PermissionControl key="reject" permissionRequired={['Revoke_Approve']} forceHidden={["REJECTED"].includes(record.status)}>
          <Button type="primary" danger onClick={() => onClickReject()}>
            Reject
          </Button>
        </PermissionControl>
        ,
        <PermissionControl key="approve" permissionRequired={['Revoke_Approve']} forceHidden={["REJECTED"].includes(record.status)}>
          <Button
            key="submit"
            type="primary"
            onClick={() => onClickApprove()}
          >
            Approve
          </Button>
        </PermissionControl>,
        <PermissionControl key="resubmit" permissionRequired={['Revoke_Submit']} forceHidden={["PENDING"].includes(record.status)}>
          <Button
            key="submit"
            type="primary"
            onClick={() => onClickResubmit()}
          >
            Resubmit
          </Button>
        </PermissionControl>,
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
        <Text name={'id'} label={'Id'} size={100} disabled={true} hidden={true}/>
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
            <Textarea
              name={'remark'}
              label={'Remark'}
              size={100}
              disabled={disabled}
            />
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
            <Richtext disabled={disabled} name={'emailContent'} label={'Inform candidate’s email content after request is approved'} size={100}/>
          </Col>
        </Row>
        <br />
        {/*<Alert type={'warning'} message={'Cert. renew required after submit.'} showIcon/>*/}
      </Form>
    </Modal>
  )
}

export default RevokeModal;