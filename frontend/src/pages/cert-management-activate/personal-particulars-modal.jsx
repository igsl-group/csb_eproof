import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";
import Checkbox from "@/components/Checkbox";
import {validators} from "../../utils/validators";
import Richtext from "../../components/Richtext";
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";

const PersonalParticularsModal = (props) =>  {

  const [form] = Form.useForm();
  const informCandidateValue = Form.useWatch('informCandidate', form);
  const certRenewRequiredValue = Form.useWatch('certRenewRequired', form);

  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, [props.onCloseCallback ]);
  const onFinish = useMemo(() => typeof props.onFinishCallback === "function" ? props.onFinishCallback : () => {}, [props.onFinishCallback ]);
  const onSave = useCallback(async () => {
    form.validateFields()
      .then(() => onFinish())
      .then(() => form.resetFields())
      .catch((e) => console.error(e))
  }, []);

  return (
    <Modal
      width={1000}
      okText={'Submit for renew (All)'}
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
        <Row gutter={24} justify={'center'}>
          <Col span={24} md={12}>
            <Text name={'name'} label={'Current Name'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newName'} label={'New Name'} size={100}/>
          </Col>
          <Col span={24} md={12}>
            <HKID name={'hkid'} label={'Current HKID'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <HKID name={'newHkid'} label={'New HKID'} size={100}/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'passport'} label={'Current Passport'} size={100} disabled/>
          </Col>
          <Col span={24} md={12}>
            <Text name={'newPassport'} label={'New Passport'} size={100}/>
          </Col>
          {/*<Col span={24} md={12}>*/}
          {/*  <Text name={'email'} label={'Latest Email'} size={100} disabled/>*/}
          {/*</Col>*/}
          {/*<Col span={24} md={12}>*/}
          {/*  <Text name={'newEmail'} label={'New Email'} size={100}/>*/}
          {/*</Col>*/}
          {/*<Col span={24} md={12}>*/}
          {/*  <Textarea name={'messageForApprover'} label={'Message for approver'} size={100}/>*/}
          {/*</Col>*/}
          <Col span={24}>
            <Textarea name={'remark'} label={'Remark'} size={100}/>
          </Col>
          {/*<Col span={24}>*/}
          {/*  <Checkbox name={'certRenewRequired'} label={'Cert. renew required after processing success.'}/>*/}
          {/*</Col>*/}
          {/*<Col span={24}>*/}
          {/*  <Checkbox name={'informCandidate'} label={'Inform candidate via email after processing success.'}/>*/}
          {/*</Col>*/}
          {/*<Col span={24}>*/}
          {/*  <Richtext name={'informCandidateEmail'} label={'Inform Candidate\'s Email Content'} size={100} hidden={!informCandidateValue}/>*/}
          {/*</Col>*/}
        </Row>
      </Form>
    </Modal>
  )
}

export default PersonalParticularsModal;