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
import {stringToHKID} from "../../components/HKID";

const HistoricalResultApproveModal = (props) =>  {

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
    if (record.status === "REJECTED") {
      return true;
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
      title:'Are you sure to approve the case?',
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

  const onClickWithdraw = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to withdraw the case?',
      width: 500,
      okText: 'Confirm',
      onOk: () => onSave("Withdraw"),
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
      delete values.hkid;
      delete values.passport;
      delete values.name;
      console.log(values, recordId);
      switch (action) {
        case "Approve":
          runExamProfileAPI('historicalResultApprove', recordId, values)
            .then(() => onFinish());
          break;
        case "Reject":
          runExamProfileAPI("historicalResultReject", recordId,  values)
            .then(() => onFinish());
          break;
        case "Withdraw":
          runExamProfileAPI("historicalResultWithdraw", recordId)
            .then(() => onFinish());
          break;
      }
    }
  }, []);

 useEffect(() => {
    if (open) {
      const _record = _.cloneDeep(record);
      form.setFieldsValue({
        ..._record.historicalResult,
        id: _record.id,
        remark: _record.remark,
        hkid: stringToHKID(_record.historicalResult?.hkid),
        passport: _record.historicalResult?.passport,
      })
    }
  }, [record, open])

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'historicalResultApprove':
          messageApi.success('Approve successfully.');
          break;
        case 'historicalResultReject':
          messageApi.success('Reject successfully.');
          break;
        case 'historicalResultWithdraw':
          messageApi.success('Withdraw successfully.');
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
        <PermissionControl key="withdraw" permissionRequired={['Revoke_Submit']} forceHidden={["PENDING"].includes(record.status)}>
          <Button
            key="submit"
            type="primary"
            onClick={() => onClickWithdraw()}
          >
            Withdraw
          </Button>
        </PermissionControl>,
      ]}
      closable={false}
      maskClosable={false}
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
        <Text name={"id"} label={'Id'} size={50} disabled/>
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
          <Col span={12}>
            <b>Certificate</b>
          </Col>
          <Col span={12}>
            <b>{record.oldValid ? 'Valid' : 'Invalid'} > {record.newValid ? 'Valid' : 'Invalid'}</b>
          </Col>
          <Col span={24}>
            <br/>
            <Row gutter={[16]} style={{fontWeight: 'bold'}}>
              <Col span={12}>
                Subject
              </Col>
              <Col span={12}>
              </Col>
              {
                record.historicalResult?.ueGrade ? (
                  <Col span={24}>
                    <Row gutter={[20, 16]}>
                      <Col span={12}>
                        UE Grade: {record.historicalResult?.ueGrade} ({record.historicalResult?.ueDate})
                      </Col>
                      <Col span={12}>
                        <b>{record.oldUeVoid ? 'Void' : 'Un-void'} > {record.newUeVoid ? 'Void' : 'Un-void'}</b>
                      </Col>
                    </Row>
                  </Col>
                ) : null
              }
              {
                record.historicalResult?.ucGrade ? (
                  <Col span={24}>
                    <Row gutter={[20, 16]}>
                      <Col span={12}>
                        UC Grade: {record.historicalResult?.ucGrade} ({record.historicalResult?.ucDate})
                      </Col>
                      <Col span={12}>
                        <b>{record.oldUcVoid ? 'Void' : 'Un-void'} > {record.newUcVoid ? 'Void' : 'Un-void'}</b>
                      </Col>
                    </Row>
                  </Col>
                ) : null
              }
              {
                record.historicalResult?.atGrade ? (
                  <Col span={24}>
                    <Row gutter={[20, 16]}>
                      <Col span={12}>
                        At Grade: {record.historicalResult?.atGrade} ({record.historicalResult?.atDate})
                      </Col>
                      <Col span={12}>
                        <b>{record.oldAtVoid ? 'Void' : 'Un-void'} > {record.newAtVoid ? 'Void' : 'Un-void'}</b>
                      </Col>
                    </Row>
                  </Col>
                ) : null
              }
              {
                record.historicalResult?.blGrade ? (
                  <Col span={24}>
                    <Row gutter={[20, 16]}>
                      <Col span={12}>
                        BLNST Grade: {record.historicalResult?.blGrade} ({record.historicalResult?.blDate})
                      </Col>
                      <Col span={12}>
                        <b>{record.oldBlVoid ? 'Void' : 'Un-void'} > {record.newBlVoid ? 'Void' : 'Un-void'}</b>
                      </Col>
                    </Row>
                  </Col>
                ) : null
              }
            </Row>
            <br/>
          </Col>
          <Col span={24}>
            <Textarea name={'remark'} label={'Remark'} size={50} disabled={disabled}/>
          </Col>
        </Row>

        <br />
      </Form>
    </Modal>
  )
}

export default HistoricalResultApproveModal;