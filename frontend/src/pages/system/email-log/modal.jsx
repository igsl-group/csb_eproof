import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Dropdown from "@/components/Dropdown";
import Textarea from "@/components/Textarea";
import Richtext from "../../../components/Richtext";

const EmailDetailModal = (props) =>  {

  const open = props.open;
  const onCloseCallback = props.onCloseCallback;
  const [form] = Form.useForm();
  const record = props.record;

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        ...record,
      })
    }
  }, [open, record]);

  const onClose = useCallback(() => {
    if (typeof onCloseCallback === "function") {
      form.resetFields();
      onCloseCallback();
    }
  }, [onCloseCallback]);


  return (
    <Modal
      width={950}
      style={{ top: 20 }}
      title={'Email Detail'}
      okText={'Submit for approval'}
      closable={false}
      maskClosable={false}
      onCancel={onClose}
      footer={[
        <Button key="back" onClick={onClose}>
          Cancel
        </Button>,
      ]}
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
            <Text  name={"createdDate"} label={'Created Date'} size={100} disabled/>
          </Col>
          <Col span={24}>
            <Textarea row={2} name={"subject"} label={'Subject'} size={100} disabled/>
          </Col>
          <Col span={24}>
            <Dropdown
              name={'to'}
              label={'To'}
              size={100}
              mode="tags"
              tokenSeparators={[',']}
              disabled
            />
          </Col>
          <Col span={24}>
            <Richtext name={'body'} label={'Body'} size={100} disabled/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default EmailDetailModal;