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

const RevokeModal = (props) =>  {

  const [form] = Form.useForm();
  const informCandidateValue = Form.useWatch('informCandidate', form);

  const onClose = useMemo(() => typeof props.onCloseCallback === "function" ? props.onCloseCallback : () => {}, [props.onCloseCallback ]);
  const onFinish = useMemo(() => typeof props.onFinishCallback === "function" ? props.onFinishCallback : () => {}, [props.onFinishCallback ]);
  const onSave = useCallback(async () => {
    form.validateFields()
      .then(() => onFinish())
      .then(() => form.resetFields())
      .catch((e) => console.error(e))
  }, []);

    const [data, setData] = useState([
      {
        serialNo: 'N000000001',
        candidateNo: 'C000001',
        hkid: 'T7700002',
        name: 'Chan Tai Man',
        email: 'taiman.chan@hotmail.com',
        examDate: '2024-01-01',
        resultLetterDate: '2024-01-25',
        emailIssuanceDate: '2024-01-31',
        ue: 'L2',
        uc: 'L1',
        at: 'Pass',
        blnst: 'Pass',
        status: 'Success',
      }
    ]);

    useEffect(() => {
      form.setFieldsValue({
        email: 'wilfred.lai@igsl-group.com',
        informCandidateEmail: `Hi Wilfred,<br/><br/>Your certificate was revoked. `,
      })
    }, [])


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
        key: 'passport',
        dataIndex: 'passport',
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
            title: 'UE',
            key: 'ue',
            dataIndex: 'ue',
            width: 100,
            sorter: true,
        },
        {
            title: 'UC',
            key: 'uc',
            dataIndex: 'uc',
            width: 100,
            sorter: true,
        },
        {
            title: 'AT',
            key: 'at',
            dataIndex: 'at',
            width: 100,
            sorter: true,
        },
        {
            title: 'BLNST',
            key: 'blnst',
            dataIndex: 'blnst',
            width: 100,
            sorter: true,
        },
        {
            title: 'Status',
            key: 'status',
            dataIndex: 'status',
            width: 100,
            sorter: true,
        },
    ], []);


    return (
    <Modal
      width={1000}
      okText={'Submit for revoke'}
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
              // rowKey={'candidateNo'}
              // rowSelection={{
              //   type: 'checkbox',
              //   ...rowSelection,
              // }}
              // onChange={tableOnChange}
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
              name={'email'}
              label={'Latest Candidate\'s email address'}
              required
              disabled
              size={100}
            />
          </Col>
          <Col span={24}>
            <Richtext name={'informCandidateEmail'} label={'Inform candidate’s email content after request is approved'} size={100}/>
          </Col>
        </Row>
        <br />
        {/*<Alert type={'warning'} message={'Cert. renew required after submit.'} showIcon/>*/}
      </Form>
    </Modal>
  )
}

export default RevokeModal;