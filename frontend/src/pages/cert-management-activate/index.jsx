import React, { useEffect, useState, useMemo, useCallback } from 'react';
import {createSearchParams, useNavigate, Link, useParams} from "react-router-dom";
import styles from './style/index.module.less';
import { useRequest } from "ahooks";
import {Divider, Form, Card, Typography, Breadcrumb, Button, Space, Tabs, Col, Row, Descriptions, Modal, Pagination} from 'antd';
import ResizeableTable from "@/components/ResizeableTable";
import {
  HomeOutlined,
  SearchOutlined,
  DeleteOutlined,
  DownloadOutlined,
  CopyOutlined,
  SendOutlined
} from '@ant-design/icons';
import Text from "@/components/Text";
import Date from "@/components/Date";
import Textarea from "@/components/Textarea";
import HKID from "@/components/HKID";
import Email from "@/components/Email";
import dayjs from "dayjs";
import {useModal} from "../../context/modal-provider";
import {useMessage} from "../../context/message-provider";
import ResendEmailModal from "./modal";
import RevokeEmailModal from "./modal";

const CertificateManagementValid = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [resendopen, setResendOpen] = useState(false);
  const [revokeOpen, setRevokeOpen] = useState(false);
  const {
    serialNo,
  } = useParams();

  const [selectedRowKeys, setSelectedRowKeys] = useState('');
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

  const defaultPaginationInfo = useMemo(() => ({
    sizeOptions: [10, 20, 40],
    pageSize: 10,
    page: 1,
    sortBy: 'id',
    orderBy: 'descend',
  }), []);

  const [pagination, setPagination] = useState({
    total: 0,
    page: defaultPaginationInfo.page,
    pageSize: defaultPaginationInfo.pageSize,
    sortBy: defaultPaginationInfo.sortBy,
    orderBy: defaultPaginationInfo.orderBy,
  });

  const tableOnChange = useCallback((pageInfo, filters, sorter, extra) => {
    const {
      order,
      columnKey
    } = sorter;
    const tempPagination = {
      ...pagination,
      orderBy: order || defaultPaginationInfo.orderBy,
      sortBy: order ? columnKey : defaultPaginationInfo.sortBy,
    }
    setPagination(tempPagination);
  }, [pagination]);

  const paginationOnChange = useCallback((page, pageSize) => {
    const tempPagination = {
      ...pagination,
      page,
      pageSize,
    }
    setPagination(tempPagination);
  }, [pagination]);


  useEffect(() => {
    form.setFieldsValue({
      serialNo: 'N000000001',
      examDate: dayjs('2024-01-11'),
      plannedAnnouncedDate: dayjs('2024-01-11'),
      location: 'Hong Kong',
    })
  }, []);

  const breadcrumbItems = useMemo(() => [
    {
      title: <HomeOutlined />,
    },
    {
      title: 'Certificate Management',
    },
    {
      title: 'Valid',
    },
  ], []);

  const onClickDownload = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download certificate?',
      width: 500,
      okText: 'Confirm',

    });
  },[]);

  const onClickRevoke= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke certificate?',
      width: 500,
      okText: 'Confirm',
      onOk: () => {
        setRevokeOpen(true);
      }
    });
  },[]);

  const onClickRevokeSelected= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke certificate?',
      width: 500,
      okText: 'Confirm',
      onOk: () => {
        setRevokeOpen(true);
      }
    });
  },[]);

  const rowSelection = useCallback({
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }, []);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const columns = useMemo(() => [
    // {
    //   title: 'Action',
    //   key: 'action',
    //   width: 160,
    //   render: (row) => (
    //     <Space>
    //       <Button size={'small'} title={'Download'} icon={<DownloadOutlined />} onClick={onClickDownload}/>
    //       <Button size={'small'} title={'Revoke Cert.'} icon={<DeleteOutlined />} onClick={onClickRevoke}/>
    //       <Button size={'small'} title={'Copy URL'} icon={<CopyOutlined />} onClick={() => messageApi.success('URL is copied')}/>
    //       <Button size={'small'} title={'Resend Email'} icon={<SendOutlined />} onClick={() => setResendOpen(true)}/>
    //     </Space>
    //   )
    // },
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
      width: 100,
      render: (row) => <Link to={`/CertificateManagement/Valid/Candidate/${row.hkid}`}>{row.hkid}</Link>,
      sorter: true,
    },
    {
      title: 'Passport',
      key: 'passport',
      width: 100,
      render: (row) => <Link to={`/CertificateManagement/Valid/Candidate/${row.passport}`}>{row.passport}</Link>,
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
      title: 'Result Letter Date',
      key: 'resultLetterDate',
      dataIndex: 'resultLetterDate',
      width: 180,
      sorter: true,
    },
    {
      title: 'Email Issuance Date',
      key: 'emailIssuanceDate',
      dataIndex: 'emailIssuanceDate',
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
  ], []);
  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Certificate Management - Valid</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>

      <br/>
      <fieldset style={{padding: '0 30px'}}>
        <legend><Typography.Title level={5}>Search</Typography.Title></legend>
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
          <Row justify={'start'}>
            <Col span={20}>
              <Row gutter={24} justify={'start'}>
                <Col span={24} md={12}>
                  <HKID name={'hkid'} label={'HKID'}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'passportNo'} label={'Passport'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Text name={'name'} label={'Candidate’s Name'} size={12}/>
                </Col>
                <Col span={24} md={12}>
                  <Email name={'email'} label={'Candidate’s Email'} size={12}/>
                </Col>
              </Row>
            </Col>
            <Col span={4}>
              <Row justify={'end'}>
                <Col>
                  <Button shape="circle" type={'primary'} icon={<SearchOutlined/>} onClick={() => {
                  }}/>
                </Col>
              </Row>
            </Col>
          </Row>
        </Form>
      </fieldset>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        {/*<Col>*/}
        {/*  <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download*/}
        {/*    Selected ({selectedRowKeys.length})</Button>*/}
        {/*</Col>*/}
        {/*<Col>*/}
        {/*  <Button type="primary" onClick={onClickRevokeSelected} disabled={selectedRowKeys.length === 0}>Revoke*/}
        {/*    Selected ({selectedRowKeys.length})</Button>*/}
        {/*</Col>*/}
        <Col>
          <Pagination
            showSizeChanger
            total={pagination.total}
            pageSizeOptions={defaultPaginationInfo.sizeOptions}
            onChange={paginationOnChange}
            current={pagination.page}
            pageSize={pagination.pageSize}
          />
        </Col>
      </Row>
      <br/>
      <Card
        bordered={false}
        className={'card-body-nopadding'}
        title={'Certificate Management'}
      >
        <ResizeableTable
          size={'big'}
          // rowKey={'candidateNo'}
          // rowSelection={{
          //   type: 'checkbox',
          //   ...rowSelection,
          // }}
          onChange={tableOnChange}
          pagination={false}
          scroll={{
            x: '100%',
          }}
          columns={columns}
          dataSource={data}
        />
        <br/>
        <Row justify={'end'}>
          <Col>
            <Pagination
              total={pagination.total}
              pageSizeOptions={defaultPaginationInfo.sizeOptions}
              onChange={paginationOnChange}
              pageSize={defaultPaginationInfo.pageSize}
              current={pagination.page}
            />
          </Col>
        </Row>
        <br/>
      </Card>
      <ResendEmailModal
        open={resendopen}
        title={'Resend Email'}
        onCloseCallback={() => setResendOpen(false)}
        onFinishCallback={() => setResendOpen(false)}
      />
      <RevokeEmailModal
        open={revokeOpen}
        title={'Revoke Email'}
        onCloseCallback={() => setRevokeOpen(false)}
        onFinishCallback={() => setRevokeOpen(false)}
      />

    </div>

  )
}

export default CertificateManagementValid;