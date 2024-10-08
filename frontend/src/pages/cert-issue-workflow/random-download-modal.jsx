import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {
  Divider,
  Form,
  Card,
  Typography,
  Breadcrumb,
  Select,
  Space,
  Button,
  Col,
  Row,
  Flex,
  Modal,
  Pagination,
  Tag
} from 'antd';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Dropdown from "@/components/Dropdown";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import Textarea from "@/components/Textarea";
import {useRequest} from "ahooks";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import {examProfileAPI} from "../../api/request";
import {stringToHKID, stringToHKIDWithBracket} from "../../components/HKID";
import {
  toQueryString,
  download,
} from "@/utils/util";
import queryString from 'query-string';

import ResizeableTable from "@/components/ResizeableTable";
import {previewPdf} from "../../utils/util";

const RandomDownloadModal = (props) =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const isOnHold = props.isOnHold;
  const onCloseCallback = props.onCloseCallback;
  const onFinishCallback = props.onFinishCallback;
  const open = props.open;
  const recordId = props.recordId;
  const [form] = Form.useForm();
  const [certData, setCertData] = useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const onClose = useCallback(() => {
    if (typeof onCloseCallback === "function") {
      form.resetFields();
      setCertData([])
      onCloseCallback();
    }
  }, [onCloseCallback]);

  const onSave = useCallback(async () => {
    const values = await form.validateFields()
      .then((values) => {
        return ({
          ...values,
          allPassed: Number(values.allFailed) /3,
          partialFailed: Number(values.allFailed) / 3,
          allFailed: Number(values.allFailed) / 3,
        })
      })
      .catch((e) => {
        console.error(e);
        return false;
      })

    if (values) {
      runExamProfileAPI('getRandomCert', queryString.stringify({
        ...values,
        certStage: 'SIGN_ISSUE'
      }))
    }
  }, []);

  const columns = useMemo(() => [
    {
      title: 'HKID',
      key: 'hkid',
      dataIndex: 'hkid',
      render: (row) => stringToHKIDWithBracket(row),
      width: 100,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passport_no',
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
    {
      title: 'Letter Type',
      key: 'letter_type',
      dataIndex: 'letterType',
      width: 80,
      sorter: true,
    },
    {
      title: 'Status',
      key: 'status',
      dataIndex: 'certStatus',
      width: 120,
      render: (row) => <Tag>{row.label}</Tag>,
      sorter: true,
    },
  ], []);

  const { runAsync: runExamProfileAPI } = useRequest(examProfileAPI, {
    manual: true,
    onSuccess: (response, params) => {
      switch (params[0]) {
        case 'getRandomCert':
          console.log(response)
          const data = response.data || {};
          let body = data.body || [];

          body = body.filter((obj, index, self) =>
            index === self.findIndex((o) => o.id === obj.id)
          );

          setCertData(body);
          break;
        case 'certIssuanceBulkDownload':
          download(response);
          messageApi.success('Download successfully.');
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

  const rowSelection = useMemo(() => ({
    selectedRowKeys,
    preserveSelectedRowKeys: true,
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }), [selectedRowKeys]);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
      onOk: () => runExamProfileAPI('certIssuanceBulkDownload', selectedRowKeys.join(','))
    });
  },[selectedRowKeys]);

  useEffect(() => {
    if (open) {
      form.setFieldsValue({
        examProfileSerialNo: recordId,
      });
    }
  }, [open, recordId]);

  return (
    <Modal
      width={1920}
      title={`Random Verification Certificate`}
      closable={false}
      maskClosable={false}
      footer={(<Button onClick={onClose}>Cancel</Button>)}
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
        <Text name={'examProfileSerialNo'} disabled hidden/>
        <Row gutter={24} >
          <Col span={12} md={8} xl={8} xxl={4}>
            <Dropdown
              name={'allFailed'}
              required
              placeholder={'Please Choose ...'}
              options={[
                {
                  value: '15',
                  label: '15',
                },
                {
                  value: '30',
                  label: '30',
                },
                {
                  value: '45',
                  label: '45',
                }
              ]}
              size="100"
            />

          </Col>
          <Col>
            <Button type="primary" onClick={onSave}>Random</Button>
          </Col>
        </Row>
      </Form>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
            Selected ({selectedRowKeys.length})</Button>
        </Col>

      </Row>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Random Certificate'}
      >
        <ResizeableTable
          size={'big'}
          rowKey={'id'}
          rowSelection={{
            type: 'checkbox',
            ...rowSelection,
          }}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={certData}
        />
      </Card>
      <br/>
    </Modal>
  )
}

export default RandomDownloadModal;