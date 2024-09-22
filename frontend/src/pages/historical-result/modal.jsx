import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";
import Radio from "@/components/Radio";
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
  const valid = Form.useWatch('valid', form);

  const voidOptions = useMemo(() => ([
    {
      value: false,
      label: 'Un-void',
    },
    {
      value: true,
      label: 'Void',
    }
  ]), []);

  const validOptions = useMemo(() => ([
    {
      value: true,
      label: 'Valid',
    },
    {
      value: false,
      label: 'Invalid',
    }
  ]), []);

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

  const onConfirm = useCallback(() => {
    form.validateFields().then(() => {
      modalApi.confirm({
        title:`Are you sure to submit for approval?`,
        width: 500,
        okText: 'Confirm',
        onOk: () => onSave()
      });
    });
  }, [record])


  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => ({
        remark: values.remark,
        historicalResultId: values.id,
        oldValid: record.valid,
        newValid: values.valid,
        oldUeVoid: record.ueVoid,
        newUeVoid: values.valid ? values.ueVoid : record.ueVoid,
        oldUcVoid: record.ucVoid,
        newUcVoid: values.valid ? values.ucVoid : record.ucVoid,
        oldAtVoid: record.atVoid,
        newAtVoid: values.valid ? values.atVoid : record.atVoid,
        oldBlVoid: record.blVoid,
        newBlVoid: values.valid ? values.blVoid : record.blVoid,
      }))
      .catch((e) => {
        console.error(e);
        return false;
      })
    console.log("values", values, record)
    if (values) {
      runExamProfileAPI('historicalResultRequest', values)
        .then(() => onFinish());
    }
    // if (values) {
    //   if ([false].includes(record.valid)) {
    //     runExamProfileAPI('historicalResultValid', record.id, {
    //       remark: values.remark
    //     })
    //       .then(() => onFinish());
    //   } else {
    //     runExamProfileAPI('historicalResultInvalid', record.id, {
    //       remark: values.remark
    //     })
    //       .then(() => onFinish());
    //   }
    //
    // }
  }, [record]);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'historicalResultRequest':
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

  return (
    <Modal
      width={600}
      style={{ top: 20 }}
      title={'Historical Result'}
      okText={'Submit for approval'}
      closable={false}
      maskClosable={false}
      onCancel={onClose}
      onOk={onConfirm}
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
          <Col span={12}>
            <b>Certificate</b>
          </Col>
          <Col span={12}>
            <Radio name={'valid'} options={validOptions} optionType={'button'} labelWidth={100}/>
          </Col>
          <Col span={24}>

            <Form.Item hidden={!valid}>
              <br/>
              <Row gutter={[16, 16]} style={{fontWeight: 'bold'}}>
                <Col span={12}>
                  Subject
                </Col>
                <Col span={12}>
                </Col>
                {
                  record.ueGrade ? (
                    <Col span={24}>
                      <Row gutter={[20, 16]}>
                        <Col span={12}>
                          UE Grade: {record.ueGrade} ({record.ueDate})
                        </Col>
                        <Col span={12}>
                          <Radio name={'ueVoid'} options={voidOptions} optionType={'button'} labelWidth={100}/>
                        </Col>
                      </Row>
                    </Col>
                  ) : null
                }
                {
                  record.ucGrade ? (
                    <Col span={24}>
                      <Row gutter={[20, 16]}>
                        <Col span={12}>
                          UC Grade: {record.ucGrade} ({record.ucDate})
                        </Col>
                        <Col span={12}>
                          <Radio name={'ucVoid'} options={voidOptions} optionType={'button'} labelWidth={100}/>
                        </Col>
                      </Row>
                    </Col>
                  ) : null
                }
                {
                  record.atGrade ? (
                    <Col span={24}>
                      <Row gutter={[20, 16]}>
                        <Col span={12}>
                          AT Grade: {record.atGrade} ({record.atDate})
                        </Col>
                        <Col span={12}>
                          <Radio name={'atVoid'} options={voidOptions} optionType={'button'} labelWidth={100}/>
                        </Col>
                      </Row>
                    </Col>
                  ) : null
                }
                {
                  record.blGrade ? (
                    <Col span={24}>
                      <Row gutter={[20, 16]}>
                        <Col span={12}>
                          BLNST Grade: {record.blGrade} ({record.blDate})
                        </Col>
                        <Col span={12}>
                          <Radio name={'blVoid'} options={voidOptions} optionType={'button'} labelWidth={100}/>
                        </Col>
                      </Row>
                    </Col>
                  ) : null
                }

              </Row>
              <br/>
            </Form.Item>

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