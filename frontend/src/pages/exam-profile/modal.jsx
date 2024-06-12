import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";

const ExamProfileFormModal = (props) =>  {

  const [form] = Form.useForm();
  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, []);

  return (
    <Modal
      width={600}
      title={'Edit Exam Profile'}
      okText={'Save'}
      closable={false}
      maskClosable={false}
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
          <Col span={24} md={12}>
            <Text name={"Serial No."} label={'Serial No.'} required size={50}/>
          </Col>
          <Col span={24} md={12}>
            <Date name={'Exam Date'} label={'Exam Date'} required size={50}/>
          </Col>
          <Col span={24} md={12}>
            <Date name={'Result Letter Date'} label={'Result Letter Date'} placeholder={'YYYY-MM-DD'} size={50}/>
          </Col>
          <Col span={24} md={12}>
            <Date name={'Planned Email Issuance Date'} label={'Planned Email Issuance Date'} placeholder={'YYYY-MM-DD'} size={50}/>
          </Col>
          <Col span={24}>
            <Text name={'Location'} label={'Location'} size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default ExamProfileFormModal;