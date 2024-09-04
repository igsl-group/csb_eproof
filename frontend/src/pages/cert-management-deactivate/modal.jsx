import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import Textarea from "@/components/Textarea";
import {validators} from "../../utils/validators";

const EmailModal = (props) =>  {

  const [form] = Form.useForm();
  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, [props.onCloseCallback ]);
  const onFinish = useMemo(() => typeof props.onCloseCallback === "function" ? props.onFinishCallback : () => {}, [props.onFinishCallback ]);
  const onSave = useCallback(async () => {
    form.validateFields()
      .then(() => onFinish())
      .then(() => form.resetFields())
      .catch((e) => console.error(e))
  }, []);

  return (
    <Modal
      width={1000}
      title={'Create Exam Profile'}
      okText={'Save'}
      closable={false}
      maskClosable={false}
      onOk={onSave}
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
        <Row gutter={24} justify={'center'}>
          <Col span={24}>
            <Dropdown
              name={"to"}
              label={'To'}
              required
              size={100}
              mode="tags"
              tokenSeparators={[',']}
              validation={[validators.emailValidator()]}
            />
          </Col>
          <Col span={24}>
            <Dropdown
              name={'cc'}
              label={'Cc'}
              size={100}
              mode="tags"
              tokenSeparators={[',']}
              validation={[validators.emailValidator()]}
            />
          </Col>
          <Col span={24}>
            <Dropdown
              name={'bcc'}
              label={'Bcc'}
              size={100}
              mode="tags"
              tokenSeparators={[',']}
              validation={[validators.emailValidator()]}
            />
          </Col>
          <Col span={24}>
            <Text name={'subject'} label={'Subject'} size={100} required/>
          </Col>
          <Col span={24}>
            <Textarea name={'body'} label={'Body'} size={100} required/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default EmailModal;