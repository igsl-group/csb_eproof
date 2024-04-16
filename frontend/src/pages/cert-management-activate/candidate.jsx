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
import PersonalParticularsModal from "./personal-particulars-modal";
import AppealModal from "./appeal-modal";
import RevokeCertModal from "./revoke-modal";

const Candidate = () =>  {

  const modalApi = useModal();
  const messageApi = useMessage();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [open, setOpen] = useState(false);
  const [revokeOpen, setRevokeOpen] = useState(false);
  const [openAppealModal, setOpenAppealModal] = useState(false);
  const {
    hkid,
  } = useParams();
  const [selectedRowKeys, setSelectedRowKeys] = useState('');

  const [data, setData] = useState([
    {
      serialNo: 'N000000001',
      candidateNo: 'C000001',
      hkid: 'T7700002',
      name: 'Chan Tai Man',
      email: 'taiman.chan@hotmail.com',
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
    {
      title: 'Candidate',
    },
    {
      title: hkid,
    },
  ], []);

  const onClickDownload = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickRevoke= useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to revoke PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const columns = useMemo(() => [
    {
      title: 'Action',
      key: 'action',
      width: 160,
      render: (row) => (
        <Row gutter={[8, 8]}>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => setOpenAppealModal(true)}>Update Result</Button></Col>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => messageApi.success('URL is copied')}>Copy URL</Button></Col>
          <Col span={24}><Button size={'small'} type={'primary'} onClick={() => {}}>Resend Email</Button></Col>
        </Row>
      )
    },
    {
      title: 'Serial No.',
      key: 'serialNo',
      dataIndex: 'serialNo',
      width: 140,
      sorter: true,
    },
    {
      title: 'Candidate No.',
      key: 'candidateNo',
      dataIndex: 'candidateNo',
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

  const rowSelection = useCallback({
    onChange: (selectedRowKeys, selectedRows) => {
      setSelectedRowKeys(selectedRowKeys);
    },
  }, []);

  useEffect(() => {
    form.setFieldsValue({
      hkid: {
        id: 'T770000',
        checkDigit: '2'
      },
      name: 'Chan Tai Man'
    })
  }, []);

  const onClickUpdatePersonalParticulars = useCallback(() => {
    setOpen(true);
  }, []);

  const onClickDownloadAll = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download all PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickDownloadSelected = useCallback(() => {
    modalApi.confirm({
      title:'Are you sure to download selected PDF?',
      width: 500,
      okText: 'Confirm',
    });
  },[]);

  const onClickRevokeSelected = useCallback(() => {
    setRevokeOpen(true);
  },[]);

  const onFinishCallback = useCallback(() => {
    setOpen(false);
    setOpenAppealModal(false);
  },[]);

  const onCloseCallback = useCallback(() => {
    setOpen(false);
    setOpenAppealModal(false);
  },[]);

  return (
    <div className={styles['exam-profile']}>
      <Typography.Title level={3}>Candidate</Typography.Title>
      <Breadcrumb items={breadcrumbItems}/>
      <br/>
      <br/>
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
          <Col span={16}>
            <Row gutter={24} justify={'start'}>
              <Col span={24} md={12}>
                <HKID name={'hkid'} label={'HKID'} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Text name={'passportNo'} label={'Passport No.'} size={12} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Text name={'name'} label={'Candidate Name'} size={12} disabled/>
              </Col>
              <Col span={24} md={12}>
                <Space>
                  <Text name={'email'} label={'Email'} size={12}/><Button type={'primary'}>Bulk Update Email</Button>
                </Space>
              </Col>
            </Row>
          </Col>
          <Col span={8}>
            <Row gutter={[8, 8]} justify={'end'}>
              <Col>
                <Button type={'primary'} onClick={onClickUpdatePersonalParticulars}>Update Personal Particulars</Button>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
      <br/>
      <Row gutter={[16, 16]} justify={'end'}>
        <Col>
          <Button type="primary" onClick={onClickDownloadSelected} disabled={selectedRowKeys.length === 0}>Download
            Selected ({selectedRowKeys.length})</Button>
        </Col>
        <Col>
          <Button type="primary" onClick={onClickRevokeSelected} disabled={selectedRowKeys.length === 0}>Revoke
            Selected ({selectedRowKeys.length})</Button>
        </Col>
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
          rowKey={'candidateNo'}
          rowSelection={{
            type: 'checkbox',
            ...rowSelection,
          }}
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
      <PersonalParticularsModal
        open={open}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
        title={'Update Personal Particulars'}
      />
      <AppealModal
        open={openAppealModal}
        onCloseCallback={onCloseCallback}
        onFinishCallback={onFinishCallback}
        title={'Update Result'}
      />
      <RevokeCertModal
          open={revokeOpen}
          title={'Revoke Cert.'}
          onCloseCallback={() => setRevokeOpen(false)}
          onFinishCallback={() => setRevokeOpen(false)}
      />
    </div>

  )
}

export default Candidate;