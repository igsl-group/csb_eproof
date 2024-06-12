import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Textarea from "@/components/Textarea";

const OnHoldModal = (props) =>  {

  const [form] = Form.useForm();
  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, []);

  useEffect(() => {
    form.setFieldsValue({
      name: 'Wilfred Lai',
      hkid: {
        id: 'T770000',
        checkDigit: '2',
      },
      permission: ['Exam Profile Maintenance', 'Freeze Exam Profile', 'Issue Certificate Workflow', 'Dispatch'],
    })
  }, []);

  return (
    <Modal
      width={800}
      title={'On-hold Case'}
      okText={'Save'}
      closable={false}
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
          <Col span={12}>
            <Text name={"name"} label={'Name'} required size={50} disabled/>
          </Col>
          <Col span={12}>
            <HKID name={"hkid"} required disabled/>
          </Col>
          <Col span={24}>
            <Textarea name={"remark"} label={'Remark'} required size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default OnHoldModal;