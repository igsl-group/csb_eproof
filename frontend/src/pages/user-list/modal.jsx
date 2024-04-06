import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {Divider, Form, Card, Typography, Breadcrumb, Grid, Space, Button, Col, Row, Flex, Modal, Pagination} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";

const UserModal = (props) =>  {

  const [form] = Form.useForm();
  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, []);

  return (
    <Modal
      width={600}
      title={'Create User'}
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
          <Col span={24}>
            <Text name={"uid"} label={'DP User ID'} required size={50}/>
          </Col>
          <Col span={24}>
            <Text name={'name'} label={'Name'} required size={50}/>
          </Col>
          <Col span={24}>
            <Text name={'post'} label={'Post'} required size={50}/>
          </Col>
          <Col span={24}>
            <Dropdown name={'role'} label={'Role'} mode={"multiple"} required size={50}/>
          </Col>
          <Col span={24}>
            <Dropdown name={'status'} label={'Status'} required size={50}/>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default UserModal;